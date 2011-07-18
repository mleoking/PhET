/* $RCSfile$
 * $Author: nicove $
 * $Date: 2006-08-30 13:20:20 -0500 (Wed, 30 Aug 2006) $
 * $Revision: 5447 $
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
import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Jaguar reader tested for the two samples files in CVS. Both
 * these files were created with Jaguar version 4.0, release 20.
 */
public class JaguarReader extends MOReader {

  private int moCount = 0;
  private float lumoEnergy = Float.MAX_VALUE;

  /**
   * @return true if need to read new line
   * @throws Exception
   * 
   */
  @Override
  protected boolean checkLine() throws Exception {
    if (line.startsWith(" Input geometry:")
        || line.startsWith(" Symmetrized geometry:")
        || line.startsWith("  final geometry:")) {
      readAtoms();
      return true;
    }
    if (line.startsWith("  Atomic charges from electrostatic potential:")) {
      readCharges();
      return true;
    }
    if (line.startsWith("  number of basis functions....")) {
      moCount = parseInt(line.substring(32).trim());
      return true;
    }
    if (line.startsWith("  basis set:")) {
      moData.put("energyUnits", "");
      moData.put("calculationType", calculationType = line.substring(13).trim());
      return true;
    }
    if (line.indexOf("Shell information") >= 0) {
      readBasis();
      return true;
    }
    if (line.indexOf("Normalized coefficients") >= 0) {
      readBasisNormalized();
      return true;
    }
    if (line.startsWith(" LUMO energy:")) {
      lumoEnergy = parseFloat(line.substring(13));
      return true;
    }
    if (line.indexOf("final wvfn") >= 0) {
      readJaguarMolecularOrbitals();
      return true;
    }
    if (line.startsWith("  harmonic frequencies in")) {
      readFrequencies();
      continuing = false;
      return false;
    }
    return checkNboLine();
  }

  private void readAtoms() throws Exception {
    // we only take the last set of atoms before the frequencies
    atomSetCollection.discardPreviousAtoms();
    // start parsing the atoms
    discardLines(2);
    int atomCount = 0;
    while (readLine() != null && line.length() >= 60 && line.charAt(2) != ' ') {
      String[] tokens = getTokens();
      String atomName = tokens[0];
      float x = parseFloat(tokens[1]);
      float y = parseFloat(tokens[2]);
      float z = parseFloat(tokens[3]);
      if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z)
          || atomName.length() < 2)
        return;
      String elementSymbol;
      char ch2 = atomName.charAt(1);
      if (ch2 >= 'a' && ch2 <= 'z')
        elementSymbol = atomName.substring(0, 2);
      else
        elementSymbol = atomName.substring(0, 1);
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementSymbol = elementSymbol;
      atom.atomName = atomName;
      setAtomCoord(atom, x, y, z);
      atomCount++;
    }
  }

  /*

 Atom       C1           H2           H3           H4           O5      
 Charge    0.24969      0.04332     -0.02466     -0.02466     -0.65455
   
 Atom       H6      
 Charge    0.41085
  
   */
  private void readCharges() throws Exception {
   int iAtom = 0;
    while (readLine() != null && line.indexOf("sum") < 0) {
      if (line.indexOf("Charge") < 0)
        continue;
      String[] tokens = getTokens();
      for (int i = 1; i < tokens.length; i++)
        atomSetCollection.getAtom(iAtom++).partialCharge = parseFloat(tokens[i]);
    }
  }

  /*

   Gaussian Functions - Shell information
   
   s    j
   h    c  i       n
   e    o  s       f
   l    n  h       s
   atom       l    t  l  l    h          z              coef            rcoef
   --------    ---  --- -- --  ---     ----------      ----------       ---------
   C1          1    3  0  1    0      71.6168373       0.1543290       2.7078144
   C1          2   -1  0  1    0      13.0450963       0.5353281       2.6188802
   C1          3   -1  0  1    0       3.5305122       0.4446345       0.8161906
   C1          4    2  2  1    1       2.9412494      -0.2956454      -0.4732386
   C1          5   -4  2  1    1       0.6834831       1.1815287       0.6329949
   C1          6    2 -1  2    2       2.9412494       0.2213487       1.2152952
   C1          7   -6 -1  2    2       0.6834831       0.8627064       0.7642102
   C1          8    1  1  1    1       0.2222899       1.0000000       0.2307278
   C1          9    1 -1  2    2       0.2222899       1.0000000       0.2175654


   */
  //private final static float ROOT3 = 1.73205080756887729f;
  private void readBasis() throws Exception {
    String lastAtom = "";
    int iAtom = -1;
    int[][] sdata = new int[moCount][4];
    List<float[]>[] sgdata = ArrayUtil.createArrayOfArrayList(moCount);
    String[] tokens;
    gaussianCount = 0;

    // trouble is that these can be out of order!

    discardLinesUntilContains("--------");
    while (readLine() != null && (tokens = getTokens()).length == 9) {
      int jCont = parseInt(tokens[2]);
      if (jCont > 0) {
        if (!tokens[0].equals(lastAtom))
          iAtom++;
        lastAtom = tokens[0];
        int iFunc = parseInt(tokens[5]);
        int iType = parseInt(tokens[4]);
        if (iType <= 2)
          iType--; // s,p --> 0,1 because SP is 2
        if (sgdata[iFunc] == null) {
          sdata[iFunc][0] = iAtom;
          sdata[iFunc][1] = iType;
          sdata[iFunc][2] = 0; //pointer
          sdata[iFunc][3] = 0; //count
          sgdata[iFunc] = new ArrayList<float[]>();
        }
        float factor = 1;//(iType == 3 ? 1.73205080756887729f : 1);
        //System.out.println("slater: " + iAtom + " " + iType + " " + gaussianCount + " " + nGaussians);
        sgdata[iFunc].add(new float[] { parseFloat(tokens[6]),
            parseFloat(tokens[8]) * factor });
        gaussianCount += jCont;
        for (int i = jCont - 1; --i >= 0;) {
          tokens = getTokens(readLine());
          sgdata[iFunc].add(new float[] { parseFloat(tokens[6]),
              parseFloat(tokens[8]) * factor });
        }
      }
    }
    float[][] garray = new float[gaussianCount][];
    List<int[]> sarray = new ArrayList<int[]>();
    gaussianCount = 0;
    for (int i = 0; i < moCount; i++)
      if (sgdata[i] != null) {
        int n = sgdata[i].size();
        sdata[i][2] = gaussianCount;
        sdata[i][3] = n;
        for (int j = 0; j < n; j++) {
          garray[gaussianCount++] = sgdata[i].get(j);
        }
        sarray.add(sdata[i]);
      }
    moData.put("shells", sarray);
    moData.put("gaussians", garray);
    if (Logger.debugging) {
      Logger.debug(sarray.size() + " slater shells read");
      Logger.debug(gaussianCount + " gaussian primitives read");
    }
  }
  
  
/*
   Gaussian Functions - Normalized coefficients
   
   s
   h    t
   e    y
   l    p    f
   atom       l    e    n          z          rcoef        rmfac     rcoef*rmfac
   --------    ---  ---  ---     ---------    ----------   ----------  -----------
   C1          1    S    1    3047.524880     0.536345     1.000000     0.536345
   C1          2    S    1     457.369518     0.989452     1.000000     0.989452
   C1          3    S    1     103.948685     1.597283     1.000000     1.597283
   C1          4    S    1      29.210155     2.079187     1.000000     2.079187
   C1          5    S    1       9.286663     1.774174     1.000000     1.774174
   C1          6    S    1       3.163927     0.612580     1.000000     0.612580
   C1          7    S    2       7.868272    -0.399556     1.000000    -0.399556
   C1          8    S    2       1.881289    -0.184155     1.000000    -0.184155
   C1          9    S    2       0.544249     0.516390     1.000000     0.516390
   C1         10    X    3       7.868272     1.296082     1.000000     1.296082
                    Y    4                                 1.000000     1.296082
                    Z    5                                 1.000000     1.296082
   C1         11    X    3       1.881289     0.993754     1.000000     0.993754

   */
  private void readBasisNormalized() throws Exception {
    
    //TODO don't know what this is about yet -- Bob Hanson
/*    
    if (true)
      return;
    String lastAtom = "";
    int iAtom = -1;
    discardLinesUntilContains("--------");
    float z = 0;
    float rCoef = 0;
    String id;

    while (readLine() != null && line.length() > 3) {
      String[] tokens = getTokens();
      if (tokens.length == 4) { //continuation
        id = tokens[0];
      } else {
        if (!tokens[0].equals(lastAtom))
          iAtom++;
        lastAtom = tokens[0];
        id = tokens[2];
        z = parseFloat(tokens[4]);
        rCoef = parseFloat(tokens[5]);
      }
    }
*/
  }

  /*

   Occupied + virtual Orbitals- final wvfn
   
   ***************************************** 
   
   
   1         2         3         4         5
   eigenvalues-            -20.56138 -11.27642  -1.35330  -0.91170  -0.68016
   1 C1               S    0.00002   0.99583  -0.07294   0.17630   0.01918
   2 C1               S   -0.00028   0.02695   0.13608  -0.34726  -0.03173
   3 C1               X   -0.00014   0.00018   0.02808   0.00925   0.23168
   4 C1               Y    0.00033  -0.00073  -0.09792  -0.06147  -0.12659
   5 C1               Z    0.00003  -0.00013  -0.01570  -0.01416   0.08005

   
   
   */

  private void readJaguarMolecularOrbitals() throws Exception {
    String[][] dataBlock = new String[moCount][];
    readLine();
    readLine();
    readLine();
    int nMo = 0;
    while (line != null) {
      readLine();
      readLine();
      readLine();
      if (line == null || line.indexOf("eigenvalues-") < 0)
        break;
      String[] eigenValues = getTokens();
      int n = eigenValues.length - 1;
      fillDataBlock(dataBlock);
      for (int iOrb = 0; iOrb < n; iOrb++) {
        float[] coefs = new float[moCount];
        Map<String, Object> mo = new Hashtable<String, Object>();
        float energy = parseFloat(eigenValues[iOrb + 1]);
        mo.put("energy", new Float(energy));
        if (Math.abs(energy - lumoEnergy) < 0.0001) {
          moData.put("HOMO", Integer.valueOf(nMo));
          lumoEnergy = Float.MAX_VALUE;
        }
        nMo++;
        for (int i = 0, pt =0; i < moCount; i++) {
          String type = dataBlock[i][2];
          //TODO: sort these?
          char ch = type.charAt(0);
          if (!isQuantumBasisSupported(ch))
            continue;
          coefs[pt] = parseFloat(dataBlock[i][iOrb + 3]);
          pt++;
        }
        mo.put("coefficients", coefs);
        setMO(mo);
      }
    }
    moData.put("mos", orbitals);
    setMOData(moData);
  }

  /* A block without symmetry, looks like:

   harmonic frequencies in cm**-1, IR intensities in km/mol, and normal modes:
   
   frequencies  1350.52  1354.79  1354.91  1574.28  1577.58  3047.10  3165.57
   intensities    14.07    13.95    13.92     0.00     0.00     0.00    25.19
   C1   X     0.00280 -0.11431  0.01076 -0.00008 -0.00001 -0.00028 -0.00406
   C1   Y    -0.00528  0.01062  0.11423 -0.00015 -0.00001 -0.00038  0.00850
   C1   Z     0.11479  0.00330  0.00502 -0.00006  0.00000  0.00007 -0.08748
   
   With symmetry:
   
   harmonic frequencies in cm**-1, IR intensities in km/mol, and normal modes:
   
   frequencies  1352.05  1352.11  1352.16  1574.91  1574.92  3046.33  3164.52
   symmetries   B3       B1       B3       A        A        A        B1      
   intensities    14.01    14.00    14.00     0.00     0.00     0.00    25.06
   C1   X     0.08399 -0.00233 -0.07841  0.00000  0.00000  0.00000 -0.01133
   C1   Y     0.06983 -0.05009  0.07631 -0.00001  0.00000  0.00000 -0.00283
   C1   Z     0.03571  0.10341  0.03519  0.00001  0.00000  0.00001 -0.08724
   */

  private void readFrequencies() throws Exception {
    int atomCount = atomSetCollection.getLastAtomSetAtomCount();
    discardLinesUntilStartsWith("  frequencies ");
    while (line != null && line.startsWith("  frequencies ")) {
      int iAtom0 = atomSetCollection.getAtomCount();
      String[] frequencies = getTokens();
      int frequencyCount = frequencies.length - 1;
      boolean[] ignore = new boolean[frequencyCount];
      // skip to "intensity" or "force" line
      String[] symmetries = null;
      String[] intensities = null;
      while (line != null && line.charAt(2) != ' ') {
        if (line.indexOf("symmetries") >= 0)
          symmetries = getTokens();
        else if (line.indexOf("intensities") >= 0)
          intensities = getTokens();
        readLine();
      }
      for (int i = 0; i < frequencyCount; i++) {
        ignore[i] = !doGetVibration(++vibrationNumber);
        if (ignore[i]) 
          continue;
        atomSetCollection.cloneFirstAtomSet();
        atomSetCollection.setAtomSetFrequency(null, symmetries == null ? null : symmetries[i + 1], frequencies[i + 1], null);
        if (intensities != null)
          atomSetCollection.setAtomSetProperty("IRIntensity",
              intensities[i + 1] + " km/mol");
      }
      haveLine = true;
      fillFrequencyData(iAtom0, atomCount, atomCount, ignore, false, 0, 0, null);
      readLine();
      readLine();
    }
  }
  
  private boolean haveLine;

  @Override
  public String readLine() throws Exception {
    if (!haveLine)
      return super.readLine();
    haveLine = false;
    return line;
  }
}
