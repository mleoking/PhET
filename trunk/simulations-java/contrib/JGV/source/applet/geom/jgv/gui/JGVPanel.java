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

package geom.jgv.gui;

import geom.jgv.JGVApplet;
import geom.jgv.controller.DragConstants;
import geom.jgv.controller.JGVController;
import geom.jgv.model.Geom;
import geom.jgv.view.BSPTree;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.applet.Applet;
import org.sjg.xml.Parser;
import org.sjg.xml.Document;

public class JGVPanel extends Panel implements DragConstants {
  public int dragmode = DRAG_ORBIT;

  JGVApplet my_applet;
  boolean app;
  ControlPanel controlPanel;
  public CameraCanvas cameraCanvas;
  public boolean debug = false;
  Geom world = new Geom();
  public JGVController controller = new JGVController(this);
  long time;

  // Defines whether this instance is a viewer or an editor.
  boolean editor = false;

  public JGVPanel(JGVApplet ap, boolean editor) {
    this.editor = editor;
    my_applet = ap;
    app = false;
    ap.addKeyListener(controller);
    init();
  }

  public JGVPanel(JGVApplet ap) {
    this(ap, false);
  }

  public JGVPanel(Frame parent) {
    app = true;
    init();
  }

  public void add(InputStream fis, String filename) throws Exception {
    filename = filename.substring(filename.lastIndexOf('/')+1);
    world = Geom.parse(fis, filename);
    if (world != null) {
      recalcWorld();
    }
  }

  public void add(URL url) throws Exception {
    String filename = url.getFile();
    InputStream is = url.openStream();
    add(is, filename);
  }

  public void addGeom(String geom) {
    ByteArrayInputStream bais = new ByteArrayInputStream(geom.getBytes());
    try {
        add(bais, "geom string");
    } catch (Exception e) {
        System.out.println(e);
    }
  }

  public void addFile(String filename) {
    try {
      if (app) {
	add(new FileInputStream(filename), filename);
      }
      else {
	add(new URL(my_applet.getDocumentBase(), filename));
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

    public void addXMLFile(InputStream is) throws Exception {
        if (is != null) {
        Document dom = Parser.parse(is);
        world = Geom.parse(dom);
        if (world != null) {
          recalcWorld();
        }
      }
    }

  public void addXMLFile(String filename) {
    try {
      InputStream is = null;
      if (app) {
	is = new FileInputStream(filename);
      }
      else {
	URL url = new URL(my_applet.getDocumentBase(), filename);
        is = url.openStream();
      }
      addXMLFile( is );
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  public void init() {

    controlPanel = new ControlPanel(this, my_applet);
    cameraCanvas = new CameraCanvas();

    controller.setCamera(cameraCanvas);
    controller.setControlPanel(controlPanel);

    GridBagLayout gridbag = new GridBagLayout();
    setLayout(gridbag);

    GridBagConstraints c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    gridbag.setConstraints(controlPanel, c);
    add(controlPanel);

    c.fill = GridBagConstraints.BOTH;
    c.weightx = 1;
    c.weighty = 1;
    gridbag.setConstraints(cameraCanvas, c);
    add(cameraCanvas);

    repaint();
  }

  public void setCameraBackground(Color color) {
    cameraCanvas.setBackground(color);
  }

  void startClock() {
    time = System.currentTimeMillis();
  }

  long checkClock() {
    long now = System.currentTimeMillis();
    return now - time;
  }

  public void recalcWorld() {
    BSPTree bsp = new BSPTree();
    bsp.debug = this.debug;
    startClock();
    bsp.build(world);
    long t = checkClock();
//  System.out.println("BSPTree build time = "+t+" ms");

    if (debug) System.out.println("BSP Tree = \n" + bsp.toString());
    cameraCanvas.setTree(bsp);
    cameraCanvas.repaint();
  }


  public void reset() {
    cameraCanvas.reset();
    cameraCanvas.repaint();
  }

  public void start() {
  }

  public void stop() {
  }

  public void setEditor(boolean editor) {
    this.editor = editor;
  }

  public boolean isEditor() {
    return editor;
  }

  public void setWorld(Geom world) {
    this.world = world;
    BSPTree bsp = new BSPTree();
    bsp.debug = this.debug;
    startClock();
    bsp.build(world);
    long t = checkClock();
//  System.out.println("BSPTree build time = "+t+" ms");

    if (debug) System.out.println("BSP Tree = \n" + bsp.toString());
    cameraCanvas.tree = bsp;
    cameraCanvas.repaint();
  }

  public Geom getWorld() {
    return world;
  }

}
