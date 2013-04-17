/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-03-18 15:59:33 -0600 (Sat, 18 Mar 2006) $
 * $Revision: 4652 $
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
package org.jmol.adapter.readers.quantum;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;
import org.jmol.util.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author hansonr <hansonr@stolaf.edu>
 */
abstract class BasisFunctionReader extends AtomSetCollectionReader {

  protected List<int[]> shells;

  protected Map<String, Object> moData = new Hashtable<String, Object>();
  protected List<Map<String, Object>> orbitals = new ArrayList<Map<String, Object>>();
  protected int nOrbitals = 0;
  protected boolean ignoreMOs = false;
  protected String alphaBeta = "";

  protected int[][] dfCoefMaps;
  
  private String[] filterTokens;
  private boolean filterIsNot; 

  protected boolean filterMO() {
    boolean isHeader = (line.indexOf('\n') == 0);
    if (!isHeader && !readMolecularOrbitals)
      return false;
    if (filter == null)
      return true;
    boolean isOK = true;
    int nOK = 0;
    line += " " + alphaBeta;
    String ucline = line.toUpperCase();
    if (filterTokens == null) {
      filterIsNot = (filter.indexOf("!") >= 0);
      filterTokens = getTokens(filter.replace('!', ' ').replace(',', ' ')
          .replace(';', ' '));
    }
    for (int i = 0; i < filterTokens.length; i++)
      if (ucline.indexOf(filterTokens[i]) >= 0) {
        if (!filterIsNot) {
          nOK = filterTokens.length;
          break;
        }
      } else if (filterIsNot) {
        nOK++;
      }
    isOK = (nOK == filterTokens.length);
    if (!isHeader)
      Logger.info("filter MOs: " + isOK + " for \"" + line + "\"");
    return isOK;
  }

  protected void setMO(Map<String, Object> mo) {
    if (dfCoefMaps != null)
      mo.put("dfCoefMaps", dfCoefMaps);
    orbitals.add(mo);
  }
  
  // Jmol's ordering is based on GAUSSIAN
  
  // We don't modify the coefficients at read time, only create a 
  // map to send to MOCalculation. 

  // DS: org.jmol.quantum.MOCalculation expects 
  //   d2z^2-x2-y2, dxz, dyz, dx2-y2, dxy
  
  // DC: org.jmol.quantum.MOCalculation expects 
  //      Dxx Dyy Dzz Dxy Dxz Dyz
  
  // FS: org.jmol.quantum.MOCalculation expects
  //        as 2z3-3x2z-3y2z
  //               4xz2-x3-xy2
  //                   4yz2-x2y-y3
  //                           x2z-y2z
  //                               xyz
  //                                  x3-3xy2
  //                                     3x2y-y3

  // FC: org.jmol.quantum.MOCalculation expects
  //           xxx yyy zzz xyy xxy xxz xzz yzz yyz xyz

  // These strings are the equivalents found in the file in Jmol order.
  // DO NOT CHANGE THESE. They are in the order the MOCalculate expects. 
  // Subclassed readers can make their own to match. 
    
  
  protected static String CANONICAL_DC_LIST = "DXX   DYY   DZZ   DXY   DXZ   DYZ";
  protected static String CANONICAL_FC_LIST = "XXX   YYY   ZZZ   XYY   XXY   XXZ   XZZ   YZZ   YYZ   XYZ";
  
  protected static String CANONICAL_DS_LIST = "d0    d1+   d1-   d2+   d2-";
  protected static String CANONICAL_FS_LIST = "f0    f1+   f1-   f2+   f2-   f3+   f3-";

  
  protected boolean isQuantumBasisSupported(char ch) {
    return (JmolAdapter.SUPPORTED_BASIS_FUNCTIONS.indexOf(Character.toUpperCase(ch)) >= 0);
  }


  /**
   * 
   * finds the position in the Jmol-required list of function types. This list is
   * reader-dependent. 
   * 
   * @param fileList 
   * @param shellType 
   * @param jmolList 
   * @param minLength 
   * @return            true if successful
   * 
   */
  protected boolean getDFMap(String fileList, int shellType, String jmolList, int minLength) {
   if (fileList.equals(jmolList))
      return true;

    
    // say we had line = "251   252   253   254   255"  i  points here
    // Jmol expects list "255   252   253   254   251"  pt points here
    // we need an array that reads
    //                    [4     0     0     0    -4]
    // meaning add that number to the pointer for this coef.
    if (dfCoefMaps == null) {
      dfCoefMaps = new int[][] {new int[1], new int[3], new int[4],
          new int[5], new int[6], new int[7], new int[10]};
    }
    String[] tokens = getTokens(fileList);
    boolean isOK = true;
    for (int i = 0; i < dfCoefMaps[shellType].length && isOK; i++) {
      String key = tokens[i];
      if (key.length() >= minLength) {
        int pt = jmolList.indexOf(key);
        if (pt >= 0) {
          pt /= 6;
          dfCoefMaps[shellType][pt] = i - pt;
          continue;
        }
      }
      isOK = false;
    }
    if (!isOK) {
      Logger.error("Disabling orbitals of type " + shellType + " -- Cannot read orbital order for: " + fileList + "\n expecting: " + jmolList);
      dfCoefMaps[shellType][0] = Integer.MIN_VALUE;
      //throw new NullPointerException("TESTING MO READER");
    }
    return isOK;
  }

  final protected static String canonicalizeQuantumSubshellTag(String tag) {
    char firstChar = tag.charAt(0);
    if (firstChar == 'X' || firstChar == 'Y' || firstChar == 'Z') {
      char[] sorted = tag.toCharArray();
      Arrays.sort(sorted);
      return new String(sorted);
    } 
    return tag;
  }
  
  protected void fixSlaterTypes(int typeOld, int typeNew) {
    // in certain cases we assume Cartesian and then later have to 
    // correct that. 
    for (int i = shells.size(); --i >=0 ;) {
      int[] slater = shells.get(i);
      if (slater[1] == typeOld)
        slater[1] = typeNew;
    }
  }


}
