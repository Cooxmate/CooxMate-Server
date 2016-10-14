package com.cooxmate.dbmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.cooxmate.configuration.*;
import com.cooxmate.configuration.Configuration.Enviroment;

public class DBManager {

	// Constants for DB
	private static final String CREATE_TABLE_RECIPE 	= "CREATE TABLE IF NOT EXISTS recipe ( recipe_id INT NOT NULL " + 
													"AUTO_INCREMENT, name VARCHAR(46) NOT NULL, publish BOOLEAN, timestamp DATETIME NOT NULL, " + 
													"type VARCHAR(46), icon BLOB, PRIMARY KEY (recipe_id) )";

	private static final String CREATE_TABLE_STEP		= "CREATE TABLE IF NOT EXISTS step ( step_id INT NOT NULL," +
													"recipe_id INT NOT NULL, description VARCHAR(248), position INT, " +
													"timer BOOLEAN, type VARCHAR(24), PRIMARY KEY (step_id), " +
													"FOREIGN KEY (recipe_id) REFERENCES recipe(recipe_id) " +
													"ON UPDATE CASCADE ON DELETE RESTRICT )";
	
	private static final String CREATE_TABLE_PARAMETER	= "CREATE TABLE IF NOT EXISTS variation ( parameter_id INT NOT NULL," +
													"step_id INT NOT NULL, time VARCHAR(24), fan_oven BOOLEAN, regular_oven BOOLEAN, " +
													"hotplate BOOLEAN, level VARCHAR(24), PRIMARY KEY (parameter_id), " +
													"FOREIGN KEY (step_id) REFERENCES step(step_id) " +
													"ON UPDATE CASCADE ON DELETE RESTRICT )";	

	private static final String CREATE_TABLE_DEPENDENCY	= "CREATE TABLE IF NOT EXISTS dependency ( step_id INT NOT NULL," +
													"depended_step_id INT NOT NULL," +
													"FOREIGN KEY (step_id) REFERENCES step(step_id) " +
													"ON UPDATE CASCADE ON DELETE RESTRICT, " +
													"FOREIGN KEY (depended_step_id) REFERENCES step(step_id) " +
													"ON UPDATE CASCADE ON DELETE RESTRICT )";
	
	/**
	 * Initialise the default database
	 */
	public static void initDataBaseIfNeeded() {
		// An url which should be setted according to the environment
		String url = "";
		switch (Configuration.getInstance().mode) {
		case Release:
			if (Configuration.getInstance().mode == Enviroment.Development) {
				if (System.getProperty("com.google.appengine.runtime.version").startsWith("Google App Engine/")) {
					// Check the System properties to determine if we are running on appengine or not
					// Google App Engine sets a few system properties that will reliably be present on a remote
					// instance.
					url = System.getProperty("ae-cloudsql.cloudsql-database-url");
					try {
						// Load the class that provides the new "jdbc:google:mysql://" prefix.
						Class.forName("com.mysql.jdbc.GoogleDriver");
					} catch (ClassNotFoundException e) {
						System.out.println("Error loading Google JDBC Driver");
					}				
				}
			}	
		case Development:
			url = System.getProperty("ae-cloudsql.local-database-url");
		}
		
		// Create the tables
		DBManager.createTablesIfNeeded(url);
	}

	/**
	 * Create all the needed tables
	 * @param url (e.g "jdbc:mysql://localhost/cooxmate?user=USER&password=PASSWORD&useSSL=false")
	 */
	private static void createTablesIfNeeded(String url) {
		try {
			Connection connection = DriverManager.getConnection(url);
			Statement station = connection.createStatement(); 
			station.execute(CREATE_TABLE_RECIPE);
			station.execute(CREATE_TABLE_STEP);
			station.execute(CREATE_TABLE_PARAMETER);
			station.execute(CREATE_TABLE_DEPENDENCY);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
