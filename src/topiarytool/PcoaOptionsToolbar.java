package topiarytool;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;


public class PcoaOptionsToolbar extends JToolBar implements ItemListener{

    JLabel distMet = new JLabel("Distance Metric:");
    Choice distanceMetrics = new Choice();
    JLabel layoutLabel = new JLabel("Layout:");
    Choice layout = new Choice();
    JLabel sampleShapeLabel = new JLabel("Sample Shape:");
    Choice sampleShape = new Choice();
    JLabel otuShapeLabel = new JLabel("OTU Shape:");
    Choice otuShape = new Choice();
    JButton runButton  = new JButton("Run");
    JLabel pcoaStatus = new JLabel("");
    int distMetIndex = 0;
    int layoutIndex = 0;
    int sampleShapeIndex = 1;
    int otuShapeIndex = 0;

    PcoaWindow frame = null;

    public PcoaOptionsToolbar(PcoaWindow _frame) {
        super(JToolBar.HORIZONTAL);
        frame = _frame;
        
        distanceMetrics.add("Bray-Curtis");
        distanceMetrics.add("Canberra");
        distanceMetrics.add("Chi-squared");
        distanceMetrics.add("Chord");
        distanceMetrics.add("Euclidean");
        distanceMetrics.add("Gower");
        distanceMetrics.add("Hellinger");
        distanceMetrics.add("Kulczynski");
        distanceMetrics.add("Manhattan");
        distanceMetrics.add("Morisita-Horn");
        distanceMetrics.add("Pearson");
        distanceMetrics.add("Soergel");
        distanceMetrics.add("Spearman-Approx");
        distanceMetrics.add("Species-Profile");
        distanceMetrics.add("Binary-Chi-Squared");
        distanceMetrics.add("Binary-Chord");
        distanceMetrics.add("Binary-Euclidean");
        distanceMetrics.add("Binary-Hamming");
        distanceMetrics.add("Binary-Jaccard");
        distanceMetrics.add("Binary-Lennon");
        distanceMetrics.add("Binary-Ochiai");
        distanceMetrics.add("Binary-Pearson");
        distanceMetrics.add("Binary-Sorensen-Dice");
        distanceMetrics.add("Load from file");
        
        distanceMetrics.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(distMetIndex != distanceMetrics.getSelectedIndex())
                {
                    distMetIndex = distanceMetrics.getSelectedIndex();
                    JOptionPane.showMessageDialog(null, "Please re-run the analysis to view the new metric");
                }
            }
        });
        
        add(distanceMetrics);
        
        /*add(layoutLabel);
                layout.add("None");
                layout.add("Spring");
                layout.add("Force");
                layout.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        if(layoutIndex != layout.getSelectedIndex())
                        {
                            frame.pcoa.setDyamicLayout(layout.getSelectedItem());
                            layoutIndex = layout.getSelectedIndex();
                        }
                    }
                });
                add(layout);*/
        addSeparator();
        
        add(sampleShapeLabel);
        sampleShape.add("Cube");
        sampleShape.add("Sphere");
        sampleShape.add("Tetrahedron");
        sampleShape.add("Octahedron");
        sampleShape.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(sampleShapeIndex != sampleShape.getSelectedIndex())
                {
                    frame.pcoa.setSampleShape(sampleShape.getSelectedItem());
                    sampleShapeIndex = sampleShape.getSelectedIndex();
                }
            }
        });
        sampleShape.select(1);
        add(sampleShape);
        
        add(otuShapeLabel);
        otuShape.add("Cube");
        otuShape.add("Sphere");
        otuShape.add("Tetrahedron");
        otuShape.add("Octahedron");
        otuShape.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(otuShapeIndex != otuShape.getSelectedIndex())
                {
                    frame.pcoa.setOtuShape(otuShape.getSelectedItem());
                    otuShapeIndex = otuShape.getSelectedIndex();
                }
            }
        });
        add(otuShape);

        addSeparator();
        runButton.setIcon(new ImageIcon("images/runpcoa.gif"));
        runButton.setToolTipText("Run PCoA Analysis");
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.runPcoaAnalysis();
            }
        });
        add(runButton);
        add(pcoaStatus);
        
        setFloatable(false);
    }
    
    public void itemStateChanged(ItemEvent e) {
    }
    
    public void setStatus(String s) {
        pcoaStatus.setText(s);
    }

}
