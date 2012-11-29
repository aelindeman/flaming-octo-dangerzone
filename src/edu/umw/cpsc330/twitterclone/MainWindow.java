package edu.umw.cpsc330.twitterclone;

import java.awt.*;
import java.awt.event.*;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
    
    private JLabel usernameLabel;
    private JLabel nameLabel;
    private JTextArea bio;
    
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
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {	}
	
	frame = new JFrame();
	frame.setTitle("Public timeline - flaming-octo-dangerzone");
	frame.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
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
	frame.setLocationRelativeTo(null);
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
	
	final JTable table = new JTable();
	table.setFillsViewportHeight(true);
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  
	
	table.addMouseListener(new MouseListener() {
	    public void mouseClicked(MouseEvent arg0) {
		
		// make the left panel display information about the selected user on double-click
		if (arg0.getClickCount() >= 2) {
		    int row = table.getSelectedRow();
		    if (row >= 0) {
			try {
			    String user = table.getValueAt(row, 0).toString();
			    User info = userDB.get(user);

			    usernameLabel.setText(info.username);
			    nameLabel.setText(info.name);
			    bio.setText(info.bio);
			} catch (Exception e) { }
		    }
		}
	    }
	    public void mouseEntered(MouseEvent e) { }
	    public void mouseExited(MouseEvent e) { }
	    public void mousePressed(MouseEvent e) { }
	    public void mouseReleased(MouseEvent e) { }
	});
	
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
	panel.setBorder(new EmptyBorder(10, 10, 10, 10));
	
	// title label
	JLabel hero = new JLabel("Login");
	hero.setAlignmentX(LEFT_ALIGNMENT);
	hero.setFont(new Font(panel.getFont().getFamily(), Font.PLAIN, 24));
	panel.add(hero);
	
	// spacing between label and login form
	panel.add(Box.createVerticalStrut(10));
	
	// username input
	user = new JTextField();
	user.setMaximumSize(new Dimension(10000, 32));
	user.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(user);
	
	// spacing between username and password inputs
	panel.add(Box.createVerticalStrut(5));
	
	// password input
	pass = new JPasswordField();
	pass.setMaximumSize(new Dimension(10000, 32));
	pass.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(pass);
	
	// make username and password input fonts larger
	Font existing = user.getFont();
	Font inputFont = new Font(existing.getFamily(), existing.getStyle(), 16);
	user.setFont(inputFont);
	pass.setFont(inputFont);
	
	// spacer to push buttons to bottom
	panel.add(Box.createGlue());
	
	// login button
	JButton submit = new JButton("Login");
	submit.setAlignmentX(LEFT_ALIGNMENT);
	frame.getRootPane().setDefaultButton(submit);
	submit.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		if (user.getText().equals(""))
		    return;
		
		User validate = validateLogin(user.getText(), new String(pass.getPassword()));
		if (validate != null) {
		    auth = validate;
		    
		    frame.remove(leftPanel);
		    frame.remove(postPanel);
		    
		    leftPanel = drawUserInfoPanel();
		    try {
			postPanel = drawPostPanel(getFollowedUsersPosts());
			frame.add(postPanel, BorderLayout.CENTER);
		    } catch (Exception e) { }
		    frame.add(leftPanel, BorderLayout.LINE_START);
		    frame.setTitle("Timeline for " + auth.username + " - flaming-octo-dangerzone");
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
		new ProfileEditor(frame, true, userDB, null, "New user");
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
	panel.setBorder(new EmptyBorder(10, 10, 10, 10));
	
	// username label
	usernameLabel = new JLabel(auth.username);
	usernameLabel.setAlignmentX(LEFT_ALIGNMENT);
	usernameLabel.setFont(new Font(panel.getFont().getFamily(), Font.BOLD, 14));
	panel.add(usernameLabel);
	
	// real name label
	nameLabel = new JLabel(auth.name);
	nameLabel.setAlignmentX(LEFT_ALIGNMENT);
	nameLabel.setFont(new Font(panel.getFont().getFamily(), Font.BOLD, 14));
	panel.add(nameLabel);
	
	// bio text area
	bio = new JTextArea(auth.bio);
	bio.setAlignmentX(LEFT_ALIGNMENT);
	bio.setBackground(frame.getBackground());
	bio.setBorder(null);
	bio.setEditable(false);
	bio.setFont(new Font(panel.getFont().getFamily(), Font.PLAIN, 12));
	bio.setLineWrap(true);
	bio.setWrapStyleWord(true);
	panel.add(bio);
	
	// new post button
	JButton create = new JButton("New post");
	create.setAlignmentX(LEFT_ALIGNMENT);
	create.addActionListener(new ActionListener() {
	   public void actionPerformed(ActionEvent arg0) {
	       Post p = new Post();
	       p.author = auth.username;
	       p.date = new Date();
	       
	       // ask for post content
	       String pc = JOptionPane.showInputDialog(frame, "Enter your post:\n(keep it under 140 characters)", "New post", JOptionPane.QUESTION_MESSAGE);
	       if (pc != null)
		   p.setContent(pc);
	       else
		   return;
	       
	       // ask for public/private/cancel
	       String[] publicOptions = {"Everyone", "Just followers", "Cancel (don't post at all)"};
	       int visibility = JOptionPane.showOptionDialog(frame, "Make this post public to everyone, or just your followers?", "New post", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, publicOptions, publicOptions[0]);
	       
	       // 2 means cancel
	       if (visibility == 2)
		   return;
	       p.isPublic = (visibility == 0);
	       
	       // add to database
	       try {
		   postDB.add(p);
		   redrawTable(getFollowedUsersPosts());
	       } catch (Exception e) { }
	       
	       JOptionPane.showMessageDialog(frame, "Post submitted successfully!");
	   }
	});
	panel.add(create);
	
	// edit profile button
	JButton edit = new JButton("Edit profile");
	edit.setAlignmentX(LEFT_ALIGNMENT);
	edit.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		new ProfileEditor(frame, true, userDB, auth, "Edit profile");
		try {
		    // reload the user
		    auth = userDB.get(auth.username);
		    redrawTable(getFollowedUsersPosts());
		} catch (Exception e) { }
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
		frame.setTitle("Public timeline - flaming-octo-dangerzone");
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
		String term = JOptionPane.showInputDialog(frame, "Enter a hashtag to search for:", "#");
		if (term != null)
		{
		    frame.setTitle("Search results for '" + term + "' - flaming-octo-dangerzone");
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
			
		    } catch (Exception e) { }
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
	} catch (Exception e) { }
	
	// show error message and clear password entry
	JOptionPane.showMessageDialog(frame, "Incorrect username or password.", "Authentication failure", JOptionPane.WARNING_MESSAGE);
	pass.setText("");
	
	return null;
    }
}
