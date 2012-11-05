package edu.umw.cpsc330.twitterclone;

import java.sql.*;

/**
 * Database methods specifically for manipulating users.
 * 
 * @author Alex Lindeman
 */
public class UserDatabase extends Database {

    /**
     * Default constructor. Attempts to create the user table if it does not
     * already exist.
     */
    public UserDatabase() {
	try {
	    Class.forName(DRIVER);
	    db = DriverManager.getConnection(URI);

	    // create user table if it doesn't already exist
	    Statement create = db.createStatement();
	    create.execute("CREATE TABLE IF NOT EXISTS users ( id INTEGER PRIMARY KEY, username TEXT UNIQUE, pwhash TEXT, name TEXT, bio TEXT );");
	} catch (ClassNotFoundException e) {
	    System.err.println("Caught exception while attempting to use database driver: " + e.getMessage());
	} catch (SQLException e) {
	    System.err.println("Caught exception while doing something to the database: " + e.getMessage());
	}
    }

    /**
     * Gets user information by ID
     * @param id user to get data for 
     * @return User
     * @throws SQLException
     */
    public User get(int id) throws SQLException {
	String sql = "SELECT FROM users WHERE id = ?;";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);

	st.setInt(1, id);

	ResultSet result = st.getResultSet();
	User u = new User();

	u.id = result.getInt("id");
	u.username = result.getString("username");
	u.pwhash = result.getString("pwhash");
	u.name = result.getString("name");
	u.bio = result.getString("bio");

	return u;
    }

    /**
     * Gets user information by username
     * @param user user to get data for
     * @return User
     * @throws SQLException
     */
    public User get(String user) throws SQLException {
	String sql = "SELECT FROM users WHERE username = ?;";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);

	st.setString(1, user);

	ResultSet result = st.getResultSet();
	User u = new User();

	u.id = result.getInt("id");
	u.username = result.getString("username");
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
    public int add(User user) throws SQLException {
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
     * @param user the user to be deleted
     * @return number of rows affected (should be 1 if the deletion was successful)
     * @throws SQLException
     */
    public int delete(int user) throws SQLException {
	String sql = "DELETE FROM users WHERE id = ?;";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);

	st.setInt(1, user);

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
    public int edit(User user) throws SQLException {
	String sql = "UPDATE users SET ( pwhash = ?, name = ?, bio = ? ) WHERE id = ?;";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);

	st.setString(1, user.pwhash);
	st.setString(2, user.name);
	st.setString(3, user.bio);
	st.setInt(4, user.id);

	st.execute();
	int result = st.getUpdateCount();
	return result;
    }
}
