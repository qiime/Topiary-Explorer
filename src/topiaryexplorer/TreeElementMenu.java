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
public class TreeElementMenu extends JMenu implements ActionListener{
    TopiaryWindow parent;
    MainFrame frame;
    TreeVis tree;
    ColorByMenu colorBy;
    JRadioButtonMenuItem noColoringMenuItem = new JRadioButtonMenuItem("No coloring",true);
    JRadioButtonMenuItem majorityColoringMenuItem = new JRadioButtonMenuItem("Majority coloring",true);
    
	// {{{ treeElementMenu constructor
    /**
     * 
     */
    public TreeElementMenu(MainFrame _frame, TopiaryWindow _parent, String name, ColorPanel _colorPanel, int elementType) {
        super(name);
         parent = _parent;
         frame = _frame;
         tree = ((TreeWindow)parent).tree;
          noColoringMenuItem.addActionListener(this);
          add(noColoringMenuItem);
          majorityColoringMenuItem.addActionListener(this);
          add(majorityColoringMenuItem);
          colorBy = new ColorByMenu(frame,parent,_colorPanel,elementType);
          add(colorBy);
    }
	// }}}
	
	public void actionPerformed(ActionEvent e) {
           if (e.getActionCommand().equals("No coloring")) {
                ((TreeWindow)parent).removeColor();
           }
           else if(e.getActionCommand().equals("Majority coloring")) {
               ((TreeWindow)parent).tree.setMajorityColoring(majorityColoringMenuItem.isSelected());
               ((TreeWindow)parent).tree.getTree().updateBranchColorFromChildren();
               // frame.recolorBranches();
           } else if (e.getActionCommand().equals("Number of OTUs")) {
                 tree.getTree().sortByNumberOfOtus();
                 tree.setYOffsets(tree.getTree(), 0);
                 tree.setTOffsets(tree.getTree(), 0);
                 tree.setROffsets(tree.getTree(), 0);
                 tree.setRadialOffsets(tree.getTree());
             } else if (e.getActionCommand().equals("Number of immediate children")) {
                 tree.getTree().sortByNumberOfChildren();
                 tree.setYOffsets(tree.getTree(), 0);
                 tree.setTOffsets(tree.getTree(), 0);
                 tree.setROffsets(tree.getTree(), 0);
                 tree.setRadialOffsets(tree.getTree());
             }
       }
       
    public void resetColorByOtuMenu() {
        colorBy.resetColorByOtuMenu();
    }
    
    public void resetColorBySampleMenu() {
        colorBy.resetColorBySampleMenu();
    }
}
