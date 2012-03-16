/*
 * $Author$
 * $Date$
 * $Rev$
 *
 * Copyright (C) 2003-2010  The Jmol Development Team
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
 * API is basically a copy of java.util.BitSet
 * a few routines are not implemented, but can be added if needed
 */

public class FastBitSet implements Cloneable {

  /*
   * Miguel Howard's raw bitset implementation -- faster by a factor of two over
   * standard BitSet class
   */

  private int[] bitmap;

  private final static int[] emptyBitmap = new int[0];

  public FastBitSet() {
    bitmap = emptyBitmap;
  }

  private FastBitSet(int bitCount) {
    bitmap = new int[getWordCountFromBitCount(bitCount)];
  }

  public FastBitSet(FastBitSet bitsetToCopy) {
    int wordCount = bitmapGetMinimumWordCount(bitsetToCopy.bitmap);
    if (wordCount == 0)
      bitmap = emptyBitmap;
    else {
      bitmap = new int[wordCount];
      System.arraycopy(bitsetToCopy.bitmap, 0, bitmap, 0, wordCount);
    }
  }

  public final static FastBitSet emptySet = new FastBitSet();

  public final static FastBitSet getEmptySet() {
    return emptySet;
  }

  public static FastBitSet allocateBitmap(int bitCount) {
    return new FastBitSet(bitCount);
  }

  public void and(FastBitSet setAnd) {
    bitmapAnd(bitmap, setAnd.bitmap);
  }

  public void andNot(FastBitSet setAndNot) {
    bitmapAndNot(bitmap, setAndNot.bitmap);
  }

  public int cardinality() {
    return bitmapGetCardinality(bitmap);
  }

  /**
   * 
   * @param max
   * @return n bits below max
   */
  public int cardinality(int max) {
    int n = bitmapGetCardinality(bitmap);
    for (int i = length(); --i >= max;)
      if (get(i))
        n--;
    return n;
  }

  public void clear() {
    bitmapClear(bitmap);
  }

  public void clear(int bitIndex) {
    if ((bitIndex >> F_ADDRESS_BITS_PER_WORD) < bitmap.length)
      bitmapClearBit(bitmap, bitIndex);
  }

  public void clear(int fromIndex, int toIndex) {
    int bitmapCount = bitmapGetSizeInBits(bitmap);
    if (fromIndex >= bitmapCount)
      return;
    if (toIndex > bitmapCount)
      toIndex = bitmapCount;
    bitmapClearRange(bitmap, fromIndex, toIndex - fromIndex);
  }

  @Override
  public Object clone() {
    int bitCount = bitmapGetSizeInBits(bitmap);
    FastBitSet result = new FastBitSet(bitCount);
    System.arraycopy(bitmap, 0, result.bitmap, 0, bitmap.length);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof FastBitSet && bitmapIsEqual(bitmap,
        ((FastBitSet) obj).bitmap));
  }

  public void flip(int bitIndex) {
    if (get(bitIndex))
      clear(bitIndex);
    else
      set(bitIndex);
  }

  public void flip(int fromIndex, int toIndex) {
    for (int i = fromIndex; i < toIndex; ++i)
      flip(i);
  }

  public boolean get(int bitIndex) {
    return (bitIndex < bitmapGetSizeInBits(bitmap) && bitmapGetBit(bitmap,
        bitIndex));
  }

  public boolean isEmpty() {
    return bitmapIsEmpty(bitmap);
  }

  /*
   * public int length() { int i = bitmapGetMinimumWordCount(bitmap) <<
   * F_ADDRESS_BITS_PER_WORD; while (--i >= 0 && ! bitmapGetBit(bitmap, i)) ;
   * return i + 1; }
   */

  public int length() {
    int i = bitmapGetMinimumWordCount(bitmap);
    return (i == 0 ? 0 : (i << F_ADDRESS_BITS_PER_WORD) - numberOfLeadingZeros(bitmap[i - 1]));
  }

  public int nextSetBit(int fromIndex) {
    return bitmapNextSetBit(bitmap, fromIndex);
  }

  public void or(FastBitSet setOr) {
    bitmapOr(bitmap = ensureSufficientWords(bitmap, setOr.bitmap.length), setOr.bitmap);
  }

  public void set(int bitIndex) {
    bitmapSetBit(bitmap = ensureSufficientBits(bitmap, bitIndex + 1), bitIndex);
  }

  public void set(int bitIndex, boolean value) {
    if (value)
      set(bitIndex);
    else
      clear(bitIndex);
  }

  public void set(int fromIndex, int toIndex) {
    bitmapSetRange(bitmap = ensureSufficientBits(bitmap, toIndex), fromIndex, toIndex
        - fromIndex);
  }

  public void set(int fromIndex, int toIndex, boolean value) {
    if (value)
      set(fromIndex, toIndex);
    else
      clear(fromIndex, toIndex);
  }

  public int size() {
    return bitmapGetSizeInBits(bitmap);
  }

  public void xor(FastBitSet setXor) {
    bitmapXor(bitmap = ensureSufficientWords(bitmap, setXor.bitmap.length),
        setXor.bitmap);
  }

  // public FastBitSet copyFast() {
  // int wordCount = bitmapGetMinimumWordCount(bitmap);
  // FastBitSet fbs = new FastBitSet(wordCount << F_ADDRESS_BITS_PER_WORD);
  // System.arraycopy(bitmap, 0, fbs.bitmap, 0, wordCount);
  // return fbs;
  // }

  public java.util.BitSet toBitSet() {
    java.util.BitSet bs = new java.util.BitSet();
    int i = bitmapGetSizeInBits(bitmap);
    while (--i >= 0)
      if (get(i))
        bs.set(i);
    return bs;
  }

  @Override
  public String toString() {
    return Escape.escape(toBitSet());
  }

  @Override
  public int hashCode() {
    long h = 1234;
    for (int i = bitmap.length; --i >= 0;)
      h ^= bitmap[i] * (i + 1);
    return (int) ((h >> 32) ^ h);
  }

  // //////////////////////////////////////////////////////////////

  /****************************************************************
   * miguel 8 Feb 2010
   * 
   * Below are implementations of bitmap functionality on top of arrays. Around
   * 2002 I chose to go with int[] instead of long[] because I felt it would
   * give better performance on contemporary hardware. I think that is probably
   * still the case. At some point over the next few years this can be changed
   * to long[].
   * 
   * Since these methods are marked private final static the compiler can make
   * easy/good decisions about which ones to open code inline.
   ****************************************************************/

  private final static int F_ADDRESS_BITS_PER_WORD = 5;
  private final static int F_BITS_PER_WORD = 1 << F_ADDRESS_BITS_PER_WORD;
  private final static int F_BIT_INDEX_MASK = F_BITS_PER_WORD - 1;

  /*
   * private final static int[] bitmapAllocateBitCount(int bitCount) { return
   * new int[getWordCountFromBitCount(bitCount)]; }
   */

  private final static boolean bitmapGetBit(int[] bitmap, int i) {
    return ((bitmap[(i >> F_ADDRESS_BITS_PER_WORD)] >> (i & F_BIT_INDEX_MASK)) & 1) != 0;
  }

  private final static void bitmapSetBit(int[] bitmap, int i) {
    bitmap[(i >> F_ADDRESS_BITS_PER_WORD)] |= 1 << (i & F_BIT_INDEX_MASK);
  }

  private final static void bitmapClearBit(int[] bitmap, int i) {
    bitmap[(i >> F_ADDRESS_BITS_PER_WORD)] &= ~(1 << (i & F_BIT_INDEX_MASK));
  }

  private final static int F_INT_ALL_BITS_SET = 0xFFFFFFFF;

  /*
   * private final static int F_INT_SHIFT_MASK = 0x80000000; private final
   * static void bitmapSetAllBits(int[] bitmap, int bitCount) { int
   * wholeWordCount = bitCount >> F_ADDRESS_BITS_PER_WORD; int
   * fractionalWordBitCount = bitCount & F_BIT_INDEX_MASK; if
   * (fractionalWordBitCount > 0) bitmap[wholeWordCount] = ~(F_INT_SHIFT_MASK >>
   * F_BITS_PER_WORD - 1 - fractionalWordBitCount); while (--wholeWordCount >=
   * 0) bitmap[wholeWordCount] = F_INT_ALL_BITS_SET; }
   */

  private final static void bitmapSetRange(int[] bitmap, int iStart,
                                           int bitCount) {
    /* increment iStart up to a word boundary the slow way */
    while ((iStart & F_BIT_INDEX_MASK) != 0) {
      bitmapSetBit(bitmap, iStart++);
      if (--bitCount == 0)
        return;
    }
    /* decrement bitCount down to a whole word boundary the slow way */
    while ((bitCount & F_BIT_INDEX_MASK) != 0) {
      bitmapSetBit(bitmap, iStart + --bitCount);
    }
    /* fill in the whole words */
    int wordIndex = iStart >> F_ADDRESS_BITS_PER_WORD;
    int wordCount = bitCount >> F_ADDRESS_BITS_PER_WORD;
    while (--wordCount >= 0)
      bitmap[wordIndex++] = F_INT_ALL_BITS_SET;
  }

  private final static void bitmapClearRange(int[] bitmap, int iStart,
                                             int bitCount) {
    /* increment iStart up to a word boundary the slow way */
    while ((iStart & F_BIT_INDEX_MASK) != 0) {
      bitmapClearBit(bitmap, iStart++);
      if (--bitCount == 0)
        return;
    }
    /* decrement bitCount down to a whole word boundary the slow way */
    while ((bitCount & F_BIT_INDEX_MASK) != 0)
      bitmapClearBit(bitmap, iStart + --bitCount);
    /* fill in the whole words */
    int wordIndex = iStart >> F_ADDRESS_BITS_PER_WORD;
    int wordCount = bitCount >> F_ADDRESS_BITS_PER_WORD;
    while (--wordCount >= 0)
      bitmap[wordIndex++] = 0;
  }

  private final static void bitmapClear(int[] bitmap) {
    for (int i = bitmap.length; --i >= 0;)
      bitmap[i] = 0;
  }

  private final static int bitmapGetMinimumWordCount(int[] bitmap) {
    int indexLast;
    for (indexLast = bitmap.length; --indexLast >= 0 && bitmap[indexLast] == 0;) {
      // nada
    }
    return indexLast + 1;
  }

  private final static int bitmapGetSizeInBits(int[] bitmap) {
    return bitmap.length << F_ADDRESS_BITS_PER_WORD;
  }

  private final static int getWordCountFromBitCount(int bitCount) {
    return (bitCount + F_BITS_PER_WORD - 1) >> F_ADDRESS_BITS_PER_WORD;
  }

  /*
   * private final static int[] bitmapResizeBitCount(int[] oldBitmap, int
   * bitCount) { int newWordCount = getWordCountFromBitCount(bitCount); int[]
   * newBitmap = new int[newWordCount]; int oldWordCount = oldBitmap.length; int
   * wordsToCopy = (newWordCount < oldWordCount) ? newWordCount : oldWordCount;
   * System.arraycopy(oldBitmap, 0, newBitmap, 0, wordsToCopy); return
   * newBitmap; }
   */

  private final static void bitmapAnd(int[] bitmap, int[] bitmapAnd) {
    int wordCount = bitmap.length < bitmapAnd.length ? bitmap.length
        : bitmapAnd.length;
    int n = bitmap.length;
    while (n > wordCount)
      bitmap[--n] = 0;
    while (--wordCount >= 0)
      bitmap[wordCount] &= bitmapAnd[wordCount];
  }

  private final static void bitmapAndNot(int[] bitmap, int[] bitmapAndNot) {
    int wordCount = (bitmap.length < bitmapAndNot.length) ? bitmap.length
        : bitmapAndNot.length;
    while (--wordCount >= 0)
      bitmap[wordCount] &= ~bitmapAndNot[wordCount];
  }

  // bitmap.length should be >= bitmapOr.length
  // to try to enforce this, I am just going to assume that it is the case
  // that way, an OOB exception will be raised
  private final static void bitmapOr(int[] bitmap, int[] bitmapOr) {
    int wordCount = bitmapOr.length;
    while (--wordCount >= 0)
      bitmap[wordCount] |= bitmapOr[wordCount];
  }

  private final static void bitmapXor(int[] bitmap, int[] bitmapXor) {
    int wordCount = bitmapXor.length;
    while (--wordCount >= 0)
      bitmap[wordCount] ^= bitmapXor[wordCount];
  }

  private final static int bitmapNextSetBit(int[] bitmap, int fromIndex) {
    int maxIndex = bitmap.length << F_ADDRESS_BITS_PER_WORD;
    if (fromIndex >= maxIndex)
      return -1;
    // get up to a word boundary
    while ((fromIndex & F_BIT_INDEX_MASK) != 0) {
      if (bitmapGetBit(bitmap, fromIndex))
        return fromIndex;
      ++fromIndex;
    }
    // skip zero words
    while (fromIndex < maxIndex) {
      if (bitmap[fromIndex >> F_ADDRESS_BITS_PER_WORD] != 0)
        break;
      fromIndex += F_BITS_PER_WORD;
    }
    while (fromIndex < maxIndex) {
      if (bitmapGetBit(bitmap, fromIndex))
        return fromIndex;
      ++fromIndex;
    }
    return -1;
  }

  // shrink a bitmap array by removing any zero words at the end of the array
  // note that this may return the bitmap itself without allocating
  // a new bitmap

  /*
   * private final static int[] bitmapMinimize(int[] bitmap) { int
   * minimumWordCount = bitmapGetMinimumWordCount(bitmap); if (minimumWordCount
   * == 0) return emptyBitmap; if (minimumWordCount == bitmap.length) return
   * bitmap; int[] newBitmap = new int[minimumWordCount];
   * System.arraycopy(bitmap, 0, newBitmap, 0, minimumWordCount); return
   * newBitmap; }
   */

  private final static int bitmapGetCardinality(int[] bitmap) {
    int count = 0;
    for (int i = bitmap.length; --i >= 0;) {
      if (bitmap[i] != 0)
        count += countBitsInWord(bitmap[i]);
    }
    return count;
  }

  private final static int countBitsInWord(int word) {
    word = (word & 0x55555555) + ((word >> 1) & 0x55555555);
    word = (word & 0x33333333) + ((word >> 2) & 0x33333333);
    word = (word & 0x0F0F0F0F) + ((word >> 4) & 0x0F0F0F0F);
    word = (word & 0x00FF00FF) + ((word >> 8) & 0x00FF00FF);
    word = (word & 0x0000FFFF) + ((word >> 16) & 0x0000FFFF);
    return word;
  }

  private final static boolean bitmapIsEqual(int[] bitmap1, int[] bitmap2) {
    if (bitmap1 == bitmap2)
      return true;
    int count1 = bitmapGetMinimumWordCount(bitmap1);
    if (count1 != bitmapGetMinimumWordCount(bitmap2))
      return false;
    while (--count1 >= 0)
      if (bitmap1[count1] != bitmap2[count1])
        return false;
    return true;
  }

  private final static boolean bitmapIsEmpty(int[] bitmap) {
    int i = bitmap.length;
    while (--i >= 0)
      if (bitmap[i] != 0)
        return false;
    return true;
  }

  private final static int numberOfLeadingZeros(int i) {
    if (i == 0)
      return 32;
    int n = 1;
    if (i >>> 16 == 0) {
      n += 16;
      i <<= 16;
    }
    if (i >>> 24 == 0) {
      n += 8;
      i <<= 8;
    }
    if (i >>> 28 == 0) {
      n += 4;
      i <<= 4;
    }
    if (i >>> 30 == 0) {
      n += 2;
      i <<= 2;
    }
    n -= i >>> 31;
    return n;
  }

  /*
   * over 13% slower
   * 
   * private void ensureSufficientBits(int minimumBitCount) { int wordCount =
   * (minimumBitCount + F_BIT_INDEX_MASK) >> F_ADDRESS_BITS_PER_WORD; if
   * (wordCount > bitmap.length) { int[] newBitmap = new int[wordCount];
   * System.arraycopy(bitmap, 0, newBitmap, 0, bitmap.length); bitmap =
   * newBitmap; } }
   */
  private final static int[] ensureSufficientBits(int[] bitmap,
                                                  int minimumBitCount) {
    return ensureSufficientWords(bitmap,
        (minimumBitCount + F_BIT_INDEX_MASK) >> F_ADDRESS_BITS_PER_WORD);
  }

  private final static int[] ensureSufficientWords(int[] bitmap,
                                                   int minimumWordCount) {
    if (minimumWordCount > bitmap.length) {
      int[] newBitmap = new int[minimumWordCount];
      System.arraycopy(bitmap, 0, newBitmap, 0, bitmap.length);
      return newBitmap;
    }
    return bitmap;
  }

  // / testing:

  /*
   * FastBitSet(java.util.BitSet bs) { bitmap = new
   * int[getWordCountFromBitCount(bs.size())]; for (int i = bs.nextSetBit(0); i
   * >= 0; i = bs.nextSetBit(i + 1)) set(i); }
   * 
   * static {
   * 
   * FastBitSet bs = new FastBitSet(Escape.unescapeBitset("{(33:45 75:80)}"));
   * System.out.println(bs.cardinality());
   * System.out.println(bs.cardinality(35)); // ...do whatever here...
   * System.out.println(bs);
   * 
   * }
   */
}
