/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-04-24 20:49:07 -0500 (Tue, 24 Apr 2007) $
 * $Revision: 7483 $
 *
 * Copyright (C) 2005  Miguel, The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net, jmol-developers@lists.sf.net
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
import java.util.List;

import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;
import org.jmol.util.Measure;
import org.jmol.util.MeshSurface;
import org.jmol.util.Quaternion;
import org.jmol.viewer.JmolConstants;
import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.*;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

public class Mesh extends MeshSurface {
  
  public final static String PREVIOUS_MESH_ID = "+PREVIOUS_MESH+";

  public String[] title;
  
  public short meshColix;
  public short[] normixes;
  private int normixCount;
  public BitSet[] bitsets; // [0]bsSelected [1]bsIgnore [2]bsTrajectory
  public List<Point3f[]> lineData;
  public String thisID;
  public boolean isValid = true;
  public String scriptCommand;
  public String colorCommand;
  public Point3f lattice;
  public boolean visible = true;
  public int lighting = JmolConstants.FRONTLIT;
  public Quaternion q;
  

  public float scale = 1;
  public boolean haveXyPoints;
  public boolean isPolygonSet; // just a set of flat polygons
  public int diameter;
  public float width;
  public Point3f ptCenter = new Point3f(0,0,0);
  public String meshType;
  public Mesh linkedMesh; //for lcaoOrbitals

  public int index;
  public int atomIndex = -1;
  public int modelIndex = -1;  // for Isosurface and Draw
  public int visibilityFlags;
  public boolean insideOut;
  public int checkByteCount;

  public void setVisibilityFlags(int n) {
    visibilityFlags = n;//set to 1 in mps
  }

  public BitSet bsDisplay;

  public boolean showContourLines = false;
  public boolean showPoints = false;
  public boolean drawTriangles = false;
  public boolean fillTriangles = true;
  public boolean showTriangles = false; //as distinct entitities
  public boolean frontOnly = false;
  public boolean isTwoSided = true;
  public boolean havePlanarContours = false;
  
  /**
   * 
   * @param thisID
   * @param g3d     IGNORED
   * @param colix
   * @param index
   */
  public Mesh(String thisID, JmolRendererInterface g3d, short colix, int index) {
    if (PREVIOUS_MESH_ID.equals(thisID))
      thisID = null;
    this.thisID = thisID;
    this.colix = colix;
    this.index = index;
    //System.out.println("Mesh " + this + " constructed");
  }

  //public void finalize() {
  //  System.out.println("Mesh " + this + " finalized");
  //}
  

  public void clear(String meshType) {
    bsDisplay = null;
    vertexCount = polygonCount = 0;
    scale = 1;
    havePlanarContours = false;
    haveXyPoints = false;
    showPoints = false;
    showContourLines = false;
    colorDensity = false;
    drawTriangles = false;
    fillTriangles = true;
    showTriangles = false; //as distinct entitities
    isPolygonSet = false;
    frontOnly = false;
    title = null;
    normixes = null;
    bitsets = null;    
    vertices = null;
    altVertices = null;
    polygonIndexes = null;
    //data1 = null;
    //data2 = null;
    slabbingObject = null;
    cappingObject = null;
    q = null;
    
    this.meshType = meshType;
  }

  public void initialize(int lighting, Point3f[] vertices, Point4f plane) {
    if (vertices == null)
      vertices = this.vertices;
    Vector3f[] normals = getNormals(vertices, plane);
    normixes = new short[normixCount];
    isTwoSided = (lighting == JmolConstants.FULLYLIT);
    BitSet bsTemp = new BitSet();
    if (haveXyPoints)
      for (int i = normixCount; --i >= 0;)
        normixes[i] = Graphics3D.NORMIX_NULL;
    else
      for (int i = normixCount; --i >= 0;)
        normixes[i] = Graphics3D.getNormix(normals[i], bsTemp);
    this.lighting = JmolConstants.FRONTLIT;
    if (insideOut)
      invertNormixes();
    setLighting(lighting);
  }

  public Vector3f[] getNormals(Point3f[] vertices, Point4f plane) {
    normixCount = (isPolygonSet ? polygonCount : vertexCount);
    Vector3f[] normals = new Vector3f[normixCount];
    for (int i = normixCount; --i >= 0;)
      normals[i] = new Vector3f();
    if (plane == null) {
      sumVertexNormals(vertices, normals);
    }else {
      Vector3f normal = new Vector3f(plane.x, plane.y, plane.z); 
      for (int i = normixCount; --i >= 0;)
        normals[i] = normal;
    }
    if (!isPolygonSet)
      for (int i = normixCount; --i >= 0;)
        normals[i].normalize();
    return normals;
  }
  
  public void setLighting(int lighting) {
    if (lighting == this.lighting)
      return;
    flipLighting(this.lighting);
    flipLighting(this.lighting = lighting);
  }
  
  private void flipLighting(int lighting) {
    if (lighting == JmolConstants.FULLYLIT)
      for (int i = normixCount; --i >= 0;)
        normixes[i] = (short)~normixes[i];
    else if ((lighting == JmolConstants.FRONTLIT) == insideOut)
      invertNormixes();
  }

  private void invertNormixes() {
    for (int i = normixCount; --i >= 0;)
      normixes[i] = Graphics3D.getInverseNormix(normixes[i]);
  }

  public void setTranslucent(boolean isTranslucent, float iLevel) {
    colix = Graphics3D.getColixTranslucent(colix, isTranslucent, iLevel);
  }

  public final Vector3f vAB = new Vector3f();
  public final Vector3f vAC = new Vector3f();
  public final Vector3f vTemp = new Vector3f();

  //public Vector data1;
  //public Vector data2;
  public List<Object> xmlProperties;
  public boolean colorDensity;
  public Object cappingObject;
  public Object slabbingObject;
  
  
  protected void sumVertexNormals(Point3f[] vertices, Vector3f[] normals) {
    // subclassed in IsosurfaceMesh
    int adjustment = checkByteCount;
    for (int i = polygonCount; --i >= 0;) {
      int[] pi = polygonIndexes[i];
      if (pi == null)
        continue;
      try {
        Point3f vA = vertices[pi[0]];
        Point3f vB = vertices[pi[1]];
        Point3f vC = vertices[pi[2]];
        if (vA.distanceSquared(vB) < 0.0001 || vB.distanceSquared(vC) < 0.0001
            || vA.distanceSquared(vC) < 0.0001)
          continue;
        Measure.calcNormalizedNormal(vA, vB, vC, vTemp, vAB, vAC);
        if (isPolygonSet) {
          normals[i].set(vTemp);
          continue;
        }
        float l = vTemp.length();
        if (l > 0.9 && l < 1.1) // test for not infinity or -infinity or isNaN
          for (int j = pi.length - adjustment; --j >= 0;) {
            int k = pi[j];
            normals[k].add(vTemp);
          }
      } catch (Exception e) {
      }
    }
  }

  public String getState(String type) {
    String sxml = null; // problem here is that it can be WAY to large. Shape.getXmlPropertyString(xmlProperties, type);
    StringBuffer s = new StringBuffer();
    if (sxml != null)
      s.append("/** XML ** ").append(sxml).append(" ** XML **/\n");
    s.append(type);
    if (!type.equals("mo"))
      s.append(" ID ").append(Escape.escape(thisID));
    if (lattice != null)
      s.append(" lattice ").append(Escape.escape(lattice));
    if (meshColix != 0)
      s.append(" color mesh ").append(Graphics3D.getHexCode(meshColix));
    s.append(fillTriangles ? " fill" : " noFill");
    s.append(drawTriangles ? " mesh" : " noMesh");
    s.append(showPoints ? " dots" : " noDots");
    s.append(frontOnly ? " frontOnly" : " notFrontOnly");
    if (showContourLines)
      s.append(" contourlines");
    if (showTriangles)
      s.append(" triangles");
    s.append(lighting == JmolConstants.BACKLIT ? " backlit"
        : lighting == JmolConstants.FULLYLIT ? " fullylit" : " frontlit");
    if (!visible)
      s.append(" hidden");
    if (bsDisplay != null) {
      s.append(";\n  ").append(type);
      if (!type.equals("mo"))
        s.append(" ID ").append(Escape.escape(thisID));
      s.append(" display " + Escape.escape(bsDisplay));
    }
    return s.toString();
  }

  public Point3f[] getOffsetVertices(Point4f thePlane) {
    if (altVertices != null)
      return (Point3f[]) altVertices;
    altVertices = new Point3f[vertexCount];
    for (int i = 0; i < vertexCount; i++)
      altVertices[i] = new Point3f(vertices[i]);
    Vector3f normal = null;
    float val = 0;
    if (scale3d != 0 && vertexValues != null && thePlane != null) {
        normal = new Vector3f(thePlane.x, thePlane.y, thePlane.z);
        normal.normalize();
        normal.scale(scale3d);
        if (q != null)
          normal = q.transform(normal);
    }
    for (int i = 0; i < vertexCount; i++) {
      if (vertexValues != null && Float.isNaN(val = vertexValues[i]))
        continue;
      if (q != null)
        altVertices[i] = q.transform((Point3f) altVertices[i]);
      Point3f pt = (Point3f) altVertices[i];
      if (ptOffset != null)
        pt.add(ptOffset);
      if (normal != null && val != 0)
        pt.scaleAdd(val, normal, pt);
    }
    
    initialize(lighting, (Point3f[]) altVertices, null);
    return (Point3f[]) altVertices;
  }

  /**
   * 
   * @param showWithinPoints
   * @param showWithinDistance2
   * @param isWithinNot
   */
  public void setShowWithin(List<Point3f> showWithinPoints,
                            float showWithinDistance2, boolean isWithinNot) {
    if (showWithinPoints.size() == 0) {
      bsDisplay = (isWithinNot ? BitSetUtil.newBitSet(0, vertexCount) : null);
      return;
    }
    bsDisplay = new BitSet();
    for (int i = 0; i < vertexCount; i++)
      if (checkWithin(vertices[i], showWithinPoints, showWithinDistance2, isWithinNot))
        bsDisplay.set(i);
  }

  public static boolean checkWithin(Point3f pti, List<Point3f> withinPoints,
                                    float withinDistance2, boolean isWithinNot) {
    if (withinPoints.size() == 0)
      return isWithinNot;
    if (isWithinNot) {
      for (int i = withinPoints.size(); --i >= 0;)
        if (pti.distanceSquared(withinPoints.get(i)) > withinDistance2)
          return true;
    } else {
      for (int i = withinPoints.size(); --i >= 0;)
        if (pti.distanceSquared(withinPoints.get(i)) <= withinDistance2)
          return true;
    }
    return false;
  }

  public int getVertexIndexFromNumber(int vertexIndex) {
    if (--vertexIndex < 0)
      vertexIndex = vertexCount + vertexIndex;
    return (vertexCount <= vertexIndex ? vertexCount - 1
        : vertexIndex < 0 ? 0 : vertexIndex);
  }

}
