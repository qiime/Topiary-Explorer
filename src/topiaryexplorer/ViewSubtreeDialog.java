package topiaryexplorer;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.media.opengl.*;
import javax.swing.table.*;
import javax.jnlp.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import java.text.*;
import java.io.*;
import javax.imageio.ImageIO;

public class ViewSubtreeDialog extends JDialog {
    MainFrame frame = null;
    TreeWindow treeWindow = null;
    JButton cancelButton = new JButton("Cancel");
    JLabel levelLabel = new JLabel("levels above");
    NumberFormatter numFormat = new NumberFormatter(NumberFormat.getIntegerInstance());
    JFormattedTextField levelsTextField = new JFormattedTextField(numFormat);
    JLabel nodeLabel = new JLabel("Node matching name");
    JTextField nodeNameField = new JTextField();
    JButton okButton = new JButton("OK");
    // ImageIcon verifiedIcon = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("icon-green-checkmark.png")));
    ImageIcon verifiedIcon = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("icon-red-x.png")));
    JLabel verifyLabel = new JLabel(verifiedIcon);
    JLabel viewLabel = new JLabel("View subtree");
    Node matchedNode = null;
    
    public ViewSubtreeDialog(TreeWindow _treewindow) throws IOException{
        treeWindow = _treewindow;
        frame = treeWindow.frame;
        
        this.setTitle("View subtree");
        this.setSize(new Dimension(280,200));
        okButton.setEnabled(false);
        
        nodeNameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			public void changedUpdate(javax.swing.event.DocumentEvent evt) {
			    try {
				doSearch();
			    }
			    catch(IOException ex)
			    {}
			}

			public void insertUpdate(javax.swing.event.DocumentEvent evt) {
				try {
				doSearch();
			    }
			    catch(IOException ex)
			    {}
			}

			public void removeUpdate(javax.swing.event.DocumentEvent evt) {
				try {
				doSearch();
			    }
			    catch(IOException ex)
			    {}
			}
		});
		
		okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButtonPressed();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButtonPressed();
            }
        });
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(levelsTextField, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(levelLabel))
                    .addComponent(viewLabel)
                    .addComponent(nodeLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nodeNameField, GroupLayout.PREFERRED_SIZE, 143, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(verifyLabel)))
                .addContainerGap(75, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(87, Short.MAX_VALUE)
                .addComponent(cancelButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(okButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(viewLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(levelsTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(levelLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nodeLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(nodeNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(verifyLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton)))
        );
        
        this.show();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    public void doSearch() throws IOException{
			    okButton.setEnabled(false);
			    matchedNode = null;
			    verifiedIcon = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("icon-red-x.png")));
                verifiedIcon.getImage().flush();
				String str = nodeNameField.getText().toUpperCase();
				ArrayList<Node> nodes = treeWindow.tree.getTree().getNodes();
				for (Node n : nodes) {
					if(str.length() > 0 && n.getLabel().trim().toUpperCase().equals(str))
					{
					    verifiedIcon = new ImageIcon(ImageIO.read(this.getClass().getClassLoader().getResource("icon-green-checkmark.png")));
                        verifiedIcon.getImage().flush();
						okButton.setEnabled(true);
						matchedNode = n;
					}
				}
				
				verifyLabel.setIcon(verifiedIcon);
				this.repaint();
			}
    
    public void okButtonPressed() {
        int l = ((Number)levelsTextField.getValue()).intValue();
        Node root = matchedNode;
        
        for(int i = 0; i < l; i++)
        {
            if(root.getParent() != null)
                root = root.getParent();
            else
                break;
        }
        
        frame.newTreeWindow(TopiaryFunctions.createNewickStringFromTree(root));
        this.dispose();
    }
    
    public void cancelButtonPressed() {
        this.dispose();
    }
}