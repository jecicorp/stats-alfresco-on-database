package fr.jeci.alfresco.saod.pojo;
/**
 * Save the size and the number of elements of a node
 * @author Djunes
 *
 */
public class NodeStat {

	private Long size;
	private Integer numberElements;

	public NodeStat(Long size, Integer number) {
		this.size=size;
		this.numberElements=number;
	}
	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Integer getNumberElements() {
		return numberElements;
	}

	public void setNumberElements(Integer number_elements) {
		this.numberElements = number_elements;
	}
}
