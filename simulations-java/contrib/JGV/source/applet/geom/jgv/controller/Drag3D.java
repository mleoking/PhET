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

import geom.jgv.view.Camera3D;
import geom.jgv.util.HMatrix3D;
import java.awt.Component;
import java.awt.event.MouseEvent;

/**
 * Drag3D is an abstract superclass for handling mouse drags in the
 * CameraCanvas window.  It's intended to be subclassed to create
 * handlers for specific types of motions, e.g. rotation, translation,
 * orbiting, etc.  The Drag3D superclass itself keeps track of
 * information, and provides methods, that are common to all motions.
 */
public class Drag3D {

  /** Coords of the last mouse down or drag */
  int lastx, lasty;

  /** Time of the last mouse down or drag */
  long lastt;

  /** The camera asscociated with our CameraCanvas */
  Camera3D cam;

  /** The matrix to be adjusted by mouse motions */
  HMatrix3D toAdjust;

  /** The component to be repainted after adjusting the matrix */
  Component toRepaint;

  /** Lord only knows what this is */
  double pt[] = new double[3];

  public Drag3D(Camera3D cam, HMatrix3D toAdjust, Component toRepaint) {
    this.cam = cam;
    this.toAdjust = toAdjust;
    this.toRepaint = toRepaint;
  }

//  Drag3D() {
//
//  }

  /**
   * Process a mouseDown event
   */
  public boolean mouseDown(MouseEvent ev, int x, int y) {
    lastx = x;
    lasty = y;
    lastt = ev.getWhen();

    return true;
  }

  /**
   * Process a mouseDrag event.  This involves calling the "nudge"
   * method, which each subclass provides its own version of, to
   * actually adjust the toAdjust matrix.
   */
  public boolean mouseDrag(MouseEvent ev, int x, int y) {
    int dx = x - lastx;
    int dy = lasty - y;
    if(nudge(ev,
	    (x - lastx) / (double)cam.winx,
	    (lasty - y) / (double)cam.winy,
	    (int)(ev.getWhen() - lastt))) {
	toRepaint.repaint();
    }
    lastx = x;
    lasty = y;
    lastt = ev.getWhen();

    return true;
  }

  /**
   * Default nudge method --- does nothing
   */
  boolean nudge(MouseEvent ev, double dx, double dy, int dt) {
    return false;
  }

  /**
   * Default mouse moved method --- does nothing
   */
  public boolean mouseMoved(MouseEvent ev, int x, int y) {
    return false;
  }
}
