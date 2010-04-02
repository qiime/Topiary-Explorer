package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.jnlp.*;


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

	BasicService bs;
	
    TreeWindow frame = null;

    public TreeOptionsToolbar(TreeWindow _frame) {
        super(JToolBar.HORIZONTAL);
        frame = _frame;
        
        try { 
        	bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
    	} catch (UnavailableServiceException e) { bs=null; } 
        
        try{
        collapseButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/collapse.gif")));
        } catch(java.net.MalformedURLException ex) {}
        collapseButton.setToolTipText("Collapse");
        collapseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.collapseTree();
            }
        });
        try{
        expandButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/expand.gif")));
        } catch(java.net.MalformedURLException ex) {}
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
        mirrorvertButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/mirror_vert.gif")));
        } catch(java.net.MalformedURLException ex) {}
        mirrorvertButton.setToolTipText("Flip Vertical");
        mirrorvertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorVert();
            }
        });
        try{
        mirrorhorzButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/mirror_horz.gif")));
        } catch(java.net.MalformedURLException ex) {}
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
        rectButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/rectangular.gif")));
        } catch(java.net.MalformedURLException ex) {}
        rectButton.setToolTipText("Rectangular");
        rectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Rectangular");
            }
        });
        try{
        triButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/triangular.gif")));
        } catch(java.net.MalformedURLException ex) {}
        triButton.setToolTipText("Triangular");
        triButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Triangular");
            }
        });
        try{
        radialButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/radial.gif")));
        } catch(java.net.MalformedURLException ex) {}
        radialButton.setToolTipText("Radial");
        radialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Radial");
            }
        });
        try{
        polarButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/polar.gif")));
        } catch(java.net.MalformedURLException ex) {}
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
