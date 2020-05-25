package fr.jeci.alfresco.saod.pojo;
/**
 * Save the size and the number of elements of a node
 * @author Djunes
 *
 */
public class NodeStat {

	private Long size;
	private Integer number_elements;

	public NodeStat(Long size, Integer number) {
		this.size=size;
		this.number_elements=number;
	}
	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Integer getNumber_elements() {
		return number_elements;
	}

	public void setNumber_elements(Integer number_elements) {
		this.number_elements = number_elements;
	}
}
