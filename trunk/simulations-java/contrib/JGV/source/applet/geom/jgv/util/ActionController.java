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

package geom.jgv.util;

import java.applet.Applet;
import java.applet.AppletContext;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Hashtable;
import geom.jgv.controller.JGVController;
import geom.jgv.model.Geom;
import org.dm.client.HttpClient;
import org.dm.client.message.BaseMessage;

public abstract class ActionController {

  static Applet applet = null;
  static JGVController controller = null;
  static Hashtable nodesByName = null;
  static HttpClient sclient = null;

  public static void initialize(Applet app) {
    applet = app;
  }

  public static void setController(JGVController c) {
    controller = c;
  }

  public static void setServerListener(HttpClient client) {
    sclient = client;
  }

  public static void loadURL(String url) {
    System.out.println("Loading URL: " + url);
    if (applet == null) { return; }
    // Show the document in a new window
    try {
      applet.getAppletContext().showDocument(new URL(url), "_blank");
    } catch (MalformedURLException me) {
      System.out.println("Unable to load URL: " + url);
    }
  }

  public static void loadGeom(String geom) {
    if (controller != null) {
      controller.loadGeom(geom);
    }
  }

  public synchronized static void registerGeomNodeByName(String name, Geom node) {
    if (nodesByName == null) {
      nodesByName = new Hashtable();
    }
    nodesByName.put(name, node);
  }
  public static Geom getGeomNodeByName(String name) {
    Geom retVal = null;
    if (nodesByName != null) {
      retVal = (Geom) nodesByName.get(name);
    }
    return retVal;
  }

  public static String sendServerMessage(BaseMessage msg) {
    String retVal = "";
    if (sclient != null) {
      retVal = sclient.sendMessage(msg);
    }
    return retVal;
  }
}