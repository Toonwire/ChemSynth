package model;

import java.util.ArrayList;
import java.util.List;

public class Node {

	private String formula;
	private int depthLevel = 0;
	private int reactionID;
	private int coefficient;
	private List<Node> reactantList;
	
	public Node(int reactionID, String formula, int coefficient) {
		this.reactionID = reactionID;
		this.formula = formula;
		this.coefficient = coefficient;
		this.reactantList = new ArrayList<>();
	}

	public void addReactant(Node node) {
		this.reactantList.add(node);
		
	}
	
	public String toString() {
		return "Reaction: " + this.reactionID + "\tFormula: " + this.formula + "\tCoefficient: " + this.coefficient; 
	}
	
	public String getFormula() {
		return this.formula;
	}
	
	public int getDepthLevel() {
		return this.depthLevel;
	}
	
	public void setDepthLevel(int depthLevel) {
		this.depthLevel = depthLevel;
	}
	
	public List<Node> getReactantNodes() {
		return this.reactantList;
	}
}
