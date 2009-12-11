package topiarytool;

import java.applet.*;
import java.lang.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * @author megumi
 */
public class DbConnectWindow extends JPanel {
    mysqlConnect c;
    JPanel mainPanel = new JPanel();
    JPanel inputPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JTextField un_field = new JTextField(20);
    JTextField db_name_field = new JTextField("jdbc:mysql://127.0.0.1/topiarytool");
    JPasswordField pw_field = new JPasswordField(20);
    JLabel db_name_label = new JLabel("Database URL: ");
    JLabel un_label = new JLabel("Username: ");
    JLabel pw_label = new JLabel("Password: ");
    JButton connect_button = new JButton("Connect");
    
    /** Creates new form db_connect_pane */
    public DbConnectWindow() {
        initComponents();
    }

    private void initComponents() {
        this.setSize(new Dimension(300,150));
        //mainPanel.setPreferredSize(new Dimension(300,200));
        mainPanel.setLayout(new BorderLayout());
        inputPanel.setLayout(new GridLayout(3,2));
        inputPanel.add(db_name_label);
        inputPanel.add(db_name_field);
        inputPanel.add(un_label);
        inputPanel.add(un_field);
        inputPanel.add(pw_label);
        inputPanel.add(pw_field);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        buttonPanel.setLayout(new GridLayout(1,3));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(connect_button);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(mainPanel, BorderLayout.CENTER);
        //this.show();
    }// </editor-fold>                        

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DbConnectWindow().setVisible(true);
            }
        });
    }

}
