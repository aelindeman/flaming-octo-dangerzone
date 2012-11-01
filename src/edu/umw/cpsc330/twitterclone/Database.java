package edu.umw.cpsc330.twitterclone;

import java.sql.*;
import java.util.Date;

/**
 * Class for the database of users and posts.
 * 
 * @author Alex Lindeman
 */
public class Database 
{
	/**
	 * Driver to use
	 */
	private static final String DRIVER = "org.sqlite.JDBC";
	
	/**
	 * URI to the database
	 */
	private static final String URI = "jdbc:sqlite:data.db";
	
	/**
	 * Database connection
	 */
	private Connection db;
	
	/**
	 * Default constructor initializes database connection
	 */
	public Database() {
		try {
			Class.forName(DRIVER);
			db = DriverManager.getConnection(URI);
		} catch (ClassNotFoundException e) {
			System.err.println("Caught exception while attempting to use database driver: " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("Caught exception while connecting to database: " + e.getMessage());
		}
	}
}
