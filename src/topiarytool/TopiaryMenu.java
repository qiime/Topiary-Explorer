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


/**
 * TopiaryMenu is the main menu bar for TopiaryTool
 */
public class TopiaryMenu extends JMenuBar implements ActionListener{

    MainFrame frame = null;
    JMenu fileMenu = new JMenu("File");
    JMenu editMenu = new JMenu("Edit");
    JMenu treeMenu = new JMenu("Tree");
    JMenu nodeMenu = new JMenu("Node");
    JMenu pcoaMenu = new JMenu("PCoA");
    JMenu colorByMenu = new JMenu("Color");
    JMenu colorByOtuMetadataMenu = new JMenu("OTU Metadata");
    JMenu colorBySampleMetadataMenu = new JMenu("Sample Metadata");
    JMenu distanceMetricMenu = new JMenu("Distance Metric");
    JMenu pcoaLayoutMenu = new JMenu("Layout");
    JMenu collapseByMenu = new JMenu("Collapse by");
    JRadioButtonMenuItem noColoringMenuItem = new JRadioButtonMenuItem("No coloring");

    ButtonGroup distanceMetricGroup = new ButtonGroup();
    ButtonGroup colorByGroup = new ButtonGroup();
    ButtonGroup pcoaLayoutGroup = new ButtonGroup();

    JCheckBoxMenuItem samplesMenuItem = new JCheckBoxMenuItem("Samples");
    JCheckBoxMenuItem otusMenuItem = new JCheckBoxMenuItem("OTUs");
    JCheckBoxMenuItem connectionsMenuItem = new JCheckBoxMenuItem("Connections");
    JCheckBoxMenuItem axesMenuItem = new JCheckBoxMenuItem("Axes");

    JCheckBoxMenuItem externalLabelsMenuItem = new JCheckBoxMenuItem("External Node Labels");

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
        
        //set up the "tree" submenus
        item = new JMenuItem("Recenter");
        item.addActionListener(this);
        treeMenu.add(item);
        collapseByMenu.add(new JSeparator());
        item = new JMenuItem("Uncollapse All");
        collapseByMenu.add(item);
        item = new JMenuItem("Collapse All");
        collapseByMenu.add(item);
        treeMenu.add(collapseByMenu);
        JMenu sortBy = new JMenu("Sort by");
        item = new JMenuItem("Number of OTUs");
        item.addActionListener(this);
        sortBy.add(item);
        item = new JMenuItem("Number of immediate children");
        item.addActionListener(this);
        sortBy.add(item);
        treeMenu.add(sortBy);
        externalLabelsMenuItem.setSelected(false);
        externalLabelsMenuItem.addActionListener(this);
        treeMenu.add(externalLabelsMenuItem);

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
		button = new JRadioButtonMenuItem("Custom...");
        distanceMetricGroup.add(button);
        distanceMetricMenu.add(button);
        pcoaMenu.add(distanceMetricMenu);

        button = new JRadioButtonMenuItem("None");
        button.setSelected(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoa.setDyamicLayout("None");
            }
        });
        pcoaLayoutGroup.add(button);
        pcoaLayoutMenu.add(button);
        button = new JRadioButtonMenuItem("Spring");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoa.setDyamicLayout("Spring");
            }
        });
        pcoaLayoutGroup.add(button);
        pcoaLayoutMenu.add(button);
        button = new JRadioButtonMenuItem("Force");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoa.setDyamicLayout("Force");
            }
        });
        pcoaLayoutGroup.add(button);
        pcoaLayoutMenu.add(button);
        pcoaMenu.add(pcoaLayoutMenu);


        samplesMenuItem.setSelected(true);
        samplesMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoa.setDisplaySamples(samplesMenuItem.getState());
            }
        });
        pcoaMenu.add(samplesMenuItem);
        otusMenuItem.setSelected(true);
        otusMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoa.setDisplayOtus(otusMenuItem.getState());
            }
        });
        pcoaMenu.add(otusMenuItem);
        connectionsMenuItem.setSelected(true);
        connectionsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoa.setDisplayConnections(connectionsMenuItem.getState());
            }
        });
        pcoaMenu.add(connectionsMenuItem);
        axesMenuItem.setSelected(true);
        axesMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.pcoa.setDisplayAxes(axesMenuItem.getState());
            }
        });
        pcoaMenu.add(axesMenuItem);
        item = new JMenuItem("Run PCoA Analysis...");
        item.addActionListener(this);
        pcoaMenu.add(item);




        //set up the "node" submenus
        item = new JMenuItem("Collapse/Expand");
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
        add(fileMenu);
        add(treeMenu);
        add(nodeMenu);
        add(pcoaMenu);
        add(colorByMenu);
     }


     public void actionPerformed(ActionEvent e) {

         if (e.getActionCommand().equals("Load Tree...")) {
            loadTree();
         } else if (e.getActionCommand().equals("Load OTU Metadata...")) {
           loadOtuMetadata();
         } else if (e.getActionCommand().equals("Load Sample Metadata...")) {
           loadSampleMetadata();
         } else if (e.getActionCommand().equals("Load OTU-Sample Map...")) {
             loadOtuSampleMap();
         } else if (e.getActionCommand().equals("No coloring")) {
             frame.removeColor();
         } else if (e.getActionCommand().equals("External Node Labels")) {
             frame.tree.setDrawText(externalLabelsMenuItem.getState());
         } else if (e.getActionCommand().equals("Collapse/Expand")) {
             if (frame.clickedNode != null) {
                frame.clickedNode.setCollapsed(!frame.clickedNode.isCollapsed());
             }
         } else if (e.getActionCommand().equals("Rotate (Swap Children)")) {
             if (frame.clickedNode != null) {
                frame.clickedNode.rotate();
                frame.tree.setYOffsets(frame.tree.getTree(), 0);
             }
         } else if (e.getActionCommand().equals("Toggle Pie Chart")) {
             if (frame.clickedNode != null) {
                frame.clickedNode.setDrawPie(!frame.clickedNode.getDrawPie());
             }
         } else if (e.getActionCommand().equals("Recenter")) {
             frame.tree.resetTreeX();
             frame.tree.resetTreeY();
             frame.treeToolbar.syncZoomSliderWithTree();
         } else if (e.getActionCommand().equals("Number of OTUs")) {
             frame.tree.getTree().sortByNumberOfOtus();
             frame.tree.setYOffsets(frame.tree.getTree(), 0);
         } else if (e.getActionCommand().equals("Number of immediate children")) {
             frame.tree.getTree().sortByNumberOfChildren();
             frame.tree.setYOffsets(frame.tree.getTree(), 0);
         } else if (e.getActionCommand().equals("Export Tree Image...")) {
            frame.tree.noLoop();
            frame.loadDataFileChooser.setDialogTitle("Select PDF File");
            int returnVal = frame.loadDataFileChooser.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String selectedFile = frame.loadDataFileChooser.getSelectedFile().getAbsolutePath();
				frame.tree.exportTreeImage(selectedFile);
			}
            frame.tree.loop();
         } else if (e.getActionCommand().equals("Export Tree Screen Capture..."))  {
             frame.tree.noLoop();
             frame.loadDataFileChooser.setDialogTitle("Select PDF File");
             int returnVal = frame.loadDataFileChooser.showSaveDialog(null);
			 if (returnVal == JFileChooser.APPROVE_OPTION) {
				String selectedFile = frame.loadDataFileChooser.getSelectedFile().getAbsolutePath();
				frame.tree.exportScreenCapture(selectedFile);
			 }
             frame.tree.loop();
         } else if (e.getActionCommand().equals("Quit")) {
             System.exit(0);
         } else if(e.getActionCommand().equals("Run PCoA Analysis...")) {
             runPcoaAnalysis();
         }

    }



   public void loadTree() {
        frame.loadDataFileChooser.setDialogTitle("Load Tree");
        int returnVal = frame.loadDataFileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            //set view
            frame.tabbedPane.setSelectedIndex(1);
            File selectedFile = frame.loadDataFileChooser.getSelectedFile();
            frame.tree.setTree(TopiaryFunctions.createTreeFromNewickFile(selectedFile));
            //make sure coloring is empty
            frame.removeColor();
            frame.treeToolbar.updateZoomBounds();
            frame.treeToolbar.zoomSlider.setValue(0);
        }
   }

   public void loadOtuMetadata() {
       frame.loadDataFileChooser.setDialogTitle("Load OTU Metadata");
       int returnVal = frame.loadDataFileChooser.showOpenDialog(null);
       if (returnVal == JFileChooser.APPROVE_OPTION) {
            //set view
            frame.tabbedPane.setSelectedIndex(0);
            frame.dataPane.setSelectedIndex(0);
            File selectedFile = frame.loadDataFileChooser.getSelectedFile();
            try {
                FileInputStream is = new FileInputStream(selectedFile);
                frame.otuMetadata = new DataTable(is);
                frame.otuMetadataTable.setModel(new DefaultTableModel(frame.otuMetadata.getDataAsArray(),
                    frame.otuMetadata.getColumnNames().toArray()){
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
                frame.removeColor();
            }
            resetColorByOtuMenu();
            resetCollapseByMenu();
       }
   }

   public void loadSampleMetadata() {
       frame.loadDataFileChooser.setDialogTitle("Load OTU Metadata");
       int returnVal = frame.loadDataFileChooser.showOpenDialog(null);
       if (returnVal == JFileChooser.APPROVE_OPTION) {
            //set view
            frame.tabbedPane.setSelectedIndex(0);
            frame.dataPane.setSelectedIndex(2);
            File selectedFile = frame.loadDataFileChooser.getSelectedFile();
            try {
                FileInputStream is = new FileInputStream(selectedFile);
                frame.sampleMetadata = new DataTable(is);
                frame.sampleMetadataTable.setModel(new DefaultTableModel(frame.sampleMetadata.getDataAsArray(),
                    frame.sampleMetadata.getColumnNames().toArray()){
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
                frame.removeColor();
            }
            resetColorBySampleMenu();
       }
   }

   public void loadOtuSampleMap() {
       frame.loadDataFileChooser.setDialogTitle("Load OTU-Sample Map");
       int returnVal = frame.loadDataFileChooser.showOpenDialog(null);
       if (returnVal == JFileChooser.APPROVE_OPTION) {
            //set view
            frame.tabbedPane.setSelectedIndex(0);
            frame.dataPane.setSelectedIndex(1);
            File selectedFile = frame.loadDataFileChooser.getSelectedFile();
            try {
                FileInputStream is = new FileInputStream(selectedFile);
                frame.otuSampleMap = new DataTable(is);
                frame.otuSampleMapTable.setModel(new DefaultTableModel(frame.otuSampleMap.getDataAsArray(),
                    frame.otuSampleMap.getColumnNames().toArray()){
                    //make it so the user can't edit the cells manually
                    public boolean isCellEditable(int rowIndex, int colIndex) {
                        return false;
                    }
                });
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Unable to load " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
       }
   }

   public void resetCollapseByMenu() {
       //NOTE: can only collapse on OTU metadata
       collapseByMenu.removeAll();
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
       collapseByMenu.add(new JSeparator());
       JMenuItem item = new JMenuItem("Uncollapse All");
       item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.uncollapseTree();
            }
       });
       collapseByMenu.add(item);
       item = new JMenuItem("Collapse All");
       item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.collapseTree();
            }
       });
       collapseByMenu.add(item);
   }

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
                    frame.colorByValue(value);
                }
            });
            colorByOtuMetadataMenu.add(item);
       }
   }

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
                    frame.colorByValue(value);
                }
            });
            colorByGroup.add(item);
            colorBySampleMetadataMenu.add(item);
       }
   }

   	//run the PCoA
	private void runPcoaAnalysis() {

		ArrayList<String> otuids = new ArrayList<String>();
		ArrayList<String> sampleids = new ArrayList<String>();

		//Write the data to file
		DefaultTableModel model = (DefaultTableModel) frame.otuSampleMapTable.getModel();
		//get the column names
		for (int i = 1; i < frame.otuSampleMapTable.getColumnCount(); i++) {
			sampleids.add((String) frame.otuSampleMapTable.getColumnName(i));
		}
		try {
			System.out.println("writing...");
			BufferedWriter o = new BufferedWriter(new FileWriter("data.txt"));
			o.write("[");
			for (int i = 0; i < frame.otuSampleMapTable.getRowCount(); i++) {
				o.write("[");
				//skip first col, which is OTU ID
				otuids.add(frame.otuSampleMapTable.getValueAt(i,0).toString());
				for (int j = 1; j < frame.otuSampleMapTable.getColumnCount(); j++) {
					Object data = frame.otuSampleMapTable.getValueAt(i, j);
					if (data instanceof Integer) {
						if (j > 1) {o.write(", ");}
						o.write(data.toString());
					}
				}
				o.write("],\n");
			}
			o.write("]");
			o.close();
			System.out.println("done");

		} catch (IOException e)  {
			JOptionPane.showMessageDialog(null, "Unable to write data file PCoA analysis", "Error", JOptionPane.ERROR_MESSAGE);
		}

		//delete data files if they already exist
		File sample_coords = new File("sample_coords.txt");
		sample_coords.delete();
		File sp_coords = new File("sp_coords.txt");
		sp_coords.delete();

		System.out.println("running analysis...");
		try {
			//get the distance metric from combo box
            String dist_metric = null;
            for (Enumeration e = distanceMetricGroup.getElements(); e.hasMoreElements();) {
                JRadioButtonMenuItem b = (JRadioButtonMenuItem) e.nextElement();
                if (b.getModel() == distanceMetricGroup.getSelection()) {
                    dist_metric = b.getText();
                    break;
                }
            }

            //is the distance metric "Custom..."?
			if (dist_metric.equals("Custom...")) {
				//allow use to select file for distance matrix
                frame.loadDataFileChooser.setDialogTitle("Load custom distance matrix");
				int returnVal = frame.loadDataFileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					dist_metric += " " + frame.loadDataFileChooser.getSelectedFile().getAbsolutePath();
				}
			}

			//run python scripts to calculate PCoA
			Process pr = Runtime.getRuntime().exec("python l19test.py " + dist_metric);
            BufferedReader br = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
   			String line;
			while((line = br.readLine()) != null) {
				System.out.println(line);
			}
            int exitVal = pr.waitFor();
            System.out.println("Process Exit Value:" + exitVal);
			System.out.println("done.");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Unable to run python script for PCoA analysis", "Error", JOptionPane.ERROR_MESSAGE);
		}


	  System.out.println("loading data files...");
	  BufferedReader br = null;
	  try {
		br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("sample_coords.txt"))));
	  } catch (FileNotFoundException e) {
	  	JOptionPane.showMessageDialog(null, "Error opening find sample_coords.txt", "Error", JOptionPane.ERROR_MESSAGE);
	  }
	  String line;
	  float[][] samplec = new float[0][];

	  try {
		while((line = br.readLine()) != null) {
		  String[] s = line.split("\t");
		  float[] data = new float[s.length];
		  for (int i = 0; i < s.length; i++) {
			try {
			  data[i] = Float.parseFloat(s[i]);
			} catch(Exception e) {
			  data[i] = 0;
			}
		  }
		  //append data onto sample_coords
		  float[][] t = new float[samplec.length+1][];
		  System.arraycopy(samplec, 0, t, 0, samplec.length);
		  samplec = t;
		  samplec[samplec.length-1] = data;
		}
	  } catch (IOException e) {
		JOptionPane.showMessageDialog(null, "Error reading sp_coords.txt", "Error", JOptionPane.ERROR_MESSAGE);
	  }

	 try {
		br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("sp_coords.txt"))));
	  } catch (FileNotFoundException e) {
		JOptionPane.showMessageDialog(null, "Error opening sp_coords.txt", "Error", JOptionPane.ERROR_MESSAGE);
	  }
	  float[][] spc = new float[0][];

	  try {
		while((line = br.readLine()) != null) {
		  String[] s = line.split("\t");
		  float[] data = new float[s.length];
		  for (int i = 0; i < s.length; i++) {
			try {
			  data[i] = Float.parseFloat(s[i]);
			} catch(Exception e) {
			  data[i] = 0;
			}
		  }
		  //append data onto sp_coords
		  float[][] t = new float[spc.length+1][];
		  System.arraycopy(spc, 0, t, 0, spc.length);
		  spc = t;
		  spc[spc.length-1] = data;
		}
	  } catch (IOException e) {
		JOptionPane.showMessageDialog(null, "Error reading sp_coords.txt", "Error", JOptionPane.ERROR_MESSAGE);
	  }

	  frame.pcoa.spData = new VertexData[spc.length];
	  System.out.println((new Integer(spc.length)).toString() + " " + (new Integer(otuids.size())).toString());
	  for (int i = 0; i < spc.length; i++) {
		frame.pcoa.spData[i] = new VertexData();
		frame.pcoa.spData[i].coords = spc[i];
		frame.pcoa.spData[i].label = otuids.get(i);
		frame.pcoa.spData[i].weight = 1;
		frame.pcoa.spData[i].groupColor = new ArrayList<Color>();
		frame.pcoa.spData[i].groupFraction = new ArrayList<Double>();
		frame.pcoa.spData[i].velocity = new float[3];
		frame.pcoa.spData[i].velocity[0] = frame.pcoa.spData[i].velocity[1] = frame.pcoa.spData[i].velocity[2] = 0;
		frame.pcoa.spData[i].sh = "cube";
	  }

	  frame.pcoa.sampleData = new VertexData[samplec.length];
	  for (int i = 0; i < samplec.length; i++) {
		frame.pcoa.sampleData[i] = new VertexData();
		frame.pcoa.sampleData[i].coords = samplec[i];
		frame.pcoa.sampleData[i].label = sampleids.get(i);
		frame.pcoa.sampleData[i].weight = 1;
		frame.pcoa.sampleData[i].groupColor = new ArrayList<Color>();
		frame.pcoa.sampleData[i].groupFraction = new ArrayList<Double>();
		frame.pcoa.sampleData[i].velocity = new float[3];
		frame.pcoa.sampleData[i].velocity[0] = frame.pcoa.sampleData[i].velocity[1] = frame.pcoa.sampleData[i].velocity[2] = 0;
		frame.pcoa.sampleData[i].sh = "sphere";
	  }

	  float[][] links = new float[0][];
	  for (int i = 0; i < spc.length; i++) {
	  	for (int j = 0; j < samplec.length; j++) {
	  		float weight = (float) ((Integer) frame.otuSampleMapTable.getValueAt(i,j+1));
	  		if (weight != 0) {
	  			float[][] t = new float[links.length+1][];
			    System.arraycopy(links, 0, t, 0, links.length);
			    links = t;
			    float[] a = new float[3];
			    a[0] = i;
			    a[1] = j;
			    a[2] = weight;
			    links[links.length-1] = a;
			    frame.pcoa.spData[i].weight = frame.pcoa.spData[i].weight + 1;
			    frame.pcoa.sampleData[j].weight = frame.pcoa.sampleData[j].weight + 1;
	  		}
	  	}
	  }

	  //make otus with a single connection be diamonds
	  //for (int i = 0; i < pcoa.spData.length; i++) {
	  //	if (pcoa.spData[i].weight == 1) {
	  //		pcoa.spData[i].sh = "diamond";
	  //	}
	  //}
	  System.out.println("done.");
	  frame.pcoa.links = links;

      //color it
      frame.recolor();
	}
  

}