/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-10-14 12:33:20 -0500 (Sun, 14 Oct 2007) $
 * $Revision: 8408 $

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

package org.jmol.modelset;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.util.Measure;
import org.jmol.util.Point3fi;
import org.jmol.util.TriangleData;

public class BoxInfo {

 
  private final Point3f bbCorner0 = new Point3f(-10, -10, -10);
  private final Point3f bbCorner1 = new Point3f(10, 10, 10);
  private final Point3f bbCenter = new Point3f();
  private final Vector3f bbVector = new Vector3f(-1, -1, -1);
  private final Point3fi[] bbVertices = new Point3fi[8];

  {
    for (int i = 8; --i >= 0;)
      bbVertices[i] = new Point3fi();
  }

  public static char[] bbcageTickEdges = {
    'z', 0, 0, 'y', 
    'x', 0, 0, 0, 
      0, 0, 0, 0};
  
  public static char[] uccageTickEdges = {
    'z', 'y', 'x', 0, 
     0, 0, 0, 0, 
     0, 0, 0, 0};
  
  public final static byte edges[] = {
      0,1, 0,2, 0,4, 1,3, 
      1,5, 2,3, 2,6, 3,7, 
      4,5, 4,6, 5,7, 6,7
      };

  /**
   * returns a set of points defining the geometric object within the given
   * plane that spans the unit cell within the given margins
   * @param plane 
   * @param scale 
   * @param flags
   *          0 -- polygon int[]  1 -- edges only 2 -- triangles only 3 -- both
   * @return    a set of points
   * 
   */
  public List<Object> intersectPlane(Point4f plane, float scale, int flags) {
    List<Object> v = new ArrayList<Object>();
    v.add(getCanonicalCopy(scale));
    return TriangleData.intersectPlane(plane, v, flags);
  }


  public Point3f[] getCanonicalCopy(float scale) {
    return getCanonicalCopy(bbVertices, scale);
  }

  public final static Point3f[] getCanonicalCopy(Point3f[] bbUcPoints, float scale) {
    Point3f[] pts = new Point3f[8];
    for (int i = 0; i < 8; i++)
      pts[toCanonical[i]] = new Point3f(bbUcPoints[i]);
    scaleBox(pts, scale);
    return pts;
  }
  
  public static void scaleBox(Point3f[] pts, float scale) {
    if (scale == 0 || scale == 1)
      return;
    Point3f center = new Point3f();
    Vector3f v = new Vector3f();
    for (int i = 0; i < 8; i++)
      center.add(pts[i]);
    center.scale(1/8f);
    for (int i = 0; i < 8; i++) {
      v.sub(pts[i], center);
      v.scale(scale);
      pts[i].add(center, v);
    }
  }

  public static Point4f[] getFacesFromCriticalPoints(Point3f[] points) {
    Point4f[] faces = new Point4f[6];
    Vector3f vNorm = new Vector3f();
    Vector3f vAB = new Vector3f();
    Vector3f vAC = new Vector3f();
    Point3f va = new Point3f();
    Point3f vb = new Point3f();
    Point3f vc = new Point3f();
    
    Point3f[] vertices = new Point3f[8];
    for (int i = 0; i < 8; i++) {
      vertices[i] = new Point3f(points[0]);
      if ((i & 1) == 1)
        vertices[i].add(points[1]);
      if ((i & 2) == 2)
        vertices[i].add(points[2]);
      if ((i & 4) == 4)
        vertices[i].add(points[3]);
    }

    for (int i = 0; i < 6; i++) {
      va.set(vertices[facePoints[i].x]);
      vb.set(vertices[facePoints[i].y]);
      vc.set(vertices[facePoints[i].z]);
      Measure.getPlaneThroughPoints(va, vb, vc, vNorm, vAB, vAC, faces[i] = new Point4f());
    }
    return faces;
  }

  /*                     Y 
   *                      2 --------6--------- 6                            
   *                     /|                   /|          
   *                    / |                  / |           
   *                   /  |                 /  |           
   *                  5   1               11   |           
   *                 /    |               /    9           
   *                /     |              /     |         
   *               3 --------7--------- 7      |         
   *               |      |             |      |         
   *               |      0 ---------2--|----- 4    X        
   *               |     /              |     /          
   *               3    /              10    /           
   *               |   0                |   8            
   *               |  /                 |  /             
   *               | /                  | /               
   *               1 ---------4-------- 5                 
   *              Z                                       
   */
  
  public final static Point3f[] unitCubePoints = { 
    new Point3f(0, 0, 0), // 0
    new Point3f(0, 0, 1), // 1
    new Point3f(0, 1, 0), // 2
    new Point3f(0, 1, 1), // 3
    new Point3f(1, 0, 0), // 4
    new Point3f(1, 0, 1), // 5
    new Point3f(1, 1, 0), // 6
    new Point3f(1, 1, 1), // 7
  };

  private static Point3i[] facePoints = {
    new Point3i(4, 0, 6),
    new Point3i(4, 6, 5), 
    new Point3i(5, 7, 1), 
    new Point3i(1, 3, 0),
    new Point3i(6, 2, 7), 
    new Point3i(1, 0, 5), 
  };

  public final static int[] toCanonical = new int[] {0, 3, 4, 7, 1, 2, 5, 6};

  protected final static Point3i[] cubeVertexOffsets = { 
    new Point3i(0, 0, 0), //0 pt
    new Point3i(1, 0, 0), //1 pt + yz
    new Point3i(1, 0, 1), //2 pt + yz + 1
    new Point3i(0, 0, 1), //3 pt + 1
    new Point3i(0, 1, 0), //4 pt + z
    new Point3i(1, 1, 0), //5 pt + yz + z
    new Point3i(1, 1, 1), //6 pt + yz + z + 1
    new Point3i(0, 1, 1)  //7 pt + z + 1 
  };

  public final static Point3f[] getCriticalPoints(Point3f[] bbVertices, Point3f offset) {
    Point3f center = new Point3f(bbVertices[0]);
    Point3f a = new Point3f(bbVertices[1]);
    Point3f b = new Point3f(bbVertices[2]);
    Point3f c = new Point3f(bbVertices[4]);
    a.sub(center);
    b.sub(center);
    c.sub(center);
    if (offset != null)
      center.add(offset);
    return new Point3f[] { center, a, b, c };
  }
  
  private final static Point3f[] unitBboxPoints = new Point3f[8];
  { 
    for (int i = 0; i < 8; i++) {
      unitBboxPoints[i] = new Point3f(-1, -1, -1);
      unitBboxPoints[i].scaleAdd(2, unitCubePoints[i], unitBboxPoints[i]); 
    }
  }

  public Point3f getBoundBoxCenter() {
    return bbCenter;
  }

  public Vector3f getBoundBoxCornerVector() {
    return bbVector;
  }

  public Point3f[] getBoundBoxPoints() {
    return new Point3f[] {bbCenter, new Point3f(bbVector), bbCorner0, bbCorner1};
  }

  public Point3fi[] getBboxVertices() {
    return bbVertices;
  }
  
  Map<String, Object> getBoundBoxInfo() {
    Map<String, Object> info = new Hashtable<String, Object>();
    info.put("center", new Point3f(bbCenter));
    info.put("vector", new Vector3f(bbVector));
    info.put("corner0", new Point3f(bbCorner0));
    info.put("corner1", new Point3f(bbCorner1));
    return info;
  }

  void setBoundBox(Point3f pt1, Point3f pt2, boolean byCorner, float scale) {
    if (pt1 != null) {
      if (scale == 0)
        return;
      if (byCorner) {
        if (pt1.distance(pt2) == 0)
          return;
        bbCorner0.set(Math.min(pt1.x, pt2.x), Math.min(pt1.y, pt2.y), Math.min(
            pt1.z, pt2.z));
        bbCorner1.set(Math.max(pt1.x, pt2.x), Math.max(pt1.y, pt2.y), Math.max(
            pt1.z, pt2.z));
      } else { // center and vector
        if (pt2.x == 0 || pt2.y == 0 && pt2.z == 0)
          return;
        bbCorner0.set(pt1.x - pt2.x, pt1.y - pt2.y, pt1.z - pt2.z);
        bbCorner1.set(pt1.x + pt2.x, pt1.y + pt2.y, pt1.z + pt2.z);
      }
    }
    setBbcage(scale);
  }

  void reset() {
    bbCorner0.set(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    bbCorner1.set(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
  }
  
  void addBoundBoxPoint(Point3f pt) {
    float t = pt.x;
    if (t < bbCorner0.x)
      bbCorner0.x = t;
    if (t > bbCorner1.x)
      bbCorner1.x = t;
    t = pt.y;
    if (t < bbCorner0.y)
      bbCorner0.y = t;
    if (t > bbCorner1.y)
      bbCorner1.y = t;
    t = pt.z;
    if (t < bbCorner0.z)
      bbCorner0.z = t;
    if (t > bbCorner1.z)
      bbCorner1.z = t;
  }

  void setBbcage(float scale) {
    bbCenter.add(bbCorner0, bbCorner1);
    bbCenter.scale(0.5f);
    bbVector.sub(bbCorner1, bbCenter);
    if (scale > 0) {
      bbVector.scale(scale);
    } else {
      bbVector.x -= scale / 2;
      bbVector.y -= scale / 2;
      bbVector.z -= scale / 2;
    }
    for (int i = 8; --i >= 0;) {
      Point3f pt = bbVertices[i];
      pt.set(unitBboxPoints[i]);
      pt.x *= bbVector.x;
      pt.y *= bbVector.y;
      pt.z *= bbVector.z;
      pt.add(bbCenter);
    }
    bbCorner0.set(bbVertices[0]);
    bbCorner1.set(bbVertices[7]);
  }
  
  boolean isWithin(Point3f pt) {
   return (pt.x >= bbCorner0.x && pt.x <= bbCorner1.x 
       && pt.y >= bbCorner0.y && pt.y <= bbCorner1.y
       && pt.z >= bbCorner0.z && pt.z <= bbCorner1.z); 
  }



}
