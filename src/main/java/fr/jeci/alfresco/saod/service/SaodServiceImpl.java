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
import fr.jeci.alfresco.saod.controller.HomeController;
import fr.jeci.alfresco.saod.pojo.NodeStat;
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
	public void loadDataFromAlfrescoDB() throws SaodException {
		lockDB();
		try {
			this.localDao.resetDatabase();

			// node_id, size
			long start = System.currentTimeMillis();
			Map<Long, NodeStat> selectDirLocalSize = this.alfrescoDao.selectNodeStat();
			int nbNodes = selectDirLocalSize.size();
			LOG.info("selectDirLocalSize : {} nodes - {} ms ", nbNodes, (System.currentTimeMillis() - start));

			start = System.currentTimeMillis();
			this.localDao.insertStatsDirLocalSize(selectDirLocalSize);
			LOG.info("insertStatsDirLocalSize : {} nodes - {} ms ", nbNodes, (System.currentTimeMillis() - start));

			loadParentId(selectDirLocalSize);
			resetFullSumSize();

			this.localDao.checkpoint();
		} finally {
			unlockDB();
		}
	}

	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW)
	/**
	 * Run the local dao
	 * 
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
	 * 
	 * @throws SaodException
	 */
	private void unlockDB() throws SaodException {
		this.localDao.stopRun();
	}

	@Transactional(isolation = Isolation.SERIALIZABLE, propagation = Propagation.REQUIRES_NEW, readOnly = true)
	private void islockDB() throws SaodException {
		this.localDao.getRun();
	}

	@Override
	public void resetFullSumSize() throws SaodException {
		long start = System.currentTimeMillis();

		this.localDao.resetDirSumSize();
		this.localDao.resetNumberElements();

		List<Long> nodes = this.localDao.selectparentFolders(this.localDao.selectLeafNode());
		int size = nodes.size();
		while (!nodes.isEmpty()) {
			this.localDao.upadteDirSumSize(nodes);
			this.localDao.updateNumberElements(nodes);
			nodes = this.localDao.selectparentFolders(nodes);
			size += nodes.size();
		}
		LOG.info("resetFullSumSize : {} nodes - {} ms ", size, (System.currentTimeMillis() - start));
	}

	/**
	 * we set parent_node_id, then create parent row if he doesnt existe, finally set parent.dir_num_size to 0 because it
	 * have child. If dir_num_size = null, we think it's a leaf.
	 * 
	 * @param selectDirLocalSize
	 * @throws SaodException
	 */
	private void loadParentId(Map<Long, NodeStat> selectDirLocalSize) throws SaodException {
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
			LOG.info("insertStatsDirNoSize : {} nodes - {} ms ", parentsid.size(), (System.currentTimeMillis() - start));

			this.localDao.updateParentNodeId(selectParentNodeId);
			this.localDao.upadteDirSumSizeZero(parentsid);
			this.localDao.upadteNumberSumElementsZero(parentsid);
			selectParentNodeId = this.alfrescoDao.selectParentNodeId(parentsid);
		}
	}

	@Override
	public List<PrintNode> getRoots() throws SaodException {
		long start = System.currentTimeMillis();
		List<Long> selectRootFolders = this.localDao.selectRootFolders();
		LOG.info("selectRootFolders : {} nodes - {} ms ", selectRootFolders.size(), (System.currentTimeMillis() - start));
		List<PrintNode> loadPrintNode = loadPrintNode(selectRootFolders);
		LOG.info("loadPrintNode : {} nodes - {} ms ", loadPrintNode.size(), (System.currentTimeMillis() - start));
		return loadPrintNode;
	}

	@Override
	public List<PrintNode> getSubFolders(final String nodeid) throws SaodException {
		Long id = Long.valueOf(nodeid);
		return this.getSubFolders(id);
	}

	@Override
	public List<PrintNode> getSubFolders(final Long nodeid) throws SaodException {
		List<Long> selectSubFolders = this.localDao.selectSubFolders(nodeid);
		LOG.info("getSubFolders : {} nodes ", selectSubFolders.size());

		return loadPrintNode(selectSubFolders);
	}

	/**
	 * Permit to obtain a list of node
	 * 
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
			node.setNodeRef(loadNodeRef(id));
			nodes.add(node);
		}

		return nodes;
	}

	@Override
	public PrintNode loadPrintNode(final String nodeid) throws SaodException {
		Long id = Long.valueOf(nodeid);

		PrintNode node = this.localDao.loadRow(id);
		if (node != null) {
			node.setLabel(loadNodeLabel(id));
			node.setNodeRef(loadNodeRef(id));
		} else {
			node = new PrintNode(id);
			node.setLabel("Node Not Fount, Need Refresh");
		}
		return node;
	}

	/**
	 * Permit to obtain the label of a node from the id
	 * 
	 * @param id
	 * @return nodeLabel
	 * @throws SaodException
	 */
	private String loadNodeLabel(Long id) throws SaodException {
		String nodeLabel = this.alfrescoDao.selectNodeLabel(id);
		String nodeRef = this.alfrescoDao.selectNodeRef(id);
		if (nodeLabel == null) {
			nodeLabel = nodeRef;
		}
		if (nodeRef!="NNF" && nodeLabel.equals(nodeRef)) {
			nodeLabel = "(No Name)";
		}
		return nodeLabel;
	}
	
	private String loadNodeRef(Long id) throws SaodException {
		return this.alfrescoDao.selectNodeRef(id);
	}

	@Override
	public String computePath(String nodeid) throws SaodException {
		return computePath(null, nodeid, " > ");
	}

	@Override
	public String computePath(String parent, String nodeid, String separator) throws SaodException {
		Long startComputePath = System.currentTimeMillis();
		StringBuilder path = new StringBuilder();

		Long id = Long.valueOf(nodeid);
		Long parentId = parent != null ? Long.valueOf(parent) : null;

		path.append(loadNodeLabel(id));
		PrintNode node;
		while ((node = this.localDao.loadRow(id)) != null) {
			if ((parentId != null && parentId.equals(node.getParent())) || parentId == node.getParent()) {
				if (parentId == null) {
					// absolute path
					path.insert(0, "|");
				} else {
					// relative path
					path.insert(0, "./");
				}
				break;
			} else {
				id = node.getParent();
				path.insert(0, loadNodeLabel(id) + separator);
			}
		}
		LOG.info("compute Path of a node, time executed : " + (System.currentTimeMillis() - startComputePath) + " ms");
		return path.toString();
	}

	@Override
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

	@Override
	public List<PrintNode> getAllChildren(PrintNode parent) throws SaodException {
		List<PrintNode> subFolders = getSubFolders(parent.getNodeid());
		List<PrintNode> children = new ArrayList<PrintNode>();

		// add children of root
		for (PrintNode node : subFolders) {
			children.add(node);
			children.addAll(getAllChildren(node));
		}
		return children;
	}

	/**
	 * Permit to export files, directories or both
	 * 
	 * @param nodeid
	 * @param typeExport
	 * @return
	 * @throws SaodException
	 */
	public List<PrintNode> getExport(final String root, String typeExport) throws SaodException {
		Long startExport = System.currentTimeMillis();
		List<PrintNode> children = this.getAllChildren(loadPrintNode(root));
		List<PrintNode> nodeToExport = new ArrayList<PrintNode>();
		// if we want only one type of export
		if (!HomeController.EXPORT_ALL.equals(typeExport)) {
			for (PrintNode node : children) {
				if (typeExport.equals(node.getType())) {
					nodeToExport.add(node);
				}
			}
		} else {
			nodeToExport = children;
		}
		LOG.info("List of exported node, time executed : " + (System.currentTimeMillis() - startExport) + " ms");
		return nodeToExport;
	}

}
