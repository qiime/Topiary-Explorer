package topiaryexplorer;
/*
 * databaseFilterPane.java
 *
 * Created on Jun 16, 2011, 12:30:52 PM
 */

/**
 *
 * @author meg pirrung
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.media.opengl.*;
import java.sql.*;
import javax.swing.table.*;
import java.io.*;
import javax.swing.table.*;
import javax.jnlp.*;
import javax.swing.text.*;
import java.text.*;

public class DatabaseFilterPanel extends JPanel {
    MainFrame frame = null;
    String tableName = "";
    JPanel selectTablePanel = new JPanel();
    JTable tableNamesTable = new JTable(new SparseTableModel());
    JScrollPane tableNamesScrollPane = new JScrollPane();
    DataTable tableNames = new DataTable();
    // JList tableNames = new JList();
    JLabel selectTableLabel = new JLabel("Select A Table");
    JTextField searchField = new JTextField();
    JLabel searchLabel = new JLabel("Search");
    JPanel columnNamesPanel = new JPanel();
    JLabel columnsLabel = new JLabel("Select Columns");
    JScrollPane columnNamesScrollPane = new JScrollPane();
    JList columnNamesList = new JList();
    JButton addColumnFilterButton = new JButton("v");
    JButton resetColumnFilterButton = new JButton("Reset");
    JPanel columnFiltersPanel = new JPanel();
    JTabbedPane columnFiltersTabPane = new JTabbedPane();
    ArrayList<ColumnFilterTab> filterTabs = new ArrayList<ColumnFilterTab>();
    JButton filterTableButton = new JButton("Filter Table");
    HashSet filters = new HashSet();

    /** Creates new form databaseFilterPane */
    public DatabaseFilterPanel(MainFrame _frame) {
        frame = _frame;
        tableNamesScrollPane = new JScrollPane(tableNamesTable);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
    		public void changedUpdate(javax.swing.event.DocumentEvent evt) {
    			doSearch();
    		}

    		public void insertUpdate(javax.swing.event.DocumentEvent evt) {
    			doSearch();
    		}

    		public void removeUpdate(javax.swing.event.DocumentEvent evt) {
    			doSearch();
    		}

    		private void doSearch() {
                // String str = search.getText().trim().toUpperCase();
    			
    		}
    	});
        
        columnNamesScrollPane.setViewportView(columnNamesList);

        addColumnFilterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addColumnFilterButtonActionPerformed(evt);
            }
        });

        resetColumnFilterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                resetButtonPressed();
            }
        });
        
        tableNamesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        tableNamesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                if(tableNamesTable.getSelectedRow() == -1)
                  return;
                    
                tableName = (String)tableNamesTable.getValueAt(tableNamesTable.getSelectedRow(), 0);
                if(frame.db_conn.c.getTableHeaders(tableName))
                {
                    ArrayList<String> result = frame.db_conn.c.getResultLines();
                    DefaultListModel model = new DefaultListModel();
                    for(String s : result)
                        model.addElement(s);
                    columnNamesList.setModel(model);
                }
                resetButtonPressed();
            }
        });
        
        filterTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                filterButtonActionPerformed(evt);
            }
        });
        filterTableButton.setEnabled(false);
        
        // obnoxious layout stuff
        GroupLayout selectTablePanelLayout = new GroupLayout(selectTablePanel);
        selectTablePanel.setLayout(selectTablePanelLayout);
        selectTablePanelLayout.setHorizontalGroup(
            selectTablePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(selectTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(selectTablePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(selectTablePanelLayout.createSequentialGroup()
                        .addComponent(tableNamesScrollPane, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(selectTablePanelLayout.createSequentialGroup()
                        // .addComponent(searchLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        // .addComponent(searchField, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(71, Short.MAX_VALUE))
                    .addGroup(selectTablePanelLayout.createSequentialGroup()
                        .addComponent(selectTableLabel)
                        .addContainerGap(142, Short.MAX_VALUE))))
        );
        selectTablePanelLayout.setVerticalGroup(
            selectTablePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(selectTablePanelLayout.createSequentialGroup()
                .addComponent(selectTableLabel)
                .addGap(4, 4, 4)
                .addComponent(tableNamesScrollPane, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(selectTablePanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    // .addComponent(searchLabel)
                    // .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    ))
        );

        GroupLayout columnNamesPanelLayout = new GroupLayout(columnNamesPanel);
        columnNamesPanel.setLayout(columnNamesPanelLayout);
        columnNamesPanelLayout.setHorizontalGroup(
            columnNamesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, columnNamesPanelLayout.createSequentialGroup()
                // .addGap(106, 106, 106)
                .addComponent(resetColumnFilterButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(addColumnFilterButton, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
                // .addContainerGap()
                .addGap(10, 10, 10)
                )
            .addGroup(columnNamesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(columnsLabel)
                .addContainerGap(133, Short.MAX_VALUE))
            .addGroup(columnNamesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(columnNamesScrollPane, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        columnNamesPanelLayout.setVerticalGroup(
            columnNamesPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(columnNamesPanelLayout.createSequentialGroup()
                .addComponent(columnsLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(columnNamesScrollPane, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(columnNamesPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(addColumnFilterButton)
                    .addComponent(resetColumnFilterButton)))
        );

        GroupLayout columnFiltersPanelLayout = new GroupLayout(columnFiltersPanel);
        columnFiltersPanel.setLayout(columnFiltersPanelLayout);
        columnFiltersPanelLayout.setHorizontalGroup(
            columnFiltersPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(columnFiltersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(columnFiltersTabPane, GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE))
        );
        columnFiltersPanelLayout.setVerticalGroup(
            columnFiltersPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, columnFiltersPanelLayout.createSequentialGroup()
                .addComponent(columnFiltersTabPane, GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                .addContainerGap())
        );

        columnFiltersTabPane.getAccessibleContext().setAccessibleName("SampleID");

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(selectTablePanel, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(columnNamesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(columnFiltersPanel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(386, Short.MAX_VALUE)
                        .addComponent(filterTableButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(selectTablePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(columnNamesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(7, 7, 7)
                .addComponent(columnFiltersPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(filterTableButton)
                .addContainerGap())
        );
    }

    public void addColumnFilterButtonActionPerformed(ActionEvent evt) {
        for(Object o : columnNamesList.getSelectedValues())
        {
            if(filters.contains((String)o))
                continue;
                
            if(frame.db_conn.c.getValuesByHeader(tableName, (String)o))
            {
                filters.add((String)o);
                ArrayList<String> result = frame.db_conn.c.getResultLines();
                ColumnFilterTab newTab = new ColumnFilterTab(frame, (String)o, result);
                columnFiltersTabPane.add((String)o, newTab);
                filterTabs.add(newTab);
                filterTableButton.setEnabled(true);
            }
        }
    }

    public void resetButtonPressed() {
        columnFiltersTabPane.removeAll();
        filters = new HashSet();
        filterTabs.clear();
        filterTableButton.setEnabled(false);
    }
    
    public void filterButtonActionPerformed(ActionEvent evt) {
        String query = "select * from "+tableName+" where (";
        
        for(ColumnFilterTab ct : filterTabs)
        {
            for(Object o : ct.getSelectedValues())
                query += ct.title + "=\"" + (String)o + "\" OR ";
            // get rid of extra OR
            query = query.substring(0, query.length()-4);
            query += ") AND (";
        }
        // get rid of extra AND (
        if(columnFiltersTabPane.getTabCount() > 0)
            query = query.substring(0, query.length()-6);
        
        // System.out.println(query);
        
        frame.db_conn.c.searchCurrentTable(query);
        // if(frame.db_conn.c.resultLines.size() > 10000)
        // {
        //     JOptionPane.showMessageDialog(null, "Only SELECT statements are allowed.", "Error", JOptionPane.ERROR_MESSAGE);
        //     return;
        // }
        if (frame.db_conn.c.resultLines.size() == 0) {
            JOptionPane.showMessageDialog(null, "No rows match your selections.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DataTable table = new DataTable(frame.db_conn.c);
        TableWindow tWindow = new TableWindow(frame, table);
    }
}
