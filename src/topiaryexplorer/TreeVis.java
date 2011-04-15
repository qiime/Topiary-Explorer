package topiaryexplorer;

import processing.core.*;
import processing.pdf.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.Color;
import java.awt.Cursor;
import javax.jnlp.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.Polygon;

public class TreeVis extends PApplet {

    //the amount of space around the tree, for viewing purposes
    private double MARGIN = 5;
    private double TREEMARGIN = 5;

    private final int SELECTED_COLOR = 0xff66CCFF;
    private final int HIGHLIGHTED_COLOR = 0xffFF66FF;
    
    private Color backgroundColor = new Color(255,255,255);

    // Pixels per unit of branch length:
    private double xscale;
    // Pixels per branch
    private double yscale;
    
    private double treerotation = 0;
    
    //scaling for the line width
    private double lineWidthScale = 1;

    //the tree that is currently being displayed
    private Node root;

    private double xstart = MARGIN;
    private double ystart = MARGIN;

    private double oldwidth =  0;
    private double oldheight = 0;
    private double rootdepth = 0;
    
    //the tree layout
    private String treeLayout = "Rectangular";

    //should the labels be drawn or not?
    private boolean drawExternalNodeLabels = false;
    private boolean drawInternalNodeLabels = false;
    //is a label being dragged?
    private boolean draggingLabel = false;
    private float collapsedPixel = 10000000;
    private double collapsedLevel = 0;
    
    private boolean zoomDrawNodeLabels = false;
    private boolean majorityColoring = true;
    private boolean mirrored = false;
    private boolean colorBranches = false;

    private Node selectedNode;
    private Node mouseOverNode;
    private Node mouseOverNodeToReplace; //used when dragging nodes around
    private Set hilightedNodes = new java.util.HashSet();

    private List listeners = new java.util.ArrayList();
    private float wedgeFontSize = 12;
    private int wedgeFontColor = 255;
    private String wFont = "SansSerif";
    private PFont wedgeFont = createFont(wFont, (int)wedgeFontSize);

    private TreeWindow frame = null;

    private int labelXOffset = 0;
    private int labelYOffset = 0;
    private double wedgeHeightScale = 1;
    private boolean drawWedgeLabels = true;
    private boolean drawNodeLabels = false;
    private float nodeFontSize = 10;
    private int nodeFontColor = 0;
    private String nFont = "SansSerif";
    private PFont nodeFont = createFont(nFont, (int)nodeFontSize);

    /**
     * setup() is called once to initialize the applet
     */
    public void setup() {
        size(800, 600);
        smooth();
        //set up default font
        textFont(nodeFont);
        oldwidth = width;
        oldheight = height;
    }

    /**
     * draw() is called whenever the tree needs to be re-drawn.
     * @see #redraw()
     */
    public void draw() {
      //color over the existing graphics with a white background
      background(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
      //has the size changed?
      if (oldwidth != width || oldheight != height) {
          oldwidth = width;
          oldheight = height;
          fireStateChanged();
      }
      //draw the tree
      try {
         g.pushMatrix();
         g.translate((float)xstart, (float)ystart);
         g.rotate((float)(treerotation*Math.PI/180.0));
         g.translate((float)-xstart, (float)-ystart);
         drawTree(root);
         g.popMatrix();
      } catch (Exception e) {
/*          frame.treeOpsToolbar.setStatus("WARNING: Error drawing tree, probably due to concurrency issues. Normally, this warning can be ignored.");*/
/*          frame.consoleWindow.update("WARNING: Error drawing tree, probably due to concurrency issues. Normally, this warning can be ignored.");*/
          e.printStackTrace();
      }
    }


    //GETTERS AND SETTERS
    public void setParent(TreeWindow f) { frame = f; }
    public double getMargin() { return MARGIN; }
    public double getTreeMargin() { return TREEMARGIN; }
    public double getYScale() { return yscale; }
    public double getXScale() { return xscale; }
    public double getYStart() { return ystart; }
    public double getXStart() { return xstart; }
    public Color getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Color c) { backgroundColor = c; }    
    public String getTreeLayout() { return treeLayout; }
    public Node getTree() { return root; }
    public Node getSelectedNode() { return selectedNode; }
    public void setSelectedNode(Node node) { selectedNode = node; }
    public Set getHilightedNodes() { return hilightedNodes; }
    public boolean getDrawExternalNodeLabels() { return drawExternalNodeLabels; }
    public boolean getDrawInternalNodeLabels() { return drawInternalNodeLabels; }
    public void setDrawExternalNodeLabels(boolean b) { drawExternalNodeLabels = b; }
    public void setDrawInternalNodeLabels(boolean b) { drawInternalNodeLabels = b; }
    public void setCollapsedPixel(float pixel) { collapsedPixel = pixel; }
    public float getCollapsedPixel() { return collapsedPixel; }
    public void setCollapsedLevel(double level) { collapsedLevel = level; }
    public double getCollapsedLevel(){ return collapsedLevel; }
    public double getLineWidthScale() { return lineWidthScale; }
    public void setLineWidthScale(double f) { lineWidthScale = f; }
    public void setMajorityColoring(boolean cond) { majorityColoring = cond; }
    public boolean getMajorityColoring() { return majorityColoring; }
    public void setLabelXOffset(int i) { labelXOffset = i; redraw(); }
    public int getLabelXOffset() { return labelXOffset; }
    public void setLabelYOffset(int i) { labelYOffset = i; redraw();}
    public int getLabelYOffset() { return labelYOffset; }
    public void setWedgeHeight(double d) { wedgeHeightScale = d; }
    public double getWedgeHeight() { return wedgeHeightScale; }
    public void setWedgeFontFace(String s) { wFont = s; 
        wedgeFont = createFont(wFont, (int)wedgeFontSize);}
    public void setWedgeFontSize(float d) { 
        if(d>0) { 
            wedgeFontSize = d; 
            wedgeFont = createFont(wFont, (int)wedgeFontSize);
            redraw();
            } 
    }
    public float getWedgeFontSize() { return wedgeFontSize; }
    public void setWedgeFontColor(int c) { wedgeFontColor = c; redraw();}
    public boolean getMirrored() { return mirrored; }
    public void setMirrored(boolean b) { mirrored = b; redraw(); }
    public void setDrawWedgeLabels(boolean b) { drawWedgeLabels = b; redraw(); }
    public void setDrawNodeLabels(boolean b) { drawNodeLabels = b; redraw(); }
    public void setNodeFontFace(String s) { nFont = s; 
        nodeFont = createFont(nFont, (int)nodeFontSize);
        }
    public void setNodeFontSize(float d) { 
        if(d>0){
            nodeFontSize = d;
            nodeFont = createFont(nFont, (int)nodeFontSize);
            redraw();
            } 
    }
    public float getNodeFontSize() { return nodeFontSize; }
    public void setNodeFontColor(int c) { nodeFontColor = c; }
    public void setColorBranches(boolean b) { colorBranches = b; }
    public boolean getColorBranches() { return colorBranches; }
    public boolean getZoomDrawNodeLabels() { return zoomDrawNodeLabels; }

    //SCROLLBAR METHODS
    public int getCurrentVerticalScrollPosition() {
      if (root==null) return 0;
      if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
      	return (int) (root.depth()*yscale - ystart);
      }
      return (int) -(ystart-MARGIN);
    }
    public void setVerticalScrollPosition(int value) {
      ystart = -value + MARGIN;
      if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
      	ystart =  root.depth()*yscale - value;
      }
      fireStateChanged();
      redraw();
    }
    public int getCurrentHorizontalScrollPosition() {
      if (root==null) return 0;
      if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
      	return (int) (root.depth()*xscale - xstart);
      }
      return (int) -(xstart-MARGIN);
    }
    public void setHorizontalScrollPosition(int value) {
      xstart = -value + MARGIN;
      if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
      	xstart =  root.depth()*xscale - value;
      }
      fireStateChanged();
      redraw();
    }
    public int getMaxVerticalScrollPosition() {
      if (root==null) return 0;
      if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
        return (int) (root.getNumberOfLeaves()*yscale + 2*MARGIN);
      } else {
        return (int) (2*root.depth()*yscale+1);
      }
    }
    public int getMaxHorizontalScrollPosition() {
      if (root==null) return 0;
      if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
        return (int) (root.depth()*xscale + MARGIN + TREEMARGIN);
      } else {
        return (int) (2*root.depth()*xscale+1);
      }      
    }
    
    public void setRotate(double value) {
    	treerotation = value;
    }

    //METHODS FOR TREE LISTENERS
    public void addChangeListener(ChangeListener l) { listeners.add(l); }
    public void removeChangeListener(ChangeListener l) { listeners.remove(l); }

    /**
     * Notifies all the listeners that the tree's state has changed.
     */
    private void fireStateChanged() {
      if (!listeners.isEmpty()) {
        ChangeEvent evt = new ChangeEvent(this);
        for (int i=0; i < listeners.size(); i++) {
          ((ChangeListener) listeners.get(i)).stateChanged(evt);
        }
      }
    }
    
    public void keyReleased() {
        if (!(keyPressed && keyCode == SHIFT)) {
            draggingLabel = false;
            mouseOverNode = findNode(mouseX, mouseY);
            mouseOverNodeToReplace = null;
            redraw();
            if (mouseOverNode == null) {
                cursor(ARROW);
            }
        }
    }
    
    public void keyPressed() {
        Node n = this.selectedNode;
        if (!draggingLabel && n!=null && !n.isLeaf() && key!=CODED && key!=BACKSPACE && key!=TAB &&
            key!=ENTER && key!=RETURN && key!= ESC && key!=DELETE) {
            n.setLabel(n.getLabel()+key);
            redraw();
        } else if (!draggingLabel && n!=null && !n.isLeaf() && key==BACKSPACE) {
            if (n.getLabel().length() > 0){
                n.setLabel(n.getLabel().substring(0,n.getLabel().length()-1));
                redraw();
            }
        }
    }

    //COORDINATE CONVERSTION METHODS


    /**
     * Convert from branch-length/row to screen coords based on scaling and translation
     */
    private double toScreenX(double l) {
        return xstart + xscale*l;
    }
    private double toScreenY(double row) {
        return ystart + yscale*row;
    }
    

    /**
     * Convert from screen coords to branch-length/row based on scaling and translation
     */
    private double toLength(double xs) {
      if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
        xs = xs - getWidth()*0.5;
      }
      return (xs - xstart)/xscale;
    }
    private double toRow(double ys) {
      if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
        ys = ys - getHeight()*0.5;
      }    
      return (ys - ystart)/(yscale);
    }


    //MOUSE LISTENER METHODS

    /**
     * mouseDragged() is called whenever the user drags the mouse
     */
    public void mouseDragged() {
      if (this.mouseOverNode == null) {
          //change the cursor to show that dragging is taking place
          cursor(MOVE);
          //difference in mouse position since mousePressed:
          double bxdiff = mouseX-pmouseX;
          double bydiff = mouseY-pmouseY;
          //update where to draw the tree based on how the tree was dragged
          xstart = xstart + bxdiff;
          ystart = ystart + bydiff;
          //ensure that the tree wasn't dragged beyond allowable bounds
          checkBounds();
          //notify listeners
          fireStateChanged();
          //re-draw the tree
          redraw();
      } else {
          //is the SHIFT key down?
          //also, CAN ONLY DRAG INTERNAL NODES
          if (keyPressed == true && keyCode == SHIFT && !mouseOverNode.isLeaf()) {
              draggingLabel = true;
              //find the node where the mouse is
              Node n = findNode(mouseX, mouseY);
              if (n == null) {
                  mouseOverNodeToReplace = null;
              } else {
                  //can't replace a leaf!
                  if (!n.isLeaf()) {
                      this.mouseOverNodeToReplace = n;
                  }
              }
              redraw();
          } else {
              this.mouseOverNode = null;
            this.mouseOverNodeToReplace = null;
          }
      }
    }

    public void mouseReleased() {
/*      Node node = findNode(mouseX, mouseY);*/
      mouseOverNodeToReplace = findNode(mouseX, mouseY);
/*      mouseOverNode = node;*/
      cursor(ARROW);
      if (draggingLabel && mouseOverNodeToReplace != null) {
          //replace node label
          String s = mouseOverNode.getLabel();
          if(s.length() == 0)
            s = mouseOverNode.getName();
            
          mouseOverNodeToReplace.setLabel(s);
          mouseOverNode.setLabel("");
          selectedNode = mouseOverNodeToReplace;
      }
      draggingLabel = false;
      mouseOverNode = findNode(mouseX, mouseY);
      mouseOverNodeToReplace = null;
      redraw();
    }

    /**
     * mouseMoved() is called whenever the mouse is moved.
     */
    public void mouseMoved() {
        if(!frame.isActive())
                   return;
             //is the mouse over a node?
             Node node = findNode(mouseX, mouseY);
             if (node != null) {
               //if so, chance the cursor's hand
               cursor(HAND);
               //outline the node
               if ((node.isLeaf() && this.drawExternalNodeLabels) ||(!node.isLeaf() && this.drawInternalNodeLabels)) {
                   mouseOverNode = node;
               }
             }
             else {
               //cursor is normal
               cursor(ARROW);
               //set outlined node to nothing
               mouseOverNode = null;
             }
             mouseOverNodeToReplace = null;
             redraw();
    }

    /**
     * mousePressed() is called whenever the mouse is pressed.
     */
    public void mousePressed() {
      //did the user click on a node?
      Node node = findNode(mouseX, mouseY);
      if (node != null) {
        if (mouseButton == LEFT) {
          if (mouseEvent.getClickCount() == 1) {
            //if they single-left-clicked on a node, select it
            selectedNode = node;
          } else if (mouseEvent.getClickCount() == 2 && !treeLayout.equals("Polar")) {
            //if they double-left-clicked on a node, collapse it
            node.setCollapsed(!node.isCollapsed());
          }
        }

      } else {
        //they clicked, but not on a node
        if (mouseEvent.getClickCount() == 1) {
          selectedNode = null;
        }
      }
      redraw();
    }

    /**
     * Ensures that the tree is not scrolled out of its bounds, and resets it back if it is
     */
    public void checkBounds() {
    	
      //if there's no tree, we can't check the bounds
      if (root==null) return;
      
      float width = 0;
      String s = root.getLongestLabel();
      for (int i = 0; i < s.length(); i++) {
          width += nodeFont.width(s.charAt(i));
      }
      TREEMARGIN = MARGIN + width*nodeFont.size + 10;

      if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
          //check horizontal tree scaling
          if (xscale < (getWidth()-MARGIN-TREEMARGIN)/root.depth()) {
            //need to rescale tree
            resetTreeX();
          }
          //check horizontal tree position
          if (xstart > MARGIN) {
            xstart = MARGIN;
          } else if (xstart + xscale*root.depth() < getWidth()-TREEMARGIN) {
            xstart = getWidth()-MARGIN - xscale*root.depth();
          }
    
          //check vertical tree scaling
          if (yscale < (getHeight()-2*MARGIN)/root.getNumberOfLeaves()) {
            //need to rescale tree
            resetTreeY();
          }
          //check vertical tree position
          if (ystart > MARGIN) {
            ystart = MARGIN;
          } else if (ystart + yscale*root.getNumberOfLeaves() < getHeight()-MARGIN) {
            ystart = getHeight()-MARGIN -yscale*root.getNumberOfLeaves();
          }
      } else if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
          //check horizontal tree scaling
          if (xscale < (Math.min(getWidth(), getHeight())*0.5-MARGIN)/root.depth()) {
            //need to rescale tree
            resetTreeX();
          }

          //check vertical tree scaling
          if (yscale < (Math.min(getWidth(), getHeight())*0.5-MARGIN)/root.depth()) {
            //need to rescale tree
            resetTreeY();
          }
   
      }

      //notify listeners
      fireStateChanged();
      //redraw tree
      redraw();
    }

    /**
     * Sets the tree layout
     */
     public void setTreeLayout(String layout) {
        treeLayout = layout;
        resetTreeX();
        resetTreeY();
        
        fireStateChanged();
        redraw();
     }

    /**
     * Resets the scale and position of the tree horizontally
     */
    public void resetTreeX() {
      if (root==null) return;
      if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
          xscale = (getWidth()-MARGIN-TREEMARGIN)/root.depth();
/*            xscale = (Math.min(getWidth(), getHeight())*0.5-MARGIN)/root.depth();*/
          xstart = MARGIN;
      } else if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
          xscale = (Math.min(getWidth(), getHeight())*0.5-MARGIN)/root.depth();
          xstart = getWidth()*0.5;
      }
     }

    /**
     * Resets the scale and position of the tree vertically
     */
    public void resetTreeY() {
      if (root==null) return;
      if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
          yscale = (getHeight()-2*MARGIN)/root.getNumberOfLeaves();
/*          ystart = getHeight()*0.5;*/
          ystart = MARGIN;
      } else if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
          yscale = (Math.min(getWidth(), getHeight())*0.5-MARGIN)/root.depth();
          ystart = getHeight()*0.5;
      }
    }
    
    public void resetTree() {
        setTree(root);
    }

    /**
     * Replace the entire tree, recalculating cached values
     *
     * @param  newRoot  the Node object that is the root of the new tree
     */
    public void setTree(Node newRoot) {      
      
      //add to the margin the longest node labels
      float width = 0;
      String s = newRoot.getLongestLabel();
      for (int i = 0; i < s.length(); i++) {
        width += nodeFont.width(s.charAt(i));
      }
      TREEMARGIN = MARGIN + width*nodeFont.size + 10;
      //set the tree
      root = newRoot;
      //recalculate the x- and y-offsets of the nodes in the tree
      setYOffsets(newRoot, 0);
      setXOffsets(newRoot, 0);
      rootdepth = root.depth();

      resetTreeX();
      resetTreeY();
      
      //notify listeners
      fireStateChanged();
      //redraw the tree
      redraw();          
    }

    public void setVerticalScaleFactor(double yvalue) {
        yscale = yvalue;
/*        double oldYstart = ystart;*/
/*        setVerticalScrollPosition(getMaxVerticalScrollPosition()/2);*/
/*        checkBounds();*/
        fireStateChanged();
/*        double r = toRow(y);*/
/*        ystart = -(yscale*MARGIN);*/
        redraw();
    }
    
    public void setHorizontalScaleFactor(double xvalue) {
        xscale = xvalue;
/*        setHorizontalScrollPosition(getMaxHorizontalScrollPosition()/2);*/
/*        checkBounds();*/
        fireStateChanged();
/*        xstart = -(xscale*MARGIN);*/
        redraw();
    }


    /**
     * Rescales the tree, keeping the point (x, y) in screen coords at the same position relative
     * to the tree.  Note that rescaling the tree only has an effect on the vertical scale.
     *
     * @param  xvalue  the new xscale value
     * @param  yvalue  the new yscale value
     * @param  x  the x position to keep the same while the tree is scaled
     * @param  y  the y position to keep the same while the tree is scaled
     */
    public void setScaleFactor(double xvalue, double yvalue, double x, double y) {
      //convert from screen position to position in the tree
      double l = toLength(x);
      double r = toRow(y);
      yscale = yvalue;
      xscale = xvalue;

      //set to new values based on new scaling

/*      xstart = xstart - (toScreenX(l) - x);*/
/*      ystart = ystart - (toScreenY(r) - y);*/
      setVerticalScrollPosition(getMaxVerticalScrollPosition()/2);
      setHorizontalScrollPosition(getMaxHorizontalScrollPosition()/2);
      checkBounds();
      fireStateChanged();
      redraw();
    }

    /**
     * Calls findNode(Node,double,double) on the root of the tree
     *
     * @param  x  the x-value to search for the node at
     * @param  y  the y-value to search for the node at
     * @see #findNode(Node,double,double)
     */
    public Node findNode(double x, double y) {
      return findNode(root, mouseX, mouseY);
    }

    /**
     * Find the node in the tree _tree_ at the given x and y coordinates.
     *
     * @param  tree  the root of the tree to search in
     * @param  x  the x-value to search for the node at
     * @param  y  the y-value to search for the node at
     * @param  _length  an internal parameter used in recusion; set to 0 when calling
     */
    private Node findNode(Node tree, double x, double y) {
      if (tree==null) return null;
      if (tree.isHidden()) return null;

      double row = 0;
      double col = 0;
      if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
          //get the y-offset of the root of the tree
          row = tree.getYOffset();
          col = tree.getXOffset();
      } else if (treeLayout.equals("Radial")) {
          row = tree.getRYOffset();
          col = tree.getRXOffset();
      } else if (treeLayout.equals("Polar")) {
          row = tree.getROffset() * Math.sin(tree.getTOffset());
          col = tree.getROffset() * Math.cos(tree.getTOffset());
      }
      //get the x and y coordinates of the current node
      double nodeX = toScreenX(col);
      double nodeY = toScreenY(row);

      double minX = nodeX;
      double maxX = minX+5;
      double minY = nodeY;
      double maxY = minY+5;
      //if node is collapsed, whole wedge is viable
      if(tree.isCollapsed())
      {
          double shortest = toScreenX(tree.shortestRootToTipDistance() - tree.getBranchLength());
          double longest = toScreenX(tree.longestRootToTipDistance() - tree.getBranchLength());
            
          if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
              
          	  double top = toScreenY(tree.getMaximumYOffset())-nodeY;
              top = (top/2)*wedgeHeightScale;
              double bottom = nodeY-toScreenY(tree.getMinimumYOffset());
              bottom = (bottom/2)*wedgeHeightScale;
          	  maxY = nodeY + top;
              minY = nodeY - bottom;
          
              int[] xs = new int[]{(int)Math.floor(nodeX), (int)Math.floor(nodeX), (int)Math.ceil(nodeX+shortest), (int)Math.ceil(nodeX+longest)};
              int[] ys = new int[]{(int)Math.floor(minY), (int)Math.ceil(maxY), (int)Math.ceil(maxY), (int)Math.floor(minY)};
          
              Polygon poly = new Polygon(xs,ys,4);
          
              if(poly.contains(x,y))
                {
                    return tree;
                }
                else
                    return null;
            }
            else if (treeLayout.equals("Radial") || treeLayout.equals("Polar"))
            {
                double maxt = tree.getMaximumTOffset();
                double mint = tree.getMinimumTOffset();

                if(wedgeHeightScale < 1)
                {
                    double theta = Math.abs(maxt-mint);
                    double f = (theta*(1-wedgeHeightScale))/2;
                    mint = mint + f;
                    maxt = maxt - f;
                }

                double x1 = tree.getParent().getRXOffset() + shortest * Math.cos(mint);
                double y1 = tree.getParent().getRYOffset() + shortest * Math.sin(mint);
                double x2 = tree.getParent().getRXOffset() + longest * Math.cos(maxt);
                double y2 = tree.getParent().getRYOffset() + longest * Math.sin(maxt);

                  int[] xs = new int[]{(int)nodeX, (int)toScreenX(x1), (int)toScreenX(x2)};
                  int[] ys = new int[]{(int)nodeY, (int)toScreenY(y1), (int)toScreenY(y2)};
                  Polygon poly = new Polygon(xs,ys,3);

                  if(poly.contains(x,y))
                      {
                          return tree;
                      }
                      else
                          return null;
            }
      }
      
      double width = 0;
      String s = tree.getLabel();
      for (int i = 0; i < s.length(); i++) {
        width += nodeFont.width(s.charAt(i));
      }
      maxX =  nodeX + 5 + (width*nodeFont.size);
      minY = nodeY - (nodeFont.descent()*nodeFont.size);
      maxY = nodeY + (nodeFont.ascent()*nodeFont.size);
      if ((tree.isLeaf() && !this.drawExternalNodeLabels) || (!tree.isLeaf() && !this.drawInternalNodeLabels)) {
          maxX = minX + 5;
          maxY = minY + 5;
          minX = minX - 5;
          minY = minY - 5;
      }
      
      //if the current node is within TOLERANCE pixels, return this node
      if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
        return tree;
      }

/*      //if the tree is collapsed, don't search it
      if (tree.isCollapsed()) return null;*/

      //search the root's children
      for (int i = 0; i < tree.nodes.size(); i++) {
        Node child = tree.nodes.get(i);
        //is it in this child?
        Node found = findNode(child, x, y);
        if (found != null) return found;
      }
      //didn't find any nodes
      return null;
    }


    /**
     * Draws the tree to the screen.  Uses the PApplet.g handle to the screen to draw to it.
     *
     * @param  node  the root the tree to draw to the screen
     * @param  x  the x-position to draw the tree at
     */
    private void drawTree(Node node) {
      g.textFont(nodeFont);
      if (yscale > nodeFontSize)
      {
        zoomDrawNodeLabels = true;
      }
      else
        zoomDrawNodeLabels = false;
      drawTree(node, g);
    }
    
   
    /**
     * Draw the entire sub-tree rooted at _node_, with _node_ positioned at the given (absolute, unscaled) coords.
     * Draws to the specified canvas.  beginDraw() and endDraw() should be called outside of this function if drawing
     * to a PGraphics objet.
     *
     * @param  node  the root of the tree to draw to the screen
     * @param  x  the x-position to draw the tree at
     * @param  canvas  a handle to the PGraphics object that should be drawn to.
     * @see  drawTree(Node, double)
     */
    private void drawTree(Node node, PGraphics canvas) {
      if (root==null) return;
      
      if(node.isHidden()) return;
      
      boolean isInternal = !node.isLeaf();

      if(node.depth()/rootdepth <= collapsedLevel && node.getParent() != null)
        {
            node.setSliderCollapsed(true);
        }
        else
            node.setSliderCollapsed(false);

      // Draw the branches first, so they get over-written by the nodes later
      if (isInternal) {
        if (node.isCollapsed()){
          //if it's an internal, collapsed node, then draw a wedge in its place
          drawWedge(node, canvas);
        }
        else {
          //if it's an internal, uncollapsed node, draw branches of the tree
          drawBranches(node, canvas);
        }
      }


      //if it's internal and not collapsed, draw all the subtrees
      if (isInternal && !node.isCollapsed()) {
        for (int i=0; i < node.nodes.size(); i++) {
          Node child = node.nodes.get(i);
          drawTree(child, canvas);
        }
      }

      //draw the current node over the branches
      drawNode(node, canvas);

      //draw the pie chart?
      if (node.getDrawPie()) {
          double x = node.getXOffset();
          double y = node.getYOffset();
          if (treeLayout.equals("Radial")) {
                x = node.getRXOffset();
                y = node.getRYOffset();
            } else if (treeLayout.equals("Polar")) {
                x = node.getROffset() * Math.cos(node.getTOffset());
                y = node.getROffset() * Math.sin(node.getTOffset());
            }
        PieChart pie = new PieChart((int)Math.round(toScreenX(x)), //draw at the same x-value as the node
          (int)Math.round(toScreenY(y)), //draw at the y-value of the node
          75, //diameter of the pie chart
          node.getGroupBranchFraction(), //the weighting of the node's colors
          node.getGroupBranchColor()); //the colors for the node
        drawPieChart(pie);
      }
    }

   private void drawPieChart(PieChart pie) {
    //get the total count
    double total = 0;
    for (int i = 0; i < pie.data.size(); i++) {
      total += pie.data.get(i);
    }
    //generate percentages
    double[] percents = new double[pie.data.size()];
    for (int i = 0; i < percents.length; i++) {
      percents[i] = pie.data.get(i)/total;
    }
    //generate angles
    double[] angles = new double[pie.data.size()];
    for (int i = 0; i < angles.length; i++) {
      angles[i] = 360*percents[i];
    }

    //draw it!
    double lastAng = 0;
    stroke(0);
    strokeWeight(2);
    for (int i = 0; i < angles.length; i++) {
      fill(pie.colors.get(i).getRGB(), 175);
      arc(pie.x, pie.y,(float) pie.diameter, (float)pie.diameter, (float)lastAng, (float)(lastAng+radians((float)angles[i])));
      double midangle = lastAng + radians((float)angles[i])/2.0;
      //fill(0,255,0);
      //text(str(percents[i]), x+(cos(midangle)*diameter*0.25), y+(sin(midangle)*diameter*0.25));
      lastAng  = lastAng + radians((float)angles[i]);
    }
  }

    /**
     * Draw the actual node, not including branch lines and child nodes.
     *
     * @param  node  the node to draw
     * @param  x  the x value to draw the node at
     * @param  y  the y value to draw the node at
     * @param  canvas  the PGraphics object to draw the node to
     */
    private void drawNode(Node node, PGraphics canvas) {
      //biases are small offsets so the text doesn't overlap the graphics
      double offsetbias = 5;
      
      String s = node.getLabel();
       if(s.length() == 0)
           s = node.getName();
      
      double x = node.getXOffset();
      double y = node.getYOffset();
      if (treeLayout.equals("Radial")) {
          x = node.getRXOffset();
          y = node.getRYOffset();
      } else if (treeLayout.equals("Polar")) {
          x = node.getROffset() * Math.cos(node.getTOffset());
          y = node.getROffset() * Math.sin(node.getTOffset());
      }
      
      //convert x and y values to actual screen values
      double xs = toScreenX(x);
      double ys = toScreenY(y);

      //is this node selected or hilighted?
      boolean selected = (node == selectedNode);
      boolean hilighted = hilightedNodes.contains(node);

      float drawX = (float)xs;
      float drawY = (float)ys;
      if (draggingLabel && this.mouseOverNode == node) {
          //this node's label is being dragged:
          // change drawing coords to be wherever the mouse is
          drawX = mouseX;
          drawY = mouseY;
          
          canvas.text(s, (float)(drawX+offsetbias), (float)(drawY));
      }
      
      if (selected || hilighted) {
        int c;
        if (selected) { c = SELECTED_COLOR; } else { c = HIGHLIGHTED_COLOR; }
        canvas.fill(c);
        canvas.stroke(c);

        //draw the selection/highlighting
        double minX = drawX+offsetbias-1;
        double width = 0;
        
        double maxX =  drawX + 5 + (width*nodeFont.size);
        double minY = drawY - (nodeFont.descent()*nodeFont.size);
        double maxY = drawY + (nodeFont.ascent()*nodeFont.size);
        if ((node.isLeaf() && !drawExternalNodeLabels) || (!node.isLeaf() && !drawInternalNodeLabels)) {
                    maxX = minX + 5;
                    maxY = minY + 5;
                    minX = minX - 5;
                    minY = minY - 5;
                }
        canvas.rect((float)minX, (float)minY, (float)(maxX-minX), (float)(maxY-minY));

      }
      
      if (mouseOverNode == node || mouseOverNodeToReplace == node) {
        //outline the node
        canvas.strokeWeight(1);
        canvas.stroke(255,0,0);
        double minX = drawX-3;
        double width = 0;
        
        double maxX =  drawX + 5 + (width*nodeFont.size)+3;
        double minY = drawY - (nodeFont.descent()*nodeFont.size)-3;
        double maxY = drawY + (nodeFont.ascent()*nodeFont.size)+3;
        canvas.line((float)minX, (float)minY, (float)minX, (float)maxY);
        canvas.line((float)minX, (float)maxY, (float)maxX, (float)maxY);
        canvas.line((float)maxX, (float)maxY, (float)maxX, (float)minY);
        canvas.line((float)maxX, (float)minY, (float)minX, (float)minY);
        canvas.stroke(0);
      }

      //set the color/weight to draw
      canvas.strokeWeight((float).2);
        Color c = null;
          if(!colorBranches)
          {
            canvas.stroke(0);
            canvas.fill(0);
          }
          else
          {
              c = node.getBranchColor(majorityColoring);
              canvas.stroke(c.getRGB());
              canvas.fill(c.getRGB());
          }
      
      //draw node label if we need to
       double minX = drawX+offsetbias-1;
       double width = 0;
       
         for (int i = 0; i < s.length(); i++) {
           width += nodeFont.width(s.charAt(i));
         }
       
      if (treeLayout.equals("Polar") || treeLayout.equals("Radial")) {
        canvas.pushMatrix();
        double rotation = 0;
        if (treeLayout.equals("Radial")) {
            rotation = Math.atan2(yscale*node.getRYOffset(), xscale*node.getRXOffset());
            double xt = node.getRXOffset();
            double yt = node.getRYOffset();
            drawX = (float)Math.sqrt((xscale*xt)*(xscale*xt)+(yscale*yt)*(yscale*yt));
        } else {
            rotation = node.getTOffset();
            double xt = node.getROffset()*Math.cos(rotation);
            double yt = node.getROffset()*Math.sin(rotation);
            rotation = Math.atan2(yscale*yt, xscale*xt);
            drawX = (float)Math.sqrt((xscale*xt)*(xscale*xt)+(yscale*yt)*(yscale*yt));
        }
        
        //make sure rotation is positive
        double fullrotation = rotation + treerotation*Math.PI/180.0;
        if (fullrotation < 0) {
            fullrotation = fullrotation + 2*Math.PI;
        }
        if (fullrotation > 2*Math.PI) {
        	fullrotation = fullrotation - 2*Math.PI;
        }
        if (rotation < 0) {
            rotation = rotation + 2*Math.PI;
        }
        if (rotation > 2*Math.PI) {
        	rotation = fullrotation - 2*Math.PI;
        }
        //draw all text rightside-up
        if (fullrotation > Math.PI/2 && fullrotation < 3*Math.PI/2) {
            //add 180 degrees
            rotation = rotation + Math.PI;
            //draw on other size
            drawX = -drawX;
            //subtract width of the text
            drawX = drawX - textWidth(node.getLabel()) - 2*(float)offsetbias;
        }
      
        drawY = (float)0;
        canvas.translate((float)xstart, (float)ystart);
        canvas.rotate((float)rotation);
      }
      
     double maxX =  drawX + 5 + (width*nodeFont.size);
     double minY = drawY - (nodeFont.descent()*nodeFont.size);
     double maxY = drawY + (nodeFont.ascent()*nodeFont.size);
     
     if (node.isLeaf() && node.getDrawLabel() && drawNodeLabels && zoomDrawNodeLabels) {           
         Color lc = node.getLabelColor(majorityColoring);
           if(lc == null)
           {
             canvas.stroke(0);
             canvas.fill(0);
           }
           else
           {
               canvas.fill(lc.getRGB());
               canvas.stroke(lc.getRGB());
           }
/*           if (yscale > nodeFontSize) */
               canvas.text(s, (float)(drawX+offsetbias), (float)(drawY));
     }
     
/*     canvas.fill(255);
     canvas.noStroke();*/
     
      //reset drawing color to default black
      canvas.fill(0);
      canvas.stroke(0);
      
      if (treeLayout.equals("Polar") || treeLayout.equals("Radial")) {
        canvas.popMatrix();
      }
    }



    /**
     * Draw branch lines connecting a node and its children.
     *
     * @param  node  the node the draw branches for
     * @param  x  the x value of the node
     * @param  y  the y value of the node
     * @param  canvas  the PGraphics object to draw the branches to
     */
    private void drawBranches(Node node, PGraphics canvas) {
    
      double x = node.getXOffset();
      double y = node.getYOffset();
      if (treeLayout.equals("Radial")) {
          x = node.getRXOffset();
          y = node.getRYOffset();
      } else if (treeLayout.equals("Polar")) {
          x = node.getROffset() * Math.cos(node.getTOffset());
          y = node.getROffset() * Math.sin(node.getTOffset());
      }
      
      String s = node.getName();
         if(node.getLabel().length() > 0)
             s = node.getLabel();
         for (int i = 0; i < s.length(); i++) {
           width += nodeFont.width(s.charAt(i));
         }
      
      //get the actual screen coordinates
      double xs = toScreenX(x);
      double ys = toScreenY(y);
        
      canvas.strokeWeight((float) (node.getLineWidth()*getLineWidthScale()));
      
      Color c = null;
      if(!colorBranches)
      {
        canvas.stroke(0);
      }
      else
      {
          c = node.getBranchColor(majorityColoring);
          canvas.stroke(c.getRGB());
      }
        
      if (treeLayout.equals("Rectangular")) {
          
          //draw vertical line through the node
          canvas.line((float)xs, (float)toScreenY(node.nodes.get(0).getYOffset()),
            (float)xs, (float)toScreenY(node.nodes.get(node.nodes.size()-1).getYOffset()));
            
          //loop over all of the children
          for (int i=0; i < node.nodes.size(); i++) {
              //draw horizontal line from the vertical line to the child node
                if(!colorBranches)
                {
                  canvas.stroke(0);
                }
                else
                {
                    c = node.nodes.get(i).getBranchColor(majorityColoring);
                    canvas.stroke(c.getRGB());
                }
              
              canvas.strokeWeight((float) (node.nodes.get(i).getLineWidth()*getLineWidthScale()));
              double yp = toScreenY(node.nodes.get(i).getYOffset());
              double xp = toScreenX(node.nodes.get(i).getXOffset());
              canvas.line((float)xs, (float)yp,
                  (float)xp, (float)yp);
                  
              if(this.drawInternalNodeLabels && !node.nodes.get(i).isLeaf() && !node.nodes.get(i).isCollapsed() ) {
                    int lc = node.getLabelColor(majorityColoring).getRGB();
                    canvas.fill(lc);
                    canvas.stroke(lc);
                      if (yscale > nodeFontSize) 
                      {
                          String n = node.nodes.get(i).getName();
                           if(node.nodes.get(i).getLabel().length() > 0)
                               n = node.nodes.get(i).getLabel();
                          canvas.text(n, (float)(xp + 1), (float)(yp+1));
                      }
                }
          }
      
            
      } else if (treeLayout.equals("Triangular")) {
          //loop over all of the children
          for (int i=0; i < node.nodes.size(); i++) {
              //draw line from parent to the child node
              if(!colorBranches)
              {
                canvas.stroke(0);
              }
              else
              {
                  c = node.nodes.get(i).getBranchColor(majorityColoring);
                  canvas.stroke(c.getRGB());
              }
              
              canvas.strokeWeight((float) (node.nodes.get(i).getLineWidth()*getLineWidthScale()));
              double yp = toScreenY(node.nodes.get(i).getYOffset());
              double xp = toScreenX(node.nodes.get(i).getXOffset());
              canvas.line((float)xs, (float)ys,
                  (float)xp, (float)yp);
                  
              if(this.drawInternalNodeLabels && !node.nodes.get(i).isLeaf() && !node.nodes.get(i).isCollapsed() ) {
                      int lc = node.getLabelColor(majorityColoring).getRGB();
                      canvas.fill(lc);
                      canvas.stroke(lc);
                        if (yscale > nodeFontSize) 
                        {
                            String n = node.nodes.get(i).getName();
                             if(node.nodes.get(i).getLabel().length() > 0)
                                 n = node.nodes.get(i).getLabel();
                            canvas.text(n, (float)(xp + 1), (float)(yp+1));
                        }
                  }
          }
      } else if (treeLayout.equals("Radial")) {
          //loop over all of the children
          for (int i=0; i < node.nodes.size(); i++) {
              //draw line from parent to the child node
              Node k = node.nodes.get(i);
/*              Color col = k.getBranchColor(majorityColoring);
              canvas.stroke(col.getRGB());*/
              
              if(!colorBranches)
                {
                  canvas.stroke(0);
                }
                else
                {
                    c = k.getBranchColor(majorityColoring);
                    canvas.stroke(c.getRGB());
                }
              
              double d = k.getLineWidth();
              
              canvas.strokeWeight((float) (d*getLineWidthScale()));
              double xp = toScreenX(k.getRXOffset());
              double yp = toScreenY(k.getRYOffset());
              canvas.line((float)xs, (float)ys,
                  (float)xp, (float)yp);
              if(this.drawInternalNodeLabels && !node.nodes.get(i).isLeaf() && !node.nodes.get(i).isCollapsed() ) {
                      int lc = node.getLabelColor(majorityColoring).getRGB();
                      canvas.fill(lc);
                      canvas.stroke(lc);
                        if (yscale > nodeFontSize) 
                        {
                            String n = node.nodes.get(i).getName();
                             if(node.nodes.get(i).getLabel().length() > 0)
                                 n = node.nodes.get(i).getLabel();
                            canvas.text(n, (float)(xp + 1), (float)(yp+1));
                        }
                  }
          }
          /*if(this.drawInternalNodeLabels) {
                        int lc = node.getLabelColor(majorityColoring).getRGB();
                        canvas.fill(lc);
                        canvas.stroke(lc);
                          if (yscale > nodeFontSize) 
                              canvas.text(s, (float)(toScreenX(node.getRXOffset) + 1), (float)(toScreenY(node.getRYOffset)));
                    }*/
          
      } else if (treeLayout.equals("Polar")) {
          canvas.ellipseMode(CENTER);
          //draw curved line through the node
          canvas.noFill();
          double radius = node.getROffset();
          double minradius = Math.min(node.nodes.get(0).getTOffset(), 
            node.nodes.get(node.nodes.size()-1).getTOffset());
          double maxradius = Math.max(node.nodes.get(0).getTOffset(), 
            node.nodes.get(node.nodes.size()-1).getTOffset());
          canvas.arc((float)xstart, (float)ystart, (float)(xscale*2*radius), 
            (float)(yscale*2*radius), (float)(minradius),
            (float)(maxradius));
          
          //loop over all of the children
          for (int i=0; i < node.nodes.size(); i++) {
              //draw line from parent to the child node
              Node k = node.nodes.get(i);
              if(!colorBranches)
                  {
                    canvas.stroke(0);
                  }
                  else
                  {
                      c = k.getBranchColor(majorityColoring);
                      canvas.stroke(c.getRGB());
                  }
              double d = k.getLineWidth();
              canvas.strokeWeight((float) (d*getLineWidthScale()));
              double xp = k.getROffset() * Math.cos(k.getTOffset());
              double yp = k.getROffset() * Math.sin(k.getTOffset());
              double ctheta = k.getTOffset();
              double xin = radius * Math.cos(ctheta);
              double yin = radius * Math.sin(ctheta);
              canvas.line((float)toScreenX(xin), (float)toScreenY(yin),
                  (float)toScreenX(xp), (float)toScreenY(yp));
                  
              if(this.drawInternalNodeLabels && !node.nodes.get(i).isLeaf() && !node.nodes.get(i).isCollapsed() ) {
                        int lc = node.getLabelColor(majorityColoring).getRGB();
                        canvas.fill(lc);
                        canvas.stroke(lc);
                          if (yscale > nodeFontSize) 
                          {
                              String n = k.getName();
                               if(k.getLabel().length() > 0)
                                   n = k.getLabel();
                              canvas.text(n, (float)(toScreenX(xp) + 1), (float)(toScreenY(yp)+1));
                          }
                    }   
          }   
      }
    }


    /**
     * Draw a wedge in place of a tree.
     *
     * @param  node  the root of the tree to draw as a wege
     * @param  x  the x value of the root node
     * @param  y  the y value of the root node
     * @param  canvas  the PGraphics object to draw to
     */
    private void drawWedge(Node node, PGraphics canvas) {
      
      double x = node.getXOffset();
      double y = node.getYOffset();
      if (treeLayout.equals("Radial")) {
          x = node.getRXOffset();
          y = node.getRYOffset();
      } else if (treeLayout.equals("Polar")) {
          x = node.getROffset() * Math.cos(node.getTOffset());
          y = node.getROffset() * Math.sin(node.getTOffset());
      }
        
      //set up the drawing properties
      canvas.strokeWeight((float) (node.getLineWidth()*getLineWidthScale()));

      Color c = null;
        if(!colorBranches)
        {
          canvas.stroke(0);
          canvas.fill(0);
        }
        else
        {
            c = node.getBranchColor(majorityColoring);
            canvas.stroke(c.getRGB());
            canvas.fill(c.getRGB());
        }
  
      //find the longest and shortest branch lengths
      double longest = node.longestRootToTipDistance() - node.getBranchLength();
      double shortest = node.shortestRootToTipDistance() - node.getBranchLength();
    
            //re-scale so height is 1/2 the number of nodes
        double top = node.getMaximumYOffset()-y;
        top = (top/2)*wedgeHeightScale;
        double bottom = y-node.getMinimumYOffset();
        bottom = (bottom/2)*wedgeHeightScale;           
        //set wedge at y location
        top = y + top;
        bottom = y - bottom;
      
      if (treeLayout.equals("Rectangular")) {   
          if(mirrored)
          {
              canvas.quad((float)toScreenX(x), (float)toScreenY(bottom), //center to bottom
                  (float)toScreenX(x),  (float)toScreenY(top),  //center to top
                  (float)toScreenX(x-shortest),  (float)toScreenY(top),  //top to longest branch length
                  (float)toScreenX(x-longest),  (float)toScreenY(bottom) );  //bottom to shortest branch length
          }
          else
          {
          //draw the wedge
          canvas.quad((float)toScreenX(x), (float)toScreenY(bottom), //center to bottom
            (float)toScreenX(x),  (float)toScreenY(top),  //center to top
            (float)toScreenX(x+shortest),  (float)toScreenY(top),  //top to longest branch length
            (float)toScreenX(x+longest),  (float)toScreenY(bottom) );  //bottom to shortest branch length
        }
    
      } else if (treeLayout.equals("Triangular")) {
          if(mirrored)
            {
                //draw the wedge
                  canvas.triangle((float)toScreenX(x), (float)toScreenY(y), //center to bottom
                    (float)toScreenX(x-shortest),  (float)toScreenY(top),  //top to longest branch length
                    (float)toScreenX(x-longest),  (float)toScreenY(bottom) );  //bottom to shortest branch length
            }
            else
            {
          //draw the wedge
          canvas.triangle((float)toScreenX(x), (float)toScreenY(y), //center to bottom
            (float)toScreenX(x+shortest),  (float)toScreenY(top),  //top to longest branch length
            (float)toScreenX(x+longest),  (float)toScreenY(bottom) );  //bottom to shortest branch length
            }
      } else if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
                
          double maxt = node.getMaximumTOffset();
          double mint = node.getMinimumTOffset();
          
          if(wedgeHeightScale < 1)
          {
              double theta = Math.abs(maxt-mint);
              double f = (theta*(1-wedgeHeightScale))/2;
              mint = mint + f;
              maxt = maxt - f;
          }
          
          double x1 = node.getParent().getRXOffset() + shortest * Math.cos(mint);
          double y1 = node.getParent().getRYOffset() + shortest * Math.sin(mint);
          double x2 = node.getParent().getRXOffset() + longest * Math.cos(maxt);
          double y2 = node.getParent().getRYOffset() + longest * Math.sin(maxt);

          //draw the wedge
          canvas.triangle((float)toScreenX(x), (float)toScreenY(y), //center to bottom
            (float)toScreenX(x1),  (float)toScreenY(y1),  //top to longest branch length
            (float)toScreenX(x2),  (float)toScreenY(y2) );  //bottom to shortest branch length
      }
      
      canvas.fill(wedgeFontColor);
      canvas.stroke(wedgeFontColor);
      if(drawWedgeLabels){
          canvas.textFont(wedgeFont);
          String s = "";
            if(drawInternalNodeLabels)
            {
                s = node.getLabel();
            }
            else if(node.getConsensusLineage() != null)
            {
                s = node.getConsensusLineage();
                if(s.lastIndexOf(";",s.length()-2) != -1)
                    s = s.substring(s.lastIndexOf(";",s.length()-2)+1,s.length());
            }
            else
            {s = ""+node.getNumberOfLeaves();}
          
          if(Math.abs((toScreenY(top)-toScreenY(bottom))) > wedgeFontSize)
          {
              canvas.textFont(wedgeFont);
              if(mirrored)
              {
                  canvas.text(s, (float)(toScreenX(x-(shortest)/2)+labelXOffset), (float)(toScreenY(bottom+(top-bottom)/2)+5)+labelYOffset);
              }
              else
                  canvas.text(s, (float)(toScreenX(x+(shortest)/2)+labelXOffset), (float)(toScreenY(bottom+(top-bottom)/2)+5)+labelYOffset);
          }
          canvas.textFont(nodeFont);
      }
      // reset drawing color to black
      canvas.fill(0);
      canvas.stroke(0);
    }

    /**
     * Sets the y-offset for each node in the tree to the average of its children
     *
     * @param  node  the root of the tree to set y-offsets for
     * @param  _prev  an internal variable for recusion
     */
    public void setYOffsets(Node node, double _prev) {

      if (node.isLeaf()) {
        //if it's a leaf, then it's position is just one more than before
        node.setYOffset(_prev+1);
      } else {
        //set the y-offset based the subtree's values
        double total = 0;
        for (int i=0; i < node.nodes.size(); i++) {
          setYOffsets(node.nodes.get(i), _prev);
          _prev = _prev + node.nodes.get(i).getNumberOfLeaves();
          total = total + node.nodes.get(i).getYOffset();
        }
        //offset is average of children
        node.setYOffset(total/node.nodes.size());
      }

        setMinMaxYOffsets(node);
    }
    
    public void setMinMaxYOffsets(Node node){
    //set the max and min y-offsets
      node.setMaximumYOffset(node.getYOffset());
      node.setMinimumYOffset(node.getYOffset());
      for (int i=0; i < node.nodes.size(); i++) {
        node.setMaximumYOffset(Math.max(node.getMaximumYOffset(), node.nodes.get(i).getMaximumYOffset()));
        node.setMinimumYOffset(Math.min(node.getMinimumYOffset(), node.nodes.get(i).getMinimumYOffset()));
      }
    }
    
    
    public void setXOffsets(Node node, double _prev) {
        node.setXOffset(_prev+node.getBranchLength());
        for (int i = 0; i < node.nodes.size(); i++) {
            setXOffsets(node.nodes.get(i), _prev+node.getBranchLength());
        }
    }
    
    //NOTE: the theta offsets must be set before the radius offsets
    public void setROffsets(Node node, double _prev) {
        node.setROffset(_prev+node.getBranchLength());
        for (int i = 0; i < node.nodes.size(); i++) {
            setROffsets(node.nodes.get(i), _prev+node.getBranchLength());
        }
        
      //set the max and min y-offsets
      node.setMaximumROffset(node.getROffset());
      node.setMinimumROffset(node.getROffset());
      for (int i=0; i < node.nodes.size(); i++) {
        node.setMaximumROffset(Math.max(node.getMaximumROffset(), node.nodes.get(i).getMaximumROffset()));
        node.setMinimumROffset(Math.min(node.getMinimumROffset(), node.nodes.get(i).getMinimumROffset()));
      }
    }
    
    public void setTOffsets(Node node, double _prev) {

      double delta = 2*Math.PI/root.getNumberOfLeaves();
      
      if (node.isLeaf()) {
        //if it's a leaf, then it's position is just one more than before
        node.setTOffset(_prev+delta);
      } else {
        //set the y-offset based the subtree's values
        double total = 0;
        for (int i=0; i < node.nodes.size(); i++) {
          setTOffsets(node.nodes.get(i), _prev);
          _prev = _prev + delta*node.nodes.get(i).getNumberOfLeaves();
          total = total + node.nodes.get(i).getTOffset();
        }
        //offset is average of children
        node.setTOffset(total/node.nodes.size());
      }
      
      //set the max and min theta-offsets
      node.setMaximumTOffset(node.getTOffset());
      node.setMinimumTOffset(node.getTOffset());
      for (int i=0; i < node.nodes.size(); i++) {
        node.setMaximumTOffset(Math.max(node.getMaximumTOffset(), node.nodes.get(i).getMaximumTOffset()));
        node.setMinimumTOffset(Math.min(node.getMinimumTOffset(), node.nodes.get(i).getMinimumTOffset()));
      }

    }
    
    public void setRadialOffsets(Node node) {
        if (node == root) {
            node.setRXOffset(0);
            node.setRYOffset(0);
        } else {
            Node p = node.getParent();
            node.setRXOffset(p.getRXOffset() + node.getBranchLength() * Math.cos(node.getTOffset()));
            node.setRYOffset(p.getRYOffset() + node.getBranchLength() * Math.sin(node.getTOffset()));
        }
        for (int i = 0; i < node.nodes.size(); i++) {
            setRadialOffsets(node.nodes.get(i));
        }
        
       //set the max and min y-offsets
      node.setMaximumRXOffset(node.getRXOffset());
      node.setMinimumRXOffset(node.getRXOffset());
      for (int i=0; i < node.nodes.size(); i++) {
        node.setMaximumRXOffset(Math.max(node.getMaximumRXOffset(), node.nodes.get(i).getMaximumRXOffset()));
        node.setMinimumRXOffset(Math.min(node.getMinimumRXOffset(), node.nodes.get(i).getMinimumRXOffset()));
      }
    }
    
    

    /**
     * Exports a screen caputre of the tree to a PDF file
     *
     * @param  path The path to write the image to
     */
    public void exportScreenCapture(String path) {
		  PGraphics canvas = createGraphics(width, height, P2D);
		  canvas.beginDraw();
		  canvas.textFont(nodeFont);
		  canvas.background(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
          canvas.pushMatrix();
          canvas.translate((float)xstart, (float)ystart);
          canvas.rotate((float)(treerotation*Math.PI/180.0));
          canvas.translate((float)-xstart, (float)-ystart);
		  drawTree(root, canvas);
          canvas.popMatrix();
		  canvas.endDraw();
		  canvas.save(path);
    }

    /**
     * Export the entire tree to a PDF file
     *
     * @param  path The path to write the image to
     * @param  dims The dimentions of the output image file
     */
    public void exportTreeImage(String path, double dims[]) {
	  try {
		  //save the current variables
		  int oldWidth = getWidth();
		  int oldHeight = getHeight();
		  double oldXScale = xscale;
		  double oldYScale = yscale;
		  double oldXStart = xstart;
		  double oldYStart = ystart;
/*        boolean oldDrawExternalNodeLabels = drawExternalNodeLabels;*/
/*        boolean oldDrawInternalNodeLabels = drawInternalNodeLabels;*/
/*        boolean oldDrawNodeLabels = drawNodeLabels;*/
	
		  //reset the sizing and zooming so that the tree can be drawn visibly
		  double longest = textWidth(root.getLongestLabel());
		  double l = root.longestRootToTipDistance();
		  double s = root.shortestRootToTipDistance();
/*        drawExternalNodeLabels = true;*/
/*        drawInternalNodeLabels = true;*/
/*        drawNodeLabels = true;*/
		  if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
			  xscale = (dims[0]-MARGIN-TREEMARGIN)/root.depth();
			  xstart = MARGIN;
		  } else if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
			  xscale = (Math.min(dims[0], dims[1])*0.5-MARGIN)/root.depth();
			  xstart = dims[0]*0.5;
		  }
		  
		 if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
			  yscale = (dims[1]-2*MARGIN)/root.getNumberOfLeaves();
			  ystart = MARGIN;
		  } else if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
			  yscale = (Math.min(dims[0], dims[1])*0.5-MARGIN)/root.depth();
			  ystart = dims[1]*0.5;
		  }
		  
		  PGraphics canvas = createGraphics((int) (dims[0]), (int) (dims[1]), PDF, path);
	
	
		  //draw the tree to the file
		  canvas.beginDraw();
		  canvas.background(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
          canvas.pushMatrix();
          canvas.translate((float)xstart, (float)ystart);
          canvas.rotate((float)(treerotation*Math.PI/180.0));
          canvas.translate((float)-xstart, (float)-ystart);
          canvas.textFont(nodeFont);
		  drawTree(root, canvas);
          canvas.popMatrix();
          canvas.dispose();
		  canvas.endDraw();

	
		  //go back to how it was
		  xscale = oldXScale;
		  yscale = oldYScale;
		  xstart = oldXStart;
		  ystart = oldYStart;
/*        drawExternalNodeLabels = oldDrawExternalNodeLabels;*/
/*        drawInternalNodeLabels = oldDrawInternalNodeLabels;*/
/*        drawNodeLabels = oldDrawNodeLabels;*/
		  redraw();
	} catch (Exception ex) {
	    System.out.println(ex);
	    }

    }
}