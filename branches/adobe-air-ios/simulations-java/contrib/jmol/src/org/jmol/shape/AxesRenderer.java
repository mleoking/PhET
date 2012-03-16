/* $RCSfile$
 * $Author: nicove $
 * $Date: 2010-07-31 02:51:00 -0700 (Sat, 31 Jul 2010) $
 * $Revision: 13783 $
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
package org.jmol.shape;

import java.awt.FontMetrics;

import org.jmol.api.SymmetryInterface;
import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.util.Point3fi;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.StateManager;

import javax.vecmath.Point3f;

public class AxesRenderer extends FontLineShapeRenderer {

  private final static String[] axisLabels = { "+X", "+Y", "+Z", null, null, null, 
                                  "a", "b", "c", 
                                  "X", "Y", "Z", null, null, null,
                                  "X", null, "Z", null, "(Y)", null};

  private final Point3f[] screens = new Point3f[6];
  {
    for (int i = 6; --i >= 0; )
      screens[i] = new Point3f();
  }
  private final Point3f originScreen = new Point3f();
  
  private short[] colixes = new short[3];

  @Override
  protected void initRenderer() {
    endcap = Graphics3D.ENDCAPS_FLAT; 
    draw000 = false;
  }

  @Override
  protected void render() {
    Axes axes = (Axes) shape;
    int mad = viewer.getObjectMad(StateManager.OBJ_AXIS1);
    if (mad == 0 || !g3d.checkTranslucent(false))
      return;
    int axesMode = viewer.getAxesMode();
    imageFontScaling = viewer.getImageFontScaling();
    if (viewer.areAxesTainted()) {
      Font3D f = axes.font3d;
      axes.initShape();
      if (f != null)
        axes.font3d = f;
    }
    font3d = g3d.getFont3DScaled(axes.font3d, imageFontScaling);

    SymmetryInterface[] cellInfos = modelSet.getCellInfos();
    boolean isXY = (axes.axisXY.z != 0);
    int modelIndex = viewer.getCurrentModelIndex();
    // includes check here for background model present
    boolean isUnitCell = (axesMode == JmolConstants.AXES_MODE_UNITCELL);
    if (viewer.isJmolDataFrame(modelIndex)
        || isUnitCell && modelIndex < 0)
      return;
    int nPoints = 6;
    int labelPtr = 0;
    if (isUnitCell && cellInfos != null
        && cellInfos[modelIndex].haveUnitCell()) {
      nPoints = 3;
      labelPtr = 6;
    } else if (isXY) {
      nPoints = 3;
      labelPtr = 9;
    } else if (axesMode == JmolConstants.AXES_MODE_BOUNDBOX) {
      nPoints = 6;
      labelPtr = (viewer.getAxesOrientationRasmol() ? 15 : 9);
    }
    if (axes.labels != null) {
      if (nPoints != 3)
        nPoints = axes.labels.length;
      labelPtr = -1;
    }
    boolean isDataFrame = viewer.isJmolDataFrame();

    int slab = g3d.getSlab();
    int diameter = mad;
    boolean drawTicks = false;
    if (isXY) {
      if (exportType == Graphics3D.EXPORT_CARTESIAN)
        return;
      if (mad >= 20) {
        // width given in angstroms as mAng.
        // max out at 500
        diameter = (mad > 500 ? 5 : mad / 100);
        if (diameter == 0)
          diameter = 2;
      } else {
        if (g3d.isAntialiased())
          diameter += diameter;
      }
      g3d.setSlab(0);
      pt0.set(viewer.transformPoint(axes.axisXY));
      originScreen.set(pt0.x, pt0.y, pt0.z);
      float zoomDimension = viewer.getScreenDim();
      float scaleFactor = zoomDimension / 10f * axes.scale;
      if (g3d.isAntialiased())
        scaleFactor *= 2;
      for (int i = 0; i < 3; i++) {
        viewer.rotatePoint(axes.getAxisPoint(i, false), screens[i]);
        screens[i].z *= -1;
        screens[i].scaleAdd(scaleFactor, screens[i], originScreen);
      }
    } else {
      drawTicks = (axes.tickInfos != null);
      if (drawTicks) {
        if (atomA == null) {
          atomA = new Point3fi();
          atomB = new Point3fi();
        }
        atomA.set(axes.getOriginPoint(isDataFrame));
      }
      viewer.transformPointNoClip(axes.getOriginPoint(isDataFrame),
          originScreen);
      diameter = getDiameter((int) originScreen.z, mad);
      for (int i = nPoints; --i >= 0;)
        viewer.transformPointNoClip(axes.getAxisPoint(i, isDataFrame),
            screens[i]);
    }
    float xCenter = originScreen.x;
    float yCenter = originScreen.y;
    colixes[0] = viewer.getObjectColix(StateManager.OBJ_AXIS1);
    colixes[1] = viewer.getObjectColix(StateManager.OBJ_AXIS2);
    colixes[2] = viewer.getObjectColix(StateManager.OBJ_AXIS3);
    for (int i = nPoints; --i >= 0;) {
      colix = colixes[i % 3];
      g3d.setColix(colix);
      String label = (axes.labels == null ? axisLabels[i + labelPtr]
          : i < axes.labels.length ? axes.labels[i] : null);
      if (label != null && label.length() > 0)
        renderLabel(label, screens[i].x, screens[i].y, screens[i].z, xCenter,
            yCenter);
      if (drawTicks) {
        tickInfo = axes.tickInfos[(i % 3) + 1];
        if (tickInfo == null)
          tickInfo = axes.tickInfos[0];
        atomB.set(axes.getAxisPoint(i, isDataFrame));
        if (tickInfo != null) {
          tickInfo.first = 0;
          tickInfo.signFactor = (i % 6 >= 3 ? -1 : 1);
        }
      }
      renderLine(originScreen, screens[i], diameter, pt0, pt1, drawTicks
          && tickInfo != null);
    }
    if (nPoints == 3 && !isXY) { // a b c
      colix = viewer.getColixBackgroundContrast();
      g3d.setColix(colix);
      renderLabel("0", originScreen.x, originScreen.y, originScreen.z, xCenter,
          yCenter);
    }
    if (isXY)
      g3d.setSlab(slab);
  }
  
  private void renderLabel(String str, float x, float y, float z, float xCenter, float yCenter) {
    FontMetrics fontMetrics = font3d.fontMetrics;
    int strAscent = fontMetrics.getAscent();
    int strWidth = fontMetrics.stringWidth(str);
    float dx = x - xCenter;
    float dy = y - yCenter;
    if ((dx != 0 || dy != 0)) {
      float dist = (float) Math.sqrt(dx * dx + dy * dy);
      dx = (strWidth * 0.75f * dx / dist);
      dy = (strAscent * 0.75f * dy / dist);
      x += dx;
      y += dy;
    }
    float xStrBaseline = x - strWidth / 2;
    float yStrBaseline = y + strAscent / 2;
    g3d.drawString(str, font3d, (int) xStrBaseline, (int) yStrBaseline, (int) z, (int) z);
  }
}
