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
    
    TreeViewPanel treeViewPanel;
    CollapsablePanel treeViewPanelCP;
    
    NodeEditPanel nodeEditPanel;
    CollapsablePanel nodeEditPanelCP;
    
    BranchEditPanel branchEditPanel;
    CollapsablePanel branchEditPanelCP;
    
    WedgeEditPanel wedgeEditPanel;// = new WedgeEditPanel(frame);
    CollapsablePanel wedgeEditPanelCP; // = new CollapsablePanel("Wedge",wedgeEditPanel);
    
    GroupLayout gLayout;
    
    JLabel treeStatus = new JLabel("");

    public TreeEditToolbar(TreeWindow _frame, MainFrame _mainframe) {
        super(JToolBar.VERTICAL);
        frame = _frame;
        mainframe = _mainframe;
        
        treeViewPanel = new TreeViewPanel(frame, this);
        treeViewPanelCP = new CollapsablePanel("Tree", treeViewPanel, true, false);
        
        nodeEditPanel = new NodeEditPanel(frame);
        nodeEditPanelCP = new CollapsablePanel("Node", nodeEditPanel);
        
        branchEditPanel = new BranchEditPanel(frame);
        branchEditPanelCP = new CollapsablePanel("Branch", branchEditPanel);
        
        wedgeEditPanel = new WedgeEditPanel(frame);
        wedgeEditPanelCP = new CollapsablePanel("Wedge",wedgeEditPanel);
        
        
        gLayout = new GroupLayout(this);
        setLayout(gLayout);
        
        gLayout.setAutoCreateGaps(true);
        gLayout.setAutoCreateContainerGaps(true);
        
        gLayout.setHorizontalGroup(gLayout.createSequentialGroup()
            .addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                .addComponent(treeViewPanelCP)
                .addComponent(nodeEditPanelCP)
                .addComponent(branchEditPanelCP)
                .addComponent(wedgeEditPanelCP)
                // .addComponent(treeStatus)
                )
        );
        
        gLayout.setVerticalGroup(gLayout.createSequentialGroup()
/*            .addGroup(gLayout.createParallelGroup(GroupLayout.Alignment.BASELINE, false))*/
                .addComponent(treeViewPanelCP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(nodeEditPanelCP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(branchEditPanelCP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(wedgeEditPanelCP, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                // .addComponent(treeStatus, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setFloatable(false);
    }
    
    public void setStatus(String s) {
/*        treeStatus.setText(s);*/
    }

}
