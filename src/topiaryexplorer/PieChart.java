package topiaryexplorer;


import java.awt.*;
import java.util.*;

public class PieChart {

  int x; //x-coordinate
  int y; //y-coordinate
  int diameter;
  ArrayList<Double> data; //a list of data to pie chart
  ArrayList<Color> colors; //the colors for each datum

  /**
    * Default constructor
    */
  PieChart(int x, int y, int diameter, ArrayList<Double> data, ArrayList<Color> colors) {
    this.x = x;
    this.y = y;
    this.diameter = diameter;
    this.data = data;
    this.colors = colors;
  }
}
