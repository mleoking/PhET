/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-03-31 11:39:51 -0700 (Thu, 31 Mar 2011) $
 * $Revision: 15365 $
 *
 * Copyright (C) 2002-2006  Miguel, Jmol Development, www.jmol.org
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

import org.jmol.popup.JmolPopup;
import org.jmol.script.ParallelProcessor;
import org.jmol.script.ScriptCompiler;
import org.jmol.script.ScriptContext;
import org.jmol.script.ScriptEvaluator;
import org.jmol.script.ScriptFunction;
import org.jmol.script.ScriptVariable;
import org.jmol.script.Token;
import org.jmol.shape.Shape;
import org.jmol.i18n.GT;
import org.jmol.modelset.Atom;
import org.jmol.modelset.AtomCollection;
import org.jmol.modelset.Bond;
import org.jmol.modelset.BoxInfo;
import org.jmol.modelset.Group;
import org.jmol.modelset.MeasurementPending;
import org.jmol.modelset.ModelLoader;
import org.jmol.modelset.ModelSet;
import org.jmol.modelset.Bond.BondSet;
import org.jmol.modelset.ModelCollection.StateScript;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.*;
import org.jmol.atomdata.AtomData;
import org.jmol.atomdata.AtomDataServer;
import org.jmol.atomdata.RadiusData;
import org.jmol.g3d.*;
import org.jmol.util.Base64;
import org.jmol.util.BitSetUtil;
import org.jmol.util.CifDataReader;
import org.jmol.util.ColorEncoder;
import org.jmol.util.CommandHistory;
import org.jmol.util.Elements;
import org.jmol.util.Escape;
import org.jmol.util.JpegEncoder;
import org.jmol.util.Logger;
import org.jmol.util.OutputStringBuffer;
import org.jmol.util.Parser;
import org.jmol.util.SurfaceFileTyper;

import org.jmol.util.Measure;
import org.jmol.util.Quaternion;
import org.jmol.util.TempArray;
import org.jmol.util.TextFormat;
import org.jmol.viewer.StateManager.Orientation;
import org.jmol.viewer.binding.Binding;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point4f;
import javax.vecmath.Point3i;
import javax.vecmath.Matrix4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.AxisAngle4f;

import java.net.URL;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;

/*
 * 
 * ****************************************************************
 * The JmolViewer can be used to render client molecules. Clients implement the
 * JmolAdapter. JmolViewer uses this interface to extract information from the
 * client data structures and render the molecule to the supplied
 * java.awt.Component
 * 
 * The JmolViewer runs on Java 1.5+ virtual machines. The 3d graphics rendering
 * package is a software implementation of a z-buffer. It does not use Java3D
 * and does not use Graphics2D from Java 1.2. 
 * 
 * public here is a test for applet-applet and JS-applet communication the idea
 * being that applet.getProperty("jmolViewer") returns this Viewer object,
 * allowing direct inter-process access to public methods.
 * 
 * e.g.
 * 
 * applet.getProperty("jmolApplet").getFullPathName()
 * 
 * 
 * This viewer can also be used with JmolData.jar, which is a 
 * frameless version of Jmol that can be used to batch-process
 * scripts from the command line. No shapes, no labels, no export
 * to JPG -- just raw data checking and output. 
 * 
 * 
 * 
 * ****************************************************************
 */

public class Viewer extends JmolViewer implements AtomDataServer {

  @Override
  protected void finalize() throws Throwable {
    Logger.debug("viewer finalize " + this);
    super.finalize();
  }

  // these are all private now so we are certain they are not
  // being accesed by any other classes

  private Container display;
  private Graphics3D g3d;
  private JmolAdapter modelAdapter;

  @Override
  public JmolAdapter getModelAdapter() {
    if (modelAdapter == null)
      modelAdapter = new SmarterJmolAdapter();
    return modelAdapter;
  }

  private CommandHistory commandHistory = new CommandHistory();
  private ColorManager colorManager;

  public ScriptCompiler compiler;
  public Map<String, Object> definedAtomSets;

  private SymmetryInterface symmetry;

  public SymmetryInterface getSymmetry() {
    if (symmetry == null)
      symmetry = (SymmetryInterface) Interface
          .getOptionInterface("symmetry.Symmetry");
    return symmetry;
  }

  public Object getSymmetryInfo(BitSet bsAtoms, String xyz, int op, Point3f pt,
                                Point3f pt2, String id, int type) {
    return modelSet.getSymmetryInfo(bsAtoms, xyz, op, pt, pt2, id, type);
  }

  private void clearModelDependentObjects() {
    setFrameOffsets(null);
    stopMinimization();
    minimizer = null;
    if (smilesMatcher != null) {
      smilesMatcher = null;
    }
    if (symmetry != null) {
      symmetry = null;
    }
  }

  private SmilesMatcherInterface smilesMatcher;

  public SmilesMatcherInterface getSmilesMatcher() {
    if (smilesMatcher == null) {
      smilesMatcher = (SmilesMatcherInterface) Interface
          .getOptionInterface("smiles.SmilesMatcher");
    }
    return smilesMatcher;
  }

  @Override
  public BitSet getSmartsMatch(String smarts, BitSet bsSelected) {
    if (bsSelected == null)
      bsSelected = getSelectionSet(false);
    return getSmilesMatcher().getSubstructureSet(smarts,
        getModelSet().atoms, getAtomCount(), bsSelected,
        true, false);
  }

  ScriptEvaluator eval;
  private AnimationManager animationManager;
  private DataManager dataManager;
  private FileManager fileManager;
  private ActionManager actionManager;
  private ShapeManager shapeManager;
  private ModelManager modelManager;
  private ModelSet modelSet;
  private MouseManager14 mouseManager;
  private RepaintManager repaintManager;
  private ScriptManager scriptManager;
  private SelectionManager selectionManager;
  private StateManager stateManager;
  private StateManager.GlobalSettings global;

  StateManager.GlobalSettings getGlobalSettings() {
    return global;
  }

  private StatusManager statusManager;
  private TempArray tempManager;
  private TransformManager transformManager;

  private String strJavaVendor;
  private String strJavaVersion;
  private String strOSName;
  private String htmlName = "";

  private String fullName = "";
  private String syncId = "";
  private String appletDocumentBase = "";
  private String appletCodeBase = "";
  private String logFilePath = "";

  // private boolean jvm11orGreater = false;
  // private boolean jvm12orGreater = false;
  // private boolean jvm14orGreater = false;
  private boolean multiTouch = false;

  private Viewer(Container display, JmolAdapter modelAdapter,
      String commandOptions) {
    // use allocateViewer
    if (Logger.debugging) {
      Logger.debug("Viewer constructor " + this);
    }
    g3d = new Graphics3D(display);
    isDataOnly = (display == null);
    haveDisplay = (!isDataOnly && (commandOptions == null || commandOptions.indexOf("-n") < 0));
    mustRender = haveDisplay;
    if (!haveDisplay)
      display = null;
    this.display = display;
    this.modelAdapter = modelAdapter;
    strJavaVendor = System.getProperty("java.vendor");
    strOSName = System.getProperty("os.name");
    strJavaVersion = System.getProperty("java.version");
    // Netscape on MacOS does not implement 1.1 event model
    // jvm11orGreater = (strJavaVersion.compareTo("1.1") >= 0 && !(strJavaVendor
    // .startsWith("Netscape")
    // && strJavaVersion.compareTo("1.1.5") <= 0 &&
    // "Mac OS".equals(strOSName)));
    // jvm12orGreater = (strJavaVersion.compareTo("1.2") >= 0);
    // jvm14orGreater = (strJavaVersion.compareTo("1.4") >= 0);

    // jvm14orGreater = jvm12orGreater = jvm11orGreater = true;

    // NOTE: Jmol 12.0 will only run with 1.5 or greater because of
    // .contains and parallel processes.

    multiTouch = (commandOptions != null && commandOptions
        .indexOf("-multitouch") >= 0);
    stateManager = new StateManager(this);
    colorManager = new ColorManager(this, g3d);
    statusManager = new StatusManager(this);
    scriptManager = new ScriptManager(this);
    transformManager = new TransformManager11(this);
    selectionManager = new SelectionManager(this);
    if (haveDisplay) {
      if (multiTouch) {
        if (commandOptions.indexOf("-multitouch-sparshui-simulated") < 0) {
          int[] pixels = new int[1];
          Image image = Toolkit.getDefaultToolkit().createImage(
              new MemoryImageSource(1, 1, pixels, 0, 1));
          Cursor transparentCursor = Toolkit.getDefaultToolkit()
              .createCustomCursor(image, new Point(0, 0), "invisibleCursor");
          display.setCursor(transparentCursor);
        }
        actionManager = (ActionManager) Interface
            .getInterface("multitouch.ActionManagerMT");
      } else {
        actionManager = new ActionManager();
      }
      actionManager.setViewer(this, commandOptions);
      mouseManager = new MouseManager14(this, actionManager);
    }
    modelManager = new ModelManager(this);
    shapeManager = new ShapeManager(this);
    tempManager = new TempArray();
    dataManager = new DataManager(this);
    animationManager = new AnimationManager(this);
    repaintManager = new RepaintManager(this, shapeManager);
    initialize();
    fileManager = new FileManager(this);
    compiler = new ScriptCompiler(this);
    definedAtomSets = new Hashtable<String, Object>();
    eval = new ScriptEvaluator(this);
  }

  /**
   * NOTE: for APPLICATION AND APPLET call
   * 
   * setModeMouse(JmolConstants.MOUSE_NONE);
   * 
   * before setting viewer=null
   * 
   * in order to remove references to display window in listeners and
   * hoverWatcher
   * 
   * This is the main access point for creating an application or applet viewer.
   * 
   * @param display
   *          either DisplayPanel or WrappedApplet
   * @param modelAdapter
   *          the model reader
   * @param fullName
   *          or null
   * @param documentBase
   *          or null
   * @param codeBase
   *          or null
   * @param commandOptions
   *          or null
   * @param statusListener
   *          or null
   * @return a viewer instance
   */

  public static JmolViewer allocateViewer(Container display,
                                          JmolAdapter modelAdapter,
                                          String fullName, URL documentBase,
                                          URL codeBase, String commandOptions,
                                          JmolStatusListener statusListener) {
    Viewer viewer = new Viewer(display, modelAdapter, commandOptions);
    viewer.setAppletContext(fullName, documentBase, codeBase, commandOptions);
    viewer.setJmolStatusListener(statusListener);
    return viewer;
  }
  
  private boolean isSilent = false;
  private boolean isApplet = false;

  @Override
  public boolean isApplet() {
    return isApplet;
  }

  private boolean isPreviewOnly = false;

  public boolean isPreviewOnly() {
    return isPreviewOnly;
  }

  public boolean haveDisplay = false;
  public boolean autoExit = false;

  private boolean mustRender = true;
  private boolean isPrintOnly = false;
  private boolean isCmdLine_C_Option = false;
  private boolean isCmdLine_c_or_C_Option = false;
  private boolean listCommands = false;
  private boolean useCommandThread = false;
  private boolean isSignedApplet = false;
  private boolean isSignedAppletLocal = false;
  private boolean isDataOnly = false;

  private String appletContext;

  String getAppletContext() {
    return appletContext;
  }

  @Override
  public synchronized void setAppletContext(String fullName, URL documentBase,
                                            URL codeBase, String commandOptions) {
    appletContext = (commandOptions == null ? "" : commandOptions);
    this.fullName = fullName = (fullName == null ? "" : fullName);
    appletDocumentBase = (documentBase == null ? "" : documentBase.toString());
    appletCodeBase = (codeBase == null ? "" : codeBase.toString());
    int i = fullName.lastIndexOf("[");
    htmlName = (i < 0 ? fullName : fullName.substring(0, i));
    syncId = (i < 0 ? "" : fullName.substring(i + 1, fullName.length() - 1));
    String str = appletContext;
    isPrintOnly = (appletContext.indexOf("-p") >= 0);
    isApplet = (appletContext.indexOf("-applet") >= 0);
    if (isApplet) {
      Logger.info("applet context: " + appletContext);
      String appletProxy = null;
      // -appletProxy must be the last flag added
      if ((i = str.indexOf("-appletProxy ")) >= 0) {
        appletProxy = str.substring(i + 13);
        str = str.substring(0, i);
      }
      fileManager.setAppletContext(documentBase, codeBase, appletProxy);
      isSignedApplet = (str.indexOf("-signed") >= 0);
      if (isSignedApplet) {
        logFilePath = TextFormat.simpleReplace(appletCodeBase, "file://", "");
        logFilePath = TextFormat.simpleReplace(logFilePath, "file:/", "");
        if (logFilePath.indexOf("//") >= 0)
          logFilePath = null;
        else
          isSignedAppletLocal = true;
      } else {
        logFilePath = null;
      }

      if ((i = str.indexOf("-maximumSize ")) >= 0)
        setMaximumSize(Parser.parseInt(str.substring(i + 13)));
      useCommandThread = (str.indexOf("-threaded") >= 0);
      if (useCommandThread)
        scriptManager.startCommandWatcher(true);
    } else {
      // not an applet -- used to pass along command line options
      g3d.setBackgroundTransparent(str.indexOf("-b") >= 0);
      isSilent = (str.indexOf("-i") >= 0);
      if (isSilent)
        Logger.setLogLevel(Logger.LEVEL_WARN); // no info, but warnings and
      // errors
      isCmdLine_c_or_C_Option = (str.toLowerCase().indexOf("-c") >= 0);
      isCmdLine_C_Option = (str.indexOf("-C") >= 0);
      listCommands = (str.indexOf("-l") >= 0);
      autoExit = (str.indexOf("-x") >= 0);
      cd(".");
    }
    isPreviewOnly = (str.indexOf("#previewOnly") >= 0);
    setBooleanProperty("_applet", isApplet);
    setBooleanProperty("_signedApplet", isSignedApplet);
    setBooleanProperty("_useCommandThread", useCommandThread);
    setIntProperty("_nProcessors", nProcessors);

    /*
     * Logger.info("jvm11orGreater=" + jvm11orGreater + "\njvm12orGreater=" +
     * jvm12orGreater + "\njvm14orGreater=" + jvm14orGreater);
     */
    if (!isSilent) {
      Logger.info(JmolConstants.copyright
          + "\nJmol Version: "
          + getJmolVersion()
          + "\njava.vendor: "
          + strJavaVendor
          + "\njava.version: "
          + strJavaVersion
          + "\nos.name: "
          + strOSName
          + "\nmemory: "
          + getParameter("_memory")
          + "\nprocessors available: "
          + nProcessors
          + "\nuseCommandThread: "
          + useCommandThread
          + (!isApplet ? "" : "\nappletId:" + htmlName
              + (isSignedApplet ? " (signed)" : "")));
    }

    zap(false, true, false); // here to allow echos
    global.setParameterValue("language", GT.getLanguage());
  }

  public boolean isDataOnly() {
    return isDataOnly;
  }

  public static String getJmolVersion() {
    return JmolConstants.version + "  " + JmolConstants.date;
  }

  public String getExportDriverList() {
    return (String) global.getParameter("exportDrivers");
  }

  String getHtmlName() {
    return htmlName;
  }

  boolean mustRenderFlag() {
    return mustRender && (refreshing || creatingImage);
  }

  @Override
  public Container getDisplay() {
    return display;
  }

  @Override
  public boolean handleOldJvm10Event(Event e) {
    return mouseManager.handleOldJvm10Event(e);
  }

  public void reset(boolean includingSpin) {
    // Eval.reset()
    // initializeModel
    modelSet.calcBoundBoxDimensions(null, 1);
    axesAreTainted = true;
    transformManager.homePosition(includingSpin);
    if (modelSet.setCrystallographicDefaults())
      stateManager.setCrystallographicDefaults();
    else
      setAxesModeMolecular(false);
    prevFrame = Integer.MIN_VALUE;
    if (!getSpinOn())
      refresh(1, "Viewer:homePosition()");
  }

  @Override
  public void homePosition() {
    evalString("reset spin");
  }

  /*
   * final Hashtable imageCache = new Hashtable();
   * 
   * void flushCachedImages() { imageCache.clear();
   * Graphics3D.flushCachedColors(); }
   */

  Map<String, Object> getAppletInfo() {
    Map<String, Object> info = new Hashtable<String, Object>();
    info.put("htmlName", htmlName);
    info.put("syncId", syncId);
    info.put("fullName", fullName);
    if (isApplet) {
      info.put("documentBase", appletDocumentBase);
      info.put("codeBase", appletCodeBase);
      info.put("registry", statusManager.getRegistryInfo());
    }
    info.put("version", JmolConstants.version);
    info.put("date", JmolConstants.date);
    info.put("javaVendor", strJavaVendor);
    info.put("javaVersion", strJavaVersion);
    info.put("operatingSystem", strOSName);
    return info;
  }

  // ///////////////////////////////////////////////////////////////
  // delegated to StateManager
  // ///////////////////////////////////////////////////////////////

  public void initialize() {
    global = stateManager.getGlobalSettings(global);
    global.setParameterValue("_applet", isApplet);
    global.setParameterValue("_signedApplet", isSignedApplet);
    global.setParameterValue("_useCommandThread", useCommandThread);
    global.setParameterValue("_width", dimScreen.width);
    global.setParameterValue("_height", dimScreen.height);
    if (haveDisplay) {
      global.setParameterValue("_multiTouchClient", actionManager.isMTClient());
      global.setParameterValue("_multiTouchServer", actionManager.isMTServer());
    }
    colorManager.resetElementColors();
    setObjectColor("background", "black");
    setObjectColor("axis1", "red");
    setObjectColor("axis2", "green");
    setObjectColor("axis3", "blue");

    // transfer default global settings to managers and g3d

    Graphics3D.setAmbientPercent(global.ambientPercent);
    Graphics3D.setDiffusePercent(global.diffusePercent);
    Graphics3D.setSpecular(global.specular);
    Graphics3D.setSpecularPercent(global.specularPercent);
    Graphics3D.setSpecularPower(-global.specularExponent);
    Graphics3D.setPhongExponent(global.phongExponent);
    Graphics3D.setSpecularPower(global.specularPower);
    Graphics3D.setZShadePower(global.zShadePower);
    if (modelSet != null)
      animationManager.setAnimationOn(false);
    animationManager.setAnimationFps(global.animationFps);

    statusManager.setAllowStatusReporting(global.statusReporting);
    setBooleanProperty("antialiasDisplay", global.antialiasDisplay);

    setTransformManagerDefaults();

  }

  public String listSavedStates() {
    return stateManager.listSavedStates();
  }

  public void saveOrientation(String saveName) {
    // from Eval
    stateManager.saveOrientation(saveName);
  }

  public boolean restoreOrientation(String saveName, float timeSeconds) {
    // from Eval
    return stateManager.restoreOrientation(saveName, timeSeconds, true);
  }

  public void restoreRotation(String saveName, float timeSeconds) {
    stateManager.restoreOrientation(saveName, timeSeconds, false);
  }

  void saveModelOrientation() {
    modelSet.saveModelOrientation(animationManager.currentModelIndex,
        stateManager.getOrientation());
  }

  public Orientation getOrientation() {
    return stateManager.getOrientation();
  }

  public String getSavedOrienationText(String name) {
    return stateManager.getSavedOrientationText(name);
  }

  void restoreModelOrientation(int modelIndex) {
    StateManager.Orientation o = modelSet.getModelOrientation(modelIndex);
    if (o != null)
      o.restore(-1, true);
  }

  void restoreModelRotation(int modelIndex) {
    StateManager.Orientation o = modelSet.getModelOrientation(modelIndex);
    if (o != null)
      o.restore(-1, false);
  }

  public void saveBonds(String saveName) {
    // from Eval
    stateManager.saveBonds(saveName);
  }

  public boolean restoreBonds(String saveName) {
    // from Eval
    clearModelDependentObjects();
    return stateManager.restoreBonds(saveName);
  }

  public void saveState(String saveName) {
    // from Eval
    stateManager.saveState(saveName);
  }

  public void deleteSavedState(String saveName) {
    stateManager.deleteSaved("State_" + saveName);
  }

  public String getSavedState(String saveName) {
    return stateManager.getSavedState(saveName);
  }

  public void saveStructure(String saveName) {
    // from Eval
    stateManager.saveStructure(saveName);
  }

  public String getSavedStructure(String saveName) {
    return stateManager.getSavedStructure(saveName);
  }

  public void saveCoordinates(String saveName, BitSet bsSelected) {
    // from Eval
    stateManager.saveCoordinates(saveName, bsSelected);
  }

  public String getSavedCoordinates(String saveName) {
    return stateManager.getSavedCoordinates(saveName);
  }

  public void saveSelection(String saveName) {
    // from Eval
    stateManager.saveSelection(saveName, getSelectionSet(false));
    stateManager.restoreSelection(saveName); // just to register the # of
    // selected atoms
  }

  public boolean restoreSelection(String saveName) {
    // from Eval
    return stateManager.restoreSelection(saveName);
  }

  // ///////////////////////////////////////////////////////////////
  // delegated to TransformManager
  // ///////////////////////////////////////////////////////////////

  public Matrix4f getMatrixtransform() {
    return transformManager.getMatrixtransform();
  }

  public Quaternion getRotationQuaternion() {
    return transformManager.getRotationQuaternion();
  }

  @Override
  public float getRotationRadius() {
    return transformManager.getRotationRadius();
  }

  public void setRotationRadius(float angstroms, boolean doAll) {
    if (doAll)
      angstroms = transformManager.setRotationRadius(angstroms, false);
    // only set the rotationRadius if this is NOT a dataframe
    if (modelSet.setRotationRadius(animationManager.currentModelIndex,
        angstroms))
      global.setParameterValue("rotationRadius", angstroms);
  }

  public Point3f getRotationCenter() {
    return transformManager.getRotationCenter();
  }

  public void setCenterAt(String relativeTo, Point3f pt) {
    // Eval centerAt boundbox|absolute|average {pt}
    if (isJmolDataFrame())
      return;
    transformManager.setCenterAt(relativeTo, pt);
  }

  public void setCenterBitSet(BitSet bsCenter, boolean doScale) {
    // Eval
    // setCenterSelected

    Point3f center = (BitSetUtil.cardinalityOf(bsCenter) > 0 ? getAtomSetCenter(bsCenter)
        : null);
    if (isJmolDataFrame())
      return;
    transformManager.setNewRotationCenter(center, doScale);
  }

  public void setNewRotationCenter(Point3f center) {
    // eval CENTER command
    if (isJmolDataFrame())
      return;
    transformManager.setNewRotationCenter(center, true);
  }

  public Point3f getNavigationCenter() {
    return transformManager.getNavigationCenter();
  }

  public float getNavigationDepthPercent() {
    return transformManager.getNavigationDepthPercent();
  }

  void navigate(int keyWhere, int modifiers) {
    if (isJmolDataFrame())
      return;
    transformManager.navigate(keyWhere, modifiers);
    if (!transformManager.vibrationOn && keyWhere != 0)
      refresh(1, "Viewer:navigate()");
  }

  public Point3f getNavigationOffset() {
    return transformManager.getNavigationOffset();
  }

  float getNavigationOffsetPercent(char XorY) {
    return transformManager.getNavigationOffsetPercent(XorY);
  }

  public boolean isNavigating() {
    return transformManager.isNavigating();
  }

  public boolean isInPosition(Vector3f axis, float degrees) {
    return transformManager.isInPosition(axis, degrees);
  }

  public void move(Vector3f dRot, float dZoom, Vector3f dTrans, float dSlab,
                   float floatSecondsTotal, int fps) {
    // from Eval
    transformManager.move(dRot, dZoom, dTrans, dSlab, floatSecondsTotal, fps);
    moveUpdate(floatSecondsTotal);
  }

  public boolean waitForMoveTo() {
    return global.waitForMoveTo;
  }

  public void stopMotion() {
    transformManager.stopMotion();
  }

  void setRotationMatrix(Matrix3f rotationMatrix) {
    transformManager.setRotation(rotationMatrix);
  }
  
  public void moveTo(float floatSecondsTotal, Point3f center, Vector3f rotAxis,
                     float degrees, Matrix3f rotationMatrix, float zoom,
                     float xTrans, float yTrans, float rotationRadius,
                     Point3f navCenter, float xNav, float yNav, float navDepth) {
    // from StateManager -- -1 for time --> no repaint
    if (!haveDisplay)
      floatSecondsTotal = 0;
    setTainted(true);
    transformManager.moveTo(floatSecondsTotal, center, rotAxis, degrees,
        rotationMatrix, zoom, xTrans, yTrans, rotationRadius, navCenter, xNav,
        yNav, navDepth);
    moveUpdate(floatSecondsTotal);
    finalizeTransformParameters();
  }

  private void moveUpdate(float floatSecondsTotal) {
    if (floatSecondsTotal > 0)
      requestRepaintAndWait();
    else if (floatSecondsTotal == 0)
      setSync();
  }

  String getMoveToText(float timespan) {
    return transformManager.getMoveToText(timespan, false);
  }

  public void navigate(float timeSeconds, Point3f[] path, float[] theta,
                       int indexStart, int indexEnd) {
    if (isJmolDataFrame())
      return;
    transformManager.navigate(timeSeconds, path, theta, indexStart, indexEnd);
    moveUpdate(timeSeconds);
  }

  public void navigate(float timeSeconds, Point3f center) {
    if (isJmolDataFrame())
      return;
    transformManager.navigate(timeSeconds, center);
    moveUpdate(timeSeconds);
  }

  public void navigate(float timeSeconds, Point3f[][] pathGuide) {
    if (isJmolDataFrame())
      return;
    transformManager.navigate(timeSeconds, pathGuide);
    moveUpdate(timeSeconds);
  }

  public void navigateSurface(float timeSeconds, String name) {
    if (isJmolDataFrame())
      return;
    transformManager.navigateSurface(timeSeconds, name);
    moveUpdate(timeSeconds);
  }

  public void navigate(float timeSeconds, Vector3f rotAxis, float degrees) {
    if (isJmolDataFrame())
      return;
    transformManager.navigate(timeSeconds, rotAxis, degrees);
    moveUpdate(timeSeconds);
  }

  public void navTranslate(float timeSeconds, Point3f center) {
    if (isJmolDataFrame())
      return;
    transformManager.navTranslate(timeSeconds, center);
    moveUpdate(timeSeconds);
  }

  public void navTranslatePercent(float timeSeconds, float x, float y) {
    if (isJmolDataFrame())
      return;
    transformManager.navTranslatePercent(timeSeconds, x, y);
    moveUpdate(timeSeconds);
  }

  private boolean mouseEnabled = true;

  public void setMouseEnabled(boolean TF) {
    // never called in Jmol
    mouseEnabled = TF;
  }

  void zoomBy(int pixels) {
    // MouseManager.mouseSinglePressDrag
    if (mouseEnabled)
      transformManager.zoomBy(pixels);
    refresh(2, statusManager.syncingMouse ? "Mouse: zoomBy " + pixels : "");
  }

  void zoomByFactor(float factor, int x, int y) {
    // MouseManager.mouseWheel
    if (mouseEnabled)
      transformManager.zoomByFactor(factor, x, y);
    refresh(2, !statusManager.syncingMouse ? "" : "Mouse: zoomByFactor "
        + factor + (x == Integer.MAX_VALUE ? "" : " " + x + " " + y));
  }

  void rotateXYBy(float xDelta, float yDelta) {
    // mouseSinglePressDrag
    if (mouseEnabled)
      transformManager.rotateXYBy(xDelta, yDelta, null);
    refresh(2, statusManager.syncingMouse ? "Mouse: rotateXYBy " + xDelta + " "
        + yDelta : "");
  }

  public void spinXYBy(int xDelta, int yDelta, float speed) {
    if (mouseEnabled)
      transformManager.spinXYBy(xDelta, yDelta, speed);
    if (xDelta == 0 && yDelta == 0)
      return;
    refresh(2, statusManager.syncingMouse ? "Mouse: spinXYBy " + xDelta + " "
        + yDelta + " " + speed : "");
  }

  public void rotateZBy(int zDelta, int x, int y) {
    // mouseSinglePressDrag
    if (mouseEnabled)
      transformManager.rotateZBy(zDelta, x, y);
    refresh(2, statusManager.syncingMouse ? "Mouse: rotateZBy " + zDelta
        + (x == Integer.MAX_VALUE ? "" : " " + x + " " + y) : "");
  }

  void rotateMolecule(float deltaX, float deltaY, BitSet bsSelected) {
    if (isJmolDataFrame())
      return;
    if (mouseEnabled) {
      if (bsSelected == null)
        bsSelected = getSelectionSet(false);
      transformManager.setRotateMolecule(!global.allowMoveAtoms);
      transformManager.rotateXYBy(deltaX, deltaY, bsSelected);
      transformManager.setRotateMolecule(false);
      refreshMeasures(true);
    }
    //TODO: note that sync may not work with set allowRotateSelectedAtoms
    refresh(2, statusManager.syncingMouse ? "Mouse: rotateMolecule " + deltaX
        + " " + deltaY : "");
  }

  public void translateXYBy(int xDelta, int yDelta) {
    // mouseDoublePressDrag, mouseSinglePressDrag
    if (mouseEnabled)
      transformManager.translateXYBy(xDelta, yDelta);
    refresh(2, statusManager.syncingMouse ? "Mouse: translateXYBy " + xDelta
        + " " + yDelta : "");
  }

  void centerAt(int x, int y, Point3f pt) {
    if (mouseEnabled)
      transformManager.centerAt(x, y, pt);
    refresh(2, statusManager.syncingMouse ? "Mouse: centerAt " + x + " " + y
        + " " + pt.x + " " + pt.y + " " + pt.z : "");
  }

  @Override
  public void rotateFront() {
    // deprecated
    transformManager.rotateFront();
    refresh(1, "Viewer:rotateFront()");
  }

  @Override
  public void rotateX(float angleRadians) {
    // deprecated
    transformManager.rotateX(angleRadians);
    refresh(1, "Viewer:rotateX()");
  }

  @Override
  public void rotateY(float angleRadians) {
    // deprecated
    transformManager.rotateY(angleRadians);
    refresh(1, "Viewer:rotateY()");
  }

  @Override
  public void rotateZ(float angleRadians) {
    // deprecated
    transformManager.rotateZ(angleRadians);
    refresh(1, "Viewer:rotateZ()");
  }

  @Override
  public void rotateX(int angleDegrees) {
    // deprecated
    rotateX(angleDegrees * Measure.radiansPerDegree);
  }

  @Override
  public void rotateY(int angleDegrees) {
    // deprecated
    rotateY(angleDegrees * Measure.radiansPerDegree);
  }

  public void translate(char xyz, float x, char type, BitSet bsAtoms) {
    int xy = (type == '\0' ? (int) x : type == '%' ? transformManager
        .percentToPixels(xyz, x) : transformManager.angstromsToPixels(x
        * (type == 'n' ? 10f : 1f)));
    if (bsAtoms != null) {
      if (xy == 0)
        return;
      repaintManager.setSelectedTranslation(bsAtoms, xyz, xy);
    } else {
      switch (xyz) {
      case 'x':
        if (type == '\0')
          transformManager.translateToPercent('x', x);
        else
          transformManager.translateXYBy(xy, 0);
        break;
      case 'y':
        if (type == '\0')
          transformManager.translateToPercent('y', x);
        else
          transformManager.translateXYBy(0, xy);
        break;
      case 'z':
        if (type == '\0')
          transformManager.translateToPercent('z', x);
        else
          transformManager.translateZBy(xy);
        break;
      }
    }
    refresh(1, "Viewer:translate()");
  }

  public float getTranslationXPercent() {
    return transformManager.getTranslationXPercent();
  }

  public float getTranslationYPercent() {
    return transformManager.getTranslationYPercent();
  }

  float getTranslationZPercent() {
    return transformManager.getTranslationZPercent();
  }

  public String getTranslationScript() {
    return transformManager.getTranslationScript();
  }

  @Override
  public int getZoomPercent() {
    // deprecated
    return (int) getZoomSetting();
  }

  public float getZoomSetting() {
    return transformManager.getZoomSetting();
  }

  @Override
  public float getZoomPercentFloat() {
    // note -- this value is only after rendering.
    return transformManager.getZoomPercentFloat();
  }

  public float getMaxZoomPercent() {
    return TransformManager.MAXIMUM_ZOOM_PERCENTAGE;
  }

  public void slabReset() {
    transformManager.slabReset();
  }

  public boolean getZoomEnabled() {
    return transformManager.zoomEnabled;
  }

  public boolean getSlabEnabled() {
    return transformManager.slabEnabled;
  }

  public boolean getSlabByMolecule() {
    return global.slabByMolecule;
  }

  public boolean getSlabByAtom() {
    return global.slabByAtom;
  }

  void slabByPixels(int pixels) {
    // MouseManager.mouseSinglePressDrag
    transformManager.slabByPercentagePoints(pixels);
    refresh(3, "slabByPixels");
  }

  void depthByPixels(int pixels) {
    // MouseManager.mouseDoublePressDrag
    transformManager.depthByPercentagePoints(pixels);
    refresh(3, "depthByPixels");

  }

  void slabDepthByPixels(int pixels) {
    // MouseManager.mouseSinglePressDrag
    transformManager.slabDepthByPercentagePoints(pixels);
    refresh(3, "slabDepthByPixels");
  }

  public void slabInternal(Point4f plane, boolean isDepth) {
    transformManager.slabInternal(plane, isDepth);
  }

  public void slabToPercent(int percentSlab) {
    // Eval.slab
    transformManager.slabToPercent(percentSlab);
  }

  public void depthToPercent(int percentDepth) {
    // Eval.depth
    transformManager.depthToPercent(percentDepth);
  }

  public void setSlabDepthInternal(boolean isDepth) {
    transformManager.setSlabDepthInternal(isDepth);
  }

  public int zValueFromPercent(int zPercent) {
    return transformManager.zValueFromPercent(zPercent);
  }

  @Override
  public Matrix4f getUnscaledTransformMatrix() {
    return transformManager.getUnscaledTransformMatrix();
  }

  void finalizeTransformParameters() {
    // FrameRenderer
    // InitializeModel

    transformManager.finalizeTransformParameters();
    g3d.setSlabAndDepthValues(transformManager.slabValue,
        transformManager.depthValue, transformManager.zShadeEnabled,
        transformManager.zSlabValue, transformManager.zDepthValue);
  }

  public void rotatePoint(Point3f pt, Point3f ptRot) {
    transformManager.rotatePoint(pt, ptRot);
  }

  public Point3i transformPoint(Point3f pointAngstroms) {
    return transformManager.transformPoint(pointAngstroms);
  }

  public Point3i transformPoint(Point3f pointAngstroms, Vector3f vibrationVector) {
    return transformManager.transformPoint(pointAngstroms, vibrationVector);
  }

  public void transformPoint(Point3f pointAngstroms, Point3i pointScreen) {
    transformManager.transformPoint(pointAngstroms, pointScreen);
  }

  public void transformPointNoClip(Point3f pointAngstroms, Point3f pt) {
    transformManager.transformPointNoClip(pointAngstroms, pt);
  }

  public void transformPoint(Point3f pointAngstroms, Point3f pointScreen) {
    transformManager.transformPoint(pointAngstroms, pointScreen);
  }

  public void transformPoints(Point3f[] pointsAngstroms, Point3i[] pointsScreens) {
    transformManager.transformPoints(pointsAngstroms.length, pointsAngstroms,
        pointsScreens);
  }

  public void transformVector(Vector3f vectorAngstroms,
                              Vector3f vectorTransformed) {
    transformManager.transformVector(vectorAngstroms, vectorTransformed);
  }

  public void unTransformPoint(Point3f pointScreen, Point3f pointAngstroms) {
    // called by Draw.move2D
    transformManager.unTransformPoint(pointScreen, pointAngstroms);
  }

  public float getScalePixelsPerAngstrom(boolean asAntialiased) {
    return transformManager.scalePixelsPerAngstrom
        * (asAntialiased || !global.antialiasDisplay ? 1f : 0.5f);
  }

  public short scaleToScreen(int z, int milliAngstroms) {
    // all shapes
    return transformManager.scaleToScreen(z, milliAngstroms);
  }

  public float unscaleToScreen(float z, float screenDistance) {
    // all shapes
    return transformManager.unscaleToScreen(z, screenDistance);
  }

  public float scaleToPerspective(int z, float sizeAngstroms) {
    // DotsRenderer
    return transformManager.scaleToPerspective(z, sizeAngstroms);
  }

  public void setSpin(String key, int value) {
    // Eval
    if (!Parser.isOneOf(key, "x;y;z;fps;X;Y;Z;FPS"))
      return;
    int i = "x;y;z;fps;X;Y;Z;FPS".indexOf(key);
    switch (i) {
    case 0:
      transformManager.setSpinXYZ(value, Float.NaN, Float.NaN);
      break;
    case 2:
      transformManager.setSpinXYZ(Float.NaN, value, Float.NaN);
      break;
    case 4:
      transformManager.setSpinXYZ(Float.NaN, Float.NaN, value);
      break;
    case 6:
    default:
      transformManager.setSpinFps(value);
      break;
    case 10:
      transformManager.setNavXYZ(value, Float.NaN, Float.NaN);
      break;
    case 12:
      transformManager.setNavXYZ(Float.NaN, value, Float.NaN);
      break;
    case 14:
      transformManager.setNavXYZ(Float.NaN, Float.NaN, value);
      break;
    case 16:
      transformManager.setNavFps(value);
      break;
    }
    global.setParameterValue((i < 10 ? "spin" : "nav") + key, value);
  }

  public String getSpinState() {
    return transformManager.getSpinState(false);
  }

  public void setSpinOn(boolean spinOn) {
    // Eval
    // startSpinningAxis
    transformManager.setSpinOn(spinOn);
  }

  boolean getSpinOn() {
    return transformManager.getSpinOn();
  }

  public void setNavOn(boolean navOn) {
    // Eval
    // startSpinningAxis
    transformManager.setNavOn(navOn);
  }

  boolean getNavOn() {
    return transformManager.getNavOn();
  }

  public void setNavXYZ(float x, float y, float z) {
    transformManager.setNavXYZ((int) x, (int) y, (int) z);
  }

  public String getOrientationText(int type, String name) {
    return (name == null ? transformManager.getOrientationText(type)
        : stateManager.getSavedOrientationText(name));
  }

  Map<String, Object> getOrientationInfo() {
    return transformManager.getOrientationInfo();
  }

  Matrix3f getMatrixRotate() {
    return transformManager.getMatrixRotate();
  }

  public void getAxisAngle(AxisAngle4f axisAngle) {
    transformManager.getAxisAngle(axisAngle);
  }

  public String getTransformText() {
    return transformManager.getTransformText();
  }

  void getRotation(Matrix3f matrixRotation) {
    transformManager.getRotation(matrixRotation);
  }

  // ///////////////////////////////////////////////////////////////
  // delegated to ColorManager
  // ///////////////////////////////////////////////////////////////

  public float[] getCurrentColorRange() {
    return colorManager.getPropertyColorRange();
  }

  private void setDefaultColors(boolean isRasmol) {
    colorManager.setDefaultColors(isRasmol);
    global.setParameterValue("colorRasmol", isRasmol);
  }

  public float getDefaultTranslucent() {
    return global.defaultTranslucent;
  }

  public int getColorArgbOrGray(short colix) {
    return g3d.getColorArgbOrGray(colix);
  }

  public void setRubberbandArgb(int argb) {
    // Eval
    colorManager.setRubberbandArgb(argb);
  }

  public short getColixRubberband() {
    return colorManager.colixRubberband;
  }

  public void setElementArgb(int elementNumber, int argb) {
    // Eval
    global.setParameterValue("=color "
        + Elements.elementNameFromNumber(elementNumber), Escape
        .escapeColor(argb));
    colorManager.setElementArgb(elementNumber, argb);
  }

  public float getVectorScale() {
    return global.vectorScale;
  }

  @Override
  public void setVectorScale(float scale) {
    global.setParameterValue("vectorScale", scale);
    global.vectorScale = scale;
  }

  public float getDefaultDrawArrowScale() {
    return global.defaultDrawArrowScale;
  }

  float getVibrationScale() {
    return global.vibrationScale;
  }

  public float getVibrationPeriod() {
    return global.vibrationPeriod;
  }

  public boolean isVibrationOn() {
    return transformManager.vibrationOn;
  }

  @Override
  public void setVibrationScale(float scale) {
    // Eval
    // public legacy in JmolViewer
    transformManager.setVibrationScale(scale);
    global.vibrationScale = scale;
    // because this is public:
    global.setParameterValue("vibrationScale", scale);
  }

  public void setVibrationOff() {
    transformManager.setVibrationPeriod(0);
  }

  @Override
  public void setVibrationPeriod(float period) {
    // Eval
    transformManager.setVibrationPeriod(period);
    period = Math.abs(period);
    global.vibrationPeriod = period;
    // because this is public:
    global.setParameterValue("vibrationPeriod", period);
  }

  void setObjectColor(String name, String colorName) {
    if (colorName == null || colorName.length() == 0)
      return;
    setObjectArgb(name, Graphics3D.getArgbFromString(colorName));
  }

  public void setObjectArgb(String name, int argb) {
    int objId = StateManager.getObjectIdFromName(name);
    if (objId < 0) {
      if (name.equalsIgnoreCase("axes")) {
        setObjectArgb("axis1", argb);
        setObjectArgb("axis2", argb);
        setObjectArgb("axis3", argb);
      }
      return;
    }
    global.objColors[objId] = argb;
    switch (objId) {
    case StateManager.OBJ_BACKGROUND:
      g3d.setBackgroundArgb(argb);
      colorManager.setColixBackgroundContrast(argb);
      break;
    }
    global.setParameterValue(name + "Color", Escape.escapeColor(argb));
  }

  public void setBackgroundImage(String fileName, Image image) {
    global.backgroundImageFileName = fileName;
    g3d.setBackgroundImage(image);
  }

  int getObjectArgb(int objId) {
    return global.objColors[objId];
  }

  public short getObjectColix(int objId) {
    int argb = getObjectArgb(objId);
    if (argb == 0)
      return getColixBackgroundContrast();
    return Graphics3D.getColix(argb);
  }

  public String getObjectState(String name) {
    int objId = StateManager
        .getObjectIdFromName(name.equalsIgnoreCase("axes") ? "axis" : name);
    if (objId < 0)
      return "";
    int mad = getObjectMad(objId);
    StringBuffer s = new StringBuffer("\n");
    Shape.appendCmd(s, name
        + (mad == 0 ? " off" : mad == 1 ? " on" : mad == -1 ? " dotted"
            : mad < 20 ? " " + mad : " " + (mad / 2000f)));
    return s.toString();
  }

  // for historical reasons, leave these two:

  @Override
  public void setColorBackground(String colorName) {
    setObjectColor("background", colorName);
  }

  @Override
  public int getBackgroundArgb() {
    return getObjectArgb(StateManager.OBJ_BACKGROUND);
  }

  public void setObjectMad(int iShape, String name, int mad) {
    int objId = StateManager
        .getObjectIdFromName(name.equalsIgnoreCase("axes") ? "axis" : name);
    if (objId < 0)
      return;
    if (mad == -2 || mad == -4) { // turn on if not set "showAxes = true"
      int m = mad + 3;
      mad = getObjectMad(objId);
      if (mad == 0)
        mad = m;
    }
    global.setParameterValue("show" + name, mad != 0);
    global.objStateOn[objId] = (mad != 0);
    if (mad == 0)
      return;
    global.objMad[objId] = mad;
    setShapeSize(iShape, mad, null); // just loads it
  }

  public int getObjectMad(int objId) {
    return (global.objStateOn[objId] ? global.objMad[objId] : 0);
  }

  public void setPropertyColorScheme(String scheme, boolean isTranslucent,
                                     boolean isOverloaded) {
    global.propertyColorScheme = scheme;
    if (scheme.startsWith("translucent ")) {
      isTranslucent = true;
      scheme = scheme.substring(12).trim();
    }
    colorManager.setPropertyColorScheme(scheme, isTranslucent, isOverloaded);
  }

  public String getPropertyColorScheme() {
    return global.propertyColorScheme;
  }

  public short getColixBackgroundContrast() {
    return colorManager.colixBackgroundContrast;
  }

  public String getSpecularState() {
    return global.getSpecularState();
  }

  public short getColixAtomPalette(Atom atom, byte pid) {
    return colorManager.getColixAtomPalette(atom, pid);
  }

  public short getColixBondPalette(Bond bond, byte pid) {
    return colorManager.getColixBondPalette(bond, pid);
  }

  public String getColorSchemeList(String colorScheme) {
    return colorManager.getColorSchemeList(colorScheme);
  }

  public void setUserScale(int[] scale) {
    colorManager.setUserScale(scale);
  }

  public short getColixForPropertyValue(float val) {
    // isosurface
    return colorManager.getColixForPropertyValue(val);
  }
  
  public Point3f getColorPointForPropertyValue(float val) {
    // x = {atomno=3}.partialcharge.color
    return Graphics3D.colorPointFromInt2(g3d.getColorArgbOrGray(colorManager
        .getColixForPropertyValue(val)));
  }

  // ///////////////////////////////////////////////////////////////
  // delegated to SelectionManager
  // ///////////////////////////////////////////////////////////////

  public void select(BitSet bs, boolean isQuiet) {
    // Eval, ActionManager
    selectionManager.select(bs, isQuiet);
    shapeManager.setShapeSize(JmolConstants.SHAPE_STICKS, Integer.MAX_VALUE,
        null, null);
  }

  @Override
  public void setSelectionSet(BitSet set) {
    // JmolViewer API only -- not used in Jmol 
    select(set, true);
  }

  public void selectBonds(BitSet bs) {
    shapeManager.setShapeSize(JmolConstants.SHAPE_STICKS, Integer.MAX_VALUE,
        null, bs);
  }

  public void hide(BitSet bs, boolean isQuiet) {
    // Eval
    selectionManager.hide(bs, isQuiet);
  }

  public void display(BitSet bs, boolean isQuiet) {
    // Eval
    selectionManager.display(modelSet.getModelAtomBitSetIncludingDeleted(-1,
        false), bs, isQuiet);
  }

  public BitSet getHiddenSet() {
    return selectionManager.getHiddenSet();
  }

  public boolean isSelected(int atomIndex) {
    return selectionManager.isSelected(atomIndex);
  }

  boolean isInSelectionSubset(int atomIndex) {
    return selectionManager.isInSelectionSubset(atomIndex);
  }

  void reportSelection(String msg) {
    if (modelSet.getSelectionHaloEnabled())
      setTainted(true);
    if (isScriptQueued || global.debugScript)
      scriptStatus(msg);
  }

  public Point3f getAtomSetCenter(BitSet bs) {
    return modelSet.getAtomSetCenter(bs);
  }

  private void clearAtomSets() {
    setSelectionSubset(null);
    definedAtomSets.clear();
  }

  @Override
  public void selectAll() {
    // initializeModel
    selectionManager.selectAll(false);
  }

  private boolean noneSelected;

  public void setNoneSelected(boolean noneSelected) {
    this.noneSelected = noneSelected;
  }

  public Boolean getNoneSelected() {
    return (noneSelected ? Boolean.TRUE : Boolean.FALSE);
  }

  @Override
  public void clearSelection() {
    // not used in this project; in jmolViewer interface, though
    selectionManager.clearSelection(true);
    global.setParameterValue("hideNotSelected", false);
  }

  public void setSelectionSubset(BitSet subset) {
    selectionManager.setSelectionSubset(subset);
  }

  public BitSet getSelectionSubset() {
    return selectionManager.getSelectionSubset();
  }

  public void invertSelection() {
    // Eval
    selectionManager.invertSelection();
  }

  public BitSet getSelectionSet(boolean includeDeleted) {
    return selectionManager.getSelectionSet(includeDeleted);
  }
  
  public void setSelectedAtom(int atomIndex, boolean TF) {
    selectionManager.setSelectedAtom(atomIndex, TF);
  }

  public boolean isAtomSelected(int atomIndex) {
    return selectionManager.isAtomSelected(atomIndex);
  }
  
  @Override
  public int getSelectionCount() {
    return selectionManager.getSelectionCount();
  }

  public void setFormalCharges(int formalCharge) {
    modelSet.setFormalCharges(getSelectionSet(false), formalCharge);
  }

  @Override
  public void addSelectionListener(JmolSelectionListener listener) {
    selectionManager.addListener(listener);
  }

  @Override
  public void removeSelectionListener(JmolSelectionListener listener) {
    selectionManager.addListener(listener);
  }

  BitSet getAtomBitSet(ScriptEvaluator eval, Object atomExpression) {
    if (eval == null)
      eval = new ScriptEvaluator(this);
    return ScriptEvaluator.getAtomBitSet(eval, atomExpression);
  }

  public BitSet getAtomBitSet(Object atomExpression) {
    return getAtomBitSet(eval, atomExpression);
  }

  List<Integer> getAtomBitSetVector(Object atomExpression) {
    return ScriptEvaluator.getAtomBitSetVector(eval, getAtomCount(),
        atomExpression);
  }

  // ///////////////////////////////////////////////////////////////
  // delegated to MouseManager
  // ///////////////////////////////////////////////////////////////

  @Override
  public void setModeMouse(int modeMouse) {
    // call before setting viewer=null
    if (modeMouse == JmolConstants.MOUSE_NONE) {
      // applet is being destroyed
      if (haveDisplay)
        mouseManager.dispose();
      clearScriptQueue();
      haltScriptExecution();
      stopAnimationThreads("setModeMouse NONE");
      scriptManager.startCommandWatcher(false);
      scriptManager.interruptQueueThreads();
      g3d.destroy();
      try {
        if (appConsole != null) {
          appConsole.dispose();
          appConsole = null;
        }
        if (scriptEditor != null) {
          scriptEditor.dispose();
          scriptEditor = null;
        }
      } catch (Exception e) {
        // ignore -- Disposal was interrupted only in Eclipse
      }
    }
  }

  Rectangle getRubberBandSelection() {
    return (haveDisplay ? actionManager.getRubberBand() : null);
  }

  public boolean isBound(int action, int gesture) {
    return (haveDisplay && actionManager.isBound(action, gesture));

  }

  public int getCursorX() {
    return (haveDisplay ? actionManager.getCurrentX() : 0);
  }

  public int getCursorY() {
    return (haveDisplay ? actionManager.getCurrentY() : 0);
  }

  // ///////////////////////////////////////////////////////////////
  // delegated to FileManager
  // ///////////////////////////////////////////////////////////////

  String getDefaultDirectory() {
    return global.defaultDirectory;
  }

  public BufferedInputStream getBufferedInputStream(String fullPathName) {
    // used by some JVXL readers
    return fileManager.getBufferedInputStream(fullPathName);
  }

  public Object getBufferedReaderOrErrorMessageFromName(
                                                        String name,
                                                        String[] fullPathNameReturn,
                                                        boolean isBinary) {
    // used by isosurface reader
    return fileManager.getBufferedReaderOrErrorMessageFromName(name,
        fullPathNameReturn, isBinary, true);
  }
/*
  public void addLoadScript(String script) {
    System.out.println("VIEWER addLoadSCript " + script);
    // fileManager.addLoadScript(script);
  }
*/
  private Map<String, Object> setLoadParameters(Map<String, Object> htParams, boolean isAppend) {
    if (htParams == null)
      htParams = new Hashtable<String, Object>();
    htParams.put("viewer", this);
    if (global.atomTypes.length() > 0)
      htParams.put("atomTypes", global.atomTypes);
    if (!htParams.containsKey("lattice"))
      htParams.put("lattice", global.getDefaultLattice());
    if (global.applySymmetryToBonds)
      htParams.put("applySymmetryToBonds", Boolean.TRUE);
    if (global.pdbGetHeader)
      htParams.put("getHeader", Boolean.TRUE);
    if (global.pdbSequential)
      htParams.put("isSequential", Boolean.TRUE);
    if (!htParams.containsKey("filter")) {
      String filter = getDefaultLoadFilter();
      if (filter.length() > 0)
        htParams.put("filter", filter);
    }
    if (isAppend && !global.appendNew && getAtomCount() > 0)
      htParams.put("merging", Boolean.TRUE);
    return htParams;
  }

  // //////////////// methods that open a file to create a model set ///////////

  private final static int FILE_STATUS_CREATING_MODELSET = 2;

  /**
   * opens a file as a model, a script, or a surface via the creation of a
   * script that is queued \t at the beginning disallows script option
   * - used by JmolFileDropper and JmolPanel file-open actions
   * - sets up a script to load the file
   * 
   * @param fileName
   */
  @Override
  public void openFileAsynchronously(String fileName) {
    fileName = fileName.trim();
    boolean allowScript = (!fileName.startsWith("\t"));
    if (!allowScript)
      fileName = fileName.substring(1);
    fileName = fileName.replace('\\', '/');
    if (isApplet && fileName.indexOf("://") < 0)
      fileName = "file://" + (fileName.startsWith("/") ? "" : "/") + fileName;

    String cmd = null;
    if (fileName.endsWith("jvxl"))
      cmd = "isosurface ";
    else if (!fileName.endsWith(".spt")) {
      String type = fileManager.getFileTypeName(fileName);
      if (type == null) {
        type = SurfaceFileTyper
            .determineSurfaceFileType(getBufferedInputStream(fileName));
        if (type != null) {
          evalString("if (_filetype == 'Pdb') { isosurface sigma 1.0 within 2.0 {*} "
              + Escape.escape(fileName)
              + " mesh nofill }; else; { isosurface "
              + Escape.escape(fileName) + "}");
          return;
        }
      } else if (type.equals("Jmol")) {
        cmd = "load ";
      } else if (type.equals("Cube")) {
        cmd = "isosurface sign red blue ";
      } else if (!type.equals("spt")) {
        evalString("zap; load auto "
            + Escape.escape(fileName)
            + ";if (_loadScript = '' && defaultLoadScript == '' && _filetype == 'Pdb') { select protein or nucleic;cartoons Only;color structure; select * }");
        return;
      }
    }
    if (allowScript && scriptEditorVisible && cmd == null)
      showEditor(new String[] { fileName, getFileAsString(fileName) });
    else
      evalString((cmd == null ? "script " : cmd) + Escape.escape(fileName));
  }

  /**
   * for JmolSimpleViewer -- external applications only
   * 
   * @param fileName
   * @return null or error
   */
  @Override
  public String openFile(String fileName) {
    zap(true, true, false);
    return loadModelFromFile(null, fileName, null, null, false, null, null, 0);
  }

  /**
   * for JmolSimpleViewer -- external applications only
   * 
   * @param fileNames
   * @return null or error
   */
  @Override
  public String openFiles(String[] fileNames) {
    zap(true, true, false);
    return loadModelFromFile(null, null, fileNames, null, false, null, null, 0);
  }

  /**
   * Opens the file, given an already-created reader.
   * 
   * @param fullPathName
   * @param fileName 
   * @param reader
   * @return null or error message
   */
  @Override
  public String openReader(String fullPathName, String fileName, Reader reader) {
    zap(true, true, false);
    return loadModelFromFile(fullPathName, fileName, null, reader, false, null, null, 0);
  }

  /**
   * applet DOM method -- does not preserve state
   * 
   * @param DOMNode
   * @return null or error
   * 
   */
  @Override
  public String openDOM(Object DOMNode) {
    // applet.loadDOMNode
    zap(true, true, false);
    setBooleanProperty("preserveState", false);
    return loadModelFromFile("?", "?", null, DOMNode, false, null, null, 0);
  }

  /**
   * Used by the ScriptEvaluator LOAD command to open one or more files. Now
   * necessary for EVERY load of a file, as loadScript must be passed to the
   * ModelLoader.
   * @param fullPathName TODO
   * @param fileName
   * @param fileNames
   * @param reader TODO
   * @param isAppend
   * @param htParams
   * @param loadScript
   * @param tokType
   * 
   * @return null or error
   */
  public String loadModelFromFile(String fullPathName, String fileName,
                                  String[] fileNames, Object reader,
                                  boolean isAppend, Map<String, Object> htParams,
                                  StringBuffer loadScript, int tokType) {
    if (htParams == null)
      htParams = setLoadParameters(null, isAppend);
    Object atomSetCollection;
    String[] saveInfo = fileManager.getFileInfo();
    if (fileNames != null) {

      // 1) a set of file names

      if (loadScript == null) {
        loadScript = new StringBuffer("load files");
        for (int i = 0; i < fileNames.length; i++)
          loadScript.append(" /*file*/$FILENAME" + (i + 1) + "$");
      }
      long timeBegin = System.currentTimeMillis();

      atomSetCollection = fileManager.createAtomSetCollectionFromFiles(
          fileNames, setLoadParameters(htParams, isAppend), isAppend);
      long ms = System.currentTimeMillis() - timeBegin;
      String msg = "";
      for (int i = 0; i < fileNames.length; i++)
        msg += (i == 0 ? "" : ",") + fileNames[i];
      Logger.info("openFiles(" + fileNames.length + ") " + ms + " ms");
      fileNames = (String[]) htParams.get("fullPathNames");
      String[] fileTypes = (String[]) htParams.get("fileTypes");
      String s = loadScript.toString();
      for (int i = 0; i < fileNames.length; i++) {
        String fname = fileNames[i];
        if (fileTypes != null && fileTypes[i] != null)
          fname = fileTypes[i] + "::" + fname;
        s = TextFormat.simpleReplace(s, "$FILENAME" + (i + 1) + "$", Escape
            .escape(fname.replace('\\', '/')));
      }

      loadScript = new StringBuffer(s);
    } else {
      if (loadScript == null)
        loadScript = new StringBuffer("load /*file*/$FILENAME$");

      if (reader == null)

        // 2) a standard, single file 

        atomSetCollection = getAtomSetCollection(fileName, isAppend, htParams,
            loadScript);

      else if (reader instanceof Reader)

        // 3) a file reader (not used by Jmol) 

        atomSetCollection = fileManager.createAtomSetCollectionFromReader(
            fullPathName, fileName, (Reader) reader,
            htParams = setLoadParameters(null, isAppend));
      else

        // 3) a DOM reader (could be used by Jmol) 

        atomSetCollection = fileManager.createAtomSetCollectionFromDOM(reader,
            htParams = setLoadParameters(null, isAppend));
    }

    // OK, the file has been read and is now closed.

    if (tokType != 0) { // all we are doing is reading atom data
      fileManager.setFileInfo(saveInfo);
      return loadAtomDataAndReturnError(atomSetCollection, tokType);
    }

    if (htParams.containsKey("isData")) {
      //      fileManager.setFileInfo(saveInfo);
      return (String) atomSetCollection;
    }

    // now we fix the load script (possibly) with the full path name
    if (loadScript != null) {
      String fname = (String) htParams.get("fullPathName");
      if (fname == null)
        fname = "";
      // may have been modified.
      if (htParams.containsKey("loadScript"))
        loadScript = (StringBuffer) htParams.get("loadScript");
      htParams.put("loadScript", loadScript = new StringBuffer(TextFormat
          .simpleReplace(loadScript.toString(), "$FILENAME$", Escape
              .escape(fname.replace('\\', '/')))));
    }

    // and finally to create the model set...

    return createModelSetAndReturnError(atomSetCollection, isAppend, loadScript);
  }

  /**
   * 
   * @param fileName
   * @param isAppend
   * @param htParams
   * @param loadScript  
   *            only necessary for string reading
   * @return            
   *            an AtomSetCollection or a String (error)
   */
  private Object getAtomSetCollection(String fileName, boolean isAppend,
                                      Map<String, Object> htParams,
                                      StringBuffer loadScript) {
    if (fileName == null)
      return null;
    if (fileName.indexOf("[]") >= 0) {
      // no reloading of string[] or file[] data -- just too complicated
      return null;
    }
    Object atomSetCollection;
    Logger.startTimer();
    htParams = setLoadParameters(htParams, isAppend);
    boolean isLoadVariable = fileName.startsWith("@");
    boolean haveFileData = (htParams.containsKey("fileData"));
    if (fileName.indexOf('$') == 0)
      htParams.put("smilesString", fileName.substring(1));
    boolean isString = (fileName.equalsIgnoreCase("string") || fileName
        .equals(JmolConstants.MODELKIT_ZAP_TITLE));
    String strModel = null;
    if (haveFileData) {
      strModel = (String) htParams.get("fileData");
      if (htParams.containsKey("isData")) {
        return loadInline(strModel, '\0', isAppend, htParams);
      }
   } else if (isString) {
      strModel = modelSet.getInlineData(-1);
      if (strModel == null)
        if (isModelKitMode())
          strModel = JmolConstants.MODELKIT_ZAP_STRING;
        else
         return "cannot find string data";
      if (loadScript != null)
        htParams.put("loadScript", loadScript = new StringBuffer(TextFormat.simpleReplace(
            loadScript.toString(), "$FILENAME$", 
            "data \"model inline\"\n" + strModel + "end \"model inline\"")));
    }
    if (strModel != null) {
      if (!isAppend)
        zap(true, false/*true*/, false);
      atomSetCollection = fileManager.createAtomSetCollectionFromString(
          strModel, loadScript, htParams, isAppend, isLoadVariable
              || haveFileData && !isString);
    } else {
      
      // if the filename has a "?" at the beginning, we don't zap, 
      // because the user might cancel the operation.
      
      atomSetCollection = fileManager.createAtomSetCollectionFromFile(fileName,
          htParams, isAppend);
    }
    Logger.checkTimer("openFile(" + fileName + ")");
    return atomSetCollection;
  }

  /// (unnecessarily) many inline versions
  
  @Override
  public String openStringInline(String strModel) {
    // JmolSimpleViewer
    return openStringInline(strModel, null, false);
  }

  @Override
  public String loadInline(String strModel) {
    // jmolViewer interface
    return loadInline(strModel, global.inlineNewlineChar, false, null);
  }

  @Override
  public String loadInline(String strModel, char newLine) {
    // JmolViewer interface
    return loadInline(strModel, newLine, false, null);
  }

  @Override
  public String loadInline(String strModel, boolean isAppend) {
    // JmolViewer interface
    return loadInline(strModel, (char) 0, isAppend, null);
  }

  @Override
  public String loadInline(String[] arrayModels) {
    // JmolViewer interface
    return loadInline(arrayModels, false);
  }

  @Override
  public String loadInline(String[] arrayModels, boolean isAppend) {
    // JmolViewer interface
    // Eval data
    // loadInline
    if (arrayModels == null || arrayModels.length == 0)
      return null;
    return openStringsInline(arrayModels, null, isAppend);
  }

  /**
   *  does not preserver state, intentionally!
   * @param arrayData 
   * @param isAppend 
   * @return            null or error string
   *  
   */
  @Override
  public String loadInline(List<Object> arrayData, boolean isAppend) {
    // NO STATE SCRIPT -- HERE WE ARE TRYING TO CONSERVE SPACE
    
    // loadInline
    if (arrayData == null || arrayData.size() == 0)
      return null;
    if (!isAppend)
      zap(true, false/*true*/, false);
    setBooleanProperty("preserveState", false);
    Object atomSetCollection = fileManager.createAtomSeCollectionFromArrayData(
        arrayData, setLoadParameters(null, isAppend), isAppend);
    return createModelSetAndReturnError(atomSetCollection, isAppend, null);
  }

  public String loadInline(String strModel, char newLine, boolean isAppend, Map<String, Object> htParams) {
    // ScriptEvaluator DATA command uses this, but anyone could.
    if (strModel == null || strModel.length() == 0)
      return null;
    if (strModel.startsWith("LOAD files")) {
      script(strModel);
      return null;
    }
    strModel = fixInlineString(strModel, newLine);
    if (newLine != 0)
      Logger.info("loading model inline, " + strModel.length()
          + " bytes, with newLine character " + (int) newLine + " isAppend="
          + isAppend);
    Logger.debug(strModel);
    String datasep = getDataSeparator();
    int i;
    if (datasep != null && datasep != ""
        && (i = strModel.indexOf(datasep)) >= 0
        && strModel.indexOf("# Jmol state") < 0) {
      int n = 2;
      while ((i = strModel.indexOf(datasep, i + 1)) >= 0)
        n++;
      String[] strModels = new String[n];
      int pt = 0, pt0 = 0;
      for (i = 0; i < n; i++) {
        pt = strModel.indexOf(datasep, pt0);
        if (pt < 0)
          pt = strModel.length();
        strModels[i] = strModel.substring(pt0, pt);
        pt0 = pt + datasep.length();
      }
      return openStringsInline(strModels, htParams, isAppend);
    }
    return openStringInline(strModel, htParams, isAppend);
  }

  public String fixInlineString(String strModel, char newLine) {
    // only if first character is "|" do we consider "|" to be new line
    int i;
    if (strModel.indexOf("\\/n") >= 0) {
      // the problem is that when this string is passed to Jmol
      // by the web page <embed> mechanism, browsers differ
      // in how they handle CR and LF. Some will pass it,
      // some will not.
      strModel = TextFormat.simpleReplace(strModel, "\n", "");
      strModel = TextFormat.simpleReplace(strModel, "\\/n", "\n");
      newLine = 0;
    }
    if (newLine != 0 && newLine != '\n') {
      boolean repEmpty = (strModel.indexOf('\n') >= 0);
      int len = strModel.length();
      for (i = 0; i < len && strModel.charAt(i) == ' '; ++i) {
      }
      if (i < len && strModel.charAt(i) == newLine)
        strModel = strModel.substring(i + 1);
      if (repEmpty)
        strModel = TextFormat.simpleReplace(strModel, "" + newLine, "");
      else
        strModel = strModel.replace(newLine, '\n');
    }
    return strModel;
  }

  // everything funnels to these two inline methods: String and String[]
  
  private String openStringInline(String strModel, Map<String, Object> htParams,
                                  boolean isAppend) {
    // loadInline, openStringInline

    BufferedReader br = new BufferedReader(new StringReader(strModel));
    String type = getModelAdapter().getFileTypeName(br);
    if (type == null)
      return "unknown file type";
    if (type.equals("spt")) {
      return "cannot open script inline";
    }

    htParams = setLoadParameters(htParams, isAppend);
    StringBuffer loadScript = (StringBuffer) htParams.get("loadScript");
    boolean isLoadCommand = htParams.containsKey("isData");
    if (loadScript == null)
      loadScript = new StringBuffer();
    if (!isAppend)
      zap(true, false/*true*/, false);
    Object atomSetCollection = fileManager.createAtomSetCollectionFromString(
        strModel, loadScript, htParams, isAppend, isLoadCommand);
    return createModelSetAndReturnError(atomSetCollection, isAppend, loadScript);
  }

  private String openStringsInline(String[] arrayModels, Map<String, Object> htParams,
                                   boolean isAppend) {
    // loadInline
    StringBuffer loadScript = new StringBuffer();
    if (!isAppend)
      zap(true, false/*true*/, false);
    Object atomSetCollection = fileManager.createAtomSeCollectionFromStrings(
        arrayModels, loadScript, setLoadParameters(htParams, isAppend), isAppend);
    return createModelSetAndReturnError(atomSetCollection, isAppend, loadScript);
  }

  public char getInlineChar() {
    // used by the ScriptEvaluator DATA command
    return global.inlineNewlineChar;
  }

  String getDataSeparator() {
    // used to separate data files within a single DATA command
    return (String) global.getParameter("dataseparator");
  }

  ////////// create the model set ////////////
  

  /**
   * finally(!) we are ready to create the 
   * "model set" from the "atom set collection"
   * 
   * @param atomSetCollection
   * @param isAppend
   * @param loadScript
   * @return errMsg
   */
  private String createModelSetAndReturnError(Object atomSetCollection,
                                              boolean isAppend,
                                              StringBuffer loadScript) {
    String fullPathName = fileManager.getFullPathName();
    String fileName = fileManager.getFileName();
    String errMsg;
    if (loadScript == null) {
      setBooleanProperty("preserveState", false);
      loadScript = new StringBuffer("load \"???\"");
    }
    if (atomSetCollection instanceof String) {
      errMsg = (String) atomSetCollection;
      setFileLoadStatus(JmolConstants.FILE_STATUS_NOT_LOADED, fullPathName, null, null,
          errMsg);
      if (displayLoadErrors && errMsg != null && !isAppend && !errMsg.equals("#CANCELED#"))
        zap(errMsg);
      return errMsg;
    }
    if (isAppend)
      clearAtomSets();
    else if (getModelkitMode() && !fileName.equals("Jmol Model Kit"))
      setModelKitMode(false);
    setFileLoadStatus(FILE_STATUS_CREATING_MODELSET, fullPathName, fileName,
        null, null);

    // null fullPathName implies we are doing a merge
    pushHoldRepaint("createModelSet");
    setErrorMessage(null);
    try {
      BitSet bsNew = new BitSet();
      modelSet = modelManager.createModelSet(fullPathName, fileName,
          loadScript, atomSetCollection, bsNew, isAppend);
      if (bsNew.cardinality() > 0) {
        String jmolScript = (String) modelSet
            .getModelSetAuxiliaryInfo("jmolscript");
        minimize(Integer.MAX_VALUE, 0, bsNew, null, 0, true, true, true);
        // no longer necessary? -- this is the JME/SMILES data:
        if (jmolScript != null)
          modelSet.getModelSetAuxiliaryInfo().put("jmolscript", jmolScript);
      }
      initializeModel(isAppend);
      // if (global.modelkitMode &&
      // (modelSet.getModelCount() > 1 || modelSet.getModels()[0].isPDB()))
      // setBooleanProperty("modelkitmode", false);

    } catch (Error er) {
      handleError(er, true);
      errMsg = getShapeErrorState();
      errMsg = ("ERROR creating model: " + er + (errMsg.length() == 0 ? ""
          : "|" + errMsg));
      zap(errMsg);
      setErrorMessage(errMsg);
    }
    popHoldRepaint("createModelSet");
    errMsg = getErrorMessage();

    setFileLoadStatus(JmolConstants.FILE_STATUS_MODELSET_CREATED, fullPathName, fileName,
        getModelSetName(), errMsg);
    if (isAppend) {
      selectAll();
      setTainted(true);
      axesAreTainted = true;
    }
    atomSetCollection = null;
    System.gc();
    return errMsg;
  }

  /**
   * 
   * or just apply the data to the current model set
   * 
   * @param atomSetCollection
   * @param tokType
   * @return error or null
   */
  private String loadAtomDataAndReturnError(Object atomSetCollection,
                                            int tokType) {
    if (atomSetCollection instanceof String)
      return (String) atomSetCollection;
    setErrorMessage(null);
    try {
      ((ModelLoader) modelSet).createAtomDataSet(tokType, atomSetCollection,
          getSelectionSet(false));
      if (tokType == Token.vibration)
        setStatusFrameChanged(Integer.MIN_VALUE);
    } catch (Error er) {
      handleError(er, true);
      String errMsg = getShapeErrorState();
      errMsg = ("ERROR adding atom data: " + er + (errMsg.length() == 0 ? ""
          : "|" + errMsg));
      zap(errMsg);
      setErrorMessage(errMsg);
      setParallel(false);
    }
    return getErrorMessage();
  }


  ////////// File-related methods ////////////
  
  public String writeCurrentFile(OutputStream os) {
    String filename = getFullPathName();
    if (filename.equals("string") || filename.indexOf("[]") >= 0
        || filename.equals("JSNode")) {
      String str = getCurrentFileAsString();
      BufferedOutputStream bos = new BufferedOutputStream(os);
      OutputStringBuffer sb = new OutputStringBuffer(bos);
      sb.append(str);
      return sb.toString();
    }
    String pathName = modelManager.getModelSetPathName();
    return (pathName == null ? "" : (String) getFileAsBytes(pathName, os));
  }

  @Override
  public Object getFileAsBytes(String pathName, OutputStream os) {
    return fileManager.getFileAsBytes(pathName, os);
  }

  public String getCurrentFileAsString() {
    String filename = getFullPathName();
    if (filename.equals("string") || filename.equals(JmolConstants.MODELKIT_ZAP_TITLE))
      return modelSet.getInlineData(getCurrentModelIndex());
    if (filename.indexOf("[]") >= 0)
      return filename;
    if (filename == "JSNode")
      return "<DOM NODE>";
    String pathName = modelManager.getModelSetPathName();
    if (pathName == null)
      return null;
    return getFileAsString(pathName, Integer.MAX_VALUE, true);
  }

  public String getFullPathName() {
    return fileManager.getFullPathName();
  }

  public String getFileName() {
    return fileManager.getFileName();
  }

  /**
   * 
   * @param filename
   * @return String[2] where [0] is fullpathname and [1] is error message or
   *         null
   */
  public String[] getFullPathNameOrError(String filename) {
    return fileManager.getFullPathNameOrError(filename);
  }

  @Override
  public String getFileAsString(String name) {
    return getFileAsString(name, Integer.MAX_VALUE, false);
  }

  public String getFileAsString(String name, int nBytesMax,
                                boolean doSpecialLoad) {
    if (name == null)
      return getCurrentFileAsString();
    String[] data = new String[2];
    data[0] = name;
    // ignore error completely
    getFileAsString(data, nBytesMax, doSpecialLoad);
    return data[1];
  }

  public String getFilePath(String name, boolean asShortName) {
    return fileManager.getFilePath(name, false, asShortName);
  }

  @Override
  public boolean getFileAsString(String[] data, int nBytesMax,
                                 boolean doSpecialLoad) {
    return fileManager.getFileDataOrErrorAsString(data, nBytesMax,
        doSpecialLoad);
  }

  public String[] getFileInfo() {
    return fileManager.getFileInfo();
  }

  public void setFileInfo(String[] fileInfo) {
    fileManager.setFileInfo(fileInfo);
  }

  // ///////////////////////////////////////////////////////////////
  // delegated to ModelManager
  // ///////////////////////////////////////////////////////////////

  public void autoCalculate(int tokProperty) {
    switch (tokProperty) {
    case Token.surfacedistance:
      modelSet.getSurfaceDistanceMax();
      break;
    case Token.straightness:
      modelSet.calculateStraightness();
      break;
    }
  }

  public float getVolume(BitSet bs, String type) {
    // Eval.calculate(), math function volume({atomExpression},"type")
    if (bs == null)
      bs = getSelectionSet(false);
    int iType = JmolConstants.getVdwType(type);
    if (iType == JmolConstants.VDW_UNKNOWN)
      iType = JmolConstants.VDW_AUTO;
    return modelSet.calculateVolume(bs, iType);
  }

  int getSurfaceDistanceMax() {
    return modelSet.getSurfaceDistanceMax();
  }

  public void calculateStraightness() {
    modelSet.setHaveStraightness(false);
    modelSet.calculateStraightness();
  }

  public Point3f[] calculateSurface(BitSet bsSelected, float envelopeRadius) {
    if (bsSelected == null)
      bsSelected = getSelectionSet(false);
    addStateScript("calculate surfaceDistance "
        + (envelopeRadius == Float.MAX_VALUE ? "FROM" : "WITHIN"), null,
        bsSelected, null, "", false, true);
    return modelSet.calculateSurface(bsSelected, envelopeRadius);
  }

  public float[][] getStructureList() {     
    return global.getStructureList();
  }

  public void setStructureList(float[] list, int type) {
    // none, turn, sheet, helix
    global.setStructureList(list, type);
    modelSet.setStructureList(getStructureList());
  }

  public boolean getDefaultStructureDSSP() {
    return global.defaultStructureDSSP;
  }
  
  public String getDefaultStructure(BitSet bsAtoms, BitSet bsAllAtoms) {
    if (bsAtoms == null)
      bsAtoms = getSelectionSet(false);
    return modelSet.getDefaultStructure(bsAtoms, bsAllAtoms);
  }

  public String calculateStructures(BitSet bsAtoms, boolean asDSSP, boolean setStructure) {
    // Eval
    if (bsAtoms == null)
      bsAtoms = getSelectionSet(false);
    return modelSet.calculateStructures(bsAtoms, asDSSP, global.dsspCalcHydrogen, setStructure);
  }

  public AtomIndexIterator getSelectedAtomIterator(BitSet bsSelected,
                                                   boolean isGreaterOnly,
                                                   boolean modelZeroBased) {
    return modelSet.getSelectedAtomIterator(bsSelected, isGreaterOnly,
        modelZeroBased, false);
  }

  public void setIteratorForAtom(AtomIndexIterator iterator, int atomIndex,
                                 float distance) {
    modelSet.setIteratorForAtom(iterator, -1, atomIndex, distance);
  }

  public void setIteratorForPoint(AtomIndexIterator iterator, int modelIndex, Point3f pt,
                                  float distance) {
    modelSet.setIteratorForPoint(iterator, modelIndex, pt, distance);
  }
  
  public void fillAtomData(AtomData atomData, int mode) {
    atomData.programInfo = "Jmol Version " + getJmolVersion();
    atomData.fileName = getFileName();
    modelSet.fillAtomData(atomData, mode);
  }

  public StateScript addStateScript(String script, boolean addFrameNumber,
                                    boolean postDefinitions) {
    return addStateScript(script, null, null, null, null, addFrameNumber,
        postDefinitions);
  }

  public StateScript addStateScript(String script1, BitSet bsBonds,
                                    BitSet bsAtoms1, BitSet bsAtoms2,
                                    String script2, boolean addFrameNumber,
                                    boolean postDefinitions) {
    return modelSet.addStateScript(script1, bsBonds, bsAtoms1, bsAtoms2,
        script2, addFrameNumber, postDefinitions);
  }

  public boolean getEchoStateActive() {
    return modelSet.getEchoStateActive();
  }

  public void setEchoStateActive(boolean TF) {
    modelSet.setEchoStateActive(TF);
  }

  public void zap(boolean notify, boolean resetUndo, boolean zapModelKit) {
    stopAnimationThreads("zap");
    if (modelSet != null) {
      //setBooleanProperty("appendNew", true);
      clearModelDependentObjects();
      fileManager.clear();
      repaintManager.clear();
      animationManager.clear();
      transformManager.clear();
      selectionManager.clear();
      clearAllMeasurements();
      clearMinimization();
      modelSet = modelManager.zap();
      if (haveDisplay) {
        mouseManager.clear();
        actionManager.clear();
      }
      stateManager.clear(global);
      tempManager.clear();
      colorManager.clear();
      definedAtomSets.clear();
      dataManager.clear();
      if (resetUndo) {
        if (zapModelKit && isModelKitMode()) {
          loadInline(JmolConstants.MODELKIT_ZAP_STRING); // a JME string for methane
          setRotationRadius(5.0f, true);
          setStringProperty("picking", "assignAtom_C");
          setStringProperty("picking", "assignBond_p");
        }
        actionStates.clear();
        actionStatesRedo.clear();
        lastUndoRedo = 0;
      }
      System.gc();
    } else {
      modelSet = modelManager.zap();
    }
    initializeModel(false);
    if (notify)
      setFileLoadStatus(JmolConstants.FILE_STATUS_ZAPPED, null, (resetUndo ? "resetUndo"
          : getZapName()), null, null);
    if (Logger.debugging)
      Logger.checkMemory();
  }

  private void zap(String msg) {
    zap(true, true, false);
    echoMessage(msg);
  }

  void echoMessage(String msg) {
    int iShape = JmolConstants.SHAPE_ECHO;
    loadShape(iShape);
    setShapeProperty(iShape, "font", getFont3D("SansSerif", "Plain", 9));
    setShapeProperty(iShape, "target", "error");
    setShapeProperty(iShape, "text", msg);
  }

  private void initializeModel(boolean isAppend) {
    stopAnimationThreads("stop from init model");
    if (isAppend) {
      animationManager.initializePointers(1);
      return;
    }
    reset(true);
    selectAll();
    rotatePrev1 = rotateBondIndex = -1;
    movingSelected = false;
    noneSelected = false;
    hoverEnabled = true;
    transformManager.setCenter();
    clearAtomSets();
    animationManager.initializePointers(1);
    setCurrentModelIndex(0);
    setBackgroundModelIndex(-1);
    setFrankOn(getShowFrank());
    if (haveDisplay)
      actionManager.startHoverWatcher(true);
    setTainted(true);
    finalizeTransformParameters();
  }

  @Override
  public String getModelSetName() {
    if (modelSet == null)
      return null;
    return modelSet.getModelSetName();
  }

  @Override
  public String getModelSetFileName() {
    return modelManager.getModelSetFileName();
  }

  public String getUnitCellInfoText() {
    return modelSet.getUnitCellInfoText();
  }

  public float getUnitCellInfo(int infoType) {
    SymmetryInterface symmetry = getCurrentUnitCell();
    if (symmetry == null)
      return Float.NaN;
    return symmetry.getUnitCellInfo(infoType);
  }

  public Map<String, Object> getSpaceGroupInfo(String spaceGroup) {
    return modelSet.getSpaceGroupInfo(-1, spaceGroup, 0, null, null, null);
  }

  public void getPolymerPointsAndVectors(BitSet bs, List<Point3f[]> vList) {
    modelSet.getPolymerPointsAndVectors(bs, vList);
  }

  public String getModelSetProperty(String strProp) {
    // no longer used in Jmol
    return modelSet.getModelSetProperty(strProp);
  }

  public Object getModelSetAuxiliaryInfo(String strKey) {
    return modelSet.getModelSetAuxiliaryInfo(strKey);
  }

  @Override
  public String getModelSetPathName() {
    return modelManager.getModelSetPathName();
  }

  public String getModelSetTypeName() {
    return modelSet.getModelSetTypeName();
  }

  @Override
  public boolean haveFrame() {
    return haveModelSet();
  }

  boolean haveModelSet() {
    return modelSet != null;
  }

  public void clearBfactorRange() {
    // Eval
    modelSet.clearBfactorRange();
  }

  public String getHybridizationAndAxes(int atomIndex, Vector3f z, Vector3f x,
                                        String lcaoType) {
    return modelSet.getHybridizationAndAxes(atomIndex, z, x, lcaoType,
        true, true);
  }

  public BitSet getMoleculeBitSet(int atomIndex) {
    return modelSet.getMoleculeBitSet(atomIndex);
  }

  public BitSet getModelUndeletedAtomsBitSet(int modelIndex) {
    BitSet bs = modelSet.getModelAtomBitSetIncludingDeleted(modelIndex, true);
    excludeAtoms(bs, false);
    return bs;
  }

  public BitSet getModelUndeletedAtomsBitSet(BitSet bsModels) {
    BitSet bs = modelSet.getModelAtomBitSetIncludingDeleted(bsModels);
    excludeAtoms(bs, false);
    return bs;
  }

  public void excludeAtoms(BitSet bs, boolean ignoreSubset) {
    selectionManager.excludeAtoms(bs, ignoreSubset);
  }

  public BitSet getModelBitSet(BitSet atomList, boolean allTrajectories) {
    return modelSet.getModelBitSet(atomList, allTrajectories);
  }

  public ModelSet getModelSet() {
    return modelSet;
  }

  public String getBoundBoxCommand(boolean withOptions) {
    return modelSet.getBoundBoxCommand(withOptions);
  }

  public void setBoundBox(Point3f pt1, Point3f pt2, boolean byCorner,
                          float scale) {
    modelSet.setBoundBox(pt1, pt2, byCorner, scale);
  }

  public Point3f getBoundBoxCenter() {
    return modelSet.getBoundBoxCenter(animationManager.currentModelIndex);
  }

  Point3f getAverageAtomPoint() {
    return modelSet.getAverageAtomPoint();
  }

  public void calcBoundBoxDimensions(BitSet bs, float scale) {
    modelSet.calcBoundBoxDimensions(bs, scale);
    axesAreTainted = true;
  }

  public BoxInfo getBoxInfo(BitSet bs, float scale) {
    return modelSet.getBoxInfo(bs, scale);
  }

  float calcRotationRadius(Point3f center) {
    return modelSet.calcRotationRadius(animationManager.currentModelIndex,
        center);
  }

  public float calcRotationRadius(BitSet bs) {
    return modelSet.calcRotationRadius(bs);
  }

  public Vector3f getBoundBoxCornerVector() {
    return modelSet.getBoundBoxCornerVector();
  }

  public Point3f[] getBoundBoxVertices() {
    return modelSet.getBboxVertices();
  }

  Map<String, Object> getBoundBoxInfo() {
    return modelSet.getBoundBoxInfo();
  }

  public BitSet getBoundBoxModels() {
    return modelSet.getBoundBoxModels();
  }

  public int getBoundBoxCenterX() {
    // used by axes renderer
    return dimScreen.width / 2;
  }

  public int getBoundBoxCenterY() {
    return dimScreen.height / 2;
  }

  @Override
  public int getModelCount() {
    return modelSet.getModelCount();
  }

  public String getModelInfoAsString() {
    return modelSet.getModelInfoAsString();
  }

  public String getSymmetryInfoAsString() {
    return modelSet.getSymmetryInfoAsString();
  }

  public String getSymmetryOperation(String spaceGroup, int symop, Point3f pt1,
                                     Point3f pt2, boolean labelOnly) {
    return modelSet.getSymmetryOperation(animationManager.currentModelIndex,
        spaceGroup, symop, pt1, pt2, null, labelOnly);
  }

  @Override
  public Properties getModelSetProperties() {
    return modelSet.getModelSetProperties();
  }

  @Override
  public Map<String, Object> getModelSetAuxiliaryInfo() {
    return modelSet.getModelSetAuxiliaryInfo();
  }

  @Override
  public int getModelNumber(int modelIndex) {
    if (modelIndex < 0)
      return modelIndex;
    return modelSet.getModelNumber(modelIndex);
  }

  public int getModelFileNumber(int modelIndex) {
    if (modelIndex < 0)
      return 0;
    return modelSet.getModelFileNumber(modelIndex);
  }

  @Override
  public String getModelNumberDotted(int modelIndex) {
    return modelIndex < 0 ? "0" : modelSet == null ? null : modelSet
        .getModelNumberDotted(modelIndex);
  }

  @Override
  public String getModelName(int modelIndex) {
    return modelSet == null ? null : modelSet.getModelName(modelIndex);
  }

  @Override
  public Properties getModelProperties(int modelIndex) {
    return modelSet.getModelProperties(modelIndex);
  }

  @Override
  public String getModelProperty(int modelIndex, String propertyName) {
    return modelSet.getModelProperty(modelIndex, propertyName);
  }

  public String getModelFileInfo() {
    return modelSet.getModelFileInfo(getVisibleFramesBitSet());
  }

  public String getModelFileInfoAll() {
    return modelSet.getModelFileInfo(null);
  }

  @Override
  public Map<String, Object> getModelAuxiliaryInfo(int modelIndex) {
    return modelSet.getModelAuxiliaryInfo(modelIndex);
  }

  @Override
  public Object getModelAuxiliaryInfo(int modelIndex, String keyName) {
    return modelSet.getModelAuxiliaryInfo(modelIndex, keyName);
  }

  public int getModelNumberIndex(int modelNumber, boolean useModelNumber,
                                 boolean doSetTrajectory) {
    return modelSet.getModelNumberIndex(modelNumber, useModelNumber,
        doSetTrajectory);
  }

  boolean modelSetHasVibrationVectors() {
    return modelSet.modelSetHasVibrationVectors();
  }

  @Override
  public boolean modelHasVibrationVectors(int modelIndex) {
    return modelSet.modelHasVibrationVectors(modelIndex);
  }

  @Override
  public int getChainCount() {
    return modelSet.getChainCount(true);
  }

  @Override
  public int getChainCountInModel(int modelIndex) {
    // revised to NOT include water chain (for menu)
    return modelSet.getChainCountInModel(modelIndex, false);
  }

  public int getChainCountInModel(int modelIndex, boolean countWater) {
    return modelSet.getChainCountInModel(modelIndex, countWater);
  }

  @Override
  public int getGroupCount() {
    return modelSet.getGroupCount();
  }

  @Override
  public int getGroupCountInModel(int modelIndex) {
    return modelSet.getGroupCountInModel(modelIndex);
  }

  @Override
  public int getPolymerCount() {
    return modelSet.getBioPolymerCount();
  }

  @Override
  public int getPolymerCountInModel(int modelIndex) {
    return modelSet.getBioPolymerCountInModel(modelIndex);
  }

  @Override
  public int getAtomCount() {
    return modelSet.getAtomCount();
  }

  @Override
  public int getAtomCountInModel(int modelIndex) {
    return modelSet.getAtomCountInModel(modelIndex);
  }

  /**
   * For use in setting a for() construct max value
   * 
   * @return used size of the bonds array;
   */
  @Override
  public int getBondCount() {
    return modelSet.getBondCount();
  }

  /**
   * from JmolPopup.udateModelSetComputedMenu
   * 
   * @param modelIndex
   *          the model of interest or -1 for all
   * @return the actual number of connections
   */
  @Override
  public int getBondCountInModel(int modelIndex) {
    return modelSet.getBondCountInModel(modelIndex);
  }

  public BitSet getBondsForSelectedAtoms(BitSet bsAtoms) {
    // eval
    return modelSet.getBondsForSelectedAtoms(bsAtoms, global.bondModeOr
        || BitSetUtil.cardinalityOf(bsAtoms) == 1);
  }

  public boolean frankClicked(int x, int y) {
    return !global.disablePopupMenu && getShowFrank()
        && shapeManager.frankClicked(x, y);
  }

  public boolean frankClickedModelKit(int x, int y) {
    return !global.disablePopupMenu && isModelKitMode()
        && x >= 0 && y >= 0 && x < 40 && y < 80;
  }

  @Override
  public int findNearestAtomIndex(int x, int y) {
    return findNearestAtomIndex(x, y, false);
      }

  public int findNearestAtomIndex(int x, int y, boolean mustBeMovable) {
    return (modelSet == null || !getAtomPicking() ? -1 : modelSet
        .findNearestAtomIndex(x, y, mustBeMovable ? selectionManager.getMotionFixedAtoms() : null));
  }

  BitSet findAtomsInRectangle(Rectangle rect) {
    return modelSet.findAtomsInRectangle(rect, getVisibleFramesBitSet());
  }

  
  /**
   * absolute or relative to origin of UNITCELL {x y z}
   * @param pt
   * @param asAbsolute TODO
   */
  public void toCartesian(Point3f pt, boolean asAbsolute) {
    SymmetryInterface unitCell = getCurrentUnitCell();
    if (unitCell != null)
      unitCell.toCartesian(pt, asAbsolute);
  }

  /**
   * absolute or relative to origin of UNITCELL {x y z}
   * @param pt
   * @param asAbsolute TODO
   */
  public void toFractional(Point3f pt, boolean asAbsolute) {
    SymmetryInterface unitCell = getCurrentUnitCell();
    if (unitCell != null)
      unitCell.toFractional(pt, asAbsolute);
  }

  /**
   * relative to origin without regard to UNITCELL {x y z}
   * @param pt
   * @param offset
   */
  public void toUnitCell(Point3f pt, Point3f offset) {
    SymmetryInterface unitCell = getCurrentUnitCell();
    if (unitCell != null)
      unitCell.toUnitCell(pt, offset);
  }

  public void setCurrentUnitCellOffset(int ijk) {
    SymmetryInterface unitCell = getCurrentUnitCell();
    if (unitCell == null)
      return;
    unitCell.setOffset(ijk);
    global.setParameterValue("=frame " + getModelNumberDotted(animationManager.currentModelIndex)
          + "; set unitcell ", ijk);
  }

  public void setCurrentUnitCellOffset(Point3f pt) {
    // from "unitcell {i j k}" via uccage
    SymmetryInterface unitCell = getCurrentUnitCell();
    if (unitCell == null)
      return;
    unitCell.setUnitCellOffset(pt);
    global.setParameterValue("=frame " + getModelNumberDotted(animationManager.currentModelIndex)
          + "; set unitcell ", Escape.escape(pt));
  }

  public boolean getFractionalRelative() {
    return global.fractionalRelative;
  }
  
  public void addUnitCellOffset(Point3f pt) {
    SymmetryInterface unitCell = getCurrentUnitCell();
    if (unitCell == null)
      return;
    pt.add(unitCell.getCartesianOffset());
  }


  public void setAtomData(int type, String name, String coordinateData,
                          boolean isDefault) {
    modelSet.setAtomData(type, name, coordinateData, isDefault);
    refreshMeasures(true);
  }

  @Override
  public void setCenterSelected() {
    // depricated
    setCenterBitSet(getSelectionSet(false), true);
  }

  public boolean getApplySymmetryToBonds() {
    return global.applySymmetryToBonds;
  }

  void setApplySymmetryToBonds(boolean TF) {
    global.applySymmetryToBonds = TF;
  }

  @Override
  public void setBondTolerance(float bondTolerance) {
    global.setParameterValue("bondTolerance", bondTolerance);
    global.bondTolerance = bondTolerance;
  }

  @Override
  public float getBondTolerance() {
    return global.bondTolerance;
  }

  @Override
  public void setMinBondDistance(float minBondDistance) {
    // PreferencesDialog
    global.setParameterValue("minBondDistance", minBondDistance);
    global.minBondDistance = minBondDistance;
  }

  @Override
  public float getMinBondDistance() {
    return global.minBondDistance;
  }

  public int[] getAtomIndices(BitSet bs) {
    return modelSet.getAtomIndices(bs);
  }

  public BitSet getAtomBits(int tokType, Object specInfo) {
    return modelSet.getAtomBits(tokType, specInfo);
  }

  public BitSet getSequenceBits(String specInfo, BitSet bs) {
    return modelSet.getSequenceBits(specInfo, bs);
  }

  public BitSet getAtomsWithin(float distance, Point3f coord) {
    BitSet bs = new BitSet();
    modelSet.getAtomsWithin(distance, coord, bs, -1);
    return bs;
  }

  public BitSet getAtomsWithin(float distance, Point4f plane) {
    return modelSet.getAtomsWithin(distance, plane);
  }

  public BitSet getAtomsWithin(float distance, BitSet bs,
                               boolean withinAllModels) {
    return modelSet.getAtomsWithin(distance, bs, withinAllModels);
  }

  public BitSet getAtomsConnected(float min, float max, int intType, BitSet bs) {
    return modelSet.getAtomsConnected(min, max, intType, bs);
  }

  public BitSet getBranchBitSet(int atomIndex, int atomIndexNot) {
    if (atomIndex < 0 || atomIndex >= getAtomCount())
      return new BitSet();
    return JmolMolecule.getBranchBitSet(modelSet.atoms,
        getModelUndeletedAtomsBitSet(modelSet.atoms[atomIndex].modelIndex),
        atomIndex, atomIndexNot, true, true);
  }

  public int getAtomIndexFromAtomNumber(int atomNumber) {
    return modelSet.getAtomIndexFromAtomNumber(atomNumber,
        getVisibleFramesBitSet());
  }

  @Override
  public BitSet getElementsPresentBitSet(int modelIndex) {
    return modelSet.getElementsPresentBitSet(modelIndex);
  }

  @Override
  public Map<String, String> getHeteroList(int modelIndex) {
    return modelSet.getHeteroList(modelIndex);
  }

  public BitSet getVisibleSet() {
    return modelSet.getVisibleSet();
  }

  public BitSet getClickableSet() {
    return modelSet.getClickableSet();
  }

  public void calcSelectedGroupsCount() {
    modelSet.calcSelectedGroupsCount(getSelectionSet(false));
  }

  public void calcSelectedMonomersCount() {
    modelSet.calcSelectedMonomersCount(getSelectionSet(false));
  }

  public void calcSelectedMoleculesCount() {
    modelSet.calcSelectedMoleculesCount(getSelectionSet(false));
  }

  String getFileHeader() {
    return modelSet.getFileHeader(animationManager.currentModelIndex);
  }

  Object getFileData() {
    return modelSet.getFileData(animationManager.currentModelIndex);
  }

  public Map<String, Object> getCifData(int modelIndex) {
    String name = getModelFileName(modelIndex);
    String data = getFileAsString(name);
    if (data == null)
      return null;
    return CifDataReader
        .readCifData(new BufferedReader(new StringReader(data)));
  }

  public String getPDBHeader() {
    return modelSet.getPDBHeader(animationManager.currentModelIndex);
  }

  public Map<String, Object> getModelInfo(Object atomExpression) {
    return modelSet.getModelInfo(getModelBitSet(getAtomBitSet(atomExpression),
        false));
  }

  public Map<String, Object> getAuxiliaryInfo(Object atomExpression) {
    return modelSet.getAuxiliaryInfo(getModelBitSet(
        getAtomBitSet(atomExpression), false));
  }

  List<Map<String, Object>> getAllAtomInfo(Object atomExpression) {
    return modelSet.getAllAtomInfo(getAtomBitSet(atomExpression));
  }

  List<Map<String, Object>> getAllBondInfo(Object atomExpression) {
    return modelSet.getAllBondInfo(getAtomBitSet(atomExpression));
  }

  List<Map<String, Object>> getMoleculeInfo(Object atomExpression) {
    return modelSet.getMoleculeInfo(getAtomBitSet(atomExpression));
  }

  public String getChimeInfo(int tok) {
    return modelSet.getChimeInfo(tok, getSelectionSet(true));
  }

  public Map<String, List<Map<String, Object>>> getAllChainInfo(Object atomExpression) {
    return modelSet.getAllChainInfo(getAtomBitSet(atomExpression));
  }

  public Map<String, List<Map<String, Object>>> getAllPolymerInfo(Object atomExpression) {
    return modelSet.getAllPolymerInfo(getAtomBitSet(atomExpression));
  }

  public String getWrappedState(boolean isImage) {
    if (isImage && !global.imageState || !global.preserveState)
      return "";
    // we remove local file references in the embedded states for images
    String s = "";
    try {
      s = JmolConstants.embedScript(FileManager.setScriptFileReferences(
          getStateInfo(null), ".", null, null));
    } catch (Throwable e) {
      // ignore if this uses too much memory
      Logger.error("state could not be saved: " + e.getMessage());
      s = "Jmol " + getJmolVersion();
    }
    return s;
  }

  @Override
  public String getStateInfo() {
    return getStateInfo(null);
  }

  public final static String STATE_VERSION_STAMP = "# Jmol state version ";

  public String getStateInfo(String type) {
    // System.out.println("viewer getStateInfo " + type);
    if (!global.preserveState)
      return "";
    boolean isAll = (type == null || type.equalsIgnoreCase("all"));
    StringBuffer s = new StringBuffer("");
    StringBuffer sfunc = (isAll ? new StringBuffer("function _setState() {\n")
        : null);
    if (isAll)
      s.append(STATE_VERSION_STAMP + getJmolVersion() + ";\n");
    if (isApplet && isAll) {
      StateManager.appendCmd(s, "# fullName = " + Escape.escape(fullName));
      StateManager.appendCmd(s, "# documentBase = "
          + Escape.escape(appletDocumentBase));
      StateManager
          .appendCmd(s, "# codeBase = " + Escape.escape(appletCodeBase));
      s.append("\n");
    }
    // window state
    if (isAll || type.equalsIgnoreCase("windowState"))
      s.append(global.getWindowState(sfunc));
    //if (isAll)
      //s.append(getFunctionCalls(null)); // removed in 12.1.16; unnecessary in state
    // file state
    if (isAll || type.equalsIgnoreCase("fileState"))
      s.append(fileManager.getState(sfunc));
    // all state scripts (definitions, dataFrames, calculations, configurations,
    // rebonding
    if (isAll || type.equalsIgnoreCase("definedState"))
      s.append(modelSet.getDefinedState(sfunc, true));
    // numerical values
    if (isAll || type.equalsIgnoreCase("variableState"))
      s.append(global.getState(sfunc)); // removed in 12.1.16; unnecessary in state // ARGH!!!
    if (isAll || type.equalsIgnoreCase("dataState"))
      dataManager.getDataState(s, sfunc, modelSet.getAtomicPropertyState(-1,
          null));
    // connections, atoms, bonds, labels, echos, shapes
    if (isAll || type.equalsIgnoreCase("modelState"))
      s.append(modelSet.getState(sfunc, true, getBooleanProperty("saveProteinStructureState")));
    // color scheme
    if (isAll || type.equalsIgnoreCase("colorState"))
      s.append(colorManager.getState(sfunc));
    // frame information
    if (isAll || type.equalsIgnoreCase("frameState"))
      s.append(animationManager.getState(sfunc));
    // orientation and slabbing
    if (isAll || type.equalsIgnoreCase("perspectiveState"))
      s.append(transformManager.getState(sfunc));
    // display and selections
    if (isAll || type.equalsIgnoreCase("selectionState"))
      s.append(selectionManager.getState(sfunc));
    if (sfunc != null) {
      StateManager.appendCmd(sfunc, "set refreshing true");
      StateManager.appendCmd(sfunc, "set antialiasDisplay "
          + global.antialiasDisplay);
      StateManager.appendCmd(sfunc, "set antialiasTranslucent "
          + global.antialiasTranslucent);
      StateManager.appendCmd(sfunc, "set antialiasImages "
          + global.antialiasImages);
      if (getSpinOn())
        StateManager.appendCmd(sfunc, "spin on");
      sfunc.append("}\n\n_setState;\n");
    }
    if (isAll)
      s.append(sfunc);
    return s.toString();
  }

  public String getStructureState() {
    return modelSet.getState(null, false, true);
  }

  public String getProteinStructureState() {
    return modelSet.getProteinStructureState(
        getSelectionSet(false), false, false, 3);
  }

  public String getCoordinateState(BitSet bsSelected) {
    return modelSet.getAtomicPropertyState(AtomCollection.TAINT_COORD,
        bsSelected);
  }

  public void setCurrentColorRange(String label) {
    float[] data = getDataFloat(label);
    BitSet bs = (data == null ? null : (BitSet) (dataManager.getData(label))[2]);
    if (bs != null && isRangeSelected())
      bs.and(getSelectionSet(false));
    setCurrentColorRange(data, bs);
  }

  public void setCurrentColorRange(float[] data, BitSet bs) {
    colorManager.setPropertyColorRange(data, bs, global.propertyColorScheme);
  }

  public void setCurrentColorRange(float min, float max) {
    colorManager.setPropertyColorRange(min, max);
  }

  public void setData(String type, Object[] data, int atomCount,
                      int matchField, int matchFieldColumnCount, int field,
                      int fieldColumnCount) {
    dataManager.setData(type, data, atomCount, matchField,
        matchFieldColumnCount, field, fieldColumnCount);
  }

  public Object[] getData(String type) {
    return dataManager.getData(type);
  }

  public float[] getDataFloat(String label) {
    return dataManager.getDataFloat(label);
  }

  public float[][] getDataFloat2D(String label) {
    return dataManager.getDataFloat2D(label);
  }

  public float[][][] getDataFloat3D(String label) {
    return dataManager.getDataFloat3D(label);
  }

  public float getDataFloat(String label, int atomIndex) {
    return dataManager.getDataFloat(label, atomIndex);
  }

  @Override
  public String getAltLocListInModel(int modelIndex) {
    return modelSet.getAltLocListInModel(modelIndex);
  }

  public BitSet setConformation() {
    // user has selected some atoms, now this sets that as a conformation
    // with the effect of rewriting the cartoons to match

    return modelSet.setConformation(getSelectionSet(false));
  }

  // AKA "configuration"
  public BitSet getConformation(int iModel, int conformationIndex, boolean doSet) {
    return modelSet.getConformation(iModel, conformationIndex, doSet);
  }

  // boolean autoLoadOrientation() {
  // return true;//global.autoLoadOrientation; 12.0.RC10
  // }

  public int autoHbond(BitSet bsFrom, BitSet bsTo) {
    if (bsFrom == null)
      bsFrom = bsTo = getSelectionSet(false);
    // bsTo null --> use DSSP method further developed 
    // here to give the "defining" Hbond set only
    return modelSet.autoHbond(bsFrom, bsTo);
  }

  public float getHbondsAngleMin() {
    return global.hbondsAngleMinimum;
  }

  public float getHbondsDistanceMax() {
    return global.hbondsDistanceMaximum;
  }

  public boolean getHbondsRasmol() {
    return global.hbondsRasmol;
  }

  public boolean hasCalculatedHBonds(BitSet bsAtoms) {
    return modelSet.hasCalculatedHBonds(bsAtoms);
  }

  @Override
  public boolean havePartialCharges() {
    return modelSet.getPartialCharges() != null;
  }

  public SymmetryInterface getCurrentUnitCell() {
    return modelSet.getUnitCell(animationManager.currentModelIndex);
  }

  public SymmetryInterface getModelUnitCell(int modelIndex) {
    return modelSet.getUnitCell(modelIndex);
  }


  /*
   * ****************************************************************************
   * delegated to MeasurementManager
   * **************************************************************************
   */

  public String getDefaultMeasurementLabel(int nPoints) {
    switch (nPoints) {
    case 2:
      return global.defaultDistanceLabel;
    case 3:
      return global.defaultAngleLabel;
    default:
      return global.defaultTorsionLabel;
    }
  }

  @Override
  public int getMeasurementCount() {
    int count = getShapePropertyAsInt(JmolConstants.SHAPE_MEASURES, "count");
    return count <= 0 ? 0 : count;
  }

  @Override
  public String getMeasurementStringValue(int i) {
    String str = ""
        + getShapeProperty(JmolConstants.SHAPE_MEASURES, "stringValue", i);
    return str;
  }

  @SuppressWarnings("unchecked")
  List<Map<String, Object>> getMeasurementInfo() {
    return (List<Map<String, Object>>) getShapeProperty(JmolConstants.SHAPE_MEASURES, "info");
  }

  public String getMeasurementInfoAsString() {
    return (String) getShapeProperty(JmolConstants.SHAPE_MEASURES, "infostring");
  }

  @Override
  public int[] getMeasurementCountPlusIndices(int i) {
    int[] List = (int[]) getShapeProperty(JmolConstants.SHAPE_MEASURES,
        "countPlusIndices", i);
    return List;
  }

  void setPendingMeasurement(MeasurementPending measurementPending) {
    // from MouseManager
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "pending",
        measurementPending);
  }

  MeasurementPending getPendingMeasurement() {
    return (MeasurementPending) getShapeProperty(JmolConstants.SHAPE_MEASURES,
        "pending");
  }

  public void clearAllMeasurements() {
    // Eval only
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "clear", null);
  }

  @Override
  public void clearMeasurements() {
    // depricated but in the API -- use "script" directly
    // see clearAllMeasurements()
    evalString("measures delete");
  }

  public boolean getJustifyMeasurements() {
    return global.justifyMeasurements;
  }

  // ///////////////////////////////////////////////////////////////
  // delegated to AnimationManager
  // ///////////////////////////////////////////////////////////////

  public void setAnimation(int tok) {
    switch (tok) {
    case Token.playrev:
      animationManager.reverseAnimation();
      // fall through
    case Token.play:
    case Token.resume:
      if (!animationManager.animationOn)
        animationManager.resumeAnimation();
      return;
    case Token.pause:
      if (animationManager.animationOn && !animationManager.animationPaused)
        animationManager.pauseAnimation();
      return;
    case Token.next:
      animationManager.setAnimationNext();
      return;
    case Token.prev:
      animationManager.setAnimationPrevious();
      return;
    case Token.first:
    case Token.rewind:
      animationManager.rewindAnimation();
      return;
    case Token.last:
      animationManager.setAnimationLast();
      return;
    }
  }

  public void setAnimationDirection(int direction) {// 1 or -1
    // Eval
    animationManager.setAnimationDirection(direction);
  }

  int getAnimationDirection() {
    return animationManager.animationDirection;
  }

  Map<String, Object> getAnimationInfo() {
    return animationManager.getAnimationInfo();
  }

  @Override
  public void setAnimationFps(int fps) {
    if (fps < 1)
      fps = 1;
    if (fps > 50)
      fps = 50;
    global.setParameterValue("animationFps", fps);
    // Eval
    // app AtomSetChooser
    animationManager.setAnimationFps(fps);
  }

  @Override
  public int getAnimationFps() {
    return animationManager.animationFps;
  }

  public void setAnimationReplayMode(int replay, float firstFrameDelay,
                                     float lastFrameDelay) {
    // Eval

    // 0 means once
    // 1 means loop
    // 2 means palindrome
    animationManager.setAnimationReplayMode(replay, firstFrameDelay,
        lastFrameDelay);
  }

  int getAnimationReplayMode() {
    return animationManager.animationReplayMode;
  }

  public void setAnimationOn(boolean animationOn) {
    // Eval
    boolean wasAnimating = animationManager.animationOn;
    if (animationOn == wasAnimating)
      return;
    animationManager.setAnimationOn(animationOn);
  }

  public void setAnimationRange(int modelIndex1, int modelIndex2) {
    animationManager.setAnimationRange(modelIndex1, modelIndex2);
  }

  @Override
  public BitSet getVisibleFramesBitSet() {
    BitSet bs = BitSetUtil.copy(animationManager.getVisibleFramesBitSet());
    modelSet.selectDisplayedTrajectories(bs);
    return bs;
  }

  boolean isAnimationOn() {
    return animationManager.animationOn;
  }

  public void setCurrentModelIndex(int modelIndex) {
    // Eval
    // initializeModel
    if (modelIndex == Integer.MIN_VALUE) {
      // just forcing popup menu update
      prevFrame = Integer.MIN_VALUE;
      setCurrentModelIndex(animationManager.currentModelIndex, true);
      return;
    }
    animationManager.setCurrentModelIndex(modelIndex);
  }

  void setTrajectory(int modelIndex) {
    modelSet.setTrajectory(modelIndex);
  }

  public void setTrajectory(BitSet bsModels) {
    modelSet.setTrajectory(bsModels);
  }

  public boolean isTrajectory(int modelIndex) {
    return modelSet.isTrajectory(modelIndex);
  }

  public BitSet getBitSetTrajectories() {
    return modelSet.getBitSetTrajectories();
  }

  public String getTrajectoryInfo() {
    return modelSet.getTrajectoryInfo();
  }

  void setFrameOffset(int modelIndex) {
    transformManager.setFrameOffset(modelIndex);
  }

  BitSet bsFrameOffsets;
  Point3f[] frameOffsets;

  public void setFrameOffsets(BitSet bsAtoms) {
    bsFrameOffsets = bsAtoms;
    transformManager.setFrameOffsets(frameOffsets = modelSet
        .getFrameOffsets(bsFrameOffsets));
  }

  public BitSet getFrameOffsets() {
    return bsFrameOffsets;
  }

  public void setCurrentModelIndex(int modelIndex, boolean clearBackground) {
    // Eval
    // initializeModel
    animationManager.setCurrentModelIndex(modelIndex, clearBackground);
  }

  public int getCurrentModelIndex() {
    return animationManager.currentModelIndex;
  }

  @Override
  public int getDisplayModelIndex() {
    // abandoned
    return animationManager.currentModelIndex;
  }

  public boolean haveFileSet() {
    return (getModelCount() > 1 && getModelNumber(0) > 1000000);
  }

  public void setBackgroundModelIndex(int modelIndex) {
    // initializeModel
    animationManager.setBackgroundModelIndex(modelIndex);
    global.setParameterValue("backgroundModel", modelSet
        .getModelNumberDotted(modelIndex));
  }

  void setFrameVariables(int firstModelIndex, int lastModelIndex) {
    global.setParameterValue("_firstFrame",
        getModelNumberDotted(firstModelIndex));
    global
        .setParameterValue("_lastFrame", getModelNumberDotted(lastModelIndex));
  }

  boolean wasInMotion = false;
  int motionEventNumber;

  @Override
  public int getMotionEventNumber() {
    return motionEventNumber;
  }

  void setInMotion(boolean inMotion) {
    // MouseManager, TransformManager
    if (wasInMotion ^ inMotion) {
      animationManager.setInMotion(inMotion);
      if (inMotion) {
        ++motionEventNumber;
      } else {
        refresh(3, "viewer stInMotion " + inMotion);
      }
      wasInMotion = inMotion;
    }
  }

  public boolean getInMotion() {
    // mps
    return animationManager.inMotion;
  }

  @Override
  public void pushHoldRepaint() {
    pushHoldRepaint(null);
  }

  /**
   * 
   * @param why
   */
  public void pushHoldRepaint(String why) {
    // System.out.println("viewer pushHoldRepaint " + why);
    repaintManager.pushHoldRepaint();
  }

  @Override
  public void popHoldRepaint() {
    //System.out.println("viewer popHoldRepaint don't know why");
        repaintManager.popHoldRepaint(true);
  }

  public void popHoldRepaint(String why) {
    //if (!why.equals("pause"))
      //System.out.println("viewer popHoldRepaint " + why);
    repaintManager.popHoldRepaint(!why.equals("pause"));
  }

  private boolean refreshing = true;

  private void setRefreshing(boolean TF) {
    refreshing = TF;
  }

  public boolean getRefreshing() {
    return refreshing;
  }

  /**
   * initiate a repaint/update sequence if it has not already been requested.
   * invoked whenever any operation causes changes that require new rendering.
   * 
   * The repaint/update sequence will only be invoked if (a) no repaint is
   * already pending and (b) there is no hold flag set in repaintManager.
   * 
   * Sequence is as follows:
   * 
   * 1) RepaintManager.refresh() checks flags and then calls Viewer.repaint() 2)
   * Viewer.repaint() invokes display.repaint(), provided display is not null
   * (headless) 3) The system responds with an invocation of
   * Jmol.update(Graphics g), which we are routing through Jmol.paint(Graphics
   * g). 4) Jmol.update invokes Viewer.setScreenDimension(size), which makes the
   * necessary changes in parameters for any new window size. 5) Jmol.update
   * invokes Viewer.renderScreenImage(g, size, rectClip) 6)
   * Viewer.renderScreenImage checks object visibility, invokes render1 to do
   * the actual creation of the image pixel map and send it to the screen, and
   * then invokes repaintView() 7) Viewer.repaintView() invokes
   * RepaintManager.repaintDone(), to clear the flags and then use notify() to
   * release any threads holding on wait().
   * 
   * @param mode
   * @param strWhy
   * 
   */
  @Override
  public void refresh(int mode, String strWhy) {
    // System.out.println("viewer refresh " + mode +
    // "-----------------------------------------------------------"
    // + Thread.currentThread().getName() + " " + strWhy);
    // System.out.flush();
    // refresh(2) indicates this is a mouse motion -- not going through Eval
    // so we bypass Eval and mainline on the other viewer!
    // refresh(-1) is used in stateManager to force no repaint
    // refresh(3) is used by operations to ONLY do a repaint -- no syncing
    // refresh(6) is used to do no refresh if in motion
    if (mode == 6 && getInMotion())
      return;
    if (repaintManager == null || !refreshing)
      return;
    if (mode > 0)
      repaintManager.refresh();
    if (mode % 3 != 0 && statusManager.doSync())
      statusManager.setSync(mode == 2 ? strWhy : null);
  }

  public void requestRepaintAndWait() {
    // called by moveUpdate from move, moveTo, navigate, navigateSurface,
    // navTranslate
    // called by ScriptEvaluator "refresh" command
    // called by AnimationThread run()
    // called by TransformationManager move and moveTo
    // called by TransformationManager11 navigate, navigateSurface, navigateTo
    if (!haveDisplay)
      return;
    repaintManager.requestRepaintAndWait();
    if (statusManager.doSync())
      statusManager.setSync(null);
  }

  void setSync() {
    if (statusManager.doSync())
      statusManager.setSync(null);
  }

  @Override
  public void notifyViewerRepaintDone() {
    repaintManager.repaintDone();
    animationManager.repaintDone();
  }

  private boolean axesAreTainted = false;

  public boolean areAxesTainted() {
    boolean TF = axesAreTainted;
    axesAreTainted = false;
    return TF;
  }

  // //////////// screen/image methods ///////////////

  final Dimension dimScreen = new Dimension();

  // final Rectangle rectClip = new Rectangle();

  private int maximumSize = Integer.MAX_VALUE;

  private void setMaximumSize(int x) {
    maximumSize = Math.max(x, 100);
  }

  @Override
  public void setScreenDimension(Dimension dim) {
    // There is a bug in Netscape 4.7*+MacOS 9 when comparing dimension objects
    // so don't try dim1.equals(dim2)
    dim.height = Math.min(dim.height, maximumSize);
    dim.width = Math.min(dim.width, maximumSize);
    int height = dim.height;
    int width = dim.width;
    if (isStereoDouble())
      width = (width + 1) / 2;
    if (dimScreen.width == width && dimScreen.height == height)
      return;
    resizeImage(width, height, false, false, true);
  }

  private float imageFontScaling = 1;

  public float getImageFontScaling() {
    return imageFontScaling;
  }

  private void resizeImage(int width, int height, boolean isImageWrite,
                           boolean isExport, boolean isReset) {
    if (!isImageWrite && creatingImage)
      return;
    antialiasDisplay = (isReset ? global.antialiasDisplay : isImageWrite
        && !isExport ? global.antialiasImages : false);
    imageFontScaling = (isReset || width <= 0 ? 1
        : (global.zoomLarge == (height > width) ? height : width)
            / getScreenDim()) * (antialiasDisplay ? 2 : 1);
    if (width > 0) {
      dimScreen.width = width;
      dimScreen.height = height;
      if (!isImageWrite) {
        global.setParameterValue("_width", width);
        global.setParameterValue("_height", height);
        setStatusResized(width, height);
      }
    } else {
      width = dimScreen.width;
      height = dimScreen.height;
    }
    transformManager.setScreenParameters(width, height,
        isImageWrite || isReset ? global.zoomLarge : false, antialiasDisplay,
        false, false);
    g3d.setWindowParameters(width, height, antialiasDisplay);
  }

  @Override
  public int getScreenWidth() {
    return dimScreen.width;
  }

  @Override
  public int getScreenHeight() {
    return dimScreen.height;
  }

  public int getScreenDim() {
    return (global.zoomLarge == (dimScreen.height > dimScreen.width) ? dimScreen.height
        : dimScreen.width);
  }

  @Override
  public String generateOutput(String type, String[] fileName, int width,
                               int height) {
    if (isDataOnly)
      return "";
    String fName = null;
    if (fileName != null) {
      fileName[0] = getFileNameFromDialog(fileName[0], Integer.MIN_VALUE);
      if (fileName[0] == null)
        return null;
      fName = fileName[0];
    }
    mustRender = true;
    int saveWidth = dimScreen.width;
    int saveHeight = dimScreen.height;
    resizeImage(width, height, true, true, false);
    setModelVisibility();
    finalizeTransformParameters();
    String data = repaintManager.generateOutput(type, g3d, modelSet, fName);
    // mth 2003-01-09 Linux Sun JVM 1.4.2_02
    // Sun is throwing a NullPointerExceptions inside graphics routines
    // while the window is resized.
    resizeImage(saveWidth, saveHeight, true, true, true);
    return data;
  }

  @Override
  public void renderScreenImage(Graphics gLeft, Graphics gRight,
                                Dimension size, Rectangle clip) {
    // from paint/update event
    // gRight is for second stereo applet
    // when this is the stereoSlave, no rendering occurs through this applet
    // directly, only from the other applet.
    // this is for relatively specialized geoWall-type installations

    // System.out.println(Thread.currentThread() + "render Screen Image " +
    // creatingImage);
    if (refreshing && !creatingImage) {
      if (isTainted || getSlabEnabled())
        setModelVisibility();
      isTainted = false;
      if (size != null)
        setScreenDimension(size);
      if (gRight == null) {
        getScreenImage(gLeft);
      } else {
        render1(gRight, getImage(true), 0, 0);
        render1(gLeft, getImage(false), 0, 0);
      }
    }
    // System.out.println(Thread.currentThread() +
    // "notifying repaintManager repaint is done");
    notifyViewerRepaintDone();
  }

  @Override
  public void renderScreenImage(Graphics g, Dimension size, Rectangle clip) {
    /*
     * Jmol repaint/update system:
     * 
     * threads invoke viewer.refresh() --> repaintManager.refresh() -->
     * viewer.repaint() --> display.repaint() --> OS event queue | Jmol.paint()
     * <-- viewer.renderScreenImage() <-- viewer.notifyViewerRepaintDone() <--
     * repaintManager.repaintDone()<-- which sets repaintPending false and does
     * notify();
     */
    renderScreenImage(g, null, size, clip);
  }

  private Image getImage(boolean isDouble) {
    Image image = null;
    try {
      g3d.beginRendering(transformManager.getStereoRotationMatrix(isDouble));
      render();
      g3d.endRendering();
      image = g3d.getScreenImage();
    } catch (Error er) {
      handleError(er, false);
      setErrorMessage("Error during rendering: " + er);
    }
    return image;
  }

  private boolean antialiasDisplay;

  private void render() {
    if (!refreshing && !creatingImage)
      return;
    boolean antialias2 = antialiasDisplay && global.antialiasTranslucent;
    repaintManager.render(g3d, modelSet);
    if (g3d.setPass2(antialias2)) {
      transformManager.setAntialias(antialias2);
      repaintManager.render(g3d, modelSet);
      transformManager.setAntialias(antialiasDisplay);
    }
  }

  private Image getStereoImage(int stereoMode) {
    g3d.beginRendering(transformManager.getStereoRotationMatrix(true));
    render();
    g3d.endRendering();
    g3d.snapshotAnaglyphChannelBytes();
    g3d.beginRendering(transformManager.getStereoRotationMatrix(false));
    render();
    g3d.endRendering();
    switch (stereoMode) {
    case JmolConstants.STEREO_REDCYAN:
      g3d.applyCyanAnaglyph();
      break;
    case JmolConstants.STEREO_CUSTOM:
      g3d.applyCustomAnaglyph(transformManager.stereoColors);
      break;
    case JmolConstants.STEREO_REDBLUE:
      g3d.applyBlueAnaglyph();
      break;
    default:
      g3d.applyGreenAnaglyph();
    }
    return g3d.getScreenImage();
  }

  private void render1(Graphics g, Image img, int x, int y) {
    if (g != null && img != null) {
      try {
        g.drawImage(img, x, y, null);
      } catch (NullPointerException npe) {
        Logger.error("Sun!! ... fix graphics your bugs!");
      }
    }
    g3d.releaseScreenImage();
  }

  @Override
  public Image getScreenImage(Graphics g) {
    boolean mergeImages = (g == null && isStereoDouble());
    Image image = (transformManager.stereoMode <= JmolConstants.STEREO_DOUBLE ? getImage(isStereoDouble())
        : getStereoImage(transformManager.stereoMode));
    Image image1 = null;
    if (mergeImages) {
      image1 = new BufferedImage(dimScreen.width << 1, dimScreen.height,
          ((BufferedImage) image).getType());
      g = image1.getGraphics();
    }
    if (g != null) {
      if (isStereoDouble()) {
        render1(g, image, dimScreen.width, 0);
        image = getImage(false);
      }
      render1(g, image, 0, 0);
    }
    return (mergeImages ? image1 : image);
  }

  @Override
  public Object getImageAs(String type, int quality, int width, int height,
                           String fileName, OutputStream os) {
    return getImageAs(type, quality, width, height, fileName, os, "");
  }

  /**
   * @param type
   *          "PNG", "JPG", "JPEG", "JPG64", "PPM", "GIF"
   * @param quality
   * @param width
   * @param height
   * @param fileName
   * @param os
   * @param comment
   * @return base64-encoded or binary version of the image
   */
  Object getImageAs(String type, int quality, int width, int height,
                    String fileName, OutputStream os, String comment) {
    int saveWidth = dimScreen.width;
    int saveHeight = dimScreen.height;
    mustRender = true;
    resizeImage(width, height, true, false, false);
    setModelVisibility();
    creatingImage = true;
    JmolImageCreatorInterface c = null;
    Object bytes = null;
    type = type.toLowerCase();
    if (!Parser.isOneOf(type, "jpg;jpeg;jpg64;jpeg64"))
      try {
        c = (JmolImageCreatorInterface) Interface
            .getOptionInterface("export.image.ImageCreator");
      } catch (Error er) {
        // unsigned applet will not have this interface
        // and thus will not use os or filename
      }
    if (c == null) {
      BufferedImage eImage = (BufferedImage) getScreenImage(null);
      if (eImage != null) {
        try {
          if (quality < 0)
            quality = 75;
          bytes = JpegEncoder.getBytes(eImage, quality, comment);
          releaseScreenImage();
          if (type.equals("jpg64") || type.equals("jpeg64"))
            bytes = (bytes == null ? "" : Base64.getBase64((byte[]) bytes)
                .toString());
        } catch (Error er) {
          releaseScreenImage();
          handleError(er, false);
          setErrorMessage("Error creating image: " + er);
          bytes = getErrorMessage();
        }
      }
    } else {
      c.setViewer(this, privateKey);
      try {
        bytes = c.getImageBytes(type, quality, fileName, null, os);
      } catch (IOException e) {
        bytes = e;
        setErrorMessage("Error creating image: " + e);
      } catch (Error er) {
        handleError(er, false);
        setErrorMessage("Error creating image: " + er);
        bytes = getErrorMessage();
      }
    }
    creatingImage = false;
    resizeImage(saveWidth, saveHeight, true, false, true);
    return bytes;
  }

  @Override
  public void releaseScreenImage() {
    g3d.releaseScreenImage();
  }

  // ///////////////////////////////////////////////////////////////
  // routines for script support
  // ///////////////////////////////////////////////////////////////

  public boolean getAllowEmbeddedScripts() {
    return global.allowEmbeddedScripts && !isPreviewOnly;
  }

  @Override
  public String evalFile(String strFilename) {
    // app -s flag
    int ptWait = strFilename.indexOf(" -noqueue"); // for TestScripts.java
    if (ptWait >= 0) {
      return (String) evalStringWaitStatus("String", strFilename.substring(0,
          ptWait), "", true, false, false);
    }
    return scriptManager.addScript(strFilename, true, false);
  }

  String interruptScript = "";

  public String getInterruptScript() {
    String s = interruptScript;
    interruptScript = "";
    if (Logger.debugging && s != "")
      Logger.debug("interrupt: " + s);
    return s;
  }

  @Override
  public String script(String strScript) {
    // JmolViewer -- just an alias for evalString
    return evalString(strScript);
  }

  @Override
  public String evalString(String strScript) {
    // JmolSimpleViewer
    return evalStringQuiet(strScript, false, true);
  }

  @Override
  public String evalStringQuiet(String strScript) {
    // JmolViewer
    return evalStringQuiet(strScript, true, true);
  }

  String evalStringQuiet(String strScript, boolean isQuiet,
                         boolean allowSyncScript) {
    // central point for all incoming script processing
    // all menu items, all mouse movement -- everything goes through this method
    // by setting syncScriptTarget = ">" the user can direct that all scripts
    // initiated WITHIN this applet (not sent to it)
    // we append #NOSYNC; here so that the receiving applet does not attempt
    // to pass it back to us or any other applet.
    // System.out.println("OK, I'm in evalStringQUiet");
    if (allowSyncScript && statusManager.syncingScripts
        && strScript.indexOf("#NOSYNC;") < 0)
      syncScript(strScript + " #NOSYNC;", null);
    if (eval.isExecutionPaused() && strScript.charAt(0) != '!')
      strScript = '!' + TextFormat.trim(strScript, "\n\r\t ");
    boolean isInterrupt = (strScript.length() > 0 && strScript.charAt(0) == '!');
    if (isInterrupt)
      strScript = strScript.substring(1);
    String msg = checkScriptExecution(strScript, isInterrupt);
    if (msg != null)
      return msg;
    if (isScriptExecuting() && (isInterrupt || eval.isExecutionPaused())) {
      interruptScript = strScript;
      if (strScript.indexOf("moveto ") == 0)
        scriptManager.flushQueue("moveto ");
      return "!" + strScript;
    }
    interruptScript = "";
    if (isQuiet)
      strScript += JmolConstants.SCRIPT_EDITOR_IGNORE;
    return scriptManager.addScript(strScript, false, isQuiet
        && !getMessageStyleChime());
  }

  private String checkScriptExecution(String strScript, boolean isInterrupt) {
    String str = strScript;
    if (str.indexOf("\1##") >= 0)
      str = str.substring(0, str.indexOf("\1##"));
    if (checkResume(str))
      return "script processing resumed";
    if (checkStepping(str))
      return "script processing stepped";
    if (checkHalt(str, isInterrupt))
      return "script execution halted";
    if (checkUndo(str))
      return "OK - " + str;  
    return null;
  }

  private boolean checkUndo(String str) {
    if (str.equalsIgnoreCase("redo"))
      undoAction(false, 0, -1);
    else if (str.equalsIgnoreCase("undo"))
      undoAction(false, 0, 1);
    else
      return false;
    return true;
  }

  public boolean usingScriptQueue() {
    return global.useScriptQueue;
  }

  public void clearScriptQueue() {
    // Eval
    // checkHalt **
    scriptManager.clearQueue();
  }

  private void setScriptQueue(boolean TF) {
    global.useScriptQueue = TF;
    if (!TF)
      clearScriptQueue();
  }

  public boolean checkResume(String str) {
    if (str.equalsIgnoreCase("resume")) {
      scriptStatus("", "execution resumed", 0, null);
      resumeScriptExecution();
      return true;
    }
    return false;
  }

  public boolean checkStepping(String str) {
    if (str.equalsIgnoreCase("step")) {
      stepScriptExecution();
      return true;
    }
    if (str.equalsIgnoreCase("?")) {
      scriptStatus(eval.getNextStatement());
      return true;
    }
    return false;
  }

  @Override
  public boolean checkHalt(String str, boolean isInterrupt) {
    if (str.equalsIgnoreCase("pause")) {
      pauseScriptExecution();
      if (scriptEditorVisible)
        scriptStatus("", "paused -- type RESUME to continue", 0, null);
      return true;
    }
    str = str.toLowerCase();
    boolean exitScript = false;
    String haltType = null;
    if (str.startsWith("exit")) {
      haltScriptExecution();
      clearScriptQueue();
      clearTimeout(null);
      exitScript = str.equals(haltType = "exit");
    } else if (str.startsWith("quit")) {
      haltScriptExecution();
      exitScript = str.equals(haltType = "quit");
    }
    if (haltType == null)
      return false;
    // !quit or !halt
    if (isInterrupt) {
      transformManager.setSpinOn(false);
      stopMinimization();
    }
    if (isInterrupt || waitForMoveTo()) {
      stopMotion();
    }
    Logger.info(isCmdLine_c_or_C_Option ? haltType
        + " -- stops script checking" : (isInterrupt ? "!" : "") + haltType
        + " received");
    isCmdLine_c_or_C_Option = false;
    return exitScript;
  }

  // / direct no-queue use:

  @Override
  public String scriptWait(String strScript) {
    scriptManager.waitForQueue();
    boolean doTranslateTemp = GT.getDoTranslate();
    GT.setDoTranslate(false);
    String str = (String) evalStringWaitStatus("JSON", strScript,
        "+scriptStarted,+scriptStatus,+scriptEcho,+scriptTerminated", false,
        false, false);
    GT.setDoTranslate(doTranslateTemp);
    return str;
  }

  @Override
  public Object scriptWaitStatus(String strScript, String statusList) {
    // null statusList will return a String 
    //  -- output from PRINT/MESSAGE/ECHO commands or an error message
    // otherwise, specific status messages will be created as a Java object
    scriptManager.waitForQueue();
    boolean doTranslateTemp = GT.getDoTranslate();
    GT.setDoTranslate(false);
    Object ret = evalStringWaitStatus("object", strScript, statusList, false,
        false, false);
    GT.setDoTranslate(doTranslateTemp);
    return ret;
  }

  public Object evalStringWaitStatus(String returnType, String strScript,
                                     String statusList) {
    scriptManager.waitForQueue();
    return evalStringWaitStatus(returnType, strScript, statusList, false,
        false, false);
  }

  int scriptIndex;
  boolean isScriptQueued = true;

  synchronized Object evalStringWaitStatus(String returnType, String strScript,
                                           String statusList,
                                           boolean isScriptFile,
                                           boolean isQuiet, boolean isQueued) {
    // from the scriptManager or scriptWait()
    if (strScript == null)
      return null;
    String str = checkScriptExecution(strScript, false);
    if (str != null)
      return str;
    StringBuffer outputBuffer = (statusList == null
        || statusList.equals("output") ? new StringBuffer() : null);

    // typically request:
    // "+scriptStarted,+scriptStatus,+scriptEcho,+scriptTerminated"
    // set up first with applet.jmolGetProperty("jmolStatus",statusList)
    // flush list
    String oldStatusList = statusManager.getStatusList();
    getProperty("String", "jmolStatus", statusList);
    if (isCmdLine_c_or_C_Option)
      Logger.info("--checking script:\n" + eval.getScript() + "\n----\n");
    boolean historyDisabled = (strScript.indexOf(")") == 0);
    if (historyDisabled)
      strScript = strScript.substring(1);
    historyDisabled = historyDisabled || !isQueued; // no history for scriptWait
    // 11.5.45
    setErrorMessage(null);
    boolean isOK = (isScriptFile ? eval.compileScriptFile(strScript, isQuiet)
        : eval.compileScriptString(strScript, isQuiet));
    String strErrorMessage = eval.getErrorMessage();
    String strErrorMessageUntranslated = eval.getErrorMessageUntranslated();
    setErrorMessage(strErrorMessage, strErrorMessageUntranslated);
    if (isOK) {
      isScriptQueued = isQueued;
      if (!isQuiet)
        scriptStatus(null, strScript, -2 - (++scriptIndex), null);
      eval.evaluateCompiledScript(isCmdLine_c_or_C_Option, isCmdLine_C_Option,
          historyDisabled, listCommands, outputBuffer);
      setErrorMessage(strErrorMessage = eval.getErrorMessage(),
          strErrorMessageUntranslated = eval.getErrorMessageUntranslated());
      if (!isQuiet)
        scriptStatus("Jmol script terminated", strErrorMessage, 1 + eval
            .getExecutionWalltime(), strErrorMessageUntranslated);
    } else {
      scriptStatus(strErrorMessage);
      scriptStatus("Jmol script terminated", strErrorMessage, 1,
          strErrorMessageUntranslated);
    }
    setStateScriptVersion(null); // set by compiler
    if (strErrorMessage != null && autoExit)
      exitJmol();
    if (isCmdLine_c_or_C_Option) {
      if (strErrorMessage == null)
        Logger.info("--script check ok");
      else
        Logger.error("--script check error\n" + strErrorMessageUntranslated);
    }
    if (isCmdLine_c_or_C_Option)
      Logger.info("(use 'exit' to stop checking)");
    isScriptQueued = true;
    if (returnType.equalsIgnoreCase("String"))
      return strErrorMessageUntranslated;
    if (outputBuffer != null)
      return (strErrorMessageUntranslated == null ? outputBuffer.toString()
          : strErrorMessageUntranslated);
    // get Vector of Vectors of Vectors info
    Object info = getProperty(returnType, "jmolStatus", statusList);
    // reset to previous status list
    getProperty("object", "jmolStatus", oldStatusList);
    return info;
  }

  public void exitJmol() {
    Logger.debug("exitJmol -- exiting");
    System.out.flush();
    System.exit(0);
  }

  private Object scriptCheck(String strScript, boolean returnContext) {
    // from ConsoleTextPane.checkCommand() and applet Jmol.scriptProcessor()
    if (strScript.indexOf(")") == 0 || strScript.indexOf("!") == 0) // history
      // disabled
      strScript = strScript.substring(1);
    ScriptContext sc = (new ScriptEvaluator(this)).checkScriptSilent(strScript);
    if (returnContext || sc.errorMessage == null)
      return sc;
    return sc.errorMessage;
  }

  @Override
  public synchronized Object scriptCheck(String strScript) {
    return scriptCheck(strScript, false);
  }

  @Override
  public boolean isScriptExecuting() {
    return eval.isScriptExecuting();
  }

  @Override
  public void haltScriptExecution() {
    eval.haltExecution();
  }

  public void resumeScriptExecution() {
    eval.resumePausedExecution();
  }

  public void stepScriptExecution() {
    eval.stepPausedExecution();
  }

  public void pauseScriptExecution() {
    eval.pauseExecution(true);
  }

  public String getDefaultLoadFilter() {
    return global.defaultLoadFilter;
  }

  public String getDefaultLoadScript() {
    return global.defaultLoadScript;
  }

  public Object setLoadFormat(String name, char type, boolean withPrefix) {
    String f = name.substring(1);
    switch (type) {
    case '=':
      String s = global.loadFormat;
      if (f.indexOf(".") > 0 && s.indexOf("%FILE.") >= 0)
        s = s.substring(0, s.indexOf("%FILE") + 5);
      return TextFormat.formatString(s, "FILE", f);
    case '$':
      f = TextFormat.simpleReplace(f, "%", "%25");
      f = TextFormat.simpleReplace(f, " ", "%20");
      return (withPrefix ? "MOL::" : "")
          + TextFormat.formatString(global.smilesUrlFormat, "FILE", f);
    case '_': // isosurface "=...", but we code that type as '-'
      String server = FileManager.fixFileNameVariables(global.edsUrlFormat, f);
      String strCutoff = FileManager.fixFileNameVariables(global.edsUrlCutoff,
          f);
      return new String[] { server, strCutoff };
    }
    return name.substring(1);
  }

  public String[] getElectronDensityLoadInfo() {
    return new String[] { global.edsUrlFormat, global.edsUrlCutoff,
        global.edsUrlOptions };
  }

  public String getStandardLabelFormat() {
    return stateManager.getStandardLabelFormat();
  }

  public int getRibbonAspectRatio() {
    // mps
    return global.ribbonAspectRatio;
  }

  public float getSheetSmoothing() {
    // mps
    return global.sheetSmoothing;
  }

  public boolean getSsbondsBackbone() {
    return global.ssbondsBackbone;
  }

  public boolean getHbondsBackbone() {
    return global.hbondsBackbone;
  }

  public boolean getHbondsSolid() {
    return global.hbondsSolid;
  }

  public Point3f[] getAdditionalHydrogens(BitSet bsAtoms, boolean doAll,
                                          boolean justCarbon,
                                          List<Atom> vConnections) {
    if (bsAtoms == null)
      bsAtoms = getSelectionSet(false);
    int[] nTotal = new int[1];
    Point3f[][] pts = modelSet.calculateHydrogens(bsAtoms, nTotal, doAll,
        justCarbon, vConnections);
    Point3f[] points = new Point3f[nTotal[0]];
    for (int i = 0, pt = 0; i < pts.length; i++)
      if (pts[i] != null)
        for (int j = 0; j < pts[i].length; j++)
          points[pt++] = pts[i][j];
    return points;
  }

  public BitSet addHydrogens(BitSet bsAtoms, boolean asScript, boolean isSilent) {
    boolean doAll = (bsAtoms == null);
    if (bsAtoms == null)
      bsAtoms = getModelUndeletedAtomsBitSet(getVisibleFramesBitSet().length() - 1);
    BitSet bsB = new BitSet();
    if (bsAtoms.cardinality() == 0)
      return bsB;
    int modelIndex = modelSet.atoms[bsAtoms.nextSetBit(0)].modelIndex;
    if (modelIndex != modelSet.getModelCount() - 1)
      return bsB;
    List<Atom> vConnections = new ArrayList<Atom>();
    Point3f[] pts = getAdditionalHydrogens(bsAtoms, doAll, false, vConnections);
    boolean wasAppendNew = false;
    wasAppendNew = getAppendNew();
    if (pts.length > 0) {
      clearModelDependentObjects();
      try {
        bsB = (asScript ? modelSet.addHydrogens(vConnections, pts)
            : addHydrogensInline(bsAtoms, vConnections, pts));
      } catch (Exception e) {
        e.printStackTrace();
        // ignore
      }
      if (wasAppendNew)
        setAppendNew(true);
    }
    if (!isSilent)
      scriptStatus(GT._("{0} hydrogens added", pts.length));
    return bsB;
  }

  public BitSet addHydrogensInline(BitSet bsAtoms, List<Atom> vConnections,
                                   Point3f[] pts) throws Exception {
    int modelIndex = getAtomModelIndex(bsAtoms.nextSetBit(0));
    if (modelIndex != modelSet.getModelCount() - 1)
      return new BitSet();

    // must be added to the LAST data set only

    BitSet bsA = getModelUndeletedAtomsBitSet(modelIndex);
    setAppendNew(false);
    // BitSet bsB = getAtomBits(Token.hydrogen, null);
    // bsA.andNot(bsB);
    int atomIndex = modelSet.getAtomCount();
    int atomno = modelSet.getAtomCountInModel(modelIndex);
    StringBuffer sbConnect = new StringBuffer();
    for (int i = 0; i < vConnections.size(); i++) {
      Atom a = vConnections.get(i);
      sbConnect.append(";  connect 0 100 ").append("({" + (atomIndex++) + "}) ")
          .append("({" + a.index + "});");
    }
    StringBuffer sb = new StringBuffer();
    sb.append(pts.length).append("\nViewer.AddHydrogens#noautobond");
    sb.append("\n");
    for (int i = 0; i < pts.length; i++)
      sb.append("H ").append(pts[i].x).append(" ").append(pts[i].y).append(" ")
          .append(pts[i].z).append(" - - - - ").append(++atomno).append('\n');
    loadInline(sb.toString(), '\n', true, null);
    eval.runScript(sbConnect.toString(), null);
    BitSet bsB = getModelUndeletedAtomsBitSet(modelIndex);
    bsB.andNot(bsA);
    return bsB;
  }

  @Override
  public void setMarBond(short marBond) {
    global.bondRadiusMilliAngstroms = marBond;
    global.setParameterValue("bondRadiusMilliAngstroms", marBond);
    setShapeSize(JmolConstants.SHAPE_STICKS, marBond * 2, BitSetUtil
        .setAll(getAtomCount()));
  }

  int hoverAtomIndex = -1;
  String hoverText;
  boolean hoverEnabled = true;

  public boolean isHoverEnabled() {
    return hoverEnabled;
  }

  public void setHoverLabel(String strLabel) {
    loadShape(JmolConstants.SHAPE_HOVER);
    setShapeProperty(JmolConstants.SHAPE_HOVER, "label", strLabel);
    hoverEnabled = (strLabel != null);
  }

  void hoverOn(int atomIndex, int action) {
    setStatusAtomHovered(atomIndex, getAtomInfoXYZ(atomIndex, false));
    if (!hoverEnabled)
      return;
    if (isModelKitMode()) {
      if (isAtomAssignable(atomIndex))
          highlight(BitSetUtil.setBit(atomIndex));
      refresh(3, "hover on atom");
      return;
    }
    if (eval != null && isScriptExecuting() || atomIndex == hoverAtomIndex
        || global.hoverDelayMs == 0)
      return;
    if (!isInSelectionSubset(atomIndex))
      return;
    loadShape(JmolConstants.SHAPE_HOVER);
    if (isBound(action, ActionManager.ACTION_dragLabel)
        && getPickingMode() == ActionManager.PICKING_LABEL
        && modelSet.atoms[atomIndex].isShapeVisible(JmolConstants
            .getShapeVisibilityFlag(JmolConstants.SHAPE_LABELS))) {
      setShapeProperty(JmolConstants.SHAPE_HOVER, "specialLabel", GT
          ._("Drag to move label"));
    }
    setShapeProperty(JmolConstants.SHAPE_HOVER, "text", null);
    setShapeProperty(JmolConstants.SHAPE_HOVER, "target", Integer.valueOf(atomIndex));
    hoverText = null;
    hoverAtomIndex = atomIndex;
    refresh(3, "hover on atom");
  }

  int getHoverDelay() {
    return global.modelKitMode ? 20 : global.hoverDelayMs;
  }

  public void hoverOn(int x, int y, String text) {
    if (!isHoverEnabled())
      return;
    // from draw for drawhover on
    if (eval != null && isScriptExecuting())
      return;
    loadShape(JmolConstants.SHAPE_HOVER);
    setShapeProperty(JmolConstants.SHAPE_HOVER, "xy", new Point3i(x, y, 0));
    setShapeProperty(JmolConstants.SHAPE_HOVER, "target", null);
    setShapeProperty(JmolConstants.SHAPE_HOVER, "specialLabel", null);
    setShapeProperty(JmolConstants.SHAPE_HOVER, "text", text);
    hoverAtomIndex = -1;
    hoverText = text;
    refresh(3, "hover on point");
  }

  void hoverOff() {
    if (isModelKitMode())
      highlight(null);
    if (!isHoverEnabled())
      return;
    boolean isHover = (hoverText != null || hoverAtomIndex >= 0);
    if (hoverAtomIndex >= 0) {
      setShapeProperty(JmolConstants.SHAPE_HOVER, "target", null);
      hoverAtomIndex = -1;
    }
    if (hoverText != null) {
      setShapeProperty(JmolConstants.SHAPE_HOVER, "text", null);
      hoverText = null;
    }
    setShapeProperty(JmolConstants.SHAPE_HOVER, "specialLabel", null);
    if (isHover)
      refresh(3, "hover off");
  }

  public void clearShapeRenderers() {
    repaintManager.clear();
  }

  public int getBfactor100Hi() {
    return modelSet.getBfactor100Hi();
  }

  short getColix(Object object) {
    return Graphics3D.getColix(object);
  }

  public boolean getRasmolSetting(int tok) {
    switch (tok) {
    case Token.hydrogen:
      return global.rasmolHydrogenSetting;
    case Token.hetero:
      return global.rasmolHeteroSetting;
    }
    return false;
  }

  public boolean getDebugScript() {
    return global.debugScript;
  }

  @Override
  public void setDebugScript(boolean debugScript) {
    global.debugScript = debugScript;
    global.setParameterValue("debugScript", debugScript);
    eval.setDebugging();
  }

  void clearClickCount() {
    setTainted(true);
  }

  public final static int CURSOR_DEFAULT = 0;
  public final static int CURSOR_HAND = 1;
  public final static int CURSOR_CROSSHAIR = 2;
  public final static int CURSOR_MOVE = 3;
  public final static int CURSOR_WAIT = 4;
  public final static int CURSOR_ZOOM = 5;

  private int currentCursor = CURSOR_DEFAULT;

  public int getCursor() {
    return currentCursor;
  }

  public void setCursor(int cursor) {
    if (currentCursor == cursor || multiTouch || !haveDisplay)
      return;
    int c;
    switch (currentCursor = cursor) {
    case CURSOR_HAND:
      c = Cursor.HAND_CURSOR;
      break;
    case CURSOR_MOVE:
      c = Cursor.MOVE_CURSOR;
      break;
    case CURSOR_ZOOM:
      c = Cursor.N_RESIZE_CURSOR;
      break;
    case CURSOR_CROSSHAIR:
      c = Cursor.CROSSHAIR_CURSOR;
      break;
    case CURSOR_WAIT:
      c = Cursor.WAIT_CURSOR;
      break;
    default:
      display.setCursor(Cursor.getDefaultCursor());
      return;
    }
    display.setCursor(Cursor.getPredefinedCursor(c));
  }

  void setPickingMode(String strMode, int pickingMode) {
    if (!haveDisplay)
      return;
    String option = null;
    if (strMode != null) {
      int pt = strMode.indexOf("_");
      if (pt >= 0) {
        option = strMode.substring(pt + 1);
        strMode = strMode.substring(0, pt);
      }
      pickingMode = ActionManager.getPickingMode(strMode);
    }
    if (pickingMode < 0)
      pickingMode = ActionManager.PICKING_IDENTIFY;
    actionManager.setPickingMode(pickingMode);
    global.setParameterValue("picking", ActionManager
        .getPickingModeName(actionManager.getAtomPickingMode()));
    if (option == null || option.length() == 0)
      return;
    option = Character.toUpperCase(option.charAt(0))
        + (option.length() == 1 ? "" : option.substring(1, 2));
    switch (pickingMode) {
    case ActionManager.PICKING_ASSIGN_ATOM:
      setAtomPickingOption(option);
      break;
    case ActionManager.PICKING_ASSIGN_BOND:
      setBondPickingOption(option);
      break;
    default:
      Logger.error("Bad picking mode: " + strMode + "_" + option);
    }
  }

  public int getPickingMode() {
    return (haveDisplay ? actionManager.getAtomPickingMode() : 0);
  }

  public boolean getDrawPicking() {
    return global.drawPicking;
  }

  public boolean isModelKitMode() {
    return global.modelKitMode;
  }

  public boolean getBondPicking() {
    return global.bondPicking || global.modelKitMode;
  }

  private boolean getAtomPicking() {
    return global.atomPicking;
  }

  void setPickingStyle(String style, int pickingStyle) {
    if (!haveDisplay)
      return;
    if (style != null)
      pickingStyle = ActionManager.getPickingStyle(style);
    if (pickingStyle < 0)
      pickingStyle = ActionManager.PICKINGSTYLE_SELECT_JMOL;
    actionManager.setPickingStyle(pickingStyle);
    global.setParameterValue("pickingStyle", ActionManager
        .getPickingStyleName(actionManager.getPickingStyle()));
  }

  public boolean getDrawHover() {
    return haveDisplay && global.drawHover;
  }

  @Override
  public String getAtomInfo(int atomOrPointIndex) {
    // only for MeasurementTable and actionManager
    return (atomOrPointIndex >= 0 ? modelSet
        .getAtomInfo(atomOrPointIndex, null) : (String) shapeManager
        .getShapeProperty(JmolConstants.SHAPE_MEASURES, "pointInfo",
            -atomOrPointIndex));
  }

  public String getAtomInfoXYZ(int atomIndex, boolean useChimeFormat) {
    return modelSet.getAtomInfoXYZ(atomIndex, useChimeFormat);
  }

  // //////////////status manager dispatch//////////////

  @Override
  public void setJmolCallbackListener(JmolCallbackListener jmolCallbackListener) {
    statusManager.setJmolCallbackListener(jmolCallbackListener);
  }

  @Override
  public void setJmolStatusListener(JmolStatusListener jmolStatusListener) {
    statusManager.setJmolStatusListener(jmolStatusListener, null);
  }

  public Map<String, List<List<Object>>> getMessageQueue() {
    // called by PropertyManager.getPropertyAsObject for "messageQueue"
    return statusManager.getMessageQueue();
  }

  List<List<List<Object>>> getStatusChanged(String statusNameList) {
    return statusManager.getStatusChanged(statusNameList);
  }

  public boolean menuEnabled() {
    return !global.disablePopupMenu;
  }

  void popupMenu(int x, int y, char type) {
    if (!haveDisplay || !refreshing || isPreviewOnly || global.disablePopupMenu)
      return;
    switch (type) {
    case 'j':
      getPopupMenu();
      jmolpopup.show(x, y);
      break;
    case 'a':
    case 'b':
    case 'm':
      if (modelkit == null) {
        modelkit = (JmolModelKitInterface) Interface
            .getOptionInterface("modelkit.ModelKit");
        if (modelkit == null)
          return;
        modelkit = modelkit.getModelKit(this, display);
        modelkit.getMenus(true);
      }
      modelkit.show(x, y, type);
    }
  }

  public String getMenu(String type) {
    getPopupMenu();
    return (jmolpopup == null ? "" : jmolpopup.getMenu("Jmol version "
        + Viewer.getJmolVersion() + "|_GET_MENU|" + type));
  }

  private Container getPopupMenu() {
    if (jmolpopup == null)
      jmolpopup = JmolPopup.newJmolPopup(this, true, menuStructure, true);
    return jmolpopup.getJMenu();
  }

  public void setMenu(String fileOrText, boolean isFile) {
    if (isFile)
      Logger.info("Setting menu "
          + (fileOrText.length() == 0 ? "to Jmol defaults" : "from file "
              + fileOrText));
    if (fileOrText.length() == 0)
      fileOrText = null;
    else if (isFile)
      fileOrText = getFileAsString(fileOrText);
    getProperty("DATA_API", "setMenu", fileOrText);
    statusManager.setCallbackFunction("menu", fileOrText);
  }

  // // JavaScript callback methods for the applet

  /*
   * 
   * animFrameCallback echoCallback (defaults to messageCallback) errorCallback
   * evalCallback hoverCallback loadStructCallback measureCallback (defaults to
   * messageCallback) messageCallback (no local version) minimizationCallback
   * pickCallback resizeCallback scriptCallback (defaults to messageCallback)
   * syncCallback
   */

  /*
   * aniframeCallback is called:
   * 
   * -- each time a frame is changed -- whenever the animation state is changed
   * -- whenever the visible frame range is changed
   * 
   * jmolSetCallback("animFrameCallback", "myAnimFrameCallback") function
   * myAnimFrameCallback(frameNo, fileNo, modelNo, firstNo, lastNo) {}
   * 
   * frameNo == the current frame in fileNo == the current file number, starting
   * at 1 modelNo == the current model number in the current file, starting at 1
   * firstNo == flag1 * (the first frame of the set, in file * 1000000 + model
   * notation) lastNo == flag2 * (the last frame of the set, in file * 1000000 +
   * model notation)
   * 
   * where flag1 = 1 if animationDirection > 1 or -1 otherwise where flag2 = 1
   * if currentDirection > 1 or -1 otherwise
   * 
   * RepaintManager.setStatusFrameChanged RepaintManager.setAnimationOff
   * RepaintManager.setCurrentModelIndex RepaintManager.clearAnimation
   * RepaintManager.rewindAnimation RepaintManager.setAnimationLast
   * RepaintManager.setAnimationRelative RepaintManager.setFrameRangeVisible
   * Viewer.setCurrentModelIndex Eval.file Eval.frame Eval.load
   * Viewer.createImage (when creating movie frames with the WRITE FRAMES
   * command) Viewer.initializeModel
   */

  int prevFrame = Integer.MIN_VALUE;

  void setStatusFrameChanged(int frameNo) {
    int modelIndex = animationManager.currentModelIndex;
    if (frameNo == Integer.MIN_VALUE) {
      // force reset (reading vibrations)
      prevFrame = Integer.MIN_VALUE;
      frameNo = modelIndex;
    }
    transformManager.setVibrationPeriod(Float.NaN);

    int firstIndex = animationManager.firstModelIndex;
    int lastIndex = animationManager.lastModelIndex;

    if (firstIndex == lastIndex)
      modelIndex = firstIndex;
    int frameID = getModelFileNumber(modelIndex);
    int fileNo = frameID;
    int modelNo = frameID % 1000000;
    int firstNo = getModelFileNumber(firstIndex);
    int lastNo = getModelFileNumber(lastIndex);
    String strModelNo;
    if (fileNo == 0) {
      strModelNo = getModelNumberDotted(firstIndex);
      if (firstIndex != lastIndex)
        strModelNo += " - " + getModelNumberDotted(lastIndex);
      if (firstNo / 1000000 == lastNo / 1000000)
        fileNo = firstNo;
    } else {
      strModelNo = getModelNumberDotted(modelIndex);
    }
    if (fileNo != 0)
      fileNo = (fileNo < 1000000 ? 1 : fileNo / 1000000);

    global.setParameterValue("_currentFileNumber", fileNo);
    global.setParameterValue("_currentModelNumberInFile", modelNo);
    global.setParameterValue("_frameID", frameID);
    global.setParameterValue("_modelNumber", strModelNo);
    global.setParameterValue("_modelName", (modelIndex < 0 ? ""
        : getModelName(modelIndex)));
    global.setParameterValue("_modelTitle", (modelIndex < 0 ? ""
        : getModelTitle(modelIndex)));
    global.setParameterValue("_modelFile", (modelIndex < 0 ? ""
        : getModelFileName(modelIndex)));

    if (modelIndex == prevFrame) {
      return;
    }
    prevFrame = modelIndex;

    statusManager.setStatusFrameChanged(frameNo, fileNo, modelNo,
        (animationManager.animationDirection < 0 ? -firstNo : firstNo),
        (animationManager.currentDirection < 0 ? -lastNo : lastNo));
  }

  /*
   * echoCallback is one of the two main status reporting mechanisms. Along with
   * scriptCallback, it outputs to the console. Unlike scriptCallback, it does
   * not output to the status bar of the application or applet. If
   * messageCallback is enabled but not echoCallback, these messages go to the
   * messageCallback function instead.
   * 
   * jmolSetCallback("echoCallback", "myEchoCallback") function
   * myEchoCallback(app, message, queueState) {}
   * 
   * queueState = 1 -- queued queueState = 0 -- not queued
   * 
   * serves:
   * 
   * Eval.instructionDispatchLoop when app has -l flag
   * ForceField.steepestDescenTakeNSteps for minimization done
   * Viewer.setPropertyError Viewer.setBooleanProperty error
   * Viewer.setFloatProperty error Viewer.setIntProperty error
   * Viewer.setStringProperty error Viewer.showString adds a Logger.warn()
   * message Eval.showString calculate, cd, dataFrame, echo, error, getProperty,
   * history, isosurface, listIsosurface, pointGroup, print, set, show, write
   * ForceField.steepestDescentInitialize for initial energy
   * ForceField.steepestDescentTakeNSteps for minimization update
   * Viewer.showParameter
   */

  public void scriptEcho(String strEcho) {
    if (!Logger.isActiveLevel(Logger.LEVEL_INFO))
      return;
    statusManager.setScriptEcho(strEcho, isScriptQueued);
    if (listCommands && strEcho != null && strEcho.indexOf("$[") == 0)
      Logger.info(strEcho);
  }

  /*
   * errorCallback is a special callback that can be used to identify errors
   * during scripting and file i/o, and also indicate out of memory conditions
   * 
   * jmolSetCallback("errorCallback", "myErrorCallback") function
   * myErrorCallback(app, errType, errMsg, objectInfo, errMsgUntranslated) {}
   * 
   * errType == "Error" or "ScriptException" errMsg == error message, possibly
   * translated, with added information objectInfo == which object (such as an
   * isosurface) was involved errMsgUntranslated == just the basic message
   * 
   * Viewer.notifyError Eval.runEval on Error and file loading Exceptions
   * Viewer.handleError Eval.runEval on OOM Error Viewer.createModelSet on OOM
   * model initialization Error Viewer.getImage on OOM rendering Error
   */
  public void notifyError(String errType, String errMsg,
                          String errMsgUntranslated) {
    global.setParameterValue("_errormessage", errMsgUntranslated);
    statusManager.notifyError(errType, errMsg, errMsgUntranslated);
  }

  /*
   * evalCallback is a special callback that evaluates expressions in JavaScript
   * rather than in Jmol.
   * 
   * Viewer.jsEval Eval.loadScriptFileInternal Eval.Rpn.evaluateScript
   * Eval.script
   */

  public String jsEval(String strEval) {
    return statusManager.jsEval(strEval);
  }

  /*
   * hoverCallback reports information about the atom being hovered over.
   * 
   * jmolSetCallback("hoverCallback", "myHoverCallback") function
   * myHoverCallback(strInfo, iAtom) {}
   * 
   * strInfo == the atom's identity, including x, y, and z coordinates iAtom ==
   * the index of the atom being hovered over
   * 
   * Viewer.setStatusAtomHovered Hover.setProperty("target") Viewer.hoverOff
   * Viewer.hoverOn
   */

  public void setStatusAtomHovered(int atomIndex, String info) {
    global.setParameterValue("_atomhovered", atomIndex);
    statusManager.setStatusAtomHovered(atomIndex, info);
  }

  /*
   * loadStructCallback indicates file load status.
   * 
   * jmolSetCallback("loadStructCallback", "myLoadStructCallback") function
   * myLoadStructCallback(fullPathName, fileName, modelName, errorMsg, ptLoad)
   * {}
   * 
   * ptLoad == JmolConstants.FILE_STATUS_NOT_LOADED == -1 ptLoad == JmolConstants.FILE_STATUS_ZAPPED == 0
   * ptLoad == JmolConstants.FILE_STATUS_CREATING_MODELSET == 2 ptLoad ==
   * JmolConstants.FILE_STATUS_MODELSET_CREATED == 3 ptLoad == JmolConstants.FILE_STATUS_MODELS_DELETED == 5
   * 
   * Only -1 (error loading), 0 (zapped), and 3 (model set created) messages are
   * passed on to the callback function. The others can be detected using
   * 
   * set loadStructCallback "jmolscript:someFunctionName"
   * 
   * At the time of calling of that method, the jmolVariable _loadPoint gives
   * the value of ptLoad. These load points are also recorded in the status
   * queue under types "fileLoaded" and "fileLoadError".
   * 
   * Viewer.setFileLoadStatus Viewer.createModelSet (2, 3)
   * Viewer.createModelSetAndReturnError (-1, 1, 4) Viewer.deleteAtoms (5)
   * Viewer.zap (0)
   */
  private void setFileLoadStatus(int ptLoad, String fullPathName,
                                 String fileName, String modelName,
                                 String strError) {
    setErrorMessage(strError);
    global.setParameterValue("_loadPoint", ptLoad);
    boolean doCallback = (ptLoad != FILE_STATUS_CREATING_MODELSET); 
    statusManager.setFileLoadStatus(fullPathName, fileName, modelName,
        strError, ptLoad, doCallback);
  }

  public String getZapName() {
    return (getModelkitMode() ? JmolConstants.MODELKIT_ZAP_TITLE : "zapped");
  }


  /*
   * measureCallback reports completed or pending measurements. Pending
   * measurements are measurements that the user has started but has not
   * completed -- this call comes when the user hesitates with the mouse over an
   * atom and the "rubber band" is showing
   * 
   * jmolSetCallback("measureCallback", "myMeasureCallback") function
   * myMeasureCallback(strMeasure, intInfo, status) {}
   * 
   * intInfo == (see below) status == "measurePicked" (intInfo == the number of
   * atoms in the measurement) "measureComplete" (intInfo == the current number
   * measurements) "measureDeleted" (intInfo == the index of the measurement
   * deleted or -1 for all) "measurePending" (intInfo == number of atoms picked
   * so far)
   * 
   * strMeasure:
   * 
   * For "set picking MEASURE ..." each time the user clicks an atom, a message
   * is sent to the pickCallback function (see below), and if the picking is set
   * to measure distance, angle, or torsion, then after the requisite number of
   * atoms is picked and the pick callback message is sent, a call is also made
   * to measureCallback with a string that indicates the measurement, such as:
   * 
   * Angle O #9 - Si #7 - O #2 : 110.51877
   * 
   * Under default conditions, when picking is not set to MEASURE, then
   * measurement reports are sent when the measure is completed, deleted, or
   * pending. These reports are in a psuedo array form that can be parsed more
   * easily, involving the atoms and measurement with units, for example:
   * 
   * [Si #3, O #8, Si #7, 60.1 <degrees mark>]
   * 
   * Viewer.setStatusMeasuring Measures.clear Measures.define
   * Measures.deleteMeasurement Measures.pending actionManager.atomPicked
   */

  public void setStatusMeasuring(String status, int intInfo, String strMeasure, float value) {
    
    // status           intInfo 

    // measureCompleted index
    // measurePicked    atom count
    // measurePending   atom count
    // measureDeleted   -1 (all) or index
    // measureSequence  -2
    statusManager.setStatusMeasuring(status, intInfo, strMeasure, value);
  }

  /*
   * minimizationCallback reports the status of a currently running
   * minimization.
   * 
   * jmolSetCallback("minimizationCallback", "myMinimizationCallback") function
   * myMinimizationCallback(app, minStatus, minSteps, minEnergy, minEnergyDiff)
   * {}
   * 
   * minStatus is one of "starting", "calculate", "running", "failed", or "done"
   * 
   * Viewer.notifyMinimizationStatus Minimizer.endMinimization
   * Minimizer.getEnergyonly Minimizer.startMinimization
   * Minimizer.stepMinimization
   */

  public void notifyMinimizationStatus() {
    Object step = getParameter("_minimizationStep");
    statusManager.notifyMinimizationStatus(
        (String) getParameter("_minimizationStatus"),
        step instanceof String ? Integer.valueOf(0) : (Integer) step,
        (Float) getParameter("_minimizationEnergy"),
        (Float) getParameter("_minimizationEnergyDiff"));
  }

  /*
   * pickCallback returns information about an atom, bond, or DRAW object that
   * has been picked by the user.
   * 
   * jmolSetCallback("pickCallback", "myPickCallback") function
   * myPickCallback(strInfo, iAtom) {}
   * 
   * iAtom == the index of the atom picked or -2 for a draw object or -3 for a
   * bond
   * 
   * strInfo depends upon the type of object picked:
   * 
   * atom: a string determinied by the PICKLABEL parameter, which if "" delivers
   * the atom identity along with its coordinates
   * 
   * bond: ["bond", bondIdentityString (quoted), x, y, z] where the coordinates
   * are of the midpoint of the bond
   * 
   * draw: ["draw", drawID(quoted), pickedModel, pickedVertex, x, y, z,
   * drawTitle(quoted)]
   * 
   * Viewer.setStatusAtomPicked Draw.checkObjectClicked (set picking DRAW)
   * Sticks.checkObjectClicked (set bondPicking TRUE; set picking IDENTIFY)
   * actionManager.atomPicked (set atomPicking TRUE; set picking IDENTIFY)
   * actionManager.queueAtom (during measurements)
   */

  public void setStatusAtomPicked(int atomIndex, String info) {
    if (info == null) {
      info = global.pickLabel;
      if (info.length() == 0)
        info = getAtomInfoXYZ(atomIndex, getMessageStyleChime());
      else
        info = modelSet.getAtomInfo(atomIndex, info);
    }
    global.setParameterValue("_atompicked", atomIndex);
    global.setParameterValue("_pickinfo", info);
    statusManager.setStatusAtomPicked(atomIndex, info);
  }

  /*
   * resizeCallback is called whenever the applet gets a resize notification
   * from the browser
   * 
   * jmolSetCallback("resizeCallback", "myResizeCallback") function
   * myResizeCallback(width, height) {}
   */

  public void setStatusResized(int width, int height) {
    statusManager.setStatusResized(width, height);
  }

  /*
   * scriptCallback is the primary way to monitor script status. In addition, it
   * serves to for passing information to the user over the status line of the
   * browser as well as to the console. Note that console messages are also sent
   * by echoCallback. If messageCallback is enabled but not scriptCallback,
   * these messages go to the messageCallback function instead.
   * 
   * jmolSetCallback("scriptCallback", "myScriptCallback") function
   * myScriptCallback(app, status, message, intStatus, errorMessageUntranslated)
   * {}
   * 
   * intStatus == -2 script start -- message is the script itself intStatus == 0
   * general messages during script execution; translated error message may be
   * present intStatus >= 1 script termination message; translated and
   * untranslated message may be present value is time for execution in
   * milliseconds
   * 
   * Eval.defineAtomSet -- compilation bug indicates problem in JmolConstants
   * array Eval.instructionDispatchLoop -- debugScript messages
   * Eval.logDebugScript -- debugScript messages Eval.pause -- script execution
   * paused message Eval.runEval -- "Script completed" message Eval.script --
   * Chime "script <exiting>" message Eval.scriptStatusOrBuffer -- various
   * messages for Eval.checkContinue (error message) Eval.connect Eval.delete
   * Eval.hbond Eval.load (logMessages message) Eval.message Eval.runEval (error
   * message) Eval.write (error reading file) Eval.zap (error message)
   * FileManager.createAtomSetCollectionFromFile "requesting..." for Chime-like
   * compatibility actionManager.atomPicked
   * "pick one more atom in order to spin..." for example
   * Viewer.evalStringWaitStatus -- see above -2, 0 only if error, >=1 at
   * termination Viewer.reportSelection "xxx atoms selected"
   */

  public void scriptStatus(String strStatus) {
    scriptStatus(strStatus, "", 0, null);
  }

  public void scriptStatus(String strStatus, String statusMessage) {
    scriptStatus(strStatus, statusMessage, 0, null);
  }

  public void scriptStatus(String strStatus, String statusMessage,
                           int msWalltime, String strErrorMessageUntranslated) {
    statusManager.setScriptStatus(strStatus, statusMessage, msWalltime,
        strErrorMessageUntranslated);
  }

  /*
   * syncCallback traps script synchronization messages and allows for
   * cancellation (by returning "") or modification
   * 
   * jmolSetCallback("syncCallback", "mySyncCallback") function
   * mySyncCallback(app, script, appletName) { ...[modify script here]... return
   * newScript }
   * 
   * StatusManager.syncSend Viewer.setSyncTarget Viewer.syncScript
   */

  // //////////
  private String getModelTitle(int modelIndex) {
    // necessary for status manager frame change?
    return modelSet == null ? null : modelSet.getModelTitle(modelIndex);
  }

  @Override
  public String getModelFileName(int modelIndex) {
    // necessary for status manager frame change?
    return modelSet == null ? null : modelSet.getModelFileName(modelIndex);
  }

  public String dialogAsk(String type, String fileName) {
    return statusManager.dialogAsk(type, fileName);
  }

  public int getScriptDelay() {
    return global.scriptDelay;
  }

  @Override
  public void showUrl(String urlString) {
    // applet.Jmol
    // app Jmol
    // StatusManager
    if (urlString == null)
      return;
    if (urlString.indexOf(":") < 0) {
      String base = fileManager.getAppletDocumentBase();
      if (base == "")
        base = fileManager.getFullPathName();
      if (base.indexOf("/") >= 0) {
        base = base.substring(0, base.lastIndexOf("/") + 1);
      } else if (base.indexOf("\\") >= 0) {
        base = base.substring(0, base.lastIndexOf("\\") + 1);
      }
      urlString = base + urlString;
    }
    Logger.info("showUrl:" + urlString);
    statusManager.showUrl(urlString);
  }

  /**
   * an external applet or app with class that extends org.jmol.jvxl.MeshCreator
   * might execute:
   * 
   * org.jmol.viewer.Viewer viewer = applet.getViewer();
   * viewer.setMeshCreator(this);
   * 
   * then that class's updateMesh(String id) method will be called whenever a
   * mesh is rendered.
   * 
   * @param meshCreator
   */
  public void setMeshCreator(Object meshCreator) {
    loadShape(JmolConstants.SHAPE_ISOSURFACE);
    setShapeProperty(JmolConstants.SHAPE_ISOSURFACE, "meshCreator", meshCreator);
  }

  public void showConsole(boolean showConsole) {
    if (!haveDisplay)
      return;
    // Eval
    try {
      if (appConsole == null)
        getProperty("DATA_API", "getAppConsole", Boolean.TRUE);
      appConsole.setVisible(showConsole);
    } catch (Exception e) {
      // no console for this client...
    }
  }

  public void clearConsole() {
    // Eval
    statusManager.clearConsole();
  }

  public Object getParameterEscaped(String key) {
    return global.getParameterEscaped(key, 0);
  }

  @Override
  public Object getParameter(String key) {
    return global.getParameter(key);
  }

  public ScriptVariable getOrSetNewVariable(String key, boolean doSet) {
    return global.getOrSetNewVariable(key, doSet);
  }

  public ScriptVariable setUserVariable(String name, ScriptVariable value) {
    return global.setUserVariable(name, value);
  }

  public void unsetProperty(String name) {
    global.unsetUserVariable(name);
  }

  public String getVariableList() {
    return global.getVariableList();
  }

  @Override
  public boolean getBooleanProperty(String key) {
    key = key.toLowerCase();
    if (global.htBooleanParameterFlags.containsKey(key))
      return global.htBooleanParameterFlags.get(key).booleanValue();
    // special cases
    if (key.endsWith("p!")) {
      if (actionManager == null)
        return false;
      String s = actionManager.getPickingState().toLowerCase();
      key = key.substring(0, key.length() - 2) + ";";
      return (s.indexOf(key) >= 0);
    }
    if (key.equalsIgnoreCase("executionPaused"))
      return eval.isExecutionPaused();
    if (key.equalsIgnoreCase("executionStepping"))
      return eval.isExecutionStepping();
    if (key.equalsIgnoreCase("haveBFactors"))
      return (modelSet.getBFactors() != null);
    if (key.equalsIgnoreCase("colorRasmol"))
      return colorManager.getDefaultColorRasmol();
    if (key.equalsIgnoreCase("frank"))
      return getShowFrank();
    if (key.equalsIgnoreCase("showSelections"))
      return getSelectionHaloEnabled();
    if (global.htUserVariables.containsKey(key)) {
      ScriptVariable t = global.getUserVariable(key);
      if (t.tok == Token.on)
        return true;
      if (t.tok == Token.off)
        return false;
    }
    Logger.error("viewer.getBooleanProperty(" + key + ") - unrecognized");
    return false;
  }

  @Override
  public void setStringProperty(String key, String value) {
    if (value == null)
      return;
    if (key.charAt(0) == '_') {
      global.setParameterValue(key, value);
      return;
    }
    setStringProperty(key, Token.getTokFromName(key.toLowerCase()), value);
  }

  public void setStringProperty(String key, int tok, String value) {
    boolean found = true;
    switch (tok) {
    case Token.defaultloadfilter:
      // 12.0.RC10
      global.defaultLoadFilter = value;
      break;
    case Token.logfile:
      value = setLogFile(value);
      if (value == null)
        return;
      break;
    case Token.filecachedirectory:
      // 11.9.21
      // not implemented -- application only -- CANNOT BE SET BY STATE
      // global.fileCacheDirectory = value;
      break;
    case Token.atomtypes:
      // 11.7.7
      global.atomTypes = value;
      break;
    case Token.currentlocalpath:
      // /11.6.RC15
      break;
    case Token.picklabel:
      // /11.5.42
      global.pickLabel = value;
      break;
    case Token.quaternionframe:
      // /11.5.39//
      if (value.length() == 2 && value.startsWith("R"))
        // C, P -- straightness from Ramachandran angles
        global.quaternionFrame = value.substring(0, 2);
      else
        global.quaternionFrame = "" + (value.toLowerCase() + "p").charAt(0);
      if (!Parser.isOneOf(global.quaternionFrame, JmolConstants.allowedQuaternionFrames))
        global.quaternionFrame = "p";
      modelSet.setHaveStraightness(false);
      break;
    case Token.defaultvdw:
      // /11.5.11//
      setDefaultVdw(value);
      return;
    case Token.language:
      // /11.1.30//
      // fr cs en none, etc.
      // also serves to change language for callbacks and menu
      new GT(value);
      language = GT.getLanguage();
      modelkit = null;
      if (jmolpopup != null) {
        jmolpopup = null;
        getPopupMenu();
      }
      statusManager.setCallbackFunction("language", language);
      value = GT.getLanguage();
      break;
    case Token.loadformat:
      // /11.1.22//
      global.loadFormat = value;
      break;
    case Token.backgroundcolor:
      // /11.1///
      setObjectColor("background", value);
      return;
    case Token.axis1color:
      setObjectColor("axis1", value);
      return;
    case Token.axis2color:
      setObjectColor("axis2", value);
      return;
    case Token.axis3color:
      setObjectColor("axis3", value);
      return;
    case Token.boundboxcolor:
      setObjectColor("boundbox", value);
      return;
    case Token.unitcellcolor:
      setObjectColor("unitcell", value);
      return;
    case Token.propertycolorscheme:
      setPropertyColorScheme(value, false, false);
      break;
    case Token.hoverlabel:
      // a special label for selected atoms
      setShapeProperty(JmolConstants.SHAPE_HOVER, "atomLabel", value);
      break;
    case Token.defaultdistancelabel:
      // /11.0///
      global.defaultDistanceLabel = value;
      break;
    case Token.defaultanglelabel:
      global.defaultAngleLabel = value;
      break;
    case Token.defaulttorsionlabel:
      global.defaultTorsionLabel = value;
      break;
    case Token.defaultloadscript:
      global.defaultLoadScript = value;
      break;
    case Token.appletproxy:
      fileManager.setAppletProxy(value);
      break;
    case Token.defaultdirectory:
      if (value == null)
        value = "";
      value = value.replace('\\', '/');
      global.defaultDirectory = value;
      break;
    case Token.helppath:
      global.helpPath = value;
      break;
    case Token.defaults:
      setDefaults(value);
      break;
    case Token.defaultcolorscheme:
      // only two are possible: "jmol" and "rasmol"
      setDefaultColors(value.equals("rasmol"));
      break;
    case Token.picking:
      setPickingMode(value, 0);
      return;
    case Token.pickingstyle:
      setPickingStyle(value, 0);
      return;
    case Token.dataseparator:
      // just saving this
      break;
    default:
      if (key.toLowerCase().indexOf("callback") >= 0) {
        statusManager.setCallbackFunction(key, (value.length() == 0
            || value.equalsIgnoreCase("none") ? null : value));
        break;
      }
      found = false;
    }
    key = key.toLowerCase();
    if (global.htNonbooleanParameterValues.containsKey(key)) {
      global.setParameterValue(key, value);
    } else if (!found
        && key.charAt(0) != '@'
        // not found -- @ is a silent mode indicator
        && (global.htBooleanParameterFlags.containsKey(key) || global.htPropertyFlagsRemoved
            .containsKey(key))) {
      // setPropertyError(GT._(
      // "ERROR: cannot set boolean flag to string value: {0}", key));
    } else {
      global.setUserVariable(key, new ScriptVariable(Token.string, value));
    }
  }

  @Override
  public void setFloatProperty(String key, float value) {
    if (Float.isNaN(value))
      return;
    if (key.charAt(0) == '_') {
      global.setParameterValue(key, value);
      return;
    }
    setFloatProperty(key, Token.getTokFromName(key.toLowerCase()), value, false);
  }

  public boolean setFloatProperty(String key, int tok, float value,
                                  boolean isInt) {
    boolean found = true;
    switch (tok) {
    case Token.multiplebondradiusfactor:
      // 12.1.11
      global.multipleBondRadiusFactor = value;
      break;
    case Token.multiplebondspacing:
      // 12.1.11
      global.multipleBondSpacing = value;
      break;
    case Token.slabrange:
      transformManager.setSlabRange(value);
      break;
    case Token.minimizationcriterion:
      global.minimizationCriterion = value;
      break;
    case Token.gestureswipefactor:
      if (haveDisplay)
        actionManager.setGestureSwipeFactor(value);
      break;
    case Token.mousedragfactor:
      if (haveDisplay)
        actionManager.setMouseDragFactor(value);
      break;
    case Token.mousewheelfactor:
      if (haveDisplay)
        actionManager.setMouseWheelFactor(value);
      break;
    case Token.strutlengthmaximum:
      // 11.9.21
      global.strutLengthMaximum = value;
      break;
    case Token.strutdefaultradius:
      global.strutDefaultRadius = value;
      break;
    case Token.navx:
      // 11.7.47
      setSpin("X", (int) value);
      break;
    case Token.navy:
      setSpin("Y", (int) value);
      break;
    case Token.navz:
      setSpin("Z", (int) value);
      break;
    case Token.navfps:
      if (Float.isNaN(value))
        return true;
      setSpin("FPS", (int) value);
      break;
    case Token.loadatomdatatolerance:
      global.loadAtomDataTolerance = value;
      break;
    case Token.hbondsangleminimum:
      // 11.7.9
      global.hbondsAngleMinimum = value;
      break;
    case Token.hbondsdistancemaximum:
      // 11.7.9
      global.hbondsDistanceMaximum = value;
      break;
    case Token.pointgroupdistancetolerance:
      // 11.6.RC2//
      global.pointGroupDistanceTolerance = value;
      break;
    case Token.pointgrouplineartolerance:
      global.pointGroupLinearTolerance = value;
      break;
    case Token.ellipsoidaxisdiameter:
      // 11.5.30//
      if (isInt)
        value = value / 1000;
      break;
    case Token.spinx:
      // /11.3.52//
      setSpin("x", (int) value);
      break;
    case Token.spiny:
      setSpin("y", (int) value);
      break;
    case Token.spinz:
      setSpin("z", (int) value);
      break;
    case Token.spinfps:
      setSpin("fps", (int) value);
      break;
    case Token.defaultdrawarrowscale:
      // /11.3.17//
      global.defaultDrawArrowScale = value;
      break;
    case Token.defaulttranslucent:
      // /11.1///
      global.defaultTranslucent = value;
      break;
    case Token.axesscale:
      setAxesScale(value);
      break;
    case Token.visualrange:
      transformManager.setVisualRange(value);
      refresh(1, "set visualRange");
      break;
    case Token.navigationdepth:
      setNavigationDepthPercent(0, value);
      break;
    case Token.navigationspeed:
      global.navigationSpeed = value;
      break;
    case Token.navigationslab:
      transformManager.setNavigationSlabOffsetPercent(value);
      break;
    case Token.cameradepth:
      transformManager.setCameraDepthPercent(value);
      refresh(1, "set cameraDepth");
      break;
    case Token.rotationradius:
      setRotationRadius(value, true);
      return true;
    case Token.hoverdelay:
      global.hoverDelayMs = (int) (value * 1000);
      break;
    case Token.sheetsmoothing:
      // /11.0///
      global.sheetSmoothing = value;
      break;
    case Token.dipolescale:
      global.dipoleScale = value;
      break;
    case Token.stereodegrees:
      transformManager.setStereoDegrees(value);
      break;
    case Token.vectorscale:
      // public -- no need to set
      setVectorScale(value);
      return true;
    case Token.vibrationperiod:
      // public -- no need to set
      setVibrationPeriod(value);
      return true;
    case Token.vibrationscale:
      // public -- no need to set
      setVibrationScale(value);
      return true;
    case Token.bondtolerance:
      setBondTolerance(value);
      return true;
    case Token.minbonddistance:
      setMinBondDistance(value);
      return true;
    case Token.scaleangstromsperinch:
      transformManager.setScaleAngstromsPerInch(value);
      break;
    case Token.solventproberadius:
      global.solventProbeRadius = value;
      break;
    default:
      if (isInt)
        return false;
      found = false;
    }
    key = key.toLowerCase();
    if (global.htNonbooleanParameterValues.containsKey(key))
      global.setParameterValue(key, value);
    else if (!found && global.htBooleanParameterFlags.containsKey(key)) {
      // setPropertyError(GT._(
      // "ERROR: cannot set boolean flag to numeric value: {0}", key));
    } else {
      global.setUserVariable(key, new ScriptVariable(Token.decimal, new Float(
          value)));
    }
    return true;
  }

  @Override
  public void setIntProperty(String key, int value) {
    if (value == Integer.MIN_VALUE)
      return;
    if (key.charAt(0) == '_') {
      global.setParameterValue(key, value);
      return;
    }
    setIntProperty(key, Token.getTokFromName(key.toLowerCase()), value);
  }
  
  public void setIntProperty(String key, int tok, int value) {
    boolean found = true;
    switch (tok) {
    case Token.isosurfacepropertysmoothingpower:
      // 12.1.11
      global.isosurfacePropertySmoothingPower = value;
      break;
    case Token.repaintwaitms:
      // 12.0.RC4
      global.repaintWaitMs = value;
      break;
    case Token.smallmoleculemaxatoms:
      // 12.0.RC3
      global.smallMoleculeMaxAtoms = value;
      break;
    case Token.minimizationsteps:
      global.minimizationSteps = value;
      break;
    case Token.propertyatomnumbercolumncount:
    case Token.propertyatomnumberfield: // 11.6.RC16
    case Token.ellipsoiddotcount: // 11.5.30
    case Token.propertydatafield: // 11.1.31
      // just save in the hashtable, not in global
      break;
    case Token.strutspacing:
      // 11.9.21
      global.strutSpacing = value;
      break;
    case Token.phongexponent:
      // 11.9.13
      Graphics3D.setPhongExponent(value);
      break;
    case Token.helixstep:
      // 11.8.RC3
      global.helixStep = value;
      modelSet.setHaveStraightness(false);
      break;
    case Token.dotscale:
      // 12.0.RC25
      global.dotScale = value;
      break;
    case Token.dotdensity:
      // 11.6.RC2//
      global.dotDensity = value;
      break;
    case Token.delaymaximumms:
      // 11.5.4//
      global.delayMaximumMs = value;
      break;
    case Token.loglevel:
      // /11.3.52//
      Logger.setLogLevel(value);
      Logger.info("logging level set to " + value);
      global.setParameterValue("logLevel", value);
      eval.setDebugging();
      return;
    case Token.axesmode:
      switch (value) {
      case JmolConstants.AXES_MODE_MOLECULAR:
        setAxesModeMolecular(true);
        return;
      case JmolConstants.AXES_MODE_BOUNDBOX:
        setAxesModeMolecular(false);
        return;
      case JmolConstants.AXES_MODE_UNITCELL:
        setAxesModeUnitCell(true);
        return;
      }
      return;
    case Token.strandcount:
      // /11.1///
      setStrandCount(0, value);
      return;
    case Token.strandcountforstrands:
      setStrandCount(JmolConstants.SHAPE_STRANDS, value);
      return;
    case Token.strandcountformeshribbon:
      setStrandCount(JmolConstants.SHAPE_MESHRIBBON, value);
      return;
    case Token.perspectivemodel:
      setPerspectiveModel(value);
      break;
    case Token.showscript:
      global.scriptDelay = value;
      break;
    case Token.specularpower:
      Graphics3D.setSpecularPower(value);
      break;
    case Token.specularexponent:
      Graphics3D.setSpecularPower(-value);
      break;
    case Token.bondradiusmilliangstroms:
      setMarBond((short) value);
      // public method -- no need to set
      return;
    case Token.specular:
      setBooleanProperty(key, tok, value == 1, true);
      return;
    case Token.specularpercent:
      Graphics3D.setSpecularPercent(value);
      break;
    case Token.diffusepercent:
      Graphics3D.setDiffusePercent(value);
      break;
    case Token.ambientpercent:
      Graphics3D.setAmbientPercent(value);
      break;
    case Token.zdepth:
      transformManager.zDepthToPercent(value);
      break;
    case Token.zslab:
      transformManager.zSlabToPercent(value);
      break;
    case Token.depth:
      transformManager.depthToPercent(value);
      break;
    case Token.slab:
      transformManager.slabToPercent(value);
      break;
    case Token.zshadepower:
      Graphics3D.setZShadePower(Math.max(value, 1));
      break;
    case Token.ribbonaspectratio:
      global.ribbonAspectRatio = value;
      break;
    case Token.pickingspinrate:
      global.pickingSpinRate = (value < 1 ? 1 : value);
      break;
    case Token.animationfps:
      setAnimationFps(value);
      break;
    case Token.percentvdwatom:
      setPercentVdwAtom(value);
      break;
    case Token.hermitelevel:
      global.hermiteLevel = value;
      break;
    default:
      if ((value != 0 && value != 1)
          || !setBooleanProperty(key, tok, value == 1, false)) {
        if (setFloatProperty(key, tok, value, true))
          return;
      }
      found = false;
    }
    key = key.toLowerCase();
    if (global.htNonbooleanParameterValues.containsKey(key)) {
      global.setParameterValue(key, value);
    } else if (!found && global.htBooleanParameterFlags.containsKey(key)) {
      // setPropertyError(GT._(
      // "ERROR: cannot set boolean flag to numeric value: {0}", key));
    } else {
      global.setUserVariable(key, ScriptVariable.intVariable(value));
    }
  }

  @Override
  public void setBooleanProperty(String key, boolean value) {
    if (key.charAt(0) == '_') {
      global.setParameterValue(key, value);
      return;
    }
    setBooleanProperty(key, Token.getTokFromName(key.toLowerCase()), value,
        true);
  }

  private boolean setBooleanProperty(String key, int tok, boolean value,
                                     boolean defineNew) {
    boolean found = true;
    boolean doRepaint = true;
    switch (tok) {
    case Token.legacyautobonding:
      global.legacyAutoBonding = value;
      break;
    case Token.defaultstructuredssp:
      global.defaultStructureDSSP = value;
      break;
    case Token.dsspcalchydrogen:
      global.dsspCalcHydrogen = value;
      break;
    case Token.allowmodelkit:
      // 11.12.RC15
      global.allowModelkit = value;
      if (!value)
        setModelKitMode(false);
      break;
    case Token.modelkitmode:
      setModelKitMode(value);
      break;
    case Token.multiprocessor:
      // 12.0.RC6
      global.multiProcessor = value && (nProcessors > 1);
      break;
    case Token.monitorenergy:
      // 12.0.RC6
      global.monitorEnergy = value;
      break;
    case Token.hbondsrasmol:
      // 12.0.RC3
      global.hbondsRasmol = value;
      break;
    case Token.minimizationrefresh:
      global.minimizationRefresh = value;
      break;
    case Token.minimizationsilent:
      // 12.0.RC5
      global.minimizationSilent = value;
      break;
    case Token.usearcball:
      global.useArcBall = value;
      break;
    case Token.iskiosk:
      // 11.9.29
      isKiosk = value;
      break;
    // 11.9.28
    case Token.waitformoveto:
      global.waitForMoveTo = value;
      break;
    case Token.logcommands:
      global.logCommands = true;
      break;
    case Token.loggestures:
      global.logGestures = true;
      break;
    case Token.allowmultitouch:
      // 11.9.24
      global.allowMultiTouch = value;
      break;
    case Token.preservestate:
      // 11.9.23
      global.preserveState = value;
      modelSet.setPreserveState(value);
      break;
    case Token.strutsmultiple:
      // 11.9.23
      global.strutsMultiple = value;
      break;
    case Token.filecaching:
      // 11.9.21
      // not implemented -- application only -- CANNOT BE SET BY STATE
      break;
    case Token.slabbyatom:
      // 11.9.19
      global.slabByAtom = value;
      break;
    case Token.slabbymolecule:
      // 11.9.18
      global.slabByMolecule = value;
      break;
    case Token.saveproteinstructurestate:
      // 11.9.15
      global.saveProteinStructureState = value;
      break;
    case Token.allowgestures:
      global.allowGestures = value;
      break;
    case Token.imagestate:
      // 11.8.RC6
      global.imageState = value;
      break;
    case Token.useminimizationthread:
      // 11.7.40
      global.useMinimizationThread = value;
      break;
    // case Token.autoloadorientation:
    // // 11.7.30; removed in 12.0.RC10 -- use FILTER "NoOrient"
    // global.autoLoadOrientation = value;
    // break;
    case Token.allowkeystrokes:
      // 11.7.24
      if (global.disablePopupMenu)
        value = false;
      global.allowKeyStrokes = value;
      break;
    case Token.dragselected:
      // 11.7.24
      global.dragSelected = value;
      break;
    case Token.showkeystrokes:
      global.showKeyStrokes = value;
      break;
    case Token.fontcaching:
      // 11.7.10
      global.fontCaching = value;
      break;
    case Token.atompicking:
      // 11.6.RC13
      global.atomPicking = value;
      break;
    case Token.bondpicking:
      // 11.6.RC13
      global.bondPicking = value;
      break;
    case Token.selectallmodels:
      // 11.5.52
      global.selectAllModels = value;
      break;
    case Token.messagestylechime:
      // 11.5.39
      global.messageStyleChime = value;
      break;
    case Token.pdbsequential:
      global.pdbSequential = value;
      break;
    case Token.pdbgetheader:
      global.pdbGetHeader = value;
      break;
    case Token.ellipsoidaxes:
      global.ellipsoidAxes = value;
      break;
    case Token.ellipsoidarcs:
      global.ellipsoidArcs = value;
      break;
    case Token.ellipsoidball:
      global.ellipsoidBall = value;
      break;
    case Token.ellipsoiddots:
      global.ellipsoidDots = value;
      break;
    case Token.ellipsoidfill:
      global.ellipsoidFill = value;
      break;
    case Token.fontscaling:
      // 11.5.4
      global.fontScaling = value;
      break;
    case Token.syncmouse:
      // 11.3.56
      setSyncTarget(0, value);
      break;
    case Token.syncscript:
      setSyncTarget(1, value);
      break;
    case Token.wireframerotation:
      // 11.3.55
      global.wireframeRotation = value;
      break;
    case Token.isosurfacepropertysmoothing:
      // 11.3.46
      global.isosurfacePropertySmoothing = value;
      break;
    case Token.drawpicking:
      // 11.3.43
      global.drawPicking = value;
      break;
    case Token.antialiasdisplay:
      // 11.3.36
      setAntialias(0, value);
      break;
    case Token.antialiastranslucent:
      setAntialias(1, value);
      break;
    case Token.antialiasimages:
      setAntialias(2, value);
      break;
    case Token.smartaromatic:
      // 11.3.29
      global.smartAromatic = value;
      break;
    case Token.applysymmetrytobonds:
      // 11.1.29
      setApplySymmetryToBonds(value);
      break;
    case Token.appendnew:
      // 11.1.22
      setAppendNew(value);
      break;
    case Token.autofps:
      global.autoFps = value;
      break;
    case Token.usenumberlocalization:
      // 11.1.21
      TextFormat.setUseNumberLocalization(global.useNumberLocalization = value);
      break;
    case Token.frank:
      key = "showFrank";
      setFrankOn(value);
      break;
    case Token.showfrank:
      // 11.1.20
      setFrankOn(value);
      break;
    case Token.solvent:
      key = "solventProbe";
      global.solventOn = value;
      break;
    case Token.solventprobe:
      global.solventOn = value;
      break;
    case Token.dynamicmeasurements:
      setDynamicMeasurements(value);
      break;
    case Token.allowrotateselected:
      // 11.1.14
      global.allowRotateSelected = value;
      break;
    case Token.allowmoveatoms:
      // 12.1.21
      global.allowMoveAtoms = value;
      global.allowRotateSelected = value;
      global.dragSelected = value;
      break;
    case Token.showscript:
      // /11.1.13///
      setIntProperty("showScript", tok, value ? 1 : 0);
      return true;
    case Token.allowembeddedscripts:
      // /11.1///
      global.allowEmbeddedScripts = value;
      break;
    case Token.navigationperiodic:
      global.navigationPeriodic = value;
      break;
    case Token.zshade:
      transformManager.setZShadeEnabled(value);
      return true;
    case Token.drawhover:
      if (haveDisplay)
        global.drawHover = value;
      break;
    case Token.navigationmode:
      setNavigationMode(value);
      break;
    case Token.navigatesurface:
      global.navigateSurface = value;
      break;
    case Token.hidenavigationpoint:
      global.hideNavigationPoint = value;
      break;
    case Token.shownavigationpointalways:
      global.showNavigationPointAlways = value;
      break;
    case Token.refreshing:
      // /11.0///
      setRefreshing(value);
      break;
    case Token.justifymeasurements:
      global.justifyMeasurements = value;
      break;
    case Token.ssbondsbackbone:
      global.ssbondsBackbone = value;
      break;
    case Token.hbondsbackbone:
      global.hbondsBackbone = value;
      break;
    case Token.hbondssolid:
      global.hbondsSolid = value;
      break;
    case Token.specular:
      Graphics3D.setSpecular(value);
      break;
    case Token.slabenabled:
      // Eval.slab
      transformManager.setSlabEnabled(value); // refresh?
      return true;
    case Token.zoomenabled:
      transformManager.setZoomEnabled(value);
      return true;
    case Token.highresolution:
      global.highResolutionFlag = value;
      break;
    case Token.tracealpha:
      global.traceAlpha = value;
      break;
    case Token.zoomlarge:
      global.zoomLarge = value;
      transformManager.scaleFitToScreen(false, value, false, true);
      break;
    case Token.languagetranslation:
      GT.setDoTranslate(value);
      break;
    case Token.hidenotselected:
      selectionManager.setHideNotSelected(value);
      break;
    case Token.scriptqueue:
      setScriptQueue(value);
      break;
    case Token.dotsurface:
      global.dotSurface = value;
      break;
    case Token.dotsselectedonly:
      global.dotsSelectedOnly = value;
      break;
    case Token.selectionhalos:
      setSelectionHalos(value);
      break;
    case Token.selecthydrogen:
      global.rasmolHydrogenSetting = value;
      break;
    case Token.selecthetero:
      global.rasmolHeteroSetting = value;
      break;
    case Token.showmultiplebonds:
      global.showMultipleBonds = value;
      break;
    case Token.showhiddenselectionhalos:
      global.showHiddenSelectionHalos = value;
      break;
    case Token.windowcentered:
      transformManager.setWindowCentered(value);
      break;
    case Token.displaycellparameters:
      global.displayCellParameters = value;
      break;
    case Token.testflag1:
      global.testFlag1 = value;
      break;
    case Token.testflag2:
      global.testFlag2 = value;
      break;
    case Token.testflag3:
      global.testFlag3 = value;
      break;
    case Token.testflag4:
      jmolTest();
      global.testFlag4 = value;
      break;
    case Token.ribbonborder:
      global.ribbonBorder = value;
      break;
    case Token.cartoonbaseedges:
      global.cartoonBaseEdges = value;
      break;
    case Token.cartoonrockets:
      global.cartoonRockets = value;
      break;
    case Token.rocketbarrels:
      global.rocketBarrels = value;
      break;
    case Token.greyscalerendering:
      g3d.setGreyscaleMode(global.greyscaleRendering = value);
      break;
    case Token.measurementlabels:
      global.measurementLabels = value;
      break;
    case Token.axeswindow:
      // remove parameters, so don't set htParameter key here
      setAxesModeMolecular(!value);
      return true;
    case Token.axesmolecular:
      // remove parameters, so don't set htParameter key here
      setAxesModeMolecular(value);
      return true;
    case Token.axesunitcell:
      // remove parameters, so don't set htParameter key here
      setAxesModeUnitCell(value);
      return true;
    case Token.axesorientationrasmol:
      // public; no need to set here
      setAxesOrientationRasmol(value);
      return true;
    case Token.colorrasmol:
      setStringProperty("defaultcolorscheme", Token.defaultcolorscheme,value ? "rasmol" : "jmol");
      return true;
    case Token.debugscript:
      setDebugScript(value);
      return true;
    case Token.perspectivedepth:
      setPerspectiveDepth(value);
      return true;
    case Token.autobond:
      // public - no need to set
      setAutoBond(value);
      return true;
    case Token.showaxes:
      setShowAxes(value);
      return true;
    case Token.showboundbox:
      setShowBbcage(value);
      return true;
    case Token.showhydrogens:
      setShowHydrogens(value);
      return true;
    case Token.showmeasurements:
      setShowMeasurements(value);
      return true;
    case Token.showunitcell:
      setShowUnitCell(value);
      return true;
    case Token.bondmodeor:
      doRepaint = false;
      global.bondModeOr = value;
      break;
    case Token.zerobasedxyzrasmol:
      doRepaint = false;
      global.zeroBasedXyzRasmol = value;
      reset(true);
      break;
    case Token.rangeselected:
      doRepaint = false;
      global.rangeSelected = value;
      break;
    case Token.measureallmodels:
      doRepaint = false;
      global.measureAllModels = value;
      break;
    case Token.statusreporting:
      doRepaint = false;
      // not part of the state
      statusManager.setAllowStatusReporting(value);
      break;
    case Token.chaincasesensitive:
      doRepaint = false;
      global.chainCaseSensitive = value;
      break;
    case Token.hidenameinpopup:
      doRepaint = false;
      global.hideNameInPopup = value;
      break;
    case Token.disablepopupmenu:
      doRepaint = false;
      global.disablePopupMenu = value;
      break;
    case Token.forceautobond:
      doRepaint = false;
      global.forceAutoBond = value;
      break;
    case Token.fractionalrelative:
      doRepaint = false;
      global.fractionalRelative = value;
      break;
    case Token.nada:
    default:
      doRepaint = false; // ??
      found = false;
    }
    if (!defineNew)
      return found;
    key = key.toLowerCase();
    boolean isJmol = global.htBooleanParameterFlags.containsKey(key);
    if (isJmol)
      global.setParameterValue(key, value);
    else if (!found && global.htNonbooleanParameterValues.containsKey(key)) {
      // setPropertyError(GT._(
      // "ERROR: Cannot set value of this variable to a boolean: {0}", key));
      return true;
    } else {
      global.setUserVariable(key, ScriptVariable.getBoolean(value));
    }
    if (!found)
      return false;
    // not sure why we are doing this, and only for booleans...
    if (doRepaint)
      setTainted(true);
    return true;
  }

  /*
   * public void setFileCacheDirectory(String fileOrDir) { if (fileOrDir ==
   * null) fileOrDir = ""; global._fileCache = fileOrDir; }
   * 
   * String getFileCacheDirectory() { if (!global._fileCaching) return null;
   * return global._fileCache; }
   */

  private void setModelKitMode(boolean value) {
    if (value || global.modelKitMode) {
      setPickingMode(null, value ? ActionManager.PICKING_ASSIGN_BOND
          : ActionManager.PICKING_IDENTIFY);
      setPickingMode(null, value ? ActionManager.PICKING_ASSIGN_ATOM
          : ActionManager.PICKING_IDENTIFY);
    }
    boolean isChange = (global.modelKitMode != value);
    global.modelKitMode = value;
    highlight(null);
    if (value) {
      setNavigationMode(false);
      selectAll();
      // setShapeProperty(JmolConstants.SHAPE_LABELS, "color", "RED");
      setAtomPickingOption("C");
      setBondPickingOption("p");
      if (!isApplet)
        popupMenu(0, 0, 'm');
      if (isChange)
        statusManager.setCallbackFunction("modelkit", "ON");
      global.modelKitMode = true;
      if (getAtomCount() == 0)
        zap(false, true, true);
    } else {
      actionManager.setPickingMode(-1);
      setStringProperty("pickingStyle", "toggle");
      setBooleanProperty("bondPicking", false);
      if (isChange)
        statusManager.setCallbackFunction("modelkit", "OFF");
    }

  }

  public boolean getModelkitMode() {
    return global.modelKitMode;
  }

  private String language = GT.getLanguage();

  public String getLanguage() {
    return language;
  }

  public void setSmilesString(String s) {
    if (s == null)
      global.removeJmolParameter("_smilesString");
    else
      global.setParameterValue("_smilesString", s);
  }

  public void removeUserVariable(String key) {
    global.removeUserVariable(key);
    if (key.indexOf("callback") >= 0)
      statusManager.setCallbackFunction(key, null);
  }

  public boolean isJmolVariable(String key) {
    return global.isJmolVariable(key);
  }

  private void jmolTest() {
    /*
     * Vector v = new Vector(); Vector m = new Vector(); v.add(m);
     * m.add("MODEL     2");m.add(
     * "HETATM    1 H1   UNK     1       2.457   0.000   0.000  1.00  0.00           H  "
     * );m.add(
     * "HETATM    2 C1   UNK     1       1.385   0.000   0.000  1.00  0.00           C  "
     * );m.add(
     * "HETATM    3 C2   UNK     1      -1.385  -0.000   0.000  1.00  0.00           C  "
     * ); v.add(new String[] { "MODEL     2",
     * "HETATM    1 H1   UNK     1       2.457   0.000   0.000  1.00  0.00           H  "
     * ,
     * "HETATM    2 C1   UNK     1       1.385   0.000   0.000  1.00  0.00           C  "
     * ,
     * "HETATM    3 C2   UNK     1      -1.385  -0.000   0.000  1.00  0.00           C  "
     * , }); v.add(new String[] {"3","testing","C 0 0 0","O 0 1 0","N 0 0 1"} );
     * v.add("3\ntesting\nC 0 0 0\nO 0 1 0\nN 0 0 1\n"); loadInline(v, false);
     */
  }

  public boolean isPdbSequential() {
    return global.pdbSequential;
  }

  boolean getSelectAllModels() {
    return global.selectAllModels;
  }

  public boolean getMessageStyleChime() {
    return global.messageStyleChime;
  }

  public boolean getFontCaching() {
    return global.fontCaching;
  }

  public boolean getFontScaling() {
    return global.fontScaling;
  }

  public void showParameter(String key, boolean ifNotSet, int nMax) {
    String sv = "" + global.getParameterEscaped(key, nMax);
    if (ifNotSet || sv.indexOf("<not defined>") < 0)
      showString(key + " = " + sv, false);
  }

  public void showString(String str, boolean isPrint) {
    if (isScriptQueued && (!isSilent || isPrint))
      Logger.info(str);
    scriptEcho(str);
  }

  public String getAllSettings(String prefix) {
    return global.getAllSettings(prefix);
  }

  public String getBindingInfo(String qualifiers) {
    return (haveDisplay ? actionManager.getBindingInfo(qualifiers) : "");
  }

  // ////// flags and settings ////////

  public int getDelayMaximum() {
    return (haveDisplay ? global.delayMaximumMs : 1);
  }

  public boolean getDotSurfaceFlag() {
    return global.dotSurface;
  }

  public boolean getDotsSelectedOnlyFlag() {
    return global.dotsSelectedOnly;
  }

  public int getDotDensity() {
    return global.dotDensity;
  }
  
  public int getDotScale() {
    return global.dotScale;
  }
  
  public boolean isRangeSelected() {
    return global.rangeSelected;
  }

  public int getIsosurfacePropertySmoothing(boolean asPower) {
    // Eval
    return (asPower ? global.isosurfacePropertySmoothingPower : global.isosurfacePropertySmoothing ? 1 : 0);
  }

  public boolean getWireframeRotation() {
    return global.wireframeRotation;
  }

  public boolean isWindowCentered() {
    return transformManager.isWindowCentered();
  }

  public void setNavigationDepthPercent(float timeSec, float percent) {
    transformManager.setNavigationDepthPercent(timeSec, percent);
    refresh(1, "set navigationDepth");
  }

  float getNavigationSpeed() {
    return global.navigationSpeed;
  }

  public boolean getShowNavigationPoint() {
    if (!global.navigationMode || !transformManager.canNavigate())
      return false;
    return (isNavigating() && !global.hideNavigationPoint
        || global.showNavigationPointAlways || getInMotion());
  }

  public float getSolventProbeRadius() {
    return global.solventProbeRadius;
  }

  public float getCurrentSolventProbeRadius() {
    return global.solventOn ? global.solventProbeRadius : 0;
  }

  boolean getSolventOn() {
    return global.solventOn;
  }

  public boolean getTestFlag1() {
    return global.testFlag1;
  }

  public boolean getTestFlag2() {
    return global.testFlag2;
  }

  public boolean getTestFlag3() {
    return global.testFlag3;
  }

  public boolean getTestFlag4() {
    return global.testFlag4;
  }

  @Override
  public void setPerspectiveDepth(boolean perspectiveDepth) {
    // setBooleanProperty
    // stateManager.setCrystallographicDefaults
    // app preferences dialog
    global.setParameterValue("perspectiveDepth", perspectiveDepth);
    transformManager.setPerspectiveDepth(perspectiveDepth);
  }

  @Override
  public void setAxesOrientationRasmol(boolean TF) {
    // app PreferencesDialog
    // stateManager
    // setBooleanproperty
    /*
     * *************************************************************** RasMol
     * has the +Y axis pointing down And rotations about the y axis are
     * left-handed setting this flag makes Jmol mimic this behavior
     * 
     * All versions of Jmol prior to 11.5.51 incompletely implement this flag.
     * Really all it is just a flag to tell Eval to flip the sign of the Y
     * rotation when specified specifically as "rotate/spin y 30".
     * 
     * In principal, we could display the axis opposite as well, but that is
     * only aesthetic and not at all justified if the axis is molecular.
     * **************************************************************
     */
    global.setParameterValue("axesOrientationRasmol", TF);
    global.axesOrientationRasmol = TF;
    reset(true);
  }

  @Override
  public boolean getAxesOrientationRasmol() {
    return global.axesOrientationRasmol;
  }

  void setAxesScale(float scale) {
    global.axesScale = scale;
    axesAreTainted = true;
  }

  public Point3f[] getAxisPoints() {
    // for uccage renderer
    return (getObjectMad(StateManager.OBJ_AXIS1) == 0
        || getAxesMode() != JmolConstants.AXES_MODE_UNITCELL
        || ((Boolean) getShapeProperty(JmolConstants.SHAPE_AXES, "axesTypeXY"))
            .booleanValue() 
        || getShapeProperty(JmolConstants.SHAPE_AXES, "origin") != null 
            ? null : (Point3f[]) getShapeProperty(
        JmolConstants.SHAPE_AXES, "axisPoints"));
  }

  public float getAxesScale() {
    return global.axesScale;
  }

  public void resetError() {
    global.removeJmolParameter("_errormessage");  
  }
  
  private void setAxesModeMolecular(boolean TF) {
    global.axesMode = (TF ? JmolConstants.AXES_MODE_MOLECULAR
        : JmolConstants.AXES_MODE_BOUNDBOX);
    axesAreTainted = true;
    global.removeJmolParameter("axesunitcell");
    global.removeJmolParameter(TF ? "axeswindow" : "axesmolecular");
    global.setParameterValue("axesMode", global.axesMode);
    global.setParameterValue(TF ? "axesMolecular" : "axesWindow", true);

  }

  void setAxesModeUnitCell(boolean TF) {
    // stateManager
    // setBooleanproperty
    global.axesMode = (TF ? JmolConstants.AXES_MODE_UNITCELL
        : JmolConstants.AXES_MODE_BOUNDBOX);
    axesAreTainted = true;
    global.removeJmolParameter("axesmolecular");
    global.removeJmolParameter(TF ? "axeswindow" : "axesunitcell");
    global.setParameterValue(TF ? "axesUnitcell" : "axesWindow", true);
    global.setParameterValue("axesMode", global.axesMode);
  }

  public int getAxesMode() {
    return global.axesMode;
  }

  public boolean getDisplayCellParameters() {
    return global.displayCellParameters;
  }

  @Override
  public boolean getPerspectiveDepth() {
    return transformManager.getPerspectiveDepth();
  }

  @Override
  public void setSelectionHalos(boolean TF) {
    // display panel can hit this without a frame, apparently
    if (modelSet == null || TF == getSelectionHaloEnabled())
      return;
    global.setParameterValue("selectionHalos", TF);
    loadShape(JmolConstants.SHAPE_HALOS);
    // a frame property, so it is automatically reset
    modelSet.setSelectionHaloEnabled(TF);
  }

  public boolean getSelectionHaloEnabled() {
    return modelSet.getSelectionHaloEnabled() || showSelected;
  }

  public boolean getBondSelectionModeOr() {
    return global.bondModeOr;
  }

  public boolean getChainCaseSensitive() {
    return global.chainCaseSensitive;
  }

  public boolean getRibbonBorder() {
    // mps
    return global.ribbonBorder;
  }

  public boolean getCartoonRocketFlag() {
    return global.cartoonRockets;
  }

  public boolean getRocketBarrelFlag() {
    return global.rocketBarrels;
  }

  public boolean getCartoonBaseEdgesFlag() {
    return global.cartoonBaseEdges;
  }

  private void setStrandCount(int type, int value) {
    switch (type) {
    case JmolConstants.SHAPE_STRANDS:
      global.strandCountForStrands = value;
      break;
    case JmolConstants.SHAPE_MESHRIBBON:
      global.strandCountForMeshRibbon = value;
      break;
    default:
      global.strandCountForStrands = value;
      global.strandCountForMeshRibbon = value;
      break;
    }
    global.setParameterValue("strandCount", value);
    global.setParameterValue("strandCountForStrands",
        global.strandCountForStrands);
    global.setParameterValue("strandCountForMeshRibbon",
        global.strandCountForMeshRibbon);
  }

  public int getStrandCount(int type) {
    return (type == JmolConstants.SHAPE_STRANDS ? global.strandCountForStrands
        : global.strandCountForMeshRibbon);
  }

  boolean getHideNameInPopup() {
    return global.hideNameInPopup;
  }

  boolean getNavigationPeriodic() {
    return global.navigationPeriodic;
  }

  /**
   * 
   * @param fromWhere
   */
  private void stopAnimationThreads(String fromWhere) {
    setVibrationOff();
    setSpinOn(false);
    setNavOn(false);
    setAnimationOn(false);
    /*
     * try { System.out.println(Thread.currentThread() + " from " + fromWhere +
     * " stopanimatinoThreads -- waiting 1 second"); Thread.sleep(1000); } catch
     * (InterruptedException e) { // TODO }
     * System.out.println(Thread.currentThread() +
     * "Viewer stopAnimationThread NOT canceling rendering"); //
     * cancelRendering();
     */

  }

  // void cancelRendering() {
  // if (haveDisplay)
  // repaintManager.cancelRendering();
  // }

  private void setNavigationMode(boolean TF) {
    global.navigationMode = TF;
    if (TF && !transformManager.canNavigate()) {
      stopAnimationThreads("setNavigationMode");
      transformManager = transformManager.getNavigationManager(this,
          dimScreen.width, dimScreen.height);
      transformManager.homePosition(true);
    }
    transformManager.setNavigationMode(TF);
  }

  public boolean getNavigationMode() {
    return global.navigationMode;
  }

  public boolean getNavigateSurface() {
    return global.navigateSurface;
  }

  /**
   * for an external application
   * 
   * @param transformManager
   */
  public void setTransformManager(TransformManager transformManager) {
    stopAnimationThreads("setTransformMan");
    this.transformManager = transformManager;
    transformManager.setViewer(this, dimScreen.width, dimScreen.height);
    setTransformManagerDefaults();
    transformManager.homePosition(true);
  }

  private void setPerspectiveModel(int mode) {
    if (transformManager.perspectiveModel == mode)
      return;
    stopAnimationThreads("setPerspectivemodeg");
    switch (mode) {
    case 10:
      transformManager = new TransformManager10(this, dimScreen.width,
          dimScreen.height);
      break;
    default:
      transformManager = transformManager.getNavigationManager(this,
          dimScreen.width, dimScreen.height);
    }
    setTransformManagerDefaults();
    transformManager.homePosition(true);
  }

  private void setTransformManagerDefaults() {
    transformManager.setCameraDepthPercent(global.cameraDepth);
    transformManager.setPerspectiveDepth(global.perspectiveDepth);
    transformManager.setStereoDegrees(JmolConstants.DEFAULT_STEREO_DEGREES);
    transformManager.setVisualRange(global.visualRange);
    transformManager.setSpinOn(false);
    transformManager.setVibrationPeriod(0);
    transformManager.setFrameOffsets(frameOffsets);
  }

  public Point3f[] getCameraFactors() {
    return transformManager.getCameraFactors();
  }

  boolean getZoomLarge() {
    return global.zoomLarge;
  }

  public boolean getTraceAlpha() {
    // mps
    return global.traceAlpha;
  }

  public int getHermiteLevel() {
    // mps
    return (getSpinOn() ? 0 : global.hermiteLevel);
  }

  public boolean getHighResolution() {
    // mps
    return global.highResolutionFlag;
  }

  String getLoadState() {
    return global.getLoadState();
  }

  @Override
  public void setAutoBond(boolean TF) {
    // setBooleanProperties
    global.setParameterValue("autobond", TF);
    global.autoBond = TF;
  }

  @Override
  public boolean getAutoBond() {
    return global.autoBond;
  }

  public int[] makeConnections(float minDistance, float maxDistance, int order,
                               int connectOperation, BitSet bsA, BitSet bsB,
                               BitSet bsBonds, boolean isBonds, float energy) {
    // eval
    clearModelDependentObjects();
    clearAllMeasurements(); // necessary for serialization
    clearMinimization();
    return modelSet.makeConnections(minDistance, maxDistance, order,
        connectOperation, bsA, bsB, bsBonds, isBonds, energy);
  }

  @Override
  public void rebond() {
    // Eval, PreferencesDialog
    clearModelDependentObjects();
    modelSet.deleteAllBonds();
    modelSet.autoBond(null, null, null, null, getMadBond(), false);
    addStateScript("connect;", false, true);
  }

  public void setPdbConectBonding(boolean isAuto) {
    // from eval
    clearModelDependentObjects();
    modelSet.deleteAllBonds();
    BitSet bsExclude = new BitSet();
    modelSet.setPdbConectBonding(0, 0, bsExclude);
    if (isAuto) {
      modelSet.autoBond(null, null, bsExclude, null, getMadBond(), false);
      addStateScript("connect PDB AUTO;", false, true);
      return;
    }
    addStateScript("connect PDB;", false, true);
  }

  // //////////////////////////////////////////////////////////////
  // Graphics3D
  // //////////////////////////////////////////////////////////////

  boolean getGreyscaleRendering() {
    return global.greyscaleRendering;
  }

  boolean getDisablePopupMenu() {
    return global.disablePopupMenu;
  }

  public boolean getForceAutoBond() {
    return global.forceAutoBond;
  }

  // ///////////////////////////////////////////////////////////////
  // delegated to stateManager
  // ///////////////////////////////////////////////////////////////

  private RadiusData rd = new RadiusData();

  @Override
  public void setPercentVdwAtom(int value) {
    global.setParameterValue("percentVdwAtom", value);
    global.percentVdwAtom = value;
    rd.value = value / 100f;
    rd.type = RadiusData.TYPE_FACTOR;
    rd.vdwType = JmolConstants.VDW_AUTO;
    setShapeSize(JmolConstants.SHAPE_BALLS, rd, null);
  }

  @Override
  public int getPercentVdwAtom() {
    return global.percentVdwAtom;
  }

  public RadiusData getDefaultRadiusData() {
    return rd;
  }

  @Override
  public short getMadBond() {
    return (short) (global.bondRadiusMilliAngstroms * 2);
  }

  public short getMarBond() {
    return global.bondRadiusMilliAngstroms;
  }

  /*
   * void setModeMultipleBond(byte modeMultipleBond) { //not implemented
   * global.modeMultipleBond = modeMultipleBond; }
   */

  public byte getModeMultipleBond() {
    // sticksRenderer
    return global.modeMultipleBond;
  }

  public boolean getShowMultipleBonds() {
    return global.showMultipleBonds;
  }

  public float getMultipleBondSpacing() {
    return global.multipleBondSpacing;
  }

  public float getMultipleBondRadiusFactor() {
    return global.multipleBondRadiusFactor;
  }

  @Override
  public void setShowHydrogens(boolean TF) {
    // PreferencesDialog
    // setBooleanProperty
    global.setParameterValue("showHydrogens", TF);
    global.showHydrogens = TF;
  }

  @Override
  public boolean getShowHydrogens() {
    return global.showHydrogens;
  }

  public boolean getShowHiddenSelectionHalos() {
    return global.showHiddenSelectionHalos;
  }

  @Override
  public void setShowBbcage(boolean value) {
    setObjectMad(JmolConstants.SHAPE_BBCAGE, "boundbox", (short) (value ? -4
        : 0));
    global.setParameterValue("showBoundBox", value);
  }

  @Override
  public boolean getShowBbcage() {
    return getObjectMad(StateManager.OBJ_BOUNDBOX) != 0;
  }

  public void setShowUnitCell(boolean value) {
    setObjectMad(JmolConstants.SHAPE_UCCAGE, "unitcell", (short) (value ? -2
        : 0));
    global.setParameterValue("showUnitCell", value);
  }

  public boolean getShowUnitCell() {
    return getObjectMad(StateManager.OBJ_UNITCELL) != 0;
  }

  @Override
  public void setShowAxes(boolean value) {
    setObjectMad(JmolConstants.SHAPE_AXES, "axes", (short) (value ? -2 : 0));
    global.setParameterValue("showAxes", value);
  }

  @Override
  public boolean getShowAxes() {
    return getObjectMad(StateManager.OBJ_AXIS1) != 0;
  }

  private boolean frankOn = true;

  @Override
  public void setFrankOn(boolean TF) {
    if (isPreviewOnly)
      TF = false;
    frankOn = TF;
    setObjectMad(JmolConstants.SHAPE_FRANK, "frank", (short) (TF ? 1 : 0));
  }

  public boolean getShowFrank() {
    if (isPreviewOnly || isApplet && creatingImage)
      return false;
    return (isSignedApplet && !isSignedAppletLocal || frankOn);
  }

  public boolean isSignedApplet() {
    return isSignedApplet;
  }

  @Override
  public void setShowMeasurements(boolean TF) {
    // setbooleanProperty
    global.setParameterValue("showMeasurements", TF);
    global.showMeasurements = TF;
  }

  @Override
  public boolean getShowMeasurements() {
    return global.showMeasurements;
  }

  public boolean getShowMeasurementLabels() {
    return global.measurementLabels;
  }

  public boolean getMeasureAllModelsFlag() {
    return global.measureAllModels;
  }

  public void setMeasureDistanceUnits(String units) {
    // stateManager
    // Eval
    global.setMeasureDistanceUnits(units);
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "reformatDistances", null);
  }

  public String getMeasureDistanceUnits() {
    return global.getMeasureDistanceUnits();
  }

  public boolean getUseNumberLocalization() {
    return global.useNumberLocalization;
  }

  public void setAppendNew(boolean value) {
    // Eval dataFrame
    global.appendNew = value;
  }

  public boolean getAppendNew() {
    return global.appendNew;
  }

  boolean getAutoFps() {
    return global.autoFps;
  }

  @Override
  public void setRasmolDefaults() {
    setDefaults("RasMol");
  }

  @Override
  public void setJmolDefaults() {
    setDefaults("Jmol");
  }

  private void setDefaults(String type) {
    if (type.equalsIgnoreCase("RasMol")) {
      stateManager.setRasMolDefaults();
      return;
    }
    stateManager.setJmolDefaults();
    setShapeSize(JmolConstants.SHAPE_BALLS, rd, getModelUndeletedAtomsBitSet(-1));
  }

  public boolean getZeroBasedXyzRasmol() {
    return global.zeroBasedXyzRasmol;
  }

  private void setAntialias(int mode, boolean TF) {
    switch (mode) {
    case 0: // display
      global.antialiasDisplay = TF;
      break;
    case 1: // translucent
      global.antialiasTranslucent = TF;
      break;
    case 2: // images
      global.antialiasImages = TF;
      return;
    }
    resizeImage(0, 0, false, false, true);
    // requestRepaintAndWait();
  }

  // //////////////////////////////////////////////////////////////
  // temp manager
  // //////////////////////////////////////////////////////////////

  public Point3f[] allocTempPoints(int size) {
    // rockets renderer
    return tempManager.allocTempPoints(size);
  }

  public void freeTempPoints(Point3f[] tempPoints) {
    tempManager.freeTempPoints(tempPoints);
  }

  public Point3i[] allocTempScreens(int size) {
    // mesh and mps
    return tempManager.allocTempScreens(size);
  }

  public void freeTempScreens(Point3i[] tempScreens) {
    tempManager.freeTempScreens(tempScreens);
  }

  public byte[] allocTempBytes(int size) {
    // mps renderer
    return tempManager.allocTempBytes(size);
  }

  public void freeTempBytes(byte[] tempBytes) {
    tempManager.freeTempBytes(tempBytes);
  }

  // //////////////////////////////////////////////////////////////
  // font stuff
  // //////////////////////////////////////////////////////////////
  public Font3D getFont3D(String fontFace, String fontStyle, float fontSize) {
    return g3d.getFont3D(fontFace, fontStyle, fontSize);
  }

  public String formatText(String text0) {
    int i;
    if ((i = text0.indexOf("@{")) < 0 && (i = text0.indexOf("%{")) < 0)
      return text0;

    // old style %{ now @{

    String text = text0;
    boolean isEscaped = (text.indexOf("\\") >= 0);
    if (isEscaped) {
      text = TextFormat.simpleReplace(text, "\\%", "\1");
      text = TextFormat.simpleReplace(text, "\\@", "\2");
      isEscaped = !text.equals(text0);
    }
    text = TextFormat.simpleReplace(text, "%{", "@{");
    String name;
    while ((i = text.indexOf("@{")) >= 0) {
      i++;
      int i0 = i + 1;
      int len = text.length();
      i = ScriptCompiler.ichMathTerminator(text, i, len);
      if (i >= len)
        return text;
      name = text.substring(i0, i);
      if (name.length() == 0)
        return text;
      Object v = evaluateExpression(name);
      if (v instanceof Point3f)
        v = Escape.escape((Point3f) v);
      text = text.substring(0, i0 - 2) + v.toString() + text.substring(i + 1);
    }
    if (isEscaped) {
      text = TextFormat.simpleReplace(text, "\2", "@");
      text = TextFormat.simpleReplace(text, "\1", "%");
    }
    return text;
  }

  // //////////////////////////////////////////////////////////////
  // Access to atom properties for clients
  // //////////////////////////////////////////////////////////////

  String getElementSymbol(int i) {
    return modelSet.getElementSymbol(i);
  }

  int getElementNumber(int i) {
    return modelSet.getElementNumber(i);
  }

  @Override
  public String getAtomName(int i) {
    return modelSet.getAtomName(i);
  }

  @Override
  public int getAtomNumber(int i) {
    return modelSet.getAtomNumber(i);
  }

  public Quaternion[] getAtomGroupQuaternions(BitSet bsAtoms, int nMax) {
    return modelSet
        .getAtomGroupQuaternions(bsAtoms, nMax, getQuaternionFrame());
  }

  public Quaternion getAtomQuaternion(int i) {
    return modelSet.getQuaternion(i, getQuaternionFrame());
  }

  @Override
  public Point3f getAtomPoint3f(int i) {
    return modelSet.atoms[i];
  }

  public List<Point3f> getAtomPointVector(BitSet bs) {
    return modelSet.getAtomPointVector(bs);
  }

  @Override
  public float getAtomRadius(int i) {
    return modelSet.getAtomRadius(i);
  }

  @Override
  public int getAtomArgb(int i) {
    return g3d.getColorArgbOrGray(modelSet.getAtomColix(i));
  }

  String getAtomChain(int i) {
    return modelSet.getAtomChain(i);
  }

  @Override
  public int getAtomModelIndex(int i) {
    return modelSet.getAtomModelIndex(i);
  }

  String getAtomSequenceCode(int i) {
    return modelSet.getAtomSequenceCode(i);
  }

  @Override
  public float getBondRadius(int i) {
    return modelSet.getBondRadius(i);
  }

  @Override
  public int getBondOrder(int i) {
    return modelSet.getBondOrder(i);
  }

  public void assignAromaticBonds() {
    modelSet.assignAromaticBonds();
  }

  public boolean getSmartAromatic() {
    return global.smartAromatic;
  }

  public void resetAromatic() {
    modelSet.resetAromatic();
  }

  @Override
  public int getBondArgb1(int i) {
    return g3d.getColorArgbOrGray(modelSet.getBondColix1(i));
  }

  @Override
  public int getBondModelIndex(int i) {
    // legacy
    return modelSet.getBondModelIndex(i);
  }

  @Override
  public int getBondArgb2(int i) {
    return g3d.getColorArgbOrGray(modelSet.getBondColix2(i));
  }

  @Override
  public Point3f[] getPolymerLeadMidPoints(int modelIndex, int polymerIndex) {
    return modelSet.getPolymerLeadMidPoints(modelIndex, polymerIndex);
  }

  // //////////////////////////////////////////////////////////////
  // stereo support
  // //////////////////////////////////////////////////////////////

  public void setStereoMode(int[] twoColors, int stereoMode, float degrees) {
    setFloatProperty("stereoDegrees", degrees);
    setBooleanProperty("greyscaleRendering",
        stereoMode > JmolConstants.STEREO_DOUBLE);
    if (twoColors != null)
      transformManager.setStereoMode(twoColors);
    else
      transformManager.setStereoMode(stereoMode);
  }

  boolean isStereoDouble() {
    return transformManager.stereoMode == JmolConstants.STEREO_DOUBLE;
  }

  // //////////////////////////////////////////////////////////////
  //
  // //////////////////////////////////////////////////////////////

  @Override
  public String getOperatingSystemName() {
    return strOSName;
  }

  @Override
  public String getJavaVendor() {
    return strJavaVendor;
  }

  @Override
  public String getJavaVersion() {
    return strJavaVersion;
  }

  public Graphics3D getGraphics3D() {
    return g3d;
  }

  @Override
  public boolean showModelSetDownload() {
    return true; // deprecated
  }

  // /////////////// getProperty /////////////

  public Object getProperty(String returnType, String infoType, String paramInfo) {
    return getProperty(returnType, infoType, (Object) paramInfo);
  }

  private boolean scriptEditorVisible;

  public boolean isScriptEditorVisible() {
    return scriptEditorVisible;
  }

  JmolAppConsoleInterface appConsole;
  JmolScriptEditorInterface scriptEditor;
  JmolPopup jmolpopup;
  
  private JmolModelKitInterface modelkit;
  
  @Override
  public Object getProperty(String returnType, String infoType, Object paramInfo) {
    // accepts a BitSet paramInfo
    // return types include "JSON", "String", "readable", and anything else
    // returns the Java object.
    // Jmol 11.7.45 also uses this method as a general API
    // for getting and returning script data from the console and editor

    if (!"DATA_API".equals(returnType))
      return PropertyManager.getProperty(this, returnType, infoType, paramInfo);

    switch (
         ("scriptCheck........." // 0
        + "scriptContext......." // 20
        + "scriptEditor........" // 40
        + "scriptEditorState..." // 60
        + "getAppConsole......." // 80
        + "getScriptEditor....." // 100
        + "setMenu............." // 120
        + "spaceGroupInfo......" // 140
        + "disablePopupMenu...." // 160
        + "defaultDirectory...." // 180
        + "getPopupMenu........" // 200
    ).indexOf(infoType)) {

    case 0:
      return scriptCheck((String) paramInfo, true);
    case 20:
      return eval.getScriptContext();
    case 40:
      showEditor((String[]) paramInfo);
      return null;
    case 60:
      scriptEditorVisible = ((Boolean) paramInfo).booleanValue();
      return null;
    case 80:
      if (paramInfo instanceof JmolAppConsoleInterface) {
        appConsole = (JmolAppConsoleInterface) paramInfo;
      } else if (paramInfo != null && !((Boolean) paramInfo).booleanValue()) {
        appConsole = null;
      } else if (appConsole == null && paramInfo != null
          && ((Boolean) paramInfo).booleanValue()) {
        for (int i = 0; i < 4 && appConsole == null; i++) {
          appConsole = (isApplet ? (JmolAppConsoleInterface) Interface
              .getOptionInterface("applet.AppletConsole")
              : (JmolAppConsoleInterface) Interface
                  .getApplicationInterface("jmolpanel.AppConsole"))
              .getAppConsole(this);
          if (appConsole == null)
            try {
              Thread.currentThread().wait(100);
            } catch (InterruptedException e) {
              //
            }
        }
      }
      scriptEditor = (appConsole == null ? null : appConsole.getScriptEditor());
      return appConsole;
    case 100:
      if (appConsole == null && paramInfo != null
          && ((Boolean) paramInfo).booleanValue()) {
        getProperty("DATA_API", "getAppConsole", Boolean.TRUE);
        scriptEditor = (appConsole == null ? null : appConsole
            .getScriptEditor());
      }
      return scriptEditor;
    case 120:
      return menuStructure = (String) paramInfo;
    case 140:
      return getSpaceGroupInfo(null);
    case 160:
      global.disablePopupMenu = true; // no false here, because it's a
      // one-time setting
      return null;
    case 180:
      return global.defaultDirectory;
    case 200:
      if (paramInfo instanceof String)
        return getMenu((String) paramInfo);
      return getPopupMenu();
    default:
      Logger.error("ERROR in getProperty DATA_API: " + infoType);
      return null;
    }
  }

  void showEditor(String[] file_text) {
    if (file_text == null)
      file_text = new String[] { null, null };
    if (file_text[1] == null)
      file_text[1] = "<no data>";
    String filename = file_text[0];
    String msg = file_text[1];
    JmolScriptEditorInterface scriptEditor = (JmolScriptEditorInterface) getProperty(
        "DATA_API", "getScriptEditor", Boolean.TRUE);
    if (scriptEditor == null)
      return;
    if (msg != null) {
      scriptEditor.setFilename(filename);
      scriptEditor.output(ScriptCompiler.getEmbeddedScript(msg));
    }
    scriptEditor.setVisible(true);
  }

  public String getModelExtract(Object atomExpression, boolean doTransform, String type) {
    return modelSet.getModelExtract(getAtomBitSet(atomExpression), doTransform, false, type);
  }

  // ////////////////////////////////////////////////

  boolean isTainted = true;

  public void setTainted(boolean TF) {
    isTainted = axesAreTainted = (TF && (refreshing || creatingImage));
  }

  public int notifyMouseClicked(int x, int y, int action, int mode) {
    // change y to 0 at bottom
    int modifiers = Binding.getModifiers(action);
    int clickCount = Binding.getClickCount(action);
    //System.out.println(action + " " + clickCount + " " + modifiers + " " + mode);
    global.setParameterValue("_mouseX", x);
    global.setParameterValue("_mouseY", dimScreen.height - y);
    global.setParameterValue("_mouseAction", action);
    global.setParameterValue("_mouseModifiers", modifiers);
    global.setParameterValue("_clickCount", clickCount);
    return statusManager.setStatusClicked(x, dimScreen.height - y, action,
        clickCount, mode);
  }

  Token checkObjectClicked(int x, int y, int modifiers) {
    return shapeManager.checkObjectClicked(x, y, modifiers,
        getVisibleFramesBitSet());
  }

  boolean checkObjectHovered(int x, int y) {
    return (shapeManager != null && shapeManager.checkObjectHovered(x, y,
        getVisibleFramesBitSet(), getBondPicking()));
  }

  void checkObjectDragged(int prevX, int prevY, int x, int y, int action) {
    int iShape = 0;
    switch (getPickingMode()) {
    case ActionManager.PICKING_LABEL:
      iShape = JmolConstants.SHAPE_LABELS;
      break;
    case ActionManager.PICKING_DRAW:
      iShape = JmolConstants.SHAPE_DRAW;
      break;
    }
    if (shapeManager.checkObjectDragged(prevX, prevY, x, y, action,
        getVisibleFramesBitSet(), iShape))
      refresh(1, "checkObjectDragged");

    // TODO: refresh 1 or 2?
  }

  public void rotateAxisAngleAtCenter(Point3f rotCenter, Vector3f rotAxis,
                                      float degreesPerSecond, float endDegrees,
                                      boolean isSpin, BitSet bsSelected) {
    // Eval: rotate FIXED
    if (Float.isNaN(degreesPerSecond) || degreesPerSecond == 0
        || endDegrees == 0)
      return;
    transformManager.rotateAxisAngleAtCenter(rotCenter, rotAxis,
        degreesPerSecond, endDegrees, isSpin, bsSelected);
    refresh(-1, "rotateAxisAngleAtCenter");
    if (bsSelected != null && !isSpin)
      checkMinimization();
  }

  public void rotateAboutPointsInternal(Point3f point1, Point3f point2,
                                        float degreesPerSecond,
                                        float endDegrees, boolean isSpin,
                                        BitSet bsSelected,
                                        Vector3f translation, List<Point3f> finalPoints) {
    // Eval: rotate INTERNAL
    if ((translation == null || translation.length() < 0.001)
        && (!isSpin || endDegrees == 0 || Float.isNaN(degreesPerSecond) || degreesPerSecond == 0)
        && (isSpin || endDegrees == 0))
      return;
    // System.out.println("viewer " + endDegrees + "\npoints " +
    // Escape.escape(point1) + " " + Escape.escape(point2) + "\ntrans:" +
    // translation);
    transformManager.rotateAboutPointsInternal(point1, point2,
        degreesPerSecond, endDegrees, false, isSpin, bsSelected, false,
        translation, finalPoints);
    refresh(-1, "rotateAxisAboutPointsInternal");
    if (bsSelected != null && !isSpin)
      checkMinimization();
  }

  int getPickingSpinRate() {
    // actionManager
    return global.pickingSpinRate;
  }

  public void startSpinningAxis(Point3f pt1, Point3f pt2, boolean isClockwise) {
    // Draw.checkObjectClicked ** could be difficult
    // from draw object click
    if (getSpinOn() || getNavOn()) {
      setSpinOn(false);
      setNavOn(false);
      return;
    }
    transformManager.rotateAboutPointsInternal(pt1, pt2,
        global.pickingSpinRate, Float.MAX_VALUE, isClockwise, true, null,
        false, null, null);
  }

  public Vector3f getModelDipole() {
    return modelSet.getModelDipole(animationManager.currentModelIndex);
  }

  public Vector3f calculateMolecularDipole() {
    return modelSet
        .calculateMolecularDipole(animationManager.currentModelIndex);
  }

  public float getDipoleScale() {
    return global.dipoleScale;
  }

  public void getAtomIdentityInfo(int atomIndex, Map<String, Object> info) {
    modelSet.getAtomIdentityInfo(atomIndex, info);
  }

  public void setDefaultLattice(Point3f ptLattice) {
    // Eval -- handled separately
    global.setDefaultLattice(ptLattice);
    global.setParameterValue("defaultLattice", Escape.escape(ptLattice));
  }

  public Point3f getDefaultLattice() {
    return global.getDefaultLattice();
  }

  public BitSet getTaintedAtoms(byte type) {
    return modelSet.getTaintedAtoms(type);
  }

  public void setTaintedAtoms(BitSet bs, byte type) {
    modelSet.setTaintedAtoms(bs, type);
  }

  @Override
  public String getData(String atomExpression, String type) {
    String exp = "";
    if (type.equalsIgnoreCase("MOL") || type.equalsIgnoreCase("SDF") 
        || type.equalsIgnoreCase("V2000") || type.equalsIgnoreCase("V3000") || type.equalsIgnoreCase("XYZVIB"))
      return getModelExtract(atomExpression, false, type);
    if (type.toLowerCase().indexOf("property_") == 0)
      exp = "{selected}.label(\"%{" + type + "}\")";
    else if (type.equalsIgnoreCase("CML"))
      return getModelCml(getAtomBitSet(atomExpression), Integer.MAX_VALUE, true);
    else if (type.equalsIgnoreCase("PDB"))
      // old crude
      exp = "{selected and not hetero}.label(\"ATOM  %5i %-4a%1A%3.3n %1c%4R%1E   %8.3x%8.3y%8.3z%6.2Q%6.2b          %2e  \").lines"
          + "+{selected and hetero}.label(\"HETATM%5i %-4a%1A%3.3n %1c%4R%1E   %8.3x%8.3y%8.3z%6.2Q%6.2b          %2e  \").lines";
    else if (type.equalsIgnoreCase("XYZRN"))
      exp = "\"\" + {selected}.size + \"\n\n\"+{selected}.label(\"%-2e %8.3x %8.3y %8.3z %4.2[vdw] 1 [%n]%r.%a#%i\").lines";
    else if (type.startsWith("USER:"))
      exp = "{selected}.label(\"" + type.substring(5) + "\").lines";
    else // if(type.equals("XYZ"))
      exp = "\"\" + {selected}.size + \"\n\n\"+{selected}.label(\"%-2e %10.5x %10.5y %10.5z\").lines";
    if (!atomExpression.equals("selected"))
      exp = TextFormat.simpleReplace(exp, "selected", atomExpression);
    return (String) evaluateExpression(exp);
  }

  public String getModelCml(BitSet bs, int nAtomsMax, boolean addBonds) {
    return modelSet.getModelCml(bs, nAtomsMax, addBonds);
  }

  // synchronized here trapped the eventQueue
  public Object evaluateExpression(Object stringOrTokens) {
    return ScriptEvaluator.evaluateExpression(this, stringOrTokens);
  }

  public Object getHelixData(BitSet bs, int tokType) {
    return modelSet.getHelixData(bs, tokType);
  }

  public String getPdbData(BitSet bs, OutputStringBuffer sb) {
    if (bs == null)
      bs = getSelectionSet(true);
    return modelSet.getPdbAtomData(bs, sb);
  }

  public boolean isJmolDataFrame(int modelIndex) {
    return modelSet.isJmolDataFrame(modelIndex);
  }

  public boolean isJmolDataFrame() {
    return modelSet.isJmolDataFrame(animationManager.currentModelIndex);
  }

  public int getJmolDataFrameIndex(int modelIndex, String type) {
    return modelSet.getJmolDataFrameIndex(modelIndex, type);
  }

  public void setJmolDataFrame(String type, int modelIndex, int dataIndex) {
    modelSet.setJmolDataFrame(type, modelIndex, dataIndex);
  }

  public void setFrameTitle(int modelIndex, String title) {
    modelSet.setFrameTitle(BitSetUtil.setBit(modelIndex), title);
  }

  public void setFrameTitle(Object title) {
    loadShape(JmolConstants.SHAPE_ECHO);
    modelSet.setFrameTitle(getVisibleFramesBitSet(), title);
  }

  public String getFrameTitle() {
    return modelSet.getFrameTitle(animationManager.currentModelIndex);
  }

  String getJmolFrameType(int modelIndex) {
    return modelSet.getJmolFrameType(modelIndex);
  }

  public int getJmolDataSourceFrame(int modelIndex) {
    return modelSet.getJmolDataSourceFrame(modelIndex);
  }

  public void setAtomProperty(BitSet bs, int tok, int iValue, float fValue,
                              String sValue, float[] values, String[] list) {
    clearMinimization();
    modelSet.setAtomProperty(bs, tok, iValue, fValue, sValue, values, list);
    switch (tok) {
    case Token.atomx:
    case Token.atomy:
    case Token.atomz:
    case Token.fracx:
    case Token.fracy:
    case Token.fracz:
    case Token.unitx:
    case Token.unity:
    case Token.unitz:
    case Token.element:
      refreshMeasures(true);
    }
  }

  public void checkCoordinatesChanged() {
    modelSet.recalculatePositionDependentQuantities(null);
    refreshMeasures(true);
  }

  public void setAtomCoord(int atomIndex, float x, float y, float z) {
    // Frame equivalent used in DATA "coord set"
    modelSet.setAtomCoord(atomIndex, x, y, z);
    // no measure refresh here -- because it may involve hundreds of calls
  }

  public void setAtomCoord(BitSet bs, int tokType, Object xyzValues) {
    modelSet.setAtomCoord(bs, tokType, xyzValues);
    refreshMeasures(true);
  }

  public void setAtomCoordRelative(int atomIndex, float x, float y, float z) {
    modelSet.setAtomCoordRelative(atomIndex, x, y, z);
    // no measure refresh here -- because it may involve hundreds of calls
  }

  public void setAtomCoordRelative(Tuple3f offset, BitSet bs) {
    // Eval
    modelSet.setAtomCoordRelative(offset, bs == null ? getSelectionSet(false) : bs);
    refreshMeasures(true);
  }

  boolean allowRotateSelected() {
    return global.allowRotateSelected;
  }

  public void invertAtomCoord(Point3f pt, BitSet bs) {
    // Eval
    modelSet.invertSelected(pt, null, -1, null, bs);
    refreshMeasures(true);
    checkMinimization();
  }

  public void invertAtomCoord(Point4f plane, BitSet bs) {
    modelSet.invertSelected(null, plane, -1, null, bs);
    refreshMeasures(true);
    checkMinimization();
  }

  public void invertSelected(Point3f pt, Point4f plane, int iAtom,
                             BitSet invAtoms) {
    // Eval
    modelSet.invertSelected(pt, plane, iAtom, invAtoms, getSelectionSet(false));
    refreshMeasures(true);
    checkMinimization();
  }

  private boolean movingSelected;
  private boolean showSelected;

  public void moveSelected(int deltaX, int deltaY, int x, int y,
                           BitSet bsSelected, boolean isTranslation) {
    // cannot synchronize this -- it's from the mouse and the event queue
    if (x == Integer.MIN_VALUE)
      rotateBondIndex = -1;
    if (isJmolDataFrame())
      return;
    if (bsSelected == null)
      bsSelected = getSelectionSet(false);
    bsSelected.andNot(selectionManager.getMotionFixedAtoms());
    if (deltaX == Integer.MIN_VALUE) {
      showSelected = true;
      loadShape(JmolConstants.SHAPE_HALOS);
      refresh(6, "moveSelected");
      return;
    }
    if (deltaX == Integer.MAX_VALUE) {
      showSelected = false;
      refresh(6, "moveSelected");
      return;
    }
    if (movingSelected)
      return;
    movingSelected = true;
    // note this does not sync with applets
    if (rotateBondIndex >= 0 && x != Integer.MIN_VALUE) {
      actionRotateBond(deltaX, deltaY, x, y);
    } else if (isTranslation) {
      Point3f ptCenter = getAtomSetCenter(bsSelected);
      Point3i ptScreen = transformPoint(ptCenter);
      Point3f ptScreenNew = new Point3f(ptScreen.x + deltaX + 0.5f, ptScreen.y
          + deltaY + 0.5f, ptScreen.z);
      Point3f ptNew = new Point3f();
      transformManager.finalizeTransformParameters();
      unTransformPoint(ptScreenNew, ptNew);
      // script("draw ID 'pt" + Math.random() + "' " + Escape.escape(ptNew));
      ptNew.sub(ptCenter);
      modelSet.setAtomCoordRelative(ptNew, bsSelected);
    } else {
      transformManager.setRotateMolecule(true);
      transformManager.rotateXYBy(deltaX, deltaY, bsSelected);
      transformManager.setRotateMolecule(false);
    }
    refresh(2, ""); // should be syncing here
    refreshMeasures(true);
    checkMinimization();
    movingSelected = false;
  }

  public void highlightBond(int index, boolean isHover) {
    if (isHover && !isHoverEnabled())
      return;
    BitSet bs = null;
    if (index >= 0) {
      Bond b = modelSet.getBonds()[index];
      int i = b.getAtomIndex2();
      if (!isAtomAssignable(i))
        return;
      bs = BitSetUtil.setBit(i);
      bs.set(b.getAtomIndex1());
    }
    highlight(bs);
    refresh(3, "highlightBond");
  }

  public void highlight(BitSet bs) {
    if (bs != null)
      loadShape(JmolConstants.SHAPE_HALOS);
    setShapeProperty(JmolConstants.SHAPE_HALOS, "highlight", bs);
  }

  private int rotateBondIndex = -1;

  void setRotateBondIndex(int index) {
    boolean haveBond = (rotateBondIndex >= 0);
    if (!haveBond && index < 0)
      return;
    rotatePrev1 = -1;
    bsRotateBranch = null;
    if (index == Integer.MIN_VALUE)
      return;
    rotateBondIndex = index;
    highlightBond(index, false);

  }

  int getRotateBondIndex() {
    return rotateBondIndex;
  }

  private int rotatePrev1 = -1;
  private int rotatePrev2 = -1;
  private BitSet bsRotateBranch;

  void actionRotateBond(int deltaX, int deltaY, int x, int y) {
    if (rotateBondIndex < 0)
      return;
    BitSet bsBranch = bsRotateBranch;
    Atom atom1, atom2;
    if (bsBranch == null) {
      Bond b = modelSet.getBonds()[rotateBondIndex];
      atom1 = b.getAtom1();
      atom2 = b.getAtom2();
      undoAction(true, atom1.index, AtomCollection.TAINT_COORD);
      Point3f pt = new Point3f(x, y, (atom1.screenZ + atom2.screenZ) / 2);
      transformManager.unTransformPoint(pt, pt);
      if (atom2.getCovalentBondCount() == 1
          || pt.distance(atom1) < pt.distance(atom2)
          && atom1.getCovalentBondCount() != 1) {
        Atom a = atom1;
        atom1 = atom2;
        atom2 = a;
      }
      if (Measure.computeAngle(pt, atom1, atom2, true) > 90
          || Measure.computeAngle(pt, atom2, atom1, true) > 90) {
        bsBranch = getBranchBitSet(atom2.index, atom1.index);
      }
      if (bsBranch != null)
        for (int n = 0, i = atom1.getBonds().length; --i >= 0;) {
          if (bsBranch.get(atom1.getBondedAtomIndex(i)) && ++n == 2) {
            bsBranch = null;
            break;
          }
        }
      if (bsBranch == null) {
        bsBranch = getMoleculeBitSet(atom1.index);
      }
      bsRotateBranch = bsBranch;
      rotatePrev1 = atom1.index;
      rotatePrev2 = atom2.index;
    } else {
      atom1 = modelSet.atoms[rotatePrev1];
      atom2 = modelSet.atoms[rotatePrev2];
    }
    Vector3f v1 = new Vector3f(atom2.screenX - atom1.screenX, atom2.screenY
        - atom1.screenY, 0);
    Vector3f v2 = new Vector3f(deltaX, deltaY, 0);
    v1.cross(v1, v2);
    float degrees = (v1.z > 0 ? 1 : -1) * v2.length();
   
    BitSet bs = BitSetUtil.copy(bsBranch);
    bs.andNot(selectionManager.getMotionFixedAtoms());

    rotateAboutPointsInternal(atom1, atom2, 0, degrees, false, bs,
        null, null);
  }

  void rotateAtoms(Matrix3f mNew, Matrix3f matrixRotate, boolean fullMolecule,
                   Point3f center, boolean isInternal, BitSet bsAtoms) {
    // from TransformManager exclusively
    modelSet.rotateAtoms(mNew, matrixRotate, bsAtoms, fullMolecule, center,
        isInternal);
    refreshMeasures(true);
    checkMinimization();
  }

  public void refreshMeasures(boolean andStopMinimization) {
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "refresh", null);
    if (andStopMinimization)
      stopMinimization();
  }

  void setDynamicMeasurements(boolean TF) { // deprecated; unnecessary
    global.dynamicMeasurements = TF;
  }

  public boolean getDynamicMeasurements() {
    return global.dynamicMeasurements;
  }

  /**
   * fills an array with data -- if nX < 0 and this would involve JavaScript,
   * then this reads a full set of Double[][] in one function call. Otherwise it
   * reads the values using individual function calls, which each return Double.
   * 
   * If the functionName begins with "file:" then data are read from a file
   * specified after the colon. The sign of nX is not relevant in that case. The
   * file may contain mixed numeric and non-numeric values; the non-numeric
   * values will be skipped by Parser.parseFloatArray
   * 
   * @param functionName
   * @param nX
   * @param nY
   * @return nX by nY array of floating values
   */
  public float[][] functionXY(String functionName, int nX, int nY) {
    String data = null;
    if (functionName.indexOf("file:") == 0)
      data = getFileAsString(functionName.substring(5));
    else if (functionName.indexOf("data2d_") != 0)
      return statusManager.functionXY(functionName, nX, nY);
    nX = Math.abs(nX);
    nY = Math.abs(nY);
    float[][] fdata;
    if (data == null) {
      fdata = getDataFloat2D(functionName);
      if (fdata != null)
        return fdata;
      data = "";
    }
    fdata = new float[nX][nY];
    float[] f = new float[nX * nY];
    Parser.parseStringInfestedFloatArray(data, null, f);
    for (int i = 0, n = 0; i < nX; i++)
      for (int j = 0; j < nY; j++)
        fdata[i][j] = f[n++];
    return fdata;
  }

  public float[][][] functionXYZ(String functionName, int nX, int nY, int nZ) {
    String data = null;
    if (functionName.indexOf("file:") == 0)
      data = getFileAsString(functionName.substring(5));
    else if (functionName.indexOf("data3d_") != 0)
      return statusManager.functionXYZ(functionName, nX, nY, nZ);
    nX = Math.abs(nX);
    nY = Math.abs(nY);
    nZ = Math.abs(nZ);
    float[][][] xyzdata;
    if (data == null) {
      xyzdata = getDataFloat3D(functionName);
      if (xyzdata != null)
        return xyzdata;
      data = "";
    }
    xyzdata = new float[nX][nY][nZ];
    float[] f = new float[nX * nY * nZ];
    Parser.parseStringInfestedFloatArray(data, null, f);
    for (int i = 0, n = 0; i < nX; i++)
      for (int j = 0; j < nY; j++)
        for (int k = 0; k < nZ; k++)
          xyzdata[i][j][k] = f[n++];
    return xyzdata;
  }

  public void getHelp(String what) {
    if (what.length() > 0 && what.indexOf("?") != 0
        && global.helpPath.indexOf("?") < 0)
      what = "?search=" + what;
    showUrl(global.helpPath + what);
  }

  // ///////////////////////////////////////////////////////////////
  // delegated to stateManager
  // ///////////////////////////////////////////////////////////////

  /*
   * Moved from the consoles to viewer, since this could be of general interest,
   * it's more a property of Eval/Viewer, and the consoles are really just a
   * mechanism for getting user input and sending results, not saving a history
   * of it all. Ultimately I hope to integrate the mouse picking and possibly
   * periodic updates of position into this history to get a full history. We'll
   * see! BH 9/2006
   */

  /**
   * Adds one or more commands to the command history
   * 
   * @param command
   *          the command to add
   */
  public void addCommand(String command) {
    if (autoExit || !haveDisplay || !getPreserveState())
      return;
    commandHistory.addCommand(TextFormat.replaceAllCharacters(command,
        "\r\n\t", " "));
  }

  /**
   * Removes one command from the command history
   * 
   * @return command removed
   */
  public String removeCommand() {
    return commandHistory.removeCommand();
  }

  /**
   * Options include: ; all n == Integer.MAX_VALUE ; n prev n >= 1 ; next n ==
   * -1 ; set max to -2 - n n <= -3 ; just clear n == -2 ; clear and turn off;
   * return "" n == 0 ; clear and turn on; return "" n == Integer.MIN_VALUE;
   * 
   * @param howFarBack
   *          number of lines (-1 for next line)
   * @return one or more lines of command history
   */
  @Override
  public String getSetHistory(int howFarBack) {
    return commandHistory.getSetHistory(howFarBack);
  }

  // ///////////////////////////////////////////////////////////////
  // image and file export
  // ///////////////////////////////////////////////////////////////

  @Override
  public void writeTextFile(String fileName, String data) {
    createImage(fileName, "txt", data, Integer.MIN_VALUE, 0, 0);
  }

  /**
   * 
   * @param text
   *          null here clips image; String clips text
   * @return "OK" for image or "OK [number of bytes]"
   */
  @Override
  public String clipImage(String text) {
    JmolImageCreatorInterface c;
    try {
      c = (JmolImageCreatorInterface) Interface
          .getOptionInterface("export.image.ImageCreator");
      c.setViewer(this, privateKey);
      return c.clipImage(text);
    } catch (Error er) {
      // unsigned applet will not have this interface
      return GT._("clipboard is not accessible -- use signed applet");
    }
  }

  /**
   * 
   * from eval write command only includes option to write set of files
   * 
   * @param fileName
   * @param type
   * @param text_or_bytes
   * @param quality
   * @param width
   * @param height
   * @param bsFrames
   * @param fullPath
   * @return message starting with "OK" or an error message
   */
  public String createImage(String fileName, String type, Object text_or_bytes,
                            int quality, int width, int height,
                            BitSet bsFrames, String[] fullPath) {
    if (bsFrames == null)
      return (String) createImage(fileName, type, text_or_bytes, quality,
          width, height, fullPath);
    String info = "";
    int n = 0;
    fileName = getFileNameFromDialog(fileName, quality);
    if (fullPath != null)
      fullPath[0] = fileName;
    if (fileName == null) 
      return null;
    int ptDot = fileName.indexOf(".");
    if (ptDot < 0)
      ptDot = fileName.length();

    String froot = fileName.substring(0, ptDot);
    String fext = fileName.substring(ptDot);
    for (int i = bsFrames.nextSetBit(0); i >= 0; i = bsFrames.nextSetBit(i + 1)) {
      setCurrentModelIndex(i);
      fileName = "0000" + (++n);
      fileName = froot + fileName.substring(fileName.length() - 4) + fext;
      if (fullPath != null)
        fullPath[0] = fileName;
      String msg = (String) createImage(fileName, type, text_or_bytes, quality,
          width, height, null, false);
      Logger.info(msg);
      info += msg + "\n";
      if (!msg.startsWith("OK"))
        return "ERROR WRITING FILE SET: \n" + info;
    }
    if (info.length() == 0)
      info = "OK\n";
    return info + "\n" + n + " files created";
  }

  private boolean creatingImage;

  @Override
  public Object createImage(String fileName, String type, Object text_or_bytes,
                            int quality, int width, int height) {
    return createImage(fileName, type, text_or_bytes, quality, width, height,
        null, true);
  }

  /**
   * general routine for creating an image or writing data to a file
   * 
   * passes request to statusManager to pass along to app or applet
   * jmolStatusListener interface
   * 
   * @param fileName
   *          starts with ? --> use file dialog; null --> to clipboard
   * @param type
   *          PNG, JPG, etc.
   * @param text_or_bytes
   *          String or byte[] or null if an image
   * @param quality
   *          Integer.MIN_VALUE --> not an image
   * @param width
   *          image width
   * @param height
   *          image height
   * @param fullPath
   * @return null (canceled) or a message starting with OK or an error message
   */
  public Object createImage(String fileName, String type, Object text_or_bytes,
                            int quality, int width, int height,
                            String[] fullPath) {
    return createImage(fileName, type, text_or_bytes, quality, width, height, fullPath, true);
  }
  
  private Object createImage(String fileName, String type, Object text_or_bytes,
                            int quality, int width, int height,
                            String[] fullPath, boolean doCheck) {


    /*
     * 
     * org.jmol.export.image.AviCreator does create AVI animations from Jpegs
     * but these aren't read by standard readers, so that's pretty much useless.
     * 
     * files must have the designated width and height
     * 
     * text_or_bytes: new Object[] { (File[]) files, (String) outputFilename,
     * (int[]) params }
     * 
     * where for now we just read param[0] as frames per second
     * 
     * 
     * Note: this method is the gateway to all file writing for the applet.
     */

    int saveWidth = dimScreen.width;
    int saveHeight = dimScreen.height;
    creatingImage = true;
    if (quality != Integer.MIN_VALUE) {
      mustRender = true;
      resizeImage(width, height, true, false, false);
      setModelVisibility();
    }
    Object err = null;

    try {
      if (fileName == null) {
        err = clipImage((String) text_or_bytes);
      } else {
        if (doCheck)
          fileName = getFileNameFromDialog(fileName, quality);
        if (fullPath != null)
          fullPath[0] = fileName;
        if (fileName == null) {
          err = "CANCELED";
        } else if (type.equals("ZIP") || type.equals("ZIPALL")) {
          err = fileManager.createZipSet(fileName, (String) text_or_bytes, type
              .equals("ZIPALL"));
        } else {
          // see if application wants to do it (returns non-null String)
          // both Jmol application and applet return null
          if (!type.equals("OutputStream"))
            err = statusManager.createImage(fileName, type, text_or_bytes,
                quality);
          if (err == null) {
            // application can do it itself or allow Jmol to do it here
            JmolImageCreatorInterface c = (JmolImageCreatorInterface) Interface
                .getOptionInterface("export.image.ImageCreator");
            c.setViewer(this, privateKey);
            err = c.createImage(fileName, type, text_or_bytes, quality);
            if (err instanceof String)
              // report error status (text_or_bytes == null)
              statusManager.createImage((String) err, type, null, quality);
          }
        }
      }
    } catch (Throwable er) {
      Logger.error(setErrorMessage((String) (err = "ERROR creating image: "
          + er)));
    }
    creatingImage = false;
    if (quality != Integer.MIN_VALUE) {
      resizeImage(saveWidth, saveHeight, true, false, true);
    }
    return ("CANCELED".equals(err) ? null : err);
  }

  private String getFileNameFromDialog(String fileName, int quality) {
    if (fileName == null)
      return null;
    boolean useDialog = (fileName.indexOf("?") == 0);
    if (useDialog)
      fileName = fileName.substring(1);
    useDialog |= isApplet;
    fileName = FileManager.getLocalPathForWritingFile(this, fileName);
    if (useDialog)
      fileName = dialogAsk(quality == Integer.MIN_VALUE ? "save" : "saveImage",
          fileName);
    return fileName;
  }

  private void setSyncTarget(int mode, boolean TF) {
    switch (mode) {
    case 0:
      statusManager.syncingMouse = TF;
      break;
    case 1:
      statusManager.syncingScripts = TF;
      break;
    case 2:
      statusManager.syncSend(TF ? SYNC_GRAPHICS_MESSAGE
          : SYNC_NO_GRAPHICS_MESSAGE, "*");
      if (Float.isNaN(transformManager.stereoDegrees))
        setFloatProperty("stereoDegrees", JmolConstants.DEFAULT_STEREO_DEGREES);
      if (TF) {
        setBooleanProperty("_syncMouse", false);
        setBooleanProperty("_syncScript", false);
      }
      return;
    }
    // if turning both off, sync the orientation now
    if (!statusManager.syncingScripts && !statusManager.syncingMouse)
      refresh(-1, "set sync");
  }

  public final static String SYNC_GRAPHICS_MESSAGE = "GET_GRAPHICS";
  public final static String SYNC_NO_GRAPHICS_MESSAGE = "SET_GRAPHICS_OFF";

  @Override
  public void syncScript(String script, String applet) {
    if (script.equalsIgnoreCase(SYNC_GRAPHICS_MESSAGE)) {
      statusManager.setSyncDriver(StatusManager.SYNC_STEREO);
      statusManager.syncSend(script, applet);
      setBooleanProperty("_syncMouse", false);
      setBooleanProperty("_syncScript", false);
      return;
    }
    // * : all applets
    // > : all OTHER applets
    // . : just me
    // ~ : disable send (just me)
    boolean disableSend = "~".equals(applet);
    // null same as ">" -- "all others"
    if (!disableSend && !".".equals(applet)) {
      statusManager.syncSend(script, applet);
      if (!"*".equals(applet))
        return;
    }
    if (script.equalsIgnoreCase("on") || script.equalsIgnoreCase("true")) {
      statusManager.setSyncDriver(StatusManager.SYNC_DRIVER);
      return;
    }
    if (script.equalsIgnoreCase("off") || script.equalsIgnoreCase("false")) {
      statusManager.setSyncDriver(StatusManager.SYNC_OFF);
      return;
    }
    if (script.equalsIgnoreCase("slave")) {
      statusManager.setSyncDriver(StatusManager.SYNC_SLAVE);
      return;
    }
    int syncMode = statusManager.getSyncMode();
    if (syncMode == StatusManager.SYNC_OFF)
      return;
    if (syncMode != StatusManager.SYNC_DRIVER)
      disableSend = false;
    if (Logger.debugging)
      Logger.debug(htmlName + " syncing with script: " + script);
    // driver is being positioned by another driver -- don't pass on the change
    // driver is being positioned by a mouse movement
    // format is from above refresh(2, xxx) calls
    // Mouse: [CommandName] [value1] [value2]
    if (disableSend)
      statusManager.setSyncDriver(StatusManager.SYNC_DISABLE);
    if (script.indexOf("Mouse: ") != 0) {
      evalStringQuiet(script, true, false);
      return;
    }
    String[] tokens = Parser.getTokens(script);
    String key = tokens[1];
    switch (tokens.length) {
    case 3:
      if (key.equals("zoomByFactor"))
        zoomByFactor(Parser.parseFloat(tokens[2]), Integer.MAX_VALUE,
            Integer.MAX_VALUE);
      else if (key.equals("zoomBy"))
        zoomBy(Parser.parseInt(tokens[2]));
      else if (key.equals("rotateZBy"))
        rotateZBy(Parser.parseInt(tokens[2]), Integer.MAX_VALUE,
            Integer.MAX_VALUE);
      break;
    case 4:
      if (key.equals("rotateXYBy"))
        rotateXYBy(Parser.parseFloat(tokens[2]), Parser.parseFloat(tokens[3]));
      else if (key.equals("translateXYBy"))
        translateXYBy(Parser.parseInt(tokens[2]), Parser.parseInt(tokens[3]));
      else if (key.equals("rotateMolecule"))
        rotateMolecule(Parser.parseFloat(tokens[2]), Parser
            .parseFloat(tokens[3]), null);
      break;
    case 5:
      if (key.equals("spinXYBy"))
        spinXYBy(Parser.parseInt(tokens[2]), Parser.parseInt(tokens[3]), Parser
            .parseFloat(tokens[4]));
      else if (key.equals("zoomByFactor"))
        zoomByFactor(Parser.parseFloat(tokens[2]), Parser.parseInt(tokens[3]),
            Parser.parseInt(tokens[4]));
      else if (key.equals("rotateZBy"))
        rotateZBy(Parser.parseInt(tokens[2]), Parser.parseInt(tokens[3]),
            Parser.parseInt(tokens[4]));
      else if (key.equals("rotateArcBall"))
        rotateArcBall(Parser.parseInt(tokens[2]), Parser.parseInt(tokens[3]),
            Parser.parseFloat(tokens[4]));
      break;
    case 7:
      if (key.equals("centerAt"))
        centerAt(Parser.parseInt(tokens[2]), Parser.parseInt(tokens[3]),
            new Point3f(Parser.parseFloat(tokens[4]), Parser
                .parseFloat(tokens[5]), Parser.parseFloat(tokens[6])));
    }
    if (disableSend)
      setSyncDriver(StatusManager.SYNC_ENABLE);
  }

  void setSyncDriver(int mode) {
    statusManager.setSyncDriver(mode);
  }

  public float[] getPartialCharges() {
    return modelSet.getPartialCharges();
  }

  /**
   * 
   * @param isMep
   * @param bsSelected
   * @param bsIgnore
   * @param fileName
   * @return calculated atom potentials
   */
  public float[] getAtomicPotentials(boolean isMep, BitSet bsSelected, BitSet bsIgnore, String fileName) {
    float[] potentials = new float[getAtomCount()];
    MepCalculationInterface m = (MepCalculationInterface) Interface.getOptionInterface("quantum.MlpCalculation");
    String data = (fileName == null ? null : getFileAsString(fileName));
    m.assignPotentials(modelSet.atoms, potentials, getSmartsMatch("a", bsSelected), 
        getSmartsMatch("/noAromatic/[$(C=O),$(O=C),$(NC=O)]", bsSelected), bsIgnore, data);
    return potentials;
  }

  public void setProteinType(byte iType, BitSet bs) {
    modelSet.setProteinType(bs == null ? getSelectionSet(false)
        : bs, iType);
  }

  /*
   * void debugStack(String msg) { //what's the right way to do this? try {
   * Logger.error(msg); String t = null; t.substring(3); } catch (Exception e) {
   * e.printStackTrace(); } }
   */

  @Override
  public Point3f getBondPoint3f1(int i) {
    // legacy -- no calls
    return modelSet.getBondAtom1(i);
  }

  @Override
  public Point3f getBondPoint3f2(int i) {
    // legacy -- no calls
    return modelSet.getBondAtom2(i);
  }

  public Vector3f getVibrationVector(int atomIndex) {
    return modelSet.getVibrationVector(atomIndex, false);
  }

  public int getVanderwaalsMar(int i) {
    return (dataManager.defaultVdw == JmolConstants.VDW_USER ? dataManager.userVdwMars[i]
        : JmolConstants.getVanderwaalsMar(i, dataManager.defaultVdw));
  }

  public int getVanderwaalsMar(int i, int iType) {
    switch (iType) {
    case JmolConstants.VDW_USER:
      if (dataManager.bsUserVdws == null)
        iType = dataManager.defaultVdw;
      else
        return dataManager.userVdwMars[i];
      break;
    case JmolConstants.VDW_UNKNOWN:
      iType = dataManager.defaultVdw;
      break;
    case JmolConstants.VDW_AUTO:
    case JmolConstants.VDW_AUTO_JMOL:
    case JmolConstants.VDW_AUTO_BABEL:
    case JmolConstants.VDW_AUTO_RASMOL:
      if (dataManager.defaultVdw != JmolConstants.VDW_AUTO)
        iType = dataManager.defaultVdw;
      break;
    }
    return (JmolConstants.getVanderwaalsMar(i, iType));
  }

  void setDefaultVdw(String type) {
    int iType = JmolConstants.getVdwType(type);
    if (iType == JmolConstants.VDW_UNKNOWN)
      iType = JmolConstants.VDW_AUTO;
    dataManager.setDefaultVdw(iType);
    global.setParameterValue("defaultVDW",
        getDefaultVdwTypeNameOrData(Integer.MIN_VALUE));
  }

  public String getDefaultVdwTypeNameOrData(int iMode) {
    return dataManager.getDefaultVdwNameOrData(iMode, null);
  }

  public int deleteAtoms(BitSet bs, boolean fullModels) {
    clearModelDependentObjects();
    if (!fullModels) {
      modelSet.deleteAtoms(bs);
      int n = selectionManager.deleteAtoms(bs);
      setTainted(true);
      return n;
    }
    // fileManager.addLoadScript("zap " + Escape.escape(bs));
    setCurrentModelIndex(0, false);
    animationManager.setAnimationOn(false);
    BitSet bsD0 = BitSetUtil.copy(getDeletedAtoms());
    BitSet bsDeleted = modelSet.deleteModels(bs);
    selectionManager.processDeletedModelAtoms(bsDeleted);
    setAnimationRange(0, 0);
    eval.deleteAtomsInVariables(bsDeleted);
    repaintManager.clear();
    animationManager.clear();
    animationManager.initializePointers(1);
    if (getModelCount() > 1)
      setCurrentModelIndex(-1, true);
    hoverAtomIndex = -1;
    setFileLoadStatus(JmolConstants.FILE_STATUS_MODELS_DELETED, null, null, null, null);
    refreshMeasures(true);
    if (bsD0 != null)
      bsDeleted.andNot(bsD0);
    return BitSetUtil.cardinalityOf(bsDeleted);
  }

  public void deleteBonds(BitSet bsDeleted) {
    modelSet.deleteBonds(bsDeleted, false);
  }

  public void deleteModelAtoms(int firstAtomIndex, int nAtoms, BitSet bsDeleted) {
    // called from ModelCollection.deleteModel
    selectionManager.deleteModelAtoms(bsDeleted);
    BitSetUtil.deleteBits(getFrameOffsets(), bsDeleted);
    setFrameOffsets(getFrameOffsets());
    dataManager.deleteModelAtoms(firstAtomIndex, nAtoms, bsDeleted);
  }

  public BitSet getDeletedAtoms() {
    return selectionManager.getDeletedAtoms();
  }

  public char getQuaternionFrame() {
    return global.quaternionFrame
        .charAt(global.quaternionFrame.length() == 2 ? 1 : 0);
  }

  public int getHelixStep() {
    return global.helixStep;
  }

  public String calculatePointGroup() {
    return modelSet.calculatePointGroup(getSelectionSet(false));
  }

  public Map<String, Object> getPointGroupInfo(Object atomExpression) {
    return modelSet.getPointGroupInfo(getAtomBitSet(atomExpression));
  }

  public String getPointGroupAsString(boolean asDraw, String type, int index,
                                      float scale) {
    return modelSet.getPointGroupAsString(getSelectionSet(false),
        asDraw, type, index, scale);
  }

  public float getPointGroupTolerance(int type) {
    switch (type) {
    case 0:
      return global.pointGroupDistanceTolerance;
    case 1:
      return global.pointGroupLinearTolerance;
    }
    return 0;
  }

  public Image getFileAsImage(String pathName, String[] retFileNameOrError) {
    if (!haveDisplay) {
      retFileNameOrError[0] = "no display";
      return null;
    }
    Image image = fileManager.getFileAsImage(pathName, retFileNameOrError);
    if (image == null)
      return null;
    MediaTracker tracker = new MediaTracker(display);
    tracker.addImage(image, 0);
    try {
      tracker.waitForID(0);
    } catch (InterruptedException e) {
      // Got to do something?
    }
    return image;
  }

  public String cd(String dir) {
    if (dir == null) {
      dir = ".";
    } else if (dir.length() == 0) {
      setStringProperty("defaultDirectory", "");
      dir = ".";
    }
    dir = fileManager.getDefaultDirectory(dir
        + (dir.equals("=") || dir.endsWith("/") ? "" : "/X"));
    if (dir.length() > 0)
      setStringProperty("defaultDirectory", dir);
    String path = fileManager.getFilePath(dir + "/", true, false);
    if (path.startsWith("file:/"))
      FileManager.setLocalPath(this, dir, false);
    return dir;
  }

  // //// Error handling

  private String errorMessage;
  private String errorMessageUntranslated;

  private String setErrorMessage(String errMsg) {
    return setErrorMessage(errMsg, null);
  }

  private String setErrorMessage(String errMsg, String errMsgUntranslated) {
    errorMessageUntranslated = errMsgUntranslated;
    return (errorMessage = errMsg);
  }

  @Override
  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public String getErrorMessageUntranslated() {
    return errorMessageUntranslated == null ? errorMessage
        : errorMessageUntranslated;
  }

  private int currentShapeID = -1;
  private String currentShapeState;

  public void setShapeErrorState(int shapeID, String state) {
    currentShapeID = shapeID;
    currentShapeState = state;
  }

  public String getShapeErrorState() {
    if (currentShapeID < 0)
      return "";
    if (modelSet != null)
      shapeManager.releaseShape(currentShapeID);
    repaintManager.clear(currentShapeID);
    return JmolConstants.getShapeClassName(currentShapeID) + " "
        + currentShapeState;
  }

  public void handleError(Error er, boolean doClear) {
    // almost certainly out of memory; could be missing Jar file
    try {
      if (doClear)
        zap("" + er); // get some breathing room
      setCursor(Viewer.CURSOR_DEFAULT);
      setBooleanProperty("refreshing", true);
      Logger.error("viewer handling error condition: " + er);
      notifyError("Error", "doClear=" + doClear + "; " + er, "" + er);
    } catch (Throwable e1) {
      try {
        Logger.error("Could not notify error " + er + ": due to " + e1);
      } catch (Throwable er2) {
        // tough luck.
      }
    }
  }

  public float[] getAtomicCharges() {
    return modelSet.getAtomicCharges();
  }

  // / User-defined functions

  public ScriptFunction getFunction(String name) {
    return stateManager.getFunction(name);
  }

  public void addFunction(ScriptFunction f) {
    stateManager.addFunction(f);
  }

  public void clearFunctions() {
    stateManager.clearFunctions();
  }

  public boolean isFunction(String name) {
    return stateManager.isFunction(name);
  }

  public String getFunctionCalls(String selectedFunction) {
    return stateManager.getFunctionCalls(selectedFunction);
  }

  public void showMessage(String s) {
    if (!isPrintOnly)
      Logger.warn(s);
  }

  public String getMoInfo(int modelIndex) {
    return modelSet.getMoInfo(modelIndex);
  }

  boolean isRepaintPending() {
    return repaintManager.repaintPending;
  }

  public Map<String, ScriptVariable> getContextVariables() {
    return eval.getContextVariables();
  }

  private double privateKey = Math.random();

  /**
   * Simple method to ensure that the image creator (which writes files) was in
   * fact opened by this viewer and not by some manipulation of the applet. When
   * the image creator is used it requires both a viewer object and that
   * viewer's private key. But the private key is private, so it is not possible
   * to create a useable image creator without working through a viewer's own
   * methods. Bob Hanson, 9/20/2009
   * 
   * @param privateKey
   * @return true if privateKey matches
   * 
   */

  @Override
  public boolean checkPrivateKey(double privateKey) {
    return privateKey == this.privateKey;
  }

  public void bindAction(String desc, String name, Point3f range1,
                         Point3f range2) {
    if (haveDisplay)
      actionManager.bindAction(desc, name, range1, range2);
  }

  public void unBindAction(String desc, String name) {
    if (haveDisplay)
      actionManager.unbindAction(desc, name);
  }

  public Object getMouseInfo() {
    return (haveDisplay ? actionManager.getMouseInfo() : null);
  }

  public void clearTimeout(String name) {
    setTimeout(name, 0, null);
  }

  public void setTimeout(String name, int mSec, String script) {
    if (haveDisplay)
      actionManager.setTimeout(name, mSec, script);
  }

  public String showTimeout(String name) {
    return (haveDisplay ? actionManager.showTimeout(name) : "");
  }

  public int getFrontPlane() {
    return transformManager.getFrontPlane();
  }

  public List<Object> getPlaneIntersection(int type, Point4f plane, float scale,
                                     int flags) {
    return modelSet.getPlaneIntersection(type, plane, scale, flags,
        animationManager.currentModelIndex);
  }

  void repaint() {
    // from RepaintManager
    if (haveDisplay)
      display.repaint();
  }

  public OutputStream getOutputStream(String localName, String[] fullPath) {
    Object ret = createImage(localName, "OutputStream", null,
        Integer.MIN_VALUE, 0, 0, fullPath);
    if (ret instanceof String) {
      Logger.error((String) ret);
      return null;
    }
    return (OutputStream) ret;
  }

  public int calculateStruts(BitSet bs1, BitSet bs2) {
    return modelSet.calculateStruts(bs1 == null ? getSelectionSet(false) : bs1,
        bs2 == null ? getSelectionSet(false) : bs2);
  }

  public boolean getStrutsMultiple() {
    return global.strutsMultiple;
  }

  public int getStrutSpacingMinimum() {
    return global.strutSpacing;
  }

  public float getStrutLengthMaximum() {
    return global.strutLengthMaximum;
  }

  public float getStrutDefaultRadius() {
    return global.strutDefaultRadius;
  }

  /**
   * This flag if set FALSE:
   * 
   * 1) turns UNDO off for the application 2) turns history off 3) prevents
   * saving of inlinedata for later LOAD "" commands 4) turns off the saving of
   * changed atom properties 5) does not guarantee accurate state representation
   * 6) disallows generation of the state
   * 
   * It is useful in situations such as web sites where memory is an issue and
   * there is no need for such.
   * 
   * 
   * @return TRUE or FALSE
   */
  public boolean getPreserveState() {
    return global.preserveState;
  }

  public boolean getDragSelected() {
    return global.dragSelected && !global.modelKitMode;
  }

  public float getLoadAtomDataTolerance() {
    return global.loadAtomDataTolerance;
  }

  public boolean getAllowGestures() {
    return global.allowGestures;
  }

  public boolean getLogGestures() {
    return global.logGestures;
  }

  public boolean allowMultiTouch() {
    return global.allowMultiTouch;
  }

  public boolean logCommands() {
    return global.logCommands;
  }

  private String logFile = null;

  public String getLogFile() {
    return (logFile == null ? "" : logFile);
  }

  private String setLogFile(String value) {
    String path = null;
    if (logFilePath == null || value.indexOf("\\") >= 0
        || value.indexOf("/") >= 0) {
      value = null;
    } else if (value.length() > 0) {
      if (!value.startsWith("JmolLog_"))
        value = "JmolLog_" + value;
      try {
        path = (isApplet ? logFilePath + value : (new File(logFilePath + value)
            .getAbsolutePath()));
      } catch (Exception e) {
        value = null;
      }
    }
    if (value == null) {
      Logger.info(GT._("Cannot set log file path."));
    } else {
      if (path != null) 
        Logger.info(GT._("Setting log file to {0}", path));
      logFile = path;
    }
    return value;
  }

  public void log(String data) {
    try {
      if (data == null)
        return;
      boolean doClear = (data.equals("$CLEAR$"));
      if (data.indexOf("$NOW$") >= 0)
        data = TextFormat.simpleReplace(data, "$NOW$", (new Date()).toString());
      if (logFile == null) {
        System.out.println(data);
        return;
      }
      FileWriter fstream = new FileWriter(logFile, !doClear);
      BufferedWriter out = new BufferedWriter(fstream);
      if (!doClear) {
        out.write(data);
        out.write('\n');
      }
      out.close();
    } catch (Exception e) {
      Logger.debug("cannot log " + data);
    }
  }

  private boolean isKiosk;

  boolean isKiosk() {
    return isKiosk;
  }

  public boolean hasFocus() {
    return (haveDisplay && (isKiosk || display.hasFocus()));
  }

  public void setFocus() {
    if (haveDisplay && !display.hasFocus())
      display.requestFocusInWindow();
  }


  private MinimizerInterface minimizer;

  public MinimizerInterface getMinimizer(boolean createNew) {
    if (minimizer == null && createNew) {
      minimizer = (MinimizerInterface) Interface
          .getOptionInterface("minimize.Minimizer");
      minimizer.setProperty("viewer", this);
    }
    return minimizer;
  }

  void stopMinimization() {
    if (minimizer != null) {
      minimizer.setProperty("stop", null);
    }
  }

  void clearMinimization() {
    if (minimizer != null)
      minimizer.setProperty("clear", null);
  }

  public String getMinimizationInfo() {
    return (minimizer == null ? "" : (String) minimizer.getProperty("log", 0));
  }

  public boolean useMinimizationThread() {
    return global.useMinimizationThread && !autoExit;
  }

  private void checkMinimization() {
    if (!global.monitorEnergy)
      return;
    minimize(0, 0, getModelUndeletedAtomsBitSet(-1), null, 0, false, true, false);
    echoMessage("Energy = " + getParameter("_minimizationEnergy"));
  }

  /**
   * 
   * @param steps
   *          Integer.MAX_VALUE --> use defaults
   * @param crit
   *          -1 --> use defaults
   * @param bsSelected
   * @param bsFixed TODO
   * @param rangeFixed
   * @param addHydrogen
   * @param isSilent
   * @param asScript
   */
  public void minimize(int steps, float crit, BitSet bsSelected,
                       BitSet bsFixed, float rangeFixed, boolean addHydrogen,
                       boolean isSilent, boolean asScript) {
    
    // We only work on atoms that are in frame
    
    BitSet bsInFrame = getModelUndeletedAtomsBitSet(getVisibleFramesBitSet());

    if (bsSelected == null)
      bsSelected = getModelUndeletedAtomsBitSet(getVisibleFramesBitSet()
          .length() - 1);
    else
      bsSelected.and(bsInFrame);
    
    if (rangeFixed <= 0)
      rangeFixed = JmolConstants.MINIMIZE_FIXED_RANGE;

    // we allow for a set of atoms to be fixed, 
    // but that is only used by default
    
    BitSet bsMotionFixed = BitSetUtil.copy(bsFixed == null ? selectionManager.getMotionFixedAtoms() : bsFixed);
    boolean haveFixed = (bsMotionFixed.cardinality() > 0); 
    if (haveFixed)
      bsSelected.andNot(bsMotionFixed);

    // We always fix any atoms that
    // are in the visible frame set and are within 5 angstroms
    // and are not already selected
    
    BitSet bsNearby = getAtomsWithin(rangeFixed, bsSelected, true);
    bsNearby.andNot(bsSelected);
    if (haveFixed) {
      bsMotionFixed.and(bsNearby);
    } else {
      bsMotionFixed = bsNearby;
    }
    bsMotionFixed.and(bsInFrame);

    if (addHydrogen)
      bsSelected.or(addHydrogens(bsSelected, asScript, isSilent));
    

    if (bsSelected.cardinality() > JmolConstants.MINIMIZATION_ATOM_MAX) {
      Logger.error("Too many atoms for minimization (>"
          + JmolConstants.MINIMIZATION_ATOM_MAX + ")");
      return;
    }
    try {
      if (!isSilent)
        Logger.info("Minimizing " + bsSelected.cardinality() + " atoms");
      getMinimizer(true).minimize(steps, crit, bsSelected, bsMotionFixed, haveFixed, isSilent);
    } catch (Exception e) {
      e.printStackTrace();
      Logger.error(e.getMessage());
    }
  }

  public void setMotionFixedAtoms(BitSet bs) {
    selectionManager.setMotionFixedAtoms(bs);
  }
  
  public BitSet getMotionFixedAtoms() {
    return selectionManager.getMotionFixedAtoms();
  }
  
  boolean useArcBall() {
    return global.useArcBall;
  }

  void rotateArcBall(int x, int y, float factor) {
    transformManager.rotateArcBall(x, y, factor);
    refresh(2, statusManager.syncingMouse ? "Mouse: rotateArcBall " + x + " "
        + y + " " + factor : "");
  }

  void getAtomicPropertyState(StringBuffer commands, byte type, BitSet bs,
                              String name, float[] data) {
    modelSet.getAtomicPropertyState(commands, type, bs, name, data);
  }

  public Point3f[][] getCenterAndPoints(List<BitSet[]> atomSets, boolean addCenter) {
    return modelSet.getCenterAndPoints(atomSets, addCenter);
  }

  public int getSmallMoleculeMaxAtoms() {
    return global.smallMoleculeMaxAtoms;
  }

  public String streamFileData(String fileName, String type, String type2, int modelIndex, Object[] parameters) {
    String msg = null;
    String[] fullPath = new String[1];
    OutputStream os = getOutputStream(fileName, fullPath);
    if (os == null)
      return "";
    OutputStringBuffer sb;
    if (type.equals("PDB")) {
      sb = new OutputStringBuffer(new BufferedOutputStream(os));
      msg = getPdbData(null, sb);
    } else if (type.equals("FILE")) {
      msg = writeCurrentFile(os);
      // quality = Integer.MIN_VALUE;
    } else if (type.equals("PLOT")) {
      sb = new OutputStringBuffer(new BufferedOutputStream(os));
      msg = modelSet.getPdbData(modelIndex, type2, getSelectionSet(false), parameters, sb);
    }
    if (msg != null)
      msg = "OK " + msg + " " + fullPath[0];
    try {
      os.flush();
      os.close();
    } catch (IOException e) {
      // TODO
    }
    return msg;
  }

  public String getPdbData(int modelIndex, String type, Object[] parameters) {
    return modelSet.getPdbData(modelIndex, type, 
        getSelectionSet(false), parameters, null);
  }

  public int getRepaintWait() {
    return global.repaintWaitMs;
  }

  public BitSet getGroupsWithin(int nResidues, BitSet bs) {
    return modelSet.getGroupsWithin(nResidues, bs);
  }

  // parallel processing

  private Object executor;
  public static int nProcessors = Runtime.getRuntime().availableProcessors();

  public Object getExecutor() {
    // a Java 1.5 function
    if (executor != null || nProcessors < 2)
      return executor; // note -- a Java 1.5 function
    try {
      executor = ParallelProcessor.getExecutor();
    } catch (Exception e) {
      executor = null;
    } catch (Error er) {
      executor = null;
    }
    if (executor == null)
      Logger.error("parallel processing is not available");
    return executor;
  }

  boolean displayLoadErrors = true;
  
  public boolean eval(ScriptContext context, ShapeManager shapeManager) {
    displayLoadErrors = false;
    boolean isOK = ScriptEvaluator.evaluateContext(this, context,
        (shapeManager == null ? this.shapeManager : shapeManager));
    displayLoadErrors = true;
    return isOK;
  }

  public Map<String, Object> getShapeInfo() {
    return shapeManager.getShapeInfo();
  }

  public void togglePickingLabel(BitSet bs) {
    // eval label toggle (atomset) and actionManager
    if (bs == null)
      bs = getSelectionSet(false);
    loadShape(JmolConstants.SHAPE_LABELS);
    // setShapeSize(JmolConstants.SHAPE_LABELS, 0, Float.NaN, bs);
    shapeManager.setShapeProperty(JmolConstants.SHAPE_LABELS, "toggleLabel",
        null, bs);
  }

  public void loadShape(int shapeID) {
    shapeManager.loadShape(shapeID);
  }

  public void setShapeSize(int shapeID, int mad, BitSet bsSelected) {
    // might be atoms or bonds
    if (bsSelected == null)
      bsSelected = getSelectionSet(false);
    shapeManager.setShapeSize(shapeID, mad, null, bsSelected);
  }

  public void setShapeSize(int shapeID, RadiusData rd, BitSet bsAtoms) {
    shapeManager.setShapeSize(shapeID, 0, rd, bsAtoms);
  }

  public void setShapeProperty(int shapeID, String propertyName, Object value) {
    // Eval, BondCollection, StateManager, local
    if (shapeID < 0)
      return; // not applicable
    shapeManager.setShapeProperty(shapeID, propertyName, value, null);
  }

  public Object getShapeProperty(int shapeType, String propertyName) {
    return shapeManager.getShapeProperty(shapeType, propertyName,
        Integer.MIN_VALUE);
  }

  public boolean getShapeProperty(int shapeType, String propertyName,
                                  Object[] data) {
    return shapeManager.getShapeProperty(shapeType, propertyName, data);
  }

  public Object getShapeProperty(int shapeType, String propertyName, int index) {
    return shapeManager.getShapeProperty(shapeType, propertyName, index);
  }

  private int getShapePropertyAsInt(int shapeID, String propertyName) {
    Object value = getShapeProperty(shapeID, propertyName);
    return value == null || !(value instanceof Integer) ? Integer.MIN_VALUE
        : ((Integer) value).intValue();
  }

  public void setModelVisibility() {
    if (shapeManager == null) // necessary for file chooser
      return;
    shapeManager.setModelVisibility();
  }

  public void resetShapes() {
    shapeManager.resetShapes();
  }

  public void setAtomLabel(String value, int i) {
    shapeManager.setAtomLabel(value, i);
  }

  public void deleteShapeAtoms(Object[] value, BitSet bs) {
    shapeManager.deleteShapeAtoms(value, bs);
  }

  public void getShapeState(StringBuffer commands, boolean isAll) {
    shapeManager.getShapeState(commands, isAll);
  }

  public void resetBioshapes(BitSet bsAllAtoms) {
    shapeManager.resetBioshapes(bsAllAtoms);
  }

  public float getAtomShapeValue(int tok, Group group, int atomIndex) {
    // Atom
    return shapeManager.getAtomShapeValue(tok, group, atomIndex);
  }

  public void mergeShapes(Shape[] newShapes) {
    // ParallelProcessor
    shapeManager.mergeShapes(newShapes);
  }

  public ShapeManager getShapeManager() {
    return shapeManager;
  }

  boolean isParallel;

  public boolean setParallel(boolean TF) {
    return (isParallel = global.multiProcessor && TF);
  }

  public boolean isParallel() {
    return global.multiProcessor && isParallel;
  }

  public BitSet transformAtoms(boolean firstPass) {
    return shapeManager.transformAtoms(firstPass);
  }

  private void setAtomPickingOption(String option) {
    if (haveDisplay)
      actionManager.setAtomPickingOption(option);
  }

  private void setBondPickingOption(String option) {
    if (haveDisplay)
      actionManager.setBondPickingOption(option);
  }

  private final static int MAX_ACTION_UNDO = 100;
  private final List<String> actionStates = new ArrayList<String>();
  private final List<String> actionStatesRedo = new ArrayList<String>();
  private int lastUndoRedo = 0;
  
  void undoAction(boolean isSave, int taintedAtom, int type) {
    int modelIndex = (taintedAtom >= 0 ? modelSet.atoms[taintedAtom].modelIndex 
        : modelSet.getModelCount() - 1);
    System.out.println(isSave + " " + type  + " undoAction " + modelSet.getModels()[modelIndex].isModelkit());
    if (!modelSet.getModels()[modelIndex].isModelkit())
      return;
    if (!isSave) {
      stopMinimization();
      String s;
      if (lastUndoRedo != 0 && lastUndoRedo != type) {
        try {
        if (type == -1) {
          actionStates.add(0, actionStatesRedo.remove(0));
        } else {
          actionStatesRedo.add(0, actionStates.remove(0));
        }
        } catch (Exception e) {
         System.out.println("oops"); // ignore
        }
      }
      lastUndoRedo = type;
      if (type == -1) {
        if (actionStatesRedo.size() == 0)
          return;
        s = actionStatesRedo.remove(0);
        actionStates.add(0, s);
      } else if (actionStates.size() == 0) {
        return;
      } else {
        s = actionStates.remove(0);
        actionStatesRedo.add(0, s);
      }
      if (Logger.debugging)
        log(s);
      evalStringQuiet(s);
      return;
    }
    lastUndoRedo = 0;
    actionStatesRedo.clear();
    BitSet bs;
    StringBuffer sb = new StringBuffer();
    if (taintedAtom >= 0) {
      bs = getModelUndeletedAtomsBitSet(modelIndex);
      modelSet.taint(bs, (byte) type);
      sb.append(modelSet.getAtomicPropertyState(-1, null));
    } else {
      bs = getModelUndeletedAtomsBitSet(modelIndex);
      sb.append("zap ");
      sb.append(Escape.escape(bs)).append(";");
      DataManager.getInlineData(sb, modelSet.getModelExtract(bs, false, true, "MOL"), true, null);
      sb.append("set refreshing false;")
          .append(actionManager.getPickingState()).append(
              transformManager.getMoveToText(0, false)).append(
              "set refreshing true;");

    }
    actionStates.add(0, sb.toString());
    if (actionStates.size() == MAX_ACTION_UNDO) {
      actionStates.remove(MAX_ACTION_UNDO - 1);
    }
  }

  public void assignBond(int bondIndex, char type) {
    try {
      BitSet bsAtoms = modelSet.setBondOrder(bondIndex, type);
      if (bsAtoms == null || type == '0')
        refresh(3, "setBondOrder");
      else
        addHydrogens(bsAtoms, false, true);
    } catch (Exception e) {
      Logger.error("assignBond failed");
    }
  }

  public void assignAtom(int atomIndex, Point3f pt, String type) {
    if (type.equals("X"))
      setRotateBondIndex(-1);
    if (modelSet.atoms[atomIndex].modelIndex != modelSet.getModelCount() - 1)
      return;
    clearModelDependentObjects();
    if (pt == null) {
      modelSet.assignAtom(atomIndex, type, true);
      modelSet.setAtomNamesAndNumbers(atomIndex, -1, null);
      refresh(3, "assignAtom");
      return;
    }
    Atom atom = modelSet.atoms[atomIndex];
    BitSet bs = BitSetUtil.setBit(atomIndex);
    Point3f[] pts = new Point3f[] { pt };
    List<Atom> vConnections = new ArrayList<Atom>();
    vConnections.add(atom);
    try {
      bs = addHydrogensInline(bs, vConnections, pts);
      atomIndex = bs.nextSetBit(0);
      modelSet.assignAtom(atomIndex, type, false);
    } catch (Exception e) {
      //
    }
    modelSet.setAtomNamesAndNumbers(atomIndex, -1, null);
  }

  public void assignConnect(int index, int index2) {
    clearModelDependentObjects();
    float[][] connections = new float[1][];
    connections[0] = new float[] { index, index2 };
    modelSet.connect(connections);
    modelSet.assignAtom(index, ".", true);
    modelSet.assignAtom(index2, ".", true);
    refresh(3, "assignConnect");
  }

  void moveAtomWithHydrogens(int atomIndex, int deltaX, int deltaY,
                             BitSet bsAtoms) {
    stopMinimization();
    Atom atom = modelSet.atoms[atomIndex];
    if (bsAtoms == null) {
      bsAtoms = BitSetUtil.setBit(atomIndex);
      Bond[] bonds = atom.getBonds();
      if (bonds != null)
        for (int i = 0; i < bonds.length; i++) {
          Atom atom2 = bonds[i].getOtherAtom(atom);
          if (atom2.getElementNumber() == 1)
            bsAtoms.set(atom2.index);
        }
    }
    moveSelected(deltaX, deltaY, Integer.MIN_VALUE, Integer.MIN_VALUE, bsAtoms,
        true);
  }

  void appendLoadStates(StringBuffer commands) {
    modelSet.appendLoadStates(commands);
  }

  public static void getInlineData(StringBuffer loadScript, String strModel,
                                   boolean isAppend) {
    // because the model is a modelKit atom set
    DataManager.getInlineData(loadScript, strModel, isAppend, null);
  }

  boolean isAtomPDB(int i) {
    return modelSet.isAtomPDB(i);
  }
  boolean isAtomAssignable(int i) {
    return modelSet.isAtomAssignable(i);
  }

  @Override
  public void deleteMeasurement(int i) {
    setShapeProperty(JmolConstants.SHAPE_MEASURES, "delete", Integer.valueOf(i));
  }

  boolean haveModelKit() {
    return modelSet.haveModelKit();
  }

  BitSet getModelKitStateBitSet(BitSet bs, BitSet bsDeleted) {
    return modelSet.getModelKitStateBitset(bs, bsDeleted);
  }

  /**
   * returns the SMILES string for a sequence or atom set
   * 
   * @param index1
   * @param index2
   * @param bsSelected
   * @param isBioSmiles
   * @param allowUnmatchedRings TODO
   * @param addCrossLinks TODO
   * @param addComment
   * @return SMILES string
   */
  public String getSmiles(int index1, int index2, BitSet bsSelected,
                          boolean isBioSmiles, boolean allowUnmatchedRings, boolean addCrossLinks, boolean addComment) {
    Atom[] atoms = getModelSet().atoms;
    if (bsSelected == null) {
      if (index1 < 0 || index2 < 0) {
        bsSelected = getSelectionSet(true);
      } else {
        if (isBioSmiles) {
          if (index1 > index2) {
            int i = index1;
            index1 = index2;
            index2 = i;
          }
          index1 = atoms[index1].getGroup().firstAtomIndex;
          index2 = atoms[index2].getGroup().lastAtomIndex;
        }
        bsSelected = new BitSet();
        bsSelected.set(index1, index2 + 1);
      }
    }
    String comment = (addComment ? getJmolVersion() + " "
        + getModelName(getCurrentModelIndex()) : null);
    return getSmilesMatcher().getSmiles(atoms, getAtomCount(), bsSelected,
        isBioSmiles, allowUnmatchedRings, addCrossLinks, comment);
  }

  public void connect(float[][] connections) {
    modelSet.connect(connections);
  }

  public String prompt(String label, String data, String[] list,
                       boolean asButtons) {
    if (!asButtons)
      return JOptionPane.showInputDialog(label, data);
    if (data != null)
      list = TextFormat.split(data, "|");
    int i = JOptionPane.showOptionDialog(null, label, "Jmol prompt",
        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
        list, list[0]);
    // ESCAPE will close the panel with no option selected.
    return (data == null ? "" + i : i == JOptionPane.CLOSED_OPTION ? "null"
        : list[i]);
  }

  String getMenuName(int i) {
    String script = "" + getModelNumberDotted(i);
    String entryName = getModelName(i);
    if (!entryName.equals(script))
      entryName = script + ": " + entryName;
    if (entryName.length() > 50)
      entryName = entryName.substring(0, 45) + "...";
    return entryName;
  }

  public ColorEncoder getColorEncoder(String colorScheme) {
    return colorManager.getColorEncoder(colorScheme);
  }

  public void displayBonds(BondSet bs, boolean isDisplay) {
    modelSet.displayBonds(bs, isDisplay);    
  }

  public String getModelAtomProperty(Atom atom, String text) {
    return modelSet.getModelAtomProperty(atom, text);
  }

  private int stateScriptVersionInt;

  public void setStateScriptVersion(String version) {
    if (version != null) {
      try {
        String[] tokens = Parser.getTokens(version.replace('.', ' ').replace(
            '_', ' '));
        int main = Integer.valueOf(tokens[0]).intValue(); //11
        int sub = Integer.valueOf(tokens[1]).intValue(); //9
        int minor = Integer.valueOf(tokens[2]).intValue(); //24
        stateScriptVersionInt = main * 10000 + sub * 100 + minor;
        // here's why:
        global.legacyAutoBonding = (stateScriptVersionInt < 110924);
        
        return;
      } catch (Exception e) {
        //
      }
    }
    global.legacyAutoBonding = false;
    stateScriptVersionInt = Integer.MAX_VALUE;
  }
  
  public boolean checkAutoBondLegacy() {    
    // aargh -- BitSet efficiencies in Jmol 11.9.24, 2/3/2010, meant that
    // state files created before that that use select BONDS will select the
    // wrong bonds. 
    return global.legacyAutoBonding;
    // reset after a state script is read
  }

  boolean initializeExporter(JmolRendererInterface g3dExport,
                                    String type, Object output) {
    return  g3dExport.initializeExporter(type, this, privateKey, g3d, output);
  }

  public void setPrivateKeyForShape(int iShape) {
    setShapeProperty(iShape, "privateKey", Double.valueOf(privateKey));
  }

  public boolean getMouseEnabled() {
    return refreshing && !creatingImage;
  }
  
}
