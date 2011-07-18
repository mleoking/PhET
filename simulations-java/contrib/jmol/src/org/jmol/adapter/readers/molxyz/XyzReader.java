/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-10-22 14:12:46 -0500 (Sun, 22 Oct 2006) $
 * $Revision: 5999 $
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

package org.jmol.adapter.readers.molxyz;

import org.jmol.adapter.smarter.*;
import org.jmol.api.JmolAdapter;

import org.jmol.util.Logger;

/**
 * Minnesota SuperComputer Center XYZ file format
 * 
 * simple symmetry extension via load command:
 * 9/2006 hansonr@stolaf.edu
 * 
 *  setAtomCoord(atom)
 *  applySymmetryAndSetTrajectory()
 *  
 *  extended to read XYZI files (Bob's invention -- allows isotope numbers)
 * 
 * extended to read XYZ files with fractional charges as, for example:
 * 
 * http://www.ccl.net/cca/software/SOURCES/FORTRAN/molden/test/reacpth.xyz
 * 
 * http://web.archive.org/web/20000120031517/www.msc.edu/msc/docs/xmol/v1.3/g94toxyz.c
 * 
 * 
 * 
 */

public class XyzReader extends AtomSetCollectionReader {

  @Override
  protected boolean checkLine() throws Exception {
    int modelAtomCount = parseInt(line);
    if (modelAtomCount == Integer.MIN_VALUE) {
      continuing = false;
      return false;
    }

    // models and vibrations are the same for XYZ files
    vibrationNumber = ++modelNumber;
    if (desiredVibrationNumber <= 0 ? doGetModel(modelNumber)
        : doGetVibration(vibrationNumber)) {
      readLine();
      checkLineForScript();
      atomSetCollection.newAtomSet();
      atomSetCollection.setAtomSetName(line);
      readAtoms(modelAtomCount);
      applySymmetryAndSetTrajectory();
      if (isLastModel(modelNumber)) {
        continuing = false;
        return false;
      }
    } else {
      skipAtomSet(modelAtomCount);
    }
    discardLinesUntilNonBlank();
    return false;
  }

  private void skipAtomSet(int modelAtomCount) throws Exception {
    readLine(); //comment
    for (int i = modelAtomCount; --i >= 0;)
      readLine(); //atoms
  }

  private void readAtoms(int modelAtomCount) throws Exception {
    for (int i = 0; i < modelAtomCount; ++i) {
      readLine();
      String[] tokens = getTokens();
      if (tokens.length < 4) {
        Logger.warn("line cannot be read for XYZ atom data: " + line);
        continue;
      }
      Atom atom = atomSetCollection.addNewAtom();
      String str = tokens[0];
      int isotope = parseInt(str);
      // xyzI
      if (isotope == Integer.MIN_VALUE) {
        atom.elementSymbol = str;
      } else {
        str = str.substring(("" + isotope).length());
        atom.elementNumber = (short) (str.length() == 0 ? isotope
            : ((isotope << 7) + JmolAdapter.getElementNumber(str)));
      }
      atom.x = parseFloat(tokens[1]);
      atom.y = parseFloat(tokens[2]);
      atom.z = parseFloat(tokens[3]);
      if (Float.isNaN(atom.x) || Float.isNaN(atom.y) || Float.isNaN(atom.z)) {
        Logger.warn("line cannot be read for XYZ atom data: " + line);
        atom.set(0, 0, 0);
      }
      int vpt = 4;
      setAtomCoord(atom);
      switch (tokens.length) {
      case 4:
        continue;
      case 5:
      case 6:
      case 8:
      case 9:
        // accepts  sym x y z c
        // accepts  sym x y z c r
        // accepts  sym x y z c vx vy vz
        // accepts  sym x y z c vx vy vz atomno
        if ((str = tokens[4]).indexOf(".") >= 0) {
          atom.partialCharge = parseFloat(str);
        } else {
          int charge = parseInt(str);
          if (charge != Integer.MIN_VALUE)
            atom.formalCharge = charge;
        }
        switch (tokens.length) {
        case 5:
          continue;
        case 6:
          atom.radius = parseFloat(tokens[5]);
          continue;
        case 9:
          atom.atomSerial = parseInt(tokens[8]);
        }
        vpt++;
        //fall through:
      default:
        // or       sym x y z vx vy vz
        float vx = parseFloat(tokens[vpt++]);
        float vy = parseFloat(tokens[vpt++]);
        float vz = parseFloat(tokens[vpt++]);
        if (Float.isNaN(vx) || Float.isNaN(vy) || Float.isNaN(vz))
          continue;
        atomSetCollection.addVibrationVector(atom.atomIndex, vx, vy, vz);
      }
    }
  }
}
