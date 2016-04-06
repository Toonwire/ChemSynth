package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NettoReaction {
	private Map<String, Integer> netReaction;
	//to store history
	private List<Integer> usedReactions;
	private List<Integer> oldReacCoef;
	private List<Integer> newReacCoef;
	private int steps;
	
	public NettoReaction (ReactionCol firstReaction){
		InitialUpdate(firstReaction);
	}
	
	public NettoReaction() {
		clear();
	}

	private void clear(){
		netReaction = new HashMap<String, Integer>();
				
		oldReacCoef = new ArrayList<Integer>();
		newReacCoef = new ArrayList<Integer>();
			
		usedReactions = new ArrayList<Integer>();
		steps = 0;
	}
	
	private void InitialUpdate(ReactionCol soleReaction){		
		clear();
		//strips first reaction
		for( Pair pair : soleReaction.getPairs()){				
			netReaction.put(pair.getFormula(), pair.getCoefficient());
		}
		usedReactions.add(soleReaction.getCurrentID());
		steps = usedReactions.size();	//=1
	}

	public void updateForward(String matterToExpand, ReactionCol currReaction) {
		if(steps == 0){
			InitialUpdate(currReaction);
		}else{
			//pre:matterToExpand is chosen elsewhere with those m : matters with negative coefficients
			//pre:currReac contains matterToExpand with positive coef...

			//finds the coefficients of matterToExpand in the net reaction
			int reactantCoef = Math.abs(netReaction.get(matterToExpand));

			//finds the coefficient of matterToExpand in currReaction
			int productCoef = 0;
			for( Pair pair : currReaction.getPairs()){
				if(pair.getFormula().equals(matterToExpand)){
					productCoef = Math.abs(pair.getCoefficient());
				}				
			}

			//finds coefficients such that matters to expand cancels out
			int gcd = gcd( Math.abs(reactantCoef), Math.abs(productCoef));
			int oldCoef = productCoef/gcd;
			int newCoef = reactantCoef/gcd;
			/*
			System.out.println("expanding on " + matterToExpand);
			System.out.print("; reactantCoef: " + reactantCoef);
			System.out.print("; productCoef: " + productCoef);
			System.out.print("; gcd: " + gcd);
			System.out.print("; oldCoef: " + oldCoef);
			System.out.println("; newCoef: " + newCoef);
			/**/

			//store to enable rollback
			usedReactions.add(currReaction.getCurrentID());
			oldReacCoef.add(oldCoef);
			newReacCoef.add(newCoef);
			steps++;

			//old net reaction is multiplied with oldCoef
			for( String key : netReaction.keySet()){
				netReaction.put(key, oldCoef*netReaction.get(key));		
			}

			//new nettoreaction is obtained  here:
			//new (current) reaction is multiplied with newCoef and added to old net reaction
			for( Pair pair : currReaction.getPairs()){
				int oldNettoReacCoef = netReaction.containsKey(pair.getFormula()) ? netReaction.get(pair.getFormula()) : 0;
				int newNettoReacCoef = oldNettoReacCoef + newCoef*pair.getCoefficient();
				if(newNettoReacCoef == 0){
					netReaction.remove(pair.getFormula());
				}else{
					netReaction.put(pair.getFormula(),  newNettoReacCoef);
				}
			}
		}
	}

	public void updateBackward(ReactionCol reactionToRemove) {
		if(reactionToRemove.getCurrentID() != usedReactions.get(steps-1))
			throw new IllegalArgumentException("wrong rollback..");
		if(steps <= 1){
			//note:if <1 something went wrong! 
			netReaction.clear();
		}else{
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
			steps--;
			//tjek
			if(steps != usedReactions.size())
				System.out.println("AAAAAAAARRRRRRGGGGGGG");
		}
	
	}

	private int gcd(int a, int b){
		//finds greatest common divisor af a and b 
		//a or b zero, sfd = other
		//a,b >=0
		int max, min, rest;
		
		if(a > b){
			max = a;
			min = b;
		} else{
			max = b;
			min = a;
		}
		
		if( a== 0 || b == 0) return max;
		
		rest = max % min;
		while(rest !=0){
			max = min;
			min = rest;
			rest = max % min;
		}
		return min;
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
//		String s = "";
//		for( String key : netReaction.keySet()){
//			s += netReaction.get(key) + "." + key + " "; 
//		}
//		return s;
		
		
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
		builder.append(reactantBuilder.toString().substring(0, reactantBuilder.toString().length()-3) 
				+ " --> " 
				+ productBuilder.toString().substring(0, productBuilder.toString().length()-3));
		
		return builder.toString();
	}
	


	
}