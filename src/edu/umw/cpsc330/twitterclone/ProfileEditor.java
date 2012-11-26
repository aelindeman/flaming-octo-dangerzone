package edu.umw.cpsc330.twitterclone;

import java.util.LinkedList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ProfileEditor extends JDialog implements ActionListener {

    private static final long serialVersionUID = 6536937420868602194L;

    private UserDatabase db;
    private User auth;
    
    private JPanel panel;
    private JTextField username;
    private JPasswordField password;
    private JTextField name;
    private JTextArea bio;

    private JButton addFollow;
    private JButton listFollow;
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
	
	panel.add(Box.createVerticalStrut(10));
	
	// manage followers section
	JLabel followerManagementLabel = new JLabel("Manage who you're following:");
	followerManagementLabel.setAlignmentX(LEFT_ALIGNMENT);
	panel.add(followerManagementLabel);
	
	JPanel followRow = new JPanel();
	followRow.setAlignmentX(LEFT_ALIGNMENT);
	followRow.setLayout(new BoxLayout(followRow, BoxLayout.X_AXIS));
	
	stopFollow = new JButton("-");
	stopFollow.addActionListener(this);
	stopFollow.setAlignmentX(CENTER_ALIGNMENT);
	followRow.add(stopFollow);
	
	listFollow = new JButton("List");
	listFollow.addActionListener(this);
	listFollow.setAlignmentX(CENTER_ALIGNMENT);
	followRow.add(listFollow);
	
	addFollow = new JButton("+");
	addFollow.addActionListener(this);
	addFollow.setAlignmentX(CENTER_ALIGNMENT);
	followRow.add(addFollow);
	
	panel.add(followRow);
	
	panel.add(Box.createVerticalStrut(10));
	
	// submit/cancel buttons
	JPanel buttonRow = new JPanel();
	buttonRow.setAlignmentX(LEFT_ALIGNMENT);
	buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
	
	cancel = new JButton("Cancel");
	cancel.addActionListener(this);
	buttonRow.add(cancel);
	
	submit = new JButton("Save changes");
	submit.addActionListener(this);
	panel.getRootPane().setDefaultButton(submit);
	buttonRow.add(submit);
	
	panel.add(buttonRow);
	
	// populate fields if a user was specified
	if (auth != null) {
	    this.auth = auth;
	    username.setText(auth.username);
	    username.setEditable(false);
	    name.setText(auth.name);
	    bio.setText(auth.bio);
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
	
	if (password.getPassword().length == 0)
	{
	    JOptionPane.showMessageDialog(panel, "Enter your password", null, JOptionPane.WARNING_MESSAGE);
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
     * Makes the proper changes to the user database
     */
    public int makeBackendChanges() {
	if (!validateInputs()) return 0;
	
	if (auth != null) {
	    // modifying existing user
	    User m = auth;
	    m.name = name.getText();
	    m.bio = bio.getText();
	    
	    m.pwhash = BCrypt.hashpw(new String(password.getPassword()), m.pwsalt);
	    
	    try {
		int result = db.edit(m);
		return result;
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	} else {
	    // new user
	    User n = new User(username.getText());
	    n.name = name.getText();
	    n.bio = bio.getText();
	    
	    String salt = BCrypt.gensalt();
	    n.pwsalt = salt;
	    n.pwhash = BCrypt.hashpw(new String(password.getPassword()), salt);
	    
	    n.following = new LinkedList<String>();
	    
	    try {
		if (db.get(username.getText()) != null)
		{
		    JOptionPane.showMessageDialog(panel, "That username is taken.\nChoose another username.", null, JOptionPane.ERROR_MESSAGE);
		    return 0;
		}
		
		int result = db.add(n);
		return result;
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	
	return 0;
    }

    public void actionPerformed(ActionEvent arg0) {
	if (arg0.getSource() == submit) {
	    if (makeBackendChanges() == 1)
	    {
		JOptionPane.showMessageDialog(panel, "Changes successful!");
		this.dispose();
	    }
	}
	
	if (arg0.getSource() == cancel) {
	    this.dispose();
	}
	
	if (arg0.getSource() == addFollow) {
	    // add user
	}
	
	if (arg0.getSource() == listFollow) {
	    // list of users
	}
	
	if (arg0.getSource() == stopFollow) {
	    // remove user
	}
    }
}
