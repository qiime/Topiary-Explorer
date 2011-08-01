package topiaryexplorer;

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
 * @author meg
 */
public class DbConnectWindow extends JPanel {
    dbConnect c;
    MainFrame frame = null;
    JPanel mainPanel = new JPanel();
    JPanel inputPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JTextField un_field = new JTextField(20);
    JTextField db_field = new JTextField(20);
    JTextField sv_field = new JTextField(20);
    JPasswordField pw_field = new JPasswordField(20);
    JLabel db_label = new JLabel("Database: ");
    JLabel sv_label = new JLabel("Server: ");
    JLabel un_label = new JLabel("Username: ");
    JLabel pw_label = new JLabel("Password: ");
    JButton connect_button = new JButton("Connect");
    
    public DbConnectWindow(MainFrame _frame) {
        frame = _frame;
        initComponents();
    }

    private void initComponents() {
        this.setSize(new Dimension(300,150));
        mainPanel.setLayout(new BorderLayout());
        inputPanel.setLayout(new GridLayout(4,2));
        inputPanel.add(sv_label);
        inputPanel.add(sv_field);
        inputPanel.add(db_label);
        inputPanel.add(db_field);
        inputPanel.add(un_label);
        inputPanel.add(un_field);
        inputPanel.add(pw_label);
        inputPanel.add(pw_field);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        buttonPanel.setLayout(new GridLayout(1,3));
        buttonPanel.add(new JLabel(""));
        buttonPanel.add(new JLabel(""));
        connect_button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        connectButtonPressed();
                    }
                });
        buttonPanel.add(connect_button);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(mainPanel, BorderLayout.CENTER);
        //this.show();
    }// </editor-fold>                        

    public void connectButtonPressed() {
         if(connect_button.getText() == "Connect")
         {
             frame.databaseStatus.setText("Trying to connect to database...");
             if(connectToDB())
             {
                 frame.showData.setEnabled(true);
                 db_field.disable();
                 sv_field.disable();
                 un_field.disable();
                 pw_field.disable();
                 connect_button.setText("Disconnect");
                 frame.databaseTabPane.setEnabledAt(1, true);
                 frame.databaseStatus.setText("Connected to "+db_field.getText()+" on " + sv_field.getText());
                 
                 c.getAvailableTables();
                 frame.resetDatabaseTable();
                 frame.databaseTabPane.setEnabledAt(0, true);
                 frame.databaseTabPane.setSelectedIndex(1);
                 frame.databaseTabPane.setEnabledAt(1, true);
                 frame.databaseTabPane.setEnabledAt(2, true);
             }
             else
             {
                 JOptionPane.showMessageDialog(null, "ERROR: could not connect to database.", "Error", JOptionPane.ERROR_MESSAGE);
                 frame.databaseStatus.setText("Database connection failed.");
            }
             
         }
         else
         {
             c.close_connection();
             frame.back.setEnabled(false);
             frame.showData.setEnabled(false);
             frame.setAs.setEnabled(false);
             db_field.enable();
             sv_field.enable();
             un_field.enable();
             pw_field.enable();
             connect_button.setText("Connect");
             frame.databaseStatus.setText("No database connected.");
             frame.databaseTabPane.setEnabledAt(1, false);
             frame.databaseTabPane.setEnabledAt(2, false);
         }
     }
     
     public Boolean connectToDB() {
        String db = db_field.getText();
        String sv = sv_field.getText();
        String un = un_field.getText();
        String pw = pw_field.getText();
        c = new dbConnect(un,pw,db,sv);  
        return c.makeConnection();
     }

}
