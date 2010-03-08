package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


/**
 * Toolbar for holding buttons for commonly used operations such as
 * load tree, save tree, view tree window and view PCoA window. 
 *
 */
public class WindowViewToolbar extends JToolBar {
    
    JButton openTreeButton = new JButton();
    JButton saveTreeButton = new JButton();
    JButton exportTreeButton = new JButton();
    JButton treeButton  = new JButton();
    JButton pcoaButton = new JButton();

    MainFrame frame = null;
    
    /**
    * Class constructor
    * @param _frame Instance of MainFrame for the current run
    */
    public WindowViewToolbar(MainFrame _frame) {
        super(JToolBar.HORIZONTAL);
        frame = _frame;
        
        openTreeButton.setIcon(new ImageIcon("images/opentree.gif"));
        openTreeButton.setToolTipText("Load Tree");
        openTreeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.treeWindow.loadTree();
            }
        });
        add(openTreeButton);
        
        saveTreeButton.setIcon(new ImageIcon("images/savetree.gif"));
        saveTreeButton.setToolTipText("Save Tree");
        saveTreeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.treeWindow.saveTree();
            }
        });
        add(saveTreeButton);
        addSeparator();
        
        treeButton.setIcon(new ImageIcon("images/treewindow.gif"));
        treeButton.setToolTipText("Tree Window");
        treeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.treeWindow.setVisible(!frame.treeWindow.isVisible());
            }
        });
        pcoaButton.setIcon(new ImageIcon("images/pcoawindow.gif"));
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
