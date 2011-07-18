/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-12-06 20:36:47 -0800 (Mon, 06 Dec 2010) $
 * $Revision: 14757 $

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

package org.jmol.modelset;

import org.jmol.api.JmolEdge;
import org.jmol.api.JmolNode;
import org.jmol.g3d.Graphics3D;

import java.util.BitSet;

import org.jmol.util.BitSetUtil;
import org.jmol.viewer.JmolConstants;

public class Bond implements JmolEdge {

  public static class BondSet extends BitSet {

    public BondSet() {
    }

    private int[] associatedAtoms;
    
    public int[] getAssociatedAtoms() {
      return associatedAtoms;
    }

    public BondSet(BitSet bs) {
      BitSetUtil.copy(bs, this);
    }

    public BondSet(BitSet bs, int[] atoms) {
      this(bs);
      associatedAtoms = atoms;
    }

  }

  Atom atom1;
  Atom atom2;
  int order;
  short mad;
  public short getMad() {
    return mad;
  }


  short colix;
  
  public short getColix() {
    return colix;
  }
  
  int index = -1;
  public int getIndex() {
    return index;
  }
  
  public Bond(Atom atom1, Atom atom2, int order,
              short mad, short colix) {
    this.atom1 = atom1;
    this.atom2 = atom2;
    this.colix = colix;
    setOrder(order);
    setMad(mad);
  }

  public void setMad(short mad) {
    this.mad = mad;
    setShapeVisibility(mad != 0);
  }

  int shapeVisibilityFlags;
  
  public void setShapeVisibilityFlags(int shapeVisibilityFlags) {
    this.shapeVisibilityFlags = shapeVisibilityFlags;
  }

  public int getShapeVisibilityFlags() {
    return shapeVisibilityFlags;
  }

  void setShapeVisibility(boolean isVisible) {
    boolean wasVisible = ((shapeVisibilityFlags & myVisibilityFlag) != 0);
    if (wasVisible == isVisible)
      return;
    atom1.addDisplayedBond(myVisibilityFlag, isVisible);
    atom2.addDisplayedBond(myVisibilityFlag, isVisible);
    if (isVisible)
      shapeVisibilityFlags |= myVisibilityFlag;
    else
      shapeVisibilityFlags &= ~myVisibilityFlag;
  }
            
  
  final static int myVisibilityFlag = JmolConstants.getShapeVisibilityFlag(JmolConstants.SHAPE_STICKS);

  public String getIdentity() {
    return (index + 1) + " "+ getOrderNumberAsString() + " " + atom1.getInfo() + " -- "
        + atom2.getInfo() + " " + atom1.distance(atom2);
  }

  public boolean isCovalent() {
    return (order & BOND_COVALENT_MASK) != 0;
  }

  public boolean isHydrogen() {
    return isHydrogen(order);
  }

  public static boolean isHydrogen(int order) {
    return (order & BOND_HYDROGEN_MASK) != 0;
  }

  boolean isStereo() {
    return (order & BOND_STEREO_MASK) != 0;
  }

  boolean isPartial() {
    return (order & BOND_PARTIAL_MASK) != 0;
  }

  boolean isAromatic() {
    return (order & BOND_AROMATIC_MASK) != 0;
  }

  /**
   * 
   * @param pid
   */
  public void setPaletteID(byte pid) {
    // hbonds only
  }

  public float getEnergy() {
    // hbonds only
    return 0;
  }
  
  int getValence() {
    return (!isCovalent() ? 0
        : isPartial() || is(BOND_AROMATIC) ? 1
        : order & 7);
  }

  void deleteAtomReferences() {
    if (atom1 != null)
      atom1.deleteBond(this);
    if (atom2 != null)
      atom2.deleteBond(this);
    atom1 = atom2 = null;
  }

  public void setColix(short colix) {
    this.colix = colix;
  }

  public void setTranslucent(boolean isTranslucent, float translucentLevel) {
    colix = Graphics3D.getColixTranslucent(colix, isTranslucent, translucentLevel);
  }
  
  boolean isTranslucent() {
    return Graphics3D.isColixTranslucent(colix);
    //but may show up translucent anyway!
  }

  public void setOrder(int order) {
    if (atom1.getElementNumber() == 16 && atom2.getElementNumber() == 16)
      order |= BOND_SULFUR_MASK;
    if (order == BOND_AROMATIC_MASK)
      order = BOND_AROMATIC;
    this.order = order | (this.order & BOND_NEW);
  }

  public Atom getAtom1() {
    return atom1;
  }

  public Atom getAtom2() {
    return atom2;
  }

  public int getAtomIndex1() {
    return atom1.index;
  }
  
  public int getAtomIndex2() {
    return atom2.index;
  }
  
  float getRadius() {
    return mad / 2000f;
  }

  public int getOrder() {
    return order;
  }

  public int getCovalentOrder() {
    return JmolConstants.getCovalentBondOrder(order);
  }

  String getOrderName() {
    return JmolConstants.getBondOrderNameFromOrder(order);
  }

  String getOrderNumberAsString() {
    return JmolConstants.getBondOrderNumberFromOrder(order);
  }

  short getColix1() {
    return Graphics3D.getColixInherited(colix, atom1.colixAtom);
  }

  short getColix2() {
    return Graphics3D.getColixInherited(colix, atom2.colixAtom);
  }

  public Atom getOtherAtom(Atom thisAtom) {
    return (atom1 == thisAtom ? atom2 : atom2 == thisAtom ? atom1 : null);
  }
  
  ////////////////////////////////////////////////////////////////
  
  public void setIndex(int i) {
    index = i;
  }

  public boolean is(int bondType) {
    return (order & ~BOND_NEW) == bondType;
  }

  public JmolNode getOtherAtom(JmolNode thisAtom) {
    return (atom1 == thisAtom ? atom2 : atom2 == thisAtom ? atom1 : null);
  }
}
