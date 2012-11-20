package edu.umw.cpsc330.twitterclone;

import java.awt.*;
import java.awt.event.*;

import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

/**
 * asdf
 * 
 * @author Alex Lindeman
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
    
    public static void main(String[] args) {
	new MainWindow();
    }
    
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
	} catch (Exception e) {
	    String error = "There was an error connecting to the database:\n" + e.getMessage();
	    JOptionPane.showMessageDialog(frame, error, "Error", JOptionPane.ERROR_MESSAGE);
	    e.printStackTrace();
	}
	
	frame.setSize(720, 380);
	frame.setMinimumSize(new Dimension(480, 320));
	frame.setVisible(true);
    }
    
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

    private JPanel drawLoginPanel() {
	final JPanel panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setAlignmentX(LEFT_ALIGNMENT);
	panel.setPreferredSize(new Dimension(200, 0));
	
	JLabel hero = new JLabel("Login");
	panel.add(hero);
	
	user = new JTextField();
	user.setMaximumSize(new Dimension(10000, 24));
	panel.add(user);
	
	pass = new JPasswordField();
	pass.setMaximumSize(new Dimension(10000, 24));
	panel.add(pass);
	
	panel.add(Box.createRigidArea(new Dimension(1, 10)));
	
	JButton submit = new JButton("Login");
	frame.getRootPane().setDefaultButton(submit);
	submit.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		User validate = validateLogin(user.getText(), new String(pass.getPassword()));
		if (validate != null) {
		    auth = validate;
		    
		    frame.remove(leftPanel);
		    frame.remove(postPanel);
		    frame.revalidate();
		    frame.repaint();
		    
		    leftPanel = drawUserInfoPanel();
		    try {
			List<Post> posts = new LinkedList<Post>();
			for (String u : auth.following) {
			    List<Post> add = postDB.getByAuthor(u);
			    posts.addAll(add);
			}
			postPanel = drawPostPanel(posts);
			frame.add(postPanel, BorderLayout.CENTER);
		    } catch (Exception e) {
			String error = "There was an error connecting to the database:\n" + e.getMessage();
			JOptionPane.showMessageDialog(frame, error, "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		    }
		    frame.add(leftPanel, BorderLayout.LINE_START);
		    frame.revalidate();
		    frame.repaint();
		}
	    }
	});
	panel.add(submit);
	
	JButton register = new JButton("Register");
	panel.add(register);
	
	JButton search = new JButton("Search");
	panel.add(search);
	
	return panel;
    }
    
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
	bio.setEditable(false);
	panel.add(bio);
	
	JButton create = new JButton("New post");
	create.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(create);
	
	JButton edit = new JButton("Edit profile");
	edit.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(edit);
	
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
		} catch (Exception e) {
		    String error = "There was an error connecting to the database:\n" + e.getMessage();
		    JOptionPane.showMessageDialog(frame, error, "Error", JOptionPane.ERROR_MESSAGE);
		    e.printStackTrace();
		}
		
		frame.revalidate();
		frame.repaint();
		
		auth = null;
	    } 
	});
	panel.add(logout);
	
	return panel;
    }
    
    private User validateLogin(String username, String password) {
	try {
	    User find = userDB.get(username);
	    
	    if (find != null) {
		if (BCrypt.checkpw(password, find.pwhash)) {
		    // login successful
		    JOptionPane.showMessageDialog(frame, "Login successful!");
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
