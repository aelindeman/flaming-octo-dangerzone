package edu.umw.cpsc330.twitterclone;

import java.util.LinkedList;
import java.util.List;

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
     * Hash of the user's password
     */
    public String pwhash;
    
    /**
     * Salt of the user's password
     */
    public String pwsalt;

    /**
     * Display name of the user
     */
    public String name;

    /**
     * Short biography of the user to display on their profile
     */
    public String bio;
    
    /**
     * List of users the user is following
     */
    public List<String> following;

    /**
     * Default constructor
     */
    public User() {
	following = new LinkedList<String>();
    }
    
    /**
     * Default constructor
     * @param username Username
     */
    public User(String username) {
	this.username = username;
	following = new LinkedList<String>();
    }
}
