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

import geom.jgv.util.ParseException;
import geom.jgv.util.TurboTokenizer;
import org.sjg.xml.Element;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Vector;

public class List extends Geom {
  Vector children = new Vector();

  public List() {
    super();
  }

  /**
   * Read a list object from an XML element
   */
  public List(Element element) throws Exception {
      super(element);
      Enumeration elemChildren = element.elements();
      while (elemChildren.hasMoreElements()) {
          Object child = elemChildren.nextElement();
          if (child instanceof Element) {
              Element elem = (Element) child;
              children.addElement(Geom.parse(elem));

          }
      }
  }

  public List(String keyword, TurboTokenizer st) throws Exception {
    this();
    parse(keyword, st);
  }

  void parse(String keyword, TurboTokenizer st) throws Exception {

    // Parse children until either we hit end of file or a '}'
    boolean done = false;
    do {
      st.nextToken();
      if (st.ttype == st.TT_EOF) {
	done = true;
      } else if (st.ttype == '}') {
	st.pushBack();
	done = true;
      } else {
	st.pushBack();
	Geom obj;
	try {
	  obj = Geom.parse(st);
	  children.addElement(obj);
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
      }
    } while (!done);

  }

  public Face[] getFaces() {

    int nchildren = children.size();
    Face childfaces[][] = new Face[nchildren][];
    int i, nfaces = 0;

    for (i=0; i<nchildren; ++i) {
      childfaces[i] = ((Geom)children.elementAt(i)).getFaces();
      nfaces += childfaces[i].length;
    }

    Face faces[] = new Face[nfaces];

    int offset = 0;
    for (i=0; i<nchildren; ++i) {
      System.arraycopy(childfaces[i], 0,
		       faces, offset,
		       childfaces[i].length);
      offset += childfaces[i].length;
    }

    return faces;
  }

  public Vector getChildren() {
    return children;
  }

}
