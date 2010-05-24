package topiarytool;

import com.sun.opengl.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.media.opengl.*;
import java.sql.*;
import javax.swing.table.*;
import java.io.*;
import javax.swing.table.*;
import javax.jnlp.*;
import java.io.*;
import java.net.*;

/**
 * PcoaWindow is the window that contains the PCoA visualization.
 */
public class PcoaWindow extends JFrame {
    MainFrame frame = null;
    PcoaVis pcoa = new PcoaVis();
    JPanel pcoaPanel = new JPanel();
    PcoaToolbar pcoaToolbar = null;
    Animator animator = null;
    PcoaOptionsToolbar pcoaOptionsToolbar = null;

    /**
    * Class constructor
    * @param _frame Instance of MainFrame for the current run
    */
	public PcoaWindow(MainFrame _frame) {
	    frame = _frame;
	    this.setSize(new Dimension(800,600));
	    
	    Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
	    
	    pcoaToolbar = new PcoaToolbar(this);
	    pcoaOptionsToolbar = new PcoaOptionsToolbar(this);
	    pane.add(pcoaOptionsToolbar, BorderLayout.WEST);
	    
	    pcoaPanel.setLayout(new BorderLayout());
        pcoaPanel.add(pcoaToolbar, BorderLayout.PAGE_END);
        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(pcoa);
        animator = new FPSAnimator(canvas, 30);
        pcoaPanel.add(canvas, BorderLayout.CENTER);
        animator.start();
        
        pane.add(pcoaPanel, BorderLayout.CENTER);
	}
	
	public void setBackgroundColor() {
	    JColorChooser colorChooser = new JColorChooser();
        Color c = colorChooser.showDialog(this, "Pick a Color", pcoa.getBackgroundColor());
        pcoa.setBackgroundColor(c);
	}
	
	public void setAxes() {
	    if (pcoa.spData == null) {
            JOptionPane.showMessageDialog(null, "PCOA analysis must be run first.", "Error", JOptionPane.ERROR_MESSAGE);
            frame.consoleWindow.update("PCOA analysis must be run first.");
        } else {
            PCSelectDialog p = new PCSelectDialog(this);
            p.pack();
            p.setVisible(true);
        }
	}
	
	/**
    * Run principle coordinates anaylsis with the currently
    * selected distance metric.
    */
	public void runPcoaAnalysis() {

		System.out.println(frame.bs.getCodeBase().toString());
        //set view
        this.setVisible(true);

		ArrayList<String> otuids = new ArrayList<String>();
		ArrayList<String> sampleids = new ArrayList<String>();

		//Write the data to file
		SparseTableModel model = (SparseTableModel) ((TableSorter)frame.otuSampleMapTable.getModel()).getTableModel();
		//get the column names
		for (int i = 1; i < frame.otuSampleMapTable.getColumnCount(); i++) {
			sampleids.add((String) frame.otuSampleMapTable.getColumnName(i));
		}
		StringBuilder datastrb = new StringBuilder();
		System.out.println("Setting up data string...");
		pcoaOptionsToolbar.setStatus("Setting up data string...");
		datastrb.append("[");
		for (int i = 0; i < frame.otuSampleMapTable.getRowCount(); i++) {
			datastrb.append("[");
			//skip first col, which is OTU ID
			Object val = frame.otuSampleMapTable.getValueAt(i,0);
			if (val==null) { val = new Integer(0); }
			otuids.add(val.toString());
			for (int j = 1; j < frame.otuSampleMapTable.getColumnCount(); j++) {
				Object data = frame.otuSampleMapTable.getValueAt(i, j);
				if (data==null || data.equals("")) {data = new Integer(0);}
				if (data instanceof Integer) {
					if (j > 1) {datastrb.append(", ");}
					datastrb.append(data.toString());
				}
			}
			datastrb.append("],");
		}
		datastrb.append("]");
		String datastr = datastrb.toString();
		System.out.println("done");
		pcoaOptionsToolbar.setStatus("Writing to server...");
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			socket = new Socket(java.net.InetAddress.getLocalHost(), 4444);
		
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch(Exception ex){
			System.out.println("Unable to open connection to socket.");
		}
		//write to server
		out.println(datastr);
		//get the distance metric from combo box
		
		String dist_metric = pcoaOptionsToolbar.distanceMetrics.getSelectedItem(); 

		//is the distance metric "Custom..."?
		if (dist_metric.equals("Load from file")) {
			//allow use to select file for distance matrix
			
			try {
				FileContents fc = frame.fos.openFileDialog(null,null);
				byte[] b = new byte[100000];
				fc.getInputStream().read(b);
				dist_metric = new String(b);
			} catch (IOException ex) {System.out.println("Error reading distance metric from file");}
		}
		out.println(dist_metric);
		
		frame.consoleWindow.update("Done writing PCoA analysis.");


	
	    // receive data files
	    try{
			int bytesRead;
			int current = 0;
			byte [] mybytearray  = new byte [100000];
			DataInputStream is = new DataInputStream(socket.getInputStream());
			
			//read file length
			int fileLen1 = is.readInt();
			System.out.println(fileLen1);
			
			//read first file
			FileContents fout = frame.es.openFile(new File("sp_coords.txt"));
			OutputStream fo = fout.getOutputStream(true);
			BufferedOutputStream bos = new BufferedOutputStream(fo);
			is.read(mybytearray, 0, fileLen1);
			//is.skipBytes(fileLen1);
			bos.write(mybytearray, 0, fileLen1);
			bos.flush();
			bos.close();
			
			//read file length
			int fileLen2 = is.readInt();
			System.out.println(fileLen2);
			
			//read second file
			fout = frame.es.openFile(new File("sample_coords.txt"));
			fo = fout.getOutputStream(true);
			bos = new BufferedOutputStream(fo);
			is.read(mybytearray, 0, fileLen2);
			bos.write(mybytearray, 0, fileLen2);
			//is.skipBytes(fileLen2);
			bos.flush();
			bos.close();
			
			//read file length
			int fileLen3 = is.readInt();
			System.out.println(fileLen3);
			
			//read third file
			fout = frame.es.openFile(new File("evals.txt"));
			fo = fout.getOutputStream(true);
			bos = new BufferedOutputStream(fo);
			is.read(mybytearray,0, fileLen3);
			bos.write(mybytearray, 0, fileLen3);
			//is.skipBytes(fileLen3);
			bos.flush();
			bos.close();
			
    	} catch(Exception ex) {
    		System.out.println("Unable to receive data files:");
    		ex.printStackTrace();
    	}


	  System.out.println("loading data files...");
	  pcoaOptionsToolbar.setStatus("Loading data files...");
	  BufferedReader br = null;
	  try {
		br = new BufferedReader(new InputStreamReader(frame.es.openFile(new File("sample_coords.txt")).getInputStream()));
	  } catch (Exception e) {
	  	JOptionPane.showMessageDialog(null, "Error opening find sample_coords.txt", "Error", JOptionPane.ERROR_MESSAGE);
	    frame.consoleWindow.update("Error opening find sample_coords.txt");
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
	    frame.consoleWindow.update("Error reading sp_coords.txt");
	  }

	 try {
		br = new BufferedReader(new InputStreamReader(frame.es.openFile(new File("sp_coords.txt")).getInputStream()));
	  } catch (Exception e) {
		JOptionPane.showMessageDialog(null, "Error opening sp_coords.txt", "Error", JOptionPane.ERROR_MESSAGE);
	    frame.consoleWindow.update("Error opening sp_coords.txt");
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
	    frame.consoleWindow.update("Error reading sp_coords.txt");
	}
	  
	  
	  try {
		br = new BufferedReader(new InputStreamReader(frame.es.openFile(new File("evals.txt")).getInputStream()));
	  } catch (Exception e) {
		JOptionPane.showMessageDialog(null, "Error opening evals.txt", "Error", JOptionPane.ERROR_MESSAGE);
	    frame.consoleWindow.update("Error opening evals.txt");
	  }
	  float[] evalsc = null;

	  try {
		  line = br.readLine();
		  String[] s = line.split("\t");
		  float[] data = new float[s.length];
		  for (int i = 0; i < s.length; i++) {
			try {
			  data[i] = Float.parseFloat(s[i]);
			} catch(Exception e) {
			  data[i] = 0;
			}
		  }
		  evalsc = data;
	  } catch (IOException e) {
		JOptionPane.showMessageDialog(null, "Error reading evals.txt", "Error", JOptionPane.ERROR_MESSAGE);
	    frame.consoleWindow.update("Error reading evals.txt");
	  }
	  
	  

	  pcoa.spData = new VertexData[spc.length];
	  System.out.println((new Integer(spc.length)).toString() + " " + (new Integer(otuids.size())).toString());
	  frame.consoleWindow.update((new Integer(spc.length)).toString() + " " + (new Integer(otuids.size())).toString());
	  for (int i = 0; i < spc.length; i++) {
		pcoa.spData[i] = new VertexData();
		pcoa.spData[i].coords = spc[i];
		pcoa.spData[i].label = otuids.get(i);
		pcoa.spData[i].weight = 1;
		pcoa.spData[i].groupColor = new ArrayList<Color>();
		pcoa.spData[i].groupFraction = new ArrayList<Double>();
		pcoa.spData[i].velocity = new float[3];
		pcoa.spData[i].velocity[0] = pcoa.spData[i].velocity[1] = pcoa.spData[i].velocity[2] = 0;
	  }

	  pcoa.sampleData = new VertexData[samplec.length];
	  for (int i = 0; i < samplec.length; i++) {
		pcoa.sampleData[i] = new VertexData();
		pcoa.sampleData[i].coords = samplec[i];
		pcoa.sampleData[i].label = sampleids.get(i);
		pcoa.sampleData[i].weight = 1;
		pcoa.sampleData[i].groupColor = new ArrayList<Color>();
		pcoa.sampleData[i].groupFraction = new ArrayList<Double>();
		pcoa.sampleData[i].velocity = new float[3];
		pcoa.sampleData[i].velocity[0] = pcoa.sampleData[i].velocity[1] = pcoa.sampleData[i].velocity[2] = 0;
	  }
	  
	  pcoa.evals = new ArrayList<Double>();
	  for (int i = 0; i < evalsc.length; i++) {
	    pcoa.evals.add(new Double(evalsc[i]));
	  }

	  float[][] links = new float[0][];
	  for (int i = 0; i < spc.length; i++) {
	  	for (int j = 0; j < samplec.length; j++) {
	  	    Object val = frame.otuSampleMapTable.getValueAt(i,j+1);
	  	    if (val==null || val.equals("")) {val=new Integer(0);}
	  		float weight = (float) ((Integer)val);
	  		if (weight != 0) {
	  			float[][] t = new float[links.length+1][];
			    System.arraycopy(links, 0, t, 0, links.length);
			    links = t;
			    float[] a = new float[3];
			    a[0] = i;
			    a[1] = j;
			    a[2] = weight;
			    links[links.length-1] = a;
			    frame.pcoaWindow.pcoa.spData[i].weight = pcoa.spData[i].weight + 1;
			    pcoa.sampleData[j].weight = pcoa.sampleData[j].weight + 1;
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
	  pcoaOptionsToolbar.setStatus("Done.");
	  pcoa.links = links;

      //color it
      frame.recolor();
      
      //set axis labels
      pcoa.resetAxisLabels();
	}
   	
	/**
    * Recolor the PCoA plot by the selected OTU value
    */
	public void recolorPcoaByOtu() {
        if (pcoa.spData == null) return;
        //loop over each sample vertex
        for (VertexData v : pcoa.sampleData) {
            v.groupColor = new ArrayList<Color>();
            v.groupFraction = new ArrayList<Double>();
            String sampleID = v.label;
            //find the column of the otu-sample map with this ID
            int colIndex = frame.otuSampleMap.getColumnNames().indexOf(sampleID);
            //get this column of the table
            ArrayList<Object> colData = frame.otuSampleMap.getColumn(colIndex);
            //for each non-zero row value
            for (int i = 0; i < colData.size(); i++) {
                Object value = colData.get(i);
                //if it's not an Integer, skip it
                if (!(value instanceof Integer)) continue;
                Integer weight = (Integer)value;
                if (weight == 0) continue;
                Object otuID = frame.otuSampleMap.getValueAt(i,0);
                //find the row that has this otuID
                int otuRowIndex = -1;
                for (int j = 0; j < frame.otuMetadata.getData().maxRow(); j++) {
                   if (frame.otuMetadata.getData().get(j,0).equals(otuID)) {
                       otuRowIndex = j;
                       break;
                   }
                }
                if (otuRowIndex == -1) {
                   JOptionPane.showMessageDialog(null, "ERROR: OTU ID "+otuID+" not found in OTU Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);
                    frame.consoleWindow.update("ERROR: OTU ID "+otuID+" not found in OTU Metadata Table.");
                   return;
                }
                Object val = frame.otuMetadata.getValueAt(otuRowIndex, frame.colorColumnIndex);
                if (val == null) {
                    v.groupColor.add(new Color(0,0,0));
                } else {
                    v.groupColor.add(frame.colorMap.get(val));
                }
                v.groupFraction.add(new Double(weight.intValue()));
            }
            v.mergeColors();
        }
        
        
          //loop over each otu vertex
         for (VertexData v : pcoa.spData) {
             //get the otuID
             String otuID = v.label;
             //find the row of the otu metadata table with this ID
             int rowIndex = -1;
             for (int i = 0; i < frame.otuMetadata.getData().maxRow(); i++) {
                 if (frame.otuMetadata.getData().get(i,0).equals(otuID)) {
                     rowIndex = i;
                     break;
                 }
             }
             if (rowIndex == -1) {
                //JOptionPane.showMessageDialog(null, "ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("ERROR: OTU ID "+otuID+" not found in OTU Metadata Table.");
                frame.consoleWindow.update("ERROR: OTU ID "+otuID+" not found in OTU Metadata Table.");
                //return;
                continue;
             }
             Object category = frame.otuMetadata.getValueAt(rowIndex, frame.colorColumnIndex);
             if (category == null) continue;
             //get the color for this category
             Color c = frame.colorMap.get(category);
             if (c == null) {
                JOptionPane.showMessageDialog(null, "ERROR: No color specified for category "+category.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                frame.consoleWindow.update("ERROR: No color specified for category "+category.toString());
                return;
             }
             v.groupColor = new ArrayList<Color>();
             v.groupFraction = new ArrayList<Double>();
             v.groupColor.add(c);
             v.groupFraction.add(1.0);
         }
         
         repaint();
        
     }

     /**
     * Recolor the PCoA plot by the selected sample value
     */
     public void recolorPcoaBySample() {
         if (pcoa.sampleData == null) return;
         //loop over each sample vertex
         for (VertexData v : pcoa.sampleData) {
             //get the sampleID
             String sampleID = v.label;
             //find the row of the sample metadata table with this ID
             int rowIndex = -1;
             for (int i = 0; i < frame.sampleMetadata.getData().maxRow(); i++) {
                 if (frame.sampleMetadata.getData().get(i,0).equals(sampleID)) {
                     rowIndex = i;
                     break;
                 }
             }
             if (rowIndex == -1) {
                //JOptionPane.showMessageDialog(null, "ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.");
                frame.consoleWindow.update("ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.");
                //return;
                continue;
             }
             Object category = frame.sampleMetadata.getValueAt(rowIndex, frame.colorColumnIndex);
             if (category == null) continue;
             //get the color for this category
             Color c = frame.colorMap.get(category);
             if (c == null) {
                JOptionPane.showMessageDialog(null, "ERROR: No color specified for category "+category.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                frame.consoleWindow.update("ERROR: No color specified for category "+category.toString());
                return;
             }
             v.groupColor = new ArrayList<Color>();
             v.groupFraction = new ArrayList<Double>();
             v.groupColor.add(c);
             v.groupFraction.add(1.0);
         }
         
        //loop over each sample vertex
        for (VertexData v : pcoa.spData) {
            v.groupColor = new ArrayList<Color>();
            v.groupFraction = new ArrayList<Double>();
            String otuID = v.label;
            //find the row of the otu-sample map with this ID
            int rowIndex = frame.otuSampleMap.getColumn(0).indexOf(otuID);
            //get this row of the table
            ArrayList<Object> rowData = frame.otuSampleMap.getRow(rowIndex);
            //for each non-zero row value
            for (int i = 1; i < rowData.size(); i++) {
                Object value = rowData.get(i);
                //if it's not an Integer, skip it
                if (!(value instanceof Integer)) continue;
                Integer weight = (Integer)value;
                if (weight == 0) continue;
                String sampleID = frame.otuSampleMap.getColumnName(i);
                //find the row that has this sampleID
                int sampleRowIndex = -1;
                for (int j = 0; j < frame.sampleMetadata.getData().maxRow(); j++) {
                   if (frame.sampleMetadata.getData().get(j,0).equals(sampleID)) {
                       sampleRowIndex = j;
                       break;
                   }
                }
                if (sampleRowIndex == -1) {
                   JOptionPane.showMessageDialog(null, "ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.", "Error", JOptionPane.ERROR_MESSAGE);
                   frame.consoleWindow.update("ERROR: Sample ID "+sampleID+" not found in Sample Metadata Table.");
                   return;
                }
                Object val = frame.sampleMetadata.getValueAt(sampleRowIndex, frame.colorColumnIndex);
                if (val == null) {
                    v.groupColor.add(new Color(0,0,0));
                } else {
                    v.groupColor.add(frame.colorMap.get(val));
                }
                v.groupFraction.add(new Double(weight.intValue()));
            }
            v.mergeColors();
        }
        repaint();
     }
}