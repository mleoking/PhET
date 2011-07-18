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
package org.jmol.shapesurface;

import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.jvxl.readers.Parameters;
import org.jmol.shape.MeshRenderer;

public class IsosurfaceRenderer extends MeshRenderer {

  private boolean iShowNormals;
  protected boolean iHideBackground;
  protected boolean isBicolorMap;
  protected short backgroundColix;
  protected int nError = 0;
  protected float[] vertexValues;
  protected IsosurfaceMesh imesh;

  @Override
  protected void initRenderer() {
    super.initRenderer();
  }
  @Override
  protected void render() {
    iShowNormals = viewer.getTestFlag4();
    Isosurface isosurface = (Isosurface) shape;
    int slabValue = (viewer.getNavigationMode() ? g3d.getSlab() : Integer.MAX_VALUE);
    for (int i = isosurface.meshCount; --i >= 0;) {
      imesh = (IsosurfaceMesh) isosurface.meshes[i];
      g3d.setTranslucentCoverOnly(imesh.frontOnly);
      thePlane = imesh.jvxlData.jvxlPlane;
      vertexValues = imesh.vertexValues;
      if (slabValue != Integer.MAX_VALUE && imesh.isSolvent) {
        g3d.setSlab((int) viewer.getNavigationOffset().z);
        render1(imesh);
        g3d.setSlab(slabValue);
      } else {
        render1(imesh);
      }
      g3d.setTranslucentCoverOnly(false);
    }
  }

  @Override
  protected void render2(boolean isExport) {
    if (imesh.jvxlData.colorDensity) {
      renderPoints();
      return;
    }
    switch (imesh.dataType) {
    case Parameters.SURFACE_LONEPAIR:
      renderLonePair(false);
      return;
    case Parameters.SURFACE_RADICAL:
      renderLonePair(true);
      return;
    }
    isBicolorMap = imesh.jvxlData.isBicolorMap;
    super.render2(isExport);
    if (!g3d.setColix(Graphics3D.BLACK)) // must be 1st pass
      return;
    if (imesh.showContourLines)
      renderContourLines();
  }
  
  private void renderLonePair(boolean isRadical) {
    pt2f.set(imesh.vertices[1]);
    viewer.transformPoint(pt2f, pt2f);
    int r = viewer.scaleToScreen((int)pt2f.z, 100);
    if (r < 1)
      r = 1;
    if (!isRadical) {
      Vector3f v1 = new Vector3f();
      Vector3f v2 = new Vector3f();
      pt1f.set(imesh.vertices[0]);
      viewer.transformPoint(pt1f, pt1f);
      v1.sub(pt2f, pt1f);
      v2.set(v1.x, v1.y, v1.z + 1);
      v2.cross(v2,v1);
      v2.normalize();
      float f = viewer.scaleToScreen((int)pt1f.z, 100);
      v2.scale(f);
      pt1f.set(pt2f);
      pt1f.add(v2);
      pt2f.sub(v2);
      screens[0].set((int)pt1f.x,(int)pt1f.y,(int)pt1f.z);
      g3d.fillSphere(r, screens[0]);
    }
    screens[1].set((int)pt2f.x,(int)pt2f.y,(int)pt2f.z);
    g3d.fillSphere(r, screens[1]);
  }
  
  private void renderContourLines() {
    // no check here for within distance
    List<Object>[] vContours = imesh.getContours();
    if (vContours == null)
      return;
    for (int i = vContours.length; --i >= 0;) {
      List<Object> v = vContours[i];
      if (v.size() < JvxlCoder.CONTOUR_POINTS)
        continue;
      colix = (imesh.meshColix == 0 ? ((short[]) v.get(JvxlCoder.CONTOUR_COLIX))[0]
          : imesh.meshColix);
      if (!g3d.setColix(colix))
        return;
      int n = v.size() - 1;
      for (int j = JvxlCoder.CONTOUR_POINTS; j < n; j++) {
        Point3f pt1 = (Point3f) v.get(j);
        Point3f pt2 = (Point3f) v.get(++j);
        viewer.transformPoint(pt1, pt1i);
        viewer.transformPoint(pt2, pt2i);
        if (Float.isNaN(pt1.x) || Float.isNaN(pt2.x))
          break;
        pt1i.z -= 2;
        pt2i.z -= 2;
        g3d.drawLine(pt1i, pt2i);
      }
    }
  }
  
  private final Point3f ptTemp = new Point3f();
  private final Point3i ptTempi = new Point3i();

  @Override
  protected void renderPoints() {
    int incr = imesh.vertexIncrement;
    int diam = viewer.getScreenDim() / 100;
    boolean showNumbers = viewer.getTestFlag3();
    int cX = (showNumbers ? viewer.getScreenWidth() / 2 : 0);
    int cY = (showNumbers ? viewer.getScreenHeight() / 2 : 0);
    if (showNumbers)
      g3d.setFont(g3d.getFontFid("Monospaced", 24));
    for (int i = (!imesh.hasGridPoints || imesh.firstRealVertex < 0 ? 0
        : imesh.firstRealVertex); i < vertexCount; i += incr) {
      if (vertexValues != null && Float.isNaN(vertexValues[i]) || frontOnly
          && transformedVectors[normixes[i]].z < 0
          || imesh.thisSet >= 0 && imesh.vertexSets[i] != imesh.thisSet
          || imesh.vertexColixes != null && !g3d.setColix(imesh.vertexColixes[i])
          || haveBsDisplay && !imesh.bsDisplay.get(i))
        continue;
      if (showNumbers && screens[i].z > 10 && Math.abs(screens[i].x - cX) < 50
          && Math.abs(screens[i].y - cY) < 50) {
        String s = i + (imesh.isColorSolid ? "" : " " + imesh.vertexValues[i]);
        //System.out.println("IsoSurfaceRenderer i=" + s + " " 
        //  + imesh.vertices[i] + " " + imesh.vertexValues[i]);
        g3d.drawStringNoSlab(s, null, screens[i].x, screens[i].y, screens[i].z);
      }
      g3d.fillSphere(diam, screens[i]);
    }
    if (incr != 3)
      return;
    g3d.setColix(isTranslucent ? Graphics3D.getColixTranslucent(
        Graphics3D.GRAY, true, 0.5f) : Graphics3D.GRAY);
    for (int i = 1; i < vertexCount; i += 3)
      g3d.fillCylinder(Graphics3D.ENDCAPS_SPHERICAL, diam / 4, screens[i],
          screens[i + 1]);

    g3d.setColix(isTranslucent ? Graphics3D.getColixTranslucent(
        Graphics3D.YELLOW, true, 0.5f) : Graphics3D.YELLOW);
    for (int i = 1; i < vertexCount; i += 3)
      g3d.fillSphere(diam, screens[i]);

    g3d.setColix(isTranslucent ? Graphics3D.getColixTranslucent(
        Graphics3D.BLUE, true, 0.5f) : Graphics3D.BLUE);
    for (int i = 2; i < vertexCount; i += 3) {
      g3d.fillSphere(diam, screens[i]);
    }
  }

  @Override
  protected void renderTriangles(boolean fill, boolean iShowTriangles,
                                 boolean isExport) {
    int[][] polygonIndexes = imesh.polygonIndexes;
    colix = (!fill && imesh.meshColix != 0 ? imesh.meshColix : imesh.colix);
    short[] vertexColixes = (!fill && imesh.meshColix != 0 ? null : imesh.vertexColixes);
    g3d.setColix(colix);
    boolean generateSet = isExport;
    if (generateSet) {
      if (frontOnly && fill)
        frontOnly = false;
      bsPolygons.clear();
    }
    if (exportType == Graphics3D.EXPORT_CARTESIAN) {
      frontOnly = false;
    }
    boolean colorSolid = (vertexColixes == null || imesh.isColorSolid);
    //System.out.println("isosurfacerenderer imesh.isColorSolid = " + imesh.isColorSolid);
    boolean noColor = (vertexColixes == null || !fill && imesh.meshColix != 0);
    boolean isPlane = (imesh.jvxlData.jvxlPlane != null);
    short colix = this.colix;
    if (isPlane && !colorSolid && !fill && imesh.fillTriangles) {
      colorSolid = true;
      colix = Graphics3D.BLACK;
    }
/*  only an idea -- causes flickering
    if (isPlane && colorSolid) {
      g3d.setNoisySurfaceShade(screens[polygonIndexes[0][0]], 
          screens[polygonIndexes[imesh.polygonCount / 2][1]], screens[polygonIndexes[imesh.polygonCount - 1][2]]);
    }
*/
    boolean colorArrayed = (colorSolid && imesh.polygonColixes != null);
    if (colorArrayed && !fill && imesh.fillTriangles)
      colorArrayed = false;
    short[] contourColixes = imesh.jvxlData.contourColixes;
    // two-sided means like a plane, with no front/back distinction
    
    for (int i = imesh.polygonCount; --i >= 0;) {
      int[] vertexIndexes = polygonIndexes[i];
      if (vertexIndexes == null)
        continue;
      int iA = vertexIndexes[0];
      int iB = vertexIndexes[1];
      int iC = vertexIndexes[2];
      if (imesh.thisSet >= 0 && imesh.vertexSets[iA] != imesh.thisSet)
        continue;
      if (haveBsDisplay && (!imesh.bsDisplay.get(iA) || !imesh.bsDisplay.get(iB) || !imesh.bsDisplay.get(iC)))
          continue;
      short nA = normixes[iA];
      short nB = normixes[iB];
      short nC = normixes[iC];
      int check = 7;
      if (frontOnly) {
        if (transformedVectors[nA].z < 0)
          check ^= 1;
        if (transformedVectors[nB].z < 0)
          check ^= 2;
        if (transformedVectors[nC].z < 0)
          check ^= 4;
      }
      if (fill && check == 0)
        continue;
      short colixA, colixB, colixC;
      if (colorSolid) {
        if (colorArrayed && i < imesh.polygonColixes.length) {
          short c = imesh.polygonColixes[i];
          if (c == 0)
            continue;
          colix = c;
        }
        colixA = colixB = colixC = colix;
      } else {
        colixA = vertexColixes[iA];
        colixB = vertexColixes[iB];
        colixC = vertexColixes[iC];
        if (isBicolorMap && (colixA != colixB || colixB != colixC))
          continue;
      }
      if (fill) {
        if (generateSet) {
          bsPolygons.set(i);
          continue;
        }
        if (iShowTriangles) {
          g3d.fillTriangle(screens[iA], colixA, nA, screens[iB], colixB, nB,
              screens[iC], colixC, nC, 0.1f);
        } else {
            g3d.fillTriangle(screens[iA], colixA, nA, screens[iB], colixB, nB,
                screens[iC], colixC, nC);
        }
        if (iShowNormals)
          renderNormals();
      } else {
        // mesh only
        // check: 1 (ab) | 2(bc) | 4(ac)
        check &= vertexIndexes[3];
        if (iShowTriangles)
          check = 7;
        if (check == 0)
          continue;
        pt1i.set(screens[iA]);
        pt2i.set(screens[iB]);
        pt3i.set(screens[iC]);
        pt1i.z -= 2;
        pt2i.z -= 2;
        pt3i.z -= 2;
        if (noColor) {
        } else if (colorArrayed) {
          g3d.setColix(mesh.fillTriangles ? Graphics3D.BLACK
              : contourColixes[vertexIndexes[4] % contourColixes.length]);
        } else {
          g3d.drawTriangle(pt1i, colixA, pt2i, colixB, pt3i, colixC, check);
          continue;
        }
        g3d.drawTriangle(pt1i, pt2i, pt3i, check);
      }
    }
    if (generateSet)
      exportSurface();
  }

  private void renderNormals() {
    // Logger.debug("mesh renderPoints: " + vertexCount);
    if (!g3d.setColix(Graphics3D.WHITE))
      return;
    g3d.setFont(g3d.getFontFid("Monospaced", 24));
    for (int i = vertexCount; --i >= 0;) {
      if (vertexValues != null && Float.isNaN(vertexValues[i])) 
        continue;
        ptTemp.set(mesh.vertices[i]);
        short n = mesh.normixes[i];
        // -n is an intensity2sided and does not correspond to a true normal
        // index
        if (n >= 0) {
          ptTemp.add(Graphics3D.getNormixVector(n));
          viewer.transformPoint(ptTemp, ptTempi);
          g3d.drawLine(screens[i], ptTempi);
          //g3d.drawStringNoSlab("" + n, null, ptTempi.x, ptTempi.y, ptTempi.z);
        }
    }
  }

}
