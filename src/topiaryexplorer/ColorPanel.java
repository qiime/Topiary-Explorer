package topiaryexplorer;

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
import javax.jnlp.*;

/**
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public class ColorPanel extends JPanel{
    MainFrame frame = null;
    JScrollPane colorKeyScrollPane = new JScrollPane();  
    JTable colorKeyTable = new JTable();
    int elementType = 0;
    
    //Holds the current coloring information
    private TreeMap<Object, Color> colorMap = new TreeMap<Object, Color>();
    private int colorColumnIndex = -1;
	// {{{ ColorPanel constructor
    /**
     * 
     */
    public ColorPanel(MainFrame _frame, int _elementType) {
        super();
        frame = _frame;
        elementType = _elementType;
        
        colorKeyTable.setModel(new ColorTableModel());
        colorKeyScrollPane = new JScrollPane(colorKeyTable);
        colorKeyScrollPane.setPreferredSize(new Dimension(190,600));
        add(colorKeyScrollPane);
    }
	// }}}
	
	public TreeMap<Object, Color> getColorMap(){ return colorMap; }
	public void setColorMap(TreeMap<Object, Color> c) { colorMap = c; }
	public JTable getColorKeyTable() { return colorKeyTable; }
	public int getColorColumnIndex() { return colorColumnIndex; }
	public void setColorColumnIndex(int i) { colorColumnIndex = i; }
    /**
      * Syncs the colorKeyTable with ColorMap
      */
     public void syncColorKeyTable() {
         //data is: name, color, selected
         ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
         for (Object key : colorMap.keySet()) {
             ArrayList<Object> newRow = new ArrayList<Object>();
             newRow.add(key);
             newRow.add(colorMap.get(key));
             newRow.add(new Boolean(false));
             data.add(newRow);
         }

         ArrayList<String> colNames = new ArrayList<String>();
         colNames.add("Category");
         colNames.add("Color");
         colNames.add("");

         colorKeyTable.setModel(new ColorTableModel(data, colNames));
         colorKeyTable.setDefaultRenderer(Color.class, new ColorRenderer(true));
         colorKeyTable.setDefaultEditor(Color.class, new ColorEditor());
         colorKeyTable.setSelectionBackground(new Color(255,255,255));
         colorKeyTable.setSelectionForeground(new Color(0,0,0));
         colorKeyTable.getColumnModel().getColumn(0).setPreferredWidth(150);
         colorKeyTable.getColumnModel().getColumn(1).setPreferredWidth(20);
         colorKeyTable.getColumnModel().getColumn(2).setPreferredWidth(20);
         colorKeyTable.setDragEnabled(true);

         colorKeyTable.getModel().addTableModelListener(new TableModelListener() {
             public void tableChanged(TableModelEvent e) {
                 int row = e.getFirstRow();
                 int column = e.getColumn();
                 ColorTableModel model = (ColorTableModel)e.getSource();
                 Object value = model.getValueAt(row, 0);
                 Object data = model.getValueAt(row, column);
                 if (column == 2) {
                     model.setValueAt(data, row, column);
                 } else if (column == 1) {
                     //update the color map
                     colorMap.remove(value);
                     colorMap.put(value, (Color)data);
                     if(elementType == 0)
                         frame.recolorBranches();
                     else if(elementType == 1)
                         frame.recolorLabels();
                 }
             }
         });
     }

     /**
      * Syncs the ColorMap with the colorKeyTable
      */
     public void syncColorMap() {
        colorMap.clear();
        for (ArrayList<Object> row : ((ColorTableModel)colorKeyTable.getModel()).getData()) {
            colorMap.put(row.get(0), (Color)row.get(1));
        }
     }
     
     public void interpolateColors() {
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
 		ArrayList<ArrayList<Object>> data = ((ColorTableModel)colorKeyTable.getModel()).getData();

 		int first = -1;
 		int second = -1;
 		//find the first color
 		for (int i = 0; i < data.size(); i++) {
 			if ((Boolean) data.get(i).get(2) == true) {
 				second = i;
 				break;
 			}
 		}
 		//now, keep moving down the list and interpolating until we reach the end
 		while (true) {
 			//switch the second color to the first
 			first = second;
 			second = -1;
 			//find the next color
 			for (int i = first+1; i < data.size(); i++) {
 				if ((Boolean) data.get(i).get(2) == true) {
 				second = i;
 				break;
 				}
 			}
 			//have we reached the end?
 			if (second == -1) {
 				((ColorTableModel) colorKeyTable.getModel()).setData(data);
                 colorKeyTable.repaint();
                 syncColorMap();
                 if(elementType == 0)
                      frame.recolorBranches();
                  else if(elementType == 1)
                      frame.recolorLabels();
                 setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                 return;
 			}
 			//interpolate
 			Color firstColor = (Color) data.get(first).get(1);
 			Color secondColor = (Color) data.get(second).get(1);
 			for (int i = first+1; i < second; i++) {
 				//here's the interpolation
 				float frac = (i-first)*(1.0f/(second-first));
 				Color c = new Color((1-frac)*firstColor.getRed()/255.0f + frac*secondColor.getRed()/255.0f,
 					(1-frac)*firstColor.getGreen()/255.0f + frac*secondColor.getGreen()/255.0f,
 					(1-frac)*firstColor.getBlue()/255.0f + frac*secondColor.getBlue()/255.0f);
                 data.get(i).set(1, c);
 			}
 		}
      }

}
