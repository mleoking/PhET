/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-10-15 17:34:01 -0500 (Sun, 15 Oct 2006) $
 * $Revision: 5957 $
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

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;


import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

import javax.vecmath.Point3f;

public class GromacsReader extends AtomSetCollectionReader {
  
  @Override
  protected void initializeReader() {
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("isPDB", Boolean.TRUE);
    atomSetCollection.newAtomSet();
    atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.TRUE);
  }
  
  @Override
  protected boolean checkLine() throws Exception {
      checkLineForScript();
      atomSetCollection.setAtomSetName(line.trim());
      readAtoms();
      readUnitCell();
      continuing = false;
      return false;
  }

   /*

"Check Your Input" (D. van der Spoel)
   59
    1TYR      N    1   1.521   1.583   2.009  0.0000  0.0000  0.0000
    1TYR     H1    2   1.421   1.595   2.009  0.0000  0.0000  0.0000
    1TYR     H2    3   1.548   1.533   1.927  0.0000  0.0000  0.0000
    1TYR     CA    4   1.585   1.713   2.009  0.0000  0.0000  0.0000
    1TYR     HA    5   1.555   1.760   1.926  0.0000  0.0000  0.0000
    1TYR     CB    6   1.541   1.791   2.133  0.0000  0.0000  0.0000

   */
  private void readAtoms() throws Exception {
    int modelAtomCount = parseInt(readLine());
    for (int i = 0; i < modelAtomCount; ++i) {
      readLine();
      int len = line.length();
      if (len != 44 && len != 68) {
        Logger.warn("line cannot be read for GROMACS atom data: " + line);
        continue;
      }
      Atom atom = new Atom();
      atom.sequenceNumber = parseInt(line, 0, 5);
      atom.group3 = parseToken(line, 5, 9).trim();  //allowing for 4 characters
      atom.atomName = line.substring(11, 15).trim();
      atom.atomSerial = parseInt(line, 15, 20);
      atom.x = parseFloat(line, 20, 28) * 10;
      atom.y = parseFloat(line, 28, 36) * 10;
      atom.z = parseFloat(line, 36, 44) * 10;
      if (Float.isNaN(atom.x) || Float.isNaN(atom.y) || Float.isNaN(atom.z)) {
        Logger.warn("line cannot be read for GROMACS atom data: " + line);
        atom.set(0, 0, 0);
      }
      setAtomCoord(atom);
      atom.elementSymbol = deduceElementSymbol(atom.group3, atom.atomName);
      if (!filterAtom(atom, i))
        continue;
      atom.isHetero = false;
      atomSetCollection.addAtom(atom);
      if (len < 69) 
        continue;
      float vx = parseFloat(line, 44, 52) * 10;
      float vy = parseFloat(line, 52, 60) * 10;
      float vz = parseFloat(line, 60, 68) * 10;
      if (Float.isNaN(vx) || Float.isNaN(vy) || Float.isNaN(vz))
        continue;
      atomSetCollection.addVibrationVector(atom.atomIndex, vx, vy, vz);
    }
  }

  String deduceElementSymbol(String group3, String atomName) {
    // best we can do
    if (atomName.length() <= 2 && group3.equals(atomName))
      return atomName;
    char ch1 = (atomName.length() == 4 ? atomName.charAt(0) : '\0');
    char ch2 = atomName.charAt(atomName.length() == 4 ? 1 : 0);
    boolean isHetero = JmolConstants.isHetero(group3);
    if (Atom.isValidElementSymbolNoCaseSecondChar(ch1, ch2))
      return (isHetero || ch1 != 'H' ? "" + ch1 + ch2 : "H");
    if (Atom.isValidElementSymbol(ch2))
      return "" + ch2;
    if (Atom.isValidElementSymbol(ch1))
      return "" + ch1;
    return "Xx";
  }

  private void readUnitCell() throws Exception {
    if (readLine() == null)
      return;
    String[] tokens = getTokens(line);
    if (tokens.length < 3 || !doApplySymmetry)
      return;
    float a = 10 * parseFloat(tokens[0]);
    float b = 10 * parseFloat(tokens[1]);
    float c = 10 * parseFloat(tokens[2]);
    setUnitCell(a, b, c, 90, 90, 90);
    setSpaceGroupName("P1");
    Atom[] atoms = atomSetCollection.getAtoms();
    Point3f pt = new Point3f(0.5f, 0.5f, 0.5f);
    for (int i = atomSetCollection.getAtomCount(); --i >= 0;) {
      setAtomCoord(atoms[i]);
      atoms[i].add(pt);
    }
  }


}

