package topiaryexplorer;

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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.jnlp.*;
import java.net.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;

/**
 * TopiaryMenu is the main menu bar for TopiaryTool
 */
public class TopiaryMenu extends JMenuBar implements ActionListener{

    MainFrame frame = null;
    JMenu fileMenu = new JMenu("File");
    JMenu viewMenu = new JMenu("View");
    JMenu nodeMenu = new JMenu("Node");
    // JMenu pcoaMenu = new JMenu("PCoA");
    JMenu distanceMetricMenu = new JMenu("Distance Metric");
    JMenu sampleShapeMenu = new JMenu("Sample Shape");
    JMenu otuShapeMenu = new JMenu("OTU Shape");
    JRadioButtonMenuItem uniformLineWidthItem = new JRadioButtonMenuItem("Uniform");
    // JMenu pcoaLayoutMenu = new JMenu("Layout");
    // JSlider pcoaLineWidthSlider = new JSlider(1, 1000, 10);

    // ButtonGroup distanceMetricGroup = new ButtonGroup();
    // ButtonGroup pcoaLayoutGroup = new ButtonGroup();
    // ButtonGroup sampleShapeGroup = new ButtonGroup();
    // ButtonGroup otuShapeGroup = new ButtonGroup();

    // JCheckBoxMenuItem samplesMenuItem = new JCheckBoxMenuItem("Samples");
    // JCheckBoxMenuItem otusMenuItem = new JCheckBoxMenuItem("OTUs");
    // JCheckBoxMenuItem connectionsMenuItem = new JCheckBoxMenuItem("Connections");
    // JCheckBoxMenuItem axesMenuItem = new JCheckBoxMenuItem("Axes");
    // JCheckBoxMenuItem axisLabelsMenuItem = new JCheckBoxMenuItem("Axis Labels");
    // JCheckBoxMenuItem sampleLabelsMenuItem = new JCheckBoxMenuItem("Sample Labels");
    // JCheckBoxMenuItem otuLabelsMenuItem = new JCheckBoxMenuItem("OTU Labels");
    // 
    // JCheckBoxMenuItem colorSamplesMenuItem = new JCheckBoxMenuItem("Color Samples");
    // JCheckBoxMenuItem colorOtusMenuItem = new JCheckBoxMenuItem("Color OTUs");

    String dir_path = "";// = (new File(".")).getCanonicalPath();
    /**
     * Constructor.  Sets up the menu.
     */
     public TopiaryMenu(MainFrame _frame) {
         frame  = _frame;
        try{
        dir_path = (new File(".")).getCanonicalPath();
        }
        catch(IOException e)
        {}
        //initialize the menus
        //set up the "file" submenus
        JMenuItem item;
        item = new JMenuItem("New Project...");
        item.addActionListener(this);
        fileMenu.add(item);
        item = new JMenuItem("Open Project...");
        item.addActionListener(this);
        fileMenu.add(item);
        item = new JMenuItem("Save Project...");
        item.addActionListener(this);
        fileMenu.add(item);
        fileMenu.add(new JSeparator());
        item = new JMenuItem("Load Tree...");
        item.addActionListener(this);
        fileMenu.add(item);
        item = new JMenuItem("Load Tip Data...");
        item.addActionListener(this);
        fileMenu.add(item);
        item = new JMenuItem("Load OTU Table...");
        item.addActionListener(this);
        fileMenu.add(item);
        item = new JMenuItem("Load Sample Data...");
        item.addActionListener(this);
        fileMenu.add(item);
        item = new JMenuItem("Load Data Table...");
        item.addActionListener(this);
        fileMenu.add(item);
        fileMenu.add(new JSeparator());
        item = new JMenuItem("Quit");
        item.addActionListener(this);
        fileMenu.add(item);
        add(fileMenu);
     }


     public void actionPerformed(ActionEvent e) {

         if (e.getActionCommand().equals("Load Tree...")) {
            frame.newTreeWindow();
         } else if (e.getActionCommand().equals("New Project...")) {
           newProject();
         } else if (e.getActionCommand().equals("Open Project...")) {
                // frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				  frame.resetOtuMenus();
         		  frame.resetSampleMenus();
                  openProject();
                  // frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else if (e.getActionCommand().equals("Save Project...")) {
                    saveProject();
        } else if (e.getActionCommand().equals("Load Tip Data...")) {
           loadOtuMetadata(null);
         } else if (e.getActionCommand().equals("Load Sample Data...")) {
           loadSampleMetadata(null);
         } else if (e.getActionCommand().equals("Load OTU Table...")) {
             loadOtuSampleMap(null);
         } else if (e.getActionCommand().equals("Load Data Table...")) {
             loadDataTable(null);
         }
           else if (e.getActionCommand().equals("PCoA Window")) {
                  // frame.pcoaWindow.setVisible(!frame.pcoaWindow.isVisible());
        }else if (e.getActionCommand().equals("PCoA Window")) {
                // frame.consoleWindow.setVisible(!frame.consoleWindow.isVisible());
        } else if (e.getActionCommand().equals("Quit")) {
            if(frame.db_conn.c != null)
                frame.db_conn.c.close_connection();
             System.exit(0);
         } else if(e.getActionCommand().equals("Run PCoA Analysis...")) {
             // frame.pcoaWindow.runPcoaAnalysis();
         }
    }
    

    public void newProject() {
        frame.newProjectChooser = new NewProjectDialog(frame);
        
    }
    
    public void openProject(){
        
        try {
		    FileContents inFile = frame.fos.openFileDialog(dir_path, new String[]{"tep"});

    	        if(inFile != null)
                {
                    HashMap data = TopiaryFunctions.parseTep(inFile);
                    
                    // load otu metadata
                     if(data.containsKey("otm")){
                         frame.setOtuMetadata(((ArrayList<String>)data.get("otm")));
                     }
                     else
                        frame.resetOtuMetadata();
                     // load otu sample map
                     if(data.containsKey("osm")){
                         frame.setOtuSampleMap(((ArrayList<String>)data.get("osm")));
                     }
                     else
                        frame.resetOtuSampleMap();
                     // load sample metadata
                     if(data.containsKey("sam")){
                         frame.setSampleMetadata(((ArrayList<String>)data.get("sam")));
                     }
                     else
                        frame.resetSampleMetadata();
                     // load tree
                     if(data.containsKey("tre")){
                         frame.closeTreeWindows();
                         for(String s : (ArrayList<String>)data.get("tre"))
                         {
                             String name = "";
                             try {
                                 name = inFile.getName();
                             }
                             catch(IOException e){}
                             frame.newTreeWindow(s, true, name);
                         }
                     }
                     if(data.containsKey("pre")){
                          frame.addSchemes(TopiaryFunctions.parsePrefsFile((ArrayList<String>)data.get("pre"), frame.sampleMetadata));
                      }
                    frame.repaint();
                }
            }
            catch(IOException e)
            {
                frame.consoleWindow.update("Error opening project.");
            }
            
        }
    
        public void openProject(String projectPath){

        try {
                // this.getClass().getClassLoader().getResource(projectPath)
                try {
                     URL projectURL = new URL(projectPath);

                    if(projectURL != null)
                    {
                        HashMap data = TopiaryFunctions.parseTep(projectURL);

                        // load otu metadata
                         if(data.containsKey("otm")){
                             frame.setOtuMetadata(((ArrayList<String>)data.get("otm")));
                         }
                         else
                            frame.resetOtuMetadata();
                         // load otu sample map
                         if(data.containsKey("osm")){
                             frame.setOtuSampleMap(((ArrayList<String>)data.get("osm")));
                         }
                         else
                            frame.resetOtuSampleMap();
                         // load sample metadata
                         if(data.containsKey("sam")){
                             frame.setSampleMetadata(((ArrayList<String>)data.get("sam")));
                         }
                         else
                            frame.resetSampleMetadata();
                         // load tree
                         if(data.containsKey("tre")){
                             frame.closeTreeWindows();
                             
                             for(String s : (ArrayList<String>)data.get("tre"))
                                 frame.newTreeWindow(s, true, projectPath);
                         }
                         if(data.containsKey("pre")){
                              frame.addSchemes(TopiaryFunctions.parsePrefsFile((ArrayList<String>)data.get("pre"), frame.sampleMetadata));
                          }
                        frame.repaint();
                    }
                }
                catch(MalformedURLException e){
                    // System.out.println("Trying to open project...["+e.getMessage()+"]");
                    try {
                        FileReader inFile = new FileReader(projectPath);
                        if(inFile != null)
                        {
                            HashMap data = TopiaryFunctions.parseTep(inFile);

                            // load otu metadata
                             if(data.containsKey("otm")){
                                 frame.setOtuMetadata(((ArrayList<String>)data.get("otm")));
                             }
                             else
                                frame.resetOtuMetadata();
                             // load otu sample map
                             if(data.containsKey("osm")){
                                 frame.setOtuSampleMap(((ArrayList<String>)data.get("osm")));
                             }
                             else
                                frame.resetOtuSampleMap();
                             // load sample metadata
                             if(data.containsKey("sam")){
                                 frame.setSampleMetadata(((ArrayList<String>)data.get("sam")));
                             }
                             else
                                frame.resetSampleMetadata();
                             // load tree
                             if(data.containsKey("tre")){
                                 frame.closeTreeWindows();
                                 for(String s : (ArrayList<String>)data.get("tre"))
                                     frame.newTreeWindow(s, true, projectPath);
                              }       
                             if(data.containsKey("pre")){
                                 frame.addSchemes(TopiaryFunctions.parsePrefsFile((ArrayList<String>)data.get("pre"), frame.sampleMetadata));
                             }
                            frame.repaint();
                        }
                    }
                    catch(FileNotFoundException ex){
                        JOptionPane.showMessageDialog(null, "Unable to open project file.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println("Could not open project.");
                    }
                } 
            }
            catch(Exception e)
            {
                JOptionPane.showMessageDialog(null, "Error opening project file.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("Error opening project."+e);
            }

        }

    public void saveProject(){
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ByteArrayOutputStream b = new ByteArrayOutputStream();
		try
		{
		    FileContents fc = frame.fss.saveFileDialog(dir_path, new String[]{"tep"}, 
				new ByteArrayInputStream(b.toByteArray()),null);
			
			fc.setMaxLength(1000000000000L);
			
		    String s = "";
			ArrayList<String> lines = new ArrayList<String>();
			
			frame.cleanupMetadata();
			
			if(frame.treeWindows.size() != 0)
            {
                for(TreeWindow w : frame.treeWindows)
                {
			        lines.add(">>tre\n");
        		    lines.add(TopiaryFunctions.createNewickStringFromTree(w.tree.getTree()));
    	        }
		    }
			if(frame.otuMetadata.getColumnCount() > 0)
			    {
			        lines.add("\n>>otm\n#");
			        lines.addAll(frame.otuMetadata.toStrings());
		        }
			if(frame.otuSampleMap.getColumnCount() > 0) 
			    {
			        lines.add(">>osm\n#"); 
			        lines.addAll(frame.otuSampleMap.toStrings());
		        }
			if(frame.sampleMetadata.getColumnCount() > 0)
			{
			    lines.add(">>sam\n#");
			    lines.addAll(frame.sampleMetadata.toStrings());
		    }
		    if(frame.schemes.size() > 0)
		    {
		        lines.add(">>pre\n");
                for(Object name : frame.schemes.keySet())
                {
                    Object[] o = (Object[])frame.schemes.get(name);
                    HashMap h = (HashMap)o[1];
                    for(Object v : h.keySet())
                    {
                        Color c = (Color)h.get(v);
                        lines.add((String)v+":"+c.getRed()+","+c.getGreen()+","+c.getBlue()+",\n");
                    }
                    lines.add(">"+(String)name+":"+(String)o[0]+"\n");
                }
		    }		    
		    
		    for(int i = 0; i < lines.size(); i++)
                // b.write(lines.get(i).getBytes());
                fc.getOutputStream(false).write(lines.get(i).getBytes());

		}
		catch(IOException e)
			{
			    JOptionPane.showMessageDialog(null, "Unable to save project file.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
              System.out.println("Error saving project.");
                // frame.consoleWindow.update("Error saving project.");
			}
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
    * Loads OTU metadata from a file selected by the user.
    * Populates otu metadata table with information from the file.
    */
   public void loadOtuMetadata(FileContents inFile){
       frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      if (inFile==null) {
      	   try {
   	           inFile = frame.fos.openFileDialog(null,null);
   	       } catch (java.io.IOException e) {}
   	   }
       if(inFile != null)
       {               
           if(frame.otuMetadata != null)
                clearOtuMetadata();
                
            //set view
            frame.dataPane.setSelectedIndex(1);
            try {
                InputStream is = inFile.getInputStream();
                frame.setOtuMetadata(is);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Unable to load " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            if (frame.currTable == frame.otuMetadata) {
                for(TreeWindow w : frame.treeWindows)
                       w.removeColor();
                
            }
            frame.resetOtuMenus();
            for(TreeWindow t : frame.treeWindows)
            {
                t.tree.redraw();
            }
            frame.otuMetadataFile = inFile;
       }
       frame.repaint();
       frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }
   
   public void clearOtuMetadata() {
       frame.resetOtuMenus();
       frame.otuMetadataFile = null;
       frame.repaint();
   }

   /**
   * Loads sample metadata from a file selected by the user.
   * Populates sample metadata table with information from the file.
   */
   public void loadSampleMetadata(FileContents inFile){
   frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
   	   if (inFile==null) {
   	       try {
   	           inFile = frame.fos.openFileDialog(null,null);
   	       } catch (java.io.IOException e) {}
   	   }
       if (inFile != null) {
/*           for(TreeWindow t : frame.treeWindows)
               t.tree.noLoop();*/
           if(frame.sampleMetadata != null)
                clearSampleMetadata();
                
            //set view
            frame.dataPane.setSelectedIndex(3);
            try {
                InputStream is = inFile.getInputStream();
                frame.setSampleMetadata(is);
			    
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Unable to load " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            if (frame.currTable == frame.sampleMetadata) {
                for(TreeWindow t : frame.treeWindows)
                    t.removeColor();
            }
            frame.resetSampleMenus();
            for(TreeWindow t : frame.treeWindows)
                t.tree.redraw();
            frame.sampleMetadataFile = inFile;
       }
       frame.repaint();
       frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }
   
   public void clearSampleMetadata() {
       frame.resetSampleMenus();
       frame.sampleMetadataFile = null;
       for(TreeWindow t : frame.treeWindows)
              t.tree.redraw();
       frame.repaint();
   }

   public void loadDataTable(FileContents inFile){
       frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
   	   if (inFile==null) {
   	       try {
   	           inFile = frame.fos.openFileDialog(null,null);
   	       } catch (java.io.IOException e) {}
   	   }   
       if (inFile != null) {
           
            try{
            InputStream is = inFile.getInputStream();
            DataTable table = new DataTable(is);
            TableWindow tWindow = new TableWindow(frame, table);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Unable to load " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
       }
       frame.repaint();
       frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }

   /**
   * Loads OTU-sample map from a file selected by the user.
   * Populates otu-sample map table with information from the file.
   */
   public void loadOtuSampleMap(FileContents inFile){
       frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
   	   if (inFile==null) {
   	       try {
   	           inFile = frame.fos.openFileDialog(null,null);
   	       } catch (java.io.IOException e) {}
   	   }   
       if (inFile != null) {
           for(TreeWindow w : frame.treeWindows)
               w.tree.noLoop();
           if(frame.otuSampleMap != null)
                clearOtuSampleMap();
            //set view
            frame.dataPane.setSelectedIndex(2);
            try{
            InputStream is = inFile.getInputStream();
            frame.setOtuSampleMap(is);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Unable to load " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }

            for(TreeWindow w : frame.treeWindows)
                   w.tree.redraw();
            frame.otuSampleMapFile = inFile;
       }
       frame.repaint();
       frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
   }
   
   public void clearOtuSampleMap() {
       frame.otuSampleMapFile = null;
       frame.repaint();
   }
   

    /**
    * Resets tip labels on the tree
    */
    public void resetTipLabels() {
    }

}