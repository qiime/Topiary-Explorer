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
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public final class ColorByPopupMenu extends JPopupMenu{
	TopiaryWindow parent;
    MainFrame frame;
    int elementType = 0;
    JMenu colorByOtuMetadataMenu = new JMenu("OTU Metadata");
    JMenu colorBySampleMetadataMenu = new JMenu("Sample Metadata");
    
    ButtonGroup colorByGroup = new ButtonGroup();
    ColorPanel colorPanel;
    
    /**
     * Constructor.  Sets up the menu.
     */
     public ColorByPopupMenu(MainFrame _frame, TopiaryWindow _parent, ColorPanel _colorPanel, int _elementType) {
/*         super("Color By");*/
         frame  = _frame;
         parent = _parent;
         colorPanel = _colorPanel;
         elementType = _elementType;
         //set up the "color by" submenus
         add((JMenuItem)colorByOtuMetadataMenu);
         add((JMenuItem)colorBySampleMetadataMenu);
     }
    

     /**
         * Resets colorby OTU menu when a new otu table is loaded
         */
         public void resetColorByOtuMenu() {
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
                              ((TreeWindow)parent).colorBranchesByValue(value);
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
         * Resets colorby Sample menu when a new sample metadata table is loaded
         */
         public void resetColorBySampleMenu() {
     /*        noColoringMenuItem.setSelected(true);*/
             colorBySampleMetadataMenu.removeAll();
             ArrayList<String> data = frame.sampleMetadata.getColumnNames();
             //start at 1 to skip ID column
             for (int i = 1; i < data.size(); i++) {
                  String value = data.get(i);
                  JRadioButtonMenuItem item = new JRadioButtonMenuItem(value);
                  item.addActionListener(new ActionListener() {
                      public void actionPerformed(ActionEvent e) {
                          //get the category to color by
                          String value = e.getActionCommand();
                          frame.currTable = frame.sampleMetadata;
                          frame.colorPane.setSelectedIndex(elementType);
                          if(elementType == 0)
                               ((TreeWindow)parent).colorBranchesByValue(value);
                           else if(elementType == 1)
                               ((TreeWindow)parent).colorLabelsByValue(value);

                          TableColumn column = colorPanel.getColorKeyTable().getColumnModel().getColumn(0);
                          column.setHeaderValue(value);
                      }
                  });
                  colorByGroup.add(item);
                  colorBySampleMetadataMenu.add(item);
             }
         }
}
