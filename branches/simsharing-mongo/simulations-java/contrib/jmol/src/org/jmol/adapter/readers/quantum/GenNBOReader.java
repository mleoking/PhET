/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-09-12 00:46:22 -0500 (Tue, 12 Sep 2006) $
 * $Revision: 5501 $
 *
 * Copyright (C) 2004-2005  The Jmol Development Team
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

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * NBO file nn reader will pull in other files as necessary
 * 
 * acknowledgments: Grange Hermitage, Frank Weinhold
 * 
 * @author hansonr
 **/

/*
 * NBO output analysis is based on
 * 
 * ********************************** NBO 5.G
 * *********************************** N A T U R A L A T O M I C O R B I T A L A
 * N D N A T U R A L B O N D O R B I T A L A N A L Y S I S
 * ***********************
 * ******************************************************* (c) Copyright
 * 1996-2004 Board of Regents of the University of Wisconsin System on behalf of
 * the Theoretical Chemistry Institute. All Rights Reserved.
 * 
 * Cite this program as:
 * 
 * NBO 5.G. E. D. Glendening, J. K. Badenhoop, A. E. Reed, J. E. Carpenter, J.
 * A. Bohmann, C. M. Morales, and F. Weinhold (Theoretical Chemistry Institute,
 * University of Wisconsin, Madison, WI, 2001); http://www.chem.wisc.edu/~nbo5
 * 
 * /AONBO / : Print the AO to NBO transformation
 */
public class GenNBOReader extends MOReader {

  private boolean isOutputFile;
  private String moType = "";
  private int nOrbitals0;

  
  @Override
  protected void initializeReader() throws Exception {
    /*
     * molname.31 AO 
     * molname.32 PNAO 
     * molname.33 NAO 
     * molname.34 PNHO 
     * molname.35 NHO
     * molname.36 PNBO 
     * molname.37 NBO 
     * molname.38 PNLMO 
     * molname.39 NLMO 
     * molname.40 MO 
     * molname.41 AO density matrix 
     * molname.46 Basis label file
     */
    String line1 = readLine().trim();
    readLine();
    isOutputFile = (line.indexOf("***") >= 0);
    boolean isOK;
    if (isOutputFile) {
      isOK = readFile31();
      super.initializeReader();
      // keep going -- we need to read the file using MOReader
      moData.put("isNormalized", Boolean.TRUE);
    } else if (line.indexOf("s in the AO basis:") >= 0) {
      moType = line.substring(1, line.indexOf("s"));
      atomSetCollection.setCollectionName(line1 + ": " + moType + "s");
      isOK = readFile31();
    } else {
      moType = "AO";
      atomSetCollection.setCollectionName(line1 + ": " + moType + "s");
      isOK = readData31(line1, line);
    }
    if (!isOK)
      Logger.error("Unimplemented shell type -- no orbitals avaliable: " + line);
    if (isOutputFile) 
      return;
    if (isOK) {
      readMOs();
    }
    continuing = false;
  }

  private void readMOs() throws Exception {
    nOrbitals0 = orbitals.size();
    readFile46();
    readOrbitalData(!moType.equals("AO"));
    setMOData(false);
    moData.put("isNormalized", Boolean.TRUE);
  }

  @Override
  protected boolean checkLine() throws Exception {
    // for .nbo only
    if (line.indexOf("SECOND ORDER PERTURBATION THEORY ANALYSIS") >= 0 && !orbitalsRead) {
      moType = "NBO";
      String data = getFileData(".37");
      if (data == null) {
        moType = "PNBO";
        data = getFileData(".36");
        if (data == null)
          return true;
      }
      BufferedReader readerSave = reader;
      reader = new BufferedReader(new StringReader(data));
      readLine();
      readLine();
      readMOs();
      reader = readerSave;
      orbitalsRead = false;
      return true;
    }
    return checkNboLine();
  }

  private String getFileData(String ext) throws Exception {
    String fileName = (String) htParams.get("fullPathName");
    int pt = fileName.lastIndexOf(".");
    if (pt < 0)
      pt = fileName.length();
    fileName = fileName.substring(0, pt) + ext;
    String data = viewer.getFileAsString(fileName);
    if (data.length() == 0 || data.indexOf("java.io.FileNotFound") >= 0)
      throw new Exception(" supplemental file " + fileName + " was not found");
    return data;
  }

  /*
   * 14_a Basis set information needed for plotting orbitals
   * ---------------------------------------------------------------------------
   * 36 90 162
   * ---------------------------------------------------------------------------
   * 6 -2.992884000 -1.750577000 1.960024000 6 -2.378528000 -1.339374000
   * 0.620578000
   */

  private boolean readFile31() throws Exception {
    String data = getFileData(".31");
    BufferedReader readerSave = reader;
    reader = new BufferedReader(new StringReader(data));
    if (!readData31(null, null))
      return false;
    reader = readerSave;
    return true;
  }

  private void readFile46() throws Exception {
    String data = getFileData(".46");
    BufferedReader readerSave = reader;
    reader = new BufferedReader(new StringReader(data));
    readData46();
    reader = readerSave;
  }

  private static String P_LIST =  "101   102   103";
  // GenNBO may be 103 101 102 
  
  private static String SP_LIST = "1     101   102   103";

  private static String DS_LIST = "255   252   253   254   251"; 
  // GenNBO is 251 252 253 254 255 
  //   for     Dxy Dxz Dyz Dx2-y2 D2z2-x2-y2
  // org.jmol.quantum.MOCalculation expects 
  //   d2z^2-x2-y2, dxz, dyz, dx2-y2, dxy

  private static String DC_LIST = "201   204   206   202   203   205";
  // GenNBO is 201 202 203 204 205 206 
  //       for Dxx Dxy Dxz Dyy Dyz Dzz
  // org.jmol.quantum.MOCalculation expects 
  //      Dxx Dyy Dzz Dxy Dxz Dyz

  private static String FS_LIST = "351   352   353   354   355   356   357";
  // GenNBO is 351 352 353 354 355 356 357
  //        as 2z3-3x2z-3y2z
  //               4xz2-x3-xy2
  //                   4yz2-x2y-y3
  //                           x2z-y2z
  //                               xyz
  //                                  x3-3xy2
  //                                     3x2y-y3
  // org.jmol.quantum.MOCalculation expects the same
  private static String FC_LIST = "301   307   310   304   302   303   306   309   308   305";
  // GenNBO is 301 302 303 304 305 306 307 308 309 310
  //       for xxx xxy xxz xyy xyz xzz yyy yyz yzz zzz
  // org.jmol.quantum.MOCalculation expects
  //           xxx yyy zzz xyy xxy xxz xzz yzz yyz xyz
  //           301 307 310 304 302 303 306 309 308 305

  

  private boolean readData31(String line1, String line2) throws Exception {
    if (line1 == null)
      line1 = readLine();
    if (line2 == null)
      line2 = readLine();

    // read atomCount, shellCount, and gaussianCount
    readLine(); // ----------
    String[] tokens = getTokens(readLine());
    int atomCount = parseInt(tokens[0]);
    shellCount = parseInt(tokens[1]);
    gaussianCount = parseInt(tokens[2]);

    // read atom types and positions
    readLine(); // ----------
    atomSetCollection.newAtomSet();
    atomSetCollection.setAtomSetName(moType + "s: " + line1.trim());
    for (int i = 0; i < atomCount; i++) {
      tokens = getTokens(readLine());
      int z = parseInt(tokens[0]);
      if (z < 0) // dummy atom
        continue;
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementNumber = (short) z;
      setAtomCoord(atom, parseFloat(tokens[1]), parseFloat(tokens[2]),
          parseFloat(tokens[3]));
    }

    // read basis functions
    shells = new ArrayList<int[]>();
    gaussians = new float[gaussianCount][];
    for (int i = 0; i < gaussianCount; i++)
      gaussians[i] = new float[6];
    readLine(); // ----------
    nOrbitals = 0;
    for (int i = 0; i < shellCount; i++) {
      tokens = getTokens(readLine());
      int[] slater = new int[4];
      slater[0] = parseInt(tokens[0]) - 1; // atom pointer; 1-based
      int n = parseInt(tokens[1]);
      nOrbitals += n;
      line = readLine().trim();
      switch (n) {
      case 1:
        slater[1] = JmolAdapter.SHELL_S;
        break;
      case 3:
        if (!getDFMap(line, JmolAdapter.SHELL_P, P_LIST, 3))
          return false;
        slater[1] = JmolAdapter.SHELL_P;
        break;
      case 4:
        if (!getDFMap(line, JmolAdapter.SHELL_SP, SP_LIST, 1))
          return false;
        slater[1] = JmolAdapter.SHELL_SP;
        break;        
      case 5:
        if (!getDFMap(line, JmolAdapter.SHELL_D_SPHERICAL, DS_LIST, 3))
          return false;
        slater[1] = JmolAdapter.SHELL_D_SPHERICAL;
        break;
      case 6:
        if (!getDFMap(line, JmolAdapter.SHELL_D_CARTESIAN, DC_LIST, 3))
          return false;
        slater[1] = JmolAdapter.SHELL_D_CARTESIAN;
        break;
      case 7:
        if (!getDFMap(line, JmolAdapter.SHELL_F_SPHERICAL, FS_LIST, 3))
          return false;
        slater[1] = JmolAdapter.SHELL_F_SPHERICAL;
        break;
      case 10:
        if (!getDFMap(line, JmolAdapter.SHELL_F_CARTESIAN, FC_LIST, 3))
          return false;
        slater[1] = JmolAdapter.SHELL_F_CARTESIAN;
        break;
      }
      slater[2] = parseInt(tokens[2]) - 1; // gaussian list pointer
      slater[3] = parseInt(tokens[3]);     // number of gaussians
      shells.add(slater);
    }

    // get alphas and exponents

    for (int j = 0; j < 5; j++) {
      readLine();
      float[] temp = new float[gaussianCount];
      fillFloatArray(temp, null, 0);
      for (int i = 0; i < gaussianCount; i++) {
        gaussians[i][j] = temp[i];
        if (j > 1)
          gaussians[i][5] += temp[i];
      }
    }
    // GenNBO lists S, P, D, F, G orbital coefficients separately
    // we need all of them in [1] if [1] is zero (not S or SP)
    for (int i = 0; i < gaussianCount; i++) {
      if (gaussians[i][1] == 0)
        gaussians[i][1] = gaussians[i][5];
    }
    if (Logger.debugging) {
      Logger.debug(shells.size() + " slater shells read");
      Logger.debug(gaussians.length + " gaussian primitives read");
    }
    return true;
  }

  private boolean readData46() throws Exception {
    String[] tokens = getTokens(readLine());
    int ipt = 1;
    if (tokens[1].equals("ALPHA")) {
      ipt = 2;
      if (haveNboOrbitals) {
        tokens = getTokens(discardLinesUntilContains("BETA"));
        alphaBeta = "beta";
      } else {
        alphaBeta = "alpha";
        haveNboOrbitals = true;
      }
    }
    if (parseInt(tokens[ipt]) != nOrbitals) {
      Logger.error("file 46 number of orbitals does not match nOrbitals: " + nOrbitals);
      return false;
    }
    String ntype = null;
    if (moType.equals("AO"))
      ntype = "AO";
    else if (moType.indexOf("NHO") >= 0)
      ntype = "NHO";
    else if (moType.indexOf("NBO") >= 0)
      ntype = "NBO";
    else if (moType.indexOf("NAO") >= 0)
      ntype = "NAO";
    else if (moType.indexOf("MO") >= 0)
      ntype = "MO";
    if (ntype == null) {
      Logger.error("uninterpretable type " + moType);
      return false;
    }
    if (!ntype.equals("AO"))
      discardLinesUntilContains(ntype.equals("MO") ? "NBO" : ntype);
    StringBuffer sb = new StringBuffer();
    while (readLine() != null && line.indexOf("O    ") < 0 && line.indexOf("ALPHA") < 0 && line.indexOf("BETA") < 0)
      sb.append(line);
    sb.append(' ');
    String data = sb.toString();
    int n = data.length() - 1;
    sb = new StringBuffer();
    for (int i = 0; i < n; i++) {
      char c = data.charAt(i);
      switch (c) {
      case '(':
      case '-':
        if (data.charAt(i + 1) == ' ')
          i++;
        break;
      case ' ':
        if (Character.isDigit(data.charAt(i + 1)) || data.charAt(i + 1) == '(')
          continue;
        break;
      }
      sb.append(c);
    }
    Logger.info(sb.toString());
    tokens = getTokens(sb.toString());
    for (int i = 0; i < tokens.length; i++) {
      Map<String, Object> mo = new Hashtable<String, Object>();
      setMO(mo);
    }
    if (ntype.equals("MO"))
      return true; // no labels here
    for (int i = 0; i < tokens.length; i++) {
      Map<String, Object> mo = orbitals.get(i + nOrbitals0);
      String type = tokens[i];
      mo.put("type", moType + " " + type);
      // TODO: does not account for SOMO
      mo.put("occupancy", new Float(type.indexOf("*") >= 0 ? 0 : 2));
    }
    return true;
  }

  private void readOrbitalData(boolean isMO) throws Exception {
    int nAOs = nOrbitals;
    nOrbitals = orbitals.size();
    line = null;
    for (int i = nOrbitals0; i < nOrbitals; i++) {
      Map<String, Object> mo = orbitals.get(i);
      float[] coefs = new float[nAOs];
      mo.put("coefficients", coefs);
      if (isMO) {
        if (line == null) {
          while (readLine() != null && Float.isNaN(parseFloat(line))) {
            // skip lines            
          }
        } else {
          line = null;
        }
        fillFloatArray(coefs, line, 0);
        line = null;
        //setMOType(mo, i);
      } else {
        coefs[i] = 1;
      }
    }
    if (moType.equals("NBO")) {
      float[] occupancies = new float[nOrbitals - nOrbitals0];
      fillFloatArray(occupancies, null, 0);   
      for (int i = nOrbitals0; i < nOrbitals; i++) {
        Map<String, Object> mo = orbitals.get(i);
        mo.put("occupancy", Float.valueOf((int) (occupancies[i - nOrbitals0] + 0.2f)));
      }
    }
  }
}
