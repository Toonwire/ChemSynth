package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Formula {

	private String formula;
	private ArrayList<Atom> atoms = new ArrayList<Atom>();
	private HashMap<Atom,Integer> atomCount = new HashMap<Atom,Integer>();
	
	// new Formula(String formula)
	public Formula(String formula){
		this.formula = formula;
		
		String[] atomSplit = formula.split("[A-Z]");
		for (int i = 0; i < atomSplit.length; i++){
			atoms.add(new Atom(atomSplit[i]));
		}
	}
	
	public ArrayList<Atom> getAtoms(){
		return atoms;
	}
	
	public HashMap<Atom,Integer> getAtomCount(){
		return atomCount;
	}
}
