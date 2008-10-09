//
// Copyright (C) 1998-2000 Geometry Technologies, Inc. <info@geomtech.com>
// Copyright (C) 2002-2004 DaerMar Communications, LLC <jgv@daermar.com>
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package geom.jgv.model;

import java.util.Vector;
import geom.geometry.Point;
import geom.jgv.model.Face;
import geom.jgv.util.HMatrix3D;

public class PickSet {

  Vector faceList;    // List of faces that were "picked" (intersected)
  Vector pointList;   // Intersection point
  Vector aPointList;  // "Annotation" points
  Point screenCoords; // Screen coordinates
  boolean mouseover = false;
  private geom.jgv.util.HMatrix3D worldTransform; // Is the pick from a mouseover?

  public PickSet() {
    faceList = new Vector(5,5);
    pointList = new Vector(5,5);
    aPointList = new Vector(5,5);
    screenCoords = new Point(0, 0, 0);
  }

  public void add(Face face, Point p) {
    faceList.addElement(face);
    pointList.addElement(p);
    aPointList.addElement(p);
  }

  public void add(Face face, Point p, Point ap) {
    faceList.addElement(face);
    pointList.addElement(p);
    aPointList.addElement(ap);
  }

  public Face getPickedFace() {
      Face retVal = null;
      if (!faceList.isEmpty()) {
        retVal = (Face) faceList.lastElement();
      }
      return retVal;
  }

  public Point getPickedPoint() {
    Point retVal = null;
    if (!pointList.isEmpty()) {
      retVal = (Point) pointList.lastElement();
    }
    return retVal;
  }

  public Point getAnnotationPoint() {
    Point retVal = null;
    if (!aPointList.isEmpty()) {
      retVal = (Point) aPointList.lastElement();
    }
    return retVal;
  }

  public Point getPickedPoint(Face f) {
    Point retVal = null;
    int idx = this.indexOf(f);
    if (idx != -1) {
      retVal = (Point) pointList.elementAt(idx);
    }
    return retVal;
  }

  public Geom getPickedGeom() {
    Geom retVal = null;
    Face f = this.getPickedFace();
    if (f != null) {
      retVal = f.parent;
    }
    return retVal;
  }

  public boolean contains(Face f) {
    return faceList.contains(f);
  }

  private int indexOf(Face f) {
    return faceList.indexOf(f);
  }

  public void setScreenCoordinates(Point s) {
    this.screenCoords = s;
  }

  public Point getScreenCoordinates() {
    return this.screenCoords;
  }

  public boolean isMouseover() {
    return mouseover;
  }
  public void setMouseover(boolean mouseover) {
    this.mouseover = mouseover;
  }
  public void setWorldTransform(HMatrix3D worldTransform) {
    this.worldTransform = worldTransform;
  }
  public HMatrix3D getWorldTransform() {
    return worldTransform;
  }
}