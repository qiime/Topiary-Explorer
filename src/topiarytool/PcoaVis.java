package topiarytool;

import processing.core.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import java.util.*;
import java.text.*;

/**
 * Panel that holds the Processing-generated TreeVis applet and a pair of
 * scrollbars that are kept in sync with the position of the tree. This panel is
 * actually fairly generic and would work with any Processing applet that
 * provides changeEvents and a couple of accessors to get and set the current
 * view position.
 */
public class PcoaVis extends JPanel implements GLEventListener, MouseMotionListener, KeyListener {

    //links in the graph
    //format is [sp_index, sample_index, weight]
    public float[][] links = null;
    //for each vertex in the graph, we have data associated with it
    public VertexData[] sampleData = null; //sample data
    public VertexData[] spData = null; //species data
    public ArrayList<Double> evals = null; //normalized eigenvalues for each axis

    private float MARGIN = 100;

    private float MINDIAMETER = 2;
    private float MAXDIAMETER=15;
    private float MAXLINEWEIGHT=3;
    
    private Color backgroundColor = new Color(255, 255, 255);
    
    private float SCALE = 1; // scaling for zoom
    private float LINEWIDTHSCALE = 1; //scaling for line width

    private boolean displaySamples = true;
    private boolean displayOtus = true;
    private boolean displayConnections = true;
    private boolean displayAxes = true;
    private boolean displayAxisLabels = true;

    //which PC's to display?
    private int axis1 = 0;
    private int axis2 = 1;
    private int axis3 = 2;
    
    private String axis1label = "";
    private String axis2label = "";
    private String axis3label = "";
    
    private String sampleShape = "Sphere";
    private String otuShape = "Cube";
    
    private boolean colorSamples = true;
    private boolean colorOtus = true;

    private boolean SHIFTPRESSED = false;

    private float prevMouseX = 0;
    private float prevMouseY = 0;
    private boolean mouseRButtonDown = false;
    
    private VTextRenderer textRenderer;

    private String dynamicLayout = "None";

    //rotation
    float xrotation = 0;
    float yrotation = 0;
    //panning
    float xshift = 0;
    float yshift = 0;

    float scaling = -1;
    float meanx = -1;
    float meany = -1;
    float yspread = -1;
    float xspread = -1;
    
    float xsize = 0;
    float ysize = 0;

    float[] attitude = {
      0,0,0};

    private GLU glu = new GLU();

	public PcoaVis() {
	}
	
	public float getScale() { return SCALE; }
	public void setScale(float s) { SCALE = s; }
    public Color getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Color c) { backgroundColor = c; }    
	public float getLineWidthScale() { return LINEWIDTHSCALE; }
	public void setLineWidthScale(float s) { LINEWIDTHSCALE = s; }
	
	public boolean getColorSamples() { return colorSamples; }
	public void setColorSamples(boolean s) { colorSamples = s; }
	public boolean getColorOtus() { return colorOtus; }
	public void setColorOtus(boolean s) { colorOtus = s; }
	public String getSampleShape() { return sampleShape; }
	public void setSampleShape(String s) { sampleShape = s; }
	public String getOtuShape() { return otuShape; }
	public void setOtuShape(String s) { otuShape = s; }
	
	public int getAxis(int i) {
	    if (i == 1) {
	        return axis1;
	    } else if (i == 2) {
	        return axis2;
	    } else if (i == 3) {
	        return axis3;
	    } else {
	        return 0;
	    }
	}
	
    public void setAxis(int i, int p) {
	    if (i == 1) {
	        axis1 = p;
	    } else if (i == 2) {
	        axis2 = p;
	    } else if (i == 3) {
	        axis3 = p;
	    }
	    resetAxisLabels();
	}
	public String getAxisLabel(int i) {
	    if (i == 1) {
	        return axis1label;
	    } else if (i == 2) {
	        return axis2label;
	    } else if (i == 3) {
	        return axis3label;
	    } else {
	        return "";
	    }
	}
	
    public void setAxisLabel(int i, String p) {
	    if (i == 1) {
	        axis1label = p;
	    } else if (i == 2) {
	        axis2label = p;
	    } else if (i == 3) {
	        axis3label = p;
	    }
	}	
	
	public void resetAxisLabels() {
	    DecimalFormat df = new DecimalFormat("#.0#");
	    
	    setAxisLabel(1, "PC" + Integer.toString(getAxis(1)+1) + ": " + 
	        df.format((100*evals.get(getAxis(1)).doubleValue())) +"%");
	    setAxisLabel(2, "PC" + Integer.toString(getAxis(2)+1) + ": " + 
	        df.format((100*evals.get(getAxis(2)).doubleValue())) +"%");
	    setAxisLabel(3, "PC" + Integer.toString(getAxis(3)+1) + ": " + 
	        df.format((100*evals.get(getAxis(3)).doubleValue())) +"%");	        
	}
	
	public void keyPressed(KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
	        SHIFTPRESSED = true;
	    }
	}
	
	public void keyReleased(KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
	        SHIFTPRESSED = false;
	    }	
	}
	
	public void keyTyped(KeyEvent e) {
	}
	
	public void init(GLAutoDrawable glDrawable) {
        GLCapabilities caps = new GLCapabilities();
        caps.setDoubleBuffered(true);
        caps.setHardwareAccelerated(true);

        glDrawable.addMouseMotionListener(this);
        glDrawable.addKeyListener(this);

        GL gl = glDrawable.getGL();     
        textRenderer = new VTextRenderer("Georgia", 18);
	}
    
    public void display(GLAutoDrawable glDrawable) {
        final GL gl = glDrawable.getGL();
        
        gl.glClearColor(backgroundColor.getRed()/255, backgroundColor.getGreen()/255, backgroundColor.getBlue()/255, 0);

        xsize = glDrawable.getWidth()/2;
        ysize = glDrawable.getHeight()/2;
        gl.glOrtho(xsize, -xsize, -ysize, ysize, 1000, -1000);
        gl.glViewport(0,0,glDrawable.getWidth(),glDrawable.getHeight());
        gl.glMatrixMode( GL.GL_PROJECTION );
        gl.glLoadIdentity();
        gl.glOrtho(xsize, -xsize, -ysize, ysize, 1000, -1000);
        gl.glViewport(0,0,glDrawable.getWidth(),glDrawable.getHeight());
        gl.glMatrixMode( GL.GL_MODELVIEW );
        gl.glLoadIdentity();
       
        //gl.glEnable(gl.GL_LINE_SMOOTH);
        //gl.glEnable(gl.GL_BLEND);
        gl.glEnable(gl.GL_DEPTH_TEST);

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        //gl.glEnable(gl.GL_LIGHTING) ;
        //gl.glEnable(gl.GL_LIGHT0) ;
        //gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE) ;
        //gl.glEnable(gl.GL_COLOR_MATERIAL) ;
        //gl.glBlendFunc(gl.GL_SRC_ALPHA,gl.GL_ONE_MINUS_SRC_ALPHA);

        if (links != null && sampleData != null && spData != null) {
            meanx = (getMinX()+getMaxX())/2.0f;
            meany = (getMinY()+getMaxY())/2.0f;
            yspread = getMaxY()-getMinY();
            xspread = getMaxX()-getMinX();
            scaling = Math.min((getWidth()-2*MARGIN)/xspread, (getHeight()-2*MARGIN)/yspread);
            //scale for zooming
            scaling = scaling * getScale();
            
            //print(getMaxY()); print(", "); print(getMinY());print(", "); print(getMaxX()); print(", "); print(getMinX()); print(", "); println(scaling);
            gl.glTranslatef((getWidth()/2.0f)-scaling*meanx, (getHeight()/2.0f)-scaling*meany, -getMaxZ()*scaling);
            //print((getWidth()/2.0)-scaling*meanx); print(", ");println((getHeight()/2.0)-scaling*meany);

            //panning
            gl.glTranslatef(xshift, yshift, 0);
  
            gl.glRotatef(xrotation, 1, 0, 0);
            gl.glRotatef(yrotation, 0, 1, 0);

            drawPCoA(gl);

            if (dynamicLayout.equals("Spring")) {
              updateSpringLayout();
            } else if (dynamicLayout.equals("Force")) {
              updateForceDirectedLayout();
            }

         }

        gl.glFlush();

    }

    public void displayChanged(GLAutoDrawable glDrawable, boolean modeChanged,boolean deviceChanged) {
    }

    public void reshape(GLAutoDrawable glDrawable, int x, int y, int width, int height) {

    }

    public VertexData[] getSpVertices() {
      if (sampleData==null) {
        return new VertexData[0];
      }
      else {
        return spData;
      }
    }

public void setDyamicLayout(String s) {
  dynamicLayout = s;
}

    
public void mousePressed(MouseEvent e) { 
  if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
      mouseRButtonDown = true; 
  } 
  prevMouseX = e.getX();
  prevMouseY = e.getY();
} 

public void mouseReleased(MouseEvent e) { 
  if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
      mouseRButtonDown = false; 
  } 
}

public void mouseDragged(MouseEvent e) {
  if (SHIFTPRESSED) {
    //pan
    xshift += prevMouseX-e.getX();
    yshift += prevMouseY-e.getY();
  } else {
      float rate = 0.5f;
      xrotation += (prevMouseY-e.getY()) * rate;
      yrotation += (e.getX()-prevMouseX) * rate;
  }
  prevMouseX = e.getX();
  prevMouseY = e.getY();

}

public void mouseMoved(MouseEvent e) {
  prevMouseX = e.getX();
  prevMouseY = e.getY();
}


public void updateForceDirectedLayout() {
  float[][][] forces = getSpringForces();
  float[][][] forces2 = getChargeForces();

  for (int i = 0; i < forces.length; i++) {
    for (int j = 0; j < forces[i].length; j++) {
      for (int k = 0; k < forces[i][j].length; k++) {
        forces[i][j][k] += forces2[i][j][k];
      }
    }
  }
  applyForces(forces);
}

public void updateSpringLayout() {
  float[][][] forces = getSpringForces();
  applyForces(forces);
}


public float[][][] getChargeForces() {

  float[][] net_forces_sp = new float[spData.length][];
  for (int i = 0; i < spData.length; i++) {
    net_forces_sp[i] = new float[3];
    net_forces_sp[i][0] = net_forces_sp[i][1] = net_forces_sp[i][2] = 0;
  }
  float[][] net_forces_sample = new float[sampleData.length][];
  for (int i = 0; i < sampleData.length; i++) {
    net_forces_sample[i] = new float[3];
    net_forces_sample[i][0] = net_forces_sample[i][1] = net_forces_sample[i][2] = 0;
  }

  for (int i = 0; i < spData.length; i++) {
    VertexData vi = spData[i];
    for (int j = 0; j < spData.length; j++) {
      if (i==j) continue;
      VertexData vj = spData[j];
      float dx = vi.coords[getAxis(1)] - vj.coords[getAxis(1)];
      float dy = vi.coords[getAxis(2)] - vj.coords[getAxis(2)];
      float dz = vi.coords[getAxis(3)] - vj.coords[getAxis(3)];
      float distance = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
      float weight1 = vi.weight;
      float weight2 = vj.weight;
      if (distance==0 || weight1 == 0 || weight2 == 0) continue;
      float chargeForce = (1/(weight1*weight2))/(distance*distance);
      net_forces_sp[i][0] += chargeForce*dx;
      net_forces_sp[i][1] += chargeForce*dy;
      net_forces_sp[i][2] += chargeForce*dz;
      net_forces_sp[j][0] += chargeForce*-dx;
      net_forces_sp[j][1] += chargeForce*-dy;
      net_forces_sp[j][2] += chargeForce*-dz;
    }
    for (int j = 0; j < sampleData.length; j++) {
      VertexData vj = sampleData[j];
      float dx = vi.coords[getAxis(1)] - vj.coords[getAxis(1)];
      float dy = vi.coords[getAxis(2)] - vj.coords[getAxis(2)];
      float dz = vi.coords[getAxis(3)] - vj.coords[getAxis(3)];
      float distance = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
      float weight1 = vi.weight;
      float weight2 = vj.weight;
      if (distance==0 || weight1 == 0 || weight2 == 0) continue;
      float chargeForce = (1/(weight1*weight2))/(distance*distance);
      net_forces_sp[i][0] += chargeForce*dx;
      net_forces_sp[i][1] += chargeForce*dy;
      net_forces_sp[i][2] += chargeForce*dz;
      net_forces_sample[j][0] += chargeForce*-dx;
      net_forces_sample[j][1] += chargeForce*-dy;
      net_forces_sample[j][2] += chargeForce*-dz;
    }
  }

  for (int i = 0; i < sampleData.length; i++) {
    VertexData vi = sampleData[i];
    for (int j = 0; j < sampleData.length; j++) {
      if (i==j) continue;
      VertexData vj = sampleData[j];
      float dx = vi.coords[getAxis(1)] - vj.coords[getAxis(1)];
      float dy = vi.coords[getAxis(2)] - vj.coords[getAxis(2)];
      float dz = vi.coords[getAxis(3)] - vj.coords[getAxis(3)];
      float distance = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
      float weight1 = vi.weight;
      float weight2 = vj.weight;
      if (distance==0 || weight1 == 0 || weight2 == 0) continue;
      float chargeForce = (1/(weight1*weight2))/(distance*distance);
      net_forces_sample[i][0] += chargeForce*dx;
      net_forces_sample[i][1] += chargeForce*dy;
      net_forces_sample[i][2] += chargeForce*dz;
      net_forces_sample[j][0] += chargeForce*-dx;
      net_forces_sample[j][1] += chargeForce*-dy;
      net_forces_sample[j][2] += chargeForce*-dz;
    }
    for (int j = 0; j < spData.length; j++) {
      VertexData vj = spData[j];
      float dx = vi.coords[getAxis(1)] - vj.coords[getAxis(1)];
      float dy = vi.coords[getAxis(2)] - vj.coords[getAxis(2)];
      float dz = vi.coords[getAxis(3)] - vj.coords[getAxis(3)];
      float distance = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
      float weight1 = vi.weight;
      float weight2 = vj.weight;
      if (distance==0 || weight1 == 0 || weight2 == 0) continue;
      float chargeForce = (1/(weight1*weight2))/(distance*distance);
      net_forces_sample[i][0] += chargeForce*dx;
      net_forces_sample[i][1] += chargeForce*dy;
      net_forces_sample[i][2] += chargeForce*dz;
      net_forces_sp[j][0] += chargeForce*-dx;
      net_forces_sp[j][1] += chargeForce*-dy;
      net_forces_sp[j][2] += chargeForce*-dz;
    }
  }

  float[][][] result = new float[2][][];
  result[0] = net_forces_sp;
  result[1] = net_forces_sample;
  return result;

}

public float[][][] getSpringForces() {

  float[][] net_forces_sp = new float[spData.length][];
  for (int i = 0; i < spData.length; i++) {
    net_forces_sp[i] = new float[3];
    net_forces_sp[i][0] = net_forces_sp[i][1] = net_forces_sp[i][2] = 0;
  }
  float[][] net_forces_sample = new float[sampleData.length][];
  for (int i = 0; i < sampleData.length; i++) {
    net_forces_sample[i] = new float[3];
    net_forces_sample[i][0] = net_forces_sample[i][1] = net_forces_sample[i][2] = 0;
  }

  //calculate spring forces
  for (int i = 0; i < links.length; i++) {
    int spindex = (int)links[i][0];
    int sampleindex = (int)links[i][1];
    VertexData vi = spData[spindex];
    VertexData vj = sampleData[sampleindex];
    float weight = links[i][2];
    if (weight == 0) continue;
    float dx = vi.coords[getAxis(1)] - vj.coords[getAxis(1)];
    float dy = vi.coords[getAxis(2)] - vj.coords[getAxis(2)];
    float dz = vi.coords[getAxis(3)] - vj.coords[getAxis(3)];
    float distance = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
    float springForce = -0.001f*(distance-1/weight);
    //print(distance); print(", "); print(1/weight); print(", ");println(springForce);
    net_forces_sp[spindex][0] += springForce*dx;
    net_forces_sp[spindex][1] += springForce*dy;
    net_forces_sp[spindex][2] += springForce*dz;
    net_forces_sample[sampleindex][0] += springForce*-dx;
    net_forces_sample[sampleindex][1] += springForce*-dy;
    net_forces_sample[sampleindex][2] += springForce*-dz;
  }

  float[][][] result = new float[2][][];
  result[0] = net_forces_sp;
  result[1] = net_forces_sample;
  return result;

}

public void applyForces(float[][][] forces) {
  float maxforce = 100;
  float minforce = -100;

  float[][] net_forces_sp = forces[0];
  float[][] net_forces_sample = forces[1];

  float damping = 0.95f;
  for (int i = 0; i < spData.length; i++) {
    VertexData vi = spData[i];
    vi.velocity[0] = (vi.velocity[0] + Math.max(Math.min(net_forces_sp[i][0], maxforce),minforce)) * damping;
    vi.velocity[1] = (vi.velocity[1] + Math.max(Math.min(net_forces_sp[i][1], maxforce),minforce)) * damping;
    vi.velocity[2] = (vi.velocity[2] + Math.max(Math.min(net_forces_sp[i][2], maxforce),minforce)) * damping;
    vi.coords[getAxis(1)] += vi.velocity[0];
    vi.coords[getAxis(2)] += vi.velocity[1];
    vi.coords[getAxis(3)] += vi.velocity[2];
  }
  for (int i = 0; i < sampleData.length; i++) {
    VertexData vi = sampleData[i];
    vi.velocity[0] = (vi.velocity[0] + Math.max(Math.min(net_forces_sample[i][0], maxforce),minforce)) * damping;
    vi.velocity[1] = (vi.velocity[1] + Math.max(Math.min(net_forces_sample[i][1], maxforce),minforce)) * damping;
    vi.velocity[2] = (vi.velocity[2] + Math.max(Math.min(net_forces_sample[i][2], maxforce),minforce)) * damping;
    vi.coords[getAxis(1)] += vi.velocity[0];
    vi.coords[getAxis(2)] += vi.velocity[1];
    vi.coords[getAxis(3)] += vi.velocity[2];
  }
}

public void drawPCoA(GL gl) {

  float maxx = getMaxX();
  float maxy = getMaxY();
  float minx = getMinX();
  float miny = getMinY();
  float minz = getMinZ();
  float maxz = getMaxZ();

  float maxweight = getMaxVertexWeight();
  
  if (displayConnections) {
    //draw all the links between vertices
    for (int i = 0; i < links.length; i++) {
      int link1 = (int) links[i][0]; //species
      int link2 = (int) links[i][1]; //sample
      float weight = links[i][2];
      //draw line
      if (weight == 0) {
        continue;
      }
      gl.glLineWidth(getLineWidthScale() * MAXLINEWEIGHT*weight/maxweight);

      float r,g,b;
      r = g = b = 0;
      float total = 0;
      //    for (int j = 0; j < spData[link1].groupFraction.length; j++) {
      //      total = total + spData[link1].groupFraction[j];
      //    }
      for (int j = 0; j < sampleData[link2].groupFraction.size(); j++) {
        total = (float) (total + sampleData[link2].groupFraction.get(j));
      }
      //    for (int j = 0; j < spData[link1].groupFraction.length; j++) {
      //      r += spData[link1].groupFraction[j]/total*(red(spData[link1].groupColor[j]));
      //      g += spData[link1].groupFraction[j]/total*(green(spData[link1].groupColor[j]));
      //      b += spData[link1].groupFraction[j]/total*(blue(spData[link1].groupColor[j]));
      //    }
      for (int j = 0; j < sampleData[link2].groupFraction.size(); j++) {
        r += sampleData[link2].groupFraction.get(j)/total*(sampleData[link2].groupColor.get(j).getRed());
        g += sampleData[link2].groupFraction.get(j)/total*(sampleData[link2].groupColor.get(j).getGreen());
        b += sampleData[link2].groupFraction.get(j)/total*(sampleData[link2].groupColor.get(j).getBlue());
      }


      gl.glBegin(gl.GL_LINES);
      //gl.glColor3f(((float)r)/255.0f,((float)g)/255.0f,((float)b)/255.0f);
      //make edges black
      gl.glColor3f(0f,0f,0f);
      gl.glVertex3f(scaling*spData[link1].coords[getAxis(1)],
      scaling*spData[link1].coords[getAxis(2)],
      scaling*spData[link1].coords[getAxis(3)]);
      gl.glVertex3f( scaling*sampleData[link2].coords[getAxis(1)],
      scaling*sampleData[link2].coords[getAxis(2)],
      scaling*sampleData[link2].coords[getAxis(3)]);
      gl.glEnd();
    }
  }

  //draw all the spData/sampleData vertices on top of the lines

  VertexData[] dataset;
  for (int d = 0; d < 2; d++) {
    if (d==0) {
      dataset = spData;
    }
    else {
      dataset = sampleData;
    }
    if (dataset==sampleData && !displaySamples) continue;
    if (dataset==spData && !displayOtus) continue;
    for (int i = 0; i < dataset.length; i++) {
      //fill(dataset[i].getColor(), 200);
      Color cl;
      if ( (dataset==sampleData && getColorSamples()) || (dataset==spData && getColorOtus()) ) {
        cl = dataset[i].getColor();
      } else {
        cl = new Color(0,0,0);
      }

      if ( (dataset == spData && getOtuShape().equals("Sphere")) ||
            (dataset == sampleData && getSampleShape().equals("Sphere")) ) {

            GLUquadric quadric=glu.gluNewQuadric();
            glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
    
            gl.glPushMatrix();
            gl.glTranslatef(scaling*dataset[i].coords[getAxis(1)],
                scaling*dataset[i].coords[getAxis(2)],
                scaling*dataset[i].coords[getAxis(3)]);
            gl.glColor3f(((float)cl.getRed())/255.0f,((float)cl.getGreen())/255.0f,((float)cl.getBlue())/255.0f);
            float weight = dataset[i].weight;
            glu.gluSphere(quadric, (MAXDIAMETER-MINDIAMETER)*weight/maxweight + MINDIAMETER, 12,12);
            gl.glPopMatrix();

      } else if ( (dataset == spData && getOtuShape().equals("Cube")) ||
            (dataset == sampleData && getSampleShape().equals("Cube")) ) {
            
            float weight = dataset[i].weight;
            Cube c = new Cube((MAXDIAMETER-MINDIAMETER)*dataset[i].weight/maxweight + MINDIAMETER,
                cl,
                scaling*dataset[i].coords[getAxis(1)],
                scaling*dataset[i].coords[getAxis(2)],
                scaling*dataset[i].coords[getAxis(3)]);
            c.drawCube(gl);
      } else if ( (dataset == spData && getOtuShape().equals("Tetrahedron")) ||
            (dataset == sampleData && getSampleShape().equals("Tetrahedron")) ) {
            gl.glPushMatrix();
            gl.glTranslatef(scaling*dataset[i].coords[getAxis(1)],
                scaling*dataset[i].coords[getAxis(2)],
                scaling*dataset[i].coords[getAxis(3)]);
            float weight = dataset[i].weight;
            float edgeLength = (MAXDIAMETER-MINDIAMETER)*weight/maxweight + MINDIAMETER;
            gl.glBegin(gl.GL_TRIANGLES);
            gl.glColor3f(((float)cl.getRed())/255.0f,((float)cl.getGreen())/255.0f,((float)cl.getBlue())/255.0f);
            // Front
            gl.glVertex3f(0.0f, edgeLength, 0.0f);
            gl.glVertex3f(-edgeLength, -edgeLength, edgeLength);
            gl.glVertex3f(edgeLength, -edgeLength, edgeLength);   
            // Right Side Facing Front
            gl.glVertex3f(0.0f, edgeLength, 0.0f);
            gl.glVertex3f(edgeLength, -edgeLength, edgeLength);
            gl.glVertex3f(0.0f, -edgeLength, -edgeLength);
            // Left Side Facing Front
            gl.glVertex3f(0.0f, edgeLength, 0.0f);
            gl.glVertex3f(0.0f, -edgeLength, -edgeLength);
            gl.glVertex3f(-edgeLength, -edgeLength, edgeLength);
            // Bottom
            gl.glVertex3f(-edgeLength, -edgeLength, edgeLength);
            gl.glVertex3f(edgeLength, -edgeLength, edgeLength);
            gl.glVertex3f(0.0f, -edgeLength, -edgeLength);
            gl.glEnd();
            gl.glPopMatrix();
            
      }
    }
  }

  //Draw the axes
  float axeslength = (float)Math.max(Math.max((maxx-minx), (maxy-miny)), (maxz-minz));
  if (displayAxes) {

    gl.glLineWidth(5);
    gl.glBegin(gl.GL_LINES);
    gl.glColor3f(0,0,0);
    gl.glVertex3f(scaling*minx,scaling*maxy,scaling*maxz);
    gl.glVertex3f(scaling*(minx+axeslength),scaling*maxy,scaling*maxz);
    gl.glEnd();

    gl.glBegin(gl.GL_LINES);
    gl.glVertex3f(scaling*minx,scaling*maxy,scaling*maxz);
    gl.glVertex3f(scaling*minx,scaling*(maxy-axeslength),scaling*maxz);
    gl.glEnd();

    gl.glBegin(gl.GL_LINES);
    gl.glVertex3f(scaling*minx,scaling*maxy,scaling*maxz);
    gl.glVertex3f(scaling*minx,scaling*maxy,scaling*(maxz-axeslength));
    gl.glEnd();
        
  }
  if (displayAxisLabels) {
      gl.glPushMatrix();
    gl.glTranslatef(scaling*(minx+axeslength),scaling*maxy,scaling*maxz);
    gl.glRotatef(-yrotation, 0, 1, 0);
    gl.glRotatef(-xrotation, 1, 0, 0);
    gl.glRotatef(180, 0, 1, 0);
    textRenderer.print(getAxisLabel(2), 
        0.0f, 0.0f, 0.0f);
    gl.glPopMatrix();
    
    gl.glPushMatrix();
    gl.glTranslatef(scaling*minx,scaling*(maxy-axeslength),scaling*maxz);
    gl.glRotatef(-yrotation, 0, 1, 0);
    gl.glRotatef(-xrotation, 1, 0, 0);
    gl.glRotatef(180, 0, 1, 0);
    textRenderer.print(getAxisLabel(1), 
        0.0f, 0.0f, 0.0f);
    gl.glPopMatrix();
    
    gl.glPushMatrix();
    gl.glTranslatef(scaling*minx,scaling*maxy,scaling*(maxz-axeslength));
    gl.glRotatef(-yrotation, 0, 1, 0);
    gl.glRotatef(-xrotation, 1, 0, 0);
    gl.glRotatef(180, 0, 1, 0);
    textRenderer.print(getAxisLabel(3), 
        0.0f, 0.0f, 0.0f);
    gl.glPopMatrix();

  
  }

}

    public float getMaxX() {
      float m = spData[0].coords[getAxis(1)];
      for (int i = 0; i < spData.length; i++) {
        m = (float)Math.max(m, spData[i].coords[getAxis(1)]);
      }
      for (int i = 0; i < sampleData.length; i++) {
        m = (float)Math.max(m, sampleData[i].coords[getAxis(1)]);
      }
      return m;
    }

    public float getMaxY() {
      float m = spData[0].coords[getAxis(2)];
      for (int i = 0; i < spData.length; i++) {
        m = (float)Math.max(m, spData[i].coords[getAxis(2)]);
      }
      for (int i = 0; i < sampleData.length; i++) {
        m = (float)Math.max(m, sampleData[i].coords[getAxis(2)]);
      }
      return m;
    }

    public float getMinX() {
      float m = spData[0].coords[getAxis(1)];
      for (int i = 0; i < spData.length; i++) {
        m = (float)Math.min(m, spData[i].coords[getAxis(1)]);
      }
      for (int i = 0; i < sampleData.length; i++) {
        m = (float)Math.min(m, sampleData[i].coords[getAxis(1)]);
      }
      return m;
    }

    public float getMinY() {
      float m = spData[0].coords[getAxis(2)];
      for (int i = 0; i < spData.length; i++) {
        m = (float)Math.min(m, spData[i].coords[getAxis(2)]);
      }
      for (int i = 0; i < sampleData.length; i++) {
        m = (float)Math.min(m, sampleData[i].coords[getAxis(2)]);
      }
      return m;
    }

    public float getMaxZ() {
      float m = spData[0].coords[getAxis(3)];
      for (int i = 0; i < spData.length; i++) {
        m = (float)Math.max(m, spData[i].coords[getAxis(3)]);
      }
      for (int i = 0; i < sampleData.length; i++) {
        m = (float)Math.max(m, sampleData[i].coords[getAxis(3)]);
      }
      return m;
    }

    public float getMinZ() {
      float m = spData[0].coords[getAxis(3)];
      for (int i = 0; i < spData.length; i++) {
        m = (float)Math.min(m, spData[i].coords[getAxis(3)]);
      }
      for (int i = 0; i < sampleData.length; i++) {
        m = (float)Math.min(m, sampleData[i].coords[getAxis(3)]);
      }
      return m;
    }



    public float getMaxVertexWeight() {
      float m = 0;
      for (int i = 0; i < spData.length; i++) {
        m = (float)Math.max(m, spData[i].weight);
      }
      for (int i = 0; i < sampleData.length; i++) {
        m = (float)Math.max(m, sampleData[i].weight);
      }
      return m;
    }

    public float getMaxLinkWeight() {
      float m = 0;
      for (int i = 0; i < links.length; i++) {
        m = (float)Math.max(m, links[i][2]);
      }
      return m;
    }

    public void setDisplaySamples(boolean v) {
      displaySamples = v;
    }
    public void setDisplayOtus(boolean v) {
      displayOtus = v;
    }
    public void setDisplayConnections(boolean v) {
      displayConnections = v;
    }
    public void setDisplayAxes(boolean v) {
      displayAxes = v;
    }
    public void setDisplayAxisLabels(boolean v) {
      displayAxisLabels = v;
    }    

}
