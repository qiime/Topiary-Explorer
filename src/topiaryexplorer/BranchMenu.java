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
public class BranchMenu extends TreeElementMenu {    
    JMenuItem item;
    
    JMenu sortBy = new JMenu("Sort by");
    
    JMenu lineWidthMenu = new JMenu("Line Width");
    JSlider lineWidthSlider = new JSlider(1, 20, 20);
    ButtonGroup lineWidthGroup = new ButtonGroup();
    JRadioButtonMenuItem uniformLineWidthItem = new JRadioButtonMenuItem("Uniform");
    JMenu lineWidthSampleMetadataMenu = new JMenu("Sample Metadata");
    JMenu lineWidthOtuMetadataMenu = new JMenu("OTU Metadata");
    
	// {{{ BranchMenu constructor
    /**
     * 
     */
    public BranchMenu(MainFrame _frame, TopiaryWindow _parent, String name) {
        super(_frame, _parent, name);
        item = new JMenuItem("Number of OTUs");
         item.addActionListener(this);
         sortBy.add(item);
         item = new JMenuItem("Number of immediate children");
         item.addActionListener(this);
         sortBy.add(item);
         add(sortBy);
         
         lineWidthMenu.add(lineWidthOtuMetadataMenu);
          lineWidthMenu.add(lineWidthSampleMetadataMenu);
          lineWidthGroup.add(uniformLineWidthItem);
          uniformLineWidthItem.setSelected(true);
          uniformLineWidthItem.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  for (Node n : ((TreeWindow)parent).tree.getTree().getNodes()) {
                      n.setLineWidth(1);
                  }
                  ((TreeWindow)parent).syncTreeWithLineWidthSlider();
              }
          });
          lineWidthMenu.add(uniformLineWidthItem);
          lineWidthSlider.addChangeListener(new ChangeListener() {
              public void stateChanged(ChangeEvent e) {
                  if (lineWidthSlider.getValueIsAdjusting()){
                      ((TreeWindow)parent).syncTreeWithLineWidthSlider();
                  }
              }
          });
          lineWidthMenu.add(lineWidthSlider);
          add(lineWidthMenu);
    }
	// }}}
          
    public void resetLineWidthOtuMenu() {
       uniformLineWidthItem.setSelected(true);
        lineWidthOtuMetadataMenu.removeAll();
        ArrayList<String> data = frame.otuMetadata.getColumnNames();
        //start at 1 to skip ID column
        for (int i = 1; i < data.size(); i++) {
             String value = data.get(i);
             item = new JRadioButtonMenuItem(value);
             lineWidthGroup.add(item);
             item.addActionListener(new ActionListener() {
                 public void actionPerformed(ActionEvent e) {
                     //get the category to color by
                     String value = e.getActionCommand();
/*                         System.out.println("*");*/
                     frame.currTable = frame.otuMetadata;
                     ((TreeWindow)parent).setLineWidthByValue(value);
                 }
             });
             lineWidthOtuMetadataMenu.add(item);
        }
    }
    
    public void resetLineWidthSampleMenu() {
        uniformLineWidthItem.setSelected(true);
           lineWidthSampleMetadataMenu.removeAll();
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
                        ((TreeWindow)parent).setLineWidthByValue(value);
                    }
                });
                lineWidthGroup.add(item);
                lineWidthSampleMetadataMenu.add(item);
           }
    }
}
