package topiaryexplorer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.jnlp.*;
import java.io.*;

public class TreeOptionsToolbar extends JToolBar {
    JButton consensusButton = new JButton();
    JButton tipLabelsButton = new JButton();
    JButton collapseButton  = new JButton();
    JButton expandButton = new JButton();
    JButton mirrorvertButton = new JButton();
    JButton mirrorhorzButton = new JButton();
    JButton rectButton = new JButton();
    JButton triButton = new JButton();
    JButton radialButton = new JButton();
    JButton polarButton = new JButton();
    JLabel treeStatus = new JLabel("");

	ExtendedService es;
	
    TreeWindow frame = null;
    MainFrame mainframe = null;

    public TreeOptionsToolbar(TreeWindow _frame, MainFrame _mainframe) {
        super(JToolBar.HORIZONTAL);
        frame = _frame;
        mainframe = _mainframe;
        
        try { 
        	es = (ExtendedService)ServiceManager.lookup("javax.jnlp.ExtendedService");
    	} catch (UnavailableServiceException e) { es=null; } 
        
        
        tipLabelsButton.setText("Tip Labels");
        tipLabelsButton.setToolTipText("Show Tip Labels");
        tipLabelsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.showTips = !frame.showTips;
                frame.tipLabels();
            }
        });
/*        add(consensusButton);*/
        add(tipLabelsButton);
         addSeparator();
        collapseButton.setText("Collapse");
        collapseButton.setToolTipText("Collapse");
        collapseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.collapseTree();
            }
        });
        expandButton.setText("Expand");
        expandButton.setToolTipText("Expand");
        expandButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.uncollapseTree();
            }
        });
        add(collapseButton);
        add(expandButton);
        addSeparator();
        mirrorvertButton.setText("Mirror Vert");
        mirrorvertButton.setToolTipText("Flip Vertical");
        mirrorvertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorVert();
            }
        });

        mirrorhorzButton.setText("Mirror Horiz");
        mirrorhorzButton.setToolTipText("Flip Horizontal");
        mirrorhorzButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorHorz();
            }
        });
        add(mirrorvertButton);
        add(mirrorhorzButton);
        addSeparator();
        
        rectButton.setText("Rectangular");
        rectButton.setToolTipText("Rectangular");
        rectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Rectangular");
                frame.rectangularradiobutton.setSelected(true);
                frame.rotateSlider.setValue(0);
                frame.syncTreeWithRotateSlider();
                frame.rotateMenu.setEnabled(false);
            }
        });

        triButton.setText("Triangular");
        triButton.setToolTipText("Triangular");
        triButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Triangular");
                frame.triangularradiobutton.setSelected(true);
                frame.rotateSlider.setValue(0);
                frame.syncTreeWithRotateSlider();
                frame.rotateMenu.setEnabled(false);
            }
        });

        radialButton.setText("Radial");
        radialButton.setToolTipText("Radial");
        radialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                treeStatus.setText("Calculating Offsets...");
                frame.tree.setRadialOffsets(frame.tree.getTree());
                frame.tree.setTOffsets(frame.tree.getTree(), 0);
                frame.tree.setROffsets(frame.tree.getTree(), 0);
                frame.tree.setTreeLayout("Radial");
                frame.radialradiobutton.setSelected(true);
                frame.rotateMenu.setEnabled(true);
                treeStatus.setText("Done drawing tree.");
            }
        });

        polarButton.setText("Polar");
        polarButton.setToolTipText("Polar");
        polarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int size = frame.tree.getTree().getNumberOfLeaves();
                if(size > 8000)
                {
                    if(JOptionPane.showConfirmDialog(null, 
                    "Viewing the polar layout of trees with more\n"
                    +"than 8000 nodes is not advised.",
                    "Warning",JOptionPane.OK_CANCEL_OPTION, 
                    JOptionPane.WARNING_MESSAGE) 
                    == JOptionPane.CANCEL_OPTION)
                        return;
                }
                treeStatus.setText("Calculating Offsets...");
                frame.tree.setRadialOffsets(frame.tree.getTree());
                frame.tree.setTOffsets(frame.tree.getTree(), 0);
                frame.tree.setROffsets(frame.tree.getTree(), 0);
                frame.tree.setTreeLayout("Polar");
                frame.polarradiobutton.setSelected(true);
                frame.rotateMenu.setEnabled(true);
                treeStatus.setText("Done drawing tree.");
            }
        });
        add(rectButton);
        add(triButton);
        add(radialButton);
        add(polarButton);
        addSeparator();
        
        add(treeStatus);
        
        setFloatable(false);
    }
    
    public void setStatus(String s) {
        treeStatus.setText(s);
    }

}
