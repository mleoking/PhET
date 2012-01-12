/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-12-18 10:29:29 -0600 (Mon, 18 Dec 2006) $
 * $Revision: 6502 $
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

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.AxisAngle4f;

import org.jmol.g3d.Text3D;
import org.jmol.script.Token;
import org.jmol.util.Escape;
import org.jmol.util.Logger;

import org.jmol.util.Quaternion;

import java.util.BitSet;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

abstract class TransformManager {

  Viewer viewer;

  final static double twoPI = 2 * Math.PI;
  final static double degreesPerRadian = 180 / Math.PI;

  static final int DEFAULT_NAV_FPS = 10;

  protected int perspectiveModel = DEFAULT_PERSPECTIVE_MODEL;
  protected float cameraScaleFactor;
  protected float referencePlaneOffset;
  protected float aperatureAngle;
  protected float cameraDistanceFromCenter;
  protected float modelCenterOffset;
  protected float modelRadius;
  protected float modelRadiusPixels;
  
  protected final Point3f navigationCenter = new Point3f();
  protected final Point3f navigationOffset = new Point3f();
  protected final Point3f navigationShiftXY = new Point3f();

  protected final Matrix4f matrixTemp = new Matrix4f();
  protected final Vector3f vectorTemp = new Vector3f();

  /**
   * sets all camera and scale factors needed by 
   * the specific perspective model instantiated
   *
   */
  abstract protected void calcCameraFactors();

  /**
   * calculate the perspective factor based on z
   * @param z
   * @return perspectiveFactor
   */
  abstract protected float getPerspectiveFactor(float z);

  /**
   * adjusts the temporary point for perspective and offsets
   * 
   */
  abstract void adjustTemporaryScreenPoint();

  TransformManager() {
  }

  TransformManager(Viewer viewer) {
    this.viewer = viewer;
  }

  TransformManager(Viewer viewer, int width, int height) {
    setViewer(viewer, width, height);
  }

  void setViewer(Viewer viewer, int width, int height) {
    this.viewer = viewer;
    setScreenParameters(width, height, true, false, true, true);
  }

  TransformManager getNavigationManager(Viewer viewer, int width, int height) {
    TransformManager t = new TransformManager11();
    t.setViewer(viewer, width, height);
    return t;
  }

  /* ***************************************************************
   * GENERAL METHODS
   ***************************************************************/

  void homePosition(boolean resetSpin) {
    // reset, setNavigationMode, setPerspectiveModel
    if (resetSpin)
      setSpinOn(false);
    setNavOn(false);
    navFps = DEFAULT_NAV_FPS;
    navX = navY = navZ = 0;
    rotationCenterDefault.set(viewer.getBoundBoxCenter());
    setFixedRotationCenter(rotationCenterDefault);
    rotationRadiusDefault = setRotationRadius(0, true);
    windowCentered = true;
    setRotationCenterAndRadiusXYZ(null, true);
    matrixRotate.setIdentity(); // no rotations
    //if (viewer.autoLoadOrientation()) {
      Matrix3f m = (Matrix3f) viewer
          .getModelSetAuxiliaryInfo("defaultOrientationMatrix");
      if (m != null)
        matrixRotate.set(m);
    //}
    setZoomEnabled(true);
    zoomToPercent(viewer.isModelKitMode() ? 50 : 100);
    zoomPercent = zoomPercentSetting;
    slabReset();
    scaleFitToScreen(true);
    if (viewer.isJmolDataFrame()) {
      fixedRotationCenter.set(0, 0, 0);
    } else {
      if (viewer.getAxesOrientationRasmol())
        rotateX((float) Math.PI);
    }
    viewer.saveOrientation("default");
    if (mode == MODE_NAVIGATION)
      setNavigationMode(true);
  }

  void clear() {
    clearVibration();
    clearSpin();
    stopMotion();
    fixedRotationCenter.set(0, 0, 0);
    navigating = false;
    slabPlane = null;
    depthPlane = null;
    resetNavigationPoint(true);
  }

  String getState(StringBuffer sfunc) {
    StringBuffer commands = new StringBuffer("");
    if (sfunc != null) {
      sfunc.append("  _setPerspectiveState;\n");
      commands.append("function _setPerspectiveState() {\n");
    }
    StateManager
        .appendCmd(commands, "set perspectiveModel " + perspectiveModel);
    StateManager.appendCmd(commands, "set scaleAngstromsPerInch "
        + scale3DAngstromsPerInch);
    StateManager
        .appendCmd(commands, "set perspectiveDepth " + perspectiveDepth);
    StateManager.appendCmd(commands, "set visualRange " + visualRange);
    if (!isWindowCentered())
      StateManager.appendCmd(commands, "set windowCentered false");
    StateManager.appendCmd(commands, "set cameraDepth " + cameraDepth);
    if (mode == MODE_NAVIGATION)
      StateManager.appendCmd(commands, "set navigationMode true");
    StateManager.appendCmd(commands, viewer.getBoundBoxCommand(false));
    StateManager.appendCmd(commands, "center "
        + Escape.escape(fixedRotationCenter));
    commands.append(viewer.getSavedOrienationText(null));
    
    
    StateManager.appendCmd(commands, getMoveToText(0, false));
    if (stereoMode != JmolConstants.STEREO_NONE)
      StateManager.appendCmd(commands, "stereo "
          + (stereoColors == null ? 
              JmolConstants.getStereoModeName(stereoMode)
              : Escape.escapeColor(stereoColors[0])
                  + " " + Escape.escapeColor(stereoColors[1]))
          + " " + stereoDegrees);
    if (mode != MODE_NAVIGATION && !zoomEnabled)
      StateManager.appendCmd(commands, "zoom off");
    commands.append("  slab ").append(slabPercentSetting).append(";depth ")
        .append(depthPercentSetting).append(
            slabEnabled && mode != MODE_NAVIGATION ? ";slab on" : "").append(";\n");
    commands.append("  set slabRange ").append(slabRange).append(";\n");
    if (zShadeEnabled)
      commands.append("  set zShade;\n");
    if (slabPlane != null)
      commands.append("  slab plane ").append(Escape.escape(slabPlane)).append(
          ";\n");
    if (depthPlane != null)
      commands.append("  depth plane ").append(Escape.escape(depthPlane))
          .append(";\n");
    commands.append(getSpinState(true)).append("\n");
    if (viewer.modelSetHasVibrationVectors() && vibrationOn) 
      StateManager.appendCmd(commands, "set vibrationPeriod " + vibrationPeriod + ";vibration on");
    if (mode == MODE_NAVIGATION) {
      commands.append(getNavigationState());
      if (depthPlane != null || slabPlane != null)
        commands.append("  slab on;\n");
    }
    if (sfunc != null)
      commands.append("}\n\n");
    return commands.toString();
  }

  /**
   * @param isAll  
   * @return spin state
   */
  String getSpinState(boolean isAll) {
    String s = "  set spinX " + (int) spinX + "; set spinY " + (int) spinY
        + "; set spinZ " + (int) spinZ + "; set spinFps " + (int) spinFps + ";";
    if (!Float.isNaN(navFps))
      s += "  set navX " + (int) navX + "; set navY " + (int) navY
          + "; set navZ " + (int) navZ + "; set navFps " + (int) navFps + ";";
    if (navOn)
      s += " navigation on;";
    if (!spinOn)
      return s;
    String prefix = (isSpinSelected ? "\n  select "
        + Escape.escape(viewer.getSelectionSet(false)) + ";\n  rotateSelected"
        : "\n ");
    if (isSpinInternal) {
      Point3f pt = new Point3f(internalRotationCenter);
      pt.sub(rotationAxis);
      s += prefix + " spin " + rotationRate + " "
          + Escape.escape(internalRotationCenter) + " " + Escape.escape(pt);
    } else if (isSpinFixed) {
      s += prefix + " spin axisangle " + Escape.escape(rotationAxis) + " "
          + rotationRate;
    } else {
      s += " spin on";
    }
    return s + ";";
  }

  protected boolean haveNotifiedNaN = false;

  final static int DEFAULT_SPIN_Y = 30;
  final static int DEFAULT_SPIN_FPS = 30; 
  float spinX, spinY = DEFAULT_SPIN_Y, spinZ, spinFps = DEFAULT_SPIN_FPS;
  protected float navX;
  protected float navY;
  protected float navZ;
  protected float navFps = Float.NaN;

  boolean isSpinInternal = false;
  boolean isSpinFixed = false;
  boolean isSpinSelected = false;

  protected final Point3f fixedRotationOffset = new Point3f();
  protected final Point3f fixedRotationCenter = new Point3f();
  protected final Point3f perspectiveOffset = new Point3f();
  protected final Point3f perspectiveShiftXY = new Point3f();

  private final Point3f rotationCenterDefault = new Point3f();
  private float rotationRadiusDefault;

  protected final AxisAngle4f fixedRotationAxis = new AxisAngle4f();
  protected final AxisAngle4f internalRotationAxis = new AxisAngle4f();
  protected Vector3f internalTranslation;
  private final Point3f internalRotationCenter = new Point3f(0, 0, 0);
  private float internalRotationAngle = 0;

  /* ***************************************************************
   * ROTATIONS
   ***************************************************************/

  // this matrix only holds rotations ... no translations
  protected final Matrix3f matrixRotate = new Matrix3f();

  private final Matrix3f matrixTemp3 = new Matrix3f();
  private final Matrix4f matrixTemp4 = new Matrix4f();
  private final AxisAngle4f axisangleT = new AxisAngle4f();
  private final Vector3f vectorT = new Vector3f();
  private final Vector3f vectorT2 = new Vector3f();
  private final Point3f pointT2 = new Point3f();

  final static int MAXIMUM_ZOOM_PERCENTAGE = 200000;
  final static int MAXIMUM_ZOOM_PERSPECTIVE_DEPTH = 10000;

  private boolean rotateMolecule;

  void setRotateMolecule(boolean TF) {
    rotateMolecule = TF;
  }

  private void setFixedRotationCenter(Point3f center) {
    if (center == null)
      return;
    fixedRotationCenter.set(center);
  }

  void setRotationPointXY(Point3f center) {
    Point3i newCenterScreen = transformPoint(center);
    fixedTranslation.set(newCenterScreen.x, newCenterScreen.y, 0);
  }

  Vector3f rotationAxis = new Vector3f();
  float rotationRate = 0;

  void spinXYBy(int xDelta, int yDelta, float speed) {
    // from mouse action
    if (xDelta == 0 && yDelta == 0) {
      if (spinThread != null && spinThread.isGesture)
        clearSpin();
      return;
    }
    clearSpin();
    Point3f pt1 = new Point3f(fixedRotationCenter);
    Point3f ptScreen = new Point3f();
    transformPoint(pt1, ptScreen);
    Point3f pt2 = new Point3f(-yDelta, xDelta, 0);
    pt2.add(ptScreen);
    unTransformPoint(pt2, pt2);
    viewer.setInMotion(false);
    rotateAboutPointsInternal(pt2, pt1, 10 * speed, Float.NaN, false, true, null, true, null, null);
  }

  final Vector3f arcBall0 = new Vector3f();
  final Vector3f arcBall1 = new Vector3f();
  final Vector3f arcBallAxis = new Vector3f();
  final Matrix3f arcBall0Rotation = new Matrix3f();

  void rotateArcBall(float x, float y, float factor) {
    // radius is half the screen pixel count. 
    float radius2 = (screenPixelCount >> 2) * screenPixelCount;
    x -= fixedTranslation.x;
    y -= fixedTranslation.y;
    float z = radius2 - x * x - y * y;
    z = (z < 0 ? -1 : 1) * (float) Math.sqrt(Math.abs(z));
    if (factor == 0) {
      // mouse down sets the initial rotation and point on the sphere
      arcBall0Rotation.set(matrixRotate);
      arcBall0.set(x, -y, z);
      if (!Float.isNaN(z))
        arcBall0.normalize();
      return;
    }
    if (Float.isNaN(arcBall0.z) || Float.isNaN(z))
      return;
    arcBall1.set(x, -y, z);
    arcBall1.normalize();
    arcBallAxis.cross(arcBall0, arcBall1);
    axisangleT.set(arcBallAxis, factor * (float) Math.acos(arcBall0.dot(arcBall1)));
    matrixRotate.set(arcBall0Rotation);
    rotateAxisAngle(axisangleT, null);
  }

  void rotateXYBy(float xDelta, float yDelta, BitSet bsAtoms) {
    // from mouse action
    rotateXRadians(yDelta * JmolConstants.radiansPerDegree, bsAtoms);
    rotateYRadians(xDelta * JmolConstants.radiansPerDegree, bsAtoms);
  }

  void rotateZBy(int zDelta, int x, int y) {
    if (x != Integer.MAX_VALUE && y != Integer.MAX_VALUE)
      resetXYCenter(x, y);
    rotateZRadians((float) (zDelta / degreesPerRadian));
  }

  void rotateFront() {
    matrixRotate.setIdentity();
  }

  void rotateX(float angleRadians) {
    matrixRotate.rotX(angleRadians);
  }

  void rotateY(float angleRadians) {
    matrixRotate.rotY(angleRadians);
  }

  void rotateZ(float angleRadians) {
    matrixRotate.rotZ(angleRadians);
  }

  private void applyRotation(Matrix3f mNew, boolean isInternal, BitSet bsAtoms) {
    if (bsAtoms == null) {
      matrixRotate.mul(mNew, matrixRotate);
      return;
    }
    viewer.rotateAtoms(mNew, matrixRotate, rotateMolecule,
        internalRotationCenter, isInternal, bsAtoms);
    if (internalTranslation != null) {
      viewer.setAtomCoordRelative(internalTranslation, bsAtoms);
      internalRotationCenter.add(internalTranslation);
    }
  }

  synchronized void rotateXRadians(float angleRadians, BitSet bsAtoms) {
    matrixTemp3.rotX(angleRadians);
    applyRotation(matrixTemp3, false, bsAtoms);
  }

  synchronized void rotateYRadians(float angleRadians, BitSet bsAtoms) {
    matrixTemp3.rotY(angleRadians);
    applyRotation(matrixTemp3, false, bsAtoms);
  }

  synchronized void rotateZRadians(float angleRadians) {
    matrixTemp3.rotZ(angleRadians);
    applyRotation(matrixTemp3, false, null);
  }

  protected void rotateAxisAngle(Vector3f rotAxis, float radians) {
    axisangleT.set(rotAxis, radians);
    rotateAxisAngle(axisangleT, null);
  }

  synchronized void rotateAxisAngle(AxisAngle4f axisAngle, BitSet bsAtoms) {
    //matrixTemp3.setIdentity();
    matrixTemp3.set(axisAngle);
    applyRotation(matrixTemp3, false, bsAtoms);
  }

  /*
   * *************************************************************** *THE* TWO
   * VIEWER INTERFACE METHODS
   * **************************************************************
   */

  void rotateAxisAngleAtCenter(Point3f rotCenter, Vector3f rotAxis,
                               float degreesPerSecond, float endDegrees,
                               boolean isSpin, BitSet bsAtoms) {

    // *THE* Viewer FIXED frame rotation/spinning entry point
    if (rotCenter != null)
      moveRotationCenter(rotCenter, true);

    setSpinOn(false);
    setNavOn(false);

    if (Float.isNaN(degreesPerSecond) || degreesPerSecond == 0)
      return;

    if (rotCenter != null) {
      setRotationPointXY(rotCenter);
    }
    setFixedRotationCenter(rotCenter);
    rotationAxis.set(rotAxis);
    rotationRate = degreesPerSecond;
    if (isSpin) {
      fixedRotationAxis.set(rotAxis, degreesPerSecond * JmolConstants.radiansPerDegree);
      isSpinInternal = false;
      isSpinFixed = true;
      isSpinSelected = (bsAtoms != null);
      setSpinOn(true, endDegrees, null, bsAtoms, false);
      return;
    }
    float radians = endDegrees * JmolConstants.radiansPerDegree;
    fixedRotationAxis.set(rotAxis, endDegrees);
    rotateAxisAngleRadiansFixed(radians, bsAtoms);
  }

  synchronized void rotateAxisAngleRadiansFixed(float angleRadians,
                                                BitSet bsAtoms) {
    // for spinning -- reduced number of radians
    axisangleT.set(fixedRotationAxis);
    axisangleT.angle = angleRadians;
    rotateAxisAngle(axisangleT, bsAtoms);
  }

  /*
   * *************************************************************** INTERNAL
   * ROTATIONS**************************************************************
   */

  void rotateAboutPointsInternal(Point3f point1, Point3f point2,
                                 float degreesPerSecond, float endDegrees,
                                 boolean isClockwise, boolean isSpin,
                                 BitSet bsAtoms, boolean isGesture,
                                 Vector3f translation, List<Point3f> finalPoints) {

    // *THE* Viewer INTERNAL frame rotation entry point

    setSpinOn(false);
    setNavOn(false);

    if ((translation == null || translation.length() < 0.001)
        && (!isSpin || endDegrees == 0 || Float.isNaN(degreesPerSecond) || degreesPerSecond == 0)
        && (isSpin || endDegrees == 0))
      return;

    Vector3f axis = new Vector3f(point2);
    axis.sub(point1);
    if (isClockwise)
      axis.scale(-1f);
    internalRotationCenter.set(point1);
    rotationAxis.set(axis);
    rotationRate = degreesPerSecond;
    if (translation == null) {
      internalTranslation = null;
    } else {
      internalTranslation = new Vector3f(translation);
      //System.out.println("TM TRANSLATE " + internalTranslation);
    }
    boolean isSelected = (bsAtoms != null);
    if (isSpin) {
      // we need to adjust the degreesPerSecond to match a multiple of the frame rate
      //System.out.println(" ?? " + (spinFps / Math.abs(degreesPerSecond) * Math.abs(endDegrees)));
      int nFrames = (int) (Math.abs(endDegrees) / Math.abs(degreesPerSecond) * spinFps + 0.5);
      if (!Float.isNaN(endDegrees)) {
        rotationRate = degreesPerSecond = endDegrees / nFrames * spinFps;
        //System.out.println("TM nFrames = " + nFrames);
        if (translation != null)
        internalTranslation.scale(1f / (nFrames));
      }
      internalRotationAxis.set(axis, rotationRate
          * JmolConstants.radiansPerDegree);
      isSpinInternal = true;
      isSpinFixed = false;
      isSpinSelected = isSelected;
      setSpinOn(true, endDegrees, finalPoints, bsAtoms, isGesture);
      return;
    }
    float radians = endDegrees * JmolConstants.radiansPerDegree;
    internalRotationAxis.set(axis, radians);
    rotateAxisAngleRadiansInternal(radians, bsAtoms);
  }

  synchronized void rotateAxisAngleRadiansInternal(float radians, BitSet bsAtoms) {

    // final matrix rotation when spinning or just rotating

    // trick is to apply the current rotation to the internal rotation axis
    // and then save the angle for generating a new fixed point later

    internalRotationAngle = radians;
    vectorT.set(internalRotationAxis.x, internalRotationAxis.y,
        internalRotationAxis.z);
    matrixRotate.transform(vectorT, vectorT2);
    axisangleT.set(vectorT2, radians);

    // NOW apply that rotation  

    matrixTemp3.set(axisangleT);
    applyRotation(matrixTemp3, true, bsAtoms);
    if (bsAtoms == null)
      getNewFixedRotationCenter();
  }

  void getNewFixedRotationCenter() {

    /*
     * (1) determine vector offset VectorT () 
     * (2) translate old point so trueRotationPt is at [0,0,0] (old - true)
     * (3) do axisangle rotation of -radians (pointT2)
     * (4) translate back (pointT2 + vectorT)
     * 
     * The new position of old point is the new rotation center
     * set this, rotate about it, and it will APPEAR that the 
     * rotation was about the desired point and axis!
     *  
     */

    // fractional OPPOSITE of angle of rotation
    axisangleT.set(internalRotationAxis);
    axisangleT.angle = -internalRotationAngle;
    //this is a fraction of the original for spinning
    matrixTemp4.set(axisangleT);

    // apply this to the fixed center point in the internal frame

    vectorT.set(internalRotationCenter);
    pointT2.set(fixedRotationCenter);
    pointT2.sub(vectorT);
    Point3f pt = new Point3f();
    matrixTemp4.transform(pointT2, pt);

    // return this point to the fixed frame

    pt.add(vectorT);

    // it is the new fixed rotation center!

    setRotationCenterAndRadiusXYZ(pt, false);
  }

  /* ***************************************************************
   * TRANSLATIONS
   ****************************************************************/
  protected final Point3f fixedTranslation = new Point3f();
  

  float xTranslationFraction = 0.5f;
  float yTranslationFraction = 0.5f;
  protected float prevZoomSetting, previousX, previousY;


  void setTranslationFractions() {
    xTranslationFraction = fixedTranslation.x / width;
    yTranslationFraction = fixedTranslation.y / height;
  }

  public void centerAt(int x, int y, Point3f pt) {
    if (pt == null) {
      translateXYBy(x, y);
      return;
    }
    if (windowCentered)
      viewer.setBooleanProperty("windowCentered", false);
    //transformPoint(pt, pointT);
    //float z = pointT.z;
    fixedTranslation.x = x;
    fixedTranslation.y = y;
    setFixedRotationCenter(pt);
    //transformPoint(pt, pointT);
    //finalizeTransformParameters();
    //System.out.println("trans man ");
  }

  int percentToPixels(char xyz, float percent) {
    switch(xyz) {
    case 'x':
      return (int) (percent / 100 * width);
    case 'y':
      return (int) (percent / 100 * height);
    case 'z':
      return (int) (percent / 100 * screenPixelCount);
    }
    return 0;
  }

  int angstromsToPixels(float distance) {
    return (int) (scalePixelsPerAngstrom * distance);
  }


  void translateXYBy(int xDelta, int yDelta) {
    // mouse action or translate x|y|z x.x nm|angstroms|%
    fixedTranslation.x += xDelta;
    fixedTranslation.y += yDelta;
    setTranslationFractions();
  }

  void translateToPercent(char type, float percent) {
    switch (type) {
    case 'x':
      xTranslationFraction = 0.5f + percent / 100;
      fixedTranslation.x = width * xTranslationFraction;
      return;
    case 'y':
      yTranslationFraction = 0.5f + percent / 100;
      fixedTranslation.y = height * yTranslationFraction;
      return;
    case 'z':
      if (mode == MODE_NAVIGATION)
        setNavigationDepthPercent(0, percent);
      return;
    }
  }

  float getTranslationXPercent() {
    return (fixedTranslation.x - width / 2) * 100 / width;
  }

  float getTranslationYPercent() {
    return (fixedTranslation.y - height / 2) * 100 / height;
  }

  float getTranslationZPercent() {
    return 0;
  }

  String getTranslationScript() {
    String info = "";
    float f = getTranslationXPercent();
    if (f != 0.0)
      info += "translate x " + f + ";";
    f = getTranslationYPercent();
    if (f != 0.0)
      info += "translate y " + f + ";";
    return info;
  }

  String getOrientationText(int type) {
    switch (type) {
    case Token.moveto:
      return getMoveToText(1, false);
    case Token.rotation:
      return getRotationQuaternion().toString();
    case Token.translation:
      StringBuffer sb = new StringBuffer();
      truncate2(sb, getTranslationXPercent());
      truncate2(sb, getTranslationYPercent());
      return sb.toString();
    default:
      return getMoveToText(1, true) + "\n#OR\n" + getRotateZyzText(true);

    }
  }

  Map<String, Object> getOrientationInfo() {
    Map<String, Object> info = new Hashtable<String, Object>();
    info.put("moveTo", getMoveToText(1, false));
    info.put("center", "center " + getCenterText());
    info.put("centerPt", fixedRotationCenter);
    AxisAngle4f aa = new AxisAngle4f();
    getAxisAngle(aa);
    info.put("axisAngle", aa);
    info.put("quaternion", new Quaternion(aa).toPoint4f());
    info.put("rotationMatrix", matrixRotate);
    info.put("rotateZYZ", getRotateZyzText(false));
    info.put("rotateXYZ", getRotateXyzText());
    info.put("transXPercent", new Float(getTranslationXPercent()));
    info.put("transYPercent", new Float(getTranslationYPercent()));
    info.put("zoom", new Float(zoomPercent));
    info.put("modelRadius", new Float(modelRadius));
    if (mode == MODE_NAVIGATION) {
      info.put("navigationCenter", "navigate center "
          + Escape.escape(navigationCenter));
      info.put("navigationOffsetXPercent", new Float(
          getNavigationOffsetPercent('X')));
      info.put("navigationOffsetYPercent", new Float(
          getNavigationOffsetPercent('Y')));
      info
          .put("navigationDepthPercent", new Float(getNavigationDepthPercent()));
    }
    return info;
  }

  void getAxisAngle(AxisAngle4f axisAngle) {
    axisAngle.set(matrixRotate);
  }

  String getTransformText() {
    return matrixRotate.toString();
  }

  Matrix3f getMatrixRotate() {
    return matrixRotate;
  }

  void setRotation(Matrix3f matrixRotation) {
    if (!Float.isNaN(matrixRotation.m00))
      matrixRotate.set(matrixRotation);
  }

  void getRotation(Matrix3f matrixRotation) {
    // hmm ... I suppose that there could be a race condiditon here
    // if matrixRotate is being modified while this is called
    matrixRotation.set(matrixRotate);
  }

  /* ***************************************************************
   * ZOOM
   ****************************************************************/
  boolean zoomEnabled = true;
  // zoomPercent is the current displayed zoom value
  float zoomPercent = 100;
  // zoomPercentSetting is the current setting of zoom
  // if zoom is not enabled then the two values will be different
  float zoomPercentSetting = 100;
  float zoomRatio;

  /**
   * standard response to user mouse vertical shift-drag
   * 
   * @param pixels
   */
  protected void zoomBy(int pixels) {
    if (pixels > 20)
      pixels = 20;
    else if (pixels < -20)
      pixels = -20;
    float deltaPercent = pixels * zoomPercentSetting / 50;
    if (deltaPercent == 0)
      deltaPercent = (pixels > 0 ? 1 : (deltaPercent < 0 ? -1 : 0));
    zoomRatio = (deltaPercent + zoomPercentSetting) / zoomPercentSetting;
    zoomPercentSetting += deltaPercent;
  }

  float getZoomPercentFloat() {
    return zoomPercent;
  }

  void zoomToPercent(float percentZoom) {
    zoomPercentSetting = percentZoom;
    zoomRatio = 0;
  }

  void translateZBy(int pixels) {
    if (pixels >= screenPixelCount)
      return;
    float sppa = scalePixelsPerAngstrom / (1 - pixels * 1.0f / screenPixelCount);
    if (sppa >= screenPixelCount)
      return;
    float newZoomPercent = sppa / scaleDefaultPixelsPerAngstrom * 100f;
    zoomRatio = newZoomPercent / zoomPercentSetting;
    zoomPercentSetting = newZoomPercent;
  }
  
  void zoomByFactor(float factor, int x, int y) {
    // overloaded in TransformManager11
    if (factor <= 0 || !zoomEnabled)
      return;
    zoomRatio = factor;
    zoomPercentSetting *= factor;
    resetXYCenter(x, y);
  }

  private void resetXYCenter(int x, int y) {
    if (x == Integer.MAX_VALUE || y == Integer.MAX_VALUE)
      return;
    if (windowCentered)
      viewer.setBooleanProperty("windowCentered", false);
    Point3f pt = new Point3f();
    transformPoint(fixedRotationCenter, pt);
    pt.set(x, y, pt.z);
    unTransformPoint(pt, pt);
    fixedTranslation.set(x, y, 0);
    setFixedRotationCenter(pt);
  }

  void zoomByPercent(float percentZoom) {
    float deltaPercent = percentZoom * zoomPercentSetting / 100;
    if (deltaPercent == 0)
      deltaPercent = (percentZoom < 0) ? -1 : 1;
    zoomRatio = (deltaPercent + zoomPercentSetting) / zoomPercentSetting;
    zoomPercentSetting += deltaPercent;
  }

  void setScaleAngstromsPerInch(float angstromsPerInch) {
    // not compatible with perspectiveDepth
    scale3D = (angstromsPerInch > 0);
    if (scale3D)
      scale3DAngstromsPerInch = angstromsPerInch;
    perspectiveDepth = !scale3D;
  }

  /* ***************************************************************
   * SLAB
   ****************************************************************/

  /*
   slab is a term defined and used in rasmol.
   it is a z-axis clipping plane. only atoms behind the slab get rendered.
   100% means:
   - the slab is set to z==0
   - 100% of the molecule will be shown
   50% means:
   - the slab is set to the center of rotation of the molecule
   - only the atoms behind the center of rotation are shown
   0% means:
   - the slab is set behind the molecule
   - 0% (nothing, nada, nil, null) gets shown
   */

  boolean slabEnabled = false;
  boolean internalSlab = false;
  boolean zShadeEnabled = false;

  int slabPercentSetting;
  int depthPercentSetting;
  int zSlabPercentSetting; // from global.zSlab
  int zDepthPercentSetting;// from global.zDepth
  
  int slabValue;
  int depthValue;
  int zSlabValue;
  int zDepthValue;

  float slabRange = 0f;

  public void setSlabRange(float value) {
    slabRange = value;    
  }

  void setSlabEnabled(boolean slabEnabled) {
    this.slabEnabled = slabEnabled;
    viewer.getGlobalSettings().setParameterValue("slabEnabled", slabEnabled);
  }

  void setZShadeEnabled(boolean zShadeEnabled) {
    this.zShadeEnabled = zShadeEnabled;
    viewer.getGlobalSettings().setParameterValue("zShade", zShadeEnabled);
  }

  void setZoomEnabled(boolean zoomEnabled) {
    this.zoomEnabled = zoomEnabled;
    viewer.getGlobalSettings().setParameterValue("zoomEnabled", zoomEnabled);
  }

  Point4f slabPlane = null;
  Point4f depthPlane = null;

  void slabReset() {
    slabToPercent(100);
    depthToPercent(0);
    depthPlane = null;
    slabPlane = null;
    setSlabEnabled(false);
    setZShadeEnabled(false);
  }

  int getSlabPercentSetting() {
    return slabPercentSetting;
  }

  void slabByPercentagePoints(int percentage) {
    slabPlane = null;
    slabPercentSetting += percentage;
    slabDepthChanged();
    if (depthPercentSetting >= slabPercentSetting)
      depthPercentSetting = slabPercentSetting - 1;
//    System.out.println("transformManager slab/depthpercentSetting: " + slabPercentSetting + " " + depthPercentSetting);
  }

  private void slabDepthChanged() {
    viewer.getGlobalSettings().setParameterValue("slab", slabPercentSetting);
    viewer.getGlobalSettings().setParameterValue("depth", depthPercentSetting);
  }

  void depthByPercentagePoints(int percentage) {
    depthPlane = null;
    depthPercentSetting += percentage;
    if (slabPercentSetting <= depthPercentSetting)
      slabPercentSetting = depthPercentSetting + 1;
    slabDepthChanged();
//    System.out.println("transformManager slab/depthpercentSetting: " + slabPercentSetting + " " + depthPercentSetting);
  }

  void slabDepthByPercentagePoints(int percentage) {
    slabPlane = null;
    depthPlane = null;
    slabPercentSetting += percentage;
    depthPercentSetting += percentage;
    slabDepthChanged();
//    System.out.println("transformManager slab/depthpercentSetting: " + slabPercentSetting + " " + depthPercentSetting);
  }

  void slabToPercent(int percentSlab) {
    viewer.setFloatProperty("slabRange", 0);
    slabPercentSetting = percentSlab;
    slabPlane = null;
    if (depthPercentSetting >= slabPercentSetting)
      depthPercentSetting = slabPercentSetting - 1;
    slabDepthChanged();
  }

  void depthToPercent(int percentDepth) {
    viewer.getGlobalSettings().setParameterValue("depth", percentDepth);
    depthPercentSetting = percentDepth;
    if (slabPercentSetting <= depthPercentSetting)
      slabPercentSetting = depthPercentSetting + 1;
    slabDepthChanged();
  }

  void zSlabToPercent(int percentSlab) {
    zSlabPercentSetting = percentSlab;
    if (zDepthPercentSetting > zSlabPercentSetting)
      zDepthPercentSetting = percentSlab;
  }

  void zDepthToPercent(int percentDepth) {
    zDepthPercentSetting = percentDepth;
    if (zDepthPercentSetting > zSlabPercentSetting)
      zSlabPercentSetting = percentDepth;
  }

  void slabInternal(Point4f plane, boolean isDepth) {
    //also from viewer
    if (isDepth) {
      depthPlane = plane;
      depthPercentSetting = 0;
    } else {
      slabPlane = plane;
      slabPercentSetting = 100;
    }
  }

  /**
   * set internal slab or depth from screen-based slab or depth
   * @param isDepth
   */
  void setSlabDepthInternal(boolean isDepth) {
    finalizeTransformParameters();
    if (isDepth)
      depthPlane = null;
    else
      slabPlane = null;
    slabInternal(getSlabDepthPlane(isDepth), isDepth);
  }

  Point4f getSlabDepthPlane(boolean isDepth) {
    // the third row of the matrix defines the Z coordinate, which is all we need
    // and, in fact, it defines the plane. How convenient!
    // eval "slab set"
    if (isDepth) {
      if (depthPlane != null)
        return depthPlane;
    } else {
      if (slabPlane != null)
        return slabPlane;
    }
    Matrix4f m = matrixTransform;
    return new Point4f(-m.m20, -m.m21, -m.m22, -m.m23
        + (isDepth ? depthValue : slabValue));
  }

  boolean checkInternalSlab(Point3f pt) {
    return (slabPlane != null
        && pt.x * slabPlane.x + pt.y * slabPlane.y + pt.z * slabPlane.z
            + slabPlane.w > 0 || depthPlane != null
        && pt.x * depthPlane.x + pt.y * depthPlane.y + pt.z * depthPlane.z
            + depthPlane.w < 0);
  }

  /* ***************************************************************
   * PERSPECTIVE
   ****************************************************************/

  /* Jmol treatment of perspective   Bob Hanson 12/06
   * 
   * See http://www.stolaf.edu/academics/chemapps/jmol/docs/misc/navigation.pdf
   * 


   DEFAULT SCALE -- (zoom == 100) 

   We start by defining a fixedRotationCenter and a modelRadius that encompasses 
   the model. Then: 

   defaultScalePixelsPerAngstrom = screenPixelCount / (2 * modelRadius)

   where:

   screenPixelCount is 2 less than the larger of height or width when zoomLarge == true
   and the smaller of the two when zoomLarge == false

   modelRadius is a rough estimate of the extent of the molecule.
   This pretty much makes a model span the window.

   This is applied as part of the matrixTransform.
   
   ADDING ZOOM
   
   For zoom, we just apply a zoom factor to the default scaling:
   
   scalePixelsPerAngstrom = zoom * defaultScalePixelsPerAngstrom
   
   
   ADDING PERSPECTIVE
   
   Imagine an old fashioned plate camera. The film surface is in front of the model 
   some distance. Lines of perspective go from the plate (our screen) to infinity 
   behind the model. We define:
   
   cameraDistance  -- the distance of the camera in pixels from the FRONT of the model.
   
   cameraDepth     -- a more scalable version of cameraDistance, 
   measured in multiples of screenPixelCount.
   
   The atom position is transformed into screen-based coordinates as:
   
   Z = modelCenterOffset + atom.z * zoom * defaultScalePixelsPerAngstrom

   where 
   
   modelCenterOffset = cameraDistance + screenPixelCount / 2
   
   Z is thus adjusted for zoom such that the center of the model stays in the same position.     
   Defining the position of a vertical plane p as:
   
   p = (modelRadius + zoom * atom.z) / (2 * modelRadius)

   and using the definitions above, we have:

   Z = cameraDistance + screenPixelCount / 2
   + zoom * atom.z * screenPixelCount / (2 * modelRadius)
   
   or, more simply:      
   
   Z = cameraDistance + p * screenPixelCount
   
   This will prove convenient for this discussion (but is never done in code).
   
   All perspective is, is the multiplication of the x and y coordinates by a scaling
   factor that depends upon this screen-based Z coordinate.
   
   We define:
   
   cameraScaleFactor = (cameraDepth + 0.5) / cameraDepth
   referencePlaneOffset = cameraDistance * cameraScaleFactor
   = (cameraDepth + 0.5) * screenPixelCount
   
   and the overall scaling as a function of distance from the camera is simply:
   
   f = perspectiveFactor = referencePlaneOffset / Z
   
   and thus using c for cameraDepth:

   f = (c + 0.5) * screenPixelCount / Z
   = (c + 0.5) * screenPixelCount / (c * screenPixelCount + p * screenPixelCount)
   
   and we simply have:
   
   f = (c + 0.5) / (c + p)
   
   Thus:

   when p = 0,   (front plane) the scale is cameraScaleFactor.
   when p = 0.5, (midplane) the scale is 1.
   when p = 1,   (rear plane) the scale is (cameraDepth + 0.5) / (cameraDepth + 1)
   
   as p approaches infinity, perspectiveFactor goes to 0; 
   if p goes negative, we ignore it. Those points won't be rendered.

   GRAPHICAL INTERPRETATION 
   
   The simplest way to see what is happening is to consider 1/f instead of f:
   
   1/f = (c + p) / (c + 0.5) = c / (c + 0.5) + p / (c + 0.5)
   
   This is a linear function of p, with 1/f=0 at p = -c, the camera position:
   
   
   
   \----------------0----------------/    midplane, p = 0.5, 1/f = 1
    \        model center           /     viewingRange = screenPixelCount
     \                             /
      \                           /
       \                         /
        \-----------------------/   front plane, p = 0, 1/f = c / (c + 0.5)
         \                     /    viewingRange = screenPixelCount / f
          \                   /
           \                 /
            \               /   The distance across is the distance that is viewable
             \             /    for this Z position. Just magnify a model and place its
              \           /     center at 0. Whatever part of the model is within the
               \         /      triangle will be viewed, scaling each distance so that
                \       /       it ends up screenWidthPixels wide.
                 \     /
                  \   /
                   \ /
                    X  camera position, p = -c, 1/f = 0
                       viewingRange = 0

   VISUAL RANGE
   
   We simply define a fixed visual range that can be seen by the observer. 
   That range is set at the referencePlaneOffset. Any point ahead of this plane is not shown. 

   VERSION 10
   
   In Jmol 10.2 there was a much more complicated formula for perspectiveFactor, namely
   (where "c" is the cameraDepth):
   
   cameraScaleFactor(old) = 1 + 0.5 / c + 0.02
   z = cameraDistance + (modelRadius + z0) * scalePixelsPerAngstrom * cameraScaleFactor * zoom

   Note that the zoom was being applied in such a way that changing the zoom also changed the
   model midplane position and that the camera scaling factor was being applied in the 
   matrix transformation. This lead to a very complicated but subtle error in perspective.
   
   This error was noticed by Charles Xie and amounts to only a few percent for the 
   cameraDepth that was fixed at 3 in Jmol 10.0. The error was 0 at the front of the model, 
   2% at the middle, and 3.5% at the back, roughly.

   Fixing this error now allows us to adjust cameraDepth at will and to do proper navigation.

   */

  protected boolean perspectiveDepth = true;
  protected boolean scale3D = false;
  protected float cameraDepth = Float.NaN;
  protected float cameraDepthSetting = 3f;
  protected float visualRange; // set in stateManager to 5f;
  protected float cameraDistance = 1000f; // prevent divide by zero on startup
  
  /**
   * This method returns data needed by the VRML, X3D, and IDTF/U3D exporters
   * It also should serve as a valuable resource for anyone adapting Jmol and 
   * wanting to know how the Jmol 11+ camera business works. 
   * 
   * @return a set of camera data
   */
  Point3f[] getCameraFactors() {
    aperatureAngle = (float) (Math.atan2(screenPixelCount / 2f,
        referencePlaneOffset) * 2 * 180 / Math.PI);
    cameraDistanceFromCenter = referencePlaneOffset / scalePixelsPerAngstrom;
    
    Point3f ptRef = new Point3f(screenWidth / 2, screenHeight / 2,
        referencePlaneOffset);
    unTransformPoint(ptRef, ptRef);

    // NOTE: Camera position will be approximate.
    // when the model has been shifted with CTRL-ALT
    // the center of distortion is not the screen center.
    // The simpler perspective model in VRML and U3D 
    // doesn't allow for that. (of course, one could argue,
    // that's because they are more REALISTIC). We do it
    // this way so that visual metrics in the model are preserved 
    // when the model is shifted using CTRL-ALT, and it was found
    // that if you didn't do that, moving the model was very odd
    // in that a fish-eye distortion was present as you moved it.
    
    // note that navigation mode should be EXACTLY reproduced
    // in these renderers. 
    
    
    Point3f ptCamera = new Point3f(screenWidth / 2, screenHeight / 2, 0);
    viewer.unTransformPoint(ptCamera, ptCamera);
    ptCamera.sub(fixedRotationCenter);
    Point3f pt = new Point3f(screenWidth / 2, screenHeight / 2, cameraDistanceFromCenter * scalePixelsPerAngstrom);
    viewer.unTransformPoint(pt, pt);
    pt.sub(fixedRotationCenter);
    ptCamera.add(pt);
/*
    System.out.println("TM no " + navigationOffset + " rpo "
        + referencePlaneOffset + " aa " + aperatureAngle + " sppa "
        + scalePixelsPerAngstrom + " vr " + visualRange + " sw/vr "
        + screenWidth / visualRange + " " + ptRef + " " + fixedRotationCenter);
*/    
    return new Point3f[] {
        ptRef,
        ptCamera,
        fixedRotationCenter,
        new Point3f(cameraDistanceFromCenter, aperatureAngle,
            scalePixelsPerAngstrom) };
  }
  
  int getFrontPlane() {
    return (int) cameraDistance;
  }

  void setPerspectiveDepth(boolean perspectiveDepth) {
    if (this.perspectiveDepth == perspectiveDepth)
      return;
    this.perspectiveDepth = perspectiveDepth;
    scaleFitToScreen(false);
  }

  boolean getPerspectiveDepth() {
    return perspectiveDepth;
  }

  /**
   * either as a percent -300, or as a float 3.0
   * note this percent is of zoom=100 size of model
   * 
   * @param percent
   */
  void setCameraDepthPercent(float percent) {
    resetNavigationPoint(true);
    float screenMultiples = (percent < 0 ? -percent / 100 : percent);
    if (screenMultiples == 0)
      return;
    cameraDepthSetting = screenMultiples;
    cameraDepth = Float.NaN;
  }

  void setVisualRange(float angstroms) {
    visualRange = angstroms;
  }

  Matrix4f getUnscaledTransformMatrix() {
    //for povray only
    Matrix4f unscaled = new Matrix4f();
    unscaled.setIdentity();
    vectorTemp.set(fixedRotationCenter);
    matrixTemp.setZero();
    matrixTemp.setTranslation(vectorTemp);
    unscaled.sub(matrixTemp);
    matrixTemp.set(matrixRotate);
    unscaled.mul(matrixTemp, unscaled);
    return unscaled;
  }

  /* ***************************************************************
   * SCREEN SCALING
   ****************************************************************/
  int width, height;
  int screenPixelCount;
  float scalePixelsPerAngstrom;
  float scaleDefaultPixelsPerAngstrom;
  float scale3DAngstromsPerInch;
  protected boolean antialias;
  private boolean useZoomLarge;

  int screenWidth, screenHeight;

  void setScreenParameters(int screenWidth, int screenHeight,
                           boolean useZoomLarge, boolean antialias,
                           boolean resetSlab, boolean resetZoom) {
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
    this.useZoomLarge = useZoomLarge;
    this.antialias = antialias;
    width = (antialias ? screenWidth * 2 : screenWidth);
    height = (antialias ? screenHeight * 2 : screenHeight);
    scaleFitToScreen(false, useZoomLarge, resetSlab, resetZoom);
    finalizeTransformParameters();
  }

  void setAntialias(boolean TF) {
    boolean isNew = (antialias != TF);
    antialias = TF;
    width = (antialias ? screenWidth * 2 : screenWidth);
    height = (antialias ? screenHeight * 2 : screenHeight);
    if (isNew)
      scaleFitToScreen(false, useZoomLarge, false, false);
  }

  float defaultScaleToScreen(float radius) {
    /* 
     * 
     * the presumption here is that the rotation center is at pixel
     * (150,150) of a 300x300 window. modelRadius is
     * a rough estimate of the furthest distance from the center of rotation
     * (but not including pmesh, special lines, planes, etc. -- just atoms)
     * 
     * also that we do not want it to be possible for the model to rotate
     * out of bounds of the applet. For internal spinning I had to turn
     * of any calculation that would change the rotation radius.  hansonr
     * 
     */
    return screenPixelCount / 2f / radius;
  }

  void scaleFitToScreen(boolean andCenter) {
    scaleFitToScreen(andCenter, viewer.getZoomLarge(), true, true);
  }

  void scaleFitToScreen(boolean andCenter, boolean zoomLarge,
                        boolean resetSlab, boolean resetZoom) {
    if (width == 0 || height == 0) {
      screenPixelCount = 1;
    } else {

      // translate to the middle of the screen
      fixedTranslation.set(width * (andCenter ? 0.5f : xTranslationFraction),
          height * (andCenter ? 0.5f : yTranslationFraction), 0);
      setTranslationFractions();
      if (resetZoom)
        resetNavigationPoint(resetSlab);
    // 2005 02 22
    // switch to finding larger screen dimension
    // find smaller screen dimension
      screenPixelCount = (zoomLarge == (height > width) ? height : width);
    }
    // ensure that rotations don't leave some atoms off the screen
    // note that this radius is to the furthest outside edge of an atom
    // given the current VDW radius setting. it is currently *not*
    // recalculated when the vdw radius settings are changed
    // leave a very small margin - only 1 on top and 1 on bottom
    if (screenPixelCount > 2)
      screenPixelCount -= 2;
    scaleDefaultPixelsPerAngstrom = defaultScaleToScreen(modelRadius);
  }

  short scaleToScreen(int z, int milliAngstroms) {
    if (milliAngstroms == 0 || z < 2)
      return 0;
    int pixelSize = (int) scaleToPerspective(z, milliAngstroms
        * scalePixelsPerAngstrom / 1000);
    return (short) (pixelSize > 0 ? pixelSize : 1);
  }

  float unscaleToScreen(float z, float screenDistance) {
    float d = screenDistance / scalePixelsPerAngstrom;
    return (perspectiveDepth ? d / getPerspectiveFactor(z) : d);
  }

  float scaleToPerspective(int z, float sizeAngstroms) {
    //DotsRenderer only
    //old: return (perspectiveDepth ? sizeAngstroms * perspectiveFactor(z)
    //: sizeAngstroms);

    return (perspectiveDepth ? sizeAngstroms * getPerspectiveFactor(z)
        : sizeAngstroms);

  }

  /* ***************************************************************
   * TRANSFORMATIONS
   ****************************************************************/

  protected final Matrix4f matrixTransform = new Matrix4f();
  protected final Matrix4f matrixTransformInv = new Matrix4f();

  Matrix4f getMatrixtransform() {
    return matrixTransform;
  }

  protected final Point3f point3fScreenTemp = new Point3f();
  protected final Point3i point3iScreenTemp = new Point3i();

  private final Point3f point3fVibrationTemp = new Point3f();

  protected boolean navigating = false;
  protected final static int MODE_STANDARD = 0;
  protected final static int MODE_NAVIGATION = 1;
  protected final static int MODE_PERSPECTIVE_CENTER = 2;

  static final int DEFAULT_PERSPECTIVE_MODEL = 11;

  protected int mode = MODE_STANDARD;
  protected int defaultMode = MODE_STANDARD;

  void setNavigationMode(boolean TF) {
    if (TF && canNavigate())
      mode = MODE_NAVIGATION;
    else
      mode = defaultMode;
    resetNavigationPoint(true);
  }

  boolean isNavigating() {
    return navigating || navOn;
  }

  synchronized void finalizeTransformParameters() {
    haveNotifiedNaN = false;
    fixedRotationOffset.set(fixedTranslation);
    internalSlab = slabEnabled && (slabPlane != null || depthPlane != null);
    float newZoom = getZoomSetting();
    if (zoomPercent != newZoom) {
      zoomPercent = newZoom;
      if (!viewer.getFontCaching())
        Text3D.clearFontCache();
    }
    calcCameraFactors();
    calcTransformMatrix();
    if (mode == MODE_NAVIGATION)
      calcNavigationPoint();
    else
      calcSlabAndDepthValues();
  }

  float getZoomSetting() {
    if (zoomPercentSetting < 5)
      zoomPercentSetting = 5;
    if (zoomPercentSetting > MAXIMUM_ZOOM_PERCENTAGE)
      zoomPercentSetting = MAXIMUM_ZOOM_PERCENTAGE;
    return (zoomEnabled || mode == MODE_NAVIGATION ? zoomPercentSetting : 100);
  }

  /**
   * sets slab and depth, possibly using visual range considerations
   * for setting the slab-clipping plane. (slab on; slab 0)
   * 
   * superceded in navigation mode
   *
   */

  protected void calcSlabAndDepthValues() {
    if (slabRange < 1)
      slabValue = zValueFromPercent(slabPercentSetting);
    else
      slabValue = (int) (modelCenterOffset * slabRange / (2 * modelRadius) * (zoomPercentSetting / 100));
    depthValue = zValueFromPercent(depthPercentSetting);
    if (zSlabPercentSetting == zDepthPercentSetting) {
      zSlabValue = slabValue;
      zDepthValue = depthValue;
    } else {
      zSlabValue = zValueFromPercent(zSlabPercentSetting);
      zDepthValue = zValueFromPercent(zDepthPercentSetting);
    }
    viewer.getGlobalSettings().setParameterValue("_slabPlane",
        Escape.escape(getSlabDepthPlane(false)));
    viewer.getGlobalSettings().setParameterValue("_depthPlane",
        Escape.escape(getSlabDepthPlane(true)));
    if (slabEnabled)
      return;
    slabValue = 0;
    depthValue = Integer.MAX_VALUE;
  }

  int zValueFromPercent(int zPercent) {
    return (int) ((1 - zPercent / 50f) * modelRadiusPixels + modelCenterOffset);
  }
  
  synchronized protected void calcTransformMatrix() {
    matrixTransform.setIdentity();

    // first, translate the coordinates back to the center

    vectorTemp.set(fixedRotationCenter);
    vectorTemp.sub(frameOffset);
    matrixTemp.setZero();
    matrixTemp.setTranslation(vectorTemp);
    matrixTransform.sub(matrixTemp);

    // multiply by angular rotations
    // this is *not* the same as  matrixTransform.mul(matrixRotate);
    matrixTemp.set(stereoFrame ? matrixStereo : matrixRotate);
    matrixTransform.mul(matrixTemp, matrixTransform);
    // cale to screen coordinates
    matrixTemp.setZero();
    matrixTemp.set(scalePixelsPerAngstrom);
    // negate y (for screen) and z (for zbuf)
    matrixTemp.m11 = matrixTemp.m22 = -scalePixelsPerAngstrom;

    matrixTransform.mul(matrixTemp, matrixTransform);
    //z-translate to set rotation center at midplane (Nav) or front plane (V10)
    matrixTransform.m23 += modelCenterOffset;
    try {
      matrixTransformInv.invert(matrixTransform);
    } catch (Exception e) {
      // ignore -- this is a Mac issue on applet startup
    }
    // note that the image is still centered at 0, 0 in the xy plane

  }

  void rotatePoint(Point3f pt, Point3f ptRot) {
    matrixRotate.transform(pt, ptRot);
    ptRot.y = -ptRot.y;
  }

  void transformPoints(int count, Point3f[] angstroms, Point3i[] screens) {
    for (int i = count; --i >= 0;)
      screens[i].set(transformPoint(angstroms[i]));
  }

  void transformPoint(Point3f pointAngstroms, Point3i pointScreen) {
    pointScreen.set(transformPoint(pointAngstroms));
  }

  void transformPointNoClip(Point3f pointAngstroms, Point3f pointScreen) {
    pointScreen.set(transformPointNoClip(pointAngstroms));
  }

  /** 
   * CAUTION! returns a POINTER TO A TEMPORARY VARIABLE
   * @param pointAngstroms
   * @return POINTER TO point3iScreenTemp
   */
  synchronized Point3i transformPoint(Point3f pointAngstroms) {
    if (pointAngstroms.z == Float.MAX_VALUE
        || pointAngstroms.z == -Float.MAX_VALUE)
      return transformScreenPoint(pointAngstroms);
    matrixTransform.transform(pointAngstroms, point3fScreenTemp);
    adjustTemporaryScreenPoint();
    if (internalSlab && checkInternalSlab(pointAngstroms))
      point3iScreenTemp.z = 1;
    return point3iScreenTemp;
  }

  private final Point3f pointTsp = new Point3f();
  private Point3i transformScreenPoint(Point3f ptXyp) {
    // just does the processing for [x y] and [x y %]
    if (ptXyp.z == -Float.MAX_VALUE) {
      point3iScreenTemp.x = (int) (ptXyp.x / 100 * screenWidth);
      point3iScreenTemp.y = (int) ((1 - ptXyp.y / 100) * screenHeight);
    } else {
      point3iScreenTemp.x = (int) ptXyp.x;
      point3iScreenTemp.y = (screenHeight - (int) ptXyp.y);
    }
    if (antialias) {
      point3iScreenTemp.x <<= 1;
      point3iScreenTemp.y <<= 1;
    }
    matrixTransform.transform(fixedRotationCenter, pointTsp);
    point3iScreenTemp.z = (int) pointTsp.z;
    return point3iScreenTemp;
  }

  /** 
   * CAUTION! returns a POINTER TO A TEMPORARY VARIABLE
   * @param pointAngstroms
   * @return POINTER TO point3iScreenTemp
   */
  synchronized Point3f transformPointNoClip(Point3f pointAngstroms) {
    matrixTransform.transform(pointAngstroms, point3fScreenTemp);
    adjustTemporaryScreenPoint();
    return point3fScreenTemp;
  }

  /**
   * @param pointAngstroms
   * @param vibrationVector
   * @return POINTER TO TEMPORARY VARIABLE (caution!) point3iScreenTemp
   */
  Point3i transformPoint(Point3f pointAngstroms, Vector3f vibrationVector) {
    point3fVibrationTemp.set(pointAngstroms);
    if (vibrationOn && vibrationVector != null)
      point3fVibrationTemp.scaleAdd(vibrationAmplitude, vibrationVector,
          pointAngstroms);
    matrixTransform.transform(point3fVibrationTemp, point3fScreenTemp);
    adjustTemporaryScreenPoint();
    if (internalSlab && checkInternalSlab(pointAngstroms))
      point3iScreenTemp.z = 1;
    return point3iScreenTemp;
  }

  void transformPoint(Point3f pointAngstroms, Point3f screen) {
    matrixTransform.transform(pointAngstroms, point3fScreenTemp);
    adjustTemporaryScreenPoint();
    if (internalSlab && checkInternalSlab(pointAngstroms))
      point3fScreenTemp.z = 1;
    screen.set(point3fScreenTemp);
  }

  void transformVector(Vector3f vectorAngstroms, Vector3f vectorTransformed) {
    //dots renderer, geodesic only
    matrixTransform.transform(vectorAngstroms, vectorTransformed);
  }

  final protected Point3f untransformedPoint = new Point3f();
  void unTransformPoint(Point3f screenPt, Point3f coordPt) {
    //draw move2D
    untransformedPoint.set(screenPt);
    switch (mode) {
    case MODE_NAVIGATION:
      untransformedPoint.x -= navigationOffset.x;
      untransformedPoint.y -= navigationOffset.y;
      break;
    case MODE_PERSPECTIVE_CENTER:
      untransformedPoint.x -= perspectiveOffset.x;
      untransformedPoint.y -= perspectiveOffset.y;
      break;
    case MODE_STANDARD:
      untransformedPoint.x -= fixedRotationOffset.x;
      untransformedPoint.y -= fixedRotationOffset.y;
    }
    if (perspectiveDepth) {
      float factor = getPerspectiveFactor(untransformedPoint.z);
      untransformedPoint.x /= factor;
      untransformedPoint.y /= factor;
    }
    switch (mode) {
    case MODE_NAVIGATION:
      untransformedPoint.x += navigationShiftXY.x;
      untransformedPoint.y += navigationShiftXY.y;
      break;
    case MODE_PERSPECTIVE_CENTER:
      untransformedPoint.x += perspectiveShiftXY.x;
      untransformedPoint.y += perspectiveShiftXY.y;
      break;
    }
    matrixTransformInv.transform(untransformedPoint, coordPt);
  }

  /* ***************************************************************
   * move/moveTo support
   ****************************************************************/

  void move(Vector3f dRot, float dZoom, Vector3f dTrans, float dSlab,
            float floatSecondsTotal, int fps) {
    int slab = getSlabPercentSetting();
    float transX = getTranslationXPercent();
    float transY = getTranslationYPercent();
    float transZ = getTranslationZPercent();

    long timeBegin = System.currentTimeMillis();
    int timePerStep = 1000 / fps;
    int totalSteps = (int) (fps * floatSecondsTotal);
    if (totalSteps <= 0)
      totalSteps = 1; // to catch a zero secondsTotal parameter
    float radiansPerDegreePerStep = (float) (1 / degreesPerRadian / totalSteps);
    float radiansXStep = radiansPerDegreePerStep * dRot.x;
    float radiansYStep = radiansPerDegreePerStep * dRot.y;
    float radiansZStep = radiansPerDegreePerStep * dRot.z;
    if (floatSecondsTotal > 0)
      viewer.setInMotion(true);
    float zoomPercent0 = zoomPercent;
    for (int i = 1; i <= totalSteps; ++i) {
      if (dRot.x != 0)
        rotateXRadians(radiansXStep, null);
      if (dRot.y != 0)
        rotateYRadians(radiansYStep, null);
      if (dRot.z != 0)
        rotateZRadians(radiansZStep);
      if (dZoom != 0)
        zoomToPercent(zoomPercent0 + dZoom * i / totalSteps);
      if (dTrans.x != 0)
        translateToPercent('x', transX + dTrans.x * i / totalSteps);
      if (dTrans.y != 0)
        translateToPercent('y', transY + dTrans.y * i / totalSteps);
      if (dTrans.z != 0)
        translateToPercent('z', transZ + dTrans.z * i / totalSteps);
      if (dSlab != 0)
        slabToPercent((int) (slab + dSlab * i / totalSteps));
      int timeSpent = (int) (System.currentTimeMillis() - timeBegin);
      int timeAllowed = i * timePerStep;
      if (timeSpent < timeAllowed) {
        viewer.requestRepaintAndWait();
        if (!viewer.isScriptExecuting())
          break;
        timeSpent = (int) (System.currentTimeMillis() - timeBegin);
        int timeToSleep = timeAllowed - timeSpent;
        if (timeToSleep > 0) {
          try {
            Thread.sleep(timeToSleep);
          } catch (InterruptedException e) {
          }
        }
      }
    }
    viewer.setInMotion(false);
  }

  protected final Point3f ptTest1 = new Point3f();
  protected final Point3f ptTest2 = new Point3f();
  protected final Point3f ptTest3 = new Point3f();
  protected final AxisAngle4f aaTest1 = new AxisAngle4f();
  protected final Matrix3f matrixTest = new Matrix3f();

  boolean isInPosition(Vector3f axis, float degrees) {
    if (Float.isNaN(degrees))
      return true;
    aaTest1.set(axis, (float) (degrees / degreesPerRadian));
    ptTest1.set(4.321f, 1.23456f, 3.14159f);
    getRotation(matrixTest);
    matrixTest.transform(ptTest1, ptTest2);
    matrixTest.set(aaTest1);
    matrixTest.transform(ptTest1, ptTest3);
    return (ptTest3.distance(ptTest2) < 0.1);
  }

  // from Viewer
  void moveTo(float floatSecondsTotal, Point3f center, Tuple3f rotAxis,
              float degrees, Matrix3f matrixEnd, float zoom, float xTrans,
              float yTrans, float newRotationRadius, Point3f navCenter,
              float xNav, float yNav, float navDepth) {
    if (matrixEnd == null) {
      matrixEnd = new Matrix3f();
      Vector3f axis = new Vector3f(rotAxis);
      if (Float.isNaN(degrees)) {
        matrixEnd.m00 = Float.NaN;
      } else if (degrees < 0.01f && degrees > -0.01f) {
        // getRotation(matrixEnd);
        matrixEnd.setIdentity();
      } else {
        if (axis.x == 0 && axis.y == 0 && axis.z == 0) {
          // invalid ... no rotation
          /*
           * why were we then sleeping? int sleepTime = (int) (floatSecondsTotal
           * * 1000) - 30; if (sleepTime > 0) { try { Thread.sleep(sleepTime); }
           * catch (InterruptedException ie) { } }
           */
          return;
        }
        AxisAngle4f aaMoveTo = new AxisAngle4f();
        aaMoveTo.set(axis, (float) (degrees / degreesPerRadian));
        matrixEnd.set(aaMoveTo);
      }
    }
    try {
      if (motion == null)
        motion = new MotionThread();
      int nSteps = motion.set(floatSecondsTotal, center, matrixEnd, zoom, xTrans,
          yTrans, newRotationRadius, navCenter, xNav, yNav, navDepth);
      if (nSteps <= 0 || viewer.waitForMoveTo()) {
        motion.startMotion(false);
        motion = null;
      } else {
        motion.startMotion(true);
      }
    } catch (Exception e) {
      // ignore
    }
  }
  
  MotionThread motion;
  
  public void stopMotion() {
    motion = null;
    //setSpinOn(false);// trouble here with Viewer.checkHalt
  }

  class MotionThread extends Thread {
    private final Vector3f aaStepCenter = new Vector3f();
    private final Vector3f aaStepNavCenter = new Vector3f();
    private final AxisAngle4f aaStep = new AxisAngle4f();
    private final AxisAngle4f aaTotal = new AxisAngle4f();
    private final Matrix3f matrixStart = new Matrix3f();
    private final Matrix3f matrixStartInv = new Matrix3f();
    private final Matrix3f matrixStep = new Matrix3f();
    private final Matrix3f matrixEnd = new Matrix3f();

    private Point3f center;
    private float zoom; 
    private float xTrans;
    private float yTrans;
    private Point3f navCenter;
    private float xNav;
    private float yNav;
    private float navDepth;
    private Point3f ptMoveToCenter;
    private float startRotationRadius;
    private float targetPixelScale;
    private int totalSteps;
    private float startPixelScale;
    private float targetRotationRadius;
    private int fps;
    private float rotationRadiusDelta;
    private float pixelScaleDelta;
    private float zoomStart;
    private float zoomDelta;
    private float xTransStart;
    private float xTransDelta;
    private float yTransStart;
    private float yTransDelta;
    private float xNavTransStart;
    private float xNavTransDelta;
    private float yNavTransDelta;
    private float yNavTransStart;
    private float navDepthStart;
    private float navDepthDelta;
    private long targetTime;
    private long frameTimeMillis;
    private int iStep;
    
    private boolean asThread;
    
    public void startMotion(boolean asThread) {
      this.asThread = asThread;
      if (asThread)
        start();
      else
        run();
    }

    @Override
    public void run() {
      if (totalSteps > 0)
        viewer.setInMotion(true);
      try {
        if (totalSteps == 0 || startMotion())
          endMotion();
      } catch (Exception e) {
        // ignore
      }
      if (totalSteps > 0)
        viewer.setInMotion(false);
      motion = null;
    }
    
    int set(float floatSecondsTotal, Point3f center, Matrix3f end, float zoom,
            float xTrans, float yTrans, float newRotationRadius,
            Point3f navCenter, float xNav, float yNav, float navDepth) {
      this.center = center;
      matrixEnd.set(end);
      this.zoom = zoom;
      this.xTrans = xTrans;
      this.yTrans = yTrans;
      this.navCenter = navCenter;
      this.xNav = xNav;
      this.yNav = yNav;
      this.navDepth = navDepth;
      ptMoveToCenter = (center == null ? fixedRotationCenter : center);
      startRotationRadius = modelRadius;
      targetRotationRadius = (center == null || Float.isNaN(newRotationRadius) ? modelRadius
          : newRotationRadius <= 0 ? viewer.calcRotationRadius(center)
              : newRotationRadius);
      startPixelScale = scaleDefaultPixelsPerAngstrom;
      targetPixelScale = (center == null ? startPixelScale
          : defaultScaleToScreen(targetRotationRadius));
      if (Float.isNaN(zoom))
        zoom = zoomPercent;
      getRotation(matrixStart);
      matrixStartInv.invert(matrixStart);
      matrixStep.mul(matrixEnd, matrixStartInv);
      aaTotal.set(matrixStep);
      fps = 30;
      totalSteps = (int) (floatSecondsTotal * fps);
      if (totalSteps == 0)
        return 0;
      frameTimeMillis = 1000 / fps;
      targetTime = System.currentTimeMillis();
      zoomStart = zoomPercent;
      zoomDelta = zoom - zoomStart;
      xTransStart = getTranslationXPercent();
      xTransDelta = xTrans - xTransStart;
      yTransStart = getTranslationYPercent();
      yTransDelta = yTrans - yTransStart;
      aaStepCenter.set(ptMoveToCenter);
      aaStepCenter.sub(fixedRotationCenter);
      aaStepCenter.scale(1f / totalSteps);
      pixelScaleDelta = (targetPixelScale - startPixelScale);
      rotationRadiusDelta = (targetRotationRadius - startRotationRadius);
      if (navCenter != null && mode == MODE_NAVIGATION) {
        aaStepNavCenter.set(navCenter);
        aaStepNavCenter.sub(navigationCenter);
        aaStepNavCenter.scale(1f / totalSteps);
      }
      float xNavTransStart = getNavigationOffsetPercent('X');
      xNavTransDelta = xNav - xNavTransStart;
      yNavTransStart = getNavigationOffsetPercent('Y');
      yNavTransDelta = yNav - yNavTransStart;
      float navDepthStart = getNavigationDepthPercent();
      navDepthDelta = navDepth - navDepthStart;
      return totalSteps;
    }
    
    boolean startMotion() {
      for (; iStep < totalSteps; ++iStep) {
        if (!Float.isNaN(matrixEnd.m00)) {
          getRotation(matrixStart);
          matrixStartInv.invert(matrixStart);
          matrixStep.mul(matrixEnd, matrixStartInv);
          aaTotal.set(matrixStep);
          aaStep.set(aaTotal);
          aaStep.angle /= (totalSteps - iStep);
          if (aaStep.angle == 0)
            matrixStep.setIdentity();
          else
            matrixStep.set(aaStep);
          matrixStep.mul(matrixStart);
        }
        float fStep = iStep / (totalSteps - 1f);
        modelRadius = startRotationRadius + rotationRadiusDelta * fStep;
        scaleDefaultPixelsPerAngstrom = startPixelScale + pixelScaleDelta
            * fStep;
        if (!Float.isNaN(xTrans)) {
          zoomToPercent(zoomStart + zoomDelta * fStep);
          translateToPercent('x', xTransStart + xTransDelta * fStep);
          translateToPercent('y', yTransStart + yTransDelta * fStep);
        }
        setRotation(matrixStep);
        if (center != null)
          fixedRotationCenter.add(aaStepCenter);
        if (navCenter != null && mode == MODE_NAVIGATION) {
          Point3f pt = new Point3f(navigationCenter);
          pt.add(aaStepNavCenter);
          navigate(0, pt);
          if (!Float.isNaN(xNav) && !Float.isNaN(yNav))
            navTranslatePercent(0, xNavTransStart + xNavTransDelta * fStep,
                yNavTransStart + yNavTransDelta * fStep);
          if (!Float.isNaN(navDepth))
            setNavigationDepthPercent(0, navDepthStart + navDepthDelta * fStep);
        }
        targetTime += frameTimeMillis;
        if (System.currentTimeMillis() < targetTime) {
          viewer.requestRepaintAndWait();
          if (motion == null || !asThread && !viewer.isScriptExecuting()) {
            return false;
          }
          int sleepTime = (int) (targetTime - System.currentTimeMillis());
          if (sleepTime > 0) {
            try {
              Thread.sleep(sleepTime);
            } catch (InterruptedException ie) {
              return false;
            }
            // System.out.println("moveto thread " +
            // Thread.currentThread().getName() + " running " +
            // System.currentTimeMillis());
          }
        }
      }
      return true;
    }

    void endMotion() {
      setRotationRadius(targetRotationRadius, true);
      scaleDefaultPixelsPerAngstrom = targetPixelScale;
      if (center != null)
        moveRotationCenter(center, !windowCentered);
      if (!Float.isNaN(xTrans)) {
        zoomToPercent(zoom);
        translateToPercent('x', xTrans);
        translateToPercent('y', yTrans);
      }
      setRotation(matrixEnd);
      if (navCenter != null && mode == MODE_NAVIGATION) {
        navigationCenter.set(navCenter);
        if (!Float.isNaN(xNav) && !Float.isNaN(yNav))
          navTranslatePercent(0, xNav, yNav);
        if (!Float.isNaN(navDepth))
          setNavigationDepthPercent(0, navDepth);
      }
    }
  }

  Quaternion getRotationQuaternion() {
    return new Quaternion(matrixRotate);
    /*
    axisangleT.set(matrixRotate);
    float degrees = (float) (axisangleT.angle * degreesPerRadian);
    vectorT.set(axisangleT.x, axisangleT.y, axisangleT.z);
    return new Quaternion(vectorT, degrees);
    */
  }
  
  String getRotationText() {
    axisangleT.set(matrixRotate);
    float degrees = (float) (axisangleT.angle * degreesPerRadian);
    StringBuffer sb = new StringBuffer();
    vectorT.set(axisangleT.x, axisangleT.y, axisangleT.z);
    if (degrees < 0.01f)
      return "{0 0 1 0}";
    vectorT.normalize();
    vectorT.scale(1000);
    sb.append("{");
    truncate0(sb, vectorT.x);
    truncate0(sb, vectorT.y);
    truncate0(sb, vectorT.z);
    truncate2(sb, degrees);
    sb.append("}");
    return sb.toString();
  }

  String getMoveToText(float timespan, boolean addComments) {
    StringBuffer sb = new StringBuffer();
    sb.append("moveto ");
    if (addComments)
      sb.append("/* time, axisAngle */ ");
    sb.append(timespan);
    sb.append(" ").append(getRotationText());
    if (addComments)
      sb.append(" /* zoom, translation */ ");
    truncate2(sb, zoomPercentSetting);
    truncate2(sb, getTranslationXPercent());
    truncate2(sb, getTranslationYPercent());
    sb.append(" ");
    if (addComments)
      sb.append(" /* center, rotationRadius */ ");
    sb.append(getCenterText());
    sb.append(" ").append(modelRadius);
    sb.append(getNavigationText(addComments));
    sb.append(";");
    return sb.toString();
  }

  private String getCenterText() {
    return Escape.escape(fixedRotationCenter);
  }

  private String getRotateXyzText() {
    StringBuffer sb = new StringBuffer();
    float m20 = matrixRotate.m20;
    float rY = -(float) (Math.asin(m20) * degreesPerRadian);
    float rX, rZ;
    if (m20 > .999f || m20 < -.999f) {
      rX = -(float) (Math.atan2(matrixRotate.m12, matrixRotate.m11)
          * degreesPerRadian);
      rZ = 0;
    } else {
      rX = (float) (Math.atan2(matrixRotate.m21, matrixRotate.m22)
          * degreesPerRadian);
      rZ = (float) (Math.atan2(matrixRotate.m10, matrixRotate.m00)
          * degreesPerRadian);
    }
    sb.append("reset");
    sb.append(";center ").append(getCenterText());
    if (rX != 0) {
      sb.append("; rotate x");
      truncate2(sb, rX);
    }
    if (rY != 0) {
      sb.append("; rotate y");
      truncate2(sb, rY);
    }
    if (rZ != 0) {
      sb.append("; rotate z");
      truncate2(sb, rZ);
    }
    sb.append(";");
    addZoomTranslationNavigationText(sb);
    return sb.toString();
  }

  private void addZoomTranslationNavigationText(StringBuffer sb) {
    if (zoomPercent != 100) {
      sb.append(" zoom");
      truncate2(sb, zoomPercent);
      sb.append(";");
    }
    float tX = getTranslationXPercent();
    if (tX != 0) {
      sb.append(" translate x");
      truncate2(sb, tX);
      sb.append(";");
    }
    float tY = getTranslationYPercent();
    if (tY != 0) {
      sb.append(" translate y");
      truncate2(sb, tY);
      sb.append(";");
    }
    if (modelRadius != rotationRadiusDefault) {
      sb.append(" set rotationRadius");
      truncate2(sb, modelRadius);
      sb.append(";");
    }
    if (mode == MODE_NAVIGATION) {
      sb.append("navigate 0 center ").append(Escape.escape(navigationCenter));
      sb.append(";navigate 0 translate");
      truncate2(sb, getNavigationOffsetPercent('X'));
      truncate2(sb, getNavigationOffsetPercent('Y'));
      sb.append(";navigate 0 depth ");
      truncate2(sb, getNavigationDepthPercent());
      sb.append(";");
    }
  }

  private String getRotateZyzText(boolean iAddComment) {
    StringBuffer sb = new StringBuffer();
    float m22 = matrixRotate.m22;
    float rY = (float) (Math.acos(m22) * degreesPerRadian);
    float rZ1, rZ2;
    if (m22 > .999f || m22 < -.999f) {
      rZ1 = (float) (Math.atan2(matrixRotate.m10, matrixRotate.m11)
          * degreesPerRadian);
      rZ2 = 0;
    } else {
      rZ1 = (float) (Math.atan2(matrixRotate.m21, -matrixRotate.m20)
          * degreesPerRadian);
      rZ2 = (float) (Math.atan2(matrixRotate.m12, matrixRotate.m02)
          * degreesPerRadian);
    }
    if (rZ1 != 0 && rY != 0 && rZ2 != 0 && iAddComment)
      sb.append("#Follows Z-Y-Z convention for Euler angles\n");
    sb.append("reset");
    sb.append(";center ").append(getCenterText());
    if (rZ1 != 0) {
      sb.append("; rotate z");
      truncate2(sb, rZ1);
    }
    if (rY != 0) {
      sb.append("; rotate y");
      truncate2(sb, rY);
    }
    if (rZ2 != 0) {
      sb.append("; rotate z");
      truncate2(sb, rZ2);
    }
    sb.append(";");
    addZoomTranslationNavigationText(sb);
    return sb.toString();
  }

  static private void truncate0(StringBuffer sb, float val) {
    sb.append(' ');
    sb.append(Math.round(val));
  }

  static private void truncate2(StringBuffer sb, float val) {
    sb.append(' ');
    sb.append(Math.round(val * 100) / 100f);
  }

  /* ***************************************************************
   * Spin support
   ****************************************************************/

  void setSpinXYZ(float x, float y, float z) {
    if (!Float.isNaN(x))
      spinX = x;
    if (!Float.isNaN(y))
      spinY = y;
    if (!Float.isNaN(z))
      spinZ = z;
    if (isSpinInternal || isSpinFixed)
      clearSpin();
  }

  void setSpinFps(int value) {
    if (value <= 0)
      value = 1;
    else if (value > 50)
      value = 50;
    spinFps = value;
  }

  void setNavXYZ(float x, float y, float z) {
    if (!Float.isNaN(x))
      navX = x;
    if (!Float.isNaN(y))
      navY = y;
    if (!Float.isNaN(z))
      navZ = z;
  }

  /**
   * @param value  
   */
  protected void setNavFps(int value) {
    // see TransformManager11
  }

  private void clearSpin() {
    setSpinOn(false);
    setNavOn(false);
    isSpinInternal = false;
    isSpinFixed = false;
    //back to the Chime defaults
  }

  protected boolean spinOn;
  boolean getSpinOn() {
    return spinOn;
  }

  protected boolean navOn;
  boolean getNavOn() {
    return navOn;
  }

  private SpinThread spinThread;

  void setSpinOn(boolean spinOn) {
    setSpinOn(spinOn, Float.MAX_VALUE, null, null, false);
  }

  private void setSpinOn(boolean spinOn, float endDegrees,
                         List<Point3f> endPositions, BitSet bsAtoms,
                         boolean isGesture) {
    if (navOn && spinOn)
      setNavOn(false);
    this.spinOn = spinOn;
    viewer.getGlobalSettings().setParameterValue("_spinning", spinOn);
    if (spinOn) {
      if (spinThread == null) {
        spinThread = new SpinThread(endDegrees, endPositions, bsAtoms, false,
            isGesture);
        if (bsAtoms == null)
          spinThread.start();
        else
          spinThread.run();
      }
    } else if (spinThread != null) {
      spinThread.isReset = true;
      spinThread.interrupt();
      spinThread = null;
    }
  }

  protected void setNavOn(boolean navOn) {
    if (Float.isNaN(navFps))
      return;
    boolean wasOn = this.navOn;
    if (navOn && spinOn)
      setSpinOn(false, 0, null, null, false);
    this.navOn = navOn;
    viewer.getGlobalSettings().setParameterValue("_navigating", navOn);
    if (navOn) {
      if (navX == 0 && navY == 0 && navZ == 0)
        navZ = 1;
      if (navFps == 0)
        navFps = 10;
      if (spinThread == null) {
        spinThread = new SpinThread(0, null, null, true, false);
        spinThread.start();
      }
    } else if (wasOn) {
      if (spinThread != null) {
        spinThread.interrupt();
        spinThread = null;
      }
    }
  }

  private class SpinThread extends Thread {
    float endDegrees;
    List<Point3f> endPositions;
    float nDegrees;
    BitSet bsAtoms;
    boolean isNav;
    boolean isGesture;
    boolean isReset;
    
    SpinThread(float endDegrees, List<Point3f> endPositions, BitSet bsAtoms, boolean isNav, boolean isGesture) {
      setName("SpinThread" + new Date());
      this.endDegrees = Math.abs(endDegrees);
      this.endPositions = endPositions;
      this.bsAtoms = bsAtoms;
      this.isNav = isNav;
      this.isGesture = isGesture;
    }

    @Override
    public void run() {
      float myFps = (isNav ? navFps : spinFps);
      viewer.getGlobalSettings().setParameterValue(isNav ? "_navigating" : "_spinning", true);
      int i = 0;
      long timeBegin = System.currentTimeMillis();
      float angle = 0;
      boolean haveNotified = false;
      while (!isInterrupted()) {
        if (isNav && myFps != navFps) {
          myFps = navFps;
          i = 0;
          timeBegin = System.currentTimeMillis();
        } else if (!isNav && myFps != spinFps && bsAtoms == null) {
          myFps = spinFps;
          i = 0;
          timeBegin = System.currentTimeMillis();
        }
        if (myFps == 0 || !(isNav ? navOn : spinOn)) {
          setSpinOn(false);
          setNavOn(false);
          break;
        }
        boolean navigatingSurface = viewer.getNavigateSurface();
        boolean refreshNeeded = (isNav ?  (navigatingSurface || (navX != 0 || navY != 0)) || navZ != 0
            : isSpinInternal && internalRotationAxis.angle != 0 
            || isSpinFixed && fixedRotationAxis.angle != 0 
            || !isSpinFixed && !isSpinInternal && (spinX != 0 || spinY != 0 || spinZ != 0));
        ++i;
        int targetTime = (int) (i * 1000 / myFps);
        int currentTime = (int) (System.currentTimeMillis() - timeBegin);
        int sleepTime = (targetTime - currentTime);
        //System.out.println(targetTime + " " + currentTime + " " + sleepTime);
        if (sleepTime <= 0) {
          if (!haveNotified)
            Logger.info("spinFPS is set too fast (" + myFps + ") -- can't keep up!");
          haveNotified = true;
        } else {
          boolean isInMotion = (bsAtoms == null && viewer.getInMotion());
          if (isInMotion) {
            if (isGesture)
              break;
            sleepTime += 1000;
          }
          try {
            if (refreshNeeded && (spinOn || navOn) && !isInMotion) {
              if (isNav) {
                setNavigationOffsetRelative(navigatingSurface);
              } else if (isSpinInternal || isSpinFixed) {
                angle = (isSpinInternal ? internalRotationAxis
                    : fixedRotationAxis).angle / myFps;
                if (isSpinInternal) {
                  rotateAxisAngleRadiansInternal(angle, bsAtoms);
                } else {
                  rotateAxisAngleRadiansFixed(angle, bsAtoms);
                }
                nDegrees += Math.abs(angle * degreesPerRadian);
                //System.out.println(i + " " + angle + " " + nDegrees);
              } else { // old way: Rx * Ry * Rz
                if (spinX != 0) {
                  rotateXRadians(spinX * JmolConstants.radiansPerDegree / myFps, null);
                }
                if (spinY != 0) {
                  rotateYRadians(spinY * JmolConstants.radiansPerDegree / myFps, null);
                }
                if (spinZ != 0) {
                  rotateZRadians(spinZ * JmolConstants.radiansPerDegree / myFps);
                }
              }
              while (!isInterrupted() && !viewer.getRefreshing()) {
                Thread.sleep(10);
              }
              if (bsAtoms == null)
                viewer.refresh(1, "SpinThread:run()");
              else
                viewer.requestRepaintAndWait();
              //System.out.println(angle * degreesPerRadian + " " + count + " " + nDegrees + " " + endDegrees);
              if (!isNav && nDegrees >= endDegrees - 0.001)
                setSpinOn(false);
            }
            Thread.sleep(sleepTime);
            if (isReset)
              break;
          } catch (InterruptedException e) {
            break;
          }
        }
      }
      if (bsAtoms != null && endPositions != null) {
        // when the standard deviations of the end points was
        // exact, we know that we want EXACTLY those final positions
        viewer.setAtomCoord(bsAtoms, Token.xyz, endPositions);
        bsAtoms = null;
        endPositions = null;
      }
      if (!isReset)
        setSpinOn(false);
    }
  }

  /* ***************************************************************
   * Vibration support
   ****************************************************************/

  boolean vibrationOn;
  private float vibrationPeriod;
  public int vibrationPeriodMs;
  private float vibrationAmplitude;
  private float vibrationRadians;
  private float vibrationScale;

  void setVibrationScale(float scale) {
    vibrationScale = scale;
  }

  /**
   * @param navigatingSurface  
   */
  protected void setNavigationOffsetRelative(boolean navigatingSurface) {
   // only in Transformmanager11
  }

  /**
   * sets the period of vibration
   * -- period > 0: sets the period and turns vibration on
   * -- period < 0: sets the period but does not turn vibration on
   * -- period = 0: sets the period to zero and turns vibration off
   * -- period Float.NaN: uses current setting (frame change)
   * 
   * @param period 
   */
  void setVibrationPeriod(float period) {
    if (Float.isNaN(period)) {
      // NaN -- new frame check
      period = vibrationPeriod;
    } else if (period == 0) {
      vibrationPeriod = 0;
      vibrationPeriodMs = 0;
    } else {
      vibrationPeriod = Math.abs(period);
      vibrationPeriodMs = (int) (vibrationPeriod * 1000);
      if (period > 0)
        return;
      period = -period;
    }
    setVibrationOn(period > 0
        && viewer.modelHasVibrationVectors(viewer.getCurrentModelIndex()));
  }

  protected void setVibrationT(float t) {
    vibrationRadians = (float) (t * twoPI);
    if (vibrationScale == 0)
      vibrationScale = viewer.getVibrationScale();
    vibrationAmplitude = (float) Math.cos(vibrationRadians) * vibrationScale;
  }

  private VibrationThread vibrationThread;

  boolean isVibrationOn() {
    return vibrationOn;
  }
  
  private void setVibrationOn(boolean vibrationOn) {
    if (!vibrationOn) {
      if (vibrationThread != null) {
        vibrationThread.interrupt();
        vibrationThread = null;
      }
      this.vibrationOn = false;
      return;
    }
    if (viewer.getModelCount() < 1) {
      this.vibrationOn = false;
      return;
    }
    if (vibrationThread == null) {
      vibrationThread = new VibrationThread();
      vibrationThread.start();
    }
    this.vibrationOn = true;
  }

  private void clearVibration() {
    setVibrationOn(false);
    vibrationScale = 0;
  }

  /*private -- removed for fixing warning*/
  class VibrationThread extends Thread {

    VibrationThread() {
      this.setName("VibrationThread");
    }

    @Override
    public void run() {
      long startTime = System.currentTimeMillis();
      long lastRepaintTime = startTime;
      try {
        do {
          long currentTime = System.currentTimeMillis();
          int elapsed = (int) (currentTime - lastRepaintTime);
          int sleepTime = 33 - elapsed;
          if (sleepTime > 0)
            Thread.sleep(sleepTime);
          //
          lastRepaintTime = currentTime = System.currentTimeMillis();
          elapsed = (int) (currentTime - startTime);
          float t = (float) (elapsed % vibrationPeriodMs) / vibrationPeriodMs;
          setVibrationT(t);
          viewer.refresh(3, "VibrationThread:run()");
        } while (!isInterrupted());
      } catch (Exception e) { //may be arithmetic %0/0
      }
    }
  }

  ////////////////////////////////////////////////////////////////
  // stereo support
  ////////////////////////////////////////////////////////////////

  int stereoMode;
  int[] stereoColors;

  void setStereoMode(int[] twoColors) {
    stereoMode = JmolConstants.STEREO_CUSTOM;
    stereoColors = twoColors;
  }

  void setStereoMode(int stereoMode) {
    stereoColors = null;
    this.stereoMode = stereoMode;
  }

  float stereoDegrees = Float.NaN; // set in state manager
  float stereoRadians;

  void setStereoDegrees(float stereoDegrees) {
    this.stereoDegrees = stereoDegrees;
    stereoRadians = stereoDegrees * JmolConstants.radiansPerDegree;
  }

  boolean stereoFrame;

  protected final Matrix3f matrixStereo = new Matrix3f();

  synchronized Matrix3f getStereoRotationMatrix(boolean stereoFrame) {
    this.stereoFrame = stereoFrame;
    if (!stereoFrame)
      return matrixRotate;
    matrixTemp3.rotY(-stereoRadians);
    matrixStereo.mul(matrixTemp3, matrixRotate);
    return matrixStereo;
  }

  /////////// rotation center ////////////

  //from Frame:

  boolean windowCentered;

  boolean isWindowCentered() {
    return windowCentered;
  }

  void setWindowCentered(boolean TF) {
    windowCentered = TF;
    resetNavigationPoint(true);
  }

  Point3f getRotationCenter() {
    return fixedRotationCenter;
  }

  float getRotationRadius() {
    return modelRadius;
  }

  float setRotationRadius(float angstroms, boolean doAll) {
    angstroms = (modelRadius = (angstroms <= 0 ? viewer
        .calcRotationRadius(fixedRotationCenter) : angstroms));
    if (doAll)
      viewer.setRotationRadius(angstroms, false);
    return angstroms;
  }

  private void setRotationCenterAndRadiusXYZ(Point3f newCenterOfRotation,
                                             boolean andRadius) {
    resetNavigationPoint(false);
    if (newCenterOfRotation == null) {
      setFixedRotationCenter(rotationCenterDefault);
      modelRadius = rotationRadiusDefault;
      return;
    }
    setFixedRotationCenter(newCenterOfRotation);
    if (andRadius && windowCentered)
      modelRadius = viewer.calcRotationRadius(fixedRotationCenter);
  }

  private void setRotationCenterAndRadiusXYZ(String relativeTo, Point3f pt) {
    Point3f pt1 = new Point3f(pt);
    if (relativeTo == "average")
      pt1.add(viewer.getAverageAtomPoint());
    else if (relativeTo == "boundbox")
      pt1.add(viewer.getBoundBoxCenter());
    else if (relativeTo != "absolute")
      pt1.set(rotationCenterDefault);
    setRotationCenterAndRadiusXYZ(pt1, true);
  }

  void setNewRotationCenter(Point3f center, boolean doScale) {
    // once we have the center, we need to optionally move it to 
    // the proper XY position and possibly scale
    if (center == null)
      center = rotationCenterDefault;
    if (windowCentered) {
      translateToPercent('x', 0);
      translateToPercent('y', 0);///CenterTo(0, 0);
      setRotationCenterAndRadiusXYZ(center, true);
      if (doScale)
        scaleFitToScreen(true);
    } else {
      moveRotationCenter(center, true);
    }
  }

  // from Viewer:

  void moveRotationCenter(Point3f center, boolean toXY) {
    setRotationCenterAndRadiusXYZ(center, false);
    if (toXY)
      setRotationPointXY(fixedRotationCenter);
  }

  void setCenter() {
    setRotationCenterAndRadiusXYZ(fixedRotationCenter, true);
  }

  void setCenterAt(String relativeTo, Point3f pt) {
    setRotationCenterAndRadiusXYZ(relativeTo, pt);
    scaleFitToScreen(true);
  }

  /* ***************************************************************
   * Navigation support
   ****************************************************************/

  boolean canNavigate() {
    return false;
  }

  /**
   * entry point for keyboard-based navigation
   * 
   * @param keyCode  0 indicates key released    
   * @param modifiers shift,alt,ctrl
   */
  synchronized void navigate(int keyCode, int modifiers) {
  }

  /**
   * scripted entry point for navigation
   * 
   * @param seconds
   * @param center
   */
  void navigate(float seconds, Point3f center) {
  }

  /**
   * scripted entry point for navigation
   * 
   * @param seconds
   * @param rotAxis
   * @param degrees
   */
  void navigate(float seconds, Vector3f rotAxis, float degrees) {
  }

  /** 
   * scripted navigation
   * 
   * @param seconds  number of seconds to allow for total movement, like moveTo
   * @param path     sequence of points to turn into a hermetian path
   * @param theta    orientation angle along path (0 aligns with window Y axis) 
   *                 [or Z axis if path is vertical]
   * @param indexStart   index of first "station"
   * @param indexEnd     index of last "station"  
   *                 
   *                 not implemented yet
   */
  void navigate(float seconds, Point3f[] path, float[] theta, int indexStart,
                int indexEnd) {
  }

  /**
   * follows a path guided by orientation and offset vectors (as Point3fs)
   * @param timeSeconds
   * @param pathGuide
   */
  void navigate(float timeSeconds, Point3f[][] pathGuide) {
  }

  /**
   * scripted entry point for navigation
   * 
   * @param seconds
   * @param pt
   */
  void navTranslate(float seconds, Point3f pt) {
  }

  /**
   * scripted entry point for navigation
   * 
   * @param seconds
   * @param x
   * @param y
   */
  void navTranslatePercent(float seconds, float x, float y) {
  }

  /**
   *  all navigation effects go through this method
   *
   */
  protected void calcNavigationPoint() {
  }

  /**
   * something has arisen that requires resetting of the navigation point. 
   * @param doResetSlab 
   */
  protected void resetNavigationPoint(boolean doResetSlab) {
  }

  /**
   *
   * @return the script that defines the current navigation state
   * 
   */
  protected String getNavigationState() {
    return "";
  }

  /**
   * sets the position of the navigation offset relative 
   * to the model (50% center; 0% rear, 100% front; can be <0 or >100)
   * 
   * @param timeSec
   * @param percent
   */
  void setNavigationDepthPercent(float timeSec, float percent) {
    viewer.getGlobalSettings().setParameterValue("navigationDepth", percent);
  }

  Point3f getNavigationCenter() {
    return null;
  }

  Point3f getNavigationOffset() {
    return null;
  }

  float getNavigationDepthPercent() {
    return Float.NaN;
  }

  /**
   * @param XorY 
   * @return percent
   */
  float getNavigationOffsetPercent(char XorY) {
    return 0;
  }

  void setNavigationSlabOffsetPercent(float offset) {
    viewer.getGlobalSettings().setParameterValue("navigationSlab", offset);
  }

  /**
   * @param addComments  
   * @return navigation text if in navigation mode
   */
  String getNavigationText(boolean addComments) {
    return "";
  }

  Point3f[] frameOffsets;
  final Point3f frameOffset = new Point3f();
  void setFrameOffset(int modelIndex) {
    if (frameOffsets == null || modelIndex < 0 || modelIndex >= frameOffsets.length)
      frameOffset.set(0, 0, 0);
    else
      frameOffset.set(frameOffsets[modelIndex]);
  }
  
  void setFrameOffsets(Point3f[] offsets) {
    frameOffsets = offsets;
  }

  /**
   * @param timeSeconds  
   * @param name 
   */
  void navigateSurface(float timeSeconds, String name) {
  }


}
