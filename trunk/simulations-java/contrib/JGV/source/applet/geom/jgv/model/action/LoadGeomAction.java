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

package geom.jgv.model.action;

import java.awt.Component;
import geom.jgv.util.ActionController;
import geom.jgv.model.PickSet;

public class LoadGeomAction extends BaseAction {

  private String geom;

  public LoadGeomAction(String geom) {
    super();
    this.geom = geom;
  }

  public void pickReceived(PickSet picked, Component comp) {
    ActionController.loadGeom(geom);
  }

  public String toString() {
    return "Load Geometry " + geom;
  }
}