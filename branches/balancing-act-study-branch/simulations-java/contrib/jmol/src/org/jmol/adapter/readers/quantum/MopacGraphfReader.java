/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-08-27 21:07:49 -0500 (Sun, 27 Aug 2006) $
 * $Revision: 5420 $
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


/**
 * Reads Mopac 2007 GRAPHF output files
 *
 * @author Bob Hanson <hansonr@stolaf.edu>
 * 
 */
public class MopacGraphfReader extends MopacSlaterReader {
    
  private int atomCount;
  private int nCoefficients;
  
  @Override
  protected void initializeReader() {
    alphaBeta = "alpha";
  }
  
  @Override
  protected boolean checkLine() throws Exception {
      readAtoms();
      if (readMolecularOrbitals) {
        readSlaterBasis();
        readMolecularOrbitals(false);
        if (readKeywords())
          readMolecularOrbitals(true);
      }
      continuing = false;
      return false;
  }
    
  private void readAtoms() throws Exception {
    atomSetCollection.newAtomSet();
    atomCount = parseInt(line);
    atomicNumbers = new int[atomCount];
    for (int i = 0; i < atomCount; i++) {
      readLine();
      atomicNumbers[i] = parseInt(line.substring(0, 4));
      Atom atom = atomSetCollection.addNewAtom();
      setAtomCoord(atom, parseFloat(line.substring(4, 17)), 
          parseFloat(line.substring(17, 29)), 
          parseFloat(line.substring(29, 41)));
      if (line.length() > 41)
        atom.partialCharge = parseFloat(line.substring(41));
      atom.elementSymbol = AtomSetCollectionReader.getElementSymbol(atomicNumbers[i]);
      //System.out.println(atom.elementSymbol + " " + atom.x + " " + atom.y + " " + atom.z);
    }
  }
  
  /*
   *  see http://openmopac.net/manual/graph.html
   *  
   Block 1, 1 line: Number of atoms (5 characters), plain text: "MOPAC-Graphical data"
   Block 2, 1 line per atom: Atom number (4 characters), Cartesian coordinates (3 sets of 12 characters)
   Block 3, 1 line per atom: Orbital exponents for "s", "p", and "d" Slater orbitals. (3 sets of 11 characters)
   Block 4, number of orbitals squared, All the molecular orbital coefficients in the order M.O. 1, M.O.2, etc. (5 data per line, 15 characters per datum, FORTRAN format: 5d15.8)
   Block 4, inverse-square-root of overlap matrix, (number of orbitals*(number of orbitals+1))/2.
   4 MOPAC-Graphical data
   
   8    0.0000000   0.0000000   0.0000000
   6    1.2108153   0.0000000   0.0000000
   1    1.7927832   0.9304938   0.0000000
   1    1.7927832  -0.9304938   0.0000000

   
   0         1         2         3         4
   01234567890123456789012345678901234567890
   

   5.4217510  2.2709600  0.0000000
   2.0475580  1.7028410  0.0000000
   1.2686410  0.0000000  0.0000000
   1.2686410  0.0000000  0.0000000
   */

  private void readSlaterBasis() throws Exception {
    /*
     * We have two data structures for each slater, using the WebMO format: 
     * 
     * int[] slaterInfo[] = {iatom, a, b, c, d}
     * float[] slaterData[] = {zeta, coef}
     * 
     * where
     * 
     *  psi = (coef)(x^a)(y^b)(z^c)(r^d)exp(-zeta*r)
     * 
     * except: a == -2 ==> z^2 ==> (coef)(2z^2-x^2-y^2)(r^d)exp(-zeta*r)
     *    and: b == -2 ==> (coef)(x^2-y^2)(r^d)exp(-zeta*r)
     */
    nCoefficients = 0;
    float[] values = new float[3];
    for (int iAtom = 0; iAtom < atomCount; iAtom++) {
      getTokensFloat(readLine(), values, 3);
      int atomicNumber = atomicNumbers[iAtom];
      float zeta;
      if ((zeta = values[0]) != 0) {
        createSphericalSlaterByType(iAtom, atomicNumber, "S", zeta, 1);
      }
      if ((zeta = values[1]) != 0) {
        createSphericalSlaterByType(iAtom, atomicNumber, "Px", zeta, 1);
        createSphericalSlaterByType(iAtom, atomicNumber, "Py", zeta, 1);
        createSphericalSlaterByType(iAtom, atomicNumber, "Pz", zeta, 1);
      }
      if ((zeta = values[2]) != 0) {
        createSphericalSlaterByType(iAtom, atomicNumber, "Dx2-y2", zeta, 1);
        createSphericalSlaterByType(iAtom, atomicNumber, "Dxz", zeta, 1);
        createSphericalSlaterByType(iAtom, atomicNumber, "Dz2", zeta, 1);
        createSphericalSlaterByType(iAtom, atomicNumber, "Dyz", zeta, 1);
        createSphericalSlaterByType(iAtom, atomicNumber, "Dxy", zeta, 1);
      }
    }
    nCoefficients = slaters.size();
    setSlaters(true, false);
  }

  private float[][] invMatrix;
  
  private boolean isNewFormat;
  private List<float[]> orbitalData;
  private List<String> orbitalInfo;
  
  private void readMolecularOrbitals(boolean isBeta) throws Exception {
    
    // read mo coefficients

    //  (5 data per line, 15 characters per datum, FORTRAN format: 5d15.8)

    if (isBeta)
      alphaBeta = "beta";
    float[][] list = null;
    if (readLine() == null)
      return;
    isNewFormat = (line.indexOf("ORBITAL") >= 0);
    if (isNewFormat) {
      orbitalData = new ArrayList<float[]>();
      if (line.length() > 10)
        orbitalInfo = new ArrayList<String>();
    } else {
      list = new float[nCoefficients][nCoefficients];
    }
    for (int iMo = 0; iMo < nCoefficients; iMo++) {
      if (iMo != 0)
        readLine();
      float[] data;
      if (isNewFormat) {
        if (line == null || line.indexOf("ORBITAL") < 0 || line.indexOf("ORBITAL_LIST") >= 0)
          break;
        orbitalData.add(data = new float[nCoefficients]);
        if (orbitalInfo != null)
          orbitalInfo.add(line);
        readLine();
      } else {
        data = list[iMo];
      }
      fillFloatArray(data, line, 15);
    }
    if (invMatrix == null) {
      if (isNewFormat && line.indexOf("MATRIX") < 0)
        readLine();
      // read lower triangle of symmetric inverse sqrt matrix and multiply
      invMatrix = new float[nCoefficients][];
      for (int iMo = 0; iMo < nCoefficients; iMo++)
        fillFloatArray(invMatrix[iMo] = new float[iMo + 1], null, 15);
    }
    nOrbitals = (orbitalData == null ? nCoefficients : orbitalData.size());
    if (orbitalData != null) {
      list = new float[nOrbitals][];
      for (int i = nOrbitals; --i >= 0;)
        list[i] = orbitalData.get(i);
    }
    float[][] list2 = new float[nOrbitals][nCoefficients];
    for (int i = 0; i < nOrbitals; i++)
      for (int j = 0; j < nCoefficients; j++) {
        for (int k = 0; k < nCoefficients; k++)
          list2[i][j] += (list[i][k] * (k >= j ? invMatrix[k][j] : invMatrix[j][k]));
        if (Math.abs(list2[i][j]) < MIN_COEF)
          list2[i][j] = 0;
      }
    /*
     System.out.println("MO coefficients: ");
     for (int i = 0; i < nCoefficients; i++) {
     System.out.print((i + 1) + ": ");
     for (int j = 0; j < nCoefficients; j++)
     System.out.print(" " + list2[i][j]);
     System.out.println();
     }
     */

    // read MO energies and occupancies, and fill "coefficients" element
    if (isNewFormat && orbitalInfo == null && line != null && line.indexOf("ORBITAL_LIST") < 0)
      readLine();
    float[] values = new float[2];
    for (int iMo = 0; iMo < nOrbitals; iMo++) {
      Map<String, Object> mo = new Hashtable<String, Object>();
      if (orbitalInfo != null) {
        line = orbitalInfo.get(iMo);
        String[] tokens = getTokens();
        mo.put("energy", new Float(parseFloat(tokens[3])));
        mo.put("occupancy", new Float(parseFloat(tokens[1])));
      } else if (readLine() != null) {
        getTokensFloat(line, values, 2);
        mo.put("energy", new Float(values[0]));
        mo.put("occupancy", new Float(values[1]));
      }
      mo.put("coefficients", list2[iMo]);
      if (isBeta)
        mo.put("type", "beta");
      line = "\n";
      if (filterMO())
        setMO(mo);
    }
    setMOs("eV");
  }
  
  private boolean readKeywords() throws Exception {
    if (readLine() == null || line.indexOf(" Keywords:") < 0)
      return false;
    moData.put("calculationType", calculationType = line.substring(11).trim());
    boolean isUHF = (line.indexOf("UHF") >= 0);
    if (isUHF) {
      for (int i = orbitals.size(); --i >= 0;) {
        orbitals.get(i).put("type", "alpha");
      }
    }
    return isUHF;
  }
}
