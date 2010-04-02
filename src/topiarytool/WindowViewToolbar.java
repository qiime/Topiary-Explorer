package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.jnlp.*;


/**
 * Toolbar for holding buttons for commonly used operations such as
 * load tree, save tree, view tree window and view PCoA window. 
 *
 */
public class WindowViewToolbar extends JToolBar {
    JButton newTreeButton = new JButton();
    JButton openTreeButton = new JButton();
    JButton saveTreeButton = new JButton();
    JButton exportTreeButton = new JButton();
    JButton treeButton  = new JButton();
    JButton pcoaButton = new JButton();
    
    BasicService bs;

    MainFrame frame = null;
    
    /**
    * Class constructor
    * @param _frame Instance of MainFrame for the current run
    */
    public WindowViewToolbar(MainFrame _frame) {
        super(JToolBar.HORIZONTAL);
        frame = _frame;
        try { 
        	bs = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
    	} catch (UnavailableServiceException e) { bs=null; }   
    	
        try{
        newTreeButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/newproject.gif")));
        } catch(java.net.MalformedURLException ex) {}
        newTreeButton.setToolTipText("New Project");
        newTreeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mainMenu.newProject();
            }
        });
        add(newTreeButton);
        
        try {
        openTreeButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/opentree.gif")));
        } catch(java.net.MalformedURLException ex) {}
        openTreeButton.setToolTipText("Open Project");
        openTreeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mainMenu.openProject();
            }
        });
        add(openTreeButton);
        
        try{
        saveTreeButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/savetree.gif")));
        } catch(java.net.MalformedURLException ex) {}
        saveTreeButton.setToolTipText("Save Project");
        saveTreeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mainMenu.saveProject();
            }
        });
        add(saveTreeButton);
        addSeparator();
        
        try{
        treeButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/treewindow.gif")));
        } catch(java.net.MalformedURLException ex) {}
        treeButton.setToolTipText("Tree Window");
        treeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.treeWindow.setVisible(!frame.treeWindow.isVisible());
            }
        });
        
        try{
        pcoaButton.setIcon(new ImageIcon(new java.net.URL(bs.getCodeBase().toString() + "images/pcoawindow.gif")));
        } catch(java.net.MalformedURLException ex) {}
        pcoaButton.setToolTipText("PCoA Window");
        pcoaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               frame.pcoaWindow.setVisible(!frame.pcoaWindow.isVisible());
            }
        });
        
        add(treeButton);
        add(pcoaButton);
        
        setFloatable(false);
    }
}
