package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import database.MySQLdatabase;

public class Model {
	
	private static final int maxDepth = 20;

	private MySQLdatabase db = null;
	private Map<Integer, ReactionCol> map = new HashMap<>();
	private Map<String, Integer> nettoReaction = new HashMap<>();
	private Stack<Integer> stack = new Stack<Integer>();
	private List<Integer> reactionIDSeq = new ArrayList<>(maxDepth);
	private List<String> recursiveList = new ArrayList<>(maxDepth);
	
	private int count = 1;
	
	private String desired;
	
	public Model(){
		
		db = new MySQLdatabase();		
		
	}

	public MySQLdatabase getDatabase() {
		return db;
	}

	
	public void setUpSynth(ArrayList<String> resources, String desired){
//		this.resourceList = resources;
		this.desired = desired;		
		
//		// the iterative part
//		int depth = 0;
//		while (!goalFound) {
//			retroSynth(desiredDepth, depth);
//			System.out.println();
//			depth++;
//		}
		
//		Formula formula = new Formula("(CH3)16(Tc(H2O)3CO(BrFe3(ReCl)3(SO4)2)2)2MnO4");
//		formula.printAtoms();
		
		test(desired);
		
	}
 	
 	public void test(String formula){
 		int bestID = prioritize(db.getReactionIDs(formula));
// 		for(Integer reactionID : db.getReactionIDs(formula)){
// 			System.out.println("push = " + reactionID);
// 			if (!stack.contains(reactionID)) {
// 				stack.push(reactionID);
// 			}
// 		}
 		stack.push(bestID);
 		
 		if (!stack.isEmpty()) {
	 		int currentID = stack.pop();
	 		if(!map.containsKey(currentID)){
	 			System.out.println("ID = " + currentID);
	 			List<Pair> list = new ArrayList<>();
	 			for(String chem : db.getChemicals(currentID)){
//	 				System.out.println("id = "+ currentID + "\t chem = " + chem);
	 				int coefficientPM = db.getCoefficient(currentID, chem);
	 				list.add(new Pair(chem, coefficientPM));
	 				System.out.println("    " + chem + "  " + coefficientPM);
	 				
	 				
	 				if (nettoReaction.containsKey(chem))
	 					nettoReaction.put(chem, nettoReaction.get(chem) + coefficientPM);
	 				else 
	 					nettoReaction.put(chem, coefficientPM);					
	 				
	 				if (coefficientPM < 0 /* reactant */ && count <= maxDepth && !abundant(chem)  && !singleAtom(chem)) {
	 					
	 					this.reactionIDSeq.add(currentID);
//		 					System.out.println("ReactionID = " + currentID + "\tFormula = " + chem + "     \tCoefficient = " + coefficientPM);
	 					count++;
	 					
	 					if (!recursiveList.contains(chem)) {
	 						recursiveList.add(chem);
	 						System.out.println("recursive on " + chem);
	 						map.put(currentID, new ReactionCol(currentID, list));
	 						printNetReaction();
	 						test(chem);
	 					}
	 				}
	 				
	 			}
//	 			for (Integer id : map.keySet()) {
// 		 			System.out.println("map has id:\t" + id);
// 		 		}
	 		}
	 		return;
 		}
 		return;
 	}

	private int prioritize(ArrayList<Integer> reactionIDs) {
		Map<Integer, Integer> map = new HashMap<>();
		int sim = 0;
		int chemCount = 0;
		int max = -1;
		int bestID = -1;
		for (Integer id : reactionIDs) {
			for (String chem : db.getChemicals(id)) {
				if (nettoReaction.containsKey(chem))
					sim++;
				chemCount++;
			}
			map.put(id, chemCount);
			if (sim >= max) {
				if (sim == max) {
					if (map.get(id) < map.get(bestID))
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
		
		return bestID;
	}

	private boolean abundant(String chem) {
		return (chem.equals("NaCl") || chem.equals("O2") || chem.equals("H2O") || chem.equals("HCl") || chem.equals("CO2") || chem.equals("NaOH") || chem.equals("H2SO4") || chem.equals("HNO3"));
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
 	
	public void setDesiredChemical(String formula) {
		this.desired = formula;
	}
	
	public List<Integer> getReactionIDSeq() {
		return this.reactionIDSeq;
	}
	
	public Map<String, Integer> getNetReactionMap() {
		return this.nettoReaction;
	}
	
	private void printNetReaction() {
		StringBuilder builder = new StringBuilder();
		StringBuilder reactantBuilder = new StringBuilder();
		StringBuilder productBuilder = new StringBuilder();
		
		for (String formula : getNetReactionMap().keySet()) {
			int coef = getNetReactionMap().get(formula);
			if (coef != 0) {
				if (coef < 0) reactantBuilder.append(Math.abs(coef) + formula + " + ");
				else if (coef > 0) productBuilder.append(Math.abs(coef) + formula + " + ");
				
			}
		}
		builder.append(reactantBuilder.toString().substring(0, reactantBuilder.toString().length()-3) 
				+ " --> " 
				+ productBuilder.toString().substring(0, productBuilder.toString().length()-3));
		System.out.println(builder.toString());
		
	}

 	
}
