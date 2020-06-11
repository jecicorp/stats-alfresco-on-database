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
	
	// Sum file space usage (alf_content_url) in this directory
	private Long localContentSize = -1L;

	// Sum file space usage (alf_content_url) recursively for sub-directories.
	private Long subdirContentSize = -1L;
	
	// Number of files (alf_content_data) in this directory
	private Integer countLocalFiles = -1;

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
	
	public void setNodeid(Long nodeid) {
		this.nodeid = nodeid;
	}

	
	public String getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
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

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
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
	
	/**
	 * Return a string corresponding to the type
	 * @return
	 */
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

	/**
	 * Permit to obtain the size of a specific node
	 * 
	 * @return localSize
	 */
	public Long getLocalContentSize() {
		return localContentSize;
	}

	/**
	 * 
	 * @return
	 */
	public String getLocalContentSizeReadable() {
		return StringUtil.readableFileSize(getLocalContentSize());
	}

	/**
	 * Give a localSize to a node
	 * 
	 * @param localSize
	 */
	public void setLocalContentSize(Long localSize) {
		this.localContentSize = localSize;
	}

	/**
	 * Obtain DirSize
	 * @return
	 */
	public Long getSubdirContentSize() {
		return subdirContentSize;
	}

	/**
	 * 
	 * @return
	 */
	public String getSubdirContentSizeReadable() {
		return StringUtil.readableFileSize(getSubdirContentSize());
	}

	/**
	 * 
	 * @param fullSize
	 */
	public void setSubdirContentSize(Long fullSize) {
		this.subdirContentSize = fullSize;
	}


	public Integer getCountLocalFiles() {
		return countLocalFiles;
	}

	public void setCountLocalFiles(Integer countLocalFiles) {
		this.countLocalFiles = countLocalFiles;
	}

	public Integer getCountSubdirFiles() {
		return countSubdirFiles;
	}

	public void setCountSubdirFiles(Integer countSubdirFiles) {
		this.countSubdirFiles = countSubdirFiles;
	}
	
	/**
	 * Permit to obtain the full size of a node
	 * 
	 * @return
	 */
	public Long getFullSize() {
		return Long.sum(localContentSize, subdirContentSize);
	}

	/**
	 * 
	 * @return
	 */
	public String getFullSizeReadable() {
		return StringUtil.readableFileSize(getFullSize());
	}

}
