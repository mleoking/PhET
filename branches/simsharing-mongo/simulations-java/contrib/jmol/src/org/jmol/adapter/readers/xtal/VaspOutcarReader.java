/* $RCSfile$
 * $Author: hansonr $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development, www.jmol.org
 *
 * Contact: jmol-developers@lists.sf.net
 *
 * Copyright (C) 2009  Piero Canepa, University of Kent, UK
 *
 * Contact: pc229@kent.ac.uk or pieremanuele.canepa@gmail.com
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
 *
 */

package org.jmol.adapter.readers.xtal;

import java.util.ArrayList;

import org.jmol.adapter.smarter.Atom;
import org.jmol.adapter.smarter.AtomSetCollectionReader;
import org.jmol.util.TextFormat;

/**
 * http://cms.mpi.univie.ac.at/vasp/
 * @author Pieremanuele Canepa, Room 104, FM Group School of Physical Sciences,
 *         Ingram Building, University of Kent, Canterbury, Kent, CT2 7NH United
 *         Kingdom, pc229@kent.ac.uk
 * 
 * @version 1.0
 */

public class VaspOutcarReader extends AtomSetCollectionReader {

  private String[] atomNames;
  private ArrayList<String> elementNames; //this array is to store the name of the element 
  private int atomCount = 0;
  private boolean inputOnly;
  private boolean mDsimulation = false; //this is for MD simulations
  private boolean isVersion5 = false;

  @Override
  protected void initializeReader() {
    setSpaceGroupName("P1");
    setFractionalCoordinates(true);
    inputOnly = checkFilter("INPUT");
  }

  @Override
  protected boolean checkLine() throws Exception {

    //reads if output is from vasp5
    if (line.contains(" vasp.5")) {
      isVersion5 = true;
    } else if (line.toUpperCase().contains("INCAR:")) {
      //reads the kind of atoms namely H, Ca etc
      readElementNames();
    } else if (line.contains("ions per type")) {
      readAtomCountAndSetNames();
    } else if (line.contains("molecular dynamics for ions")) {
      mDsimulation = true;
    } else if (line.contains("direct lattice vectors")) {
      readUnitCellVectors();
    } else if (line.contains("position of ions in fractional coordinates")) {
      readInitialCoordinates();
      if (inputOnly)
        continuing = false;
    } else if (line.contains("POSITION")) {
      readPOSITION();
      return true;
    } else if (line.startsWith("  FREE ENERGIE") && !mDsimulation) {
      readEnergy();
    } else if (line.contains("ENERGIE OF THE ELECTRON-ION-THERMOSTAT")
        && mDsimulation) {
      readMdyn();
    } else if (line
        .startsWith(" Eigenvectors and eigenvalues of the dynamical matrix")) {
      readFrequency();
    }
    return true;
  }

  @Override
  protected void finalizeReader() throws Exception {
    setSymmetry();
  }

  /*  
    POTCAR:    PAW H                                 
    POTCAR:    PAW O                                 
    POTCAR:    PAW Pd                                
    POTCAR:    PAW H                                 
      VRHFIN =H: ultrasoft test 
   */
  private void readElementNames() throws Exception {
    elementNames = new ArrayList<String>();
    String elementList = "";
    while (readLine() != null && line.indexOf("VRHFIN") < 0) {
      int pt = (line.contains("_") ? 2 : 1);
      if (pt == 2)
        line = line.replace("_", " ");
      String[] tokens = getTokens(line.substring(line.indexOf(":") + 1));
      String sym = tokens[pt];
      String key = ";" + sym + ";";
      if (elementList.indexOf(key) >= 0)
        continue;
      elementList += key;
      elementNames.add(sym);
    }
  }

  /*  
  Dimension of arrays:
    k-Points           NKPTS =     10   number of bands    NBANDS=     16
    number of dos      NEDOS =    301   number of ions     NIONS =      8
    non local maximal  LDIM  =      4   non local SUM 2l+1 LMDIM =      8
    total plane-waves  NPLWV =  74088
    max r-space proj   IRMAX =   5763   max aug-charges    IRDMAX=  42942
    dimension x,y,z NGX =    42 NGY =   42 NGZ =   42
    dimension x,y,z NGXF=    84 NGYF=   84 NGZF=   84
    support grid    NGXF=    84 NGYF=   84 NGZF=   84
    ions per type =               6   2*/

  private void readAtomCountAndSetNames() throws Exception {
    int[] numofElement = new int[100];
    //    readLine();
    String[] tokens = getTokens(line.substring(line.indexOf("=") + 1));
    atomCount = 0;
    for (int i = 0; i < tokens.length; i++)
      atomCount += (numofElement[i] = parseInt(tokens[i].trim()));
    //this is to reconstruct the atomMappedarray containing the atom
    atomNames = new String[atomCount];
    int nElements = elementNames.size();
    for (int pt = 0, i = 0; i < nElements; i++)
      for (int j = 0; j < numofElement[i]; j++)
        atomNames[pt++] = elementNames.get(i);
  }

  /*direct lattice vectors                 reciprocal lattice vectors
    1.850000000  1.850000000  0.000000000     0.270270270  0.270270270 -0.270270270
    0.000000000  1.850000000  1.850000000    -0.270270270  0.270270270  0.270270270
    1.850000000  0.000000000  1.850000000     0.270270270 -0.270270270  0.270270270*/

  private float[] unitCellData = new float[18];

  private void readUnitCellVectors() throws Exception {
    if (atomSetCollection.getAtomCount() > 0) {
      setSymmetry();
      atomSetCollection.newAtomSet();
      setAtomSetInfo();
    }
    fillFloatArray(unitCellData, null, 0);
    setUnitCell();
  }

  private void setUnitCell() {
    addPrimitiveLatticeVector(0, unitCellData, 0);
    addPrimitiveLatticeVector(1, unitCellData, 6);
    addPrimitiveLatticeVector(2, unitCellData, 12);
  }

  private void setSymmetry() throws Exception {
    applySymmetryAndSetTrajectory();
    setSpaceGroupName("P1");
    setFractionalCoordinates(false);
  }

  /*
  position of ions in fractional coordinates (direct lattice) 
  0.87800000  0.62200000  0.25000000
  0.25000000  0.87800000  0.62200000
  0.62200000  0.25000000  0.87800000
  0.12200000  0.37800000  0.75000000
  0.75000000  0.12200000  0.37800000
  0.37800000  0.75000000  0.12200000
  0.00000000  0.00000000  0.00000000
  0.50000000  0.50000000  0.50000000

  position of ions in cartesian coordinates
   */
  ///This is the initial geometry not the geometry during the geometry dump
  private void readInitialCoordinates() throws Exception {
    int counter = 0;
    while (readLine() != null && line.length() > 10) {
      Atom atom = atomSetCollection.addNewAtom();
      String[] tokens = getTokens();
      atom.atomName = atomNames[counter++];
      float x = parseFloat(tokens[0]);
      float y = parseFloat(tokens[1]);
      float z = parseFloat(tokens[2]);
      setAtomCoord(atom, x, y, z);
    }
    atomSetCollection.setAtomSetName("Initial Coordinates");
  }

  /*  
  POSITION                                       TOTAL-FORCE (eV/Angst)
  -----------------------------------------------------------------------------------
       1.14298      0.83102      6.90311        -0.003060      0.001766      0.000000
      -1.29117      0.57434      6.90311         0.000000     -0.003533      0.000000
       0.14819     -1.40536      6.90311         0.003060      0.001766      0.000000
      -1.14298     -0.83102      4.93079         0.003060     -0.001766      0.000000
       1.29117     -0.57434      4.93079         0.000000      0.003533      0.000000
      -0.14819      1.40536      4.93079        -0.003060     -0.001766      0.000000
       0.00000      0.00000      0.00000         0.000000      0.000000      0.000000
       0.00000      0.00000      5.91695         0.000000      0.000000      0.000000
  -----------------------------------------------------------------------------------
   */
  private void readPOSITION() throws Exception {
    int counter = 0;
    discardLines(1);
    while (readLine() != null && line.indexOf("----------") < 0) {
      Atom atom = atomSetCollection.addNewAtom();
      String[] tokens = getTokens();
      atom.atomName = atomNames[counter];
      float x = parseFloat(tokens[0]);
      float y = parseFloat(tokens[1]);
      float z = parseFloat(tokens[2]);
      setAtomCoord(atom, x, y, z);
      counter++;
    }
  }

  /*  FREE ENERGIE OF THE ION-ELECTRON SYSTEM (eV)
  ---------------------------------------------------
  free  energy   TOTEN  =       -20.028155 eV

  energy  without entropy=      -20.028155  energy(sigma->0) =      -20.028155
   */

  private Double gibbsEnergy, gibbsEntropy;

  private void readEnergy() throws Exception {
    readLine();
    String[] tokens = getTokens(readLine());
    gibbsEnergy = Double.valueOf(Double.parseDouble(tokens[4]));
    readLine();
    tokens = getTokens(readLine());
    /* please double-check:

    entropy T*S    EENTRO =        -0.01255935
    eigenvalues    EBANDS =        27.21110509
    atomic energy  EATOM  =       181.97672381
    ---------------------------------------------------
    free energy    TOTEN  =        35.37614365 eV

    energy without entropy =       35.38870300  energy(sigma->0) =       35.38242333

     * G = H - TS, so TS = H - G
     * 
     * My reading of this is that TOTEN is G,
     * "energy without entropy" is H, and so the "T*S" line is
     * actually -T*S, not T*S.
     * 
     * Can that be?
     * 
     * Bob
     * 
     * 
     */
    double enthalpy = Double.parseDouble(tokens[3]);
    gibbsEntropy = Double.valueOf(enthalpy - gibbsEnergy.doubleValue());
  }

  private void setAtomSetInfo() {
    if (gibbsEnergy == null)
      return;
    atomSetCollection.setAtomSetEnergy("" + gibbsEnergy,
        gibbsEnergy.floatValue());
    atomSetCollection.setAtomSetAuxiliaryInfo("Energy", gibbsEnergy);
    atomSetCollection.setAtomSetAuxiliaryInfo("Entropy", gibbsEntropy);
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("Energy", gibbsEnergy);
    atomSetCollection
        .setAtomSetCollectionAuxiliaryInfo("Entropy", gibbsEntropy);
    atomSetCollection.setAtomSetName("G = " + gibbsEnergy + " eV, T*S = "
        + gibbsEntropy + " eV");
  }

  private Double electronEne, kinEne, totEne;
  private float temp;

  private void readMdyn() throws Exception {
    String[] tokens = getTokens();
    readLine();
    tokens = getTokens(readLine());
    electronEne = Double.valueOf(Double.parseDouble(tokens[4]));
    tokens = getTokens(readLine());
    kinEne = Double.valueOf(Double.parseDouble(tokens[4]));
    temp = parseFloat(tokens[6]);
    discardLines(3);
    tokens = getTokens(readLine());
    totEne = Double.valueOf(Double.parseDouble(tokens[4]));
    setAtomSetInfoMd();
  }

  private void setAtomSetInfoMd() {
    atomSetCollection.setAtomSetName("Temp. = "
        + TextFormat.formatDecimal((temp), 2) + " K, Energy = " + totEne
        + " eV");
    atomSetCollection.setAtomSetAuxiliaryInfo("Energy", totEne);
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("Energy", totEne);
    atomSetCollection.setAtomSetAuxiliaryInfo("EleEnergy", kinEne);
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("EleEnergy",
        electronEne);
    atomSetCollection.setAtomSetAuxiliaryInfo("Kinetic", electronEne);
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("Kinetic", kinEne);
    atomSetCollection.setAtomSetAuxiliaryInfo("Temperature",
        TextFormat.formatDecimal((temp), 2));
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("Temperature",
        TextFormat.formatDecimal((temp), 2));
  }

  /*  
    Eigenvectors after division by SQRT(mass)   ///This is lines is not there in VASP5

    Eigenvectors and eigenvalues of the dynamical matrix
    ----------------------------------------------------


      1 f  =   61.880092 THz   388.804082 2PiTHz 2064.097613 cm-1   255.915580 meV
                X         Y         Z           dx          dy          dz
         1.154810  0.811470  6.887910     0.142328    0.284744    0.218505  
        -1.280150  0.594360  6.887910    -0.325017   -0.005914    0.256184  
         0.125350 -1.405820  6.887910     0.147813   -0.308571    0.250139  
        -1.154810 -0.811470  4.919940    -0.142322   -0.284738   -0.218502  
         1.280150 -0.594360  4.919940     0.325007    0.005917   -0.256181  
        -0.125350  1.405820  4.919940    -0.147808    0.308554   -0.250128  
         0.000000  0.000000  0.000000     0.000000    0.000000    0.000000  
         0.000000  0.000000  5.903930     0.000000    0.000000    0.000000  

      2 f  =   56.226671 THz   353.282596 2PiTHz 1875.519821 cm-1   232.534905 meV
                X         Y         Z           dx          dy          dz
         1.154810  0.811470  6.887910     0.005057   -0.193034   -0.074407  
        -1.280150  0.594360  6.887910    -0.215700   -0.046660    0.255430  
         0.125350 -1.405820  6.887910    -0.155522    0.437794   -0.342377  
        -1.154810 -0.811470  4.919940     0.005053   -0.193045   -0.074415  
         1.280150 -0.594360  4.919940    -0.215675   -0.046662    0.255412  
        -0.125350  1.405820  4.919940    -0.155521    0.437777   -0.342368  
         0.000000  0.000000  0.000000     0.015694    0.002977    0.005811  
         0.000000  0.000000  5.903930     0.011571   -0.016988    0.006533 



   */
  private void readFrequency() throws Exception {

    int pt = atomSetCollection.getCurrentAtomSetIndex();
    int iAtom0 = atomSetCollection.getAtomCount();

    if (isVersion5) {
      discardLines(3);
    } else {
      discardLinesUntilContains("Eigenvectors after division by SQRT(mass)");
      discardLines(5);
    }

    boolean[] ignore = new boolean[1];
    while (readLine() != null
        && (line.contains("f  = ") || line.contains("f/i= "))) {
      cloneLastAtomSet(-1);
      if (!ignore[0]) {
        atomSetCollection.setCurrentAtomSetIndex(++pt);
        atomSetCollection.setAtomSetFrequency(null, null,
            line.substring(line.indexOf("2PiTHz") + 6, line.indexOf("c") - 1)
                .trim(), null);
      }
      readLine();
      fillFrequencyData(iAtom0, atomCount, atomCount, ignore, true, 35, 12,
          null);
      iAtom0 += atomCount;
      readLine();
    }
  }
}
