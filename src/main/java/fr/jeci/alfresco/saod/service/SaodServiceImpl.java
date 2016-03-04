package fr.jeci.alfresco.saod.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.jeci.alfresco.saod.SaodException;
import fr.jeci.alfresco.saod.pojo.PrintNode;
import fr.jeci.alfresco.saod.sql.AlfrescoDao;
import fr.jeci.alfresco.saod.sql.LocalDao;

@Component
public class SaodServiceImpl implements SaodService {
	static final Logger LOG = LoggerFactory.getLogger(SaodServiceImpl.class);

	@Autowired
	private AlfrescoDao alfrescoDao;

	@Autowired
	private LocalDao localDao;

	@Override
	public void loadDataFromAlfrescoDB() throws SaodException {
		this.localDao.initDatabase();

		// node_id, size
		long start = System.currentTimeMillis();
		Map<Long, Long> selectDirLocalSize = this.alfrescoDao.selectDirLocalSize();
		LOG.info("selectDirLocalSize : {} ms ", (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		this.localDao.insertStatsDirLocalSize(selectDirLocalSize);
		LOG.info("insertStatsDirLocalSize : {} ms ", (System.currentTimeMillis() - start));

		loadParentId(selectDirLocalSize);

		// Aggregate size from leaf to root
		resetFullSumSize();
	}

	@Override
	public void resetFullSumSize() throws SaodException {
		this.localDao.resetDirSumSize();

		List<Long> nodes = this.localDao.selectparentFolders(this.localDao.selectLeafNode());
		while (nodes.size() > 0) {
			this.localDao.upadteDirSumSize(nodes);
			nodes = this.localDao.selectparentFolders(nodes);
		}
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
		LOG.info("selectParentNodeId : {} ms ", (System.currentTimeMillis() - start));

		while (selectParentNodeId.size() > 0) {

			// Create parent into locale if need
			Collection<Long> parents = selectParentNodeId.values();
			List<Long> parentsid = new ArrayList<>(parents.size());
			parentsid.addAll(parents);
			start = System.currentTimeMillis();
			this.localDao.insertStatsDirNoSize(parentsid);
			LOG.info("selectParentNodeId : {} ms ", (System.currentTimeMillis() - start));

			start = System.currentTimeMillis();
			this.localDao.updateParentNodeId(selectParentNodeId);
			LOG.info("selectParentNodeId : {} ms ", (System.currentTimeMillis() - start));

			start = System.currentTimeMillis();
			this.localDao.upadteDirSumSizeZero(parentsid);
			LOG.info("selectParentNodeId : {} ms ", (System.currentTimeMillis() - start));

			start = System.currentTimeMillis();
			selectParentNodeId = this.alfrescoDao.selectParentNodeId(parentsid);
			LOG.info("selectParentNodeId : {} ms ", (System.currentTimeMillis() - start));
		}
	}

	@Override
	public List<PrintNode> getRoots() throws SaodException {
		List<Long> selectRootFolders = this.localDao.selectRootFolders();
		return loadPrintNode(selectRootFolders);
	}

	@Override
	public List<PrintNode> getSubFolders(final String nodeid) throws SaodException {
		Long id = Long.valueOf(nodeid);

		List<Long> selectSubFolders = this.localDao.selectSubFolders(id);
		return loadPrintNode(selectSubFolders);
	}

	private List<PrintNode> loadPrintNode(final List<Long> ids) throws SaodException {
		List<PrintNode> nodes = new ArrayList<>(ids.size());
		for (Long id : ids) {
			PrintNode node = this.localDao.loadRow(id);
			if (node == null) {
				LOG.warn("node is null for id {}", id);
				continue;
			}
			node.setLabel(this.alfrescoDao.selectNodeLabel(id));
			nodes.add(node);
		}

		return nodes;
	}

	@Override
	public PrintNode loadPrintNode(final String nodeid) throws SaodException {
		Long id = Long.valueOf(nodeid);

		PrintNode node = this.localDao.loadRow(id);
		if (node != null) {
			node.setLabel(this.alfrescoDao.selectNodeLabel(id));
		} else {
			node = new PrintNode(id);
			node.setLabel("Node Not Fount, Need Refresh");
		}
		return node;
	}

	@Override
	public String computePath(String nodeid) throws SaodException {
		StringBuilder sb = new StringBuilder();
		Long id = Long.valueOf(nodeid);

		sb.append(this.alfrescoDao.selectNodeLabel(id));
		PrintNode node = null;
		while ((node = this.localDao.loadRow(id)) != null) {
			if (node.getParent() == null) {
				sb.insert(0, "|");
				break;
			} else {
				id = node.getParent();
				sb.insert(0, this.alfrescoDao.selectNodeLabel(id) + " > ");
			}
		}

		return sb.toString();
	}
}
