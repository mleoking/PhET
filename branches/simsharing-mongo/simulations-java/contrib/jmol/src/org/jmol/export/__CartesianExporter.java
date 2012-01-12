/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-05-18 15:41:42 -0500 (Fri, 18 May 2007) $
 * $Revision: 7752 $

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

package org.jmol.export;

import java.awt.Image;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Tuple3f;

import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;

/*
 * for programs that use the standard 3D coordinates.
 * 
 */
abstract public class __CartesianExporter extends ___Exporter {

  public __CartesianExporter() {
    exportType = Graphics3D.EXPORT_CARTESIAN;
    lineWidthMad = 100;
  }

  protected AxisAngle4f viewpoint = new AxisAngle4f();

  protected Point3f getModelCenter() {
    // "center" is the center of rotation, not
    // necessary the screen center or the center of the model. 
    // When the user uses ALT-CTRL-drag, Jmol is applying an 
    // XY screen translation AFTER the matrix transformation. 
    // Apparently, that's unusual in this business. 
    // (The rotation center is generally directly
    // in front of the observer -- not allowing, for example,
    // holding the model in one's hand at waist level and rotating it.)

    // But there are good reasons to do it the Jmol way. If you don't, then
    // what happens is that the distortion pans over the moving model
    // and you get an odd lens effect rather than the desired smooth
    // panning. So we must approximate.

    return referenceCenter;
  }

  protected Point3f getCameraPosition() {

    // used for VRML/X3D only

    Point3f ptCamera = new Point3f();
    Point3f pt = new Point3f(screenWidth / 2, screenHeight / 2, 0);
    viewer.unTransformPoint(pt, ptCamera);
    ptCamera.sub(center);
    // this is NOT QUITE correct when the model has been shifted with CTRL-ALT
    // because in that case the center of distortion is not the screen center,
    // and these simpler perspective models don't allow for that.
    tempP3.set(screenWidth / 2, screenHeight / 2, cameraDistance
        * scalePixelsPerAngstrom);
    viewer.unTransformPoint(tempP3, tempP3);
    tempP3.sub(center);
    ptCamera.add(tempP3);

    System.out.println(ptCamera + " " + cameraPosition);
    //  return ptCamera;

    return cameraPosition;

  }

  private void setTempPoints(Point3f ptA, Point3f ptB, boolean isCartesian) {
    if (isCartesian) {
      // really first order -- but actual coord
      tempP1.set(ptA);
      tempP2.set(ptB);
    } else {
      viewer.unTransformPoint(ptA, tempP1);
      viewer.unTransformPoint(ptB, tempP2);
    }
  }

  protected int getCoordinateMap(Tuple3f[] vertices, int[] coordMap, BitSet bsValid) {
    int n = 0;
    for (int i = 0; i < coordMap.length; i++) {
      if (bsValid != null && !bsValid.get(i) || Float.isNaN(vertices[i].x)) {
        if (bsValid != null)
          bsValid.clear(i);
        continue;
      }
      coordMap[i] = n++;
    }
    return n;
  }

  protected int[] getNormalMap(Tuple3f[] normals, int nNormals,
                               BitSet bsValid, List<String> vNormals) {
    Map<String, Integer> htNormals = new Hashtable<String, Integer>();
    int[] normalMap = new int[nNormals];
    for (int i = 0; i < nNormals; i++) {
      String s;
      if (bsValid != null && !bsValid.get(i) || Float.isNaN(normals[i].x)){
        if (bsValid != null)
          bsValid.clear(i);
        continue;
      }
      s = (round(normals[i].x) + " " + round(normals[i].y) + " "
          + round(normals[i].z) + "\n");
      if (htNormals.containsKey(s)) {
        normalMap[i] = htNormals.get(s).intValue();
      } else {
        normalMap[i] = vNormals.size();
        vNormals.add(s);
        htNormals.put(s, Integer.valueOf(normalMap[i]));
      }
    }
    return normalMap;
  }

  protected void outputIndices(int[][] indices, int[] map, int nPolygons,
                               BitSet bsPolygons, int faceVertexMax) {
    boolean isAll = (bsPolygons == null);
    int i0 = (isAll ? nPolygons - 1 : bsPolygons.nextSetBit(0));
    for (int i = i0; i >= 0; i = (isAll ? i - 1 : bsPolygons.nextSetBit(i + 1)))
      outputFace(indices[i], map, faceVertexMax);
  }

  // these are elaborated in IDTF, MAYA, VRML, or X3D:

  protected abstract void outputFace(int[] is, int[] coordMap, int faceVertexMax);

  abstract protected void outputCircle(Point3f pt1, Point3f pt2, float radius,
                                       short colix, boolean doFill);

  abstract protected void outputCone(Point3f ptBase, Point3f ptTip,
                                     float radius, short colix);

  abstract protected boolean outputCylinder(Point3f ptCenter, Point3f pt1,
                                            Point3f pt2, short colix1,
                                            byte endcaps, float radius,
                                            Point3f ptX, Point3f ptY);

  abstract protected void outputEllipsoid(Point3f center, Point3f[] points,
                                          short colix);

  abstract protected void outputSphere(Point3f ptAtom2, float f, short colix);

  abstract protected void outputTextPixel(Point3f pt, int argb);

  abstract protected void outputTriangle(Point3f pt1, Point3f pt2, Point3f pt3,
                                         short colix);

  // these are called by Export3D:

  @Override
  void drawAtom(Atom atom) {
    outputSphere(atom, atom.madAtom / 2000f, atom.getColix());
  }

  @Override
  void drawCircle(int x, int y, int z, int diameter, short colix, boolean doFill) {
    // draw circle
    tempP3.set(x, y, z);
    viewer.unTransformPoint(tempP3, tempP1);
    float radius = viewer.unscaleToScreen(z, diameter) / 2;
    tempP3.set(x, y, z + 1);
    viewer.unTransformPoint(tempP3, tempP3);
    outputCircle(tempP1, tempP3, radius, colix, doFill);
  }

  @Override
  boolean drawEllipse(Point3f ptCenter, Point3f ptX, Point3f ptY, short colix,
                      boolean doFill) {
    tempV1.set(ptX);
    tempV1.sub(ptCenter);
    tempV2.set(ptY);
    tempV2.sub(ptCenter);
    tempV2.cross(tempV1, tempV2);
    tempV2.normalize();
    tempV2.scale(doFill ? 0.002f : 0.005f);
    tempP1.set(ptCenter);
    tempP1.sub(tempV2);
    tempP2.set(ptCenter);
    tempP2.add(tempV2);
    return outputCylinder(ptCenter, tempP1, tempP2, colix,
        doFill ? Graphics3D.ENDCAPS_FLAT : Graphics3D.ENDCAPS_NONE, 1.01f, ptX,
        ptY);
  }

  @Override
  void drawPixel(short colix, int x, int y, int z, int scale) {
    //measures, meshRibbon, dots
    tempP3.set(x, y, z);
    viewer.unTransformPoint(tempP3, tempP1);
    outputSphere(tempP1, 0.02f * scale, colix);
  }

  @Override
  void drawTextPixel(int argb, int x, int y, int z) {
    // text only
    tempP3.set(x, y, z);
    viewer.unTransformPoint(tempP3, tempP1);
    outputTextPixel(tempP1, argb);
  }

  @Override
  void fillConeScreen(short colix, byte endcap, int screenDiameter,
                      Point3f screenBase, Point3f screenTip, boolean isBarb) {
    viewer.unTransformPoint(screenBase, tempP1);
    viewer.unTransformPoint(screenTip, tempP2);
    float radius = viewer.unscaleToScreen(screenBase.z, screenDiameter) / 2;
    if (radius < 0.05f)
      radius = 0.05f;
    outputCone(tempP1, tempP2, radius, colix);
  }

  @Override
  void drawCylinder(Point3f ptA, Point3f ptB, short colix1, short colix2,
                    byte endcaps, int mad, int bondOrder) {
    setTempPoints(ptA, ptB, bondOrder == -1);
    float radius = mad / 2000f;
    if (colix1 == colix2) {
      outputCylinder(null, tempP1, tempP2, colix1, endcaps, radius, null, null);
    } else {
      tempV2.set(tempP2);
      tempV2.add(tempP1);
      tempV2.scale(0.5f);
      tempP3.set(tempV2);
      outputCylinder(null, tempP1, tempP3, colix1,
          (endcaps == Graphics3D.ENDCAPS_SPHERICAL ? Graphics3D.ENDCAPS_NONE
              : endcaps), radius, null, null);
      outputCylinder(null, tempP3, tempP2, colix2,
          (endcaps == Graphics3D.ENDCAPS_SPHERICAL ? Graphics3D.ENDCAPS_NONE
              : endcaps), radius, null, null);
      if (endcaps == Graphics3D.ENDCAPS_SPHERICAL) {
        outputSphere(tempP1, radius * 1.01f, colix1);
        outputSphere(tempP2, radius * 1.01f, colix2);
      }
    }
  }

  @Override
  void fillCylinderScreenMad(short colix, byte endcaps, int mad,
                             Point3f screenA, Point3f screenB) {
    float radius = mad / 2000f;
    setTempPoints(screenA, screenB, false);
    outputCylinder(null, tempP1, tempP2, colix, endcaps, radius, null, null);
  }

  @Override
  void fillCylinderScreen(short colix, byte endcaps, int screenDiameter,
                          Point3f screenA, Point3f screenB) {
    // vectors, polyhedra
    int mad = (int) (viewer.unscaleToScreen((screenA.z + screenB.z) / 2,
        screenDiameter) * 1000);
    fillCylinderScreenMad(colix, endcaps, mad, screenA, screenB);
  }

  @Override
  void fillEllipsoid(Point3f center, Point3f[] points, short colix, int x,
                     int y, int z, int diameter, Matrix3f toEllipsoidal,
                     double[] coef, Matrix4f deriv, Point3i[] octantPoints) {
    outputEllipsoid(center, points, colix);
  }

  @Override
  void fillSphere(short colix, int diameter, Point3f pt) {
    viewer.unTransformPoint(pt, tempP1);
    outputSphere(tempP1, viewer.unscaleToScreen(pt.z, diameter) / 2, colix);
  }

  @Override
  protected void fillTriangle(short colix, Point3f ptA, Point3f ptB,
                              Point3f ptC, boolean twoSided) {
    viewer.unTransformPoint(ptA, tempP1);
    viewer.unTransformPoint(ptB, tempP2);
    viewer.unTransformPoint(ptC, tempP3);
    outputTriangle(tempP1, tempP2, tempP3, colix);
    if (twoSided)
      outputTriangle(tempP1, tempP3, tempP2, colix);
  }

  @Override
  void plotImage(int x, int y, int z, Image image, short bgcolix, int width,
                 int height) {
    g3d.plotImage(x, y, z, image, jmolRenderer, bgcolix, width, height);
  }

  @Override
  void plotText(int x, int y, int z, short colix, String text, Font3D font3d) {
    // over-ridden in VRML and X3D
    // trick here is that we use Jmol's standard g3d package to construct
    // the bitmap, but then output to jmolRenderer, which returns control
    // here via drawPixel.
    g3d.plotText(x, y, z, g3d.getColorArgbOrGray(colix), text, font3d,
        jmolRenderer);
  }

  protected Matrix4f sphereMatrix = new Matrix4f();

  protected void setSphereMatrix(Point3f center, float rx, float ry, float rz,
                                 AxisAngle4f a, Matrix4f sphereMatrix) {
    if (a != null) {
      Matrix3f mq = new Matrix3f();
      Matrix3f m = new Matrix3f();
      m.m00 = rx;
      m.m11 = ry;
      m.m22 = rz;
      mq.set(a);
      mq.mul(m);
      sphereMatrix.set(mq);
    } else {
      sphereMatrix.setIdentity();
      sphereMatrix.m00 = rx;
      sphereMatrix.m11 = ry;
      sphereMatrix.m22 = rz;
    }
    sphereMatrix.m03 = center.x;
    sphereMatrix.m13 = center.y;
    sphereMatrix.m23 = center.z;
    sphereMatrix.m33 = 1;
  }
}
