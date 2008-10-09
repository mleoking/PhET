//
// Copyright (C) 2002-2004 DaerMar Communications, LLC <jgv@daermar.com>
// @author Daeron Meyer
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
import geom.jgv.util.ParseException;
import geom.jgv.util.RGB;
import org.sjg.xml.Attribute;
import org.sjg.xml.Element;
import java.util.Enumeration;
import java.util.Vector;

public class Annotation extends Geom {

    public double x, y, z;
    public RGB color = null;
    public String label = null;
    public String onNode = null;

    public Annotation() {
        super();
    }

    public Annotation(Element element) throws Exception {
        super(element);
        x = Geom.getAttributeAsDouble("x", element);
        y = Geom.getAttributeAsDouble("y", element);
        z = Geom.getAttributeAsDouble("z", element);

        try {
            double r, g, b;
            r = Geom.getAttributeAsDouble("r", element);
            g = Geom.getAttributeAsDouble("g", element);
            b = Geom.getAttributeAsDouble("b", element);
            color = new RGB(r, g, b);
        } catch (ParseException pe) {
            // Do nothing since these are optional
        }
        Attribute on = element.getAttribute("on");
        if (on != null) { onNode = on.getValue(); }
        label = Geom.getRequiredChildElement("note", element).getContents();
    }

    public Face[] getFaces() {
      // Subclasses override this with a method that actually
      // returns a list of faces.
      Face[] faces = new Face[1];
      Face f = faces[0] = new Face(label);
      f.vertices = new Point[1];
      f.vertices[0] = new Point(x, y, z);
      f.parent = this;
      if (color != null) {
        f.cr = color.r;
        f.cg = color.g;
        f.cb = color.b;
      }
      return faces;
    }

    public String toXML() {
      String retVal = "<geom-annotation";
      if (onNode != null) {
        retVal += " on=\"" + onNode + "\"";
      }
      retVal += " x=\"" + x + "\" y=\"" + y + "\" z=\"" + z + "\"" +
                " r=\"" + color.r + "\" g=\"" + color.g + "\" b=\"" + color.b + "\">";
      retVal += "<note>" + this.label + "</note>";
      retVal += "</geom-annotation>";
      return retVal;
    }
}