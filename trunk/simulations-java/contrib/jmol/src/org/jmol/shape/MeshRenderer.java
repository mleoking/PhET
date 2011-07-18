/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-04-25 09:53:35 -0500 (Wed, 25 Apr 2007) $
 * $Revision: 7491 $
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
package org.jmol.shape;

import java.util.BitSet;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point3i;

import org.jmol.api.SymmetryInterface;
import org.jmol.g3d.Graphics3D;

public abstract class MeshRenderer extends ShapeRenderer {

  protected Mesh mesh;
  protected Point3f[] vertices;
  protected short[] normixes;
  protected Point3i[] screens;
  protected Vector3f[] transformedVectors;
  protected int vertexCount;
  
  protected float imageFontScaling;
  protected float scalePixelsPerMicron;
  protected int diameter;
  protected float width;

  protected boolean isTranslucent;
  protected boolean frontOnly;
  protected boolean antialias;
  protected boolean haveBsDisplay;

  protected Point4f thePlane;
  protected Point3f latticeOffset = new Point3f();

  protected final Point3f pt1f = new Point3f();
  protected final Point3f pt2f = new Point3f();

  protected final Point3i pt1i = new Point3i();
  protected final Point3i pt2i = new Point3i();
  protected final Point3i pt3i = new Point3i();

  @Override
  protected void render() {
    antialias = g3d.isAntialiased();  
    MeshCollection mc = (MeshCollection) shape;
    for (int i = mc.meshCount; --i >= 0;)
      render1(mc.meshes[i]);
  }

  // draw, isosurface, molecular orbitals
  public boolean render1(Mesh mesh) { // used by mps renderer
    this.mesh = mesh;
    if (!setVariables())
      return false;
    if (!g3d.setColix(colix) && !mesh.showContourLines)
      return mesh.title != null;
    latticeOffset.set(0, 0, 0);
    for (int i = vertexCount; --i >= 0;)
      if (vertices[i] != null)
        viewer.transformPoint(vertices[i], screens[i]);
    if (mesh.lattice == null || mesh.modelIndex < 0) {
      render2(exportType != Graphics3D.EXPORT_NOT);
    } else {
      SymmetryInterface unitcell = viewer.getModelUnitCell(mesh.modelIndex);
      if (unitcell != null) {
        Point3f vTemp = new Point3f();
        Point3i minXYZ = new Point3i();
        Point3i maxXYZ = new Point3i((int) mesh.lattice.x,
            (int) mesh.lattice.y, (int) mesh.lattice.z);
        unitcell.setMinMaxLatticeParameters(minXYZ, maxXYZ);
        for (int tx = minXYZ.x; tx < maxXYZ.x; tx++)
          for (int ty = minXYZ.y; ty < maxXYZ.y; ty++)
            for (int tz = minXYZ.z; tz < maxXYZ.z; tz++) {
              latticeOffset.set(tx, ty, tz);
              unitcell.toCartesian(latticeOffset, false);
              for (int i = vertexCount; --i >= 0;) {
                vTemp.set(vertices[i]);
                vTemp.add(latticeOffset);
                viewer.transformPoint(vTemp, screens[i]);
              }
              render2(exportType != Graphics3D.EXPORT_NOT);
            }
      }
    }

    if (screens != null)
      viewer.freeTempScreens(screens);
    return true;
  }

  private boolean setVariables() {
    vertices = (mesh.ptOffset == null && mesh.scale3d == 0 
        && mesh.q == null 
        ? mesh.vertices : mesh.getOffsetVertices(thePlane)); 
    
    colix = mesh.colix;
    if (mesh.visibilityFlags == 0)
      return false;
    if (mesh.lineData == null) {
      if ((vertexCount = mesh.vertexCount) == 0)
        return false;
      normixes = mesh.normixes;
      if (normixes == null || vertices == null)
        return false;
      // this can happen when user switches windows
      // during a surface calculation

      haveBsDisplay = (mesh.bsDisplay != null);
      frontOnly = !viewer.getSlabEnabled() && mesh.frontOnly && !mesh.isTwoSided;
      screens = viewer.allocTempScreens(vertexCount);
      transformedVectors = g3d.getTransformedVertexVectors();
    }
    isTranslucent = Graphics3D.isColixTranslucent(mesh.colix);
    return true;
  }

  // all of the following methods are overridden in subclasses
  // DO NOT change parameters without first checking for the
  // same method in a subclass.
  
  /**
   * @param i 
   * @return T/F
   * 
   */
  protected boolean isPolygonDisplayable(int i) {
    return true;
  }

  //isosurface,meshRenderer::render1 (just about everything)
  protected void render2(boolean generateSet) {
    if (!g3d.setColix(colix))
      return;
    if (mesh.showPoints || mesh.polygonCount == 0)
      renderPoints();
    if (mesh.drawTriangles)
      renderTriangles(false, mesh.showTriangles, false);
    if (mesh.fillTriangles)
      renderTriangles(true, mesh.showTriangles, generateSet);
  }
  
  protected void renderPoints() {
    if (mesh.isPolygonSet) {
      int[][] polygonIndexes = mesh.polygonIndexes;
      BitSet bsPoints = new BitSet(mesh.vertexCount);
      if (haveBsDisplay) {
        bsPoints.set(0, mesh.vertexCount);
        bsPoints.andNot(mesh.bsDisplay);
      }
      for (int i = mesh.polygonCount; --i >= 0;) {
        if (!isPolygonDisplayable(i))
          continue;
        int[] p = polygonIndexes[i];
        if (frontOnly && transformedVectors[normixes[i]].z < 0)
          continue;
        for (int j = p.length - 1; --j >= 0;) {
          int pt = p[j];
          if (bsPoints.get(pt))
            continue;
          bsPoints.set(pt);
          g3d.fillSphere(4, screens[pt]);
        }
      }
      return;
    }
    for (int i = vertexCount; --i >= 0;) {
      if (frontOnly && transformedVectors[normixes[i]].z < 0)
        continue;
      g3d.fillSphere(4, screens[i]);
    }
  }

  protected BitSet bsPolygons = new BitSet();
  protected void renderTriangles(boolean fill, boolean iShowTriangles,
                                 boolean generateSet) {
    int[][] polygonIndexes = mesh.polygonIndexes;
    colix = mesh.colix;
    // vertexColixes are only isosurface properties of IsosurfaceMesh, not Mesh
    g3d.setColix(colix);
    if (generateSet) {
      if (frontOnly && fill)
        frontOnly = false;
      bsPolygons.clear();
    }
    for (int i = mesh.polygonCount; --i >= 0;) {
      if (!isPolygonDisplayable(i))
        continue;
      int[] vertexIndexes = polygonIndexes[i];
      int iA = vertexIndexes[0];
      int iB = vertexIndexes[1];
      int iC = vertexIndexes[2];
      if (haveBsDisplay && (!mesh.bsDisplay.get(iA) || !mesh.bsDisplay.get(iB) || !mesh.bsDisplay.get(iC)))
        continue;
      if (iB == iC) {
        // line or point
        drawLine(iA, iB, fill, vertices[iA], vertices[iB], screens[iA],
            screens[iB]);
        continue;
      }
      int check;
      if (mesh.isPolygonSet) {
        short normix = normixes[i];
        if (!g3d.isDirectedTowardsCamera(normix))
          continue;
        if (fill) {
          if (exportType != Graphics3D.EXPORT_NOT) {
            g3d.fillTriangle(screens[iC], colix, normix, screens[iB], colix,
                normix, screens[iA], colix, normix);
          } else if (iShowTriangles) {
            g3d.fillTriangle(screens[iA], colix, normix, screens[iB], colix, normix,
                screens[iC], colix, normix, 0.1f);
          } else {
            g3d.fillTriangle(screens[iA], colix, normix, screens[iB], colix,
                normix, screens[iC], colix, normix);
          }
          continue;
        }
        check = vertexIndexes[3];
        if (iShowTriangles)
          check = 7;
        if ((check & 1) == 1)
          drawLine(iA, iB, true, vertices[iA], vertices[iB], screens[iA],
              screens[iB]);
        if ((check & 2) == 2)
          drawLine(iB, iC, true, vertices[iB], vertices[iC], screens[iB],
              screens[iC]);
        if ((check & 4) == 4)
          drawLine(iA, iC, true, vertices[iA], vertices[iC], screens[iA],
              screens[iC]);
        continue;
      }
      check = 7;
      if (frontOnly) {
        if (transformedVectors[normixes[iA]].z < 0)
          check ^= 1;
        if (transformedVectors[normixes[iB]].z >= 0)
          check ^= 2;
        if (transformedVectors[normixes[iC]].z >= 0)
          check ^= 4;
      }
      if (fill && check != 7)
        continue;
      switch (vertexIndexes.length) {
      case 3:
        if (fill) {
          if (generateSet) {
            bsPolygons.set(i);
            continue;
          }
          if (iShowTriangles) {
            g3d.fillTriangle(screens[iA], colix, normixes[iA], screens[iB],
                colix, normixes[iB], screens[iC], colix, normixes[iC], 0.1f);
            continue;
          }
          // System.out.println(normixes[iA]+ " " + normixes[iB] + " " +
          // normixes[iC]);
          g3d.fillTriangle(screens[iA], colix, normixes[iA], screens[iB],
              colix, normixes[iB], screens[iC], colix, normixes[iC]);
          continue;
        }
        g3d.drawTriangle(screens[iA], screens[iB], screens[iC], check);
        continue;
      case 4:
        int iD = vertexIndexes[3];
        if (fill) {
          if (generateSet) {
            bsPolygons.set(i);
            continue;
          }
          g3d.fillQuadrilateral(screens[iA], colix, normixes[iA], screens[iB],
              colix, normixes[iB], screens[iC], colix, normixes[iC],
              screens[iD], colix, normixes[iD]);
          continue;
        }
        g3d.drawQuadrilateral(colix, screens[iA], screens[iB], screens[iC],
            screens[iD]);
      }
    }
    if (generateSet)
      exportSurface();
  }

  protected void drawLine(int iA, int iB, boolean fill, 
                          Point3f vA, Point3f vB, 
                          Point3i sA, Point3i sB) {
    byte endCap = (iA != iB  && !fill ? Graphics3D.ENDCAPS_NONE 
        : width < 0 || width == -0.0 || iA != iB && isTranslucent ? Graphics3D.ENDCAPS_FLAT
        : Graphics3D.ENDCAPS_SPHERICAL);
    if (width == 0) {
      if (diameter == 0)
        diameter = (mesh.diameter > 0 ? mesh.diameter : iA == iB ? 7 : 3);
      if (exportType == Graphics3D.EXPORT_CARTESIAN) {
        pt1f.set(vA);
        pt1f.add(vB);
        pt1f.scale(1f / 2f);
        viewer.transformPoint(pt1f, pt1i);
        diameter = (int) (viewer.unscaleToScreen(pt1i.z, diameter) * 1000);
      }
      if (iA == iB) {
        g3d.fillSphere(diameter, sA);
      } else {
        g3d.fillCylinder(endCap, diameter, sA, sB);
      }
    } else {
      pt1f.set(vA);
      pt1f.add(vB);
      pt1f.scale(1f / 2f);
      viewer.transformPoint(pt1f, pt1i);
      int mad = (int) (Math.abs(width) * 1000); 
      diameter = (exportType == Graphics3D.EXPORT_CARTESIAN ? mad 
          : viewer.scaleToScreen(pt1i.z, mad));
      if (diameter == 0)
        diameter = 1;
      viewer.transformPoint(vA, pt1f);
      viewer.transformPoint(vB, pt2f);
      g3d.fillCylinderBits(endCap, diameter, pt1f, pt2f);
    }
  }

  protected void exportSurface() {
    mesh.normals = mesh.getNormals(vertices, null);
    mesh.bsPolygons = bsPolygons;
    mesh.offset = latticeOffset;
    g3d.drawSurface(mesh);
    mesh.normals = null;
    mesh.bsPolygons = null;
  }
  
}
