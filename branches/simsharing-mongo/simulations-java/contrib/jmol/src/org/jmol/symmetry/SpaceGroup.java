/* $RCSfile$
 * $Author: egonw $
 * $Date: 2005-11-10 09:52:44 -0600 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
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

package org.jmol.symmetry;


import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.jmol.api.SymmetryInterface;
import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.TextFormat;

/*
 * 
 * A general class to deal with Hermann-Mauguin or Hall names
 * 
 * Bob Hanson 9/2006
 * 
 * references: International Tables for Crystallography Vol. A. (2002) 
 *
 * http://www.iucr.org/iucr-top/cif/cifdic_html/1/cif_core.dic/Ispace_group_symop_operation_xyz.html
 * http://www.iucr.org/iucr-top/cif/cifdic_html/1/cif_core.dic/Isymmetry_equiv_pos_as_xyz.html
 *
 * Hall symbols:
 * 
 * http://cci.lbl.gov/sginfo/hall_symbols.html
 * 
 * and
 * 
 * http://cci.lbl.gov/cctbx/explore_symmetry.html
 * 
 * (-)L   [N_A^T_1]   [N_A^T_2]   ...  [N_A^T_P]   V(Nx Ny Nz)
 * 
 * lattice types S and T are not supported here
 * 
 * data table is from Syd Hall, private email, 9/4/2006, 
 * amended using * ** to indicate nonstandard H-M symbols or full names  
 * 
 * NEVER ACCESS THESE METHODS DIRECTLY! ONLY THROUGH CLASS Symmetry
 * 
 *
 */

class SpaceGroup {

  int index;
  String hallSymbol;
  //String schoenfliesSymbol; //parsed but not read
  String hmSymbol; 
  String hmSymbolFull; 
  //String hmSymbolCompressed; 
  String hmSymbolExt;
  String hmSymbolAbbr;
  String hmSymbolAlternative;
  String hmSymbolAbbrShort;
  char ambiguityType;
  char uniqueAxis; 
  char axisChoice;
  //int cellChoice; 
  //int originChoice;
  String intlTableNumber;
  String intlTableNumberFull;
  String intlTableNumberExt;
  HallInfo hallInfo;
  int latticeParameter;
  char latticeCode;
  SymmetryOperation[] operations;
  int operationCount;
  boolean doNormalize = true;

  SpaceGroup(boolean doNormalize) {
    this.doNormalize = doNormalize;
    addSymmetry("x,y,z", 0);
  }
  
  private SpaceGroup(String cifLine) {
    buildSpaceGroup(cifLine);
  }

  static SpaceGroup createSpaceGroup(int desiredSpaceGroupIndex,
                                                  String name,
                                                  float[] notionalUnitcell) {

    SpaceGroup sg = null;
    if (desiredSpaceGroupIndex >= 0) {
      sg = spaceGroupDefinitions[desiredSpaceGroupIndex];
    } else {
      sg = determineSpaceGroup(name, notionalUnitcell);
      if (sg == null)
        sg = createSpaceGroup(name);
    }
    if (sg != null)
      sg.generateAllOperators(null);
    return sg;
  }

  int addSymmetry(String xyz, int opId) {
    xyz = xyz.toLowerCase();
    if (xyz.indexOf("[[") < 0 && 
        (xyz.indexOf("x") < 0 || xyz.indexOf("y") < 0 || xyz.indexOf("z") < 0))
      return -1;
    return addOperation(xyz, opId);
  }
   
  SymmetryOperation[] finalOperations;
  
  void setFinalOperations(Point3f[] atoms, int atomIndex,
                                                int count, boolean doNormalize) {
    //from AtomSetCollection.applySymmetry only
    if (hallInfo == null && latticeParameter != 0) {
      HallInfo h = new HallInfo(Translation
          .getHallLatticeEquivalent(latticeParameter));
      generateAllOperators(h);
      doNormalize = false;
    }

    finalOperations = new SymmetryOperation[operationCount];
    if (doNormalize && count > 0 && atoms != null) {
      // we must apply this first to (x,y,z) JUST IN CASE the 
      // model center itself is out of bounds, because we want
      // NO operation for (x,y,z). This requires REDEFINING ATOM LOCATIONS
      finalOperations[0] = new SymmetryOperation(operations[0], atoms,
          atomIndex, count, true);
      Point3f atom = atoms[atomIndex];
      Point3f c = new Point3f(atom);
      finalOperations[0].transform(c);
      if (c.distance(atom) > 0.0001) // not cartesian, but this is OK here
        for (int i = 0; i < count; i++) {
          atom = atoms[atomIndex + i];
          c.set(atom);
          finalOperations[0].transform(c);
          atom.set(c);
        }
    }
    for (int i = 0; i < operationCount; i++) {
      finalOperations[i] = new SymmetryOperation(operations[i], atoms,
          atomIndex, count, doNormalize);
    }
  }

  int getOperationCount() {
    return finalOperations.length;
  }

  Matrix4f getOperation(int i) {
    return finalOperations[i];
  }

  String getXyz(int i, boolean doNormalize) {
    return finalOperations[i].getXyz(doNormalize);
  }

  void newPoint(int i, Point3f atom1, Point3f atom2,
                       int transX, int transY, int transZ) {
    finalOperations[i].newPoint(atom1, atom2, transX, transY, transZ);
  }
    
  Object rotateEllipsoid(int i, Point3f ptTemp, Vector3f[] axes,
                                UnitCell unitCell, Point3f ptTemp1,
                                Point3f ptTemp2) {
    return finalOperations[i].rotateEllipsoid(ptTemp, axes, unitCell, ptTemp1,
        ptTemp2);
  }

  static String getInfo(String spaceGroup, SymmetryInterface cellInfo) {
    SpaceGroup sg;
    if (cellInfo != null) {
      if (spaceGroup.indexOf("[") >= 0)
        spaceGroup = spaceGroup.substring(0, spaceGroup.indexOf("[")).trim();
      if (spaceGroup.equals("unspecified!"))
        return "no space group identified in file";
      sg = SpaceGroup.determineSpaceGroup(spaceGroup, cellInfo.getNotionalUnitCell());
    } else if (spaceGroup.equalsIgnoreCase("ALL")) {
      return SpaceGroup.dumpAll();
    } else if (spaceGroup.equalsIgnoreCase("ALLSEITZ")) {
      return SpaceGroup.dumpAllSeitz();
    } else {
      sg = SpaceGroup.determineSpaceGroup(spaceGroup);
      if (sg == null) {
        sg = SpaceGroup.createSpaceGroup(spaceGroup);
      } else {
        StringBuilder sb = new StringBuilder();
        while (sg != null) {
          sb.append(sg.dumpInfo(null));
          sg = SpaceGroup.determineSpaceGroup(spaceGroup, sg);
        }
        return sb.toString();
      }
    }
    return sg == null ? "?" : sg.dumpInfo(cellInfo);
  }
  
  /**
   * 
   * @param cellInfo
   * @return detailed information
   */
  String dumpInfo(SymmetryInterface cellInfo) {
    Object info  = dumpCanonicalSeitzList();
    if (info instanceof SpaceGroup)
      return ((SpaceGroup)info).dumpInfo(null);
    StringBuilder sb = new StringBuilder("\nHermann-Mauguin symbol: ");
    sb.append(hmSymbol).append(hmSymbolExt.length() > 0 ? ":" + hmSymbolExt : "")
        .append("\ninternational table number: ").append(intlTableNumber)
        .append(intlTableNumberExt.length() > 0 ? ":" + intlTableNumberExt : "")
        .append("\n\n").append(operationCount).append(" operators")
        .append(!hallInfo.hallSymbol.equals("--") ? " from Hall symbol "  + hallInfo.hallSymbol: "")
        .append(": ");
    for (int i = 0; i < operationCount; i++) {
      sb.append("\n").append(operations[i].xyz);
    }
    sb.append("\n\n").append(hallInfo == null ? "invalid Hall symbol" : hallInfo.dumpInfo());

    sb.append("\n\ncanonical Seitz: ").append((String) info) 
        .append("\n----------------------------------------------------\n");
    return sb.toString();
  }

  String getName() {
    return hallSymbol + " ["+hmSymbolFull+"]";  
  }
/*  
  int getLatticeParameter() {
    return latticeParameter;
  }
 
  char getLatticeCode() {
    return latticeCode;
  }
*/ 
  String getLatticeDesignation() {    
    return latticeCode + ": " + Translation.getLatticeDesignation(latticeParameter);
  }  
 
  void setLattice(int latticeParameter) {
    // implication here is that we do NOT have a Hall symbol.
    // so we generate one.
    // The idea here is that we can represent any LATT number
    // as a simple set of rotation/translation operations
    this.latticeParameter = latticeParameter;
    latticeCode = Translation.getLatticeCode(latticeParameter);
    if (latticeParameter > 10) { // use negative
      this.latticeParameter = -Translation.getLatticeIndex(latticeCode);
    }
  }

  ///// private methods /////
  
  private static String[] canonicalSeitzList;
  
  private Object dumpCanonicalSeitzList() {
    if (hallInfo == null)
      hallInfo = new HallInfo(hallSymbol);
    generateAllOperators(null);
    String[] list = new String[operationCount];
    for (int i = 0; i < operationCount; i++)
      list[i] = SymmetryOperation.dumpCanonicalSeitz(operations[i]);
    Arrays.sort(list, 0, operationCount);
    StringBuilder sb = new StringBuilder("\n[");
    for (int i = 0; i < operationCount; i++)
      sb.append(list[i].replace('\t',' ').replace('\n',' ')).append("; ");
    sb.append("]");
    if (index >= spaceGroupDefinitions.length) {
      if (canonicalSeitzList == null) {
      canonicalSeitzList = new String[spaceGroupDefinitions.length];
      for (int i = 0; i < spaceGroupDefinitions.length; i++)
        canonicalSeitzList[i] = (String) spaceGroupDefinitions[i].dumpCanonicalSeitzList();
      }
      String s = sb.toString();
      for (int i = 0; i < spaceGroupDefinitions.length; i++)
        if (canonicalSeitzList[i].indexOf(s) >= 0)
          return spaceGroupDefinitions[i];
    }
    return (index >= 0 && index < spaceGroupDefinitions.length 
        ? hallSymbol + " = " : "") + sb.toString();
  }
  
  private final static String dumpAll() {
   StringBuilder sb = new StringBuilder();
   for (int i = 0; i < spaceGroupDefinitions.length; i++)
     sb.append("\n----------------------\n" + spaceGroupDefinitions[i].dumpInfo(null));
   return sb.toString();
  }
  
  private final static String dumpAllSeitz() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < spaceGroupDefinitions.length; i++)
      sb.append("\n").append(spaceGroupDefinitions[i].dumpCanonicalSeitzList());
    return sb.toString();
  }
   
  private void setLattice(char latticeCode, boolean isCentrosymmetric) {
    this.latticeCode = latticeCode;
    latticeParameter = Translation.getLatticeIndex(latticeCode);
    if (!isCentrosymmetric)
      latticeParameter = -latticeParameter;
  }
  
  private final static SpaceGroup createSpaceGroup(String name) {
    name = name.trim();
    SpaceGroup sg = determineSpaceGroup(name);
    HallInfo hallInfo;
    if (sg == null) {
      // try unconventional Hall symbol
      hallInfo = new HallInfo(name);
      if (hallInfo.nRotations > 0) {
        sg = new SpaceGroup("0;--;--;" + name);
        sg.hallInfo = hallInfo;
      } else if (name.indexOf(",") >= 0) {
        sg = new SpaceGroup("0;--;--;--");
        sg.doNormalize = false;
        sg.generateOperatorsFromXyzInfo(name);
      } else {
        sg = null;
      }
    }
    if (sg != null)
      sg.generateAllOperators(null);
    return sg;
  }
  
  Map<String, Integer> xyzList = new Hashtable<String, Integer>();
  private int addOperation(String xyz0, int opId) {
    if (xyz0 == null || xyz0.length() < 3) {
      xyzList = new Hashtable<String, Integer>();
      return -1;
    }
    boolean isSpecial = (xyz0.charAt(0) == '=');
    if (isSpecial) xyz0 = xyz0.substring(1);
    if (xyzList.containsKey(xyz0))
      return xyzList.get(xyz0).intValue();

    SymmetryOperation symmetryOperation = new SymmetryOperation(doNormalize, opId);
    if (!symmetryOperation.setMatrixFromXYZ(xyz0)) {
      Logger.error("couldn't interpret symmetry operation: " + xyz0);      
      return -1;
    }
    String xyz = symmetryOperation.xyz;
    if (!isSpecial) {
      // ! in character 0 indicates we are using the symop() function and want to be explicit
      if (xyzList.containsKey(xyz))
        return xyzList.get(xyz).intValue();
      xyzList.put(xyz, Integer.valueOf(operationCount));
    }
    if (!xyz.equals(xyz0))
      xyzList.put(xyz0, Integer.valueOf(operationCount));
    if (operations == null) {
      operations = new SymmetryOperation[4];
      operationCount = 0;
    }
    if (operationCount == operations.length)
      operations = (SymmetryOperation[]) ArrayUtil.setLength(operations,
          operationCount * 2);
    operations[operationCount++] = symmetryOperation;
    if (Logger.debugging)
        Logger.debug("\naddOperation " + operationCount
        + symmetryOperation.dumpInfo());
    return operationCount - 1;
  }

  private void generateOperatorsFromXyzInfo(String xyzInfo) {
    addOperation(null, 0);
    addSymmetry("x,y,z", 0);
    String[] terms = TextFormat.split(xyzInfo.toLowerCase(), ";");
    for (int i = 0; i < terms.length; i++)
      addSymmetry(terms[i], 0);
  }
  
  /// operation based on Hall name and unit cell parameters only

  private void generateAllOperators(HallInfo h) {
    if (h == null) {
      h = hallInfo;
      if (operationCount > 0)
        return;
      operations = new SymmetryOperation[4];
      operationCount = 0;
      if (hallInfo == null || hallInfo.nRotations == 0)
        h = hallInfo = new HallInfo(hallSymbol);
      setLattice(hallInfo.latticeCode, hallInfo.isCentrosymmetric);
      addOperation(null, 0);
      addSymmetry("x,y,z", 0);
    }
    Matrix4f mat1 = new Matrix4f();
    Matrix4f operation = new Matrix4f();
    Matrix4f[] newOps = new Matrix4f[7];
    for (int i = 0; i < 7; i++)
      newOps[i] = new Matrix4f();
    // prior to Jmol 11.7.36/11.6.23 this was setting nOps within the loop
    // and setIdentity() outside the loop. That caused a multiplication of
    // operations, not a resetting of them each time. 
    for (int i = 0; i < h.nRotations; i++) {
      mat1.set(h.rotationTerms[i].seitzMatrix12ths);
      int nRot = h.rotationTerms[i].order;
      // this would iterate int nOps = operationCount;
      newOps[0].setIdentity();
      int nOps = operationCount;
      for (int j = 1; j <= nRot; j++) {
        newOps[j].mul(mat1, newOps[0]);
        newOps[0].set(newOps[j]);
        for (int k = 0; k < nOps; k++) {
          
          operation.mul(newOps[j], operations[k]);
          SymmetryOperation.normalizeTranslation(operation);
          String xyz = SymmetryOperation.getXYZFromMatrix(operation, true, true, true);
          addSymmetry(xyz, operation);
        }
      }
    }
  }

  private void addSymmetry(String xyz, Matrix4f operation) {
    int iop = addOperation(xyz, 0);
    if (iop < 0)
      return;
    SymmetryOperation symmetryOperation = operations[iop];
    symmetryOperation.set(operation);
  }

  private final static SpaceGroup determineSpaceGroup(String name) {
    return determineSpaceGroup(name, 0f, 0f, 0f, 0f, 0f, 0f, -1);
  }

  private final static SpaceGroup determineSpaceGroup(String name, SpaceGroup sg) {
    return determineSpaceGroup(name, 0f, 0f, 0f, 0f, 0f, 0f, sg.index);
  }

  private final static SpaceGroup determineSpaceGroup(String name,
                                                     float[] notionalUnitcell) {
    if (notionalUnitcell == null)
      return determineSpaceGroup(name, 0f, 0f, 0f, 0f, 0f, 0f, -1);
    return determineSpaceGroup(name, notionalUnitcell[0], notionalUnitcell[1],
        notionalUnitcell[2], notionalUnitcell[3], notionalUnitcell[4],
        notionalUnitcell[5], -1);
  }

  private final static SpaceGroup determineSpaceGroup(String name, float a, float b,
                                                float c, float alpha,
                                                float beta, float gamma,
                                                int lastIndex) {

    int i = determineSpaceGroupIndex(name, a, b, c, alpha, beta, gamma,
        lastIndex);
    return (i >= 0 ? spaceGroupDefinitions[i] : null);
  }

  private final static int NAME_HALL = 5;
  private final static int NAME_HM = 3;

  private final static int determineSpaceGroupIndex(String name, float a,
                                                    float b, float c,
                                                    float alpha, float beta,
                                                    float gamma, int lastIndex) {

    if (lastIndex < 0)
      lastIndex = spaceGroupDefinitions.length;
    name = name.trim().toLowerCase();
    int nameType = (name.startsWith("hall:") ? NAME_HALL : name
        .startsWith("hm:") ? NAME_HM : 0);
    if (nameType > 0)
      name = name.substring(nameType);
    String nameExt = name;
    int i;
    boolean haveExtension = false;

    // '_' --> ' '
    name = name.replace('_', ' ');

    // get lattice term to upper case and separated
    if (name.length() >= 2) {
      i = (name.indexOf("-") == 0 ? 2 : 1);
      if (i < name.length() && name.charAt(i) != ' ')
        name = name.substring(0, i) + " " + name.substring(i);
      name = name.substring(0, 2).toUpperCase() + name.substring(2);
    }

    // get extension
    String ext = "";
    if ((i = name.indexOf(":")) > 0) {
      ext = name.substring(i + 1);
      name = name.substring(0, i).trim();
      haveExtension = true;
    }

    if (nameType != NAME_HALL && !haveExtension
        && Parser.isOneOf(name, ambiguousNames)) {
      ext = "?";
      haveExtension = true;
    }

    // generate spaceless abbreviation "P m m m" --> "Pmmm"  "P 2(1)/c" --> "P21/c"
    String abbr = TextFormat.replaceAllCharacters(name, " ()", "");

    SpaceGroup s;

    // exact matches:

    // Hall symbol

    if (nameType != NAME_HM && !haveExtension)
      for (i = lastIndex; --i >= 0;) {
        s = spaceGroupDefinitions[i];
        if (s.hallSymbol.equals(name))
          return i;
      }

    if (nameType != NAME_HALL) {

      // Full intl table entry, including :xx

      if (nameType != NAME_HM)
        for (i = lastIndex; --i >= 0;) {
          s = spaceGroupDefinitions[i];
          if (s.intlTableNumberFull.equals(nameExt))
            return i;
        }

      // Full H-M symbol, including :xx

      // BUT some on the list presume defaults. The way to finesse this is
      // to add ":?" to a space group name to force axis ambiguity check

      for (i = lastIndex; --i >= 0;) {
        s = spaceGroupDefinitions[i];
        if (s.hmSymbolFull.equals(nameExt))
          return i;
      }

      // alternative, but unique H-M symbol, specifically for F m 3 m/F m -3 m type
      for (i = lastIndex; --i >= 0;) {
        s = spaceGroupDefinitions[i];
        if (s.hmSymbolAlternative != null
            && s.hmSymbolAlternative.equals(nameExt))
          return i;
      }

      // Abbreviated H-M with intl table :xx

      if (haveExtension) // P2/m:a      
        for (i = lastIndex; --i >= 0;) {
          s = spaceGroupDefinitions[i];
          if (s.hmSymbolAbbr.equals(abbr) && s.intlTableNumberExt.equals(ext))
            return i;
        }

      // shortened -- not including " 1 " terms
      if (haveExtension) // P2/m:a      
        for (i = lastIndex; --i >= 0;) {
          s = spaceGroupDefinitions[i];
          if (s.hmSymbolAbbrShort.equals(abbr)
              && s.intlTableNumberExt.equals(ext))
            return i;
        }

      // unique axis, cell and origin options with H-M abbr

      char uniqueAxis = determineUniqueAxis(a, b, c, alpha, beta, gamma);

      if (!haveExtension || ext.charAt(0) == '?')
        // no extension or unknown extension, so we look for unique axis
        for (i = lastIndex; --i >= 0;) {
          s = spaceGroupDefinitions[i];
          if (s.hmSymbolAbbr.equals(abbr) || s.hmSymbolAbbrShort.equals(abbr)) {
            switch (s.ambiguityType) {
            case '\0':
              return i;
            case 'a':
              if (s.uniqueAxis == uniqueAxis || uniqueAxis == '\0')
                return i;
              break;
            case 'o':
              if (ext.length() == 0) {
                if (s.hmSymbolExt.equals("2"))
                  return i; // defaults to origin:2
              } else if (s.hmSymbolExt.equals(ext))
                return i;
              break;
            case 't':
              if (ext.length() == 0) {
                if (s.axisChoice == 'h')
                  return i; //defaults to hexagonal
              } else if ((s.axisChoice + "").equals(ext))
                return i;
              break;
            }
          }
        }

    }
    // inexact just the number; no extension indicated

    if (ext.length() == 0)
      for (i = lastIndex; --i >= 0;) {
        s = spaceGroupDefinitions[i];
        if (s.intlTableNumber.equals(nameExt))
          return i;
      }
    return -1;
  }
   
   private final static char determineUniqueAxis(float a, float b, float c, float alpha, float beta, float gamma) {
     if (a == b)
       return (b == c ? '\0' : 'c');
     if (b == c)
       return 'a';
     if (c == a)
       return 'b';
     if (alpha == beta)
       return (beta == gamma ? '\0' : 'c');
     if (beta == gamma)
       return 'a';
     if (gamma == alpha)
       return 'b';
     return '\0';
   }

  ///  data  ///

  private static int sgIndex = -1;
  
  private static String ambiguousNames = "";
  private static String lastInfo = "";
  
  private void buildSpaceGroup(String cifLine) {
    index = ++sgIndex;
    String[] terms = TextFormat.split(cifLine.toLowerCase(), ";");
    String[] parts;

    intlTableNumberFull = terms[0].trim(); // International Table Number :
                                           // options
    parts = TextFormat.split(intlTableNumberFull, ":");
    intlTableNumber = parts[0];
    intlTableNumberExt = (parts.length == 1 ? "" : parts[1]);
    ambiguityType = '\0';
    if (intlTableNumberExt.length() > 0) {
      String term = intlTableNumberExt;
      if (term.startsWith("-"))
        term = term.substring(1);
      if (term.equals("h") || term.equals("r")) {
        ambiguityType = 't';
        axisChoice = intlTableNumberExt.charAt(0);
      } else if (intlTableNumberExt.startsWith("1")
          || intlTableNumberExt.startsWith("2")) {
        ambiguityType = 'o';
        // originChoice = intlTableNumberExt.charAt(0);
      } else if (intlTableNumberExt.length() <= 2) { // :a or :b3
        ambiguityType = 'a';
        uniqueAxis = intlTableNumberExt.charAt(0);
        // if (intlTableNumberExt.length() == 2)
        // cellChoice = intlTableNumberExt.charAt(1);
      }
    }
    /* schoenfliesSymbol = terms[1] */

    hmSymbolFull = Character.toUpperCase(terms[2].charAt(0))
        + terms[2].substring(1);
    parts = TextFormat.split(hmSymbolFull, ":");
    hmSymbol = parts[0];
    hmSymbolExt = (parts.length == 1 ? "" : parts[1]);
    int pt = hmSymbol.indexOf(" -3");
    if (pt >= 1)
      if ("admn".indexOf(hmSymbol.charAt(pt - 1)) >= 0) {
        hmSymbolAlternative = (hmSymbol.substring(0, pt) + " 3"
            + hmSymbol.substring(pt + 3)).toLowerCase();
      }
    hmSymbolAbbr = TextFormat.simpleReplace(hmSymbol, " ", "");
    hmSymbolAbbrShort = TextFormat.simpleReplace(hmSymbol, " 1", "");
    hmSymbolAbbrShort = TextFormat.simpleReplace(hmSymbolAbbrShort, " ", "");

    hallSymbol = terms[3];
    if (hallSymbol.length() > 1)
      hallSymbol = hallSymbol.substring(0, 2).toUpperCase() + hallSymbol.substring(2);
    String info = intlTableNumber + hallSymbol;
    if (intlTableNumber.charAt(0) != '0' && lastInfo.equals(info))
      ambiguousNames += hmSymbol + ";";
    lastInfo = info;

//    System.out.println(intlTableNumber + (intlTableNumberExt.equals("") ? "" : ":" + intlTableNumberExt) + "\t"
  //      + hmSymbol + "\t" + hmSymbolAbbr + "\t" + hmSymbolAbbrShort + "\t"
    //    + hallSymbol);
  }

  
  private final static SpaceGroup[] spaceGroupDefinitions = {
    new SpaceGroup("1;c1^1;p 1;p 1")
    , new SpaceGroup("2;ci^1;p -1;-p 1")
    , new SpaceGroup("3:b;c2^1;p 1 2 1;p 2y") //full name
    , new SpaceGroup("3:b;c2^1;p 2;p 2y")
    , new SpaceGroup("3:c;c2^1;p 1 1 2;p 2")
    , new SpaceGroup("3:a;c2^1;p 2 1 1;p 2x")
    , new SpaceGroup("4:b;c2^2;p 1 21 1;p 2yb") //full name
    , new SpaceGroup("4:b;c2^2;p 21;p 2yb")
    , new SpaceGroup("4:b*;c2^2;p 1 21 1*;p 2y1") //nonstandard
    , new SpaceGroup("4:c;c2^2;p 1 1 21;p 2c")
    , new SpaceGroup("4:c*;c2^2;p 1 1 21*;p 21") //nonstandard
    , new SpaceGroup("4:a;c2^2;p 21 1 1;p 2xa")
    , new SpaceGroup("4:a*;c2^2;p 21 1 1*;p 2x1") //nonstandard
    , new SpaceGroup("5:b1;c2^3;c 1 2 1;c 2y") //full name
    , new SpaceGroup("5:b1;c2^3;c 2;c 2y")
    , new SpaceGroup("5:b2;c2^3;a 1 2 1;a 2y")
    , new SpaceGroup("5:b3;c2^3;i 1 2 1;i 2y")
    , new SpaceGroup("5:c1;c2^3;a 1 1 2;a 2")
    , new SpaceGroup("5:c2;c2^3;b 1 1 2;b 2")
    , new SpaceGroup("5:c3;c2^3;i 1 1 2;i 2")
    , new SpaceGroup("5:a1;c2^3;b 2 1 1;b 2x")
    , new SpaceGroup("5:a2;c2^3;c 2 1 1;c 2x")
    , new SpaceGroup("5:a3;c2^3;i 2 1 1;i 2x")
    , new SpaceGroup("6:b;cs^1;p 1 m 1;p -2y") //full name
    , new SpaceGroup("6:b;cs^1;p m;p -2y")
    , new SpaceGroup("6:c;cs^1;p 1 1 m;p -2")
    , new SpaceGroup("6:a;cs^1;p m 1 1;p -2x")
    , new SpaceGroup("7:b1;cs^2;p 1 c 1;p -2yc") //full name
    , new SpaceGroup("7:b1;cs^2;p c;p -2yc")
    , new SpaceGroup("7:b2;cs^2;p 1 n 1;p -2yac") //full name
    , new SpaceGroup("7:b2;cs^2;p n;p -2yac")
    , new SpaceGroup("7:b3;cs^2;p 1 a 1;p -2ya") //full name
    , new SpaceGroup("7:b3;cs^2;p a;p -2ya")
    , new SpaceGroup("7:c1;cs^2;p 1 1 a;p -2a")
    , new SpaceGroup("7:c2;cs^2;p 1 1 n;p -2ab")
    , new SpaceGroup("7:c3;cs^2;p 1 1 b;p -2b")
    , new SpaceGroup("7:a1;cs^2;p b 1 1;p -2xb")
    , new SpaceGroup("7:a2;cs^2;p n 1 1;p -2xbc")
    , new SpaceGroup("7:a3;cs^2;p c 1 1;p -2xc")
    , new SpaceGroup("8:b1;cs^3;c 1 m 1;c -2y") //full name
    , new SpaceGroup("8:b1;cs^3;c m;c -2y")
    , new SpaceGroup("8:b2;cs^3;a 1 m 1;a -2y")
    , new SpaceGroup("8:b3;cs^3;i 1 m 1;i -2y") //full name
    , new SpaceGroup("8:b3;cs^3;i m;i -2y")
    , new SpaceGroup("8:c1;cs^3;a 1 1 m;a -2")
    , new SpaceGroup("8:c2;cs^3;b 1 1 m;b -2")
    , new SpaceGroup("8:c3;cs^3;i 1 1 m;i -2")
    , new SpaceGroup("8:a1;cs^3;b m 1 1;b -2x")
    , new SpaceGroup("8:a2;cs^3;c m 1 1;c -2x")
    , new SpaceGroup("8:a3;cs^3;i m 1 1;i -2x")
    , new SpaceGroup("9:b1;cs^4;c 1 c 1;c -2yc") //full name
    , new SpaceGroup("9:b1;cs^4;c c;c -2yc")
    , new SpaceGroup("9:b2;cs^4;a 1 n 1;a -2yab")
    , new SpaceGroup("9:b3;cs^4;i 1 a 1;i -2ya")
    , new SpaceGroup("9:-b1;cs^4;a 1 a 1;a -2ya")
    , new SpaceGroup("9:-b2;cs^4;c 1 n 1;c -2yac")
    , new SpaceGroup("9:-b3;cs^4;i 1 c 1;i -2yc")
    , new SpaceGroup("9:c1;cs^4;a 1 1 a;a -2a")
    , new SpaceGroup("9:c2;cs^4;b 1 1 n;b -2ab")
    , new SpaceGroup("9:c3;cs^4;i 1 1 b;i -2b")
    , new SpaceGroup("9:-c1;cs^4;b 1 1 b;b -2b")
    , new SpaceGroup("9:-c2;cs^4;a 1 1 n;a -2ab")
    , new SpaceGroup("9:-c3;cs^4;i 1 1 a;i -2a")
    , new SpaceGroup("9:a1;cs^4;b b 1 1;b -2xb")
    , new SpaceGroup("9:a2;cs^4;c n 1 1;c -2xac")
    , new SpaceGroup("9:a3;cs^4;i c 1 1;i -2xc")
    , new SpaceGroup("9:-a1;cs^4;c c 1 1;c -2xc")
    , new SpaceGroup("9:-a2;cs^4;b n 1 1;b -2xab")
    , new SpaceGroup("9:-a3;cs^4;i b 1 1;i -2xb")
    , new SpaceGroup("10:b;c2h^1;p 1 2/m 1;-p 2y") //full name
    , new SpaceGroup("10:b;c2h^1;p 2/m;-p 2y")
    , new SpaceGroup("10:c;c2h^1;p 1 1 2/m;-p 2")
    , new SpaceGroup("10:a;c2h^1;p 2/m 1 1;-p 2x")
    , new SpaceGroup("11:b;c2h^2;p 1 21/m 1;-p 2yb") //full name
    , new SpaceGroup("11:b;c2h^2;p 21/m;-p 2yb")
    , new SpaceGroup("11:b*;c2h^2;p 1 21/m 1*;-p 2y1") //nonstandard
    , new SpaceGroup("11:c;c2h^2;p 1 1 21/m;-p 2c")
    , new SpaceGroup("11:c*;c2h^2;p 1 1 21/m*;-p 21") //nonstandard
    , new SpaceGroup("11:a;c2h^2;p 21/m 1 1;-p 2xa")
    , new SpaceGroup("11:a*;c2h^2;p 21/m 1 1*;-p 2x1") //nonstandard
    , new SpaceGroup("12:b1;c2h^3;c 1 2/m 1;-c 2y") //full name
    , new SpaceGroup("12:b1;c2h^3;c 2/m;-c 2y")
    , new SpaceGroup("12:b2;c2h^3;a 1 2/m 1;-a 2y")
    , new SpaceGroup("12:b3;c2h^3;i 1 2/m 1;-i 2y") //full name
    , new SpaceGroup("12:b3;c2h^3;i 2/m;-i 2y")
    , new SpaceGroup("12:c1;c2h^3;a 1 1 2/m;-a 2")
    , new SpaceGroup("12:c2;c2h^3;b 1 1 2/m;-b 2")
    , new SpaceGroup("12:c3;c2h^3;i 1 1 2/m;-i 2")
    , new SpaceGroup("12:a1;c2h^3;b 2/m 1 1;-b 2x")
    , new SpaceGroup("12:a2;c2h^3;c 2/m 1 1;-c 2x")
    , new SpaceGroup("12:a3;c2h^3;i 2/m 1 1;-i 2x")
    , new SpaceGroup("13:b1;c2h^4;p 1 2/c 1;-p 2yc") //full name
    , new SpaceGroup("13:b1;c2h^4;p 2/c;-p 2yc")
    , new SpaceGroup("13:b2;c2h^4;p 1 2/n 1;-p 2yac") //full name
    , new SpaceGroup("13:b2;c2h^4;p 2/n;-p 2yac")
    , new SpaceGroup("13:b3;c2h^4;p 1 2/a 1;-p 2ya") //full name
    , new SpaceGroup("13:b3;c2h^4;p 2/a;-p 2ya")
    , new SpaceGroup("13:c1;c2h^4;p 1 1 2/a;-p 2a")
    , new SpaceGroup("13:c2;c2h^4;p 1 1 2/n;-p 2ab")
    , new SpaceGroup("13:c3;c2h^4;p 1 1 2/b;-p 2b")
    , new SpaceGroup("13:a1;c2h^4;p 2/b 1 1;-p 2xb")
    , new SpaceGroup("13:a2;c2h^4;p 2/n 1 1;-p 2xbc")
    , new SpaceGroup("13:a3;c2h^4;p 2/c 1 1;-p 2xc")
    , new SpaceGroup("14:b1;c2h^5;p 1 21/c 1;-p 2ybc") //full name
    , new SpaceGroup("14:b1;c2h^5;p 21/c;-p 2ybc")
    , new SpaceGroup("14:b2;c2h^5;p 1 21/n 1;-p 2yn") //full name
    , new SpaceGroup("14:b2;c2h^5;p 21/n;-p 2yn")
    , new SpaceGroup("14:b3;c2h^5;p 1 21/a 1;-p 2yab") //full name
    , new SpaceGroup("14:b3;c2h^5;p 21/a;-p 2yab")
    , new SpaceGroup("14:c1;c2h^5;p 1 1 21/a;-p 2ac")
    , new SpaceGroup("14:c2;c2h^5;p 1 1 21/n;-p 2n")
    , new SpaceGroup("14:c3;c2h^5;p 1 1 21/b;-p 2bc")
    , new SpaceGroup("14:a1;c2h^5;p 21/b 1 1;-p 2xab")
    , new SpaceGroup("14:a2;c2h^5;p 21/n 1 1;-p 2xn")
    , new SpaceGroup("14:a3;c2h^5;p 21/c 1 1;-p 2xac")
    , new SpaceGroup("15:b1;c2h^6;c 1 2/c 1;-c 2yc") //full name
    , new SpaceGroup("15:b1;c2h^6;c 2/c;-c 2yc")
    , new SpaceGroup("15:b2;c2h^6;a 1 2/n 1;-a 2yab")
    , new SpaceGroup("15:b3;c2h^6;i 1 2/a 1;-i 2ya") //full name
    , new SpaceGroup("15:b3;c2h^6;i 2/a;-i 2ya")
    , new SpaceGroup("15:-b1;c2h^6;a 1 2/a 1;-a 2ya")
    , new SpaceGroup("15:-b2;c2h^6;c 1 2/n 1;-c 2yac") //full name
    , new SpaceGroup("15:-b2;c2h^6;c 2/n;-c 2yac")
    , new SpaceGroup("15:-b3;c2h^6;i 1 2/c 1;-i 2yc") //full name
    , new SpaceGroup("15:-b3;c2h^6;i 2/c;-i 2yc")
    , new SpaceGroup("15:c1;c2h^6;a 1 1 2/a;-a 2a")
    , new SpaceGroup("15:c2;c2h^6;b 1 1 2/n;-b 2ab")
    , new SpaceGroup("15:c3;c2h^6;i 1 1 2/b;-i 2b")
    , new SpaceGroup("15:-c1;c2h^6;b 1 1 2/b;-b 2b")
    , new SpaceGroup("15:-c2;c2h^6;a 1 1 2/n;-a 2ab")
    , new SpaceGroup("15:-c3;c2h^6;i 1 1 2/a;-i 2a")
    , new SpaceGroup("15:a1;c2h^6;b 2/b 1 1;-b 2xb")
    , new SpaceGroup("15:a2;c2h^6;c 2/n 1 1;-c 2xac")
    , new SpaceGroup("15:a3;c2h^6;i 2/c 1 1;-i 2xc")
    , new SpaceGroup("15:-a1;c2h^6;c 2/c 1 1;-c 2xc")
    , new SpaceGroup("15:-a2;c2h^6;b 2/n 1 1;-b 2xab")
    , new SpaceGroup("15:-a3;c2h^6;i 2/b 1 1;-i 2xb")
    , new SpaceGroup("16;d2^1;p 2 2 2;p 2 2")
    , new SpaceGroup("17;d2^2;p 2 2 21;p 2c 2")
    , new SpaceGroup("17*;d2^2;p 2 2 21*;p 21 2") //nonstandard
    , new SpaceGroup("17:cab;d2^2;p 21 2 2;p 2a 2a")
    , new SpaceGroup("17:bca;d2^2;p 2 21 2;p 2 2b")
    , new SpaceGroup("18;d2^3;p 21 21 2;p 2 2ab")
    , new SpaceGroup("18:cab;d2^3;p 2 21 21;p 2bc 2")
    , new SpaceGroup("18:bca;d2^3;p 21 2 21;p 2ac 2ac")
    , new SpaceGroup("19;d2^4;p 21 21 21;p 2ac 2ab")
    , new SpaceGroup("20;d2^5;c 2 2 21;c 2c 2")
    , new SpaceGroup("20*;d2^5;c 2 2 21*;c 21 2") //nonstandard
    , new SpaceGroup("20:cab;d2^5;a 21 2 2;a 2a 2a")
    , new SpaceGroup("20:cab*;d2^5;a 21 2 2*;a 2a 21") //nonstandard
    , new SpaceGroup("20:bca;d2^5;b 2 21 2;b 2 2b")
    , new SpaceGroup("21;d2^6;c 2 2 2;c 2 2")
    , new SpaceGroup("21:cab;d2^6;a 2 2 2;a 2 2")
    , new SpaceGroup("21:bca;d2^6;b 2 2 2;b 2 2")
    , new SpaceGroup("22;d2^7;f 2 2 2;f 2 2")
    , new SpaceGroup("23;d2^8;i 2 2 2;i 2 2")
    , new SpaceGroup("24;d2^9;i 21 21 21;i 2b 2c")
    , new SpaceGroup("25;c2v^1;p m m 2;p 2 -2")
    , new SpaceGroup("25:cab;c2v^1;p 2 m m;p -2 2")
    , new SpaceGroup("25:bca;c2v^1;p m 2 m;p -2 -2")
    , new SpaceGroup("26;c2v^2;p m c 21;p 2c -2")
    , new SpaceGroup("26*;c2v^2;p m c 21*;p 21 -2") //nonstandard
    , new SpaceGroup("26:ba-c;c2v^2;p c m 21;p 2c -2c")
    , new SpaceGroup("26:ba-c*;c2v^2;p c m 21*;p 21 -2c") //nonstandard
    , new SpaceGroup("26:cab;c2v^2;p 21 m a;p -2a 2a")
    , new SpaceGroup("26:-cba;c2v^2;p 21 a m;p -2 2a")
    , new SpaceGroup("26:bca;c2v^2;p b 21 m;p -2 -2b")
    , new SpaceGroup("26:a-cb;c2v^2;p m 21 b;p -2b -2")
    , new SpaceGroup("27;c2v^3;p c c 2;p 2 -2c")
    , new SpaceGroup("27:cab;c2v^3;p 2 a a;p -2a 2")
    , new SpaceGroup("27:bca;c2v^3;p b 2 b;p -2b -2b")
    , new SpaceGroup("28;c2v^4;p m a 2;p 2 -2a")
    , new SpaceGroup("28*;c2v^4;p m a 2*;p 2 -21") //nonstandard
    , new SpaceGroup("28:ba-c;c2v^4;p b m 2;p 2 -2b")
    , new SpaceGroup("28:cab;c2v^4;p 2 m b;p -2b 2")
    , new SpaceGroup("28:-cba;c2v^4;p 2 c m;p -2c 2")
    , new SpaceGroup("28:-cba*;c2v^4;p 2 c m*;p -21 2") //nonstandard
    , new SpaceGroup("28:bca;c2v^4;p c 2 m;p -2c -2c")
    , new SpaceGroup("28:a-cb;c2v^4;p m 2 a;p -2a -2a")
    , new SpaceGroup("29;c2v^5;p c a 21;p 2c -2ac")
    , new SpaceGroup("29:ba-c;c2v^5;p b c 21;p 2c -2b")
    , new SpaceGroup("29:cab;c2v^5;p 21 a b;p -2b 2a")
    , new SpaceGroup("29:-cba;c2v^5;p 21 c a;p -2ac 2a")
    , new SpaceGroup("29:bca;c2v^5;p c 21 b;p -2bc -2c")
    , new SpaceGroup("29:a-cb;c2v^5;p b 21 a;p -2a -2ab")
    , new SpaceGroup("30;c2v^6;p n c 2;p 2 -2bc")
    , new SpaceGroup("30:ba-c;c2v^6;p c n 2;p 2 -2ac")
    , new SpaceGroup("30:cab;c2v^6;p 2 n a;p -2ac 2")
    , new SpaceGroup("30:-cba;c2v^6;p 2 a n;p -2ab 2")
    , new SpaceGroup("30:bca;c2v^6;p b 2 n;p -2ab -2ab")
    , new SpaceGroup("30:a-cb;c2v^6;p n 2 b;p -2bc -2bc")
    , new SpaceGroup("31;c2v^7;p m n 21;p 2ac -2")
    , new SpaceGroup("31:ba-c;c2v^7;p n m 21;p 2bc -2bc")
    , new SpaceGroup("31:cab;c2v^7;p 21 m n;p -2ab 2ab")
    , new SpaceGroup("31:-cba;c2v^7;p 21 n m;p -2 2ac")
    , new SpaceGroup("31:bca;c2v^7;p n 21 m;p -2 -2bc")
    , new SpaceGroup("31:a-cb;c2v^7;p m 21 n;p -2ab -2")
    , new SpaceGroup("32;c2v^8;p b a 2;p 2 -2ab")
    , new SpaceGroup("32:cab;c2v^8;p 2 c b;p -2bc 2")
    , new SpaceGroup("32:bca;c2v^8;p c 2 a;p -2ac -2ac")
    , new SpaceGroup("33;c2v^9;p n a 21;p 2c -2n")
    , new SpaceGroup("33*;c2v^9;p n a 21*;p 21 -2n") //nonstandard
    , new SpaceGroup("33:ba-c;c2v^9;p b n 21;p 2c -2ab")
    , new SpaceGroup("33:ba-c*;c2v^9;p b n 21*;p 21 -2ab") //nonstandard
    , new SpaceGroup("33:cab;c2v^9;p 21 n b;p -2bc 2a")
    , new SpaceGroup("33:cab*;c2v^9;p 21 n b*;p -2bc 21") //nonstandard
    , new SpaceGroup("33:-cba;c2v^9;p 21 c n;p -2n 2a")
    , new SpaceGroup("33:-cba*;c2v^9;p 21 c n*;p -2n 21") //nonstandard
    , new SpaceGroup("33:bca;c2v^9;p c 21 n;p -2n -2ac")
    , new SpaceGroup("33:a-cb;c2v^9;p n 21 a;p -2ac -2n")
    , new SpaceGroup("34;c2v^10;p n n 2;p 2 -2n")
    , new SpaceGroup("34:cab;c2v^10;p 2 n n;p -2n 2")
    , new SpaceGroup("34:bca;c2v^10;p n 2 n;p -2n -2n")
    , new SpaceGroup("35;c2v^11;c m m 2;c 2 -2")
    , new SpaceGroup("35:cab;c2v^11;a 2 m m;a -2 2")
    , new SpaceGroup("35:bca;c2v^11;b m 2 m;b -2 -2")
    , new SpaceGroup("36;c2v^12;c m c 21;c 2c -2")
    , new SpaceGroup("36*;c2v^12;c m c 21*;c 21 -2") //nonstandard
    , new SpaceGroup("36:ba-c;c2v^12;c c m 21;c 2c -2c")
    , new SpaceGroup("36:ba-c*;c2v^12;c c m 21*;c 21 -2c") //nonstandard
    , new SpaceGroup("36:cab;c2v^12;a 21 m a;a -2a 2a")
    , new SpaceGroup("36:cab*;c2v^12;a 21 m a*;a -2a 21") //nonstandard
    , new SpaceGroup("36:-cba;c2v^12;a 21 a m;a -2 2a")
    , new SpaceGroup("36:-cba*;c2v^12;a 21 a m*;a -2 21") //nonstandard
    , new SpaceGroup("36:bca;c2v^12;b b 21 m;b -2 -2b")
    , new SpaceGroup("36:a-cb;c2v^12;b m 21 b;b -2b -2")
    , new SpaceGroup("37;c2v^13;c c c 2;c 2 -2c")
    , new SpaceGroup("37:cab;c2v^13;a 2 a a;a -2a 2")
    , new SpaceGroup("37:bca;c2v^13;b b 2 b;b -2b -2b")
    , new SpaceGroup("38;c2v^14;a m m 2;a 2 -2")
    , new SpaceGroup("38:ba-c;c2v^14;b m m 2;b 2 -2")
    , new SpaceGroup("38:cab;c2v^14;b 2 m m;b -2 2")
    , new SpaceGroup("38:-cba;c2v^14;c 2 m m;c -2 2")
    , new SpaceGroup("38:bca;c2v^14;c m 2 m;c -2 -2")
    , new SpaceGroup("38:a-cb;c2v^14;a m 2 m;a -2 -2")
    , new SpaceGroup("39;c2v^15;a b m 2;a 2 -2b")
    , new SpaceGroup("39:ba-c;c2v^15;b m a 2;b 2 -2a")
    , new SpaceGroup("39:cab;c2v^15;b 2 c m;b -2a 2")
    , new SpaceGroup("39:-cba;c2v^15;c 2 m b;c -2a 2")
    , new SpaceGroup("39:bca;c2v^15;c m 2 a;c -2a -2a")
    , new SpaceGroup("39:a-cb;c2v^15;a c 2 m;a -2b -2b")
    , new SpaceGroup("40;c2v^16;a m a 2;a 2 -2a")
    , new SpaceGroup("40:ba-c;c2v^16;b b m 2;b 2 -2b")
    , new SpaceGroup("40:cab;c2v^16;b 2 m b;b -2b 2")
    , new SpaceGroup("40:-cba;c2v^16;c 2 c m;c -2c 2")
    , new SpaceGroup("40:bca;c2v^16;c c 2 m;c -2c -2c")
    , new SpaceGroup("40:a-cb;c2v^16;a m 2 a;a -2a -2a")
    , new SpaceGroup("41;c2v^17;a b a 2;a 2 -2ab")
    , new SpaceGroup("41:ba-c;c2v^17;b b a 2;b 2 -2ab")
    , new SpaceGroup("41:cab;c2v^17;b 2 c b;b -2ab 2")
    , new SpaceGroup("41:-cba;c2v^17;c 2 c b;c -2ac 2")
    , new SpaceGroup("41:bca;c2v^17;c c 2 a;c -2ac -2ac")
    , new SpaceGroup("41:a-cb;c2v^17;a c 2 a;a -2ab -2ab")
    , new SpaceGroup("42;c2v^18;f m m 2;f 2 -2")
    , new SpaceGroup("42:cab;c2v^18;f 2 m m;f -2 2")
    , new SpaceGroup("42:bca;c2v^18;f m 2 m;f -2 -2")
    , new SpaceGroup("43;c2v^19;f d d 2;f 2 -2d")
    , new SpaceGroup("43:cab;c2v^19;f 2 d d;f -2d 2")
    , new SpaceGroup("43:bca;c2v^19;f d 2 d;f -2d -2d")
    , new SpaceGroup("44;c2v^20;i m m 2;i 2 -2")
    , new SpaceGroup("44:cab;c2v^20;i 2 m m;i -2 2")
    , new SpaceGroup("44:bca;c2v^20;i m 2 m;i -2 -2")
    , new SpaceGroup("45;c2v^21;i b a 2;i 2 -2c")
    , new SpaceGroup("45:cab;c2v^21;i 2 c b;i -2a 2")
    , new SpaceGroup("45:bca;c2v^21;i c 2 a;i -2b -2b")
    , new SpaceGroup("46;c2v^22;i m a 2;i 2 -2a")
    , new SpaceGroup("46:ba-c;c2v^22;i b m 2;i 2 -2b")
    , new SpaceGroup("46:cab;c2v^22;i 2 m b;i -2b 2")
    , new SpaceGroup("46:-cba;c2v^22;i 2 c m;i -2c 2")
    , new SpaceGroup("46:bca;c2v^22;i c 2 m;i -2c -2c")
    , new SpaceGroup("46:a-cb;c2v^22;i m 2 a;i -2a -2a")
    , new SpaceGroup("47;d2h^1;p m m m;-p 2 2")
    , new SpaceGroup("48:1;d2h^2;p n n n:1;p 2 2 -1n")
    , new SpaceGroup("48:2;d2h^2;p n n n:2;-p 2ab 2bc")
    , new SpaceGroup("49;d2h^3;p c c m;-p 2 2c")
    , new SpaceGroup("49:cab;d2h^3;p m a a;-p 2a 2")
    , new SpaceGroup("49:bca;d2h^3;p b m b;-p 2b 2b")
    , new SpaceGroup("50:1;d2h^4;p b a n:1;p 2 2 -1ab")
    , new SpaceGroup("50:2;d2h^4;p b a n:2;-p 2ab 2b")
    , new SpaceGroup("50:1cab;d2h^4;p n c b:1;p 2 2 -1bc")
    , new SpaceGroup("50:2cab;d2h^4;p n c b:2;-p 2b 2bc")
    , new SpaceGroup("50:1bca;d2h^4;p c n a:1;p 2 2 -1ac")
    , new SpaceGroup("50:2bca;d2h^4;p c n a:2;-p 2a 2c")
    , new SpaceGroup("51;d2h^5;p m m a;-p 2a 2a")
    , new SpaceGroup("51:ba-c;d2h^5;p m m b;-p 2b 2")
    , new SpaceGroup("51:cab;d2h^5;p b m m;-p 2 2b")
    , new SpaceGroup("51:-cba;d2h^5;p c m m;-p 2c 2c")
    , new SpaceGroup("51:bca;d2h^5;p m c m;-p 2c 2")
    , new SpaceGroup("51:a-cb;d2h^5;p m a m;-p 2 2a")
    , new SpaceGroup("52;d2h^6;p n n a;-p 2a 2bc")
    , new SpaceGroup("52:ba-c;d2h^6;p n n b;-p 2b 2n")
    , new SpaceGroup("52:cab;d2h^6;p b n n;-p 2n 2b")
    , new SpaceGroup("52:-cba;d2h^6;p c n n;-p 2ab 2c")
    , new SpaceGroup("52:bca;d2h^6;p n c n;-p 2ab 2n")
    , new SpaceGroup("52:a-cb;d2h^6;p n a n;-p 2n 2bc")
    , new SpaceGroup("53;d2h^7;p m n a;-p 2ac 2")
    , new SpaceGroup("53:ba-c;d2h^7;p n m b;-p 2bc 2bc")
    , new SpaceGroup("53:cab;d2h^7;p b m n;-p 2ab 2ab")
    , new SpaceGroup("53:-cba;d2h^7;p c n m;-p 2 2ac")
    , new SpaceGroup("53:bca;d2h^7;p n c m;-p 2 2bc")
    , new SpaceGroup("53:a-cb;d2h^7;p m a n;-p 2ab 2")
    , new SpaceGroup("54;d2h^8;p c c a;-p 2a 2ac")
    , new SpaceGroup("54:ba-c;d2h^8;p c c b;-p 2b 2c")
    , new SpaceGroup("54:cab;d2h^8;p b a a;-p 2a 2b")
    , new SpaceGroup("54:-cba;d2h^8;p c a a;-p 2ac 2c")
    , new SpaceGroup("54:bca;d2h^8;p b c b;-p 2bc 2b")
    , new SpaceGroup("54:a-cb;d2h^8;p b a b;-p 2b 2ab")
    , new SpaceGroup("55;d2h^9;p b a m;-p 2 2ab")
    , new SpaceGroup("55:cab;d2h^9;p m c b;-p 2bc 2")
    , new SpaceGroup("55:bca;d2h^9;p c m a;-p 2ac 2ac")
    , new SpaceGroup("56;d2h^10;p c c n;-p 2ab 2ac")
    , new SpaceGroup("56:cab;d2h^10;p n a a;-p 2ac 2bc")
    , new SpaceGroup("56:bca;d2h^10;p b n b;-p 2bc 2ab")
    , new SpaceGroup("57;d2h^11;p b c m;-p 2c 2b")
    , new SpaceGroup("57:ba-c;d2h^11;p c a m;-p 2c 2ac")
    , new SpaceGroup("57:cab;d2h^11;p m c a;-p 2ac 2a")
    , new SpaceGroup("57:-cba;d2h^11;p m a b;-p 2b 2a")
    , new SpaceGroup("57:bca;d2h^11;p b m a;-p 2a 2ab")
    , new SpaceGroup("57:a-cb;d2h^11;p c m b;-p 2bc 2c")
    , new SpaceGroup("58;d2h^12;p n n m;-p 2 2n")
    , new SpaceGroup("58:cab;d2h^12;p m n n;-p 2n 2")
    , new SpaceGroup("58:bca;d2h^12;p n m n;-p 2n 2n")
    , new SpaceGroup("59:1;d2h^13;p m m n:1;p 2 2ab -1ab")
    , new SpaceGroup("59:2;d2h^13;p m m n:2;-p 2ab 2a")
    , new SpaceGroup("59:1cab;d2h^13;p n m m:1;p 2bc 2 -1bc")
    , new SpaceGroup("59:2cab;d2h^13;p n m m:2;-p 2c 2bc")
    , new SpaceGroup("59:1bca;d2h^13;p m n m:1;p 2ac 2ac -1ac")
    , new SpaceGroup("59:2bca;d2h^13;p m n m:2;-p 2c 2a")
    , new SpaceGroup("60;d2h^14;p b c n;-p 2n 2ab")
    , new SpaceGroup("60:ba-c;d2h^14;p c a n;-p 2n 2c")
    , new SpaceGroup("60:cab;d2h^14;p n c a;-p 2a 2n")
    , new SpaceGroup("60:-cba;d2h^14;p n a b;-p 2bc 2n")
    , new SpaceGroup("60:bca;d2h^14;p b n a;-p 2ac 2b")
    , new SpaceGroup("60:a-cb;d2h^14;p c n b;-p 2b 2ac")
    , new SpaceGroup("61;d2h^15;p b c a;-p 2ac 2ab")
    , new SpaceGroup("61:ba-c;d2h^15;p c a b;-p 2bc 2ac")
    , new SpaceGroup("62;d2h^16;p n m a;-p 2ac 2n")
    , new SpaceGroup("62:ba-c;d2h^16;p m n b;-p 2bc 2a")
    , new SpaceGroup("62:cab;d2h^16;p b n m;-p 2c 2ab")
    , new SpaceGroup("62:-cba;d2h^16;p c m n;-p 2n 2ac")
    , new SpaceGroup("62:bca;d2h^16;p m c n;-p 2n 2a")
    , new SpaceGroup("62:a-cb;d2h^16;p n a m;-p 2c 2n")
    , new SpaceGroup("63;d2h^17;c m c m;-c 2c 2")
    , new SpaceGroup("63:ba-c;d2h^17;c c m m;-c 2c 2c")
    , new SpaceGroup("63:cab;d2h^17;a m m a;-a 2a 2a")
    , new SpaceGroup("63:-cba;d2h^17;a m a m;-a 2 2a")
    , new SpaceGroup("63:bca;d2h^17;b b m m;-b 2 2b")
    , new SpaceGroup("63:a-cb;d2h^17;b m m b;-b 2b 2")
    , new SpaceGroup("64;d2h^18;c m c a;-c 2ac 2")
    , new SpaceGroup("64:ba-c;d2h^18;c c m b;-c 2ac 2ac")
    , new SpaceGroup("64:cab;d2h^18;a b m a;-a 2ab 2ab")
    , new SpaceGroup("64:-cba;d2h^18;a c a m;-a 2 2ab")
    , new SpaceGroup("64:bca;d2h^18;b b c m;-b 2 2ab")
    , new SpaceGroup("64:a-cb;d2h^18;b m a b;-b 2ab 2")
    , new SpaceGroup("65;d2h^19;c m m m;-c 2 2")
    , new SpaceGroup("65:cab;d2h^19;a m m m;-a 2 2")
    , new SpaceGroup("65:bca;d2h^19;b m m m;-b 2 2")
    , new SpaceGroup("66;d2h^20;c c c m;-c 2 2c")
    , new SpaceGroup("66:cab;d2h^20;a m a a;-a 2a 2")
    , new SpaceGroup("66:bca;d2h^20;b b m b;-b 2b 2b")
    , new SpaceGroup("67;d2h^21;c m m a;-c 2a 2")
    , new SpaceGroup("67:ba-c;d2h^21;c m m b;-c 2a 2a")
    , new SpaceGroup("67:cab;d2h^21;a b m m;-a 2b 2b")
    , new SpaceGroup("67:-cba;d2h^21;a c m m;-a 2 2b")
    , new SpaceGroup("67:bca;d2h^21;b m c m;-b 2 2a")
    , new SpaceGroup("67:a-cb;d2h^21;b m a m;-b 2a 2")
    , new SpaceGroup("68:1;d2h^22;c c c a:1;c 2 2 -1ac")
    , new SpaceGroup("68:2;d2h^22;c c c a:2;-c 2a 2ac")
    , new SpaceGroup("68:1ba-c;d2h^22;c c c b:1;c 2 2 -1ac")
    , new SpaceGroup("68:2ba-c;d2h^22;c c c b:2;-c 2a 2c")
    , new SpaceGroup("68:1cab;d2h^22;a b a a:1;a 2 2 -1ab")
    , new SpaceGroup("68:2cab;d2h^22;a b a a:2;-a 2a 2b")
    , new SpaceGroup("68:1-cba;d2h^22;a c a a:1;a 2 2 -1ab")
    , new SpaceGroup("68:2-cba;d2h^22;a c a a:2;-a 2ab 2b")
    , new SpaceGroup("68:1bca;d2h^22;b b c b:1;b 2 2 -1ab")
    , new SpaceGroup("68:2bca;d2h^22;b b c b:2;-b 2ab 2b")
    , new SpaceGroup("68:1a-cb;d2h^22;b b a b:1;b 2 2 -1ab")
    , new SpaceGroup("68:2a-cb;d2h^22;b b a b:2;-b 2b 2ab")
    , new SpaceGroup("69;d2h^23;f m m m;-f 2 2")
    , new SpaceGroup("70:1;d2h^24;f d d d:1;f 2 2 -1d")
    , new SpaceGroup("70:2;d2h^24;f d d d:2;-f 2uv 2vw")
    , new SpaceGroup("71;d2h^25;i m m m;-i 2 2")
    , new SpaceGroup("72;d2h^26;i b a m;-i 2 2c")
    , new SpaceGroup("72:cab;d2h^26;i m c b;-i 2a 2")
    , new SpaceGroup("72:bca;d2h^26;i c m a;-i 2b 2b")
    , new SpaceGroup("73;d2h^27;i b c a;-i 2b 2c")
    , new SpaceGroup("73:ba-c;d2h^27;i c a b;-i 2a 2b")
    , new SpaceGroup("74;d2h^28;i m m a;-i 2b 2")
    , new SpaceGroup("74:ba-c;d2h^28;i m m b;-i 2a 2a")
    , new SpaceGroup("74:cab;d2h^28;i b m m;-i 2c 2c")
    , new SpaceGroup("74:-cba;d2h^28;i c m m;-i 2 2b")
    , new SpaceGroup("74:bca;d2h^28;i m c m;-i 2 2a")
    , new SpaceGroup("74:a-cb;d2h^28;i m a m;-i 2c 2")
    , new SpaceGroup("75;c4^1;p 4;p 4")
    , new SpaceGroup("76;c4^2;p 41;p 4w")
    , new SpaceGroup("76*;c4^2;p 41*;p 41") //nonstandard
    , new SpaceGroup("77;c4^3;p 42;p 4c")
    , new SpaceGroup("77*;c4^3;p 42*;p 42") //nonstandard
    , new SpaceGroup("78;c4^4;p 43;p 4cw")
    , new SpaceGroup("78*;c4^4;p 43*;p 43") //nonstandard
    , new SpaceGroup("79;c4^5;i 4;i 4")
    , new SpaceGroup("80;c4^6;i 41;i 4bw")
    , new SpaceGroup("81;s4^1;p -4;p -4")
    , new SpaceGroup("82;s4^2;i -4;i -4")
    , new SpaceGroup("83;c4h^1;p 4/m;-p 4")
    , new SpaceGroup("84;c4h^2;p 42/m;-p 4c")
    , new SpaceGroup("84*;c4h^2;p 42/m*;-p 42") //nonstandard
    , new SpaceGroup("85:1;c4h^3;p 4/n:1;p 4ab -1ab")
    , new SpaceGroup("85:2;c4h^3;p 4/n:2;-p 4a")
    , new SpaceGroup("86:1;c4h^4;p 42/n:1;p 4n -1n")
    , new SpaceGroup("86:2;c4h^4;p 42/n:2;-p 4bc")
    , new SpaceGroup("87;c4h^5;i 4/m;-i 4")
    , new SpaceGroup("88:1;c4h^6;i 41/a:1;i 4bw -1bw")
    , new SpaceGroup("88:2;c4h^6;i 41/a:2;-i 4ad")
    , new SpaceGroup("89;d4^1;p 4 2 2;p 4 2")
    , new SpaceGroup("90;d4^2;p 4 21 2;p 4ab 2ab")
    , new SpaceGroup("91;d4^3;p 41 2 2;p 4w 2c")
    , new SpaceGroup("91*;d4^3;p 41 2 2*;p 41 2c") //nonstandard
    , new SpaceGroup("92;d4^4;p 41 21 2;p 4abw 2nw")
    , new SpaceGroup("93;d4^5;p 42 2 2;p 4c 2")
    , new SpaceGroup("93*;d4^5;p 42 2 2*;p 42 2") //nonstandard
    , new SpaceGroup("94;d4^6;p 42 21 2;p 4n 2n")
    , new SpaceGroup("95;d4^7;p 43 2 2;p 4cw 2c")
    , new SpaceGroup("95*;d4^7;p 43 2 2*;p 43 2c") //nonstandard
    , new SpaceGroup("96;d4^8;p 43 21 2;p 4nw 2abw")
    , new SpaceGroup("97;d4^9;i 4 2 2;i 4 2")
    , new SpaceGroup("98;d4^10;i 41 2 2;i 4bw 2bw")
    , new SpaceGroup("99;c4v^1;p 4 m m;p 4 -2")
    , new SpaceGroup("100;c4v^2;p 4 b m;p 4 -2ab")
    , new SpaceGroup("101;c4v^3;p 42 c m;p 4c -2c")
    , new SpaceGroup("101*;c4v^3;p 42 c m*;p 42 -2c") //nonstandard
    , new SpaceGroup("102;c4v^4;p 42 n m;p 4n -2n")
    , new SpaceGroup("103;c4v^5;p 4 c c;p 4 -2c")
    , new SpaceGroup("104;c4v^6;p 4 n c;p 4 -2n")
    , new SpaceGroup("105;c4v^7;p 42 m c;p 4c -2")
    , new SpaceGroup("105*;c4v^7;p 42 m c*;p 42 -2") //nonstandard
    , new SpaceGroup("106;c4v^8;p 42 b c;p 4c -2ab")
    , new SpaceGroup("106*;c4v^8;p 42 b c*;p 42 -2ab") //nonstandard
    , new SpaceGroup("107;c4v^9;i 4 m m;i 4 -2")
    , new SpaceGroup("108;c4v^10;i 4 c m;i 4 -2c")
    , new SpaceGroup("109;c4v^11;i 41 m d;i 4bw -2")
    , new SpaceGroup("110;c4v^12;i 41 c d;i 4bw -2c")
    , new SpaceGroup("111;d2d^1;p -4 2 m;p -4 2")
    , new SpaceGroup("112;d2d^2;p -4 2 c;p -4 2c")
    , new SpaceGroup("113;d2d^3;p -4 21 m;p -4 2ab")
    , new SpaceGroup("114;d2d^4;p -4 21 c;p -4 2n")
    , new SpaceGroup("115;d2d^5;p -4 m 2;p -4 -2")
    , new SpaceGroup("116;d2d^6;p -4 c 2;p -4 -2c")
    , new SpaceGroup("117;d2d^7;p -4 b 2;p -4 -2ab")
    , new SpaceGroup("118;d2d^8;p -4 n 2;p -4 -2n")
    , new SpaceGroup("119;d2d^9;i -4 m 2;i -4 -2")
    , new SpaceGroup("120;d2d^10;i -4 c 2;i -4 -2c")
    , new SpaceGroup("121;d2d^11;i -4 2 m;i -4 2")
    , new SpaceGroup("122;d2d^12;i -4 2 d;i -4 2bw")
    , new SpaceGroup("123;d4h^1;p 4/m m m;-p 4 2")
    , new SpaceGroup("124;d4h^2;p 4/m c c;-p 4 2c")
    , new SpaceGroup("125:1;d4h^3;p 4/n b m:1;p 4 2 -1ab")
    , new SpaceGroup("125:2;d4h^3;p 4/n b m:2;-p 4a 2b")
    , new SpaceGroup("126:1;d4h^4;p 4/n n c:1;p 4 2 -1n")
    , new SpaceGroup("126:2;d4h^4;p 4/n n c:2;-p 4a 2bc")
    , new SpaceGroup("127;d4h^5;p 4/m b m;-p 4 2ab")
    , new SpaceGroup("128;d4h^6;p 4/m n c;-p 4 2n")
    , new SpaceGroup("129:1;d4h^7;p 4/n m m:1;p 4ab 2ab -1ab")
    , new SpaceGroup("129:2;d4h^7;p 4/n m m:2;-p 4a 2a")
    , new SpaceGroup("130:1;d4h^8;p 4/n c c:1;p 4ab 2n -1ab")
    , new SpaceGroup("130:2;d4h^8;p 4/n c c:2;-p 4a 2ac")
    , new SpaceGroup("131;d4h^9;p 42/m m c;-p 4c 2")
    , new SpaceGroup("132;d4h^10;p 42/m c m;-p 4c 2c")
    , new SpaceGroup("133:1;d4h^11;p 42/n b c:1;p 4n 2c -1n")
    , new SpaceGroup("133:2;d4h^11;p 42/n b c:2;-p 4ac 2b")
    , new SpaceGroup("134:1;d4h^12;p 42/n n m:1;p 4n 2 -1n")
    , new SpaceGroup("134:2;d4h^12;p 42/n n m:2;-p 4ac 2bc")
    , new SpaceGroup("135;d4h^13;p 42/m b c;-p 4c 2ab")
    , new SpaceGroup("135*;d4h^13;p 42/m b c*;-p 42 2ab") //nonstandard
    , new SpaceGroup("136;d4h^14;p 42/m n m;-p 4n 2n")
    , new SpaceGroup("137:1;d4h^15;p 42/n m c:1;p 4n 2n -1n")
    , new SpaceGroup("137:2;d4h^15;p 42/n m c:2;-p 4ac 2a")
    , new SpaceGroup("138:1;d4h^16;p 42/n c m:1;p 4n 2ab -1n")
    , new SpaceGroup("138:2;d4h^16;p 42/n c m:2;-p 4ac 2ac")
    , new SpaceGroup("139;d4h^17;i 4/m m m;-i 4 2")
    , new SpaceGroup("140;d4h^18;i 4/m c m;-i 4 2c")
    , new SpaceGroup("141:1;d4h^19;i 41/a m d:1;i 4bw 2bw -1bw")
    , new SpaceGroup("141:2;d4h^19;i 41/a m d:2;-i 4bd 2")
    , new SpaceGroup("142:1;d4h^20;i 41/a c d:1;i 4bw 2aw -1bw")
    , new SpaceGroup("142:2;d4h^20;i 41/a c d:2;-i 4bd 2c")
    , new SpaceGroup("143;c3^1;p 3;p 3")
    , new SpaceGroup("144;c3^2;p 31;p 31")
    , new SpaceGroup("145;c3^3;p 32;p 32")
    , new SpaceGroup("146:h;c3^4;r 3:h;r 3")
    , new SpaceGroup("146:r;c3^4;r 3:r;p 3*")
    , new SpaceGroup("147;c3i^1;p -3;-p 3")
    , new SpaceGroup("148:h;c3i^2;r -3:h;-r 3")
    , new SpaceGroup("148:r;c3i^2;r -3:r;-p 3*")
    , new SpaceGroup("149;d3^1;p 3 1 2;p 3 2")
    , new SpaceGroup("150;d3^2;p 3 2 1;p 3 2\"")
    , new SpaceGroup("151;d3^3;p 31 1 2;p 31 2 (0 0 4)")
    , new SpaceGroup("152;d3^4;p 31 2 1;p 31 2\"")
    , new SpaceGroup("153;d3^5;p 32 1 2;p 32 2 (0 0 2)")
    , new SpaceGroup("154;d3^6;p 32 2 1;p 32 2\"") // TODO MSA quartz.cif gives different operators for this -- 
    , new SpaceGroup("155:h;d3^7;r 3 2:h;r 3 2\"")
    , new SpaceGroup("155:r;d3^7;r 3 2:r;p 3* 2")
    , new SpaceGroup("156;c3v^1;p 3 m 1;p 3 -2\"")
    , new SpaceGroup("157;c3v^2;p 3 1 m;p 3 -2")
    , new SpaceGroup("158;c3v^3;p 3 c 1;p 3 -2\"c")
    , new SpaceGroup("159;c3v^4;p 3 1 c;p 3 -2c")
    , new SpaceGroup("160:h;c3v^5;r 3 m:h;r 3 -2\"")
    , new SpaceGroup("160:r;c3v^5;r 3 m:r;p 3* -2")
    , new SpaceGroup("161:h;c3v^6;r 3 c:h;r 3 -2\"c")
    , new SpaceGroup("161:r;c3v^6;r 3 c:r;p 3* -2n")
    , new SpaceGroup("162;d3d^1;p -3 1 m;-p 3 2")
    , new SpaceGroup("163;d3d^2;p -3 1 c;-p 3 2c")
    , new SpaceGroup("164;d3d^3;p -3 m 1;-p 3 2\"")
    , new SpaceGroup("165;d3d^4;p -3 c 1;-p 3 2\"c")
    , new SpaceGroup("166:h;d3d^5;r -3 m:h;-r 3 2\"")
    , new SpaceGroup("166:r;d3d^5;r -3 m:r;-p 3* 2")
    , new SpaceGroup("167:h;d3d^6;r -3 c:h;-r 3 2\"c")
    , new SpaceGroup("167:r;d3d^6;r -3 c:r;-p 3* 2n")
    , new SpaceGroup("168;c6^1;p 6;p 6")
    , new SpaceGroup("169;c6^2;p 61;p 61")
    , new SpaceGroup("170;c6^3;p 65;p 65")
    , new SpaceGroup("171;c6^4;p 62;p 62")
    , new SpaceGroup("172;c6^5;p 64;p 64")
    , new SpaceGroup("173;c6^6;p 63;p 6c")
    , new SpaceGroup("173*;c6^6;p 63*;p 63 ") //nonstandard; space added so not identical to H-M P 63
    , new SpaceGroup("174;c3h^1;p -6;p -6")
    , new SpaceGroup("175;c6h^1;p 6/m;-p 6")
    , new SpaceGroup("176;c6h^2;p 63/m;-p 6c")
    , new SpaceGroup("176*;c6h^2;p 63/m*;-p 63") //nonstandard
    , new SpaceGroup("177;d6^1;p 6 2 2;p 6 2")
    , new SpaceGroup("178;d6^2;p 61 2 2;p 61 2 (0 0 5)")
    , new SpaceGroup("179;d6^3;p 65 2 2;p 65 2 (0 0 1)")
    , new SpaceGroup("180;d6^4;p 62 2 2;p 62 2 (0 0 4)")
    , new SpaceGroup("181;d6^5;p 64 2 2;p 64 2 (0 0 2)")
    , new SpaceGroup("182;d6^6;p 63 2 2;p 6c 2c")
    , new SpaceGroup("182*;d6^6;p 63 2 2*;p 63 2c") //nonstandard
    , new SpaceGroup("183;c6v^1;p 6 m m;p 6 -2")
    , new SpaceGroup("184;c6v^2;p 6 c c;p 6 -2c")
    , new SpaceGroup("185;c6v^3;p 63 c m;p 6c -2")
    , new SpaceGroup("185*;c6v^3;p 63 c m*;p 63 -2") //nonstandard
    , new SpaceGroup("186;c6v^4;p 63 m c;p 6c -2c")
    , new SpaceGroup("186*;c6v^4;p 63 m c*;p 63 -2c") //nonstandard
    , new SpaceGroup("187;d3h^1;p -6 m 2;p -6 2")
    , new SpaceGroup("188;d3h^2;p -6 c 2;p -6c 2")
    , new SpaceGroup("189;d3h^3;p -6 2 m;p -6 -2")
    , new SpaceGroup("190;d3h^4;p -6 2 c;p -6c -2c")
    , new SpaceGroup("191;d6h^1;p 6/m m m;-p 6 2")
    , new SpaceGroup("192;d6h^2;p 6/m c c;-p 6 2c")
    , new SpaceGroup("193;d6h^3;p 63/m c m;-p 6c 2")
    , new SpaceGroup("193*;d6h^3;p 63/m c m*;-p 63 2") //nonstandard
    , new SpaceGroup("194;d6h^4;p 63/m m c;-p 6c 2c")
    , new SpaceGroup("194*;d6h^4;p 63/m m c*;-p 63 2c") //nonstandard
    , new SpaceGroup("195;t^1;p 2 3;p 2 2 3")
    , new SpaceGroup("196;t^2;f 2 3;f 2 2 3")
    , new SpaceGroup("197;t^3;i 2 3;i 2 2 3")
    , new SpaceGroup("198;t^4;p 21 3;p 2ac 2ab 3")
    , new SpaceGroup("199;t^5;i 21 3;i 2b 2c 3")
    , new SpaceGroup("200;th^1;p m -3;-p 2 2 3")
    , new SpaceGroup("201:1;th^2;p n -3:1;p 2 2 3 -1n")
    , new SpaceGroup("201:2;th^2;p n -3:2;-p 2ab 2bc 3")
    , new SpaceGroup("202;th^3;f m -3;-f 2 2 3")
    , new SpaceGroup("203:1;th^4;f d -3:1;f 2 2 3 -1d")
    , new SpaceGroup("203:2;th^4;f d -3:2;-f 2uv 2vw 3")
    , new SpaceGroup("204;th^5;i m -3;-i 2 2 3")
    , new SpaceGroup("205;th^6;p a -3;-p 2ac 2ab 3")
    , new SpaceGroup("206;th^7;i a -3;-i 2b 2c 3")
    , new SpaceGroup("207;o^1;p 4 3 2;p 4 2 3")
    , new SpaceGroup("208;o^2;p 42 3 2;p 4n 2 3")
    , new SpaceGroup("209;o^3;f 4 3 2;f 4 2 3")
    , new SpaceGroup("210;o^4;f 41 3 2;f 4d 2 3")
    , new SpaceGroup("211;o^5;i 4 3 2;i 4 2 3")
    , new SpaceGroup("212;o^6;p 43 3 2;p 4acd 2ab 3")
    , new SpaceGroup("213;o^7;p 41 3 2;p 4bd 2ab 3")
    , new SpaceGroup("214;o^8;i 41 3 2;i 4bd 2c 3")
    , new SpaceGroup("215;td^1;p -4 3 m;p -4 2 3")
    , new SpaceGroup("216;td^2;f -4 3 m;f -4 2 3")
    , new SpaceGroup("217;td^3;i -4 3 m;i -4 2 3")
    , new SpaceGroup("218;td^4;p -4 3 n;p -4n 2 3")
    , new SpaceGroup("219;td^5;f -4 3 c;f -4a 2 3")
    , new SpaceGroup("220;td^6;i -4 3 d;i -4bd 2c 3")
    , new SpaceGroup("221;oh^1;p m -3 m;-p 4 2 3")
    , new SpaceGroup("222:1;oh^2;p n -3 n:1;p 4 2 3 -1n")
    , new SpaceGroup("222:2;oh^2;p n -3 n:2;-p 4a 2bc 3")
    , new SpaceGroup("223;oh^3;p m -3 n;-p 4n 2 3")
    , new SpaceGroup("224:1;oh^4;p n -3 m:1;p 4n 2 3 -1n")
    , new SpaceGroup("224:2;oh^4;p n -3 m:2;-p 4bc 2bc 3")
    , new SpaceGroup("225;oh^5;f m -3 m;-f 4 2 3")
    , new SpaceGroup("226;oh^6;f m -3 c;-f 4a 2 3")
    , new SpaceGroup("227:1;oh^7;f d -3 m:1;f 4d 2 3 -1d")
    , new SpaceGroup("227:2;oh^7;f d -3 m:2;-f 4vw 2vw 3")
    , new SpaceGroup("228:1;oh^8;f d -3 c:1;f 4d 2 3 -1ad")
    , new SpaceGroup("228:2;oh^8;f d -3 c:2;-f 4ud 2vw 3")
    , new SpaceGroup("229;oh^9;i m -3 m;-i 4 2 3")
    , new SpaceGroup("230;oh^10;i a -3 d;-i 4bd 2c 3")

  };
  
  /*  see http://cci.lbl.gov/sginfo/itvb_2001_table_a1427_hall_symbols.html

intl#     H-M full       HM-abbr   HM-short  Hall
1         P 1            P1        P         P 1       
2         P -1           P-1       P-1       -P 1      
3:b       P 1 2 1        P121      P2        P 2y      
3:b       P 2            P2        P2        P 2y      
3:c       P 1 1 2        P112      P2        P 2       
3:a       P 2 1 1        P211      P2        P 2x      
4:b       P 1 21 1       P1211     P21       P 2yb     
4:b       P 21           P21       P21       P 2yb     
4:b*      P 1 21 1*      P1211*    P21*      P 2y1     
4:c       P 1 1 21       P1121     P21       P 2c      
4:c*      P 1 1 21*      P1121*    P21*      P 21      
4:a       P 21 1 1       P2111     P21       P 2xa     
4:a*      P 21 1 1*      P2111*    P21*      P 2x1     
5:b1      C 1 2 1        C121      C2        C 2y      
5:b1      C 2            C2        C2        C 2y      
5:b2      A 1 2 1        A121      A2        A 2y      
5:b3      I 1 2 1        I121      I2        I 2y      
5:c1      A 1 1 2        A112      A2        A 2       
5:c2      B 1 1 2        B112      B2        B 2       
5:c3      I 1 1 2        I112      I2        I 2       
5:a1      B 2 1 1        B211      B2        B 2x      
5:a2      C 2 1 1        C211      C2        C 2x      
5:a3      I 2 1 1        I211      I2        I 2x      
6:b       P 1 m 1        P1m1      Pm        P -2y     
6:b       P m            Pm        Pm        P -2y     
6:c       P 1 1 m        P11m      Pm        P -2      
6:a       P m 1 1        Pm11      Pm        P -2x     
7:b1      P 1 c 1        P1c1      Pc        P -2yc    
7:b1      P c            Pc        Pc        P -2yc    
7:b2      P 1 n 1        P1n1      Pn        P -2yac   
7:b2      P n            Pn        Pn        P -2yac   
7:b3      P 1 a 1        P1a1      Pa        P -2ya    
7:b3      P a            Pa        Pa        P -2ya    
7:c1      P 1 1 a        P11a      Pa        P -2a     
7:c2      P 1 1 n        P11n      Pn        P -2ab    
7:c3      P 1 1 b        P11b      Pb        P -2b     
7:a1      P b 1 1        Pb11      Pb        P -2xb    
7:a2      P n 1 1        Pn11      Pn        P -2xbc   
7:a3      P c 1 1        Pc11      Pc        P -2xc    
8:b1      C 1 m 1        C1m1      Cm        C -2y     
8:b1      C m            Cm        Cm        C -2y     
8:b2      A 1 m 1        A1m1      Am        A -2y     
8:b3      I 1 m 1        I1m1      Im        I -2y     
8:b3      I m            Im        Im        I -2y     
8:c1      A 1 1 m        A11m      Am        A -2      
8:c2      B 1 1 m        B11m      Bm        B -2      
8:c3      I 1 1 m        I11m      Im        I -2      
8:a1      B m 1 1        Bm11      Bm        B -2x     
8:a2      C m 1 1        Cm11      Cm        C -2x     
8:a3      I m 1 1        Im11      Im        I -2x     
9:b1      C 1 c 1        C1c1      Cc        C -2yc    
9:b1      C c            Cc        Cc        C -2yc    
9:b2      A 1 n 1        A1n1      An        A -2yab   
9:b3      I 1 a 1        I1a1      Ia        I -2ya    
9:-b1     A 1 a 1        A1a1      Aa        A -2ya    
9:-b2     C 1 n 1        C1n1      Cn        C -2yac   
9:-b3     I 1 c 1        I1c1      Ic        I -2yc    
9:c1      A 1 1 a        A11a      Aa        A -2a     
9:c2      B 1 1 n        B11n      Bn        B -2ab    
9:c3      I 1 1 b        I11b      Ib        I -2b     
9:-c1     B 1 1 b        B11b      Bb        B -2b     
9:-c2     A 1 1 n        A11n      An        A -2ab    
9:-c3     I 1 1 a        I11a      Ia        I -2a     
9:a1      B b 1 1        Bb11      Bb        B -2xb    
9:a2      C n 1 1        Cn11      Cn        C -2xac   
9:a3      I c 1 1        Ic11      Ic        I -2xc    
9:-a1     C c 1 1        Cc11      Cc        C -2xc    
9:-a2     B n 1 1        Bn11      Bn        B -2xab   
9:-a3     I b 1 1        Ib11      Ib        I -2xb    
10:b      P 1 2/m 1      P12/m1    P2/m      -P 2y     
10:b      P 2/m          P2/m      P2/m      -P 2y     
10:c      P 1 1 2/m      P112/m    P2/m      -P 2      
10:a      P 2/m 1 1      P2/m11    P2/m      -P 2x     
11:b      P 1 21/m 1     P121/m1   P21/m     -P 2yb    
11:b      P 21/m         P21/m     P21/m     -P 2yb    
11:b*     P 1 21/m 1*    P121/m1*  P21/m*    -P 2y1    
11:c      P 1 1 21/m     P1121/m   P21/m     -P 2c     
11:c*     P 1 1 21/m*    P1121/m*  P21/m*    -P 21     
11:a      P 21/m 1 1     P21/m11   P21/m     -P 2xa    
11:a*     P 21/m 1 1*    P21/m11*  P21/m*    -P 2x1    
12:b1     C 1 2/m 1      C12/m1    C2/m      -C 2y     
12:b1     C 2/m          C2/m      C2/m      -C 2y     
12:b2     A 1 2/m 1      A12/m1    A2/m      -A 2y     
12:b3     I 1 2/m 1      I12/m1    I2/m      -I 2y     
12:b3     I 2/m          I2/m      I2/m      -I 2y     
12:c1     A 1 1 2/m      A112/m    A2/m      -A 2      
12:c2     B 1 1 2/m      B112/m    B2/m      -B 2      
12:c3     I 1 1 2/m      I112/m    I2/m      -I 2      
12:a1     B 2/m 1 1      B2/m11    B2/m      -B 2x     
12:a2     C 2/m 1 1      C2/m11    C2/m      -C 2x     
12:a3     I 2/m 1 1      I2/m11    I2/m      -I 2x     
13:b1     P 1 2/c 1      P12/c1    P2/c      -P 2yc    
13:b1     P 2/c          P2/c      P2/c      -P 2yc    
13:b2     P 1 2/n 1      P12/n1    P2/n      -P 2yac   
13:b2     P 2/n          P2/n      P2/n      -P 2yac   
13:b3     P 1 2/a 1      P12/a1    P2/a      -P 2ya    
13:b3     P 2/a          P2/a      P2/a      -P 2ya    
13:c1     P 1 1 2/a      P112/a    P2/a      -P 2a     
13:c2     P 1 1 2/n      P112/n    P2/n      -P 2ab    
13:c3     P 1 1 2/b      P112/b    P2/b      -P 2b     
13:a1     P 2/b 1 1      P2/b11    P2/b      -P 2xb    
13:a2     P 2/n 1 1      P2/n11    P2/n      -P 2xbc   
13:a3     P 2/c 1 1      P2/c11    P2/c      -P 2xc    
14:b1     P 1 21/c 1     P121/c1   P21/c     -P 2ybc   
14:b1     P 21/c         P21/c     P21/c     -P 2ybc   
14:b2     P 1 21/n 1     P121/n1   P21/n     -P 2yn    
14:b2     P 21/n         P21/n     P21/n     -P 2yn    
14:b3     P 1 21/a 1     P121/a1   P21/a     -P 2yab   
14:b3     P 21/a         P21/a     P21/a     -P 2yab   
14:c1     P 1 1 21/a     P1121/a   P21/a     -P 2ac    
14:c2     P 1 1 21/n     P1121/n   P21/n     -P 2n     
14:c3     P 1 1 21/b     P1121/b   P21/b     -P 2bc    
14:a1     P 21/b 1 1     P21/b11   P21/b     -P 2xab   
14:a2     P 21/n 1 1     P21/n11   P21/n     -P 2xn    
14:a3     P 21/c 1 1     P21/c11   P21/c     -P 2xac   
15:b1     C 1 2/c 1      C12/c1    C2/c      -C 2yc    
15:b1     C 2/c          C2/c      C2/c      -C 2yc    
15:b2     A 1 2/n 1      A12/n1    A2/n      -A 2yab   
15:b3     I 1 2/a 1      I12/a1    I2/a      -I 2ya    
15:b3     I 2/a          I2/a      I2/a      -I 2ya    
15:-b1    A 1 2/a 1      A12/a1    A2/a      -A 2ya    
15:-b2    C 1 2/n 1      C12/n1    C2/n      -C 2yac   
15:-b2    C 2/n          C2/n      C2/n      -C 2yac   
15:-b3    I 1 2/c 1      I12/c1    I2/c      -I 2yc    
15:-b3    I 2/c          I2/c      I2/c      -I 2yc    
15:c1     A 1 1 2/a      A112/a    A2/a      -A 2a     
15:c2     B 1 1 2/n      B112/n    B2/n      -B 2ab    
15:c3     I 1 1 2/b      I112/b    I2/b      -I 2b     
15:-c1    B 1 1 2/b      B112/b    B2/b      -B 2b     
15:-c2    A 1 1 2/n      A112/n    A2/n      -A 2ab    
15:-c3    I 1 1 2/a      I112/a    I2/a      -I 2a     
15:a1     B 2/b 1 1      B2/b11    B2/b      -B 2xb    
15:a2     C 2/n 1 1      C2/n11    C2/n      -C 2xac   
15:a3     I 2/c 1 1      I2/c11    I2/c      -I 2xc    
15:-a1    C 2/c 1 1      C2/c11    C2/c      -C 2xc    
15:-a2    B 2/n 1 1      B2/n11    B2/n      -B 2xab   
15:-a3    I 2/b 1 1      I2/b11    I2/b      -I 2xb    
16        P 2 2 2        P222      P222      P 2 2     
17        P 2 2 21       P2221     P2221     P 2c 2    
17*       P 2 2 21*      P2221*    P2221*    P 21 2    
17:cab    P 21 2 2       P2122     P2122     P 2a 2a   
17:bca    P 2 21 2       P2212     P2212     P 2 2b    
18        P 21 21 2      P21212    P21212    P 2 2ab   
18:cab    P 2 21 21      P22121    P22121    P 2bc 2   
18:bca    P 21 2 21      P21221    P21221    P 2ac 2ac 
19        P 21 21 21     P212121   P212121   P 2ac 2ab 
20        C 2 2 21       C2221     C2221     C 2c 2    
20*       C 2 2 21*      C2221*    C2221*    C 21 2    
20:cab    A 21 2 2       A2122     A2122     A 2a 2a   
20:cab*   A 21 2 2*      A2122*    A2122*    A 2a 21   
20:bca    B 2 21 2       B2212     B2212     B 2 2b    
21        C 2 2 2        C222      C222      C 2 2     
21:cab    A 2 2 2        A222      A222      A 2 2     
21:bca    B 2 2 2        B222      B222      B 2 2     
22        F 2 2 2        F222      F222      F 2 2     
23        I 2 2 2        I222      I222      I 2 2     
24        I 21 21 21     I212121   I212121   I 2b 2c   
25        P m m 2        Pmm2      Pmm2      P 2 -2    
25:cab    P 2 m m        P2mm      P2mm      P -2 2    
25:bca    P m 2 m        Pm2m      Pm2m      P -2 -2   
26        P m c 21       Pmc21     Pmc21     P 2c -2   
26*       P m c 21*      Pmc21*    Pmc21*    P 21 -2   
26:ba-c   P c m 21       Pcm21     Pcm21     P 2c -2c  
26:ba-c*  P c m 21*      Pcm21*    Pcm21*    P 21 -2c  
26:cab    P 21 m a       P21ma     P21ma     P -2a 2a  
26:-cba   P 21 a m       P21am     P21am     P -2 2a   
26:bca    P b 21 m       Pb21m     Pb21m     P -2 -2b  
26:a-cb   P m 21 b       Pm21b     Pm21b     P -2b -2  
27        P c c 2        Pcc2      Pcc2      P 2 -2c   
27:cab    P 2 a a        P2aa      P2aa      P -2a 2   
27:bca    P b 2 b        Pb2b      Pb2b      P -2b -2b 
28        P m a 2        Pma2      Pma2      P 2 -2a   
28*       P m a 2*       Pma2*     Pma2*     P 2 -21   
28:ba-c   P b m 2        Pbm2      Pbm2      P 2 -2b   
28:cab    P 2 m b        P2mb      P2mb      P -2b 2   
28:-cba   P 2 c m        P2cm      P2cm      P -2c 2   
28:-cba*  P 2 c m*       P2cm*     P2cm*     P -21 2   
28:bca    P c 2 m        Pc2m      Pc2m      P -2c -2c 
28:a-cb   P m 2 a        Pm2a      Pm2a      P -2a -2a 
29        P c a 21       Pca21     Pca21     P 2c -2ac 
29:ba-c   P b c 21       Pbc21     Pbc21     P 2c -2b  
29:cab    P 21 a b       P21ab     P21ab     P -2b 2a  
29:-cba   P 21 c a       P21ca     P21ca     P -2ac 2a 
29:bca    P c 21 b       Pc21b     Pc21b     P -2bc -2c
29:a-cb   P b 21 a       Pb21a     Pb21a     P -2a -2ab
30        P n c 2        Pnc2      Pnc2      P 2 -2bc  
30:ba-c   P c n 2        Pcn2      Pcn2      P 2 -2ac  
30:cab    P 2 n a        P2na      P2na      P -2ac 2  
30:-cba   P 2 a n        P2an      P2an      P -2ab 2  
30:bca    P b 2 n        Pb2n      Pb2n      P -2ab -2a
30:a-cb   P n 2 b        Pn2b      Pn2b      P -2bc -2b
31        P m n 21       Pmn21     Pmn21     P 2ac -2  
31:ba-c   P n m 21       Pnm21     Pnm21     P 2bc -2bc
31:cab    P 21 m n       P21mn     P21mn     P -2ab 2ab
31:-cba   P 21 n m       P21nm     P21nm     P -2 2ac  
31:bca    P n 21 m       Pn21m     Pn21m     P -2 -2bc 
31:a-cb   P m 21 n       Pm21n     Pm21n     P -2ab -2 
32        P b a 2        Pba2      Pba2      P 2 -2ab  
32:cab    P 2 c b        P2cb      P2cb      P -2bc 2  
32:bca    P c 2 a        Pc2a      Pc2a      P -2ac -2a
33        P n a 21       Pna21     Pna21     P 2c -2n  
33*       P n a 21*      Pna21*    Pna21*    P 21 -2n  
33:ba-c   P b n 21       Pbn21     Pbn21     P 2c -2ab 
33:ba-c*  P b n 21*      Pbn21*    Pbn21*    P 21 -2ab 
33:cab    P 21 n b       P21nb     P21nb     P -2bc 2a 
33:cab*   P 21 n b*      P21nb*    P21nb*    P -2bc 21 
33:-cba   P 21 c n       P21cn     P21cn     P -2n 2a  
33:-cba*  P 21 c n*      P21cn*    P21cn*    P -2n 21  
33:bca    P c 21 n       Pc21n     Pc21n     P -2n -2ac
33:a-cb   P n 21 a       Pn21a     Pn21a     P -2ac -2n
34        P n n 2        Pnn2      Pnn2      P 2 -2n   
34:cab    P 2 n n        P2nn      P2nn      P -2n 2   
34:bca    P n 2 n        Pn2n      Pn2n      P -2n -2n 
35        C m m 2        Cmm2      Cmm2      C 2 -2    
35:cab    A 2 m m        A2mm      A2mm      A -2 2    
35:bca    B m 2 m        Bm2m      Bm2m      B -2 -2   
36        C m c 21       Cmc21     Cmc21     C 2c -2   
36*       C m c 21*      Cmc21*    Cmc21*    C 21 -2   
36:ba-c   C c m 21       Ccm21     Ccm21     C 2c -2c  
36:ba-c*  C c m 21*      Ccm21*    Ccm21*    C 21 -2c  
36:cab    A 21 m a       A21ma     A21ma     A -2a 2a  
36:cab*   A 21 m a*      A21ma*    A21ma*    A -2a 21  
36:-cba   A 21 a m       A21am     A21am     A -2 2a   
36:-cba*  A 21 a m*      A21am*    A21am*    A -2 21   
36:bca    B b 21 m       Bb21m     Bb21m     B -2 -2b  
36:a-cb   B m 21 b       Bm21b     Bm21b     B -2b -2  
37        C c c 2        Ccc2      Ccc2      C 2 -2c   
37:cab    A 2 a a        A2aa      A2aa      A -2a 2   
37:bca    B b 2 b        Bb2b      Bb2b      B -2b -2b 
38        A m m 2        Amm2      Amm2      A 2 -2    
38:ba-c   B m m 2        Bmm2      Bmm2      B 2 -2    
38:cab    B 2 m m        B2mm      B2mm      B -2 2    
38:-cba   C 2 m m        C2mm      C2mm      C -2 2    
38:bca    C m 2 m        Cm2m      Cm2m      C -2 -2   
38:a-cb   A m 2 m        Am2m      Am2m      A -2 -2   
39        A b m 2        Abm2      Abm2      A 2 -2b   
39:ba-c   B m a 2        Bma2      Bma2      B 2 -2a   
39:cab    B 2 c m        B2cm      B2cm      B -2a 2   
39:-cba   C 2 m b        C2mb      C2mb      C -2a 2   
39:bca    C m 2 a        Cm2a      Cm2a      C -2a -2a 
39:a-cb   A c 2 m        Ac2m      Ac2m      A -2b -2b 
40        A m a 2        Ama2      Ama2      A 2 -2a   
40:ba-c   B b m 2        Bbm2      Bbm2      B 2 -2b   
40:cab    B 2 m b        B2mb      B2mb      B -2b 2   
40:-cba   C 2 c m        C2cm      C2cm      C -2c 2   
40:bca    C c 2 m        Cc2m      Cc2m      C -2c -2c 
40:a-cb   A m 2 a        Am2a      Am2a      A -2a -2a 
41        A b a 2        Aba2      Aba2      A 2 -2ab  
41:ba-c   B b a 2        Bba2      Bba2      B 2 -2ab  
41:cab    B 2 c b        B2cb      B2cb      B -2ab 2  
41:-cba   C 2 c b        C2cb      C2cb      C -2ac 2  
41:bca    C c 2 a        Cc2a      Cc2a      C -2ac -2a
41:a-cb   A c 2 a        Ac2a      Ac2a      A -2ab -2a
42        F m m 2        Fmm2      Fmm2      F 2 -2    
42:cab    F 2 m m        F2mm      F2mm      F -2 2    
42:bca    F m 2 m        Fm2m      Fm2m      F -2 -2   
43        F d d 2        Fdd2      Fdd2      F 2 -2d   
43:cab    F 2 d d        F2dd      F2dd      F -2d 2   
43:bca    F d 2 d        Fd2d      Fd2d      F -2d -2d 
44        I m m 2        Imm2      Imm2      I 2 -2    
44:cab    I 2 m m        I2mm      I2mm      I -2 2    
44:bca    I m 2 m        Im2m      Im2m      I -2 -2   
45        I b a 2        Iba2      Iba2      I 2 -2c   
45:cab    I 2 c b        I2cb      I2cb      I -2a 2   
45:bca    I c 2 a        Ic2a      Ic2a      I -2b -2b 
46        I m a 2        Ima2      Ima2      I 2 -2a   
46:ba-c   I b m 2        Ibm2      Ibm2      I 2 -2b   
46:cab    I 2 m b        I2mb      I2mb      I -2b 2   
46:-cba   I 2 c m        I2cm      I2cm      I -2c 2   
46:bca    I c 2 m        Ic2m      Ic2m      I -2c -2c 
46:a-cb   I m 2 a        Im2a      Im2a      I -2a -2a 
47        P m m m        Pmmm      Pmmm      -P 2 2    
48:1      P n n n        Pnnn      Pnnn      P 2 2 -1n 
48:2      P n n n        Pnnn      Pnnn      -P 2ab 2bc
49        P c c m        Pccm      Pccm      -P 2 2c   
49:cab    P m a a        Pmaa      Pmaa      -P 2a 2   
49:bca    P b m b        Pbmb      Pbmb      -P 2b 2b  
50:1      P b a n        Pban      Pban      P 2 2 -1ab
50:2      P b a n        Pban      Pban      -P 2ab 2b 
50:1cab   P n c b        Pncb      Pncb      P 2 2 -1bc
50:2cab   P n c b        Pncb      Pncb      -P 2b 2bc 
50:1bca   P c n a        Pcna      Pcna      P 2 2 -1ac
50:2bca   P c n a        Pcna      Pcna      -P 2a 2c  
51        P m m a        Pmma      Pmma      -P 2a 2a  
51:ba-c   P m m b        Pmmb      Pmmb      -P 2b 2   
51:cab    P b m m        Pbmm      Pbmm      -P 2 2b   
51:-cba   P c m m        Pcmm      Pcmm      -P 2c 2c  
51:bca    P m c m        Pmcm      Pmcm      -P 2c 2   
51:a-cb   P m a m        Pmam      Pmam      -P 2 2a   
52        P n n a        Pnna      Pnna      -P 2a 2bc 
52:ba-c   P n n b        Pnnb      Pnnb      -P 2b 2n  
52:cab    P b n n        Pbnn      Pbnn      -P 2n 2b  
52:-cba   P c n n        Pcnn      Pcnn      -P 2ab 2c 
52:bca    P n c n        Pncn      Pncn      -P 2ab 2n 
52:a-cb   P n a n        Pnan      Pnan      -P 2n 2bc 
53        P m n a        Pmna      Pmna      -P 2ac 2  
53:ba-c   P n m b        Pnmb      Pnmb      -P 2bc 2bc
53:cab    P b m n        Pbmn      Pbmn      -P 2ab 2ab
53:-cba   P c n m        Pcnm      Pcnm      -P 2 2ac  
53:bca    P n c m        Pncm      Pncm      -P 2 2bc  
53:a-cb   P m a n        Pman      Pman      -P 2ab 2  
54        P c c a        Pcca      Pcca      -P 2a 2ac 
54:ba-c   P c c b        Pccb      Pccb      -P 2b 2c  
54:cab    P b a a        Pbaa      Pbaa      -P 2a 2b  
54:-cba   P c a a        Pcaa      Pcaa      -P 2ac 2c 
54:bca    P b c b        Pbcb      Pbcb      -P 2bc 2b 
54:a-cb   P b a b        Pbab      Pbab      -P 2b 2ab 
55        P b a m        Pbam      Pbam      -P 2 2ab  
55:cab    P m c b        Pmcb      Pmcb      -P 2bc 2  
55:bca    P c m a        Pcma      Pcma      -P 2ac 2ac
56        P c c n        Pccn      Pccn      -P 2ab 2ac
56:cab    P n a a        Pnaa      Pnaa      -P 2ac 2bc
56:bca    P b n b        Pbnb      Pbnb      -P 2bc 2ab
57        P b c m        Pbcm      Pbcm      -P 2c 2b  
57:ba-c   P c a m        Pcam      Pcam      -P 2c 2ac 
57:cab    P m c a        Pmca      Pmca      -P 2ac 2a 
57:-cba   P m a b        Pmab      Pmab      -P 2b 2a  
57:bca    P b m a        Pbma      Pbma      -P 2a 2ab 
57:a-cb   P c m b        Pcmb      Pcmb      -P 2bc 2c 
58        P n n m        Pnnm      Pnnm      -P 2 2n   
58:cab    P m n n        Pmnn      Pmnn      -P 2n 2   
58:bca    P n m n        Pnmn      Pnmn      -P 2n 2n  
59:1      P m m n        Pmmn      Pmmn      P 2 2ab -1
59:2      P m m n        Pmmn      Pmmn      -P 2ab 2a 
59:1cab   P n m m        Pnmm      Pnmm      P 2bc 2 -1
59:2cab   P n m m        Pnmm      Pnmm      -P 2c 2bc 
59:1bca   P m n m        Pmnm      Pmnm      P 2ac 2ac 
59:2bca   P m n m        Pmnm      Pmnm      -P 2c 2a  
60        P b c n        Pbcn      Pbcn      -P 2n 2ab 
60:ba-c   P c a n        Pcan      Pcan      -P 2n 2c  
60:cab    P n c a        Pnca      Pnca      -P 2a 2n  
60:-cba   P n a b        Pnab      Pnab      -P 2bc 2n 
60:bca    P b n a        Pbna      Pbna      -P 2ac 2b 
60:a-cb   P c n b        Pcnb      Pcnb      -P 2b 2ac 
61        P b c a        Pbca      Pbca      -P 2ac 2ab
61:ba-c   P c a b        Pcab      Pcab      -P 2bc 2ac
62        P n m a        Pnma      Pnma      -P 2ac 2n 
62:ba-c   P m n b        Pmnb      Pmnb      -P 2bc 2a 
62:cab    P b n m        Pbnm      Pbnm      -P 2c 2ab 
62:-cba   P c m n        Pcmn      Pcmn      -P 2n 2ac 
62:bca    P m c n        Pmcn      Pmcn      -P 2n 2a  
62:a-cb   P n a m        Pnam      Pnam      -P 2c 2n  
63        C m c m        Cmcm      Cmcm      -C 2c 2   
63:ba-c   C c m m        Ccmm      Ccmm      -C 2c 2c  
63:cab    A m m a        Amma      Amma      -A 2a 2a  
63:-cba   A m a m        Amam      Amam      -A 2 2a   
63:bca    B b m m        Bbmm      Bbmm      -B 2 2b   
63:a-cb   B m m b        Bmmb      Bmmb      -B 2b 2   
64        C m c a        Cmca      Cmca      -C 2ac 2  
64:ba-c   C c m b        Ccmb      Ccmb      -C 2ac 2ac
64:cab    A b m a        Abma      Abma      -A 2ab 2ab
64:-cba   A c a m        Acam      Acam      -A 2 2ab  
64:bca    B b c m        Bbcm      Bbcm      -B 2 2ab  
64:a-cb   B m a b        Bmab      Bmab      -B 2ab 2  
65        C m m m        Cmmm      Cmmm      -C 2 2    
65:cab    A m m m        Ammm      Ammm      -A 2 2    
65:bca    B m m m        Bmmm      Bmmm      -B 2 2    
66        C c c m        Cccm      Cccm      -C 2 2c   
66:cab    A m a a        Amaa      Amaa      -A 2a 2   
66:bca    B b m b        Bbmb      Bbmb      -B 2b 2b  
67        C m m a        Cmma      Cmma      -C 2a 2   
67:ba-c   C m m b        Cmmb      Cmmb      -C 2a 2a  
67:cab    A b m m        Abmm      Abmm      -A 2b 2b  
67:-cba   A c m m        Acmm      Acmm      -A 2 2b   
67:bca    B m c m        Bmcm      Bmcm      -B 2 2a   
67:a-cb   B m a m        Bmam      Bmam      -B 2a 2   
68:1      C c c a        Ccca      Ccca      C 2 2 -1ac
68:2      C c c a        Ccca      Ccca      -C 2a 2ac 
68:1ba-c  C c c b        Cccb      Cccb      C 2 2 -1ac
68:2ba-c  C c c b        Cccb      Cccb      -C 2a 2c  
68:1cab   A b a a        Abaa      Abaa      A 2 2 -1ab
68:2cab   A b a a        Abaa      Abaa      -A 2a 2b  
68:1-cba  A c a a        Acaa      Acaa      A 2 2 -1ab
68:2-cba  A c a a        Acaa      Acaa      -A 2ab 2b 
68:1bca   B b c b        Bbcb      Bbcb      B 2 2 -1ab
68:2bca   B b c b        Bbcb      Bbcb      -B 2ab 2b 
68:1a-cb  B b a b        Bbab      Bbab      B 2 2 -1ab
68:2a-cb  B b a b        Bbab      Bbab      -B 2b 2ab 
69        F m m m        Fmmm      Fmmm      -F 2 2    
70:1      F d d d        Fddd      Fddd      F 2 2 -1d 
70:2      F d d d        Fddd      Fddd      -F 2uv 2vw
71        I m m m        Immm      Immm      -I 2 2    
72        I b a m        Ibam      Ibam      -I 2 2c   
72:cab    I m c b        Imcb      Imcb      -I 2a 2   
72:bca    I c m a        Icma      Icma      -I 2b 2b  
73        I b c a        Ibca      Ibca      -I 2b 2c  
73:ba-c   I c a b        Icab      Icab      -I 2a 2b  
74        I m m a        Imma      Imma      -I 2b 2   
74:ba-c   I m m b        Immb      Immb      -I 2a 2a  
74:cab    I b m m        Ibmm      Ibmm      -I 2c 2c  
74:-cba   I c m m        Icmm      Icmm      -I 2 2b   
74:bca    I m c m        Imcm      Imcm      -I 2 2a   
74:a-cb   I m a m        Imam      Imam      -I 2c 2   
75        P 4            P4        P4        P 4       
76        P 41           P41       P41       P 4w      
76*       P 41*          P41*      P41*      P 41      
77        P 42           P42       P42       P 4c      
77*       P 42*          P42*      P42*      P 42      
78        P 43           P43       P43       P 4cw     
78*       P 43*          P43*      P43*      P 43      
79        I 4            I4        I4        I 4       
80        I 41           I41       I41       I 4bw     
81        P -4           P-4       P-4       P -4      
82        I -4           I-4       I-4       I -4      
83        P 4/m          P4/m      P4/m      -P 4      
84        P 42/m         P42/m     P42/m     -P 4c     
84*       P 42/m*        P42/m*    P42/m*    -P 42     
85:1      P 4/n          P4/n      P4/n      P 4ab -1ab
85:2      P 4/n          P4/n      P4/n      -P 4a     
86:1      P 42/n         P42/n     P42/n     P 4n -1n  
86:2      P 42/n         P42/n     P42/n     -P 4bc    
87        I 4/m          I4/m      I4/m      -I 4      
88:1      I 41/a         I41/a     I41/a     I 4bw -1bw
88:2      I 41/a         I41/a     I41/a     -I 4ad    
89        P 4 2 2        P422      P422      P 4 2     
90        P 4 21 2       P4212     P4212     P 4ab 2ab 
91        P 41 2 2       P4122     P4122     P 4w 2c   
91*       P 41 2 2*      P4122*    P4122*    P 41 2c   
92        P 41 21 2      P41212    P41212    P 4abw 2nw
93        P 42 2 2       P4222     P4222     P 4c 2    
93*       P 42 2 2*      P4222*    P4222*    P 42 2    
94        P 42 21 2      P42212    P42212    P 4n 2n   
95        P 43 2 2       P4322     P4322     P 4cw 2c  
95*       P 43 2 2*      P4322*    P4322*    P 43 2c   
96        P 43 21 2      P43212    P43212    P 4nw 2abw
97        I 4 2 2        I422      I422      I 4 2     
98        I 41 2 2       I4122     I4122     I 4bw 2bw 
99        P 4 m m        P4mm      P4mm      P 4 -2    
100       P 4 b m        P4bm      P4bm      P 4 -2ab  
101       P 42 c m       P42cm     P42cm     P 4c -2c  
101*      P 42 c m*      P42cm*    P42cm*    P 42 -2c  
102       P 42 n m       P42nm     P42nm     P 4n -2n  
103       P 4 c c        P4cc      P4cc      P 4 -2c   
104       P 4 n c        P4nc      P4nc      P 4 -2n   
105       P 42 m c       P42mc     P42mc     P 4c -2   
105*      P 42 m c*      P42mc*    P42mc*    P 42 -2   
106       P 42 b c       P42bc     P42bc     P 4c -2ab 
106*      P 42 b c*      P42bc*    P42bc*    P 42 -2ab 
107       I 4 m m        I4mm      I4mm      I 4 -2    
108       I 4 c m        I4cm      I4cm      I 4 -2c   
109       I 41 m d       I41md     I41md     I 4bw -2  
110       I 41 c d       I41cd     I41cd     I 4bw -2c 
111       P -4 2 m       P-42m     P-42m     P -4 2    
112       P -4 2 c       P-42c     P-42c     P -4 2c   
113       P -4 21 m      P-421m    P-421m    P -4 2ab  
114       P -4 21 c      P-421c    P-421c    P -4 2n   
115       P -4 m 2       P-4m2     P-4m2     P -4 -2   
116       P -4 c 2       P-4c2     P-4c2     P -4 -2c  
117       P -4 b 2       P-4b2     P-4b2     P -4 -2ab 
118       P -4 n 2       P-4n2     P-4n2     P -4 -2n  
119       I -4 m 2       I-4m2     I-4m2     I -4 -2   
120       I -4 c 2       I-4c2     I-4c2     I -4 -2c  
121       I -4 2 m       I-42m     I-42m     I -4 2    
122       I -4 2 d       I-42d     I-42d     I -4 2bw  
123       P 4/m m m      P4/mmm    P4/mmm    -P 4 2    
124       P 4/m c c      P4/mcc    P4/mcc    -P 4 2c   
125:1     P 4/n b m      P4/nbm    P4/nbm    P 4 2 -1ab
125:2     P 4/n b m      P4/nbm    P4/nbm    -P 4a 2b  
126:1     P 4/n n c      P4/nnc    P4/nnc    P 4 2 -1n 
126:2     P 4/n n c      P4/nnc    P4/nnc    -P 4a 2bc 
127       P 4/m b m      P4/mbm    P4/mbm    -P 4 2ab  
128       P 4/m n c      P4/mnc    P4/mnc    -P 4 2n   
129:1     P 4/n m m      P4/nmm    P4/nmm    P 4ab 2ab 
129:2     P 4/n m m      P4/nmm    P4/nmm    -P 4a 2a  
130:1     P 4/n c c      P4/ncc    P4/ncc    P 4ab 2n -
130:2     P 4/n c c      P4/ncc    P4/ncc    -P 4a 2ac 
131       P 42/m m c     P42/mmc   P42/mmc   -P 4c 2   
132       P 42/m c m     P42/mcm   P42/mcm   -P 4c 2c  
133:1     P 42/n b c     P42/nbc   P42/nbc   P 4n 2c -1
133:2     P 42/n b c     P42/nbc   P42/nbc   -P 4ac 2b 
134:1     P 42/n n m     P42/nnm   P42/nnm   P 4n 2 -1n
134:2     P 42/n n m     P42/nnm   P42/nnm   -P 4ac 2bc
135       P 42/m b c     P42/mbc   P42/mbc   -P 4c 2ab 
135*      P 42/m b c*    P42/mbc*  P42/mbc*  -P 42 2ab 
136       P 42/m n m     P42/mnm   P42/mnm   -P 4n 2n  
137:1     P 42/n m c     P42/nmc   P42/nmc   P 4n 2n -1
137:2     P 42/n m c     P42/nmc   P42/nmc   -P 4ac 2a 
138:1     P 42/n c m     P42/ncm   P42/ncm   P 4n 2ab -
138:2     P 42/n c m     P42/ncm   P42/ncm   -P 4ac 2ac
139       I 4/m m m      I4/mmm    I4/mmm    -I 4 2    
140       I 4/m c m      I4/mcm    I4/mcm    -I 4 2c   
141:1     I 41/a m d     I41/amd   I41/amd   I 4bw 2bw 
141:2     I 41/a m d     I41/amd   I41/amd   -I 4bd 2  
142:1     I 41/a c d     I41/acd   I41/acd   I 4bw 2aw 
142:2     I 41/a c d     I41/acd   I41/acd   -I 4bd 2c 
143       P 3            P3        P3        P 3       
144       P 31           P31       P31       P 31      
145       P 32           P32       P32       P 32      
146:h     R 3            R3        R3        R 3       
146:r     R 3            R3        R3        P 3*      
147       P -3           P-3       P-3       -P 3      
148:h     R -3           R-3       R-3       -R 3      
148:r     R -3           R-3       R-3       -P 3*     
149       P 3 1 2        P312      P32       P 3 2     
150       P 3 2 1        P321      P32       P 3 2     
151       P 31 1 2       P3112     P312      P 31 2 (0 
152       P 31 2 1       P3121     P312      P 31 2    
153       P 32 1 2       P3212     P322      P 32 2 (0 
154       P 32 2 1       P3221     P322      P 32 2    
155:h     R 3 2          R32       R32       R 3 2     
155:r     R 3 2          R32       R32       P 3* 2    
156       P 3 m 1        P3m1      P3m       P 3 -2    
157       P 3 1 m        P31m      P3m       P 3 -2    
158       P 3 c 1        P3c1      P3c       P 3 -2    c
159       P 3 1 c        P31c      P3c       P 3 -2c   
160:h     R 3 m          R3m       R3m       R 3 -2    
160:r     R 3 m          R3m       R3m       P 3* -2   
161:h     R 3 c          R3c       R3c       R 3 -2    c
161:r     R 3 c          R3c       R3c       P 3* -2n  
162       P -3 1 m       P-31m     P-3m      -P 3 2    
163       P -3 1 c       P-31c     P-3c      -P 3 2c   
164       P -3 m 1       P-3m1     P-3m      -P 3 2    
165       P -3 c 1       P-3c1     P-3c      -P 3 2    c
166:h     R -3 m         R-3m      R-3m      -R 3 2    
166:r     R -3 m         R-3m      R-3m      -P 3* 2   
167:h     R -3 c         R-3c      R-3c      -R 3 2    c
167:r     R -3 c         R-3c      R-3c      -P 3* 2n  
168       P 6            P6        P6        P 6       
169       P 61           P61       P61       P 61      
170       P 65           P65       P65       P 65      
171       P 62           P62       P62       P 62      
172       P 64           P64       P64       P 64      
173       P 63           P63       P63       P 6c      
173*      P 63*          P63*      P63*      P 63      
174       P -6           P-6       P-6       P -6      
175       P 6/m          P6/m      P6/m      -P 6      
176       P 63/m         P63/m     P63/m     -P 6c     
176*      P 63/m*        P63/m*    P63/m*    -P 63     
177       P 6 2 2        P622      P622      P 6 2     
178       P 61 2 2       P6122     P6122     P 61 2 (0 
179       P 65 2 2       P6522     P6522     P 65 2 (0 
180       P 62 2 2       P6222     P6222     P 62 2 (0 
181       P 64 2 2       P6422     P6422     P 64 2 (0 
182       P 63 2 2       P6322     P6322     P 6c 2c   
182*      P 63 2 2*      P6322*    P6322*    P 63 2c   
183       P 6 m m        P6mm      P6mm      P 6 -2    
184       P 6 c c        P6cc      P6cc      P 6 -2c   
185       P 63 c m       P63cm     P63cm     P 6c -2   
185*      P 63 c m*      P63cm*    P63cm*    P 63 -2   
186       P 63 m c       P63mc     P63mc     P 6c -2c  
186*      P 63 m c*      P63mc*    P63mc*    P 63 -2c  
187       P -6 m 2       P-6m2     P-6m2     P -6 2    
188       P -6 c 2       P-6c2     P-6c2     P -6c 2   
189       P -6 2 m       P-62m     P-62m     P -6 -2   
190       P -6 2 c       P-62c     P-62c     P -6c -2c 
191       P 6/m m m      P6/mmm    P6/mmm    -P 6 2    
192       P 6/m c c      P6/mcc    P6/mcc    -P 6 2c   
193       P 63/m c m     P63/mcm   P63/mcm   -P 6c 2   
193*      P 63/m c m*    P63/mcm*  P63/mcm*  -P 63 2   
194       P 63/m m c     P63/mmc   P63/mmc   -P 6c 2c  
194*      P 63/m m c*    P63/mmc*  P63/mmc*  -P 63 2c  
195       P 2 3          P23       P23       P 2 2 3   
196       F 2 3          F23       F23       F 2 2 3   
197       I 2 3          I23       I23       I 2 2 3   
198       P 21 3         P213      P213      P 2ac 2ab 
199       I 21 3         I213      I213      I 2b 2c 3 
200       P m -3         Pm-3      Pm-3      -P 2 2 3  
201:1     P n -3         Pn-3      Pn-3      P 2 2 3 -1
201:2     P n -3         Pn-3      Pn-3      -P 2ab 2bc
202       F m -3         Fm-3      Fm-3      -F 2 2 3  
203:1     F d -3         Fd-3      Fd-3      F 2 2 3 -1
203:2     F d -3         Fd-3      Fd-3      -F 2uv 2vw
204       I m -3         Im-3      Im-3      -I 2 2 3  
205       P a -3         Pa-3      Pa-3      -P 2ac 2ab
206       I a -3         Ia-3      Ia-3      -I 2b 2c 3
207       P 4 3 2        P432      P432      P 4 2 3   
208       P 42 3 2       P4232     P4232     P 4n 2 3  
209       F 4 3 2        F432      F432      F 4 2 3   
210       F 41 3 2       F4132     F4132     F 4d 2 3  
211       I 4 3 2        I432      I432      I 4 2 3   
212       P 43 3 2       P4332     P4332     P 4acd 2ab
213       P 41 3 2       P4132     P4132     P 4bd 2ab 
214       I 41 3 2       I4132     I4132     I 4bd 2c 3
215       P -4 3 m       P-43m     P-43m     P -4 2 3  
216       F -4 3 m       F-43m     F-43m     F -4 2 3  
217       I -4 3 m       I-43m     I-43m     I -4 2 3  
218       P -4 3 n       P-43n     P-43n     P -4n 2 3 
219       F -4 3 c       F-43c     F-43c     F -4a 2 3 
220       I -4 3 d       I-43d     I-43d     I -4bd 2c 
221       P m -3 m       Pm-3m     Pm-3m     -P 4 2 3  
222:1     P n -3 n       Pn-3n     Pn-3n     P 4 2 3 -1
222:2     P n -3 n       Pn-3n     Pn-3n     -P 4a 2bc 
223       P m -3 n       Pm-3n     Pm-3n     -P 4n 2 3 
224:1     P n -3 m       Pn-3m     Pn-3m     P 4n 2 3 -
224:2     P n -3 m       Pn-3m     Pn-3m     -P 4bc 2bc
225       F m -3 m       Fm-3m     Fm-3m     -F 4 2 3  
226       F m -3 c       Fm-3c     Fm-3c     -F 4a 2 3 
227:1     F d -3 m       Fd-3m     Fd-3m     F 4d 2 3 -
227:2     F d -3 m       Fd-3m     Fd-3m     -F 4vw 2vw
228:1     F d -3 c       Fd-3c     Fd-3c     F 4d 2 3 -
228:2     F d -3 c       Fd-3c     Fd-3c     -F 4ud 2vw
229       I m -3 m       Im-3m     Im-3m     -I 4 2 3  
230       I a -3 d       Ia-3d     Ia-3d     -I 4bd 2c 

   */
}
