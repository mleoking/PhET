/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-07-05 00:45:22 -0500 (Wed, 05 Jul 2006) $
 * $Revision: 5271 $
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

import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import java.util.Map;

import javax.vecmath.Vector3f;

import org.jmol.util.Logger;

class SpartanArchive {
  
  // OK, a bit of a hack.
  // It's not a reader, but it needs the capabilities of BasisFunctionReader
  

  private int atomCount = 0;
  private String bondData; // not in archive; may or may not have
  private int moCount = 0;
  private int coefCount = 0;
  private int shellCount = 0;
  private int gaussianCount = 0;
  private String endCheck;
  
  private BasisFunctionReader r;

  SpartanArchive(BasisFunctionReader r) {
    initialize(r, "");
  }

  SpartanArchive(BasisFunctionReader r, String bondData, String endCheck) {
    initialize(r, bondData);
    this.endCheck = endCheck;
  }

  private void initialize(BasisFunctionReader r, String bondData) {
    this.r = r;
    r.moData.put("isNormalized", Boolean.TRUE);
    r.moData.put("energyUnits","");
    this.bondData = bondData;
  }

  int readArchive(String infoLine, boolean haveGeometryLine, 
                  int atomCount0, boolean doAddAtoms) throws Exception {
    modelAtomCount = setInfo(infoLine);
    line = (haveGeometryLine ? "GEOMETRY" : "");
    boolean haveMOData = false;
    while (line != null) {
      if (line.equals("GEOMETRY")) {
        readAtoms(atomCount0, doAddAtoms);
        if (doAddAtoms && bondData.length() > 0)
          addBonds(bondData, atomCount0);
      } else if (line.indexOf("BASIS") == 0) {
        readBasis();
      } else if (line.indexOf("WAVEFUNC") == 0 || line.indexOf("BETA") == 0) {
        if (r.readMolecularOrbitals) {
          readMolecularOrbital();
          haveMOData = true;
        }
        
      } else if (line.indexOf("ENERGY") == 0) {
        readEnergy();
      } else if (line.equals("ENDARCHIVE")
          || endCheck != null && line.indexOf(endCheck) == 0) {
        break;
      }
      readLine();
    }
    if (haveMOData)
      r.setMOData(r.moData);
    return atomCount;
  }

  private void readEnergy() throws Exception {
    String[] tokens = getTokens(readLine());
    float value = parseFloat(tokens[0]);
    r.atomSetCollection.setAtomSetAuxiliaryInfo("energy", new Float(value));
    if (r instanceof SpartanSmolReader) {
      String prefix = ((SpartanSmolReader)r).constraints;
      r.atomSetCollection.setAtomSetName(prefix + (prefix.length() == 0 ? "" : " ") + "Energy=" + value + " KJ");
    }
    r.atomSetCollection.setAtomSetEnergy(tokens[0], value);
  }

  private int modelAtomCount;
  
  private int setInfo(String info) throws Exception {
    //    5  17  11  18   0   1  17   0 RHF      3-21G(d)           NOOPT FREQ
    //    0   1  2   3    4   5   6   7  8        9

    String[] tokens = getTokens(info);
    if (Logger.debugging) {
      Logger.debug("reading Spartan archive info :" + info);
    }
    modelAtomCount = parseInt(tokens[0]);
    coefCount = parseInt(tokens[1]);
    shellCount = parseInt(tokens[2]);
    gaussianCount = parseInt(tokens[3]);
    //overallCharge = parseInt(tokens[4]);
    moCount = parseInt(tokens[6]);
    r.calculationType = tokens[9];
    String s = (String) r.moData.get("calculationType");
    if (s == null)
      s = r.calculationType;
    else if (s.indexOf(r.calculationType) < 0)
      s = r.calculationType + s;
    r.moData.put("calculationType", r.calculationType = s);
    return modelAtomCount;
  }

  private void readAtoms(int atomCount0, boolean doAddAtoms) throws Exception {
    for (int i = 0; i < modelAtomCount; i++) {
      String tokens[] = getTokens(readLine());
      Atom atom = (doAddAtoms ? r.atomSetCollection.addNewAtom()
          : r.atomSetCollection.getAtom(atomCount0 - modelAtomCount + i));
      atom.elementSymbol = AtomSetCollectionReader
          .getElementSymbol(parseInt(tokens[0]));
      r.setAtomCoord(atom, parseFloat(tokens[1]) * AtomSetCollectionReader.ANGSTROMS_PER_BOHR, 
          parseFloat(tokens[2]) * AtomSetCollectionReader.ANGSTROMS_PER_BOHR, 
          parseFloat(tokens[3]) * AtomSetCollectionReader.ANGSTROMS_PER_BOHR);
    }
    if (doAddAtoms && Logger.debugging) {
      Logger.debug(atomCount + " atoms read");
    }
  }

  void addBonds(String data, int atomCount0) {
    /* from cached data:
     
     <one number per atom>
     1    2    1
     1    3    1
     1    4    1
     1    5    1
     1    6    1
     1    7    1

     */

    String tokens[] = getTokens(data);
    for (int i = modelAtomCount; i < tokens.length;) {
      int sourceIndex = parseInt(tokens[i++]) - 1 + atomCount0;
      int targetIndex = parseInt(tokens[i++]) - 1 + atomCount0;
      int bondOrder = parseInt(tokens[i++]);
      if (bondOrder > 0) {
        r.atomSetCollection.addBond(new Bond(sourceIndex, targetIndex,
            bondOrder < 4 ? bondOrder : bondOrder == 5 ? JmolAdapter.ORDER_AROMATIC : 1));
      }
    }
    int bondCount = r.atomSetCollection.getBondCount();
    if (Logger.debugging) {
      Logger.debug(bondCount + " bonds read");
    }
  }

  /* 
   * Jmol:   XX, YY, ZZ, XY, XZ, YZ 
   * qchem: dxx, dxy, dyy, dxz, dyz, dzz : VERIFIED
   * Jmol:   d0, d1+, d1-, d2+, d2-
   * qchem: d 1=d2-, d 2=d1-, d 3=d0, d 4=d1+, d 5=d2+
   * Jmol:   XXX, YYY, ZZZ, XYY, XXY, XXZ, XZZ, YZZ, YYZ, XYZ
   * qchem: fxxx, fxxy, fxyy, fyyy, fxxz, fxyz, fyyz, fxzz, fyzz, fzzz
   * Jmol:   f0, f1+, f1-, f2+, f2-, f3+, f3-
   * qchem: f 1=f3-, f 2=f2-, f 3=f1-, f 4=f0, f 5=f1+, f 6=f2+, f 7=f3+
   * 
   */

  //private static String DS_LIST = "d2-   d1-   d0    d1+   d2+";
  //private static String FS_LIST = "f3-   f2-   f1-   f0    f1+   f2+   f3+";
  //private static String DC_LIST = "DXX   DXY   DYY   DXZ   DYZ   DZZ";
  //private static String FC_LIST = "XXX   XXY   XYY   YYY   XXZ   XYZ   YYZ   XZZ   YZZ   ZZZ";

  void readBasis() throws Exception {
    /*
     * standard Gaussian format:
     
     BASIS
     0   2   1   1   0
     0   1   3   1   0
     0   3   4   2   0
     1   2   7   2   0
     1   1   9   2   0
     0   3  10   3   0
     ...
     5.4471780000D+00
     3.9715132057D-01   0.0000000000D+00   0.0000000000D+00   0.0000000000D+00
     8.2454700000D-01
     5.5791992333D-01   0.0000000000D+00   0.0000000000D+00   0.0000000000D+00

     */

    ArrayList<int[]> shells = new ArrayList<int[]>();
    float[][] gaussians = new float[gaussianCount][];
    int[] typeArray = new int[gaussianCount];
    //if (false) { // checking these still
    // r.getDFMap(DC_LIST, JmolAdapter.SHELL_D_CARTESIAN, BasisFunctionReader.CANONICAL_DC_LIST, 3);
    // r.getDFMap(FC_LIST, JmolAdapter.SHELL_F_CARTESIAN, BasisFunctionReader.CANONICAL_FC_LIST, 3);
    // r.getDFMap(DS_LIST, JmolAdapter.SHELL_D_SPHERICAL, BasisFunctionReader.CANONICAL_DS_LIST, 3);
    // r.getDFMap(FS_LIST, JmolAdapter.SHELL_F_SPHERICAL, BasisFunctionReader.CANONICAL_FS_LIST, 3);
    // }
    for (int i = 0; i < shellCount; i++) {
      String[] tokens = getTokens(readLine());
      boolean flag4 = (tokens[4].charAt(0) == '1');
      int[] slater = new int[4];
      slater[0] = parseInt(tokens[3]) - 1; //atom pointer; 1-based
      int iBasis = parseInt(tokens[0]); //0 = S, 1 = SP, 2 = D, 3 = F
      switch (iBasis) {
      case 0:
        iBasis = JmolAdapter.SHELL_S;
        break;
      case 1:
        iBasis = JmolAdapter.SHELL_SP;
        break;
      case 2:
        iBasis = (flag4 ? JmolAdapter.SHELL_D_SPHERICAL : JmolAdapter.SHELL_D_CARTESIAN);
        break;
      case 3:
        iBasis = (flag4 ? JmolAdapter.SHELL_F_SPHERICAL : JmolAdapter.SHELL_F_CARTESIAN);
        break;
      }
      slater[1] = iBasis;
      int gaussianPtr = slater[2] = parseInt(tokens[2]) - 1;
      int nGaussians = slater[3] = parseInt(tokens[1]);
      for (int j = 0; j < nGaussians; j++)
        typeArray[gaussianPtr + j] = iBasis;
      shells.add(slater);
    }
    for (int i = 0; i < gaussianCount; i++) {
      float alpha = parseFloat(readLine());
      String[] tokens = getTokens(readLine());
      int nData = tokens.length;
      float[] data = new float[nData + 1];
      data[0] = alpha;
      //we put D and F into coef 1. This may change if I find that Gaussian output
      //lists D and F in columns 3 and 4 as well.
      switch (typeArray[i]) {
      case JmolAdapter.SHELL_S:
        data[1] = parseFloat(tokens[0]);
        break;
      case JmolAdapter.SHELL_SP:
        data[1] = parseFloat(tokens[0]);
        data[2] = parseFloat(tokens[1]);
        if (data[1] == 0) {
          data[1] = data[2];
          typeArray[i] = JmolAdapter.SHELL_P;
        }
        break;
      case JmolAdapter.SHELL_D_CARTESIAN:
      case JmolAdapter.SHELL_D_SPHERICAL:
        data[1] = parseFloat(tokens[2]);
        break;
      case JmolAdapter.SHELL_F_CARTESIAN:
      case JmolAdapter.SHELL_F_SPHERICAL:
        data[1] = parseFloat(tokens[3]);
        break;
      }
      gaussians[i] = data;
    }
    int nCoeff = 0;
    for (int i = 0; i < shellCount; i++) {
      int[] slater = shells.get(i);
      switch(typeArray[slater[2]]) {
      case JmolAdapter.SHELL_S:
        nCoeff++;
        break;
      case JmolAdapter.SHELL_P:
        slater[1] = JmolAdapter.SHELL_P;
        nCoeff += 3;
        break;
      case JmolAdapter.SHELL_SP:
        nCoeff += 4;
        break;
      case JmolAdapter.SHELL_D_SPHERICAL:
        nCoeff += 5;
        break;
      case JmolAdapter.SHELL_D_CARTESIAN:
        nCoeff += 6;
        break;
      case JmolAdapter.SHELL_F_SPHERICAL:
        nCoeff += 7;
        break;
      case JmolAdapter.SHELL_F_CARTESIAN:
        nCoeff += 10;
        break;
      }
    }
    boolean isD5F7 = (nCoeff < coefCount);
    if (isD5F7)
    for (int i = 0; i < shellCount; i++) {
      int[] slater = shells.get(i);
      switch (typeArray[i]) {
      case JmolAdapter.SHELL_D_CARTESIAN:
        slater[1] = JmolAdapter.SHELL_D_SPHERICAL;
        break;
      case JmolAdapter.SHELL_F_CARTESIAN:
        slater[1] = JmolAdapter.SHELL_F_SPHERICAL;
        break;
      }
    }
    r.moData.put("shells", shells);
    r.moData.put("gaussians", gaussians);
    if (Logger.debugging) {
      Logger.debug(shells.size() + " slater shells read");
      Logger.debug(gaussians.length + " gaussian primitives read");
    }
  }

  void readMolecularOrbital() throws Exception {
    int tokenPt = 0;
    r.orbitals = new ArrayList<Map<String, Object>>();
    String[] tokens = getTokens("");
    float[] energies = new float[moCount];
    float[][] coefficients = new float[moCount][coefCount];
    for (int i = 0; i < moCount; i++) {
      if (tokenPt == tokens.length) {
        tokens = getTokens(readLine());
        tokenPt = 0;
      }
      energies[i] = parseFloat(tokens[tokenPt++]);
    }
    for (int i = 0; i < moCount; i++) {
      for (int j = 0; j < coefCount; j++) {
        if (tokenPt == tokens.length) {
          tokens = getTokens(readLine());
          tokenPt = 0;
        }
        coefficients[i][j] = parseFloat(tokens[tokenPt++]);
      }
    }
    for (int i = 0; i < moCount; i++) {
      Map<String, Object> mo = new Hashtable<String, Object>();
      mo.put("energy", Float.valueOf(energies[i]));
      //mo.put("occupancy", new Float(-1));
      mo.put("coefficients", coefficients[i]);
      r.setMO(mo);
    }
    if (Logger.debugging) {
      Logger.debug(r.orbitals.size() + " molecular orbitals read");
    }
    r.moData.put("mos", r.orbitals);
  }

  void readProperties() throws Exception {
    Logger.debug("Reading PROPARC properties records...");
    while (readLine() != null
        && !line.startsWith("ENDPROPARC") && !line.startsWith("END Directory Entry ")) {
      if (line.startsWith("PROP"))
        readProperty();
      else if (line.startsWith("DIPOLE"))
        readDipole();
      else if (line.startsWith("VIBFREQ"))
        readVibFreqs();
    }
    setVibrationsFromProperties();
  }

  void readDipole() throws Exception {
    //fall-back if no other dipole record
    setDipole(getTokens(readLine()));
  }

  private void setDipole(String[] tokens) {
    if (tokens.length != 3)
      return;
    Vector3f dipole = new Vector3f(parseFloat(tokens[0]),
        parseFloat(tokens[1]), parseFloat(tokens[2]));
    r.atomSetCollection.setAtomSetAuxiliaryInfo("dipole", dipole);
  }

  private void readProperty() throws Exception {
    String tokens[] = getTokens(line);
    if (tokens.length == 0)
      return;
    //System.out.println("reading property line:" + line);
    boolean isString = (tokens[1].startsWith("STRING"));
    String keyName = tokens[2];
    boolean isDipole = (keyName.equals("DIPOLE_VEC"));
    Object value = new Object();
    List<Object> vector = new ArrayList<Object>();
    if (tokens[3].equals("=")) {
      if (isString) {
        value = getQuotedString(tokens[4].substring(0, 1));
      } else {
        value = new Float(parseFloat(tokens[4]));
      }
    } else if (tokens[tokens.length - 1].equals("BEGIN")) {
      int nValues = parseInt(tokens[tokens.length - 2]);
      if (nValues == 0)
        nValues = 1;
      boolean isArray = (tokens.length == 6);
      List<Float> atomInfo = new ArrayList<Float>();
      int ipt = 0;
      while (readLine() != null
          && !line.substring(0, 3).equals("END")) {
        if (isString) {
          value = getQuotedString("\"");
          vector.add(value);
        } else {
          String tokens2[] = getTokens(line);
          if (isDipole)
            setDipole(tokens2);
          for (int i = 0; i < tokens2.length; i++, ipt++) {
            if (isArray) {
              atomInfo.add(Float.valueOf(parseFloat(tokens2[i])));
              if ((ipt + 1) % nValues == 0) {
                vector.add(atomInfo);
                atomInfo = new ArrayList<Float>();
              }
            } else {
              value = Float.valueOf(parseFloat(tokens2[i]));
              vector.add(value);
            }
          }
        }
      }
      value = null;
    } else {
      if (Logger.debugging) {
        Logger.debug(" Skipping property line " + line);
      }
    }
    //Logger.debug(keyName + " = " + value + " ; " + vector);
    if (value != null)
      r.atomSetCollection.setAtomSetCollectionAuxiliaryInfo(keyName, value);
    if (vector.size() != 0)
      r.atomSetCollection.setAtomSetCollectionAuxiliaryInfo(keyName, vector);
  }

  // Logger.debug("reading property line:" + line);

  void readVibFreqs() throws Exception {
    readLine();
    String label = "";
    int frequencyCount = parseInt(line);
    List<List<List<Float>>> vibrations = new ArrayList<List<List<Float>>>();
    List<Map<String, Object>> freqs = new ArrayList<Map<String,Object>>();
    if (Logger.debugging) {
      Logger.debug("reading VIBFREQ vibration records: frequencyCount = "
          + frequencyCount);
    }
    boolean[] ignore = new boolean[frequencyCount];
    for (int i = 0; i < frequencyCount; ++i) {
      int atomCount0 = r.atomSetCollection.getAtomCount();
      ignore[i] = !r.doGetVibration(i + 1);
      if (!ignore[i] && r.desiredVibrationNumber <= 0) {
        r.atomSetCollection.cloneLastAtomSet();
        addBonds(bondData, atomCount0);
      }
      readLine();
      Map<String, Object> info = new Hashtable<String, Object>();
      float freq = parseFloat(line);
      info.put("freq", new Float(freq));
      if (line.length() > 15
          && !(label = line.substring(15, line.length())).equals("???"))
        info.put("label", label);
      freqs.add(info);
      if (!ignore[i]) {
        r.atomSetCollection.setAtomSetFrequency(null, label, "" + freq, null);
      }
    }
    r.atomSetCollection.setAtomSetCollectionAuxiliaryInfo("VibFreqs", freqs);
    int atomCount = r.atomSetCollection.getFirstAtomSetAtomCount();
    List<List<Float>> vib = new ArrayList<List<Float>>();
    List<Float> vibatom = new ArrayList<Float>();
    int ifreq = 0;
    int iatom = atomCount;
    int nValues = 3;
    float[] atomInfo = new float[3];
    while (readLine() != null) {
      String tokens2[] = getTokens(line);
      for (int i = 0; i < tokens2.length; i++) {
        float f = parseFloat(tokens2[i]);
        atomInfo[i % nValues] = f;
        vibatom.add(Float.valueOf(f));
        if ((i + 1) % nValues == 0) {
          if (!ignore[ifreq]) {
            r.atomSetCollection.addVibrationVector(iatom, atomInfo[0], atomInfo[1], atomInfo[2]);
            vib.add(vibatom);
            vibatom = new ArrayList<Float>();
          }
          ++iatom;
        }
      }
      if (iatom % atomCount == 0) {
        if (!ignore[ifreq]) {
          vibrations.add(vib);
        }
        vib = new ArrayList<List<Float>>();
        if (++ifreq == frequencyCount) {
          break; // /loop exit
        }
      }
    }
    r.atomSetCollection.setAtomSetCollectionAuxiliaryInfo("vibration", vibrations);
  }

  @SuppressWarnings("unchecked")
  private void setVibrationsFromProperties() throws Exception {
    List<List<Float>> freq_modes = (List<List<Float>>) r.atomSetCollection.getAtomSetCollectionAuxiliaryInfo("FREQ_MODES");
    if (freq_modes == null) {
      return;
    }
    List<String> freq_lab = (List<String>) r.atomSetCollection.getAtomSetCollectionAuxiliaryInfo("FREQ_LAB");
    List<Float> freq_val = (List<Float>) r.atomSetCollection.getAtomSetCollectionAuxiliaryInfo("FREQ_VAL");
    int frequencyCount = freq_val.size();
    List<List<List<Float>>> vibrations = new ArrayList<List<List<Float>>>();
    List<Map<String, Object>> freqs = new ArrayList<Map<String,Object>>();
    if (Logger.debugging) {
      Logger.debug(
          "reading PROP VALUE:VIB FREQ_MODE vibration records: frequencyCount = " + frequencyCount);
    }
    Float v;
    for (int i = 0; i < frequencyCount; ++i) {
      int atomCount0 = r.atomSetCollection.getAtomCount();
      r.atomSetCollection.cloneLastAtomSet();
      addBonds(bondData, atomCount0);
      Map<String, Object> info = new Hashtable<String, Object>();
      info.put("freq", (v = freq_val.get(i)));
      float freq = v.floatValue();
      String label = freq_lab.get(i);
      if (!label.equals("???")) {
        info.put("label", label);
      }
      freqs.add(info);
      r.atomSetCollection.setAtomSetName(label + " " + freq + " cm^-1");
      r.atomSetCollection.setAtomSetProperty("Frequency", freq + " cm^-1");
      r.atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY, "Frequencies");
    }
    r.atomSetCollection.setAtomSetCollectionAuxiliaryInfo("VibFreqs", freqs);
    int atomCount = r.atomSetCollection.getFirstAtomSetAtomCount();
    int iatom = atomCount; // add vibrations starting at second atomset
    for (int i = 0; i < frequencyCount; i++) {
      if (!r.doGetVibration(i + 1))
        continue;
      int ipt = 0;
      List<List<Float>> vib = new ArrayList<List<Float>>();
      List<Float> mode = freq_modes.get(i);
      for (int ia = 0; ia < atomCount; ia++, iatom++) {
        List<Float> vibatom = new ArrayList<Float>();
        float vx = (v = mode.get(ipt++)).floatValue();
        vibatom.add(v);
        float vy = (v = mode.get(ipt++)).floatValue();
        vibatom.add(v);
        float vz = (v = mode.get(ipt++)).floatValue();
        vibatom.add(v);
        r.atomSetCollection.addVibrationVector(iatom, vx, vy, vz);
        vib.add(vibatom);
      }
      vibrations.add(vib);
    }
    r.atomSetCollection.setAtomSetCollectionAuxiliaryInfo("vibration", vibrations);
  }

  private String getQuotedString(String strQuote) {
    int i = line.indexOf(strQuote);
    int j = line.lastIndexOf(strQuote);
    return (j == i ? "" : line.substring(i + 1, j));
  }
  
  private int parseInt(String info) {
    return r.parseInt(info);
  }

  private float parseFloat(String info) {
    return r.parseFloat(info);
  }

  private String[] getTokens(String s) {
    return AtomSetCollectionReader.getTokens(s);
  }
  
  private String line;
 
  private String readLine() throws Exception {
    return (line = r.readLine());
  }
}
