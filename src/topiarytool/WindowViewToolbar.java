package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class WindowViewToolbar extends JToolBar {

    JButton treeButton  = new JButton();
    JButton pcoaButton = new JButton();
    JButton dataButton = new JButton();

    MainFrame frame = null;

    public WindowViewToolbar(MainFrame _frame) {
        super(JToolBar.HORIZONTAL);
        frame = _frame;
        
        treeButton.setIcon(new ImageIcon("images/treewindow.gif"));
        treeButton.setToolTipText("Tree Window");
        treeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //frame.collapseTree();
            }
        });
        pcoaButton.setIcon(new ImageIcon("images/pcoawindow.gif"));
        pcoaButton.setToolTipText("PCoA Window");
        pcoaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               // frame.uncollapseTree();
            }
        });
        dataButton.setIcon(new ImageIcon("images/datawindow.gif"));
        dataButton.setToolTipText("Data Window");
        dataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //frame.mirrorVert();
            }
        });
        
        add(treeButton);
        add(pcoaButton);
        add(dataButton);
        
        setFloatable(false);
    }
}
