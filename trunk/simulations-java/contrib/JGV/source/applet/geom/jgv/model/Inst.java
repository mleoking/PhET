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

import geom.jgv.util.HMatrix3D;
import geom.jgv.util.ParseException;
import geom.jgv.util.TurboTokenizer;
import geom.jgv.util.ParseException;
import org.sjg.xml.Element;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Vector;

//import geom.geometry.*;

public class Inst extends Geom {
  Geom geom;			// child geom
  HMatrix3D transforms[];

  public Inst() {
    super();
  }

  public Inst(Element element) throws Exception {
    super(element);

    boolean haveGeom = false;
    boolean haveTransform = false;

    Vector tmpT = new Vector();
    Enumeration children = element.elements();
    while (children.hasMoreElements()) {
        Object child = children.nextElement();
        if (child instanceof Element) {
            Element childElement = (Element) child;
            String name = childElement.getName();
            if (name.equals("transform")) {
                Vector transform = this.getContentsAsDoubleArray(childElement);
                double T[] = new double[transform.size()];
                Enumeration nums = transform.elements();
                int idx = 0;
                while (nums.hasMoreElements()) {
                    Double n = (Double) nums.nextElement();
                    T[idx++] = n.doubleValue();
                }
                tmpT.addElement(new HMatrix3D(T));
                haveTransform = true;
            } else if (name.startsWith("geom")) {
                // Try parsing it as a geom
                geom = Geom.parse(childElement);
            }
        }
    }

    transforms = new HMatrix3D[tmpT.size()];
    tmpT.copyInto(transforms);


  }

  public Inst(String keyword, TurboTokenizer st) throws Exception {
    this();
    parse(keyword, st);
  }

  void parse(String keyword, TurboTokenizer st) throws Exception {

    boolean haveGeom = false;
    boolean haveTransform = false;

    do {
      st.nextToken();
      if (st.ttype != st.TT_WORD) {
	throw new ParseException(st, "\"geom\", \"transform\", or \"transforms\" expected in Inst");
      }
      if (st.sval.equals("geom")) {
	if (haveGeom) {
	  throw new ParseException(st, "INST can only have one geom");
	}
	try {
	  geom = Geom.parse(st);
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
	haveGeom = true;
      } else if (st.sval.equals("transform")) {
	if (haveTransform) {
	  throw new ParseException(st, "INST can only have one \"transform\" or \"transforms\"");
	}
	try {
	  HMatrix3D T = HMatrix3D.parse(st);
	  transforms = new HMatrix3D[1];
	  transforms[0] = T;
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
	haveTransform = true;
      } else if (st.sval.equals("transforms")) {
	if (haveTransform) {
	  throw new ParseException(st, "INST can only have one \"transform\" or \"transforms\"");
	}
	// Peek at next token; if it's a number, assume it's the
	// first entry of another transform, and add it to tmpT.
	// We collect transforms in this way until we encounter
	// a token that's not a number.
	Vector tmpT = new Vector();
	int type;
	do {
	  st.nextToken();
	  type = st.ttype;
	  st.pushBack();
	  if (type == st.TT_NUMBER) {
	    HMatrix3D T;
	    try {
	      T = HMatrix3D.parse(st);
	      tmpT.addElement(T);
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	  }
	} while (type == st.TT_NUMBER);
	// Now copy the transforms from tmpT into an array of the
	// appropriate size (and forget about the Vector tmpT --- it
	// was just for temporary use during the parsing).
	transforms = new HMatrix3D[tmpT.size()];
	tmpT.copyInto(transforms);
	haveTransform = true;
      } else {
	throw new ParseException(st, "\"geom\", \"transform\", or \"transforms\" expected in Inst");
      }
    } while (!haveGeom || !haveTransform);

  }

  public Face[] getFaces() {
    return getFaces((HMatrix3D)null);
  }

  /** Here's where we apply this Inst's transform(s).  We fetch the
   * child's faces by calling geom.getFaces(newT) , where newT is the
   * matrix to transform the faces by (the product of the one passed
   * in with the current Inst transform).  If the child geom turns out
   * to be another Inst, the call to geom.getFaces(newT) invokes this
   * method recursively to accumulate the matrices.  Once we get to a
   * leaf node the recursion bottoms out and Geom.getFaces(T) gets the
   * actual list of faces by calling getFaces() (no arg) and applies
   * the final matrix to them.  */
  public Face[] getFaces(HMatrix3D T) {
    Face faces[] = null;
    int i;
    for (i=0; i<transforms.length; ++i) {
      HMatrix3D newT = new HMatrix3D(transforms[i]);
      if (T != null) newT.rmult(T);
      Face geomFaces[] = geom.getFaces(newT);
      if (faces==null) {
	faces = new Face[ transforms.length * geomFaces.length ];
      }
      System.arraycopy(geomFaces, 0,
		       faces, i*geomFaces.length,
		       geomFaces.length);
    }
    if (nodeName != null) {
      for (i = 0; i < faces.length; i++) {
        faces[i].parent = this;
      }
    }
    return faces;
  }

}
