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

package geom.jgv.controller;

import geom.jgv.event.PickListener;
import geom.jgv.gui.JGVPanel;
import geom.jgv.gui.ControlPanel;
import geom.jgv.gui.CameraCanvas;
import geom.jgv.model.Annotation;
import geom.jgv.model.Geom;
import geom.jgv.model.List;
import geom.jgv.util.ActionController;
import geom.jgv.view.Camera3D;
import org.sandia.PSGr;
import org.dm.client.HttpClient;
import org.dm.client.message.PrintMessage;
import org.dm.client.message.AnnotationMessage;
import java.io.ByteArrayOutputStream;
import java.awt.Component;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Vector;

public class JGVController implements DragConstants, KeyListener, MouseMotionListener, MouseListener {

  JGVPanel parent;
  ControlPanel cp = null;
  CameraCanvas canvas = null;
  HttpClient client = null;
  Drag3D mousehandler[] = null;
  Drag3DPickable actionhandler[] = null;
  int dragmode = DRAG_ORBIT;
  int actionmode = DRAG_ACTION;
  boolean rightmouse = false;

  public JGVController(JGVPanel parent) {
    this.parent = parent;
    ActionController.setController(this);
  }

  public void setCamera(CameraCanvas canvas) {

    this.canvas = canvas;

    mousehandler = new Drag3D[10];
    mousehandler[DRAG_ORBIT] = new Drag3DOrbit(canvas.camera, canvas);
    mousehandler[DRAG_ROTATE] = new Drag3DOrbit(canvas.camera, canvas);
    mousehandler[DRAG_TRANSLATE] = new Drag3DTranslate(canvas.camera, canvas);
    mousehandler[DRAG_SCALE] = new Drag3DScale(canvas.camera, canvas);
    mousehandler[DRAG_PICK] = new Drag3DPick(canvas.camera, canvas);

    actionhandler = new Drag3DPickable[10];
    actionhandler[DRAG_ACTION] = new Drag3DAction(canvas.camera, canvas);
    actionhandler[DRAG_ANNOTATE] = new Drag3DAnnotate(canvas.camera, canvas);
    actionhandler[DRAG_SELECT] = new Drag3DAnnotate(canvas.camera, canvas);

    canvas.addMouseListener(this);
    canvas.addMouseMotionListener(this);
  }

  public boolean processEvent(KeyEvent e) {

    switch (e.getKeyChar()) {

        case 'm':
        case 'M':
            setDragMode(DRAG_PICK);
            return true;

        case 'r':
        case 'R':
            setDragMode(DRAG_ROTATE);
            return true;

        case 't':
        case 'T':
            setDragMode(DRAG_TRANSLATE);
            return true;

        case 's':
        case 'S':
            setDragMode(DRAG_SCALE);
            return true;

        case 'h':
        case 'H':
            /* parent.cameraCanvas.popup.show(parent, 100, 100); */
            parent.reset();
            return true;

        case 'p':
        case 'P':
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

            PSGr g = new PSGr(byteArray, parent.getGraphics());
            parent.cameraCanvas.paint(g);
            g.showpage();
            g.dispose();
            g = null;

            String output = byteArray.toString();
            if (client != null) {
                client.sendMessage(new PrintMessage(output, canvas.camera.O2W));
            } else {
                System.out.println(output);
            }
            return true;
    }

    return false;
  }

  public void keyPressed(KeyEvent e) {
      this.processEvent(e);
  }
  public void keyTyped(KeyEvent e) {}
  public void keyReleased(KeyEvent e) {}
  int mc = 0;
  public void mouseMoved(MouseEvent e) {
    // System.out.println("Mouse Moved");
    if (actionmode == this.DRAG_ACTION) {
      mousehandler[DRAG_PICK].mouseMoved(e, e.getX(), e.getY());
    } else if (actionmode == this.DRAG_ANNOTATE) {
      actionhandler[DRAG_ANNOTATE].mouseMoved(e, e.getX(), e.getY());
    }
  }
  public void mouseExited(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) { rightmouse = false; }
  public void mouseClicked(MouseEvent e) {
    //System.out.println("Mouse Clicked " + e);
    actionhandler[actionmode].mouseDown(e, e.getX(), e.getY());
  }

  public void mousePressed(MouseEvent e) {

      if (0 != (e.getModifiers() & (e.BUTTON3_MASK | e.SHIFT_MASK))) {
        //System.out.println("Button 3 or shift click");
        //canvas.popup.show(canvas, e.getX(), e.getY());
        if (actionmode == this.DRAG_ACTION) {
          actionhandler[actionmode].mouseDown(e, e.getX(), e.getY());
        }
        rightmouse = true;
      } else {
        // System.out.println("Mouse Pressed");
        mousehandler[dragmode].mouseDown(e, e.getX(), e.getY());

      }
//      parent.getParent().requestFocus();

  }

  public void mouseDragged(MouseEvent e) {

    if ((0 != (e.getModifiers() & (e.BUTTON3_MASK | e.SHIFT_MASK)))
        || rightmouse) {
      actionhandler[actionmode].mouseDrag(e, e.getX(), e.getY());
    } else {
      // System.out.println("Mouse Dragged " + e);
      mousehandler[dragmode].mouseDrag(e, e.getX(), e.getY());
    }
  }

  public void setDragMode(int dragmode) {
    this.dragmode = dragmode;
    if (cp != null) {
      cp.setDragMode(dragmode);
    }
  }

  public void setActionMode(int actionmode) {
    this.actionmode = actionmode;
    if (cp != null) {
      cp.setActionMode(actionmode);
    }
  }

  public void setPickListener(int actionmode, PickListener listener) {
    this.actionhandler[actionmode].setPickListener(listener);
  }

  public void setControlPanel(ControlPanel cp) {
    this.cp = cp;
    cp.setController(this);
  }

  public void setServerListener(HttpClient client) {
     this.client = client;
     ActionController.setServerListener(client);
  }

  public void loadGeom(String geomName) {
    if (geomName.endsWith(".xml")) {
      parent.addXMLFile(geomName);
    } else {
      parent.addFile(geomName);
    }
  }

  public void addAnnotation(Annotation annotation, String nodeName) {
    Geom world = parent.getWorld();
    if (!(world instanceof List)) {
      List newWorld = new List();
      newWorld.getChildren().addElement(world);
      world = newWorld;
    }
    ((List)world).getChildren().addElement(annotation);
    parent.setWorld(world);
    ActionController.sendServerMessage(new AnnotationMessage(annotation));
  }

  public void deleteAnnotation(Annotation annotation) {
    Geom world = parent.getWorld();
    if (world instanceof List) {
      List root = (List) world;
      deleteAnnotation(annotation, root);
    }
    parent.setWorld(world);
  }

  private void deleteAnnotation(Annotation ann, List root) {
    Vector children = root.getChildren();
    if (children.contains(ann)) {
      children.removeElement(ann);
    } else {
      Enumeration e = children.elements();
      while (e.hasMoreElements()) {
        Geom g = (Geom) e.nextElement();
        if (g instanceof List) {
          deleteAnnotation(ann, (List) g);
        }
      }
    }
  }

  public void refreshDisplay() {
    if (canvas != null) {
      canvas.repaint();
    }
  }
}
