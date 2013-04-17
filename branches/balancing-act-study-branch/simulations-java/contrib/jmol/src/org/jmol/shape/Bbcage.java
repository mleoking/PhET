/* $RCSfile$
 * $Author: nicove $
 * $Date: 2010-07-31 02:51:00 -0700 (Sat, 31 Jul 2010) $
 * $Revision: 13783 $
 *
 * Copyright (C) 2002-2006  Miguel, Jmol Development, www.jmol.org
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
 *  Lesser General License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.shape;

import java.util.BitSet;

import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.StateManager;

public class Bbcage extends FontLineShape {

  @Override
  public void initShape() {
    super.initShape();
    font3d = g3d.getFont3D(JmolConstants.AXES_DEFAULT_FONTSIZE);
    myType = "boundBox";
  }

  boolean isVisible;
  int mad;
  
  @Override
  public void setVisibilityFlags(BitSet bs) {
    isVisible = ((mad = viewer.getObjectMad(StateManager.OBJ_BOUNDBOX)) != 0);
    if (!isVisible)
      return;
    BitSet bboxModels = viewer.getBoundBoxModels();
    if (bboxModels == null)
      return;
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
      if (bboxModels.get(i))
        return;
    isVisible = false;
  }
  
}
