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

public class ColumnFilterTab extends JPanel {
    MainFrame frame = null;
    String title = "";
    ArrayList<String> values = new ArrayList<String>();
    JList valuesList = new JList();
    JScrollPane valuesScrollPane = new JScrollPane();
    
    public ColumnFilterTab(MainFrame _frame, String _title, ArrayList<String> _values) {
        frame = _frame;
        title = _title;
        values = _values;
        this.setLayout(new BorderLayout());
        HashSet uniqueVals = new HashSet(values);
        valuesList = new JList(uniqueVals.toArray(new String[uniqueVals.size()]));
        valuesScrollPane.setViewportView(valuesList);
        add(valuesScrollPane, BorderLayout.CENTER);
    }
    
    public Object[] getSelectedValues() {
        return valuesList.getSelectedValues();
    }
}