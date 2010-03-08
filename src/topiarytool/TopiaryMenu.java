package topiarytool;

/*
Topiary Explorer - tree viewer/data explorer for phylogenetic trees and associated data
Copyright (C) 2009  University of Colorado

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;


/**
 * TopiaryMenu is the main menu bar for TopiaryTool
 */
public class TopiaryMenu extends JMenuBar implements ActionListener{

    MainFrame frame = null;
    JMenu fileMenu = new JMenu("File");
    JMenu viewMenu = new JMenu("View");
    JMenu treeMenu = new JMenu("Tree");
    JMenu nodeMenu = new JMenu("Node");
    JMenu pcoaMenu = new JMenu("PCoA");
    JMenu colorByMenu = new JMenu("Color");
    JMenu colorByOtuMetadataMenu = new JMenu("OTU Metadata");
    JMenu colorBySampleMetadataMenu = new JMenu("Sample Metadata");
    JMenu distanceMetricMenu = new JMenu("Distance Metric");
    JMenu lineWidthMenu = new JMenu("Line Width");
    JMenu sampleShapeMenu = new JMenu("Sample Shape");
    JMenu otuShapeMenu = new JMenu("OTU Shape");
    JMenu lineWidthOtuMetadataMenu = new JMenu("OTU Metadata");
    JMenu lineWidthSampleMetadataMenu = new JMenu("Sample Metadata");
    JRadioButtonMenuItem uniformLineWidthItem = new JRadioButtonMenuItem("Uniform");
    JMenu pcoaLayoutMenu = new JMenu("Layout");
    JMenu collapseByMenu = new JMenu("Collapse by");
    JRadioButtonMenuItem noColoringMenuItem = new JRadioButtonMenuItem("No coloring");
    JSlider lineWidthSlider = new JSlider(1, 1000, 20);
    JSlider pcoaLineWidthSlider = new JSlider(1, 1000, 10);

    ButtonGroup distanceMetricGroup = new ButtonGroup();
    ButtonGroup colorByGroup = new ButtonGroup();
    ButtonGroup lineWidthGroup = new ButtonGroup();
    ButtonGroup pcoaLayoutGroup = new ButtonGroup();
    ButtonGroup treeLayoutGroup = new ButtonGroup();
    ButtonGroup sampleShapeGroup = new ButtonGroup();
    ButtonGroup otuShapeGroup = new ButtonGroup();

    JCheckBoxMenuItem samplesMenuItem = new JCheckBoxMenuItem("Samples");
    JCheckBoxMenuItem otusMenuItem = new JCheckBoxMenuItem("OTUs");
    JCheckBoxMenuItem connectionsMenuItem = new JCheckBoxMenuItem("Connections");
    JCheckBoxMenuItem axesMenuItem = new JCheckBoxMenuItem("Axes");
    JCheckBoxMenuItem axisLabelsMenuItem = new JCheckBoxMenuItem("Axis Labels");
    JCheckBoxMenuItem sampleLabelsMenuItem = new JCheckBoxMenuItem("Sample Labels");
    JCheckBoxMenuItem otuLabelsMenuItem = new JCheckBoxMenuItem("OTU Labels");
    
    JCheckBoxMenuItem colorSamplesMenuItem = new JCheckBoxMenuItem("Color Samples");
    JCheckBoxMenuItem colorOtusMenuItem = new JCheckBoxMenuItem("Color OTUs");

    JCheckBoxMenuItem externalLabelsMenuItem = new JCheckBoxMenuItem("Tip Labels...");
    JCheckBoxMenuItem internalLabelsMenuItem = new JCheckBoxMenuItem("Internal Node Labels");

    /**
     * Constructor.  Sets up the menu.
     */
     public TopiaryMenu(MainFrame _frame) {
         frame  = _frame;
        //initialize the menus
        
        //set up the "file" submenus
        JMenuItem item;
        item = new JMenuItem("Load Tree...");
        item.addActionListener(this);
        fileMenu.add(item);
        item = new JMenuItem("Load OTU Metadata...");
        item.addActionListener(this);
        fileMenu.add(item);
        item = new JMenuItem("Load OTU-Sample Map...");
        item.addActionListener(this);
        fileMenu.add(item);
        item = new JMenuItem("Load Sample Metadata...");
        item.addActionListener(this);
        fileMenu.add(item);
        fileMenu.add(new JSeparator());
        item = new JMenuItem("Save Tree...");
        item.addActionListener(this);
        //fileMenu.add(item);
        //item = new JMenuItem("Save Metadata...");
        //item.addActionListener(this);
        fileMenu.add(item);
        fileMenu.add(new JSeparator());
        item = new JMenuItem("Export Tree Image...");
        item.addActionListener(this);
        fileMenu.add(item);
        item = new JMenuItem("Export Tree Screen Capture...");
        item.addActionListener(this);
        fileMenu.add(item);
        fileMenu.add(new JSeparator());
        item = new JMenuItem("Quit");
        item.addActionListener(this);
        fileMenu.add(item);
        
        //set up the view menu
        JCheckBoxMenuItem checkbox = new JCheckBoxMenuItem("Tree Window");
        checkbox.addActionListener(this);
        viewMenu.add(checkbox);
        checkbox = new JCheckBoxMenuItem("PCoA Window");
        checkbox.addActionListener(this);
        viewMenu.add(checkbox);
        
        //set up the "tree" submenus
        item = new JMenuItem("Beautify");
        item.addActionListener(this);
        treeMenu.add(item);
        item = new JMenuItem("Recenter");
        item.addActionListener(this);
        treeMenu.add(item);
        item = new JMenuItem("Mirror left/right");
        item.addActionListener(this);
        treeMenu.add(item);
        item = new JMenuItem("Mirror up/down");
        item.addActionListener(this);
        treeMenu.add(item);
        this.resetCollapseByMenu();
        treeMenu.add(collapseByMenu);
        JMenu sortBy = new JMenu("Sort by");
        item = new JMenuItem("Number of OTUs");
        item.addActionListener(this);
        sortBy.add(item);
        item = new JMenuItem("Number of immediate children");
        item.addActionListener(this);
        sortBy.add(item);
        treeMenu.add(sortBy);
        
        
        JMenu layout = new JMenu("Layout");
        JRadioButtonMenuItem radiobutton;
        radiobutton = new JRadioButtonMenuItem("Rectangular");
        radiobutton.setSelected(true);
        radiobutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.treeWindow.tree.setTreeLayout("Rectangular");
            }
        });
        treeLayoutGroup.add(radiobutton);
        layout.add(radiobutton);
        
        radiobutton = new JRadioButtonMenuItem("Triangular");
        radiobutton.setSelected(true);
        radiobutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.treeWindow.tree.setTreeLayout("Triangular");
            }
        });
        treeLayoutGroup.add(radiobutton);
        layout.add(radiobutton);
        
        
        radiobutton = new JRadioButtonMenuItem("Radial");
        radiobutton.setSelected(true);
        radiobutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.treeWindow.tree.setTreeLayout("Radial");
            }
        });
        treeLayoutGroup.add(radiobutton);
        layout.add(radiobutton);
        
        radiobutton = new JRadioButtonMenuItem("Polar");
        radiobutton.setSelected(true);
        radiobutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.treeWindow.tree.setTreeLayout("Polar");
            }
        });
        treeLayoutGroup.add(radiobutton);
        layout.add(radiobutton);
        
        
        treeMenu.add(layout);  
        
        lineWidthMenu.add(lineWidthOtuMetadataMenu);
        lineWidthMenu.add(lineWidthSampleMetadataMenu);
        lineWidthGroup.add(uniformLineWidthItem);
        uniformLineWidthItem.setSelected(true);
        uniformLineWidthItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (Node n : frame.treeWindow.tree.getTree().getNodes()) {
                    n.setLineWidth(1);
                }
                syncTreeWithLineWidthSlider();
            }
        });
        lineWidthMenu.add(uniformLineWidthItem);
        lineWidthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (lineWidthSlider.getValueIsAdjusting()){
                    syncTreeWithLineWidthSlider();
                }
            }
        });
        lineWidthMenu.add(lineWidthSlider);
        treeMenu.add(lineWidthMenu);
        
        
        item = new JMenuItem("Background Color...");        
        item.addActionListener(new ActionListener() {        
            public void actionPerformed(ActionEvent e) {
                JColorChooser colorChooser = new JColorChooser();
                Color c = colorChooser.showDialog(frame, "Pick a Color", frame.treeWindow.tree.getBackgroundColor());
                frame.treeWindow.tree.setBackgroundColor(c);
            }
        });
        treeMenu.add(item);
   
        
        externalLabelsMenuItem.setSelected(false);
        externalLabelsMenuItem.addActionListener(this);
        internalLabelsMenuItem.setSelected(false);
        internalLabelsMenuItem.addActionListener(this);
        treeMenu.add(externalLabelsMenuItem);
        treeMenu.add(internalLabelsMenuItem);

        //set up the "pcoa" submenus"
        JRadioButtonMenuItem button;
        button = new JRadioButtonMenuItem("Bray-Curtis");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
        button.setSelected(true);
		button = new JRadioButtonMenuItem("Canberra");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Chi-squared");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Chord");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Euclidean");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Gower");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Hellinger");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Kulczynski");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Manhattan");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Morisita-Horn");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Pearson");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Soergel");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Spearman-Approx");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Species-Profile");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Binary-Chi-Squared");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Binary-Chord");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Binary-Euclidean");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Binary-Hamming");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Binary-Jaccard");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Binary-Lennon");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Binary-Ochiai");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Binary-Pearson");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Binary-Sorensen-Dice");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
		button = new JRadioButtonMenuItem("Load from file");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
        pcoaMenu.add(distanceMetricMenu);

        button = new JRadioButtonMenuItem("None");
        button.setSelected(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setDyamicLayout("None");
            }
        });
        pcoaLayoutGroup.add(button);
        pcoaLayoutMenu.add(button);
        button = new JRadioButtonMenuItem("Spring");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setDyamicLayout("Spring");
            }
        });
        pcoaLayoutGroup.add(button);
        pcoaLayoutMenu.add(button);
        button = new JRadioButtonMenuItem("Force");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setDyamicLayout("Force");
            }
        });
        pcoaLayoutGroup.add(button);
        pcoaLayoutMenu.add(button);
        pcoaMenu.add(pcoaLayoutMenu);
        
        
        pcoaLineWidthSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (pcoaLineWidthSlider.getValueIsAdjusting()){
                    syncPcoaWithLineWidthSlider();
                }
            }
        });
        JMenu pcoaLineWidthMenu = new JMenu("Line width");
        pcoaLineWidthMenu.add(pcoaLineWidthSlider);
        pcoaMenu.add(pcoaLineWidthMenu);
        
        item = new JMenuItem("Background Color...");        
        item.addActionListener(new ActionListener() {        
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.setBackgroundColor();
                }
        });
        pcoaMenu.add(item);
        
        item = new JMenuItem("Set axes...");        
        item.addActionListener(new ActionListener() {        
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.setAxes();
            }
        });
        pcoaMenu.add(item);
        
        pcoaMenu.add(new JSeparator());
        
        button = new JRadioButtonMenuItem("Cube");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setSampleShape("Cube");
            }
        });
        sampleShapeGroup.add(button);
        sampleShapeMenu.add(button);
		button = new JRadioButtonMenuItem("Sphere");
	    button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setSampleShape("Sphere");
            }
        });
        button.setSelected(true);
        sampleShapeGroup.add(button);
        sampleShapeMenu.add(button);
        button = new JRadioButtonMenuItem("Tetrahedron");
	    button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setSampleShape("Tetrahedron");
            }
        });
        sampleShapeGroup.add(button);
        sampleShapeMenu.add(button);
        button = new JRadioButtonMenuItem("Octahedron");
	    button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setSampleShape("Octahedron");
            }
        });
        sampleShapeGroup.add(button);
        sampleShapeMenu.add(button);
        pcoaMenu.add(sampleShapeMenu);
        
        button = new JRadioButtonMenuItem("Cube");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setOtuShape("Cube");
            }
        });
        button.setSelected(true);
        otuShapeGroup.add(button);
        otuShapeMenu.add(button);
		button = new JRadioButtonMenuItem("Sphere");
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setOtuShape("Sphere");
            }
        });
        otuShapeGroup.add(button);
        otuShapeMenu.add(button);
        button = new JRadioButtonMenuItem("Tetrahedron");
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setOtuShape("Tetrahedron");
            }
        });
        otuShapeGroup.add(button);
        otuShapeMenu.add(button);
        button = new JRadioButtonMenuItem("Octahedron");
		button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setOtuShape("Octahedron");
            }
        });
        otuShapeGroup.add(button);
        otuShapeMenu.add(button);
        pcoaMenu.add(otuShapeMenu);
        
        
        pcoaMenu.add(new JSeparator());
        
        samplesMenuItem.setSelected(true);
        samplesMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setDisplaySamples(samplesMenuItem.getState());
            }
        });
        pcoaMenu.add(samplesMenuItem);
        sampleLabelsMenuItem.setSelected(false);
        sampleLabelsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setDisplaySampleIDs(sampleLabelsMenuItem.getState());
            }
        });
        pcoaMenu.add(sampleLabelsMenuItem);
        otusMenuItem.setSelected(true);
        otusMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setDisplayOtus(otusMenuItem.getState());
            }
        });
        pcoaMenu.add(otusMenuItem);
        otuLabelsMenuItem.setSelected(false);
        otuLabelsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setDisplayOtuIDs(otuLabelsMenuItem.getState());
            }
        });
        pcoaMenu.add(otuLabelsMenuItem);
        connectionsMenuItem.setSelected(true);
        connectionsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setDisplayConnections(connectionsMenuItem.getState());
            }
        });
        pcoaMenu.add(connectionsMenuItem);
        axesMenuItem.setSelected(true);
        axesMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setDisplayAxes(axesMenuItem.getState());
            }
        });
        pcoaMenu.add(axesMenuItem);
        axisLabelsMenuItem.setSelected(true);
        axisLabelsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setDisplayAxisLabels(axisLabelsMenuItem.getState());
            }
        });
        pcoaMenu.add(axisLabelsMenuItem);        
        
        pcoaMenu.add(new JSeparator());
        
        colorSamplesMenuItem.setSelected(true);
        colorSamplesMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setColorSamples(colorSamplesMenuItem.getState());
            }
        });
        pcoaMenu.add(colorSamplesMenuItem); 
        
        colorOtusMenuItem.setSelected(true);
        colorOtusMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoaWindow.pcoa.setColorOtus(colorOtusMenuItem.getState());
            }
        });
        pcoaMenu.add(colorOtusMenuItem); 
        
        pcoaMenu.add(new JSeparator());
        
        item = new JMenuItem("Run PCoA Analysis...");
        item.addActionListener(this);
        pcoaMenu.add(item);

        //set up the "node" submenus
        item = new JMenuItem("Collapse/Expand");
        item.addActionListener(this);
        nodeMenu.add(item);
        item = new JMenuItem("Collapse/Expand All Children");
        item.addActionListener(this);
        nodeMenu.add(item);
        item = new JMenuItem("Rotate (Swap Children)");
        item.addActionListener(this);
        nodeMenu.add(item);
        item = new JMenuItem("Toggle Pie Chart");
        item.addActionListener(this);
        nodeMenu.add(item);

        //set up the "color by" submenus
        colorByMenu.add(colorByOtuMetadataMenu);
        colorByMenu.add(colorBySampleMetadataMenu);
        colorByGroup.add(noColoringMenuItem);
        noColoringMenuItem.setSelected(true);
        noColoringMenuItem.addActionListener(this);
        colorByMenu.add(noColoringMenuItem);
        
        //add all the menus to the menu bar
        treeMenu.setEnabled(false);
        nodeMenu.setEnabled(false);
        pcoaMenu.setEnabled(false);
        colorByMenu.setEnabled(false);
        add(fileMenu);
        add(viewMenu);
        add(treeMenu);
        add(nodeMenu);
        add(pcoaMenu);
        add(colorByMenu);
     }


     public void actionPerformed(ActionEvent e) {

         if (e.getActionCommand().equals("Load Tree...")) {
            frame.treeWindow.loadTree();
         } else if (e.getActionCommand().equals("Load OTU Metadata...")) {
           loadOtuMetadata();
         } else if (e.getActionCommand().equals("Load Sample Metadata...")) {
           loadSampleMetadata();
         } else if (e.getActionCommand().equals("Load OTU-Sample Map...")) {
             loadOtuSampleMap();
         } else if (e.getActionCommand().equals("Save Tree...")) {
             frame.treeWindow.saveTree();
         } else if (e.getActionCommand().equals("Tree Window")) {
                  frame.treeWindow.setVisible(!frame.treeWindow.isVisible());
          } else if (e.getActionCommand().equals("PCoA Window")) {
                  frame.pcoaWindow.setVisible(!frame.pcoaWindow.isVisible());
        } else if (e.getActionCommand().equals("No coloring")) {
             frame.treeWindow.removeColor();
         } else if (e.getActionCommand().equals("Tip Labels...")) {
/*             frame.tree.setDrawExternalNodeLabels(externalLabelsMenuItem.getState());*/
             frame.treeWindow.setTipLabels(externalLabelsMenuItem.getState());
         } else if (e.getActionCommand().equals("Internal Node Labels")) {
             frame.treeWindow.tree.setDrawInternalNodeLabels(internalLabelsMenuItem.getState());
         } else if (e.getActionCommand().equals("Collapse/Expand")) {
             if (frame.clickedNode != null) {
                frame.clickedNode.setCollapsed(!frame.clickedNode.isCollapsed());
             }
         }
         else if (e.getActionCommand().equals("Beautify")) {
              frame.treeWindow.tree.getTree().sortByBranchLength();
              frame.treeWindow.tree.setYOffsets(frame.treeWindow.tree.getTree(), 0);
              frame.treeWindow.tree.setTOffsets(frame.treeWindow.tree.getTree(), 0);
              frame.treeWindow.tree.setROffsets(frame.treeWindow.tree.getTree(), 0);
              frame.treeWindow.tree.setRadialOffsets(frame.treeWindow.tree.getTree());
          }
          else if (e.getActionCommand().equals("Collapse/Expand All Children")) {
              if (frame.clickedNode != null) {
                 ArrayList<Node> children = frame.clickedNode.getNodes();
                 for(Node n: children)
                     {n.setCollapsed(!n.isCollapsed());}
                 frame.clickedNode.setCollapsed(!frame.clickedNode.isCollapsed());
              }
         } else if (e.getActionCommand().equals("Rotate (Swap Children)")) {
             if (frame.clickedNode != null) {
                frame.clickedNode.rotate();
                frame.treeWindow.tree.setYOffsets(frame.treeWindow.tree.getTree(), 0);
             }
         } else if (e.getActionCommand().equals("Toggle Pie Chart")) {
             if (frame.clickedNode != null) {
                frame.clickedNode.setDrawPie(!frame.clickedNode.getDrawPie());
             }
         } else if (e.getActionCommand().equals("Recenter")) {
             frame.treeWindow.recenter();
         } else if (e.getActionCommand().equals("Mirror left/right")) {
            frame.treeWindow.mirrorHorz();
         } else if (e.getActionCommand().equals("Mirror up/down")) {
             frame.treeWindow.mirrorVert();
         } else if (e.getActionCommand().equals("Number of OTUs")) {
             frame.treeWindow.tree.getTree().sortByNumberOfOtus();
             frame.treeWindow.tree.setYOffsets(frame.treeWindow.tree.getTree(), 0);
             frame.treeWindow.tree.setTOffsets(frame.treeWindow.tree.getTree(), 0);
             frame.treeWindow.tree.setROffsets(frame.treeWindow.tree.getTree(), 0);
             frame.treeWindow.tree.setRadialOffsets(frame.treeWindow.tree.getTree());
         } else if (e.getActionCommand().equals("Number of immediate children")) {
             frame.treeWindow.tree.getTree().sortByNumberOfChildren();
             frame.treeWindow.tree.setYOffsets(frame.treeWindow.tree.getTree(), 0);
             frame.treeWindow.tree.setTOffsets(frame.treeWindow.tree.getTree(), 0);
             frame.treeWindow.tree.setROffsets(frame.treeWindow.tree.getTree(), 0);
             frame.treeWindow.tree.setRadialOffsets(frame.treeWindow.tree.getTree());
         } else if (e.getActionCommand().equals("Export Tree Image...") && frame.treeWindow.tree.getTree()!= null) {
            frame.treeWindow.exportTreeImage();
         } else if (e.getActionCommand().equals("Export Tree Screen Capture...") && frame.treeWindow.tree.getTree()!= null)  {
             frame.treeWindow.tree.noLoop();
             frame.loadDataFileChooser.setDialogTitle("Save As...");
             int returnVal = frame.loadDataFileChooser.showSaveDialog(null);
			 if (returnVal == JFileChooser.APPROVE_OPTION) {
				String selectedFile = frame.loadDataFileChooser.getSelectedFile().getAbsolutePath();
				frame.treeWindow.tree.exportScreenCapture(selectedFile);
			 }
             frame.treeWindow.tree.loop();
         } else if (e.getActionCommand().equals("Quit")) {
             frame.db_conn.c.close_connection();
             System.exit(0);
         } else if(e.getActionCommand().equals("Run PCoA Analysis...")) {
             frame.pcoaWindow.runPcoaAnalysis();
         }
    }
    
    /**
    * Loads OTU metadata from a file selected by the user.
    * Populates otu metadata table with information from the file.
    */
   public void loadOtuMetadata() {
       frame.loadDataFileChooser.setDialogTitle("Load OTU Metadata");
       int returnVal = frame.loadDataFileChooser.showOpenDialog(null);
       if (returnVal == JFileChooser.APPROVE_OPTION) {
            frame.treeWindow.tree.noLoop();
            //set view
            frame.dataPane.setSelectedIndex(1);
            File selectedFile = frame.loadDataFileChooser.getSelectedFile();
            try {
                FileInputStream is = new FileInputStream(selectedFile);
                frame.otuMetadata = new DataTable(is);
                frame.otuMetadataTable.setModel(new SparseTableModel(frame.otuMetadata.getData(),
                    frame.otuMetadata.getColumnNames()){
                    //make it so the user can't edit the cells manually
                    public boolean isCellEditable(int rowIndex, int colIndex) {
                        return false;
                    }
                });
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Unable to load " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            if (frame.currTable == frame.otuMetadata) {
                frame.treeWindow.removeColor();
            }
            resetColorByOtuMenu();
            resetLineWidthOtuMenu();
            resetCollapseByMenu();
            resetTipLabels();
            frame.treeWindow.tree.loop();
            frame.treeWindow.resetTipLabelCustomizer(externalLabelsMenuItem.getState());
       }
   }

   /**
   * Loads sample metadata from a file selected by the user.
   * Populates sample metadata table with information from the file.
   */
   public void loadSampleMetadata() {
       frame.loadDataFileChooser.setDialogTitle("Load Sample Metadata");
       int returnVal = frame.loadDataFileChooser.showOpenDialog(null);
       if (returnVal == JFileChooser.APPROVE_OPTION) {
            frame.treeWindow.tree.noLoop();
            //set view
            frame.dataPane.setSelectedIndex(3);
            File selectedFile = frame.loadDataFileChooser.getSelectedFile();
            try {
                FileInputStream is = new FileInputStream(selectedFile);
                frame.sampleMetadata = new DataTable(is);
                frame.sampleMetadataTable.setModel(new SparseTableModel(frame.sampleMetadata.getData(),
                    frame.sampleMetadata.getColumnNames()){
                    //make it so the user can't edit the cells manually
                    public boolean isCellEditable(int rowIndex, int colIndex) {
                        return false;
                    }
                });
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Unable to load " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            if (frame.currTable == frame.sampleMetadata) {
                frame.treeWindow.removeColor();
            }
            resetColorBySampleMenu();
            resetLineWidthSampleMenu();
            frame.treeWindow.tree.loop();
            frame.treeWindow.resetTipLabelCustomizer(externalLabelsMenuItem.getState());
       }
   }

   /**
   * Loads OTU-sample map from a file selected by the user.
   * Populates otu-sample map table with information from the file.
   */
   public void loadOtuSampleMap() {
       frame.loadDataFileChooser.setDialogTitle("Load OTU-Sample Map");
       int returnVal = frame.loadDataFileChooser.showOpenDialog(null);
       if (returnVal == JFileChooser.APPROVE_OPTION) {
            frame.treeWindow.tree.noLoop();
            //set view
            frame.dataPane.setSelectedIndex(2);
            File selectedFile = frame.loadDataFileChooser.getSelectedFile();
            try {
                FileInputStream is = new FileInputStream(selectedFile);
                frame.otuSampleMap = new DataTable(is);
                frame.otuSampleMapTable.setModel(new SparseTableModel(frame.otuSampleMap.getData(),
                    frame.otuSampleMap.getColumnNames()){
                    //make it so the user can't edit the cells manually
                    public boolean isCellEditable(int rowIndex, int colIndex) {
                        return false;
                    }
                });
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Unable to load " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            frame.treeWindow.tree.loop();
       }
   }
   
   /**
   * 
   */
    public void syncTreeWithLineWidthSlider() {
        if (frame.treeWindow.tree.getTree() == null) return;
        double value = lineWidthSlider.getValue();
        value = value/20.0;
        frame.treeWindow.tree.setLineWidthScale(value);
        frame.treeWindow.tree.redraw();
    }
    
    /**
    * 
    */
    public void syncPcoaWithLineWidthSlider() {
        double value = pcoaLineWidthSlider.getValue();
        value = value/10.0;
        frame.pcoaWindow.pcoa.setLineWidthScale((float)value);
    }

    /**
    * Resets tip labels on the tree
    */
    public void resetTipLabels() {
/*        frame.tlc = new TipLabelCustomizer(frame);
        frame.tlc.setVisible(false);
        for(Node n : frame.tree.getTree().getNodes())
            n.setLabel(n.getName());*/
        frame.treeWindow.tree.redraw();
    }

    /**
    * Resets collapse by menu when new OTU or Sample metadata
    * files are loaded
    */
   public void resetCollapseByMenu() {
       //NOTE: can only collapse on OTU metadata
       collapseByMenu.removeAll();
       /*if (frame.otuMetadata != null) {
                  ArrayList<String> data = frame.otuMetadata.getColumnNames();
                  //start at 1 to skip ID column
                  for (int i = 1; i < data.size(); i++) {
                       String value = data.get(i);
                       JMenuItem item = new JMenuItem(value);
                       item.addActionListener(new ActionListener() {
                           public void actionPerformed(ActionEvent e) {
                               //get the category to color by
                               String value = e.getActionCommand();
                               frame.collapseByValue(value);
                           }
                       });
                       collapseByMenu.add(item);
                  }
              }*/
       collapseByMenu.add(new JSeparator());
       JMenuItem item = new JMenuItem("Uncollapse All");
       item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.treeWindow.uncollapseTree();
            }
       });
       collapseByMenu.add(item);
       item = new JMenuItem("Collapse All");
       item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.treeWindow.collapseTree();
            }
       });
       collapseByMenu.add(item);
       item = new JMenuItem("Internal Node Labels");
       item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.treeWindow.collapseTreeByInternalNodeLabels();
            }
       });
       collapseByMenu.add(item);
   }

   /**
   * Resets colorby OTU menu when a new otu table is loaded
   */
   public void resetColorByOtuMenu() {
       noColoringMenuItem.setSelected(true);
       colorByOtuMetadataMenu.removeAll();
       ArrayList<String> data = frame.otuMetadata.getColumnNames();
       //start at 1 to skip ID column
       for (int i = 1; i < data.size(); i++) {
            String value = data.get(i);
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(value);
            colorByGroup.add(item);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //get the category to color by
                    String value = e.getActionCommand();
                    System.out.println("*");
                    frame.currTable = frame.otuMetadata;
                    frame.treeWindow.colorByValue(value);
                }
            });
            colorByOtuMetadataMenu.add(item);
       }
   }


   /**
   * Resets colorby Sample menu when a new sample metadata table is loaded
   */
   public void resetColorBySampleMenu() {
       noColoringMenuItem.setSelected(true);
       colorBySampleMetadataMenu.removeAll();
       ArrayList<String> data = frame.sampleMetadata.getColumnNames();
       //start at 1 to skip ID column
       for (int i = 1; i < data.size(); i++) {
            String value = data.get(i);
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(value);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //get the category to color by
                    String value = e.getActionCommand();
                    frame.currTable = frame.sampleMetadata;
                    frame.treeWindow.colorByValue(value);
                }
            });
            colorByGroup.add(item);
            colorBySampleMetadataMenu.add(item);
       }
   }
   
   
      public void resetLineWidthOtuMenu() {
       uniformLineWidthItem.setSelected(true);
       lineWidthOtuMetadataMenu.removeAll();
       ArrayList<String> data = frame.otuMetadata.getColumnNames();
       //start at 1 to skip ID column
       for (int i = 1; i < data.size(); i++) {
            String value = data.get(i);
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(value);
            lineWidthGroup.add(item);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //get the category to color by
                    String value = e.getActionCommand();
                    System.out.println("*");
                    frame.currTable = frame.otuMetadata;
                    frame.treeWindow.setLineWidthByValue(value);
                }
            });
            lineWidthOtuMetadataMenu.add(item);
       }
   }

   public void resetLineWidthSampleMenu() {
       uniformLineWidthItem.setSelected(true);
       lineWidthSampleMetadataMenu.removeAll();
       ArrayList<String> data = frame.sampleMetadata.getColumnNames();
       //start at 1 to skip ID column
       for (int i = 1; i < data.size(); i++) {
            String value = data.get(i);
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(value);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //get the category to color by
                    String value = e.getActionCommand();
                    frame.currTable = frame.sampleMetadata;
                    frame.treeWindow.setLineWidthByValue(value);
                }
            });
            lineWidthGroup.add(item);
            lineWidthSampleMetadataMenu.add(item);
       }
   }

}