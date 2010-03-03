package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class WindowViewToolbar extends JToolBar {

    JButton treeButton  = new JButton();
    JButton pcoaButton = new JButton();

    MainFrame frame = null;

    public WindowViewToolbar(MainFrame _frame) {
        super(JToolBar.HORIZONTAL);
        frame = _frame;
        
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
