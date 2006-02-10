/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.display3d.simple3d;
import java.awt.*;
import java.util.*;
import org.opensourcephysics.controls.*;

/**
 * <p>Title: ElementSegment</p>
 * <p>Description: A Segment using the painter's algorithm</p>
 * @author Francisco Esquembre
 * @version March 2005
 */
public class ElementTrail extends Element implements org.opensourcephysics.display3d.core.ElementTrail {
  // Configuration variables
  private boolean connected = true;
  private int maximum = 0;

  // Implementation variables
  private int theFirstPoint = 0;
  protected ArrayList list = new ArrayList();
  private TrailPoint[] points = new TrailPoint[0];
  // private boolean cloning = false;

  // -------------------------------------
  // New configuration methods
  // -------------------------------------
  public void addPoint(double x, double y, double z) {
    addPoint(x, y, z, this.connected);
  }

  public void addPoint(double[] point) {
    if(point.length>2) {
      addPoint(point[0], point[1], point[2], this.connected);
    } else {
      addPoint(point[0], point[1], 0, this.connected);
    }
  }

  public void moveToPoint(double x, double y, double z) {
    addPoint(x, y, z, false);
  }

  public void setMaximumPoints(int maximum) {
    this.maximum = Math.max(maximum, 0);
    clear();
  }

  public int getMaximumPoints() {
    return this.maximum;
  }

  public void setConnected(boolean connected) {
    this.connected = connected;
  }

  public boolean isConnected() {
    return this.connected;
  }

  public synchronized void clear() {
    // while(cloning); // wait for cloning to finish
    synchronized(list) {
      list.clear();
    }
    points = new TrailPoint[0];
    theFirstPoint = 0;
  }

  private void addPoint(double _x, double _y, double _z, boolean _c) {
    synchronized(list) {
      int position = list.size();
      if(maximum>0&&position>=maximum) {
        position = theFirstPoint;
        list.remove(theFirstPoint);
        theFirstPoint = (theFirstPoint+1)%maximum;
      }
      TrailPoint point = new TrailPoint(position, _x, _y, _z, _c);
      // while (cloning); // wait for cloning to finish
      list.add(position, point);
      if(getPanel()!=null) {
        point.transformAndProject();
      }
    }
  }

  public void getExtrema(double[] min, double[] max) {
    double minX = Double.POSITIVE_INFINITY, maxX = Double.NEGATIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
    double minZ = Double.POSITIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
    double[] aPoint = new double[3];
    TrailPoint[] tmpPoints = new TrailPoint[0];
    synchronized(list) {
      tmpPoints = (TrailPoint[]) list.toArray(tmpPoints);
    }
    for(int i = 0, n = tmpPoints.length;i<n;i++) {
      aPoint[0] = tmpPoints[i].xp;
      aPoint[1] = tmpPoints[i].yp;
      aPoint[2] = tmpPoints[i].zp;
      sizeAndToSpaceFrame(aPoint);
      minX = Math.min(minX, aPoint[0]);
      maxX = Math.max(maxX, aPoint[0]);
      minY = Math.min(minY, aPoint[1]);
      maxY = Math.max(maxY, aPoint[1]);
      minZ = Math.min(minZ, aPoint[2]);
      maxZ = Math.max(maxZ, aPoint[2]);
    }
    min[0] = minX;
    max[0] = maxX;
    min[1] = minY;
    max[1] = maxY;
    min[2] = minZ;
    max[2] = maxZ;
  }

  // -------------------------------------
  // Abstract part of Element or Parent methods overwritten
  // -------------------------------------
  Object3D[] getObjects3D() {
    synchronized(list) {
      if(!isVisible()||list.size()<=0) {
        return null;
      }
      // cloning = true;
      points = (TrailPoint[]) list.toArray(points);
      // cloning = false;
    }
    if(hasChanged()) {
      transformAndProjectPoints();
    } else if(needsToProject()) {
      projectPoints();
    }
    return points;
  }

  void draw(Graphics2D _g2, int _index) {
    TrailPoint point = points[_index];
    Color theColor = getPanel().projectColor(getRealStyle().getLineColor(), point.getDistance());
    _g2.setStroke(getRealStyle().getLineStroke());
    _g2.setColor(theColor);
    if(_index==theFirstPoint||!point.connected) {
      _g2.drawLine((int) point.pixel[0], (int) point.pixel[1], (int) point.pixel[0], (int) point.pixel[1]);
    } else {
      if(_index==0) {
        _index = maximum;
      }
      TrailPoint pointPrev = points[_index-1];
      _g2.drawLine((int) point.pixel[0], (int) point.pixel[1], (int) pointPrev.pixel[0], (int) pointPrev.pixel[1]);
    }
  }

  void drawQuickly(Graphics2D _g2) {
    synchronized(list) {
      if(!isVisible()||list.size()<=0) {
        return;
      }
      points = (TrailPoint[]) list.toArray(points);
    }
    if(hasChanged()) {
      transformAndProjectPoints();
    } else if(needsToProject()) {
      projectPoints();
    }
    _g2.setStroke(getRealStyle().getLineStroke());
    _g2.setColor(getRealStyle().getLineColor());
    TrailPoint point = points[theFirstPoint];
    int aPrev = (int) point.pixel[0], bPrev = (int) point.pixel[1];
    _g2.drawLine(aPrev, bPrev, aPrev, bPrev);
    for(int i = 1, n = points.length;i<n;i++) { // The order is relevant
      int index;
      if(maximum>0) {
        index = (i+theFirstPoint)%maximum;
      } else {
        index = i;
      }
      point = points[index];
      if(point.connected) {
        _g2.drawLine((int) point.pixel[0], (int) point.pixel[1], aPrev, bPrev);
      } else {
        _g2.drawLine((int) point.pixel[0], (int) point.pixel[1], (int) point.pixel[0], (int) point.pixel[1]);
      }
      aPrev = (int) point.pixel[0];
      bPrev = (int) point.pixel[1];
    }
  }

  // -------------------------------------
  // Private methods
  // -------------------------------------
  synchronized void transformAndProjectPoints() {
    for(int i = 0, n = points.length;i<n;i++) {
      points[i].transformAndProject();
    }
    setNeedToProject(false);
    setElementChanged(false);
  }

  synchronized void projectPoints() {
    for(int i = 0, n = points.length;i<n;i++) {
      points[i].transformAndProject();
    }
    setNeedToProject(false);
  }

  // ----------------------------------------------------
  // A class for the individual points of the trail
  // ----------------------------------------------------
  private class TrailPoint extends Object3D {
    boolean connected;
    private double xp, yp, zp;
    private double[] coordinates = new double[3];
    double[] pixel = new double[3];

    TrailPoint(int _index, double _x, double _y, double _z, boolean _c) {
      super(ElementTrail.this, _index);
      xp = _x;
      yp = _y;
      zp = _z;
      connected = _c;
    }

    void transformAndProject() {
      coordinates[0] = xp;
      coordinates[1] = yp;
      coordinates[2] = zp;
      sizeAndToSpaceFrame(coordinates);
      getPanel().project(coordinates, pixel);
      super.setDistance(pixel[2]);
    }

    void project() {
      getPanel().project(coordinates, pixel);
      super.setDistance(pixel[2]);
    }
  } // End of class TrailPoint

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

  static private class Loader extends org.opensourcephysics.display3d.core.ElementTrail.Loader {
    public Object createObject(XMLControl control) {
      return new ElementTrail();
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
