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
		
		String sql = "select formula FROM compound WHERE formula like '%H%'";
		
		try {
		    PreparedStatement statement = db.connect().prepareStatement(sql);
		    ResultSet resultSet = statement.executeQuery(sql);
		    
		    // look through the information received from the database
		    
		    System.out.println("Compound formulas with a hydrogen atom:");
		    while (resultSet.next()) {
		        System.out.println("\t" + resultSet.getString("formula"));
		    }
		    
		} catch (SQLException e) {
		    e.printStackTrace();		    
		    
		} finally {
			db.disconnect();
		}
	}
}
