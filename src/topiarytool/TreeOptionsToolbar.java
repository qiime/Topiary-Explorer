package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.jnlp.*;
import java.io.*;

public class TreeOptionsToolbar extends JToolBar {

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
        
/*        try{
        FileContents fc = es.openFile(new File("images/collapse.gif"));
                byte[] buffer = new byte[(int)fc.getLength()];
                fc.getInputStream().read(buffer);
                collapseButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}*/
        tipLabelsButton.setText("Tip Labels");
        tipLabelsButton.setToolTipText("Show Tip Labels");
        tipLabelsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.showTips = !frame.showTips;
                frame.tipLabels();
            }
        });
        collapseButton.setText("Collapse");
        collapseButton.setToolTipText("Collapse");
        collapseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.collapseTree();
            }
        });
/*        try{
        FileContents fc = es.openFile(new File("images/expand.gif"));
                byte[] buffer = new byte[(int)fc.getLength()];
                fc.getInputStream().read(buffer);
                expandButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}*/
        expandButton.setText("Expand");
        expandButton.setToolTipText("Expand");
        expandButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.uncollapseTree();
            }
        });
        add(tipLabelsButton);
        add(collapseButton);
        add(expandButton);
        addSeparator();
/*        try{
        FileContents fc = es.openFile(new File("images/mirror_vert.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        mirrorvertButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}*/
        mirrorvertButton.setText("Mirror Vert");
        mirrorvertButton.setToolTipText("Flip Vertical");
        mirrorvertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorVert();
            }
        });
        /*try{
                FileContents fc = es.openFile(new File("images/mirror_horz.gif"));
                byte[] buffer = new byte[(int)fc.getLength()];
                fc.getInputStream().read(buffer);
                mirrorhorzButton.setIcon(new ImageIcon(buffer));
                } catch(java.io.IOException ex) {}*/
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
        
        /*try{
                FileContents fc = es.openFile(new File("images/rectangular.gif"));
                byte[] buffer = new byte[(int)fc.getLength()];
                fc.getInputStream().read(buffer);
                rectButton.setIcon(new ImageIcon(buffer));
                } catch(java.io.IOException ex) {}*/
        rectButton.setText("Rectangular");
        rectButton.setToolTipText("Rectangular");
        rectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Rectangular");
                mainframe.mainMenu.rectangularradiobutton.setSelected(true);
                mainframe.mainMenu.rotateSlider.setValue(0);
                mainframe.mainMenu.syncTreeWithRotateSlider();
                mainframe.mainMenu.rotateMenu.setEnabled(false);
            }
        });
        /*try{
                FileContents fc = es.openFile(new File("images/triangular.gif"));
                byte[] buffer = new byte[(int)fc.getLength()];
                fc.getInputStream().read(buffer);
                triButton.setIcon(new ImageIcon(buffer));
                } catch(java.io.IOException ex) {}*/
        triButton.setText("Triangular");
        triButton.setToolTipText("Triangular");
        triButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Triangular");
                mainframe.mainMenu.triangularradiobutton.setSelected(true);
                mainframe.mainMenu.rotateSlider.setValue(0);
                mainframe.mainMenu.syncTreeWithRotateSlider();
                mainframe.mainMenu.rotateMenu.setEnabled(false);
            }
        });
        /*try{
                FileContents fc = es.openFile(new File("images/radial.gif"));
                byte[] buffer = new byte[(int)fc.getLength()];
                fc.getInputStream().read(buffer);
                radialButton.setIcon(new ImageIcon(buffer));
                } catch(java.io.IOException ex) {}*/
        radialButton.setText("Radial");
        radialButton.setToolTipText("Radial");
        radialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Radial");
                mainframe.mainMenu.radialradiobutton.setSelected(true);
                mainframe.mainMenu.rotateMenu.setEnabled(true);
            }
        });
/*        try{
        FileContents fc = es.openFile(new File("images/polar.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        polarButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}*/
        polarButton.setText("Polar");
        polarButton.setToolTipText("Polar");
        polarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Polar");
                mainframe.mainMenu.polarradiobutton.setSelected(true);
                mainframe.mainMenu.rotateMenu.setEnabled(true);
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
