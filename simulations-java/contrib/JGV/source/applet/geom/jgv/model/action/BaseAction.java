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

import java.util.Enumeration;
import geom.jgv.event.PickListener;
import geom.jgv.model.action.URLAction;
import org.sjg.xml.Attribute;
import org.sjg.xml.Element;

public abstract class BaseAction implements Action {

  public BaseAction() {
  }

  public static Action parse(Element pickable) {
    Action action = null;
    Enumeration children = pickable.elements("action");
    if (children.hasMoreElements()) {
      Element child = (Element) children.nextElement();
      Attribute actionType = child.getAttribute("type");
      if (actionType != null) {
        String aType = actionType.getValue().toLowerCase();
        if (aType.equals("url")) {
          action = new URLAction(child.getContents());
        } else if (aType.equals("menu")) {
          action = new MenuAction(child);
        } else if (aType.equals("load-geom")) {
          action = new LoadGeomAction(child.getContents());
        } else if (aType.equals("appearance")) {
          action = new AppearanceAction(child);
        } else if (aType.equals("server")) {
          action = new ServerAction(child);
        }
      }
    }
    return action;
  }
}