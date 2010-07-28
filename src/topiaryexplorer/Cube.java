package topiaryexplorer;

import java.awt.*;
import javax.media.opengl.*;

// Simple Cube class, based on Quads
class Cube {

  // Properties
  float w;
  Color color;
  float shiftX, shiftY, shiftZ;

  // Constructor
  Cube(float w, Color color, float shiftX, float shiftY, float shiftZ){
    this.w = w;
    this.color = color;
    this.shiftX = shiftX;
    this.shiftY = shiftY;
    this.shiftZ = shiftZ;
  }

  /* Main cube drawing method, which looks
   more confusing than it really is. It's
   just a bunch of rectangles drawn for
   each cube face */
  public void drawCube(GL gl){


    gl.glBegin(gl.GL_QUADS);
    gl.glColor3f(((float)color.getRed())/255.0f,((float)color.getGreen())/255.0f,((float)color.getBlue())/255.0f);
    // Front face
    gl.glNormal3f(0, 0, -1);
    gl.glVertex3f(-w/2 + shiftX, -w/2 + shiftY, -w/2 + shiftZ);
    gl.glVertex3f(w + shiftX, -w/2 + shiftY, -w/2 + shiftZ);
    gl.glVertex3f(w + shiftX, w + shiftY, -w/2 + shiftZ);
    gl.glVertex3f(-w/2 + shiftX, w + shiftY, -w/2 + shiftZ);

    // Back face
    gl.glNormal3f(0, 0, 1);
    gl.glVertex3f(-w/2 + shiftX, -w/2 + shiftY, w + shiftZ);
    gl.glVertex3f(w + shiftX, -w/2 + shiftY, w + shiftZ);
    gl.glVertex3f(w + shiftX, w + shiftY, w + shiftZ);
    gl.glVertex3f(-w/2 + shiftX, w + shiftY, w + shiftZ);

    // Left face
    gl.glNormal3f(-1, 0, 0);
    gl.glVertex3f(-w/2 + shiftX, -w/2 + shiftY, -w/2 + shiftZ);
    gl.glVertex3f(-w/2 + shiftX, -w/2 + shiftY, w + shiftZ);
    gl.glVertex3f(-w/2 + shiftX, w + shiftY, w + shiftZ);
    gl.glVertex3f(-w/2 + shiftX, w + shiftY, -w/2 + shiftZ);

    // Right face
    gl.glNormal3f(1, 0, 0);
    gl.glVertex3f(w + shiftX, -w/2 + shiftY, -w/2 + shiftZ);
    gl.glVertex3f(w + shiftX, -w/2 + shiftY, w + shiftZ);
    gl.glVertex3f(w + shiftX, w + shiftY, w + shiftZ);
    gl.glVertex3f(w + shiftX, w + shiftY, -w/2 + shiftZ);

    // Top face
    gl.glNormal3f(0, -1, 0);
    gl.glVertex3f(-w/2 + shiftX, -w/2 + shiftY, -w/2 + shiftZ);
    gl.glVertex3f(w + shiftX, -w/2 + shiftY, -w/2 + shiftZ);
    gl.glVertex3f(w + shiftX, -w/2 + shiftY, w + shiftZ);
    gl.glVertex3f(-w/2 + shiftX, -w/2 + shiftY, w + shiftZ);

    // Bottom face
    gl.glNormal3f(0, 1, 0);
    gl.glVertex3f(-w/2 + shiftX, w + shiftY, -w/2 + shiftZ);
    gl.glVertex3f(w + shiftX, w + shiftY, -w/2 + shiftZ);
    gl.glVertex3f(w + shiftX, w + shiftY, w + shiftZ);
    gl.glVertex3f(-w/2 + shiftX, w + shiftY, w + shiftZ);
    gl.glEnd();
  }
}