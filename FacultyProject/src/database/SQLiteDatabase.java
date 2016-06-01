package database;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLiteDatabase {

	private Connection connection = null;
	private Statement statement = null;
	private PreparedStatement prepStmt = null;
	private HashMap<Integer, String> reactionFromID = new HashMap<Integer, String>();
	
	public SQLiteDatabase() {
		
		createTables();
		insertIntoTable();
	}
	
	public void createTables(){
		
		try{
			connection = this.connect();
			statement = connection.createStatement();
			
//			String sql1 = "drop table if exists reactants";
//			String sql2 = "drop table if exists products";
//			String sql3 = "drop table if exists costs";
//			statement.executeUpdate(sql1);
//			statement.executeUpdate(sql2);
//			statement.executeUpdate(sql3);
			
		    String reactantTable = "create table if not exists reactants (reactionID integer, formula varchar(30), coefficient integer, PRIMARY KEY(reactionID, formula))";
		    String productTable = "create table if not exists products (reactionID integer, formula varchar(30), coefficient integer, PRIMARY KEY(reactionID, formula))";
		    String costTable = "create table if not exists costs(formula varchar(30), cost integer, PRIMARY KEY(formula));";
			
			statement.executeUpdate(reactantTable);
			statement.executeUpdate(productTable);
			statement.executeUpdate(costTable);
		   

		    
//		    System.out.println("Created table");

		    disconnect();
		    
		} catch (SQLException e) {
			System.err.println("Could not create table");
			e.printStackTrace();
		}	
	}

	public void insertIntoTable(){
		
		try{
			connection = this.connect();
			
			// get the path for the reactions.txt file
			URL url = getClass().getResource("reactions.txt");
			InputStream in = null;
			Scanner s = null;
			try {
				in = url.openStream();
				s = new Scanner(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/*
			 * Uncomment the insertFile statements below to create
			 * and see the resulting SQL insert statements.
			 */
//			PrintStream insertFile = new PrintStream(new File("insertFile.txt"));	
			
			int reactionID = 1;
			while(s.hasNextLine()) {
				String line = s.nextLine();
				reactionFromID .put(reactionID, line);
				String[] reaction = line.trim().split("->");
				
				Pattern p = Pattern.compile("\\w+(\\(\\w+\\)\\w)*");
				Matcher m = p.matcher(reaction[0]);
				
				while(m.find()) {
					String formula = m.group();
					int coefficient;
					try {
						coefficient = Integer.parseInt(formula.split("\\D")[0]);
						formula = formula.split("(?<=\\d)(?=\\D)", 2)[1];	
						// limit = 2: only split the coefficient from the formula, not the numbers inside the formula
					} catch (Exception e) {
						coefficient = 1;
					}
					
//					String reactantInsert = "insert or ignore into reactants(reactionID, formula, coefficient) values(" + reactionID + "," + "'" + formula + "'" + "," + coefficient + ");";
//					insertFile.println(reactantInsert);
					
					prepStmt = connection.prepareStatement("insert or ignore into reactants(reactionID, formula, coefficient) values(?,?,?);");
					prepStmt.setInt(1, reactionID);
					prepStmt.setString(2, formula);
					prepStmt.setInt(3, coefficient);
					prepStmt.execute();
				}
				
				Matcher m2 = p.matcher(reaction[1]);
				
				while(m2.find()) {
					String formula = m2.group();
					int coefficient;
					try {
						coefficient = Integer.parseInt(formula.split("\\D")[0]);
						formula = formula.split("(?<=\\d)(?=\\D)", 2)[1];
					} catch (Exception e) {
						coefficient = 1;
					}
					
//					String productInsert = "insert or ignore into products(reactionID, formula, coefficient) values(" + reactionID + "," + "'" + formula + "'" + "," + coefficient + ");";
//					insertFile.println(productInsert);
					
					prepStmt = connection.prepareStatement("insert or ignore into products(reactionID, formula, coefficient) values(?,?,?);");
					prepStmt.setInt(1, reactionID);
					prepStmt.setString(2, formula);
					prepStmt.setInt(3, coefficient);
					prepStmt.execute();
				}
				reactionID++;
			}
			
			// get the path of the compound costs text file
			url = getClass().getResource("compoundCosts.txt");
			try {
				in = url.openStream();
				s = new Scanner(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while(s.hasNextLine()) {
				String[] compoundCost = s.nextLine().trim().split("\\$");
				String compound = compoundCost[0];
				int cost = 0;
				try {
					cost = Integer.parseInt(compoundCost[1]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				
					
//				String costInsert = "insert or ignore into costs(formula, cost) values('" + compound + "', " + cost + ");";
//				insertFile.println(costInsert);
				
				prepStmt = connection.prepareStatement("insert or ignore into costs(formula, cost) values(?,?);");
				prepStmt.setString(1, compound);
				prepStmt.setInt(2, cost);
				prepStmt.execute();
			}
			
			try {
				in.close();
				s.close();
//				insertFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
//			System.out.println("Inserted into table");
			disconnect();
			
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public boolean checkResource(String resource){
		boolean exists = false;
		String sql = "select formula from (select distinct formula from reactants UNION select distinct formula from products) where formula=?;"; 
		
		try {
			connection = this.connect();
			prepStmt = connection.prepareStatement(sql);
			prepStmt.setString(1, resource);
			
			ResultSet resultSet = prepStmt.executeQuery();
	      
			if (resultSet.next()) {
				exists = true;
			}
			      
			resultSet.close();
			resultSet = null;
			
//			System.out.println("Extracted information from database");
			disconnect();
		      
		} catch (SQLException e){
				e.printStackTrace();
		} 
		
		return exists;
	}
		
	private Connection connect(){
		try {
			Class.forName("org.sqlite.JDBC");
		    connection = DriverManager.getConnection("jdbc:sqlite::resource:" + getClass().getResource("chemSynth.db").toString());

//			System.out.println("---> Connected to database");
		    
		} catch (Exception e){
			System.err.println("---> Could not connect to the database");
			e.printStackTrace();
		}
		
		return connection;
	}
		
	private void disconnect() {
		try {
			if (statement != null) 
				statement.close();
			else if (prepStmt != null){
				prepStmt.close();
			}
			
			
			connection.close();
			
			connection = null;
			statement = null;
			prepStmt = null;
			
//			System.out.println("---> Disconnected from database\n");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
//	public ArrayList<String> getAttributeNames(String table){
//		
//		ArrayList<String> attributeNames = new ArrayList<String>();
//		try{
//			connection = this.connect();
//			statement = connection.createStatement();
//			
//			// Only used to get a ResultSet object back, of which we get the MetaData
//			ResultSet rs = statement.executeQuery("select * from " + table);	
//			
//			ResultSetMetaData rsmd = rs.getMetaData();		// this is what we're after
//			int columnCount = rsmd.getColumnCount();
//
//			// The column count starts from 1, for some reason..
//			for (int i = 1; i <= columnCount; i++ ) {
//				attributeNames.add(rsmd.getColumnName(i));
//			}
//		} catch (SQLException e){
//			e.printStackTrace();
//		}
//		
//		return attributeNames;
//	}
//	
//	public String toString(){
//		return "Da fuck you doin' mon'";
//	}

	public ArrayList<Integer> getReactionIDs(String desired) {
		ArrayList<Integer> reactionIDs = new ArrayList<Integer>();
		
		String sql = "select reactionID from products where formula=?;";
		
		try {
			connection = this.connect();
			prepStmt = connection.prepareStatement(sql);
			prepStmt.setString(1, desired);
			
			ResultSet resultSet = prepStmt.executeQuery();
	      
			while (resultSet.next()) {
				reactionIDs.add(resultSet.getInt("reactionID"));
			}
			      
			resultSet.close();
			resultSet = null;
			
//			System.out.println("Extracted information from database");
			disconnect();
		      
		} catch (SQLException e){
				e.printStackTrace();
		} 
		
		return reactionIDs;
	}

//	public LinkedList<String> getReactants(Integer reactionID) {
//		LinkedList<String> reactants = new LinkedList<String>();
//		
//		String sql = "select * from reactants where reactionID=?;";
//		
//		try {
//			connection = this.connect();
//			prepStmt = connection.prepareStatement(sql);
//			prepStmt.setInt(1, reactionID);
//			
//			ResultSet resultSet = prepStmt.executeQuery();
//	      
//			if (resultSet.next()) {
//				reactants.add(resultSet.getString("formula"));
//			}
//			      
//			resultSet.close();
//			resultSet = null;
//			
////			System.out.println("Extracted information from database");
//			disconnect();
//		      
//		} catch (SQLException e) {
//				e.printStackTrace();
//		} 
//		
//		return reactants;
//	}

	public int getCoefficient(int reactionID, String formula) {
		int coefficient = 1;
		
		String productSql = "select coefficient from products where reactionID=? and formula=?;";
		String reactantSql = "select coefficient from reactants where reactionID=? and formula=?;";
		
		try {
			connection = this.connect();
			prepStmt = connection.prepareStatement(reactantSql);
			prepStmt.setInt(1, reactionID);
			prepStmt.setString(2, formula);

			ResultSet resultSet = prepStmt.executeQuery();
	      
			if (resultSet.next()) {
				coefficient = -resultSet.getInt("coefficient") ;
			}
			      
			resultSet.close();
			
			prepStmt = connection.prepareStatement(productSql);
			prepStmt.setInt(1, reactionID);
			prepStmt.setString(2, formula);
			
			resultSet = prepStmt.executeQuery();
			
			if (resultSet.next()) {
				coefficient = resultSet.getInt("coefficient");
			}
			
			resultSet.close();
			resultSet = null;
			
//			System.out.println("Extracted information from database");
			disconnect();
		      
		} catch (SQLException e){
				e.printStackTrace();
		} 
		
		return coefficient;
	}

	public ArrayList<String> getChemicals(int reactionID) {
		ArrayList<String> chemicals = new ArrayList<String>();
		
		String sql = "select distinct formula from reactants where reactionID=? union select distinct formula from products where reactionID=?;";
		
		try {
			connection = this.connect();
			prepStmt = connection.prepareStatement(sql);
			prepStmt.setInt(1, reactionID);
			prepStmt.setInt(2, reactionID);
			
			ResultSet resultSet = prepStmt.executeQuery();
	      
			while (resultSet.next()) {
				chemicals.add(resultSet.getString("formula"));
			}
			      
			resultSet.close();
			resultSet = null;
			
//			System.out.println("Extracted information from database");
			disconnect();
		      
		} catch (SQLException e){
				e.printStackTrace();
		} 
		
		return chemicals;
	}
	
	
//	public ArrayList<String> getAllCompounds() {
//		ArrayList<String> result = new ArrayList<String>();
//		
//		String sql = "select formula from reactants union select formula from products;";
//		
//		try {
//			connection = this.connect();
//			prepStmt = connection.prepareStatement(sql);
//			
//			ResultSet resultSet = prepStmt.executeQuery();
//	      
//			while (resultSet.next()) {
//				result.add(resultSet.getString("formula"));
//			}
//			      
//			resultSet.close();
//			resultSet = null;
//			
////			System.out.println("Extracted information from database");
//			disconnect();
//		      
//		} catch (SQLException e){
//				e.printStackTrace();
//		} 
//		
//		return result;
//	}

	public HashMap<String, Integer> getCompoundCosts() {
		HashMap<String, Integer> result = new HashMap<>();
		
		String sql = "select * from costs;";
		
		try {
			connection = this.connect();
			prepStmt = connection.prepareStatement(sql);
			ResultSet resultSet = prepStmt.executeQuery();
			
			int column1Pos = resultSet.findColumn("formula");
			int column2Pos = resultSet.findColumn("cost");
			
			while (resultSet.next()) {
			    String formula = resultSet.getString(column1Pos);
			    int cost = resultSet.getInt(column2Pos);
			    result.put(formula, cost);
			}
	      
			resultSet.close();
			resultSet = null;
			
//			System.out.println("Extracted information from database");
			disconnect();
		      
		} catch (SQLException e){
				e.printStackTrace();
		} 
		
		return result;
	}
	
	public HashMap<Integer, String> getReactionsFromID() {
		return reactionFromID;
	}

}

