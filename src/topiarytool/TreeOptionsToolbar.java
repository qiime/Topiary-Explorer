package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.jnlp.*;
import java.io.*;

public class TreeOptionsToolbar extends JToolBar {

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

    public TreeOptionsToolbar(TreeWindow _frame) {
        super(JToolBar.HORIZONTAL);
        frame = _frame;
        
        try { 
        	es = (ExtendedService)ServiceManager.lookup("javax.jnlp.ExtendedService");
    	} catch (UnavailableServiceException e) { es=null; } 
        
        try{
        FileContents fc = es.openFile(new File("images/collapse.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        collapseButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        collapseButton.setToolTipText("Collapse");
        collapseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.collapseTree();
            }
        });
        try{
        FileContents fc = es.openFile(new File("images/expand.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        expandButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        expandButton.setToolTipText("Expand");
        expandButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.uncollapseTree();
            }
        });
        add(collapseButton);
        add(expandButton);
        addSeparator();
        try{
        FileContents fc = es.openFile(new File("images/mirror_vert.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        mirrorvertButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        mirrorvertButton.setToolTipText("Flip Vertical");
        mirrorvertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorVert();
            }
        });
        try{
        FileContents fc = es.openFile(new File("images/mirror_horiz.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        mirrorhorzButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        mirrorhorzButton.setToolTipText("Flip Horizontal");
        mirrorhorzButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorHorz();
            }
        });
        add(mirrorvertButton);
        add(mirrorhorzButton);
        addSeparator();
        
        try{
        FileContents fc = es.openFile(new File("images/rectangular.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        rectButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        rectButton.setToolTipText("Rectangular");
        rectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Rectangular");
            }
        });
        try{
        FileContents fc = es.openFile(new File("images/triangular.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        triButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        triButton.setToolTipText("Triangular");
        triButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Triangular");
            }
        });
        try{
        FileContents fc = es.openFile(new File("images/radial.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        radialButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        radialButton.setToolTipText("Radial");
        radialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Radial");
            }
        });
        try{
        FileContents fc = es.openFile(new File("images/polar.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        polarButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        polarButton.setToolTipText("Polar");
        polarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Polar");
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
