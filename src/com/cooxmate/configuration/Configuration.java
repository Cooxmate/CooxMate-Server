package com.cooxmate.configuration;

public class Configuration {
	private static Configuration instance = null;
	
	public Enviroment mode = Enviroment.Development;
	
	public enum Enviroment {
		Development, Release		
	}
	
	protected Configuration() {
		// Exists only to defeat instantiation.
	}

	public static Configuration getInstance() {
		if(instance == null) {
			instance = new Configuration();
		}
		return instance;
	}
}
