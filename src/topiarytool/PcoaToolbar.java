package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class PcoaToolbar extends JToolBar {

    JButton zoomOutButton  = new JButton("-");
    JButton zoomInButton = new JButton("+");
    JSlider zoomSlider = new JSlider(0, 8, 0);
    JButton lineWidthPlusButton = new JButton("+");
    JButton lineWidthMinusButton = new JButton("-");
    JSlider lineWidthSlider = new JSlider(1, 1000, 20);
    JTextField search = new JTextField();
    JPanel spacer1 = new JPanel();
    JPanel spacer2 = new JPanel();
    
    MainFrame frame = null;

    public PcoaToolbar(MainFrame _frame) {

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
        spacer2.setPreferredSize(new Dimension(100,10));
        add(spacer2);
        
        add(new JLabel("Line Width:"));
        lineWidthMinusButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                lineWidthSlider.setValue(lineWidthSlider.getValue() - 10);
                syncPcoaWithLineWidthSlider();
            }

        });
        lineWidthPlusButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                lineWidthSlider.setValue(lineWidthSlider.getValue() + 10);
                syncPcoaWithLineWidthSlider();
            }

        });
        add(lineWidthMinusButton);
        lineWidthSlider.setSnapToTicks(true);
        lineWidthSlider.setPreferredSize(new Dimension(200,28));
        lineWidthSlider.setMaximumSize(new Dimension(200,28));
        lineWidthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (lineWidthSlider.getValueIsAdjusting()){
                    syncPcoaWithLineWidthSlider();
                }
            }
        });
        add(lineWidthSlider);
        add(lineWidthPlusButton);
        spacer1.setPreferredSize(new Dimension(245,10));
        add(spacer1);



        setFloatable(false);
    }

    public void syncPcoaWithZoomSlider() {
        double minScale = 1;      
        double newScale = minScale * Math.pow(Math.pow(2.0, 0.5), zoomSlider.getValue());
        frame.pcoa.setScale((float)newScale);
    }
    
    public void syncPcoaWithLineWidthSlider() {
        double value = lineWidthSlider.getValue();
        value = value/10.0;
        frame.pcoa.setLineWidthScale((float)value);
    }

}
