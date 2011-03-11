package topiaryexplorer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class VerticalTreeToolbar extends JToolBar {

    JButton zoomOutButton  = new JButton("-");
    JButton zoomInButton = new JButton("+");
    JSlider zoomSlider = new JSlider(JSlider.VERTICAL, 1, 500, 1);
    JPanel spacer1 = new JPanel();
    double lastValue = zoomSlider.getValue();
    double minYScale = 0;

    TreeWindow frame = null;

    public VerticalTreeToolbar(TreeWindow _frame) {

        super(JToolBar.VERTICAL);
        
        frame = _frame;
        zoomOutButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                zoomSlider.setValue(zoomSlider.getValue() - 1);
                syncTreeWithZoomSlider();
                lastValue = zoomSlider.getValue();
            }

        });
        zoomInButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                zoomSlider.setValue(zoomSlider.getValue() + 1);
                syncTreeWithZoomSlider();
                lastValue = zoomSlider.getValue();
            }

        });
        add(zoomInButton);
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setPreferredSize(new Dimension(28,200));
        zoomSlider.setMaximumSize(new Dimension(28,200));
        zoomSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (zoomSlider.getValueIsAdjusting()){
                    double changeby = zoomSlider.getValue() - lastValue;
                    syncTreeWithZoomSlider();
                    lastValue = zoomSlider.getValue();
                }
            }
        });

        add(zoomSlider);
        add(zoomOutButton);
        setFloatable(false);
    }

    public void setScale() {
        if (frame.tree.getTreeLayout().equals("Rectangular") || frame.tree.getTreeLayout().equals("Triangular")) {
            minYScale = (frame.tree.getHeight() - 2*frame.tree.getMargin())/frame.tree.getTree().getNumberOfLeaves();
        } else {
		    minYScale = (Math.min(frame.tree.getWidth(), frame.tree.getHeight())*0.5-frame.tree.getMargin())/frame.tree.getTree().depth();
        }
    }
    
    public void syncTreeWithZoomSlider() {
        if (frame.tree.getTree() == null) return;
        double newScale = minYScale * zoomSlider.getValue();
        frame.tree.setVerticalScaleFactor(newScale);
/*        frame.tree.setScaleFactor(frame.tree.getXScale(), newScale, frame.tree.getXStart(), frame.tree.getYStart());*/
    }

    public void syncZoomSliderWithTree() {
        if (frame.tree.getTree() == null) return;
        zoomSlider.setValue((int)(frame.tree.getYScale()/minYScale));
    }

}
