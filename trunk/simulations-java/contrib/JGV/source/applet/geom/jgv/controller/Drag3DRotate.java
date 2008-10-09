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

public class Drag3DRotate extends Drag3D {
  HMatrix3D incr = new HMatrix3D();

//  Drag3DRotate(Camera3D cam, HMatrix3D toAdjust, Component toRepaint) {
//      super(cam, toAdjust, toRepaint);
//  }

  public Drag3DRotate(Camera3D cam, Component toRepaint) {
      super(cam, cam.O2W, toRepaint);
  }

  public boolean nudge(MouseEvent ev, double dx, double dy, int dt) {
    if(dx != 0 || dy != 0) {
      double hvec[] = new double[4];
      if(ev != null && (0 != (ev.getModifiers() & ev.BUTTON2_MASK))) {
	// Meta-drag (or middle button): rotate in screen plane
	// by some fraction of the distance to the fixed point.
	double frac = (dx + dy) / 2;
	hvec[3] = frac * Math.PI;
      }
      else {
	hvec[0] = dy * Math.PI / 2;
	hvec[1] = -dx * Math.PI / 2;
      }

      incr.unit();
      incr.rotabout(hvec,
	  Math.sqrt(hvec[0]*hvec[0]+hvec[1]*hvec[1]+hvec[2]*hvec[2]));
      toAdjust.rmult(incr);

      return true;
    }

    return false;
  }
}
