/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-05-18 08:19:45 -0500 (Fri, 18 May 2007) $
 * $Revision: 7742 $

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

package org.jmol.api;


import org.jmol.util.Elements;

import java.util.BitSet;


/**
 * an independent class utilizing only org.jmol.api.JmolNode, not org.jmol.modelset.Atom
 * for use in finding molecules in models and SMILES strings
 * 
 */
public class JmolMolecule {
  
  public JmolMolecule() {}
  
  public JmolNode[] nodes;
  
  public int moleculeIndex;
  public int modelIndex;
  public int indexInModel;
  public int firstAtomIndex;
  public int atomCount;
  public int nElements;
  public int[] elementCounts = new int[Elements.elementNumberMax];
  public int[] altElementCounts = new int[Elements.altElementMax];
  public int elementNumberMax;
  public int altElementMax;
  public String mf;
  public BitSet atomList;

  /**
   * Creates an array of JmolMolecules from a set of atoms in the form of simple
   * JmolNodes. Allows for appending onto an already established set of
   * molecules.
   * @param atoms
   *          set of atoms to check
   * @param bsModelAtoms
   *          per-model atom list, or null
   * @param molecules
   *          previously made set of molecules, or null
   * @param moleculeCount TODO
   * @param bsExclude
   *          set of atoms to exclude, or null
   * @return an array of JmolMolecules
   */
  public final static JmolMolecule[] getMolecules(JmolNode[] atoms,
                                                  BitSet[] bsModelAtoms,
                                                  JmolMolecule[] molecules,
                                                  int moleculeCount, BitSet bsExclude) {
    BitSet bsToTest = null;
    BitSet bsBranch = new BitSet();
    int thisModelIndex = -1;
    int indexInModel = 0;
    if (molecules == null)
      molecules = new JmolMolecule[4];
    if (bsModelAtoms == null) {
      bsToTest = new BitSet();
      bsToTest.set(0, atoms.length);
    }
    if (bsExclude == null)
      bsExclude = new BitSet();
    for (int i = 0; i < atoms.length; i++)
      if (!bsExclude.get(i) && !bsBranch.get(i)) {
        if (atoms[i].isDeleted()) {
          bsExclude.set(i);
          continue;
        }          
        int modelIndex = atoms[i].getModelIndex();
        if (modelIndex != thisModelIndex) {
          thisModelIndex = modelIndex;
          indexInModel = 0;
          if (bsModelAtoms != null)
            bsToTest = bsModelAtoms[modelIndex];
        }
        bsBranch = getBranchBitSet(atoms, bsToTest, i, -1, true, true);
        if (bsBranch.nextSetBit(0) >= 0)
          molecules = addMolecule(molecules, moleculeCount++, atoms, i, bsBranch,
              modelIndex, indexInModel++, bsExclude);
      }
    return allocateArray(molecules, moleculeCount);
  }
  
  /**
   * 
   * given a set of atoms, a subset of atoms to test, two atoms that start the
   * branch, and whether or not to allow the branch to cycle back on itself,
   * deliver the set of atoms constituting this branch.
   * 
   * @param atoms
   * @param bsToTest
   *          some subset of those atoms
   * @param atomIndex
   *          the first atom of the branch
   * @param atomIndexNot
   *          the "root" atom stopping branch development; often a ring atom; if
   *          -1, then this method will return all atoms in a connected set of
   *          atoms.
   * @param allowCyclic
   *          allow
   * @param allowBioResidue TODO
   * @return a bitset of atoms along this branch
   */
  public static BitSet getBranchBitSet(JmolNode[] atoms, BitSet bsToTest,
                                       int atomIndex, int atomIndexNot,
                                       boolean allowCyclic, boolean allowBioResidue) {
    BitSet bs = new BitSet(atoms.length);
    if (atomIndex < 0)
      return bs;
    if (atomIndexNot >= 0)
      bsToTest.clear(atomIndexNot);
    return (getCovalentlyConnectedBitSet(atoms[atomIndex], bsToTest,
        allowCyclic, allowBioResidue, bs) ? bs : new BitSet());
  }

  public final static JmolMolecule[] addMolecule(JmolMolecule[] molecules, int iMolecule,
                                       JmolNode[] atoms, int iAtom,
                                       BitSet bsBranch, int modelIndex,
                                       int indexInModel, BitSet bsExclude) {
    bsExclude.or(bsBranch);
    if (iMolecule == molecules.length)
      molecules = allocateArray(molecules, iMolecule * 2 + 1);
    molecules[iMolecule] = new JmolMolecule(atoms, iMolecule, iAtom,
        bsBranch, modelIndex, indexInModel);
    return molecules;
  }
  
  public static String getMolecularFormula(JmolNode[] atoms, BitSet bsSelected, boolean includeMissingHydrogens) {
    JmolMolecule m = new JmolMolecule();
    m.nodes = atoms;
    m.atomList = bsSelected;
    return m.getMolecularFormula(includeMissingHydrogens);
  }
  
  public String getMolecularFormula(boolean includeMissingHydrogens) {
    if (mf != null)
      return mf;
    getElementAndAtomCount(includeMissingHydrogens);
    String mf = "";
    String sep = "";
    int nX;
    for (int i = 1; i <= elementNumberMax; i++) {
      nX = elementCounts[i];
      if (nX != 0) {
        mf += sep + Elements.elementSymbolFromNumber(i) + " " + nX;
        sep = " ";
      }
    }
    for (int i = 1; i <= altElementMax; i++) {
      nX = altElementCounts[i];
      if (nX != 0) {
        mf += sep
            + Elements.elementSymbolFromNumber(Elements
                .altElementNumberFromIndex(i)) + " " + nX;
        sep = " ";
      }
    }
    return mf;
  }

  
  private JmolMolecule(JmolNode[] nodes, int moleculeIndex, int firstAtomIndex, BitSet atomList, int modelIndex,
      int indexInModel) {
    this.nodes = nodes;
    this.firstAtomIndex = firstAtomIndex;
    this.atomList = atomList;
    this.moleculeIndex = moleculeIndex;
    this.modelIndex = modelIndex;
    this.indexInModel = indexInModel;
  }

  private void getElementAndAtomCount(boolean includeMissingHydrogens) {
    if (atomList == null) {
      atomList = new BitSet();
      atomList.set(0, nodes.length);
    }
    elementCounts = new int[Elements.elementNumberMax];
    altElementCounts = new int[Elements.altElementMax];
    int count = 0;
    for (int i = atomList.nextSetBit(0); i >= 0; i = atomList.nextSetBit(i + 1)) {
      count++;
      int n = nodes[i].getAtomicAndIsotopeNumber();
      if (n < Elements.elementNumberMax) {
        elementCounts[n]++;
        if (elementCounts[n] == 1)
          nElements++;
        elementNumberMax = Math.max(elementNumberMax, n);
        if (includeMissingHydrogens) {
          int nH = nodes[i].getImplicitHydrogenCount();
          if (nH > 0) {
            if (elementCounts[1] == 0)
              nElements++;
            elementCounts[1] += nH;
          }
        }
      } else {
        n = Elements.altElementIndexFromNumber(n);
        altElementCounts[n]++;
        if (altElementCounts[n] == 1)
          nElements++;
        altElementMax = Math.max(altElementMax, n);
      }
    }
    atomCount = count;
  }

  private static boolean getCovalentlyConnectedBitSet(JmolNode atom,
                                                      BitSet bsToTest,
                                                      boolean allowCyclic,
                                                      boolean allowBioResidue, BitSet bsResult) {
    int atomIndex = atom.getIndex();
    if (!bsToTest.get(atomIndex))
      return allowCyclic;
    if (!allowBioResidue && atom.getGroupType().length() > 0)
      return allowCyclic;
    bsToTest.clear(atomIndex);
    bsResult.set(atomIndex);
    JmolEdge[] bonds = atom.getEdges();
    if (bonds == null)
      return true;
    for (int i = bonds.length; --i >= 0;) {
      JmolEdge bond = bonds[i];
      if (bond.isCovalent()
          && !getCovalentlyConnectedBitSet(bond.getOtherAtom(atom), bsToTest,
              allowCyclic, allowBioResidue, bsResult))
        return false;
    }
    return true;
  }
  
  private static JmolMolecule[] allocateArray(JmolMolecule[] molecules, int len) {
    if (molecules.length == len)
      return molecules;
    JmolMolecule[] jm = new JmolMolecule[len];
    System.arraycopy(molecules, 0, jm, 0, len < molecules.length ? len : molecules.length);
    return jm;
  }
  

}  
