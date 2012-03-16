/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-04-17 16:24:30 -0700 (Sat, 17 Apr 2010) $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development, www.jmol.org
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
package org.jmol.bspt;

import java.util.BitSet;

import javax.vecmath.Point3f;

import org.jmol.util.Logger;

/**
 * A Binary Space Partitioning Forest
 *<p>
 * This is simply an array of Binary Space Partitioning Trees identified
 * by indexes
 *
 * @author Miguel, miguel@jmol.org
*/

public final class Bspf {

  int dimMax;
  Bspt bspts[];
  //SphereIterator[] sphereIterators;
  CubeIterator[] cubeIterators;
  
  public Bspf(int dimMax) {
    this.dimMax = dimMax;
    bspts = new Bspt[0];
    cubeIterators = new CubeIterator[0];
  }

  public int getBsptCount() {
    return bspts.length;
  }
  
  public void clearBspt(int bsptIndex) {
    bspts[bsptIndex] = null;
  }
  
  public boolean isInitialized(int bsptIndex) {
    return bspts.length > bsptIndex && bspts[bsptIndex] != null;
  }
  
  public void addTuple(int bsptIndex, Point3f tuple) {
    if (bsptIndex >= bspts.length) {
      Bspt[] t = new Bspt[bsptIndex + 1];
      System.arraycopy(bspts, 0, t, 0, bspts.length);
      bspts = t;
    }
    Bspt bspt = bspts[bsptIndex];
    if (bspt == null)
      bspt = bspts[bsptIndex] = new Bspt(dimMax);
    bspt.addTuple(tuple);
  }

  public void stats() {
    for (int i = 0; i < bspts.length; ++i)
      if (bspts[i] != null)
        bspts[i].stats();
  }

  
  public void dump() {
    for (int i = 0; i < bspts.length; ++i) {
      Logger.info(">>>>\nDumping bspt #" + i + "\n>>>>");
      bspts[i].dump();
    }
    Logger.info("<<<<");
  }
  
/*
  public SphereIterator getSphereIterator(int bsptIndex) {
    if (bsptIndex >= sphereIterators.length) {
      SphereIterator[] t = new SphereIterator[bsptIndex + 1];
      System.arraycopy(sphereIterators, 0, t, 0, sphereIterators.length);
      sphereIterators = t;
    }
    if (sphereIterators[bsptIndex] == null &&
        bspts[bsptIndex] != null)
      sphereIterators[bsptIndex] = bspts[bsptIndex].allocateSphereIterator();
    return sphereIterators[bsptIndex];
  }
*/  
  /**
   * @param bsptIndex  a model index
   * @return           either a cached or a new CubeIterator
   * 
   */
  public CubeIterator getCubeIterator(int bsptIndex) {
    if (bsptIndex < 0)
      return getNewCubeIterator(-1 - bsptIndex);
    if (bsptIndex >= cubeIterators.length) {
      CubeIterator[] t = new CubeIterator[bsptIndex + 1];
      System.arraycopy(cubeIterators, 0, t, 0, cubeIterators.length);
      cubeIterators = t;
    }
    if (cubeIterators[bsptIndex] == null &&
        bspts[bsptIndex] != null)
      cubeIterators[bsptIndex] = getNewCubeIterator(bsptIndex);
    return cubeIterators[bsptIndex];
  }

  public CubeIterator getNewCubeIterator(int bsptIndex) {
      return bspts[bsptIndex].allocateCubeIterator();
  }

  public void initialize(int modelIndex, Point3f[] atoms, BitSet modelAtomBitSet) {
    for (int i = modelAtomBitSet.nextSetBit(0); i >= 0; i = modelAtomBitSet.nextSetBit(i + 1))
      addTuple(modelIndex, atoms[i]);
  }

}
