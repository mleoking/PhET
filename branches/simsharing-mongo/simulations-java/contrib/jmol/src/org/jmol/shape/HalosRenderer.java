/* $RCSfile$
 * $Author: migueljmol $
 * $Date: 2006-03-25 09:27:43 -0600 (Sat, 25 Mar 2006) $
 * $Revision: 4696 $

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

package org.jmol.shape;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.viewer.JmolConstants;

import java.util.BitSet;

public class HalosRenderer extends ShapeRenderer {

  boolean isAntialiased;
  @Override
  protected void render() {
    Halos halos = (Halos) shape;
    boolean selectDisplayTrue = viewer.getSelectionHaloEnabled();
    boolean showHiddenSelections = (selectDisplayTrue && viewer
        .getShowHiddenSelectionHalos());
    if (halos.mads == null && halos.bsHighlight == null && !selectDisplayTrue)
      return;
    isAntialiased = g3d.isAntialiased();
    Atom[] atoms = modelSet.atoms;
    BitSet bsSelected = (selectDisplayTrue ? viewer.getSelectionSet(false) : null);
    for (int i = modelSet.getAtomCount(); --i >= 0;) {
      Atom atom = atoms[i];
      if ((atom.getShapeVisibilityFlags() & JmolConstants.ATOM_IN_FRAME) == 0)
        continue;
      boolean isHidden = modelSet.isAtomHidden(i);
      mad = (halos.mads == null ? 0 : halos.mads[i]);
      colix = (halos.colixes == null || i >= halos.colixes.length ? Graphics3D.INHERIT_ALL
          : halos.colixes[i]);
      if (selectDisplayTrue && bsSelected.get(i)) {
        if (isHidden && !showHiddenSelections)
          continue;
        if (mad == 0)
          mad = -1; // unsized
        if (colix == Graphics3D.INHERIT_ALL)
          colix = halos.colixSelection;
        if (colix == Graphics3D.USE_PALETTE)
          colix = Graphics3D.GOLD;
        else if (colix == Graphics3D.INHERIT_ALL)
          colix = Graphics3D.getColixInherited(colix, atom.getColix());
      } else if (isHidden) {
        continue;
      } else {
        colix = Graphics3D.getColixInherited(colix, atom.getColix());
      }
      if (mad != 0)
        render1(atom);
      if (!isHidden && halos.bsHighlight != null && halos.bsHighlight.get(i)) {
        mad = -2;
        colix = halos.colixHighlight;
        render1(atom);
      }       
    }
  }

  void render1(Atom atom) {
    short colixFill = (mad == -2 ? 0 : Graphics3D.getColixTranslucent(colix, true, 0.5f));
    int z = atom.screenZ;
    int diameter = mad;
    if (diameter < 0) { //unsized selection
      diameter = atom.screenDiameter;
      if (diameter == 0) {
        float ellipsemax = atom.getADPMinMax(true);
        if (ellipsemax > 0)
          diameter = viewer.scaleToScreen(z, (int) (ellipsemax * 2000));
        if (diameter == 0) {
          diameter = viewer.scaleToScreen(z, mad == -2 ? 250 : 500);
        }
      }
    } else {
      diameter = viewer.scaleToScreen(z, mad);
    }
    float d = diameter;
//    System.out.println(atom + "scaleToScreen(" + z + "," + mad +")=" + diameter);
    if (isAntialiased)
      d /= 2;
    float more = (d / 2);
    if (mad == -2)
      more /= 2;
    if (more < 8)
      more = 8;
    if (more > 20)
      more = 20;
    d += more;
    if (isAntialiased)
      d *= 2;
    if (d < 1)
      return;
    g3d.drawFilledCircle(colix, colixFill, (int) d,
        atom.screenX, atom.screenY, atom.screenZ);
  }  
}
