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

import java.awt.Component;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import geom.geometry.Point;
import geom.jgv.util.ActionController;
import geom.jgv.model.PickSet;
import org.sjg.xml.Attribute;
import org.sjg.xml.Element;

public class MenuAction extends BaseAction implements ActionListener {

  Hashtable menuActions;
  Vector menuLabels;
  PopupMenu menu = null;
  Component c = null;
  PickSet ps = null;

  public MenuAction(Element action) {
    super();
    menuActions = new Hashtable();
    menuLabels = new Vector();
    parseMenuAction(action);
  }

  public void pickReceived(PickSet picked, Component comp) {
    c = comp;
    ps = picked;
    menu = new PopupMenu("Actions");
    Enumeration labels = menuLabels.elements();
    while (labels.hasMoreElements()) {
      menu.add((String) labels.nextElement());
    }
    c.add(menu);
    Point sc = picked.getScreenCoordinates();
    menu.show(c, (int) sc.x, (int) sc.y);
    menu.addActionListener(this);

  }

  public synchronized void actionPerformed(ActionEvent ev) {
    /*
    System.out.println("action: " + ev.getActionCommand() + " " +
                                    ev.getID() + " " +
                                    ev.getSource());
    */
    if (menu != null) {
      c.remove(menu);
      menu = null;
      Action action = (Action) menuActions.get(ev.getActionCommand());
      if (action != null) {
        action.pickReceived(ps, c);
      }
      c.requestFocus();
    }
  }

  private void parseMenuAction(Element action) {
    int maxIdx = 0;
    Enumeration items = action.elements("item");
    Hashtable actionMap = new Hashtable();
    Hashtable labelMap = new Hashtable();
    while (items.hasMoreElements()) {
      Element item = (Element) items.nextElement();
      Attribute idx = item.getAttribute("index");
      Attribute label = item.getAttribute("label");
      if (idx != null && label != null) {
        labelMap.put(idx.getValue(), label.getValue());
        actionMap.put(idx.getValue(), this.parse(item));
        int idxVal = idx.getIntValue();
        if (idxVal > maxIdx) {
          maxIdx = idxVal;
        }
      }
    }

    for (int i = 0; i <= maxIdx; i++) {
      String iKey = String.valueOf(i);
      menuLabels.addElement(labelMap.get(iKey));
      menuActions.put(labelMap.get(iKey),actionMap.get(iKey));
    }
  }

  public String toString() {
    return "Action Menu";
  }
}