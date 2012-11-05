package edu.umw.cpsc330.twitterclone;

import java.sql.*;
import java.util.List;

/**
 * Database methods specifically for manipulating posts.
 * 
 * @author Alex Lindeman
 */
public class PostDatabase extends Database {

    /**
     * Default constructor. Attempts to create the post table if it doesn't
     * already exist.
     */
    public PostDatabase() {
	try {
	    Class.forName(DRIVER);
	    db = DriverManager.getConnection(URI);

	    // create post table if it doesn't already exist
	    Statement create = db.createStatement();
	    create.execute("CREATE TABLE IF NOT EXISTS posts ( id INTEGER PRIMARY KEY, author TEXT, date INTEGER, isPublic INTEGER, content TEXT );");
	} catch (ClassNotFoundException e) {
	    System.err.println("Caught exception while attempting to use database driver: " + e.getMessage());
	} catch (SQLException e) {
	    System.err.println("Caught exception while doing something to the database: " + e.getMessage());
	}
    }

    // get post by ID
    public Post getByID(int id) throws SQLException {

    }

    // get all public posts
    public List<Post> getAllPublicPosts() throws SQLException {

    }

    // get all posts by author (username)
    public List<Post> getByAuthor(String user) throws SQLException {

    }

    // get all posts by author (id)
    public List<Post> getByAuthor(int user) throws SQLException {

    }

    // get all posts with an @mention to someone
    public List<Post> getByMention(String mention) throws SQLException {

    }

    // get all posts with a hashtag
    public List<Post> getByHashtag(String hashtag) throws SQLException {

    }

    /**
     * Adds a post to the database
     * @param post Post to add
     * @return number of rows affected (should be 1 if post was successful)
     * @throws SQLException
     */
    public int add(Post post) throws SQLException {
	String sql = "INSERT INTO users VALUES ( null, ?, ?, ?, ? );";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);

	// convert date and publicity to fit in the database
	int date = (int) post.date.getTime();
	int isPublic = post.isPublic ? 1 : 0;
	
	st.setString(1, post.author);
	st.setInt(2, date);
	st.setInt(3, isPublic);
	st.setString(4, post.getContent());

	st.execute();
	int result = st.getUpdateCount();
	return result;
    }

    /**
     * Deletes a post from the database
     * @param id the post's ID
     * @return number of rows affected
     * @throws SQLException
     */
    public int delete(int id) throws SQLException {
	String sql = "DELETE FROM posts WHERE id = ?;";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);

	st.setInt(1, id);

	st.execute();
	int result = st.getUpdateCount();
	return result;
    }
}
