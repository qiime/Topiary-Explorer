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
    JPanel holder = new JPanel();
    JPanel spacer1 = new JPanel();
    double minYScale = 0;

    TreeWindow frame = null;

    public VerticalTreeToolbar(TreeWindow _frame) {

        super(JToolBar.VERTICAL);
        this.setLayout(new BorderLayout());
/*        holder.setLayout(new BoxLayout(holder, BoxLayout.Y_AXIS));*/
        holder.setLayout(new BorderLayout());
        
        frame = _frame;
        zoomOutButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if(frame.zoomLocked)
                {
                    frame.zoomOut();
                    return;
                }
                zoomSlider.setValue(zoomSlider.getValue() - 1);
                syncTreeWithZoomSlider();
            }

        });
        zoomInButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                if(frame.zoomLocked)
                {
                    frame.zoomIn();
                    return;
                }
                zoomSlider.setValue(zoomSlider.getValue() + 1);
                syncTreeWithZoomSlider();
            }

        });
        holder.add(zoomInButton, BorderLayout.NORTH);
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setPreferredSize(new Dimension(20,200));
        zoomSlider.setMaximumSize(new Dimension(20,200));
        zoomSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (zoomSlider.getValueIsAdjusting()){
                    if(frame.zoomLocked)
                    {
                        frame.treeToolbar.setValue(zoomSlider.getValue());
                        frame.treeToolbar.syncTreeWithZoomSlider();
                    }
                    syncTreeWithZoomSlider();
                }
            }
        });

        holder.add(zoomSlider, BorderLayout.CENTER);
        holder.add(zoomOutButton, BorderLayout.SOUTH);
        add(holder, BorderLayout.SOUTH);
        setFloatable(false);
    }
    
    public void sliderEnabled(boolean b) {
        zoomSlider.setEnabled(b);
    }

    public void setValue(int i) {
        zoomSlider.setValue(i);
        syncTreeWithZoomSlider();
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
        frame.tree.redraw();
    }

    public void syncZoomSliderWithTree() {
        if (frame.tree.getTree() == null) return;
        zoomSlider.setValue((int)(frame.tree.getYScale()/minYScale));
    }

}
