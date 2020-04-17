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

	private Long nodeid;
	private String label;
	private Long localSize;
	private Long dirSize;
	private Long parent;
	private int nodetype;
	
	
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

	/**
	 * If it's a file or a directory
	 * @param type
	 */
	public void setType(int type) {
		this.nodetype=type;
	}
	
	public String getType() {
		String res = "";
		if(this.nodetype==0) {
			res = "File";
		}else if(this.nodetype==1){
			res = "Directory";
		}
		return res;
	}


}
