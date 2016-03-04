package fr.jeci.alfresco.saod.sql;

import java.util.List;
import java.util.Map;

import fr.jeci.alfresco.saod.SaodException;
import fr.jeci.alfresco.saod.pojo.PrintNode;

/**
 * Data Accesss of local database
 * 
 * @author jlesage
 *
 */
public interface LocalDao {

	/**
	 * Drop existing table and load schema
	 */
	void initDatabase() throws SaodException;

	/**
	 * Insert localsize of each dir in local db
	 * 
	 * @param dirLocalSize
	 */
	void insertStatsDirLocalSize(Map<Long, Long> dirLocalSize) throws SaodException;

	/**
	 * Insert dir with no locale size (dir without file but with subdirectory)
	 * 
	 * @param dirLocalSize
	 * @throws SaodException 
	 */
	void insertStatsDirNoSize(List<Long> parentsid) throws SaodException;

	/**
	 * 
	 * Update row with parent_node_id
	 * 
	 * @param nodeids
	 * @throws SaodException
	 */
	void updateParentNodeId(Map<Long, Long> nodeids) throws SaodException;

	/**
	 * Select Folder that have no parent
	 * 
	 * @return
	 * @throws SaodException
	 */
	List<Long> selectRootFolders() throws SaodException;

	/**
	 * Select SubFolder
	 * 
	 * @param nodeid
	 * @return
	 */
	List<Long> selectSubFolders(Long nodeid) throws SaodException;

	/**
	 * Load PrintNode from data in row
	 * @param id
	 * @return
	 * @throws SaodException
	 */
	PrintNode loadRow(Long id) throws SaodException;
	 */

}
