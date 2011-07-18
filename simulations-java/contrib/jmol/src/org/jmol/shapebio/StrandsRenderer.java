/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-03-29 04:39:40 -0500 (Thu, 29 Mar 2007) $
 * $Revision: 7248 $
 *
 * Copyright (C) 2003-2006  Miguel, Jmol Development, www.jmol.org
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

import javax.vecmath.Point3i;

public class StrandsRenderer extends BioShapeRenderer {

  protected int strandCount;
  protected float strandSeparation;
  protected float baseOffset;

  @Override
  protected void renderBioShape(BioShape bioShape) {
    if (!setStrandCount())
      return;
    render1();
  }
  
  protected boolean setStrandCount() {
    if (wingVectors == null)
      return false;
    strandCount = viewer.getStrandCount(((Strands) shape).shapeID);
    strandSeparation = (strandCount <= 1) ? 0 : 1f / (strandCount - 1);
    baseOffset = ((strandCount & 1) == 0 ? strandSeparation / 2
        : strandSeparation);
    return true;
  }

  protected void render1() {
    Point3i[] screens;
    for (int i = strandCount >> 1; --i >= 0;) {
      float f = (i * strandSeparation) + baseOffset;
      screens = calcScreens(f);
      render1Strand(screens);
      viewer.freeTempScreens(screens);
      screens = calcScreens(-f);
      render1Strand(screens);
      viewer.freeTempScreens(screens);
    }
    if (strandCount % 2 == 1) {
      screens = calcScreens(0f);
      render1Strand(screens);
      viewer.freeTempScreens(screens);
    }
  }

  private void render1Strand(Point3i[] screens) {
    for (int i = bsVisible.nextSetBit(0); i >= 0; i = bsVisible.nextSetBit(i + 1))
      renderHermiteCylinder(screens, i);
  }
}
