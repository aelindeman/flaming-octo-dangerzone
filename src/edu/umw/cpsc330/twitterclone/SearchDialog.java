package edu.umw.cpsc330.twitterclone;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class SearchDialog extends Dialog {

    protected Object result;
    protected Shell shlSearch;
    private Text search;

    /**
     * Create the dialog.
     * @param parent
     * @param style
     */
    public SearchDialog(Shell parent, int style) {
	super(parent, style);
	setText("SWT Dialog");
    }

    /**
     * Open the dialog.
     * @return the result
     */
    public Object open() {
	createContents();
	shlSearch.open();
	shlSearch.layout();
	Display display = getParent().getDisplay();
	while (!shlSearch.isDisposed()) {
	    if (!display.readAndDispatch()) {
		display.sleep();
	    }
	}
	return result;
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
	shlSearch = new Shell(getParent(), getStyle());
	shlSearch.setSize(250, 65);
	shlSearch.setText("Search");
	shlSearch.setLayout(new GridLayout(2, false));
	
	search = new Text(shlSearch, SWT.BORDER | SWT.SEARCH);
	search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
	
	Button performSearch = new Button(shlSearch, SWT.CENTER);
	performSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
	performSearch.setText("Search");

    }
}
