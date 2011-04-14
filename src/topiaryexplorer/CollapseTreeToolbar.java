package topiaryexplorer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;

/**
* A toolbar containing a slider which determines the collapse level of the tree.
**/

class CollapseTreeToolbar extends JToolBar {
    // The slider represents the percent of the tree length
    // that is collapsed, so the value of the slider divided
    // by 1000 gives you the percentage of the tree that
    // should be collapsed.
    private JSlider collapseSlider = new JSlider(0, 1001, 10);
    private JPanel spacer = new JPanel();
    private JLabel collapseLabel = new JLabel("Collapse tree: ");

    private TreeWindow frame = null;
    
    /**
    * Creates a slider within a toolbar for collapsing a tree.
    **/
    CollapseTreeToolbar(TreeWindow _frame) {

        frame = _frame;
        add(collapseLabel);

        spacer.setMinimumSize(new Dimension(this.getWidth() - frame.treeHolder.getWidth(), 28));
        spacer.setMaximumSize(new Dimension(this.getWidth() - frame.treeHolder.getWidth(), 28));
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

        add(spacer);
        add(collapseSlider);
        setFloatable(false);
    }
    
    /**
    * Set the value of the collapse slider.
    **/
    void setValue(int v) {
        // Slider only goes from 0 to 1000
        if(v >= 0 && v <= 1001)
            collapseSlider.setValue(v);  
        syncTreeWithCollapseSlider();      
    }
    
    void setMax() {
        collapseSlider.setValue(collapseSlider.getMaximum());
    }
    
    void sliderEnabled(boolean b) {
        collapseSlider.setEnabled(b);
        if(b)
            collapseSlider.setToolTipText("Drag slider to collapse tree.");
        else
            collapseSlider.setToolTipText("The collapse slider is not available in this view.");
    }

    /**
    * Reset the slider width if the window containing it is modified.
    **/
    void resetLayout() {
        spacer.setMinimumSize(new Dimension(this.getWidth() - frame.treeHolder.getWidth(), 28));
        spacer.setMaximumSize(new Dimension(this.getWidth() - frame.treeHolder.getWidth(), 28));
        collapseSlider.setPreferredSize(new Dimension(frame.treeHolder.getWidth(),28));
        syncTreeWithCollapseSlider();
    }

    /**
    * Redraw the tree at the appropriate collapse level.
    **/
    void syncTreeWithCollapseSlider() {
        if (frame.tree==null) return;
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        frame.tree.setCollapsedLevel(1-collapseSlider.getValue()/1000.0);
        frame.tree.redraw();
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

}
