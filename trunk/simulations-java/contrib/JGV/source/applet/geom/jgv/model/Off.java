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

import geom.geometry.Point;
import geom.jgv.util.TurboTokenizer;
import geom.jgv.util.ParseException;
import geom.jgv.util.RGB;
import org.sjg.xml.Element;
import java.io.*;
import java.util.Enumeration;
import java.util.Vector;

public class Off extends Geom {
  private boolean colors  = false;
  private boolean normals = false;
  private PrintStream out = System.out;
  public boolean debug   = true;

  int   nverts, nfaces, nedges;	// # of vertices, faces, edges

  Point	vertices[];		// array of vertex coordinates

  int 	ifaces[][];		// arrays of face indices
  RGB   facecolors[];           // color of each face

  public Off() {
    super();
    nverts	= 0;
    nedges	= 0;
    nfaces	= 0;
    facecolors	= null;
    vertices	= null;
  }

  /**
   * Read an Off object from an XML element
   */
  public Off(Element element) throws Exception {
    super(element);

    // Read the header
    nverts = element.getAttribute("vertices").getIntValue();
    nfaces = element.getAttribute("faces").getIntValue();
    if (element.hasAttribute("edges")) {
      nedges = element.getAttribute("edges").getIntValue();
    }
    if (element.hasAttribute("node")) {
        this.nodeName = element.getAttribute("node").getValue();
    }

    Element child = this.getRequiredChildElement("vertices", element);
    // grab the vertices and put them in the vertices array.
    double x, y, z;
    vertices = new Point[nverts];
    Enumeration children = child.elements("vertex");
    while (children.hasMoreElements()) {
      child = (Element) children.nextElement();
      int index = this.getAttributeAsInt("index", child);
      x = this.getAttributeAsDouble("x", child);
      y = this.getAttributeAsDouble("y", child);
      z = this.getAttributeAsDouble("z", child);
      if ((index < 0) || (index >= vertices.length)) {
          throw new ParseException("Vertex index parameter of " + index + " is invalid");
      }
      vertices[index] = new Point(x, y, z);
    }

    child = this.getRequiredChildElement("faces", element);
    // grab the faces and put them in the face array
    ifaces = new int[nfaces][];
    int i = 0;
    children = child.elements("face");
    while (children.hasMoreElements()) {

      if (i >= nfaces) {
          throw new ParseException("More than " + nfaces + " <face> elements specified");
      }

      double r, g, b;
      Element faceElement = (Element) children.nextElement();

      // read vertex indices for this face
      Element verticesElement = this.getRequiredChildElement("vertices", faceElement);
      Vector indices = this.getContentsAsIntArray(verticesElement);
      ifaces[i] = new int[indices.size()];
      Enumeration facevertices = indices.elements();
      int j = 0;
      while (facevertices.hasMoreElements()) {
        ifaces[i][j] = ((Integer)facevertices.nextElement()).intValue();
        j++;
      }

      // Read colors for this face
      Element colorElement = this.getOptionalChildElement("color", faceElement);
      if (facecolors == null) {
        facecolors = new RGB[nfaces];
      }
      if (colorElement != null) {
        r = this.getAttributeAsDouble("r", colorElement);
        g = this.getAttributeAsDouble("g", colorElement);
        b = this.getAttributeAsDouble("b", colorElement);
        if (r <= 1.0) {
          facecolors[i] = new RGB(r, g, b);
        } else {
          facecolors[i] = new RGB((int)r, (int)g, (int)b);
        }
      } else {
        facecolors[i] = new RGB();
      }
      i++;
    }
  }





  /**
   * Read an Off object from an input stream
   */
  public Off(String keyword, TurboTokenizer st) throws Exception {
    this();
    parse(keyword, st);
  }


  /**
   * Parses an OFF file.  Assumes that the keyword at the beginning
   * has already been read and is passed in as the "keyword"
   * parameter.  The TurboTokenizer "st" should be positioned to read
   * the next token from the file after the keyword.
   */
  void parse(String keyword, TurboTokenizer st) throws Exception {

    // decide which type of OFF file it is: [C][N][n]OFF
    // NOTE: I've excluded the [4] option because [n] handles it, and it's a
    //       lot harder to make things parse

    if (debug) out.println(keyword);

    if (keyword.indexOf("C") != -1) colors = true;
    if (keyword.indexOf("N") != -1) normals = true;

    if (debug) {
      out.println("colors = "+colors);
      out.println("normals = "+normals);
    }

    /* grab the number of vertices, faces and edges.
    These need to be cast since they should be ints, and Java requires
    explict casts from the .nval double to an int. */

    st.nextToken();
    if (st.ttype != st.TT_NUMBER)
      throw new ParseException(st, "number of vertices must be a number");

    nverts = (int)st.nval;

    st.nextToken();
    if (st.ttype != st.TT_NUMBER)
      throw new ParseException(st, "number of faces must be a number");

    nfaces = (int)st.nval;

    st.nextToken();
    if (st.ttype != st.TT_NUMBER)
      throw new ParseException(st, "number of edges must be a number");

    nedges = (int)st.nval;

    if (debug) {
      out.println("nverts = "+nverts);
      out.println("nfaces = "+nfaces);
      out.println("nedges = "+nedges);
    }


    // grab the vertices and put them in the vertices array.

    vertices = new Point[nverts];
    for (int i = 0; i < nverts; i++) {
      double x, y, z;

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


      // just ignore vertex colors
      if (colors) {
	st.nextToken();
	if (st.ttype != st.TT_NUMBER)
	  throw new ParseException(st, "vertex color must be a number");

	st.nextToken();
	if (st.ttype != st.TT_NUMBER)
	  throw new ParseException(st, "vertex color must be a number");

	st.nextToken();
	if (st.ttype != st.TT_NUMBER)
	  throw new ParseException(st, "vertex color must be a number");
      }

      // just ignore vertex normals
      if (normals) {
	st.nextToken();
	if (st.ttype != st.TT_NUMBER)
	  throw new ParseException(st, "vertex normal must be a number");

	st.nextToken();
	if (st.ttype != st.TT_NUMBER)
	  throw new ParseException(st, "vertex normal must be a number");

	st.nextToken();
	if (st.ttype != st.TT_NUMBER)
	  throw new ParseException(st, "vertex normal must be a number");
      }

    }


    // grab the faces and put them in the face array
    ifaces = new int[nfaces][];
    for (int i = 0; i < nfaces; i++) {
      st.eolIsSignificant(false);

      st.nextToken();
      if (st.ttype != st.TT_NUMBER)
	throw new ParseException("st, vertex reference count must be a number");

      int nfaceverts = (int)st.nval;

      ifaces[i] = new int[nfaceverts];

      for (int j = 0; j < nfaceverts; j++) {
        st.nextToken();

	if (st.ttype != st.TT_NUMBER) {
	  throw new ParseException(st, "vertex reference must be a number");
	}
	ifaces[i][j] = (int)st.nval;
      }

      /* get colors if they're there. Currently, this only handles
        three color values between 0..255.
        Set EOL significant for color delimitation. */
      st.eolIsSignificant(true);
      st.turboParse(true);
      st.nextToken();
      if (facecolors == null) {
	facecolors = new RGB[nfaces];
      }
      if (st.ttype != st.TT_EOL) {
	int r, g, b;
        r = colorValue(st);
        st.nextToken();
        g = colorValue(st);
        st.nextToken();
        b = colorValue(st);
	facecolors[i] = new RGB(r,g,b);
      } else {
	facecolors[i] = new RGB(255,255,255);
      }
      st.eolIsSignificant(false);
      st.turboParse(false);

    }
    if (debug) {
        System.out.println("Done reading OFF");
    }
  }

  int colorValue(TurboTokenizer st) {
    int value;

    if ((st.ttype == st.TT_NUMBER) ||
        (st.ttype == st.TT_DOUBLE)) {
      if (st.nval > 1) {
	value = (int)st.nval;
      } else {
	value = (int)(255*st.nval);
      }
    } else if (st.ttype == st.TT_INT) {
      value = st.ival;
    } else {
      value = 0;
    }
    return value;
  }


  public Face[] getFaces() {
    int i, j;
    Face[] faces = new Face[nfaces];

    for (i=0; i<nfaces; ++i) {
      Face f = faces[i] = new Face();
      f.vertices = new Point[ ifaces[i].length ];
      for (j=0; j<ifaces[i].length; ++j) {
	f.vertices[j] = vertices[ ifaces[i][j] ];
      }
      f.parent = this;
      f.nverts = ifaces[i].length;
      f.cr = facecolors[i].r;
      f.cg = facecolors[i].g;
      f.cb = facecolors[i].b;
    }

    return faces;
  }




}
