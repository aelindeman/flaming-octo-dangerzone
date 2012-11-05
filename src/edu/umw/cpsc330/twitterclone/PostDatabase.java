package edu.umw.cpsc330.twitterclone;

import java.sql.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Database methods specifically for manipulating posts.
 * 
 * @author Alex Lindeman
 */
public class PostDatabase extends Database {
    
    /**
     * Sets an arbitrary limit on the number of records that SQL queries
     * retrieve to increase performance.
     */
    private static final int LIMIT = 500;

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

    /**
     * Gets a post by its ID
     * @param id ID of the post
     * @return Post
     * @throws SQLException
     */
    public Post getByID(int id) throws SQLException {
	String sql = "SELECT * FROM posts WHERE id = ?";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);
	st.setInt(1, id);
	
	ResultSet results = st.executeQuery();
	List<Post> parsed = parseResults(results);
	
	// there should only be 1 element in the list, or we have duped IDs
	assert (parsed.size() == 1);
	return parsed.get(0);
    }
    
    /**
     * Gets all posts
     * @return List of posts
     * @throws SQLException
     */
    public List<Post> getAll() throws SQLException {
	String sql = "SELECT * FROM posts ORDER BY date DESC LIMIT ?";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);
	st.setInt(1, LIMIT);
	
	ResultSet results = st.executeQuery();
	List<Post> parsed = parseResults(results);
	return parsed;
    }

    /**
     * Gets all public posts
     * @return List of posts
     * @throws SQLException
     */
    public List<Post> getAllPublic() throws SQLException {
	String sql = "SELECT * FROM posts WHERE isPublic = 1 ORDER BY date DESC LIMIT ?";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);
	st.setInt(1, LIMIT);
	
	ResultSet results = st.executeQuery();
	List<Post> parsed = parseResults(results);
	return parsed;
    }

    /**
     * Get all posts by an author (searched for by username)
     * @param user User whose posts to fetch
     * @return List of posts
     * @throws SQLException
     */
    public List<Post> getByAuthor(String user) throws SQLException {
	String sql = "SELECT * FROM posts WHERE author = ? ORDER BY date DESC LIMIT ?";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);
	
	st.setString(1, user);
	st.setInt(2, LIMIT);
	
	ResultSet results = st.executeQuery();
	List<Post> parsed = parseResults(results);
	return parsed;
    }

    /**
     * Get all posts by an author (searched for by ID)
     * @param user User whose posts to fetch
     * @return List of posts
     * @throws SQLException
     */
    public List<Post> getByAuthor(int user) throws SQLException {
	UserDatabase ud = new UserDatabase();
	User resolved = ud.get(user);

	return getByAuthor(resolved.id);
    }

    // TODO: get all posts with an @mention to someone
    public List<Post> getByMention(String mention) throws SQLException {
	return null;
    }

    // TODO: get all posts with a hashtag
    public List<Post> getByHashtag(String hashtag) throws SQLException {
	return null;
    }
    
    /**
     * Iterates through a ResultSet and converts it to a list
     * @param results Results of a SQL query
     * @return List of posts
     * @throws SQLException
     */
    private static List<Post> parseResults(ResultSet results) throws SQLException {
	List<Post> postList = new LinkedList<Post>();

	while (results.next()) {
	    Post p = new Post();

	    p.id = results.getInt("id");
	    p.author = results.getString("author");
	    long date = results.getInt("date") * 1000L; // milliseconds
	    p.isPublic = results.getInt("isPublic") == 1;
	    p.setContent(results.getString("content"));

	    p.date = new Date(date);

	    postList.add(p);
	}

	return postList;
    }

    /**
     * Adds a post to the database
     * @param post Post to add
     * @return number of rows affected (should be 1 if post was successful)
     * @throws SQLException
     */
    public int add(Post post) throws SQLException {
	String sql = "INSERT INTO posts VALUES ( null, ?, ?, ?, ? )";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);

	// convert date and publicity to fit in the database
	int date = (int) Math.floor(post.date.getTime() / 1000);
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
	String sql = "DELETE FROM posts WHERE id = ?";
	PreparedStatement st = db.prepareStatement(sql);
	st.setQueryTimeout(TIMEOUT);

	st.setInt(1, id);

	st.execute();
	int result = st.getUpdateCount();
	return result;
    }
}
