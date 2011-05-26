package topiaryexplorer;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.jnlp.*;

/**
 * This pop-up menu contains values by which branches
 * and nodes of the tree can be colored.
 * @author Meg Pirrung &lt;&gt;
 * @see #JpopupMenu
 */
 
class ColorByPopupMenu extends JPopupMenu{
	private TopiaryWindow parent;
    private MainFrame frame;
    private ColorPanel colorPanel;
    private int elementType = 0;
    private JMenu colorByOtuMetadataMenu = new JMenu("Tip Data");
    private JMenu colorBySampleMetadataMenu = new JMenu("Sample Data");
    
    private ButtonGroup colorByGroup = new ButtonGroup();
    
    /**
     * Creates a pop-up menu for the specified element type.
     */
     ColorByPopupMenu(MainFrame _frame, TopiaryWindow _parent, ColorPanel _colorPanel, int _elementType) {
         frame  = _frame;
         parent = _parent;
         colorPanel = _colorPanel;
         elementType = _elementType;
         //set up the "color by" submenus
         add((JMenuItem)colorByOtuMetadataMenu);
         add((JMenuItem)colorBySampleMetadataMenu);
     }
    

     /**
     * Resets colorby OTU menu when a new otu table is loaded.
     */
     void resetColorByOtuMenu() {
 /*        noColoringMenuItem.setSelected(true);*/
         colorByOtuMetadataMenu.removeAll();
         ArrayList<String> data = frame.otuMetadata.getColumnNames();
         //start at 1 to skip ID column
         for (int i = 1; i < data.size(); i++) {
              String value = data.get(i);
              JRadioButtonMenuItem item = new JRadioButtonMenuItem(value);
              colorByGroup.add(item);
              item.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                      //get the category to color by
                      String value = e.getActionCommand();
                      frame.currTable = frame.otuMetadata;
                      frame.colorPane.setSelectedIndex(elementType);
                      if(elementType == 0)
                      {
                          ((TreeWindow)parent).colorBranchesByValue(value);
                          ((TreeWindow)parent).treeEditToolbar.branchEditPanel.coloringMenuItem.setSelected(false);
                          ((TreeWindow)parent).tree.setColorBranches(true);
                          ((TreeWindow)parent).treeEditToolbar.branchEditPanel.majorityColoringMenuItem.setEnabled(true);
                      }
                      else if(elementType == 1)
                          ((TreeWindow)parent).colorLabelsByValue(value);

                      TableColumn column = colorPanel.getColorKeyTable().getColumnModel().getColumn(0);
                      column.setHeaderValue(value);
                  }
              });
              colorByOtuMetadataMenu.add(item);
         }
     }

     /**
     * Resets colorby Sample menu when a new sample metadata table is loaded.
     */
     void resetColorBySampleMenu() {
 /*        noColoringMenuItem.setSelected(true);*/
         colorBySampleMetadataMenu.removeAll();
         // colorBySampleMetadataMenu.add(new JRadioButtonMenuItem("hurf"));
         ArrayList<String> data = frame.sampleMetadata.getColumnNames();
         //start at 1 to skip ID column
         // System.out.println(frame.sampleMetadata.getColumnNames().size());
         for (int i = 1; i < data.size(); i++) {
              String value = data.get(i);
              JRadioButtonMenuItem item = new JRadioButtonMenuItem(value);
              // System.out.println("["+value+"]");
              item.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                      //get the category to color by
                      String value = e.getActionCommand();
                      frame.currTable = frame.sampleMetadata;
                      frame.colorPane.setSelectedIndex(elementType);
                      if(elementType == 0)
                       {
                             ((TreeWindow)parent).colorBranchesByValue(value);
                             ((TreeWindow)parent).treeEditToolbar.branchEditPanel.coloringMenuItem.setSelected(false);
                             ((TreeWindow)parent).tree.setColorBranches(true);
                             ((TreeWindow)parent).treeEditToolbar.branchEditPanel.coloringMenuItem.setEnabled(true);
                             ((TreeWindow)parent).treeEditToolbar.branchEditPanel.majorityColoringMenuItem.setEnabled(true);
                         }
                       else if(elementType == 1)
                           ((TreeWindow)parent).colorLabelsByValue(value);

                      TableColumn column = colorPanel.getColorKeyTable().getColumnModel().getColumn(0);
                      column.setHeaderValue(value);
                  }
              });
              colorByGroup.add(item);
              colorBySampleMetadataMenu.add(item);
         }
         // colorBySampleMetadataMenu.add(new JRadioButtonMenuItem("durf"));
     }
}
