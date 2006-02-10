/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display3d.simple3d;
import org.opensourcephysics.controls.*;

/**
 * <p>Title: ElementCylinder</p>
 * <p>Description: Painter's algorithm implementation of a Cylinder</p>
 * @author Francisco Esquembre
 * @version March 2005
 */
public class ElementCone extends AbstractTile implements org.opensourcephysics.display3d.core.ElementCone {
  // Configuration variables
  private boolean closedBottom = true, closedTop = true;
  private boolean closedLeft = true, closedRight = true;
  private int minAngle = 0, maxAngle = 360;
  private double truncationHeight = Double.NaN;

  // Implementation variables
  private boolean changeNTiles = true;
  private int nr = -1, nu = -1, nz = -1; // Make sure arrays are allocated
  private double[][][] standardCone = null;

  { // Initialization block
    getStyle().setResolution(new Resolution(3, 12, 5));
  }

  // -------------------------------------
  // Configuration
  // -------------------------------------
  public void setTruncationHeight(double height) {
    if(height<0) {
      height = Double.NaN;
    }
    this.truncationHeight = height;
    setElementChanged(true);
    changeNTiles = true;
  }

  public double getTruncationHeight() {
    return truncationHeight;
  }

  public void setClosedBottom(boolean close) {
    this.closedBottom = close;
    setElementChanged(true);
    changeNTiles = true;
  }

  public boolean isClosedBottom() {
    return this.closedBottom;
  }

  public void setClosedTop(boolean close) {
    this.closedTop = close;
    setElementChanged(true);
    changeNTiles = true;
  }

  public boolean isClosedTop() {
    return this.closedTop;
  }

  public void setClosedLeft(boolean close) {
    this.closedLeft = close;
    setElementChanged(true);
    changeNTiles = true;
  }

  public boolean isClosedLeft() {
    return this.closedLeft;
  }

  public void setClosedRight(boolean close) {
    this.closedRight = close;
    setElementChanged(true);
    changeNTiles = true;
  }

  public boolean isClosedRight() {
    return this.closedRight;
  }

  public void setMinimumAngle(int angle) {
    this.minAngle = angle;
    setElementChanged(true);
    changeNTiles = true;
  }

  public int getMinimumAngle() {
    return this.minAngle;
  }

  public void setMaximumAngle(int angle) {
    this.maxAngle = angle;
    setElementChanged(true);
    changeNTiles = true;
  }

  public int getMaximumAngle() {
    return this.maxAngle;
  }

  // -------------------------------------
  // Private or protected methods
  // -------------------------------------
  protected synchronized void computeCorners() {
    int theNr = 1, theNu = 1, theNz = 1;
    double angle1 = minAngle, angle2 = maxAngle;
    if(Math.abs(angle2-angle1)>360) {
      angle2 = angle1+360;
    }
    org.opensourcephysics.display3d.core.Resolution res = getRealStyle().getResolution();
    if(res!=null) {
      switch(res.getType()) {
      case Resolution.DIVISIONS :
        theNr = Math.max(res.getN1(), 1);
        theNu = Math.max(res.getN2(), 1);
        theNz = Math.max(res.getN3(), 1);
        break;
      case Resolution.MAX_LENGTH :
        double dx = Math.abs(getSizeX())/2, dy = Math.abs(getSizeY())/2, dz = Math.abs(getSizeZ());
        if(!Double.isNaN(truncationHeight)) {
          dz = Math.min(dz, truncationHeight);
        }
        theNr = Math.max((int) Math.round(0.49+Math.max(dx, dy)/res.getMaxLength()), 1);
        theNu = Math.max((int) Math.round(0.49+Math.abs(angle2-angle1)*TO_RADIANS*(dx+dy)/res.getMaxLength()), 1);
        theNz = Math.max((int) Math.round(0.49+dz/res.getMaxLength()), 1);
        break;
      }
    }
    if(nr!=theNr||nu!=theNu||nz!=theNz||changeNTiles) { // Reallocate arrays
      nr = theNr;
      nu = theNu;
      nz = theNz;
      changeNTiles = false;
      double height = truncationHeight/getSizeZ();
      if(!Double.isNaN(height)) {
        height = Math.min(height, 1.0);
      }
      standardCone = createStandardCone(nr, nu, nz, angle1, angle2, closedTop, closedBottom, closedLeft, closedRight, height);
      setCorners(new double[standardCone.length][4][3]);
    }
    for(int i = 0;i<numberOfTiles;i++) {
      for(int j = 0, sides = corners[i].length;j<sides;j++) {
        System.arraycopy(standardCone[i][j], 0, corners[i][j], 0, 3);
        sizeAndToSpaceFrame(corners[i][j]);
      }
    }
    setElementChanged(false);
  }

  static final protected double TO_RADIANS = Math.PI/180.0;
  static final protected double[] vectorx = {1.0, 0.0, 0.0}, // Standard vertical Cone
                                  vectory = {0.0, 1.0, 0.0}, vectorz = {0.0, 0.0, 1.0};

  static private double[][][] createStandardCone(int nr, int nu, int nz, double angle1, double angle2, boolean top, boolean bottom, boolean left, boolean right, double height) {
    int totalN = nu*nz;
    if(bottom) {
      totalN += nr*nu;
    }
    if(!Double.isNaN(height)&&top) {
      totalN += nr*nu;
    }
    if(Math.abs(angle2-angle1)<360) {
      if(left) {
        totalN += nr*nz;
      }
      if(right) {
        totalN += nr*nz;
      }
    }
    double[][][] data = new double[totalN][4][3];
    // Compute sines and cosines
    double[] cosu = new double[nu+1], sinu = new double[nu+1];
    for(int u = 0;u<=nu;u++) {     // compute sines and cosines
      double angle = ((nu-u)*angle1+u*angle2)*TO_RADIANS/nu;
      cosu[u] = Math.cos(angle)/2; // The /2 is because the element is centered
      sinu[u] = Math.sin(angle)/2;
    }
    // Now compute the tiles
    int tile = 0;
    double[] center = new double[] {-vectorz[0]/2, -vectorz[1]/2, -vectorz[2]/2};
    {                                     // Tiles along the z axis
      double N;
      if(Double.isNaN(height)) {
        N = nz;
      } else if(height==0) {
        N = Integer.MAX_VALUE;
      } else {
        N = nz/height;
      }
      double aux = 1.0/N;
      for(int j = 0;j<nz;j++) {
        for(int u = 0;u<nu;u++, tile++) { // This ordering is important for the computations below (see ref)
          for(int k = 0;k<3;k++) {
            data[tile][0][k] = center[k]+(cosu[u]*vectorx[k]+sinu[u]*vectory[k])*(N-j)/N+j*aux*vectorz[k];
            data[tile][1][k] = center[k]+(cosu[u+1]*vectorx[k]+sinu[u+1]*vectory[k])*(N-j)/N+j*aux*vectorz[k];
            data[tile][2][k] = center[k]+(cosu[u+1]*vectorx[k]+sinu[u+1]*vectory[k])*(N-j-1)/N+(j+1)*aux*vectorz[k];
            data[tile][3][k] = center[k]+(cosu[u]*vectorx[k]+sinu[u]*vectory[k])*(N-j-1)/N+(j+1)*aux*vectorz[k];
          }
        }
      }
    }
    if(bottom) {                                                      // Tiles at bottom
      //int ref = 0;                                                    // not used
      for(int u = 0;u<nu;u++) {
        for(int i = 0;i<nr;i++, tile++) {
          for(int k = 0;k<3;k++) {
            data[tile][0][k] = ((nr-i)*center[k]+i*data[u][0][k])/nr; // should be ref+u
            data[tile][1][k] = ((nr-i-1)*center[k]+(i+1)*data[u][0][k])/nr; // should be ref+u
            data[tile][2][k] = ((nr-i-1)*center[k]+(i+1)*data[u][1][k])/nr; // should be ref+u
            data[tile][3][k] = ((nr-i)*center[k]+i*data[u][1][k])/nr; // should be ref+u
          }
        }
      }
    }
    if(!Double.isNaN(height)&&top) { // Tiles at top
      int ref = nu*(nz-1);
      center[0] = vectorz[0];
      center[1] = vectorz[1];
      if(Double.isNaN(height)) {
        center[2] = vectorz[2]-0.5;
      } else {
        center[2] = height*vectorz[2]-0.5;
      }
      for(int u = 0;u<nu;u++) {
        for(int i = 0;i<nr;i++, tile++) {
          for(int k = 0;k<3;k++) {
            data[tile][0][k] = ((nr-i)*center[k]+i*data[ref+u][3][k])/nr;
            data[tile][1][k] = ((nr-i-1)*center[k]+(i+1)*data[ref+u][3][k])/nr;
            data[tile][2][k] = ((nr-i-1)*center[k]+(i+1)*data[ref+u][2][k])/nr;
            data[tile][3][k] = ((nr-i)*center[k]+i*data[ref+u][2][k])/nr;
          }
        }
      }
    }
    if(Math.abs(angle2-angle1)<360) { // No need to close left or right if the Cylinder is 'round' enough
      center[0] = -vectorz[0]/2;
      center[1] = -vectorz[1]/2;
      center[2] = -vectorz[2]/2;
      if(right) { // Tiles at right
        int ref = 0;
        double N;
        double[] nextCenter = new double[3];
        if(Double.isNaN(height)) {
          N = nz;
        } else if(height==0) {
          N = Integer.MAX_VALUE;
        } else {
          N = nz/height;
        }
        double aux = 1.0/N;
        for(int j = 0;j<nz;j++, ref += nu) {
          center[0] = j*aux*vectorz[0];
          center[1] = j*aux*vectorz[1];
          center[2] = j*aux*vectorz[2]-0.5;
          nextCenter[0] = (j+1)*aux*vectorz[0];
          nextCenter[1] = (j+1)*aux*vectorz[1];
          nextCenter[2] = (j+1)*aux*vectorz[2]-0.5;
          for(int i = 0;i<nr;i++, tile++) {
            for(int k = 0;k<3;k++) {
              data[tile][0][k] = ((nr-i)*center[k]+i*data[ref][0][k])/nr;
              data[tile][1][k] = ((nr-i-1)*center[k]+(i+1)*data[ref][0][k])/nr;
              data[tile][2][k] = ((nr-i-1)*nextCenter[k]+(i+1)*data[ref][3][k])/nr;
              data[tile][3][k] = ((nr-i)*nextCenter[k]+i*data[ref][3][k])/nr;
            }
          }
        }
      }
      if(left) { // Tiles at left
        int ref = nu-1;
        double N;
        double[] nextCenter = new double[3];
        if(Double.isNaN(height)) {
          N = nz;
        } else if(height==0) {
          N = Integer.MAX_VALUE;
        } else {
          N = nz/height;
        }
        double aux = 1.0/N;
        for(int j = 0;j<nz;j++, ref += nu) {
          center[0] = j*aux*vectorz[0];
          center[1] = j*aux*vectorz[1];
          center[2] = j*aux*vectorz[2]-0.5;
          nextCenter[0] = (j+1)*aux*vectorz[0];
          nextCenter[1] = (j+1)*aux*vectorz[1];
          nextCenter[2] = (j+1)*aux*vectorz[2]-0.5;
          for(int i = 0;i<nr;i++, tile++) {
            for(int k = 0;k<3;k++) {
              data[tile][0][k] = ((nr-i)*center[k]+i*data[ref][1][k])/nr;
              data[tile][1][k] = ((nr-i-1)*center[k]+(i+1)*data[ref][1][k])/nr;
              data[tile][2][k] = ((nr-i-1)*nextCenter[k]+(i+1)*data[ref][2][k])/nr;
              data[tile][3][k] = ((nr-i)*nextCenter[k]+i*data[ref][2][k])/nr;
            }
          }
        }
      }
    }
    return data;
  }

  // ----------------------------------------------------
  // XML loader
  // ----------------------------------------------------

  /**
   * Returns an XML.ObjectLoader to save and load object data.
   * @return the XML.ObjectLoader
   */
  public static XML.ObjectLoader getLoader() {
    return new Loader();
  }

  static protected class Loader extends org.opensourcephysics.display3d.core.ElementCone.Loader {
    public Object createObject(XMLControl control) {
      return new ElementCone();
    }
  }
}

/* 
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */
