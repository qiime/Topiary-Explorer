package topiaryexplorer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import javax.jnlp.*;
import java.io.*;

public class TreeEditToolbar extends JToolBar {
    TreeWindow frame = null;
    MainFrame mainframe = null;
    
    ButtonGroup layoutGroup = new ButtonGroup();
    JPanel layoutPanel = new JPanel();
    CollapsablePanel layoutCP = new CollapsablePanel("Layout", layoutPanel, true, false);
    JToggleButton rectButton = new JToggleButton(new ImageIcon("./src/images/rectangular.gif"),true);
    JToggleButton triButton = new JToggleButton(new ImageIcon("./src/images/triangular.gif"));
    JToggleButton radialButton = new JToggleButton(new ImageIcon("./src/images/radial.gif"));
    JToggleButton polarButton = new JToggleButton(new ImageIcon("./src/images/polar.gif"));
    JLabel treeStatus = new JLabel("");
    
    JToggleButton wedgeEditButton = new JToggleButton("Wedge Edit", new ImageIcon("./src/images/edit_wedge.gif"));
    WedgeEditPanel wedgeEditPanel;// = new WedgeEditPanel(frame);
    CollapsablePanel wedgeEditPanelCP; // = new CollapsablePanel("Wedge",wedgeEditPanel);
    
    NodeEditPanel nodeEditPanel;
    CollapsablePanel nodeEditPanelCP;
    
    
    JToggleButton nodeEditButton = new JToggleButton("Node Edit", new ImageIcon("./src/images/edit_node.gif"));
    
/*    JButton consensusButton = new JButton();*/
/*    JButton tipLabelsButton = new JButton();*/
/*    JButton collapseButton  = new JButton();
    JButton expandButton = new JButton();*/
    JToggleButton mirrorvertButton = new JToggleButton("Flip Vertical", new ImageIcon("./src/images/mirror_vert.gif"));
    JToggleButton mirrorhorzButton = new JToggleButton("Flip Horizontal", new ImageIcon("./src/images/mirror_horz.gif"));
    
	ExtendedService es;

    public TreeEditToolbar(TreeWindow _frame, MainFrame _mainframe) {
        super(JToolBar.VERTICAL);
        frame = _frame;
        mainframe = _mainframe;
        wedgeEditPanel = new WedgeEditPanel(frame);
        wedgeEditPanelCP = new CollapsablePanel("Wedge",wedgeEditPanel);
        
        nodeEditPanel = new NodeEditPanel(frame);
        nodeEditPanelCP = new CollapsablePanel("Node", nodeEditPanel);
        
        GroupLayout gLayout = new GroupLayout(this);
        setLayout(gLayout);
        
        gLayout.setAutoCreateGaps(true);
        gLayout.setAutoCreateContainerGaps(true);
        
        gLayout.setHorizontalGroup(gLayout.createSequentialGroup()
            .addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(layoutCP)
                .addComponent(wedgeEditPanelCP)
                .addComponent(nodeEditPanelCP)
                .addComponent(treeStatus))
        );
        
        gLayout.setVerticalGroup(gLayout.createSequentialGroup()
/*            .addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.BASELINE, false))*/
                .addComponent(layoutCP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(wedgeEditPanelCP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nodeEditPanelCP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(treeStatus, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        
        rectButton.setToolTipText("Rectangular");
        rectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Rectangular");
                frame.rectangularradiobutton.setSelected(true);
                frame.rotateSlider.setValue(0);
                frame.syncTreeWithRotateSlider();
                frame.rotateMenu.setEnabled(false);
            }
        });

        triButton.setToolTipText("Triangular");
        triButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.tree.setTreeLayout("Triangular");
                frame.triangularradiobutton.setSelected(true);
                frame.rotateSlider.setValue(0);
                frame.syncTreeWithRotateSlider();
                frame.rotateMenu.setEnabled(false);
            }
        });

        radialButton.setToolTipText("Radial");
        radialButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                treeStatus.setText("Calculating Offsets...");
                frame.tree.noLoop();
                frame.tree.setRadialOffsets(frame.tree.getTree());
                frame.tree.setTOffsets(frame.tree.getTree(), 0);
                frame.tree.setROffsets(frame.tree.getTree(), 0);
                frame.tree.loop();
                frame.tree.setTreeLayout("Radial");
                frame.radialradiobutton.setSelected(true);
                frame.rotateMenu.setEnabled(true);
                treeStatus.setText("Done drawing tree.");
            }
        });

        polarButton.setToolTipText("Polar");
        polarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int size = frame.tree.getTree().getNumberOfLeaves();
                if(size > 8000)
                {
                    if(JOptionPane.showConfirmDialog(null, 
                    "Viewing the polar layout of trees with more\n"
                    +"than 8000 nodes is not advised.",
                    "Warning",JOptionPane.OK_CANCEL_OPTION, 
                    JOptionPane.WARNING_MESSAGE) 
                    == JOptionPane.CANCEL_OPTION)
                        return;
                }
                treeStatus.setText("Calculating Offsets...");
                frame.tree.noLoop();
                frame.tree.setRadialOffsets(frame.tree.getTree());
                frame.tree.setTOffsets(frame.tree.getTree(), 0);
                frame.tree.setROffsets(frame.tree.getTree(), 0);
                frame.tree.loop();
                frame.tree.setTreeLayout("Polar");
                frame.polarradiobutton.setSelected(true);
                frame.rotateMenu.setEnabled(true);
                treeStatus.setText("Done drawing tree.");
            }
        });
        layoutGroup.add(rectButton);
        layoutGroup.add(triButton);
        layoutGroup.add(radialButton);
        layoutGroup.add(polarButton);
        
        layoutPanel.setLayout(new GridLayout(1,4));
        layoutPanel.add(rectButton);
        layoutPanel.add(triButton);
        layoutPanel.add(radialButton);
        layoutPanel.add(polarButton);
/*        add(layoutCP);*/

/*        add(consensusButton);*/
/*        add(tipLabelsButton);*/
        
        mirrorvertButton.setToolTipText("Flip Vertical");
        mirrorvertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorVert();
            }
        });
        
        mirrorhorzButton.setToolTipText("Flip Horizontal");
        mirrorhorzButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.mirrorHorz();
            }
        });
        add(mirrorvertButton);
        add(mirrorhorzButton);
        addSeparator();
        
        add(treeStatus);
        setFloatable(false);
    }
    
    public void setStatus(String s) {
        treeStatus.setText(s);
    }

}
