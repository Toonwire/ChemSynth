package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.MySQLdatabase;

public class Model {

	private MySQLdatabase db = null;
	
	public Model(){
		
//		sample sql query database information retrieval
		db = new MySQLdatabase();
		
		String sql = "select * from compound where (formula like '%N%' AND formula like'%H%')";
		
		try {
		    PreparedStatement statement = db.connect().prepareStatement(sql);
		    ResultSet resultSet = statement.executeQuery(sql);
		    
		    // look through the information received from the database
		    
		    System.out.println("Compound formulas with a nitrogen and oxygen atom:");
		    while (resultSet.next()) {
		        System.out.println("\tFormula: " + resultSet.getString("formula") + "\tCharge: " + resultSet.getString("charge"));
		    }
		    
		} catch (SQLException e) {
		    e.printStackTrace();		    
		    
		} finally {
			db.disconnect();
		}
	}
}
