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

package geom.jgv.controller;

import geom.geometry.Line;
import geom.geometry.Point;
import geom.jgv.event.PickListener;
import geom.jgv.model.Geom;
import geom.jgv.model.PickSet;
import geom.jgv.model.action.Action;
import geom.jgv.util.HMatrix3D;
import geom.jgv.util.PickLine;
import geom.jgv.gui.CameraCanvas;
import geom.jgv.view.Camera3D;
import geom.jgv.view.Picker;
import geom.jgv.view.BSPTree;
import java.awt.Component;
import java.awt.event.MouseEvent;

public class Drag3DAction extends Drag3DPickable implements PickListener {

    CameraCanvas cv;

    public Drag3DAction(Camera3D cam, HMatrix3D toAdjust, CameraCanvas toRepaint) {
      super(cam, toAdjust, toRepaint);
      cv = toRepaint;
      this.setPickListener(this);
    }

    public Drag3DAction(Camera3D cam, CameraCanvas toRepaint) {
      super(cam, cam.O2W, toRepaint);
      cv = toRepaint;
      this.setPickListener(this);
    }

    public boolean mouseDrag(MouseEvent ev, int x, int y) {
      return true;
    }

    public boolean mouseMoved(MouseEvent ev, int x, int y) {
      return true;
    }

    /**
     * Process a mouseDown event
     */
    public boolean mouseDown(MouseEvent ev, int x, int y) {
      PickLine pickLine = cam.getPickLine(x, y);
      Picker picker = new Picker(pickLine, cam);
      if (cv.tree != null) {
        double EyeO[] = cam.getEyeObj();
        cv.tree.pickBSP(new Point(EyeO[0], EyeO[1], EyeO[2]), picker);
        picker.getPickSet().setScreenCoordinates(new Point(x, y, 0));
        picker.getPickSet().setWorldTransform(cam.O2W);
        notifyPickListener(picker.getPickSet());
      }
      return true;
    }

    public void pickReceived(PickSet picked, Component comp) {
        System.out.println("Handling action pick");
        Geom geom = picked.getPickedGeom();
        if (geom != null) {
          Action action = geom.getAction();
          if (action != null) {
            action.pickReceived(picked, comp);
          }
        }
    }
}