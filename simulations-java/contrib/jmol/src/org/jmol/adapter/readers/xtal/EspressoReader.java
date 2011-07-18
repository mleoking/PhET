package org.jmol.adapter.readers.xtal;

/**
 * Piero Canepa
 * 
 * Quantum Espresso
 * http://www.quantum-espresso.org and http://qe-forge.org/frs/?group_id=10
 * @author Pieremanuele Canepa, Room 104, FM Group School of Physical Sciences,
 *         Ingram Building, University of Kent, Canterbury, Kent, CT2 7NH United
 *         Kingdom, pc229@kent.ac.uk
 * 
 * @version 1.0
 */

import org.jmol.adapter.smarter.Atom;
import org.jmol.adapter.smarter.AtomSetCollectionReader;

public class EspressoReader extends AtomSetCollectionReader {

  private float[] cellParams;
  private Double totEnergy;

  @Override
  protected void initializeReader() {
    setSpaceGroupName("P1");
    // inputOnly = checkFilter("INPUT");
  }

  @Override
  protected boolean checkLine() throws Exception {
    if (line.contains("lattice parameter (a_0)")) {
      readAparam();
    } else if (line.contains("crystal axes:")) {
      readCellParam(false);
    } else if (line.contains("CELL_PARAMETERS")) {
      readCellParam(true);
    } else if (line.contains("   Cartesian axes")) {
      if (doGetModel(++modelNumber))
        readAtomsCartesian();
    } else if (line.contains("ATOMIC_POSITIONS")) {
      if (doGetModel(++modelNumber))
        readAtomsFractional();
    } else if (line.contains("!    total energy")) {
      readEnergy();
    }
    return true;
  }

  private float aPar;

  private void readAparam() throws Exception {
    aPar = parseFloat(getTokens()[4]) * ANGSTROMS_PER_BOHR;
  }

  /*
  crystal axes: (cart. coord. in units of a_0)
            a(1) = (  1.000000  0.000000  0.000000 )  
            a(2) = ( -0.500000  0.866025  0.000000 )  
            a(3) = (  0.000000  0.000000  0.744955 )  

  reciprocal axes: (cart. coord. in units 2 pi/a_0)
            b(1) = (  1.000000  0.577350  0.000000 )  
            b(2) = (  0.000000  1.154701  0.000000 )  
            b(3) = (  0.000000  0.000000  1.342362 )  

   */

  /*  
  CELL_PARAMETERS (alat= 17.62853047)
  1.019135101   0.000000000   0.000000000
  -0.509567550   0.882596887   0.000000000
  0.000000000   0.000000000   0.737221415

   */

  private void readCellParam(boolean andAPar) throws Exception {
    int i0 = (andAPar ? 0 : 3);

    /*    in the old version of Espresso optimized cell parameters
    are expressed in function of the original aPar stored at the beginning
    CELL_PARAMETERS (alat)
    1.001108280   0.000000000   0.000000000
    0.000000000   1.001108280   0.000000000
    0.000000000   0.000000000   1.301023011
    So we also check for = in the line 
     *
     */

    if (andAPar && line.contains("="))
      aPar = parseFloat(line.substring(line.indexOf("=") + 1))
          * ANGSTROMS_PER_BOHR;

    //Can you look at the example HAP_fullopt_40_r1.fullopt from the 2nd model on the representation is correct 
    //The 1st is wrong. 
    // BH: It's just a bad starting geometry, but the program 
    //     very nicely cleans it up in just one step.
    //PC this is not true as it happens for every single jobs and even for single SCF calculations

    cellParams = new float[9];
    for (int n = 0, i = 0; n < 3; n++) {
      String[] tokens = getTokens(readLine());
      cellParams[i++] = parseFloat(tokens[i0]) * aPar;
      cellParams[i++] = parseFloat(tokens[i0 + 1]) * aPar;
      cellParams[i++] = parseFloat(tokens[i0 + 2]) * aPar;
    }
  }

  /*
  Cartesian axes

    site n.     atom                  positions (a_0 units)
        1           Ca  tau(  1) = (   0.5000000  -0.2886751  -0.0018296  )
        2           Ca  tau(  2) = (  -0.5000000   0.2886751   0.3706481  )
        3           Ca  tau(  3) = (  -0.5000000   0.2886751   0.0001849  )
        4           Ca  tau(  4) = (   0.5000000  -0.2886751  -0.3722928  )
        5           Ca  tau(  5) = (   0.2493986  -0.0086855   0.1856962  )
        6           Ca  tau(  6) = (  -0.2493986   0.0086855  -0.1867815  )
        7           Ca  tau(  7) = (  -0.1171775   0.2203283   0.1856962  )
        8           Ca  tau(  8) = (  -0.1322212  -0.2116428   0.1856962  )
        9           Ca  tau(  9) = (   0.1322212   0.2116428  -0.1867815  )
       10           Ca  tau( 10) = (   0.1171775  -0.2203283  -0.1867815  )
   */
  private void readAtomsCartesian() throws Exception {
    setFractionalCoordinates(false);
    newAtomSet();
    discardLines(2);
    while (readLine() != null && (line.indexOf("(")) >= 0) {
      String[] tokens = getTokens();
      Atom atom = atomSetCollection.addNewAtom();
      atom.atomName = tokens[1];
      // here the coordinates are a_lat there fore expressed on base of cube of side a 
      float x = parseFloat(tokens[tokens.length - 4]) * aPar;
      float y = parseFloat(tokens[tokens.length - 3]) * aPar;
      float z = parseFloat(tokens[tokens.length - 2]) * aPar;
      /* not for now
      if (x < 0)
        x += 1;
      if (y < 0)
        y += 1;
      if (z < 0)
        z += 1;
       */
      setAtomCoord(atom, x, y, z);
    }
    applySymmetryAndSetTrajectory();
  }

  private void newAtomSet() throws Exception {
    atomSetCollection.newAtomSet();
    if (totEnergy != null)
      setEnergy();
    setCellParams();
  }

  private void setCellParams() throws Exception {
    if (cellParams != null) {
      addPrimitiveLatticeVector(0, cellParams, 0);
      addPrimitiveLatticeVector(1, cellParams, 3);
      addPrimitiveLatticeVector(2, cellParams, 6);
    }
  }

  private void readAtomsFractional() throws Exception {

    /*    
     * some just end with a blank line; others end with a short phrase:
     * 
        O       -0.088707198  -0.347657305   0.434774168
        O       -0.258950107   0.088707198   0.434774168
        O        0.000000000   0.000000000  -0.214003341
        O        0.000000000   0.000000000   0.286225136
        H        0.000000000   0.000000000  -0.071496337
        H        0.000000000   0.000000000   0.428733409
        End final coordinates
     */

    boolean isFractional = !line.contains("bohr");
    setFractionalCoordinates(isFractional);
    float factor = (isFractional ? 1 : ANGSTROMS_PER_BOHR);
    newAtomSet();
    while (readLine() != null && line.length() > 45) {
      String[] tokens = getTokens();
      Atom atom = atomSetCollection.addNewAtom();
      atom.atomName = tokens[0];
      //Here the coordinates are in BOHR
      float x = parseFloat(tokens[1]) * factor;
      float y = parseFloat(tokens[2]) * factor;
      float z = parseFloat(tokens[3]) * factor;
      // This we can't do, at least not by default....
      // None of the other readers do this. So let's think
      // about why you are feeling it is important. 
      // What's the case that is the problem? This is what
      // I call "packed" -- it's certainly an option, but
      // it's not the default. Please see if PACKED gets you
      // what you want.
      /*  (for now) 
      if (x < 0)
        x += 1;
      if (y < 0)
        y += 1;
      if (z < 0)
        z += 1;
       */
      setAtomCoord(atom, x, y, z);
    }
    applySymmetryAndSetTrajectory();
  }

  //!    total energy              =   -1668.20791579 Ry

  private void readEnergy() throws Exception {
    totEnergy = Double.valueOf(Double.parseDouble(getTokens(line.substring(line
        .indexOf("=") + 1))[0]));
  }

  private void setEnergy() {
    atomSetCollection.setAtomSetEnergy("" + totEnergy, totEnergy.floatValue());
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("Energy", totEnergy);
    atomSetCollection.setAtomSetName("E = " + totEnergy + " Ry");
  }
}
