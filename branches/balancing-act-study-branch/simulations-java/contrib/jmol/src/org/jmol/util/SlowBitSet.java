/* $RCSfile$
 * $Author: egonw $
 * $Date: 2005-11-10 09:52:44 -0600 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
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
package org.jmol.util;

/*
 * for comparison purposes -- the standard BitSet implementation (dots and geosurfaces only)
 * implemented by EnvelopeCalculation using set testflag2 TRUE
 * 
 * needs full implemention of public FastBitSet methods to work properly.
 * 
 */

public class SlowBitSet {//extends FastBitSet {
/*
  public static FastBitSet allocateBitmap(int count) {
    return new SlowBitSet(count, true);
  }
  
  private java.util.BitSet bs;

  public SlowBitSet() {
    bs = new java.util.BitSet();
  }

  protected SlowBitSet(int count, boolean asBits) {
    bs = new java.util.BitSet(asBits ? count : count * 64 ); 
  }
  
  protected SlowBitSet(FastBitSet bsToCopy) {
    bs = (java.util.BitSet) ((SlowBitSet) bsToCopy).bs.clone();
  }
  
  public void and(FastBitSet setAnd) {
    bs.and(((SlowBitSet) setAnd).bs);
  }

  public void andNot(FastBitSet setAndNot) {
    bs.andNot(((SlowBitSet) setAndNot).bs);
  }

  public int cardinality() {
    return bs.cardinality();
  }

  public int cartdinality(int dotCount) {
    return bs.cardinality();
  }

  public void clear() {
    bs.clear();
  }

  public void clear(int i) {
    bs.clear(i);
  }

  public void clear(int i, int j) {
    bs.clear(i, j);
  }
  
  public Object clone() {
    SlowBitSet result = new SlowBitSet();
    result.bs = (java.util.BitSet) bs.clone();
    return result;
  }

  public FastBitSet copy() {
    return (SlowBitSet) clone();
  }

  public boolean equals(Object obj) {
    return (obj instanceof SlowBitSet && ((SlowBitSet) obj).bs.equals(bs));
  }

  public void flip(int i) {
    bs.flip(i);
  }

  public boolean get(int i) {
    return bs.get(i);
  }

  
  public void setAllBits(int count) {
    bs.set(0, count);
  }
  
  
  public boolean isEmpty() {
    return bs.isEmpty();
  }

  public int length() {
    return bs.length();
  }

  public int nextSetBit(int fromIndex) {
    return bs.nextSetBit(fromIndex);
  }

  public void or(FastBitSet setOr) {
    bs.or(((SlowBitSet) setOr).bs);
  }

  public void set(int i) {
   bs.set(i);
  }

  public void set(int i, int j) {
    bs.set(i, j);
  }

  public int size() {
    return bs.size();
  }

  public void xor(FastBitSet setXor) {
    bs.xor(((SlowBitSet) setXor).bs);
  }

  public java.util.BitSet toBitSet() {
    return BitSetUtil.copy(bs);
  }
      
  public String toString() {
    return Escape.escape(bs);
  }

  public int hashCode() {
    return bs.hashCode();
  }

*/
}
