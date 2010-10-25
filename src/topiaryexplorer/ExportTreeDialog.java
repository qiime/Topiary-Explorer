package topiaryexplorer;

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
import javax.swing.text.*;
import java.text.*;
/**
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public class ExportTreeDialog extends JDialog implements ActionListener, DocumentListener {
    TreeWindow frame = null;
    TreeVis tree = null;
    
    JPanel mainPanel = new JPanel();
    JLabel dimsLabel = new JLabel("Dimensions:");
    JPanel dimsPanel = new JPanel();
    JLabel horzLabel = new JLabel("Horizontal: ");
    JTextField xdim = new JTextField(10);
    JLabel vertLabel = new JLabel("Vertical: ");
    JTextField ydim = new JTextField(10);
    JLabel saveName = new JLabel("Save as:");
    JTextField saveField = new JTextField(100);
    JPanel okCancelPanel = new JPanel();
    JButton okButton = new JButton("Save");
    JButton cancelButton = new JButton("Cancel");
    
    public double dims[] = {0, 0};
    private DecimalFormat df = new DecimalFormat("#.####");
    
    public ExportTreeDialog(TreeWindow _frame) {
        super((Frame)_frame, true);
        frame = _frame;
        this.setTitle("Save Tree Image");
        this.setSize(new Dimension(350,150));
        
        //dimensions stuff
        tree = frame.tree;
        String treeLayout = tree.getTreeLayout();
        
       if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
          dims[0] = (tree.getXScale() * tree.getTree().depth()) + tree.getMargin() + tree.getTreeMargin();
          dims[1] = (tree.getYScale() * tree.getTree().getNumberOfLeaves()) + 2*tree.getMargin();
      } else if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
          dims[0] = ((tree.getXScale() * tree.getTree().depth()) + tree.getMargin())*2;
          dims[1] = dims[0];
      }
      
     
        xdim = new JTextField();
        xdim.setText(df.format(dims[0]));
        xdim.setColumns(10);
        xdim.getDocument().addDocumentListener(this);
        xdim.getDocument().putProperty("name", "xdim");
        ydim = new JTextField();
        ydim.setText(df.format(dims[1]));
        ydim.setColumns(10);
        ydim.getDocument().addDocumentListener(this);
        ydim.getDocument().putProperty("name", "ydim");
        
        //add stuff
        mainPanel.setLayout(new GridLayout(5,1));
        mainPanel.add(dimsLabel);
        dimsPanel.setLayout(new GridLayout(1,4));
        dimsPanel.add(horzLabel);
        dimsPanel.add(xdim);
        dimsPanel.add(vertLabel);
        dimsPanel.add(ydim);
        mainPanel.add(dimsPanel);
        mainPanel.add(saveName);
        mainPanel.add(saveField);
        okCancelPanel.setLayout(new GridLayout(1,4));
        okCancelPanel.add(new JLabel());
        okCancelPanel.add(new JLabel());
        cancelButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  dims[0] = 0;
                  dims[1] = 0;
                  dispose();
              }
           });
        okCancelPanel.add(cancelButton);
        okButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  exportTree();
                  dispose();
              }
           });
        okCancelPanel.add(okButton);
        mainPanel.add(okCancelPanel);
        this.add(mainPanel,BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
	// }}}
	
	public void exportTree() {
	    //Determine PDF dimensions
         if (dims[0]!=0 && dims[1]!=0 && saveField.getText().length() > 0) {
            tree.exportTreeImage(frame.dir_path+"/tree_export_images/"+saveField.getText(), dims);
		}
		else
		{
		    JOptionPane.showMessageDialog(frame,
                "Please check inputs.\n"+
                "Dimensions cannot be 0 and \n"+
                "you must supply a file name.",
                "Save error",
                JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void change(DocumentEvent e) {
        String name = (String)e.getDocument().getProperty("name");
        if (name.equals("xdim")) {
            double newval = 0;
            try {
                newval = Double.parseDouble(xdim.getText());
                dims[0] = newval;
            } catch (NumberFormatException ex) {
            }
        } else if (name.equals("ydim")) {
            double newval = 0;
            try {
                newval = Double.parseDouble(ydim.getText());
                dims[1] = newval;
            } catch (NumberFormatException ex) {
            }
        }
    }
    
    public void insertUpdate(DocumentEvent e) {
        change(e);
    }
    public void removeUpdate(DocumentEvent e) {
    }
    public void changedUpdate(DocumentEvent e) {
    }
    
    public void actionPerformed(ActionEvent e) {
    }
    
}
