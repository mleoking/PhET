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

import java.io.*;
import geom.geometry.Point;
import geom.jgv.util.RGB;
import geom.jgv.util.TurboTokenizer;
import geom.jgv.util.ParseException;

public class Vect extends Geom {
  int npolylines;		// # polylines
  int nvertices;		// total # vertices
  int ncolors;			// total # colors
  int nv[];			// # vertices in each polyline [npolylines]
  int nc[];			// # colors in each polyline [npolylines]
  RGB colors[];			// the colors [ncolors]
  Point	vertices[];		// the vertices [nvertices]

  public Vect() {
    super();
    npolylines = 0;
    nvertices = 0;
    ncolors = 0;
    nv = null;
    nc = null;
    colors = null;
    vertices = null;
  }

  /**
   * Read an Vect object from an input stream
   */
  public Vect(String keyword, TurboTokenizer st) throws Exception {
    this();
    parse(keyword, st);
  }


  /**
   * Parses an Vect file.  Assumes that the keyword at the beginning
   * has already been read and is passed in as the "keyword"
   * parameter.  The TurboTokenizer "st" should be positioned to read
   * the next token from the file after the keyword.
   */
  void parse(String keyword, TurboTokenizer st) throws Exception {

    int i;
    double x,y,z;
    double r,g,b;

    // grab the number of polylines, vertices, and colors
    st.nextToken();
    if (st.ttype != st.TT_NUMBER)
      throw new ParseException(st, "number of polylines must be a number");
    npolylines = (int)st.nval;
    if (npolylines<=0)
      throw new ParseException(st, "number of polylines must be positive");

    st.nextToken();
    if (st.ttype != st.TT_NUMBER)
      throw new ParseException(st, "number of vertices must be a number");
    nvertices = (int)st.nval;
    if (nvertices<=0)
      throw new ParseException(st, "number of vertices must be positive");

    st.nextToken();
    if (st.ttype != st.TT_NUMBER)
      throw new ParseException(st, "number of colors must be a number");
    ncolors = (int)st.nval;
    if (ncolors<0)
      throw new ParseException(st, "number of colors must be nonnegative");

    nv = new int[npolylines];
    nc = new int[npolylines];
    colors = new RGB[ncolors];
    vertices = new Point[nvertices];

    for (i = 0; i<npolylines; ++i) {
      st.nextToken();
      if (st.ttype != st.TT_NUMBER)
	throw new ParseException(st, "vertex count must be a number");
      nv[i] = (int)st.nval;
      if (nv[i]<=0)
	throw new ParseException(st, "vertex count must be positive");
    }

    for (i = 0; i<npolylines; ++i) {
      st.nextToken();
      if (st.ttype != st.TT_NUMBER)
	throw new ParseException(st, "color count must be a number");
      nc[i] = (int)st.nval;
      if (nc[i]<0)
	throw new ParseException(st, "vertex count must be nonnegative");
    }

    for (i = 0; i<nvertices; ++i) {
      st.nextToken();
      if (st.ttype != st.TT_NUMBER)
	throw new ParseException(st, "vertex coordinate must be a number");
      x = st.nval;
      st.nextToken();
      if (st.ttype != st.TT_NUMBER)
	throw new ParseException(st, "vertex coordinate must be a number");
      y = st.nval;
      st.nextToken();
      if (st.ttype != st.TT_NUMBER)
	throw new ParseException(st, "vertex coordinate must be a number");
      z = st.nval;
      vertices[i] = new Point(x,y,z);
    }

    for (i = 0; i<ncolors; ++i) {
      st.nextToken();
      if (st.ttype != st.TT_NUMBER)
	throw new ParseException(st, "color coordinate must be a number");
      r = st.nval;
      st.nextToken();
      if (st.ttype != st.TT_NUMBER)
	throw new ParseException(st, "color coordinate must be a number");
      g = st.nval;
      st.nextToken();
      if (st.ttype != st.TT_NUMBER)
	throw new ParseException(st, "color coordinate must be a number");
      b = st.nval;
      colors[i] = new RGB(r,g,b);

      st.nextToken();		// ignore alpha value
    }
  }

  public Face[] getFaces() {
    int pi, fi, ni, vi, ci, nfaces;
    int r=0,g=0,b=0;
    Face faces[];

    // Compute total number of "faces", i.e. line segments, and
    // allocate array of that length
    nfaces = 0;
    for (pi=0; pi<npolylines; ++pi) {
      if (nv[pi]==1) {
	++nfaces;		// 1 vertex ==> 1 face
      } else {
	nfaces += (nv[pi]-1);	// n vertices ==> n-1 faces
      }
    }
    faces = new Face[nfaces];

    ni = 0;
    vi = 0;
    ci = 0;
    for (pi=0; pi<npolylines; ++pi) {

      if (nv[pi]==1) {
	// If this polyline is a single point, make a single
	// Face representing that point.
	Face f = faces[ni++] = new Face();
	f.nverts = 1;
	f.vertices = new Point[1];
	f.vertices[0] = vertices[vi++];
	f.parent = this;
	switch (nc[pi]) {
	case 0:
	  f.cr = f.cg = f.cb = 0;
	  break;
	default:
	case 1:
	  f.cr = colors[ci].r;
	  f.cg = colors[ci].g;
	  f.cb = colors[ci].b;
	  ++ci;
	  break;
	}
      } else {
	// This polyline has at least two vertices, so make
	// a sequence of Faces representing each line segment
	// in the polyline.
	for (fi=0; fi<nv[pi]-1; ++fi) {
	  Face f = faces[ni++] = new Face();
	  f.nverts = 2;
	  f.vertices = new Point[2];
	  f.vertices[0] = vertices[vi];
	  f.vertices[1] = vertices[vi+1];
	  ++vi;
	  f.parent = this;
	  switch (nc[pi]) {	// if # colors specified for this polyline is
	  case 0:		//    0, then set this face color to black
	    f.cr = f.cg = f.cb = 0;
	    break;
	  case 1:		//    1, then set this face color to that
	    if (fi==0) {	//       one color, which we remember in
	      f.cr = r = colors[ci].r; // the local vars r,g,b for future faces
	      f.cg = g = colors[ci].g;
	      f.cb = b = colors[ci].b;
	      ++ci;
	    } else {
	      f.cr = r;
	      f.cg = g;
	      f.cb = b;
	    }
	    break;
	  default:		//    > 1, then assume one color per vertex
	    f.cr = colors[ci].r;
	    f.cg = colors[ci].g;
	    f.cb = colors[ci].b;
	    ++ci;
	  }
	}
	++vi;
      }

    }
    return faces;
  }

}
