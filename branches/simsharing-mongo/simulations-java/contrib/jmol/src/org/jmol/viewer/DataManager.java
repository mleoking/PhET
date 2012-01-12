/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-10-03 20:53:36 -0500 (Wed, 03 Oct 2007) $
 * $Revision: 8351 $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development Team
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
package org.jmol.viewer;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.jmol.modelset.AtomCollection;
import org.jmol.script.Token;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Elements;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Parser;


/*
 * a class for storing and retrieving user data,
 * including atom-related and color-related data
 * 
 */

class DataManager {

  private Map<String, Object[]> dataValues = new Hashtable<String, Object[]>();

  Viewer viewer;
  DataManager(Viewer viewer) {
    this.viewer = viewer;
  }

  void clear() {
    dataValues.clear();
  }
  
  void setData(String type, Object[] data, int atomCount, int matchField,
               int matchFieldColumnCount, int field, int fieldColumnCount) {
    //Eval
    /*
     * data[0] -- label
     * data[1] -- string or float[] or float[][]
     * data[2] -- selection bitset or int[] atomMap when field > 0
     * 
     * matchField = data must match atomNo in this column, >= 1
     * field = column containing the data, >= 1:
     *   0 ==> values are a simple list; clear the data
     *   Integer.MAX_VALUE ==> values are a simple list; don't clear the data
     *   Integer.MIN_VALUE ==> one SINGLE data value should be used for all selected atoms
     */
    if (type == null) {
      clear();
      return;
    }
    type = type.toLowerCase();
    if (type.equals("element_vdw")) {
      String stringData = ((String) data[1]).trim();
      if (stringData.length() == 0) {
        userVdwMars = null;
        userVdws = null;
        bsUserVdws = null;
        return;
      }
      if (bsUserVdws == null)
        setUserVdw(defaultVdw);
      Parser.parseFloatArrayFromMatchAndField(stringData, bsUserVdws, 1, 0,
          (int[]) data[2], 2, 0, userVdws, 1);
      for (int i = userVdws.length; --i >= 0;)
        userVdwMars[i] = (int) (userVdws[i] * 1000);
      return;
    }
    if (data[2] != null && atomCount > 0) {
      boolean createNew = (matchField != 0 || field != Integer.MIN_VALUE
          && field != Integer.MAX_VALUE);
      Object[] oldData = dataValues.get(type);
      BitSet bs;
      float[] f = (oldData == null || createNew ? new float[atomCount]
          : ArrayUtil.ensureLength(((float[]) oldData[1]), atomCount));

      // check to see if the data COULD be interpreted as a string of float values
      // and if so, do that. This pre-fetches the tokens in that case.

      String stringData = (data[1] instanceof String ? (String) data[1] : null);
      float[] floatData = (data[1] instanceof float[] ? (float[]) data[1] : null);
      String[] strData = null;
      if (field == Integer.MIN_VALUE
          && (strData = Parser.getTokens(stringData)).length > 1)
        field = 0;

      if (field == Integer.MIN_VALUE) {
        // set the selected data elements to a single value
        bs = (BitSet) data[2];
        Parser.setSelectedFloats(Parser.parseFloat(stringData), bs, f);
      } else if (field == 0 || field == Integer.MAX_VALUE) {
        // just get the selected token values
        bs = (BitSet) data[2];
        if (floatData != null) {
          for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) 
            f[i] = floatData[i];
        } else {
          Parser.parseFloatArray(strData == null ? Parser.getTokens(stringData)
            : strData, bs, f);
        }
      } else if (matchField <= 0) {
        // get the specified field >= 1 for the selected atoms
        bs = (BitSet) data[2];
        Parser.parseFloatArrayFromMatchAndField(stringData, bs, 0, 0, null,
            field, fieldColumnCount, f, 1);
      } else {
        // get the selected field, with an integer match in a specified field
        // in this case, bs is created and indicates which data points were set
        int[] iData = (int[]) data[2];
        Parser.parseFloatArrayFromMatchAndField(stringData, null, matchField,
            matchFieldColumnCount, iData, field, fieldColumnCount, f, 1);
        bs = new BitSet();
        for (int i = iData.length; --i >= 0;)
          if (iData[i] >= 0)
            bs.set(iData[i]);
      }
      if (oldData != null && oldData[2] instanceof BitSet && !createNew)
        bs.or((BitSet) (oldData[2]));
      data[2] = bs;
      data[1] = f;
      if (type.indexOf("property_atom.") == 0) {
        int tok = Token.getSettableTokFromString(type = type.substring(14));
        if (tok == Token.nada) {
          Logger.error("Unknown atom property: " + type);
          return;
        }
        int nValues = bs.cardinality();
        float[] fValues = new float[nValues];
        for (int n = 0, i = bs.nextSetBit(0); n < nValues; i = bs
            .nextSetBit(i + 1))
          fValues[n++] = f[i];
        viewer.setAtomProperty(bs, tok, 0, 0, null, fValues, null);
        return;
      }
    }
    dataValues.put(type, data);
  }

  Object[] getData(String type) {
    if (dataValues == null || type == null)
      return null;
    if (type.equalsIgnoreCase("types")) {
      String[] info = new String[2];
      info[0] = "types";
      info[1] = "";
      int n = 0;
      Iterator<String> e = dataValues.keySet().iterator();
      while (e.hasNext())
        info[1] += (n++ > 0 ? "\n" : "") + e.next();
      return info;
    }
    return dataValues.get(type);
  }

  float[] getDataFloat(String label) {
    if (dataValues == null)
      return null;
    Object[] data = getData(label);
    if (data == null || !(data[1] instanceof float[]))
      return null;
    return (float[]) data[1];
  }

  float getDataFloat(String label, int atomIndex) {
    if (dataValues != null) {
      Object[] data = getData(label);
      if (data != null && data[1] instanceof float[]) {
        float[] f = (float[]) data[1];
        if (atomIndex < f.length)
          return f[atomIndex];
      }
    }
    return Float.NaN;
  }

  float[][] getDataFloat2D(String label) {
    if (dataValues == null)
      return null;
    Object[] data = getData(label);
    if (data == null || !(data[1] instanceof float[][]))
      return null;
    return (float[][]) data[1];
  }

  float[][][] getDataFloat3D(String label) {
    if (dataValues == null)
      return null;
    Object[] data = getData(label);
    if (data == null || !(data[1] instanceof float[][][]))
      return null;
    return (float[][][]) data[1];
  }

  void deleteModelAtoms(int firstAtomIndex, int nAtoms, BitSet bsDeleted) {
    if (dataValues == null)
      return;
    Iterator<String> e = dataValues.keySet().iterator();
    while (e.hasNext()) {
      String name = e.next();
      if (name.indexOf("property_") == 0) {
        Object[] obj = dataValues.get(name);
        BitSetUtil.deleteBits((BitSet) obj[2], bsDeleted);
        if (obj[1] instanceof float[]) {
          obj[1] = ArrayUtil.deleteElements(obj[1], firstAtomIndex, nAtoms);
        } else if (obj[1] instanceof float[][]){
          obj[1] = ArrayUtil.deleteElements(obj[1], firstAtomIndex, nAtoms);
        } else {
          // is there anything else??
        }
      }
    }    
  }

  void getDataState(StringBuffer state, StringBuffer sfunc, String atomProps) {
    if (dataValues == null)
      return;
    Iterator<String> e = dataValues.keySet().iterator();
    StringBuffer sb = new StringBuffer();
    int n = 0;
    if (atomProps.length() > 0) {
      n = 1;
      sb.append(atomProps);
    }
    while (e.hasNext()) {
      String name = e.next();
      if (name.indexOf("property_") == 0) {
        n++;
        Object[] obj = dataValues.get(name);
        Object data = obj[1];
        if (data instanceof float[]) {
          viewer.getAtomicPropertyState(sb, AtomCollection.TAINT_MAX, 
              (BitSet) obj[2], 
              name, (float[]) data);
          sb.append("\n");
        } else {
          sb.append("\n").append(Escape.encapsulateData(name, data));
        }
      } else if (name.indexOf("data2d") == 0) {
        Object data = dataValues.get(name)[1];
        if (data instanceof float[][]) {
          n++;
          sb.append("\n").append(Escape.encapsulateData(name, data));
        }
      }
    }
    
    if (userVdws != null) {
      String info = getDefaultVdwNameOrData(JmolConstants.VDW_USER, bsUserVdws);
      if (info.length() > 0) {
        n++;
        sb.append(info);
      }
    }
    
    if (n == 0)
      return;
    if (sfunc != null)
      state.append("function _setDataState() {\n");
    state.append(sb);  
    if (sfunc != null) {
      sfunc.append("  _setDataState;\n");
      state.append("}\n\n");
    }
  }

  private float[] userVdws;
  int[] userVdwMars;
  int defaultVdw = JmolConstants.VDW_JMOL;
  BitSet bsUserVdws;
  
  private void setUserVdw(int iMode) {
    userVdwMars = new int[Elements.elementNumberMax];
    userVdws = new float[Elements.elementNumberMax];
    bsUserVdws = new BitSet();
    if (iMode == JmolConstants.VDW_USER)
      iMode = JmolConstants.VDW_JMOL;
    for (int i = 1; i < Elements.elementNumberMax; i++) {
      userVdwMars[i] = JmolConstants.getVanderwaalsMar(i, iMode);
      userVdws[i] = userVdwMars[i] / 1000f;
    }
  }

  void setDefaultVdw(int iType) {
    // only allowed types here are VDW_JMOL, VDW_BABEL, VDW_RASMOL, VDW_USER, VDW_AUTO
    switch (iType) {
    case JmolConstants.VDW_JMOL:
    case JmolConstants.VDW_BABEL:
    case JmolConstants.VDW_RASMOL:
    case JmolConstants.VDW_AUTO:
      break;
    default:
      iType = JmolConstants.VDW_JMOL;
    }
    if (iType != defaultVdw && iType == JmolConstants.VDW_USER  
        && bsUserVdws == null)
      setUserVdw(defaultVdw);
    defaultVdw = iType;    
  }

  String getDefaultVdwNameOrData(int iType, BitSet bs) {
    // called by getDataState and via Viewer: Eval.calculate,
    // Eval.show, StateManager.getLoadState, Viewer.setDefaultVdw
    switch (iType) {
    case Integer.MIN_VALUE:
      // iMode Integer.MIN_VALUE -- just the name
      return JmolConstants.vdwLabels[defaultVdw];
    case Integer.MAX_VALUE:
      // iMode = Integer.MAX_VALUE -- user, only selected
      if ((bs = bsUserVdws) == null)
        return "";
      iType = JmolConstants.VDW_USER;
      break;
    case JmolConstants.VDW_AUTO:
    case JmolConstants.VDW_UNKNOWN:
      iType = defaultVdw;
      break;
    }
    if (iType == JmolConstants.VDW_USER && bsUserVdws == null) {
      setUserVdw(defaultVdw);
    }
    StringBuffer sb = new StringBuffer(JmolConstants.vdwLabels[iType] + "\n");
    boolean isAll = (bs == null);
    int i0 = (isAll ? 1 : bs.nextSetBit(0));
    int i1 = (isAll ? Elements.elementNumberMax : bs.length());
    for (int i = i0; i < i1 && i >= 0; i = (isAll ? i + 1 : bs
        .nextSetBit(i + 1)))
      sb.append(i).append('\t').append(
          iType == JmolConstants.VDW_USER ? userVdws[i] : JmolConstants
              .getVanderwaalsMar(i, iType) / 1000f).append('\t').append(
          Elements.elementSymbolFromNumber(i)).append('\n');
    return (bs == null ? sb.toString() : "\n  DATA \"element_vdw\"\n"
        + sb.append("  end \"element_vdw\";\n\n").toString());
  }

  static void getInlineData(StringBuffer loadScript, String strModel, boolean isAppend, String loadFilter) {
    String tag = (isAppend ? "append" : "model") + " inline";
    loadScript.append("load data \"").append(tag).append("\"\n")
        .append(strModel).append("end \"").append(tag)
        .append(loadFilter == null || loadFilter.length() == 0 ? "" : " filter" + Escape.escape(loadFilter))
        .append("\";");
  }
}
