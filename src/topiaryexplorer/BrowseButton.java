package topiarytool;

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
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public class BrowseButton extends JButton{
    NewProjectDialog frame = null;
    JTextField partner = null;
    FileContents fl = null;
    
	// {{{ browsButton constructor
    /**
     * 
     */
    public BrowseButton(NewProjectDialog _frame, JTextField _partner, String fileType) {
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
