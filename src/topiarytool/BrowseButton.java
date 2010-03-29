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

/**
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public class BrowseButton extends JButton{
    NewProjectDialog frame = null;
    JTextField partner = null;
    String title = null;
    File fl = null;
    
	// {{{ browsButton constructor
    /**
     * 
     */
    public BrowseButton(NewProjectDialog _frame, JTextField _partner, String _title) {
        frame = _frame;
        partner = _partner;
        title = _title;
        this.setText("Browse...");
        this.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   fl = buttonPressed(title);
                   if(fl != null)
                       partner.setText(fl.getAbsolutePath());
               }
        });
    }
    // }}}
    
    public File buttonPressed(String windowTitle) {
        frame.frame.loadDataFileChooser.setAcceptAllFileFilterUsed(true);
        frame.frame.loadDataFileChooser.setDialogTitle(windowTitle);
       int returnVal = frame.frame.loadDataFileChooser.showOpenDialog(null);
       if (returnVal == JFileChooser.APPROVE_OPTION) {
           File selectedFile = frame.frame.loadDataFileChooser.getSelectedFile();
           return selectedFile;
       }
       else
        return null;
    }
}
