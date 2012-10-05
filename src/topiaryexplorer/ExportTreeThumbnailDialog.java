package topiaryexplorer;

import java.text.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.media.opengl.*;
import javax.swing.table.*;
import javax.swing.border.LineBorder;
import javax.jnlp.*;

/**
 * <<Class summary>>
 *
 * @author Meg Pirrung &lt;&gt;
 * @version $Rev$
 */
public class ExportTreeThumbnailDialog extends JDialog implements ActionListener, DocumentListener {
    TreeWindow frame = null;
    TreeVis tree = null;
    
    JPanel mainPanel = new JPanel();
    JLabel dimsLabel = new JLabel("PDF dimensions:");
    JPanel dimsPanel = new JPanel();
    JLabel horzLabel = new JLabel("Horizontal: ");
    JTextField xdim = new JTextField(5);
    JLabel vertLabel = new JLabel("Vertical: ");
    JTextField ydim = new JTextField(5);
	JLabel colorLabelText = new JLabel("Unselected category color");
	JLabel colorLabel = new JLabel("  ");
	JPanel colorPanel = new JPanel();
    JLabel saveName = new JLabel("Thumbnail folder name: ");
    JTextField saveField = new JTextField(100);
    JPanel okCancelPanel = new JPanel();
    JButton okButton = new JButton("Export");
    JButton cancelButton = new JButton("Cancel");
    
	public String prefix = "";
	public Color unselectedColor = new Color(0);
    public int dims[] = {0, 0};
    private DecimalFormat df = new DecimalFormat("#.####");
    
    public ExportTreeThumbnailDialog(TreeWindow _frame) {
        super((Frame)_frame, true);
        frame = _frame;
        this.setTitle("Export Tree Thumbnail Images");
        this.setSize(new Dimension(350,170));
        
        //dimensions stuff
        tree = frame.tree;
        String treeLayout = tree.getTreeLayout();
        
       if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
          dims[0] = (int)((tree.getXScale() * tree.getTree().depth()) + tree.getMargin() + tree.getTreeMargin());
          dims[1] = (int)((tree.getYScale() * tree.getTree().getNumberOfLeaves()) + 2*tree.getMargin());
      } else if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
          dims[0] = (int)((Math.max(tree.getXScale(),tree.getYScale()) * tree.getTree().depth() + tree.getTreeMargin() + tree.getMargin() + 10)*2);
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
        mainPanel.setLayout(new GridLayout(6,1));
        mainPanel.add(dimsLabel);
        dimsPanel.setLayout(new GridLayout(1,4));
        dimsPanel.add(horzLabel);
        dimsPanel.add(xdim);
        dimsPanel.add(vertLabel);
        dimsPanel.add(ydim);
        mainPanel.add(dimsPanel);
        

		colorLabel.setPreferredSize(new Dimension(20,20));
        colorLabel.setOpaque(true);
        colorLabel.setBorder(LineBorder.createGrayLineBorder());
        colorLabel.setBackground(Color.BLACK);
        frame.setNoCountColor(Color.BLACK);
        colorLabel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                                     ExportTreeThumbnailDialog.this,
                                     "Choose Unselected Color",
                                     colorLabel.getBackground());
                 if(newColor != null)
                 {
                     colorLabel.setBackground(newColor);
                     unselectedColor = newColor;
                 }
            }
            
            public void mouseExited(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mousePressed(MouseEvent e) {}
        });
        colorLabel.setToolTipText("Chose color for categories when they are not the subject of the thumbnail");
		colorPanel.add(colorLabel);
		colorPanel.add(colorLabelText);
		mainPanel.add(colorPanel);
		mainPanel.add(saveName);
        mainPanel.add(saveField);
		
        okCancelPanel.setLayout(new GridLayout(1,4));
        okCancelPanel.add(new JLabel());
        okCancelPanel.add(new JLabel());
        cancelButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  dims[0] = 0;
                  dims[1] = 0;
				  unselectedColor = null;
				  prefix = null;
                  dispose();
              }
           });
        okCancelPanel.add(cancelButton);
        okButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
				  prefix = saveField.getText();
                  exportTrees();
                  dispose();
              }
           });
        okCancelPanel.add(okButton);
        mainPanel.add(okCancelPanel);
        this.add(mainPanel,BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
	// }}}
	
	public void exportTrees() {
	    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    //Determine PDF dimensions
         if (dims[0]!=0 && dims[1]!=0 && saveField.getText().length() > 0) {
           frame.cycleColorsThroughMetadata(dims, prefix, unselectedColor);
		}
		else
		{
		    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    JOptionPane.showMessageDialog(frame,
                "Please check inputs.\n"+
                "Dimensions cannot be 0 and \n"+
                "you must supply a folder name.",
                "Save error",
                JOptionPane.ERROR_MESSAGE);
		}
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	public void change(DocumentEvent e) {
        String name = (String)e.getDocument().getProperty("name");
        if (name.equals("xdim")) {
            int newval = 0;
            try {
                newval = Integer.parseInt(xdim.getText());
                dims[0] = newval;
            } catch (NumberFormatException ex) {
            }
        } else if (name.equals("ydim")) {
            int newval = 0;
            try {
                newval = Integer.parseInt(ydim.getText());
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
