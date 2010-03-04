package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class PcoaToolbar extends JToolBar {

    JButton zoomOutButton  = new JButton("-");
    JButton zoomInButton = new JButton("+");
    JSlider zoomSlider = new JSlider(-8, 8, 0);
    JPanel spacer1 = new JPanel();
    
    PcoaWindow frame = null;

    public PcoaToolbar(PcoaWindow _frame) {

        frame = _frame;

        zoomOutButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                zoomSlider.setValue(zoomSlider.getValue() - 1);
                syncPcoaWithZoomSlider();
            }

        });
        zoomInButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                zoomSlider.setValue(zoomSlider.getValue() + 1);
                syncPcoaWithZoomSlider();
            }

        });
        add(spacer1);
        add(zoomOutButton);
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setPreferredSize(new Dimension(200,28));
        zoomSlider.setMaximumSize(new Dimension(200,28));
        zoomSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (zoomSlider.getValueIsAdjusting()){
                    syncPcoaWithZoomSlider();
                }
            }
        });
        
        add(zoomSlider);
        add(zoomInButton);
        
        spacer1.setPreferredSize(new Dimension(245,10));
        add(spacer1);

        setFloatable(false);
    }

    public void syncPcoaWithZoomSlider() {
        double minScale = 1;      
        double newScale = minScale * Math.pow(Math.pow(2.0, 0.5), zoomSlider.getValue());
        frame.pcoa.setScale((float)newScale);
    }

}
