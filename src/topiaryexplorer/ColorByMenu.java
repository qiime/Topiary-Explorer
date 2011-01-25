package topiaryexplorer;

/*
Topiary Explorer - tree viewer/data explorer for phylogenetic trees and associated data
Copyright (C) 2009  University of Colorado

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

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
 * TopiaryMenu is the main menu bar for TopiaryTool
 */
public class ColorByMenu extends JMenu{
    private TopiaryWindow parent;
    private MainFrame frame;
    private ColorPanel colorPanel;
    private int elementType = 0;
    private JMenu colorByOtuMetadataMenu = new JMenu("OTU Metadata");
    private JMenu colorBySampleMetadataMenu = new JMenu("Sample Metadata");
    
    private ButtonGroup colorByGroup = new ButtonGroup();
    
    /**
     * Creates a menu for the specified element type.
     */
     public ColorByMenu(MainFrame _frame, TopiaryWindow _parent, ColorPanel _colorPanel, int _elementType) {
         super("Color By");
         frame  = _frame;
         parent = _parent;
         colorPanel = _colorPanel;
         elementType = _elementType;
         //set up the "color by" submenus
         add(colorByOtuMetadataMenu);
         add(colorBySampleMetadataMenu);
     }

    /**
    * Resets colorby OTU menu when a new otu table is loaded.
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
    * Resets colorby Sample menu when a new sample metadata table is loaded.
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
