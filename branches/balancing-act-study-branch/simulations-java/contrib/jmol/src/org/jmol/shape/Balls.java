/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-10-02 16:11:05 -0700 (Sat, 02 Oct 2010) $
 * $Revision: 14386 $

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
 *  Lesser General License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.shape;

import java.util.BitSet;
import java.util.Map;

import org.jmol.atomdata.RadiusData;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.viewer.JmolConstants;

import java.util.Hashtable;

public class Balls extends AtomShape {
  
  @Override
  protected void setSize(RadiusData rd, BitSet bsSelected) {
    isActive = true;
    if (bsSizeSet == null)
      bsSizeSet = new BitSet();
    int bsLength = Math.min(atoms.length, bsSelected.length());
    for (int i = bsSelected.nextSetBit(0); i >= 0 && i < bsLength; i = bsSelected
        .nextSetBit(i + 1)) {
      Atom atom = atoms[i];
      atom.setMadAtom(viewer, rd);
      bsSizeSet.set(i);
    }
  }

  @Override
  public void setProperty(String propertyName, Object value, BitSet bs) {
    if ("color" == propertyName) {
      short colix = Graphics3D.getColix(value);
      if (colix == Graphics3D.INHERIT_ALL)
        colix = Graphics3D.USE_PALETTE;
      if (bsColixSet == null)
        bsColixSet = new BitSet();
      byte pid = JmolConstants.pidOf(value);
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
        Atom atom = atoms[i];
        atom.setColixAtom(setColix(colix, pid, atom));
        bsColixSet.set(i, colix != Graphics3D.USE_PALETTE
            || pid != JmolConstants.PALETTE_NONE);
        atom.setPaletteID(pid);
      }
      return;
    }
    if ("colorValues" == propertyName) {
      int[] values = (int[]) value;
      if (values.length == 0)
        return;
      if (bsColixSet == null)
        bsColixSet = new BitSet();
      int n = 0;
      Integer color = null;
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
        if (n >= values.length)
          return;
        color = Integer.valueOf(values[n++]);
        short colix = Graphics3D.getColix(color);
        if (colix == Graphics3D.INHERIT_ALL)
          colix = Graphics3D.USE_PALETTE;
        byte pid = JmolConstants.pidOf(color);
        Atom atom = atoms[i];
        atom.setColixAtom(setColix(colix, pid, atom));
        bsColixSet.set(i, colix != Graphics3D.USE_PALETTE
            || pid != JmolConstants.PALETTE_NONE);
        atom.setPaletteID(pid);
      }
      return;
    }
    if ("translucency" == propertyName) {
      boolean isTranslucent = (((String) value).equals("translucent"));
      if (bsColixSet == null)
        bsColixSet = new BitSet();
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
        atoms[i].setTranslucent(isTranslucent, translucentLevel);
        if (isTranslucent)
          bsColixSet.set(i);
      }
      return;
    }
    super.setProperty(propertyName, value, bs);
  }

 @Override
public void setModelClickability() {
   BitSet bs = viewer.getDeletedAtoms();
   if (bs == null)
     bs = new BitSet();
   for (int i = atomCount; --i >= 0;) {
     Atom atom = atoms[i];
     atom.setClickable(0);
     if (bs.get(i) || (atom.getShapeVisibilityFlags() & myVisibilityFlag) == 0
         || modelSet.isAtomHidden(i))
       continue;
     atom.setClickable(myVisibilityFlag);
   }
 }
  
 @Override
public void setVisibilityFlags(BitSet bs) {
    boolean showHydrogens = viewer.getShowHydrogens();
    BitSet bsDeleted = viewer.getDeletedAtoms();
    for (int i = atomCount; --i >= 0; ) {
      Atom atom = atoms[i];
      int flag = atom.getShapeVisibilityFlags();
      flag &= (~JmolConstants.ATOM_IN_FRAME & ~myVisibilityFlag);
      atom.setShapeVisibilityFlags(flag);
      if (bsDeleted != null && bsDeleted.get(i) 
          || !showHydrogens && atom.getElementNumber() == 1)
        continue;
      int modelIndex = atom.getModelIndex();
      if (bs.get(modelIndex)) { 
        atom.setShapeVisibility(JmolConstants.ATOM_IN_FRAME, true);
        if (atom.madAtom != 0 &&  !modelSet.isAtomHidden(i))
          atom.setShapeVisibility(myVisibilityFlag, true);
      }
    }
  }

 @Override
public String getShapeState() {
    Map<String, BitSet> temp = new Hashtable<String, BitSet>();
    float r = 0;
    for (int i = 0; i < atomCount; i++) {
      if (bsSizeSet != null && bsSizeSet.get(i)) {
        if ((r = atoms[i].madAtom) < 0)
          setStateInfo(temp, i, "Spacefill on");
        else
          setStateInfo(temp, i, "Spacefill " + (r / 2000f));
      }
      if (bsColixSet != null && bsColixSet.get(i)) {
        byte pid = atoms[i].getPaletteID();
        if (pid != JmolConstants.PALETTE_CPK || atoms[i].isTranslucent())
          setStateInfo(temp, i, getColorCommand("atoms", pid, atoms[i].getColix()));
      }
    }
    return getShapeCommands(temp, null);
  }
  
  /*
  boolean checkObjectHovered(int x, int y) {
    //just for debugging
    if (!viewer.getNavigationMode())
      return false;
    viewer.hoverOn(x, y, x + " " + y);
    return true;
  }
  */

}
