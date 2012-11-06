package edu.umw.cpsc330.twitterclone;

import java.awt.FlowLayout;
import java.util.List;
import javax.swing.*;

/**
 * Graphical interface for the timeline.
 * 
 * @author Alex Lindeman
 */
public class Timeline extends JFrame {

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -3104578688336085439L;
    
    /**
     * Timeline database
     */
    private PostDatabase db;
    
    /**
     * Main window
     */
    private static JFrame frame;
    
    /**
     * Main view for all posts
     */
    private static JList<Post> posts;

    /**
     * Default constructor
     */
    public Timeline () {
	db = new PostDatabase();
	setTitle("Public timeline");
	ui();
    }
    
    /**
     * Main method
     * @param args command-line arguments
     */
    public static void main(String[] args) {
	new Timeline();
    }

    /**
     * Creates the user interface
     */
    public void ui() {
	frame = new JFrame();
	frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	frame.setLayout(new FlowLayout());
	
	JLabel title = new JLabel("Public Timeline");
	frame.getContentPane().add(title);

	posts = new JList<Post>(populate());
	frame.getContentPane().add(posts);
	
	frame.pack();
	frame.setVisible(true);
    }
    
    /**
     * Populates the timeline with posts
     * @return array of public posts
     */
    public Post[] populate() {
	try {
	    List<Post> data = db.getAllPublic();
	    Post[] list = data.toArray(new Post[data.size()]);
	    return list;
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(frame,
		    "An error occurred while populating the timeline:\n" + e.getMessage(),
		    "Error",
		    JOptionPane.ERROR_MESSAGE);
	    frame.dispose();
	} finally {
	    db.close();
	}

	// return an empty array instead of null, so nothing breaks
	Post[] empty = {};
	return empty;
    }
}
