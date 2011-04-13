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
    
	// {{{ AddColumnButton constructor
    /**
     * 
     */
    public AddColumnButton(MainFrame _frame, DataTable _data, JTable _table) {
        super("+");
        frame = _frame;
        data = _data;
        table = _table;
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.addColumnDialog = new AddColumnDialog(frame, data, table);
            }
        });
    }
	// }}}
}
