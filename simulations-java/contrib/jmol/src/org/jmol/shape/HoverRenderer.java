/* $RCSfile$
 * $Author: nicove $
 * $Date: 2010-07-31 02:51:00 -0700 (Sat, 31 Jul 2010) $
 * $Revision: 13783 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.shape;

import org.jmol.modelset.Atom;
import org.jmol.modelset.LabelToken;

public class HoverRenderer extends ShapeRenderer {
  @Override
  protected void render() {
    if (viewer.isNavigating())
      return;
    Hover hover = (Hover) shape;
    boolean antialias = g3d.isAntialiased();
    if (hover.atomIndex >= 0) {
      Atom atom = modelSet.atoms[hover.atomIndex];
      String label = (hover.specialLabel != null ? hover.specialLabel 
          : hover.atomFormats != null
          && hover.atomFormats[hover.atomIndex] != null ? 
              LabelToken.formatLabel(viewer, atom, hover.atomFormats[hover.atomIndex])
          : hover.labelFormat != null ? LabelToken.formatLabel(viewer, atom, fixLabel(atom, hover.labelFormat))
              : null);
      if (label == null)
        return;
      Text text = hover.hoverText;
      text.setText(label);
      text.setXY(atom.screenX, atom.screenY);
      text.render(g3d, 0, antialias ? 2 : 1, false);
    } else if (hover.text != null) {
      Text text = hover.hoverText;
      text.setText(hover.text);
      text.setXY(hover.xy.x, hover.xy.y);
      text.render(g3d, 0, antialias ? 2 : 1, false);
    }
  }
  
  String fixLabel(Atom atom, String label) {
    if (label == null)
      return null;
    return (viewer.isJmolDataFrame(atom.getModelIndex()) 
        && label.equals("%U") ?"%W" : label);
  }
}
