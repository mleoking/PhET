/* $RCSfile$
 * $Author: hansonr $
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

/* 
 * Copyright (C) 2009  Joerg Meyer, FHI Berlin
 *
 * Contact: meyer@fhi-berlin.mpg.de
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

package org.jmol.adapter.readers.xtal;

import org.jmol.adapter.smarter.*;
import org.jmol.util.Logger;

import javax.vecmath.Vector3f;

/**
 * CASTEP (http://www.castep.org) .cell file format
 *
 * relevant section of .cell file are included as comments below
 *
 * @author Joerg Meyer, FHI Berlin 2009 (meyer@fhi-berlin.mpg.de)
 * @version 1.2
 */

public class CastepReader extends AtomSetCollectionReader {

  private String[] tokens;

  private float a, b, c, alpha, beta, gamma;
  private Vector3f[] abc = new Vector3f[3];
  private boolean iHaveFractionalCoordinates;

  @Override
  public void initializeReader() throws Exception {
    
    while (tokenizeCastepCell() > 0) {

      if ((tokens.length >= 2) && (tokens[0].equalsIgnoreCase("%BLOCK"))) {

          /*
           * unit cell can only be set later
           * to get symmetry properly initialized -
           * see below!
           */

          /*
%BLOCK LATTICE_ABC
ang
  16.66566792 8.33283396  16.82438907
  90.0    90.0    90.0
%ENDBLOCK LATTICE_ABC
          */
        if (tokens[1].equalsIgnoreCase("LATTICE_ABC")) {
          readLatticeAbc();
          continue;
        }
          /*
%BLOCK LATTICE_CART
ang
  16.66566792 0.0   0.0
  0.0   8.33283396  0.0
  0.0   0.0   16.82438907
%ENDBLOCK LATTICE_CART
          */
        if (tokens[1].equalsIgnoreCase("LATTICE_CART")) {
          readLatticeCart();
          continue;
        }

          /* coordinates are set immediately */
          /*
%BLOCK POSITIONS_FRAC
   Pd         0.0 0.0 0.0
%ENDBLOCK POSITIONS_FRAC
          */
        if (tokens[1].equalsIgnoreCase("POSITIONS_FRAC")) {
          readPositionsFrac();
          iHaveFractionalCoordinates = true;
          continue;
        }
          /*
%BLOCK POSITIONS_ABS
ang
   Pd         0.00000000         0.00000000       0.00000000 
%ENDBLOCK POSITIONS_ABS
          */
        if (tokens[1].equalsIgnoreCase("POSITIONS_ABS")) {
          readPositionsAbs();
          iHaveFractionalCoordinates = false;
          continue;
        }
      }
    }
    continuing = false;
  }
  
  @Override
  protected void finalizeReader() throws Exception {
      
    doApplySymmetry = true;
    setFractionalCoordinates(iHaveFractionalCoordinates);
    // relay length of and angles between cell vectors to Jmol
    setUnitCell(a, b, c, alpha, beta, gamma);
    /*
     * IMPORTANT: 
     * also hand over (matrix of) unit cell vectors to trigger
     * the calculation of the correct transformation matrices
     * from cartesian to fractional coordinates (which are used
     * internally by Jmol)
     */
    float[] lv = new float[3];
    for (int n = 0; n < 3; n++) {
      abc[n].get(lv);
      addPrimitiveLatticeVector(n, lv, 0);
    }

    int nAtoms = atomSetCollection.getAtomCount();
    /*
     * this needs to be run either way (i.e. even if coordinates are already
     * fractional) - to satisfy the logic in AtomSetCollectionReader()
     */
    for (int n = 0; n < nAtoms; n++) {
      Atom atom = atomSetCollection.getAtom(n);
      setAtomCoord(atom);
    }
    super.finalizeReader();
  }

  private void readLatticeAbc() throws Exception {
    if (tokenizeCastepCell() == 0)
      return;
    float factor = readLengthUnit();
    if (tokens.length >= 3) {
      a = parseFloat(tokens[0]) * factor;
      b = parseFloat(tokens[1]) * factor;
      c = parseFloat(tokens[2]) * factor;
    } else {
      Logger
          .warn("error reading a,b,c in %BLOCK LATTICE_ABC in CASTEP .cell file");
      return;
    }

    if (tokenizeCastepCell() == 0)
      return;
    if (tokens.length >= 3) {
      alpha = parseFloat(tokens[0]);
      beta = parseFloat(tokens[1]);
      gamma = parseFloat(tokens[2]);
    } else {
      Logger
          .warn("error reading alpha,beta,gamma in %BLOCK LATTICE_ABC in CASTEP .cell file");
    }

    // initialize lattice vectors to NaN - since not present in .cell file
    for (int n = 0; n < 3; n++) {
      abc[n] = new Vector3f(Float.NaN, Float.NaN, Float.NaN);
    }
  }

  private void readLatticeCart() throws Exception {
    if (tokenizeCastepCell() == 0)
      return;
    float factor = readLengthUnit();
    float x, y, z;
    for (int n = 0; n < 3; n++) {
      if (tokens.length >= 3) {
        x = parseFloat(tokens[0]) * factor;
        y = parseFloat(tokens[1]) * factor;
        z = parseFloat(tokens[2]) * factor;
        abc[n] = new Vector3f(x, y, z);
      } else {
        Logger.warn("error reading coordinates of lattice vector "
            + Integer.toString(n + 1)
            + " in %BLOCK LATTICE_CART in CASTEP .cell file");
        return;
      }
      if (tokenizeCastepCell() == 0)
        return;
    }
    a = abc[0].length();
    b = abc[1].length();
    c = abc[2].length();
    alpha = (float) Math.toDegrees(abc[1].angle(abc[2]));
    beta = (float) Math.toDegrees(abc[2].angle(abc[0]));
    gamma = (float) Math.toDegrees(abc[0].angle(abc[1]));
  }

  private void readPositionsFrac() throws Exception {
    if (tokenizeCastepCell() == 0)
      return;
    readAtomData(1.0f);
  }

  private void readPositionsAbs() throws Exception {
    if (tokenizeCastepCell() == 0)
      return;
    float factor = readLengthUnit();
    readAtomData(factor);
  }

  /*
     to be kept in sync with Utilities/io.F90
  */
  private final static String[] lengthUnitIds = {
    "bohr", "m", "cm", "nm", "ang", "a0" };

  private final static float[] lengthUnitFactors = {
    ANGSTROMS_PER_BOHR, 1E10f, 1E8f, 1E1f, 1.0f, ANGSTROMS_PER_BOHR };

  private final static int lengthUnits = lengthUnitIds.length;

  private float readLengthUnit() throws Exception {
    float factor = 1.0f;
    for (int i=0; i<lengthUnits; i++) {
      if (tokens[0].equalsIgnoreCase(lengthUnitIds[i])) {
        factor = lengthUnitFactors[i];
        tokenizeCastepCell();
      }
    }
    return factor;
  }

  private void readAtomData(float factor) throws Exception {
    float x, y, z;
    do {
      if (tokens[0].equalsIgnoreCase("%ENDBLOCK"))
        break;
      if (tokens.length >= 4) {
        Atom atom = atomSetCollection.addNewAtom();
        x = parseFloat(tokens[1]) * factor;
        y = parseFloat(tokens[2]) * factor;
        z = parseFloat(tokens[3]) * factor;
        atom.set(x, y, z);
        atom.elementSymbol = tokens[0];
      } else {
        Logger.warn("cannot read line with CASTEP atom data: " + line);
      }
    } while (tokenizeCastepCell() > 0);
  }

  private int tokenizeCastepCell() throws Exception {
    while (true) {
      if (readLine() == null)
        return 0;
      if (line.trim().length() == 0)
        continue;
      tokens = getTokens();
      if (line.startsWith("#") || line.startsWith("!") || tokens[0].equals("#")
          || tokens[0].equals("!"))
        continue;
      break;
    }
    return tokens.length;
  }
}
