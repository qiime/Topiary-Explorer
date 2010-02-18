package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class TreeOptionsToolbar extends JToolBar {

    JButton collapseButton  = new JButton();
    JButton expandButton = new JButton();
    JButton mirrorvertButton = new JButton();
    JButton mirrorhorzButton = new JButton();
    JButton rectButton = new JButton();
    JButton triButton = new JButton();
    JButton radialButton = new JButton();
    JButton polarButton = new JButton();

    MainFrame frame = null;

    public TreeOptionsToolbar(MainFrame _frame) {
        super(JToolBar.HORIZONTAL);
        frame = _frame;
        
        collapseButton.setIcon(new ImageIcon("images/collapse.gif"));
        collapseButton.setToolTipText("Collapse");
        collapseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.collapseTree();
            }
        });
        expandButton.setIcon(new ImageIcon("images/expand.gif"));
        expandButton.setToolTipText("Expand");
        expandButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.uncollapseTree();
            }
        });
        add(collapseButton);
        add(expandButton);
        addSeparator();
        
        mirrorvertButton.setIcon(new ImageIcon("images/mirror_vert.gif"));
        mirrorvertButton.setToolTipText("Flip Vertical");
        mirrorvertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorVert();
            }
        });
        mirrorhorzButton.setIcon(new ImageIcon("images/mirror_horz.gif"));
        mirrorhorzButton.setToolTipText("Flip Horizontal");
        mirrorhorzButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorHorz();
            }
        });
        add(mirrorvertButton);
        add(mirrorhorzButton);
        addSeparator();
        
        rectButton.setIcon(new ImageIcon("images/rectangular.gif"));
        rectButton.setToolTipText("Rectangular");
        rectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Rectangular");
            }
        });
        triButton.setIcon(new ImageIcon("images/triangular.gif"));
        triButton.setToolTipText("Triangular");
        triButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Triangular");
            }
        });
        radialButton.setIcon(new ImageIcon("images/radial.gif"));
        radialButton.setToolTipText("Radial");
        radialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Radial");
            }
        });
        polarButton.setIcon(new ImageIcon("images/polar.gif"));
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
        
        setFloatable(false);
    }

}
