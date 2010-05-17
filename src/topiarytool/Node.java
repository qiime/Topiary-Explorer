package topiarytool;

/**
 * Represents a node in the tree, which may be a leaf or internal node.
 */

import java.awt.*;
import java.util.*;

public class Node {
  private Node parent = null;
  public ArrayList<Node> nodes = new ArrayList(); //children

  private String label = "";
  private String name = "";
  private double branchlength = 0;
  private double yoffset = 0;
  private double xoffset = 0;
  private double roffset = 0; //radius
  private double toffset = 0; //theta
  private double rxoffset = 0;
  private double ryoffset = 0;
  private boolean drawPie = false; //should a pie chart be drawn for this node?
  private double maximumYOffset = 0;
  private double minimumYOffset = 0;
  private double maximumTOffset = 0;
  private double minimumTOffset = 0;
  private double maximumROffset = 0;
  private double minimumROffset = 0;
  private double maximumRXOffset = 0;
  private double minimumRXOffset = 0;
  
  //parallel arrays of colors and the weight to be drawn with each
  private boolean colored = true;
  private ArrayList<Color> groupColor = new ArrayList<Color>();
  private ArrayList<Double> groupWeight = new ArrayList<Double>();
  
  private double lineWidth = 1;

  private boolean collapsed = false; //if true, the children are not shown (draws a wedge)

  Object userObject = null;


  Node() {}

  Node(String _label, double _branchlength) {
    label = _label;
    name = _label;
    branchlength = _branchlength;
  }

  //GETTERS AND SETTERS
  public boolean isLeaf() { return nodes.size() == 0; }
  public void setDrawPie(boolean b) { drawPie = b; }
  public boolean getDrawPie() { return drawPie; }
  public ArrayList<Color> getGroupColor() { return groupColor; }
  public ArrayList<Double> getGroupFraction() { return groupWeight; }
  public boolean isCollapsed() { return collapsed; }
  public void setCollapsed(boolean cond) { collapsed = cond; }
  public void setLabel(String s) { label = s; }
  public String getLabel() { return label; }
  public double getBranchLength() { return branchlength; }
  public void setName(String s) { name = s; }
  public String getName() { return name; }
  public void setBranchLength(double f) { if (f >= 0) branchlength = f; }
  public double getYOffset() { return yoffset; }
  public double getXOffset() { return xoffset; }
  public void setYOffset(double f) { yoffset = f; }
  public void setXOffset(double f) { xoffset = f; }
  public double getROffset() { return roffset; }
  public double getTOffset() { return toffset; }
  public void setROffset(double f) { roffset = f; }
  public void setTOffset(double f) { toffset = f; }  
  public double getRXOffset() { return rxoffset; }
  public double getRYOffset() { return ryoffset; }
  public void setRXOffset(double f) { rxoffset = f; }
  public void setRYOffset(double f) { ryoffset = f; }   
  public Node getParent() { return parent; }
  public double getMaximumYOffset() { return maximumYOffset; }
  public void setMaximumYOffset(double f) { maximumYOffset = f; }
  public double getMinimumYOffset() { return minimumYOffset; }
  public void setMinimumYOffset(double f) { minimumYOffset = f; }
  public double getMaximumTOffset() { return maximumTOffset; }
  public void setMaximumTOffset(double f) { maximumTOffset = f; }
  public double getMinimumTOffset() { return minimumTOffset; }
  public void setMinimumTOffset(double f) { minimumTOffset = f; }
  public double getMaximumROffset() { return maximumROffset; }
  public void setMaximumROffset(double f) { maximumROffset = f; }
  public double getMinimumROffset() { return minimumROffset; }
  public void setMinimumROffset(double f) { minimumROffset = f; }  
  public double getMinimumRXOffset() { return minimumRXOffset; }
  public void setMinimumRXOffset(double f) { minimumRXOffset = f; }    
  public double getMaximumRXOffset() { return maximumRXOffset; }
  public void setMaximumRXOffset(double f) { maximumRXOffset = f; }  
  public double getLineWidth() { return lineWidth; }
  public void setLineWidth(double f) { lineWidth = f; }
  
  public int getNumberOfLeaves() {
      int total = 0;
      if (isLeaf()) {
          total = 1;
      } else {
          for (Node n : nodes) {
              total = total + n.getNumberOfLeaves();
          }
      }
      return total;
  }

  /**
   * Based on the groupWeight and groupColor field, return an overall blended color
   */
 public Color getColor() {
    //if there's no color, use black
    if (!colored) {
        return new Color(0,0,0);
    }
    double r,g,b;
    r = g = b = 0;
    double total = 0;
    for (Double weight : groupWeight) {
      total = total + weight;
    }
    for (int i = 0; i < groupWeight.size(); i++) {
      r += groupWeight.get(i)/total*groupColor.get(i).getRed();
      g += groupWeight.get(i)/total*groupColor.get(i).getGreen();
      b += groupWeight.get(i)/total*groupColor.get(i).getBlue();
    }
    return new Color((float)r/255,(float)g/255,(float)b/255);
  }

  /**
   * Returns a list of all the leaves of the tree
   */
  public ArrayList<Node> getLeaves() {
      ArrayList<Node> result = new ArrayList<Node>();

      if (isLeaf()) {
          result.add(this);
          return result;
      }
      
      for (Node n : nodes) {
          result.addAll(n.getLeaves());
      }
      return result;
  }

  /**
   * Returns all of the nodes of the tree.
   */
  public ArrayList<Node> getNodes() {
      ArrayList<Node> result = new ArrayList<Node>();
      for (Node n: nodes) {
          result.addAll(n.getNodes());
      }
      result.add(this);
      return result;
  }
  
  public void noColor() {
    colored = false;
  }

  public void clearColor() {
    groupWeight = new ArrayList<Double>();
    groupColor = new ArrayList<Color>();
  }

  public void addColor(Color c, double w) {
      groupWeight.add(new Double(w));
      groupColor.add(c);
      colored = true;
  }

  /**
   * Add a child to the tree
   */
  public void addChild(Node child) {
    nodes.add(child);
    child.parent = this;
    updateColorFromChildren();
  }

  public void rotate() {
      Stack<Node> s = new Stack<Node>();
      for (Node n : nodes) {
          s.push(n);
      }
      nodes.clear();
      while (s.size() > 0) {
          nodes.add(s.pop());
      }
  }

  /**
   * Max depth of the tree (as a sum of branch lengths)
   */
  public double depth() {
    double deepest = 0;
    for (int i = 0; i <nodes.size(); i++) {
      double depth = nodes.get(i).depth();
      if (depth > deepest) {
        deepest = depth;
      }
    }
    return deepest + getBranchLength();
  }


  /**
   * Returns the shortest root-to-tip branch length of the tree
   */
  public double shortestRootToTipDistance() {
    if (nodes.size() == 0) {
      return getBranchLength();
    }
    double shortest = 100000000;
    for (int i = 0; i < nodes.size(); i++) {
      double s = nodes.get(i).shortestRootToTipDistance();
      if (s < shortest) {
        shortest = s;
      }
    }
    return shortest + getBranchLength();
  }

  /**
   * Returns the longest root-to-tip branch length of the tree
   */
  public double longestRootToTipDistance() {
    if (nodes.size() == 0) {
      return getBranchLength();
    }
    double longest = -100000000;
    for (int i = 0; i < nodes.size(); i++) {
      double l = nodes.get(i).longestRootToTipDistance();
      if (l > longest) {
        longest = l;
      }
    }
    return longest + getBranchLength();
  }

  /**
   * Returns the longest label of any node in the tree
   */
  public String getLongestLabel() {
    String longest = getLabel();
    for (int i = 0; i < nodes.size(); i++) {
      String lbl = nodes.get(i).getLongestLabel();
      if (lbl.length() > longest.length()) {
        longest = lbl;
      }
    }
    return longest;
  }


  //SORTING METHODS
  public void sortByNumberOfOtus() {
    //sort each of the subtrees
    for (int i=0; i < nodes.size(); i++) {
      nodes.get(i).sortByNumberOfOtus();
    }

    //sort this node
    Collections.sort(nodes, new java.util.Comparator() {
      public int compare(Object o1, Object o2) {
        if ( ((Node)o1).getNumberOfLeaves() < ((Node)o2).getNumberOfLeaves()) {
          return -1;
        } else if ( ((Node)o1).getNumberOfLeaves() > ((Node)o2).getNumberOfLeaves()) {
          return 1;
        } else {
          return 0;
        }
      }
    });
  }
  
  public void sortByBranchLength() {
      for (int i=0; i < nodes.size(); i++) {
        nodes.get(i).sortByBranchLength();
      }

      //sort this node
      Collections.sort(nodes, new java.util.Comparator() {
        public int compare(Object o1, Object o2) {
          if ( ((Node)o1).longestRootToTipDistance() < ((Node)o2).longestRootToTipDistance()) {
            return -1;
          } else if ( ((Node)o1).longestRootToTipDistance() > ((Node)o2).longestRootToTipDistance()) {
            return 1;
          } else {
            return 0;
          }
        }
      });
  }

  public void sortByNumberOfChildren() {
        //sort each of the subtrees
    for (int i=0; i < nodes.size(); i++) {
      nodes.get(i).sortByNumberOfChildren();
    }

    //sort this node
    Collections.sort(nodes, new java.util.Comparator() {
      public int compare(Object o1, Object o2) {
        if ( ((Node)o1).nodes.size() < ((Node)o2).nodes.size()) {
          return -1;
        } else if ( ((Node)o1).nodes.size() > ((Node)o2).nodes.size()) {
          return 1;
        } else {
          return 0;
        }
      }
    });
  }

  /**
   * Set the color by blending the children's colors, weighted by the number of leaves in each child
   * this recursively works over the entire tree.
   */
  public void updateColorFromChildren() {
    if (isLeaf()) { aggregateData(); return; }

    //make the lists empty
    groupColor = new ArrayList<Color>();
    groupWeight = new ArrayList<Double>();
    for (int i=0; i < nodes.size(); i++) {
      //recursion
      nodes.get(i).updateColorFromChildren();

      //get the overall color for this node
      for (int j = 0; j < nodes.get(i).groupColor.size(); j++) {
        groupColor.add(nodes.get(i).groupColor.get(j));
        groupWeight.add(nodes.get(i).groupWeight.get(j));
      }
    }
    aggregateData();
  }
  
  
  public void updateLineWidthsFromChildren() {
    if (isLeaf()) { return; }

    //make the lists empty
    double total = 0;
    for (int i=0; i < nodes.size(); i++) {
      //recursion
      nodes.get(i).updateLineWidthsFromChildren();

      total = total + nodes.get(i).getLineWidth();
    }
    setLineWidth(total/nodes.size());
  }

  /**
   * Put all of the same colors together (so each color is just one wedge)
   */
  public void aggregateData() {
    ArrayList<Color> newGroupColor = new ArrayList<Color>();
    ArrayList<Double> newGroupWeight = new ArrayList<Double>();

    for (int i = 0; i < groupColor.size(); i++) {
      if (newGroupColor.contains(groupColor.get(i))) {
          int index = newGroupColor.indexOf(groupColor.get(i));
          newGroupWeight.set(index, newGroupWeight.get(index) + groupWeight.get(i));
      } else {
        newGroupColor.add(groupColor.get(i));
        newGroupWeight.add(groupWeight.get(i));
      }
    }

    groupWeight = newGroupWeight;
    groupColor = newGroupColor;
  }
}