package com.cooxmate.dbmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.cooxmate.configuration.*;
import com.cooxmate.configuration.Configuration.Enviroment;

public class DBManager {

	// Constants for creating DB
	private static final String CREATE_TABLE_RECIPE 	= "CREATE TABLE IF NOT EXISTS recipe ( recipe_id INT NOT NULL " + 
													"AUTO_INCREMENT, name VARCHAR(46) NOT NULL, publish BOOLEAN, timestamp DATETIME NOT NULL, " + 
													"type VARCHAR(46), icon_url VARCHAR(46), icon BLOB, PRIMARY KEY (recipe_id) )";

	private static final String CREATE_TABLE_STEP		= "CREATE TABLE IF NOT EXISTS step ( step_id INT NOT NULL AUTO_INCREMENT," +
													"recipe_id INT NOT NULL, description VARCHAR(248), position INT, " +
													"timer BOOLEAN, type VARCHAR(24), PRIMARY KEY (step_id), " +
													"FOREIGN KEY (recipe_id) REFERENCES recipe(recipe_id) " +
													"ON UPDATE CASCADE ON DELETE RESTRICT )";
	
	private static final String CREATE_TABLE_VARIATION	= "CREATE TABLE IF NOT EXISTS variation ( parameter_id INT NOT NULL AUTO_INCREMENT," +
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
	
	// Constants for deleting DB
	private static final String DELETE_TABLE_DEPENDENCY	= "DROP TABLE dependency";
	private static final String DELETE_TABLE_VARIATION	= "DROP TABLE variation";
	private static final String DELETE_TABLE_STEP		= "DROP TABLE step";
	private static final String DELETE_TABLE_RECIPE		= "DROP TABLE recipe";

	// Constants for demo data
	
	private static final String INSERT_DEMO_RECIPE				= "INSERT INTO cooxmate.recipe (name, publish, timestamp, type, icon_url) VALUES (\"STEAK\",TRUE,now(),\"NORMAL\", \"icon.png\")";
	private static final String INSERT_DEMO_FIRST_STEP			= "INSERT INTO cooxmate.step (recipe_id, description, position, timer, type) VALUES (1,\"salt steak etc....\",0,false, \"typeA\")";
	private static final String INSERT_DEMO_SECOND_STEP			= "INSERT INTO cooxmate.step (recipe_id, description, position, timer, type) VALUES (1,\"grill steak etc....\",0,true, \"typeA\")";
	private static final String INSERT_DEMO_VARIATION			= "INSERT INTO cooxmate.variation (step_id, time, fan_oven, regular_oven, hotplate, level) VALUES (2, \"10min\", false ,false, true, \"medium\")";
	private static final String INSERT_DEMO_VARIATION_SECOND	= "INSERT INTO cooxmate.variation (step_id, time, fan_oven, regular_oven, hotplate, level) VALUES (4, \"10min\", false ,false, true, \"medium\")";
	private static final String INSERT_DEMO_DEPENDENCY			= "INSERT INTO cooxmate.dependency (step_id, depended_step_id) VALUES (2, 1)";
	private static final String INSERT_DEMO_DEPENDENCY_SECOND	= "INSERT INTO cooxmate.dependency (step_id, depended_step_id) VALUES (4, 3)";
	
	/**
	 * Initialise the default database
	 */
	public static void initDataBaseIfNeeded() {
		
		String url = DBManager.fetchDatabaseURL();
		// Create the tables
		DBManager.createTablesIfNeeded(url);
	}

	public static void fetchRecipes(String from, String to) {
		
		
		String url = DBManager.fetchDatabaseURL();

		try {
			Connection connection = DriverManager.getConnection(url);

			// Execute SQL query
			Statement stmt = connection.createStatement();
			String sql;
			sql = "SELECT id, first, last, age FROM Employees";
			

			ResultSet rs = stmt.executeQuery(sql);				
			// Extract data from result set
			//        while(rs.next()){
			//           //Retrieve by column name
			//           int id  = rs.getInt("id");
			//           int age = rs.getInt("age");
			//           String first = rs.getString("first");
			//           String last = rs.getString("last");
			//
			//           //Display values
			//           out.println("ID: " + id + "<br>");
			//           out.println(", Age: " + age + "<br>");
			//           out.println(", First: " + first + "<br>");
			//           out.println(", Last: " + last + "<br>");
			//        }
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete old data and creates new data
	 */
	public static void createDemoData() {

		String url = DBManager.fetchDatabaseURL();

		try {
			Connection connection = DriverManager.getConnection(url);
			Statement station = connection.createStatement();

			station.execute(DELETE_TABLE_DEPENDENCY);			
			station.execute(DELETE_TABLE_VARIATION);
			station.execute(DELETE_TABLE_STEP);
			station.execute(DELETE_TABLE_RECIPE);

			station.execute(CREATE_TABLE_RECIPE);
			station.execute(CREATE_TABLE_STEP);
			station.execute(CREATE_TABLE_VARIATION);
			station.execute(CREATE_TABLE_DEPENDENCY);

			station.execute(INSERT_DEMO_RECIPE);
			station.execute(INSERT_DEMO_FIRST_STEP);
			station.execute(INSERT_DEMO_SECOND_STEP);
			station.execute(INSERT_DEMO_VARIATION);			
			station.execute(INSERT_DEMO_DEPENDENCY);

			station.execute(INSERT_DEMO_RECIPE);
			station.execute(INSERT_DEMO_FIRST_STEP);
			station.execute(INSERT_DEMO_SECOND_STEP);
			station.execute(INSERT_DEMO_VARIATION_SECOND);			
			station.execute(INSERT_DEMO_DEPENDENCY_SECOND);
			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
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
			station.execute(CREATE_TABLE_VARIATION);
			station.execute(CREATE_TABLE_DEPENDENCY);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static String fetchDatabaseURL() {

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
		return url;
	}
}
