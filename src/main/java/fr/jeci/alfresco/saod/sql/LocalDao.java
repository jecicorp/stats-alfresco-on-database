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
	static final int FETCH_SIZE = 128;

	/**
	 * try database connection
	 * 
	 * @throws SaodException
	 */
	void ping() throws SaodException;

	/**
	 * Drop existing table and load schema
	 */
	void resetDatabase() throws SaodException;

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
	 * Select parent folder of this childs
	 * 
	 * @param nodeid
	 * @return
	 */
	List<Long> selectparentFolders(List<Long> nodesid) throws SaodException;

	/**
	 * Load PrintNode from data in row
	 * 
	 * @param id
	 * @return
	 * @throws SaodException
	 */
	PrintNode loadRow(Long id) throws SaodException;

	/**
	 * Set dir_num_size to zero (means no has child)
	 * 
	 * @param parentsid
	 * @throws SaodException
	 */
	void upadteDirSumSizeZero(List<Long> parentsid) throws SaodException;

	/**
	 * Set All non-null sum_size to zero
	 * 
	 * @throws SaodException
	 */
	void resetDirSumSize() throws SaodException;

	/**
	 * Select all node with sum_size to NULL
	 * 
	 * @return
	 * @throws SaodException
	 */
	List<Long> selectLeafNode() throws SaodException;

	/**
	 * Append to parent.sum_size the local_size his child
	 * 
	 * @param nodes
	 * @throws SaodException
	 */
	void upadteDirSumSize(List<Long> nodes) throws SaodException;

	/**
	 * Performing a checkpoint to the hsqldb
	 */
	void checkpoint();

}
