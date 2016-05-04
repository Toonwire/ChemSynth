package model;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.SynthController;
import database.SQLiteDatabase;

public class Model {
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	
	private static final int maxDepth = 20;

	private SQLiteDatabase db;
	
	private Map<Integer, ReactionCol> map;
	private Map<String, Integer> costMap;
	private Map<Integer, String> reactionsIDMap;
	private Map<NetReaction, Integer> netMap;
	private Map<Integer, Integer> initialReactionCosts;
	
	private ArrayList<String> resourceList;
	private List<String> recursiveList;
	private NetReaction netReaction;
	
	private String desired;
	private int initialBestID;
	private int recursiveDepth;
	
	public Model(){
		
		db = new SQLiteDatabase();
		
		this.map = new HashMap<>();
		this.netMap = new HashMap<>();
		this.initialReactionCosts = new HashMap<>();
		
		this.recursiveList = new ArrayList<>(maxDepth);
		this.resourceList = new ArrayList<>();
		this.netReaction = new NetReaction();
		
		this.initialBestID = -1;
		this.recursiveDepth = 0;
		
		this.costMap = db.getCompoundCosts();
		this.reactionsIDMap = db.getReactionsFromID();
	}

	public SQLiteDatabase getDatabase() {
		return db;
	}

	
	public void setUpSynth(ArrayList<String> resources, String desired){
		this.resourceList = resources;
		this.desired = desired;	

		// adding default resources (abundant)
		resourceList.add("NaCl");
		resourceList.add("O2");
		resourceList.add("H2O");
		resourceList.add("HCl");
		resourceList.add("CO2");
		resourceList.add("NaOH");
		
		retroSynth(desired);
		computeNetReaction();
		
	}
 
	private void computeNetReaction() {
		int minCost = Integer.MAX_VALUE;
		for (int initialID : initialReactionCosts.keySet()) {
			if (initialReactionCosts.get(initialID) < minCost)
				minCost = initialReactionCosts.get(initialID);
		}
		
		List<Integer> netIDs = new ArrayList<>();
		int minChemCount = Integer.MAX_VALUE;
		for (NetReaction nr : netMap.keySet()) {
			if (netMap.get(nr) == minCost) {
				int currentChemCount = Integer.MAX_VALUE;
				for (String chem : nr.getMap().keySet()) {
					if (!isAbundant(chem))
						currentChemCount++;
				}
				if (currentChemCount < minChemCount) {
					netIDs = nr.getUsedReactions();
					minChemCount = currentChemCount;
					this.netReaction = nr;
				}
			}
		}
		
		if (!netIDs.isEmpty()) {
			System.out.println("\n\nMinimum cost for a synthesis of the chemical " + desired + " is : " + minCost + "\nAchieved by the sequence of reactions:");
			for (int usedID = 0; usedID < netIDs.size(); usedID++) {
				/*
				 * SWING WORKER HERE MAYBE!?
				 */
				System.out.println(reactionsIDMap.get(netIDs.get(usedID)));
				rememberPath(netIDs.get(usedID), netReaction.getRecursiveList().get(usedID), reactionsIDMap.get(netIDs.get(usedID)));
			}
			//System.out.println("\nResulting in the net reaction: \n" + netReaction);
			
		} else {
//			System.err.println("A retro synthesis was not deemed possible. See reasons below:"
//					+ "\n- You have initiated a retro synthesis for an abundant chemical (" + desired + ")"
//					+ "\n- No reaction product matches your desired chemical");
		}
	}


	public void retroSynth(String formula){
		recursiveDepth++;
 		int bestID = prioritize(db.getReactionIDs(formula));
 		
 		if (formula.equals(desired))
 			initialBestID = bestID;
 		
 		if (bestID == -1 || isAbundant(formula)) {
 			return;
 		}
	
 		if (!map.containsKey(bestID)) {
 			//System.out.println("recursive on " + formula);
			//System.out.println("ID = " + bestID);
			List<Pair> pairList = new ArrayList<>();		
			List<String> chemList = new ArrayList<>();		
			
			for (String chem : db.getChemicals(bestID)) {
				int coefficientPM = db.getCoefficient(bestID, chem);
				pairList.add(new Pair(chem, coefficientPM));
			}
	
			ReactionCol rCol = new ReactionCol(bestID, pairList);
			map.put(bestID, rCol);
			
			netReaction.update(formula, rCol);
			//System.out.println(netReaction);
			
			if (!netReaction.getMap().containsKey(desired)) {
				netReaction.rollback(rCol);
//				System.out.println("rollback");
				recursiveDepth--;
				retroSynth(formula);
				return;
			}
			
			
			for(String chem : netReaction.getMap().keySet()) {
				if (netReaction.getMap().get(chem) < 0 /* reactant */ && recursiveDepth <= maxDepth && !isAbundant(chem)  && !singleAtom(chem)) {
					if (!recursiveList.contains(chem)) {
						recursiveList.add(chem);
						chemList.add(chem);
					}
				}
			}
	
			for (String c : chemList) {
				retroSynth(c);
				recursiveDepth--;
			}
 		}
		
		if (recursiveDepth == 1 && !initialReactionCosts.containsKey(initialBestID)) {
			initialReactionCosts.put(initialBestID, getNetCost());
			//System.out.println("\n\n\n");
			netMap.put(netReaction, getNetCost());
			
			this.netReaction = new NetReaction();
			this.map.clear();
			this.recursiveList.clear();
			recursiveDepth = 0;
			
			retroSynth(desired);
		}
 	}

	private int prioritize(ArrayList<Integer> reactionIDs) {
		
		Map<Integer, Integer> similarityMap = new HashMap<>();
		int sim = 0;	// similarity count
		int chemCount = 0;
		int max = -1;
		int bestID = -1;
		for (Integer id : reactionIDs) {
			if (!initialReactionCosts.containsKey(id)){
				for (String chem : db.getChemicals(id)) {
					if (netReaction.getMap().containsKey(chem))
						sim++;
					chemCount++;
				}
				if (!map.containsKey(id)) {
					similarityMap.put(id, chemCount);
					if (sim >= max) {
						if (sim == max) {
							if (similarityMap.get(id) < similarityMap.get(bestID)) {
								bestID = id;
								max = sim;
							}
						} else {
							bestID = id;
							max = sim;
						}
					}
				}
				sim = 0;
				chemCount = 0;
			}
		}
		
		return bestID;
	}

	private boolean isAbundant(String chem) {
		return resourceList.contains(chem);
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
	
	
	private int getNetCost() {
		int totalCost = 0;
		for (String chem : netReaction.getMap().keySet()) {
			if (netReaction.getMap().get(chem) > 0)	{
				if (!chem.equals(desired))
					totalCost += costMap.get(chem);
			}
		}
		
		return totalCost;
	}

	public void registerListeners(SynthController controller) {
		this.addPropertyChangeListener(controller);
	}
	
	public void addPropertyChangeListener(SynthController controller) {
		changeSupport.addPropertyChangeListener(controller);
		
	}
	
	public void reset() {
		this.map = new HashMap<>();
		this.netMap = new HashMap<>();
		this.initialReactionCosts = new HashMap<>();
		this.recursiveList = new ArrayList<>(maxDepth);
		this.netReaction = new NetReaction();
		this.initialBestID = -1;
		this.recursiveDepth = 0;
	}
	


	public void rememberPath(int reactionID, String recursiveOnFormula, String newReaction) {
		changeSupport.firePropertyChange("" + reactionID, recursiveOnFormula, newReaction);
	}

	public NetReaction getNetReaction() {
		return netReaction;
	}
}
