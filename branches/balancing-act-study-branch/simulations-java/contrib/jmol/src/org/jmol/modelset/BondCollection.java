/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-10-14 12:33:20 -0500 (Sun, 14 Oct 2007) $
 * $Revision: 8408 $

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


import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Logger;

import org.jmol.viewer.JmolConstants;
import org.jmol.api.JmolEdge;
import org.jmol.api.JmolMolecule;
import org.jmol.modelset.Bond.BondSet;
import org.jmol.script.Token;

abstract public class BondCollection extends AtomCollection {

  @Override
  protected void releaseModelSet() {
    bonds = null;
    freeBonds = null;
    super.releaseModelSet();
  }

  //note: Molecules is set up to only be calculated WHEN NEEDED
  protected JmolMolecule[] molecules = new JmolMolecule[4];
  protected int moleculeCount;

  protected Bond[] bonds;
  protected int bondCount;
  
  public Bond[] getBonds() {
    return bonds;
  }

  public Bond getBondAt(int bondIndex) {
    return bonds[bondIndex];
  }

  public int getBondCount() {
    return bondCount;
  }
  
  public BondIterator getBondIterator(int bondType, BitSet bsSelected) {
    //Dipoles, Sticks
    return new BondIteratorSelected(bonds, bondCount, bondType, bsSelected, 
        viewer.getBondSelectionModeOr());
  }

  public BondIterator getBondIterator(BitSet bsSelected) {
    //Sticks
    return new BondIteratorSelected(bonds, bondCount, bsSelected);
  }
  
  public Atom getBondAtom1(int i) {
    return bonds[i].atom1;
  }

  public Atom getBondAtom2(int i) {
    return bonds[i].atom2;
  }

  public float getBondRadius(int i) {
    return bonds[i].getRadius();
  }

  public int getBondOrder(int i) {
    return bonds[i].getOrder();
  }

  public short getBondColix1(int i) {
    return bonds[i].getColix1();
  }

  public short getBondColix2(int i) {
    return bonds[i].getColix2();
  }
  
  public int getBondModelIndex(int i) {
    return bonds[i].atom1.modelIndex;
  }

  /**
   * for general use
   * 
   * @param modelIndex the model of interest or -1 for all
   * @return the actual number of connections
   */
  protected int getBondCountInModel(int modelIndex) {
    int n = 0;
    for (int i = bondCount; --i >= 0;)
      if (bonds[i].atom1.modelIndex == modelIndex)
        n++;
    return n;
  }

  public BitSet getBondsForSelectedAtoms(BitSet bsAtoms, boolean bondSelectionModeOr) {
    BitSet bs = new BitSet();
    for (int iBond = 0; iBond < bondCount; ++iBond) {
      Bond bond = bonds[iBond];
      boolean isSelected1 = bsAtoms.get(bond.atom1.index);
      boolean isSelected2 = bsAtoms.get(bond.atom2.index);
      if ((!bondSelectionModeOr & isSelected1 & isSelected2)
          || (bondSelectionModeOr & (isSelected1 | isSelected2)))
        bs.set(iBond);
    }
    return bs;
  }

  public Bond bondAtoms(Atom atom1, Atom atom2, int order, short mad, BitSet bsBonds, float energy, boolean isNew) {
    // this method used when a bond must be flagged as new
    Bond bond = getOrAddBond(atom1, atom2, order, mad, bsBonds, energy, true);
    if (isNew)
      bond.order |= JmolEdge.BOND_NEW;
    return bond;
  }

  protected final static int BOND_GROWTH_INCREMENT = 250;

  private Bond getOrAddBond(Atom atom, Atom atomOther, int order, short mad,
                            BitSet bsBonds, float energy, boolean overrideBonding) {
    int i;
    if (order == JmolEdge.BOND_ORDER_NULL || order == JmolEdge.BOND_ORDER_ANY)
      order = 1;
    if (atom.isBonded(atomOther)) {
      i = atom.getBond(atomOther).index;
      if (overrideBonding) {
        bonds[i].setOrder(order);
        bonds[i].setMad(mad);
        if (bonds[i] instanceof HBond)
          ((HBond) bonds[i]).energy = energy;
      }
    } else {
      if (bondCount == bonds.length)
        bonds = (Bond[]) ArrayUtil.setLength(bonds, bondCount
            + BOND_GROWTH_INCREMENT);
      i = setBond(bondCount++,
          bondMutually(atom, atomOther, order, mad, energy)).index;
    }
    if (bsBonds != null)
      bsBonds.set(i);
    return bonds[i];
  }

  protected Bond setBond(int index, Bond bond) {
    return bonds[bond.index = index] = bond;
  }

  protected Bond bondMutually(Atom atom, Atom atomOther, int order, short mad, float energy) {
    Bond bond;
    if (Bond.isHydrogen(order)) {
      bond = new HBond(atom, atomOther, order, mad, (short) 0, energy);
    } else {
      bond = new Bond(atom, atomOther, order, mad, (short) 0);
    }
    addBondToAtom(atom, bond);
    addBondToAtom(atomOther, bond);
    return bond;
  }

  private void addBondToAtom(Atom atom, Bond bond) {
    if (atom.bonds == null) {
      atom.bonds = new Bond[1];
      atom.bonds[0] = bond;
    } else {
      atom.bonds = addToBonds(bond, atom.bonds);
    }
  }

  protected final static int MAX_BONDS_LENGTH_TO_CACHE = 5;
  protected final static int MAX_NUM_TO_CACHE = 200;
  protected int[] numCached = new int[MAX_BONDS_LENGTH_TO_CACHE];
  protected Bond[][][] freeBonds = new Bond[MAX_BONDS_LENGTH_TO_CACHE][][];
  {
    for (int i = MAX_BONDS_LENGTH_TO_CACHE; --i > 0;)
      // .GT. 0
      freeBonds[i] = new Bond[MAX_NUM_TO_CACHE][];
  }

  private Bond[] addToBonds(Bond newBond, Bond[] oldBonds) {
    Bond[] newBonds;
    if (oldBonds == null) {
      if (numCached[1] > 0)
        newBonds = freeBonds[1][--numCached[1]];
      else
        newBonds = new Bond[1];
      newBonds[0] = newBond;
    } else {
      int oldLength = oldBonds.length;
      int newLength = oldLength + 1;
      if (newLength < MAX_BONDS_LENGTH_TO_CACHE && numCached[newLength] > 0)
        newBonds = freeBonds[newLength][--numCached[newLength]];
      else
        newBonds = new Bond[newLength];
      newBonds[oldLength] = newBond;
      for (int i = oldLength; --i >= 0;)
        newBonds[i] = oldBonds[i];
      if (oldLength < MAX_BONDS_LENGTH_TO_CACHE
          && numCached[oldLength] < MAX_NUM_TO_CACHE)
        freeBonds[oldLength][numCached[oldLength]++] = oldBonds;
    }
    return newBonds;
  }

  ////// bonding methods //////
  
  protected BitSet bsHBondsRasmol;

  void getRasmolHydrogenBonds(Model m, BitSet bsA, BitSet bsB,
                              List<Bond> vHBonds, boolean nucleicOnly,
                              int nMax, boolean dsspIgnoreHydrogens) {
    boolean doAdd = (vHBonds == null);
    Polymer bp, bp1;
    if (doAdd)
      vHBonds = new ArrayList<Bond>();
    if (nMax < 0)
      nMax = Integer.MAX_VALUE;
    boolean asDSSP = (bsB == null);
    if (asDSSP && m.bioPolymerCount > 0) {
      m.bioPolymers[0].calculateStructures(m.bioPolymers, m.bioPolymerCount,
          vHBonds, false, dsspIgnoreHydrogens, false);
    } else {
      for (int i = m.bioPolymerCount; --i >= 0;) {
        bp = m.bioPolymers[i];
        int type = bp.getType();
        if ((nucleicOnly || type != Polymer.TYPE_AMINO)
            && type != Polymer.TYPE_NUCLEIC)
          continue;
        boolean isRNA = bp.isRna();
        boolean isAmino = (type == Polymer.TYPE_AMINO);
        if (isAmino)
          bp.calcRasmolHydrogenBonds(null, bsA, bsB, vHBonds, nMax, null, true,
              false);
        for (int j = m.bioPolymerCount; --j >= 0;) {
          if ((bp1 = m.bioPolymers[j]) != null && (isRNA || i != j)
              && type == bp1.getType()) {
            bp1.calcRasmolHydrogenBonds(bp, bsA, bsB, vHBonds, nMax, null,
                true, false);
          }
        }
      }
    }
    if (vHBonds.size() == 0 || !doAdd)
      return;
    m.hasRasmolHBonds = true;
    for (int i = 0; i < vHBonds.size(); i++) {
      HBond bond = (HBond) vHBonds.get(i);
      Atom atom1 = bond.atom1;
      Atom atom2 = bond.atom2;
      if (atom1.isBonded(atom2))
        continue;
      int n = addHBond(atom1, atom2, bond.order, bond.getEnergy());
      if (bsHBondsRasmol != null)
        bsHBondsRasmol.set(n);
    }
  }
  
  protected int addHBond(Atom atom1, Atom atom2, int order, float energy) {
    // from autoHbond
    if (bondCount == bonds.length)
      bonds = (Bond[]) ArrayUtil.setLength(bonds, bondCount
          + BOND_GROWTH_INCREMENT);
    return setBond(bondCount++, bondMutually(atom1, atom2, order, (short) 1, energy)).index;
  }

  protected static short getBondOrder(float bondingRadiusA,
                             float bondingRadiusB, float distance2,
                             float minBondDistance2, float bondTolerance) {
    if (bondingRadiusA == 0 || bondingRadiusB == 0 || distance2 < minBondDistance2)
      return 0;
    float maxAcceptable = bondingRadiusA + bondingRadiusB + bondTolerance;
    float maxAcceptable2 = maxAcceptable * maxAcceptable;
    return (distance2 > maxAcceptable2 ? (short) 0 : (short) 1);
  }

  private boolean haveWarned = false;

  protected boolean checkValencesAndBond(Atom atomA, Atom atomB, int order, short mad,
                            BitSet bsBonds) {
    if (atomA.getCurrentBondCount() > JmolConstants.MAXIMUM_AUTO_BOND_COUNT
        || atomB.getCurrentBondCount() > JmolConstants.MAXIMUM_AUTO_BOND_COUNT) {
      if (!haveWarned)
        Logger.warn("maximum auto bond count reached");
      haveWarned = true;
      return false;
    }
    int formalChargeA = atomA.getFormalCharge();
    if (formalChargeA != 0) {
      int formalChargeB = atomB.getFormalCharge();
      if ((formalChargeA < 0 && formalChargeB < 0)
          || (formalChargeA > 0 && formalChargeB > 0))
        return false;
    }
    if (atomA.alternateLocationID != atomB.alternateLocationID
        && atomA.alternateLocationID != '\0' && atomB.alternateLocationID != '\0')
      return false;
    getOrAddBond(atomA, atomB, order, mad, bsBonds, 0, false);
    return true;
  }

  protected void deleteAllBonds() {
    viewer.setShapeProperty(JmolConstants.SHAPE_STICKS, "reset", null);
    for (int i = bondCount; --i >= 0;) {
      bonds[i].deleteAtomReferences();
      bonds[i] = null;
    }
    bondCount = 0;
  }
  

  protected short defaultCovalentMad;

  /**
   * When creating a new bond, determine bond diameter from order
   * 
   * @param order
   * @return if hydrogen bond, default to 1; otherwise 0 (general default)
   */
  protected short getDefaultMadFromOrder(int order) {
    return (short) (Bond.isHydrogen(order) ? 1
        : (order & JmolEdge.BOND_STRUT) != 0 ? (int) (viewer
            .getStrutDefaultRadius() * 2000) : defaultCovalentMad);
  }

  protected int[] deleteConnections(float minDistance, float maxDistance,
                                    int order, BitSet bsA, BitSet bsB,
                                    boolean isBonds, boolean matchNull,
                                    float minDistanceSquared,
                                    float maxDistanceSquared) {
    boolean minDistanceIsFractionRadius = (minDistance < 0);
    boolean maxDistanceIsFractionRadius = (maxDistance < 0);
    float dAB = 0;
    float dABcalc = 0;
    if (minDistanceIsFractionRadius)
      minDistance = -minDistance;
    if (maxDistanceIsFractionRadius)
      maxDistance = -maxDistance;
    BitSet bsDelete = new BitSet();
    int nDeleted = 0;
    int newOrder = order |= JmolEdge.BOND_NEW;
    if (!matchNull && Bond.isHydrogen(order))
      order = JmolEdge.BOND_HYDROGEN_MASK;
    BitSet bsBonds;
    if (isBonds) {
      bsBonds = bsA;
    } else {
      bsBonds = new BitSet();
      for (int i = bsA.nextSetBit(0); i >= 0; i = bsA.nextSetBit(i + 1)) {
        Atom a = atoms[i];
        if (a.bonds != null)
          for (int j = a.bonds.length; --j >= 0; )
            if (bsB.get(a.getBondedAtomIndex(j)))
              bsBonds.set(a.bonds[j].index);
      }
    }
    for (int i = bsBonds.nextSetBit(0); i < bondCount && i >= 0; i = bsBonds.nextSetBit(i + 1)) {
      Bond bond = bonds[i];
      Atom atom1 = bond.atom1;
      Atom atom2 = bond.atom2;
        float distanceSquared = atom1.distanceSquared(atom2);
        if (minDistanceIsFractionRadius || maxDistanceIsFractionRadius) {
          dAB = atom1.distance(atom2);
          dABcalc = atom1.getBondingRadiusFloat()
              + atom2.getBondingRadiusFloat();
        }
        if ((minDistanceIsFractionRadius ? dAB < dABcalc * minDistance
            : distanceSquared < minDistanceSquared)
            || (maxDistanceIsFractionRadius ? dAB > dABcalc * maxDistance
                : distanceSquared > maxDistanceSquared))
          continue;
        if (matchNull
            || newOrder == (bond.order & ~JmolEdge.BOND_SULFUR_MASK | JmolEdge.BOND_NEW)
            || (order & bond.order & JmolEdge.BOND_HYDROGEN_MASK) != 0) {
          bsDelete.set(i);
          nDeleted++;
        }
    }
    if (nDeleted > 0)
      deleteBonds(bsDelete, false);
    return new int[] { 0, nDeleted };
  }

  public void deleteBonds(BitSet bsBond, boolean isFullModel) {
    int iDst = bsBond.nextSetBit(0);
    if (iDst < 0)
      return;
    molecules = null;
    moleculeCount = 0;
    int modelIndexLast = -1;
    int n = bsBond.cardinality();
    for (int iSrc = iDst; iSrc < bondCount; ++iSrc) {
      Bond bond = bonds[iSrc];
      if (n > 0 && bsBond.get(iSrc)) {
        n--;
        if (!isFullModel) {
          int modelIndex = bond.atom1.modelIndex;
          if (modelIndex != modelIndexLast)
            ((ModelCollection) this).models[modelIndexLast = modelIndex]
                .resetBoundCount();
        }
        bond.deleteAtomReferences();
      } else {
        setBond(iDst++, bond);
      }
    }
    for (int i = bondCount; --i >= iDst;)
      bonds[i] = null;
    bondCount = iDst;
    BitSet[] sets = (BitSet[]) viewer.getShapeProperty(
        JmolConstants.SHAPE_STICKS, "sets");
    for (int i = 0; i < sets.length; i++)
      BitSetUtil.deleteBits(sets[i], bsBond);
    BitSetUtil.deleteBits(bsHBondsRasmol, bsBond);
    BitSetUtil.deleteBits(bsAromatic, bsBond);
  }


  /*
   * aromatic single/double bond assignment 
   * by Bob Hanson, hansonr@stolaf.edu, Oct. 2007
   * Jmol 11.3.29.
   * 
   * This algorithm assigns alternating single/double bonds to all 
   * sets of bonds of type AROMATIC in a system. Any bonds already
   * assigned AROMATICSINGLE or AROMATICDOUBLE by the user are preserved.
   * 
   * In this way the user can assign ONE bond, and Jmol will take it from
   * there.
   * 
   * The algorithm is highly recursive.
   * 
   * We track two bond bitsets: bsAromaticSingle and bsAromaticDouble.
   *  
   * Loop through all aromatic bonds. 
   *   If unassigned, assignAromaticDouble(Bond bond).
   *   If unsuccessful, assignAromaticSingle(Bond bond).
   * 
   * assignAromaticDouble(Bond bond):
   * 
   *   Each of the two atoms must have exactly one double bond.
   *   
   *   bsAromaticDouble.set(thisBond)
   *   
   *   For each aromatic bond connected to each atom that is not
   *   already assigned AROMATICSINGLE or AROMATICDOUBLE:
   *   
   *     assignAromaticSingle(Bond bond)
   *     
   *   If unsuccessful, bsAromaticDouble.clear(thisBond) and 
   *   return FALSE, otherwise return TRUE.
   * 
   * assignAromaticSingle(Bond bond):
   * 
   *   Each of the two atoms must have exactly one double bond.
   *   
   *   bsAromaticSingle.set(thisBond)
   *   
   *   For each aromatic bond connected to this atom that is not
   *   already assigned:
   *   
   *     for one: assignAromaticDouble(Bond bond) 
   *     the rest: assignAromaticSingle(Bond bond)
   *     
   *   If two AROMATICDOUBLE bonds to the same atom are found
   *   or unsuccessful in assigning AROMATICDOUBLE or AROMATICSINGLE, 
   *   bsAromaticSingle.clear(thisBond) and 
   *   return FALSE, otherwise return TRUE.
   *   
   * The process continues until all bonds are processed. It is quite
   * possible that the first assignment will fail either because somewhere
   * down the line the user has assigned an incompatible AROMATICDOUBLE or
   * AROMATICSINGLE bond. 
   * 
   * This is no problem though, because the assignment is self-correcting, 
   * and in the second pass the process will be opposite, and success will
   * be achieved.
   * 
   * It is possible that no correct assignment is possible because the structure
   * has no valid closed-shell Lewis structure. In that case, AROMATICSINGLE 
   * bonds will be assigned to problematic areas.  
   * 
   * Bob Hanson -- 10/2007
   * 
   */

  private BitSet bsAromaticSingle;
  private BitSet bsAromaticDouble;
  protected BitSet bsAromatic = new BitSet();

  public void resetAromatic() {
    for (int i = bondCount; --i >= 0;) {
      Bond bond = bonds[i];
      if (bond.isAromatic())
        bond.setOrder(JmolEdge.BOND_AROMATIC);
    }
  }
  
  public void assignAromaticBonds() {
    assignAromaticBonds(true, null);
  }

  /**
   * algorithm discussed above.
   * 
   * @param isUserCalculation   if set, don't reset the base aromatic bitset
   *                            and do report changes to STICKS as though this
   *                            were a bondOrder command.
   * @param bsBonds  passed to us by autoBond routine
   */
  protected void assignAromaticBonds(boolean isUserCalculation, BitSet bsBonds) {
    // bsAromatic tracks what was originally in the file, but
    // individual bonds are cleared if the connect command has been used.
    // in this way, users can override the file designations.
    if (!isUserCalculation)
      bsAromatic = new BitSet();

    //set up the two temporary bitsets and reset bonds.

    bsAromaticSingle = new BitSet();
    bsAromaticDouble = new BitSet();
    boolean isAll = (bsBonds == null);
    int i0 = (isAll ? bondCount - 1 : bsBonds.nextSetBit(0));
    for (int i = i0; i >= 0; i = (isAll ? i - 1 : bsBonds.nextSetBit(i + 1))) {
        Bond bond = bonds[i];
        if (bsAromatic.get(i))
          bond.setOrder(JmolEdge.BOND_AROMATIC);
        switch (bond.order & ~JmolEdge.BOND_NEW) {
        case JmolEdge.BOND_AROMATIC:
          bsAromatic.set(i);
          break;
        case JmolEdge.BOND_AROMATIC_SINGLE:
          bsAromaticSingle.set(i);
          break;
        case JmolEdge.BOND_AROMATIC_DOUBLE:
          bsAromaticDouble.set(i);
          break;
        }
      }
    // main recursive loop
    Bond bond;
    isAll = (bsBonds == null);
    i0 = (isAll ? bondCount - 1 : bsBonds.nextSetBit(0));
    for (int i = i0; i >= 0; i = (isAll ? i - 1 : bsBonds.nextSetBit(i + 1))) {
        bond = bonds[i];
        if (!bond.is(JmolEdge.BOND_AROMATIC)
            || bsAromaticDouble.get(i) || bsAromaticSingle.get(i))
          continue;
        if (!assignAromaticDouble(bond))
          assignAromaticSingle(bond);
      }
    // all done: do the actual assignments and clear arrays.
    for (int i = i0; i >= 0; i = (isAll ? i - 1 : bsBonds.nextSetBit(i + 1))) {
        bond = bonds[i];
        if (bsAromaticDouble.get(i)) {
          if (!bond.is(JmolEdge.BOND_AROMATIC_DOUBLE)) {
            bsAromatic.set(i);
            bond.setOrder(JmolEdge.BOND_AROMATIC_DOUBLE);
          }
        } else if (bsAromaticSingle.get(i) || bond.isAromatic()) {
          if (!bond.is(JmolEdge.BOND_AROMATIC_SINGLE)) {
            bsAromatic.set(i);
            bond.setOrder(JmolEdge.BOND_AROMATIC_SINGLE);
          }
        }
      }

    assignAromaticNandO(bsBonds);

    bsAromaticSingle = null;
    bsAromaticDouble = null;    
  }

  /**
   * try to assign AROMATICDOUBLE to this bond. Each atom needs to be
   * have all single bonds except for this one.  
   * 
   * @param bond
   * @return      true if successful; false otherwise
   */
  private boolean assignAromaticDouble(Bond bond) {
    int bondIndex = bond.index;
    if (bsAromaticSingle.get(bondIndex))
      return false;
    if (bsAromaticDouble.get(bondIndex))
      return true;
    bsAromaticDouble.set(bondIndex);
    if (!assignAromaticSingle(bond.atom1, bondIndex)
        || !assignAromaticSingle(bond.atom2, bondIndex)) {
      bsAromaticDouble.clear(bondIndex);
      return false;
    }
    return true;
  }
  
  /**
   * try to assign AROMATICSINGLE to this bond. Each atom needs to be
   * able to have one aromatic double bond attached.  
   * 
   * @param bond
   * @return      true if successful; false otherwise
   */
  private boolean assignAromaticSingle(Bond bond) {
    int bondIndex = bond.index;
    if (bsAromaticDouble.get(bondIndex))
      return false;
    if (bsAromaticSingle.get(bondIndex))
      return true;
    bsAromaticSingle.set(bondIndex);
    if (!assignAromaticDouble(bond.atom1) || !assignAromaticDouble(bond.atom2)) {
      bsAromaticSingle.clear(bondIndex);
      return false;
    }
    return true;
  }

  /**
   * N atoms with 3 bonds cannot also have a double bond; 
   * other atoms needs all single bonds, 
   * because the bond leading up to it is double.
   * 
   * @param atom
   * @param notBondIndex  that index of the bond leading to this atom --- to be ignored
   * @return      true if successful, false if not
   */
  private boolean assignAromaticSingle(Atom atom, int notBondIndex) {
    Bond[] bonds = atom.bonds;
    if (bonds == null || assignAromaticSingleHetero(atom))
      return false;
    for (int i = bonds.length; --i >= 0;) {
      Bond bond = bonds[i];
      int bondIndex = bond.index;
      if (bondIndex == notBondIndex || !bond.isAromatic()
          || bsAromaticSingle.get(bondIndex))
        continue;
      if (bsAromaticDouble.get(bondIndex) || !assignAromaticSingle(bond)) {
        return false;
      }
    }
    return true;
  }
 
  /**
   * N atoms with 3 bonds cannot also have a double bond; 
   * other atoms need one and only one double bond;
   * the rest must be single bonds.
   * 
   * @param atom
   * @return      true if successful, false if not
   */
  private boolean assignAromaticDouble(Atom atom) {
    Bond[] bonds = atom.bonds;
    if (bonds == null)
      return false;
    boolean haveDouble = assignAromaticSingleHetero(atom);
    int lastBond = -1;
    for (int i = bonds.length; --i >= 0;) {
      if (bsAromaticDouble.get(bonds[i].index))
        haveDouble = true;
      if (bonds[i].isAromatic())
        lastBond = i;
    }
    for (int i = bonds.length; --i >= 0;) {
      Bond bond = bonds[i];
      int bondIndex = bond.index;
      if (!bond.isAromatic() || bsAromaticDouble.get(bondIndex)
          || bsAromaticSingle.get(bondIndex))
        continue;
      if (!haveDouble && assignAromaticDouble(bond))
        haveDouble = true;
      else if ((haveDouble || i < lastBond) && !assignAromaticSingle(bond)) {
        return false;
      }
    }
    return haveDouble;
  } 
  
  private boolean assignAromaticSingleHetero(Atom atom) {
    // only C N O S may be a problematic:
    int n = atom.getElementNumber();
    switch (n) {
    case 6: // C
    case 7: // N
    case 8: // O
    case 16: // S
      break;
    default:
      return true;
    }
    int nAtoms = atom.getValence();
    switch (n) {
    case 6: // C
      return (nAtoms == 4);
    case 7: // N
    case 8: // O
      return (nAtoms == 10 - n && atom.getFormalCharge() < 1);
    case 16: // S
      return (nAtoms == 18 - n && atom.getFormalCharge() < 1);
    }
    return false;
  }
  
  private void assignAromaticNandO(BitSet bsSelected) {
    Bond bond;
    boolean isAll = (bsSelected == null);
    int i0 = (isAll ? bondCount - 1 : bsSelected.nextSetBit(0));
    for (int i = i0; i >= 0; i = (isAll ? i - 1 : bsSelected.nextSetBit(i + 1))) {
        bond = bonds[i];
        if (!bond.is(JmolEdge.BOND_AROMATIC_SINGLE))
          continue;
        Atom atom1;
        Atom atom2 = bond.atom2;
        int n1;
        int n2 = atom2.getElementNumber();
        if (n2 == 7 || n2 == 8) {
          n1 = n2;
          atom1 = atom2;
          atom2 = bond.atom1;
          n2 = atom2.getElementNumber();
        } else {
          atom1 = bond.atom1;
          n1 = atom1.getElementNumber();
        }
        if (n1 != 7 && n1 != 8)
          continue;
        int valence = atom1.getValence();
        if (valence < 0)
          continue; // deleted
        int bondorder = atom1.getCovalentBondCount();
        int charge = atom1.getFormalCharge();
        switch (n1) {
        case 7:
          //trivalent nonpositive N with lone pair in p orbital
          //next to trivalent C --> N=C
          if (valence == 3 && bondorder == 3 && charge < 1 && n2 == 6
              && atom2.getValence() == 3)
            bond.setOrder(JmolEdge.BOND_AROMATIC_DOUBLE);
          break;
        case 8:
          //monovalent nonnegative O next to P or S
          if (valence == 1 && charge == 0 && (n2 == 14 || n2 == 16))
            bond.setOrder(JmolEdge.BOND_AROMATIC_DOUBLE);
          break;
        }
      }
  }

  @Override
  protected BitSet getAtomBits(int tokType, Object specInfo) {
    BitSet bs;
    switch (tokType) {
    case Token.isaromatic:
      bs = new BitSet();
      for (int i = bondCount; --i >= 0;)
        if (bonds[i].isAromatic()) {
          bs.set(bonds[i].atom1.index);
          bs.set(bonds[i].atom2.index);
        }
      return bs;
    case Token.bonds:
      bs = new BitSet();
      BitSet bsBonds = (BitSet) specInfo;
      for (int i = bsBonds.nextSetBit(0); i >= 0; i = bsBonds.nextSetBit(i + 1)) {
        bs.set(bonds[i].atom1.index);
        bs.set(bonds[i].atom2.index);
      }
      return bs;
    }
    return super.getAtomBits(tokType, specInfo);
  }
 
  public BitSet setBondOrder(int bondIndex, char type) {
    int bondOrder = type - '0';
    Bond bond = bonds[bondIndex];
    switch (type) {
    case '0':
    case '1':
    case '2':
    case '3':
      break;
    case 'p':
    case 'm':
      bondOrder = JmolConstants.getBondOrderNumberFromOrder(
          bond.getCovalentOrder()).charAt(0)
          - '0' + (type == 'p' ? 1 : -1);
      if (bondOrder > 3)
        bondOrder = 1;
      else if (bondOrder < 0)
        bondOrder = 3;
      break;
    default:
      return null;
    }
    BitSet bsAtoms = new BitSet();
    try {
      if (bondOrder == 0) {
        BitSet bs = new BitSet();
        bs.set(bond.index);
        bsAtoms.set(bond.getAtomIndex1());
        bsAtoms.set(bond.getAtomIndex2());
        deleteBonds(bs, false);
        return bsAtoms;
      }
      bond.setOrder(bondOrder | JmolEdge.BOND_NEW);
      removeUnnecessaryBonds(bond.atom1, false);
      removeUnnecessaryBonds(bond.atom2, false);
      bsAtoms.set(bond.getAtomIndex1());
      bsAtoms.set(bond.getAtomIndex2());
    } catch (Exception e) {
      Logger.error("Exception in seBondOrder: " + e.getMessage());
    }
    return bsAtoms;
  }

  protected void removeUnnecessaryBonds(Atom atom, boolean deleteAtom) {
    BitSet bs = new BitSet();
    BitSet bsBonds = new BitSet();
    Bond[] bonds = atom.bonds;
    if (bonds == null)
      return;
    for (int i = 0; i < bonds.length; i++)
      if (bonds[i].isCovalent()) {
        Atom atom2 = bonds[i].getOtherAtom(atom);
        if (atom2.getElementNumber() == 1)
          bs.set(bonds[i].getOtherAtom(atom).index);
      } else {
        bsBonds.set(bonds[i].index);
      }
    if (bsBonds.nextSetBit(0) >= 0)
      deleteBonds(bsBonds, false);
    if (deleteAtom)
      bs.set(atom.index);
    if (bs.nextSetBit(0) >= 0)
      viewer.deleteAtoms(bs, false);
  }
  
  protected boolean haveHiddenBonds;
  
  public void displayBonds(BondSet bs, boolean isDisplay) {
    if (!isDisplay)
      haveHiddenBonds = true;
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1))
      if (i < bondCount && bonds[i].mad != 0)
        bonds[i].setShapeVisibility(isDisplay);
  }

}

