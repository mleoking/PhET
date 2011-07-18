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

/*
 * Bob Hanson 9/2006
 * 
 * references: International Tables for Crystallography Vol. A. (2002) 
 *
 * http://www.iucr.org/iucr-top/cif/cifdic_html/1/cif_core.dic/Ispace_group_symop_operation_xyz.html
 * http://www.iucr.org/iucr-top/cif/cifdic_html/1/cif_core.dic/Isymmetry_equiv_pos_as_xyz.html
 *
 * LATT : http://macxray.chem.upenn.edu/LATT.pdf thank you, Patrick Carroll
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
 * NEVER ACCESS THESE METHODS OUTSIDE OF THIS PACKAGE
 * 
 *
 */

import javax.vecmath.Point3i;
import javax.vecmath.Matrix4f;

import org.jmol.util.Logger;

class HallInfo {
  
  String hallSymbol;
  String primitiveHallSymbol;
  char latticeCode;
  String latticeExtension;
  boolean isCentrosymmetric;
  int nRotations;
  RotationTerm[] rotationTerms = new RotationTerm[16];
  Point3i vector12ths;
  String vectorCode;
  
  HallInfo(String hallSymbol) {
    try {
      String str = this.hallSymbol = hallSymbol.trim();
      str = extractLatticeInfo(str);
      if (Translation.getLatticeIndex(latticeCode) == 0)
        return;
      latticeExtension = Translation.getLatticeExtension(latticeCode,
          isCentrosymmetric);
      str = extractVectorInfo(str) + latticeExtension;
      Logger.info("Hallinfo: " + hallSymbol + " " + str);
      int prevOrder = 0;
      char prevAxisType = '\0';
      primitiveHallSymbol = "P";
      while (str.length() > 0 && nRotations < 16) {
        str = extractRotationInfo(str, prevOrder, prevAxisType);
        RotationTerm r = rotationTerms[nRotations - 1];
        prevOrder = r.order;
        prevAxisType = r.axisType;
        primitiveHallSymbol += " " + r.primitiveCode;
      }
      primitiveHallSymbol += vectorCode;
    } catch (Exception e) {
      Logger.error("Invalid Hall symbol");
      nRotations = 0;
    }
  }
  
  String dumpInfo() {
    StringBuffer sb =  new StringBuffer("\nHall symbol: ").append(hallSymbol)
        .append("\nprimitive Hall symbol: ").append(primitiveHallSymbol)
        .append("\nlattice type: ").append(getLatticeDesignation());
    for (int i = 0; i < nRotations; i++) {
      sb.append("\n\nrotation term ").append(i + 1).append(rotationTerms[i].dumpInfo());
    }
    return sb.toString();
  }

/*  
  String getCanonicalSeitzList() {
    String[] list = new String[nRotations];
    for (int i = 0; i < nRotations; i++)
      list[i] = SymmetryOperation.dumpSeitz(rotationTerms[i].seitzMatrix12ths);
    Arrays.sort(list, 0, nRotations);
    String s = "";
    for (int i = 0; i < nRotations; i++)
      s += list[i];
    s = s.replace('\t',' ').replace('\n',';');
    return s;
  }
*/
  private String getLatticeDesignation() {    
    return Translation.getLatticeDesignation(latticeCode, isCentrosymmetric);
  }  
   
  private String extractLatticeInfo(String name) {
    int i = name.indexOf(" ");
    if (i < 0)
      return "";
    String term = name.substring(0, i).toUpperCase();
    latticeCode = term.charAt(0);
    if (latticeCode == '-') {
      isCentrosymmetric = true;
      latticeCode = term.charAt(1);
    }
    return name.substring(i + 1).trim();
  } 
  
  private String extractVectorInfo(String name) {
    // (nx ny nz)  where n is 1/12 of the edge. 
    // also allows for (nz), though that is not standard
    vector12ths = new Point3i();
    vectorCode = "";
    int i = name.indexOf("(");
    int j = name.indexOf(")", i);
    if (i > 0 && j > i) {
      String term = name.substring(i + 1, j);
      vectorCode = " (" + term + ")";
      name = name.substring(0, i).trim();
      i = term.indexOf(" ");
      if (i >= 0) {
        vector12ths.x = Integer.parseInt(term.substring(0, i));
        term = term.substring(i + 1).trim();
        i = term.indexOf(" ");
        if (i >= 0) {
          vector12ths.y = Integer.parseInt(term.substring(0, i));
          term = term.substring(i + 1).trim();
        }
      }
      vector12ths.z = Integer.parseInt(term);
    }
    return name;
  }
  
  private String extractRotationInfo(String name, int prevOrder, char prevAxisType) {
    int i = name.indexOf(" ");
    String code;
    if (i >= 0) {
      code = name.substring(0, i);
      name = name.substring(i + 1).trim();
    } else {
      code = name;
      name = "";
    }
    rotationTerms[nRotations] = new RotationTerm(code, prevOrder, prevAxisType);
    nRotations++;
    return name;
  }
  
  class RotationTerm {
    
    RotationTerm() {      
    }
    
    String inputCode;
    String primitiveCode;
    String lookupCode;
    String translationString;
    Rotation rotation;
    Translation translation;
    Matrix4f seitzMatrix12ths = new Matrix4f();
    boolean isImproper;
    int order;
    char axisType;
    char diagonalReferenceAxis;
    
    RotationTerm(String code, int prevOrder, char prevAxisType) {
      getRotationInfo(code, prevOrder, prevAxisType);
    }
    
    boolean allPositive = true; //for now
    
    String dumpInfo() {
      StringBuffer sb= new StringBuffer("\ninput code: ")
           .append(inputCode).append("; primitive code: ").append(primitiveCode)
           .append("\norder: ").append(order).append(isImproper ? " (improper axis)" : "");
      if (axisType != '_') {
        sb.append("; axisType: ").append(axisType);
        if (diagonalReferenceAxis != '\0')
          sb.append(diagonalReferenceAxis);
      }
      if (translationString.length() > 0)
        sb.append("; translation: ").append(translationString);
      if (vectorCode.length() > 0)
        sb.append("; vector offset:").append(vectorCode);
      if (rotation != null)
        sb.append("\noperator: ").append(getXYZ(allPositive)).append("\nSeitz matrix:\n")
            .append(SymmetryOperation.dumpSeitz(seitzMatrix12ths));
      return sb.toString();
    }
    
   String getXYZ(boolean allPositive) {
     return SymmetryOperation.getXYZFromMatrix(seitzMatrix12ths, true, allPositive, true);
   }
   
   private void getRotationInfo(String code, int prevOrder, char prevAxisType) {
      this.inputCode = code;
      code += "   ";
      if (code.charAt(0) == '-') {
        isImproper = true;
        code = code.substring(1);
      }
      primitiveCode = "";
      order = code.charAt(0) - '0';
      diagonalReferenceAxis = '\0';
      axisType = '\0';
      int ptr = 2; // pointing to "c" in 2xc
      char c;
      switch (c = code.charAt(1)) {
      case 'x':
      case 'y':
      case 'z':
        switch (code.charAt(2)) {
        case '\'':
        case '"':
          diagonalReferenceAxis = c;
          c = code.charAt(2);
          ptr++;
        }
        // fall through
      case '*':
        axisType = c;
        break;
      case '\'':
      case '"':
        axisType = c;
        switch (code.charAt(2)) {
        case 'x':
        case 'y':
        case 'z':
          diagonalReferenceAxis = code.charAt(2);
          ptr++;
          break;
        default:
          diagonalReferenceAxis = prevAxisType;
        }
        break;
      default:
        // implicit axis type
        axisType = (order == 1 ? '_'// no axis for 1
            : nRotations == 0 ? 'z' // z implied for first rotation
                : nRotations == 2 ? '*' // 3* implied for third rotation
                    : prevOrder == 2 || prevOrder == 4 ? 'x' // x implied for 2
                        // or 4
                        : '\'' // a-b (') implied for 3 or 6 previous
        );
        code = code.substring(0, 1) + axisType + code.substring(1);
      }
      primitiveCode += (axisType == '_' ? "1" : code.substring(0, 2));
      if (diagonalReferenceAxis != '\0') {
        // 2' needs x or y or z designation
        code = code.substring(0, 1) + diagonalReferenceAxis + axisType
            + code.substring(ptr);
        primitiveCode += diagonalReferenceAxis;
        ptr = 3;
      }
      lookupCode = code.substring(0, ptr);
      rotation = Rotation.lookup(lookupCode);
      if (rotation == null) {
        Logger.error("Rotation lookup could not find " + inputCode + " ? "
            + lookupCode);
        return;
      }

      // now for translational part 1 2 3 4 5 6 a b c n u v w d r
      // The "r" is my addition to handle rhombohedral lattice with
      // primitive notation. This made coding FAR simpler -- all lattice
      // operations indicated by one to three 1xxx or -1 extensions.

      translation = new Translation();
      translationString = "";
      int len = code.length();
      for (int i = ptr; i < len; i++) {
        char translationCode = code.charAt(i);
        Translation t = new Translation(translationCode, order);
        if (t.translationCode != '\0') {
          translationString += "" + t.translationCode;
          translation.rotationShift12ths += t.rotationShift12ths;
          translation.vectorShift12ths.add(t.vectorShift12ths);
        }
      }
      primitiveCode = (isImproper ? "-" : "") + primitiveCode
          + translationString;

      // set matrix, including translations and vector adjustment

      if (isImproper) {
        seitzMatrix12ths.set(rotation.seitzMatrixInv);
      } else {
        seitzMatrix12ths.set(rotation.seitzMatrix);
      }
      seitzMatrix12ths.m03 = translation.vectorShift12ths.x;
      seitzMatrix12ths.m13 = translation.vectorShift12ths.y;
      seitzMatrix12ths.m23 = translation.vectorShift12ths.z;
      switch (axisType) {
      case 'x':
        seitzMatrix12ths.m03 += translation.rotationShift12ths;
        break;
      case 'y':
        seitzMatrix12ths.m13 += translation.rotationShift12ths;
        break;
      case 'z':
        seitzMatrix12ths.m23 += translation.rotationShift12ths;
        break;
      }

      if (vectorCode.length() > 0) {
        Matrix4f m1 = new Matrix4f();
        Matrix4f m2 = new Matrix4f();
        m1.setIdentity();
        m2.setIdentity();
        m1.m03 = vector12ths.x;
        m1.m13 = vector12ths.y;
        m1.m23 = vector12ths.z;
        m2.m03 = -vector12ths.x;
        m2.m13 = -vector12ths.y;
        m2.m23 = -vector12ths.z;
        seitzMatrix12ths.mul(m1, seitzMatrix12ths);
        seitzMatrix12ths.mul(m2);
      }
      if (Logger.debugging) {
        Logger.debug("code = " + code + "; primitive code =" + primitiveCode
            + "\n Seitz Matrix(12ths):" + seitzMatrix12ths);
      }
    }
  }  
}

class Translation {
  
  char translationCode = '\0';
  int rotationOrder;
  int rotationShift12ths;
  Point3i vectorShift12ths = new Point3i();

  Translation() {
  }
  
  Translation(char translationCode, int order) {
    for (int i = 0; i < hallTranslationTerms.length; i++) {
      Translation h = hallTranslationTerms[i];
      if (h.translationCode == translationCode) {
        if (h.rotationOrder == 0 || h.rotationOrder == order) {
          this.translationCode = translationCode;
          rotationShift12ths = h.rotationShift12ths;
          vectorShift12ths = h.vectorShift12ths;
          return;
        }
      }
    }
  }

  private Translation(char translationCode, 
      Point3i vectorShift12ths) {
    this.translationCode = translationCode;
    this.rotationOrder = 0;
    this.rotationShift12ths = 0;
    this.vectorShift12ths = vectorShift12ths;        
  }
  
  private Translation(char translationCode, int order, 
      int rotationShift12ths) {
    this.translationCode = translationCode;
    this.rotationOrder = order;
    this.rotationShift12ths = rotationShift12ths;
    this.vectorShift12ths = new Point3i();        
  }

  final static String getHallLatticeEquivalent(int latticeParameter) {
   // SHELX LATT --> Hall term
    char latticeCode = Translation.getLatticeCode(latticeParameter);
    boolean isCentrosymmetric = (latticeParameter > 0);
    return (isCentrosymmetric ? "-" : "") + latticeCode + " 1";
  }
  
  final static int getLatticeIndex(char latt) {
    /*
     * returns lattice code (1-9, including S and T) for a given lattice type
     * 1-7 match SHELX codes
     * 
     */
    for (int i = 1, ipt = 3; i <= nLatticeTypes; i++, ipt+=3)
      if (latticeTranslationData[ipt].charAt(0) == latt)
        return i;
    return 0;
  }
  
  /**
   * 
   * @param latt SHELX index or character
   * @return lattice character P I R F A B C S T or \0
   * 
   */
  final static char getLatticeCode(int latt) {
    if (latt < 0)
      latt = -latt;
    return (latt == 0 ? '\0' : latt > nLatticeTypes ?
        getLatticeCode(getLatticeIndex((char)latt))
        : latticeTranslationData[latt * 3].charAt(0));
  }

  final static String getLatticeDesignation(int latt) {
    boolean isCentrosymmetric = (latt > 0);
    String str = (isCentrosymmetric ? "-" : "");
    if (latt < 0)
      latt = -latt;
    if (latt == 0 || latt > nLatticeTypes)
      return "";
    return str + getLatticeCode(latt) + ": "
        + (isCentrosymmetric ? "centrosymmetric " : "")
        + latticeTranslationData[latt * 3 + 1];
  }  
 
  final static String getLatticeDesignation(char latticeCode, boolean isCentrosymmetric) {
    int latt = getLatticeIndex(latticeCode);
    if (!isCentrosymmetric)
      latt = - latt;
    return getLatticeDesignation(latt);
  }  
 
  final static String getLatticeExtension(char latt, boolean isCentrosymmetric) {
    /*
     * returns a set of rotation terms that are equivalent to the lattice code
     * 
     */
    for (int i = 1, ipt = 3; i <= nLatticeTypes; i++, ipt += 3)
      if (latticeTranslationData[ipt].charAt(0) == latt)
        return latticeTranslationData[ipt + 2]
            + (isCentrosymmetric ? " -1" : "");
    return "";
  }

  final static String[] latticeTranslationData = {
    "\0", "unknown",         ""
    ,"P", "primitive",       ""
    ,"I", "body-centered",   " 1n"
    ,"R", "rhombohedral",    " 1r 1r"
    ,"F", "face-centered",   " 1ab 1bc 1ac"
    ,"A", "A-centered",      " 1bc"
    ,"B", "B-centered",      " 1ac"
    ,"C", "C-centered",      " 1ab"
    ,"S", "rhombohedral(S)", " 1s 1s"
    ,"T", "rhombohedral(T)", " 1t 1t"
  };
  
  final static int nLatticeTypes = latticeTranslationData.length/3 - 1;
 

  final static Translation[] hallTranslationTerms = {
    // all units are 12ths
    new Translation('a', new Point3i(6, 0, 0))
    , new Translation('b', new Point3i(0, 6, 0))
    , new Translation('c', new Point3i(0, 0, 6))
    , new Translation('n', new Point3i(6, 6, 6))
    , new Translation('u', new Point3i(3, 0, 0))
    , new Translation('v', new Point3i(0, 3, 0))
    , new Translation('w', new Point3i(0, 0, 3))
    , new Translation('d', new Point3i(3, 3, 3))
    , new Translation('1', 2, 6)
    , new Translation('1', 3, 4)
    , new Translation('2', 3, 8)
    , new Translation('1', 4, 3)
    , new Translation('3', 4, 9)
    , new Translation('1', 6, 2)
    , new Translation('2', 6, 4)
    , new Translation('4', 6, 8)
    , new Translation('5', 6, 10)
    // extension to handle rhombohedral lattice as primitive
    , new Translation('r', new Point3i(4, 8, 8))
    , new Translation('s', new Point3i(8, 8, 4))
    , new Translation('t', new Point3i(8, 4, 8))
  };
}

class Rotation {
  String rotCode;
  //int order;
  Matrix4f seitzMatrix = new Matrix4f();
  Matrix4f seitzMatrixInv = new Matrix4f();
  
  Rotation () {
  }
  
  private Rotation (String code, String matrixData) {
    rotCode = code;
    //order = code.charAt(0) - '0';
    float[] data = new float[16];
    float[] dataInv = new float[16];
    data[15] = dataInv[15] = 1f;
    
    for (int i = 0, ipt = 0; ipt < 11; i++) {
      int value = 0;
      switch(matrixData.charAt(i)) {
      case ' ':
        ipt++;
        continue;
      case '+':
      case '1':
        value = 1;
        break;
      case '-':
        value = -1;
        break;
      }
      data[ipt] = value;
      dataInv[ipt] = -value; 
      ipt++;
    }
    seitzMatrix.set(data);
    seitzMatrixInv.set(dataInv);
  }
  
  final static Rotation lookup(String code) {
    for (int i = hallRotationTerms.length; --i >= 0;)
      if (hallRotationTerms[i].rotCode.equals(code))
        return hallRotationTerms[i];
    return null;
  }
  
  final static Rotation[] hallRotationTerms = {
    // in matrix definitions, "+" = 1; "-" = -1;
    // just a compact way of indicating a 3x3
      new Rotation("1_"   , "+00 0+0 00+")
    , new Rotation("2x"   , "+00 0-0 00-")
    , new Rotation("2y"   , "-00 0+0 00-")
    , new Rotation("2z"   , "-00 0-0 00+")
    , new Rotation("2\'"  , "0-0 -00 00-") //z implied
    , new Rotation("2\""  , "0+0 +00 00-") //z implied
    , new Rotation("2x\'" , "-00 00- 0-0")
    , new Rotation("2x\"" , "-00 00+ 0+0")
    , new Rotation("2y\'" , "00- 0-0 -00")
    , new Rotation("2y\"" , "00+ 0-0 +00")
    , new Rotation("2z\'" , "0-0 -00 00-")
    , new Rotation("2z\"" , "0+0 +00 00-")
    , new Rotation("3x"   , "+00 00- 0+-")
    , new Rotation("3y"   , "-0+ 0+0 -00")
    , new Rotation("3z"   , "0-0 +-0 00+")
    , new Rotation("3*"   , "00+ +00 0+0")
    , new Rotation("4x"   , "+00 00- 0+0")
    , new Rotation("4y"   , "00+ 0+0 -00")
    , new Rotation("4z"   , "0-0 +00 00+")
    , new Rotation("6x"   , "+00 0+- 0+0")
    , new Rotation("6y"   , "00+ 0+0 -0+")
    , new Rotation("6z"   , "+-0 +00 00+")
  };    
}  
