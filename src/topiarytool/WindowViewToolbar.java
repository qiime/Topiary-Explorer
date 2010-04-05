package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.jnlp.*;
import java.io.*;


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
    JButton consoleButton = new JButton();
    
    ExtendedService es;

    MainFrame frame = null;
    
    /**
    * Class constructor
    * @param _frame Instance of MainFrame for the current run
    */
    public WindowViewToolbar(MainFrame _frame) {
        super(JToolBar.HORIZONTAL);
        frame = _frame;
        try { 
        	es = (ExtendedService)ServiceManager.lookup("javax.jnlp.ExtendedService");
    	} catch (UnavailableServiceException e) { es=null; }   
    	
        try{
        FileContents fc = es.openFile(new File("images/newproject.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        newTreeButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        newTreeButton.setToolTipText("New Project");
        newTreeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mainMenu.newProject();
            }
        });
        add(newTreeButton);
        
        try {
        FileContents fc = es.openFile(new File("images/opentree.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        openTreeButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        openTreeButton.setToolTipText("Open Project");
        openTreeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mainMenu.openProject();
            }
        });
        add(openTreeButton);
        
        try{
        FileContents fc = es.openFile(new File("images/savetree.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        saveTreeButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        saveTreeButton.setToolTipText("Save Project");
        saveTreeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mainMenu.saveProject();
            }
        });
        add(saveTreeButton);
        addSeparator();
        
        try{
        FileContents fc = es.openFile(new File("images/treewindow.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        treeButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        treeButton.setToolTipText("Tree Window");
        treeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.treeWindow.setVisible(!frame.treeWindow.isVisible());
            }
        });
        
        try{
        FileContents fc = es.openFile(new File("images/pcoawindow.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        pcoaButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        pcoaButton.setToolTipText("PCoA Window");
        pcoaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               frame.pcoaWindow.setVisible(!frame.pcoaWindow.isVisible());
            }
        });
        
        try{
        FileContents fc = es.openFile(new File("images/consolewindow.gif"));
        byte[] buffer = new byte[(int)fc.getLength()];
        fc.getInputStream().read(buffer);
        consoleButton.setIcon(new ImageIcon(buffer));
        } catch(java.io.IOException ex) {}
        consoleButton.setToolTipText("Console Window");
        consoleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               frame.consoleWindow.setVisible(!frame.consoleWindow.isVisible());
            }
        });
        
        add(treeButton);
        add(pcoaButton);
        add(consoleButton);
        
        setFloatable(false);
    }
}
