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

import geom.jgv.util.HMatrix3D;
import geom.jgv.view.Camera3D;
import java.awt.Component;
import java.awt.event.MouseEvent;

public class Drag3DScale extends Drag3D {
  public Drag3DScale(Camera3D cam, HMatrix3D toAdjust, Component toRepaint) {
    super(cam, toAdjust, toRepaint);
  }

  public Drag3DScale(Camera3D cam, Component toRepaint) {
      /* super(cam, cam.C2W, toRepaint); */
      super(cam, cam.O2W, toRepaint);
  }
/*
  boolean nudge(MouseEvent ev, double dx, double dy, int dt) {
    if(dx != 0 || dy != 0) {
       if (ev != null && !ev.isMetaDown()) {
          double frac = 1.0 - ( (dx + dy)/2.0 );
          double fov = cam.getFov();
          cam.setFov( frac * fov );
          return true;
       }
    }
    return false;
  }
  */
  public boolean nudge(MouseEvent ev, double dx, double dy, int dt) {

    if(dx != 0 || dy != 0) {
       if (ev != null && !ev.isMetaDown()) {
          double frac = 1.0 - ( (dx + dy)/2.0 );
          //double fov = cam.getFov();
          //cam.setFov( frac * fov );
          toAdjust.scale(1/frac);
          return true;
       }
    }
    return false;
  }
}
