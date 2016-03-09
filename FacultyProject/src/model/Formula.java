package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formula {

	private Map<String,Integer> atomMap = new HashMap<String,Integer>();
	private Map<String,Integer> atomMap2 = new HashMap<String,Integer>();
	private ArrayList<String> atomList = new ArrayList<String>();
	private int amount = 1;
	private String regex = "[A-Z][a-z]?\\d*|(?<!\\([^)]{0,10})\\(.*\\)\\d+(?![^(]{0,10}\\))";
//	private String regex = "[A-Z][a-z]?\\d*|\\((?:[^()]{0,10}(?:\\(.*\\))?[^()]{0,10})+\\)\\d+";
	
	
	public Formula(String formula){
		
		findRegex(formula, regex, amount);
				
		
		for(Iterator<Entry<String, Integer>> it = atomMap.entrySet().iterator(); it.hasNext(); ) {
			Entry<String, Integer> entry = it.next();
		      
		      if (entry.getKey().matches(".*\\d")){
		    	  String atom = entry.getKey().split("\\d")[0];
		    	  int value = Integer.parseInt(entry.getKey().split("[A-Z][a-z]?")[1]);
		    	  System.out.println("Split " + entry.getKey() + " into " + atom + " and " + value);
		    	  it.remove();
		    	  if (atomMap2.containsKey(atom)){
		    		  value += atomMap2.get(atom);
		    	  }
		    	  atomMap2.put(atom, value);
		    	  
		      } else{
		    	  atomMap2.put(entry.getKey(), atomMap.get(entry.getKey()));
		      }
		      
		      
		}
		
		System.out.println("Total amount of single atoms in formula:");
		for (String str : atomMap2.keySet()){
			System.out.println(str + "\t" + atomMap2.get(str));
		}
		
	}

	private void findRegex(String strToCheck, String regex, int currentAmount){
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(strToCheck);
		
		System.out.println("Matches found by the RegEx over " + strToCheck + ":");
		System.out.println("Amount: " + amount + "\n" + "New Amount: " + currentAmount +"\n");
		int i = 1;
		while(m.find()){
//			System.out.println(i + ")\t" + m.group());
			atomList.add(m.group());
			if (atomMap.keySet().contains(m.group())){
				atomMap.put(m.group(), amount + atomMap.get(m.group()));
				System.out.println("Put " + m.group() + "  : " + amount);
			}
			atomMap.putIfAbsent(m.group(), amount * currentAmount);
			System.out.println("Put " + m.group() + "  : " + amount*currentAmount);
			i++;
		}
		this.amount = currentAmount;
		System.out.println("\n---------\n");
		
		for (int j = 0; j < atomList.size(); j++) {
			System.out.println(j + ") " +atomList.get(j) + "  : " + atomMap.get(atomList.get(j)));
			if (atomList.get(j).contains("(")) {
				String str = atomList.get(j);
		        atomList.remove(j);
		        atomMap.remove(str);
		        System.out.println("removed " + str);
		        j--;
		        
		        int newAmount = Integer.parseInt(str.substring(str.length()-1));
		        findRegex(str.substring(1, str.length()-2), regex, newAmount);
		    }
		}
		System.out.println("\n-----");
	}
	
	public ArrayList<String> getatomMap(){
		return new ArrayList<String>(atomMap.keySet());
	}
	
	public Map<String,Integer> getAtomMap(){
		return atomMap;
	}
}
