package topiaryexplorer;

import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.*;
import java.awt.Component;
import java.awt.event.*;
import java.util.*;

public class TextCellEditor extends AbstractCellEditor
                         implements TableCellEditor,
			            ActionListener, KeyListener {
    JTable table;
    int column;
    String currentText;
    String originalText;
    JTextField field;
    JPopupMenu menu;
    protected static final String EDIT = "edit";

    public TextCellEditor() {
        //Set up the editor (from the table's point of view),
        //which is a button.
        //This button brings up the color chooser dialog,
        //which is the editor from the user's point of view.
        field = new JTextField();
        field.setActionCommand(EDIT);
        field.addKeyListener(this);
        field.addActionListener(this);
        menu = new JPopupMenu();
        
        if(table == null)
            return;
    }
    
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER)
        {
            currentText = field.getText();
            menu.hide();
            fireEditingStopped();
            // table.getModel().setValueAt()
            return;
        }
        
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_TAB)
        {
            currentText = originalText;
            menu.hide();
            fireEditingStopped();
            return;
        }
        
        if(e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            menu = new JPopupMenu();
            HashSet<String> values = new HashSet<String>();
            for(int i = 0; i < table.getModel().getRowCount(); i++)
                values.add(table.getModel().getValueAt(i, column).toString());
            
            if(values.size() == table.getModel().getRowCount())
                return;
            
            Object[] uniqueVals = values.toArray();
            Arrays.sort(uniqueVals);
            for(int i = 0; i < uniqueVals.length; i++)
            {
                JMenuItem item = new JMenuItem(uniqueVals[i].toString());
                item.addActionListener(this);
                menu.add(item);
            }
            menu.show(field, 0, 15);
        }
    }
    
    public void keyReleased(KeyEvent e) {
        
    }
    
    public void keyTyped(KeyEvent e) {
        
    }

    /**
     * Handles events from the editor button and from
     * the dialog's OK button.
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().getClass() == JMenuItem.class) {
            currentText = e.getActionCommand();
            field.setText(currentText);
            fireEditingStopped();
        }
        else {
            currentText = field.getText();
            fireEditingStopped();
        }
    }
    
    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {
            int clickCount = 2;

            return ((MouseEvent)evt).getClickCount() >= clickCount;
        }
        return true;
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return currentText;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable _table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int _column) {
        table = _table;
        column = _column;
        currentText = value.toString();
        originalText = value.toString();
        field.setText(currentText);
        return field;
    }
}