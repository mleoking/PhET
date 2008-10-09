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

package org.dm.client.message;

/**
 * <p>Title: JGV</p>
 * <p>Description: Update/Extend JGV</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Daeron Meyer
 * @version 1.0
 */

import java.util.Enumeration;
import java.util.Hashtable;
import geom.geometry.Point;
import geom.jgv.model.*;
import geom.jgv.model.action.ServerAction;
import geom.jgv.util.HMatrix3D;

public class ServerActionMessage extends BaseMessage {

  public ServerActionMessage(PickSet ps, Hashtable params) {
    super();
    this.msgType = "action";
    this.content = this.getXMLHeader();

    // Parameters
    Enumeration e = params.keys();
    String paramStr = "";
    while (e.hasMoreElements()) {
      String key = (String) e.nextElement();
      paramStr += " " + key + "=\"" + params.get(key) + "\"";
    }
    this.content += "<params" + paramStr + "/>\n";


    // On (geom node name)
    Face f = ps.getPickedFace();
    if (f != null) {
      String nodeName = f.parent.nodeName;
      if (nodeName != null) {
        this.content += "<on>" + nodeName + "</on>\n";
      }
    }

    // Pick point
    Point p = ps.getPickedPoint();
    if (p != null) {
      this.content += "<pickpoint x=\"" + p.x + "\" \""
                                        + p.y + "\" \""
                                        + p.z + "\" />\n";
    }

    // O2W Transform
    this.content += "<transform>\n";
    HMatrix3D O2W = ps.getWorldTransform();
    if (O2W != null) {
      for (int i = 0; i < O2W.T.length; i++) {
        this.content += O2W.T[i] + " ";
      }
    }
    this.content += "</transform>\n";
    this.content += this.getXMLFooter();
    //System.out.println(this.content);
  }

}