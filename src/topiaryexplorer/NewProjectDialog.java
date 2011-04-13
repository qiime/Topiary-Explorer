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
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public class NewProjectDialog extends JFrame{

    MainFrame frame = null;
    JPanel mainPanel = new JPanel();
    JPanel topPanel = new JPanel();
    JPanel bottomPanel = new JPanel();
    
    JPanel treePanel = new JPanel();
    JLabel treeFileLabel = new JLabel("Tree File: ");
    JTextField treeFileField = new JTextField("", 30);
    BrowseButton tButton = null;

    JPanel otuMetaPanel = new JPanel();
    JLabel otuMetaLabel = new JLabel("OTU Metadata: ");
    JTextField otuMetaField = new JTextField("", 30);
    BrowseButton omButton = null;
    
    JPanel otuSamplePanel = new JPanel();
    JLabel otuSampleLabel = new JLabel("OTU Abundnace Table: ");
    JTextField otuSampleField = new JTextField("", 30);
    BrowseButton osButton = null;
    
    JPanel sampleMetaPanel = new JPanel();
    JLabel sampleMetaLabel = new JLabel("Sample Metadata Mapping: ");
    JTextField sampleMetaField = new JTextField("", 30);
    BrowseButton smButton = null;
    
    JButton okButton = new JButton("Create Project");
    JButton cancelButton = new JButton("Cancel");
    
    //FileContents objects for each file
    FileContents treeFile = null;
    FileContents otuMetadataFile = null;
    FileContents sampleMetadataFile = null;
    FileContents otuSampleMapFile = null;
    
	// {{{ newProjectDialog constructor
    /**
     * 
     */
    public NewProjectDialog(MainFrame _frame) {
        frame = _frame;
        this.setSize(new Dimension(500,190));
        this.setTitle("Create new project");
        
        tButton = new BrowseButton(this, treeFileField, "tree");
        omButton = new BrowseButton(this, otuMetaField, "otuMetadata");
        osButton = new BrowseButton(this, otuSampleField, "otuSampleMap");
        smButton = new BrowseButton(this, sampleMetaField, "sampleMetadata");
        
        cancelButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   dispose();
               }
        });
        
/*        bottomPanel.add(cancelButton);*/
        
        okButton.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   if(okButtonPressed())
                   {
                       frame.resetSampleMenus();
               	       frame.resetOtuMenus();
                       dispose();
                    }
               }
        });
        
/*        bottomPanel.add(okButton);*/
/*        this.add(bottomPanel, BorderLayout.SOUTH);*/

        GroupLayout layout = new GroupLayout(getContentPane());
        this.setLayout(layout);
        
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(
           layout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGroup(layout.createSequentialGroup()
               .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                  .addComponent(treeFileLabel)
                  .addComponent(otuMetaLabel)
                  .addComponent(otuSampleLabel)
                  .addComponent(sampleMetaLabel)
                  )
              .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                   .addComponent(treeFileField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                   .addComponent(otuMetaField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                   .addComponent(otuSampleField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                   .addComponent(sampleMetaField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                   )
               .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addComponent(tButton)
                      .addComponent(omButton)
                      .addComponent(osButton)
                      .addComponent(smButton)
                      )
                )
/*            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)*/
/*                      .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)*/
              .addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
/*                      )*/
        );
        layout.setVerticalGroup(
           layout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGroup(layout.createSequentialGroup()
               .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                      .addComponent(treeFileLabel)
                      .addComponent(treeFileField)
                      .addComponent(tButton)
                      ) 
              .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                     .addComponent(otuMetaLabel)
                     .addComponent(otuMetaField)
                     .addComponent(omButton)
                     )
             .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                      .addComponent(otuSampleLabel)
                      .addComponent(otuSampleField)
                      .addComponent(osButton)
                      )
              .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(sampleMetaLabel)
                        .addComponent(sampleMetaField)
                        .addComponent(smButton)
                        )
/*               .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)*/
/*                      .addComponent(cancelButton)*/
              .addComponent(okButton)
/*                      )*/
                      )
        );


        this.setVisible(true);
    }
	// }}}
	
	public boolean okButtonPressed() {
	    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    File temp = null;
        
	    if(treeFile!=null)
	    {
            frame.newTreeWindow(treeFile, true);
	    }
    
	    if(otuMetadataFile != null)
	    {
            frame.mainMenu.loadOtuMetadata(otuMetadataFile);    
        }
      else
        {frame.mainMenu.clearOtuMetadata();}
	        
        
	    if(otuSampleMapFile!=null)
	    {
            frame.mainMenu.loadOtuSampleMap(otuSampleMapFile); 
	    }
      else
            {frame.mainMenu.clearOtuSampleMap();}
    
	    if(sampleMetadataFile!=null)
	    {
            frame.mainMenu.loadSampleMetadata(sampleMetadataFile); 
	    }
      else
            {frame.mainMenu.clearSampleMetadata();}
      this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    return true;
	}
}
