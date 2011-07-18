/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-10-08 22:18:02 -0500 (Mon, 08 Oct 2007) $
 * $Revision: 8391 $

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

package org.jmol.shapespecial;

import java.util.BitSet;
import java.util.Iterator;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.util.Quadric;
import org.jmol.modelset.Atom;
import org.jmol.shape.Shape;
import org.jmol.shape.ShapeRenderer;
import org.jmol.shapespecial.Ellipsoids.Ellipsoid;
import org.jmol.viewer.JmolConstants;

public class EllipsoidsRenderer extends ShapeRenderer {

  private Ellipsoids ellipsoids;
  private boolean drawDots, drawArcs, drawAxes, drawFill, drawBall;
  private boolean wireframeOnly;
  private int dotCount;
  private int[] coords;
  private Vector3f[] axes;
  private final float[] factoredLengths = new float[3];
  private int diameter, diameter0;
  private int selectedOctant = -1;
  private Point3i[] selectedPoints = new Point3i[3];
  private int iCutout = -1;

  private Matrix3f mat = new Matrix3f();
  private Matrix3f mTemp = new Matrix3f();
  private Matrix4f mDeriv = new Matrix4f();
  private Matrix3f matScreenToCartesian = new Matrix3f();
  private Matrix3f matScreenToEllipsoid = new Matrix3f();
  private Matrix3f matEllipsoidToScreen = new Matrix3f();
  
  
  private double[] coef = new double[10];
  private final Vector3f v1 = new Vector3f();
  private final Vector3f v2 = new Vector3f();
  private final Vector3f v3 = new Vector3f();  
  private final Point3f pt1 = new Point3f();
  private final Point3f pt2 = new Point3f();
  private final Point3i s0 = new Point3i();
  private final Point3i s1 = new Point3i();
  private final Point3i s2 = new Point3i();
  private int dotScale;
  
  private final static float toRadians = (float) Math.PI/180f;
  private final static float[] cossin = new float[36];
  static {
    for (int i = 5, pt = 0; i <= 90; i += 5) {
      cossin[pt++] = (float) Math.cos(i * toRadians);
      cossin[pt++] = (float) Math.sin(i * toRadians);
    }
  }
  

  @Override
  protected void render() {
    ellipsoids = (Ellipsoids) shape;
    if (ellipsoids.mads == null && !ellipsoids.haveEllipsoids)
      return;
    wireframeOnly = (viewer.getWireframeRotation() && viewer.getInMotion());
    drawAxes = viewer.getBooleanProperty("ellipsoidAxes");
    drawArcs = viewer.getBooleanProperty("ellipsoidArcs");
    drawBall = viewer.getBooleanProperty("ellipsoidBall") && !wireframeOnly;
    drawDots = viewer.getBooleanProperty("ellipsoidDots") && !wireframeOnly;
    drawFill = viewer.getBooleanProperty("ellipsoidFill") && !wireframeOnly;
    fillArc = drawFill && !drawBall;
    diameter0 = (int) (((Float) viewer.getParameter("ellipsoidAxisDiameter"))
        .floatValue() * 1000);
    //perspectiveOn = viewer.getPerspectiveDepth();
    /* general logic:
     * 
     * 
     * 1) octant and DOTS are incompatible; octant preferred over dots
     * 2) If not BALL, ARCS, or DOTS, the rendering defaults to AXES
     * 3) If DOTS, then turn off ARCS and FILL
     * 
     * note that FILL serves to provide a cut-out for BALL and a 
     * filling for ARCS
     */

    if (drawBall)
      drawDots = false;
    if (!drawDots && !drawArcs && !drawBall)
      drawAxes = true;
    if (drawDots) {
      drawArcs = false;
      drawFill = false;
      dotScale = viewer.getDotScale();
    }

    if (drawDots) {
      dotCount = ((Integer) viewer.getParameter("ellipsoidDotCount"))
          .intValue();
      if (coords == null || coords.length != dotCount * 3)
        coords = new int[dotCount * 3];
    }

    Matrix4f m4 = viewer.getMatrixtransform();
    mat.setRow(0, m4.m00, m4.m01, m4.m02);
    mat.setRow(1, m4.m10, m4.m11, m4.m12);
    mat.setRow(2, m4.m20, m4.m21, m4.m22);
    matScreenToCartesian.invert(mat);

    Atom[] atoms = modelSet.atoms;
    for (int i = modelSet.getAtomCount(); --i >= 0;) {
      Atom atom = atoms[i];
      if (!atom.isVisible(myVisibilityFlag))
        continue;
      if (atom.screenZ <= 1)
        continue;
      Object[] ellipsoid = atom.getEllipsoid();
      if (ellipsoid == null)
        continue;
      colix = Shape.getColix(ellipsoids.colixes, i, atom);
      if (!g3d.setColix(colix))
        continue;
      render1(atom, ellipsoid);
    }
    
    if (ellipsoids.haveEllipsoids) {
      Iterator<Ellipsoid> e = ellipsoids.htEllipsoids.values().iterator();
      while (e.hasNext()) {
        Ellipsoid ellipsoid = e.next();
        if (ellipsoid.visible && ellipsoid.isValid)
          renderEllipsoid(ellipsoid); 
      }
    }
    coords = null;
  }

  private final Point3i[] screens = new Point3i[32];
  //private final int[] intensities = new int[32];
  private final Point3f[] points = new Point3f[6];
  {
    for (int i = 0; i < points.length; i++)
      points[i] = new Point3f();
    for (int i = 0; i < screens.length; i++)
      screens[i] = new Point3i();
  }

  private static int[] axisPoints = {-1, 1, -2, 2, -3, 3};
  
  // octants are sets of three axisPoints references in proper rotation order
  // axisPoints[octants[i]] indicates the axis and direction (pos/neg)

  private static int[] octants = {
    5, 0, 3,
    5, 2, 0, //arc
    4, 0, 2,
    4, 3, 0, //arc
    5, 2, 1, 
    5, 1, 3, //arc
    4, 3, 1, 
    4, 1, 2  //arc
  };

  private int dx;
  private float perspectiveFactor;
  private Point3f center;
  
  private void render1(Atom atom, Object[] ellipsoid) {
    s0.set(atom.screenX, atom.screenY, atom.screenZ);
    float[] lengths = (float[]) ellipsoid[1];
    boolean isOK = true;
    for (int i = 3; --i >= 0;) {
      factoredLengths[i] = lengths[i + 3];
      if (Float.isNaN(factoredLengths[i]))
        isOK = false;
    }
    axes = (Vector3f[]) ellipsoid[0];
    if (axes == null) { //isotropic
      axes = unitVectors;
    }
    setMatrices();
    //[0] is shortest; [2] is longest
    center = atom;
    setAxes(1.0f);
    if (g3d.isClippedXY(dx + dx, atom.screenX, atom.screenY))
      return;
    diameter = viewer.scaleToScreen(atom.screenZ, wireframeOnly ? 1 : diameter0);
    if (!isOK || drawBall) {
      renderBall();
      if (!isOK)
        return;
      if (drawArcs || drawAxes) {
        g3d.setColix(viewer.getColixBackgroundContrast());
        //setAxes(atom, 1.0f);
        if (drawAxes)
          renderAxes();
        if (drawArcs)
          renderArcs(atom);
        g3d.setColix(colix);
      }
    } else {
      if (drawAxes)
        renderAxes();
      if (drawArcs)
        renderArcs(atom);      
    }
    if (drawDots)
      renderDots(atom);
  }

  private void setMatrices() {
    Quadric.setEllipsoidMatrix(axes, factoredLengths, v1, mat);
    // make this screen coordinates to ellisoidal coordinates
    matScreenToEllipsoid.mul(mat, matScreenToCartesian);
    matEllipsoidToScreen.invert(matScreenToEllipsoid);
    //matEllipsoidToScreen.mul(viewer.scaleToScreen(s0.z, 1000));
    perspectiveFactor = viewer.scaleToPerspective(s0.z, 1.0f);
    matScreenToEllipsoid.mul(1f/perspectiveFactor);
  }
  
  private final static Vector3f[] unitVectors = {
    JmolConstants.axisX, JmolConstants.axisY, JmolConstants.axisZ};
  
  private final static Vector3f[] unitAxisVectors = {
    JmolConstants.axisNX, JmolConstants.axisX, 
    JmolConstants.axisNY, JmolConstants.axisY, 
    JmolConstants.axisNZ, JmolConstants.axisZ };

  
  private void setAxes(float f) {
    for (int i = 0; i < 6; i++) {
      int iAxis = axisPoints[i];
      int i012 = Math.abs(iAxis) - 1;
      points[i].scaleAdd(f * factoredLengths[i012] * (iAxis < 0 ? -1 : 1),
          axes[i012], center);
      pt1.set(unitAxisVectors[i]);
      pt1.scale(f);

      matEllipsoidToScreen.transform(pt1);
      screens[i].set((int) (s0.x + pt1.x * perspectiveFactor),
          (int) (s0.y + pt1.y * perspectiveFactor), (int) (pt1.z + s0.z));
    }
    dx = 2 + viewer.scaleToScreen(s0.z, 
        (int) (f * (Float.isNaN(factoredLengths[2]) ? 1.0 : factoredLengths[2]) * 1000));
  }

  private void renderAxes() {
    if (drawBall && drawFill) {
      g3d.fillCylinder(Graphics3D.ENDCAPS_FLAT, diameter, s0,
          selectedPoints[0]);
      g3d.fillCylinder(Graphics3D.ENDCAPS_FLAT, diameter, s0,
          selectedPoints[1]);
      g3d.fillCylinder(Graphics3D.ENDCAPS_FLAT, diameter, s0,
          selectedPoints[2]);
      return;
    }

//    if (Logger.debugging) {
//      g3d.setColix(Graphics3D.RED);
//      g3d.fillCylinder(Graphics3D.ENDCAPS_FLAT, diameter, screens[0],
//          screens[1]);
//      g3d.setColix(Graphics3D.GREEN);
//      g3d.fillCylinder(Graphics3D.ENDCAPS_FLAT, diameter, screens[2],
//          screens[3]);
//      g3d.setColix(Graphics3D.BLUE);
//      g3d.fillCylinder(Graphics3D.ENDCAPS_FLAT, diameter, screens[4],
//          screens[5]);
//      g3d.setColix(colix);
//    } else {
      g3d.fillCylinder(Graphics3D.ENDCAPS_FLAT, diameter, screens[0],
          screens[1]);
      g3d.fillCylinder(Graphics3D.ENDCAPS_FLAT, diameter, screens[2],
          screens[3]);
      g3d.fillCylinder(Graphics3D.ENDCAPS_FLAT, diameter, screens[4],
          screens[5]);
//    }

  }
  
  private void renderDots(Point3f ptAtom) {
    for (int i = 0; i < coords.length;) {
      float fx = (float) Math.random();
      float fy = (float) Math.random();
      fx *= (Math.random() > 0.5 ? -1 : 1);
      fy *= (Math.random() > 0.5 ? -1 : 1);
      float fz = (float) Math.sqrt(1 - fx * fx - fy * fy);
      if (Float.isNaN(fz))
        continue;
      fz = (Math.random() > 0.5 ? -1 : 1) * fz;
      pt1.scaleAdd(fx * factoredLengths[0], axes[0], ptAtom);
      pt1.scaleAdd(fy * factoredLengths[1], axes[1], pt1);
      pt1.scaleAdd(fz * factoredLengths[2], axes[2], pt1);
      viewer.transformPoint(pt1, s1);
      coords[i++] = s1.x;
      coords[i++] = s1.y;
      coords[i++] = s1.z;
    }
    g3d.drawPoints(dotCount, coords, dotScale);
  }

  private void renderArcs(Point3f ptAtom) {
    if (g3d.drawEllipse(ptAtom, points[0], points[2], fillArc, wireframeOnly)) {
      g3d.drawEllipse(ptAtom, points[2], points[5], fillArc, wireframeOnly);
      g3d.drawEllipse(ptAtom, points[5], points[0], fillArc, wireframeOnly);
      return;
    }
    for (int i = 1; i < 8; i += 2) {
      int pt = i*3;
      renderArc(ptAtom, octants[pt], octants[pt + 1]);
      renderArc(ptAtom, octants[pt + 1], octants[pt + 2]);
      renderArc(ptAtom, octants[pt + 2], octants[pt]);
    }
  }
  
  private boolean fillArc;
  private BitSet bsTemp = new BitSet();
  
  private void renderArc(Point3f ptAtom, int ptA, int ptB) {
    v1.set(points[ptA]);
    v1.sub(ptAtom);
    v2.set(points[ptB]);
    v2.sub(ptAtom);
    float d1 = v1.length();
    float d2 = v2.length();
    v1.normalize();
    v2.normalize();
    v3.cross(v1, v2);
    pt1.set(points[ptA]);
    s1.set(screens[ptA]);
    short normix = Graphics3D.get2SidedNormix(v3, bsTemp);
    if (!fillArc && !wireframeOnly)
      screens[6].set(s1);
    for (int i = 0, pt = 0; i < 18; i++, pt += 2) {
      pt2.scaleAdd(cossin[pt] * d1, v1, ptAtom);
      pt2.scaleAdd(cossin[pt + 1] * d2, v2, pt2);
      viewer.transformPoint(pt2, s2);
      if (fillArc)
        g3d.fillTriangle(s0, colix, normix, s1, colix, normix, s2, colix,
            normix);
      else if (wireframeOnly)
        g3d.fillCylinder(Graphics3D.ENDCAPS_FLAT, diameter, s1, s2);
      else
        screens[i + 7].set(s2);
      pt1.set(pt2);
      s1.set(s2);
    }
    if (!fillArc && !wireframeOnly)
      for (int i = 0; i < 18; i++) {
        g3d.fillHermite(5, diameter, diameter, diameter, 
            screens[i == 0 ? i + 6 : i + 5], 
            screens[i + 6], 
            screens[i + 7], 
            screens[i == 17 ? i + 7 : i + 8]);
      }
  }


  protected void renderEllipsoid(Ellipsoid ellipsoid) {
    axes = ellipsoid.axes;
    for (int i = 0; i < 3; i++)
      factoredLengths[i] = ellipsoid.lengths[i];
    viewer.transformPoint(ellipsoid.center, s0);
    setMatrices();
    center = ellipsoid.center;
    setAxes(1);
    colix = ellipsoid.colix;
    if (!g3d.setColix(colix))
      return;
    renderBall();
  }
 
  protected void renderBall() {
    setSelectedOctant();
    // get equation and differential
    Quadric.getEquationForQuadricWithCenter(s0.x, s0.y, s0.z,
        matScreenToEllipsoid, v1, mTemp, coef, mDeriv);
    g3d.fillEllipsoid(center, points, s0.x, s0.y, s0.z, dx + dx, matScreenToEllipsoid,
        coef, mDeriv, selectedOctant, selectedOctant >= 0 ? selectedPoints : null);
  }

  private void setSelectedOctant() {
    int zMin = Integer.MAX_VALUE;
    selectedOctant = -1;
    iCutout = -1;
    if (drawFill) {
      for (int i = 0; i < 8; i++) {
        int ptA = octants[i * 3];
        int ptB = octants[i * 3 + 1];
        int ptC = octants[i * 3 + 2];
        int z = screens[ptA].z + screens[ptB].z + screens[ptC].z;
        if (z < zMin) {
          zMin = z;
          iCutout = i;
        }
      }
      //TODO -- adjust x and y for perspective?
      s1.set(selectedPoints[0] = screens[octants[iCutout * 3]]);
      s1.add(selectedPoints[1] = screens[octants[iCutout * 3 + 1]]);
      s1.add(selectedPoints[2] = screens[octants[iCutout * 3 + 2]]);
      s1.scaleAdd(-3, s0, s1);
      pt1.set(s1.x, s1.y, s1.z);
      matScreenToEllipsoid.transform(pt1);
      selectedOctant = Quadric.getOctant(pt1);
    }
  }  
}
