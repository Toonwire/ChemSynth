package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.MySQLdatabase;

public class Model {

	MySQLdatabase db = null;
	
	public Model(){
		
//		sample sql query database information retrieval
		db = new MySQLdatabase();
		
		String sql = "select * from instructor natural join department where instructor.DeptName = department.DeptName and department.Building = 'Painter'";
		
		try {
		    PreparedStatement statement = db.connect().prepareStatement(sql);
		    ResultSet resultSet = statement.executeQuery(sql);
		    
		    // look through the information received from the database
		    
		    while (resultSet.next()) {
		        System.out.println("Instructor name: " + resultSet.getString("InstName"));
		    }
		    
		} catch (SQLException e) {
		    e.printStackTrace();		    
		    
		} finally {
			db.disconnect();
		}
	}
}
