/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-11-12 16:41:15 -0800 (Fri, 12 Nov 2010) $
 * $Revision: 14641 $
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
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.modelset;

import java.util.BitSet;

public final class Chain {

  ModelSet modelSet;
  Model model;
  char chainID;
  int groupCount;
  Group[] groups = new Group[16];

  private int selectedGroupCount;
  private boolean isDna, isRna;
  private BitSet bsSelectedGroups;

  public Chain(ModelSet modelSet, Model model, char chainID) {
    this.modelSet = modelSet;
    this.model = model;
    this.chainID = chainID;
  }

  public void setModelSet(ModelSet modelSet) {
    this.modelSet = modelSet;
  }
  
  public char getChainID() {
    return chainID;
  }
  
  public ModelSet getModelSet() {
    return modelSet;
  }
  
  public boolean isDna() { return isDna; }
  public boolean isRna() { return isRna; }

  public void setIsDna(boolean TF) {isDna = TF;}
  public void setIsRna(boolean TF) {isRna = TF;}

  public Group getGroup(int groupIndex) {
    return groups[groupIndex];
  }
  
  public int getGroupCount() {
    return groupCount;
  }

  public int getAtomCount() {
    return groups[groupCount - 1].lastAtomIndex + 1 - groups[0].firstAtomIndex;
  }
  
  public Atom getAtom(int index) {
    return modelSet.atoms[index];
  }
  
  /**
   * prior to coloring by group, we need the chain count per chain
   * that is selected
   * 
   * @param bsSelected
   */
  public void calcSelectedGroupsCount(BitSet bsSelected) {
    selectedGroupCount = 0;
    if (bsSelectedGroups == null)
      bsSelectedGroups = new BitSet();
    bsSelectedGroups.clear();
    for (int i = 0; i < groupCount; i++) {
      if (groups[i].isSelected(bsSelected)) {
        groups[i].selectedIndex = selectedGroupCount++;
        bsSelectedGroups.set(i);
      } else {
        groups[i].selectedIndex = -1;
      }
    }
  }

  public int selectSeqcodeRange(int index0, int seqcodeA, int seqcodeB,
                                BitSet bs) {
    int seqcode, indexA, indexB, minDiff;
    boolean isInexact = false;
    for (indexA = index0; indexA < groupCount
        && groups[indexA].seqcode != seqcodeA; indexA++) {
    }
    if (indexA == groupCount) {
      // didn't find A exactly -- go find the nearest that is GREATER than this value
      if (index0 > 0)
        return -1;
      isInexact = true;
      minDiff = Integer.MAX_VALUE;
      for (int i = groupCount; --i >= 0;)
        if ((seqcode = groups[i].seqcode) > seqcodeA
            && (seqcode - seqcodeA) < minDiff) {
          indexA = i;
          minDiff = seqcode - seqcodeA;
        }
      if (minDiff == Integer.MAX_VALUE)
        return -1;
    }
    if (seqcodeB == Integer.MAX_VALUE) {
      indexB = groupCount - 1;
      isInexact = true;
    } else {
      for (indexB = indexA; indexB < groupCount
          && groups[indexB].seqcode != seqcodeB; indexB++) {
      }
      if (indexB == groupCount) {
        // didn't find B exactly -- get the nearest that is LESS than this value
        if (index0 > 0)
          return -1;
        isInexact = true;
        minDiff = Integer.MAX_VALUE;
        for (int i = indexA; i < groupCount; i++)
          if ((seqcode = groups[i].seqcode) < seqcodeB
              && (seqcodeB - seqcode) < minDiff) {
            indexB = i;
            minDiff = seqcodeB - seqcode;
          }
        if (minDiff == Integer.MAX_VALUE)
          return -1;
      }
    }
    for (int i = indexA; i <= indexB; ++i)
      groups[i].selectAtoms(bs);
    return (isInexact ? -1 : indexB + 1);
  }
  
  int getSelectedGroupCount() {
    return selectedGroupCount;
  }

  public void fixIndices(int atomsDeleted) {
    for (int i = 0; i < groupCount; i++) {
      groups[i].firstAtomIndex -= atomsDeleted;
      groups[i].leadAtomIndex -= atomsDeleted;
      groups[i].lastAtomIndex -= atomsDeleted;
    }
  }
}
