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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Vector3f;

import org.jmol.api.JmolAdapter;
import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;

/**
 * Reader for Gaussian 94/98/03/09 output files.
 * 
 * 4/11/2009 -- hansonr -- added NBO support as extension of MOReader
 *
 **/
public class GaussianReader extends MOReader {
  
  /**
   * Word index of atomic number in line with atom coordinates in an
   * orientation block.
   */
  private final static int STD_ORIENTATION_ATOMIC_NUMBER_OFFSET = 1;
  
  /** Calculated energy with units (if possible). */
  private String energyString = "";
  /**
   * Type of energy calculated, e.g., E(RB+HF-PW91).
   */
  private String energyKey = "";
  
  /** The number of the calculation being interpreted. */
  private int calculationNumber = 1;
  
  /**  The scan point, where -1 denotes no scan information. */
  private int scanPoint = -1;
  
  /**
   * The number of equivalent atom sets.
   * <p>Needed to associate identical properties to multiple atomsets
   */
  private int equivalentAtomSets = 0;
  private int stepNumber;


  /**
   * Reads a Collection of AtomSets from a BufferedReader.
   * 
   * <p>
   * New AtomSets are generated when an <code>Input</code>,
   * <code>Standard</code> or <code>Z-Matrix</code> orientation is read. The
   * occurence of these orientations seems to depend on (in pseudo-code): <code>
   *  <br>&nbsp;if (opt=z-matrix) Z-Matrix; else Input;
   *  <br>&nbsp;if (!NoSymmetry) Standard;
   * </code> <br>
   * Which means that if <code>NoSymmetry</code> is used with a z-matrix
   * optimization, no other orientation besides <code>Z-Matrix</code> will be
   * present. This is important because <code>Z-Matrix</code> may have dummy
   * atoms while the analysis of the calculation results will not, i.e., the
   * <code>Center Numbers</code> in the z-matrix orientation may be different
   * from those in the population analysis!
   * 
   * <p>
   * Single point or frequency calculations always have an <code>Input</code>
   * orientation. If symmetry is used a <code>Standard</code> will be present
   * too.
   * 
   * @return TRUE to read a new line
   * 
   * @throws Exception 
   * 
   **/

  @Override
  protected boolean checkLine() throws Exception {
    if (line.startsWith(" Step number")) {
      equivalentAtomSets = 0;
      stepNumber++;
      // check for scan point information
      int scanPointIndex = line.indexOf("scan point");
      if (scanPointIndex > 0) {
        scanPoint = parseInt(line, scanPointIndex + 10);
      } else {
        scanPoint = -1; // no scan point information
      }
      return true;
    }
    if (line.indexOf("-- Stationary point found") > 0) {
      // stationary point, if have scanPoint: need to increment now...
      // to get the initial geometry for the next scan point in the proper
      // place
      if (scanPoint >= 0)
        scanPoint++;
      return true;
    }
    if (line.indexOf("Input orientation:") >= 0
        || line.indexOf("Z-Matrix orientation:") >= 0
        || line.indexOf("Standard orientation:") >= 0) {
      if (!doGetModel(++modelNumber))
        return checkLastModel();
      equivalentAtomSets++;
      //if (Logger.debugging)
        Logger.info(atomSetCollection.getAtomSetCount() + " model " + modelNumber + " step " + stepNumber
            + " equivalentAtomSet " + equivalentAtomSets + " calculation "
            + calculationNumber + " scan point " + scanPoint + line);
      readAtoms();
      return false;
    }
    if (!doProcessLines)
      return true;
    if (line.startsWith(" Energy=")) {
      setEnergy();
      return true;
    }
    if (line.startsWith(" SCF Done:")) {
      readSCFDone();
      return true;
    }
    if (line.startsWith(" Harmonic frequencies")) {
      readFrequencies();
      return true;
    }
    if (line.startsWith(" Total atomic charges:")
        || line.startsWith(" Mulliken atomic charges:")) {
      // NB this only works for the Standard or Input orientation of
      // the molecule since it does not list the values for the
      // dummy atoms in the z-matrix
      readPartialCharges();
      return true;
    }
    if (line.startsWith(" Dipole moment")) {
      readDipoleMoment();
      return true;
    }
    if (line.startsWith(" Standard basis:") || line.startsWith(" General basis read from")) {
      energyUnits = "";
      calculationType = line.substring(line.indexOf(":") + 1).trim();
      return true;
    }
    if (line.startsWith(" AO basis set")) {
      readBasis();
      return true;
    }
    if (line.indexOf("Molecular Orbital Coefficients") >= 0) {
      if (!filterMO())
        return true;
      readMolecularOrbitals();
      if (Logger.debugging) {
        Logger.debug(orbitals.size() + " molecular orbitals read");
      }
      return true;
    }
    if (line.startsWith(" Normal termination of Gaussian")) {
      ++calculationNumber;
      equivalentAtomSets = 0;
      // avoid next calculation to set the last title string
      return true;
    }
    return checkNboLine();
  }
  
  /**
   * Interprets the SCF Done: section.
   * 
   * <p>
   * The energyKey and energyString will be set for further AtomSets that have
   * the same molecular geometry (e.g., frequencies). The energy, convergence,
   * -V/T and S**2 values will be set as properties for the atomSet.
   * 
   * @throws Exception
   *           If an error occurs
   **/
  private void readSCFDone() throws Exception {
    String tokens[] = getTokens(line, 11);
    if (tokens.length < 4)
      return;
    energyKey = tokens[0];
    atomSetCollection.setAtomSetEnergy(tokens[2], parseFloat(tokens[2]));
    energyString = tokens[2] + " " + tokens[3];
    // now set the names for the last equivalentAtomSets
    atomSetCollection.setAtomSetNames(energyKey + " = " + energyString,
        equivalentAtomSets);
    // also set the properties for them
    atomSetCollection.setAtomSetProperties(energyKey, energyString,
        equivalentAtomSets);
    tokens = getTokens(readLine());
    if (tokens.length > 2) {
      atomSetCollection.setAtomSetProperties(tokens[0], tokens[2],
          equivalentAtomSets);
      if (tokens.length > 5)
        atomSetCollection.setAtomSetProperties(tokens[3], tokens[5],
            equivalentAtomSets);
      tokens = getTokens(readLine());
    }
    if (tokens.length > 2)
      atomSetCollection.setAtomSetProperties(tokens[0], tokens[2],
          equivalentAtomSets);
  }
  
  /**
   * Interpret the Energy= line for non SCF type energy output
   *
   */
  private void setEnergy() {
    String tokens[] = getTokens();
    energyKey = "Energy";
    energyString = tokens[1];
    atomSetCollection.setAtomSetNames("Energy = "+tokens[1], equivalentAtomSets);
    atomSetCollection.setAtomSetEnergy(energyString, parseFloat(energyString));
  }
  
  /* GAUSSIAN STRUCTURAL INFORMATION THAT IS EXPECTED
   It looks like sometimes it is possible to have in g03's standard
   orientation section a 'space' for the atomic type, so reading
   the last three tokens as x, y, and z should always work
   */
  
  // GAUSSIAN 04 format
  /*                 Standard orientation:
   ----------------------------------------------------------
   Center     Atomic              Coordinates (Angstroms)
   Number     Number             X           Y           Z
   ----------------------------------------------------------
   1          6           0.000000    0.000000    1.043880
   ##SNIP##    
   ---------------------------------------------------------------------
   */
  
  // GAUSSIAN 98 and 03 format
  /*                    Standard orientation:                         
   ---------------------------------------------------------------------
   Center     Atomic     Atomic              Coordinates (Angstroms)
   Number     Number      Type              X           Y           Z
   ---------------------------------------------------------------------
   1          6             0        0.852764   -0.020119    0.050711
   ##SNIP##
   ---------------------------------------------------------------------
   */
  
  private void readAtoms() throws Exception {
    atomSetCollection.newAtomSet();
    // default title : the energy of the previous structure as title
    // this is needed for the last structure in an optimization
    // if energy information is found for this structure the reader
    // will overwrite this setting later.
    atomSetCollection.setAtomSetName(energyKey + " = " + energyString);
    atomSetCollection.setAtomSetEnergy(energyString, parseFloat(energyString));
//  atomSetCollection.setAtomSetName("Last read atomset.");
    String path = getTokens()[0]; // path = type of orientation
    discardLines(4);
    String tokens[];
    while (readLine() != null &&
        !line.startsWith(" --")) {
      tokens = getTokens(); // get the tokens in the line
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementNumber =
        (short)parseInt(tokens[STD_ORIENTATION_ATOMIC_NUMBER_OFFSET]);
      if (atom.elementNumber < 0)
        atom.elementNumber = 0; // dummy atoms have -1 -> 0
      int offset = tokens.length-3;
      setAtomCoord(atom, parseFloat(tokens[offset]), 
          parseFloat(tokens[++offset]), 
          parseFloat(tokens[++offset]));
    }
    atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY,
        "Calculation "+calculationNumber+
        (scanPoint>=0?(SmarterJmolAdapter.PATH_SEPARATOR+"Scan Point "+scanPoint):"")+
        SmarterJmolAdapter.PATH_SEPARATOR+path);
  }
  /* SAMPLE BASIS OUTPUT */
  /*
   * see also http://www.gaussian.com/g_ur/k_gen.htm  -- thank you, Rick Spinney

   Standard basis: VSTO-3G (5D, 7F)
   AO basis set:
   Atom O1       Shell     1 SP   3    bf    1 -     4          0.000000000000          0.000000000000          0.216790088607
   0.5033151319D+01 -0.9996722919D-01  0.1559162750D+00
   0.1169596125D+01  0.3995128261D+00  0.6076837186D+00
   0.3803889600D+00  0.7001154689D+00  0.3919573931D+00
   Atom H2       Shell     2 S   3     bf    5 -     5          0.000000000000          1.424913022638         -0.867160354429
   0.3425250914D+01  0.1543289673D+00
   0.6239137298D+00  0.5353281423D+00
   0.1688554040D+00  0.4446345422D+00
   Atom H3       Shell     3 S   3     bf    6 -     6          0.000000000000         -1.424913022638         -0.867160354429
   0.3425250914D+01  0.1543289673D+00
   0.6239137298D+00  0.5353281423D+00
   0.1688554040D+00  0.4446345422D+00
   There are     3 symmetry adapted basis functions of A1  symmetry.
   There are     0 symmetry adapted basis functions of A2  symmetry.
   There are     1 symmetry adapted basis functions of B1  symmetry.
   There are     2 symmetry adapted basis functions of B2  symmetry.
   */

  private void readBasis() throws Exception {
    shells = new ArrayList<int[]>();
    List<String[]> gdata = new ArrayList<String[]>();
    int atomCount = 0;
    gaussianCount = 0;
    shellCount = 0;
    String lastAtom = "";
    String[] tokens;
    
    boolean doSphericalD = (calculationType != null && (calculationType.indexOf("5D") > 0));
    boolean doSphericalF = (calculationType != null && (calculationType.indexOf("7F") > 0));
    while (readLine() != null && line.startsWith(" Atom")) {
      shellCount++;
      tokens = getTokens();
      int[] slater = new int[4];
      if (!tokens[1].equals(lastAtom))
        atomCount++;
      lastAtom = tokens[1];
      slater[0] = atomCount - 1;
      String oType = tokens[4];
      if (doSphericalF && oType.indexOf("F") >= 0 || doSphericalD && oType.indexOf("D") >= 0)
        slater[1] = JmolAdapter.getQuantumShellTagIDSpherical(oType);
      else
        slater[1] = JmolAdapter.getQuantumShellTagID(oType);
      
      int nGaussians = parseInt(tokens[5]);
      slater[2] = gaussianCount; // or parseInt(tokens[7]) - 1
      slater[3] = nGaussians;
      shells.add(slater);
      gaussianCount += nGaussians;
      for (int i = 0; i < nGaussians; i++) {
        gdata.add(getTokens(readLine()));
      }
    }
    if (atomCount == 0)
      atomCount = 1;
    gaussians = new float[gaussianCount][];
    for (int i = 0; i < gaussianCount; i++) {
      tokens = gdata.get(i);
      gaussians[i] = new float[tokens.length];
      for (int j = 0; j < tokens.length; j++)
        gaussians[i][j] = parseFloat(tokens[j]);
    }
    if (Logger.debugging) {
      Logger.debug(shellCount + " slater shells read");
      Logger.debug(gaussianCount + " gaussian primitives read");
    }
  }
  
  /*

   Molecular Orbital Coefficients
                            1         2         3         4         5
                        (A1)--O   (A1)--O   (B2)--O   (A1)--O   (B1)--O
   EIGENVALUES --     -20.55790  -1.34610  -0.71418  -0.57083  -0.49821
   1 1   O  1S          0.99462  -0.20953   0.00000  -0.07310   0.00000
   2        2S          0.02117   0.47576   0.00000   0.16367   0.00000
   3        2PX         0.00000   0.00000   0.00000   0.00000   0.63927
   4        2PY         0.00000   0.00000   0.50891   0.00000   0.00000
   5        2PZ        -0.00134  -0.09475   0.00000   0.55774   0.00000
   6        3S          0.00415   0.43535   0.00000   0.32546   0.00000

but:

 105        4S        -47.27845  63.29565-100.44035   1.98362 -51.35328

   */
  private void readMolecularOrbitals() throws Exception {
    if (shells == null)
      return;
    Map<String, Object>[] mos = ArrayUtil.createArrayOfHashtable(5);
    List<String>[] data = ArrayUtil.createArrayOfArrayList(5);
    int nThisLine = 0;
    while (readLine() != null
        && line.toUpperCase().indexOf("DENS") < 0) {
      String[] tokens;
      if (line.indexOf("                    ") == 0) {
        addMOData(nThisLine, data, mos);
        tokens = getTokens(readLine());
        nThisLine = tokens.length;
        for (int i = 0; i < nThisLine; i++) {
          mos[i] = new Hashtable<String, Object>();
          data[i] = new ArrayList<String>();
          String sym = tokens[i];
          mos[i].put("symmetry", sym);
          if (sym.indexOf("--O") >= 0)
            mos[i].put("occupancy", new Float(2));
          else if (sym.indexOf("--V") >= 0)
            mos[i].put("occupancy", new Float(0));
        }
        line = readLine().substring(21);
        tokens = getTokens();
        if (tokens.length != nThisLine)
          tokens = getStrings(line, nThisLine, 10);
        for (int i = 0; i < nThisLine; i++)
          mos[i].put("energy", new Float(tokens[i]));
        continue;
      } else if (line.length() < 21 || (line.charAt(11) != ' ' 
                                        && ! Character.isDigit(line.charAt(11)) 
                                        ) ) {
        continue;
      }
      try {
        String type = line.substring(13, 18).trim();
        if (!isQuantumBasisSupported(type.charAt(0)) && "XYZ".indexOf(type.charAt(0)) >= 0)
          type = (type.length() == 2 ? "D" : "F") + type;
        if (!isQuantumBasisSupported(type.charAt(0)))
          continue;
        tokens = getStrings(line.substring(21), nThisLine, 10);
        for (int i = 0; i < nThisLine; i++)
          data[i].add(tokens[i]);
      } catch (Exception e) {
        Logger.error("Error reading Gaussian file Molecular Orbitals at line: "
            + line);
        break;
      }
    }
    addMOData(nThisLine, data, mos);
    setMOData(false); // perhaps in the future we might change this to TRUE
  }

  /* SAMPLE FREQUENCY OUTPUT */
  /*
   Harmonic frequencies (cm**-1), IR intensities (KM/Mole), Raman scattering
   activities (A**4/AMU), depolarization ratios for plane and unpolarized
   incident light, reduced masses (AMU), force constants (mDyne/A),
   and normal coordinates:
                       1                      2                      3
                      A1                     B2                     B1
   Frequencies --    64.6809                64.9485               203.8241
   Red. masses --     8.0904                 2.2567                 1.0164
   Frc consts  --     0.0199                 0.0056                 0.0249
   IR Inten    --     1.4343                 1.4384                15.8823
   Atom AN      X      Y      Z        X      Y      Z        X      Y      Z
   1   6     0.00   0.00   0.48     0.00  -0.05   0.23     0.01   0.00   0.00
   2   6     0.00   0.00   0.48     0.00  -0.05  -0.23     0.01   0.00   0.00
   3   1     0.00   0.00   0.49     0.00  -0.05   0.63     0.03   0.00   0.00
   4   1     0.00   0.00   0.49     0.00  -0.05  -0.63     0.03   0.00   0.00
   5   1     0.00   0.00  -0.16     0.00  -0.31   0.00    -1.00   0.00   0.00
   6  35     0.00   0.00  -0.16     0.00   0.02   0.00     0.01   0.00   0.00
   ##SNIP##
                      10                     11                     12
                      A1                     B2                     A1
   Frequencies --  2521.0940              3410.1755              3512.0957
   Red. masses --     1.0211                 1.0848                 1.2333
   Frc consts  --     3.8238                 7.4328                 8.9632
   IR Inten    --   264.5877               109.0525                 0.0637
   Atom AN      X      Y      Z        X      Y      Z        X      Y      Z
   1   6     0.00   0.00   0.00     0.00   0.06   0.00     0.00  -0.10   0.00
   2   6     0.00   0.00   0.00     0.00   0.06   0.00     0.00   0.10   0.00
   3   1     0.00   0.01   0.00     0.00  -0.70   0.01     0.00   0.70  -0.01
   4   1     0.00  -0.01   0.00     0.00  -0.70  -0.01     0.00  -0.70  -0.01
   5   1     0.00   0.00   1.00     0.00   0.00   0.00     0.00   0.00   0.00
   6  35     0.00   0.00  -0.01     0.00   0.00   0.00     0.00   0.00   0.00
   
   -------------------
   - Thermochemistry -
   -------------------
   */
  
  /**
   * Interprets the Harmonic frequencies section.
   *
   * <p>The vectors are added to a clone of the last read AtomSet.
   * Only the Frequencies, reduced masses, force constants and IR intensities
   * are set as properties for each of the frequency type AtomSet generated.
   *
   * @throws Exception If no frequences were encountered
   * @throws IOException If an I/O error occurs
   **/
  private void readFrequencies() throws Exception, IOException {
    discardLinesUntilContains(":");
    if (line == null)
      throw (new Exception("No frequencies encountered"));
    while ((line= readLine()) != null && line.length() > 15) {
      // we now have the line with the vibration numbers in them, but don't need it
      String[] symmetries = getTokens(readLine());
      String[] frequencies = getTokens(
          discardLinesUntilStartsWith(" Frequencies"), 15);
      String[] red_masses = getTokens(
          discardLinesUntilStartsWith(" Red. masses"), 15);
      String[] frc_consts = getTokens(
          discardLinesUntilStartsWith(" Frc consts"), 15);
      String[] intensities = getTokens(
          discardLinesUntilStartsWith(" IR Inten"), 15);
      int iAtom0 = atomSetCollection.getAtomCount();
      int atomCount = atomSetCollection.getLastAtomSetAtomCount();
      int frequencyCount = frequencies.length;
      boolean[] ignore = new boolean[frequencyCount];
      for (int i = 0; i < frequencyCount; ++i) {
        ignore[i] = !doGetVibration(++vibrationNumber);
        if (ignore[i])
          continue;  
        atomSetCollection.cloneLastAtomSet();
        // set the properties
        atomSetCollection.setAtomSetFrequency("Calculation " + calculationNumber, symmetries[i], frequencies[i], null);
        atomSetCollection.setAtomSetProperty("ReducedMass",
            red_masses[i]+" AMU");
        atomSetCollection.setAtomSetProperty("ForceConstant",
            frc_consts[i]+" mDyne/A");
        atomSetCollection.setAtomSetProperty("IRIntensity",
            intensities[i]+" KM/Mole");
      }
      discardLinesUntilContains(" AN ");
      fillFrequencyData(iAtom0, atomCount, atomCount, ignore, true, 0, 0, null);
    }
  }
  
  void readDipoleMoment() throws Exception {
    //  X=     0.0000    Y=     0.0000    Z=    -1.2917  Tot=     1.2917
    String tokens[] = getTokens(readLine());
    if (tokens.length != 8)
      return;
    Vector3f dipole = new Vector3f(parseFloat(tokens[1]),
        parseFloat(tokens[3]), parseFloat(tokens[5]));
    Logger.info("Molecular dipole for model " + atomSetCollection.getAtomSetCount()
        + " = " + dipole);
    atomSetCollection.setAtomSetAuxiliaryInfo("dipole", dipole);
  }

  
  /* SAMPLE Mulliken Charges OUTPUT from G98 */
  /*
   Mulliken atomic charges:
   1
   1  C   -0.238024
   2  C   -0.238024
   ###SNIP###
   6  Br  -0.080946
   Sum of Mulliken charges=   0.00000
   */
  
  /**
   * Reads partial charges and assigns them only to the last atom set. 
   * @throws Exception When an I/O error or discardlines error occurs
   */
  // TODO this really should set the charges for the last nOrientations read
  // being careful about the dummy atoms...
  void readPartialCharges() throws Exception {
    discardLines(1);
    int atomCount = atomSetCollection.getAtomCount();
    int i0 = atomSetCollection.getLastAtomSetAtomIndex();
    Atom[] atoms = atomSetCollection.getAtoms();
    for (int i = i0; i < atomCount; ++i) {
      // first skip over the dummy atoms
      while (atoms[i].elementNumber == 0)
        ++i;
      // assign the partial charge
      float charge = parseFloat(getTokens(readLine())[2]);
      atoms[i].partialCharge = charge;
    }
    Logger.info("Mulliken charges found for Model " + atomSetCollection.getAtomSetCount());
  }
  
}
