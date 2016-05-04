package model;

import java.util.List;

public class ReactionCol {
	
	private int currentID;
	private List<Pair> pairs;

	public ReactionCol(int currentID, List<Pair> pairs) {
		this.currentID = currentID;
		this.pairs = pairs;
	}

	public List<Pair> getPairs() {
		return pairs;
	}


	public int getCurrentID() {
		return this.currentID;
	}
	
//	public String toString() {
//		return "{ ReactionID = " + this.currentID + ", " + listToString(pairs) + "}";
//	}
//	
//	private String listToString(List<?> list) {
//	    StringBuilder builder = new StringBuilder();
//	   	    
//	    for (Pair p : pairs) {
//	    	builder.append(p.toString());
//	    }
//	    return builder.toString();
//	}
}
