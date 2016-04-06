package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formula {

	private Map<String,Integer> atomMap = new HashMap<String,Integer>();
	private Map<String,Integer> finalAtomMap = new HashMap<String,Integer>();
	private ArrayList<String> atomList = new ArrayList<String>();
	private int amount = 1;
	private final String regex = "[A-Z][a-z]?\\d*|\\((?:[^()]{0,10}(?:\\(.*\\))?[^()]{0,10})+\\)\\d+";
	
	private String name;
	private int coefficient;
	
	public Formula(String formula) {
		this(formula,1);
	}
	
	public Formula(String formula, int coefficient){
		this.name = formula;
		this.coefficient = coefficient;
		
		parseFormula(formula, amount);
		cleanUpAtomCount();
		
	}
	
	public void printAtoms() {
		
		if (finalAtomMap != null) {
			System.out.println("Total amount of single atoms in formula " + name + ":");
			for (String atom : finalAtomMap.keySet()){
				System.out.println("--> " + atom + "\t: " + finalAtomMap.get(atom));
			}
			System.out.println();
		}
	}
	
	public Map<String, Integer> getAtoms() {
//		System.out.println("Total amount of single atoms in formula " + name + ":");
//		for (String atom : finalAtomMap.keySet()){
//			System.out.println("--> " + atom + "\t: " + finalAtomMap.get(atom));
//		}
		return finalAtomMap;
		
	}

	private void cleanUpAtomCount() {
		printState();
		
		for (String atom : atomMap.keySet()) {
			String[] a = atom.split("(?<=\\w)(?=\\d)");
			int coef = atomMap.get(atom);
			
			if (a.length > 1) {
				atom = a[0];
				coef *= Integer.parseInt(a[1]);
			} 
			
			if (finalAtomMap.containsKey(atom))
					coef += finalAtomMap.get(atom);
			
			finalAtomMap.put(atom, coef);
		}
	}

	private void parseFormula(String strToCheck, int currentAmount){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(strToCheck);
		
		
//		System.out.println("\nMatches found by the RegEx over " + strToCheck + ":");
//		System.out.println("Amount: " + amount + "\n" + "New Amount: " + currentAmount +"\n");
		while(m.find()){
			String atom = m.group();
			atomList.add(atom);
			if (atomMap.keySet().contains(atom)){
				atomMap.put(m.group(), amount*currentAmount + atomMap.get(atom));
//				System.out.println("Put " + atom + " \t: " + amount);
			} else {
				atomMap.put(atom, amount * currentAmount);
//				System.out.println("Put " + atom + " \t: " + amount*currentAmount);
			}
		}
//		printState();
//		System.out.println("\n---------\n");
		
		for (int j = 0; j < atomList.size(); j++) {
//			System.out.println(j + ") " +atomList.get(j) + " \t: " + atomMap.get(atomList.get(j)));
			if (atomList.get(j).contains("(")) {
				this.amount = atomMap.get(atomList.get(j));
				String str = atomList.get(j);
		        atomList.remove(j);
		        atomMap.remove(str);
//		        System.out.println("\nRemoved " + str);
		        j--;
		        
		        String[] temp = str.split("(?<=[\\)])(?![A-Za-z])");
		        int newAmount = Integer.parseInt(temp[temp.length-1]);
		        parseFormula(str.substring(1, str.length()-temp[temp.length-1].length()-1), newAmount);
		    }
		}
//		System.out.println("\n-----");
	}
	
	private void printState() {
//		System.out.println("\nState of AtomMap:");
//		for (String atom : atomMap.keySet()) {
//			System.out.println("--> " + atom + "\t: " + atomMap.get(atom));
//		}
//		System.out.println();
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getCoefficient() {
		return this.coefficient;
	}
}
