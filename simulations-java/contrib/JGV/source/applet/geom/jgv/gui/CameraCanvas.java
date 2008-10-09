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

import geom.jgv.controller.*;
import geom.jgv.model.*;
import geom.jgv.view.*;
import geom.jgv.util.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

import geom.geometry.Plane;
import geom.geometry.Point;

import javax.swing.*;

public class CameraCanvas extends JComponent implements DragConstants {

  public BSPTree tree;
  public Camera3D camera;
  Graphics gr;
  Point eye;
  public int dragmode = DRAG_ORBIT;
  Font copyrightFont = new Font("TimesRoman", Font.PLAIN,10);
  String copyright = "";
  String version = "";
  int copyrightLength,fontDescent,versionLength,fontAscent;
  Color copyrightColor = Color.black;
  Color frameColor = Color.black;
  public PopupMenu popup = new PopupMenu("Picked");

  Image offscreen;
  boolean doublebuffered = true;

  public CameraCanvas() {
    camera = new Camera3D();

    FontMetrics fontMetrics = getFontMetrics(copyrightFont);
    copyrightLength = fontMetrics.stringWidth(copyright);
    versionLength = fontMetrics.stringWidth(version);
    fontDescent = fontMetrics.getMaxDescent();
    fontAscent = fontMetrics.getMaxAscent();


    popup.add("Hello");
    popup.add("Goodbye");
    this.add(popup);
  }

  public void paint(Graphics g) {
    if (tree == null) return;

    int width = getSize().width;
    int height = getSize().height;
    camera.setSize(width, height);
    camera.preView();

    double EyeO[] = camera.getEyeObj();
    eye = new Point(EyeO[0], EyeO[1], EyeO[2]);
    gr = g;

    if (tree == null) {
      System.out.println("No BSP tree set for this CameraCanvas");
      return;
    }
    else tree.renderBSP(eye, gr, camera);

    g.setFont(copyrightFont);
    g.setColor(copyrightColor);
    g.drawString(copyright,
                 (width-copyrightLength)/2,
                 height-fontDescent-3);
    g.drawString(version,
                 3,
                 fontAscent+3);
    g.drawRect(0, 0, width-1, height-1);
  }

  public final void setTree(BSPTree t) {
    tree = t;
    camera.normalize(t);
  }

  public void reset() {
    camera.reset();
  }

  public void setBackground(Color c) {
    super.setBackground(c);
    double r = c.getRed()   / 255.0;
    double g = c.getGreen() / 255.0;
    double b = c.getBlue()  / 255.0;
    double brightness = Math.sqrt( (r*r+g*g+b*b) );
    if (brightness < Math.sqrt(3.0)/2) {
        copyrightColor = Color.white;
    } else {
        copyrightColor = Color.black;
    }
  }

  public String toString() {
    String val = "";

    if (eye != null) {
      val += "eye: ";
      val += eye.toString();
      val += "\n";
    }
    if (camera != null) {
      val += "camera: ";
      val += camera.toString();
      val += "\n";
    }
    if (tree != null) {
      val += "tree: ";
      val += tree.toString();
      val += "\n";
    }

    val += "---";

    return val;
  }

  // This update method should be pluggable into anything that's a Component
  // (e.g. a subclass of Panel.)
  // We assume two instance variables declared above:
  //	 Image offscreen;		// Scratch space
  //	 boolean doublebuffered;	// If true, do double-buffering
  // With luck this may be all you need.
  public void update(Graphics screeng){
    Rectangle area = screeng.getClipBounds(); //getClipRect();
    Graphics g;

    if (doublebuffered) {
      /* If we're double-buffering, we need an (off-screen) image to
       * draw into.  Keep one lying around in "offscreen".
       * If that hasn't yet been initialized, or if our size has
       * changed since then, then create a new one.
       */
      if(offscreen == null ||
         offscreen.getWidth(null) != area.width ||
	 offscreen.getHeight(null) != area.height) {
	offscreen = createImage(area.width, area.height);
      }
      /* We'll render into the off-screen image's graphics area.
       * Start by clearing it to the background color.
       */
      g = offscreen.getGraphics();
    }
    else {
      /* Otherwise, we're single-buffered; render directly to the screen.  */
      g = screeng;
    }

    g.setColor(getBackground());
    g.fillRect(0, 0, area.width, area.height);
    g.setColor(getForeground());
    paint(g);

    /*
     * If we were double-buffering, finish by copying the image we just
     * drew into the relevant piece of the screen.
     */
    if (doublebuffered) {
      screeng.drawImage(offscreen,
	                area.x, area.y, area.width, area.height,
	                this);
    }
  }

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        paint( g );
    }
}

