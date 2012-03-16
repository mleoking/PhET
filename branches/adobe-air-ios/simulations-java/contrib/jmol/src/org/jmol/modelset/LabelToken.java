/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-10-14 12:33:20 -0500 (Sun, 14 Oct 2007) $
 * $Revision: 8408 $

 *
 * Copyright (C) 2003-2005  The Jmol Development Team
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

package org.jmol.modelset;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.vecmath.Tuple3f;

import org.jmol.util.TextFormat;
import org.jmol.script.Token;
import org.jmol.viewer.Viewer;

public class LabelToken {

  /*
   * by Bob Hanson, 5/28/2009
   * 
   * a compiler for the atom label business.
   * 
   * Prior to this, once for every atom, twice for every bond, and 2-4 times for every
   * measurement we were scanning the format character by character. And if data were
   * involved, then calls were made for every atom to find the data set and return its
   * value. Now you can still do that, but the Jmol code doesn't. 
   * 
   * Instead, we now first compile a set of tokens -- either pure text or some
   * sort of %xxxx business. Generally we would alternate between these, so the
   * compiler is set up to initialize an array that has 2n+1 elements, where n is the
   * number of % signs in the string. This is guaranteed to be more than really necessary.
   * 
   * Because we are working with tokens, we can go beyond the limiting A-Za-z business
   * that we had before. That still works, but now we can have any standard token be
   * used in brackets:
   * 
   *   %n.m[xxxxx]
   * 
   * This complements the 
   * 
   *   %n.m{xxxxx}
   *   
   * used for data. The brackets make for a nice-looking format:
   * 
   * 
   *  print {*}.bonds.label("%6[atomName]1 - %6[atomName]2  %3ORDER  %6.2LENGTH")
   * 
   * [Note that the %ORDER and %LENGTH variables are bond labeling options, and 
   *  the 1 and 2 after %[xxx] indicate which atom in involved.
   * 
   * 
   */

  private String text;
  private String key;
  private Object data;
  private int tok;
  private int pt = -1;
  private char ch1;
  private int width;
  private int precision = Integer.MAX_VALUE;
  private boolean alignLeft;
  private boolean zeroPad;
  private boolean intAsFloat;

  // do not change array order without changing string order as well
  // new tokens can be added to the list at the end
  // and then also added in appendTokenValue()
  // and also in Eval, to atomProperty()

  final private static String labelTokenParams = "AaBbCcDEefGgIiLlMmNnoPpQqRrSsTtUuVvWXxYyZz%%%gqW";
  final private static int[] labelTokenIds = {
      /* 'A' */Token.altloc,
      /* 'a' */Token.atomname,
      /* 'B' */Token.atomtype,
      /* 'b' */Token.temperature,
      /* 'C' */Token.formalcharge,
      /* 'c' */Token.chain,
      /* 'D' */Token.atomindex,
      /* 'E' */Token.insertion,
      /* 'e' */Token.element,
      /* 'f' */Token.phi,
      /* 'G' */Token.groupindex,
      /* 'g' */'g', //getSelectedGroupIndexWithinChain()
      /* 'I' */Token.ionic,
      /* 'i' */Token.atomno,
      /* 'L' */Token.polymerlength,
      /* 'l' */Token.elemno,
      /* 'M' */Token.model,
      /* 'm' */Token.group1,
      /* 'N' */Token.molecule,
      /* 'n' */Token.group,
      /* 'o' */Token.symmetry,
      /* 'P' */Token.partialcharge,
      /* 'p' */Token.psi,
      /* 'Q' */'Q', //occupancy 0.0 to 1.0
      /* 'q' */Token.occupancy,
      /* 'R' */Token.resno,
      /* 'r' */'r',
      /* 'S' */Token.site,
      /* 's' */Token.chain,
      /* 'T' */Token.straightness,
      /* 't' */Token.temperature,
      /* 'U' */Token.identify,
      /* 'u' */Token.surfacedistance,
      /* 'V' */Token.vanderwaals,
      /* 'v' */Token.vibxyz,
      /* 'W' */'W', // identifier and XYZ coord
      /* 'X' */Token.fracx,
      /* 'x' */Token.atomx,
      /* 'Y' */Token.fracy,
      /* 'y' */Token.atomy,
      /* 'Z' */Token.fracz,
      /* 'z' */Token.atomz,

      // not having letter equivalents:

      //new for Jmol 11.9.5:
      Token.backbone, Token.cartoon, Token.dots, Token.ellipsoid,
      Token.geosurface, Token.halo, Token.meshRibbon, Token.ribbon,
      Token.rocket, Token.star, Token.strands, Token.trace,

      Token.adpmax, Token.adpmin, Token.atomid, Token.bondcount, Token.color,
      Token.groupid, Token.covalent, Token.file, Token.format, Token.label,
      Token.mass, Token.modelindex, Token.eta, Token.omega, Token.polymer, Token.property,
      Token.radius, Token.selected, Token.shape, Token.sequence,
      Token.spacefill, Token.structure, Token.substructure, Token.strucno,
      Token.strucid, Token.symbol, Token.theta, Token.unitx, Token.unity,
      Token.unitz, Token.valence, Token.vibx, Token.viby, Token.vibz,
      Token.volume, Token.unitxyz, Token.fracxyz, Token.xyz, Token.fuxyz,
      Token.fux, Token.fuy, Token.fuz,

  };

  private static boolean isLabelPropertyTok(int tok) {
    for (int i = labelTokenIds.length; --i >= 0;)
      if (labelTokenIds[i] == tok)
        return true;
    return false;
  }

  public static final String STANDARD_LABEL = "%[identify]";

  private final static String twoCharLabelTokenParams = "fuv";

  private final static int[] twoCharLabelTokenIds = { Token.fracx, Token.fracy,
      Token.fracz, Token.unitx, Token.unity, Token.unitz, Token.vibx,
      Token.viby, Token.vibz, };

  private LabelToken(String text) {
    this.text = text;
  }

  private LabelToken(int pt) {
    this.pt = pt;
  }

  /**
   * Compiles a set of tokens for each primitive element of a 
   * label. This is the efficient way to create a set of labels. 
   * 
   * @param viewer
   * @param strFormat
   * @param chAtom
   * @param htValues
   * @return   array of tokens
   */
  public static LabelToken[] compile(Viewer viewer, String strFormat,
                                     char chAtom, Map<String, Object> htValues) {
    if (strFormat == null || strFormat.length() == 0)
      return null;
    if (strFormat.indexOf("%") < 0 || strFormat.length() < 2)
      return new LabelToken[] { new LabelToken(strFormat) };
    int n = 0;
    int ich = -1;
    int cch = strFormat.length();
    while (++ich < cch && (ich = strFormat.indexOf('%', ich)) >= 0)
      n++;
    LabelToken[] tokens = new LabelToken[n * 2 + 1];
    int ichPercent;
    int i = 0;
    for (ich = 0; (ichPercent = strFormat.indexOf('%', ich)) >= 0;) {
      if (ich != ichPercent)
        tokens[i++] = new LabelToken(strFormat.substring(ich, ichPercent));
      LabelToken lt = tokens[i++] = new LabelToken(ichPercent);
      viewer.autoCalculate(lt.tok);
      ich = setToken(viewer, strFormat, lt, cch, chAtom, htValues);
    }
    if (ich < cch)
      tokens[i++] = new LabelToken(strFormat.substring(ich));
    return tokens;
  }

  //////////// label formatting for atoms, bonds, and measurements ///////////

  public static String formatLabel(Viewer viewer, Atom atom, String strFormat) {
    if (strFormat == null || strFormat.length() == 0)
      return null;
    LabelToken[] tokens = compile(viewer, strFormat, '\0', null);
    return formatLabel(viewer, atom, tokens, '\0', null);    
  }

  /**
   * returns a formatted string based on the precompiled label tokens
   * 
   * @param viewer
   * @param atom
   * @param tokens
   * @param chAtom
   * @param indices
   * @return   formatted string
   */
  public static String formatLabel(Viewer viewer, Atom atom,
                                   LabelToken[] tokens, char chAtom,
                                   int[] indices) {
    if (atom == null)
      return null;
    StringBuffer strLabel = (chAtom > '0' ? null : new StringBuffer());
    if (tokens != null)
      for (int i = 0; i < tokens.length; i++) {
        LabelToken t = tokens[i];
        if (t == null)
          break;
        if (chAtom > '0' && t.ch1 != chAtom)
          continue;
        if (t.tok <= 0 || t.key != null) {
          if (strLabel != null) {
            strLabel.append(t.text);
            if (t.ch1 != '\0')
              strLabel.append(t.ch1);
          }
        } else {
          appendAtomTokenValue(viewer, atom, t, strLabel, indices);
        }
      }
    return (strLabel == null ? null : strLabel.toString().intern());
  }

  public static Map<String, Object> getBondLabelValues() {
    Map<String, Object> htValues = new Hashtable<String, Object>();
    htValues.put("#", "");
    htValues.put("ORDER", "");
    htValues.put("TYPE", "");
    htValues.put("LENGTH", new Float(0));
    htValues.put("ENERGY", new Float(0));
    return htValues;
  }

  public static String formatLabel(Viewer viewer, Bond bond,
                                   LabelToken[] tokens,
                                   Map<String, Object> values, int[] indices) {
    values.put("#", "" + (bond.index + 1));
    values.put("ORDER", "" + bond.getOrderNumberAsString());
    values.put("TYPE", bond.getOrderName());
    values.put("LENGTH", new Float(bond.atom1.distance(bond.atom2)));
    values.put("ENERGY", new Float(bond.getEnergy()));
    setValues(tokens, values);
    formatLabel(viewer, bond.atom1, tokens, '1', indices);
    formatLabel(viewer, bond.atom2, tokens, '2', indices);
    return getLabel(tokens);
  }

  public static String formatLabel(Viewer viewer, Measurement measurement,
                                   String label, float value, String units) {
    Map<String, Object> htValues = new Hashtable<String, Object>();
    htValues.put("#", "" + (measurement.getIndex() + 1));
    htValues.put("VALUE", new Float(value));
    htValues.put("UNITS", units);
    LabelToken[] tokens = compile(viewer, label, '\1', htValues);
    setValues(tokens, htValues);
    Atom[] atoms = measurement.modelSet.atoms;
    int[] indices = measurement.getCountPlusIndices();
    for (int i = indices[0]; i >= 1; --i)
      if (indices[i] >= 0)
        formatLabel(viewer, atoms[indices[i]], tokens, (char) ('0' + i), null);
    label = getLabel(tokens);
    return (label == null ? "" : label);
  }

  public static void setValues(LabelToken[] tokens, Map<String, Object> values) {
    for (int i = 0; i < tokens.length; i++) {
      LabelToken lt = tokens[i];
      if (lt == null)
        break;
      if (lt.key == null)
        continue;
      Object value = values.get(lt.key);
      lt.text = (value instanceof Float ? lt.format(((Float) value)
          .floatValue(), null, null) : lt.format(Float.NaN, (String) value,
          null));
    }
  }

  public static String getLabel(LabelToken[] tokens) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < tokens.length; i++) {
      LabelToken lt = tokens[i];
      if (lt == null)
        break;
      sb.append(lt.text);
    }
    return sb.toString();
  }

  /////////////////// private methods
  
  /**
   * sets a label token based on a label string
   * 
   * @param viewer
   * @param strFormat
   * @param lt
   * @param cch
   * @param chAtom
   * @param htValues
   * @return         new position
   */
  private static int setToken(Viewer viewer, String strFormat, LabelToken lt,
                              int cch, int chAtom, Map<String, Object> htValues) {
    int ich = lt.pt + 1;
    char ch;
    if (strFormat.charAt(ich) == '-') {
      lt.alignLeft = true;
      ++ich;
    }
    if (ich < cch && strFormat.charAt(ich) == '0') {
      lt.zeroPad = true;
      ++ich;
    }
    while (ich < cch && Character.isDigit(ch = strFormat.charAt(ich))) {
      lt.width = (10 * lt.width) + (ch - '0');
      ++ich;
    }
    lt.precision = Integer.MAX_VALUE;
    boolean isNegative = false;
    if (ich < cch && strFormat.charAt(ich) == '.') {
      ++ich;
      if (ich < cch && (ch = strFormat.charAt(ich)) == '-') {
        isNegative = true;
        ++ich;
      }
      if (ich < cch && Character.isDigit(ch = strFormat.charAt(ich))) {
        lt.precision = ch - '0';
        if (isNegative)
          lt.precision = -1 - lt.precision;
        ++ich;
      }
    }
    if (ich < cch && htValues != null) {
      Iterator<String> keys = htValues.keySet().iterator();
      while (keys.hasNext()) {
        String key = keys.next();
        if (strFormat.indexOf(key) == ich) {
          lt.key = key;
          return ich + key.length();
        }
      }
    }
    if (ich < cch)
      switch (ch = strFormat.charAt(ich++)) {
      case '%':
        lt.text = "%";
        return ich;
      case '[':
        int ichClose = strFormat.indexOf(']', ich);
        if (ichClose < ich) {
          ich = cch;
          break;
        }
        String propertyName = strFormat.substring(ich, ichClose).toLowerCase();
        if (propertyName.startsWith("property_")) {
          lt.text = propertyName;
          lt.tok = Token.data;
          lt.data = viewer.getDataFloat(lt.text);
        } else {
          Token token = Token.getTokenFromName(propertyName);
          if (token != null && isLabelPropertyTok(token.tok))
            lt.tok = token.tok;
        }
        ich = ichClose + 1;
        break;
      case '{':
        // label %{altName}
        // client property name deprecated in 12.1.22
        // but this can be passed to Jmol from the reader
        // as an auxiliaryInfo array or '\n'-delimited string
        int ichCloseBracket = strFormat.indexOf('}', ich);
        if (ichCloseBracket < ich) {
          ich = cch;
          break;
        }
        lt.text = strFormat.substring(ich, ichCloseBracket);
        lt.data = viewer.getDataFloat(lt.text);
        if (lt.data == null) {
          lt.data = viewer.getData(lt.text);
          if (lt.data instanceof Object[]) {
            lt.data = ((Object[]) lt.data)[1];
            if (lt.data instanceof String)
              lt.data = TextFormat.split((String) lt.data, '\n');
          }
          if (!(lt.data instanceof String[]))
            lt.data = null;
        }
        lt.tok = (lt.data == null ? Token.string : Token.data);
        ich = ichCloseBracket + 1;
        break;
      default:
        int i,
        i1;
        if (ich < cch && (i = twoCharLabelTokenParams.indexOf(ch)) >= 0
            && (i1 = "xyz".indexOf(strFormat.charAt(ich))) >= 0) {
          lt.tok = twoCharLabelTokenIds[i * 3 + i1];
          ich++;
        } else if ((i = labelTokenParams.indexOf(ch)) >= 0) {
          lt.tok = labelTokenIds[i];
        }
      }
    lt.text = strFormat.substring(lt.pt, ich);
    if (ich < cch && chAtom != '\0'
        && Character.isDigit(ch = strFormat.charAt(ich))) {
      ich++;
      lt.ch1 = ch;
      if (ch != chAtom && chAtom != '\1')
        lt.tok = 0;
    }
    return ich;
  }

  private static void appendAtomTokenValue(Viewer viewer, Atom atom,
                                           LabelToken t, StringBuffer strLabel,
                                           int[] indices) {
    String strT = null;
    float floatT = Float.NaN;
    Tuple3f ptT = null;
    try {
      switch (t.tok) {

      // special cases only for labels 

      case Token.atomindex:
        strT = "" + (indices == null ? atom.index : indices[atom.index]);
        break;
      case Token.color:
        ptT = Atom.atomPropertyTuple(atom, t.tok);
        break;
      case Token.data:
        if (t.data != null) {
          if (t.data instanceof float[])
            floatT = ((float[]) t.data)[atom.index];
          else if (t.data instanceof String[]) {
            String[] sdata = (String[]) t.data;
            strT = (atom.index < sdata.length ? sdata[atom.index] : "");
          }
        }
        break;
      case Token.formalcharge:
        int formalCharge = atom.getFormalCharge();
        if (formalCharge > 0)
          strT = "" + formalCharge + "+";
        else if (formalCharge < 0)
          strT = "" + -formalCharge + "-";
        else
          strT = "";
        break;
      case 'g':
        strT = "" + atom.getSelectedGroupIndexWithinChain();
        break;
      case Token.model:
        strT = atom.getModelNumberForLabel();
        break;
      case Token.occupancy:
        strT = "" + Atom.atomPropertyInt(atom, t.tok);
        break;
      case 'Q':
        floatT = atom.getOccupancy100() / 100f;
        break;
      case Token.radius:
        floatT = Atom.atomPropertyFloat(viewer, atom, t.tok);
        break;
      case 'r':
        strT = atom.getSeqcodeString();
        break;
      case Token.strucid:
        strT = atom.getStructureId();
        break;
      case Token.strucno:
        int id = atom.getStrucNo();
        strT = (id <= 0 ? "" : "" + id);
        break;
      case Token.straightness:
        floatT = atom.getGroupParameter(Token.straightness);
        if (Float.isNaN(floatT))
          strT = "null";
        break;
      case Token.string:
        // label %{altName}
        strT = viewer.getModelAtomProperty(atom, t.text.substring(2, t.text
            .length() - 1));
        break;
      case Token.structure:
      case Token.substructure:
        strT = Atom.atomPropertyString(viewer, atom, t.tok);
        break;
      case 'W':
        strT = atom.getIdentityXYZ(false);
        break;

      // standard 

      default:
        switch (t.tok & Token.PROPERTYFLAGS) {
        case Token.intproperty:
          if (t.intAsFloat)
            floatT = Atom.atomPropertyInt(atom, t.tok);
          else
            strT = "" + Atom.atomPropertyInt(atom, t.tok);
          break;
        case Token.floatproperty:
          floatT = Atom.atomPropertyFloat(viewer, atom, t.tok);
          break;
        case Token.strproperty:
          strT = Atom.atomPropertyString(viewer, atom, t.tok);
          break;
        case Token.atomproperty:
          ptT = Atom.atomPropertyTuple(atom, t.tok);
        default:
          // any dual case would be here -- must handle specially
        }
      }
    } catch (IndexOutOfBoundsException ioobe) {
      floatT = Float.NaN;
      strT = null;
      ptT = null;
    }
    strT = t.format(floatT, strT, ptT);
    if (strLabel == null)
      t.text = strT;
    else
      strLabel.append(strT);
  }

  private String format(float floatT, String strT, Tuple3f ptT) {
    if (!Float.isNaN(floatT)) {
      return TextFormat.format(floatT, width, precision, alignLeft, zeroPad);
    } else if (strT != null) {
      return TextFormat.format(strT, width, precision, alignLeft, zeroPad);
    } else if (ptT != null) {
      if (width == 0 && precision == Integer.MAX_VALUE) {
        width = 6;
        precision = 2;
      }
      return TextFormat.format(ptT.x, width, precision, false, false)
          + TextFormat.format(ptT.y, width, precision, false, false)
          + TextFormat.format(ptT.z, width, precision, false, false);
    } else {
      return text;
    }
  }

}
