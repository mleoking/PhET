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

package org.jmol.modelset;

import java.util.BitSet;

import javax.vecmath.Point3f;

import org.jmol.api.AtomIndexIterator;
import org.jmol.bspt.Bspf;
import org.jmol.bspt.CubeIterator;

public class AtomIteratorWithinModel implements AtomIndexIterator {

  private CubeIterator bsptIter;
  private Bspf bspf;
  private boolean threadSafe;
  private boolean hemisphereOnly;
  private boolean isZeroBased;

  private int modelIndex = Integer.MAX_VALUE;
  private int atomIndex = -1;
  private int zeroBase;
  private float distanceSquared;

  private BitSet bsSelected;
  private boolean isGreaterOnly;
  private boolean checkGreater;

  
  /**
   * 
   * ############## ITERATOR SHOULD BE RELEASED #################
   * 
   * @param bspf
   * @param bsSelected 
   * @param isGreaterOnly 
   * @param isZeroBased
   * @param hemisphereOnly TODO
   * @param threadSafe 
   * 
   */

  void initialize(Bspf bspf, BitSet bsSelected, boolean isGreaterOnly, 
                  boolean isZeroBased, boolean hemisphereOnly, boolean threadSafe) {
    this.bspf = bspf;
    this.bsSelected = bsSelected;
    this.isGreaterOnly = isGreaterOnly;
    this.isZeroBased = isZeroBased;
    this.hemisphereOnly = hemisphereOnly;
    this.threadSafe = threadSafe;
  }

  public void set(int modelIndex, int firstModelAtom, int atomIndex, Point3f center, float distance) {
    if (threadSafe)
      modelIndex = -1 - modelIndex; // no caching
    if (modelIndex != this.modelIndex || bsptIter == null) {
      bsptIter = bspf.getCubeIterator(modelIndex);
      this.modelIndex = modelIndex;
      //bspf.dump();
    }
    zeroBase = (isZeroBased ? firstModelAtom : 0);
    if (distance == Integer.MIN_VALUE) // distance and center will be added later
      return;
    this.atomIndex = (distance < 0 ? -1 : atomIndex);
    checkGreater = (isGreaterOnly && atomIndex != Integer.MAX_VALUE);
    set(center, distance);
  }

  public void set(Point3f center, float distance) {
    if (bsptIter == null)
      return;
    bsptIter.initialize(center, distance, hemisphereOnly);
    distanceSquared = distance * distance;
  }
  
  private int iNext;
  public boolean hasNext() {
    if (atomIndex >= 0)
      while (bsptIter.hasMoreElements()) {
        Atom a = (Atom) bsptIter.nextElement();
        if ((iNext = a.index) != atomIndex
            && (!checkGreater || iNext > atomIndex)
            && (bsSelected == null || bsSelected.get(iNext))) {
          return true;
        }
      }
    else if (bsptIter.hasMoreElements()) {
      Atom a = (Atom) bsptIter.nextElement();
      iNext = a.index;
      return true;
    }
    iNext = -1;
    return false;
  }
  
  public int next() {
    return iNext - zeroBase;
  }
  
  public float foundDistance2() {
    return (bsptIter == null ? -1 : bsptIter.foundDistance2());
  }
  
  /**
   * turns this into a SPHERICAL iterator
   * for "within Distance" measures
   * 
   * @param bsResult 
   * 
   */
  public void addAtoms(BitSet bsResult) {
    int iAtom;
    while (hasNext())
      if ((iAtom = next()) >= 0
          && foundDistance2() <= distanceSquared)
        bsResult.set(iAtom);    
  }

  public void release() {
    if (bsptIter != null) {
      bsptIter.release();
      bsptIter = null;
    }
  }

}

