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

package geom.jgv;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import java.net.*;

import geom.jgv.gui.JGVPanel;
import geom.jgv.util.ActionController;
import org.dm.client.HttpClient;
import org.dm.client.message.HelloMessage;

public class JGVApplet extends java.applet.Applet {
  JGVPanel panel;
  boolean debug = false;

  URL documentBase;
  boolean showImageLoadingStatus = true;

  Color cameraBackground = new Color((float)0.6,
                                     (float)0.6,
                                     (float)0.6);

  public void init() {
    documentBase = getDocumentBase();

    ActionController.initialize(this);

    // Configure JGVPanel to be an editor or not based
    // on the applet parameter "editor"
    String editor = getParameter("editor");
    panel = null;
    if (editor != null) {
      if (editor.equals("true") || editor.equals("1")) {
        panel = new JGVPanel(this, true);
      }
    }
    if (panel == null) {
      panel = new JGVPanel(this);
    }
    GridLayout grid = new GridLayout(1,1);
    setLayout(grid);
    add(panel);

    String file = getParameter("file");
    if (file != null && file != "") {
      panel.addFile(file);
    } else if (getParameter("xmlfile") != null ){
        panel.addXMLFile(getParameter("xmlfile"));
    } else {
      String geom = getParameter("geom");
      if (geom != null && geom != "") {
	String geom_with_newlines = geom.replace(';', '\n');
	panel.addGeom(geom_with_newlines);
      }
    }

    String server = getParameter("server");
    if (server != null) {
        HttpClient client = new HttpClient(server);
        panel.controller.setServerListener(client);
        client.sendMessage(new HelloMessage());
    }

    String debugString = getParameter("debug");
    if (debugString != null) {
      if (debugString == "true") {
	debug = true;
	panel.debug = true;
      }
    }

    String backgroundString = getParameter("background");
    if (backgroundString != null) {
      if (   backgroundString.startsWith("0x")
	  || backgroundString.startsWith("0X") ) {
	String s = backgroundString.substring(2);
	int colorint = Integer.parseInt(s, 16);
	cameraBackground = new Color( colorint );
      }
    }
    panel.setCameraBackground(cameraBackground);


  }

  public void start() {
    panel.start();
  }

  public void stop() {
    panel.stop();
  }


  public Image getImageNow(String imagePath) {
    MediaTracker tracker = new MediaTracker(this);

    Image image = null;
    if (showImageLoadingStatus) {
      showStatus("Loading image: " + imagePath);
    }
    image = getImage(documentBase, imagePath);
    tracker.addImage(image, 0);
    try {
      image = getImage(documentBase, imagePath);
      tracker.addImage(image, 0);
      tracker.waitForID(0);
    }
    catch (InterruptedException e) {
    }
    if (tracker.isErrorID(0)) {
      showStatus("ERROR loading image: " + imagePath);
      System.out.println("ERROR loading image: " + imagePath);
    }
    if (showImageLoadingStatus) {
      showStatus("");
    }
    return image;
  }

}
