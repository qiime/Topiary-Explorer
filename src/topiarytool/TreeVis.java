package topiarytool;

import java.awt.event.KeyEvent;
import processing.core.*;
import javax.swing.event.*;
import java.util.*;

public class TreeVis extends PApplet {

    //the amount of space around the tree, for viewing purposes
    private double MARGIN = 10;
    private final double ORIGMARGIN = 10;

    private final int SELECTED_COLOR = 0xff66CCFF;
    private final int HIGHLIGHTED_COLOR = 0xffFF66FF;

    // Pixels per unit of branch length:
    private double xscale;
    // Pixels per branch
    private double yscale;
    //MAXYSCALE is necessary so that the zoom slider matches with double-clicking
    private double MAXYSCALE = 1000;

    //the tree that is currently being displayed
    private Node root;

    private double xstart = MARGIN;
    private double ystart = MARGIN;

    private double oldwidth =  0;
    private double oldheight = 0;

    //should the labels be drawn or not?
    private boolean drawExternalNodeLabels = false;
    private boolean drawInternalNodeLabels = false;
    //is a label being dragged?
    private boolean draggingLabel = false;

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
      background(255);
      //has the size changed?
      if (oldwidth != width || oldheight != height) {
          oldwidth = width;
          oldheight = height;
          fireStateChanged();
      }
      //draw the tree
      try {
          //keyPressed() isn't working, so:
          if (!(keyPressed && keyCode == SHIFT)) {
            draggingLabel = false;
            mouseOverNode = findNode(mouseX, mouseY);
            mouseOverNodeToReplace = null;
            if (mouseOverNode == null) {
                cursor(ARROW);
            }
          }

         drawTree(root, 0);
      } catch (Exception e) {
          System.out.println("WARNING: Error drawing tree, probably due to concurrency issues. Normally, this warning can be ignored.");
      }
    }


    //GETTERS AND SETTERS
    public double getMaxYScale() { return MAXYSCALE; }
    public double getMargin() { return MARGIN; }
    public double getYScale() { return yscale; }
    public double getXScale() { return xscale; }
    public Node getTree() { return root; }
    public Node getSelectedNode() { return selectedNode; }
    public void setSelectedNode(Node node) { selectedNode = node; }
    public Set getHilightedNodes() { return hilightedNodes; }
    public boolean getDrawExternalNodeLabels() { return drawExternalNodeLabels; }
    public boolean getDrawInternalNodeLabels() { return drawInternalNodeLabels; }
    public void setDrawExternalNodeLabels(boolean b) { drawExternalNodeLabels = b; }
    public void setDrawInternalNodeLabels(boolean b) { drawInternalNodeLabels = b; }

    //SCROLLBAR METHODS
    public int getCurrentVerticalScrollPosition() {
      if (root==null) return 0;
      return (int) -(ystart-MARGIN);
    }
    public void setVerticalScrollPosition(int value) {
      ystart = -value + MARGIN;
      fireStateChanged();
      redraw();
    }
    public int getCurrentHorizontalScrollPosition() {
      if (root==null) return 0;
      return (int) -(xstart-MARGIN);
    }
    public void setHorizontalScrollPosition(int value) {
      xstart = -value + MARGIN;
      fireStateChanged();
      redraw();
    }
    public int getMaxVerticalScrollPosition() {
      if (root==null) return 0;
      return (int) (root.getNumberOfLeaves()*yscale + 2*MARGIN);
    }
    public int getMaxHorizontalScrollPosition() {
      if (root==null) return 0;
      return (int) (root.depth()*xscale + 2*MARGIN);
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


    /**
     * Returns the appropriate stroke weight to draw the tree with.  Scales based on how zoomed in the tree is.
     */
    private double scaledStrokeWeight() {
      return Math.min(0.9*yscale, 3.0);
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
      return (xs - xstart)/xscale;
    }
    private double toRow(double ys) {
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
        else if (mouseEvent.getClickCount() == 2) {
          //if they double-clicked not on a node, zoom in/out
          if (mouseButton == RIGHT) setScaleFactor(yscale / pow(2, 0.5f), mouseX, mouseY);
          else if (mouseButton == LEFT) setScaleFactor(yscale * pow(2, 0.5f), mouseX, mouseY);
        }
      }
    }




    /**
     * Ensures that the tree is not scrolled out of its bounds, and resets it back if it is
     */
    public void checkBounds() {
      //if there's no tree, we can't check the bounds
      if (root==null) return;

      //check horizontal tree scaling
      if (xscale < (getWidth()-2*MARGIN)/root.depth()) {
        //need to rescale tree
        resetTreeX();
      }
      //check horizontal tree position
      if (xstart > MARGIN) {
        xstart = MARGIN;
      } else if (xstart + xscale*root.depth() < getWidth()-MARGIN) {
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

      //notify listeners
      fireStateChanged();
      //redraw tree
      redraw();
    }


    /**
     * Resets the scale and position of the tree horizontally
     */
    public void resetTreeX() {
      if (root==null) return;
      xscale = (getWidth()-2*MARGIN)/root.depth();
      xstart = MARGIN;
     }

    /**
     * Resets the scale and position of the tree vertically
     */
    public void resetTreeY() {
      if (root==null) return;
      yscale = (getHeight()-2*MARGIN)/root.getNumberOfLeaves();
      ystart = MARGIN;
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
      MARGIN = ORIGMARGIN + width*currFont.size + 5;
      //set the tree
      root = newRoot;
      resetTreeX();
      resetTreeY();
      //recalculate the y-offsets of the nodes in the tree
      setYOffsets(newRoot, 0);

      ystart = MARGIN;
      xstart = MARGIN;
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
    public void setScaleFactor(double value, double x, double y) {
      //convert from screen position to position in the tree
      double l = toLength(x);
      double r = toRow(y);
      //make sure we can scale it
      if (value < MAXYSCALE) {
        //only change the scale if the scale is less than the maximum
        yscale = value;
      }
      //set to new values based on new scaling

      xstart = xstart - (toScreenX(l) - x);
      ystart = ystart - (toScreenY(r) - y);

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
      return findNode(root, mouseX, mouseY, 0);
    }

    /**
     * Find the node in the tree _tree_ at the given x and y coordinates.
     *
     * @param  tree  the root of the tree to search in
     * @param  x  the x-value to search for the node at
     * @param  y  the y-value to search for the node at
     * @param  _length  an interal parameter used in recusion; set to 0 when calling
     */
    private Node findNode(Node tree, double x, double y, double _length) {
      if (tree==null) return null;

      //get the y-offset of the root of the tree
      double row = tree.getYOffset();
      //get the x and y coordinates of the current node
      double nodeX = toScreenX(_length);
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
        Node found = findNode(child, x, y, _length + child.getBranchLength());
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
    private void drawTree(Node node, double x) {
      drawTree(node, x, g);
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
    private void drawTree(Node node, double x, PGraphics canvas) {
      if (root==null) return;

      boolean isInternal = !node.isLeaf();

      // Draw the branches first, so they get over-written by the nodes later:
      if (isInternal) {
        if (node.isCollapsed()){
          //if it's an internal, collapsed node, then draw a wedge in its place
          drawWedge(node,x,node.getYOffset(), canvas);
        }
        else {
          //if it's an internal, uncollapsed node, draw brances of the tree
          drawBranches(node, x, node.getYOffset(), canvas);
        }
      }

      //draw the current node over the branches
      drawNode(node, x, node.getYOffset(), canvas);

      //if it's internal and not collapsed, draw all the subtrees
      if (isInternal && !node.isCollapsed()) {
        for (int i=0; i < node.nodes.size(); i++) {
          Node child = node.nodes.get(i);
          drawTree(child, x + child.getBranchLength(), canvas);
        }
      }

      //draw the pie chart?
      if (node.getDrawPie() == true) {
        PieChart pie = new PieChart((int)Math.round(toScreenX(x)), //draw at the same x-value as the node
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
    strokeWeight((float)scaledStrokeWeight());
  }

    /**
     * Draw the actual node, not including branch lines and child nodes.
     *
     * @param  node  the node to draw
     * @param  x  the x value to draw the node at
     * @param  y  the y value to draw the node at
     * @param  canvas  the PGraphics object to draw the node to
     */
    private void drawNode(Node node, double x, double y, PGraphics canvas) {
      //biases are small offsets so the text doesn't overlap the graphics
      double xbias = 5;
      double ybias = 5;

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
        double minX = drawX;
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
      canvas.strokeWeight((float)scaledStrokeWeight());
      canvas.stroke(node.getColor().getRGB());
      canvas.fill(node.getColor().getRGB());
      //draw node label if we need to
      if (node.isLeaf()) {
        if (drawExternalNodeLabels)
            canvas.text(node.getLabel(), (float)(drawX+xbias), (float)(drawY+ybias));
      } else {
          if (drawInternalNodeLabels) {
              canvas.text(node.getLabel(), (float)(drawX+xbias), (float)(drawY+ybias));
          }
      }
      //reset drawing color to default black
      canvas.fill(0);
      canvas.stroke(0);
    }



    /**
     * Draw branch lines connecting a node and its children.
     *
     * @param  node  the node the draw branches for
     * @param  x  the x value of the node
     * @param  y  the y value of the node
     * @param  canvas  the PGraphics object to draw the branches to
     */
    private void drawBranches(Node node, double x, double y, PGraphics canvas) {
      //get the actual screen coordinates
      double xs = toScreenX(x);
      double ys = toScreenY(y);

      //draw vertical line through the node
      canvas.strokeWeight((float)scaledStrokeWeight());
      canvas.stroke(node.getColor().getRGB());
      canvas.line((float)xs, (float)toScreenY( node.nodes.get(0).getYOffset()),
        (float)xs, (float)toScreenY(node.nodes.get(node.nodes.size()-1).getYOffset()));

      //loop over all of the children
      for (int i=0; i < node.nodes.size(); i++) {
        //draw horizontal line from the vertical line to the child node
        canvas.stroke(node.nodes.get(i).getColor().getRGB());
        double yp = toScreenY(node.nodes.get(i).getYOffset());
        canvas.line((float)xs, (float)yp,
          (float)toScreenX(x + node.nodes.get(i).getBranchLength()), (float)yp);
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
    private void drawWedge(Node node, double x, double y, PGraphics canvas) {
      //set up the drawing properties
      canvas.strokeWeight((float)scaledStrokeWeight());
      canvas.stroke(node.getColor().getRGB());
      canvas.fill(node.getColor().getRGB());

      // find heights for wedge
      double top = node.getMaximumYOffset()-y;
      double bottom = y-node.getMinimumYOffset();

      //re-scale so height is 1/2 the number of nodes
      double total = top+bottom;
      double newHeight = total*0.5f;
      top = y + (top/total)*newHeight;
      bottom = y - (bottom/total)*newHeight;

      //find the longest and shortest branch lengths
      double longest = node.longestRootToTipDistance() - node.getBranchLength();
      double shortest = node.shortestRootToTipDistance() - node.getBranchLength();

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
        node.setMinimumYOffset(Math.min(node.getMinimumYOffset(), node.nodes.get(i).getMaximumYOffset()));
      }
    }

    /**
     * Exports a screen caputre of the tree to a PDF file
     *
     * @param  filename  the file to write the image to
     */
    public void exportScreenCapture(String filename) {
      PGraphics canvas = createGraphics(width, height, PDF, filename);
      canvas.beginDraw();
      textFont(currFont);
      drawTree(root, 0, canvas);
      canvas.dispose();
      canvas.endDraw();
    }

    /**
     * Export the entire tree to a PDF file
     *
     * @param  filename  the file to write the image to
     */
    public void exportTreeImage(String filename) {

      //save the current variables
      int oldWidth = getWidth();
      int oldHeight = getHeight();
      double oldXScale = xscale;
      double oldYScale = yscale;
      double oldXStart = xstart;
      double oldYStart = ystart;
      boolean oldDrawText = drawExternalNodeLabels;

      //reset the sizing and zooming so that the tree can be drawn visibly
      double longest = textWidth(root.getLongestLabel());
      double l = root.longestRootToTipDistance();
      double s = root.shortestRootToTipDistance();
      PGraphics canvas = createGraphics((int) ((l/s)*400+MARGIN+longest), (int) (12*root.getNumberOfLeaves() + 2*MARGIN), PDF, filename);
      drawExternalNodeLabels = true;
      int w = (int) ((l/s)*400+MARGIN+longest);
      int h = (int) (12*root.getNumberOfLeaves() + 2*MARGIN);
      xscale = (w-MARGIN-longest)/root.depth();
      yscale = (h-2*MARGIN)/root.getNumberOfLeaves();
      xstart = MARGIN;
      ystart = MARGIN;

      //draw the three to the file
      canvas.beginDraw();
      canvas.textFont(currFont);
      drawTree(root, 0, canvas);
      canvas.dispose();
      canvas.endDraw();

      //go back to how it was
      xscale = oldXScale;
      yscale = oldYScale;
      xstart = oldXStart;
      ystart = oldYStart;
      drawExternalNodeLabels = oldDrawText;

      redraw();

    }
}