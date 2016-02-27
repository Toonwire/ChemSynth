package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class MySQLdatabase {

	private Connection connection = null;
	private Statement statement = null;
	
	public MySQLdatabase() {
		
		createTable();
		insertIntoTable();
	}
	
	public void createTable(){
		
		try{
			connection = this.connect();
			// TODO : Better change to PreparedStatement for SQL security purposes.
			statement = connection.createStatement();
			
//		    String sql = "create table if not exists compound (id integer primary key autoincrement, chemName varchar(50) unique not null, formula varchar(30), charge int, altName varchar(50));";
		    String sql2 = "create table if not exists reactants (reactionID integer, formula varchar(30), amount integer, PRIMARY KEY(reactionID, formula))";
		    String sql3 = "create table if not exists products (reactionID integer, formula varchar(30), amount integer, PRIMARY KEY(reactionID, formula))";
			
//		    String sql = "drop table reactants";
//		    String sql4 = "drop table products";
		   
		    statement.executeUpdate(sql2);
		    statement.executeUpdate(sql3);
		    
		    System.out.println("Created table");

		    disconnect();
		    
		} catch (SQLException e) {
			System.err.println("Could not create table");
			e.printStackTrace();
		}	
	}

	public void insertIntoTable(){
		
		try{
			
			connection = this.connect();
			statement = connection.createStatement();
			
			// read all inserts from text file to avoid big ass code spam
			Scanner s = new Scanner(new File("inserts_into_table.txt"));
			while(s.hasNextLine()){
				statement.executeUpdate(s.nextLine());
			}
			s.close();
			
			System.out.println("Inserted into table");
			disconnect();
			
		} catch (SQLException | FileNotFoundException e){
			System.err.println("Most likely, the file wasn't found - see StackTrace");
			e.printStackTrace();
		}
	}
	
	public boolean checkResource(String sql, String resource){

//		ArrayList<String> info = new ArrayList<String>();
		boolean exists = false;
		
		try {
			connection = this.connect();
			statement = connection.createStatement();
			PreparedStatement prepStmt = connection.prepareStatement(sql);
			prepStmt.setString(1, resource);
			
			ResultSet resultSet = prepStmt.executeQuery();
	      
			if (resultSet.next()) {
				exists = true;
			}
			      
			resultSet.close();
			resultSet = null;
			
			System.out.println("Extracted information from database");
			disconnect();
		      
		} catch (SQLException e){
				e.printStackTrace();
		} 
		
		return exists;
	}
		
	private Connection connect(){
		try {
			
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:chemSynth.db");
			System.out.println("---> Connected to database");
			
		} catch (Exception e){
			System.err.println("---> Could not connect to the database");
			e.printStackTrace();
		}
		
		return connection;
	}
		
	private void disconnect() {
		try {
			statement.close();
			connection.close();
			
			connection = null;
			statement = null;
			
			System.out.println("---> Disconnected from database\n");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getAttributeNames(String table){
		
		ArrayList<String> attributeNames = new ArrayList<String>();
		try{
			connection = this.connect();
			statement = connection.createStatement();
			
			// Only used to get a ResultSet object back, of which we get the MetaData
			ResultSet rs = statement.executeQuery("select * from " + table);	
			
			ResultSetMetaData rsmd = rs.getMetaData();		// this is what we're after
			int columnCount = rsmd.getColumnCount();

			// The column count starts from 1, for some reason..
			for (int i = 1; i <= columnCount; i++ ) {
				attributeNames.add(rsmd.getColumnName(i));
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		return attributeNames;
	}
}

