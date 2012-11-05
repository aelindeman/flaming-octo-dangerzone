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
	    create.execute("CREATE TABLE IF NOT EXISTS users ( id INTEGER PRIMARY KEY, username TEXT UNIQUE, pwhash TEXT, name TEXT, bio TEXT );");
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
     * Gets user information 
     * @param username user to get data for 
     * @return User
     * @throws SQLException
     */
    public User getUser(String user) throws SQLException {
	String sql = "SELECT FROM users WHERE username = ?;";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);
	
	st.setString(1, user);
	
	ResultSet result = st.getResultSet();
	User u = new User();
	
	u.id = result.getInt("id");
	u.username = user;
	u.pwhash = result.getString("pwhash");
	u.name = result.getString("name");
	u.bio = result.getString("bio");
	
	return u;
    }
    
    /**
     * Adds a user to the database
     * @param user the user to be added
     * @return number of rows affected (should be 1 if the add was successful)
     * @throws SQLException
     */
    public int addUser(User user) throws SQLException {
	String sql = "INSERT INTO users VALUES ( null, ?, ?, ?, ? );";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);

	st.setString(1, user.username);
	st.setString(2, user.pwhash);
	st.setString(3, user.name);
	st.setString(4, user.bio);
	
	st.execute();
	int result = st.getUpdateCount();
	return result;
    }
    
    /**
     * Removes a user from the database
     * @param username the user to be deleted
     * @return number of rows affected (should be 1 if the deletion was successful)
     * @throws SQLException
     */
    public int deleteUser(String user) throws SQLException {
	String sql = "DELETE FROM users WHERE username = ?;";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);
	
	st.setString(1, user);
	
	st.execute();
	int result = st.getUpdateCount();
	return result;
    }
    
    /**
     * Edits a user in the database
     * @param user user to edit
     * @return number of rows affected
     * @throws SQLException
     */
    public int editUser(User user) throws SQLException {
	String sql = "UPDATE users SET ( pwhash = ?, name = ?, bio = ? ) WHERE username = ?;";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);
	
	st.setString(1, user.pwhash);
	st.setString(2, user.name);
	st.setString(3, user.bio);
	st.setString(4, user.username);
	
	st.execute();
	int result = st.getUpdateCount();
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
	    System.err.println("Caught exception while closing connection to database: " + e.getMessage());
	}
    }
}
