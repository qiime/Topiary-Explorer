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
    // JLabel dimsLabel = new JLabel("PDF dimensions");
    // JPanel dimsPanel = new JPanel();
    JLabel horzLabel = new JLabel("Width: ");
    JTextField xdim = new JTextField(5);
    JLabel vertLabel = new JLabel("Height: ");
    JTextField ydim = new JTextField(5);
	JLabel colorLabelText = new JLabel("Unselected Category Color");
	JLabel colorLabel = new JLabel("  ");
	JPanel colorPanel = new JPanel();
    JLabel saveName = new JLabel("Folder Name: ");
    JTextField saveField = new JTextField();
    JPanel okCancelPanel = new JPanel();
    JButton okButton = new JButton("Export");
    JButton cancelButton = new JButton("Cancel");
    JCheckBox normalizeBox = new JCheckBox("",false);
	JLabel normalizeText = new JLabel("Normalize Abundance");
	JPanel optionPanel = new JPanel();
	
	public String prefix = "";
	public Color unselectedColor = new Color(0);
    public int dims[] = {0, 0};
    private DecimalFormat df = new DecimalFormat("#.####");
    
    public ExportTreeThumbnailDialog(TreeWindow _frame) {
        super((Frame)_frame, true);
        frame = _frame;
        this.setTitle("Batch Export Tree Images");
        this.setSize(new Dimension(350,190));
        
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
        // mainPanel.setLayout(new GridLayout(7,1));
        //         mainPanel.add(dimsLabel);
        //         dimsPanel.setLayout(new GridLayout(1,4));
        //         dimsPanel.add(horzLabel);
        //         dimsPanel.add(xdim);
        //         dimsPanel.add(vertLabel);
        //         dimsPanel.add(ydim);
        //         mainPanel.add(dimsPanel);
        

		colorLabel.setPreferredSize(new Dimension(20,20));
        colorLabel.setOpaque(true);
        colorLabel.setBorder(LineBorder.createGrayLineBorder());
        colorLabel.setBackground(Color.BLACK);
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
		// colorPanel.add(colorLabel);
		// colorPanel.add(colorLabelText);
		// mainPanel.add(colorPanel);
		// mainPanel.add(normalizeBox);
		// mainPanel.add(saveName);
		//         mainPanel.add(saveField);
		
        // okCancelPanel.setLayout(new GridLayout(1,4));
        // okCancelPanel.add(new JLabel());
        // okCancelPanel.add(new JLabel());
        cancelButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  dims[0] = 0;
                  dims[1] = 0;
				  unselectedColor = null;
				  prefix = null;
                  dispose();
              }
           });
        // okCancelPanel.add(cancelButton);
        okButton.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
				  prefix = saveField.getText();
                  exportTrees();
                  // dispose();
              }
           });
        // okCancelPanel.add(okButton);
        // mainPanel.add(okCancelPanel);
        // this.add(mainPanel,BorderLayout.CENTER);
		
		//layout stuff
		optionPanel.setBorder(BorderFactory.createTitledBorder("Options:"));
		GroupLayout optionPanelLayout = new GroupLayout(optionPanel);
		        optionPanel.setLayout(optionPanelLayout);
		        optionPanelLayout.setHorizontalGroup(
		            optionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		            .addGroup(optionPanelLayout.createSequentialGroup()
		                .addContainerGap()
		                .addGroup(optionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		                    .addGroup(optionPanelLayout.createSequentialGroup()
		                        .addComponent(horzLabel)
		                        .addGap(2, 2, 2)
		                        .addComponent(xdim, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
		                        .addGap(18, 18, 18)
		                        .addComponent(vertLabel)
		                        .addGap(2, 2, 2)
		                        .addComponent(ydim, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
		                    .addGroup(optionPanelLayout.createSequentialGroup()
		                        .addGroup(optionPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addGroup(optionPanelLayout.createSequentialGroup()
			                                .addComponent(colorLabelText)
			                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			                                .addComponent(colorLabel, GroupLayout.DEFAULT_SIZE, 22, GroupLayout.PREFERRED_SIZE))))
							.addGroup(optionPanelLayout.createSequentialGroup()
		                        .addGroup(optionPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
										.addGroup(optionPanelLayout.createSequentialGroup()
			                                .addComponent(normalizeText)
			                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
			                                .addComponent(normalizeBox))))
						)
		                .addContainerGap())
		        );
		        optionPanelLayout.setVerticalGroup(
		            optionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		            .addGroup(optionPanelLayout.createSequentialGroup()
		                .addGroup(optionPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                    .addComponent(horzLabel)
		                    .addComponent(vertLabel)
		                    .addComponent(ydim, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		                    .addComponent(xdim, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                .addGroup(optionPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                    .addComponent(colorLabelText)
		                    .addComponent(colorLabel, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                .addGroup(optionPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
		                    .addComponent(normalizeText)
		                    .addComponent(normalizeBox))
		                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		        );

		        GroupLayout layout = new GroupLayout(getContentPane());
		        this.setLayout(layout);
		        layout.setHorizontalGroup(
		            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		            .addGroup(layout.createSequentialGroup()
		                .addContainerGap()
		                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
		                    .addComponent(optionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
		                        .addComponent(saveName)
		                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                        .addComponent(saveField, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)))
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
		                    .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		        );
		        layout.setVerticalGroup(
		            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		            .addGroup(layout.createSequentialGroup()
		                .addContainerGap()
		                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		                    .addGroup(layout.createSequentialGroup()
		                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		                            .addComponent(saveField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
		                            .addComponent(saveName))
		                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                        .addComponent(optionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		                    .addGroup(layout.createSequentialGroup()
		                        .addComponent(okButton)
		                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                        .addComponent(cancelButton)))
		                .addContainerGap())
		        );
		
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
	// }}}
	
	public void exportTrees() {
	    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    //Determine PDF dimensions
         if (dims[0]!=0 && dims[1]!=0 && saveField.getText().length() > 0) {
			 if(!normalizeBox.isSelected())
           	 	frame.cycleColorsThroughMetadata(dims, prefix, unselectedColor);
			 else
			    frame.cycleColorsThroughMetadataNormalized(dims, prefix, unselectedColor);
		   dispose();
		}
		else
		{
		    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    JOptionPane.showMessageDialog(frame,
                "Please check options.\n"+
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
