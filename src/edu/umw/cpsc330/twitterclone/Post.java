package edu.umw.cpsc330.twitterclone;

import java.util.Date;

/**
 * Container class for posts.
 * 
 * @author Alex Lindeman
 */
public class Post {
    /**
     * ID of the post.
     */
    public int id;

    /**
     * Author of the post.
     */
    public String author;
    
    /**
     * Date when the post was made.
     */
    public Date date;
    
    /**
     * Contents of the post.
     */
    private String content;

    /**
     * Defines if the message is directed at anyone or just followers of the
     * author.
     */
    public boolean is_public;
    
    /**
     * Default constructor
     * @param new_content Sets the contents of the post
     */
    public Post (String new_content) {
	date = new Date();
	setContent (new_content);
    }

    /**
     * @return Contents of the post.
     */
    public String getContent() {
	return content;
    }

    /**
     * Sets content of the post. Also checks that the post is 140 characters or
     * less.
     * @param content Content to add
     */
    public void setContent(String content) {
	if (content.length() <= 140)
	    this.content = content;
	else
	    this.content = content.substring(0, 139);
	
    }
}
