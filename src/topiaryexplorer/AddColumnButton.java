package topiaryexplorer;

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

/**
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public final class AddColumnButton extends JButton{
    MainFrame frame = null;
    DataTable data = null;
    JTable table = null;
    
    JPopupMenu menu = new JPopupMenu();
    
    String dir_path = "";
    
	// {{{ AddColumnButton constructor
    /**
     * 
     */
    public AddColumnButton(MainFrame _frame, DataTable _data, JTable _table) {
        super("+");
        frame = _frame;
        data = _data;
        table = _table;
        
        try{
        dir_path = (new File(".")).getCanonicalPath();
        }
        catch(IOException e)
        {}
        
        JMenuItem item = new JMenuItem("Add column...");
        item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  frame.addColumnDialog = new AddColumnDialog(frame, data, table);
              }
        });
        menu.add(item);
        item = new JMenuItem("Save as tab delimited text");
        item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  exportTable();
              }
        }); 
        menu.add(item);
        
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMenu();
            }
        });
        
    }
    
    public void showMenu() {
        menu.show(this, 0, 0);
    }
    
    public void exportTable() {
        try
		{
            ArrayList<String> lines = data.toStrings();
            ByteArrayOutputStream b = new ByteArrayOutputStream();
	    
    	    for(int i = 0; i < lines.size(); i++)
    	        b.write(lines.get(i).getBytes());
	        
            FileContents fc = frame.fss.saveFileDialog(dir_path, new String[]{"txt"}, 
    			new ByteArrayInputStream(b.toByteArray()),null);
		}
		catch(IOException e)
		{
            JOptionPane.showMessageDialog(null, "Error exporting table.\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
	// }}}
}
