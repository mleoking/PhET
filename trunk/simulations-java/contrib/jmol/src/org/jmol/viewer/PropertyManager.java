/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-03-23 20:46:59 -0700 (Wed, 23 Mar 2011) $
 * $Revision: 15335 $
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
package org.jmol.viewer;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix3f;

import org.jmol.script.ScriptEvaluator;
import org.jmol.script.ScriptVariable;
import org.jmol.script.Token;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.TextFormat;

/**
 * 
 * The PropertyManager handles all operations relating to delivery of
 * properties with the getProperty() method, or its specifically cast 
 * forms getPropertyString() or getPropertyJSON().
 *
 */

public class PropertyManager {

  private final static String atomExpression = "<atom selection>";
  
  private final static String[] propertyTypes = {
    "appletInfo"      , "", "",
    "fileName"        , "", "",
    "fileHeader"      , "", "",
    "fileContents"    , "<pathname>", "",
    "fileContents"    , "", "",  
    "animationInfo"   , "", "",
    "modelInfo"       , atomExpression, "(*)",
    "X -vibrationInfo", "", "",  //not implemented -- see modelInfo
    "shapeInfo"       , "", "",
    "measurementInfo" , "", "",
    
    "centerInfo"      , "", "",
    "orientationInfo" , "", "",
    "transformInfo"   , "", "",
    "atomList"        , atomExpression, "(visible)",
    "atomInfo"        , atomExpression, "(visible)",
    
    "bondInfo"        , atomExpression, "(visible)",
    "chainInfo"       , atomExpression, "(visible)",
    "polymerInfo"     , atomExpression, "(visible)",
    "moleculeInfo"    , atomExpression, "(visible)",
    "stateInfo"       , "<state type>", "all",
    
    "extractModel"    , atomExpression, "(visible)",
    "jmolStatus"      , "statusNameList", "",
    "jmolViewer"      , "", "",
    "messageQueue"    , "", "",
    "auxiliaryInfo"   , atomExpression, "*",
    
    "boundBoxInfo"    , "", "",  
    "dataInfo"        , "<data type>", "types",
    "image"           , "", "",
    "evaluate"        , "<expression>", "",
    "menu"            , "<type>", "current",
    "minimizationInfo", "", "",
    "pointGroupInfo"  , atomExpression, "(visible)",
    "fileInfo"         , "<type>", "",
    "errorMessage", "", "",
    "mouseInfo", "", "",
  };

  private final static int PROP_APPLET_INFO = 0;
  private final static int PROP_FILENAME = 1;
  private final static int PROP_FILEHEADER = 2;
          public final static int PROP_FILECONTENTS_PATH = 3;
  private final static int PROP_FILECONTENTS = 4;
  
  private final static int PROP_ANIMATION_INFO = 5;
  private final static int PROP_MODEL_INFO = 6;
  //private final static int PROP_VIBRATION_INFO = 7; //not implemented -- see auxiliaryInfo
  private final static int PROP_SHAPE_INFO = 8;
  private final static int PROP_MEASUREMENT_INFO = 9;
  
  private final static int PROP_CENTER_INFO = 10;
  private final static int PROP_ORIENTATION_INFO = 11;
  private final static int PROP_TRANSFORM_INFO = 12;
  private final static int PROP_ATOM_LIST = 13;
  private final static int PROP_ATOM_INFO = 14;
  
  private final static int PROP_BOND_INFO = 15;
  private final static int PROP_CHAIN_INFO = 16;
  private final static int PROP_POLYMER_INFO = 17;
  private final static int PROP_MOLECULE_INFO = 18;
  private final static int PROP_STATE_INFO = 19;
  
  private final static int PROP_EXTRACT_MODEL = 20;
  private final static int PROP_JMOL_STATUS = 21;
  private final static int PROP_JMOL_VIEWER = 22;
  private final static int PROP_MESSAGE_QUEUE = 23;
  private final static int PROP_AUXILIARY_INFO = 24;
  
  private final static int PROP_BOUNDBOX_INFO = 25;
  private final static int PROP_DATA_INFO = 26;
  private final static int PROP_IMAGE = 27;
  private final static int PROP_EVALUATE = 28;
  private final static int PROP_MENU = 29;
  private final static int PROP_MINIMIZATION_INFO = 30;
  private final static int PROP_POINTGROUP_INFO = 31;
  private final static int PROP_FILE_INFO = 32;
  private final static int PROP_ERROR_MESSAGE = 33;
  private final static int PROP_MOUSE_INFO = 34;
  private final static int PROP_COUNT = 35;

  //// static methods used by Eval and Viewer ////
  
  public static int getPropertyNumber(String infoType) {
    if (infoType == null)
      return -1;
    for(int i = 0; i < PROP_COUNT; i++)
      if(infoType.equalsIgnoreCase(getPropertyName(i)))
        return i;
    return -1;
  }
  
  public static String getDefaultParam(int propID) {
    if (propID < 0)
      return "";
    return propertyTypes[propID * 3 + 2];
  }
  
  public static boolean acceptsStringParameter(String name) {
    int propID = getPropertyNumber(name);
    String type = getParamType(propID);
    return (type.length() > 0 && type != atomExpression);
  }
  
  public static Object getProperty(Viewer viewer, String returnType, String infoType, Object paramInfo) {
    if (propertyTypes.length != PROP_COUNT * 3)
      Logger.warn("propertyTypes is not the right length: " + propertyTypes.length + " != " + PROP_COUNT * 3);
    Object info;
    if (infoType.indexOf(".") >= 0 || infoType.indexOf("[") >= 0) {
      info = getModelProperty(viewer, infoType, paramInfo);
    } else {
      info = getPropertyAsObject(viewer, infoType, paramInfo, returnType);
    }
    if (returnType == null)
      return info;
    boolean requestedReadable = returnType.equalsIgnoreCase("readable");
    if (requestedReadable)
      returnType = (isReadableAsString(infoType) ? "String" : "JSON");
    if (returnType.equalsIgnoreCase("String")) return (info == null ? "" : info.toString());
    if (requestedReadable)
      return Escape.toReadable(infoType, info);
    else if (returnType.equalsIgnoreCase("JSON"))
      return "{" + Escape.toJSON(infoType, info) + "}";
    return info;
  }
  
  static Object getModelProperty(Viewer viewer, String propertyName, Object propertyValue) {
    propertyName = propertyName.replace(']', ' ').replace('[', ' ').replace(
        '.', ' ');
    propertyName = TextFormat.simpleReplace(propertyName, "  ", " ");
    String[] names = TextFormat.split(TextFormat.trim(propertyName, " "), " ");
    ScriptVariable[] args = new ScriptVariable[names.length];
    propertyName = names[0];
    int n;
    for (int i = 1; i < names.length; i++) {
      if ((n = Parser.parseInt(names[i])) != Integer.MIN_VALUE)
        args[i] = new ScriptVariable(Token.integer, n);
      else
        args[i] = new ScriptVariable(Token.string, names[i]);
    }
    return extractProperty(getProperty(viewer, null, propertyName, propertyValue), args, 1);
  }

  @SuppressWarnings("unchecked")
  public static Object extractProperty(Object property, ScriptVariable[] args, int ptr) {
    if (ptr >= args.length)
      return property;
    int pt;
    ScriptVariable arg = args[ptr++];
    switch (arg.tok) {
    case Token.integer:
      pt = ScriptVariable.iValue(arg) - 1;  //one-based, as for array selectors
      if (property instanceof List<?>) {
        List<Object> v = (List<Object>) property;
        if (pt < 0)
          pt += v.size();
        if (pt >= 0 && pt < v.size())
          return extractProperty(v.get(pt), args, ptr);
        return "";
      }
      if (property instanceof String[]) {
        String[] slist = (String[]) property;
        if (pt < 0)
          pt += slist.length;
        if (pt >= 0 && pt < slist.length)
          return slist[pt];
        return "";
      }
      if (property instanceof Matrix3f) {
        Matrix3f m = (Matrix3f) property;
        float[][] f = new float[][] {
            new float[] {m.m00, m.m01, m.m02}, 
            new float[] {m.m10, m.m11, m.m12}, 
            new float[] {m.m20, m.m21, m.m22}}; 
        if (pt < 0)
          pt += 3;
        if (pt >= 0 && pt < 3)
          return extractProperty(f, args, --ptr);
        return "";
      }
      if (property instanceof float[]) {
        float[] flist = (float[]) property;
        if (pt < 0)
          pt += flist.length;
        if (pt >= 0 && pt < flist.length)
          return new Float(flist[pt]);
        return "";
      }
      if (property instanceof int[]) {
        int[] ilist = (int[]) property;
        if (pt < 0)
          pt += ilist.length;
        if (pt >= 0 && pt < ilist.length)
          return Integer.valueOf(ilist[pt]);
        return "";
      }
      if (property instanceof float[][]) {
        float[][] fflist = (float[][]) property;
        if (pt < 0)
          pt += fflist.length;
        if (pt >= 0 && pt < fflist.length)
          return extractProperty(fflist[pt], args, ptr);
        return "";
      }
      if (property instanceof int[][]) {
        int[][] iilist = (int[][]) property;
        if (pt < 0)
          pt += iilist.length;
        if (pt >= 0 && pt < iilist.length)
          return extractProperty(iilist[pt], args, ptr);
        return "";
      }
      if (property instanceof Object[]) {
        Object[] olist = (Object[]) property;
        if (pt < 0)
          pt += olist.length;
        if (pt >= 0 && pt < olist.length)
          return olist[pt];
        return "";
      }
      break;
    case Token.string:
      String key = ScriptVariable.sValue(arg);
      if (property instanceof Map<?,?>) {
        Map<String, Object> h = (Map<String, Object>) property;
        if (key.equalsIgnoreCase("keys")) {
          List<Object> keys = new ArrayList<Object>();
          Iterator<String> e = h.keySet().iterator();
          while (e.hasNext())
            keys.add(e.next()); 
          return extractProperty(keys, args, ptr);
        }
        if (!h.containsKey(key)) {
          Iterator<String> e = h.keySet().iterator();
          String newKey = "";
          while (e.hasNext())
            if ((newKey = e.next()).equalsIgnoreCase(key)) {
              key = newKey;
              break;
            }
        }
        if (h.containsKey(key))
          return extractProperty(h.get(key), args, ptr);
        return "";
      }
      if (property instanceof List<?>) {
        // drill down into vectors for this key
        List<Object> v = (List<Object>) property;
        List<Object> v2 = new ArrayList<Object>();
        ptr--;
        for (pt = 0; pt < v.size(); pt++) {
          Object o = v.get(pt);
          if (o instanceof Map<?,?>)
            v2.add(extractProperty(o, args, ptr));
        }
        return v2;
      }
      break;
    }
    return property;
  }

  //// private static methods ////
  
  private static String getPropertyName(int propID) {
    if (propID < 0)
      return "";
    return propertyTypes[propID * 3];
  }
  
  private static String getParamType(int propID) {
    if (propID < 0)
      return "";
    return propertyTypes[propID * 3 + 1];
  }
  
  private final static String[] readableTypes = {
    "", "stateinfo", "extractmodel", "filecontents", "fileheader", "image", "menu"};
  
  private static boolean isReadableAsString(String infoType) {
    for (int i = readableTypes.length; --i >= 0; )
      if (infoType.equalsIgnoreCase(readableTypes[i]))
          return true;
    return false;
  }
  
  private static Object getPropertyAsObject(Viewer viewer, String infoType,
                                            Object paramInfo, String returnType) {
    //Logger.debug("getPropertyAsObject(\"" + infoType+"\", \"" + paramInfo + "\")");
    if (infoType.equals("tokenList")) {
      return Token.getTokensLike((String) paramInfo);
    }
    int id = getPropertyNumber(infoType);
    boolean iHaveParameter = (paramInfo != null && paramInfo.toString()
        .length() > 0);
    Object myParam = (iHaveParameter ? paramInfo : getDefaultParam(id));
    //myParam may now be a bitset
    switch (id) {
    case PROP_APPLET_INFO:
      return viewer.getAppletInfo();
    case PROP_ANIMATION_INFO:
      return viewer.getAnimationInfo();
    case PROP_ATOM_LIST:
      return viewer.getAtomBitSetVector(myParam);
    case PROP_ATOM_INFO:
      return viewer.getAllAtomInfo(myParam);
    case PROP_AUXILIARY_INFO:
      return viewer.getAuxiliaryInfo(myParam);
    case PROP_BOND_INFO:
      return viewer.getAllBondInfo(myParam);
    case PROP_BOUNDBOX_INFO:
      return viewer.getBoundBoxInfo();
    case PROP_CENTER_INFO:
      return viewer.getRotationCenter();
    case PROP_CHAIN_INFO:
      return viewer.getAllChainInfo(myParam);
    case PROP_EXTRACT_MODEL:
      return viewer.getModelExtract(myParam, true, "MOL");
    case PROP_FILENAME:
      return viewer.getFullPathName();
    case PROP_FILEHEADER:
      return viewer.getFileHeader();
    case PROP_FILECONTENTS:
    case PROP_FILECONTENTS_PATH:
      if (iHaveParameter)
        return viewer.getFileAsString(myParam.toString());
      return viewer.getCurrentFileAsString();
    case PROP_JMOL_STATUS:
      return viewer.getStatusChanged(myParam.toString());
    case PROP_JMOL_VIEWER:
      return viewer;
    case PROP_MEASUREMENT_INFO:
      return viewer.getMeasurementInfo();
    case PROP_MENU:
      return viewer.getMenu(myParam.toString());
    case PROP_MESSAGE_QUEUE:
      return viewer.getMessageQueue();
    case PROP_MINIMIZATION_INFO:
      return viewer.getMinimizationInfo();
    case PROP_MODEL_INFO:
      return viewer.getModelInfo(myParam);
    case PROP_MOLECULE_INFO:
      return viewer.getMoleculeInfo(myParam);
    case PROP_ORIENTATION_INFO:
      return viewer.getOrientationInfo();
    case PROP_POLYMER_INFO:
      return viewer.getAllPolymerInfo(myParam);
    case PROP_SHAPE_INFO:
      return viewer.getShapeInfo();
    case PROP_STATE_INFO:
      return viewer.getStateInfo(myParam.toString());
    case PROP_POINTGROUP_INFO:
      return viewer.getPointGroupInfo(myParam);
    case PROP_FILE_INFO:
      return getFileInfo(viewer.getFileData(), myParam.toString());
    case PROP_ERROR_MESSAGE:
      return viewer.getErrorMessageUntranslated();
    case PROP_TRANSFORM_INFO:
      return viewer.getMatrixRotate();
    case PROP_DATA_INFO:
      return viewer.getData(myParam.toString());
    case PROP_MOUSE_INFO:
      return viewer.getMouseInfo();
    case PROP_EVALUATE:
      return ScriptEvaluator.evaluateExpression(viewer, myParam.toString());
    case PROP_IMAGE:
      String params = myParam.toString();
      int height = -1, width = -1;
      int pt;
      if ((pt = params.indexOf("height=")) >= 0)
        height = Parser.parseInt(params.substring(pt + 7));
      if ((pt = params.indexOf("width=")) >= 0)
        width = Parser.parseInt(params.substring(pt + 6));
      if (width < 0 && height < 0)
        height = width = -1;
      else if (width < 0)
        width = height;
      else
        height = width;        
      return viewer.getImageAs(returnType == null ? "JPEG" : "JPG64", -1, width, height,
          null, null);
    }
    String[] data = new String[PROP_COUNT];
    for (int i = 0; i < PROP_COUNT; i++) {
      String paramType = getParamType(i);
      String paramDefault = getDefaultParam(i);
      String name = getPropertyName(i);
      data[i] = (name.charAt(0) == 'X' ? "" : name
          + (paramType != "" ? " " + getParamType(i)
              + (paramDefault != "" ? " #default: " + getDefaultParam(i) : "")
              : ""));
    }
    Arrays.sort(data);
    StringBuffer info = new StringBuffer("getProperty ERROR\n" + infoType
        + "?\nOptions include:\n");
    for (int i = 0; i < PROP_COUNT; i++)
      if (data[i].length() > 0)
        info.append("\n getProperty ").append(data[i]);
    return info.toString();
  }
  
  @SuppressWarnings("unchecked")
  static Object getFileInfo(Object objHeader, String type) {
    Map ht = new Hashtable();
    if (objHeader == null)
      return ht;
    boolean haveType = (type != null && type.length() > 0);
    if (objHeader instanceof Map) {
      return (haveType ? ((Map) objHeader).get(type) : objHeader);
    }
    String[] lines = TextFormat.split((String)objHeader, '\n');
    String keyLast = "";
    StringBuilder sb = new StringBuilder();
    if (haveType)
      type = type.toUpperCase();
    String key = "";
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      if (line.length() < 12) continue;
      key = line.substring(0,6).trim();
      String cont = line.substring(7,10).trim();
      if (key.equals("REMARK")) {
        key += cont;
      }
      if (!key.equals(keyLast)) {
        if (haveType && keyLast.equals(type))
          return sb.toString();
        if (sb != null && !haveType) {
          ht.put(keyLast, sb.toString());
          sb = new StringBuilder();
        }
        keyLast = key;
      }
      if (!haveType || key.equals(type))
        sb.append(line.substring(10).trim()).append('\n');      
    }
    if (sb != null && !haveType) {
      ht.put(keyLast, sb.toString());
    }
    if (haveType)
      return (key.equals(type) ? sb.toString() : "");
    return ht;
  }

}
