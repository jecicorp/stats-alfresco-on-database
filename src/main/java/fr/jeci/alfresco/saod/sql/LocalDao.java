package fr.jeci.alfresco.saod.sql;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import fr.jeci.alfresco.saod.SaodException;
import fr.jeci.alfresco.saod.pojo.NodeStat;
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
	void insertStatsDirLocalSize(Map<Long, NodeStat> dirLocalSize) throws SaodException;

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
	 * Update numberElements and dirsumSize
	 * @param nodes
	 * @throws SaodException
	 */
	void upadteStatsDatabase(List<Long> nodes) throws SaodException;
	
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
	 * Permit to update all values of numberElements and dirsumSize
	 * @param parentsid
	 * @throws SaodException
	 */
	void upadteStatsDatabaseZero(List<Long> parentsid) throws SaodException;
	
	/**
	 * Set all non-null numberElements and dirsumSize to zero
	 * @throws SaodException
	 */
	void resetStatsDatabase() throws SaodException;

	/**
	 * Select all node with sum_size to NULL
	 * 
	 * @return
	 * @throws SaodException
	 */
	List<Long> selectLeafNode() throws SaodException;

	/**
	 * Performing a checkpoint to the hsqldb
	 */
	void checkpoint();

	/**
	 * Insert row in RUN_LOG table with current timestamp at start_ts
	 * 
	 * @throws SaodException
	 */
	void startRun() throws SaodException;

	/**
	 * Update row in RUN_LOG table with has stop_ts NULL
	 * 
	 * @throws SaodException
	 */
	void stopRun() throws SaodException;

	/**
	 * Return start_ts timestamp of last row with status=0
	 * 
	 * @return timestamp or NULL
	 * @throws SaodException
	 */
	Timestamp getRun() throws SaodException;

	/**
	 * Return start_ts timestamp of last row with status=1
	 * 
	 * @return timestamp or NULL
	 * @throws SaodException
	 */
	Timestamp getLastSuccess() throws SaodException;

}
