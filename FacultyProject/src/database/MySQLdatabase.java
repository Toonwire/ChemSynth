package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLdatabase {

	private Connection connection = null;
	
	public Connection connect() {
		
		String url = "jdbc:mysql://localhost:3306/chemical_reaction";
		String username = "root";
		String password = "chemsynth";


		// establish a connection to the database
		try {
			connection = DriverManager.getConnection(url, username, password);
		    
		} catch (SQLException e) {
		    throw new IllegalStateException("Cannot connect the database!", e);
		}
		

		// Load the jdbc driver from classpath (check referenced libraries for .jar file)
		try {
		    Class.forName("com.mysql.jdbc.Driver");
		    
		} catch (ClassNotFoundException e) {
		    throw new IllegalStateException("Cannot find the driver in the classpath!", e);
		}
		
		return connection;
	}
	
	
	public void disconnect() {
	    if (connection != null) {
	        try {
	            connection.close();	            
	            connection = null;
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
}

