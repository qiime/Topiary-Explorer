package topiaryexplorer;

import java.awt.GridBagConstraints;
import java.awt.event.ComponentEvent;
import java.awt.event.*;
import java.awt.event.MouseWheelEvent;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel that holds the Processing-generated TreeVis applet and a pair of
 * scrollbars that are kept in sync with the position of the tree. This panel is
 * actually fairly generic and would work with any Processing applet that
 * provides changeEvents and a couple of accessors to get and set the current
 * view position.
 *
 * @author moss
 */
public class TreeAppletHolder extends JComponent {
	private TreeVis tree;
	private JScrollBar verticalScroll;
	private JScrollBar horizontalScroll;
	private TreeWindow frame;

	public TreeAppletHolder(TreeVis tree, TreeWindow frame) {
		this.tree = tree;
		this.frame = frame;

        tree.init();
		init();
	}

	private void init() {
		setLayout(new java.awt.GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(tree, gbc);

		verticalScroll = new JScrollBar(JScrollBar.VERTICAL);
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(verticalScroll, gbc);

		horizontalScroll = new JScrollBar(JScrollBar.HORIZONTAL);
		gbc = new GridBagConstraints();
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.BOTH;
		add(horizontalScroll, gbc);

		verticalScroll.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				tree.setVerticalScrollPosition(verticalScroll.getValue());
			}
		});

		horizontalScroll.getModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				tree.setHorizontalScrollPosition(horizontalScroll.getValue());
			}
		});

		// Re-paint the tree after resizing; otherwise it seems to end up blank
		// until some other action causes it to redraw:
		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				tree.checkBounds();
				tree.redraw();

                frame.collapseTreeToolbar.resetLayout();
				syncScrollbarsWithTree();
				//frame.updateZoomBounds();
				//frame.syncZoomSliderWithTree();
			}
		});

		tree.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				//frame.syncZoomSliderWithTree();
				syncScrollbarsWithTree();
			}
		});
		
        addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
               int notches = e.getWheelRotation();
               int scrollchange = 0;
               
               if (e.isShiftDown()) {
                    // horizontal scroll
                    //this accounts for different scrolling on different systems
                   if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                      scrollchange = tree.getCurrentHorizontalScrollPosition()+notches*(tree.getWidth()/10);
                   } else { //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
                       scrollchange = tree.getCurrentHorizontalScrollPosition()+notches*(tree.getWidth()*9/10);
                   }
               
                 // can't scroll past the bottom
                 if(scrollchange > tree.getMaxHorizontalScrollPosition()-tree.getWidth())
                     tree.setHorizontalScrollPosition(tree.getMaxHorizontalScrollPosition()-tree.getWidth());
                 // can't scroll past the top
                 else if(scrollchange < 0)
                     tree.setHorizontalScrollPosition(0);
                 else
                     tree.setHorizontalScrollPosition(scrollchange);   
                } else {
                    // vertical scroll
                   //this accounts for different scrolling on different systems
                   if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                      scrollchange = tree.getCurrentVerticalScrollPosition()+notches*(tree.getHeight()/10);
                   } else { //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
                       scrollchange = tree.getCurrentVerticalScrollPosition()+notches*(tree.getHeight()*9/10);
                   }
               
                 // can't scroll past the bottom
                 if(scrollchange > tree.getMaxVerticalScrollPosition()-tree.getHeight())
                     tree.setVerticalScrollPosition(tree.getMaxVerticalScrollPosition()-tree.getHeight());
                 // can't scroll past the top
                 else if(scrollchange < 0)
                     tree.setVerticalScrollPosition(0);
                 else
                     tree.setVerticalScrollPosition(scrollchange);               
                }
            syncScrollbarsWithTree();   
            }
        });
	}
	
	
	public void zoomScrollBars() {
	    int oldRatio = verticalScroll.getValue()/verticalScroll.getVisibleAmount();
	    verticalScroll.setValue(oldRatio*tree.getMaxVerticalScrollPosition());
	    verticalScroll.setVisibleAmount(tree.getHeight());
		verticalScroll.setBlockIncrement(tree.getHeight()*9/10);
		verticalScroll.setUnitIncrement(tree.getHeight()/10);
		
		oldRatio = horizontalScroll.getValue()/horizontalScroll.getVisibleAmount();
		horizontalScroll.setValue(oldRatio*tree.getMaxHorizontalScrollPosition());
		horizontalScroll.setVisibleAmount(tree.getWidth());
		horizontalScroll.setBlockIncrement(tree.getWidth()*9/10);
		horizontalScroll.setUnitIncrement(tree.getWidth()/10);
	}

	public void syncScrollbarsWithTree() {
		int newValue = tree.getCurrentVerticalScrollPosition();
		verticalScroll.setMaximum(tree.getMaxVerticalScrollPosition());
		verticalScroll.setValue(newValue);
		verticalScroll.setVisibleAmount(tree.getHeight());
		verticalScroll.setBlockIncrement(tree.getHeight()*9/10);
		verticalScroll.setUnitIncrement(tree.getHeight()/10);
		
		newValue = tree.getCurrentHorizontalScrollPosition();
		horizontalScroll.setMaximum(tree.getMaxHorizontalScrollPosition());
		horizontalScroll.setValue(newValue);
		horizontalScroll.setVisibleAmount(tree.getWidth());
		horizontalScroll.setBlockIncrement(tree.getWidth()*9/10);
		horizontalScroll.setUnitIncrement(tree.getWidth()/10);
		
		frame.collapseTreeToolbar.resetLayout();
	}
}
