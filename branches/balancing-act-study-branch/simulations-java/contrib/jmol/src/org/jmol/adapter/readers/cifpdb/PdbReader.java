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

package org.jmol.adapter.readers.cifpdb;

import org.jmol.adapter.smarter.*;



import org.jmol.api.JmolAdapter;
import org.jmol.util.Logger;
import org.jmol.util.TextFormat;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

/**
 * PDB file reader.
 *
 *<p>
 * <a href='http://www.rcsb.org'>
 * http://www.rcsb.org
 * </a>
 *
 * @author Miguel, Egon, and Bob (hansonr@stolaf.edu)
 * 
 * symmetry added by Bob Hanson:
 * 
 *  setFractionalCoordinates()
 *  setSpaceGroupName()
 *  setUnitCell()
 *  initializeCartesianToFractional();
 *  setUnitCellItem()
 *  setAtomCoord()
 *  applySymmetryAndSetTrajectory()
 *  
 */

public class PdbReader extends AtomSetCollectionReader {
  private int lineLength;
  // index into atoms array + 1
  // so that 0 can be used for the null value
  private final Map<String, Map<String, Boolean>> htFormul = new Hashtable<String, Map<String, Boolean>>();
  private Map<String, String> htHetero = null;
  private Map<String, Map<String, Object>> htSites = null;
  private String currentGroup3;
  private int currentResno = Integer.MIN_VALUE;
  private Map<String, Boolean> htElementsInCurrentGroup;
  private int maxSerial;
  private int[] chainAtomCounts;
  private int nUNK;
  private int nRes;
  private boolean isMultiModel;  // MODEL ...
  
 final private static String lineOptions = 
   "ATOM    " + //0
   "HETATM  " + //1
   "MODEL   " + //2
   "CONECT  " + //3
   "HELIX   " + //4,5,6
   "SHEET   " +
   "TURN    " +
   "HET     " + //7
   "HETNAM  " + //8
   "ANISOU  " + //9
   "SITE    " + //10
   "CRYST1  " + //11
   "SCALE1  " + //12,13,14
   "SCALE2  " +
   "SCALE3  " +
   "EXPDTA  " + //15
   "FORMUL  " + //16
   "REMARK  " + //17
   "HEADER  " + //18
   "COMPND  " + //19
   "SOURCE  ";  //20

 private int serial = 0;
 private StringBuffer pdbHeader;
 private int configurationPtr = Integer.MIN_VALUE;
 private boolean applySymmetry;

 @Override
 protected void initializeReader() throws Exception {
   atomSetCollection.setIsPDB();
   pdbHeader = (getHeader ? new StringBuffer() : null);
   applySymmetry = !checkFilter("NOSYMMETRY");
   
   if (checkFilter("CONF ")) {
     configurationPtr = parseInt(filter, filter.indexOf("CONF ") + 5);
     sbIgnored = new StringBuffer();
     sbSelected = new StringBuffer();
   }
 }

  @Override
  protected boolean checkLine() throws Exception {
    int ptOption = ((lineLength = line.length()) < 6 ? -1 : lineOptions
        .indexOf(line.substring(0, 6))) >> 3;
    boolean isAtom = (ptOption == 0 || ptOption == 1);
    boolean isModel = (ptOption == 2);
    if (isAtom)
      serial = parseInt(line, 6, 11);
    boolean isNewModel = ((isTrajectory || isSequential) && !isMultiModel
        && isAtom && serial == 1);
    if (getHeader) {
      if (isAtom || isModel)
        getHeader = false;
      else
        pdbHeader.append(line).append('\n');
    }
    if (isModel || isNewModel) {
      isMultiModel = isModel;
      getHeader = false;
      // PDB is different -- targets actual model number
      int modelNo = (isNewModel ? modelNumber + 1 : getModelNumber());
      modelNumber = (bsModels == null ? modelNo : modelNumber + 1);
      if (!doGetModel(modelNumber))
        return checkLastModel();
      atomSetCollection.connectAll(maxSerial);
      if (atomCount > 0)
        applySymmetryAndSetTrajectory();
      // supposedly MODEL is only for NMR
      model(modelNo);
      if (!isAtom)
        return true;
    }
    /*
     * OK, the PDB file format is messed up here, because the above commands are
     * all OUTSIDE of the Model framework. Of course, different models might
     * have different secondary structures, but it is not clear that PDB
     * actually supports this. So you can't concatinate PDB files the way you
     * can CIF files. --Bob Hanson 8/30/06
     */
    if (isMultiModel && !doProcessLines)
      return true;
    if (isAtom) {
      getHeader = false;
      atom(serial);
      return true;
    }
    switch (ptOption) {
    case 3:
      conect();
      return true;
    case 4:
    case 5:
    case 6:
      // if (line.startsWith("HELIX ") || line.startsWith("SHEET ")
      // || line.startsWith("TURN  ")) {
      structure();
      return true;
    case 7:
      het();
      return true;
    case 8:
      hetnam();
      return true;
    case 9:
      anisou();
      return true;
    case 10:
      site();
      return true;
    case 11:
      cryst1();
      return true;
    case 12:
    case 13:
    case 14:
      // if (line.startsWith("SCALE1")) {
      // if (line.startsWith("SCALE2")) {
      // if (line.startsWith("SCALE3")) {
      scale(ptOption - 11);
      return true;
    case 15:
      expdta();
      return true;
    case 16:
      formul();
      return true;
    case 17:
      if (line.startsWith("REMARK 350")) {
        remark350();
        return false;
      }
      if (line.startsWith("REMARK 290")) {
        remark290();
        return false;
      }
      checkLineForScript();
      return true;
    case 18:
      header();
      return true;
    case 19:
      compndOld();
      compndSource(false);
      return true;
    case 20:
      compndSource(true);
      return true;
    }
    return true;
  }

  @Override
  protected void finalizeReader() throws Exception {
    checkNotPDB();
    atomSetCollection.connectAll(maxSerial);
    if (biomolecules != null && biomolecules.size() > 0
        && atomSetCollection.getAtomCount() > 0) {
      atomSetCollection.setAtomSetAuxiliaryInfo("biomolecules", biomolecules);
      setBiomoleculeAtomCounts();
      if (biomts != null && applySymmetry) {
        atomSetCollection.applySymmetry(biomts, notionalUnitCell, applySymmetryToBonds, filter);
      }
    }
    super.finalizeReader();
    if (vCompnds != null)
      atomSetCollection.setAtomSetCollectionAuxiliaryInfo("compoundSource", vCompnds);
    if (htSites != null) { // && atomSetCollection.getAtomSetCount() == 1)
      addSites(htSites);
    }
    if (pdbHeader != null)
      atomSetCollection.setAtomSetCollectionAuxiliaryInfo("fileHeader",
          pdbHeader.toString());
    if (configurationPtr > 0) {
      Logger.info(sbSelected.toString());
      Logger.info(sbIgnored.toString());
    }
  }
  
  @Override
  public void applySymmetryAndSetTrajectory() throws Exception {
    // This speeds up calculation, because no crosschecking
    // No special-position atoms in mmCIF files, because there will
    // be no center of symmetry, no rotation-inversions, 
    // no atom-centered rotation axes, and no mirror or glide planes. 
    atomSetCollection.setCheckSpecial(false);
    super.applySymmetryAndSetTrajectory();
  }

  private void header() {
    if (lineLength < 8)
      return;
    if (lineLength >= 66)
      atomSetCollection.setCollectionName(line.substring(62, 66));
    if (lineLength > 50)
      line = line.substring(0, 50);
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("CLASSIFICATION", line.substring(7).trim());
  }

  private List<Map<String, String>> vCompnds;
  private Map<String, String> currentCompnd;
  private String currentKey;
  private Map<String, Map<String, String>> htMolIds;
  private boolean resetKey = true;
  
  private void compndSource(boolean isSource) {
    if (vCompnds == null) {
      if (isSource)
        return;
      vCompnds = new ArrayList<Map<String,String>>();
      htMolIds = new Hashtable<String, Map<String,String>>();
      currentCompnd = new Hashtable<String, String>();
      currentCompnd.put("select", "(*)");
      currentKey = "MOLECULE";
      htMolIds.put("", currentCompnd);
    }
    if (isSource && resetKey) {
      resetKey = false;
      currentKey = "SOURCE";
      currentCompnd = htMolIds.get("");
    }
    line = line.substring(10, Math.min(lineLength, 72)).trim();
    int pt = line.indexOf(":");
    if (pt < 0 || pt > 0 && line.charAt(pt - 1) == '\\')
      pt = line.length();
    String key = line.substring(0, pt).trim();
    String value = (pt < line.length() ? line.substring(pt + 1) : null);
    if (key.equals("MOL_ID")) {
      if (value == null)
        return;
      if (isSource) {
        currentCompnd = htMolIds.remove(value);
        return;
      }
      currentCompnd = new Hashtable<String, String>();
      vCompnds.add(currentCompnd);
      htMolIds.put(value, currentCompnd);
    }
    if (currentCompnd == null)
      return;
    if (value == null) {
      value = currentCompnd.get(currentKey);
      if (value == null)
        value = "";
      value += key;
      if (vCompnds.size() == 0)
        vCompnds.add(currentCompnd);
    } else {
      currentKey = key;
    }
    if (value.endsWith(";"))
      value = value.substring(0, value.length() - 1);
    currentCompnd.put(currentKey, value);
    if (currentKey.equals("CHAIN"))
      currentCompnd.put("select", "(:"
          + TextFormat.simpleReplace(TextFormat
              .simpleReplace(value, ", ", ",:"), " ", "") + ")");
  }

  private String compnd = null;
  private void compndOld() {
    if (compnd == null)
      compnd = "";
    else
      compnd += " ";
    String s = line;
    if (lineLength > 62)
      s = s.substring(0, 62);
    compnd += s.substring(10).trim();
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("COMPND", compnd);
  }


  @SuppressWarnings("unchecked")
  private void setBiomoleculeAtomCounts() {
    for (int i = biomolecules.size(); --i >= 0;) {
      Map<String, Object> biomolecule = biomolecules.get(i);
      String chain = (String) biomolecule.get("chains");
      int nTransforms = ((List<Matrix4f>) biomolecule.get("biomts")).size();
      int nAtoms = 0;
      for (int j = chain.length() - 1; --j >= 0;)
        if (chain.charAt(j) == ':')
          nAtoms += chainAtomCounts[chain.charAt(j + 1)];
      biomolecule.put("atomCount", Integer.valueOf(nAtoms * nTransforms));
    }
  }

/* 
 REMARK 350 BIOMOLECULE: 1                                                       
 REMARK 350 APPLY THE FOLLOWING TO CHAINS: 1, 2, 3, 4, 5, 6,  
 REMARK 350 A, B, C
 REMARK 350   BIOMT1   1  1.000000  0.000000  0.000000        0.00000            
 REMARK 350   BIOMT2   1  0.000000  1.000000  0.000000        0.00000            
 REMARK 350   BIOMT3   1  0.000000  0.000000  1.000000        0.00000            
 REMARK 350   BIOMT1   2  0.309017 -0.809017  0.500000        0.00000            
 REMARK 350   BIOMT2   2  0.809017  0.500000  0.309017        0.00000            
 REMARK 350   BIOMT3   2 -0.500000  0.309017  0.809017        0.00000
 
             
             or, as fount in http://www.ebi.ac.uk/msd-srv/pqs/pqs-doc/macmol/1k28.mmol
             
REMARK 350 AN OLIGOMER OF TYPE :HEXAMERIC : CAN BE ASSEMBLED BY
REMARK 350 APPLYING THE FOLLOWING TO CHAINS:
REMARK 350 A, D
REMARK 350   BIOMT1   1  1.000000  0.000000  0.000000        0.00000
REMARK 350   BIOMT2   1  0.000000  1.000000  0.000000        0.00000
REMARK 350   BIOMT3   1  0.000000  0.000000  1.000000        0.00000
REMARK 350 IN ADDITION APPLY THE FOLLOWING TO CHAINS:
REMARK 350 A, D
REMARK 350   BIOMT1   2  0.000000 -1.000000  0.000000        0.00000
REMARK 350   BIOMT2   2  1.000000 -1.000000  0.000000        0.00000
REMARK 350   BIOMT3   2  0.000000  0.000000  1.000000        0.00000
REMARK 350 IN ADDITION APPLY THE FOLLOWING TO CHAINS:
REMARK 350 A, D
REMARK 350   BIOMT1   3 -1.000000  1.000000  0.000000        0.00000
REMARK 350   BIOMT2   3 -1.000000  0.000000  0.000000        0.00000
REMARK 350   BIOMT3   3  0.000000  0.000000  1.000000        0.00000

*/
 
  private List<Map<String, Object>> biomolecules;
  private List<Matrix4f> biomts;
  
  private void remark350() throws Exception {
    List<Matrix4f> biomts = null;
    biomolecules = new ArrayList<Map<String,Object>>();
    chainAtomCounts = new int[255];
    String title = "";
    String chainlist = "";
    int iMolecule = 0;
    boolean needLine = true;
    Map<String, Object> info = null;
    int nBiomt = 0;
    Matrix4f mIdent = new Matrix4f();
    mIdent.setIdentity();
    while (true) {
      if (needLine)
        readLine();
      else
        needLine = true;
      if (line == null || !line.startsWith("REMARK 350"))
        break;
      try {
        if (line.startsWith("REMARK 350 BIOMOLECULE:")) {
          if (nBiomt > 0)
            Logger.info("biomolecule " + iMolecule + ": number of transforms: "
                + nBiomt);
          info = new Hashtable<String, Object>();
          biomts = new ArrayList<Matrix4f>();
          iMolecule = parseInt(line.substring(line.indexOf(":") + 1));
          title = line.trim();
          info.put("molecule", Integer.valueOf(iMolecule));
          info.put("title", title);
          info.put("chains", "");
          info.put("biomts", biomts);
          biomolecules.add(info);
          nBiomt = 0;
          //continue; need to allow for next IF, in case this is a reconstruction
        }
        if (line.indexOf("APPLY THE FOLLOWING TO CHAINS:") >= 0) {
          if (info == null) {
            // need to initialize biomolecule business first and still flag this section
            // see http://www.ebi.ac.uk/msd-srv/pqs/pqs-doc/macmol/1k28.mmol
            needLine = false;
            line = "REMARK 350 BIOMOLECULE: 1  APPLY THE FOLLOWING TO CHAINS:";
            continue;
          }
          chainlist = ":" + line.substring(41).trim().replace(' ', ':');
          needLine = false;
          while (readLine() != null && line.indexOf("BIOMT") < 0)
            chainlist += ":" + line.substring(11).trim().replace(' ', ':');
          if (checkFilter("BIOMOLECULE " + iMolecule + ";")) {
            setFilter(filter.replace(':', '_') + chainlist);
            Logger.info("filter set to \"" + filter + "\"");
            this.biomts = biomts;
          }
          info.put("chains", chainlist);
          continue;
        }
        /*
         0         1         2         3         4         5         6         7
         0123456789012345678901234567890123456789012345678901234567890123456789
         REMARK 350   BIOMT2   1  0.000000  1.000000  0.000000        0.00000
         */
        if (line.startsWith("REMARK 350   BIOMT1 ")) {
          nBiomt++;
          float[] mat = new float[16];
          for (int i = 0; i < 12;) {
            String[] tokens = getTokens();
            mat[i++] = parseFloat(tokens[4]);
            mat[i++] = parseFloat(tokens[5]);
            mat[i++] = parseFloat(tokens[6]);
            mat[i++] = parseFloat(tokens[7]);
            if (i == 4 || i == 8)
              readLine();
          }
          mat[15] = 1;
          Matrix4f m4 = new Matrix4f();
          m4.set(mat);
          if (m4.equals(mIdent))
            biomts.add(0, m4);
          else
            biomts.add(m4);
          continue;
        }
      } catch (Exception e) {
        // probably just 
        this.biomts = null;
        this.biomolecules = null;
        return;
      }
    }
    if (nBiomt > 0)
      Logger.info("biomolecule " + iMolecule + ": number of transforms: "
          + nBiomt);
  }

  /*
REMARK 290                                                                      
REMARK 290 CRYSTALLOGRAPHIC SYMMETRY                                            
REMARK 290 SYMMETRY OPERATORS FOR SPACE GROUP: P 1 21 1                         
REMARK 290                                                                      
REMARK 290      SYMOP   SYMMETRY                                                
REMARK 290     NNNMMM   OPERATOR                                                
REMARK 290       1555   X,Y,Z                                                   
REMARK 290       2555   -X,Y+1/2,-Z                                             
REMARK 290                                                                      
REMARK 290     WHERE NNN -> OPERATOR NUMBER                                     
REMARK 290           MMM -> TRANSLATION VECTOR                                  
REMARK 290                                                                      
REMARK 290 CRYSTALLOGRAPHIC SYMMETRY TRANSFORMATIONS                            
REMARK 290 THE FOLLOWING TRANSFORMATIONS OPERATE ON THE ATOM/HETATM             
REMARK 290 RECORDS IN THIS ENTRY TO PRODUCE CRYSTALLOGRAPHICALLY                
REMARK 290 RELATED MOLECULES.                                                   
REMARK 290   SMTRY1   1  1.000000  0.000000  0.000000        0.00000            
REMARK 290   SMTRY2   1  0.000000  1.000000  0.000000        0.00000            
REMARK 290   SMTRY3   1  0.000000  0.000000  1.000000        0.00000            
REMARK 290   SMTRY1   2 -1.000000  0.000000  0.000000        0.00000            
REMARK 290   SMTRY2   2  0.000000  1.000000  0.000000        9.32505            
REMARK 290   SMTRY3   2  0.000000  0.000000 -1.000000        0.00000            
REMARK 290                                                                      
REMARK 290 REMARK: NULL                                                         

   */
  private void remark290() throws Exception {
    while (readLine() != null && line.startsWith("REMARK 290")) {
      if (line.indexOf("NNNMMM   OPERATOR") >= 0) {
        while (readLine() != null) {
          String[] tokens = getTokens();
          if (tokens.length < 4)
            break;
          setSymmetryOperator(tokens[3]);
        }
      }
    }
  }

  private int atomCount;
  private String lastAtomData;
  private int lastAtomIndex;
  private int iAtom;
  
  private void atom(int serial) {
    Atom atom = new Atom();
    atom.atomName = line.substring(12, 16).trim();
    char ch = line.charAt(16);
    if (ch != ' ')
      atom.alternateLocationID = ch;
    atom.group3 = parseToken(line, 17, 20);
    ch = line.charAt(21);
    if (chainAtomCounts != null)
      chainAtomCounts[ch]++;
    atom.chainID = ch;
    atom.sequenceNumber = parseInt(line, 22, 26);
    atom.insertionCode = JmolAdapter.canonizeInsertionCode(line.charAt(26));
    atom.isHetero = line.startsWith("HETATM");
    if (!filterAtom(atom, iAtom++))
      return;
    
    atom.atomSerial = serial;
    if (serial > maxSerial)
      maxSerial = serial;
    if (atom.group3 == null) {
      if (currentGroup3 != null) {
        currentGroup3 = null;
        currentResno = Integer.MIN_VALUE;
        htElementsInCurrentGroup = null;
      }
    } else if (!atom.group3.equals(currentGroup3) || atom.sequenceNumber != currentResno) {
      currentGroup3 = atom.group3;
      currentResno = atom.sequenceNumber;
      htElementsInCurrentGroup = htFormul.get(atom.group3);
      nRes++;
      if (atom.group3.equals("UNK"))
        nUNK++;
    }
    atom.elementSymbol = deduceElementSymbol(atom.isHetero);
    //calculate the charge from cols 79 & 80 (1-based): 2+, 3-, etc
    int charge = 0;
    if (lineLength >= 80) {
      char chMagnitude = line.charAt(78);
      char chSign = line.charAt(79);
      if (chSign >= '0' && chSign <= '7') {
        char chT = chSign;
        chSign = chMagnitude;
        chMagnitude = chT;
      }
      if ((chSign == '+' || chSign == '-' || chSign == ' ')
          && chMagnitude >= '0' && chMagnitude <= '7') {
        charge = chMagnitude - '0';
        if (chSign == '-')
          charge = -charge;
      }
    }
    atom.formalCharge = charge;

    setAtomCoord(atom, parseFloat(line, 30, 38), parseFloat(line, 38, 46),
        parseFloat(line, 46, 54));

    setAdditionalAtomParameters(atom);

    lastAtomData = line.substring(6, 26);
    lastAtomIndex = atomSetCollection.getAtomCount();
    if (haveMappedSerials)
      atomSetCollection.addAtomWithMappedSerialNumber(atom);
    else
      atomSetCollection.addAtom(atom);
    if (atomCount++ == 0)
      atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.TRUE);
    // note that values are +1 in this serial map
    if (atom.isHetero) {
      if (htHetero != null) {
        atomSetCollection.setAtomSetAuxiliaryInfo("hetNames", htHetero);
        htHetero = null;
      }
    }
  }

  private int lastGroup = Integer.MIN_VALUE;
  private char lastInsertion;
  private char lastAltLoc;
  private int conformationIndex;
  StringBuffer sbIgnored, sbSelected;

  @Override
  protected boolean filterAtom(Atom atom, int iAtom) {
    if (!super.filterAtom(atom, iAtom))
      return false;
    if (configurationPtr > 0) {
      if (atom.sequenceNumber != lastGroup || atom.insertionCode != lastInsertion) {
        conformationIndex = configurationPtr - 1;
        lastGroup = atom.sequenceNumber;
        lastInsertion = atom.insertionCode;
        lastAltLoc = '\0';
      }
      // ignore atoms that have no designation
      if (atom.alternateLocationID != '\0') {
        // count down until we get the desired index into the list
        String msg = " atom [" + atom.group3 + "]"
                           + atom.sequenceNumber 
                           + (atom.insertionCode == '\0' ? "" : "^" + atom.insertionCode)
                           + (atom.chainID == '\0' ? "" : ":" + atom.chainID)
                           + "." + atom.atomName
                           + "%" + atom.alternateLocationID + "\n";
        if (conformationIndex >= 0 && atom.alternateLocationID != lastAltLoc) {
          lastAltLoc = atom.alternateLocationID;
          conformationIndex--;
        }
        if (conformationIndex < 0 && atom.alternateLocationID != lastAltLoc) {
          sbIgnored.append("ignoring").append(msg);
          return false;
        }
        sbSelected.append("loading").append(msg);
      }
    }
    return true;
  }

  /**
   * adaptable via subclassing
   * 
   * @param atom
   */
  protected void setAdditionalAtomParameters(Atom atom) {

    /****************************************************************
     * read the occupancy from cols 55-60 (1-based)
     * should be in the range 0.00 - 1.00
     ****************************************************************/
    float floatOccupancy = parseFloat(line, 54, 60);
    atom.occupancy = (Float.isNaN(floatOccupancy) ? 100 : (int) (floatOccupancy * 100));

    /****************************************************************
     * read the bfactor from cols 61-66 (1-based)
     ****************************************************************/
    atom.bfactor = parseFloat(line, 60, 66);

  }
  
  private String deduceElementSymbol(boolean isHetero) {
    if (lineLength >= 78) {
      char ch76 = line.charAt(76);
      char ch77 = line.charAt(77);
      if (ch76 == ' ' && Atom.isValidElementSymbol(ch77))
        return "" + ch77;
      if (Atom.isValidElementSymbolNoCaseSecondChar(ch76, ch77))
        return "" + ch76 + ch77;
    }
    char ch12 = line.charAt(12);
    char ch13 = line.charAt(13);
    if ((htElementsInCurrentGroup == null ||
         htElementsInCurrentGroup.get(line.substring(12, 14)) != null) &&
        Atom.isValidElementSymbolNoCaseSecondChar(ch12, ch13))
      return (isHetero || ch12 != 'H' ? "" + ch12 + ch13 : "H");
    if ((htElementsInCurrentGroup == null ||
         htElementsInCurrentGroup.get("" + ch13) != null) &&
        Atom.isValidElementSymbol(ch13))
      return "" + ch13;
    if ((htElementsInCurrentGroup == null ||
         htElementsInCurrentGroup.get("" + ch12) != null) &&
        Atom.isValidElementSymbol(ch12))
      return "" + ch12;
    return "Xx";
  }

  private StringBuffer sbConect;
  private void conect() {
    // adapted for improper non-crossreferenced files such as 1W7R
    if (sbConect == null)
      sbConect = new StringBuffer();
    int sourceSerial = -1;
    sourceSerial = parseInt(line, 6, 11);
    if (sourceSerial < 0)
      return;
    for (int i = 0; i < 9; i += (i == 5 ? 2 : 1)) {
      int offset = i * 5 + 11;
      int offsetEnd = offset + 5;
      int targetSerial = (offsetEnd <= lineLength ? parseInt(line, offset,
          offsetEnd) : -1);
      if (targetSerial < 0)
        continue;
      int i1;
      boolean isSwapped = (targetSerial < sourceSerial);
      if (isSwapped) {
        i1 = targetSerial;
        targetSerial = sourceSerial;
      } else {
        i1 = sourceSerial;
      }
      String st = ";" + i1 + " " + targetSerial + ";";
      if (sbConect.indexOf(st) >= 0)
        continue;
      sbConect.append(st);
      atomSetCollection.addConnection(new int[] { i1, targetSerial,
          i < 4 ? 1 : JmolAdapter.ORDER_HBOND });
    }
  }

  /*
          1         2         3
  0123456789012345678901234567890123456
  HELIX    1  H1 ILE      7  LEU     18
  HELIX    2  H2 PRO     19  PRO     19
  HELIX    3  H3 GLU     23  TYR     29
  HELIX    4  H4 THR     30  THR     30
  SHEET    1  S1 2 THR     2  CYS     4
  SHEET    2  S2 2 CYS    32  ILE    35
  SHEET    3  S3 2 THR    39  PRO    41
  TURN     1  T1 GLY    42  TYR    44

  HELIX     1 H1 ILE A    7  PRO A   19
  HELIX     2 H2 GLU A   23  THR A   30
  SHEET     1 S1 0 CYS A   3  CYS A   4
  SHEET     2 S2 0 CYS A  32  ILE A  35

  HELIX  113 113 ASN H  307  ARG H  327  1                                  21    
  SHEET    1   A 6 ASP A  77  HIS A  80  0                                        
  SHEET    2   A 6 GLU A  47  ILE A  51  1  N  ILE A  48   O  ASP A  77           
  SHEET    3   A 6 ARG A  22  ILE A  26  1  N  VAL A  23   O  GLU A  47           


TYPE OF HELIX CLASS NUMBER (COLUMNS 39 - 40)
--------------------------------------------------------------
Right-handed alpha (default) 1
Right-handed omega 2
Right-handed pi 3
Right-handed gamma 4
Right-handed 310 5
Left-handed alpha 6
Left-handed omega 7
Left-handed gamma 8
27 ribbon/helix 9
Polyproline 10

   */
  
  private void structure() {
    int structureType = 0;
    int substructureType = 0;
    int startChainIDIndex;
    int startIndex;
    int endChainIDIndex;
    int endIndex;
    int strandCount = 0;
    if (line.startsWith("HELIX ")) {
      structureType = Structure.PROTEIN_STRUCTURE_HELIX;
      startChainIDIndex = 19;
      startIndex = 21;
      endChainIDIndex = 31;
      endIndex = 33;
      if (line.length() >= 40)
      substructureType = Structure.getHelixType(parseInt(line.substring(38, 40)));
    } else if (line.startsWith("SHEET ")) {
      structureType = Structure.PROTEIN_STRUCTURE_SHEET;
      startChainIDIndex = 21;
      startIndex = 22;
      endChainIDIndex = 32;
      endIndex = 33;
      strandCount = parseInt(line.substring(14, 16));
    } else if (line.startsWith("TURN  ")) {
      structureType = Structure.PROTEIN_STRUCTURE_TURN;
      startChainIDIndex = 19;
      startIndex = 20;
      endChainIDIndex = 30;
      endIndex = 31;
    } else
      return;

    if (lineLength < endIndex + 4)
      return;

    String structureID = line.substring(11, 15).trim();
    int serialID = parseInt(line.substring(7, 10));
    char startChainID = line.charAt(startChainIDIndex);
    int startSequenceNumber = parseInt(line, startIndex, startIndex + 4);
    char startInsertionCode = line.charAt(startIndex + 4);
    char endChainID = line.charAt(endChainIDIndex);
    int endSequenceNumber = parseInt(line, endIndex, endIndex + 4);
    // some files are chopped to remove trailing whitespace
    char endInsertionCode = ' ';
    if (lineLength > endIndex + 4)
      endInsertionCode = line.charAt(endIndex + 4);

    // this should probably call Structure.validateAndAllocate
    // in order to check validity of parameters
    // model number set to -1 here to indicate ALL MODELS
    if (substructureType == 0)
      substructureType = structureType;
    Structure structure = new Structure(-1, structureType, substructureType,
        structureID, serialID, strandCount, startChainID, startSequenceNumber,
        startInsertionCode, endChainID, endSequenceNumber, endInsertionCode);
    atomSetCollection.addStructure(structure);
  }

  private int getModelNumber() {
    try {
      int startModelColumn = 6; // should be 10 0-based
      int endModelColumn = 14;
      if (endModelColumn > lineLength)
        endModelColumn = lineLength;
      return parseInt(line, startModelColumn, endModelColumn);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
  
  private void model(int modelNumber) {
    /****************************************************************
     * mth 2004 02 28
     * note that the pdb spec says:
     * COLUMNS       DATA TYPE      FIELD         DEFINITION
     * ----------------------------------------------------------------------
     *  1 -  6       Record name    "MODEL "
     * 11 - 14       Integer        serial        Model serial number.
     *
     * but I received a file with the serial
     * number right after the word MODEL :-(
     ****************************************************************/
    checkNotPDB();
    haveMappedSerials = false;
    sbConect = null;
    atomSetCollection.newAtomSet();
    atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.TRUE);
    atomSetCollection.setAtomSetNumber(modelNumber);
  }

  private void checkNotPDB() {
    if (nRes > 0 && nUNK == nRes)
      atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.FALSE);
    nUNK = nRes = 0;
    currentGroup3 = null;
  }

  private void cryst1() throws Exception {
    float a = getFloat(6, 9);
    if (a == 1)
      a = Float.NaN; // 1 for a means no unit cell
    setUnitCell(a, getFloat(15, 9), getFloat(24, 9), getFloat(33,
        7), getFloat(40, 7), getFloat(47, 7));
    setSpaceGroupName(parseTrimmed(line, 55, 66));
  }

  private float getFloat(int ich, int cch) throws Exception {
    return parseFloat(line, ich, ich+cch);
  }

  private void scale(int n) throws Exception {
    int pt = n * 4 + 2;
    setUnitCellItem(pt++,getFloat(10, 10));
    setUnitCellItem(pt++,getFloat(20, 10));
    setUnitCellItem(pt++,getFloat(30, 10));
    setUnitCellItem(pt++,getFloat(45, 10));
  }

  private void expdta() {
    if (line.toUpperCase().indexOf("NMR") >= 0)
      atomSetCollection.setAtomSetCollectionAuxiliaryInfo("isNMRdata", "true");
  }

  private void formul() {
    String groupName = parseToken(line, 12, 15);
    String formula = parseTrimmed(line, 19, 70);
    int ichLeftParen = formula.indexOf('(');
    if (ichLeftParen >= 0) {
      int ichRightParen = formula.indexOf(')');
      if (ichRightParen < 0 || ichLeftParen >= ichRightParen ||
          ichLeftParen + 1 == ichRightParen ) // pick up () case in 1SOM.pdb
        return; // invalid formula;
      formula = parseTrimmed(formula, ichLeftParen + 1, ichRightParen);
    }
    Map<String, Boolean> htElementsInGroup = htFormul.get(groupName);
    if (htElementsInGroup == null)
      htFormul.put(groupName, htElementsInGroup = new Hashtable<String, Boolean>());
    // now, look for atom names in the formula
    next[0] = 0;
    String elementWithCount;
    while ((elementWithCount = parseTokenNext(formula)) != null) {
      if (elementWithCount.length() < 2)
        continue;
      char chFirst = elementWithCount.charAt(0);
      char chSecond = elementWithCount.charAt(1);
      if (Atom.isValidElementSymbolNoCaseSecondChar(chFirst, chSecond))
        htElementsInGroup.put("" + chFirst + chSecond, Boolean.TRUE);
      else if (Atom.isValidElementSymbol(chFirst))
        htElementsInGroup.put("" + chFirst, Boolean.TRUE);
    }
  }
  
  private void het() {
    if (line.length() < 30) {
      return;
    }
    if (htHetero == null) {
      htHetero = new Hashtable<String, String>();
    }
    String groupName = parseToken(line, 7, 10);
    if (htHetero.containsKey(groupName)) {
      return;
    }
    String hetName = parseTrimmed(line, 30, 70);
    htHetero.put(groupName, hetName);
  }
  
  private void hetnam() {
    if (htHetero == null) {
      htHetero = new Hashtable<String, String>();
    }
    String groupName = parseToken(line, 11, 14);
    String hetName = parseTrimmed(line, 15, 70);
    if (groupName == null) {
      Logger.error("ERROR: HETNAM record does not contain a group name: " + line);
      return;
    }
    String htName = htHetero.get(groupName);
    if (htName != null) {
      hetName = htName + hetName;
    }
    htHetero.put(groupName, hetName);
    //Logger.debug("hetero: "+groupName+" "+hetName);
  }
  
  /*
 The ANISOU records present the anisotropic temperature factors.

Record Format

COLUMNS        DATA TYPE       FIELD         DEFINITION                  
----------------------------------------------------------------------
 1 -  6        Record name     "ANISOU"                                  

 7 - 11        Integer         serial        Atom serial number.         

13 - 16        Atom            name          Atom name.                  

17             Character       altLoc        Alternate location indicator.                  

18 - 20        Residue name    resName       Residue name.               

22             Character       chainID       Chain identifier.           

23 - 26        Integer         resSeq        Residue sequence number.    

27             AChar           iCode         Insertion code.             

29 - 35        Integer         u[0][0]       U(1,1)                

36 - 42        Integer         u[1][1]       U(2,2)                

43 - 49        Integer         u[2][2]       U(3,3)                

50 - 56        Integer         u[0][1]       U(1,2)                

57 - 63        Integer         u[0][2]       U(1,3)                

64 - 70        Integer         u[1][2]       U(2,3)                

73 - 76        LString(4)      segID         Segment identifier, left-justified.

77 - 78        LString(2)      element       Element symbol, right-justified.

79 - 80        LString(2)      charge        Charge on the atom.       

Details

* Columns 7 - 27 and 73 - 80 are identical to the corresponding ATOM/HETATM record.

* The anisotropic temperature factors (columns 29 - 70) are scaled by a factor of 10**4 (Angstroms**2) and are presented as integers.

* The anisotropic temperature factors are stored in the same coordinate frame as the atomic coordinate records. 
   */
  private boolean  haveMappedSerials;
  
  private void anisou() {
    float[] data = new float[8];
    data[6] = 1; //U not B
    int serial = parseInt(line, 6, 11);
    int index;
    if (line.substring(6, 26).equals(lastAtomData)) {
      index = lastAtomIndex;
    } else {
      if (!haveMappedSerials)
        atomSetCollection.createAtomSerialMap();
      index = atomSetCollection.getAtomSerialNumberIndex(serial);
      haveMappedSerials = true;
    }
    if (index < 0) {
      //normal when filtering
      //System.out.println("ERROR: ANISOU record does not correspond to known atom");
      return;
    }
    Atom atom = atomSetCollection.getAtom(index);
    for (int i = 28, pt = 0; i < 70; i += 7, pt++)
      data[pt] = parseFloat(line, i, i + 7);
    for (int i = 0; i < 6; i++) {
      if (Float.isNaN(data[i])) {
          Logger.error("Bad ANISOU record: " + line);
          return;
      }
      data[i] /= 10000f;
    }
    atomSetCollection.setAnisoBorU(atom, data, 8);
    // Ortep Type 8: D = 2pi^2, C = 2, a*b*
  }
  /*
   * http://www.wwpdb.org/documentation/format23/sect7.html
   * 
 Record Format

COLUMNS       DATA TYPE         FIELD            DEFINITION
------------------------------------------------------------------------
 1 -  6       Record name       "SITE    "
 8 - 10       Integer           seqNum      Sequence number.
12 - 14       LString(3)        siteID      Site name.
16 - 17       Integer           numRes      Number of residues comprising 
                                            site.

19 - 21       Residue name      resName1    Residue name for first residue
                                            comprising site.
23            Character         chainID1    Chain identifier for first residue
                                            comprising site.
24 - 27       Integer           seq1        Residue sequence number for first
                                            residue comprising site.
28            AChar             iCode1      Insertion code for first residue
                                            comprising site.
30 - 32       Residue name      resName2    Residue name for second residue
...
41 - 43       Residue name      resName3    Residue name for third residue
...
52 - 54       Residue name      resName4    Residue name for fourth residue
 
   */
  
  private void site() {
    if (htSites == null) {
      htSites = new Hashtable<String, Map<String, Object>>();
    }
    //int seqNum = parseInt(line, 7, 10);
    int nResidues = parseInt(line, 15, 17);
    String siteID = parseTrimmed(line, 11, 14);
    Map<String, Object> htSite = htSites.get(siteID);
    if (htSite == null) {
      htSite = new Hashtable<String, Object>();
      //htSite.put("seqNum", "site_" + seqNum);
      htSite.put("nResidues", Integer.valueOf(nResidues));
      htSite.put("groups", "");
      htSites.put(siteID, htSite);
    }
    String groups = (String)htSite.get("groups");
    for (int i = 0; i < 4; i++) {
      int pt = 18 + i * 11;
      String resName = parseTrimmed(line, pt, pt + 3);
      if (resName.length() == 0)
        break;
      String chainID = parseTrimmed(line, pt + 4, pt + 5);
      String seq = parseTrimmed(line, pt + 5, pt + 9);
      String iCode = parseTrimmed(line, pt + 9, pt + 10);
      groups += (groups.length() == 0 ? "" : ",") + "[" + resName + "]" + seq;
      if (iCode.length() > 0)
        groups += "^" + iCode;
      if (chainID.length() > 0)
        groups += ":" + chainID;
      htSite.put("groups", groups);
    }
  }
}

