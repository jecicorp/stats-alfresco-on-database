package fr.jeci.alfresco.saod.sql;

import java.util.List;
import java.util.Map;

import fr.jeci.alfresco.saod.SaodException;

public interface AlfrescoDao {

	/**
	 * try database connection
	 * 
	 * @throws SaodException
	 */
	void ping() throws SaodException;

	/**
	 * Map of node_id and size of all file is this folder (depth=1)
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

	/**
	 * Look for label of this node
	 * 
	 * @param id
	 * @return
	 * @throws SaodException
	 */
	String selectNodeLabel(Long id) throws SaodException;

	/**
	 * Look For NodeRef
	 * 
	 * @param id
	 * @return
	 */
	String selectNodeRef(Long id) throws SaodException;

}
