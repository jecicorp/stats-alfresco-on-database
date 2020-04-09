package fr.jeci.alfresco.saod.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.jeci.alfresco.saod.ConcurrentRunSaodException;
import fr.jeci.alfresco.saod.SaodException;
import fr.jeci.alfresco.saod.StringUtil;
import fr.jeci.alfresco.saod.pojo.PrintNode;
import fr.jeci.alfresco.saod.sql.AlfrescoDao;
import fr.jeci.alfresco.saod.sql.LocalDao;
/**
 * Class of SaodService
 */
@Component
public class SaodServiceImpl implements SaodService {
	static final Logger LOG = LoggerFactory.getLogger(SaodServiceImpl.class);

	@Autowired
	private AlfrescoDao alfrescoDao;

	@Autowired
	private LocalDao localDao;

	@Autowired
	private MessageSource messageSource;


	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRES_NEW)
	/**
	 * Load data from the Alfresco Database
	 */
	public void loadDataFromAlfrescoDB() throws SaodException {
		lockDB();
		try {
			this.localDao.resetDatabase();

			// node_id, size
			long start = System.currentTimeMillis();
			Map<Long, Long> selectDirLocalSize = this.alfrescoDao.selectDirLocalSize();
			int nbNodes = selectDirLocalSize.size();
			LOG.info("selectDirLocalSize : {} nodes - {} ms ", nbNodes, (System.currentTimeMillis() - start));

			start = System.currentTimeMillis();
			this.localDao.insertStatsDirLocalSize(selectDirLocalSize);
			LOG.info("insertStatsDirLocalSize : {} nodes - {} ms ", nbNodes, (System.currentTimeMillis() - start));

			loadParentId(selectDirLocalSize);

			// Aggregate size from leaf to root
			resetFullSumSize();

			this.localDao.checkpoint();
		} finally {
			unlockDB();
		}
	}

	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
	/**
	 * Run the local dao
	 * @throws SaodException
	 */
	private void lockDB() throws SaodException {
		Timestamp run = this.localDao.getRun();
		if (run == null) {
			this.localDao.startRun();
		} else {
			throw new ConcurrentRunSaodException(run);
		}

	}

	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
	/**
	 * Stop running the local dao
	 * @throws SaodException
	 */
	private void unlockDB() throws SaodException {
		this.localDao.stopRun();
	}

	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	/**
	 * 
	 * @throws SaodException
	 */
	private void islockDB() throws SaodException {
		this.localDao.getRun();
	}

	@Override
	public void resetFullSumSize() throws SaodException {
		long start = System.currentTimeMillis();

		this.localDao.resetDirSumSize();

		List<Long> nodes = this.localDao.selectparentFolders(this.localDao.selectLeafNode());
		int size = nodes.size();
		while (!nodes.isEmpty()) {
			this.localDao.upadteDirSumSize(nodes);
			nodes = this.localDao.selectparentFolders(nodes);
			size += nodes.size();
		}

		LOG.info("resetFullSumSize : {} nodes - {} ms ", size, (System.currentTimeMillis() - start));
	}

	/**
	 * we set parent_node_id, then create parent row if he doesnt existe,
	 * finally set parent.dir_num_size to 0 because it have child. If
	 * dir_num_size = null, we think it's a leaf.
	 * 
	 * @param selectDirLocalSize
	 * @throws SaodException
	 */
	private void loadParentId(Map<Long, Long> selectDirLocalSize) throws SaodException {
		List<Long> list = new ArrayList<>();
		list.addAll(selectDirLocalSize.keySet());

		// child_node_id, parent_node_id
		long start = System.currentTimeMillis();
		Map<Long, Long> selectParentNodeId = this.alfrescoDao.selectParentNodeId(list);
		LOG.info("selectParentNodeId : {} nodes - {} ms ", selectDirLocalSize.size(),
				(System.currentTimeMillis() - start));

		while (selectParentNodeId.size() > 0) {

			// Create parent into locale if need
			Collection<Long> parents = selectParentNodeId.values();
			List<Long> parentsid = new ArrayList<>(parents.size());
			parentsid.addAll(parents);
			start = System.currentTimeMillis();
			this.localDao.insertStatsDirNoSize(parentsid);
			LOG.info("insertStatsDirNoSize : {} nodes - {} ms ", parentsid.size(),
					(System.currentTimeMillis() - start));

			this.localDao.updateParentNodeId(selectParentNodeId);
			this.localDao.upadteDirSumSizeZero(parentsid);
			selectParentNodeId = this.alfrescoDao.selectParentNodeId(parentsid);
		}
	}

	@Override
	public List<PrintNode> getRoots() throws SaodException {
		long start = System.currentTimeMillis();
		List<Long> selectRootFolders = this.localDao.selectRootFolders();
		LOG.info("selectRootFolders : {} nodes - {} ms ", selectRootFolders.size(),
				(System.currentTimeMillis() - start));
		List<PrintNode> loadPrintNode = loadPrintNode(selectRootFolders);
		LOG.info("loadPrintNode : {} nodes - {} ms ", loadPrintNode.size(), (System.currentTimeMillis() - start));
		return loadPrintNode;
	}

	@Override
	public List<PrintNode> getSubFolders(final String nodeid) throws SaodException {
		Long id = Long.valueOf(nodeid);
		List<Long> selectSubFolders = this.localDao.selectSubFolders(id);
		return loadPrintNode(selectSubFolders);
	}

	/**
	 * Permit to obtain a list of node
	 * @param ids
	 * @return nodes
	 * @throws SaodException
	 */
	private List<PrintNode> loadPrintNode(final List<Long> ids) throws SaodException {
		List<PrintNode> nodes = new ArrayList<>(ids.size());
		for (Long id : ids) {
			PrintNode node = this.localDao.loadRow(id);
			if (node == null) {
				LOG.warn("node is null for id {}", id);
				continue;
			}
			node.setLabel(loadNodeLabel(id));
			nodes.add(node);
		}

		return nodes;
	}

	@Override
	/**
	 * Permit to obtain the node
	 * @return node
	 */
	public PrintNode loadPrintNode(final String nodeid) throws SaodException {
		Long id = Long.valueOf(nodeid);

		PrintNode node = this.localDao.loadRow(id);
		if (node != null) {
			node.setLabel(loadNodeLabel(id));
		} else {
			node = new PrintNode(id);
			node.setLabel("Node Not Fount, Need Refresh");
		}
		return node;
	}

	/**
	 * Permit to obtain the label of a node from the id
	 * @param id
	 * @return nodeLabel
	 * @throws SaodException
	 */
	private String loadNodeLabel(Long id) throws SaodException {
		String nodeLabel = this.alfrescoDao.selectNodeLabel(id);
		if (nodeLabel == null) {
			nodeLabel = this.alfrescoDao.selectNodeRef(id);
		}
		return nodeLabel;
	}

	@Override
	/**
	 * Compute path of this node
	 * @return a StringBuilder
	 */
	public String computePath(String nodeid) throws SaodException {
		StringBuilder sb = new StringBuilder();
		Long id = Long.valueOf(nodeid);

		sb.append(loadNodeLabel(id));
		PrintNode node;
		while ((node = this.localDao.loadRow(id)) != null) {
			if (node.getParent() == null) {
				sb.insert(0, "|");
				break;
			} else {
				id = node.getParent();
				sb.insert(0, loadNodeLabel(id) + " > ");
			}
		}

		return sb.toString();
	}

	@Override
	/**
	 * Permit to obtain the date of last run and duration
	 */
	public String lastRunMessage() {
		try {
			Timestamp run = this.localDao.getRun();

			if (run != null) {
				return StringUtil.format(this.messageSource, "saod.service.last-run-message.running", run);
			}

			run = this.localDao.getLastSuccess();

			if (run != null) {
				return StringUtil.format(this.messageSource, "saod.service.last-run-message.last", run);
			}

		} catch (SaodException e) {
			LOG.error(e.getMessage(), e);
			return e.getLocalizedMessage();
		}

		return "Empty database";
	}

	/**
	 * 
	 * @return false
	 */
	@Override
	public boolean isRunning() {
		try {
			Timestamp run = this.localDao.getRun();
			return run != null;
		} catch (SaodException e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * Permit to get all the information from a node to all children
	 * @param nodes
	 * @return
	 * @throws SaodException
	 */
	@Override
	public List<PrintNode> getAllChildren(String nodeid) throws SaodException{
		//add children of root
		List<PrintNode> children= getSubFolders(nodeid);
		//for each children
		for(int i=0; i<children.size();i++) {
			List<PrintNode> littleChildren = getAllChildren(children.get(i).getNodeid().toString());
			for(PrintNode littleChild : littleChildren) {
				children.add(littleChild);
			}
		}
		return children;	
	}
	
	/**
	 * Permit to obtain the path of a node from where it has been download
	 * @param root
	 * @param nodeid
	 * @return path
	 */
	public String getPath(String root, PrintNode node) {
		String result = root;
		List<String> path = new ArrayList<String>();
		PrintNode currentNode = node;
		Long currentNodeID = node.getNodeid();
		
		//while we didn't find the root
		while(!currentNodeID.toString().equals(root)) {
			//add the ID of the node
			path.add(currentNodeID.toString());
			currentNodeID=currentNode.getParent();
		}
			
		//add all the path in the result
		for(int i=path.size();i<0;i--) {
			result+="/"+path.get(i);
		}
		
		return result;
	}
	
	@Override
	public String getPath(String root,String nodeid) throws SaodException {
		String completePath = computePath(nodeid);
		String[] res = completePath.split(" > ");
		
		String path = "";
		int cpt = 0;
		boolean find = false;
		//while we haven't find the root
		while(!find) {
			//check if it is in the table
			for(String s : res) {
				cpt++;
				if(s.equals(loadPrintNode(root).getLabel())) {
					System.out.println("String : "+ s);
					System.out.println("Path : " + path);
					find=true;
					path+=s+" / ";
				}
			}
		}
		
		for(int i=cpt;i<res.length;i++ ) {
			path+=res[cpt]+" / ";
		}
		return path;
	}

}
