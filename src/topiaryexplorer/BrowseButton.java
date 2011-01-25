package topiaryexplorer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.media.opengl.*;
import java.sql.*;
import javax.swing.table.*;
import java.io.*;
import javax.swing.table.*;
import javax.jnlp.*;

/**
 * A button used specifically for browsing for files.
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 * @see #javax.swing.JButton
 */
class BrowseButton extends JButton{
    /**
    * Reference to the <code>NewProjectDialog</code> that this browse button resides on.
    **/
    private NewProjectDialog frame = null;
    /**
    * Reference to the textfield on <code>NewProjectDialog</code> that this button is partnered with.
    **/
    private JTextField partner = null;
    private FileContents fl = null;
    
	// {{{ browsButton constructor
    /**
     * Creates a button to be used on a <code>NewProjectDialog</code> linked to a text field
     * with a certain file type.
     */
    BrowseButton(NewProjectDialog _frame, JTextField _partner, String fileType) {
        frame = _frame;
        partner = _partner;
        this.setText("Browse...");
        if (fileType.equals("tree")) {
			this.addActionListener(new ActionListener() {
				   public void actionPerformed(ActionEvent e) {
					   fl = buttonPressed();
					   if(fl != null) {
						   try {
							  partner.setText(fl.getName());
							  frame.treeFile = fl;
						   } catch (IOException ex) {}
					   }
				   }
			});
		} else if (fileType.equals("otuMetadata")) {
			this.addActionListener(new ActionListener() {
				   public void actionPerformed(ActionEvent e) {
					   fl = buttonPressed();
					   if(fl != null) {
						   try {
							  partner.setText(fl.getName());
							  frame.otuMetadataFile = fl;
						   } catch (IOException ex) {}
					   }
				   }
			});
		} else if (fileType.equals("sampleMetadata")) {
			this.addActionListener(new ActionListener() {
				   public void actionPerformed(ActionEvent e) {
					   fl = buttonPressed();
					   if(fl != null) {
						   try {
							  partner.setText(fl.getName());
							  frame.sampleMetadataFile = fl;
						   } catch (IOException ex) {}
					   }
				   }
			});
		} else if (fileType.equals("otuSampleMap")) {
			this.addActionListener(new ActionListener() {
				   public void actionPerformed(ActionEvent e) {
					   fl = buttonPressed();
					   if(fl != null) {
						   try {
							  partner.setText(fl.getName());
							  frame.otuSampleMapFile = fl;
						   } catch (IOException ex) {}
					   }
				   }
			});
		}
    }
    // }}}
    
    /**
    * @return <code>FileContents</code> containing file information.
    **/
    public FileContents buttonPressed() {
    	FileOpenService fos;
    	try { 
        	fos = (FileOpenService)ServiceManager.lookup("javax.jnlp.FileOpenService");
    	} catch (UnavailableServiceException e) {fos = null;} 
    	FileContents fc = null;
    	try {
    		fc = fos.openFileDialog(null,null);
        } catch (IOException ex) {};
        return fc;

    }
}
