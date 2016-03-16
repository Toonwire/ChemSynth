package model;

import java.util.ArrayList;
import java.util.List;

public class ReactionCell {

	private int depthLevel = 0;
	private int reactionID = 0;
	private int coefficient = 1;
	private List<ReactionCell> reactantList;
	private String formula = "";
	
	public ReactionCell(int reactionID, String formula, int coefficient) {
		this.reactionID = reactionID;
		this.formula = formula;
		this.coefficient = coefficient;
		this.reactantList = new ArrayList<>();
	}
	
	public ReactionCell(int reactionID, int coefficient) {
		this.reactionID = reactionID;
		this.coefficient = coefficient;
		this.reactantList = new ArrayList<>();
	}
	
	public int getReactionID() {
		return this.reactionID;
	}
	
	public int getCoefficient() {
		return this.coefficient;
	}
	
	public String toString() {
		return "ReactionID = " + reactionID + "\tCoefficient = " + coefficient;
	}
	
	public void setDepthLevel(int newDepth) {
		this.depthLevel = newDepth;
	}
	
	public int getDepthLevel() {
		return this.depthLevel;
	}
	
	public void addReactant(ReactionCell rc) {
		this.reactantList.add(rc);
	}

	public String getFormula() {
		return this.formula;
	}

	public List<ReactionCell> getReactantRCs() {
		return this.reactantList;
	}
}
