/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-11-22 04:06:32 -0600 (Thu, 22 Nov 2007) $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development, www.jmol.org
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
package org.jmol.bspt;

import javax.vecmath.Point3f;

/**
 * Iterator used for finding all points within a box or a hemi-box
 *<p>
 * Obtain a CubeIterator by calling Bspt.allocateCubeIterator().
 *<p>
 * call initialize(...) or initializeHemizphere(...)
 *<p>
 * re-initialize in order to reuse the same CubeIterator
 *
 * @author Miguel, miguel@jmol.org
 */
public class CubeIterator {
  Bspt bspt;

  Element[] stack;
  int sp;
  int leafIndex;
  Leaf leaf;

  //Point3f center;
  float radius;
  
  float[] centerValues;
  private float cx, cy, cz;
  protected float dx, dy, dz;

  // when set, only the hemisphere sphere .GE. the point
  // (on the first dim) is returned
  boolean tHemisphere;

  CubeIterator(Bspt bspt) {
    this.bspt = bspt;
    centerValues = new float[bspt.dimMax];
    stack = new Element[bspt.treeDepth];
  }

  /**
   * initialize to return all points within the sphere defined
   * by center and radius
   *
   * @param center
   * @param radius
   * @param hemisphereOnly 
   */
  public void initialize(Point3f center, float radius, boolean hemisphereOnly) {
    //this.center = center;
    this.radius = radius;
    tHemisphere = false;
    cx = centerValues[0] = center.x;
    cy = centerValues[1] = center.y;
    cz = centerValues[2] = center.z;
    leaf = null;
    stack[0] = bspt.eleRoot;
    sp = 1;
    findLeftLeaf();
    tHemisphere = hemisphereOnly;
  }

  /**
   * nulls internal references
   */
  public void release() {
    for (int i = bspt.treeDepth; --i >= 0; )
      stack[i] = null;
  }

  /**
   * normal iterator predicate
   *
   * @return boolean
   */
  public boolean hasMoreElements() {
    while (leaf != null) {
      for ( ; leafIndex < leaf.count; ++leafIndex)
        if (isWithinRadius(leaf.tuples[leafIndex]))
          return true;
      findLeftLeaf();
    }
    return false;
  }

  /**
   * normal iterator method
   *
   * @return Tuple
   */
  public Point3f nextElement() {
    return leaf.tuples[leafIndex++];
  }

  /**
   * After calling nextElement(), allows one to find out
   * the value of the distance squared. To get the distance
   * just take the sqrt.
   *
   * @return float
   */
  public float foundDistance2() {
    return dx * dx + dy * dy + dz * dz;
  }
  
  /**
   * does the work
   */
  private void findLeftLeaf() {
    leaf = null;
    if (sp == 0)
      return;
    Element ele = stack[--sp];
    while (ele instanceof Node) {
      Node node = (Node)ele;
      float centerValue = centerValues[node.dim];
      float maxValue = centerValue + radius;
      float minValue = centerValue;
      if (! tHemisphere || node.dim != 0)
        minValue -= radius;
      if (minValue <= node.maxLeft && maxValue >= node.minLeft) {
        if (maxValue >= node.minRight && minValue <= node.maxRight)
          stack[sp++] = node.eleRight;
        ele = node.eleLeft;
      } else if (maxValue >= node.minRight && minValue <= node.maxRight) {
        ele = node.eleRight;
      } else {
        if (sp == 0)
          return;
        ele = stack[--sp];
      }
    }
    leaf = (Leaf)ele;
    leafIndex = 0;
  }

  /**
   * checks one Point3f for distance
   * @param t
   * @return boolean
   */
  private boolean isWithinRadius(Point3f t) {
    dx = t.x - cx;
    return (!tHemisphere || dx >= 0)        
    && (dx = Math.abs(dx)) <= radius
    && (dy = Math.abs(t.y - cy)) <= radius
    && (dz = Math.abs(t.z - cz)) <= radius;
  }
    
}
