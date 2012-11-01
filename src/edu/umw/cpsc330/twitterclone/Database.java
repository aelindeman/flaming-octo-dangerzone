package edu.umw.cpsc330.twitterclone;

import java.sql.*;

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
	
	/**
	 * Performs a query on the database
	 * @param query SQL query
	 * @return results from query
	 */
	public ResultSet query(String query) throws SQLException
	{
		Statement st = db.createStatement();
		st.setQueryTimeout(30);
		
		ResultSet result = st.executeQuery(query);
		
		return result;
	}
	
	/**
	 * Closes the connection to the database
	 */
	public void close() {
		try {
			if (db != null)
				db.close();
		} catch (SQLException e) {
			System.err.println ("Caught exception while closing connection to database: " + e.getMessage());
		}
	}
}
