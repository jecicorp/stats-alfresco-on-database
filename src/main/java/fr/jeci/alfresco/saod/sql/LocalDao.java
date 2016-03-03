package fr.jeci.alfresco.saod.sql;

import java.util.Map;

/**
 * Data Accesss of local database
 * 
 * @author jlesage
 *
 */
public interface LocalDao {

	/**
	 * Insert localsize of each dir in local db
	 * 
	 * @param dirLocalSize
	 */
	void insertStatsDirLocalSize(Map<Long, Long> dirLocalSize);

	/**
	 * 
	 * Update row with parent_node_id
	 * 
	 * @param nodeids
	 */
	void updateParentNodeId(Map<Long, Long> nodeids);

}
