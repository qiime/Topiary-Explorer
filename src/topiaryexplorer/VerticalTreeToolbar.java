package topiaryexplorer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class VerticalTreeToolbar extends JToolBar {

    JButton zoomOutButton  = new JButton("-");
    JButton zoomInButton = new JButton("+");
    JSlider zoomSlider = new JSlider(JSlider.VERTICAL, 0, 20, 0);
    JPanel spacer1 = new JPanel();
    double lastValue = zoomSlider.getValue();

    TreeWindow frame = null;

    public VerticalTreeToolbar(TreeWindow _frame) {

        super(JToolBar.VERTICAL);
        
        frame = _frame;
        zoomOutButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                zoomSlider.setValue(zoomSlider.getValue() - 1);
/*                frame.tree.changeFontSize(Math.max(frame.tree.getFontSize()-.3,1));*/
                syncTreeWithZoomSlider();
                lastValue = zoomSlider.getValue();
            }

        });
        zoomInButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                zoomSlider.setValue(zoomSlider.getValue() + 1);
/*                frame.tree.changeFontSize(Math.min(frame.tree.getFontSize()+.3,12));*/
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
                    frame.tree.changeFontSize(Math.min(12, Math.max(frame.tree.getFontSize()+(.5*changeby),1)));
                    syncTreeWithZoomSlider();
                    lastValue = zoomSlider.getValue();
                }
            }
        });

        add(zoomSlider);
        add(zoomOutButton);
        setFloatable(false);
    }

    public void syncTreeWithZoomSlider() {
        if (frame.tree.getTree() == null) return;
        double minYScale = 0;
        if (frame.tree.getTreeLayout().equals("Rectangular") || frame.tree.getTreeLayout().equals("Triangular")) {
            minYScale = (frame.tree.getHeight() - 2*frame.tree.getMargin())/frame.tree.getTree().getNumberOfLeaves();
        } else {
		    minYScale = (Math.min(frame.tree.getWidth(), frame.tree.getHeight())*0.5-frame.tree.getMargin())/frame.tree.getTree().depth();
        }
        double newScale = minYScale * Math.pow(Math.pow(2.0, 0.5), zoomSlider.getValue());
        frame.tree.setScaleFactor(frame.tree.getXScale(), newScale, frame.tree.getXStart(), frame.tree.getYStart());
    }

    public void syncZoomSliderWithTree() {
        if (frame.tree.getTree() == null) return;
        double minYScale = 0;
        if (frame.tree.getTreeLayout().equals("Rectangular") || frame.tree.getTreeLayout().equals("Triangular")) {
            minYScale = (frame.tree.getHeight() - 2*frame.tree.getMargin())/frame.tree.getTree().getNumberOfLeaves();
        } else {
		    minYScale = (Math.min(frame.tree.getWidth(), frame.tree.getHeight())*0.5-frame.tree.getMargin())/frame.tree.getTree().depth();
        }
        int currStep = (int) Math.floor(Math.log(frame.tree.getYScale()/minYScale) / Math.log(Math.pow(2.0,0.5))+0.00001);
        zoomSlider.setValue(currStep);
    }

}
