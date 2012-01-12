/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-08-22 03:13:40 -0500 (Tue, 22 Aug 2006) $
 * $Revision: 5412 $

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
import java.util.Hashtable;
import java.util.Map;

import org.jmol.atomdata.RadiusData;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.viewer.JmolConstants;

public abstract class AtomShape extends Shape {

  // Balls, Dots, Ellipsoids, Halos, Labels, Polyhedra, Stars, Vectors

  public short[] mads;
  public short[] colixes;
  public byte[] paletteIDs;
  protected BitSet bsSizeSet;
  protected BitSet bsColixSet;
  public int atomCount;
  public Atom[] atoms;
  public boolean isActive;

  @Override
  protected void initModelSet() {
    atoms = modelSet.atoms;
    atomCount = modelSet.getAtomCount();
    // in case this is due to "load append"
    if (mads != null)
      mads = ArrayUtil.setLength(mads, atomCount);
    if (colixes != null)
      colixes = ArrayUtil.setLength(colixes, atomCount);
    if (paletteIDs != null)
      paletteIDs = ArrayUtil.setLength(paletteIDs, atomCount);
  }

  @Override
  public int getSize(int atomIndex) {
    return (mads == null ? 0 : mads[atomIndex]);
  }
  
  @Override
  protected void setSize(int size, BitSet bsSelected) {
    if (size == 0)
      setSize(null, bsSelected);
    else
      setSize(new RadiusData(size, RadiusData.TYPE_SCREEN, 0), bsSelected);
  }

  @Override
  protected void setSize(RadiusData rd, BitSet bsSelected) {
    // Halos Stars Vectors only
    if (atoms == null)  // vector values are ignored if there are none for a model 
      return;
    isActive = true;
    if (bsSizeSet == null)
      bsSizeSet = new BitSet();
    boolean isVisible = (rd != null && rd.value != 0);
    boolean isAll = (bsSelected == null);
    int i0 = (isAll ? atomCount - 1 : bsSelected.nextSetBit(0));
    if (mads == null && i0 >= 0)
      mads = new short[atomCount];
    for (int i = i0; i >= 0; i = (isAll ? i - 1 : bsSelected.nextSetBit(i + 1))) {
      Atom atom = atoms[i];
      mads[i] = atom.calculateMad(viewer, rd);
//      System.out.println("atomSHape " + atom + " mad=" + mads[i]);
      bsSizeSet.set(i, isVisible);
      atom.setShapeVisibility(myVisibilityFlag, isVisible);
    }
  }

  @Override
  public void setProperty(String propertyName, Object value, BitSet bs) {
    if ("color" == propertyName) {
      isActive = true;
      short colix = Graphics3D.getColix(value);
      byte pid = JmolConstants.pidOf(value);
      if (bsColixSet == null)
        bsColixSet = new BitSet();
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
        setColixAndPalette(colix, pid, i);
      return;
    }
    if ("translucency" == propertyName) {
      isActive = true;
      boolean isTranslucent = (value.equals("translucent"));
      if (bsColixSet == null)
        bsColixSet = new BitSet();
      for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
        if (colixes == null) {
          colixes = new short[atomCount];
          paletteIDs = new byte[atomCount];
        }
        colixes[i] = Graphics3D.getColixTranslucent(colixes[i], isTranslucent,
            translucentLevel);
        if (isTranslucent)
          bsColixSet.set(i);
      }
      return;
    }
    if (propertyName == "deleteModelAtoms") {
      atoms = (Atom[]) ((Object[]) value)[1];
      int[] info = (int[]) ((Object[]) value)[2];
      atomCount = modelSet.getAtomCount();
      int firstAtomDeleted = info[1];
      int nAtomsDeleted = info[2];
      mads = (short[]) ArrayUtil.deleteElements(mads, firstAtomDeleted,
          nAtomsDeleted);
      colixes = (short[]) ArrayUtil.deleteElements(colixes, firstAtomDeleted,
          nAtomsDeleted);
      paletteIDs = (byte[]) ArrayUtil.deleteElements(paletteIDs,
          firstAtomDeleted, nAtomsDeleted);
      BitSetUtil.deleteBits(bsSizeSet, bs);
      BitSetUtil.deleteBits(bsColixSet, bs);
      return;
    }
    super.setProperty(propertyName, value, bs);
  }

  protected void setColixAndPalette(short colix, byte paletteID, int atomIndex) {
    if (colixes == null || atomIndex >= colixes.length) {
      if (colix == Graphics3D.INHERIT_ALL)
        return;
      colixes = ArrayUtil.ensureLength(colixes, atomIndex + 1);
      paletteIDs = ArrayUtil.ensureLength(paletteIDs, atomIndex + 1);
    }
    if (bsColixSet == null)
      bsColixSet = new BitSet();
    colixes[atomIndex] = colix = setColix(colix, paletteID, atomIndex);
    bsColixSet.set(atomIndex, colix != Graphics3D.INHERIT_ALL);
    paletteIDs[atomIndex] = paletteID;
  }

  @Override
  public void setModelClickability() {
    if (!isActive)
      return;
    for (int i = atomCount; --i >= 0;) {
      Atom atom = atoms[i];
      if ((atom.getShapeVisibilityFlags() & myVisibilityFlag) == 0
          || modelSet.isAtomHidden(i))
        continue;
      atom.setClickable(myVisibilityFlag);
    }
  }

  @Override
  public String getShapeState() {
    if (!isActive)
      return "";
    Map<String, BitSet> temp = new Hashtable<String, BitSet>();
    Map<String, BitSet> temp2 = new Hashtable<String, BitSet>();
    String type = JmolConstants.shapeClassBases[shapeID];
    if (bsSizeSet != null)
      for (int i = bsSizeSet.nextSetBit(0); i >= 0; i = bsSizeSet.nextSetBit(i + 1))
          setStateInfo(temp, i, type + (mads[i] < 0 ? " on" : " " + mads[i] / 2000f));
    if (bsColixSet != null)
      for (int i = bsColixSet.nextSetBit(0); i >= 0; i = bsColixSet.nextSetBit(i + 1))
          setStateInfo(temp2, i, getColorCommand(type, paletteIDs[i], colixes[i]));
    return getShapeCommands(temp, temp2);
  }

}
