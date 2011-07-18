/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-07-14 18:41:50 -0500 (Fri, 14 Jul 2006) $
 * $Revision: 5311 $
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
import org.jmol.api.JmolAdapter;

import org.jmol.util.Logger;

/*
 * Wavefunction INPUT section reader
 * 
 */

public abstract class SpartanInputReader extends BasisFunctionReader {

  protected String modelName;
  protected int modelAtomCount;
  protected int atomCount;
  protected String bondData = "";

  protected void readInputRecords() throws Exception {
    int atomCount0 = atomCount;
      readInputHeader();
      while (readLine() != null) {
        String[] tokens = getTokens();
        //charge and spin
        if (tokens.length == 2 && parseInt(tokens[0]) != Integer.MIN_VALUE && parseInt(tokens[1]) >= 0)
          break;
      }
      if (line == null)
        return;
      readInputAtoms();
      discardLinesUntilContains("ATOMLABELS");
      if (line != null)
        readAtomNames();
      if (modelAtomCount > 1) {
        discardLinesUntilContains("HESSIAN");
        if (line != null)
          readBonds(atomCount0);
        if (line != null && line.indexOf("BEGINCONSTRAINTS") >= 0)
          readConstraints();
      }
      while (line != null && line.indexOf("END ") < 0 && line.indexOf("MOLSTATE") < 0)
        readLine();
      if (line != null && line.indexOf("MOLSTATE") >= 0)
        readTransform();
      if (atomSetCollection.getAtomCount() > 0)
        atomSetCollection.setAtomSetName(modelName);
  }

  String constraints = "";
  private void readConstraints() throws Exception {
    constraints = "";
    while (readLine() != null && line.indexOf("END") < 0)
      constraints += (constraints == "" ? "" : "\n") + line;
    readLine();
    if (constraints.length() == 0)
      return;
    atomSetCollection.setAtomSetAuxiliaryInfo("constraints", constraints);
    atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY, "EnergyProfile");
    atomSetCollection.setAtomSetProperty("Constraint", constraints);
  }

  private void readTransform() throws Exception {
    readLine();
    String[] tokens = getTokens(readLine() + " " + readLine());
    //BEGINMOLSTATE
    //MODEL=3~HYDROGEN=1~LABELS=0
    //0.70925283  0.69996750 -0.08369886  0.00000000 -0.70480913  0.70649898 -0.06405880  0.00000000
    //0.01429412  0.10442561  0.99443018  0.00000000  0.00000000  0.00000000  0.00000000  1.00000000
    //ENDMOLSTATE
    setTransform(
        parseFloat(tokens[0]), parseFloat(tokens[1]), parseFloat(tokens[2]),
        parseFloat(tokens[4]), parseFloat(tokens[5]), parseFloat(tokens[6]),
        parseFloat(tokens[8]), parseFloat(tokens[9]), parseFloat(tokens[10])
    );
  }
  
  private void readInputHeader() throws Exception {
    while (readLine() != null
        && !line.startsWith(" ")) {}
    readLine();
    modelName = line + ";";
    modelName = modelName.substring(0, modelName.indexOf(";")).trim();
  }
  
  private void readInputAtoms() throws Exception {
    modelAtomCount = 0;
    while (readLine() != null
        && !line.startsWith("ENDCART")) {
      String[] tokens = getTokens();
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementSymbol = getElementSymbol(parseInt(tokens[0]));
      setAtomCoord(atom, parseFloat(tokens[1]), parseFloat(tokens[2]), parseFloat(tokens[3]));
      modelAtomCount++;
    }
    atomCount = atomSetCollection.getAtomCount();
    if (Logger.debugging)
      Logger.debug(atomCount + " atoms read");
  }

  private void readAtomNames() throws Exception {
    int atom0 = atomCount - modelAtomCount;
    // note that atomSetCollection.isTrajectory() gets set onlyAFTER an input is
    // read.
    for (int i = 0; i < modelAtomCount; i++) {
      line = readLine().trim();
      String name = line.substring(1, line.length() - 1);
      atomSetCollection.getAtom(atom0 + i).atomName = name;
    }
  }
  
  private void readBonds(int atomCount0) throws Exception {
    int nAtoms = modelAtomCount;
    /*
     <one number per atom>
     1    2    1
     1    3    1
     1    4    1
     1    5    1
     1    6    1
     1    7    1
     */
    bondData = ""; //used for frequency business
    while (readLine() != null && !line.startsWith("ENDHESS")) {
      String[] tokens = getTokens();
      bondData += line + " ";
      if (nAtoms == 0) {
        int sourceIndex = parseInt(tokens[0]) - 1 + atomCount0;
        int targetIndex = parseInt(tokens[1]) - 1 + atomCount0;
        int bondOrder = parseInt(tokens[2]);
        if (bondOrder > 0) {
          atomSetCollection.addBond(new Bond(sourceIndex, targetIndex,
              bondOrder < 4 ? bondOrder : bondOrder == 5 ? JmolAdapter.ORDER_AROMATIC : 1));
        }
      } else {
        nAtoms -= tokens.length;
      }
    }
    readLine();
    if (Logger.debugging)
      Logger.debug(atomSetCollection.getBondCount() + " bonds read");
  }
}
