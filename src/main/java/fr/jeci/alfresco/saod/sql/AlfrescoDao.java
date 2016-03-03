package fr.jeci.alfresco.saod.sql;

import java.util.List;
import java.util.Map;

import fr.jeci.alfresco.saod.SaodException;

public interface AlfrescoDao {

	/**
	 * Map on node_id on size of all file is this folder (depth=1)
	 * 
	 * @return Map<node_id, size>
	 * @throws SaodException
	 */
	Map<Long, Long> selectDirLocalSize() throws SaodException;

	/**
	 * return parent node id of selected node
	 * 
	 * @return Map<node_id, size>
	 * @throws SaodException
	 */
	Map<Long, Long> selectParentNodeId(List<Long> child_id) throws SaodException;

}
