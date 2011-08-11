package topiaryexplorer;

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
 * @author meg
 */
public class DbSearchWindow extends JPanel {
    MainFrame frame = null;
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
    JTextField query = new JTextField(30);
    JButton updatequery = new JButton("Update");
    JPanel querypanel = new JPanel(new BorderLayout());
    JLabel querylabel = new JLabel("You can enter an SQL query or generate one by clicking on values of interest.");
    
    public DbSearchWindow(MainFrame _frame) {
        frame = _frame;
        initComponents();
    }

    private void initComponents() {
        this.setSize(new Dimension(400,150));
        mainPanel.setLayout(new BorderLayout());
        
        querypanel.add(querylabel, BorderLayout.NORTH);
        query.setToolTipText("Enter an SQL query");
        querypanel.add(query, BorderLayout.CENTER);
        updatequery.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e){
               updateQuery();
           }
        });
        querypanel.add(updatequery, BorderLayout.EAST);
        mainPanel.add(querypanel, BorderLayout.NORTH);
                
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
        searchButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e){
               searchButtonPressed();
           }
        });
        resetButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e){
               frame.db_conn.c.resetCurrentTable();
           } 
        });
        buttonPanel.add(resetButton);
        buttonPanel.add(searchButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(mainPanel, BorderLayout.CENTER);
        this.show();
    }                       

    public void searchButtonPressed() {
        if(query.getText().length() < 6)
        {
            JOptionPane.showMessageDialog(null, "Only SELECT statements are allowed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // System.out.println(query.getText().substring(0,6));
        
         if(query.getText().substring(0,6).toUpperCase().equals("SELECT"))
         {
             updateQuery();
             if(frame.db_conn.c.searchCurrentTable(query.getText()))
             {
                 DataTable table = new DataTable(frame.db_conn.c);
                 TableWindow tWindow = new TableWindow(frame, table);
             }
         }
         else
             JOptionPane.showMessageDialog(null, "Only SELECT statements are allowed.", "Error", JOptionPane.ERROR_MESSAGE);
            
      }

      public void updateQuery() {
          int rowIndexStart = frame.databaseTable.getSelectedRow();
           if(rowIndexStart != -1 && frame.db_conn.c.currentTable != null)
           {
               String temp = "select * from "+ frame.db_conn.c.currentTable +" where ";
               Boolean useor = true;
               if(andRadioButton.isSelected() == true)
                   useor = false;
               int rowIndexEnd = frame.databaseTable.getSelectionModel().getMaxSelectionIndex();
               int colIndexStart = frame.databaseTable.getSelectedColumn();
               int colIndexEnd = frame.databaseTable.getColumnModel().getSelectionModel().getMaxSelectionIndex();
               String[] headers = frame.database.getColumnNames().toArray(new String[0]);
               // Check each cell in the range
               for (int r=rowIndexStart; r<=rowIndexEnd; r++) {
                   for (int c=colIndexStart; c<=colIndexEnd; c++) {
                       if (frame.databaseTable.isCellSelected(r, c)) {
                           // cell is selected
                           temp += headers[c] + " = ";
                           temp += "\'" + frame.databaseTable.getValueAt(r,c).toString() + "\'";
                           if(useor)
                           {
                               temp += " OR ";
                           }
                           else
                             temp += " AND ";
                       }
                   }
               }
               temp = temp.trim();
               query.setText(temp.substring(0,temp.lastIndexOf(' ')));
           }
      }

}
