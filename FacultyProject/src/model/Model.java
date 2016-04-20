package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import database.SQLiteDatabase;

public class Model {
	
	private static final int maxDepth = 20;

	private SQLiteDatabase db = null;
	private Map<Integer, ReactionCol> map = new HashMap<>();
	private List<String> recursiveList = new ArrayList<>(maxDepth);
	private NetReaction netReaction;
	
	private Map<NetReaction, Integer> netMap = new HashMap<>();
	
	private int depth = 0;
	private int count = 1;
	private String desired;

	private HashMap<String, Integer> costMap;

	private Map<Integer, Integer> initialReactionCosts = new HashMap<>();
	private int initialBestID = -1;

	private ArrayList<String> resourceList;
	
	public Model(){
		
		db = new SQLiteDatabase();
		this.netReaction = new NetReaction();
//		Formula f = new Formula("Ca(OH(Cl2Fe3(Co(PO4)3)2)4)2");
//		f.printAtoms();
		
	}

	public SQLiteDatabase getDatabase() {
		return db;
	}

	
	public void setUpSynth(ArrayList<String> resources, String desired){
		this.resourceList = resources;
		this.desired = desired;	

		/*
		 * adding default resources (abundant)
		 */
		resourceList.add("NaCl");
		resourceList.add("O2");
		resourceList.add("H2O");
		resourceList.add("HCl");
		resourceList.add("CO2");
		resourceList.add("NaOH");
		
		costMap = db.getCompoundCosts();
		retroSynth(desired);
		computeNetReaction();
		
	}
 
	private void computeNetReaction() {
		NetReaction finalNetReaction = null;
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
					finalNetReaction = nr;
				}
			}
		}
		
		if (!netIDs.isEmpty()) {
			System.out.println("\n\nMinimum cost for a synthesis of the chemical " + desired + " is : " + minCost +"\nAchieved by the sequence of reactions:");
			for (int usedID = netIDs.size()-1; usedID >= 0; usedID--) {
				System.out.println("ID = " + netIDs.get(usedID) + ": \t" + db.getReactionsIDMap().get(netIDs.get(usedID)));			
			}
			
			System.out.println("\nResulting in the net reaction: \n" + finalNetReaction);
		} else if (desired.equals("H2O")) {

			System.err.println("CRY ME A FUCKING RIVER... LITERALLY!");
		} else {
			System.err.println("A retro synthesis was not deemed possible. See reasons below:"
					+ "\n- You have initiated a retro synthesis for an abundant chemical"
					+ "\n- No reaction product matches your desired chemical");
		}
	}

	public void retroSynth(String formula){
		depth++;
 		int bestID = prioritize(db.getReactionIDs(formula));
 		
 		if (formula.equals(desired))
 			initialBestID = bestID;
 		
 		if (bestID == -1 || isAbundant(formula)) {
 			return;
 		}
	
 		if (!map.containsKey(bestID)) {
 			System.out.println("recursive on " + formula);
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
			System.out.println(netReaction);
			System.out.println(depth);
			
			if (!netReaction.getMap().containsKey(desired)) {
				netReaction.rollback(rCol);
				System.out.println("rollback");
				depth--;
				retroSynth(formula);
				return;
			}
			
			
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
				depth--;
				System.out.println(depth);
			}
 		}
		
		if (depth == 1 && !initialReactionCosts.containsKey(initialBestID)) {
			initialReactionCosts.put(initialBestID, getNetCost());
			System.out.println("\n\n\n");
			netMap.put(netReaction, getNetCost());
			this.netReaction = new NetReaction();
			this.map.clear();
			this.recursiveList.clear();
			depth = 0;
			
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
 	
	public void setDesiredChemical(String formula) {
		this.desired = formula;
	}
}
