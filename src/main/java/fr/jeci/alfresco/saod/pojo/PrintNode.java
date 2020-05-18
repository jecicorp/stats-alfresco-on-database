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

	private Long nodeid;
	private String nodeRef;
	private String label;
	private Long localSize;
	private Long dirSize;
	private Long parent;
	private Integer nodetype;

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
}
