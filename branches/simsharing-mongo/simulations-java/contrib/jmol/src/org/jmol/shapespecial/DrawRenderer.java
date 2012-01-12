/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-02-25 11:44:18 -0600 (Sat, 25 Feb 2006) $
 * $Revision: 4528 $
 *
 * Copyright (C) 2005  Miguel, Jmol Development
 *
 * Contact: jmol-developers@lists.sf.net, jmol-developers@lists.sourceforge.net
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
package org.jmol.shapespecial;

import java.util.List;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.shape.MeshRenderer;
import org.jmol.viewer.ActionManager;
import org.jmol.viewer.JmolConstants;

public class DrawRenderer extends MeshRenderer {

  private int drawType;
  private DrawMesh dmesh;
  
  private Point3f[] controlHermites;
  private final Point3f vpt0 = new Point3f();
  private final Point3f vpt1 = new Point3f();
  private final Point3f vpt2 = new Point3f();
  private final Vector3f vTemp = new Vector3f();
  private final Vector3f vTemp2 = new Vector3f();

  @Override
  protected void render() {
    /*
     * Each drawn object, draw.meshes[i], may consist of several polygons, one
     * for each MODEL FRAME. Or, it may be "fixed" and only contain one single
     * polygon.
     * 
     */
    imageFontScaling = viewer.getImageFontScaling();
    Draw draw = (Draw) shape;
    for (int i = draw.meshCount; --i >= 0;)
      if (render1(dmesh = (DrawMesh) draw.meshes[i]))
        renderInfo();
  }
  
  @Override
  protected boolean isPolygonDisplayable(int i) {
    return Draw.isPolygonDisplayable(dmesh, i) 
        && (dmesh.modelFlags == null || dmesh.bsMeshesVisible.get(i)); 
  }
  
  @Override
  protected void render2(boolean isExport) {
    drawType = dmesh.drawType;
    diameter = dmesh.diameter;
    width = dmesh.width;
    if (mesh.lineData != null) {
      drawLineData(mesh.lineData);
      return;
    }
    boolean isDrawPickMode = (viewer.getPickingMode() == ActionManager.PICKING_DRAW);
    int nPoints = vertexCount;
    boolean isCurved = (
        (drawType == JmolConstants.DRAW_CURVE 
            || drawType == JmolConstants.DRAW_ARROW
            || drawType == JmolConstants.DRAW_ARC) 
        && vertexCount >= 2);
    boolean isSegments = (drawType == JmolConstants.DRAW_LINE_SEGMENT);
    if (width > 0 && isCurved) {
      pt1f.set(0, 0, 0);
      int n = (drawType == JmolConstants.DRAW_ARC ? 2 :vertexCount);
      for (int i = 0; i < n; i++)
        pt1f.add(vertices[i]);
      pt1f.scale(1f / n);
      viewer.transformPoint(pt1f, pt1i);
      diameter = viewer.scaleToScreen(pt1i.z, (int) (width * 1000));
      if (diameter == 0)
        diameter = 1;
    }    
    if ((dmesh.isVector) && dmesh.haveXyPoints) {
      int ptXY = 0;
      // [x y] or [x,y] refers to an xy point on the screen
      // just a Point3f with z = Float.MAX_VALUE
      //  [x y %] or [x,y %] refers to an xy point on the screen
      // as a percent 
      // just a Point3f with z = -Float.MAX_VALUE
      for (int i = 0; i < 2; i++)
        if (vertices[i].z == Float.MAX_VALUE || vertices[i].z == -Float.MAX_VALUE)
          ptXY += i + 1;
      if (--ptXY < 2) {
        renderXyArrow(ptXY);
        return;
      }
    }    
    int tension = 5;
    switch (drawType) {
    default:
      super.render2(false);
      break;
    case JmolConstants.DRAW_CIRCULARPLANE:
      if (dmesh.scale > 0)
        width *= dmesh.scale;
      super.render2(false);
      break;
    case JmolConstants.DRAW_CIRCLE:
      viewer.transformPoint(vertices[0], pt1i);
      if (diameter == 0 && width == 0)
        width = 1.0f;
      if (dmesh.scale > 0)
        width *= dmesh.scale;
      if (width > 0)
        diameter = viewer.scaleToScreen(pt1i.z, (int) (width * 1000));
      if (diameter > 0 && (mesh.drawTriangles || mesh.fillTriangles))
        g3d.drawFilledCircle(colix, mesh.fillTriangles ? colix : 0, diameter, pt1i.x, pt1i.y, pt1i.z);
      break;
    case JmolConstants.DRAW_CURVE:
    case JmolConstants.DRAW_LINE_SEGMENT:
      //unnecessary
      break;
    case JmolConstants.DRAW_ARC:
      //renderArrowHead(controlHermites[nHermites - 2], controlHermites[nHermites - 1], false);
      // 
      // {pt1} {pt2} {ptref} {nDegreesOffset, theta, fractionalOffset}
      float nDegreesOffset = (vertexCount > 3 ? vertices[3].x : 0);
      float theta = (vertexCount > 3 ? vertices[3].y : 360);
      if (theta == 0)
        return;
      float fractionalOffset = (vertexCount > 3 ? vertices[3].z : 0);
      vTemp.set(vertices[1]);
      vTemp.sub(vertices[0]);
      // crossing point
      pt1f.scaleAdd(fractionalOffset, vTemp, vertices[0]);
      // define rotational axis
      Matrix3f mat = new Matrix3f();
      mat.set(new AxisAngle4f(vTemp, (float) (nDegreesOffset * Math.PI / 180)));
      // vector to rotate
      if (vertexCount > 2)
        vTemp2.set(vertices[2]);
      else 
        vTemp2.set(Draw.randomPoint());
      vTemp2.sub(vertices[0]);
      vTemp2.cross(vTemp, vTemp2);
      vTemp2.cross(vTemp2, vTemp);
      vTemp2.normalize();
      vTemp2.scale(dmesh.scale / 2);
      mat.transform(vTemp2);
      //control points
      float degrees = theta / 5;
      while (Math.abs(degrees) > 5)
        degrees /= 2;
      nPoints = (int) (theta / degrees + 0.5f) + 1;
      while (nPoints < 10) {
        degrees /= 2;
        nPoints = (int) (theta / degrees + 0.5f) + 1;
      }
      mat.set(new AxisAngle4f(vTemp, (float) (degrees * Math.PI / 180)));
      screens = viewer.allocTempScreens(nPoints);
      int iBase = nPoints - (dmesh.scale < 2 ? 3 : 3);
      for (int i = 0; i < nPoints; i++) {
        if (i == iBase)
          vpt0.set(vpt1);
        vpt1.scaleAdd(1, vTemp2, pt1f);
        if (i == 0)
          vpt2.set(vpt1);
        viewer.transformPoint(vpt1, screens[i]);
        mat.transform(vTemp2);
      }
      if (dmesh.isVector && !dmesh.noHead) {
        renderArrowHead(vpt0, vpt1, 0.3f, false, false, dmesh.isBarb);
        viewer.transformPoint(pt1f, screens[nPoints - 1]);
      }
      pt1f.set(vpt2);
      break;
    case JmolConstants.DRAW_ARROW:
      if (vertexCount == 2) {
        renderArrowHead(vertices[0], vertices[1], 0, false, true, dmesh.isBarb);
        return;
      }
      int nHermites = 5;
      if (controlHermites == null || controlHermites.length < nHermites + 1) {
        controlHermites = new Point3f[nHermites + 1];
      }
      Graphics3D.getHermiteList(tension, vertices[vertexCount - 3],
            vertices[vertexCount - 2], vertices[vertexCount - 1],
            vertices[vertexCount - 1], vertices[vertexCount - 1],
            controlHermites, 0, nHermites);
      renderArrowHead(controlHermites[nHermites - 2], controlHermites[nHermites - 1], 0, false, false, dmesh.isBarb);
      break;
    }
    if (diameter == 0)
      diameter = 3;
    if (isCurved) {
      for (int i = 0, i0 = 0; i < nPoints - 1; i++) {
        g3d.fillHermite(tension, diameter, diameter, diameter, screens[i0],
            screens[i], screens[i + 1], screens[i
                + (i == nPoints - 2 ? 1 : 2)]);
        i0 = i;
      }
    } else if (isSegments) {
      for (int i = 0; i < nPoints - 1; i++)
        drawLine(i, i + 1, true, vertices[i], vertices[i + 1], screens[i], screens[i + 1]);
    }
    
    if (isDrawPickMode && !isExport) {
      renderHandles();
    }
  }
  
  private void drawLineData(List<Point3f[]> lineData) {
    if (diameter == 0)
      diameter = 3;
    for (int i = lineData.size(); --i >= 0;) {
      Point3f[] pts = lineData.get(i);
      viewer.transformPoint(pts[0], pt1i);
      viewer.transformPoint(pts[1], pt2i);
      drawLine(-1, -2, true, pts[0], pts[1], pt1i, pt2i);
    }
  }

  private void renderXyArrow(int ptXY) {
    int ptXYZ = 1 - ptXY;
    Point3f[] arrowPt = new Point3f[2];
    arrowPt[ptXYZ] = vpt1;
    arrowPt[ptXY] = vpt0;
    // set up (0,0,0) to ptXYZ in real and screen coordinates
    vpt0.set(screens[ptXY].x, screens[ptXY].y, screens[ptXY].z);
    viewer.rotatePoint(vertices[ptXYZ], vpt1);
    vpt1.z *= -1;
    float zoomDimension = viewer.getScreenDim();
    float scaleFactor = zoomDimension / 20f;
    vpt1.scaleAdd(dmesh.scale * scaleFactor, vpt1, vpt0);
    if (diameter == 0)
      diameter = 1;
    pt1i.set((int) vpt0.x, (int) vpt0.y, (int) vpt0.z );
    pt2i.set((int) vpt1.x, (int) vpt1.y, (int) vpt1.z );
    if (diameter < 0)
      g3d.drawDottedLine(pt1i, pt2i);
    else
      g3d.fillCylinder(Graphics3D.ENDCAPS_FLAT, diameter, pt1i, pt2i);
    renderArrowHead(vpt0, vpt1, 0, true, false, false);
  }

  private final Point3f pt0f = new Point3f();
  private final Point3i pt0i = new Point3i();

  private void renderArrowHead(Point3f pt1, Point3f pt2, float factor2, 
                               boolean isTransformed, boolean withShaft, boolean isBarb) {
    if (dmesh.noHead)
      return;
    float fScale = dmesh.drawArrowScale;
    if (fScale == 0)
      fScale = viewer.getDefaultDrawArrowScale();
    if (fScale <= 0)
      fScale = 0.5f;
    if (isTransformed)
      fScale *= 40;
    if (factor2 > 0)
      fScale *= factor2;
    
    pt0f.set(pt1);
    pt2f.set(pt2);
    float d = pt0f.distance(pt2f);
    if (d == 0)
      return;
    vTemp.set(pt2f);
    vTemp.sub(pt0f);
    vTemp.normalize();
    vTemp.scale(fScale / 5);
    if (!withShaft)
      pt2f.add(vTemp);
    vTemp.scale(5);
    pt1f.set(pt2f);
    pt1f.sub(vTemp);
    if (isTransformed) {
      pt1i.set((int)pt1f.x, (int)pt1f.y, (int)pt1f.z);
      pt2i.set((int)pt2f.x, (int)pt2f.y, (int)pt2f.z);
    } else {
      viewer.transformPoint(pt2f, pt2i);
      viewer.transformPoint(pt1f, pt1i);
      viewer.transformPoint(pt0f, pt0i);
    }
    if (pt2i.z == 1 || pt1i.z == 1) //slabbed
      return;
    int headDiameter;
    if (diameter > 0) {
      headDiameter = diameter * 3;
    } else {
      vTemp.set(pt2i.x - pt1i.x, pt2i.y - pt1i.y, pt2i.z - pt1i.z);
      headDiameter = (int) (vTemp.length() * .5);
      diameter = headDiameter / 5;
    }
    if (diameter < 1)
      diameter = 1;
    if (headDiameter > 2)
      g3d.fillConeScreen(Graphics3D.ENDCAPS_FLAT, headDiameter, pt1i, pt2i, isBarb);
    if (withShaft)
      g3d.fillCylinderScreen(Graphics3D.ENDCAPS_OPENEND, diameter, pt0i,
           pt1i);
  }
  
  private void renderHandles() {
    int diameter = (int) (10 * imageFontScaling);
    switch (drawType) {
    case JmolConstants.DRAW_NONE:
    case JmolConstants.DRAW_TRIANGLE:
      return;
    default:
      short colixFill = Graphics3D.getColixTranslucent(Graphics3D.GOLD, true, 0.5f);
      for (int i = dmesh.polygonCount; --i >= 0;) {
        if (!isPolygonDisplayable(i))
          continue;
        int[] vertexIndexes = dmesh.polygonIndexes[i];
        if (vertexIndexes == null)
          continue;
        for (int j = vertexIndexes.length; --j >= 0;) {
          int k = vertexIndexes[j];
          g3d.drawFilledCircle(Graphics3D.GOLD, colixFill, diameter, screens[k].x,
              screens[k].y, screens[k].z);
        }
        break;
      }
    }
  }
  
  private void renderInfo() {
    if (mesh.title == null || viewer.getDrawHover()
        || !g3d.setColix(viewer.getColixBackgroundContrast()))
      return;
    for (int i = dmesh.polygonCount; --i >= 0;)
      if (isPolygonDisplayable(i)) {
        //just the first line of the title -- nothing fancy here.
        byte fid = g3d.getFontFid(14 * imageFontScaling);
        g3d.setFont(fid);
        String s = mesh.title[i < mesh.title.length ? i : mesh.title.length - 1];
        int pt = 0;
        if (s.length() > 1 && s.charAt(0) == '>') {
          pt = dmesh.polygonIndexes[i].length - 1;
          s = s.substring(1);
          if (drawType == JmolConstants.DRAW_ARC)
            pt1f.set(pt2f);
        } 
        if (drawType != JmolConstants.DRAW_ARC)
          pt1f.set(vertices[dmesh.polygonIndexes[i][pt]]);
        viewer.transformPoint(pt1f, pt1i);
        int offset = (int) (5 * imageFontScaling);
        g3d.drawString(s, null, pt1i.x + offset, pt1i.y - offset,
            pt1i.z, pt1i.z);
        break;
      }
  }
  
}
