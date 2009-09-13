package topiarytool;

import java.awt.*;
import java.util.*;

public class VertexData {
  //parallel arrays of colors and the weight to be drawn with each
  public ArrayList<Color> groupColor = new ArrayList<Color>();
  public ArrayList<Double> groupFraction = new ArrayList<Double>();

  public float weight; //weight (size) of the node
  public String sh; //shape of the node;
  public String label; //label of the vertex
  public float[] coords = new float[3]; //location of the vertex
  public float[] velocity = new float[3]; //velocity of the particle

  public Color getColor() {

    if (groupColor.isEmpty()) {
        return new Color(0,0,0);
    }
    float r,g,b;
    r = g = b = 0;
    double total = 0;
    for (int i = 0; i < groupFraction.size(); i++) {
      total += groupFraction.get(i);
    }
    for (int i = 0; i < groupFraction.size(); i++) {
      r += groupFraction.get(i)/total*(groupColor.get(i).getRed());
      g += groupFraction.get(i)/total*(groupColor.get(i).getGreen());
      b += groupFraction.get(i)/total*(groupColor.get(i).getBlue());
    }
    return new Color((int)r,(int)g,(int)b);
  }

  //merges all colors into just one
  public void mergeColors() {
      Color c = getColor();
      groupColor.clear();
      groupFraction.clear();
      groupColor.add(c);
      groupFraction.add(new Double(1.0));
  }

  public void clearColor() {
      groupColor.clear();
      groupFraction.clear();
  }

}