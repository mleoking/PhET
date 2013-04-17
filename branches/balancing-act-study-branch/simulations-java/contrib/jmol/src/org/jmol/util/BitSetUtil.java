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

import java.util.BitSet;

final public class BitSetUtil {

  public static final BitSet bsNull= new BitSet();

  public static BitSet setBit(int i) {
    BitSet bs = new BitSet(i + 1);
    bs.set(i);
    return bs;
  }
  
  public static boolean areEqual(BitSet a, BitSet b) {
    return (a == null || b == null ? a == null && b == null : a.equals(b));
  }

  public static boolean haveCommon(BitSet a, BitSet b) {
    return (a == null || b == null ? false : a.intersects(b));
  }

  /**
   * cardinality = "total number of set bits"
   * @param bs
   * @return number of set bits
   */
  public static int cardinalityOf(BitSet bs) {
    return (bs == null ? 0 : bs.cardinality());
  }

  public static BitSet newBitSet(int i0, int i1) {
    BitSet bs = new BitSet(i1);
    bs.set(i0, i1);
    return bs;
  }
  
  public static BitSet setAll(int n) {
    BitSet bs = new BitSet(n);
    bs.set(0, n);
    return bs;
  }

  public static BitSet andNot(BitSet a, BitSet b) {
    if (b != null)
      a.andNot(b);
    return a;
  }

  public static BitSet copy(BitSet bs) {
    return bs == null ? null : (BitSet) bs.clone();
  }

  public static BitSet copy(BitSet a, BitSet b) {
    if (a == null || b == null)
      return null;
    b.clear();
    b.or(a);
    return b;
  }
  
  public static BitSet copyInvert(BitSet bs, int n) {
    return (bs == null ? null : andNot(setAll(n), bs));
  }
  
  /**
   * inverts the bitset bits 0 through n-1, 
   * and returns a reference to the modified bitset
   * 
   * @param bs
   * @param n
   * @return  pointer to original bitset, now inverted
   */
  public static BitSet invertInPlace(BitSet bs, int n) {
    return copy(copyInvert(bs, n), bs);
  }

  /**
   * a perhaps curious method:
   * 
   * b is a reference set, perhaps all atoms in a certain molecule a is the
   * working set, perhaps representing all displayed atoms
   * 
   * For each set bit in b: a) if a is also set, then clear a's bit UNLESS b) if
   * a is not set, then add to a all set bits of b
   * 
   * Thus, if a equals b --> clear all if a is a subset of b, then --> b if b is
   * a subset of a, then --> a not b if a only intersects with b, then --> a or
   * b if a does not intersect with b, then a or b
   * 
   * In "toggle" mode, when you click on any atom of the molecule, you want
   * either:
   * 
   * (a) all the atoms in the molecule to be displayed if not all are already
   * displayed, or
   * 
   * (b) the whole molecule to be hidden if all the atoms of the molecule are
   * already displayed.
   * 
   * @param a
   * @param b
   * @return a handy pointer to the working set, a
   */
  public static BitSet toggleInPlace(BitSet a, BitSet b) {
    if (a.equals(b)) {
      // all on -- toggle all off
      a.clear();
    } else if (andNot(copy(b), a).length() == 0) {
      // b is a subset of a -> remove all b bits from a
      andNot(a, b); 
    } else {
      // may or may not be some overlap
      // combine a and b
      a.or(b);
    }
    return a;
  }
  
  /**
   * this one slides deleted bits out of a pattern.
   * 
   *    deleteBits 101011b, 000011b  --> 1010b
   *    
   *    Java 1.4, not 1.3
   * 
   * @param bs
   * @param bsDelete
   * @return             shorter bitset
   */
  public static BitSet deleteBits(BitSet bs, BitSet bsDelete) {
    if (bs == null || bsDelete == null)
      return bs;
    int ipt = bsDelete.nextSetBit(0);
    if (ipt < 0)
      return bs;
    int len = bs.length();
    int lend = Math.min(len, bsDelete.length());
    int i;
    for (i = bsDelete.nextClearBit(ipt); i < lend && i >= 0; i = bsDelete.nextClearBit(i + 1))
      bs.set(ipt++, bs.get(i));
    for (i = lend; i < len; i++)
      bs.set(ipt++, bs.get(i));
    if (ipt < len)
      bs.clear(ipt, len);
    return bs;
  }
}
