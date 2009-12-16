package topiarytool;

import java.applet.*;
import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 *
 * @author megumi
 */
public class DbSearchWindow extends JPanel {
    JTable optionsTable = new JTable();
    JPanel mainPanel = new JPanel();
    JPanel inputPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JPanel rButtonspanel = new JPanel();
    JRadioButton orRadioButton = new JRadioButton("OR", true);
    JRadioButton andRadioButton = new JRadioButton("AND");
    ButtonGroup selectionRadioButtons = new ButtonGroup();
    JPanel selectionPanel = new JPanel();
    JButton searchButton = new JButton("Search");
    JButton resetButton = new JButton("Reset");
    JScrollPane optionsPane = new JScrollPane();
    JLabel searchLabel = new JLabel("Search database for selected options.");
    
    public DbSearchWindow() {
        initComponents();
    }

    private void initComponents() {
        this.setSize(new Dimension(400,150));
        mainPanel.setLayout(new BorderLayout());
        
        selectionRadioButtons.add(orRadioButton);
        selectionRadioButtons.add(andRadioButton);
        
        selectionPanel.setLayout(new GridLayout(1,3));
        selectionPanel.add(new JLabel("Search mode: "));
        orRadioButton.setToolTipText("eg, Select samples where sex = f  OR age = 30");
        andRadioButton.setToolTipText("eg, Select samples where sex = f AND age = 30");
        selectionPanel.add(orRadioButton);
        selectionPanel.add(andRadioButton);      
        
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        buttonPanel.setLayout(new GridLayout(1,5));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(resetButton);
        buttonPanel.add(searchButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(mainPanel, BorderLayout.CENTER);
        this.show();
    }                       

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new DbSearchWindow().setVisible(true);
            }
        });
    }

}
