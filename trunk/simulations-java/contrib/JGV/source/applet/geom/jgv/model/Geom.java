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
import java.net.*;
import java.util.*;

import geom.geometry.*;
import geom.jgv.model.action.*;
import geom.jgv.util.*;
import org.sjg.xml.*;

public class Geom {
  public Appearance appearance;
  public String nodeName;
  private Action pickAction;

  public Geom() {
    appearance = new Appearance();
    nodeName = null;
    pickAction = null;
  }

  public Geom(Element element) throws ParseException {
    this();

    // Read the node name if any
    if (element.hasAttribute("node")) {
        nodeName = element.getAttribute("node").getValue();
        ActionController.registerGeomNodeByName(nodeName, this);
    }
    // Read appearance ... (TODO)

    // Is this geom/node pickable?
    Element pickable = this.getOptionalChildElement("pickable", element);
    if (pickable != null) {
      pickAction = this.parseAction(pickable);
    }

  }

  public static Geom parse(TurboTokenizer st) throws Exception {
    Geom obj = null;

    // Get the first token to see if it's a '{'
    st.nextToken();

    // If it was a '{', deal with bracket nesting here by recursively
    // calling this parse method to read whatever Geom follows the
    // '{', then parse the matching '}'.  This use of recursion
    // conveniently allows us to use the program stack to keep track
    // of the nesting level.
    if ( st.ttype == '{' ) {
      // Deal with '{' nesting here
      obj = parse(st);
      st.nextToken();
      if ( st.ttype != '}' ) {
	throw new ParseException(st, "missing '}' at end of Geom object");
      }
    } else {
      // The first token wasn't a '{', so push it back and parse
      // an actual Geom.
      st.pushBack();

      // Get the keyword to decide what type of object it is
      st.nextToken();

      if (st.ttype != st.TT_WORD) {
	throw new ParseException(st, "missing keyword at start of Geom object");
      } else {
	try {
	  if (st.sval.endsWith("OFF")) {
	    obj = new Off(st.sval, st);
	  } else if (st.sval.endsWith("VECT")) {
	    obj = new Vect(st.sval, st);
	  } else if (st.sval.endsWith("LIST")) {
	    obj = new List(st.sval, st);
	  } else if (st.sval.endsWith("INST")) {
	    obj = new Inst(st.sval, st);
	  }
	  // add other object types here
	  else {
	    System.out.println("Unknown Geom keyword"+st.sval);
	    return null;
	  }
	}
	catch (FileNotFoundException e) {
	  System.out.println(e);
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
      }
    }

    return obj;
  }

  public static Geom parse(InputStream fis, String filename) throws Exception {
    TurboTokenizer st = new TurboTokenizer(fis);
    st.commentChar('#');

    return parse(st);
  }

  public static Geom parse(URL url) throws Exception {
    String filename = url.getFile();
    InputStream is = url.openStream();
    return parse(is, filename);
  }

  //////////////////////////////////////////////////////////
  //
  // Start parsing from an XML DOM

  public static Geom parse(Document dom) throws Exception {
    Geom obj = null;

    Element root = dom.getRoot();
    if (root == null) {
      throw new ParseException("Empty XML Message");
    } else if (!root.getName().equals("geom")) {
      throw new ParseException("Outermost tag must be <geom>");
    }
    Enumeration children = root.elements();
    if (!children.hasMoreElements()) {
      throw new ParseException("Outermost <geom> tag has no children");
    }
    obj = new List(root);

    return obj;
  }

  public static Geom parse(Element element) throws Exception {
    Geom obj = null;
    if (element.getName().equals("geom-off")) {
        obj = new Off(element);
    } else if (element.getName().equals("geom-list")) {
        obj = new List(element);
    } else if (element.getName().equals("geom-inst")) {
        obj = new Inst(element);
    } else if (element.getName().equals("geom-annotation")) {
        obj = new Annotation(element);
    }
    if (obj == null) {
        throw new ParseException("<" + element.getName() + "> is not a recognized geometry tag");
    }
    return obj;
  }

  public Action parseAction(Element pickable) {
    return BaseAction.parse(pickable);
  }

  public static Element getOptionalChildElement(String name, Element parent) throws ParseException {
    Enumeration children = parent.elements(name);
    Element retVal = null;
    if (!children.hasMoreElements()) {
        retVal = null;
    } else {
        retVal = (Element) children.nextElement();
    }
    return retVal;
  }
  public static Element getRequiredChildElement(String name, Element parent) throws ParseException {
    Enumeration children = parent.elements(name);
    Element retVal = null;
    if (!children.hasMoreElements()) {
        throw new ParseException("Did not find required tag <" + name + "> as child of <" + parent.getName() + ">");
    } else {
        retVal = (Element) children.nextElement();
    }
    return retVal;
  }

  public static double getAttributeAsDouble(String attributeName, Element node) throws ParseException {
      double retVal;

      try {
          retVal = Double.valueOf(node.getAttribute(attributeName).getValue()).doubleValue();
      } catch (Exception e) {
          throw new ParseException("Couldn't find attribute \"" + attributeName +
                                   "\" in tag <" + node.getName() + ">");
      }
      return retVal;
  }

  public static int getAttributeAsInt(String attributeName, Element node) throws ParseException {
      int retVal;

      try {
          retVal = Integer.valueOf(node.getAttribute(attributeName).getValue()).intValue();
      } catch (Exception e) {
          throw new ParseException("Couldn't find attribute \"" + attributeName +
                                   "\" in tag <" + node.getName() + ">");
      }
      return retVal;
  }


  public static Vector getContentsAsIntArray(Element node) throws ParseException {
    Vector retVal = new Vector();
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(node.getContents().getBytes());
      TurboTokenizer st = new TurboTokenizer(bais);
      while (st.ttype != st.TT_EOF) {
        st.nextToken();
        if (st.ttype != st.TT_NUMBER) {
            // Do nothing
        } else {
            retVal.addElement(new Integer((int)st.nval));
        }
      }
    } catch (IOException ie) {}
    if (retVal.size() == 0) {
        throw new ParseException("Found no integers within tag <" + node.getName() + ">");
    }
    return retVal;
  }

    public static Vector getContentsAsDoubleArray(Element node) throws ParseException {
    Vector retVal = new Vector();
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(node.getContents().getBytes());
      TurboTokenizer st = new TurboTokenizer(bais);
      while (st.ttype != st.TT_EOF) {
        st.nextToken();
        if (st.ttype != st.TT_NUMBER) {
            // Do nothing
        } else {
            retVal.addElement(new Double(st.nval));
        }
      }
    } catch (IOException ie) {}
    if (retVal.size() == 0) {
        throw new ParseException("Found no doubles within tag <" + node.getName() + ">");
    }
    return retVal;
  }

  // End of methods for parsing from an XML DOM
  //
  //////////////////////////////////////////////////////////



  /**
   * Return a list of this Geom's faces, transformed by the matrix T.
   * This method appears here, in the Geom class, for efficiency when
   * getting the faces in an INST heirarchy.  This way, all Geoms have
   * a method for returning a transformed face list, so we can
   * accumulate just the transform matrices when traversing the
   * hierarchy, actually applying them to faces only at the leaf
   * nodes.  This is more efficient than transforming the list of
   * faces once for each level in a hierarchy.  See the comments for
   * the getFaces() method in the Inst class for details.
   */
  public Face[] getFaces(HMatrix3D T) {
    Face[] faces = getFaces();
    if (faces == null) return null;
    Face[] transformed_faces = new Face[faces.length];
    Face f;
    int i,j;
    for (i=0; i<faces.length; ++i) {
      f = faces[i];
      transformed_faces[i] = f.copyAllButVertices();
      for (j=0; j<f.nverts; ++j) {
	Point p = f.vertices[j];
	Point np = transformed_faces[i].vertices[j] = new Point();
	double nw =   T.T[3]  * p.x
	    	    + T.T[7]  * p.y
		    + T.T[11] * p.z
		    + T.T[15];
	np.x = ( T.T[0] * p.x
	       + T.T[4] * p.y
	       + T.T[8] * p.z
	       + T.T[12]      ) / nw;
	np.y = ( T.T[1] * p.x
	       + T.T[5] * p.y
	       + T.T[9] * p.z
	       + T.T[13]      ) / nw;
	np.z = ( T.T[2]  * p.x
	       + T.T[6]  * p.y
	       + T.T[10] * p.z
	       + T.T[14]      ) / nw;
      }
    }
    return transformed_faces;
  }

  public Face[] getFaces() {
    // Subclasses override this with a method that actually
    // returns a list of faces.
    return null;
  }

  public Action getAction() {
    return pickAction;
  }

}
