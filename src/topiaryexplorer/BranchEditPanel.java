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
 * Component of the TreeViewToolbar for editing branch properties
 *
 * @author Meg Pirrung &lt;&gt;
 * @see #javax.swing.JPanel
 */
class BranchEditPanel extends JPanel{
    //A reference to the <code>TreeWindow</code> that contains this panel.
    private TreeWindow frame = null;
    /**
    * A reference to the <code>TreeVis</code> that this panel modifies.
    **/
    private TreeVis vis = null;
    
    /**
    * A checkbox indicating the colored status of branches.
    **/
    JCheckBox coloringMenuItem = new JCheckBox("No coloring",true);
    /**
    * A checkbox indicating the type of coloring used for the branches.
    **/
    JCheckBox majorityColoringMenuItem = new JCheckBox("Majority coloring",true);
    
    /**
    * A checkbox indicating whether or not the coloring is weighted by OTU count
    **/
    JCheckBox weightedColoringMenuItem = new JCheckBox("Weighted",true);
    
    /**
    * A checkbox indicating whether or not the coloring is weighted by OTU count
    **/
    JCheckBox blackAsNocountMenuItem = new JCheckBox("Black as not counted",false);
    
    
    /**
    * A button that triggers menus containing fields that the branches can be colored by.
    **/
    JButton colorBy = new JButton("Color by...");
    /**
    * Menu triggered by colorBy button which contains fields that branches can be colored by.
    **/
    ColorByPopupMenu colorByMenu;
    
/*    JButton sortByButton = new JButton("Sort by...");
    JMenu sortBy = new JMenu("Sort by");*/
    
    JMenu lineWidthMenu = new JMenu("Line Width");
    JLabel lineWidthLabel = new JLabel("Line Width:");
    JSlider lineWidthSlider = new JSlider(1, 75, 15);
    // ButtonGroup lineWidthGroup = new ButtonGroup();
    
/*    JRadioButtonMenuItem uniformLineWidthItem = new JRadioButtonMenuItem("Uniform");
    JMenu lineWidthSampleMetadataMenu = new JMenu("Sample Metadata");
    JMenu lineWidthOtuMetadataMenu = new JMenu("OTU Metadata");*/
    
	// {{{ BranchEditPanel constructor
    /**
     * Creates a panel with reference to the <code>TreeWindow</code> it is contained in.
     */
    BranchEditPanel(TreeWindow _frame) {
        super();
        frame = _frame;
        vis = frame.tree;
        // TreeWindow frame, MainFrame frame.frame
        colorByMenu = new ColorByPopupMenu(frame.frame, frame, frame.frame.branchColorPanel,0);
        this.setToolTipText("Customize Branches");
/*        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));*/
        setLayout(new GridLayout(7,1));
        
        // coloringMenuItem.setEnabled(false);
        coloringMenuItem.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {                frame.treeEditToolbar.summaryPanel.summaryScrollPane.setVisible(!coloringMenuItem.isSelected());  
                weightedColoringMenuItem.setEnabled(!coloringMenuItem.isSelected()); majorityColoringMenuItem.setEnabled(!coloringMenuItem.isSelected());
                blackAsNocountMenuItem.setEnabled(!coloringMenuItem.isSelected());
                vis.setColorBranches(!coloringMenuItem.isSelected());
                vis.redraw();
                frame.treePopupMenu.getComponent(4).setEnabled(!coloringMenuItem.isSelected());
            }
        });
        add(coloringMenuItem);
        majorityColoringMenuItem.setEnabled(false);
        majorityColoringMenuItem.setToolTipText("Toggle branch color mode between majority and mixed coloring.");
        majorityColoringMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                   vis.setMajorityColoring(majorityColoringMenuItem.isSelected());
                   vis.getTree().updateBranchColorFromChildren();
                   frame.recolorBranches();
            }
        });
        add(majorityColoringMenuItem);
        
        weightedColoringMenuItem.setEnabled(false);
        weightedColoringMenuItem.setToolTipText("Toggle branch color weighting by OTU count.");
        weightedColoringMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.recolorBranches();
                vis.redraw();
            }
        });
        add(weightedColoringMenuItem);
        
        blackAsNocountMenuItem.setEnabled(false);
        blackAsNocountMenuItem.setToolTipText("Toggle branch color weighting by OTU count.");
        blackAsNocountMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setBlackAsNoCount(blackAsNocountMenuItem.isSelected());
                frame.recolorBranches();
                vis.redraw();
            }
        });
        add(blackAsNocountMenuItem);
        
        colorBy.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
				colorByMenu.show(colorBy, 100, 10);
           } 
        });
        add(colorBy);
        add(lineWidthLabel);
        lineWidthSlider.setMajorTickSpacing(15);
        lineWidthSlider.setSnapToTicks(true);
        lineWidthSlider.addChangeListener(new ChangeListener() {
            public synchronized void stateChanged(ChangeEvent e) {
                if (lineWidthSlider.getValueIsAdjusting()){
                    frame.tree.setLineWidthScale(lineWidthSlider.getValue()/15.0F);
                }
                frame.tree.redraw();
            }
        });
        add(lineWidthSlider);
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
