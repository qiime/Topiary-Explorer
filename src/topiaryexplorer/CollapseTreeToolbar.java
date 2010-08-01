package topiaryexplorer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class CollapseTreeToolbar extends JToolBar {
    JSlider collapseSlider = new JSlider(0, 1000, 1000);
    JPanel spacer1 = new JPanel();

    TreeWindow frame = null;

    public CollapseTreeToolbar(TreeWindow _frame) {

        frame = _frame;

//        this.setLayout(new BorderLayout());

        spacer1.setMinimumSize(new Dimension(this.getWidth() - frame.treeHolder.getWidth(), 28));
        spacer1.setMaximumSize(new Dimension(this.getWidth() - frame.treeHolder.getWidth(), 28));
        collapseSlider.setSnapToTicks(true);
        collapseSlider.setToolTipText("Drag slider to collapse tree.");
        collapseSlider.setPreferredSize(new Dimension(this.getWidth(),28));
        collapseSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (collapseSlider.getValueIsAdjusting()){
                    syncTreeWithCollapseSlider();
                }
            }
        });

        add(spacer1);
        add(collapseSlider);
        setFloatable(false);
    }

    public void resetLayout() {
        spacer1.setMinimumSize(new Dimension(this.getWidth() - frame.treeHolder.getWidth(), 28));
        spacer1.setMaximumSize(new Dimension(this.getWidth() - frame.treeHolder.getWidth(), 28));
        collapseSlider.setPreferredSize(new Dimension(frame.treeHolder.getWidth(),28));
        syncTreeWithCollapseSlider();
    }

    public void syncTreeWithCollapseSlider() {
        if (frame.tree==null) return;
        float pixelsIn = (float)(collapseSlider.getValue()/1000.0 * frame.treeHolder.getWidth());
        frame.tree.setCollapsedPixel(pixelsIn);
    }

}
