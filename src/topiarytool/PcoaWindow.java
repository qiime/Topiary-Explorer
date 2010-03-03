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

/**
 * PcoaWindow is the window that contains the PCoA visualization.
 */

public class PcoaWindow extends JFrame {
    MainFrame frame = null;
    PcoaVis pcoa = new PcoaVis();
    JPanel pcoaPanel = new JPanel();
    PcoaToolbar pcoaToolbar = null;
    Animator animator = null;

	public PcoaWindow(MainFrame _frame) {
	    frame = _frame;
	    this.setSize(new Dimension(600,600));
	    
	    Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
	    
	    pcoaToolbar = new PcoaToolbar(this);
	    
	    pcoaPanel.setLayout(new BorderLayout());
        pcoaPanel.add(pcoaToolbar, BorderLayout.PAGE_START);
        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(pcoa);
        animator = new FPSAnimator(canvas, 30);
        pcoaPanel.add(canvas, BorderLayout.CENTER);
        animator.start();
        
        pane.add(pcoaPanel, BorderLayout.CENTER);
	}
	
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
                //return;
                continue;
             }
             Object category = frame.otuMetadata.getValueAt(rowIndex, frame.colorColumnIndex);
             if (category == null) continue;
             //get the color for this category
             Color c = frame.colorMap.get(category);
             if (c == null) {
                JOptionPane.showMessageDialog(null, "ERROR: No color specified for category "+category.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
             }
             v.groupColor = new ArrayList<Color>();
             v.groupFraction = new ArrayList<Double>();
             v.groupColor.add(c);
             v.groupFraction.add(1.0);
         }
         
         repaint();
        
     }

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
                //return;
                continue;
             }
             Object category = frame.sampleMetadata.getValueAt(rowIndex, frame.colorColumnIndex);
             if (category == null) continue;
             //get the color for this category
             Color c = frame.colorMap.get(category);
             if (c == null) {
                JOptionPane.showMessageDialog(null, "ERROR: No color specified for category "+category.toString(), "Error", JOptionPane.ERROR_MESSAGE);
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