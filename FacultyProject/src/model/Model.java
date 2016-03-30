package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import database.MySQLdatabase;

public class Model {

	private MySQLdatabase db = null;
	private ArrayList<String> resourceList = new ArrayList<String>();
	private ArrayList<ReactionCell> reactionCells = new ArrayList<ReactionCell>();
	private Map<String, ArrayList<ReactionCell>> matrix = new HashMap<String, ArrayList<ReactionCell>>();
	private Map<Integer, ReactionCol> map = new HashMap<>();
	private Map<String, Integer> nettoReaction = new HashMap<>();
	private Stack<Integer> stack = new Stack<Integer>();
	private List<Integer> reactionIDSeq = new ArrayList<>();
	
	private static final int maxDepth = 10;
	private String desired;
	private int stop = 1;
	
	private boolean goalFound = false;
//	private Stack<Integer> reactionStack;
	private int depth;
	
	public Model(){
		
		db = new MySQLdatabase();		
		
	}

	public MySQLdatabase getDatabase() {
		return db;
	}

	
	public void setUpSynth(ArrayList<String> resources, String desired){
		this.resourceList = resources;
		this.desired = desired;		
		
		// the iterative part
		int depth = 0;
//		while (!goalFound) {
//			retroSynth(desiredDepth, depth);
//			System.out.println();
//			depth++;
//		}
		
	}
	
	public void runDeepeningSearch(Node startNode) {
		// the iterative part
		int depth = 0;
		while (!goalFound) {
			depthLimitedSearch(startNode, depth);
			System.out.println();
			depth++;
		}
	}

 	// standard depth-first-search (with limited depth in each iteration)
 	private void depthLimitedSearch(Node startNode, int depth) {

 		Stack<Node> stack = new Stack<Node>();
 		startNode.setDepthLevel(0);
 		stack.push(startNode);

 		System.out.print("Depth " + depth + ":\t" );
 		while (!stack.isEmpty()) {
 			Node actualNode = stack.pop();
 			System.out.print(actualNode + " ");

 			if (depth == maxDepth) {
 				System.out.print("maximum depth of " + maxDepth + " reached");
 				this.goalFound = true;
 				return;
 			}

 			if (actualNode.getDepthLevel() >= depth) {
 				continue;
 			}

 			for (Node reactantNode : actualNode.getReactantNodes()) {
 				reactantNode.setDepthLevel(actualNode.getDepthLevel() + 1); 
 				
//		 				if (!availableResources.contains(reactantNode))
//		 					stack.push(reactantNode);
//		 				else {
//		 					availableResources.remove(reactantNode);
//		 				}
 				stack.push(reactantNode);
 				
 			}
 		}
 	}
	

	private void retroSynth(String desired, int depth) {
		Stack<Integer> reactionStack = new Stack<Integer>();
		
		for (Integer id : db.getReactionIDs(desired)) {
			reactionStack.push(id);
		}
		int reactionID = reactionStack.pop();
		
		ArrayList<ReactionCell> rcList = new ArrayList<ReactionCell>();
		for (String formula : db.getChemicals(reactionID)) {
			ReactionCell rc = new ReactionCell(reactionID, db.getCoefficient(reactionID, formula));
			this.reactionCells.add(rc);
			rcList.add(rc);
			this.matrix.put(formula, rcList);
			
		}
		
		for (String formula : db.getReactants(reactionID)) {
			for (Integer id : db.getReactionIDs(formula)) {
				reactionStack.push(id);
			}
		}
		
	}
	
	
	// standard depth-first-search (with limited depth in each iteration)
 	private void depthLimitedSearch(ReactionCell rc, int depth) {

 		Stack<ReactionCell> stack = new Stack<ReactionCell>();
 		rc.setDepthLevel(0);
 		stack.push(rc);

 		System.out.print("Depth " + depth + ":\t" );
 		while (!stack.isEmpty()) {
 			ReactionCell actualRC = stack.pop();
 			System.out.print(actualRC + " ");

 			if (actualRC.getFormula().equals(this.desired)) {
 				System.out.print("\n\n" + desired + " found at depth " + depth);
 				this.goalFound = true;
 				return;
 			}

 			if (actualRC.getDepthLevel() >= depth) {
 				continue;
 			}

 			for (ReactionCell reactantRC : actualRC.getReactantRCs()) {
 				reactantRC.setDepthLevel(actualRC.getDepthLevel() + 1); 
 				
//	 				if (!availableResources.contains(reactantNode))
//	 					stack.push(reactantNode);
//	 				else {
//	 					availableResources.remove(reactantNode);
//	 				}
 				stack.push(reactantRC);
	 				
 			}
 		}
 	}
 	
 	public void test(String formula){
 		for(Integer reactionID : db.getReactionIDs(formula)){
 			stack.push(reactionID);
 		}
 		int currentID = stack.pop(); 		
 		if(!map.containsKey(currentID) && currentID != 73){
 			List<Pair> list = new ArrayList<>();
 			for(String chem : db.getChemicals(currentID)){
// 				System.out.println("chem = " + chem);
 				int coefficientPM = db.getCoefficient(currentID, chem);
 				list.add(new Pair(chem, coefficientPM));
 				
 				if (nettoReaction.containsKey(chem))
 					nettoReaction.put(chem, nettoReaction.get(chem) + coefficientPM);
 				else {
 					nettoReaction.put(chem, coefficientPM);					
 				
	 				if (coefficientPM < 0 /* reactant */ && stop <= 20 && !abundant(chem)) {
	 					
	 					this.reactionIDSeq.add(currentID);
	 					System.out.println("ReactionID = " + currentID + "\tFormula = " + chem + "     \tCoefficient = " + coefficientPM);
	 					stop++;
	 					test(chem);
	 				}
 				}
 			}
 			
 			map.put(currentID, new ReactionCol(currentID, list));
 		}
 	}

	private boolean abundant(String chem) {
		return (chem.equals("NaCl") || chem.equals("O2") || chem.equals("H2O"));
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
