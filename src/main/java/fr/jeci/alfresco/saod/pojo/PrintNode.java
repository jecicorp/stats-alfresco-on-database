package fr.jeci.alfresco.saod.pojo;

import java.io.Serializable;
import fr.jeci.alfresco.saod.StringUtil;

/**
 * Node to be print
 * 
 * @author jlesage
 *
 */
public class PrintNode implements Serializable {
	private static final long serialVersionUID = -6436273787435371994L;
	private static final String TYPE_DIRECTORY = "Directory";
	private static final String TYPE_FILE = "File";

	// Node DB id (alf_node.id)
	private Long nodeid = -1L;
	private String nodeRef = null;

	// Node DB Store id (alf_node.store_id)
	private Long storeId = -1L;
	
	// Node DB Store id (alf_node.uuid)
	private String uuid = null;
	
	// cm:name or other printable string
	private String label = null;

	// Store content url (alf_content_url.content_url)
	private String contentUrl= null;

	// Parent Node DB id (alf_child_assoc.parent_node_id)
	private Long parent = -1L;

	// Currently File or Directory
	private Integer nodetype = -1;
	
	// TODO rename into localContentSize
	// Sum file space usage (alf_content_url) in this directory
	private Long localSize = -1L;
	
	// TODO rename into subdirContentSize
	// Sum file space usage (alf_content_url) recursively for sub-directories.
	private Long dirSize = -1L;
	
	// TODO rename into countLocalFiles
	// Number of files (alf_content_data) in this directory
	private Integer nbElements = -1;
	
	// Number of files (alf_content_data) recursively for sub-directories.
	private Integer countSubdirFiles = -1;

	/**
	 * Constructor of a node (file)
	 * 
	 * @param id
	 */
	public PrintNode(Long id) {
		this.nodeid = id;
	}

	/**
	 * Permit to obtain the id of a specific node
	 * 
	 * @return id
	 */
	public Long getNodeid() {
		return nodeid;
	}

	public String getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}
	
	/**
	 * Permit to obtain the label of a specific node
	 * 
	 * @return label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Give a label to a node
	 * 
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Permit to obtain the size of a specific node
	 * 
	 * @return localSize
	 */
	public Long getLocalSize() {
		return localSize;
	}

	/**
	 * 
	 * @return
	 */
	public String getLocalSizeReadable() {
		return StringUtil.readableFileSize(getLocalSize());
	}

	/**
	 * Give a localSize to a node
	 * 
	 * @param localSize
	 */
	public void setLocalSize(Long localSize) {
		this.localSize = localSize;
	}

	/**
	 * Permit to obtain the full size of a node
	 * 
	 * @return
	 */
	public Long getFullSize() {
		return Long.sum(localSize, dirSize);
	}

	/**
	 * 
	 * @return
	 */
	public String getFullSizeReadable() {
		return StringUtil.readableFileSize(getFullSize());
	}

	/**
	 * 
	 * @return
	 */
	public Long getDirSize() {
		return dirSize;
	}

	/**
	 * 
	 * @return
	 */
	public String getDirSizeReadable() {
		return StringUtil.readableFileSize(getDirSize());
	}

	/**
	 * 
	 * @param fullSize
	 */
	public void setDirSize(Long fullSize) {
		this.dirSize = fullSize;
	}

	/**
	 * Permit to obtain the parent of a node
	 * 
	 * @return parent
	 */
	public Long getParent() {
		return parent;
	}

	/**
	 * Give a parent to a node
	 * 
	 * @param parent
	 */
	public void setParent(Long parent) {
		this.parent = parent;
	}

	public Integer getNodetype() {
		return nodetype;
	}

	public void setNodetype(Integer nodetype) {
		this.nodetype = nodetype;
	}

	public String getType() {
		switch (this.nodetype) {
		case 0:
			return TYPE_FILE;
		case 1:
			return TYPE_DIRECTORY;
		default:
			return null;
		}
	}

	public Integer getNbElements() {
		return nbElements;
	}

	public void setNbElements(Integer nb) {
		this.nbElements = nb;
	}

	public Long getStoreId() {
		return storeId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Integer getCountSubdirFiles() {
		return countSubdirFiles;
	}

	public void setCountSubdirFiles(Integer countSubdirFiles) {
		this.countSubdirFiles = countSubdirFiles;
	}

	public void setNodeid(Long nodeid) {
		this.nodeid = nodeid;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}
}
