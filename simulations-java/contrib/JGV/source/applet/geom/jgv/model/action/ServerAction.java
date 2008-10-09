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
import org.dm.client.message.ServerActionMessage;
import org.sjg.xml.*;

public class ServerAction extends BaseAction {

  private Hashtable attributes;
  private String label;

  public ServerAction(Element action) {
    super();
    attributes = new Hashtable();
    label = "";
    parseServerAction(action);
  }

  public void pickReceived(PickSet picked, Component cam) {
    ServerActionMessage sam = new ServerActionMessage(picked, attributes);
    String result = ActionController.sendServerMessage(sam);
    Action a = null;
    try {
      Document msg = Parser.parse(result);
      Element root = msg.getRoot();
      // Wrap the result in a <pickable> tag to be properly parsed by BaseAction
      result = msg.getHeader() + "<pickable>" + root.toString() + "</pickable>";
      msg = Parser.parse(result);
      root = msg.getRoot();
      a = BaseAction.parse(root);
    } catch (Exception e) {
      System.out.println("Unable to process server action");
    }
    if (a != null) {
      a.pickReceived(picked, cam);
    } else {
      System.out.println("Did not receive back a valid <action> from the server");
    }
  }

  private void parseServerAction(Element action) {

    Enumeration attr = action.attributes();
    while (attr.hasMoreElements()) {
      Attribute a = (Attribute) attr.nextElement();
      attributes.put(a.getName(), a.getValue());
    }
    if (attributes.containsKey("label")) {
      label = (String) attributes.get("label");
    }
    attributes.remove("type");
  }

  public String toString() {
    return label;
  }

}