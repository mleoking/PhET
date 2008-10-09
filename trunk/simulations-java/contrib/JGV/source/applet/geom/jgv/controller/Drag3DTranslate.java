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

public class Drag3DTranslate extends Drag3D {
  public Drag3DTranslate(Camera3D cam, HMatrix3D toAdjust, Component toRepaint) {
    super(cam, toAdjust, toRepaint);
  }

  public Drag3DTranslate(Camera3D cam, Component toRepaint) {
    super(cam, cam.O2W, toRepaint);
  }

  public boolean nudge(MouseEvent ev, double dx, double dy, int dt) {
    if(dx != 0 || dy != 0) {
      double hvec[] = new double[4];
      hvec[3] = 0;
      if(ev != null && (0 != (ev.getModifiers() & ev.BUTTON2_MASK))) {
	// Meta-drag (or middle button): translate forward/backward
	// by some fraction of the focal length.
	double frac = (dx + dy) / 2;
	hvec[2] = frac * cam.scalez();
	toAdjust.translate(0, 0,
	    -frac*Math.sqrt(pt[0]*pt[0] + pt[1]*pt[1] + pt[2]*pt[2]));

	//return true;
      }
      else {
	// Conjugate rotation to the point we want to fix.
	hvec[0] = dx * cam.scalex();
	hvec[1] = dy * cam.scaley();
      }

      cam.C2W.htransform(hvec, hvec, 1);
      toAdjust.rtranslate(hvec[0], hvec[1], hvec[2]);

      return true;
    }

    return false;
  }
}
