package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import database.MySQLdatabase;

public class Model {

	private MySQLdatabase db = null;
	private ArrayList<String> resourceList = new ArrayList<String>();
	private LinkedList<Integer> reactionIDs = new LinkedList<Integer>();
	private String desired;
	
	boolean goalReached = false;
	private Stack<String> resourceStack;
	private int depth, maxDepth;
	
	public Model(){
		
		db = new MySQLdatabase();
		
//		Formula formula = new Formula("C6H2(NO2)3CH3");
//		System.out.println("----------");
////		Formula formula2 = new Formula("Co3(Fe(CN)6)2");
////		formulaResourceList.add(formula);
//		
//		System.exit(0);
		
		
		
	}

	public MySQLdatabase getDatabase() {
		return db;
	}

	
	public void setUpSynth(ArrayList<String> resources, String desired){
		this.resourceList = resources;
		this.desired = desired;
	}
}
