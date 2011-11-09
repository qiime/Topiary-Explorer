package topiaryexplorer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class TooltipCellRenderer extends JLabel
                           implements TableCellRenderer {
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    public TooltipCellRenderer(boolean isBordered) {
        this.isBordered = isBordered;
    }

    public Component getTableCellRendererComponent(
                            JTable table, Object text,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
        setText(text.toString());
        setToolTipText(text.toString());
        return this;
    }
}
