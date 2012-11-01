package edu.umw.cpsc330.twitterclone;

/**
 * Container class for user accounts.
 * 
 * @author Alex Lindeman
 */
public class User {
	
	/**
	 * User ID number (SQL unique key)
	 */
    public int id;
    
    /**
     * Unique username of the user
     */
    public String username;
    
    /**
     * SHA1 hash of the user's password
     */
    public String pwhash;

    /**
     * Display name of the user
     */
    public String name;
    
    /**
     * Short biography of the user to display on their profile
     */
    public String bio;

    /**
     * Default constructor
     */
    public User (String username, String name) {
    	this.username = username;
    	this.name = name;
    }
}
