package model;

import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.MySQLdatabase;

public class Model {

	private MySQLdatabase db = null;
	
	public Model(){
		
		db = new MySQLdatabase();
		
//		SELECT METHOD TEST
		String attribute = "formula", sql = "select * from compound;";
		
		ArrayList<String> list = db.select(attribute, sql);
		System.out.println("\nDisplaying information:");
		for (String s : list){
			System.out.println("\t" + attribute + ": " + s);
		}
		
		
		
	}

	public MySQLdatabase getDatabase() {
		return db;
	}
}
