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

package geom.jgv.model.action;

import java.awt.*;
import java.util.Enumeration;
import geom.jgv.event.*;
import geom.jgv.model.*;
import geom.jgv.util.ActionController;
import org.sjg.xml.Attribute;
import org.sjg.xml.Element;

public class AppearanceAction extends BaseAction {

  private Boolean drawfaces = null;
  private Boolean drawedges = null;
  private String onNode = "";

  public AppearanceAction(Element action) {
    parseAppearanceAction(action);
  }

  public void pickReceived(PickSet picked, Component cam) {
    Geom node = ActionController.getGeomNodeByName(onNode);
    if (node != null) {
      if (drawfaces != null) { node.appearance.drawfaces = drawfaces.booleanValue(); }
      if (drawedges != null) { node.appearance.drawedges = drawedges.booleanValue(); }
    }
    cam.repaint();
  }

  private void parseAppearanceAction(Element action) {

    Attribute on = action.getAttribute("on");
    if (on != null) {
      this.onNode = on.getValue();
    } else {
      System.out.println("Found no on attribute");
    }
    Enumeration drawface = action.elements("drawface");
    if (drawface.hasMoreElements()) {
      Element df = (Element) drawface.nextElement();
      if ("false".equals(df.getContents())) {
          drawfaces = new Boolean(false);
      } else if ("true".equals(df.getContents())) {
          drawfaces = new Boolean(true);
      }
    }
    Enumeration drawedge = action.elements("drawedge");
    if (drawedge.hasMoreElements()) {
      Element de = (Element) drawedge.nextElement();
      if ("false".equals(de.getContents())) {
        drawedges = new Boolean(false);
      } else if ("true".equals(de.getContents())) {
        drawedges = new Boolean(true);
      }
    }
  }

  public String toString() {
      return "Change Appearance";
  }
}