/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-12-03 14:51:57 -0600 (Sun, 03 Dec 2006) $
 * $Revision: 6372 $
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

package org.jmol.shapesurface;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.util.ColorEncoder;
import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.viewer.Viewer;
import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.jvxl.data.JvxlData;

import org.jmol.jvxl.calc.MarchingSquares;
import org.jmol.shape.Mesh;

public class IsosurfaceMesh extends Mesh {
  JvxlData jvxlData = new JvxlData();
  int vertexIncrement = 1;
  int firstRealVertex = -1;
  int dataType;
  boolean hasGridPoints;
  Object calculatedArea;
  Object calculatedVolume;
  public boolean isSolvent;
  
  IsosurfaceMesh(String thisID, Graphics3D g3d, short colix, int index) {
    super(thisID, g3d, colix, index);
    checkByteCount = 2;
    jvxlData.version = Viewer.getJmolVersion();
  }

  void clear(String meshType, boolean iAddGridPoints) {
    super.clear(meshType);  
    nSets = 0;
    thisSet = -1;
    vertexIncrement = 1;
    firstRealVertex = -1;
    hasGridPoints = iAddGridPoints;
    showPoints = iAddGridPoints;
    jvxlData.jvxlSurfaceData = "";
    jvxlData.jvxlEdgeData = "";
    jvxlData.jvxlColorData = "";
    jvxlData.jvxlVolumeDataXml = "";
    isColorSolid = true;
    colorEncoder = null;
    vertexColixes = null;
    vertexValues = null;
    polygonColixes = null;
    jvxlData.contourValues = null;
    jvxlData.contourValuesUsed = null;
    jvxlData.contourColixes = null;
    jvxlData.contourColors = null;
    assocGridPointMap = null;
    assocGridPointNormals = null;
    vertexSets = null;
    centers = null;
    jvxlData.vContours = null;
    jvxlData.colorDensity = false;
    surfaceSet = null;
  }  

  void allocVertexColixes() {
    if (vertexColixes == null) {
      vertexColixes = new short[vertexCount];
      for (int i = vertexCount; --i >= 0; )
        vertexColixes[i] = colix;
    }
    isColorSolid = false;
  }

  Map<Integer, Integer> assocGridPointMap;
  Map<Integer, Vector3f> assocGridPointNormals;

  int addVertexCopy(Point3f vertex, float value, int assocVertex, boolean associateNormals) {
    int vPt = addVertexCopy(vertex, value);
    switch (assocVertex) {
    case MarchingSquares.CONTOUR_POINT:
      if (firstRealVertex < 0)
        firstRealVertex = vPt;
      break;
    case MarchingSquares.VERTEX_POINT:
      hasGridPoints = true;
      break;
    case MarchingSquares.EDGE_POINT:
      vertexIncrement = 3;
      break;
    default:
      if (firstRealVertex < 0)
        firstRealVertex = vPt;
      if (associateNormals) {
        if (assocGridPointMap == null) {
          assocGridPointMap = new Hashtable<Integer, Integer>();
          assocGridPointNormals = new Hashtable<Integer, Vector3f>();
        }
        Integer key = Integer.valueOf(assocVertex);
        assocGridPointMap.put(Integer.valueOf(vPt), key);
        if (!assocGridPointNormals.containsKey(key))
          assocGridPointNormals.put(key, new Vector3f(0, 0, 0));
      }
    }
    return vPt;
  }

  @Override
  public void setTranslucent(boolean isTranslucent, float iLevel) {
    super.setTranslucent(isTranslucent, iLevel);
    if (vertexColixes != null)
      for (int i = vertexCount; --i >= 0; )
        vertexColixes[i] =
          Graphics3D.getColixTranslucent(vertexColixes[i], isTranslucent, iLevel);
  }
  
  int thisSet = -1;
  
  @Override
  protected void sumVertexNormals(Point3f[] vertices, Vector3f[] vectorSums) {
    super.sumVertexNormals(vertices, vectorSums);
    /* 
     * OK, so if there is an associated grid point (because the 
     * point is so close to one), we now declare that associated
     * point to be used for the vecetorSum instead of a new, 
     * independent one for the point itself.
     *  
     *  Bob Hanson, 05/2006
     *  
     *  having 2-sided normixes is INCOMPATIBLE with this when not a plane 
     *  
     */
    if (assocGridPointMap != null) {
      for (Map.Entry<Integer, Integer> entry : assocGridPointMap.entrySet()) {
        Integer I = entry.getKey();
        assocGridPointNormals.get(entry.getValue()).add(vectorSums[I.intValue()]);
      }
      for (Map.Entry<Integer, Integer> entry : assocGridPointMap.entrySet()) {
        Integer I = entry.getKey();
        vectorSums[I.intValue()] = assocGridPointNormals.get(entry.getValue());
      }
    }
  }
  
  Point3f[] centers;
  Point3f[] getCenters() {
    if (centers != null)
      return centers;
    centers = new Point3f[polygonCount];
    for (int i = 0; i < polygonCount; i++) {
      int[] pi = polygonIndexes[i];
      if (pi == null)
        continue;
      Point3f pt = centers[i] = new Point3f();
      pt.add(vertices[pi[0]]);
      pt.add(vertices[pi[1]]);
      pt.add(vertices[pi[2]]);
      pt.scale(1/3f);
    }
    return centers;
  }
  
  Point4f getFacePlane(int i, Vector3f vNorm) {
    Point4f plane = new Point4f();
    Measure.getPlaneThroughPoints(vertices[polygonIndexes[i][0]], 
        vertices[polygonIndexes[i][1]], vertices[polygonIndexes[i][2]], 
        vNorm, vAB, vAC, plane);
    return plane;
  }
  
  /**
   * create a set of contour data. 
   * 
   * Each contour is a Vector containing:
   *   0 Integer number of polygons (length of BitSet) 
   *   1 BitSet of critical triangles
   *   2 Float value
   *   3 int[] [colorArgb]
   *   4 StringBuffer containing encoded data for each segment:
   *     char type ('3', '6', '5') indicating which two edges
   *       of the triangle are connected: 
   *         '3' 0x011 AB-BC
   *         '5' 0x101 AB-CA
   *         '6' 0x110 BC-CA
   *     char fraction along first edge (jvxlFractionToCharacter)
   *     char fraction along second edge (jvxlFractionToCharacter)
   *   5- stream of pairs of points for rendering
   * 
   * @return contour vector set
   */
  @SuppressWarnings("unchecked")
  List<Object>[] getContours() {
    int n = jvxlData.nContours;
    if (n == 0 || polygonIndexes == null)
      return null;
    havePlanarContours = (jvxlData.jvxlPlane != null);
    if (havePlanarContours)
      return null; // not necessary; 
    if (n < 0)
      n = -1 - n;
    List<Object>[] vContours = jvxlData.vContours;
    if (vContours != null) {
      for (int i = 0; i < n; i++) {
        if (vContours[i].size() > JvxlCoder.CONTOUR_POINTS)
          return jvxlData.vContours;
        JvxlCoder.set3dContourVector(vContours[i], polygonIndexes, vertices);
      }
      //dumpData();
      return jvxlData.vContours;
    }
    //dumpData();
    vContours = new List[n];
    for (int i = 0; i < n; i++) {
      vContours[i] = new ArrayList<Object>();
    }
    if (jvxlData.contourValuesUsed == null) {
      float dv = (jvxlData.valueMappedToBlue - jvxlData.valueMappedToRed)
          / (n + 1);
      // n + 1 because we want n lines between n + 1 slices
      for (int i = 0; i < n; i++) {
        float value = jvxlData.valueMappedToRed + (i + 1) * dv;
        get3dContour(vContours[i], value, jvxlData.contourColixes[i]);
      }
      Logger.info(n + " contour lines; separation = " + dv);
    } else {
      for (int i = 0; i < n; i++) {
        float value = jvxlData.contourValuesUsed[i];
        get3dContour(vContours[i], value, jvxlData.contourColixes[i]);
      }      
    }
    jvxlData.contourColixes = new short[n];
    jvxlData.contourValues = new float[n];
    for (int i = 0; i < n; i++) {
      jvxlData.contourValues[i] = ((Float) vContours[i].get(2)).floatValue();
      jvxlData.contourColixes[i] = ((short[]) vContours[i].get(3))[0];
    }
    return jvxlData.vContours = vContours;
  }
  
  private void get3dContour(List<Object> v, float value, short colix) {
    BitSet bsContour = new BitSet(polygonCount);
    StringBuffer fData = new StringBuffer();
    int color = Graphics3D.getArgb(colix);
    setContourVector(v, polygonCount, bsContour, value, colix, color, fData);
    for (int i = 0; i < polygonCount; i++)
      if (setABC(i))
        addContourPoints(v, bsContour, i, fData, vertices,
            vertexValues, iA, iB, iC, value);
  }

  public static void setContourVector(List<Object> v, int nPolygons,
                                      BitSet bsContour, float value, short colix,
                                      int color, StringBuffer fData) {
    v.add(JvxlCoder.CONTOUR_NPOLYGONS, Integer.valueOf(nPolygons));
    v.add(JvxlCoder.CONTOUR_BITSET, bsContour);
    v.add(JvxlCoder.CONTOUR_VALUE, new Float(value));
    v.add(JvxlCoder.CONTOUR_COLIX, new short[] { colix });
    v.add(JvxlCoder.CONTOUR_COLOR, new int[] { color });
    v.add(JvxlCoder.CONTOUR_FDATA, fData);
  }

  public static void addContourPoints(List<Object> v, BitSet bsContour, int i,
                                      StringBuffer fData, Point3f[] vertices,
                                      float[] vertexValues, int iA, int iB,
                                      int iC, float value) {
    Point3f pt1 = null;
    Point3f pt2 = null;
    int type = 0;
    // check AB
    float f1 = checkPt(vertexValues, iA, iB, value);
    if (!Float.isNaN(f1)) {
      pt1 = getContourPoint(vertices, iA, iB, f1);
      type |= 1;
    }
    // check BC only if v not found only at B already in testing AB
    float f2 = (f1 == 1 ? Float.NaN : checkPt(vertexValues, iB, iC, value));
    if (!Float.isNaN(f2)) {
      pt2 = getContourPoint(vertices, iB, iC, f2);
      if (type == 0) {
        pt1 = pt2;
        f1 = f2;
      }
      type |= 2;
    }
    // only check CA under certain circumstances
    switch (type) {
    case 0:
      return; // not in AB or BC, so ignore
    case 1:
      if (f1 == 0)
        return; //because at A and not along BC, so only at A
      // fall through
    case 2:
      // check CA only if v not found only at C already in testing BC
      f2 = (f2 == 1 ? Float.NaN : checkPt(vertexValues, iC, iA, value));
      if (!Float.isNaN(f2)) {
        pt2 = getContourPoint(vertices, iC, iA, f2);
        type |= 4;
      }
      break;
    }
    // only types AB-BC, AB-CA, or BC-CA are valid intersections
    switch (type) {
    case 3:
    case 5:
    case 6:
      break;
    default:
      return;
    }
    bsContour.set(i);
    JvxlCoder.appendContourTriangleIntersection(type, f1, f2, fData);
    v.add(pt1);
    v.add(pt2);
  }

  /**
   *  two values -- v1, and v2, which need not be ordered v1 < v2.
   *  v == v1 --> 0
   *  v == v2 --> 1
   *  v1 < v < v2 --> f in (0,1)
   *  v2 < v < v1 --> f in (0,1) 
   *  i.e. (v1 < v) == (v < v2)
   *
   *  We check AB, then (usually) BC, then (sometimes) CA.
   *  
   *  What if two end points are identical values?
   *  So, for example, if v = 1.0 and:
   *  
   *      A             1.0         0.5        1.0         1.0      
   *     / \            /  \        /  \       /  \        /  \
   *    /   \          /    \      /    \     /    \      /    \
   *   C-----B        1.0--0.5    1.0--1.0   0.5--1.0   1.0---1.0
   *                   case I      case II   case III    case IV
   *                   
   *     case I: AB[0] and BC[1], type == 3 --> CA not tested.
   *    case II: AB[1] and CA[0]; f1 == 1.0 --> BC not tested.
   *   case III: AB[0] and BC[0], type == 3 --> CA not tested.
   *   case  IV: AB[0] and BC[0], type == 3 --> CA not tested.
   *  
   *  what if v = 0.5?
   *  
   *     case I: AB[1]; BC not tested --> type == 1, invalid.
   *    case II: AB[0]; type == 1, f1 == 0.0 --> CA not tested.
   *   case III: BC[1]; f2 == 1.0 --> CA not tested.
   *   
   * @param vertexValues
   * @param i
   * @param j
   * @param v
   * @return fraction along the edge or NaN
   */
  private static float checkPt(float[] vertexValues, int i, int j, float v) {
    float v1, v2;
    return (v == (v1 = vertexValues[i]) ? 0 
        : v == (v2 = vertexValues[j]) ? 1 
        : (v1 < v) == (v < v2) ? (v - v1) / (v2 - v1) 
        : Float.NaN);
  }

  private static Point3f getContourPoint(Point3f[] vertices, int i, int j, float f) {
    Point3f pt = new Point3f();
    pt.set(vertices[j]);
    pt.sub(vertices[i]);
    pt.scale(f);
    pt.add(vertices[i]);
    return pt;
  }

  float[] contourValues;
  short[] contourColixes;
  ColorEncoder colorEncoder;
  
  public void setDiscreteColixes(float[] values, short[] colixes) {
    if (values != null)
      jvxlData.contourValues = values;
    if (values == null || values.length == 0)
      values = jvxlData.contourValues = jvxlData.contourValuesUsed;
    if (colixes == null && jvxlData.contourColixes != null) {
      colixes = jvxlData.contourColixes;
    } else {
      jvxlData.contourColixes = colixes;
      jvxlData.contourColors = Graphics3D.getHexCodes(colixes);
    }
    if (vertices == null || vertexValues == null || values == null)
      return;
    int n = values.length;
    float vMax = values[n - 1];
    colorCommand = null;
    boolean haveColixes = (colixes != null && colixes.length > 0);
    isColorSolid = haveColixes && jvxlData.jvxlPlane != null;
    if (jvxlData.vContours != null) {
      if (haveColixes)
        for (int i = 0; i < jvxlData.vContours.length; i++) {
          short colix  = colixes[i % colixes.length];
          ((short[]) jvxlData.vContours[i].get(JvxlCoder.CONTOUR_COLIX))[0] = colix;
          ((int[]) jvxlData.vContours[i].get(JvxlCoder.CONTOUR_COLOR))[0] = Graphics3D.getArgb(colix);
        }
      return;
    }
    short defaultColix = 0;
    polygonColixes = new short[polygonCount];
    for (int i = 0; i < polygonCount; i++) {
      int[] pi = polygonIndexes[i];
      if (pi == null)
        continue;
      polygonColixes[i] = defaultColix;
      float v = (vertexValues[pi[0]] + vertexValues[pi[1]] + vertexValues[pi[2]])/3;
      //System.out.println(i + " " + v);
      for (int j = n; --j >= 0;) {
        if (v >= values[j] && v < vMax) {
          polygonColixes[i] = (haveColixes ? colixes[j % colixes.length] : 0);
          //System.out.println(i + " " + v + " " + j + " " + colixes.length + " " + colixes[j % colixes.length]);
          break;
        }
      }
    }
  }

  /**
   * 
   * @param viewer
   * @return a Hashtable containing "values" and "colors"
   * 
   */
  Map<String, Object> getContourList(Viewer viewer) {
    Map<String, Object> ht = new Hashtable<String, Object>();
    ht.put("values", (jvxlData.contourValuesUsed == null ? jvxlData.contourValues : jvxlData.contourValuesUsed));
    List<Point3f> colors = new ArrayList<Point3f>();
    if (jvxlData.contourColixes != null) {
      // set in SurfaceReader.colorData()
      for (int i = 0; i < jvxlData.contourColixes.length; i++) {
        colors.add(Graphics3D.colorPointFromInt2(Graphics3D.getArgb(jvxlData.contourColixes[i])));
      }
      ht.put("colors", colors);
    }
    return ht;
  }

  //private void dumpData() {
    //for (int i =0;i<10;i++) {
    //  System.out.println("P["+i+"]="+polygonIndexes[i][0]+" "+polygonIndexes[i][1]+" "+polygonIndexes[i][2]+" "+ polygonIndexes[i][3]+" "+vertices[i]);
    //}
  //}
}
