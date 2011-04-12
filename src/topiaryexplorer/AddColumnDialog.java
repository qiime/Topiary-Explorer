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
 * @author meg pirrung
 */
public class AddColumnDialog extends JFrame {
    MainFrame frame = null;
    DataTable data = null;
    JTable table = null;

    JPanel buttonPanel = new JPanel();
    
    ButtonGroup opsButtons = new ButtonGroup();
    JRadioButton emptyButton = new JRadioButton("Blank", true);
    JRadioButton copyButton = new JRadioButton("Copy");
    JRadioButton combineButton = new JRadioButton("Combine");
    
    JScrollPane optionsPane = new JScrollPane();
    JList optionsTable = new JList();
    Vector<Object> cheader = new Vector<Object>();
    Vector<Object> vals = new Vector<Object>();
    
    JPanel bottomPanel = new JPanel();
    JLabel newColNameLabel = new JLabel("Name: ");
    JTextField newColTextBox = new JTextField("newCol", 10);
    JButton addButton = new JButton("Add");
    JButton cancelButton = new JButton("Cancel");
    
    public AddColumnDialog(MainFrame _frame, DataTable _data, JTable _table) {
        frame = _frame;
        data = _data;
        table = _table;
        
        this.setSize(new Dimension(500,200));
        this.setTitle("Create a new column");
        
        emptyButton.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                optionsTable.setEnabled(false);
                optionsTable.clearSelection();
            }
        });
        emptyButton.setToolTipText("Create an empty column");
        opsButtons.add(emptyButton);
        
        copyButton.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                optionsTable.setEnabled(true);
                optionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                optionsTable.clearSelection();
            }
        });
        copyButton.setToolTipText("Create a copy of an existing column");
        opsButtons.add(copyButton);
        
        combineButton.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                optionsTable.setEnabled(true);
                optionsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                optionsTable.clearSelection();
            }
        });
        combineButton.setToolTipText("Create column by combining existing columns");
        opsButtons.add(combineButton);
        
        buttonPanel.add(emptyButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(combineButton);
        
/*        cheader.add("Columns");*/
        
        optionsTable = new JList(data.getColumnNames().toArray());
        optionsTable.setEnabled(false);
        optionsPane = new JScrollPane(optionsTable);
        
/*        add(optionsPane, BorderLayout.CENTER);*/
        
        bottomPanel.add(newColNameLabel);
        bottomPanel.add(newColTextBox);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addColumn();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeWindow();
            }
        });
        bottomPanel.add(cancelButton);
        bottomPanel.add(addButton);
        
/*        add(bottomPanel, BorderLayout.SOUTH);*/

        GroupLayout layout = new GroupLayout(getContentPane());
        this.setLayout(layout);

/*        layout.setAutoCreateGaps(true);*/
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(
           layout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGroup(layout.createSequentialGroup()
              .addComponent(newColNameLabel)
              .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                   .addComponent(newColTextBox, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                   .addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
               .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                      .addComponent(addButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                      .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                )
            .addComponent(optionsPane, GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
           layout.createParallelGroup(GroupLayout.Alignment.LEADING)
           .addGroup(layout.createSequentialGroup()
              .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                   .addComponent(newColNameLabel)
                   .addComponent(newColTextBox)
                   .addComponent(addButton))
               .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                      .addComponent(buttonPanel)
                      .addComponent(cancelButton))
               .addComponent(optionsPane, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
        );
        
        this.show();
    }
    
    public void closeWindow(){
        this.dispose();
    }
     
    public void addColumn() {
        ArrayList<Object> newColumn = new ArrayList<Object>();
        String newColName = newColTextBox.getText();
        
        if(newColName.length() < 1)
        {
            JOptionPane.showMessageDialog(null, "ERROR: you must supply a name for the new column.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(data.getColumnIndex(newColName) != -1)
        {
            newColTextBox.selectAll();
            JOptionPane.showMessageDialog(null, "ERROR: you must supply a unique name for the new column.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        newColumn.add(newColName);
        
        if(emptyButton.isSelected())
        {
            for(int i = 0; i < data.getRowCount(); i++)
                newColumn.add(TopiaryFunctions.objectify(""));
        }
        else if(copyButton.isSelected()) {
            String colName = (String)optionsTable.getSelectedValue();
            newColumn.addAll(data.getColumn(data.getColumnIndex(colName)));
        }
        else if(combineButton.isSelected()) {
            Object[] colNames = optionsTable.getSelectedValues();
            ArrayList<ArrayList<Object>> cols = new ArrayList<ArrayList<Object>>();
            for(Object c : colNames)
                cols.add(data.getColumn(data.getColumnIndex((String)c)));
            String temp = "";
            for(int i = 0; i < data.getRowCount(); i++)
            {
                temp = "";
                for(ArrayList<Object> col : cols)
                    temp += col.get(i);
                newColumn.add(temp);
            }
        }
        
        data.addColumn(newColumn);
        
        SparseTableModel model = null;
        TableSorter sorter = null;
        
        model = new SparseTableModel(data.getData(),
		data.getColumnNames());
		sorter = new TableSorter(model, table.getTableHeader());
		table.setModel(sorter);
        
        frame.resetSampleMenus();
        frame.resetOtuMenus();
        this.dispose();
    }
}