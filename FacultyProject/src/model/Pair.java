package model;

public class Pair {

	private String formula;
	private int coefficient;
	
	public Pair(String formula, int coefficient) {
		
		this.formula = formula;
		this.coefficient = coefficient;
	}
	
	public String getFormula() {
		return this.formula;
	}
	
	public int getCoefficient() {
		return this.coefficient;
	}
	
	public String toString() {
		return "Formula = " + this.formula + ", Coefficient = " + this.coefficient;
	}
}


