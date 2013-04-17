/* $RCSfile$
 * $Author: egonw $
 * $Date: 2005-11-10 09:52:44 -0600 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
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

import javax.vecmath.Point3f;
import javax.vecmath.Matrix3f;

import java.util.Hashtable;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Bond;
import org.jmol.modelset.LabelToken;
import org.jmol.modelset.ModelSet;
import org.jmol.script.ScriptFunction;
import org.jmol.script.ScriptVariable;
import org.jmol.util.Escape;

import org.jmol.util.BitSetUtil;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.TextFormat;

import java.util.Arrays;

public class StateManager {

  /* steps in adding a global variable:
   
   In Viewer:
   
   1. add a check in setIntProperty or setBooleanProperty or setFloat.. or setString...
   2. create new set/get methods
   
   In StateManager
   
   3. create the global.xxx variable
   4. in registerParameter() register it so that it shows up as having a value in math
   
   */

  public final static int OBJ_BACKGROUND = 0;
  public final static int OBJ_AXIS1 = 1;
  public final static int OBJ_AXIS2 = 2;
  public final static int OBJ_AXIS3 = 3;
  public final static int OBJ_BOUNDBOX = 4;
  public final static int OBJ_UNITCELL = 5;
  public final static int OBJ_FRANK = 6;
  public final static int OBJ_MAX = 8;
  private final static String objectNameList = "background axis1      axis2      axis3      boundbox   unitcell   frank      ";

  public static String getVariableList(Map<String, ScriptVariable> htVariables, int nMax,
                                       boolean withSites) {
    StringBuffer sb = new StringBuffer();
    // user variables only:
    int n = 0;

    String[] list = new String[htVariables.size()];
    for (Map.Entry<String, ScriptVariable> entry : htVariables.entrySet()) {
      String key = entry.getKey();
      ScriptVariable var = entry.getValue();
      if (withSites || (!key.startsWith("@site_") && !key.startsWith("site_")))
        list[n++] = key
            + (key.charAt(0) == '@' ? " " + ScriptVariable.sValue(var) : " = "
                + varClip(key, var.escape(), nMax));
    }
    Arrays.sort(list, 0, n);
    for (int i = 0; i < n; i++)
      if (list[i] != null)
        appendCmd(sb, list[i]);
    if (n == 0)
      sb.append("# --no global user variables defined--;\n");
    return sb.toString();
  }
  
  public static int getObjectIdFromName(String name) {
    if (name == null)
      return -1;
    int objID = objectNameList.indexOf(name.toLowerCase());
    return (objID < 0 ? objID : objID / 11);
  }

  static String getObjectNameFromId(int objId) {
    if (objId < 0 || objId >= OBJ_MAX)
      return null;
    return objectNameList.substring(objId * 11, objId * 11 + 11).trim();
  }

  Viewer viewer;
  Map<String, Object> saved = new Hashtable<String, Object>();
  String lastOrientation = "";
  String lastConnections = "";
  String lastSelected = "";
  String lastState = "";
  String lastShape = "";
  String lastCoordinates = "";

  StateManager(Viewer viewer) {
    this.viewer = viewer;
  }

  GlobalSettings getGlobalSettings(GlobalSettings gsOld) {
    return new GlobalSettings(gsOld);
  }

  void clear(GlobalSettings global) {
    viewer.setShowAxes(false);
    viewer.setShowBbcage(false);
    viewer.setShowUnitCell(false);
    global.clear();
  }

  void setCrystallographicDefaults() {
    //axes on and mode unitCell; unitCell on; perspective depth off;
    viewer.setAxesModeUnitCell(true);
    viewer.setShowAxes(true);
    viewer.setShowUnitCell(true);
    viewer.setBooleanProperty("perspectiveDepth", false);
  }

  private void setCommonDefaults() {
    viewer.setBooleanProperty("perspectiveDepth", true);
    viewer.setFloatProperty("bondTolerance",
        JmolConstants.DEFAULT_BOND_TOLERANCE);
    viewer.setFloatProperty("minBondDistance",
        JmolConstants.DEFAULT_MIN_BOND_DISTANCE);
  }

  void setJmolDefaults() {
    setCommonDefaults();
    viewer.setStringProperty("defaultColorScheme", "Jmol");
    viewer.setBooleanProperty("axesOrientationRasmol", false);
    viewer.setBooleanProperty("zeroBasedXyzRasmol", false);
    viewer.setIntProperty("percentVdwAtom",
        JmolConstants.DEFAULT_PERCENT_VDW_ATOM);
    viewer.setIntProperty("bondRadiusMilliAngstroms",
        JmolConstants.DEFAULT_BOND_MILLIANGSTROM_RADIUS);
    viewer.setDefaultVdw("auto");
  }

  void setRasMolDefaults() {
    setCommonDefaults();
    viewer.setStringProperty("defaultColorScheme", "RasMol");
    viewer.setBooleanProperty("axesOrientationRasmol", true);
    viewer.setBooleanProperty("zeroBasedXyzRasmol", true);
    viewer.setIntProperty("percentVdwAtom", 0);
    viewer.setIntProperty("bondRadiusMilliAngstroms", 1);
    viewer.setDefaultVdw("Rasmol");
  }

  String getStandardLabelFormat() {
    // from the RasMol 2.6b2 manual: RasMol uses the label
    // "%n%r:%c.%a" if the molecule contains more than one chain:
    // "%e%i" if the molecule has only a single residue (a small molecule) and
    // "%n%r.%a" otherwise.
    
    // Jmol 11.7.39 -- see Atom.getIdentity(true);
    
    String strLabel = LabelToken.STANDARD_LABEL;
    
    /*
    int modelCount = viewer.getModelCount();
    if (viewer.getChainCount() > modelCount)
      strLabel = "[%n]%r:%c.%a";
    else if (viewer.getGroupCount() <= modelCount)
      strLabel = "%e%i";
    else
      strLabel = "[%n]%r.%a";
    if (viewer.getModelCount() > 1)
      strLabel += "/%M";
    */
    
    return strLabel;
  }

  String listSavedStates() {
    String names = "";
    Iterator<String> e = saved.keySet().iterator();
    while (e.hasNext())
      names += "\n" + e.next();
    return names;
  }

  private void deleteSavedType(String type) {
    Iterator<String> e = saved.keySet().iterator();
    while (e.hasNext()) {
      String name = e.next();
      if (name.startsWith(type)) {
        e.remove();
        Logger.debug("deleted " + name);
      }
    }
  }

  void deleteSaved(String name) {
    saved.remove(name);
  }
  
  void saveSelection(String saveName, BitSet bsSelected) {
    if (saveName.equalsIgnoreCase("DELETE")) {
      deleteSavedType("Selected_");
      return;
    }
    saveName = lastSelected = "Selected_" + saveName;
    saved.put(saveName, BitSetUtil.copy(bsSelected));
  }

  boolean restoreSelection(String saveName) {
    String name = (saveName.length() > 0 ? "Selected_" + saveName
        : lastSelected);
    BitSet bsSelected = (BitSet) saved.get(name);
    if (bsSelected == null) {
      viewer.select(new BitSet(), false);
      return false;
    }
    viewer.select(bsSelected, false);
    return true;
  }

  void saveState(String saveName) {
    if (saveName.equalsIgnoreCase("DELETE")) {
      deleteSavedType("State_");
      return;
    }
    saveName = lastState = "State_" + saveName;
    saved.put(saveName, viewer.getStateInfo());
  }

  String getSavedState(String saveName) {
    String name = (saveName.length() > 0 ? "State_" + saveName : lastState);
    String script = (String) saved.get(name);
    return (script == null ? "" : script);
  }

  /*  
   boolean restoreState(String saveName) {
   //not used -- more efficient just to run the script 
   String name = (saveName.length() > 0 ? "State_" + saveName
   : lastState);
   String script = (String) saved.get(name);
   if (script == null)
   return false;
   viewer.script(script + CommandHistory.NOHISTORYATALL_FLAG);
   return true;
   }
   */
  void saveStructure(String saveName) {
    if (saveName.equalsIgnoreCase("DELETE")) {
      deleteSavedType("Shape_");
      return;
    }
    saveName = lastShape = "Shape_" + saveName;
    saved.put(saveName, viewer.getStructureState());
  }

  String getSavedStructure(String saveName) {
    String name = (saveName.length() > 0 ? "Shape_" + saveName : lastShape);
    String script = (String) saved.get(name);
    return (script == null ? "" : script);
  }

  void saveCoordinates(String saveName, BitSet bsSelected) {
    if (saveName.equalsIgnoreCase("DELETE")) {
      deleteSavedType("Coordinates_");
      return;
    }
    saveName = lastCoordinates = "Coordinates_" + saveName;
    saved.put(saveName, viewer.getCoordinateState(bsSelected));
  }

  String getSavedCoordinates(String saveName) {
    String name = (saveName.length() > 0 ? "Coordinates_" + saveName
        : lastCoordinates);
    String script = (String) saved.get(name);
    return (script == null ? "" : script);
  }

  Orientation getOrientation() {
    return new Orientation(false);
  }

  String getSavedOrientationText(String saveName) {
    Orientation o;
    if (saveName != null) {
      o = getOrientation(saveName);
      return (o == null ? "" : o.getMoveToText(true));      
    } 
    StringBuffer sb = new StringBuffer();
    Iterator<String> e = saved.keySet().iterator();
    while (e.hasNext()) {
       String name = e.next();
       if (!name.startsWith("Orientation_")) {
         continue;
       }
       sb.append(((Orientation) saved.get(name)).getMoveToText(true));
    }
    return sb.toString(); 
  }


  void saveOrientation(String saveName) {
    if (saveName.equalsIgnoreCase("DELETE")) {
      deleteSavedType("Orientation_");
      return;
    }
    Orientation o = new Orientation(saveName.equals("default"));
    o.saveName = lastOrientation = "Orientation_" + saveName;
    saved.put(o.saveName, o);
  }
  
  boolean restoreOrientation(String saveName, float timeSeconds, boolean isAll) {
    Orientation o = getOrientation(saveName);
    if (o == null)
      return false;
    o.restore(timeSeconds, isAll);
    //    Logger.info(listSavedStates());
    return true;
  }

  private Orientation getOrientation(String saveName) {
    String name = (saveName.length() > 0 ? "Orientation_" + saveName
        : lastOrientation);    
    return (Orientation) saved.get(name);
  }

  public class Orientation {

    String saveName;

    Matrix3f rotationMatrix = new Matrix3f();
    float xTrans, yTrans;
    float zoom, rotationRadius;
    Point3f center = new Point3f();
    Point3f navCenter = new Point3f();
    float xNav = Float.NaN;
    float yNav = Float.NaN;
    float navDepth = Float.NaN;
    boolean windowCenteredFlag;
    boolean navigationMode;
    boolean navigateSurface;
    String moveToText;
    

    Orientation(boolean asDefault) {
      if (asDefault) {
        Matrix3f rotationMatrix = (Matrix3f) viewer
          .getModelSetAuxiliaryInfo("defaultOrientationMatrix");
        if (rotationMatrix == null)
          this.rotationMatrix.setIdentity();
        else
          this.rotationMatrix.set(rotationMatrix);
      } else {
        viewer.getRotation(this.rotationMatrix);
      }
      xTrans = viewer.getTranslationXPercent();
      yTrans = viewer.getTranslationYPercent();
      zoom = viewer.getZoomSetting();
      center.set(viewer.getRotationCenter());
      windowCenteredFlag = viewer.isWindowCentered();
      rotationRadius = viewer.getRotationRadius();
      navigationMode = viewer.getNavigationMode();
      navigateSurface = viewer.getNavigateSurface();
      moveToText = viewer.getMoveToText(-1);
      if (navigationMode) {
        xNav = viewer.getNavigationOffsetPercent('X');
        yNav = viewer.getNavigationOffsetPercent('Y');
        navDepth = viewer.getNavigationDepthPercent();
        navCenter = new Point3f(viewer.getNavigationCenter());
      }
    }

    public String getMoveToText(boolean asCommand) {
      return (asCommand ? "  " + moveToText + "\n  save orientation \"" 
          + saveName.substring(12) + "\";\n" : moveToText);
    }
    
    public void restore(float timeSeconds, boolean isAll) {
      if (!isAll) {
        viewer.setRotationMatrix(rotationMatrix);
        return;
      }
      viewer.setBooleanProperty("windowCentered", windowCenteredFlag);
      viewer.setBooleanProperty("navigationMode", navigationMode);
      viewer.setBooleanProperty("navigateSurface", navigateSurface);
      viewer.moveTo(timeSeconds, center, null, Float.NaN, rotationMatrix, zoom, xTrans, yTrans,
          rotationRadius, navCenter, xNav, yNav, navDepth);
    }
  }

  void saveBonds(String saveName) {
    if (saveName.equalsIgnoreCase("DELETE")) {
      deleteSavedType("Bonds_");
      return;
    }
    Connections b = new Connections();
    b.saveName = lastConnections = "Bonds_" + saveName;
    saved.put(b.saveName, b);
  }

  boolean restoreBonds(String saveName) {
    String name = (saveName.length() > 0 ? "Bonds_" + saveName
        : lastConnections);
    Connections c = (Connections) saved.get(name);
    if (c == null)
      return false;
    c.restore();
    //    Logger.info(listSavedStates());
    return true;
  }

  class Connections {

    String saveName;
    int bondCount;
    Connection[] connections;

    Connections() {
      ModelSet modelSet = viewer.getModelSet();
      if (modelSet == null)
        return;
      bondCount = modelSet.getBondCount();
      connections = new Connection[bondCount + 1];
      Bond[] bonds = modelSet.getBonds();
      for (int i = bondCount; --i >= 0;) {
        Bond b = bonds[i];
        connections[i] = new Connection(b.getAtomIndex1(), b.getAtomIndex2(), b
            .getMad(), b.getColix(), b.getOrder(), b.getEnergy(), b.getShapeVisibilityFlags());
      }
    }

    void restore() {
      ModelSet modelSet = viewer.getModelSet();
      if (modelSet == null)
        return;
      modelSet.deleteAllBonds();
      for (int i = bondCount; --i >= 0;) {
        Connection c = connections[i];
        int atomCount = modelSet.getAtomCount();
        if (c.atomIndex1 >= atomCount || c.atomIndex2 >= atomCount)
          continue;
        Bond b = modelSet.bondAtoms(modelSet.atoms[c.atomIndex1],
            modelSet.atoms[c.atomIndex2], c.order, c.mad, null, c.energy, true);
        b.setColix(c.colix);
        b.setShapeVisibilityFlags(c.shapeVisibilityFlags);
      }
      for (int i = bondCount; --i >= 0;)
        modelSet.getBondAt(i).setIndex(i);
      viewer.setShapeProperty(JmolConstants.SHAPE_STICKS, "reportAll", null);
    }
  }

  static class Connection {
    int atomIndex1;
    int atomIndex2;
    short mad;
    short colix;
    int order;
    float energy;
    int shapeVisibilityFlags;

    Connection(int atom1, int atom2, short mad, short colix, int order, float energy,
        int shapeVisibilityFlags) {
      atomIndex1 = atom1;
      atomIndex2 = atom2;
      this.mad = mad;
      this.colix = colix;
      this.order = order;
      this.energy = energy;
      this.shapeVisibilityFlags = shapeVisibilityFlags;
    }
  }

  public static boolean isMeasurementUnit(String units) {
    return Parser.isOneOf(units.toLowerCase(),
        "angstroms;au;bohr;nanometers;nm;picometers;pm");
  }

  private final static Map<String, ScriptFunction> staticFunctions = new Hashtable<String, ScriptFunction>();
  private Map<String, ScriptFunction> localFunctions = new Hashtable<String, ScriptFunction>();

  Map<String, ScriptFunction> getFunctions(boolean isStatic) {
    return (isStatic ? staticFunctions : localFunctions);
  }

  String getFunctionCalls(String selectedFunction) {
    if (selectedFunction == null)
      selectedFunction = "";
    StringBuffer s = new StringBuffer();
    int pt = selectedFunction.indexOf("*");
    boolean isGeneric = (pt >= 0);
    boolean isStatic = (selectedFunction.indexOf("static_") == 0);
    boolean namesOnly = (selectedFunction.equalsIgnoreCase("names") || selectedFunction.equalsIgnoreCase("static_names"));
    if (namesOnly)
      selectedFunction = "";
    if (isGeneric)
      selectedFunction = selectedFunction.substring(0, pt);
    selectedFunction = selectedFunction.toLowerCase();
    Map<String, ScriptFunction> ht = getFunctions(isStatic);
    String[] names = new String[ht.size()];
    Iterator<String> e = ht.keySet().iterator();
    int n = 0;
    while (e.hasNext()) {
      String name = e.next();
      if (selectedFunction.length() == 0 && !name.startsWith("_")
          || name.equalsIgnoreCase(selectedFunction) || isGeneric
          && name.toLowerCase().indexOf(selectedFunction) == 0)
        names[n++] = name;
    }
    Arrays.sort(names, 0, n);
    for (int i = 0; i < n; i++) {
      ScriptFunction f = ht.get(names[i]);
      s.append(namesOnly ? f.getSignature() : f.toString());
      s.append('\n');
    }
    return s.toString();
  }

  public void clearFunctions() {
    staticFunctions.clear();
    localFunctions.clear();
  }

  private static boolean isStaticFunction(String name) {
    return name.startsWith("static_");  
  }
  
  boolean isFunction(String name) {
    return (isStaticFunction(name) ? staticFunctions : localFunctions).containsKey(name);
  }

  void addFunction(ScriptFunction function) {
    (isStaticFunction(function.name) ? staticFunctions
        : localFunctions).put(function.name, function);
  }

  ScriptFunction getFunction(String name) {
    if (name == null)
      return null;
    ScriptFunction function = (isStaticFunction(name) ? staticFunctions
        : localFunctions).get(name);
    return (function == null || function.aatoken == null ? null : function);
  }
  
  protected final static String unreportedProperties =
    //these are handled individually in terms of reporting for the state
    //NOT EXCLUDING the load state settings, because although we
    //handle these specially for the CURRENT FILE, their current
    //settings won't be reflected in the load state, which is determined
    //earlier, when the file loads. 
    //
    //place any parameter here you do NOT want to have in the state
    //
    // _xxxxx variables are automatically exempt
    //
    (";ambientpercent;animationfps"
        + ";antialiasdisplay;antialiasimages;antialiastranslucent;appendnew;axescolor"
        + ";axesposition;axesmolecular;axesorientationrasmol;axesunitcell;axeswindow;axis1color;axis2color"
        + ";axis3color;backgroundcolor;backgroundmodel;bondsymmetryatoms;boundboxcolor;cameradepth"
        + ";debug;debugscript;defaultlatttice;defaults;diffusepercent;exportdrivers"
        + ";_filecaching;_filecache;fontcaching;fontscaling;language"
        + ";legacyautobonding"
        + ";loglevel;logfile;loggestures;logcommands;measureStyleChime"
        + ";loadformat;smilesurlformat;edsurlformat;edsurlcutoff;multiprocessor;navigationmode;"
        + ";perspectivedepth;phongexponent;perspectivemodel;preservestate;refreshing;repaintwaitms;rotationradius"
        + ";showaxes;showaxis1;showaxis2;showaxis3;showboundbox;showfrank;showunitcell"
        + ";slabenabled;slab;slabrange;depth;zshade;zshadepower;specular;specularexponent;specularpercent;specularpower;stateversion"
        + ";statusreporting;stereo;stereostate;vibrationperiod"
        + ";unitcellcolor;visualrange;windowcentered;zerobasedxyzrasmol;zoomenabled;mousedragfactor;mousewheelfactor"
        //    saved in the hash table but not considered part of the state:
        + ";scriptqueue;scriptreportinglevel;syncscript;syncmouse;syncstereo;" 
        + ";defaultdirectory;currentlocalpath;defaultdirectorylocal"
        //    more settable Jmol variables    
        + ";ambient;bonds;colorrasmol;diffuse;frank;hetero;hidenotselected"
        + ";hoverlabel;hydrogen;languagetranslation;measurementunits;navigationdepth;navigationslab"
        + ";picking;pickingstyle;propertycolorschemeoverload;radius;rgbblue;rgbgreen;rgbred"
        + ";scaleangstromsperinch;selectionhalos;showscript;showselections;solvent;strandcount"
        + ";spinx;spiny;spinz;spinfps;navx;navy;navz;navfps;" + JmolConstants.getCallbackName(-1)
        + ";undo;bondpicking;modelkitmode;allowgestures;allowkeystrokes;allowmultitouch;allowmodelkit"
        + ";").toLowerCase();

  protected static int getJmolVersionInt() {
    // 11.9.999 --> 1109999
    String s = JmolConstants.version;
    int version = -1;

    try {
      // Major number
      int i = s.indexOf(".");
      if (i < 0) {
        version = 100000 * Integer.parseInt(s);
        return version;
      }
      version = 100000 * Integer.parseInt(s.substring(0, i));

      // Minor number
      s = s.substring(i + 1);
      i = s.indexOf(".");
      if (i < 0) {
        version += 1000 * Integer.parseInt(s);
        return version;
      }
      version += 1000 * Integer.parseInt(s.substring(0, i));

      // Revision number
      s = s.substring(i + 1);
      i = s.indexOf("_");
      if (i >= 0)
        s = s.substring(0, i);
      i = s.indexOf(" ");
      if (i >= 0)
        s = s.substring(0, i);
      version += Integer.parseInt(s);
    } catch (NumberFormatException e) {
      // We simply keep the version currently found
    }

    return version;
  }

  class GlobalSettings {

    Map<String, Object> htNonbooleanParameterValues;
    Map<String, Boolean> htBooleanParameterFlags;
    Map<String, Boolean> htPropertyFlagsRemoved;
    Map<String, ScriptVariable> htUserVariables = new Hashtable<String, ScriptVariable>();

    /*
     *  Mostly these are just saved and restored directly from Viewer.
     *  They are collected here for reference and to ensure that no 
     *  methods are written that bypass viewer's get/set methods.
     *  
     *  Because these are not Frame variables, they (mostly) should persist past
     *  a new file loading. There is some question in my mind whether all
     *  should be in this category.
     *  
     */

    GlobalSettings(GlobalSettings gsOld) {
      registerAllValues(gsOld);
    }

    void clear() {
      Iterator<String> e = htUserVariables.keySet().iterator();
      while (e.hasNext()) {
        String key = e.next();
        if (key.charAt(0) == '@' || key.startsWith("site_"))
          e.remove();
      }

      // PER-zap settings made
      setParameterValue("_atompicked", -1);
      setParameterValue("_atomhovered", -1);
      setParameterValue("_pickinfo", "");
      setParameterValue("selectionhalos", false);
      setParameterValue("hidenotselected", false); // to synchronize with selectionManager
      setParameterValue("measurementlabels", measurementLabels = true);
      setParameterValue("drawHover", drawHover = false);
      

    }

    void registerAllValues(GlobalSettings g) {
      htNonbooleanParameterValues = new Hashtable<String, Object>();
      htBooleanParameterFlags = new Hashtable<String, Boolean>();
      htPropertyFlagsRemoved = new Hashtable<String, Boolean>();

      if (g != null) {
        //persistent values not reset with the "initialize" command
        debugScript = g.debugScript;
        disablePopupMenu = g.disablePopupMenu;
        messageStyleChime = g.messageStyleChime;
        defaultDirectory = g.defaultDirectory;
        allowGestures = g.allowGestures;
        allowModelkit = g.allowModelkit;
        allowMultiTouch = g.allowMultiTouch;
        allowKeyStrokes = g.allowKeyStrokes;
        legacyAutoBonding = g.legacyAutoBonding;
        useScriptQueue = g.useScriptQueue;
        useArcBall = g.useArcBall;
      }

      for (int i = 0;;i++) {        
        String callbackName = JmolConstants.getCallbackName(i);
        if (callbackName == null)
          break;
        resetParameterStringValue(callbackName, g);        
      }

      setParameterValue("historyLevel", 0); //deprecated ? doesn't do anything

      // These next are just placeholders so that the math processor
      // knows they are Jmol variables. They are held by other managers.
      // This is NOT recommended, because it is easy to forget they are 
      // here and then not reset them properly. Basically it means that
      // the other manager must ensure that the value changed there is
      // updated here, AND when an initialization occurs, they remain in
      // sync. This is difficult to manage and should be changed.
      // The good news is that this manager is initialized FIRST, so 
      // we really just have to make sure that all these values are definitely
      // also initialized within the managers. 

      setParameterValue("gestureSwipeFactor", ActionManager.DEFAULT_GESTURE_SWIPE_FACTOR);
      setParameterValue("hideNotSelected", false); //maintained by the selectionManager
      setParameterValue("hoverLabel", ""); // maintained by the Hover shape
      setParameterValue("isKiosk", viewer.isKiosk()); // maintained by Viewer
      setParameterValue("logFile", viewer.getLogFile()); // maintained by Viewer
      setParameterValue("logLevel", Logger.getLogLevel());

      setParameterValue("mouseWheelFactor", ActionManager.DEFAULT_MOUSE_WHEEL_FACTOR);
      setParameterValue("mouseDragFactor", ActionManager.DEFAULT_MOUSE_DRAG_FACTOR);
      setParameterValue("navFps", TransformManager.DEFAULT_NAV_FPS); 
      setParameterValue("navigationDepth", 0);   // maintained by TransformManager
      setParameterValue("navigationSlab", 0);    // maintained by TransformManager
      setParameterValue("navX", 0);              // maintained by TransformManager
      setParameterValue("navY", 0);              // maintained by TransformManager
      setParameterValue("navZ", 0);              // maintained by TransformManager
      setParameterValue("perspectiveModel", TransformManager.DEFAULT_PERSPECTIVE_MODEL);
      setParameterValue("picking", "identify");      // maintained by ActionManager
      setParameterValue("pickingStyle", "toggle");   // maintained by ActionManager
      setParameterValue("refreshing", true);         // maintained by Viewer
      setParameterValue("rotationRadius", 0);        // maintained by TransformManager
      setParameterValue("scaleAngstromsPerInch", 0); // maintained by TransformManager
      setParameterValue("scriptReportingLevel", 0);  // maintained by ScriptEvaluator
      setParameterValue("selectionHalos", false);    // maintained by ModelSet
      setParameterValue("showaxes", false);          // maintained by Axes
      setParameterValue("showboundbox", false);      // maintained by Bbcage
      setParameterValue("showfrank", false);         // maintained by Viewer
      setParameterValue("showUnitcell", false);      // maintained by Uccage
      setParameterValue("slabEnabled", false);       // maintained by TransformManager     
      setParameterValue("depth", 0);                 // maintained by TransformManager
      setParameterValue("slab", 100);                // maintained by TransformManager
      setParameterValue("slabrange", 0f);            // maintained by TransformManager
      setParameterValue("spinX", 0);                 // maintained by TransformManager
      setParameterValue("spinY", TransformManager.DEFAULT_SPIN_Y);
      setParameterValue("spinZ", 0);                 // maintained by TransformManager
      setParameterValue("spinFps", TransformManager.DEFAULT_SPIN_FPS);
      setParameterValue("stereoDegrees", JmolConstants.DEFAULT_STEREO_DEGREES); 
      setParameterValue("stateversion", 0); // only set by a saved state being recalled
      setParameterValue("windowCentered", true); // maintained by TransformManager
      setParameterValue("zoomEnabled", true);    // maintained by TransformManager
      setParameterValue("zShade", false);        // maintained by TransformManager
      

      // These next values have no other place than the global Hashtables.
      // This just means that a call to viewer.getXxxxProperty() is necessary.
      // Otherwise, it's the same as if they had a global variable. 
      // It's just an issue of speed of access. Generally, these should only be
      // accessed by the user. 
      
      setParameterValue("_version", getJmolVersionInt());

      setParameterValue("axesWindow", true);
      setParameterValue("axesMolecular", false);
      setParameterValue("axesPosition", false);
      setParameterValue("axesUnitcell", false);
      setParameterValue("backgroundModel", 0);
      setParameterValue("colorRasmol", false);
      setParameterValue("currentLocalPath", "");
      setParameterValue("defaultLattice", "{0 0 0}");
      setParameterValue("defaultColorScheme", "Jmol");
      setParameterValue("defaultDirectoryLocal", "");
      setParameterValue("defaults", "Jmol");
      setParameterValue("defaultVDW", "Jmol");
      setParameterValue("exportDrivers", JmolConstants.EXPORT_DRIVER_LIST);
      setParameterValue("propertyAtomNumberColumnCount", 0);
      setParameterValue("propertyAtomNumberField", 0);
      setParameterValue("propertyDataColumnCount", 0);
      setParameterValue("propertyDataField", 0);
      setParameterValue("undo", true);

      // OK, all of the rest of these are maintained here as global values (below)

      setParameterValue("allowEmbeddedScripts", allowEmbeddedScripts);
      setParameterValue("allowGestures", allowGestures);
      setParameterValue("allowKeyStrokes", allowKeyStrokes);
      setParameterValue("allowModelkit", allowModelkit);
      setParameterValue("allowMultiTouch", allowMultiTouch);
      setParameterValue("allowRotateSelected", allowRotateSelected);
      setParameterValue("allowMoveAtoms", allowMoveAtoms);
      setParameterValue("ambientPercent", ambientPercent);
      setParameterValue("animationFps", animationFps);
      setParameterValue("antialiasImages", antialiasImages);
      setParameterValue("antialiasDisplay", antialiasDisplay);
      setParameterValue("antialiasTranslucent", antialiasTranslucent);
      setParameterValue("appendNew", appendNew);
      setParameterValue("appletProxy", appletProxy);
      setParameterValue("applySymmetryToBonds", applySymmetryToBonds);
      setParameterValue("atomPicking", atomPicking);
      setParameterValue("atomTypes", atomTypes);
      setParameterValue("autoBond", autoBond);
      setParameterValue("autoFps", autoFps);
//      setParameterValue("autoLoadOrientation", autoLoadOrientation);
      setParameterValue("axesMode", axesMode);
      setParameterValue("axesScale", axesScale);
      setParameterValue("axesOrientationRasmol", axesOrientationRasmol);
      setParameterValue("bondModeOr", bondModeOr);
      setParameterValue("bondPicking", bondPicking);
      setParameterValue("bondRadiusMilliAngstroms", bondRadiusMilliAngstroms);
      setParameterValue("bondTolerance", bondTolerance);
      setParameterValue("cameraDepth", cameraDepth);
      setParameterValue("cartoonBaseEdges", cartoonBaseEdges);
      setParameterValue("cartoonRockets", cartoonRockets);
      setParameterValue("chainCaseSensitive", chainCaseSensitive);
      setParameterValue("dataSeparator", dataSeparator);
      setParameterValue("debugScript", debugScript);
      setParameterValue("defaultAngleLabel", defaultAngleLabel);
      setParameterValue("defaultDrawArrowScale", defaultDrawArrowScale);
      setParameterValue("defaultDirectory", defaultDirectory);
      setParameterValue("defaultDistanceLabel", defaultDistanceLabel);
      setParameterValue("defaultLoadFilter", defaultLoadFilter);
      setParameterValue("defaultLoadScript", defaultLoadScript);
      setParameterValue("defaultStructureDSSP", defaultStructureDSSP);
      setParameterValue("defaultTorsionLabel", defaultTorsionLabel);
      setParameterValue("defaultTranslucent", defaultTranslucent);
      setParameterValue("delayMaximumMs", delayMaximumMs);
      setParameterValue("diffusePercent", diffusePercent);
      setParameterValue("dipoleScale", dipoleScale);
      setParameterValue("disablePopupMenu", disablePopupMenu);
      setParameterValue("displayCellParameters", displayCellParameters);
      setParameterValue("dotDensity", dotDensity);
      setParameterValue("dotScale", dotScale);
      setParameterValue("dotsSelectedOnly", dotsSelectedOnly);
      setParameterValue("dotSurface", dotSurface);
      setParameterValue("dragSelected", dragSelected);
      setParameterValue("drawHover", drawHover);
      setParameterValue("drawPicking", drawPicking);
      setParameterValue("dsspCalculateHydrogenAlways", dsspCalcHydrogen);
      setParameterValue("dynamicMeasurements", dynamicMeasurements);
      setParameterValue("edsUrlFormat", edsUrlFormat);
      //setParameterValue("edsUrlOptions", edsUrlOptions);
      setParameterValue("edsUrlCutoff", edsUrlCutoff);
      setParameterValue("ellipsoidArcs", ellipsoidArcs);
      setParameterValue("ellipsoidAxes", ellipsoidAxes);
      setParameterValue("ellipsoidAxisDiameter", ellipsoidAxisDiameter);
      setParameterValue("ellipsoidBall", ellipsoidBall);
      setParameterValue("ellipsoidDotCount", ellipsoidDotCount);
      setParameterValue("ellipsoidDots", ellipsoidDots);
      setParameterValue("ellipsoidFill", ellipsoidFill);
//      setParameterValue("_fileCaching", _fileCaching);
//      setParameterValue("_fileCache", _fileCache);
      setParameterValue("fontScaling", fontScaling);
      setParameterValue("fontCaching", fontCaching);
      setParameterValue("forceAutoBond", forceAutoBond);
      setParameterValue("fractionalRelative", fractionalRelative);
      setParameterValue("greyscaleRendering", greyscaleRendering);
      setParameterValue("hbondsAngleMinimum", hbondsAngleMinimum);
      setParameterValue("hbondsDistanceMaximum", hbondsDistanceMaximum);
      setParameterValue("hbondsBackbone", hbondsBackbone);
      setParameterValue("hbondsRasmol", hbondsRasmol);
      setParameterValue("hbondsSolid", hbondsSolid);
      setParameterValue("helixStep", helixStep);
      setParameterValue("helpPath", helpPath);
      setParameterValue("hermiteLevel", hermiteLevel);
      setParameterValue("hideNameInPopup", hideNameInPopup);
      setParameterValue("hideNavigationPoint", hideNavigationPoint);
      setParameterValue("highResolution", highResolutionFlag);
      setParameterValue("hoverDelay", hoverDelayMs / 1000f);
      setParameterValue("imageState", imageState);
      setParameterValue("isosurfacePropertySmoothing",
          isosurfacePropertySmoothing);
      setParameterValue("isosurfacePropertySmoothingPower",
          isosurfacePropertySmoothingPower);
      setParameterValue("justifyMeasurements", justifyMeasurements);
      setParameterValue("legacyAutoBonding", legacyAutoBonding);
      setParameterValue("loadAtomDataTolerance", loadAtomDataTolerance);
      setParameterValue("loadFormat", loadFormat);
      setParameterValue("logCommands", logCommands);
      setParameterValue("logGestures", logGestures);
      setParameterValue("measureAllModels", measureAllModels);
      setParameterValue("measurementLabels", measurementLabels);
      setParameterValue("measurementUnits", measureDistanceUnits);
      setParameterValue("messageStyleChime", messageStyleChime);
      setParameterValue("minBondDistance", minBondDistance);
      setParameterValue("minimizationSteps", minimizationSteps);
      setParameterValue("minimizationRefresh", minimizationRefresh);
      setParameterValue("minimizationSilent", minimizationSilent);
      setParameterValue("minimizationCriterion", minimizationCriterion);
      setParameterValue("modelKitMode", modelKitMode);
      setParameterValue("monitorEnergy", monitorEnergy);
      setParameterValue("multipleBondRadiusFactor", multipleBondRadiusFactor);
      setParameterValue("multipleBondSpacing", multipleBondSpacing);
      setParameterValue("multiProcessor", multiProcessor && (Viewer.nProcessors > 1));
      setParameterValue("navigationMode", navigationMode);
      setParameterValue("navigateSurface", navigateSurface);
      setParameterValue("navigationPeriodic", navigationPeriodic);
      setParameterValue("navigationSpeed", navigationSpeed);
      setParameterValue("pdbGetHeader", pdbGetHeader); // new 11.5.39
      setParameterValue("pdbSequential", pdbSequential); // new 11.5.39
      setParameterValue("perspectiveDepth", perspectiveDepth);
      setParameterValue("percentVdwAtom", percentVdwAtom);
      setParameterValue("phongExponent", phongExponent);
      setParameterValue("pickingSpinRate", pickingSpinRate);
      setParameterValue("pickLabel", pickLabel);
      setParameterValue("pointGroupLinearTolerance", pointGroupLinearTolerance);
      setParameterValue("pointGroupDistanceTolerance", pointGroupDistanceTolerance);
      setParameterValue("preserveState", preserveState);
      setParameterValue("propertyColorScheme", propertyColorScheme);
      setParameterValue("quaternionFrame", quaternionFrame);
      setParameterValue("rangeSelected", rangeSelected);
      setParameterValue("repaintWaitMs", repaintWaitMs);
      setParameterValue("ribbonAspectRatio", ribbonAspectRatio);
      setParameterValue("ribbonBorder", ribbonBorder);
      setParameterValue("rocketBarrels", rocketBarrels);
      setParameterValue("saveProteinStructureState", saveProteinStructureState);
      setParameterValue("scriptqueue", useScriptQueue);
      setParameterValue("selectAllModels", selectAllModels);
      setParameterValue("selectHetero", rasmolHeteroSetting);
      setParameterValue("selectHydrogen", rasmolHydrogenSetting);
      setParameterValue("sheetSmoothing", sheetSmoothing);
      setParameterValue("showHiddenSelectionHalos", showHiddenSelectionHalos);
      setParameterValue("showHydrogens", showHydrogens);
      setParameterValue("showKeyStrokes", showKeyStrokes);
      setParameterValue("showMeasurements", showMeasurements);
      setParameterValue("showMultipleBonds", showMultipleBonds);
      setParameterValue("showNavigationPointAlways", showNavigationPointAlways);
      setParameterValue("showScript", scriptDelay);
      setParameterValue("slabByMolecule", slabByMolecule);
      setParameterValue("slabByAtom", slabByAtom);
      setParameterValue("smartAromatic", smartAromatic);
      setParameterValue("smallMoleculeMaxAtoms", smallMoleculeMaxAtoms);
      setParameterValue("smilesUrlFormat", smilesUrlFormat);
      setParameterValue("solventProbe", solventOn);
      setParameterValue("solventProbeRadius", solventProbeRadius);
      setParameterValue("specular", specular);
      setParameterValue("specularExponent", specularExponent);
      setParameterValue("specularPercent", specularPercent);
      setParameterValue("specularPower", specularPower);
      setParameterValue("ssbondsBackbone", ssbondsBackbone);
      setParameterValue("statusReporting", statusReporting);
      setParameterValue("strandCount", strandCountForStrands);
      setParameterValue("strandCountForStrands", strandCountForStrands);
      setParameterValue("strandCountForMeshRibbon", strandCountForMeshRibbon);
      setParameterValue("strutDefaultRadius", strutDefaultRadius);
      setParameterValue("strutLengthMaximum", strutLengthMaximum);
      setParameterValue("strutSpacing", strutSpacing);
      setParameterValue("strutsMultiple", strutsMultiple);
      setParameterValue("testFlag1", testFlag1);
      setParameterValue("testFlag2", testFlag2);
      setParameterValue("testFlag3", testFlag3);
      setParameterValue("testFlag4", testFlag4);
      setParameterValue("traceAlpha", traceAlpha);
      setParameterValue("useArcBall", useArcBall);
      setParameterValue("useMinimizationThread", useMinimizationThread);
      setParameterValue("useNumberLocalization", useNumberLocalization);
      setParameterValue("vectorScale", vectorScale);
      setParameterValue("vibrationPeriod", vibrationPeriod);
      setParameterValue("vibrationScale", vibrationScale);
      setParameterValue("visualRange", visualRange);
      setParameterValue("wireframeRotation", wireframeRotation);
      setParameterValue("zDepth", zDepth);
      setParameterValue("zeroBasedXyzRasmol", zeroBasedXyzRasmol);
      setParameterValue("zoomLarge", zoomLarge);
      setParameterValue("zShadePower", zShadePower);
      setParameterValue("zSlab", zSlab);  
    }

    //lighting (see Graphics3D.Shade3D

    int ambientPercent = 45;
    int diffusePercent = 84;
    boolean specular = true;
    int specularExponent = 6;  // log2 of phongExponent
    int phongExponent = 64;    // 2^specularExponent
    int specularPercent = 22;
    int specularPower = 40;
    int zDepth = 0;
    int zShadePower = 1;
    int zSlab = 0;
    
    boolean slabByMolecule = false;
    boolean slabByAtom = false;

    //file loading

    boolean allowEmbeddedScripts = true;
    boolean appendNew = true;
    String appletProxy = "";
    boolean applySymmetryToBonds = false; //new 11.1.29
    String atomTypes = "";
    boolean autoBond = true;
//    boolean autoLoadOrientation = false; // 11.7.30 for Spartan and Sygress/CAChe loading with or without rotation
       // starting with Jmol 12.0.RC10, this setting is ignored, and FILTER "NoOrient" is required if the file
       // is to be loaded without reference to the orientation saved in the file.
    boolean axesOrientationRasmol = false;
    short bondRadiusMilliAngstroms = JmolConstants.DEFAULT_BOND_MILLIANGSTROM_RADIUS;
    float bondTolerance = JmolConstants.DEFAULT_BOND_TOLERANCE;
    String defaultDirectory = "";
    boolean defaultStructureDSSP = true; // Jmol 12.1.15
    final Point3f ptDefaultLattice = new Point3f();
    String defaultLoadScript = "";
    String defaultLoadFilter = "";
//    boolean _fileCaching = false;
//    String _fileCache = "";
    boolean forceAutoBond = false;
    boolean fractionalRelative = false; // true: UNITCELL offset will change meaning of {1/2 1/2 1/2} 
    char inlineNewlineChar = '|'; //pseudo static
    String loadFormat = "http://www.rcsb.org/pdb/files/%FILE.pdb.gz";
    String smilesUrlFormat = "http://cactus.nci.nih.gov/chemical/structure/%FILE/file?format=sdf&get3d=True"; 
      
    //  "http://cheminfov.informatics.indiana.edu/rest/thread/d3.py/SMILES/%FILE";
    String edsUrlFormat = "http://eds.bmc.uu.se/eds/dfs/%LC13/%LCFILE/%LCFILE.omap";
    String edsUrlCutoff = "load('http://eds.bmc.uu.se/eds/dfs/%LC13/%LCFILE/%LCFILE.sfdat').lines.find('MAP_SIGMA').split(' ')[2]";
    String edsUrlOptions = "within 2.0 {*}";
    float minBondDistance = JmolConstants.DEFAULT_MIN_BOND_DISTANCE;
    boolean pdbGetHeader = false; // true to get PDB header in auxiliary info
    boolean pdbSequential = false; // true for no bonding check
    int percentVdwAtom = JmolConstants.DEFAULT_PERCENT_VDW_ATOM;
    int smallMoleculeMaxAtoms = 40000;
    boolean smartAromatic = true;
    boolean zeroBasedXyzRasmol = false;
    boolean legacyAutoBonding = false;

    /**
     *  these settings are determined when the file is loaded and are
     *  kept even though they might later change. So we list them here
     *  and ALSO let them be defined in the settings. 10.9.98 missed this. 
     *  
     * @return script command
     */
    String getLoadState() {
      
      
      // some commands register flags so that they will be 
      // restored in a saved state definition, but will not execute
      // now so that there is no chance any embedded scripts or
      // default load scripts will run and slow things down.
      StringBuffer str = new StringBuffer();
      appendCmd(str, "set allowEmbeddedScripts false");
      if (allowEmbeddedScripts)
        setParameterValue("allowEmbeddedScripts", true);
      appendCmd(str, "set appendNew " + appendNew);
      appendCmd(str, "set appletProxy " + Escape.escape(appletProxy));
      appendCmd(str, "set applySymmetryToBonds " + applySymmetryToBonds);
      if (atomTypes.length() > 0)
        appendCmd(str, "set atomTypes " + Escape.escape(atomTypes));
      appendCmd(str, "set autoBond " + autoBond);
//      appendCmd(str, "set autoLoadOrientation " + autoLoadOrientation);
      if (axesOrientationRasmol)
        appendCmd(str, "set axesOrientationRasmol true");
      appendCmd(str, "set bondRadiusMilliAngstroms " + bondRadiusMilliAngstroms);
      appendCmd(str, "set bondTolerance " + bondTolerance);
      appendCmd(str, "set defaultLattice " + Escape.escape(ptDefaultLattice));
      appendCmd(str, "set defaultLoadFilter " + Escape.escape(defaultLoadFilter)) ;
      appendCmd(str, "set defaultLoadScript \"\"");
      if (defaultLoadScript.length() > 0)
        setParameterValue("defaultLoadScript", defaultLoadScript);
      appendCmd(str, "set defaultStructureDssp " + defaultStructureDSSP);
      String sMode = viewer.getDefaultVdwTypeNameOrData(Integer.MIN_VALUE);
      appendCmd(str, "set defaultVDW " + sMode);
      if (sMode.equals("User"))
        appendCmd(str, viewer.getDefaultVdwTypeNameOrData(Integer.MAX_VALUE));
      appendCmd(str, "set forceAutoBond " + forceAutoBond);
      appendCmd(str, "#set defaultDirectory " + Escape.escape(defaultDirectory));
      appendCmd(str, "#set loadFormat " + Escape.escape(loadFormat));
      appendCmd(str, "#set smilesUrlFormat " + Escape.escape(smilesUrlFormat));
      appendCmd(str, "#set edsUrlFormat " + Escape.escape(edsUrlFormat));
      appendCmd(str, "#set edsUrlCutoff " + Escape.escape(edsUrlCutoff));
//      if (autoLoadOrientation)
  //      appendCmd(str, "set autoLoadOrientation true");
      appendCmd(str, "set legacyAutoBonding " + legacyAutoBonding);
      appendCmd(str, "set minBondDistance " + minBondDistance);
      // these next two might be part of a 2D->3D operation
      appendCmd(str, "set minimizationCriterion  " + minimizationCriterion);
      appendCmd(str, "set minimizationSteps  " + minimizationSteps);
      appendCmd(str, "set pdbGetHeader " + pdbGetHeader);
      appendCmd(str, "set pdbSequential " + pdbSequential);
      appendCmd(str, "set percentVdwAtom " + percentVdwAtom);
      appendCmd(str, "set smallMoleculeMaxAtoms " + smallMoleculeMaxAtoms);
      appendCmd(str, "set smartAromatic " + smartAromatic);
      if (zeroBasedXyzRasmol)
        appendCmd(str, "set zeroBasedXyzRasmol true");
      return str.toString();
    }

    void setDefaultLattice(Point3f ptLattice) {
      ptDefaultLattice.set(ptLattice);
    }

    Point3f getDefaultLattice() {
      return ptDefaultLattice;
    }

    //centering and perspective

    boolean allowRotateSelected = false;
    boolean allowMoveAtoms = false;
    boolean perspectiveDepth = true;
    float visualRange = 5f;

    //solvent

    boolean solventOn = false;

    //measurements

    String defaultAngleLabel = "%VALUE %UNITS";
    String defaultDistanceLabel = "%VALUE %UNITS"; //also %_ and %a1 %a2 %m1 %m2, etc.
    String defaultTorsionLabel = "%VALUE %UNITS";
    boolean justifyMeasurements = false;
    boolean measureAllModels = false;

    // minimization  // 11.5.21 03/2008

    int minimizationSteps = 100;
    boolean minimizationRefresh = true;
    boolean minimizationSilent = false;
    float minimizationCriterion = 0.001f;

    //rendering

    boolean antialiasDisplay = false;
    boolean antialiasImages = true;
    boolean imageState = true;
    boolean antialiasTranslucent = true;
    boolean displayCellParameters = true;
    boolean dotsSelectedOnly = false;
    boolean dotSurface = true;
    int dotDensity = 3;
    int dotScale = 1;
    boolean dynamicMeasurements = false;
    boolean greyscaleRendering = false;
    boolean isosurfacePropertySmoothing = true;
    int isosurfacePropertySmoothingPower = 7;
    int repaintWaitMs = 1000;
    boolean showHiddenSelectionHalos = false;
    boolean showKeyStrokes = true;
    boolean showMeasurements = true;
    boolean zoomLarge = true; //false would be like Chime
    String backgroundImageFileName;
    
    //atoms and bonds

    boolean bondModeOr = false;
    boolean hbondsBackbone = false;
    float hbondsAngleMinimum = 90f;
    float hbondsDistanceMaximum = 3.25f;
    boolean hbondsRasmol = true; // 12.0.RC3
    boolean hbondsSolid = false;
    byte modeMultipleBond = JmolConstants.MULTIBOND_NOTSMALL;
    boolean showHydrogens = true;
    boolean showMultipleBonds = true;
    boolean ssbondsBackbone = false;
    float multipleBondSpacing = -1;     // 0.35?
    float multipleBondRadiusFactor = 0; // 0.75?

    //secondary structure + Rasmol

    boolean cartoonBaseEdges = false;
    boolean cartoonRockets = false;
    boolean chainCaseSensitive = false;
    int hermiteLevel = 0;
    boolean highResolutionFlag = false;
    boolean rangeSelected = false;
    boolean rasmolHydrogenSetting = true;
    boolean rasmolHeteroSetting = true;
    int ribbonAspectRatio = 16;
    boolean ribbonBorder = false;
    boolean rocketBarrels = false;
    float sheetSmoothing = 1; // 0: traceAlpha on alphas for helix, 1 on midpoints
    boolean traceAlpha = true;

    //misc

    boolean allowGestures = false;
    boolean allowModelkit = true;
    boolean allowMultiTouch = true; // but you still need to set the parameter multiTouchSparshUI=true
    boolean allowKeyStrokes = false;
    int animationFps = 10;
    boolean autoFps = false;
    int axesMode = JmolConstants.AXES_MODE_BOUNDBOX;
    float axesScale = 2;
    float cameraDepth = 3.0f;
    String dataSeparator = "~~~";
    boolean debugScript = false;
    float defaultDrawArrowScale = 0.5f;
    float defaultTranslucent = 0.5f;
    int delayMaximumMs = 0;
    float dipoleScale = 1.0f;
    boolean disablePopupMenu = false;
    boolean dragSelected = false;
    boolean drawHover = false;
    boolean drawPicking = false;
    boolean dsspCalcHydrogen = true;
    boolean bondPicking = false;
    boolean atomPicking = true;
    String helpPath = JmolConstants.DEFAULT_HELP_PATH;
    boolean fontScaling = false;
    boolean fontCaching = true;
    int helixStep = 1;
    boolean hideNameInPopup = false;
    int hoverDelayMs = 500;
    float loadAtomDataTolerance = 0.01f;
    boolean logCommands = false;
    boolean logGestures = false;
    boolean measurementLabels = true;
    boolean messageStyleChime = false;
    boolean monitorEnergy = false;
    boolean multiProcessor = true;
    int pickingSpinRate = 10;
    String pickLabel = "";
    float pointGroupDistanceTolerance = 0.2f;
    float pointGroupLinearTolerance = 8.0f;
    boolean preserveState = true;
    String propertyColorScheme = "roygb";
    String quaternionFrame = "p"; // was c prior to Jmol 11.7.47
    boolean saveProteinStructureState = true;
    float solventProbeRadius = 1.2f;
    int scriptDelay = 0;
    boolean selectAllModels = true;
    boolean statusReporting = true;
    int strandCountForStrands = 5;
    int strandCountForMeshRibbon = 7;
    int strutSpacing = 6;
    float strutLengthMaximum = 7.0f;
    float strutDefaultRadius = JmolConstants.DEFAULT_STRUT_RADIUS;
    boolean strutsMultiple = false; //on a single position    
    boolean useArcBall = false;
    boolean useMinimizationThread = true;
    boolean useNumberLocalization = true;
    boolean useScriptQueue = true;
    boolean waitForMoveTo = true; // Jmol 11.9.24
    float vectorScale = 1f;
    float vibrationPeriod = 1f;
    float vibrationScale = 1f;
    boolean wireframeRotation = false;

    // window

    boolean hideNavigationPoint = false;
    boolean navigationMode = false;
    boolean navigateSurface = false;
    boolean navigationPeriodic = false;
    float navigationSpeed = 5;
    boolean showNavigationPointAlways = false;
    String stereoState = null;
    boolean modelKitMode = false;

    // special persistent object characteristics -- bbcage, uccage, axes:

    int[] objColors = new int[OBJ_MAX];
    boolean[] objStateOn = new boolean[OBJ_MAX];
    int[] objMad = new int[OBJ_MAX];

    boolean ellipsoidAxes = false;
    boolean ellipsoidDots = false;
    boolean ellipsoidArcs = false;
    boolean ellipsoidFill = false;
    boolean ellipsoidBall = true;

    int ellipsoidDotCount = 200;
    float ellipsoidAxisDiameter = 0.02f;

    String getWindowState(StringBuffer sfunc) {
      StringBuffer str = new StringBuffer();
      if (sfunc != null) {
        sfunc
            .append("  initialize;\n  set refreshing false;\n  _setWindowState;\n");
        str.append("\nfunction _setWindowState() {\n");
      }
      str.append("# height " + viewer.getScreenHeight() + ";\n# width "
          + viewer.getScreenWidth() + ";\n");
      appendCmd(str, "stateVersion = " + getParameter("_version"));
      appendCmd(str, "background " + Escape.escapeColor(objColors[0]));
      for (int i = 1; i < OBJ_MAX; i++)
        if (objColors[i] != 0)
          appendCmd(str, getObjectNameFromId(i) + "Color = \""
              + Escape.escapeColor(objColors[i]) + '"');
      if (backgroundImageFileName != null)
        appendCmd(str, "background IMAGE /*file*/" + Escape.escape(backgroundImageFileName));
      str.append(getSpecularState());
      appendCmd(str, "statusReporting  = " + statusReporting);
      if (sfunc != null)
        str.append("}\n\n");
      return str.toString();
    }

    String getSpecularState() {
      StringBuffer str = new StringBuffer("");
      appendCmd(str, "set ambientPercent " + Graphics3D.getAmbientPercent());
      appendCmd(str, "set diffusePercent " + Graphics3D.getDiffusePercent());
      appendCmd(str, "set specular " + Graphics3D.getSpecular());
      appendCmd(str, "set specularPercent " + Graphics3D.getSpecularPercent());
      appendCmd(str, "set specularPower " + Graphics3D.getSpecularPower());
      int se = Graphics3D.getSpecularExponent();
      int pe = Graphics3D.getPhongExponent();
      if (Math.pow(2, se) == pe)
        appendCmd(str, "set specularExponent " + se);
      else
        appendCmd(str, "set phongExponent " + pe);        
      appendCmd(str, "set zShadePower " + Graphics3D.getZShadePower());
      return str.toString();
    }

    //testing

    boolean testFlag1 = false;
    boolean testFlag2 = false;
    boolean testFlag3 = false;
    boolean testFlag4 = false;

    //controlled access:

    private String measureDistanceUnits = "nanometers";

    void setMeasureDistanceUnits(String units) {
      if (units.equalsIgnoreCase("angstroms"))
        measureDistanceUnits = "angstroms";
      else if (units.equalsIgnoreCase("nanometers")
          || units.equalsIgnoreCase("nm"))
        measureDistanceUnits = "nanometers";
      else if (units.equalsIgnoreCase("picometers")
          || units.equalsIgnoreCase("pm"))
        measureDistanceUnits = "picometers";
      else if (units.equalsIgnoreCase("bohr") || units.equalsIgnoreCase("au"))
        measureDistanceUnits = "au";
      setParameterValue("measurementUnits", measureDistanceUnits);
    }

    String getMeasureDistanceUnits() {
      return measureDistanceUnits;
    }

    boolean isJmolVariable(String key) {
      return key.charAt(0) == '_'
          || htNonbooleanParameterValues.containsKey(key = key.toLowerCase())
          || htBooleanParameterFlags.containsKey(key)
          || unreportedProperties.indexOf(";" + key + ";") >= 0;
    }

    private void resetParameterStringValue(String name, GlobalSettings g) {
      setParameterValue(name, g == null ? "" : (String) g.getParameter(name));
    }
    
    void setParameterValue(String name, boolean value) {
      name = name.toLowerCase();
      if (htNonbooleanParameterValues.containsKey(name))
        return; // don't allow setting boolean of a numeric
      htBooleanParameterFlags.put(name, value ? Boolean.TRUE : Boolean.FALSE);
    }

    void setParameterValue(String name, int value) {
      name = name.toLowerCase();
      if (htBooleanParameterFlags.containsKey(name))
        return; // don't allow setting numeric of a boolean
      htNonbooleanParameterValues.put(name, Integer.valueOf(value));
    }

    void setParameterValue(String name, float value) {
      if (Float.isNaN(value))
        return;
      name = name.toLowerCase();
      if (htBooleanParameterFlags.containsKey(name))
        return; // don't allow setting numeric of a boolean
      htNonbooleanParameterValues.put(name, new Float(value));
    }

    void setParameterValue(String name, String value) {
      name = name.toLowerCase();
      if (value == null || htBooleanParameterFlags.containsKey(name))
        return; // don't allow setting string of a boolean
      htNonbooleanParameterValues.put(name, value);
    }

    void removeJmolParameter(String key) {
      if (htBooleanParameterFlags.containsKey(key)) {
        htBooleanParameterFlags.remove(key);
        if (!htPropertyFlagsRemoved.containsKey(key))
          htPropertyFlagsRemoved.put(key, Boolean.FALSE);
        return;
      }
      if (htNonbooleanParameterValues.containsKey(key))
        htNonbooleanParameterValues.remove(key);
    }

    ScriptVariable setUserVariable(String key, ScriptVariable var) {
      if (var == null) 
        return null;
      key = key.toLowerCase();
      htUserVariables.put(key, var.setName(key).setGlobal());
      return var;
    }

    void unsetUserVariable(String key) {
      key = key.toLowerCase();
        if (key.equals("all") || key.equals("variables")) {
          htUserVariables.clear();
          Logger.info("all user-defined variables deleted");
        } else if (htUserVariables.containsKey(key)) {
          Logger.info("variable " + key + " deleted");
          htUserVariables.remove(key);
        }
    }

    void removeUserVariable(String key) {
      htUserVariables.remove(key);
    }

    ScriptVariable getUserVariable(String name) {
      if (name == null)
        return null;
      name = name.toLowerCase();
      return htUserVariables.get(name);
    }

    String getParameterEscaped(String name, int nMax) {
      name = name.toLowerCase();
      if (htNonbooleanParameterValues.containsKey(name)) {
        Object v = htNonbooleanParameterValues.get(name);
        return varClip(name, Escape.escape(v), nMax);
      }
      if (htBooleanParameterFlags.containsKey(name))
        return htBooleanParameterFlags.get(name).toString();
      if (htUserVariables.containsKey(name))
        return htUserVariables.get(name).escape();
      if (htPropertyFlagsRemoved.containsKey(name))
        return "false";
      return "<not defined>";
    }

    /**
     * 
     * strictly a getter -- returns "" if not found
     * 
     * @param name
     * @return      a Integer, Float, String, BitSet, or Variable
     */
    Object getParameter(String name) {
      Object v = getParameter(name, false);
      return (v == null ? "" : v);
    }

    /**
     *  
     * 
     * @param name
     * @param doSet
     * @return     a new variable if possible, but null if "_xxx"
     * 
     */
    ScriptVariable getOrSetNewVariable(String name, boolean doSet) {
      if (name == null || name.length() == 0)
        name = "x";
      Object v = getParameter(name, true);
      return (v == null && doSet && name.charAt(0) != '_' ?
        setUserVariable(name, new ScriptVariable())
         : ScriptVariable.getVariable(v));
    }

    Object getParameter(String name, boolean asVariable) {
      name = name.toLowerCase();
      if (name.equals("_memory")) {
        Runtime runtime = Runtime.getRuntime();
        float bTotal = runtime.totalMemory() / 1000000f;
        float bFree = runtime.freeMemory() / 1000000f;
        String value = TextFormat.formatDecimal(bTotal - bFree, 1) + "/"
            + TextFormat.formatDecimal(bTotal, 1);
        htNonbooleanParameterValues.put("_memory", value);
      }
      if (htNonbooleanParameterValues.containsKey(name))
        return htNonbooleanParameterValues.get(name);
      if (htBooleanParameterFlags.containsKey(name))
        return htBooleanParameterFlags.get(name);
      if (htPropertyFlagsRemoved.containsKey(name))
        return Boolean.FALSE;
      if (htUserVariables.containsKey(name)) {
        ScriptVariable v = htUserVariables.get(name);
        return (asVariable ? v : ScriptVariable.oValue(v));
      }
      return null;
    }

    String getAllSettings(String prefix) {
      StringBuffer commands = new StringBuffer("");
      Iterator<String> e;
      String key;
      String[] list = new String[htBooleanParameterFlags.size()
          + htNonbooleanParameterValues.size()];
      //booleans
      int n = 0;
      String _prefix = "_" + prefix;
      e = htBooleanParameterFlags.keySet().iterator();
      while (e.hasNext()) {
        key = e.next();
        if (prefix == null || key.indexOf(prefix) == 0
            || key.indexOf(_prefix) == 0)
          list[n++] = (key.indexOf("_") == 0 ? key + " = " : "set " + key + " ")
              + htBooleanParameterFlags.get(key);
      }
      //save as _xxxx if you don't want "set" to be there first
      e = htNonbooleanParameterValues.keySet().iterator();
      while (e.hasNext()) {
        key = e.next();
        if (key.charAt(0) != '@'
            && (prefix == null || key.indexOf(prefix) == 0 || key
                .indexOf(_prefix) == 0)) {
          Object value = htNonbooleanParameterValues.get(key);
          if (value instanceof String)
            value = chop(Escape.escape((String) value));
          list[n++] = (key.indexOf("_") == 0 ? key + " = " : "set " + key + " ")
              + value;
        }
      }
      Arrays.sort(list, 0, n);
      for (int i = 0; i < n; i++)
        if (list[i] != null)
          appendCmd(commands, list[i]);
      commands.append("\n");
      return commands.toString();
    }

    private String chop(String s) {
      int len = s.length();
      if (len < 512)
        return s;
      StringBuilder sb = new StringBuilder();
      String sep = "\"\\\n    + \"";
      int pt = 0;
      for (int i = 72; i < len; pt = i, i += 72) {
        while (s.charAt(i - 1) == '\\')
          i++;
        sb.append((pt == 0 ? "" : sep)).append(s.substring(pt, i));
      }
      sb.append(sep).append(s.substring(pt, len));
      return sb.toString();
    }

    String getState(StringBuffer sfunc) {
      String[] list = new String[htBooleanParameterFlags.size()
          + htNonbooleanParameterValues.size()];
      StringBuffer commands = new StringBuffer();
      boolean isState = (sfunc != null);
      if (isState) {
        sfunc.append("  _setVariableState;\n");
        commands.append("function _setVariableState() {\n\n");
      }
      int n = 0;
      Iterator<String> e;
      String key;
      //booleans
      e = htBooleanParameterFlags.keySet().iterator();
      while (e.hasNext()) {
        key = e.next();       
        if (doReportProperty(key))
          list[n++] = "set " + key + " " + htBooleanParameterFlags.get(key);
      }
      e = htNonbooleanParameterValues.keySet().iterator();
      while (e.hasNext()) {
        key = e.next();
        if (key.charAt(0) != '@' && doReportProperty(key)) {
          Object value = htNonbooleanParameterValues.get(key);
          if (key.charAt(0) == '=') {
            //save as =xxxx if you don't want "set" to be there first
            // (=color [element], =frame ...; set unitcell) -- see Viewer.java
            key = key.substring(1);
          } else {
            if (key.indexOf("default") == 0)
              key = " set " + key;
            else
              key = "set " + key;
            value = Escape.escape(value);
          }
          list[n++] = key + " " + value;
        }
      }
      switch (axesMode) {
      case JmolConstants.AXES_MODE_UNITCELL:
        list[n++] = "set axes unitcell";
        break;
      case JmolConstants.AXES_MODE_BOUNDBOX:
        list[n++] = "set axes window";
        break;
      default:
        list[n++] = "set axes molecular";
      }

      //nonboolean variables:
      e = htNonbooleanParameterValues.keySet().iterator();
      while (e.hasNext()) {
        key = e.next();
        if (key.charAt(0) == '@')
          list[n++] = key + " " + htNonbooleanParameterValues.get(key);
      }
      Arrays.sort(list, 0, n);
      for (int i = 0; i < n; i++)
        if (list[i] != null)
          appendCmd(commands, list[i]);
/*
      commands.append("\n#user-defined variables; \n");
      commands.append(StateManager.getVariableList(htUserVariables, 0, !isState));
*/
      // label defaults

      viewer.loadShape(JmolConstants.SHAPE_LABELS);
      commands.append(viewer.getShapeProperty(JmolConstants.SHAPE_LABELS,
          "defaultState"));

      // structure defaults
      
      if (haveSetStructureList) {
        commands.append("struture HELIX set " + Escape.escape(structureList[JmolConstants.PROTEIN_STRUCTURE_HELIX]));
        commands.append("struture SHEET set " + Escape.escape(structureList[JmolConstants.PROTEIN_STRUCTURE_SHEET]));
        commands.append("struture TURN set " + Escape.escape(structureList[JmolConstants.PROTEIN_STRUCTURE_TURN]));
      }
      if (sfunc != null)
        commands.append("\n}\n\n");
      return commands.toString();
    }

    private boolean doReportProperty(String name) {
      return (name.charAt(0) != '_' && unreportedProperties.indexOf(";" + name
          + ";") < 0);
    }

    String getVariableList() {
      return StateManager.getVariableList(htUserVariables, 0, true);
    }

    // static because we don't plan to be changing these
    private float[][] structureList = new float[][] {
      null, // none
      new float[] { // turn
          30, 90, -15, 95,
        },
      new float[] { // sheet
          -180, -10,   70,  180, 
          -180, -45, -180, -130, 
           140, 180,   90, 180, 
        },
      new float[] {  // helix
        -160, 0, -100, 45,
      },
    };
    
    private boolean haveSetStructureList;
    
    public void setStructureList(float[] list, int type) {
      haveSetStructureList = true;
      structureList[type] = list;
    }
    
    public float[][] getStructureList() {
      return structureList;
    }
  }

  ///////// state serialization 

  public static void setStateInfo(Map<String, BitSet> ht,
                                  int i1, int i2, String key) {
    BitSet bs;
    if (ht.containsKey(key)) {
      bs = ht.get(key);
    } else {
      bs = new BitSet();
      ht.put(key, bs);
    }
    for (int i = i1; i <= i2; i++)
      bs.set(i);
  }

  public static String varClip(String name, String sv, int nMax) {
    if (nMax > 0 && sv.length() > nMax)
      sv = sv.substring(0, nMax) + " #...more (" + sv.length()
          + " bytes -- use SHOW " + name + " or MESSAGE @" + name
          + " to view)";
    return sv;
  }

  public static String getCommands(Map<String, BitSet> ht) {
    return getCommands(ht, null, "select");
  }

  public static String getCommands(Map<String, BitSet> htDefine,
                                   Map<String, BitSet> htMore) {
    return getCommands(htDefine, htMore, "select");
  }

  public static String getCommands(Map<String, BitSet> htDefine,
                                   Map<String, BitSet> htMore,
                                   String selectCmd) {
    StringBuffer s = new StringBuffer();
    String setPrev = getCommands(htDefine, s, null, selectCmd);
    if (htMore != null)
      getCommands(htMore, s, setPrev, "select");
    return s.toString();
  }

  private static String getCommands(Map<String, BitSet> ht,
                                    StringBuffer s,
                                    String setPrev, String selectCmd) {
    if (ht == null)
      return "";
    for (Map.Entry<String, BitSet> entry : ht.entrySet()) {
      String key = entry.getKey();
      String set = Escape.escape(entry.getValue());
      if (set.length() < 5) // nothing selected
        continue;
      set = selectCmd + " " + set;
      if (!set.equals(setPrev))
        appendCmd(s, set);
      setPrev = set;
      if (key.indexOf("-") != 0) // - for key means none required
        appendCmd(s, key);
    }
    return setPrev;
  }

  public static void appendCmd(StringBuffer s, String cmd) {
    if (cmd.length() == 0)
      return;
    s.append("  ").append(cmd).append(";\n");
  }
}
