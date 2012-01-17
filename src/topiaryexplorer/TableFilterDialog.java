package topiaryexplorer;

import com.sun.opengl.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.media.opengl.*;
import java.sql.*;
import javax.swing.table.*;
import java.io.*;
import javax.jnlp.*;
import java.text.*;

public class TableFilterDialog extends JDialog {
    MainFrame frame = null;
    TableWindow parent = null;
    DataTable search_table = null;
    DataTable ref_table = null;
    JLabel firstLabel = new JLabel("Filter using values in column");
    JComboBox columnComboBox = new JComboBox();
    JLabel secondLabel = new JLabel("from table");
    JComboBox tableComboBox = new JComboBox(new Object[]{"Tip Data", "OTU Table", "Sample Data"});
    JTextField delimiterTextField = new JTextField(";");
    JLabel searchLabel = new JLabel("Search for");
    ButtonGroup toggleButtons = new ButtonGroup();
    JToggleButton lastToggleButton = new JToggleButton("Last",true);
    JToggleButton anyToggleButton = new JToggleButton("Any");
    JToggleButton allToggleButton = new JToggleButton("Exact");
    JLabel delimiterLabel = new JLabel("Delimiter");
    JButton filterButton = new JButton("Filter");
    JButton cancelButton = new JButton("Cancel");
    
    public TableFilterDialog(MainFrame _frame, TableWindow _parent, DataTable _dataTable) {
        frame = _frame;
        parent = _parent;
        search_table = _dataTable;
        
        this.setSize(new Dimension(350,230));
        
        filterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filterButtonPressed();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButtonPressed();
            }
        });
        
        toggleButtons.add(lastToggleButton);
        // anyToggleButton.setEnabled(false);
        toggleButtons.add(anyToggleButton);
        // allToggleButton.setEnabled(false);
        toggleButtons.add(allToggleButton);
        
        tableComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                tableSelected();
            }
        });
        tableSelected();
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(delimiterLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delimiterTextField))
                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(searchLabel)
                        .addGap(18, 18, 18)
                        .addComponent(lastToggleButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(anyToggleButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(allToggleButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(secondLabel)
                            .addComponent(firstLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tableComboBox, 0, 150, Short.MAX_VALUE)
                            .addComponent(columnComboBox, 0, 150, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(178, Short.MAX_VALUE)
                .addComponent(cancelButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(filterButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(columnComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(secondLabel)
                    .addComponent(tableComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(searchLabel)
                    .addComponent(lastToggleButton)
                    .addComponent(anyToggleButton)
                    .addComponent(allToggleButton))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(delimiterTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(delimiterLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(filterButton)
                    .addComponent(cancelButton))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        this.setVisible(true);
    }
    
    public void cancelButtonPressed() {
        this.dispose();
    }
    
    public void filterButtonPressed() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int ref_col = columnComboBox.getSelectedIndex(); //column to search by
        int search_col = 0; //column to be searched
        ArrayList<String> filteredTable = new ArrayList<String>(); // lines of new filtered table
        //make a header for the new table
        String temp = "#ID\t";
        for(String s : search_table.getColumnNames())
            temp += s+"\t";
        temp = temp.substring(0,temp.length()-1)+"\n";
        filteredTable.add(temp);
        
        String search_term = "";
        ArrayList<String> search_terms = new ArrayList<String>();
        int found_index = 0;
        // loop through each search term in the reference table
        for(int i = 0; i < ref_table.getRowCount(); i++)
        {
            // need to keep track of the id for merging later
            temp = ref_table.getValueAt(i,0)+"\t"; // get the ID
            
            if(lastToggleButton.isSelected())
            {
                search_term = ""+ref_table.getValueAt(i, ref_col);
                String delimiter = delimiterTextField.getText();
                search_term = search_term.split(delimiter)[search_term.split(delimiter).length-1];
            }
            
            if(anyToggleButton.isSelected())
            {
                search_term = ""+ref_table.getValueAt(i, ref_col);
                Collections.addAll(search_terms, search_term.split(delimiterTextField.getText()));
            }
            
            if(allToggleButton.isSelected())
            {
                search_term = ""+ref_table.getValueAt(i, ref_col);
            }
            
            search_term = search_term.toLowerCase();

            // have to turn the objects into strings for proper matching
            ArrayList<String> searches = new ArrayList<String>();
            for(Object o : search_table.getColumn(search_col))
                searches.add(o.toString().toLowerCase());
            
            
            // going through the array backwards because usually the most
            // specific term is the rightmost
            for(int j = search_terms.size()-1; j >= 0; j--)
            {
                search_term = search_terms.get(j).toLowerCase();
                if(searches.indexOf(search_term) != -1)
                    break;
            }
            
            found_index = searches.indexOf(search_term);
            
            // if the search term isn't found, add appropriate blank spots
            if(found_index == -1)
            {
                for(int k = 0; k<search_table.getColumnCount(); k++)
                    temp += "\t";
            }
            else
            {
                for(int k = 0; k<search_table.getColumnCount(); k++)
                    temp += search_table.getValueAt(found_index,k)+"\t";
            }
            temp = temp.substring(0,temp.length()-1)+"\n";
            filteredTable.add(temp);
        }
        
        // for(String s:filteredTable)
            // System.out.println(s);
        try{
        parent = new TableWindow(frame, new DataTable(filteredTable));
        }
        catch(ParseException e)
        {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(null, "ERROR: Could not set filtered table.", "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.dispose();
    }
    
    public void tableSelected() {
        if(tableComboBox.getSelectedIndex() == 0)
        {
            ref_table = frame.otuMetadata;
        }
        else if(tableComboBox.getSelectedIndex() == 1)
        {
            ref_table = frame.otuSampleMap;
        }
        else if(tableComboBox.getSelectedIndex() == 2)
        {
            ref_table = frame.sampleMetadata;
        }
        
        if(ref_table == null)
        {
            JOptionPane.showMessageDialog(null, "ERROR: The table you have selected is empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        DefaultComboBoxModel model = new DefaultComboBoxModel(
            ref_table.getColumnNames().toArray());
        columnComboBox.setModel(model);
    }
    
}