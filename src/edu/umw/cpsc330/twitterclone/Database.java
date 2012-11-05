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
	 * Maximum time allowed for any SQL queries, in seconds
	 */
	private static final int TIMEOUT = 30;
	
	/**
	 * Database connection
	 */
	private Connection db;
	
	/**
	 * Default constructor initializes database connection, and creates tables
	 * if they don't exist
	 */
	public Database() {
		try {
			// initialize connection
			Class.forName(DRIVER);
			db = DriverManager.getConnection(URI);
			
			// create tables if they don't already exist
			Statement create = db.createStatement();
			create.execute("CREATE TABLE IF NOT EXISTS posts ( id INTEGER PRIMARY KEY, author TEXT, date INTEGER, isPublic INTEGER, content TEXT );");
			create.execute("CREATE TABLE IF NOT EXISTS users ( id INTEGER PRIMARY KEY, username TEXT, pwhash TEXT, name TEXT, bio TEXT );");
		} catch (ClassNotFoundException e) {
			System.err.println("Caught exception while attempting to use database driver: " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("Caught exception while doing something to the database: " + e.getMessage());
		}
	}
	
	/**
	 * Performs a query on the database
	 * @param query SQL query
	 * @return results from query
	 */
	public ResultSet query(String query) throws SQLException {
		Statement st = db.createStatement();
		st.setQueryTimeout(TIMEOUT);
		
		// return a ResultSet if there were any results, null if not
		boolean result = st.execute(query);
		return result ?
				st.getResultSet() :
					null;
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
