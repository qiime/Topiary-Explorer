package topiaryexplorer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.media.opengl.*;
import java.sql.*;
import javax.swing.table.*;
import java.io.*;
import javax.jnlp.*;

/**
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public final class BranchEditPanel extends JPanel{
    TreeWindow frame = null;
    TreeVis vis = null;
    
    JCheckBox coloringMenuItem = new JCheckBox("No coloring",true);
    JCheckBox majorityColoringMenuItem = new JCheckBox("Majority coloring",true);
    
    JButton colorBy = new JButton("Color by...");
    ColorByPopupMenu colorByMenu;
    
    JButton sortByButton = new JButton("Sort by...");
    JMenu sortBy = new JMenu("Sort by");
    
    JMenu lineWidthMenu = new JMenu("Line Width");
    JSlider lineWidthSlider = new JSlider(1, 20, 20);
    ButtonGroup lineWidthGroup = new ButtonGroup();
    
    JRadioButtonMenuItem uniformLineWidthItem = new JRadioButtonMenuItem("Uniform");
    JMenu lineWidthSampleMetadataMenu = new JMenu("Sample Metadata");
    JMenu lineWidthOtuMetadataMenu = new JMenu("OTU Metadata");
    
	// {{{ BranchEditPanel constructor
    /**
     * 
     */
    public BranchEditPanel(TreeWindow _frame) {
        super();
        frame = _frame;
        vis = frame.tree;
        colorByMenu = new ColorByPopupMenu(frame.frame, frame, frame.frame.branchColorPanel,0);
        this.setToolTipText("Customize Branches");
/*        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));*/
        setLayout(new GridLayout(5,1));
        
        coloringMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(coloringMenuItem.isSelected())
                    frame.removeColor();
            }
        });
        add(coloringMenuItem);
        
        majorityColoringMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                   vis.setMajorityColoring(majorityColoringMenuItem.isSelected());
                   vis.getTree().updateBranchColorFromChildren();
                   frame.frame.recolorBranches();
            }
        });
        add(majorityColoringMenuItem);
        
        colorBy.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
				colorByMenu.show(colorBy, colorBy.getX(), colorBy.getY());
           } 
        });
        add(colorBy);
    }
	// }}}
	
/*  public void resetLineWidthOtuMenu() {
       uniformLineWidthItem.setSelected(true);
        lineWidthOtuMetadataMenu.removeAll();
        ArrayList<String> data = frame.frame.otuMetadata.getColumnNames();
        //start at 1 to skip ID column
        for (int i = 1; i < data.size(); i++) {
             String value = data.get(i);
             item = new JRadioButtonMenuItem(value);
             lineWidthGroup.add(item);
             item.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                     //get the category to color by
                     String value = e.getActionCommand();
                     frame.frame.currTable = frame.frame.otuMetadata;
                     ((TreeWindow)parent).setLineWidthByValue(value);
                 }
             });
             lineWidthOtuMetadataMenu.add(item);
        }
    }
    
    public void resetLineWidthSampleMenu() {
        uniformLineWidthItem.setSelected(true);
           lineWidthSampleMetadataMenu.removeAll();
           ArrayList<String> data = frame.frame.sampleMetadata.getColumnNames();
           //start at 1 to skip ID column
           for (int i = 1; i < data.size(); i++) {
                String value = data.get(i);
                JRadioButtonMenuItem item = new JRadioButtonMenuItem(value);
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //get the category to color by
                        String value = e.getActionCommand();
                        frame.frame.currTable = frame.frame.sampleMetadata;
                        ((TreeWindow)parent).setLineWidthByValue(value);
                    }
                });
                lineWidthGroup.add(item);
                lineWidthSampleMetadataMenu.add(item);
           }
    }*/
}
