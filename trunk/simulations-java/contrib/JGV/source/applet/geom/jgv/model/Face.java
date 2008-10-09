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

import geom.jgv.util.PolyConstants;
import java.awt.Graphics;
import java.awt.Color;
import java.util.Vector;

import geom.geometry.Line;
import geom.geometry.Plane;
import geom.geometry.Point;

/**
 *  Your basic storage class for polygonal face information.
 */
public class Face implements PolyConstants {

  boolean debug = false;

  /**
    * Whether to draw edges
    */
  public boolean draw_edges = true;

  /**
   * Number of vertices
   */
  public int    nverts;

  /**
   * Face color in RGB
   */
  public int    cr, cg, cb;

  public Plane  plane;

  /**
   * Array of vertices
   */
  public Point  vertices[];

  public Geom parent;

  public String label = null;
  public int labelWidth = 0;
  public int labelHeight = 0;

  /**
   * A "labelled" face = for annotation
   */
  public Face(String label) {
    this();
    this.label = label;
    nverts = 1;
  }

  public Face() {
    nverts = 0;
    vertices = null;
    cr = 255; cg = 255; cb = 255;
  }

  public Face(int nv) {
    nverts = nv;
    cr = 255; cg = 255; cb = 255;
  }

  public int dimension() {
    if (nverts<=0) return -1;
    if (nverts==1) return 0;
    if (nverts==2) return 1;
    return 2;
  }

  public Face copyAllButVertices() {
    Face face = new Face();
    face.cr = this.cr;
    face.cg = this.cg;
    face.cb = this.cb;
    face.nverts = this.nverts;
    face.draw_edges = this.draw_edges;
    face.plane = this.plane;
    face.parent = this.parent;
    face.vertices = new Point[this.nverts];
    face.label = face.label;
    return face;
  }

  /**
   * Returns the plane of a polygon
   */
  public Plane getPlane() {
    if (plane == null) {
      if (nverts > 2) {
        Point e1 = vertices[0].minus(vertices[1]);
        Point e2 = vertices[1].minus(vertices[2]);
        plane = (new Plane(vertices[0], e1.cross(e2)));

        if (plane.normal.dot(plane.normal) == 0) {
          System.out.println("plane.normal = " + plane.normal + "polygon = " + this);
          plane.normal.x = 1;
        }
      } else {
        plane = new Plane();
        plane.normal.x = 1;
      }
    }

    return(plane);
  }

  /**
   * Returns a polygon's position in relation to a plane
   * (CONCIDENT, SPANNING, IN_BACK_OF, or IN_FRONT_OF)
   */
  public int classifyPolygon(Plane plane) {
    int i;
    boolean coin = false;
    boolean in_front = false;
    boolean in_back = false;

    for (i = 0; i < nverts; i++) {
      Point p = vertices[i];

      int pos = positionOf(p.classify(plane));

      switch (pos) {
        case COINCIDENT:
          coin = true;
          break;

        case IN_BACK_OF:
          in_back = true;
          break;

        case IN_FRONT_OF:
          in_front = true;
          break;
       }
     }

     if (in_back & !in_front) return IN_BACK_OF;
     else if (in_front & !in_back) return IN_FRONT_OF;
     else if (in_front & in_back) return SPANNING;
     else return COINCIDENT;
  }

  // Takes the number from classifyPoint and changes it to one of the
  // following for readability of code:
  // This should be changed to n > EPSILON for IN_FRONT_OF and
  // n < EPSILON && n > -EPSILON for COINCIDENT, to avoid spliting
  // polygons due to floating point error.
  private int positionOf(double n) {
    if (n > EPSILON) return (IN_FRONT_OF);
    else if (n == 0 || (n < EPSILON && n > -EPSILON)) return (COINCIDENT);
    else return (IN_BACK_OF);
  }

  /**
   * Given a polygon which passes through the plane part, this will
   * split it into two polygons.
   */
  public void splitPolygon(Plane part, Face front, Face back) {
    int   count = this.nverts,
          out_c = 0, in_c = 0,
          i;
    Point ptA, ptB;
    Point outpts[] = new Point[MAXPTS],
          inpts[] = new Point[MAXPTS];
    double sideA, sideB;

    ptA = this.vertices[count - 1];
    sideA = ptA.classify(part);

    this.getPlane();

    switch (count) {
    case 2:			// line segment
      ptB = this.vertices[0];
      sideB = ptB.classify(part);
      if (sideA*sideB < 0) {	// ptA & ptB are on different sides of plane
	Point v = ptB.minus(ptA);
	double sect = -ptA.classify(part) / (part.normal.dot(v));
	if (sect < 0)
	  sect = 0;
	else if (sect > 1)
	  sect = 1;
	// First point of both halves is intersection point
	outpts[out_c++] = inpts[in_c++] = ptA.plus(v.scalarMult(sect));
	if (sideA < 0) {
	  // ptA is on inside, ptB on outside
	  inpts[in_c++] = ptA;
	  outpts[out_c++] = ptB;
	} else {
	  // ptA is on outside, ptB on inside
	  inpts[in_c++] = ptB;
	  outpts[out_c++] = ptA;
	}
      } else {			// ptA & ptB are on same side
	// Probably this will never happen, because if both points are
	// on the same side, we wouldn't be calling this method in the
	// first place.  But just in case, we deal with this situation
	// anyway.  Note that we have to be a little careful here to
	// handle the case when one (or both) of the points is ON the
	// plane.
	if (sideA<0 || sideB<0) {
	  inpts[in_c++] = ptA;	  // segment is on the inside
	  inpts[in_c++] = ptB;
	} else {
	  outpts[out_c++] = ptB;  // it's the outside
	  outpts[out_c++] = ptB;
	  // This case also includes the possibility that both points
	  // are ON the plane (sideA=sideB=0), but in that case the
	  // method really shouldn't have been called, one would hope.
	}
      }
      break;

    default:
      for (i = 0; i < count; ++i) {
	ptB = this.vertices[i];
	sideB = ptB.classify(part);

	if (sideB > 0) {
          if (sideA < 0) {
	    /* compute the intersection point of the line
	       from point A to point B with the partition
	       plane. This is a simple ray-plane intersection. */
	    Point v = ptB.minus(ptA);
	    double sect = -ptA.classify(part) / (part.normal.dot(v));
	    if (sect < 0)
	      sect = 0;
	    else if (sect > 1)
	      sect = 1;
	    outpts[out_c++] = inpts[in_c++] = ptA.plus(v.scalarMult(sect));
          }
          outpts[out_c++] = ptB;
	}
	else if (sideB < 0) {
          if (sideA > 0) {
	    /* compute the intersection point of the line
	       from point A to point B with the partition
	       plane. This is a simple ray-plane intersection. */

	    Point v = ptB.minus(ptA);
	    double sect = -ptA.classify(part) / (part.normal.dot(v));
	    if (sect < 0)
	      sect = 0;
	    else if (sect > 1)
	      sect = 1;
	    outpts[out_c++] = inpts[in_c++] = ptA.plus(v.scalarMult(sect));
          }
          inpts[in_c++] = ptB;
	}
	else {
	  outpts[out_c++] = inpts[in_c++] = ptB;
	}

	ptA = ptB;
	sideA = sideB;
      }  // end for (i = 0; i < count; ++i)
      break;


    }

    front.nverts = out_c;
    front.vertices = outpts;
    front.draw_edges = draw_edges;
    front.plane = this.plane;
    front.cr = this.cr;
    front.cg = this.cg;
    front.cb = this.cb;
    front.parent = this.parent;
    back.nverts = in_c;
    back.vertices = inpts;
    back.draw_edges = draw_edges;
    back.plane = this.plane;
    back.cr = this.cr;
    back.cg = this.cg;
    back.cb = this.cb;
    back.parent = this.parent;
  }

  public void getBoundingBox(Line in) {
      for (int i = 0; i < this.nverts; i++) {
        if (vertices[i].x < in.a.x) { in.a.x = vertices[i].x; }
        if (vertices[i].y < in.a.y) { in.a.y = vertices[i].y; }
        if (vertices[i].z < in.a.z) { in.a.z = vertices[i].z; }
        if (vertices[i].x > in.b.x) { in.b.x = vertices[i].x; }
        if (vertices[i].y > in.b.y) { in.b.y = vertices[i].y; }
        if (vertices[i].z > in.b.z) { in.b.z = vertices[i].z; }
      }
  }

  public void dprintln(String s) {
    if (debug) System.out.println(s);
  }

  public String toString() {
    Vector v = new Vector();

    for (int i = 0; i < this.nverts; i++)
      v.addElement(vertices[i]);

    return("Face("+this.nverts+")"+v);
  }
}

