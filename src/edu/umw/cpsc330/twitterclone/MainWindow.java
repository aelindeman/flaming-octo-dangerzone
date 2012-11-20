package edu.umw.cpsc330.twitterclone;

import java.awt.*;
import java.awt.event.*;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

/**
 * The main flaming-octo-dangerzone window.
 * 
 * @author Alex Lindeman
 * @author Aaron Crowe
 */
public class MainWindow extends JFrame {
    
    private static final long serialVersionUID = 6261794915072960904L;

    private JFrame frame;
    
    private JTextField user;
    private JPasswordField pass;
    
    /**
     * The currently-logged-in user
     */
    private User auth;
    
    private static Component leftPanel;
    private static Component postPanel;
    
    private PostDatabase postDB = new PostDatabase();
    private UserDatabase userDB = new UserDatabase();
    
    /**
     * Main method
     */
    public static void main(String[] args) {
	new MainWindow();
    }
    
    /**
     * Default constructor
     */
    private MainWindow() {
	frame = new JFrame();
	frame.setTitle("Public timeline - flaming-octo-dangerzone");
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	frame.setLayout(new BorderLayout());
	
	leftPanel = drawLoginPanel();
	frame.add(leftPanel, BorderLayout.LINE_START);
	
	try {
	    postPanel = drawPostPanel(postDB.getAllPublic());
	    frame.add(postPanel, BorderLayout.CENTER);
	} catch (SQLException e) {
	    String error = "There was an error connecting to the database.\n" + e.getMessage();
	    JOptionPane.showMessageDialog(frame, error, "Error", JOptionPane.ERROR_MESSAGE);
	    e.printStackTrace();
	}
	
	frame.setSize(720, 380);
	frame.setMinimumSize(new Dimension(480, 320));
	frame.setVisible(true);
    }
    
    /**
     * Draws the post panel
     * 
     * @param posts Data for posts
     * @return JPanel
     */
    private JPanel drawPostPanel(java.util.List<Post> posts) {
	final JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	
	JTable table = new JTable();
	table.setFillsViewportHeight(true);
	
	JScrollPane scroller = new JScrollPane(table);
	panel.add(scroller);
	
	table.setModel(new PostDisplayModel(posts));
	table.getColumnModel().getColumn(2).setPreferredWidth(250);
	
	return panel;
    }

    /**
     * Draws the login panel
     * 
     * @return JPanel
     */
    private JPanel drawLoginPanel() {
	final JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setPreferredSize(new Dimension(200, 0));
	
	JLabel hero = new JLabel("Login");
	hero.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(hero);
	
	user = new JTextField();
	user.setMaximumSize(new Dimension(10000, 32));
	user.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(user);
	
	pass = new JPasswordField();
	pass.setMaximumSize(new Dimension(10000, 32));
	pass.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(pass);
	
	panel.add(Box.createGlue());
	
	// login button
	JButton submit = new JButton("Login");
	submit.setAlignmentX(LEFT_ALIGNMENT);
	frame.getRootPane().setDefaultButton(submit);
	submit.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		User validate = validateLogin(user.getText(), new String(pass.getPassword()));
		if (validate != null) {
		    auth = validate;
		    
		    frame.remove(leftPanel);
		    frame.remove(postPanel);
		    
		    leftPanel = drawUserInfoPanel();
		    try {
			List<Post> posts = new LinkedList<Post>();
			posts.addAll(postDB.getByAuthor(auth.username));
			for (String u : auth.following) {
			    List<Post> add = postDB.getByAuthor(u);
			    posts.addAll(add);
			}
			postPanel = drawPostPanel(posts);
			frame.add(postPanel, BorderLayout.CENTER);
		    } catch (SQLException e) { }
		    frame.add(leftPanel, BorderLayout.LINE_START);
		    frame.revalidate();
		    frame.repaint();
		}
	    }
	});
	panel.add(submit);
	
	// register button
	JButton register = new JButton("Register");
	register.setAlignmentX(LEFT_ALIGNMENT);
	register.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		// TODO: spawn a new user window
	    }
	});
	panel.add(register);
	
	// search button
	JButton search = new JButton("Search");
	search.setAlignmentX(LEFT_ALIGNMENT);
	search.addActionListener(searchBox());
	panel.add(search);
	
	return panel;
    }
    
    /**
     * Draws the user information panel
     * 
     * @return JPanel
     */
    private JPanel drawUserInfoPanel() {
	final JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setPreferredSize(new Dimension(200, 0));
	
	JLabel username = new JLabel("@" + auth.username);
	username.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(username);
	
	JLabel name = new JLabel(auth.name);
	name.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(name);
	
	JTextArea bio = new JTextArea(auth.bio);
	bio.setAlignmentX(LEFT_ALIGNMENT);
	bio.setBackground(frame.getBackground());
	bio.setEditable(false);
	panel.add(bio);
	
	// new post button
	JButton create = new JButton("New post");
	create.setAlignmentX(LEFT_ALIGNMENT);
	create.addActionListener(new ActionListener() {
	   public void actionPerformed(ActionEvent arg0) {
	       Post p = new Post();
	       p.author = auth.username;
	       p.date = new Date();
	       
	       String pc = JOptionPane.showInputDialog(frame, "Enter your post:\n(keep it under 140 characters)", "New post", JOptionPane.QUESTION_MESSAGE);
	       if (pc != null)
		   p.setContent(pc);
	       else
		   return;
	       
	       String[] publicOptions = {"Everyone", "Just followers", "Cancel (don't post at all)"};
	       int visibility = JOptionPane.showOptionDialog(frame, "Make this post public to everyone, or just your followers?", "New post", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, publicOptions, publicOptions[0]);
	       
	       // 2 means cancel
	       if (visibility == 2)
		   return;
	       p.isPublic = (visibility == 0);
	       
	       try {
		   postDB.add(p);
		   redrawTable(getFollowedUsersPosts());
	       } catch (SQLException e) { }
	       
	       JOptionPane.showMessageDialog(frame, "Post submitted successfully!");
	   }
	});
	panel.add(create);
	
	// edit profile button
	JButton edit = new JButton("Edit profile");
	edit.setAlignmentX(LEFT_ALIGNMENT);
	edit.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		// TODO: spawn an editor window of some kind...
	    }
	});
	panel.add(edit);
	
	// search button
	JButton search = new JButton("Search");
	edit.setAlignmentX(LEFT_ALIGNMENT);
	search.addActionListener(searchBox());
	panel.add(search);
	
	// logout button
	JButton logout = new JButton("Log out");
	logout.setAlignmentX(LEFT_ALIGNMENT);
	logout.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		// remove the left panel and replace it with the login panel
		frame.remove(leftPanel);
		frame.remove(postPanel);
		frame.revalidate();
		frame.repaint();

		leftPanel = drawLoginPanel();
		frame.add(leftPanel, BorderLayout.LINE_START);
		
		try {
		    postPanel = drawPostPanel(postDB.getAllPublic());
		    frame.add(postPanel, BorderLayout.CENTER);
		} catch (SQLException e) { }
		
		frame.revalidate();
		frame.repaint();
		
		auth = null;
	    } 
	});
	panel.add(logout);
	
	return panel;
    }
    
    /**
     * Redraws the table
     * 
     * @param data Data to use
     */
    private void redrawTable(List<Post> data) {
	frame.remove(postPanel);
	
	postPanel = drawPostPanel(data);
	frame.add(postPanel, BorderLayout.CENTER);
	
	frame.revalidate();
	frame.repaint();
    }
    
    /**
     * An ActionListener that spawns a search box
     * 
     * @return ActionListener
     */
    private ActionListener searchBox() {
	return new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		String term = JOptionPane.showInputDialog(frame, "Enter something to search for:\nPosts by a user: '@username'\nHashtags: '#hashtag'", "Search", JOptionPane.QUESTION_MESSAGE);
		if (term != null)
		{
		    try {
			// TODO: just search all public posts for now... bleh
			List<Post> all = postDB.getAllPublic();
			List<Post> results = new LinkedList<Post>();
			
			for (Post p : all) {
			    if (p.getContent().contains(term))
			    {
				results.add(p);
			    }
			}
			redrawTable(results);
			
		    } catch (SQLException e) { }
		}
	    }
	};
    }
    
    /**
     * Gets posts from the current user's list of followers
     * 
     * @return List of posts
     * @throws SQLException
     */
    private List<Post> getFollowedUsersPosts() throws SQLException {
	List<Post> posts = new LinkedList<Post>();
	posts.addAll(postDB.getByAuthor(auth.username));
	for (String u : auth.following) {
	    List<Post> add = postDB.getByAuthor(u);
	    posts.addAll(add);
	}
	return posts;
    }
    
    /**
     * Validates input from the login form
     * 
     * @param username Username text
     * @param password Password text
     * @return User to be logged in as
     */
    private User validateLogin(String username, String password) {
	try {
	    User find = userDB.get(username);
	    
	    if (find != null) {
		if (BCrypt.checkpw(password, find.pwhash)) {
		    // login successful
		    // JOptionPane.showMessageDialog(frame, "Login successful!");
		    return find;
		}
	    }
	} catch (SQLException e) { }
	
	// show error message and clear password entry
	JOptionPane.showMessageDialog(frame, "Incorrect username or password.", "Authentication failure", JOptionPane.WARNING_MESSAGE);
	pass.setText("");
	
	return null;
    }
}
