package edu.umw.cpsc330.twitterclone;

import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ProfileEditor extends JDialog implements ActionListener {

    private static final long serialVersionUID = 6536937420868602194L;

    private UserDatabase db;
    private User auth;
    
    private List<String> following = new LinkedList<String>();
    
    private JPanel panel;
    private JTextField username;
    private JPasswordField password;
    private JTextField name;
    private JTextArea bio;

    private JList<String> followList;
    private JButton addFollow;
    private JButton stopFollow;
    
    private JButton submit;
    private JButton cancel;

    /**
     * Creates a profile editor window
     * 
     * @param frame Parent frame
     * @param modal Set as a modal dialog
     * @param db UserDatabase connection
     * @param auth Currently authenticated user (if applicable)
     * @param title Title of the dialog
     */
    public ProfileEditor(JFrame frame, boolean modal, UserDatabase db, User auth, String title) {
	super(frame, modal);
	this.setTitle(title);
	this.setResizable(false);
	
	this.db = db;
	
	panel = new JPanel();
	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.setBorder(new EmptyBorder(10, 10, 10, 10));
	this.getContentPane().add(panel);
	
	// username
	JLabel usernameLabel = new JLabel("Username");
	usernameLabel.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(usernameLabel);
	username = new JTextField();
	username.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(username);
	
	// password
	JLabel passwordLabel = new JLabel("Password"); 
	passwordLabel.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(passwordLabel);
	password = new JPasswordField();
	password.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(password);
	
	panel.add(Box.createVerticalStrut(20));
	
	// name
	JLabel nameLabel = new JLabel("Name");
	nameLabel.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(nameLabel);
	name = new JTextField();
	name.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(name);
	
	// bio
	panel.add(new JLabel("Bio"));
	bio = new JTextArea();
	bio.setAlignmentX(LEFT_ALIGNMENT);
	bio.setBorder(name.getBorder());
	bio.setFont(name.getFont());
	bio.setLineWrap(true);
	bio.setRows(4);
	bio.setWrapStyleWord(true);
	panel.add(bio);
	
	panel.add(Box.createVerticalStrut(20));
	
	// following
	if (auth != null) {
	    panel.add(new JLabel("Following"));
	    followList = new JList<String>(); 

	    JScrollPane scroll = new JScrollPane(followList);
	    scroll.setAlignmentX(LEFT_ALIGNMENT);
	    scroll.setPreferredSize(new Dimension(0, 60));
	    panel.add(scroll);

	    JPanel followButtons = new JPanel();
	    followButtons.setAlignmentX(LEFT_ALIGNMENT);
	    followButtons.setLayout(new BoxLayout(followButtons, BoxLayout.X_AXIS));

	    addFollow = new JButton("+");
	    addFollow.setBorder(new EmptyBorder(4, 6, 4, 6));
	    addFollow.addActionListener(this);
	    followButtons.add(addFollow);

	    stopFollow = new JButton("-");
	    stopFollow.setBorder(new EmptyBorder(4, 6, 4, 6));
	    stopFollow.addActionListener(this);
	    followButtons.add(stopFollow);

	    panel.add(followButtons);

	    panel.add(Box.createVerticalStrut(10));
	}
	
	// submit/cancel buttons
	JPanel buttonRow = new JPanel();
	buttonRow.setAlignmentX(LEFT_ALIGNMENT);
	buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
	
	submit = new JButton("Save changes");
	submit.addActionListener(this);
	panel.getRootPane().setDefaultButton(submit);
	buttonRow.add(submit);
	
	cancel = new JButton("Cancel");
	cancel.addActionListener(this);
	buttonRow.add(cancel);
	
	panel.add(buttonRow);
	
	// populate fields if a user was specified
	if (auth != null) {
	    this.auth = auth;
	    username.setText(auth.username);
	    username.setEditable(false);
	    name.setText(auth.name);
	    bio.setText(auth.bio);
	    
	    if (auth.following.size() > 0)
	    {
		// copy
		for (String u : auth.following)
		    following.add(u);
		
		followList.setListData(following.toArray(new String[following.size()]));
	    }
	}

	pack();
        setLocationRelativeTo(frame);
        setVisible(true);
    }
    
    public boolean validateInputs() {
	// validate inputs
	if (username.getText().length() == 0)
	{
	    JOptionPane.showMessageDialog(panel, "Username cannot be blank.", null, JOptionPane.WARNING_MESSAGE);
	    return false;
	}
	
	if (auth == null && password.getPassword().length == 0)
	{
	    JOptionPane.showMessageDialog(panel, "Password cannot be blank.", null, JOptionPane.WARNING_MESSAGE);
	    return false;
	}
	
	if (name.getText().length() == 0)
	{
	    JOptionPane.showMessageDialog(panel, "Name cannot be blank.", null, JOptionPane.WARNING_MESSAGE);
	    return false;
	}
	
	return true;
    }
    
    /**
     * Writes changes to the database
     * @return rows affected
     */
    public int submitChanges() {
	boolean creatingNewUser = (auth == null);
	User u = new User();
	
	// validate
	if (!validateInputs())
	    return 0;
	
	if (creatingNewUser) {
	    // new user
	    u.username = username.getText();
	    u.name = name.getText();
	    u.bio = bio.getText();
	    String salt = BCrypt.gensalt();
	    u.pwhash = BCrypt.hashpw(new String(password.getPassword()), salt);
	    u.pwsalt = salt;
	    
	    u.following = new LinkedList<String>();
	} else {
	    // editing existing user
	    u.username = auth.username;
	    u.name = name.getText();
	    u.bio = bio.getText();
	    u.pwsalt = auth.pwsalt;
	    
	    if (password.getPassword().length > 0)
		u.pwhash = BCrypt.hashpw(new String(password.getPassword()), auth.pwsalt);
	    else
		u.pwhash = auth.pwhash;
	    
	    u.following = following;
	}
	
	int result = 0;
	try {
	    if (creatingNewUser) {
		// check that the username isn't already taken
		if (db.get(u.username) != null) {
		    JOptionPane.showMessageDialog(panel, "That username is taken.\nPick another username.", "Create user", JOptionPane.ERROR_MESSAGE);
		    return 0;
		}
		
		result = db.add(u);
		auth = u;
	    } else {
		result = db.edit(u);
		auth = u;
	    }
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(panel, "An error occured while trying to save:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
	    e.printStackTrace();
	}
	
	return result;
    }
    /**
     * Handles button press events
     */
    public void actionPerformed(ActionEvent arg0) {
	if (arg0.getSource() == submit) {
	    int result = submitChanges();
	    if (result == 1) {
		JOptionPane.showMessageDialog(panel, "Changes saved.");
		this.dispose();
	    }
	}
	
	if (arg0.getSource() == cancel) {
	    this.dispose();
	}
	
	if (arg0.getSource() == addFollow) {
	    String input = JOptionPane.showInputDialog(panel, "Username to follow:");
	    if (input != null) {
		if (input.length() > 0) {
		    following.add(input);
		    followList.setListData(following.toArray(new String[following.size()]));
		    followList.repaint();
		}
	    }
	}
	
	if (arg0.getSource() == stopFollow) {
	    if (followList.getSelectedIndex() != -1) {
		String drop = followList.getSelectedValue();
		if (drop.length() > 0) {
		    if (following.size() != 0) {
			int sure = JOptionPane.showConfirmDialog(panel, "Are you sure you want to stop following " + drop + "?", "Unfollow a user", JOptionPane.YES_NO_OPTION);
			if (sure == JOptionPane.YES_OPTION) { 
			    following.remove(drop);
			    followList.setListData(following.toArray(new String[following.size()]));
			    followList.repaint();
			}
		    }
		}
	    }
	}
    }
}