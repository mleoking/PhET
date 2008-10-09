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

import geom.jgv.util.HMatrix3D;
import geom.jgv.util.PickLine;
import geom.jgv.event.PickListener;
import geom.jgv.gui.CameraCanvas;
import geom.jgv.model.PickSet;
import geom.jgv.view.Camera3D;

public class Drag3DPickable extends Drag3D {

  protected PickListener listener = null;

  public Drag3DPickable(Camera3D cam, HMatrix3D toAdjust, CameraCanvas toRepaint) {
    super(cam, toAdjust, toRepaint);
  }

  public Drag3DPickable(Camera3D cam, CameraCanvas toRepaint) {
    super(cam, cam.O2W, toRepaint);
  }

  public synchronized void setPickListener(PickListener listener) {
    this.listener = listener;
  }

  protected void notifyPickListener(PickSet picked) {
    if (listener != null) {
      listener.pickReceived(picked, this.toRepaint);
    }
  }

}