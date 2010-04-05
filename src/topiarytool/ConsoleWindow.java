package topiarytool;

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
public class ConsoleWindow extends JFrame{
    /** Version control identifier strings. */
    public static final String[] RCS_ID = {
        "$URL: http://macromates.com/svn/Bundles/trunk/Bundles/Java.tmbundle/Templates/Java Class/class-insert.java $",
        "$Id$",
    };
    JTextArea console = new JTextArea(20,80);
    JScrollPane pane = new JScrollPane(console);
    MainFrame frame = null;
    JButton clear = new JButton("Clear Console");
    
	// {{{ ConsoleWindow constructor
    /**
     * 
     */
    public ConsoleWindow(MainFrame _frame) {
        frame = _frame;
        this.setSize(new Dimension(500,300));
        this.setTitle("Console");
        this.setLayout(new BorderLayout());
        console.setEditable(false);
        this.add(pane, BorderLayout.CENTER);
        clear.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   console.removeAll();
               }
        });
        this.add(clear, BorderLayout.SOUTH);
    }
    
    public void update(String s) {
        console.append(s);
    }
	// }}}
}
