package view;


public class Vertex {

	private Vertex linkedVertex;
	private String formula;

	public Vertex(String formula) {
		this.formula = formula;
	}


	public void addLink(Vertex linkedVertex) {
		this.linkedVertex = linkedVertex;
		
	}
	
	
}
