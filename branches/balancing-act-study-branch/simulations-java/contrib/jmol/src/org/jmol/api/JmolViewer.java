/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2011-03-11 16:34:40 -0800 (Fri, 11 Mar 2011) $
 * $Revision: 15276 $
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

package org.jmol.api;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.BitSet;
import java.util.Map;
import java.util.Properties;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;


import org.jmol.viewer.Viewer;

/**
 * This is the high-level API for the JmolViewer for simple access.
 * <p>
 * We will implement a low-level API at some point
 * 
 *
 **/

abstract public class JmolViewer extends JmolSimpleViewer {

  /**
   *  This is the main access point for creating an application
   *  or applet viewer. In Jmol 11.6 it was manditory that one of 
   *  the next commands is either 
   *  
   *    viewer.evalString("ZAP");
   *    
   *    or at least:
   *    
   *    viewer.setAppletContext("",null,null,"")
   *    
   *    One or the other of these was necessary to establish the 
   *    first modelset, which might be required by one or more
   *    later evaluated commands or file loadings.
   *    
   *    Starting with Jmol 11.7, setAppletContext is rolled into
   *    allocateViewer so that the full initialization is done
   *    all at once.
   *    
   * 
   * @param container
   * @param jmolAdapter
   * @param htmlName 
   * @param documentBase 
   * @param codeBase 
   * @param commandOptions 
   * @param statusListener 
   * @return              a JmolViewer object
   */
  static public JmolViewer allocateViewer(Container container,
                                          JmolAdapter jmolAdapter,
                                          String htmlName, URL documentBase, 
                                          URL codeBase,
                                          String commandOptions, 
                                          JmolStatusListener statusListener) {
    
    return Viewer.allocateViewer(container, jmolAdapter,
        htmlName, documentBase, codeBase, commandOptions, statusListener);
  }

  /**
   * default null htmlName, URL bases, comandOptions, and statusListener.
   * 
   * @param container
   * @param jmolAdapter
   * @return             a viewer
   */
  public static JmolViewer allocateViewer(Container container, JmolAdapter jmolAdapter) {
    return Viewer.allocateViewer(container, jmolAdapter, null, null, null, null, null);
  }
  
  /**
   * sets a custom console -- should be called IMMEDIATELY following allocateViewer
   * 
   * create your console with, perhaps:
   * 
   * new org.openscience.jmol.app.jmolPanel.AppConsole(viewer, displayFrame, 
   *                               externalJPanel, buttonsEnabled);
   * 
   * (see examples/basic/org/jmol/Integration.java
   * 
   * @param console        the console to use  
   * 
   */
  public void setConsole(JmolAppConsoleInterface console) {
    getProperty("DATA_API", "getAppConsole", console); 
  }

  abstract public BitSet getSmartsMatch(String smarts, BitSet bsSelected);
  
  /**
   * an added class for rendering stereo in two independent applets
   * 
   * @param gLeft
   * @param gRight
   * @param size
   * @param clip
   */
  abstract public void renderScreenImage(Graphics gLeft, Graphics gRight, Dimension size,
                                         Rectangle clip);

  static public String getJmolVersion() {
    return Viewer.getJmolVersion();
  }

  static public boolean checkOption(JmolViewer viewer, String option) {
    Object testFlag = viewer.getParameter(option);
    return (testFlag instanceof Boolean && ((Boolean) testFlag).booleanValue()
        || testFlag instanceof Integer && ((Integer) testFlag).intValue() != 0);
  }

  // for POV-Ray -- returns the INI file
  
  abstract public String generateOutput(String type, String[] fileName, int width, int height); 

  abstract public void setJmolCallbackListener(JmolCallbackListener jmolCallbackListener);

  abstract public void setJmolStatusListener(JmolStatusListener jmolStatusListener);

  abstract public void setAppletContext(String htmlName, URL documentBase, URL codeBase,
                                        String commandOptions);

  abstract public boolean checkHalt(String strCommand, boolean isInterrupt);
  abstract public void haltScriptExecution();

  abstract public String getOperatingSystemName();
  abstract public String getJavaVersion();
  abstract public String getJavaVendor();

  abstract public boolean haveFrame();

  abstract public void pushHoldRepaint();
  abstract public void popHoldRepaint();

  // for example: getData("selected","XYZ");
  abstract public String getData(String atomExpression, String type);


  // change this to width, height
  abstract public void setScreenDimension(Dimension dim);
  abstract public int getScreenWidth();
  abstract public int getScreenHeight();

  public Image getScreenImage() {
    return getScreenImage(null);
  }

  abstract public Image getScreenImage(Graphics g);
  abstract public void releaseScreenImage();
  
  abstract public void writeTextFile(String string, String data);
  
  /**
   * 
   * @param text   null here clips image; String clips text
   * @return "OK" for image or "OK [number of bytes]"
   */
  abstract public String clipImage(String text);

  /**
   * 
   * @param fileName
   * @param type
   * @param text_or_bytes
   * @param quality
   * @param width 
   * @param height 
   * @return          null (canceled) or a message starting with OK or an error message
   */
  abstract public Object createImage(String fileName, String type, Object text_or_bytes, int quality,
                                   int width, int height);

  /**
   * @param type      "PNG", "JPG", "JPEG", "JPG64", "PPM", "GIF"
   * @param quality
   * @param width 
   * @param height 
   * @param fileName 
   * @param os 
   * @return base64-encoded or binary version of the image
   */
  abstract public Object getImageAs(String type, int quality, int width, int height, String fileName, OutputStream os);

  abstract public boolean handleOldJvm10Event(Event e);

  abstract public int getMotionEventNumber();

  /**
   * Opens the file and creates the model set, given the reader.
   * 
   * name is a text name of the file ... to be displayed in the window no need
   * to pass a BufferedReader ... ... the FileManager will wrap a buffer around
   * it
   * 
   * DO NOT USE IN JMOL -- THIS METHOD IS ONLY HERE BECAUSE IT IS
   * PART OF THE LEGACY INTERFACE
   * IF USED BY ANOTHER APPLICATION, YOU ARE RESPONSIBLE FOR CLOSING THE READER
   * 
   * @param fullPathName
   * @param fileName
   * @param reader
   * @return       null or error message
   */
   
  abstract public String openReader(String fullPathName, String fileName, Reader reader);
  
  /*
   * REMOVED -- this method does not actually open the file
   * 
   * @param fullPathName
   * @param fileName
   * @param clientFile
   * @deprecated
   */
//  abstract public void openClientFile(String fullPathName, String fileName,
  //                           Object clientFile);

  abstract public void showUrl(String urlString);


  abstract public int getMeasurementCount();
  abstract public String getMeasurementStringValue(int i);
  abstract public int[] getMeasurementCountPlusIndices(int i);

  abstract public Container getDisplay();

  abstract public BitSet getElementsPresentBitSet(int modelIndex);

  abstract public int getAnimationFps();

  abstract public int findNearestAtomIndex(int x, int y);

  abstract public String script(String script);
  abstract public Object scriptCheck(String script);
  abstract public String scriptWait(String script);
  abstract public Object scriptWaitStatus(String script, String statusList);
  abstract public String loadInline(String strModel);
  abstract public String loadInline(String strModel, boolean isAppend);
  abstract public String loadInline(String strModel, char newLine);
  abstract public String loadInline(String[] arrayModels);
  /**
   * 
   * @param arrayModels and array of models, each of which is a String
   * @param isAppend
   * @return null or error message
   */
  abstract public String loadInline(String[] arrayModels, boolean isAppend);
  /**
   * 
   * NOTE: THIS METHOD DOES NOT PRESERVE THE STATE
   * 
   * @param arrayData a Vector of models, where each model is either a String
   *                  or a String[] or a Vector<String>
   * @param isAppend TRUE to append models (no ZAP)
   * @return null or error message
   */
  abstract public String loadInline(List<Object> arrayData, boolean isAppend);

  abstract public String evalStringQuiet(String script);
  abstract public boolean isScriptExecuting();

  abstract public String getModelSetName();
  abstract public String getModelSetFileName();
  abstract public String getModelSetPathName();
  abstract public String getFileAsString(String filename);
  abstract public boolean getFileAsString(String[] data, int nBytesMax, boolean doSpecialLoad);
  abstract public Properties getModelSetProperties();
  abstract public Map<String, Object> getModelSetAuxiliaryInfo();
  abstract public int getModelNumber(int modelIndex);
  abstract public String getModelName(int modelIndex);
  abstract public String getModelNumberDotted(int modelIndex);
  abstract public Properties getModelProperties(int modelIndex);
  abstract public String getModelProperty(int modelIndex, String propertyName);
  abstract public Map<String, Object> getModelAuxiliaryInfo(int modelIndex);
  abstract public Object getModelAuxiliaryInfo(int modelIndex, String keyName);
  abstract public boolean modelHasVibrationVectors(int modelIndex);

  abstract public int getModelCount();
  abstract public int getDisplayModelIndex();
  abstract public BitSet getVisibleFramesBitSet();
  abstract public int getAtomCount();
  abstract public int getBondCount(); // NOT THE REAL BOND COUNT -- just an array maximum
  abstract public int getGroupCount();
  abstract public int getChainCount();
  abstract public int getPolymerCount();
  abstract public int getAtomCountInModel(int modelIndex);
  abstract public int getBondCountInModel(int modelIndex);  // use -1 here for "all"
  abstract public int getGroupCountInModel(int modelIndex);
  abstract public int getChainCountInModel(int modelIindex);
  abstract public int getPolymerCountInModel(int modelIndex);
  abstract public int getSelectionCount();

  abstract public void addSelectionListener(JmolSelectionListener listener);
  abstract public void removeSelectionListener(JmolSelectionListener listener);
//BH 2/2006  abstract public BitSet getSelectionSet();

  abstract public void homePosition();

  abstract public Map<String, String> getHeteroList(int modelIndex);


  abstract public boolean getPerspectiveDepth();
  abstract public boolean getShowHydrogens();
  abstract public boolean getShowMeasurements();
  abstract public boolean getShowAxes();
  abstract public boolean getShowBbcage();

  abstract public int getAtomNumber(int atomIndex);
  abstract public String getAtomName(int atomIndex);
  abstract public String getAtomInfo(int atomIndex); // also gets measurement information for points

  abstract public float getRotationRadius();

  abstract public int getZoomPercent(); //deprecated
  abstract public float getZoomPercentFloat();
  abstract public Matrix4f getUnscaledTransformMatrix();

  abstract public int getBackgroundArgb();
  
  abstract public float getAtomRadius(int atomIndex);
  abstract public Point3f getAtomPoint3f(int atomIndex);
  abstract public int getAtomArgb(int atomIndex);
  abstract public int getAtomModelIndex(int atomIndex);

  abstract public float getBondRadius(int bondIndex);
  abstract public Point3f getBondPoint3f1(int bondIndex);
  abstract public Point3f getBondPoint3f2(int bondIndex);
  abstract public int getBondArgb1(int bondIndex);
  abstract public int getBondArgb2(int bondIndex);
  abstract public int getBondOrder(int bondIndex);
  abstract public int getBondModelIndex(int bondIndex);

  abstract public Point3f[] getPolymerLeadMidPoints(int modelIndex, int polymerIndex);
  
  abstract public boolean getAxesOrientationRasmol();
  abstract public int getPercentVdwAtom();

  abstract public boolean getAutoBond();

  abstract public short getMadBond();

  abstract public float getBondTolerance();

  abstract public void rebond();

  abstract public float getMinBondDistance();

  abstract public void refresh(int isOrientationChange, String strWhy);

  abstract public boolean showModelSetDownload();
  
  abstract public void notifyViewerRepaintDone();

  abstract public boolean getBooleanProperty(String propertyName);
  /**
   * @param key 
   * @param doICare IGNORED  
   * @return T/F
   */
  public boolean getBooleanProperty(String key, boolean doICare) {
    return getBooleanProperty(key); // don't ask for what doesn't exist; you should care!
  }
  abstract public Object getParameter(String name);

  abstract public String getSetHistory(int howFarBack);
  
  abstract public boolean havePartialCharges();

  abstract public boolean isApplet();

  abstract public String getAltLocListInModel(int modelIndex);

  abstract public String getStateInfo();
  
  abstract public void syncScript(String script, String applet);  

  //but NOTE that if you use the following, you are
  //bypassing the script history. If you want a script history, use
  //viewer.script("set " + propertyName + " " + value);
  
  abstract public void setColorBackground(String colorName);
  abstract public void setShowAxes(boolean showAxes);
  abstract public void setShowBbcage(boolean showBbcage);
  abstract public void setJmolDefaults();
  abstract public void setRasmolDefaults();

  abstract public void setBooleanProperty(String propertyName, boolean value);
  abstract public void setIntProperty(String propertyName, int value);
  abstract public void setFloatProperty(String propertyName, float value);
  abstract public void setStringProperty(String propertyName, String value);

  abstract public void setModeMouse(int modeMouse); //only MOUSEMODE_NONE, prior to nulling viewer

  abstract public void setShowHydrogens(boolean showHydrogens);
  abstract public void setShowMeasurements(boolean showMeasurements);
  abstract public void setPerspectiveDepth(boolean perspectiveDepth);
  abstract public void setAutoBond(boolean autoBond);
  abstract public void setMarBond(short marBond);
  abstract public void setBondTolerance(float bondTolerance);
  abstract public void setMinBondDistance(float minBondDistance);
  abstract public void setAxesOrientationRasmol(boolean axesMessedUp);
  abstract public void setPercentVdwAtom(int percentVdwAtom);
  
  //for each of these the script equivalent is shown  
  abstract public void setAnimationFps(int framesPerSecond);
  //viewer.script("animation fps x.x")
  abstract public void setFrankOn(boolean frankOn);
  //viewer.script("frank on")
  abstract public void setDebugScript(boolean debugScript);
  //viewer.script("set logLevel 5/4")
  //viewer.script("set debugScript on/off")
  abstract public void deleteMeasurement(int i);
  //viewer.script("measures delete " + (i + 1));
  abstract public void clearMeasurements();
  //viewer.script("measures delete");
  abstract public void setVectorScale(float vectorScaleValue);
  //viewer.script("vector scale " + vectorScaleValue);
  abstract public void setVibrationScale(float vibrationScaleValue);
  //viewer.script("vibration scale " + vibrationScaleValue);
  abstract public void setVibrationPeriod(float vibrationPeriod);
  //viewer.script("vibration " + vibrationPeriod);
  abstract public void selectAll();
  //viewer.script("select all");
  abstract public void clearSelection();
  //viewer.script("select none");
  //viewer.script("select ({2 3:6})");
  abstract public void setSelectionSet(BitSet newSelection);
  //viewer.script("selectionHalos ON"); //or OFF
  abstract public void setSelectionHalos(boolean haloEnabled);
  //viewer.script("center (selected)");
  abstract public void setCenterSelected(); 

  //not used in Jmol application:
  
  abstract public void rotateFront();
  // "To" was removed in the next, because they don't 
  // rotate "TO" anything. They just rotate.
  
  abstract public void rotateX(int degrees);
  abstract public void rotateY(int degrees);
  abstract public void rotateX(float radians);
  abstract public void rotateY(float radians);
  abstract public void rotateZ(float radians);

  abstract public JmolAdapter getModelAdapter();

  abstract public void openFileAsynchronously(String fileName);
  abstract public Object getFileAsBytes(String fullPathName, OutputStream os);

  abstract public String getErrorMessage();
  abstract public String getErrorMessageUntranslated();

  abstract public String getModelFileName(int modelIndex);

  /**
   * @param privateKey  
   * @return T/F
   */
  abstract public boolean checkPrivateKey(double privateKey);
  
  public String menuStructure;

}

