package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetReaction {
	
	private Map<String, Integer> netReaction;
	private List<Integer> usedReactions;	//to store history
	private List<Integer> oldReacCoef;
	private List<Integer> newReacCoef;
	private int steps = 0;
	private List<String> recursiveList = new ArrayList<>();
	
	public NetReaction() {
		netReaction = new HashMap<String, Integer>();
		oldReacCoef = new ArrayList<Integer>();
		newReacCoef = new ArrayList<Integer>();
		usedReactions = new ArrayList<Integer>();
		
	}
	
	private void InitialUpdate(ReactionCol rCol){		
		//strips first reaction
		for( Pair pair : rCol.getPairs()){				
			netReaction.put(pair.getFormula(), pair.getCoefficient());
		}
		usedReactions.add(rCol.getCurrentID());
		steps = 1;
	}

	public void update(String formula, ReactionCol currentCol) {
		if(steps == 0){
			InitialUpdate(currentCol);
			recursiveList.add(formula);
		} else {
			recursiveList.add(formula);

			//finds the coefficient of formula in current reaction
			if (netReaction.get(formula) == null) {
				return;
			}
			int reactantCoef = Math.abs(netReaction.get(formula));
			int productCoef = 0;
			for(Pair pair : currentCol.getPairs()){
				if(pair.getFormula().equals(formula)){
					productCoef = Math.abs(pair.getCoefficient());
				}				
			}

			//finds coefficients such that matters to expand cancels out
			int gcd = gcd(Math.abs(reactantCoef), Math.abs(productCoef));
			int oldCoef = productCoef/gcd;
			int newCoef = reactantCoef/gcd;
			
			
			//old net reaction is multiplied with oldCoef
			for(String pair : netReaction.keySet()){
				netReaction.put(pair, oldCoef*netReaction.get(pair));		
			}

			//store to enable rollback
			usedReactions.add(currentCol.getCurrentID());
			oldReacCoef.add(oldCoef);
			newReacCoef.add(newCoef);
			steps++;


			//new nettoreaction is obtained  here:
			//new (current) reaction is multiplied with newCoef and added to old net reaction
			for(Pair pair : currentCol.getPairs()){
				int oldNettoReacCoef = netReaction.containsKey(pair.getFormula()) ? netReaction.get(pair.getFormula()) : 0;
				int newNettoReacCoef = oldNettoReacCoef + newCoef*pair.getCoefficient();
				if(newNettoReacCoef == 0){
					netReaction.remove(pair.getFormula());
				}else{
					netReaction.put(pair.getFormula(), newNettoReacCoef);
				}
			}
		}
	}

	public void rollback(ReactionCol reactionToRemove) {
		if (reactionToRemove.getCurrentID() != usedReactions.get(steps-1))
			throw new IllegalArgumentException("No rollback possible");
		if (steps <= 1){
			netReaction.clear();
		} else {
			//opposite of computation steps in updateForward
			int newCoef = newReacCoef.remove(steps-2);
			int oldCoef = oldReacCoef.remove(steps-2);
			for( Pair pair : reactionToRemove.getPairs()){
				int currNettoReacCoef = netReaction.containsKey(pair.getFormula()) ? netReaction.get(pair.getFormula()) : 0;
				int prevNettoReacCoef = currNettoReacCoef - newCoef*pair.getCoefficient();
				if(prevNettoReacCoef == 0){
					netReaction.remove(pair.getFormula());
				}else{
					netReaction.put(pair.getFormula(),  prevNettoReacCoef);
				}
			}
			for( String key : netReaction.keySet()){
				netReaction.put(key, netReaction.get(key)/oldCoef);		
			}
			
			//remove reaction from usedList
			usedReactions.remove(steps-1);
			recursiveList.remove(steps-1);
			steps--;
		}
	
	}

	private int gcd(int a, int b){
		return (b == 0) ? a : gcd(b, a%b);
	}
	
	public Map<String, Integer> getMap(){
		return netReaction;
	}

	public List<Integer> getUsedReactions(){
		return usedReactions;
	}
	
	public int getLastReaction(){
		return usedReactions.get(steps-1);
	}
	
	public String toString(){
		
		StringBuilder builder = new StringBuilder();
		StringBuilder reactantBuilder = new StringBuilder();
		StringBuilder productBuilder = new StringBuilder();
		
		for (String formula : netReaction.keySet()) {
			int coef = netReaction.get(formula);
			if (coef != 0) {
				if (coef < 0) reactantBuilder.append(Math.abs(coef) + formula + " + ");
				else if (coef > 0) productBuilder.append(Math.abs(coef) + formula + " + ");
				
			}
		}
		if (reactantBuilder.toString().length() != 0 && productBuilder.toString().length() != 0) {
			builder.append(reactantBuilder.toString().substring(0, reactantBuilder.toString().length()-3));
			builder.append(" \u2192 ");
			builder.append(productBuilder.toString().substring(0, productBuilder.toString().length()-3));
		}
		String netString = builder.toString();
		netString = netString.replaceAll("(?<=\\D)*1(?=\\D)", "");
		
		return netString;
	}
	
	public List<String> getRecursiveList() {
		return this.recursiveList;
	}

}