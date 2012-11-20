package edu.umw.cpsc330.twitterclone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Provides a way of displaying a list of Posts in a JTable.
 * 
 * @author Alex Lindeman
 */
public class PostDisplayModel extends AbstractTableModel {
    
    private static final long serialVersionUID = 4853285294643287115L;
    
    private List<Post> data = new LinkedList<Post>();
    private String[] columns = { "Author", "Date", "Post" };
    
    public PostDisplayModel(List<Post> data) {
	this.data = data;
    }
    
    /**
     * Number of columns in the table
     */
    public int getColumnCount() {
	return 3;
    }

    /**
     * Number of rows in the table
     */
    public int getRowCount() {
	return data.size();
    }
    
    /**
     * Returns the name of a column
     */
    public String getColumnName(int col) {
	return columns[col];
    }

    /**
     * Fetches data to put it in the table
     */
    public Object getValueAt(int row, int cell) {
	Post p = data.get(row);
	switch (cell) {
	case 0:
	    return p.author;
	case 1:
	    // use a more readable date format
	    DateFormat df = new SimpleDateFormat("EEE MMM d, HH:mm:ss");
	    String date = df.format(p.date.getTime());
	    return date;
	case 2:
	    return p.getContent();
	}
	
	return null;
    }

}
