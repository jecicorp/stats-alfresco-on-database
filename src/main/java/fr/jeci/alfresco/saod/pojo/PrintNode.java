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

	/**
	 * Constructor of a node
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
	 * Get the type of a node
	 * @return file or directory
	 */
	public String getType() {
		//non fini
		//le if ne marche pas
		String[] extension = this.getLabel().split(".") ;
		System.out.println(getLabel());
		String type="Directory";
		for(int i=0;i<extension.length;i++) {
			if(extension[i]!=null) {
				type = "File";
			}
		}
		return type;
	}

}
