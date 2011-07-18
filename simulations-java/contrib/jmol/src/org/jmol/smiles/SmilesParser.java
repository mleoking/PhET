/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-11-24 16:27:22 -0800 (Wed, 24 Nov 2010) $
 * $Revision: 14707 $
 *
 * Copyright (C) 2005  The Jmol Development Team
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

package org.jmol.smiles;


import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import java.util.Map;

import org.jmol.util.Elements;
import org.jmol.util.TextFormat;
import org.jmol.util.Logger;

/**
 * Parses a SMILES String to create a <code>SmilesMolecule</code>.
 * The SMILES specification has been found at the
 * <a href="http://www.daylight.com/smiles/">SMILES Home Page</a>.
 * An other explanation can be found in the
 * <a href="http://www.daylight.com/dayhtml/doc/theory/index.html">Daylight Theory Manual</a>. <br>
 * 
 * Currently this parser supports only parts of the SMILES specification. <br>
 * 
 * An example on how to use it:
 * <pre><code>
 * try {
 *   SmilesParser sp = new SmilesParser();
 *   SmilesMolecule sm = sp.parseSmiles("CC(C)C(=O)O");
 *   // Use the resulting molecule 
 * } catch (InvalidSmilesException e) {
 *   // Exception management
 * }
 * </code></pre>
 * 
 * @see <a href="http://www.daylight.com/smiles/">SMILES Home Page</a>
 */
public class SmilesParser {

  /*
   * see package.html for details
   *
   * Bob Hanson, Jmol 12.0.RC10, 5/8/2010
   * 
   */

  /*
   * implicit hydrogen calculation
   * 
   * For a match to a SMILES string, as in "CCC".find("[Ch2]")
   * Jmol will return no match, because "CCC" refers to a structure
   * with a specific set of H atoms. Just because the H atoms are 
   * "implicit" in "CCC" is irrelevant.
   * 
   * For a match to a 3D model, [*h2] refers to all atoms such 
   * that the "calculate hydrogens" command would add two hydrogens,
   * and [*h] refers to cases where Jmol would add at least one hydrogen.
   * 
   * Jmol calculates the number of implicit hydrogens as follows:
   * 
   *  int targetValence = getTargetValence();
   *  int charge = getFormalCharge();
   *  if (charge != 0)
   targetValence += (targetValence == 4 ? -Math.abs(charge) : charge);
   * int n = targetValence - getValence();
   * nImplicitHydrogens = (n < 0 ? 0 : n);
   * 
   * Where getTargetValence() returns:
   *     switch (getElementNumber()) {
   *     case 6: //C
   *     case 14: //Si      
   *       return 4;
   *     case 5:  // B
   *     case 7:  // N
   *     case 15: // P
   *       return 3;
   *     case 8: //O
   *     case 16: //S
   *       return 2;
   *     default:
   *       return -1;
   *     }
   *     
   * Thus the implicit hydrogen count is:
   * 
   * a) 0 for all atoms other than {B,C,N,O,P,Si,S}
   * b) 0 for BR3
   * c) 0 for NR3, 1 for NR2, 2 for NR
   * d) 0 for RN=R, 1 for R=N, 0 for R=NR2(+), 0 for R2N(-)
   * e) 0 for CR4, 1 for CR3, 2 for CR2, 3 for CR
   * f) 0 for CR3(+), 0 for CR3(-)
   * 
   */
  private boolean isSmarts;
  private boolean isBioSequence;
  private char bioType;
  private Map<Integer, SmilesBond> ringBonds = new Hashtable<Integer, SmilesBond>();
  private int braceCount;
  private int branchLevel;

  public static SmilesSearch getMolecule(String pattern, boolean isSmarts)
      throws InvalidSmilesException {
    return (new SmilesParser(isSmarts)).parse(pattern);
  }

  private SmilesParser(boolean isSmarts) {
    this.isSmarts = isSmarts;
  }

  /**
   * Parses a SMILES String
   * 
   * @param pattern
   *          SMILES String
   * @return Molecule corresponding to <code>pattern</code>
   * @throws InvalidSmilesException
   */
  SmilesSearch parse(String pattern) throws InvalidSmilesException {
    // if (Logger.debugging)
    // Logger.debug("Smiles Parser: " + pattern);
    if (pattern == null)
      throw new InvalidSmilesException("SMILES expressions must not be null");
    pattern = cleanPattern(pattern, isSmarts);
    boolean noAromatic = false;
    boolean ignoreStereochemistry = false;
    while (pattern.startsWith("/")) {
      String flags = getSubPattern(pattern, 0, '/').toUpperCase();
      pattern = pattern.substring(flags.length());
      if (flags.indexOf("NOAROMATIC") >= 0)
        noAromatic = true;
      if (flags.indexOf("NOSTEREO") >= 0)
        ignoreStereochemistry = true;
    }
    if (pattern.indexOf("$") >= 0)
      pattern = parseVariables(pattern);
    if (isSmarts && pattern.indexOf("[$(") >= 0)
      pattern = parseVariableLength(pattern);
    if (pattern.indexOf("||") >= 0) {
      SmilesSearch search = new SmilesSearch();
      String[] patterns = TextFormat.split(pattern, "||");  
      String toDo = "";
      search.subSearches = new SmilesSearch[patterns.length];
      for (int i = 0; i < patterns.length; i++) {
        String key = "|" + patterns[i] + "|";
        if (toDo.indexOf(key) < 0) {
          search.subSearches[i] = getSearch(search, patterns[i], noAromatic, ignoreStereochemistry);
          toDo += key;
        }
      }
      //if (Logger.debugging)
        Logger.info(toDo);
      return search;
    }
     return getSearch(null, pattern, noAromatic, ignoreStereochemistry);
  }

  private String parseVariableLength(String pattern)
      throws InvalidSmilesException {
    StringBuffer sout = new StringBuffer();
    // fix internal ||
    int len = pattern.length() - 1;
    int nParen = 0;
    boolean haveInternalOr = false;
    for (int i = 0; i < len; i++) {
      switch (pattern.charAt(i)) {
      case '(':
        nParen++;
        break;
      case ')':
        nParen--;
        break;
      case '|':
        if (nParen > 0) {
          haveInternalOr = true;
          if (pattern.charAt(i + 1) == '|') {
            pattern = pattern.substring(0, i) + pattern.substring(i + 1);
            len--;
          }
        }
        break;
      }
    }
    if (pattern.indexOf("||") >= 0) {
      String[] patterns = TextFormat.split(pattern, "||");
      for (int i = 0; i < patterns.length; i++)
        sout.append("||").append(parseVariableLength(patterns[i]));
    } else {
      int pt = -1;
      int[] ret = new int[1];
      // [$(...)n] or [$(...)m-n]
      boolean isOK = true;
      String bracketed = null;
      while ((pt = pattern.indexOf("[$(", pt + 1)) >= 0) {
        int pt0 = pt;
        bracketed = getSubPattern(pattern, pt, '[');
        int pt1 = pt + bracketed.length() + 2;
        String repeat = getSubPattern(bracketed, 1, '(');
        pt += 4 + repeat.length();
        if (repeat.indexOf(':') >= 0 && repeat.indexOf('|') < 0) {
          // must enclose first ":" in ()
          int parenCount = 0;
          int n = repeat.length();
          int ptColon = -1;
          for (int i = 0; i < n; i++) {
            switch (repeat.charAt(i)) {
            case '[':
            case '(':
              parenCount++;
              break;
            case ')':
            case ']':
              parenCount--;
              break;
            case '.':
              if (ptColon >= 0 && parenCount == 0)
                n = i;
              break;
            case ':':
              if (ptColon < 0 && parenCount == 0)
                ptColon = i;
              break;
            }
          }
          if (ptColon > 0)
            repeat = repeat.substring(0, ptColon) + "("
                + repeat.substring(ptColon, n) + ")" + repeat.substring(n);
        }
        if (!Character.isDigit(getChar(pattern, pt))) {
          int ptOr = repeat.indexOf("|");
          if (ptOr >= 0)
            return parseVariableLength(pattern.substring(0, pt0 + ptOr + 3) + ")1"
                + pattern.substring(pt) + "||"
                + pattern.substring(0, pt0 + 3) + repeat.substring(ptOr + 1) + ")1"
                + pattern.substring(pt));
          continue;
        }
        pt = getDigits(pattern, pt, ret);
        int min = ret[0];
        int max = min;
        if (getChar(pattern, pt) == '-') {
          if (!Character.isDigit(getChar(pattern, pt + 1))) {
            isOK = false;
            break;
          }
          pt = getDigits(pattern, pt + 1, ret);
          max = ret[0];
        }
        if (pt != pt1 - 1) {
          isOK = false;
          break;
        }
        if (repeat.indexOf("|") >= 0)
          repeat = "[$(" + repeat + ")]";
        for (int i = min; i <= max; i++) {
          StringBuffer sb = new StringBuffer();
          sb.append("||").append(pattern.substring(0, pt0));
          for (int j = 0; j < i; j++)
            sb.append(repeat);
          sb.append(pattern.substring(pt1));
          sout.append(sb);
        }
      }
      if (!isOK)
        throw new InvalidSmilesException("bad variable expression: "
            + bracketed);
    }
    if (haveInternalOr)
      return parseVariableLength(sout.substring(2));
    return (sout.length() < 2 ? pattern : sout.substring(2));
  }

  private SmilesSearch getSearch(SmilesSearch parent, String pattern,
                                 boolean noAromatic,
                                 boolean ignoreStereochemistry)
      throws InvalidSmilesException {
    // First pass
    htMeasures = new Hashtable<String, SmilesMeasure>();
    SmilesSearch molecule = new SmilesSearch();
    molecule.setParent(parent);
    molecule.isSmarts = isSmarts;
    molecule.pattern = pattern;
    molecule.noAromatic = noAromatic;
    molecule.ignoreStereochemistry = ignoreStereochemistry;
    if (pattern.indexOf("$(") >= 0)
      pattern = parseNested(molecule, pattern);
    parseSmiles(molecule, pattern, null, false);

    // Check for braces

    if (braceCount != 0)
      throw new InvalidSmilesException("unmatched '{'");

    // Check for rings

    if (!ringBonds.isEmpty())
      throw new InvalidSmilesException("Open ring");

    // finalize atoms

    molecule.setAtomArray();

    for (int i = molecule.atomCount; --i >= 0;) {
      SmilesAtom atom = molecule.patternAtoms[i];
      atom.setBondArray();
      if (!isSmarts && atom.bioType == '\0' && !atom.setHydrogenCount(molecule))
        throw new InvalidSmilesException("unbracketed atoms must be one of: "
            + SmilesAtom.UNBRACKETED_SET);
    }

    // set the searches now that we know what's a bioAtom and what's not

    if (isSmarts)
      for (int i = molecule.atomCount; --i >= 0;) {
        SmilesAtom atom = molecule.patternAtoms[i];
        checkNested(molecule, atom, noAromatic, ignoreStereochemistry);
        for (int k = 0; k < atom.nAtomsOr; k++)
          checkNested(molecule, atom.atomsOr[k], noAromatic, ignoreStereochemistry);
        for (int k = 0; k < atom.nPrimitives; k++)
          checkNested(molecule, atom.primitives[k], noAromatic, ignoreStereochemistry);
      }
    if (!isSmarts && !isBioSequence)
      molecule.elementCounts[1] = molecule.getMissingHydrogenCount();
    fixChirality(molecule);

    return molecule;
  }
  
  private void checkNested(SmilesSearch molecule, SmilesAtom atom, 
                           boolean noAromatic, boolean ignoreStereochemistry) throws InvalidSmilesException {
    if (atom.iNested > 0) {
      Object o = molecule.getNested(atom.iNested);
      if (o instanceof String) {
        String s = (String) o;
        if (s.charAt(0) != '~' && atom.bioType != '\0')
          s = "~" + atom.bioType + "~" + s;
        SmilesSearch search = getSearch(molecule, s, noAromatic, ignoreStereochemistry);
        if (search.atomCount > 0 && search.patternAtoms[0].selected)
          atom.selected = true;
        molecule.setNested(atom.iNested, search);
      }
    }
  }

  private void fixChirality(SmilesSearch molecule)
      throws InvalidSmilesException {
    for (int i = molecule.atomCount; --i >= 0;) {
      SmilesAtom sAtom = molecule.patternAtoms[i];
      int stereoClass = sAtom.getChiralClass();
      if (stereoClass == Integer.MIN_VALUE)
        continue;
      int nBonds = sAtom.missingHydrogenCount;
      if (nBonds < 0)
        nBonds = 0;
      nBonds += sAtom.getBondCount();
      switch (stereoClass) {
      case SmilesAtom.STEREOCHEMISTRY_DEFAULT:
        switch (nBonds) {
        case 2:
          stereoClass = (sAtom.getValence() == 3 ? 3 : 2);
          break;
        case 3:
        case 4:
        case 5:
        case 6:
          stereoClass = nBonds;
          break;
        }
        break;
      case SmilesAtom.STEREOCHEMISTRY_SQUARE_PLANAR:
        if (nBonds != 4)
          stereoClass = SmilesAtom.STEREOCHEMISTRY_DEFAULT;
        break;
/*        
      case SmilesAtom.STEREOCHEMISTRY_DOUBLE_BOND:
        if (nBonds != 2 && nBonds != 3)
          stereoClass = SmilesAtom.STEREOCHEMISTRY_DEFAULT;
        break;
*/        
      case SmilesAtom.STEREOCHEMISTRY_ALLENE:
      case SmilesAtom.STEREOCHEMISTRY_OCTAHEDRAL:
      case SmilesAtom.STEREOCHEMISTRY_TETRAHEDRAL:
      case SmilesAtom.STEREOCHEMISTRY_TRIGONAL_BIPYRAMIDAL:
        if (nBonds != stereoClass)
          stereoClass = SmilesAtom.STEREOCHEMISTRY_DEFAULT;
        break;
      }
      if (stereoClass == SmilesAtom.STEREOCHEMISTRY_DEFAULT)
        throw new InvalidSmilesException(
            "Incorrect number of bonds for stereochemistry descriptor");
      sAtom.setChiralClass(stereoClass);
    }
  }

  /**
   * Parses a part of a SMILES String
   * 
   * @param molecule
   *          Resulting molecule
   * @param pattern
   *          SMILES String
   * @param currentAtom
   *          Current atom
   * @param isBranchAtom 
   *          If we are starting a new branch
   * @throws InvalidSmilesException
   */
  private void parseSmiles(SmilesSearch molecule, String pattern,
                           SmilesAtom currentAtom, boolean isBranchAtom)
      throws InvalidSmilesException {
    int[] ret = new int[1];
    int pt = 0;
    char ch;
    SmilesBond bond = null;
    while (pattern != null && pattern.length() != 0) {
      int index = 0;
      if (currentAtom == null || bond != null && bond.bondType == SmilesBond.TYPE_NONE)
        index = checkBioType(pattern, 0);
      ch = getChar(pattern, index);
      boolean haveOpen = checkBrace(molecule, ch, '{');
      if (haveOpen)
        ch = getChar(pattern, ++index);

      // Branch -- note that bonds come AFTER '(', not before. 

      if (ch == '(') {
        boolean isMeasure = (getChar(pattern, index + 1) == '.');
        if (currentAtom == null)
          throw new InvalidSmilesException("No previous atom for "
              + (isMeasure ? "measure" : "branch"));
        String subString = getSubPattern(pattern, index, '(');
        if (subString.startsWith(".")) {
          parseMeasure(molecule, subString.substring(1), currentAtom);
        } else if (subString.length() == 0 && isBioSequence) {
          currentAtom.notCrossLinked = true;
        } else {
          branchLevel++;
          parseSmiles(molecule, subString, currentAtom, true);
          branchLevel--;
        }
        index = subString.length() + 2;
        ch = getChar(pattern, index);
        if (ch == '}' && checkBrace(molecule, ch, '}'))
          index++;
        // DAYLIGHT DEPICT doesn't care...if (index == len && !isSmarts && !isBioSequence)
        //throw new InvalidSmilesException("Pattern must not end with ')'");
      } else {
        pt = index;
        while (SmilesBond.isBondType(ch, isSmarts, isBioSequence))
          ch = getChar(pattern, ++index);

        bond = parseBond(molecule, null, pattern
            .substring(pt, index), null, currentAtom, false, isBranchAtom);


        if (haveOpen && bond.bondType != SmilesBond.TYPE_UNKNOWN)
          index = pt;
        ch = getChar(pattern, index);
        if (checkBrace(molecule, ch, '{'))
          ch = getChar(pattern, ++index);
        if (ch == '~' && bond.bondType == SmilesBond.TYPE_NONE) {
          index = checkBioType(pattern, index);
          ch = getChar(pattern, index);
        }
        if (ch == '\0' && bond.bondType == SmilesBond.TYPE_NONE)
          return;
        boolean isRing = (Character.isDigit(ch) || ch == '%');
        boolean isAtom = (!isRing && (ch == '_' || ch == '[' || ch == '*' || Character
            .isLetter(ch)));
        if (isRing) {
          int ringNumber;
          switch (ch) {
          case '%':
            // [ringPoint]
            if (getChar(pattern, index + 1) == '(') { // %(nnn)
              String subPattern = getSubPattern(pattern, index + 1, '(');
              getDigits(subPattern, 0, ret);
              index += subPattern.length() + 3;
              if (ret[0] < 0)
                throw new InvalidSmilesException("Invalid ring designation: "
                    + subPattern);
            } else {
              if (index + 3 <= pattern.length())
                index = getDigits(pattern.substring(0, index + 3), index + 1,
                    ret);
              if (ret[0] < 10)
                throw new InvalidSmilesException(
                    "Two digits must follow the % sign");
            }
            ringNumber = ret[0];
            break;
          default:
            ringNumber = ch - '0';
            index++;
          }
          parseRing(molecule, ringNumber, currentAtom, bond);
        } else if (isAtom) {
          switch (ch) {
          case '[':
          case '_':
            String subPattern = getSubPattern(pattern, index, ch);
            index += subPattern.length() + (ch == '[' ? 2 : 0);
            if (isBioSequence && ch == '[' && subPattern.indexOf(".") < 0
                && subPattern.indexOf("_") < 0)
              subPattern += ".0";
            currentAtom = parseAtom(molecule, null, subPattern, currentAtom,
                bond, ch == '[', false, isBranchAtom);
            if (bond.bondType != SmilesBond.TYPE_UNKNOWN
                && bond.bondType != SmilesBond.TYPE_NONE)
              bond.set(null, currentAtom);
            break;
          default:
            // [atomType]
            char ch2 = (!isBioSequence && Character.isUpperCase(ch) ? getChar(
                pattern, index + 1) : '\0');
            if (ch != 'X' || ch2 != 'x')
              if (!Character.isLowerCase(ch2)
                  || Elements.elementNumberFromSymbol(pattern.substring(index,
                      index + 2), true) == 0)
                ch2 = '\0';
            // guess at some ambiguous SEARCH strings:
            if (ch2 != '\0'
                && "NA CA BA PA SC AC".indexOf(pattern.substring(index,
                    index + 2)) >= 0) {
              //System.out.println("Note: " + ch + ch2 + " NOT interpreted as an element");
              ch2 = '\0';
            }
            int size = (Character.isUpperCase(ch) && Character.isLowerCase(ch2) ? 2
                : 1);
            currentAtom = parseAtom(molecule, null, pattern.substring(index,
                index + size), currentAtom, bond, false, false, isBranchAtom);
            index += size;
          }

        } else {
          throw new InvalidSmilesException("Unexpected character: "
              + getChar(pattern, index));
        }

        ch = getChar(pattern, index);
        if (ch == '}' && checkBrace(molecule, ch, '}'))
          index++;

      }
      // [connections]

      pattern = pattern.substring(index);
      isBranchAtom = false;
    }
  }

  private int checkBioType(String pattern, int index) {
    isBioSequence = (pattern.charAt(index) == '~');
    if (isBioSequence) {
      index++;
      bioType = '*';
      char ch = getChar(pattern, 2);
      if (ch == '~'
          && ((ch = pattern.charAt(1)) == '*' || Character.isLowerCase(ch))) {
        bioType = ch;
        index = 3;
      }
    }
    return index;
  }

  Map<String, SmilesMeasure> htMeasures = new Hashtable<String, SmilesMeasure>();
  
  private void parseMeasure(SmilesSearch molecule, String strMeasure,
                            SmilesAtom currentAtom)
      throws InvalidSmilesException {
    // parsing of C(.d:1.5,1.6)C
    // or C(.d1:1.5-1.6)C(.d1)
    // or C(.d1:!1.5-1.6)C(.d1)
    int pt = strMeasure.indexOf(":");
    boolean isNot = false;
    String id = (pt < 0 ? strMeasure : strMeasure.substring(0, pt));
    while (pt != 0) { // no real repeat here -- just an enclosure for break
      int len = id.length();
      if (len == 1)
        id += "0";
      SmilesMeasure m = htMeasures.get(id);
      if ((m == null) == (pt < 0))
        break;
      try {
        if (pt > 0) {
          int type = (SmilesMeasure.TYPES.indexOf(id.charAt(0)));
          if (type < 2)
            break;
          int[] ret = new int[1];
          int index = getDigits(id, 1, ret);
          int pt2 = strMeasure.indexOf(",", pt);
          if (pt2 < 0)
            pt2 = strMeasure.indexOf("-", pt + 1);
          if (pt2 < 0)
            break;
          String s = strMeasure.substring(pt + 1, pt2);
          if (s.startsWith("!")) {
            isNot = true;
            s = s.substring(1);
          }
          float min = (pt + 1 == pt2 ? 0 : Float.parseFloat(s));
          s = strMeasure.substring(pt2 + 1);
          float max = (s.length() == 0 ? Float.MAX_VALUE : Float.parseFloat(s));
          m = new SmilesMeasure(molecule, index, type, min, max, isNot);
          molecule.measures.add(m);
          if (index > 0)
            htMeasures.put(id, m);
          else if (index == 0 && Logger.debugging)
            Logger.debug("measure created: " + m);
        } else {
          if (!m.addPoint(currentAtom.index))
            break;
          if (m.nPoints == m.type) {
            htMeasures.remove(id);
            if (Logger.debugging)
              Logger.debug("measure created: " + m);
          }
          return;
        }
        if (!m.addPoint(currentAtom.index))
          break;
      } catch (NumberFormatException e) {
        break;
      }
      return;
    }
    throw new InvalidSmilesException("invalid measure: " + strMeasure);
  }

  private boolean checkBrace(SmilesSearch molecule, char ch, char type)
      throws InvalidSmilesException {
    switch (ch) {
    case '{':
      if (ch != type)
        break;
      // 3D SEARCH {C}CCC{C}C subset selection
      braceCount++;
      molecule.parent.haveSelected = true;
      return true;
    case '}':
      if (ch != type)
        break;
      if (braceCount > 0) {
        braceCount--;
        return true;
      }
    default:
      return false;
    }
    throw new InvalidSmilesException("Unmatched '}'");
  }

  private String parseNested(SmilesSearch molecule, String pattern)
      throws InvalidSmilesException {
    int index;
    while ((index = pattern.lastIndexOf("$(")) >= 0) {
      String s = getSubPattern(pattern, index + 1, '(');
      int pt = index + s.length() + 3;
      pattern = pattern.substring(0, index) 
      + "_" + molecule.addNested(s) + "_"
      + pattern.substring(pt);
    }
    return pattern;
  }

  private String parseVariables(String pattern) throws InvalidSmilesException {
    List<String> keys = new ArrayList<String>();
    List<String> values = new ArrayList<String>();
    int index;
    int ipt = 0;
    int iptLast = -1;
    while ((index = pattern.indexOf("$", ipt)) >= 0) {
      if (getChar(pattern, ipt + 1) == '(')
        break;
      ipt = skipTo(pattern, index, '=');
      if (ipt <= index + 1 || getChar(pattern, ipt + 1) != '\"')
        break;
      String key = pattern.substring(index, ipt);
      if (key.lastIndexOf('$') > 0 || key.indexOf(']') > 0)
        throw new InvalidSmilesException("Invalid variable name: " + key);
      String s = getSubPattern(pattern, ipt + 1, '\"');
      keys.add("[" + key + "]");
      values.add(s);
      ipt += s.length() + 2;
      ipt = skipTo(pattern, ipt, ';');
      iptLast = ++ipt;
    }
    if (iptLast < 0)
      return pattern;
    return TextFormat.replaceStrings(pattern.substring(iptLast), keys, values);
  }

  /**
   * Parses an atom definition
   * 
   * @param molecule
   *          Resulting molecule
   * @param atomSet
   * @param pattern
   *          SMILES String
   * @param currentAtom
   *          Current atom
   * @param bond 
   * @param isBracketed
   *          Indicates if is a isBracketed definition (between [])
   * @param isPrimitive
   * @param isBranchAtom
   * @return New atom
   * @throws InvalidSmilesException
   */
  private SmilesAtom parseAtom(SmilesSearch molecule, SmilesAtom atomSet,
                               String pattern, SmilesAtom currentAtom,
                               SmilesBond bond, boolean isBracketed,
                               boolean isPrimitive, boolean isBranchAtom)
      throws InvalidSmilesException {
    if (pattern == null || pattern.length() == 0)
      throw new InvalidSmilesException("Empty atom definition");

    SmilesAtom newAtom = (atomSet == null ? molecule.addAtom()
        : isPrimitive ? atomSet.addPrimitive() : atomSet.addAtomOr());

    if (braceCount > 0)
      newAtom.selected = true;

    if (!checkLogic(molecule, pattern, newAtom, null, currentAtom, isPrimitive,
        isBranchAtom)) {

      int[] ret = new int[1];

      if (isBioSequence && pattern.length() == 1)
        pattern += ".0";
      char ch = pattern.charAt(0);

      int index = 0;
      boolean isNot = false;
      if (isSmarts && ch == '!') {
        ch = getChar(pattern, ++index);
        if (ch == '\0')
          throw new InvalidSmilesException("invalid '!'");
        newAtom.not = isNot = true;
      }

      int hydrogenCount = Integer.MIN_VALUE;
      int biopt = pattern.indexOf('.');
      if (biopt >= 0) {
        String name = pattern.substring(index, biopt);
        if (name.length() == 0)
          name = "*";
        if (name.length() > 1)
          newAtom.residueName = name.toUpperCase();
        else if (!name.equals("*"))
          newAtom.residueChar = name;
        name = pattern.substring(biopt + 1).toUpperCase();
        if ((biopt = name.indexOf("#")) >= 0) {
          getDigits(name, biopt + 1, ret);
          newAtom.elementNumber = (short) ret[0];
          name = name.substring(0, biopt);
        }
        if (name.length() == 0)
          name = "*";
        if (!name.equals("*"))
          newAtom.setAtomName(name);
        ch = '\0';
      }
      newAtom.setBioAtom(bioType);
      while (ch != '\0') {
        newAtom.setAtomName(isBioSequence ? "0" : "");
        if (Character.isDigit(ch)) {
          index = getDigits(pattern, index, ret);
          int mass = ret[0];
          if (mass == Integer.MIN_VALUE)
            throw new InvalidSmilesException("Non numeric atomic mass");
          if (getChar(pattern, index) == '?') {
            index++;
            mass = -mass; // or undefined
          }
          newAtom.setAtomicMass(mass);
        } else {
          switch (ch) {
          case '_': // $(...) nesting
            index = getDigits(pattern, index + 1, ret) + 1;
            if (ret[0] == Integer.MIN_VALUE)
              throw new InvalidSmilesException("Invalid SEARCH primitive: "
                  + pattern.substring(index));
            newAtom.iNested = ret[0];
            if (isBioSequence && isBracketed) {
              if (index != pattern.length())
                    throw new InvalidSmilesException("invalid characters: " + pattern.substring(index));
            }
            break;
          case '=':
            index = getDigits(pattern, index + 1, ret);
            newAtom.jmolIndex = ret[0];
            break;
          case '#':
            index = getDigits(pattern, index + 1, ret);
            newAtom.elementNumber = (short) ret[0];
            break;
          case '-':
          case '+':
            index = checkCharge(pattern, index, newAtom);
            break;
          case '@':
            molecule.haveAtomStereochemistry = true;
            index = checkChirality(pattern, index,
                molecule.patternAtoms[newAtom.index]);
            break;
          default:
            // SMARTS has ambiguities in terms of chaining without &.
            // H alone is "one H atom"
            // "!H" is "not hydrogen" but "!H2" is "not two attached hydrogens"
            // [Rh] could be rhodium or "Any ring atom with at least one implicit H atom"
            // [Ar] could be argon or "Any aliphatic ring atom"
            // There is no ambiguity, though, with [Rh3] or [Ar6] because
            // in those cases the number forces the single-character property
            // "h3" or "r6", thus forcing "R" or "A"
            // also, Jmol has Xx
            // We manage this by simply asserting that if & is not used and
            // the first two-letters of the expression could be interpreted
            // as a symbol, AND the next character is not a digit, then it WILL be interpreted as a symbol
            char nextChar = getChar(pattern, index + 1);
            String sym2 = pattern.substring(index + 1, index
                + (Character.isLowerCase(nextChar)
                    && (!isBracketed || !Character.isDigit(getChar(pattern,
                        index + 2))) ? 2 : 1));
            String symbol = Character.toUpperCase(ch) + sym2;
            boolean mustBeSymbol = true;
            boolean checkForPrimitive = (isBracketed && Character
                .isLetter(ch));
            if (checkForPrimitive) {
              if (!isNot && (isPrimitive ? atomSet : newAtom).hasSymbol) {
                // if we already have a symbol, and we aren't negating,
                // then this MUST be a property
                // because you can't have [C&O], but you can have [R&!O&!C]
                // We also allow [C&!O], though that's not particularly useful.
                mustBeSymbol = false;
              } else if (ch == 'H') {
                // only H if not H<n> or H1? 
                // 
                mustBeSymbol = !Character.isDigit(nextChar)
                    || getChar(pattern, index + 2) == '?';
              } else if ("DdhRrvXx".indexOf(ch) >= 0
                  && Character.isDigit(nextChar)) {
                // not a symbol if any of these are followed by a number 
                mustBeSymbol = false;
              } else if (!symbol.equals("A") && !symbol.equals("Xx")) {
                // check for two-character symbol, then one-character symbol
                mustBeSymbol = (Elements.elementNumberFromSymbol(symbol, true) > 0);
                if (!mustBeSymbol && sym2 != "") {
                  sym2 = "";
                  symbol = symbol.substring(0, 1);
                  mustBeSymbol = (Elements
                      .elementNumberFromSymbol(symbol, true) > 0);
                }
              }
            }
            if (mustBeSymbol) {
              if (!isBracketed && !isSmarts && !isBioSequence
                  && !SmilesAtom.allowSmilesUnbracketed(symbol)
                  || !newAtom.setSymbol(symbol = ch + sym2))
                throw new InvalidSmilesException("Invalid atom symbol: "
                    + symbol);
              if (isPrimitive)
                atomSet.hasSymbol = true;
              // indicates we have already assigned an atom number
              if (!symbol.equals("*"))
                molecule.parent.needAromatic = true;
              index += symbol.length();
            } else {
              index = getDigits(pattern, index + 1, ret);
              int val = ret[0];
              switch (ch) {
              default:
                throw new InvalidSmilesException("Invalid SEARCH primitive: "
                    + pattern.substring(index));
              case 'D':
                // default is 1
                newAtom.setDegree(val == Integer.MIN_VALUE ? 1 : val);
                break;
              case 'd':
                // default is 1
                newAtom
                    .setNonhydrogenDegree(val == Integer.MIN_VALUE ? 1 : val);
                break;
              case 'H':
                // default is 1
                hydrogenCount = (val == Integer.MIN_VALUE ? 1 : val);
                break;
              case 'h':
                // default > 1
                newAtom.setImplicitHydrogenCount(val == Integer.MIN_VALUE ? -1
                    : val);
                break;
              case 'R':
                if (val == Integer.MIN_VALUE)
                  val = -1; // R --> !R0; !R --> R0
                newAtom.setRingMembership(val);
                molecule.parent.needRingData = true;
                break;
              case 'r':
                if (val == Integer.MIN_VALUE) {
                  val = -1; // r --> !R0; !r --> R0
                  newAtom.setRingMembership(val);
                } else {
                  newAtom.setRingSize(val);
                  if (val > molecule.ringDataMax)
                    molecule.ringDataMax = val;
                }
                molecule.parent.needRingData = true;
                break;
              case 'v':
                // default 1
                newAtom.setValence(val == Integer.MIN_VALUE ? 1 : val);
                break;
              case 'X':
                // default 1
                newAtom.setConnectivity(val == Integer.MIN_VALUE ? 1 : val);
                break;
              case 'x':
                // default > 0
                newAtom
                    .setRingConnectivity(val == Integer.MIN_VALUE ? -1 : val);
                molecule.parent.needRingData = true;
                break;
              }
            }
          }
        }
        ch = getChar(pattern, index);
        if (isNot && ch != '\0')
          throw new InvalidSmilesException(
              "'!' may only involve one primitive.");
      }
      if (hydrogenCount == Integer.MIN_VALUE && isBracketed)
        hydrogenCount = Integer.MIN_VALUE + 1;
      newAtom.setExplicitHydrogenCount(hydrogenCount);
      // for stereochemistry only:
      molecule.patternAtoms[newAtom.index]
          .setExplicitHydrogenCount(hydrogenCount);
    }

    // Final check

    if (currentAtom != null && bond.bondType == SmilesBond.TYPE_NONE) {
      newAtom.notBondedIndex = currentAtom.index;
    }
    if (currentAtom != null && bond.bondType != SmilesBond.TYPE_NONE) {
      if (bond.bondType == SmilesBond.TYPE_UNKNOWN)
        bond.bondType = (isBioSequence && isBranchAtom ? SmilesBond.TYPE_BIO_PAIR
            : isSmarts || currentAtom.isAromatic() && newAtom.isAromatic() ? SmilesBond.TYPE_ANY
                : SmilesBond.TYPE_SINGLE);
      if (!isBracketed)
        bond.set(null, newAtom);
      if (branchLevel == 0 && 
          (bond.bondType == SmilesBond.TYPE_AROMATIC 
              || bond.bondType == SmilesBond.TYPE_BIO_PAIR))
        branchLevel++;
    }
    // if (Logger.debugging)
    // Logger.debug("new atom: " + newAtom);
    if (branchLevel == 0)
      molecule.lastChainAtom = newAtom;
    return newAtom;
  }

  /**
   * Parses a ring definition
   * 
   * @param molecule
   *          Resulting molecule
   * @param ringNum
   * @param currentAtom
   *          Current atom
   * @param bond 
   * @throws InvalidSmilesException
   */
  private void parseRing(SmilesSearch molecule, int ringNum,
                         SmilesAtom currentAtom, SmilesBond bond)
      throws InvalidSmilesException {

    // Ring management

    Integer r = Integer.valueOf(ringNum);
    SmilesBond bond0 = ringBonds.get(r);
    if (bond0 == null) {
      ringBonds.put(r, bond);
      return;
    }
    ringBonds.remove(r);
    // wnen the bond type is unknown, we go with:
    // (1) the other end, if defined there
    // (2) or if SMARTS, "ANY"
    // (3) or if both atoms are aromatic, "ANY"
    // (4) or "SINGLE"
    // we must check for C1......C/1....
    // in which case the "/" is referring to "second to first" not "first to second"
    switch (bond.bondType) {
    case SmilesBond.TYPE_UNKNOWN:
      bond.bondType = (bond0.bondType != SmilesBond.TYPE_UNKNOWN ? bond0.bondType
          : isSmarts || currentAtom.isAromatic()
              && bond0.getAtom1().isAromatic() ? SmilesBond.TYPE_ANY
              : SmilesBond.TYPE_SINGLE);
      break;
    case SmilesBond.TYPE_DIRECTIONAL_1:
      bond.bondType = SmilesBond.TYPE_DIRECTIONAL_2;
      break;
    case SmilesBond.TYPE_DIRECTIONAL_2:
      bond.bondType = SmilesBond.TYPE_DIRECTIONAL_1;
      break;
    }
    if (bond0.bondType != SmilesBond.TYPE_UNKNOWN
        && bond0.bondType != bond.bondType)
      throw new InvalidSmilesException("Incoherent bond type for ring");
    bond0.set(bond);
    currentAtom.bondCount--;
    bond0.setAtom2(currentAtom);
  }

  private int checkCharge(String pattern, int index, SmilesAtom newAtom)
      throws InvalidSmilesException {
    // Charge
    int len = pattern.length();
    char ch = pattern.charAt(index);
    int count = 1;
    ++index;
    if (index < len) {
      char nextChar = pattern.charAt(index);
      if (Character.isDigit(nextChar)) {
        int[] ret = new int[1];
        index = getDigits(pattern, index, ret);
        count = ret[0];
        if (count == Integer.MIN_VALUE)
          throw new InvalidSmilesException("Non numeric charge");
      } else {
        while (index < len && pattern.charAt(index) == ch) {
          index++;
          count++;
        }
      }
    }
    newAtom.setCharge(ch == '+' ? count : -count);
    return index;
  }

  private int checkChirality(String pattern, int index, SmilesAtom newAtom)
      throws InvalidSmilesException {
    int stereoClass = 0;
    int order = Integer.MIN_VALUE;
    int len = pattern.length();
    char ch;
    stereoClass = SmilesAtom.STEREOCHEMISTRY_DEFAULT;
    order = 1;
    if (++index < len) {
      switch (ch = pattern.charAt(index)) {
      case '@':
        order = 2;
        index++;
        break;
      case 'H':
        break;
      case 'A':
      case 'D':
      case 'E':
      case 'O':
      case 'S':
      case 'T':
        stereoClass = (index + 1 < len ? SmilesAtom.getChiralityClass(pattern
            .substring(index, index + 2)) : -1);
        index += 2;
        break;
      default:
        order = (Character.isDigit(ch) ? 1 : -1);
      }
      int pt = index;
      if (order == 1) {
        while (pt < len && Character.isDigit(pattern.charAt(pt)))
          pt++;
        if (pt > index) {
          try {
            order = Integer.parseInt(pattern.substring(index, pt));
          } catch (NumberFormatException e) {
            order = -1;
          }
          index = pt;
        }
      }
      if (order < 1 || stereoClass < 0)
        throw new InvalidSmilesException("Invalid stereochemistry descriptor");
    }
    newAtom.setChiralClass(stereoClass);
    newAtom.setChiralOrder(order);
    if (getChar(pattern, index) == '?') {
      Logger.info("Ignoring '?' in stereochemistry");
      index++;
    }
    return index;
  }

  private SmilesBond parseBond(SmilesSearch molecule, SmilesBond bondSet,
                               String pattern, SmilesBond bond,
                               SmilesAtom currentAtom, boolean isPrimitive,
                               boolean isBranchAtom)
      throws InvalidSmilesException {

    // pattern length will be 1 or 2 only, x or !x

    char ch = getChar(pattern, 0);
    if (ch == '.') {
      if (bond != null || bondSet != null)
        throw new InvalidSmilesException("invalid '.'");
      isBioSequence = (getChar(pattern, 1) == '~');
      return new SmilesBond(SmilesBond.TYPE_NONE, false);
    }
    if (ch == '+' && bondSet != null)
      throw new InvalidSmilesException("invalid '+'");

    SmilesBond newBond = (bondSet == null ? (bond == null ? new SmilesBond(
        currentAtom,
        null,
        (isBioSequence && currentAtom != null ? (isBranchAtom ? SmilesBond.TYPE_BIO_PAIR
            : SmilesBond.TYPE_BIO_SEQUENCE)
            : SmilesBond.TYPE_UNKNOWN), false)
        : bond)
        : isPrimitive ? bondSet.addPrimitive() : bondSet.addBondOr());

    if (ch != '\0'
        && !checkLogic(molecule, pattern, null, newBond, currentAtom,
            isPrimitive, false)) {
      boolean isBondNot = (ch == '!');
      if (isBondNot) {
        ch = getChar(pattern, 1);
        if (ch == '\0' || ch == '!')
          throw new InvalidSmilesException("invalid '!'");
      }
      int bondType = SmilesBond.getBondTypeFromCode(ch);
      if (bondType == SmilesBond.TYPE_RING)
        molecule.parent.needRingMemberships = true;
      if (currentAtom == null && bondType != SmilesBond.TYPE_NONE)
        throw new InvalidSmilesException("Bond without a previous atom");
      switch (bondType) {
      case SmilesBond.TYPE_ATROPISOMER_1:
      case SmilesBond.TYPE_ATROPISOMER_2:
        if (isBondNot) {
          isBondNot = false;
          bondType = (bondType == SmilesBond.TYPE_ATROPISOMER_1 ? SmilesBond.TYPE_ATROPISOMER_2 : SmilesBond.TYPE_ATROPISOMER_1);
        }
//        if (bondSet != null)
  //        bondSet.bondType = bondType;
        molecule.haveBondStereochemistry = true;
        break;
      case SmilesBond.TYPE_DIRECTIONAL_1:
      case SmilesBond.TYPE_DIRECTIONAL_2:
        molecule.haveBondStereochemistry = true;
        break;
      case SmilesBond.TYPE_AROMATIC:
        if (!isBioSequence)
          molecule.parent.needAromatic = true;
        break;
      case SmilesBond.TYPE_DOUBLE:
      case SmilesBond.TYPE_SINGLE:
        if (currentAtom.isAromatic())
          molecule.parent.needRingData = true;
        break;
      }
      newBond.set(bondType, isBondNot);
      // in the case of a bioSequence, we also mark the 
      // parent bond, especially if NOT
      if (isBioSequence && bondSet != null) 
        bondSet.set(bondType, isBondNot);
    }
    return newBond;
  }

  private boolean checkLogic(SmilesSearch molecule, String pattern,
                             SmilesAtom atom, SmilesBond bond,
                             SmilesAtom currentAtom, boolean isPrimitive,
                             boolean isBranchAtom)
      throws InvalidSmilesException {
    int pt = pattern.indexOf(',');
    int len = pattern.length();
    while (true) {
      boolean haveOr = (pt > 0);
      if (haveOr && !isSmarts || pt == 0)
        break;
      String props = "";
      pt = pattern.indexOf(';');
      if (pt >= 0) {
        if (!isSmarts || pt == 0)
          break;
        props = "&" + pattern.substring(pt + 1);
        pattern = pattern.substring(0, pt);
        if (!haveOr) {
          pattern += props;
          props = "";
        }
      }
      int index = 0;
      if (haveOr) {
        pattern += ",";
        while ((pt = pattern.indexOf(',', index)) > 0 && pt <= len) {
          String s = pattern.substring(index, pt) + props;
          if (s.length() == 0)
            throw new InvalidSmilesException("missing "
                + (bond == null ? "atom" : "bond") + " token");
          if (bond == null)
            parseAtom(molecule, atom, s, null, null, true, false, isBranchAtom);
          else
            parseBond(molecule, bond, s, null, currentAtom, false, false);
          index = pt + 1;
        }
      } else if ((pt = pattern.indexOf('&')) >= 0 || bond != null && len > 1
          && !isPrimitive) {
        // process primitive
        if (!isSmarts || pt == 0)
          break;
        if (bond != null && pt < 0) {
          // bonds are simpler, because they have only one character
          if (len > 1) {
            StringBuffer sNew = new StringBuffer();
            for (int i = 0; i < len;) {
              char ch = pattern.charAt(i++);
              sNew.append(ch);
              if (ch != '!' && i < len)
                sNew.append('&');
            }
            pattern = sNew.toString();
            len = pattern.length();
          }
        }
        pattern += "&";
        while ((pt = pattern.indexOf('&', index)) > 0 && pt <= len) {
          String s = pattern.substring(index, pt) + props;
          if (bond == null)
            parseAtom(molecule, atom, s, null, null, true, true, isBranchAtom);
          else
            parseBond(molecule, bond, s, null, currentAtom, true, false);
          index = pt + 1;
        }
      } else {
        return false;
      }
      return true;
    }
    char ch = pattern.charAt(pt);
    throw new InvalidSmilesException(isSmarts ? "invalid placement for '" + ch
        + "'" : "[" + ch + "] notation only valid with SMARTS, not SMILES");
  }

  private static String getSubPattern(String pattern, int index, char ch)
      throws InvalidSmilesException {
    char ch2;
    int margin = 1;
    switch (ch) {
    case '[':
      ch2 = ']';
      break;
    case '"':
    case '%':
      ch2 = ch;
      break;
    case '(':
      ch2 = ')';
      break;
    default:
      ch2 = ch;
      margin = 0;
    }
    int len = pattern.length();
    int pCount = 1;
    for (int pt = index + 1; pt < len; pt++) {
      char ch1 = pattern.charAt(pt);
      if (ch1 == ch2) {
        pCount--;
        if (pCount == 0)
          return pattern.substring(index + margin, pt + 1 - margin);
      } else if (ch1 == ch) {
        pCount++;
      }
    }
    throw new InvalidSmilesException("Unmatched " + ch);
  }

  private static char getChar(String pattern, int i) {
    return (i < pattern.length() ? pattern.charAt(i) : '\0');
  }

  /**
   * 
   * @param pattern
   * @param index
   * @param ret
   * @return  pointer to the character AFTER the digits
   */
  private static int getDigits(String pattern, int index, int[] ret) {
    int pt = index;
    int len = pattern.length();
    while (pt < len && Character.isDigit(pattern.charAt(pt)))
      pt++;
    try {
      ret[0] = Integer.parseInt(pattern.substring(index, pt));
    } catch (NumberFormatException e) {
      ret[0] = Integer.MIN_VALUE;
    }
    return pt;
  }

  private static int skipTo(String pattern, int index, char ch0) {
    int pt = index;
    char ch;
    while ((ch = getChar(pattern, ++pt)) != ch0 && ch != '\0') {
    }
    return (ch == '\0' ? -1 : pt);
  }

  static String getRingPointer(int i) {
    return (i < 10 ? "" + i : i < 100 ? "%" + i : "%(" + i + ")");
  }

  /**
   * 
   * @param pattern
   * @param isSmarts
   * @return  comments and white space removed, also ^^ to '
   */
  private static String cleanPattern(String pattern, boolean isSmarts) {
    pattern = pattern.replaceAll("\\s", "").replaceAll("\\^\\^","'");
    int i = 0;
    int i2 = 0;
    while ((i = pattern.indexOf("//*")) >= 0
        && (i2 = pattern.indexOf("*//")) >= i)
      pattern = pattern.substring(0, i) + pattern.substring(i2 + 3);
    pattern = pattern.replaceAll("//", "");
    return pattern;
  }

}
