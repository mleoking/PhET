/* $RCSfile: ADFReader.java,v $
 * $Author: egonw $
 * $Date: 2004/02/23 08:52:55 $
 * $Revision: 1.3.2.4 $
 *
 * Copyright (C) 2002-2004  The Jmol Development Team
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
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.jmol.adapter.readers.quantum;

import org.jmol.adapter.smarter.*;
import org.jmol.quantum.SlaterData;
import org.jmol.util.Logger;

import java.util.Hashtable;
import java.util.Map;

/**
 * A reader for Dgrid BASISFILE data. http://www.scm.com/News/DGrid.html
 * http://www.scm.com/Doc/Doc2009.01/ADF/ADFUsersGuide/page430.html
 *
 *
 */
public class DgridReader extends SlaterReader {

  private String title;

  @Override
  protected boolean checkLine() throws Exception {
    if (line.indexOf(":title") == 0) {
      title = readLine().substring(2);
      return true;
    }
    if (line.indexOf("basis:  CARTESIAN  STO") >= 0) {
      readSlaterBasis(); // Cartesians
      return true;
    }
    if (line.indexOf(":atom") == 0) {
      readCoordinates();
      return true;
    }
    if (line.indexOf(" MO  DATA ") >= 0) {
      if (readMolecularOrbitals)
        readMolecularOrbitals();
      return true;
    }
    return true;
  }

  /**
   * Reads a set of coordinates
   *
   * @exception Exception  if an I/O error occurs
   */
  private void readCoordinates() throws Exception {

    /*
     * 
:atom  No.         x          y          z          charge
:---------------------------------------------------------
  N     1:       0.0000     0.0000     4.8054         5.00
  O     2:       0.0000     0.0000     7.0933         6.00
  C     3:       0.0000     0.0000    -0.0761         4.00
  C     4:       0.0000     0.0000     2.6002         4.00
  C     5:       2.3400     0.0000    -1.3762         4.00
     * 
     */
    atomSetCollection.newAtomSet();
    atomSetCollection.setAtomSetName(title);
    discardLinesUntilContains("----");
    while (readLine() != null && !line.startsWith(":-----")) {
      String[] tokens = getTokens();
      if (tokens.length < 5)
        break;
      String symbol = tokens[0];
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementSymbol = symbol;
      setAtomCoord(atom, parseFloat(tokens[2]) * ANGSTROMS_PER_BOHR, 
          parseFloat(tokens[3]) * ANGSTROMS_PER_BOHR, 
          parseFloat(tokens[4]) * ANGSTROMS_PER_BOHR);
    }
  }

  Map<String, Float> htExponents = new Hashtable<String, Float>();
  private void readSlaterBasis() throws Exception {
     /*
:                           +--------------------------+
                            :  basis:  CARTESIAN  STO  :
:                           +--------------------------+


: atom  No.       type            exponents and coefficients
:-----------------------------------------------------------------------------------
   N     1         1s    exp:     6.38000000e+00
   N     1         2s    exp:     1.50000000e+00
   N     1         2s    exp:     2.50000000e+00
   N     1         2s    exp:     5.15000000e+00
   N     1         2p    exp:     1.00000000e+00
   N     1         2p    exp:     1.88000000e+00
       */
    discardLinesUntilContains(":-");
    char ch = 'a';    
    while (readLine() != null && line.indexOf(":-") < 0) {
      String atomSymbol = line.substring(3,6).trim();
      String xyz = line.substring(19, 21);
      String code = atomSymbol + xyz;
      if (htExponents.get(code) == null) {
        ch = 'a';
      } else {
        code += "_" + ch++;
      }
      String exp = line.substring(34);
      htExponents.put(code, Float.valueOf(parseFloat(exp)));
    }
  }

  private Map<String, Integer> htFuncMap;
  private void readMolecularOrbitals() throws Exception {
    /*
sym: A1                 1 1s            2 1s            3 1s            4 1s            5 1s         
                        9 1s            6 1s            8 1s            7 1s           10 1s         
                       11 1s            1 2s            1 2s_a          1 2s_b          1 2pz        
                        1 2pz_a         1 2pz_b         1 3dz2          1 3dx2          1 3dy2       
     */
    htFuncMap = new Hashtable<String, Integer>();
    discardLines(3);
    while (line != null && line.indexOf(":") != 0) {
      discardLinesUntilContains("sym: ");
      String symmetry = line.substring(4, 10).trim();
      if (symmetry.indexOf("_FC") >= 0)
        break;
      StringBuffer data = new StringBuffer();
      data.append(line.substring(15));
      while (readLine() != null && line.length() >= 15)
        data.append(line);
      String[] tokens = getTokens(data.toString());
      int nFuncs = tokens.length / 2;
      int[] ptSlater = new int[nFuncs];
      Atom[] atoms = atomSetCollection.getAtoms();
      for (int i = 0, pt = 0; i < tokens.length;) {
        int iAtom = parseInt(tokens[i++]) - 1;
        String code = tokens[i++];
        String key = iAtom + "_" + code;
        if (htFuncMap.containsKey(key)) {
          ptSlater[pt++] = htFuncMap.get(key).intValue();
        } else {
          int n = slaters.size();
          ptSlater[pt++] = n;
          htFuncMap.put(key, Integer.valueOf(n));
          //System.out.println(code + " " + key);
          addSlater(createSlaterData(iAtom, atoms[iAtom].elementSymbol, code), n);
        }
      }
      discardLinesUntilContains(":-");
      readLine();
      while (line != null && line.length() >= 20) {
        int iOrb = parseInt(line.substring(0, 10));
        float energy = parseFloat(line.substring(10, 20));
        StringBuffer cData = new StringBuffer();
        cData.append(line.substring(20));
        while (readLine() != null && line.length() >= 10) {
          if (line.charAt(3) != ' ')
            break;
          cData.append(line);
        }
        float[] list = new float[slaters.size()];
        tokens = getTokens(cData.toString());
        if (tokens.length != nFuncs)
          Logger
              .error("DgridReader: number of coefficients (" + tokens.length + ") does not equal number of functions (" + nFuncs + ")");
        for (int i = 0; i < tokens.length; i++) {
          int pt = ptSlater[i];
          list[pt] = parseFloat(tokens[i]);
        }
        Map<String, Object> mo = new Hashtable<String, Object>();
        mo.put("energy", new Float(energy));
        mo.put("coefficients", list);
        mo.put("symmetry", symmetry + "_" + iOrb);
        setMO(mo);
        //System.out.println(orbitals.size() + " " + symmetry + "_" + iOrb);
      }
    }

    /*
:                        +------------+
                         | OCCUPATION |
:                        +------------+



:------------------------------------------------------------
:  #  symmetry         orb          ALPHA            BETA
:------------------------------------------------------------
   1  A1                1      1.00000000000    1.00000000000
   2  A1                2      1.00000000000    1.00000000000
   3  A1                3      1.00000000000    1.00000000000
     */
    discardLinesUntilContains(":  #  symmetry");
    readLine();
    for (int i = 0; i < orbitals.size(); i++) {
      readLine();
      float occupancy = parseFloat(line.substring(31, 45)) + parseFloat(line.substring(47, 61));
      orbitals.get(i).put("occupancy", Float.valueOf(occupancy));
    }
    sortOrbitals();
    // System.out.println(Escape.escape(list, false));
    setSlaters(true, true);
    setMOs("eV");
  }

  private SlaterData createSlaterData(int iAtom, String atomSymbol, String xyz) {
    char ch;
    char abc = ' ';
    char type = ' ';
    int exp = 1;
    int el = 0;
    int x = 0;
    int y = 0;
    int z = 0;
    for (int i = xyz.length(); --i >= 0;) {
      switch (ch = xyz.charAt(i)) {
      case '_':
        type = abc;
        break;
      case '1':
      case '2':
      case '3':
      case '4':
        exp = ch - '0';
        break;
      case 'x':
        x = exp;
        el += exp;
        exp = 1;
        break;
      case 'y':
        y = exp;
        el += exp;
        exp = 1;
        break;
      case 'z':
        z = exp;
        el += exp;
        exp = 1;
        break;
      case 's':
      case 'p':
      case 'd':
      case 'f':
      default:
        abc = ch;
      }
    }
    int r = (exp - el - 1);
    String code = atomSymbol + xyz.substring(0, 2);
    if (type != ' ')
      code += "_" + type;
    Float f = htExponents.get(code);
    float zeta = 0;
    if (f == null)
      Logger.error("Exponent for " + code + " not found");
    else
      zeta = f.floatValue();
    //System.out.println("DgridReader [" + iAtom + " " 
        //+ x + " " + y + " " + z + " " + r + "]" + " " + alpha);
    return new SlaterData(iAtom, x, y, z, r, zeta, 1);
  }
}
