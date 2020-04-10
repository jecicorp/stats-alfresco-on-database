package fr.jeci.alfresco.saod.service;

import java.util.List;

import fr.jeci.alfresco.saod.SaodException;
import fr.jeci.alfresco.saod.pojo.PrintNode;

public interface SaodService {

	/**
	 * Load data from Alfresco DB into local DB
	 * 
	 * @throws SaodException
	 */
	void loadDataFromAlfrescoDB() throws SaodException;

	/**
	 * List root folders
	 * 
	 * @return list of id
	 */
	List<PrintNode> getRoots() throws SaodException;

	/**
	 * List sub-folders
	 * 
	 * @param nodeid
	 * @return
	 * @throws SaodException
	 */
	List<PrintNode> getSubFolders(String nodeid) throws SaodException;

	/**
	 * Load printable node
	 * 
	 * @param nodeid
	 * @return
	 * @throws SaodException
	 */
	PrintNode loadPrintNode(String nodeid) throws SaodException;

	/**
	 * Compute path of this node
	 * 
	 * @param nodeid
	 * @return
	 * @throws SaodException
	 */
	String computePath(String nodeid) throws SaodException;
	
	/**
	 * Compute path of this node
	 * @return a List of printNode
	 */
	List<PrintNode> computePathList(String nodeid) throws SaodException ;

	/**
	 * Compute full size of all directory starting from leaf to root
	 * 
	 * @throws SaodException
	 */
	void resetFullSumSize() throws SaodException;

	/**
	 * Return a string with date of last run and duration.
	 * 
	 * @return compute string or "Empty database"
	 */
	String lastRunMessage();

	/**
	 * Return is compute is currently running
	 * 
	 * @return true if compute is running.
	 */
	boolean isRunning();
	
	/**
	 * Permit to get all the information from a node to all children
	 * @param nodes
	 * @return
	 * @throws SaodException
	 */
	List<PrintNode> getAllChildren(String nodeid) throws SaodException;
	
	/**
	 * Permit to obtain the path of a node from where it has been download
	 * @param root
	 * @param nodeid
	 * @return path
	 */
	String getPath(String root, String nodeid) throws SaodException;

}
