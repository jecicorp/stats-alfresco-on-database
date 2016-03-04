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
	private Long fullSize;
	private Long parent;

	public PrintNode(Long id) {
		this.nodeid = id;
	}

	public Long getNodeid() {
		return nodeid;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Long getLocalSize() {
		return localSize;
	}
	
	public String getLocalSizeReadable(){
		return StringUtil.readableFileSize(getLocalSize());
	}

	public void setLocalSize(Long localSize) {
		this.localSize = localSize;
	}

	public Long getFullSize() {
		return fullSize;
	}
	
	public String getFullSizeReadable(){
		return StringUtil.readableFileSize(getFullSize());
	}

	public void setFullSize(Long fullSize) {
		this.fullSize = fullSize;
	}

	public Long getParent() {
		return parent;
	}

	public void setParent(Long parent) {
		this.parent = parent;
	}

}
