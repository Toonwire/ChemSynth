package view.components;

import javax.swing.JPanel;

public class Vertex extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Vertex linkedVertex;
	private String formula;
	private int coef;
	private int reactionID;
	private boolean primaryLink;

	public Vertex(int reactionID, String formula) {
		this.reactionID = reactionID;
		this.formula = formula;
		
		try {
			this.coef = Integer.parseInt(formula.split("\\D")[0]);
			this.formula = formula.split("\\d", 2)[1];
			
		} catch (Exception e) {
			this.coef = 1;
		}
	}

	public void addLink(Vertex linkedVertex, boolean primaryLink) {
		this.linkedVertex = linkedVertex;
		this.primaryLink = primaryLink;
	}

	public String getFormula() {
		return formula;
	}

	public int getReactionID() {
		return reactionID;
	}
	
	public Vertex getLinkedVertex() {
		return linkedVertex;
	}
	
	public String toString() {
		return "<" + reactionID + ", " + formula + ">";
	}
	
	public int getCoef() {
		return coef;
	}

	public boolean isPrimaryLink() {
		return primaryLink;
	}

	

}
