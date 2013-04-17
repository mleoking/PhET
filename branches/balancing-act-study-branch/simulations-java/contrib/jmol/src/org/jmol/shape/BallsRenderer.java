/* $RCSfile$
 * $Author: nicove $
 * $Date: 2010-07-31 02:51:00 -0700 (Sat, 31 Jul 2010) $
 * $Revision: 13783 $

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

import java.util.BitSet;

import org.jmol.api.JmolMolecule;
import org.jmol.g3d.*;
import org.jmol.modelset.Atom;

import javax.vecmath.*;
public class BallsRenderer extends ShapeRenderer {

  private int minX, minY, minZ, maxX, maxY, maxZ;

  @Override
  protected void render() {
    // minX = rectClip.x;
    // maxX = minX + rectClip.width;
    // minY = rectClip.y;
    // maxY = minY + rectClip.height;
    int atomCount = modelSet.getAtomCount();
    boolean firstPass = g3d.setColix(Graphics3D.BLACK);
    boolean slabbing = viewer.getSlabEnabled();
    boolean slabByMolecule = viewer.getSlabByMolecule();
    boolean slabByAtom = viewer.getSlabByAtom();
    boolean renderCrosshairs = (atomCount > 0
        && viewer.getShowNavigationPoint()
        && exportType == Graphics3D.EXPORT_NOT && firstPass);
    boolean renderBalls = !viewer.getWireframeRotation()
        || !viewer.getInMotion();

    if (slabbing) {
      minZ = g3d.getSlab();
      maxZ = g3d.getDepth();
    }
    if (renderCrosshairs) {
      minX = Integer.MAX_VALUE;
      maxX = Integer.MIN_VALUE;
      minY = Integer.MAX_VALUE;
      maxY = Integer.MIN_VALUE;
    }

    Atom[] atoms = modelSet.atoms;
    BitSet bsOK = viewer.transformAtoms(firstPass);
    if (firstPass && slabByMolecule && slabbing) {
      JmolMolecule[] molecules = modelSet.getMolecules();
      int moleculeCount = modelSet.getMoleculeCountInModel(-1);
      for (int i = 0; i < moleculeCount; i++) {
        JmolMolecule m = molecules[i];
        int j = 0;
        int pt = m.firstAtomIndex;
        if (!bsOK.get(pt))
          continue;
        for (; j < m.atomCount; j++, pt++)
          if (g3d.isClippedZ(atoms[pt].screenZ
              - (atoms[pt].screenDiameter >> 1)))
            break;
        if (j != m.atomCount) {
          pt = m.firstAtomIndex;
          for (int k = 0; k < m.atomCount; k++) {
            bsOK.clear(pt);
            atoms[pt++].screenZ = 0;
          }
        }
      }
    }
    for (int i = bsOK.nextSetBit(0); i >= 0; i = bsOK.nextSetBit(i + 1)) {
      Atom atom = atoms[i];
      if (firstPass && slabbing) {
        if (g3d.isClippedZ(atom.screenZ
            - (slabByAtom ? atoms[i].screenDiameter >> 1 : 0))) {
          atom.setClickable(0);
          // note that in the case of navigation,
          // maxZ is set to Integer.MAX_VALUE.
          int r = (slabByAtom ? -1 : 1) * atom.screenDiameter / 2;
          if (atom.screenZ + r < minZ || atom.screenZ - r > maxZ
              || !g3d.isInDisplayRange(atom.screenX, atom.screenY)) {
            bsOK.clear(i);
            continue;
          }
        }
      }
      // note: above transform is required for all other renderings

      if (renderBalls && atom.screenDiameter > 0
          && (atom.getShapeVisibilityFlags() & myVisibilityFlag) != 0
          && g3d.setColix(atom.getColix())) {
        if (renderCrosshairs) {
          if (atom.screenX < minX)
            minX = atom.screenX;
          if (atom.screenX > maxX)
            maxX = atom.screenX;
          if (atom.screenY < minY)
            minY = atom.screenY;
          if (atom.screenY > maxY)
            maxY = atom.screenY;
        }
        g3d.drawAtom(atom);
      }
    }

    // this is the square and crosshairs for the navigator
    if (renderCrosshairs) {
      Point3f navOffset = new Point3f(viewer.getNavigationOffset());
      boolean antialiased = g3d.isAntialiased();
      float navDepth = viewer.getNavigationDepthPercent();
      g3d.setColix(navDepth < 0 ? Graphics3D.RED
          : navDepth > 100 ? Graphics3D.GREEN : Graphics3D.GOLD);
      int x = Math.max(Math.min(viewer.getScreenWidth(), (int) navOffset.x), 0);
      int y = Math
          .max(Math.min(viewer.getScreenHeight(), (int) navOffset.y), 0);
      int z = (int) navOffset.z + 1;
      // TODO: fix for antialiasDisplay
      int off = (antialiased ? 8 : 4);
      int h = (antialiased ? 20 : 10);
      int w = (antialiased ? 2 : 1);
      g3d.drawRect(x - off, y, z, 0, h, w);
      g3d.drawRect(x, y - off, z, 0, w, h);
      g3d.drawRect(x - off, y - off, z, 0, h, h);
      off = h;
      h = h >> 1;
      g3d.setColix(maxX < navOffset.x ? Graphics3D.YELLOW : Graphics3D.GREEN);
      g3d.drawRect(x - off, y, z, 0, h, w);
      g3d.setColix(minX > navOffset.x ? Graphics3D.YELLOW : Graphics3D.GREEN);
      g3d.drawRect(x + h, y, z, 0, h, w);
      g3d.setColix(maxY < navOffset.y ? Graphics3D.YELLOW : Graphics3D.GREEN);
      g3d.drawRect(x, y - off, z, 0, w, h);
      g3d.setColix(minY > navOffset.y ? Graphics3D.YELLOW : Graphics3D.GREEN);
      g3d.drawRect(x, y + h, z, 0, w, h);
    }
  }
}
