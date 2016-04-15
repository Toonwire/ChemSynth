package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import database.SQLiteDatabase;

public class Model {
	
	private static final int maxDepth = 20;

	private SQLiteDatabase db = null;
	private Map<Integer, ReactionCol> map = new HashMap<>();
	private Map<String, Integer> nettoReaction = new HashMap<>();
	private Stack<Integer> stack = new Stack<Integer>();
	private List<Integer> reactionIDSeq = new ArrayList<>(maxDepth);
	private List<String> recursiveList = new ArrayList<>(maxDepth);
	private NetReaction netReaction;
	
	private int count = 1;
	private String desired;

	private HashMap<String, Integer> costMap;
	
	public Model(){
		
		db = new SQLiteDatabase();
		this.netReaction = new NetReaction();
		
	}

	public SQLiteDatabase getDatabase() {
		return db;
	}

	
	public void setUpSynth(ArrayList<String> resources, String desired){
//		this.resourceList = resources;
		this.desired = desired;		
		
		costMap = db.getCompoundCosts();
		retroSynth(desired);
		

 		printNetCost();
	}
 

	public void retroSynth(String formula){
 		int bestID = prioritize(db.getReactionIDs(formula));
 		
 		if (bestID == -1 || isAbundant(formula)) {
 			return;
 		}
 		System.out.println("recursive on " + formula);
 		
 		if(!map.containsKey(bestID)){
 			System.out.println("ID = " + bestID);
 			List<Pair> pairList = new ArrayList<>();		
 			List<String> chemList = new ArrayList<>();		
 			
 			for (String chem : db.getChemicals(bestID)) {
 				int coefficientPM = db.getCoefficient(bestID, chem);
 				pairList.add(new Pair(chem, coefficientPM));
 			}

 			ReactionCol rCol = new ReactionCol(bestID, pairList);
			map.put(bestID, rCol);
 			
			
			netReaction.update(formula, rCol);
			
			if (!netReaction.getMap().containsKey(desired)) {
				netReaction.rollback(rCol);
				retroSynth(formula);
				return;
			}
			
			System.out.println(netReaction);
 			
 			for(String chem : netReaction.getMap().keySet()) {
 				if (netReaction.getMap().get(chem) < 0 /* reactant */ && count <= maxDepth && !isAbundant(chem)  && !singleAtom(chem)) {
 					if (!recursiveList.contains(chem)) {
 						recursiveList.add(chem);
 						chemList.add(chem);
 					}
 				}
 			}

			for (String c : chemList) {
				retroSynth(c);
			}
			// should probably do rollback stuff or evaluate cost here
 		}
 	}

	private int prioritize(ArrayList<Integer> reactionIDs) {
		
		Map<Integer, Integer> similarityMap = new HashMap<>();
		int sim = 0;	// similarity count
		int chemCount = 0;
		int max = -1;
		int bestID = -1;
		for (Integer id : reactionIDs) {
			for (String chem : db.getChemicals(id)) {
				if (netReaction.getMap().containsKey(chem))
					sim++;
				chemCount++;
			}
			if (!map.containsKey(id)) {
				similarityMap.put(id, chemCount);
				if (sim >= max) {
					if (sim == max) {
						if (similarityMap.get(id) < similarityMap.get(bestID))
							bestID = id;
							max = sim;
							sim = 0;
					} else {
						bestID = id;
						max = sim;
						sim = 0;
					}
				}
			}
		}
		
		return bestID;
	}

	private boolean isAbundant(String chem) {
		return (chem.equals("NaCl") || chem.equals("O2") || chem.equals("H2O") || chem.equals("HCl") || chem.equals("CO2") || chem.equals("NaOH"));
	}
	
	private boolean singleAtom(String chem) {
		int upCount = 0;
		
		for (int k = 0; k < chem.length(); k++) {
		    
		    // Check for uppercase letters.
		    if (Character.isUpperCase(chem.charAt(k)))
		    	upCount++;
		}
		return upCount == 1;
	}
	
	public void printNetCost() {
		int totalCost = 0;
		for (String chem : netReaction.getMap().keySet()) {
			int cost = costMap.get(chem);
			if (cost < 0 || !chem.equals(desired))
				totalCost += costMap.get(chem);
		}
		
		System.out.println("\nTotal cost of the net reaction is : " + totalCost);
	}
 	
	public void setDesiredChemical(String formula) {
		this.desired = formula;
	}
	
	public List<Integer> getReactionIDSeq() {
		return this.reactionIDSeq;
	}
	
	public Map<String, Integer> getNetReactionMap() {
		return this.nettoReaction;
	}
	
}
