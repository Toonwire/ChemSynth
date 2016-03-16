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

}
