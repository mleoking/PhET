/* $RCSfile$
 * $Author: nicove $
 * $Date: 2006-08-30 13:20:20 -0500 (Wed, 30 Aug 2006) $
 * $Revision: 5447 $
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;
import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;

/**
 * A reader for NWChem 4.6
 * NWChem is a quantum chemistry program developed at
 * Pacific Northwest National Laboratory.
 * See http://www.nwchem-sw.org/index.php/NWChem_Documentation
 * for orbital plotting, one needs to use the following switches:
 * 
 * print "final vectors" "final vectors analysis"
 *
 * <p>AtomSets will be generated for
 * output coordinates in angstroms,
 * energy gradients with vector information of the gradients,
 * and frequencies with an AtomSet for every separate frequency containing
 * vector information of the vibrational mode.
 * <p>Note that the different modules give quite different formatted output
 * so it is not certain that all modules will be properly interpreted.
 * Most testing has been done with the SCF and DFT tasks.
 * 
 * no support yet for orbitals
 * 
**/

public class NWChemReader extends MOReader {

  /**
   * The number of the task begin interpreted.
   * <p>Used for the construction of the 'path' for the atom set.
   */
  private int taskNumber = 1;

  /**
   * The number of equivalent atom sets.
   * <p>Needed to associate identical properties to multiple atomsets
   */
  private int equivalentAtomSets = 0;

  /**
   * The type of energy last calculated.
   */
  private String energyKey = "";
  /**
   * The last calculated energy value.
   */
  private String energyValue = "";

  // need to remember a bit of the state of what was read before...
  private boolean converged;
  private boolean haveEnergy;
  private boolean haveAt;
  private boolean inInput;

  private List<String> atomTypes;
  private boolean readROHFonly;

  @Override
  protected void initializeReader() {
    readROHFonly = (filter != null && filter.indexOf("ROHF") >= 0);
    calculationType = "(NWCHEM)"; // normalization is different for NWCHEM
  }

  /**
    * @return true if need to read new line
    * @throws Exception
    * 
    */
  @Override
  protected boolean checkLine() throws Exception {

    if (line.trim().startsWith("NWChem")) {
      readNWChemLine();
      return true;
    }

    if (line.startsWith("          Step")) {
      init();
      return true;
    }
    if (line.indexOf("  wavefunction    = ") >= 0) {
      calculationType = line.substring(line.indexOf("=") + 1).trim()
          + "(NWCHEM)";
      moData.put("calculationType", calculationType);
      return true;
    }
    if (line.indexOf("Total") >= 0) {
      readTotal();
      return true;
    }
    if (line.indexOf("@") >= 0) {
      readAtSign();
      return true;
    }
    if (line.startsWith(" Task  times")) {
      init();
      taskNumber++; // starting a new task
      return true;
    }
    if (line.startsWith("      Optimization converged")) {
      converged = true;
      return true;
    }
    if (line.startsWith("      Symmetry information")) {
      readSymmetry();
      return true;
    }
    if (line.indexOf("Output coordinates in angstroms") >= 0) {
      if (!doGetModel(++modelNumber))
        return checkLastModel();
      equivalentAtomSets++;
      readAtoms();
      return true;
    }
    if (!doProcessLines)
      return true;

    if (line.indexOf("NWChem Nuclear Hessian and Frequency Analysis") >= 0) {
      readFrequencies();
      return true;
    }

    if (line.indexOf("ENERGY GRADIENTS") >= 0) {
      equivalentAtomSets++;
      readGradients();
      return true;
    }

    if (line.startsWith("  Mulliken analysis of the total density")) {
      // only do this if I have read an atom set in this task/step
      if (equivalentAtomSets == 0)
        return true;
      readPartialCharges();
      return true;
    }
    if (line.contains("Basis \"ao basis\"") && readMolecularOrbitals) {
      return readBasis();
    }
    if (line.contains("Final Molecular Orbital Analysis")) {
      if (equivalentAtomSets == 0)
        return true;
      return readMolecularOrbitalAnalysis(true);
    }

    if (line.contains("Final Alpha Molecular Orbital Analysis")) {
      if (equivalentAtomSets == 0)
        return true;
      alphaBeta = "alpha ";
      return readMolecularOrbitalAnalysis(true);
    }

    if (line.contains("Final Beta Molecular Orbital Analysis")) {
      if (equivalentAtomSets == 0)
        return true;
      return readMolecularOrbitalAnalysis(false);
    }

    if (!readROHFonly && line.contains("Final MO vectors")) {
      if (equivalentAtomSets == 0)
        return true;
      return readMolecularOrbitalVectors();
    }

    return true;
  }

  private void init() {
    haveEnergy = false;
    haveAt = false;
    converged = false;
    inInput = false;
    equivalentAtomSets = 0;
  }

  /**
   * 
   * @param key
   * @param value
   * @param nAtomSets NOT USED
   */
  private void setEnergies(String key, String value, int nAtomSets) {
    energyKey = key;
    energyValue = value;
    atomSetCollection.setAtomSetProperties(energyKey, energyValue,
        equivalentAtomSets);
    atomSetCollection.setAtomSetNames(energyKey + " = " + energyValue,
        equivalentAtomSets);
    atomSetCollection.setAtomSetEnergy(value, parseFloat(value));
    haveEnergy = true;
  }

  private void setEnergy(String key, String value) {
    energyKey = key;
    energyValue = value;
    atomSetCollection.setAtomSetProperty(energyKey, energyValue);
    atomSetCollection.setAtomSetName(energyKey + " = " + energyValue);
    haveEnergy = true;
  }

  /**
   * Read the symmetry information and set the property.
   * @throws Exception If an error occurs.
   */
  private void readSymmetry() throws Exception {
    discardLines(2);
    if (readLine() == null)
      return;
    String tokens[] = getTokens();
    atomSetCollection.setAtomSetProperties("Symmetry group name",
        tokens[tokens.length - 1], equivalentAtomSets);
  }

  private void readNWChemLine() {
    // currently only keep track of whether I am in the input module or not.
    inInput = (line.indexOf("NWChem Input Module") >= 0);
  }

  /**
   * Interpret a line starting with a line with "Total" in it.
   * <p>Determine whether it reports the energy, if so set the property and name(s)
   */
  private void readTotal() {
    String tokens[] = getTokens();
    try {
      if (tokens[2].startsWith("energy")) {
        // in an optimization an energy is reported in a follow up step
        // that energy I don't want so only set the energy once
        if (!haveAt)
          setEnergies("E(" + tokens[1] + ")", tokens[tokens.length - 1],
              equivalentAtomSets);
      }
    } catch (Exception e) {
      // ignore any problems in dealing with the line
    }
  }

  private void readAtSign() throws Exception {
    if (line.charAt(2) == 'S') {
      discardLines(1); // skip over the line with the --- in it
      if (readLine() == null)
        return;
    }
    String tokens[] = getTokens();
    if (!haveEnergy) { // if didn't already have the energies, set them now
      setEnergies("E", tokens[2], equivalentAtomSets);
    } else {
      // @ comes after gradients, so 'reset' the energies for additional
      // atom sets that may be been parsed.
      setEnergies(energyKey, energyValue, equivalentAtomSets);
    }
    atomSetCollection.setAtomSetProperties("Step", tokens[1],
        equivalentAtomSets);
    haveAt = true;
  }

  // NWChem Output coordinates
  /*
    Output coordinates in angstroms (scale by  1.889725989 to convert to a.u.)

    No.       Tag          Charge          X              Y              Z
   ---- ---------------- ---------- -------------- -------------- --------------
      1 O                    8.0000     0.00000000     0.00000000     0.14142136
      2 H                    1.0000     0.70710678     0.00000000    -0.56568542
      3 H                    1.0000    -0.70710678     0.00000000    -0.56568542

        Atomic Mass 
  */

  /**
   * Reads the output coordinates section into a new AtomSet.
   * @throws Exception If an error occurs.
   **/
  private void readAtoms() throws Exception {
    discardLines(3); // skip blank line, titles and dashes
    String tokens[];
    haveEnergy = false;
    atomSetCollection.newAtomSet();
    atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY, "Task "
        + taskNumber
        + (inInput ? SmarterJmolAdapter.PATH_SEPARATOR + "Input"
            : SmarterJmolAdapter.PATH_SEPARATOR + "Geometry"));
    atomTypes = new ArrayList<String>();
    while (readLine() != null && line.length() > 0) {
      tokens = getTokens(); // get the tokens in the line
      if (tokens.length < 6)
        break; // if don't have enough of them: done
      Atom atom = atomSetCollection.addNewAtom();
      atom.atomName = fixTag(tokens[1]);
      atomTypes.add(atom.atomName);
      setAtomCoord(atom, parseFloat(tokens[3]), parseFloat(tokens[4]),
          parseFloat(tokens[5]));
    }
    // only if was converged, use the last energy for the name and properties
    if (converged) {
      setEnergy(energyKey, energyValue);
      atomSetCollection.setAtomSetProperty("Step", "converged");
    } else if (inInput) {
      atomSetCollection.setAtomSetName("Input");
    }
  }

  // NWChem Gradients output
  // The 'atom' is really a Tag (as above)
  /*
                           UHF ENERGY GRADIENTS

      atom               coordinates                        gradient
                   x          y          z           x          y          z
     1 O       0.000000   0.000000   0.267248    0.000000   0.000000  -0.005967
     2 H       1.336238   0.000000  -1.068990   -0.064647   0.000000   0.002984
     3 H      -1.336238   0.000000  -1.068990    0.064647   0.000000   0.002984

  */
  // NB one could consider removing the previous read structure since that
  // must have been the input structure for the optimizition?
  /**
   * Reads the energy gradients section into a new AtomSet.
   *
   * <p>One could consider not adding a new AtomSet for this, but just
   * adding the gradient vectors to the last AtomSet read (if that was
   * indeed the same nuclear arrangement).
   * @throws Exception If an error occurs.
   **/
  private void readGradients() throws Exception {
    discardLines(3); // skip blank line, titles and dashes
    String tokens[];
    atomSetCollection.newAtomSet();
    if (equivalentAtomSets > 1)
      atomSetCollection.cloneLastAtomSetProperties();
    atomSetCollection.setAtomSetProperty("vector", "gradient");
    atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY, "Task "
        + taskNumber + SmarterJmolAdapter.PATH_SEPARATOR + "Gradients");
    while (readLine() != null && line.length() > 0) {
      tokens = getTokens(); // get the tokens in the line
      if (tokens.length < 8)
        break; // make sure I have enough tokens
      Atom atom = atomSetCollection.addNewAtom();
      atom.atomName = fixTag(tokens[1]);
      setAtomCoord(atom, parseFloat(tokens[2]) * ANGSTROMS_PER_BOHR,
          parseFloat(tokens[3]) * ANGSTROMS_PER_BOHR, parseFloat(tokens[4])
              * ANGSTROMS_PER_BOHR);
      // Keep gradients in a.u. (larger value that way)
      // need to multiply with -1 so the direction is in the direction the
      // atom needs to move to lower the energy
      atomSetCollection.addVibrationVector(atom.atomIndex,
          -parseFloat(tokens[5]), -parseFloat(tokens[6]),
          -parseFloat(tokens[7]));
    }
  }

  // SAMPLE FREQUENCY OUTPUT
  // First the structure. The atom column has real element names (not the tags)
  // units of X Y and Z in a.u.
  /*
   ---------------------------- Atom information ----------------------------
   atom    #        X              Y              Z            mass
   --------------------------------------------------------------------------
   O        1  9.5835700E-02  3.1863970E-07  0.0000000E+00  1.5994910E+01
   H        2 -9.8328438E-01  1.5498085E+00  0.0000000E+00  1.0078250E+00
   H        3 -9.8328460E-01 -1.5498088E+00  0.0000000E+00  1.0078250E+00
   --------------------------------------------------------------------------

   */
  // NB another header but with subhead (Frequencies expressed in cm-1)
  // is in the output before this....
  /*
   -------------------------------------------------
   NORMAL MODE EIGENVECTORS IN CARTESIAN COORDINATES
   -------------------------------------------------
   (Projected Frequencies expressed in cm-1)

   1           2           3           4           5           6
   
   P.Frequency        0.00        0.00        0.00        0.00        0.00        0.00
   
   1     0.03302     0.00000     0.00000     0.00000    -0.02102     0.23236
   2     0.08894     0.00000     0.00000     0.00000     0.22285     0.00752
   3     0.00000     0.00000     0.25004     0.00000     0.00000     0.00000
   4     0.52206     0.00000     0.00000     0.00000    -0.33418     0.13454
   5     0.42946     0.00000     0.00000     0.00000     0.00480    -0.06059
   6     0.00000     0.99611     0.00000     0.00000     0.00000     0.00000
   7    -0.45603     0.00000     0.00000     0.00000     0.29214     0.33018
   8     0.42946     0.00000     0.00000     0.00000     0.00480    -0.06059
   9     0.00000     0.00000     0.00000     0.99611     0.00000     0.00000

   7           8           9
   
   P.Frequency     1484.76     3460.15     3551.50
   
   1    -0.06910    -0.04713     0.00000
   2     0.00000     0.00000    -0.06994
   3     0.00000     0.00000     0.00000
   4     0.54837     0.37401    -0.38643
   5     0.39688    -0.58189     0.55498
   6     0.00000     0.00000     0.00000
   7     0.54837     0.37402     0.38641
   8    -0.39688     0.58191     0.55496
   9     0.00000     0.00000     0.00000



   ----------------------------------------------------------------------------
   Normal Eigenvalue ||    Projected Derivative Dipole Moments (debye/angs)
   Mode   [cm**-1]  ||      [d/dqX]             [d/dqY]           [d/dqZ]
   ------ ---------- || ------------------ ------------------ -----------------
   1        0.000 ||       0.159               2.123             0.000
   2        0.000 ||       0.000               0.000             2.480
   3        0.000 ||       0.000               0.000            -0.044
   4        0.000 ||       0.000               0.000             2.480
   5        0.000 ||      -0.101              -0.015             0.000
   6        0.000 ||       1.116              -0.303             0.000
   7     1484.764 ||       2.112               0.000             0.000
   8     3460.151 ||       1.877               0.000             0.000
   9     3551.497 ||       0.000               3.435             0.000
   ----------------------------------------------------------------------------



   
   
   ----------------------------------------------------------------------------
   Normal Eigenvalue ||           Projected Infra Red Intensities
   Mode   [cm**-1]  || [atomic units] [(debye/angs)**2] [(KM/mol)] [arbitrary]
   ------ ---------- || -------------- ----------------- ---------- -----------
   1        0.000 ||    0.196398           4.531       191.459      10.742
   2        0.000 ||    0.266537           6.149       259.833      14.578
   3        0.000 ||    0.000084           0.002         0.081       0.005
   4        0.000 ||    0.266537           6.149       259.833      14.578
   5        0.000 ||    0.000452           0.010         0.441       0.025
   6        0.000 ||    0.057967           1.337        56.509       3.170
   7     1484.764 ||    0.193384           4.462       188.520      10.577
   8     3460.151 ||    0.152668           3.522       148.828       8.350
   9     3551.497 ||    0.511498          11.801       498.633      27.976
   ----------------------------------------------------------------------------
   */

  /**
   * Reads the AtomSet and projected frequencies in the frequency section.
   *
   * <p>Attaches the vibration vectors of the projected frequencies to
   * duplicates of the atom information in the frequency section.
   * @throws Exception If an error occurs.
   **/
  private void readFrequencies() throws Exception {
    int firstFrequencyAtomSetIndex = atomSetCollection.getAtomSetCount();
    String path = "Task " + taskNumber + SmarterJmolAdapter.PATH_SEPARATOR
        + "Frequencies";

    // position myself to read the atom information, i.e., structure
    discardLinesUntilContains("Atom information");
    discardLines(2);
    atomSetCollection.newAtomSet();
    String tokens[];
    while (readLine() != null && line.indexOf("---") < 0) {
      tokens = getTokens();
      Atom atom = atomSetCollection.addNewAtom();
      atom.atomName = fixTag(tokens[0]);
      setAtomCoord(atom, parseFloat(tokens[2]) * ANGSTROMS_PER_BOHR,
          parseFloat(tokens[3]) * ANGSTROMS_PER_BOHR, parseFloat(tokens[4])
              * ANGSTROMS_PER_BOHR);
    }

    discardLinesUntilContains("(Projected Frequencies expressed in cm-1)");
    discardLines(3); // step over the line with the numbers

    boolean firstTime = true;
    while (readLine() != null && line.indexOf("P.Frequency") >= 0) {
      tokens = getTokens(line, 12);
      int frequencyCount = tokens.length;
      int iAtom0 = atomSetCollection.getAtomCount();
      int atomCount = atomSetCollection.getLastAtomSetAtomCount();
      if (firstTime)
        iAtom0 -= atomCount;
      boolean[] ignore = new boolean[frequencyCount];
      // clone the last atom set nFreq-1 times the first time, later nFreq times.

      // assign the frequency values to each atomset's name and property
      for (int i = 0; i < frequencyCount; ++i) {
        ignore[i] = !doGetVibration(++vibrationNumber);
        if (ignore[i])
          continue;
        if (!firstTime || i > 0) {
          atomSetCollection.cloneLastAtomSet();
        }
        atomSetCollection.setAtomSetFrequency(path, null, tokens[i], null);
      }
      firstTime = false;
      discardLines(1);
      fillFrequencyData(iAtom0, atomCount, atomCount, ignore, false, 0, 0, null);
      discardLines(3);
    }

    // now set the names and properties of the atomsets associated with
    // the frequencies
    // NB this is not always there: try/catch and possibly set freq value again  
    try {
      discardLinesUntilContains("Projected Infra Red Intensities");
      discardLines(2);
      for (int i = vibrationNumber, idx = firstFrequencyAtomSetIndex; --i >= 0;) {
        if (readLine() == null)
          return;
        if (!doGetVibration(i + 1))
          continue;
        tokens = getTokens();
        int iset = atomSetCollection.getCurrentAtomSetIndex();
        atomSetCollection.setCurrentAtomSetIndex(idx++);
        atomSetCollection.setAtomSetFrequency(null, null, tokens[i], null);
        atomSetCollection.setAtomSetProperty("IRIntensity", tokens[5]
            + " KM/mol");
        atomSetCollection.setCurrentAtomSetIndex(iset);
      }
    } catch (Exception e) {
      // If exception was thrown, don't do anything here...
    }
  }

  /**
   * Reads partial charges and assigns them only to the last atom set. 
   * @throws Exception When an I/O error or discardlines error occurs
   */
  void readPartialCharges() throws Exception {
    String tokens[];
    discardLines(4);
    int atomCount = atomSetCollection.getAtomCount();
    int i0 = atomSetCollection.getLastAtomSetAtomIndex();
    Atom[] atoms = atomSetCollection.getAtoms();
    for (int i = i0; i < atomCount; ++i) {
      // first skip over the dummy atoms (not sure whether that really is needed..)
      while (atoms[i].elementNumber == 0)
        ++i;
      do {
        // assign the partial charge
        if (readLine() == null || line.length() < 3)
          return;
        tokens = getTokens();
      } while (tokens[0].indexOf(".") >= 0);
      atoms[i].partialCharge = parseInt(tokens[2]) - parseFloat(tokens[3]);
    }
  }

  /**
   * Returns a modified identifier for a tag, so that the element can be
   * determined from it in the {@link Atom}.
   *<p>
   * The result is that a tag that started with Bq (case insensitive) will be
   * renamed to have the Bq removed and '-Bq' appended to it. <br>
   * A tag consisting only of Bq (case insensitive) will return X. This can
   * happen in a frequency analysis.
   * 
   * @param tag
   *          the tag to be modified
   * @return a possibly modified tag
   **/
  private String fixTag(String tag) {
    // make sure that Bq's are not interpreted as boron
    if (tag.equalsIgnoreCase("bq"))
      return "X";
    if (tag.toLowerCase().startsWith("bq"))
      tag = tag.substring(2) + "-Bq";
    return "" + Character.toUpperCase(tag.charAt(0))
        + (tag.length() == 1 ? "" : "" + Character.toLowerCase(tag.charAt(1)));
  }

  int nBasisFunctions;

  /*

                        Basis "ao basis" -> "ao basis" (cartesian)
                        -----
    C (Carbon)
    ----------
              Exponent  Coefficients 
         -------------- ---------------------------------------------------------
    1 S  6.66500000E+03  0.000692
    1 S  1.00000000E+03  0.005329
    1 S  2.28000000E+02  0.027077
    1 S  6.47100000E+01  0.101718
    1 S  2.10600000E+01  0.274740
    1 S  7.49500000E+00  0.448564
    1 S  2.79700000E+00  0.285074
    1 S  5.21500000E-01  0.015204

    2 S  6.66500000E+03 -0.000146
    2 S  1.00000000E+03 -0.001154
    2 S  2.28000000E+02 -0.005725
    2 S  6.47100000E+01 -0.023312
    2 S  2.10600000E+01 -0.063955
    2 S  7.49500000E+00 -0.149981
    2 S  2.79700000E+00 -0.127262
    2 S  5.21500000E-01  0.544529

    3 S  1.59600000E-01  1.000000

    4 P  9.43900000E+00  0.038109
    4 P  2.00200000E+00  0.209480
    4 P  5.45600000E-01  0.508557

    5 P  1.51700000E-01  1.000000

    6 D  5.50000000E-01  1.000000

    F (Fluorine)
    ------------
              Exponent  Coefficients 
         -------------- ---------------------------------------------------------
    1 S  1.47100000E+04  0.000721
    1 S  2.20700000E+03  0.005553
    1 S  5.02800000E+02  0.028267
    1 S  1.42600000E+02  0.106444
    1 S  4.64700000E+01  0.286814
    1 S  1.67000000E+01  0.448641
    1 S  6.35600000E+00  0.264761
    1 S  1.31600000E+00  0.015333

    2 S  1.47100000E+04 -0.000165
    2 S  2.20700000E+03 -0.001308
    2 S  5.02800000E+02 -0.006495
    2 S  1.42600000E+02 -0.026691
    2 S  4.64700000E+01 -0.073690
    2 S  1.67000000E+01 -0.170776
    2 S  6.35600000E+00 -0.112327
    2 S  1.31600000E+00  0.562814

    3 S  3.89700000E-01  1.000000

    4 P  2.26700000E+01  0.044878
    4 P  4.97700000E+00  0.235718
    4 P  1.34700000E+00  0.508521

    5 P  3.47100000E-01  1.000000

    6 D  1.64000000E+00  1.000000

    H (Hydrogen)
    ------------
              Exponent  Coefficients 
         -------------- ---------------------------------------------------------
    1 S  1.30100000E+01  0.019685
    1 S  1.96200000E+00  0.137977
    1 S  4.44600000E-01  0.478148

    2 S  1.22000000E-01  1.000000

    3 P  7.27000000E-01  1.000000



   Summary of "ao basis" -> "ao basis" (cartesian)

   * 
   */

  private static String DS_LIST = "d2-   d1-   d0    d1+   d2+";
  private static String FS_LIST = "f3-   f2-   f1-   f0    f1+   f2+   f3+";
//  private static String FS_LIST = "f3+   f2+   f1+   f0    f1-   f2-   f3-";
  private static String DC_LIST = "DXX   DXY   DXZ   DYY   DYZ   DZZ";
  private static String FC_LIST = "XXX   XXY   XXZ   XYY   XYZ   XZZ   YYY   YYZ   YZZ   ZZZ";

  private boolean readBasis() throws Exception {
    gaussianCount = 0;
    shellCount = 0;
    nBasisFunctions = 0;

    boolean isD6F10 = (line.indexOf("cartesian") >= 0);
    if (isD6F10) {
      getDFMap(DC_LIST, JmolAdapter.SHELL_D_CARTESIAN, CANONICAL_DC_LIST, 3);
      getDFMap(FC_LIST, JmolAdapter.SHELL_F_CARTESIAN, CANONICAL_FC_LIST, 3);
    } else {
      getDFMap(DS_LIST, JmolAdapter.SHELL_D_SPHERICAL, CANONICAL_DS_LIST, 2);
      getDFMap(FS_LIST, JmolAdapter.SHELL_F_SPHERICAL, CANONICAL_FS_LIST, 2);
    }
    shells = new ArrayList<int[]>();
    Map<String, List<List<Object[]>>> atomInfo = new Hashtable<String, List<List<Object[]>>>();
    String atomSym = null;
    List<List<Object[]>> atomData = null;
    List<Object[]> shellData = null;
    while (line != null) {
      int nBlankLines = 0;
      while (line.length() < 3 || line.charAt(2) == ' ') {
        shellData = new ArrayList<Object[]>();
        readLine();
        if (line.length() < 3)
          nBlankLines++;
      }
      if (nBlankLines >= 2)
        break;
      if (parseInt(line) == Integer.MIN_VALUE) {
        // next atom type
        atomSym = getTokens()[0];
        atomData = new ArrayList<List<Object[]>>();
        atomInfo.put(atomSym, atomData);
        readLine();
        readLine();
        continue;
      }
      while (line != null && line.length() > 3) {
        String[] tokens = getTokens();
        Object[] o = new Object[] { tokens[1],
            new float[] { parseFloat(tokens[2]), parseFloat(tokens[3]) } };
        shellData.add(o);
        readLine();
      }
      atomData.add(shellData);
    }

    int nD = (isD6F10 ? 6 : 5);
    int nF = (isD6F10 ? 10 : 7);
    List<float[]> gdata = new ArrayList<float[]>();
    for (int i = 0; i < atomTypes.size(); i++) {
      atomData = atomInfo.get(atomTypes.get(i));
      int nShells = atomData.size();
      for (int ishell = 0; ishell < nShells; ishell++) {
        shellCount++;
        shellData = atomData.get(ishell);
        int nGaussians = shellData.size();
        String type = (String) shellData.get(0)[0];
        switch (type.charAt(0)) {
        case 'S':
          nBasisFunctions += 1;
          break;
        case 'P':
          nBasisFunctions += 3;
          break;
        case 'D':
          nBasisFunctions += nD;
          break;
        case 'F':
          nBasisFunctions += nF;
          break;
        }
        int[] slater = new int[4];
        slater[0] = i;
        slater[1] = (isD6F10 ? JmolAdapter.getQuantumShellTagID(type)
            : JmolAdapter.getQuantumShellTagIDSpherical(type));
        slater[2] = gaussianCount;
        slater[3] = nGaussians;
        shells.add(slater);
        for (int ifunc = 0; ifunc < nGaussians; ifunc++)
          gdata.add((float[]) shellData.get(ifunc)[1]);
        gaussianCount += nGaussians;
      }
    }
    gaussians = new float[gaussianCount][];
    for (int i = 0; i < gaussianCount; i++)
      gaussians[i] = gdata.get(i);
    return true;
  }

  /*
   * 
                       ROHF Final Molecular Orbital Analysis
                       -------------------------------------

  Vector    9  Occ=2.000000D+00  E=-1.152419D+00  Symmetry=a1
              MO Center=  1.0D-19,  2.0D-16, -4.4D-01, r^2= 2.3D+00
   Bfn.  Coefficient  Atom+Function         Bfn.  Coefficient  Atom+Function  
  ----- ------------  ---------------      ----- ------------  ---------------
    77      0.180031   6 C  s                47      0.180031   4 C  s         
    32      0.178250   3 C  s                92      0.178250   7 C  s         
    62      0.177105   5 C  s                 2      0.174810   1 C  s         

  Vector   10  Occ=2.000000D+00  E=-1.030565D+00  Symmetry=b2
              MO Center=  2.1D-18,  1.8D-17, -3.6D-01, r^2= 2.9D+00
   Bfn.  Coefficient  Atom+Function         Bfn.  Coefficient  Atom+Function  
  ----- ------------  ---------------      ----- ------------  ---------------
    92      0.216716   7 C  s                32     -0.216716   3 C  s         
    47     -0.205297   4 C  s                77      0.205297   6 C  s         

  ...
  Vector   39  Occ=0.000000D+00  E= 6.163870D-01  Symmetry=b2
              MO Center= -9.0D-31,  2.4D-14, -1.7D-01, r^2= 5.7D+00
   Bfn.  Coefficient  Atom+Function         Bfn.  Coefficient  Atom+Function  
  ----- ------------  ---------------      ----- ------------  ---------------
    68      2.509000   5 C  py               39     -2.096777   3 C  pz        
    99      2.096777   7 C  pz               54     -1.647235   4 C  pz        
    84      1.647235   6 C  pz                8     -1.004675   1 C  py        
    98     -0.870389   7 C  py               38     -0.870389   3 C  py        
    83      0.691421   6 C  py               53      0.691421   4 C  py        


  center of mass
   * 
   */

  private Map<Integer, Map<String, Object>> moInfo;
  
  int moCount;
  
  private boolean readMolecularOrbitalAnalysis(boolean doClear) throws Exception {

    if (shells == null)
      return true;
    int moCount = 0;
    boolean isBeta = false;
    if (doClear && !readROHFonly) {
      moInfo = new Hashtable<Integer, Map<String, Object>>();
    }
    while (line != null) {
      while ((line.length() < 3 || line.charAt(1) == ' ')
          && line.indexOf("Final") < 0) {
        readLine();
      }
      Logger.info(line);
      if (line.indexOf("Final") >= 0) {
        if (line.indexOf("MO") >= 0)
          break;
        if (line.indexOf("Final Beta") >= 0) {
          isBeta = true;
        }
        readLine();
        continue;
      }
      if (line.charAt(1) != 'V')
        break;
      line = line.replace('=', ' ');
      //  Vector    9  Occ=2.000000D+00  E=-1.152419D+00  Symmetry=a1
      String[] tokens = getTokens();
      int iMo = parseInt(tokens[1]);
      float occupancy = parseFloat(tokens[3]);
      float energy = parseFloat(tokens[5]);
      String symmetry = tokens[7];
      discardLines(3);
      Map<String, Object> mo = new Hashtable<String, Object>();
      mo.put("occupancy", Float.valueOf(occupancy));
      mo.put("energy", Float.valueOf(energy));
      mo.put("symmetry", symmetry);
      float[] coefs = null;
      if (readROHFonly) {
        setMO(mo);
        mo.put("type", "ROHF " + (++moCount));
        coefs = new float[nBasisFunctions];
        mo.put("coefficients", coefs);
      } else {
        moInfo.put(Integer.valueOf(isBeta ? -iMo : iMo), mo);
      }

      //    68      2.509000   5 C  py               39     -2.096777   3 C  pz        
      while (readLine() != null && line.length() > 3) {
        if (readROHFonly) {
          tokens = getTokens();
          coefs[parseInt(tokens[0]) - 1] = parseFloat(tokens[1]);
          int pt = tokens.length / 2;
          if (pt == 5 || pt == 6)
            coefs[parseInt(tokens[pt]) - 1] = parseFloat(tokens[pt + 1]);
        }
      }
    }
    energyUnits = "a.u.";
    if (readROHFonly)
      setMOData(false);
    return false;
  }

  /*

                                 Final MO vectors
                                 ----------------


  global array: scf_init: MOs[1:80,1:80],  handle: -1000 

            1           2           3           4           5           6  
       ----------- ----------- ----------- ----------- ----------- -----------
   1       0.98048    -0.02102     0.00000    -0.00000    -0.00000     0.06015
  ...
  80       0.00006    -0.00005    -0.00052     0.00006     0.00000     0.01181

            7           8           9          10          11          12  
       ----------- ----------- ----------- ----------- ----------- -----------
   1      -0.00000    -0.00000     0.02285    -0.00000    -0.00000     0.00000
   
   */
  private boolean readMolecularOrbitalVectors() throws Exception {

    if (shells == null)
      return true;
    Map<String, Object>[] mos = null;
    List<String>[] data = null;
    int moCount = 0;
    int iListed = 0;
    int ptOffset = -1;
    int fieldSize = 0;
    int nThisLine = 0;
    discardLines(5);
    boolean isBeta = false;
    boolean betaOnly = !filterMO();
    while (readLine() != null) {
      if (parseInt(line) != iListed + 1) {
        if (line.indexOf("beta") < 0)
          break;
        alphaBeta = "beta ";
        if (!filterMO())
          break;
        isBeta = true;
        iListed = 0;
        readLine();
        continue;
      }
      
      readLine();
      String[] tokens = getTokens();
      if (Logger.debugging) {
        Logger.debug(tokens.length + " --- " + line);
      }
      nThisLine = tokens.length;
      ptOffset = 6;
      fieldSize = 12;
      mos = ArrayUtil.createArrayOfHashtable(nThisLine);
      data = ArrayUtil.createArrayOfArrayList(nThisLine);
      for (int i = 0; i < nThisLine; i++) {
        mos[i] = new Hashtable<String, Object>();
        data[i] = new ArrayList<String>();
      }

      while (readLine() != null && line.length() > 0)
        for (int i = 0, pt = ptOffset; i < nThisLine; i++, pt += fieldSize)
          data[i].add(line.substring(pt, pt + fieldSize).trim());

      for (int iMo = 0; iMo < nThisLine; iMo++) {
        float[] coefs = new float[data[iMo].size()];
        int iCoeff = 0;
        while (iCoeff < coefs.length) {
          coefs[iCoeff] = parseFloat(data[iMo].get(iCoeff));
          iCoeff++;
        }
        mos[iMo].put("coefficients", coefs);
        mos[iMo].put("type", alphaBeta + " "            + (++iListed));
        ++moCount;
        Map<String, Object> mo = (moInfo == null ? null : moInfo.get(Integer
            .valueOf(isBeta ? -iListed : iListed)));
        if (mo != null)
          mos[iMo].putAll(mo);
        if (!betaOnly || isBeta)
          setMO(mos[iMo]);
      }
      line = "";
    }
    energyUnits = "a.u.";
    setMOData(false);
    return true;
  }

  /*
------------------------------------------------------------
EAF file 0: "./CeO2-ECP-RHF.aoints.0" size=19922944 bytes
------------------------------------------------------------
               write      read    awrite     aread      wait
               -----      ----    ------     -----      ----
     calls:       38        15         0       555       555
   data(b): 1.99e+07  7.86e+06  0.00e+00  2.91e+08
   time(s): 5.03e-02  3.82e-03  0.00e+00  1.17e-01  5.40e-05
rate(mb/s): 3.96e+02  2.06e+03  0.00e+00* 2.49e+03*
------------------------------------------------------------
* = Effective rate.  Full wait time used for read and write.

   */
  
  private boolean purging;
  @Override
  public String readLine() throws Exception {
    super.readLine();
    if (!purging && line != null && line.startsWith("--")) {
      purging = true;
      discardLinesUntilStartsWith("*");
      readLine();
      purging = false;
      super.readLine();
    }
    return line;
 }


}
