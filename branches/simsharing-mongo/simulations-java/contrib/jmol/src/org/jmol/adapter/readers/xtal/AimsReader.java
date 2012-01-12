/* $RCSfile$
 * $Author: hansonr $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development, www.jmol.org
 *
 * Contact: jmol-developers@lists.sf.net
 *
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
 *
 */

package org.jmol.adapter.readers.xtal;

import org.jmol.adapter.smarter.*;
import org.jmol.util.Logger;

import javax.vecmath.Vector3f;

/**
 * FHI-aims (http://www.fhi-berlin.mpg.de/aims) geometry.in file format
 *
 * samples of relevant lines in geometry.in file are included as comments below
 *
 * @author Joerg Meyer, FHI Berlin 2009 (meyer@fhi-berlin.mpg.de)
 * @version 1.2
 * 
 */

public class AimsReader extends AtomSetCollectionReader {

  private int nLatticeVectors = 0;
  private Vector3f[] latticeVectors = new Vector3f[3];

  @Override
  protected boolean checkLine() {
    String[] tokens = getTokens();
    float x, y, z;
    if (tokens.length == 0)
      return true;
    /*
     * atom 2.750645380 2.750645380 25.000000000 Pd
     */
    if (tokens[0].equals("atom")) {
      if (tokens.length < 5) {
        Logger.warn("cannot read line with FHI-aims atom data: " + line);
      } else {
        x = parseFloat(tokens[1]);
        y = parseFloat(tokens[2]);
        z = parseFloat(tokens[3]);
        String symbol = tokens[4];
        Atom atom = atomSetCollection.addNewAtom();
        atom.set(x, y, z);
        atom.elementSymbol = symbol;
      }
      return true;
    }

    /*
     * multipole 2.750645380 2.750645380 25.000000000 0 46.0
     */
    if (tokens[0].equals("multipole")) {
      if (tokens.length < 6) {
        Logger.warn("cannot read line with FHI-aims atom data: " + line);
      } else {
        x = parseFloat(tokens[1]);
        y = parseFloat(tokens[2]);
        z = parseFloat(tokens[3]);
        int order = parseInt(tokens[4]);
        float charge = parseFloat(tokens[5]);
        if (order > 0) {
          Logger
              .warn("multipole line ignored since only monopoles are currently supported: "
                  + line);
          return true;
        }
        Atom atom = atomSetCollection.addNewAtom();
        atom.set(x, y, z);
        atom.partialCharge = charge;
        atom.formalCharge = Math.round(charge);
      }
      return true;
    }

    /*
     * lattice_vector 16.503872273 0.000000000 0.000000000
     */
    if (tokens[0].equals("lattice_vector")) {
      if (tokens.length < 4) {
        Logger.warn("cannot read line with FHI-aims lattice vector: " + line);
      } else if (nLatticeVectors > 2) {
        Logger.warn("more than 3 FHI-aims lattice vectors found with line: "
            + line);
      } else {
        x = parseFloat(tokens[1]);
        y = parseFloat(tokens[2]);
        z = parseFloat(tokens[3]);
        latticeVectors[nLatticeVectors] = new Vector3f(x, y, z);
      }
      nLatticeVectors++;
      return true;
    }
    return true;
  }

  @Override
  protected void finalizeReader() throws Exception {
    /*
     * translational symmetry in less than three directions is currently neither
     * supported by Jmol nor FHI-aims
     */

    if (nLatticeVectors == 3) {

      /*
       * enforce application of (translational!) symmetry in Jmol (in case of
       * crystal lattice being present in the FHI-aims input file)
       * 
       * coordinates in FHI-aims input file are not fractional
       * 
       * transformation of lattice represenation (i.e. some linear algebra) is
       * done in separate (recyclable) routine below
       */

      doApplySymmetry = true;
      setFractionalCoordinates(false);
      setUnitCellFromLatticeVectors(latticeVectors);

      /*
       * better do not hardcode as other interpreted to be given in file
       * setSpaceGroupName("P1");
       */

      /*
       * this takes care of properly converting cartesian coordinates to
       * fractionals when necessary (i.e. use of symmetry is turned on) - and is
       * crucial for unit cell being internally 'accepted' as such (as indicated
       * in AtomSetCollectionReader.java) doing this above (when adding atoms)
       * is not sufficient, as a unit cell has (deliberately!) not yet been
       * added then
       */
      int nAtoms = atomSetCollection.getAtomCount();
      for (int n = 0; n < nAtoms; n++) {
        Atom atom = atomSetCollection.getAtom(n);
        setAtomCoord(atom);
      }
    }
    super.finalizeReader();
  }

  private void setUnitCellFromLatticeVectors(Vector3f[] abc) {
    float a = abc[0].length();
    float b = abc[1].length();
    float c = abc[2].length();
    float alpha = (float) Math.toDegrees(abc[1].angle(abc[2]));
    float beta = (float) Math.toDegrees(abc[2].angle(abc[0]));
    float gamma = (float) Math.toDegrees(abc[0].angle(abc[1]));
    // relay length of and angles between cell vectors to Jmol
    setUnitCell(a, b, c, alpha, beta, gamma);
    /*
     * IMPORTANT: also hand over (matrix of) unit cell vectors to trigger the
     * calculation of the correct transformation matrices from cartesian to
     * fractional coordinates (which are used internally by Jmol)
     */
    float[] lv = new float[3];
    for (int n = 0; n < 3; n++) {
      abc[n].get(lv);
      addPrimitiveLatticeVector(n, lv, 0);
    }
  }
}
