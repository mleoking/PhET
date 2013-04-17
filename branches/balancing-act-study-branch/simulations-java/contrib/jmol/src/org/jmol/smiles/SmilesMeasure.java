/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-06-10 13:04:22 -0500 (Thu, 10 Jun 2010) $
 * $Revision: 13329 $
 *
 * Copyright (C) 2005  The Jmol Development Team
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

package org.jmol.smiles;

import javax.vecmath.Point3f;

import org.jmol.smiles.SmilesSearch.VTemp;

public class SmilesMeasure  {

  // processing of C(.d:1.5,1.6)C
  // or C(.d1:1.5)C(.d1:1.6)
  SmilesSearch search;
  int nPoints;
  int type;
  int index;
  boolean isNot;
  
  private int[] indices = new int[4];
  static final String TYPES = "__dat";
  
  private float min;
  private float max;
  
  SmilesMeasure(SmilesSearch search, int index, int type, float min, float max, boolean isNot) {
    this.search = search;
    this.type = Math.min(4, Math.max(type, 2));
    this.index = index;
    this.min = Math.min(min, max);
    this.max = Math.max(min, max);
    this.isNot = isNot;
  }
  
  @Override
  public String toString() {
    String s = "(." + TYPES.charAt(type) + index + ":" + min + "," + max + ") for";
    for (int i = 0; i < type; i++)
      s+= " " + (i >= nPoints ? "?" : "" + indices[i]);
    return s;
  }
  boolean addPoint(int index) {
    if (nPoints == type)
      return false;
    if (nPoints == 0)
      for (int i = 1; i < type; i++)
        indices[i] = index + i;
    indices[nPoints++] = index;
    return true;
  }
  
  private final static float radiansPerDegree = (float) (2 * Math.PI / 360);

  private final Point3f[] points = new Point3f[4];
  
  boolean check() {
    for (int i = 0; i < type; i++) {
      int iAtom = search.patternAtoms[indices[i]].getMatchingAtom();
      //System.out.print(iAtom + "-");
      points[i] = (Point3f) search.jmolAtoms[iAtom];
      //System.out.println(points[i]);
    }
    float d = 0;
    switch (type) {
    case 2:
      d = points[0].distance(points[1]);
      break;
    case 3:
      search.v.vA.sub(points[0], points[1]);
      search.v.vB.sub(points[2], points[1]);
      d = search.v.vA.angle(search.v.vB) / radiansPerDegree;
      break;
    case 4: 
      setTorsionData(points[0], points[1], points[2], points[3], search.v, true);
      d = search.v.vTemp1.angle(search.v.vTemp2) / radiansPerDegree * (search.v.vNorm1.dot(search.v.vNorm2) < 0 ? 1 : -1);
      break;
    }
    //System.out.println(type + " " + min + " " + max + " " + d + " " + isNot);
    return ((d < min || d > max) == isNot);   
  }

  public static void setTorsionData(Point3f pt1a, Point3f pt1,
                                    Point3f pt2, Point3f pt2a,
                                    VTemp v, boolean isAll) {
    // We cross dihedral bonds with the bond axis
    // to get two vector projections in the
    // plane perpendicular to the bond axis
    v.vTemp1.set(pt1a);
    v.vTemp1.sub(pt1);
    v.vTemp2.set(pt2a);
    v.vTemp2.sub(pt2);
    if (!isAll)
      return;
    v.vNorm1.set(pt1);
    v.vNorm1.sub(pt2);
    v.vNorm1.normalize();
    v.vTemp1.cross(v.vTemp1, v.vNorm1);
    v.vTemp1.normalize();
    v.vTemp2.cross(v.vTemp2, v.vNorm1);
    v.vTemp2.normalize();
    v.vNorm2.cross(v.vTemp1, v.vTemp2);
  }
}