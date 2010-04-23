package topiarytool;

import processing.core.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.Color;
import javax.jnlp.*;
import java.io.*;

public class TreeVis extends PApplet {

    //the amount of space around the tree, for viewing purposes
    private double MARGIN = 10;
    private double TREEMARGIN = 10;

    private final int SELECTED_COLOR = 0xff66CCFF;
    private final int HIGHLIGHTED_COLOR = 0xffFF66FF;
    
    private Color backgroundColor = new Color(255,255,255);

    // Pixels per unit of branch length:
    private double xscale;
    // Pixels per branch
    private double yscale;
    
    //scaling for the line width
    private double lineWidthScale = 1;

    //the tree that is currently being displayed
    private Node root;

    private double xstart = MARGIN;
    private double ystart = MARGIN;

    private double oldwidth =  0;
    private double oldheight = 0;
    
    //the tree layour
    private String treeLayout = "Rectangular";

    //should the labels be drawn or not?
    private boolean drawExternalNodeLabels = false;
    private boolean drawInternalNodeLabels = false;
    //is a label being dragged?
    private boolean draggingLabel = false;
    private float collapsedPixel = 10000000;

    private Node selectedNode;
    private Node mouseOverNode;
    private Node mouseOverNodeToReplace; //used when dragging nodes around
    private Set hilightedNodes = new java.util.HashSet();

    private List listeners = new java.util.ArrayList();
    private PFont currFont = createFont("georgia", 12);


    /**
     * setup() is called once to initialize the applet
     */
    public void setup() {
        size(800, 600);
        smooth();
        //noLoop();
        frameRate(30);
        //set up default font
        textFont(currFont);
        oldwidth = width;
        oldheight = height;
    }

    /**
     * draw() is called whenever the tree needs to be re-drawn.
     * @see redraw
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
         drawTree(root);
      } catch (Exception e) {
          System.out.println("WARNING: Error drawing tree, probably due to concurrency issues. Normally, this warning can be ignored.");
          //frame.consoleWindow.update("WARNING: Error drawing tree, probably due to concurrency issues. Normally, this warning can be ignored.");
          e.printStackTrace();
      }
    }


    //GETTERS AND SETTERS
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
    public double getLineWidthScale() { return lineWidthScale; }
    public void setLineWidthScale(double f) { lineWidthScale = f; }

    //SCROLLBAR METHODS
    public int getCurrentVerticalScrollPosition() {
      if (root==null) return 0;
      return (int) -(ystart-MARGIN);
    }
    public void setVerticalScrollPosition(int value) {
      ystart = -value + MARGIN;
      if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
        ystart = ystart + getHeight()*0.5;
      }
      fireStateChanged();
      redraw();
    }
    public int getCurrentHorizontalScrollPosition() {
      if (root==null) return 0;
      return (int) -(xstart-MARGIN);
    }
    public void setHorizontalScrollPosition(int value) {
      xstart = -value + MARGIN;
      if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
        xstart = xstart + getWidth()*0.5;
      }
      fireStateChanged();
      redraw();
    }
    public int getMaxVerticalScrollPosition() {
      if (root==null) return 0;
      if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
        return (int) (root.getNumberOfLeaves()*yscale + 2*MARGIN);
      } else {
        return (int) (root.depth()*yscale + 2*MARGIN);
      }
    }
    public int getMaxHorizontalScrollPosition() {
      if (root==null) return 0;
      if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
        return (int) (root.depth()*xscale + MARGIN + TREEMARGIN);
      } else {
        return (int) (root.depth()*xscale + 2*MARGIN);
      }      
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
        } else if (!draggingLabel && n!=null && !n.isLeaf() && key==BACKSPACE) {
            if (n.getLabel().length() > 0){
                n.setLabel(n.getLabel().substring(0,n.getLabel().length()-1));
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
              //can't replace a leaf!
              if (n == null) {
                  mouseOverNodeToReplace = null;
              } else {
                  //can't replace a leaf!
                  if (!n.isLeaf()) {
                      this.mouseOverNodeToReplace = n;
                  }
              }
          } else {
              this.mouseOverNode = null;
            this.mouseOverNodeToReplace = null;
          }
      }
    }

    public void mouseReleased() {
      cursor(ARROW);
      if (draggingLabel && mouseOverNodeToReplace != null) {
          //replace node label
          mouseOverNodeToReplace.setLabel(mouseOverNode.getLabel());
          mouseOverNode.setLabel("");
          selectedNode = mouseOverNodeToReplace;
      }
      draggingLabel = false;
      mouseOverNode = findNode(mouseX, mouseY);
      mouseOverNodeToReplace = null;
    }

    /**
     * mouseMoved() is called whenever the mouse is moved.
     */
    public void mouseMoved() {
      //is the mouse over a node?
      Node node = findNode(mouseX, mouseY);
      if (node != null) {
        //if so, chance the cursor's hand
        cursor(HAND);
        //outline the node
        if ((node.isLeaf() && this.drawExternalNodeLabels) ||(!node.isLeaf() && this.drawInternalNodeLabels)) {
            mouseOverNode = node;
        }
        redraw();
      }
      else {
        //cursor is normal
        cursor(ARROW);
        //set outlined node to nothing
        mouseOverNode = null;
      }
      mouseOverNodeToReplace = null;
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
          } else if (mouseEvent.getClickCount() == 2) {
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
    }

    /**
     * Ensures that the tree is not scrolled out of its bounds, and resets it back if it is
     */
    public void checkBounds() {
      //if there's no tree, we can't check the bounds
      if (root==null) return;

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
          ystart = MARGIN;
      } else if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
          yscale = (Math.min(getWidth(), getHeight())*0.5-MARGIN)/root.depth();
          ystart = getHeight()*0.5;
      }
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
        width += currFont.width(s.charAt(i));
      }
      TREEMARGIN = MARGIN + width*currFont.size + 5;
      //set the tree
      root = newRoot;
      //recalculate the x- and y-offsets of the nodes in the tree
      setYOffsets(newRoot, 0);
      setXOffsets(newRoot, 0);
      setTOffsets(newRoot, 0);
      setROffsets(newRoot, 0);
      setRadialOffsets(newRoot);

      resetTreeX();
      resetTreeY();
      
      //notify listeners
      fireStateChanged();
      //redraw the tree
      redraw();          
}


    /**
     * Rescales the tree, keeping the point (x, y) in screen coords at the same position relative
     * to the tree.  Note that rescaling the tree only has an effect on the vertical scale.
     *
     * @param  value  the new scale value
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

      //xstart = xstart - (toScreenX(l) - x);
      //ystart = ystart - (toScreenY(r) - y);

      checkBounds();
      fireStateChanged();
      redraw();
    }



    /**
     * Calls findNode(Node,double,double,double) on the root of the tree
     *
     * @param  x  the x-value to search for the node at
     * @param  y  the y-value to search for the node at
     * @see findNode(Node,double,double,double)
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
      double width = 0;
      String s = tree.getLabel();
      for (int i = 0; i < s.length(); i++) {
        width += currFont.width(s.charAt(i));
      }
      double maxX =  nodeX + 5 + (width*currFont.size);
      double minY = nodeY - (currFont.descent()*currFont.size);
      double maxY = nodeY + (currFont.ascent()*currFont.size);
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

      //if the tree is collapsed, don't search it
      if (tree.isCollapsed()) return null;

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

      boolean isInternal = !node.isLeaf();
      boolean collapsed = false;
      if (treeLayout.equals("Rectangular") || treeLayout.equals("Triangular")) {
          collapsed = node.isCollapsed() || toScreenX(node.getXOffset()) > collapsedPixel;
      } else {
          collapsed = node.isCollapsed() || node.getROffset()/root.depth() > collapsedPixel/getWidth();
      }

      // Draw the branches first, so they get over-written by the nodes later
      if (isInternal) {
        if (collapsed){
          //if it's an internal, collapsed node, then draw a wedge in its place
          drawWedge(node, canvas);
        }
        else {
          //if it's an internal, uncollapsed node, draw branches of the tree
          drawBranches(node, canvas);
        }
      }


      //if it's internal and not collapsed, draw all the subtrees
      if (isInternal && !collapsed) {
        for (int i=0; i < node.nodes.size(); i++) {
          Node child = node.nodes.get(i);
          drawTree(child, canvas);
        }
      }

      //draw the current node over the branches
      drawNode(node, canvas);

      //draw the pie chart?
      if (node.getDrawPie() == true) {
        PieChart pie = new PieChart((int)Math.round(toScreenX(node.getXOffset())), //draw at the same x-value as the node
          (int)Math.round(toScreenY(node.getYOffset())), //draw at the y-value of the node
          75, //diameter of the pie chart
          node.getGroupFraction(), //the weighting of the node's colors
          node.getGroupColor()); //the colors for the node
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

      //is this node slected or hilighted?
      boolean selected = (node == selectedNode);
      boolean hilighted = hilightedNodes.contains(node);

      float drawX = (float)xs;
      float drawY = (float)ys;
      if (draggingLabel && this.mouseOverNode == node) {
          //this node's label is being dragged:
          // change drawing coords to be wherever the mouse is
          drawX = mouseX;
          drawY = mouseY;
      }

      if (selected || hilighted) {
        int c;
        if (selected) { c = SELECTED_COLOR; } else { c = HIGHLIGHTED_COLOR; }
        canvas.fill(c);
        canvas.stroke(c);

        //draw the selection/highlighting
        double minX = drawX+offsetbias-1;
        double width = 0;
        String s = node.getLabel();
        for (int i = 0; i < s.length(); i++) {
          width += currFont.width(s.charAt(i));
        }
        double maxX =  drawX + 5 + (width*currFont.size);
        double minY = drawY - (currFont.descent()*currFont.size);
        double maxY = drawY + (currFont.ascent()*currFont.size);
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
        canvas.strokeWeight(3);
        canvas.stroke(255,0,0);
        double minX = drawX-3;
        double width = 0;
        String s = node.getLabel();
        for (int i = 0; i < s.length(); i++) {
          width += currFont.width(s.charAt(i));
        }
        double maxX =  drawX + 5 + (width*currFont.size)+3;
        double minY = drawY - (currFont.descent()*currFont.size)-3;
        double maxY = drawY + (currFont.ascent()*currFont.size)+3;
        canvas.line((float)minX, (float)minY, (float)minX, (float)maxY);
        canvas.line((float)minX, (float)maxY, (float)maxX, (float)maxY);
        canvas.line((float)maxX, (float)maxY, (float)maxX, (float)minY);
        canvas.line((float)maxX, (float)minY, (float)minX, (float)minY);
        canvas.stroke(0);
      }

      //set the color/weight to draw
      canvas.strokeWeight(1);
      canvas.stroke(node.getColor().getRGB());
      canvas.fill(node.getColor().getRGB());
      //draw node label if we need to
       double minX = drawX+offsetbias-1;
       double width = 0;
       String s = node.getLabel();
       for (int i = 0; i < s.length(); i++) {
         width += currFont.width(s.charAt(i));
       }
       canvas.fill(255);
       canvas.noStroke();
       
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
        if (rotation < 0) {
            rotation = rotation + 2*Math.PI;
        }
        //draw all text rightside-up
        if (rotation > Math.PI/2 && rotation < 3*Math.PI/2) {
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
      
     double maxX =  drawX + 5 + (width*currFont.size);
     double minY = drawY - (currFont.descent()*currFont.size);
     double maxY = drawY + (currFont.ascent()*currFont.size);
     
      
      if (node.isLeaf()) {
        if (drawExternalNodeLabels && node.getLabel().length() > 0) {
            //canvas.rect((float)minX, (float)minY, (float)(maxX-minX), (float)(maxY-minY));
            canvas.fill(0);
            canvas.stroke(0);
            canvas.text(node.getLabel(), (float)(drawX+offsetbias), (float)(drawY));
        }
      } else {
          if (drawInternalNodeLabels && node.getLabel().length() > 0) {
            //canvas.rect((float)minX, (float)minY, (float)(maxX-minX), (float)(maxY-minY));
            canvas.fill(0);
            canvas.stroke(0);
            canvas.text(node.getLabel(), (float)(drawX+offsetbias), (float)(drawY));
          }
      }
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
      
      //get the actual screen coordinates
      double xs = toScreenX(x);
      double ys = toScreenY(y);


      canvas.strokeWeight((float) (node.getLineWidth()*getLineWidthScale()));
      canvas.stroke(node.getColor().getRGB());
      if (treeLayout.equals("Rectangular")) {
          
          //draw vertical line through the node
          canvas.line((float)xs, (float)toScreenY( node.nodes.get(0).getYOffset()),
            (float)xs, (float)toScreenY(node.nodes.get(node.nodes.size()-1).getYOffset()));
            
          //loop over all of the children
          for (int i=0; i < node.nodes.size(); i++) {
              //draw horizontal line from the vertical line to the child node
              canvas.stroke(node.nodes.get(i).getColor().getRGB());
              canvas.strokeWeight((float) (node.nodes.get(i).getLineWidth()*getLineWidthScale()));
              double yp = toScreenY(node.nodes.get(i).getYOffset());
              double xp = toScreenX(node.nodes.get(i).getXOffset());
              canvas.line((float)xs, (float)yp,
                  (float)xp, (float)yp);
          }
      
            
      } else if (treeLayout.equals("Triangular")) {
          //loop over all of the children
          for (int i=0; i < node.nodes.size(); i++) {
              //draw line from parent to the child node
              canvas.stroke(node.nodes.get(i).getColor().getRGB());
              canvas.strokeWeight((float) (node.nodes.get(i).getLineWidth()*getLineWidthScale()));
              double yp = toScreenY(node.nodes.get(i).getYOffset());
              double xp = toScreenX(node.nodes.get(i).getXOffset());
              canvas.line((float)xs, (float)ys,
                  (float)xp, (float)yp);
          }
      } else if (treeLayout.equals("Radial")) {
          //loop over all of the children
          for (int i=0; i < node.nodes.size(); i++) {
              //draw line from parent to the child node
              canvas.stroke(node.nodes.get(i).getColor().getRGB());
              canvas.strokeWeight((float) (node.nodes.get(i).getLineWidth()*getLineWidthScale()));
              double xp = toScreenX(node.nodes.get(i).getRXOffset());
              double yp = toScreenY(node.nodes.get(i).getRYOffset());
              canvas.line((float)xs, (float)ys,
                  (float)xp, (float)yp);
          }
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
              canvas.stroke(node.nodes.get(i).getColor().getRGB());
              canvas.strokeWeight((float) (node.nodes.get(i).getLineWidth()*getLineWidthScale()));
              double xp = node.nodes.get(i).getROffset() * Math.cos(node.nodes.get(i).getTOffset());
              double yp = node.nodes.get(i).getROffset() * Math.sin(node.nodes.get(i).getTOffset());
              double ctheta = node.nodes.get(i).getTOffset();
              double xin = radius * Math.cos(ctheta);
              double yin = radius * Math.sin(ctheta);
              canvas.line((float)toScreenX(xin), (float)toScreenY(yin),
                  (float)toScreenX(xp), (float)toScreenY(yp));
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
      canvas.stroke(node.getColor().getRGB());
      canvas.fill(node.getColor().getRGB());
  
      //find the longest and shortest branch lengths
      double longest = node.longestRootToTipDistance() - node.getBranchLength();
      double shortest = node.shortestRootToTipDistance() - node.getBranchLength();
    
      if (treeLayout.equals("Rectangular")) {      

          // find heights for wedge
          double top = node.getMaximumYOffset()-y;
          double bottom = y-node.getMinimumYOffset();
    
          //re-scale so height is 1/2 the number of nodes
          top = y + (top/2);
          bottom = y - (bottom/2);

          //draw the wedge
          canvas.quad((float)toScreenX(x), (float)toScreenY(bottom), //center to bottom
            (float)toScreenX(x),  (float)toScreenY(top),  //center to top
            (float)toScreenX(x+longest),  (float)toScreenY(top),  //top to longest branch length
            (float)toScreenX(x+shortest),  (float)toScreenY(bottom) );  //bottom to shortest branch length
    
          //create white background for text
          canvas.fill(255);
          canvas.stroke(255);
          canvas.quad((float)(toScreenX(x)+5), (float)(toScreenY((top+bottom)/2)+7),
            (float)(toScreenX(x)+5+textWidth(str(node.getNumberOfLeaves()))), (float)(toScreenY((top+bottom)/2)+7),
            (float)(toScreenX(x)+5+textWidth(str(node.getNumberOfLeaves()))), (float)(toScreenY((top+bottom)/2)-3),
            (float)(toScreenX(x)+5), (float)(toScreenY((top+bottom)/2)-3));
          // write in the text (number of OTUs contained) in black
          canvas.fill(0);
          canvas.stroke(0);
          canvas.text(str(node.getNumberOfLeaves()), (float)(toScreenX(x)+5), (float)(toScreenY((top+bottom)/2)+5));
      } else if (treeLayout.equals("Triangular")) {
          // find heights for wedge
          double top = node.getMaximumYOffset()-y;
          double bottom = y-node.getMinimumYOffset();
    
          //re-scale so height is 1/2 the number of nodes
          top = y + (top/2);
          bottom = y - (bottom/2);

          //draw the wedge
          canvas.triangle((float)toScreenX(x), (float)toScreenY(y), //center to bottom
            (float)toScreenX(x+longest),  (float)toScreenY(top),  //top to longest branch length
            (float)toScreenX(x+shortest),  (float)toScreenY(bottom) );  //bottom to shortest branch length
    
          //create white background for text
          canvas.fill(255);
          canvas.stroke(255);
          canvas.quad((float)(toScreenX(x)+5), (float)(toScreenY((top+bottom)/2)+7),
            (float)(toScreenX(x)+5+textWidth(str(node.getNumberOfLeaves()))), (float)(toScreenY((top+bottom)/2)+7),
            (float)(toScreenX(x)+5+textWidth(str(node.getNumberOfLeaves()))), (float)(toScreenY((top+bottom)/2)-3),
            (float)(toScreenX(x)+5), (float)(toScreenY((top+bottom)/2)-3));
          // write in the text (number of OTUs contained) in black
          canvas.fill(0);
          canvas.stroke(0);
          canvas.text(str(node.getNumberOfLeaves()), (float)(toScreenX(x)+5), (float)(toScreenY((top+bottom)/2)+5));
      
      } else if (treeLayout.equals("Radial") || treeLayout.equals("Polar")) {
          
          double maxt = node.getMaximumTOffset();
          double mint = node.getMinimumTOffset();
          double x1 = node.getParent().getRXOffset() + shortest * Math.cos(mint);
          double y1 = node.getParent().getRYOffset() + shortest * Math.sin(mint);
          double x2 = node.getParent().getRXOffset() + longest * Math.cos(maxt);
          double y2 = node.getParent().getRYOffset() + longest * Math.sin(maxt);

          //draw the wedge
          canvas.triangle((float)toScreenX(x), (float)toScreenY(y), //center to bottom
            (float)toScreenX(x1),  (float)toScreenY(y1),  //top to longest branch length
            (float)toScreenX(x2),  (float)toScreenY(y2) );  //bottom to shortest branch length
    
          //create white background for text
          canvas.fill(255);
          canvas.stroke(255);
          canvas.quad((float)(toScreenX(x)+5), (float)(toScreenY(y)+7),
            (float)(toScreenX(x)+5+textWidth(str(node.getNumberOfLeaves()))), (float)(toScreenY(y)+7),
            (float)(toScreenX(x)+5+textWidth(str(node.getNumberOfLeaves()))), (float)(toScreenY(y)-3),
            (float)(toScreenX(x)+5), (float)(toScreenY(y)-3));
          // write in the text (number of OTUs contained) in black
          canvas.fill(0);
          canvas.stroke(0);
          canvas.text(str(node.getNumberOfLeaves()), (float)(toScreenX(x)+5), (float)(toScreenY(y)+5)); 
      }
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
     * @param  filename  the file to write the image to
     */
    public void exportScreenCapture(FileContents fc) {
      try {
		  String filename = fc.getName();
		  PGraphics canvas = createGraphics(width, height, PDF, filename);
		  canvas.beginDraw();
		  canvas.textFont(currFont);
		  background(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
		  drawTree(root, canvas);
		  canvas.dispose();
		  canvas.endDraw();
      } catch (java.io.IOException e) {}
    }

    /**
     * Export the entire tree to a PDF file
     *
     * @param  filename  the file to write the image to
     */
    public void exportTreeImage(FileContents file, double dims[]) {
	  try {
		  //save the current variables
		  int oldWidth = getWidth();
		  int oldHeight = getHeight();
		  double oldXScale = xscale;
		  double oldYScale = yscale;
		  double oldXStart = xstart;
		  double oldYStart = ystart;
		  float oldCollapsedPixel = getCollapsedPixel();
		  boolean oldDrawExternalNodeLabels = drawExternalNodeLabels;
		  boolean oldDrawInternalNodeLabels = drawInternalNodeLabels;
	
		  //reset the sizing and zooming so that the tree can be drawn visibly
		  double longest = textWidth(root.getLongestLabel());
		  double l = root.longestRootToTipDistance();
		  double s = root.shortestRootToTipDistance();
		  drawExternalNodeLabels = true;
		  drawInternalNodeLabels = true;
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
		  
		  PGraphics canvas = createGraphics((int) (dims[0]), (int) (dims[1]), PDF, file.getName());
	
		  setCollapsedPixel((float)(getCollapsedPixel()*(xscale/oldXScale)));
	
		  //draw the three to the file
		  canvas.beginDraw();
		  canvas.textFont(currFont);
		  canvas.background(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
		  drawTree(root, canvas);
		  canvas.dispose();
		  canvas.endDraw();
	
		  //go back to how it was
		  xscale = oldXScale;
		  yscale = oldYScale;
		  xstart = oldXStart;
		  ystart = oldYStart;
		  drawExternalNodeLabels = oldDrawExternalNodeLabels;
		  drawInternalNodeLabels = oldDrawInternalNodeLabels;
		  setCollapsedPixel(oldCollapsedPixel);
	
		  redraw();
	} catch (IOException ex) {};

    }
}