package topiaryexplorer;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import javax.swing.*;
import processing.core.*;
import processing.pdf.*;
import java.awt.*;
import java.util.*;

public class PieChartVis extends PApplet {
    private int radius = 148;
    private PieChart pie;
    
    public void setup() {
        size(200, 150);
        smooth();
        draw();
    }
    
    public void draw() {
      //draw the chart
      try {
         background(0xffededed);
         smooth();
         drawPieChart(pie, g);
      } catch (Exception e) {
          // System.out.println("cant draw pie chart yet");
      }
    }
    
    public void setPieChartVis(Node root) {
        pie = new PieChart(100, 75, radius, root.getGroupBranchFraction(),
          root.getGroupBranchColor());
          // redraw();
    }
    
    public void drawPieChart(PieChart pie, PGraphics canvas) {
    // canvas.text("hurf",0,0);
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
    canvas.noStroke();
    canvas.strokeWeight(1);
    for (int i = 0; i < angles.length; i++) {
      canvas.fill(pie.colors.get(i).getRGB());
      canvas.arc(pie.x, pie.y,(float) pie.diameter, (float)pie.diameter, (float)lastAng, (float)(lastAng+radians((float)angles[i])));
      double midangle = lastAng + radians((float)angles[i])/2.0;
      lastAng  = lastAng + radians((float)angles[i]);
    }
  }
}