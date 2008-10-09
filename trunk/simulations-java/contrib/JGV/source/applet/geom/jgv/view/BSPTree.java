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

package geom.jgv.view;

import geom.jgv.model.Face;
import geom.jgv.model.Geom;
import geom.jgv.util.PolyConstants;
import geom.geometry.Line;
import geom.geometry.Plane;
import geom.geometry.Point;
import java.awt.Graphics;

public class BSPTree implements PolyConstants {
  Face polygons[];

  Plane plane = new Plane();
  int numpoly = 0;
  BSPTree front;
  BSPTree back;

  public boolean debug = false;
  public static Render render = new Render();

  public BSPTree() {
  }

  void dprint(String s) {
    if (debug) System.out.println(s);
  }

  boolean insure2D(Face polys[]) {
    // If the list is empty, return false
    if (polys.length <= 0) return false;
    // If the zeroth Face in the list is already 2D; return true
    if (polys[0].dimension() >= 2) return true;
    // Otherwise, search for a 2D face starting at the end
    // of the list, and swap it with the zeroth one.
    for (int i=polys.length-1; i>=0; --i) {
      if (polys[i]!=null && polys[i].dimension() >= 2) {
	Face p = polys[0];
	polys[0] = polys[i];
	polys[i] = p;
	return true;
      }
    }
    // If we get this far, then there were no 2D faces, so
    // return false.
    return false;
  }


  /**
   * Sort an array of faces in decreasing order of dimension;
   * used to order the faces at each node of the BSPTree so that
   * points get drawn after (on top of) lines, and lines get drawn
   * after polygons.  (Note: as I'm writing this, there are no
   * points, but this should work when/if they're added).
   * Returns a new array of exactly n Faces. (The original array
   * may have been allocated to hold more than n, but we only
   * deal with the first n entries.)
   */
  Face[] sortByDimension(Face polys[], int n) {
    Face newpolys[] = new Face[n];
    int i,j,dim;

    j = 0;
    for (dim=2; dim>=0; --dim) {
      for (i=0; i<n; ++i) {
	if (polys[i].dimension()==dim) newpolys[j++] = polys[i];
      }
    }
    return newpolys;
  }


  public void build(Geom geom) {
      Face polys[] = geom.getFaces();

      if (polys != null && polys.length != 0) {
          build(polys);
      }
  }

  public void build(Face polys[]) {

    // Make sure the first polygon in the list is actually
    // a 2D face
    if (insure2D(polys)) {
      // First polygon is a 2D face, so use it as the splitting
      // plane for the BSPTree rooted at this node
      Face root = polys[0];
      this.polygons = new Face[polys.length];
      Face backlist[] = new Face[polys.length];
      Face frontlist[] = new Face[polys.length];
      Face poly = new Face();
      int numback = 0;
      int numfront = 0;

      this.plane = root.getPlane();

      for (int i = 1; i < polys.length; i++) {
	if (polys[i] == null) break;

	poly = polys[i];

	int result = poly.classifyPolygon(this.plane);

	switch (result) {
	case COINCIDENT:
	  this.polygons[numpoly] = poly;
	  numpoly++;
	  break;

	case IN_BACK_OF:
	  backlist[numback] = poly;
	  numback++;
	  break;

	case IN_FRONT_OF:
	  frontlist[numfront] = poly;
	  numfront++;
	  break;

	case SPANNING:
	  // Make sure we don't try to split a point; actually, I hope
	  // that we'd never end up in the SPANNING case for a point
	  // anyway, but just in case ...
	  if (poly.dimension() > 0) {
	    Face front_piece = new Face();
	    Face back_piece = new Face();

	    poly.splitPolygon(this.plane, front_piece, back_piece);

	    frontlist[numfront] = front_piece;
	    numfront++;
	    backlist[numback] = back_piece;
	    numback++;
	  } else {
	    // It's a point; just add it to the list for this node
	    this.polygons[numpoly] = poly;
	    numpoly++;
	  }

	  break;
	}
      }

      this.polygons[numpoly] = root;
      numpoly++;

      this.polygons = sortByDimension(this.polygons, numpoly);

      if (numfront != 0) {
	this.front = new BSPTree();
	this.front.build(frontlist);
      }
      if (numback != 0) {
	this.back = new BSPTree();
	this.back.build(backlist);
      }
    } else {
      // There are no 2D faces in the list. so no need to
      // do any splitting.  Just store the entire list
      // at this node
      for (numpoly=0;
	   numpoly<polys.length && polys[numpoly]!=null;
	   ++numpoly);
      this.polygons = new Face[numpoly];
      System.arraycopy(polys, 0, this.polygons, 0, numpoly);
    }

  }

  // Renders BSP tree in proper order, given eyepoint.
  public void renderBSP(Point eye, Graphics g, Camera3D camera) {

    double result = eye.classify(this.plane);

    if (result > 0) {
      if (this.back != null) this.back.renderBSP(eye, g, camera);
      for (int i=0; i < numpoly; i++) {
	render.renderFace(polygons[i], g, camera);
      }
      if (this.front != null) this.front.renderBSP(eye, g, camera);
    }
    else if (result <= 0) {
      if (this.front != null) this.front.renderBSP(eye, g, camera);
      for (int i=0; i < numpoly; i++) {
	render.renderFace(polygons[i], g, camera);
      }
      if (this.back != null) this.back.renderBSP(eye, g, camera);
    }
  }

  // Picks BSP tree in proper order, given eyepoint and picker.
  public void pickBSP(Point eye, Picker picker) {

    double result = eye.classify(this.plane);

    if (result > 0) {
      if (this.back != null) this.back.pickBSP(eye, picker);
      for (int i=0; i < numpoly; i++) {
        picker.pickFace(polygons[i]);
      }
      if (this.front != null) this.front.pickBSP(eye, picker);
    }
    else if (result <= 0) {
      if (this.front != null) this.front.pickBSP(eye, picker);
      for (int i=0; i < numpoly; i++) {
        picker.pickFace(polygons[i]);
      }
      if (this.back != null) this.back.pickBSP(eye, picker);
    }
  }

  public void getBoundingBox(Line in) {
      if (back != null) back.getBoundingBox(in);
      for (int i=0; i < numpoly; i++) {
	  polygons[i].getBoundingBox(in);
      }
      if (front != null) front.getBoundingBox(in);
  }


  String indentString(String prefix, String string) {
    int i, len = string.length();
    char c;
    String newstring = "";
    newstring += prefix;
    for (i=0; i<len; ++i) {
      c = string.charAt(i);
      if (c == '\n') {
	newstring += "\n" + prefix;
      } else {
	newstring += c;
      }
    }
    return newstring;
  }

  public String toString() {
    String val = "";
    String faces;
    int i;

    val = "Tree[\n";

    if (front == null) {
      val += "  (null)";
    } else {
      val += indentString("  ", front.toString()) + ",";
    }
    val += "\n";

    faces = "{";
    for (i=0; i<numpoly; ++i) {
      faces += polygons[i];
      if (i<numpoly-1) faces += ",\n";
    }
    faces += "}";
    val += indentString("  ", faces)  + ",\n";

    if (back == null) {
      val += "  (null)";
    } else {
      val += indentString("  ", back.toString());
    }
    val += "]\n";

    return val;
  }

  public void shortDisplay(int indent) {
    int i;

    if (front != null) front.shortDisplay(indent+1);

    for (i = 0; i < indent; i++)
      System.out.print(" ");

    if (back != null) back.shortDisplay(indent+1);
  }

}
