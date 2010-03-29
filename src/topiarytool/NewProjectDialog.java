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
public class NewProjectDialog extends JFrame{

    MainFrame frame = null;
    JFileChooser fileChooser = new JFileChooser();
    JPanel mainPanel = new JPanel();
    JPanel topPanel = new JPanel();
    JPanel bottomPanel = new JPanel();
    
    JPanel treePanel = new JPanel();
    JLabel treeFileLabel = new JLabel("Tree File: ");
    JTextField treeFileField = new JTextField("", 20);

    JPanel otuMetaPanel = new JPanel();
    JLabel otuMetaLabel = new JLabel("OTU Metadata: ");
    JTextField otuMetaField = new JTextField("", 20);
    
    JPanel otuSamplePanel = new JPanel();
    JLabel otuSampleLabel = new JLabel("OTU-Sample Map: ");
    JTextField otuSampleField = new JTextField("", 20);
    
    JPanel sampleMetaPanel = new JPanel();
    JLabel sampleMetaLabel = new JLabel("Sample Metadata: ");
    JTextField sampleMetaField = new JTextField("", 20);
    
    JLabel projectNameLabel = new JLabel("Name: ");
    JTextField projectName = new JTextField("", 30);
    
    JButton okButton = new JButton("Create Project");
    JButton cancelButton = new JButton("Cancel");
	// {{{ newProjectDialog constructor
    /**
     * 
     */
    public NewProjectDialog(MainFrame _frame) {
        frame = _frame;
        this.setSize(new Dimension(500,300));
        this.setTitle("Create new project");
        
        this.setLayout(new BorderLayout());
        topPanel.setLayout(new FlowLayout());
        topPanel.add(projectNameLabel);
        topPanel.add(projectName);
        this.add(topPanel, BorderLayout.NORTH);
        
        mainPanel.setLayout(new GridLayout(4,1));
        
        treePanel.add(treeFileLabel);
        treePanel.add(treeFileField);
        treePanel.add(new BrowseButton(this, treeFileField, "Choose tree file location"));
        mainPanel.add(treePanel);
        
        otuMetaPanel.add(otuMetaLabel);
        otuMetaPanel.add(otuMetaField);
        otuMetaPanel.add(new BrowseButton(this, otuMetaField, "Choose OTU metadata file location"));
        mainPanel.add(otuMetaPanel);
        
        otuSamplePanel.add(otuSampleLabel);
        otuSamplePanel.add(otuSampleField);
        otuSamplePanel.add(new BrowseButton(this, otuSampleField, "Choose OTU-Sample map file location"));
        mainPanel.add(otuSamplePanel);
        
        sampleMetaPanel.add(sampleMetaLabel);
        sampleMetaPanel.add(sampleMetaField);
        sampleMetaPanel.add(new BrowseButton(this, sampleMetaField, "Choose sample metadata file location"));
        mainPanel.add(sampleMetaPanel);
        
        this.add(mainPanel, BorderLayout.CENTER);
        
        cancelButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   dispose();
               }
        });
        
        bottomPanel.add(cancelButton);
        
        okButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   if(okButtonPressed())
                        dispose();
               }
        });
        
        bottomPanel.add(okButton);
        
        this.add(bottomPanel, BorderLayout.SOUTH);
        this.setVisible(true);
    }
	// }}}
	
	public boolean okButtonPressed() {
	    File temp = null;
        
	    if(treeFileField.getText() != "")
	    {
            temp = new File(treeFileField.getText());
            if(temp.exists())
                {
                    frame.treeWindow.loadTree(temp);
                }
	    }
    
	    if(otuMetaField.getText().length() > 0)
	    {
            temp = new File(otuMetaField.getText());
            if(temp.exists())
            {
                frame.mainMenu.loadOtuMetadata(temp); 
            }
	    }
      else
        {frame.mainMenu.clearOtuMetadata();}
	        
        
	    if(otuSampleField.getText().length() > 0)
	    {
            temp = new File(otuSampleField.getText());
            if(temp.exists())
            {
                frame.mainMenu.loadOtuSampleMap(temp); 
            }
	    }
      else
            {frame.mainMenu.clearOtuSampleMap();}
    
	    if(sampleMetaField.getText().length() > 0)
	    {
            temp = new File(sampleMetaField.getText());
            if(temp.exists())
            {
                frame.mainMenu.loadSampleMetadata(temp); 
            }
	    }
      else
            {frame.mainMenu.clearSampleMetadata();}
	        
	    return true;
	}
}
