package edu.umw.cpsc330.twitterclone;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;

/**
 * Graphical interface for the timeline.
 * 
 * @author Alex Lindeman
 */
public class Timeline extends JFrame implements ActionListener {

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
    private static JList<String> posts;

    /**
     * Default constructor
     */
    public Timeline () {
	db = new PostDatabase();
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
	frame = new JFrame("Public Timeline");
	frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	frame.setLayout(new GridBagLayout());
	
	GridBagConstraints c = new GridBagConstraints();
	c.fill = GridBagConstraints.HORIZONTAL;
	
	JButton[] toolbar = {
	    new JButton("Refresh"),
	    new JButton("Register"),
	    new JButton("Login")
	};
	
	for (int i = 0; i < toolbar.length; i ++) {
	    c.weightx = 0.333;
	    c.weighty = 0;
	    c.gridx = i;
	    c.gridy = 1;
	    
	    frame.add(toolbar[i], c);
	}
	
	posts = new JList<String>(populate());
	
	JScrollPane postScrollPane = new JScrollPane(posts);
	postScrollPane.setPreferredSize(new Dimension(350, 500));

	c.weightx = 1;
	c.weighty = 1;
	c.gridwidth = 3;
	c.fill = GridBagConstraints.BOTH;
	c.gridx = 0;
	c.gridy = 0;
	frame.add(postScrollPane, c);
	
	frame.pack();
	frame.setVisible(true);
    }
    
    /**
     * Populates the timeline with posts
     * @return array of public posts
     */
    public String[] populate() {
	try {
	    List<Post> data = db.getAllPublic();
	    Post[] list = data.toArray(new Post[data.size()]);
	    
	    String[] display = new String[list.length];
	    for (int i = 0; i < list.length; i ++) {
		Format f = new SimpleDateFormat("MMM F YYYY, h:mm:ss aa");
		String date = f.format(list[i].date);
		
		display[i] = "<html><font color=navy>" + list[i].author + " - "
			+ date + "</font><br><font size=+1>"
			+ list[i].getContent() + "</font></html>";
	    }
	    
	    return display;
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(frame,
		    "An error occurred while populating the timeline:\n" + e.getMessage(),
		    "Error",
		    JOptionPane.ERROR_MESSAGE);
	    frame.dispose();
	}
	
	return null;
    }

    /**
     * Method called when an action is performed
     */
    public void actionPerformed(ActionEvent arg0) {
	
    }
}
