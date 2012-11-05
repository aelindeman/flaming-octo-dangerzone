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
    protected static final String DRIVER = "org.sqlite.JDBC";

    /**
     * URI to the database
     */
    protected static final String URI = "jdbc:sqlite:data.db";

    /**
     * Maximum time allowed for any SQL queries, in seconds
     */
    protected static final int TIMEOUT = 30;

    /**
     * Database connection
     */
    protected Connection db;

    /**
     * Default constructor initializes database connection, and creates tables
     * if they don't exist
     */
    public Database() {
	try {
	    // initialize connection
	    Class.forName(DRIVER);
	    db = DriverManager.getConnection(URI);
	} catch (ClassNotFoundException e) {
	    System.err.println("Caught exception while attempting to use database driver: " + e.getMessage());
	} catch (SQLException e) {
	    System.err.println("Caught exception while doing something to the database: " + e.getMessage());
	}
    }

    /**
     * Performs a manual query on the database
     * @param query SQL query
     * @return results from query
     * @throws SQLException
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
	    System.err.println("Caught exception while closing connection to database: " + e.getMessage());
	}
    }
}
