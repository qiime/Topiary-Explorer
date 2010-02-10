package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class TreeOptionsToolbar extends JToolBar {

    JButton collapseButton  = new JButton("Collapse");
    JButton expandButton = new JButton("Expand");
    JButton mirrorvertButton = new JButton("Flip");
    JButton mirrorhorzButton = new JButton("Flip");
    JButton rectButton = new JButton("Rectangular");
    JButton triButton = new JButton("Triangular");
    JButton radialButton = new JButton("Radial");
    JButton polarButton = new JButton("Polar");

    MainFrame frame = null;

    public TreeOptionsToolbar(MainFrame _frame) {
        super(JToolBar.HORIZONTAL);
        frame = _frame;
        
        collapseButton.setIcon(new ImageIcon("images/collapse.gif"));
        collapseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.getTree().setCollapsed(true);
            }
        });
        expandButton.setIcon(new ImageIcon("images/expand.gif"));
        expandButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.getTree().setCollapsed(false);
            }
        });
        add(collapseButton);
        add(expandButton);
        addSeparator();
        
        mirrorvertButton.setIcon(new ImageIcon("images/mirror_vert.gif"));
        mirrorvertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorVert();
            }
        });
        mirrorhorzButton.setIcon(new ImageIcon("images/mirror_horz.gif"));
        mirrorhorzButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorHorz();
            }
        });
        add(mirrorvertButton);
        add(mirrorhorzButton);
        addSeparator();
        
        rectButton.setIcon(new ImageIcon("images/rectangular.gif"));
        rectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Rectangular");
            }
        });
        triButton.setIcon(new ImageIcon("images/triangular.gif"));
        triButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Triangular");
            }
        });
        radialButton.setIcon(new ImageIcon("images/radial.gif"));
        radialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Radial");
            }
        });
        polarButton.setIcon(new ImageIcon("images/polar.gif"));
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
