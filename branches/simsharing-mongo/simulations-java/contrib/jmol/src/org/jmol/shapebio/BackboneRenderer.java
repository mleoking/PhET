/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-11-17 10:45:52 -0600 (Fri, 17 Nov 2006) $
 * $Revision: 6250 $

 *
 * Copyright (C) 2003-2005  The Jmol Development Team
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

package org.jmol.shapebio;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;

public class BackboneRenderer extends BioShapeRenderer {

  @Override
  protected void renderBioShape(BioShape bioShape) {
    boolean isDataFrame = viewer.isJmolDataFrame(bioShape.modelIndex);
    for (int i = bsVisible.nextSetBit(0); i >= 0; i = bsVisible.nextSetBit(i + 1)) {
      Atom atomA = modelSet.atoms[leadAtomIndices[i]];
      Atom atomB = modelSet.atoms[leadAtomIndices[i + 1]];
      if (atomA.getNBackbonesDisplayed() == 0 || atomB.getNBackbonesDisplayed() == 0
          || modelSet.isAtomHidden(atomB.getIndex()))
        continue;
      if (!isDataFrame && atomA.distance(atomB) > 10)
        continue;
      int xA = atomA.screenX, yA = atomA.screenY, zA = atomA
          .screenZ;
      int xB = atomB.screenX, yB = atomB.screenY, zB = atomB
          .screenZ;
      short colixA = Graphics3D.getColixInherited(colixes[i], atomA.getColix());
      short colixB = Graphics3D.getColixInherited(colixes[i + 1], atomB.getColix());
      mad = mads[i];
      if (mad < 0) {
        g3d.drawLine(colixA, colixB, xA, yA, zA, xB, yB, zB);
      } else {
        int width = (exportType == Graphics3D.EXPORT_CARTESIAN ? mad 
            : viewer.scaleToScreen((zA + zB) / 2, mad));
        g3d.fillCylinder(colixA, colixB, Graphics3D.ENDCAPS_SPHERICAL, width,
            xA, yA, zA, xB, yB, zB);
      }
    }
  }  
}
