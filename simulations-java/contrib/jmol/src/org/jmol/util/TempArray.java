/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-04-05 09:07:28 -0500 (Thu, 05 Apr 2007) $
 * $Revision: 7326 $
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

import javax.vecmath.*;

public class TempArray {

  public TempArray() {
  }

  
  public void clear() {
    clearTempPoints();
    clearTempScreens();
    //clearTempBooleans();
  }
  
  private static int findBestFit(int size, int[] lengths) {
    int iFit = -1;
    int fitLength = Integer.MAX_VALUE;

    for (int i = lengths.length; --i >= 0;) {
      int freeLength = lengths[i];
      if (freeLength >= size && freeLength < fitLength) {
        fitLength = freeLength;
        iFit = i;
      }
    }
    if (iFit >= 0)
      lengths[iFit] = 0;
    return iFit;
  }

  private static int findShorter(int size, int [] lengths) {
    for (int i = lengths.length; --i >= 0;)
      if (lengths[i] == 0) {
        lengths[i] = size;
        return i;
      }
    int iShortest = 0;
    int shortest = lengths[0];
    for (int i = lengths.length; --i > 0;)
      if (lengths[i] < shortest) {
        shortest = lengths[i];
        iShortest = i;
      }
    if (shortest < size) {
      lengths[iShortest] = size;
      return iShortest;
    }
    return -1;
  }

  ////////////////////////////////////////////////////////////////
  // temp Points
  ////////////////////////////////////////////////////////////////
  private final static int freePointsSize = 6;
  private final int[] lengthsFreePoints = new int[freePointsSize];
  private final Point3f[][] freePoints = new Point3f[freePointsSize][];

  private void clearTempPoints() {
    for (int i = 0; i < freePointsSize; i++) {
      lengthsFreePoints[i] = 0;
      freePoints[i] = null;
    }
  }
  
  public Point3f[] allocTempPoints(int size) {
    Point3f[] tempPoints;
    int iFit = findBestFit(size, lengthsFreePoints);
    if (iFit > 0) {
      tempPoints = freePoints[iFit];
    } else {
      tempPoints = new Point3f[size];
      for (int i = size; --i >= 0;)
        tempPoints[i] = new Point3f();
    }
    return tempPoints;
  }

  public void freeTempPoints(Point3f[] tempPoints) {
    for (int i = 0; i < freePoints.length; i++)
      if (freePoints[i] == tempPoints) {
        lengthsFreePoints[i] = tempPoints.length;
        return;
      }
    int iFree = findShorter(tempPoints.length, lengthsFreePoints);
    if (iFree >= 0)
      freePoints[iFree] = tempPoints;
  }

  ////////////////////////////////////////////////////////////////
  // temp Screens
  ////////////////////////////////////////////////////////////////
  private final static int freeScreensSize = 6;
  private final int[] lengthsFreeScreens = new int[freeScreensSize];
  private final Point3i[][] freeScreens = new Point3i[freeScreensSize][];

  private void clearTempScreens() {
    for (int i = 0; i < freeScreensSize; i++) {
      lengthsFreeScreens[i] = 0;
      freeScreens[i] = null;
    }
  }
  
  public Point3i[] allocTempScreens(int size) {
    Point3i[] tempScreens;
    int iFit = findBestFit(size, lengthsFreeScreens);
    if (iFit > 0) {
      tempScreens = freeScreens[iFit];
    } else {
      tempScreens = new Point3i[size];
      for (int i = size; --i >= 0;)
        tempScreens[i] = new Point3i();
    }
    return tempScreens;
  }

  public void freeTempScreens(Point3i[] tempScreens) {
    for (int i = 0; i < freeScreens.length; i++)
      if (freeScreens[i] == tempScreens) {
        lengthsFreeScreens[i] = tempScreens.length;
        return;
      }
    int iFree = findShorter(tempScreens.length, lengthsFreeScreens);
    if (iFree >= 0)
      freeScreens[iFree] = tempScreens;
  }

  ////////////////////////////////////////////////////////////////
  // temp booleans
  ////////////////////////////////////////////////////////////////
  /*
  final static int freeBooleansSize = 2;
  final int[] lengthsFreeBooleans = new int[freeBooleansSize];
  final boolean[][] freeBooleans = new boolean[freeBooleansSize][];

  void clearTempBooleans() {
    for (int i = 0; i < freeBooleansSize; i++) {
      lengthsFreeBooleans[i] = 0;
      freeBooleans[i] = null;
    }
  }
  
  boolean[] allocTempBooleans(int size) {
    boolean[] tempBooleans;
    int iFit = findBestFit(size, lengthsFreeBooleans);
    if (iFit > 0) {
      tempBooleans = freeBooleans[iFit];
    } else {
      tempBooleans = new boolean[size];
    }
    return tempBooleans;
  }

  void freeTempBooleans(boolean[] tempBooleans) {
    for (int i = 0; i < freeBooleans.length; i++)
      if (freeBooleans[i] == tempBooleans) {
        lengthsFreeBooleans[i] = tempBooleans.length;
        return;
      }
    int iFree = findShorter(tempBooleans.length, lengthsFreeBooleans);
    if (iFree >= 0)
      freeBooleans[iFree] = tempBooleans;
  }
*/
  ////////////////////////////////////////////////////////////////
  // temp bytes
  ////////////////////////////////////////////////////////////////
  private final static int freeBytesSize = 2;
  private final int[] lengthsFreeBytes = new int[freeBytesSize];
  private final byte[][] freeBytes = new byte[freeBytesSize][];

  public byte[] allocTempBytes(int size) {
    byte[] tempBytes;
    int iFit = findBestFit(size, lengthsFreeBytes);
    if (iFit > 0) {
      tempBytes = freeBytes[iFit];
    } else {
      tempBytes = new byte[size];
    }
    return tempBytes;
  }

  public void freeTempBytes(byte[] tempBytes) {
    for (int i = 0; i < freeBytes.length; i++)
      if (freeBytes[i] == tempBytes) {
        lengthsFreeBytes[i] = tempBytes.length;
        return;
      }
    int iFree = findShorter(tempBytes.length, lengthsFreeBytes);
    if (iFree >= 0)
      freeBytes[iFree] = tempBytes;
  }
}
