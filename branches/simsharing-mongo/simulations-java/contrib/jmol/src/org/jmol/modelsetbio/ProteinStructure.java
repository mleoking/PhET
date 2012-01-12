/* $RCSfile$
 * $Author: nicove $
 * $Date: 2007-03-25 06:44:28 -0500 (Sun, 25 Mar 2007) $
 * $Revision: 7224 $
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
package org.jmol.modelsetbio;

import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

public abstract class ProteinStructure {

  static int globalSerialID = 1000;
  AlphaPolymer apolymer;
  byte type;
  byte subtype;
  int monomerIndexFirst;
  int monomerIndexLast;
  int monomerCount;
  Point3f axisA, axisB;
  Vector3f axisUnitVector;
  final Vector3f vectorProjection = new Vector3f();
  Point3f[] segments;
  int uniqueID;
  String structureID;
  int serialID;
  int strandCount;
  
  /**
   * 
   * @param apolymer
   * @param type
   * @param monomerIndex
   * @param monomerCount
   * @param id              UNUSED
   */
  ProteinStructure(AlphaPolymer apolymer, byte type,
                   int monomerIndex, int monomerCount, int id) {
    uniqueID = ++globalSerialID;
    this.apolymer = apolymer;
    this.type = type;    
    monomerIndexFirst = monomerIndex;
    addMonomer(monomerIndex + monomerCount - 1);
    
    if(Logger.debugging)
      Logger.debug(
          "Creating ProteinStructure " + uniqueID 
          + " " + JmolConstants.getProteinStructureName(type, false) 
          + " from " + monomerIndexFirst + " through "+ monomerIndexLast
          + " in polymer " + apolymer);
  }
  
  /**
   * Note that this method does not check to see 
   * that there are no overlapping protein structures.
   *  
   * @param index
   */
  void addMonomer(int index) {
    monomerIndexFirst = Math.min(monomerIndexFirst, index);
    monomerIndexLast = Math.max(monomerIndexLast, index);
    monomerCount = monomerIndexLast - monomerIndexFirst + 1;
  }

  /**
   * should be OK here to remove the first -- we just get a 
   * monomerCount of 0; but we don't remove monomers that aren't
   * part of this structure.
   * 
   * @param monomerIndex
   * @return the number of monomers AFTER this one that have been abandoned
   */
  int removeMonomer(int monomerIndex) {
    if (monomerIndex > monomerIndexLast || monomerIndex < monomerIndexFirst)
      return 0;
    int ret = monomerIndexLast - monomerIndex;
    monomerIndexLast = Math.max(monomerIndexFirst, monomerIndex) - 1;
    monomerCount = monomerIndexLast - monomerIndexFirst + 1;
    return ret;
  }

  public void calcAxis() {
  }

  void calcSegments() {
    if (segments != null)
      return;
    calcAxis();
    segments = new Point3f[monomerCount + 1];
    segments[monomerCount] = axisB;
    segments[0] = axisA;
    Vector3f axis = new Vector3f(axisUnitVector);
    axis.scale(axisB.distance(axisA) / monomerCount);
    for (int i = 1; i < monomerCount; i++) {
      Point3f point = segments[i] = new Point3f();
      point.set(segments[i - 1]);
      point.add(axis);
      //now it's just a constant-distance segmentation. 
      //there isn't anything significant about seeing the
      //amino colors in different-sized slices, and (IMHO)
      //it looks better this way anyway. RMH 11/2006
      
      //apolymer.getLeadMidPoint(monomerIndex + i, point);
      //projectOntoAxis(point);
    }
  }

  boolean lowerNeighborIsHelixOrSheet() {
    if (monomerIndexFirst == 0)
      return false;
    return apolymer.monomers[monomerIndexFirst - 1].isHelix()
        || apolymer.monomers[monomerIndexFirst - 1].isSheet();
  }

  boolean upperNeighborIsHelixOrSheet() {
    int upperNeighborIndex = monomerIndexFirst + monomerCount;
    if (upperNeighborIndex == apolymer.monomerCount)
      return false;
    return apolymer.monomers[upperNeighborIndex].isHelix()
        || apolymer.monomers[upperNeighborIndex].isSheet();
  }

  public int getMonomerCount() {
    return monomerCount;
  }
  
  public boolean isWithin(int monomerIndex) {
    return (monomerIndex > monomerIndexFirst 
        && monomerIndex < monomerIndexLast);
  }

  public int getMonomerIndex() {
    return monomerIndexFirst;
  }

  public int getIndex(Monomer monomer) {
    Monomer[] monomers = apolymer.monomers;
    int i;
    for (i = monomerCount; --i >= 0; )
      if (monomers[monomerIndexFirst + i] == monomer)
        break;
    return i;
  }

  public Point3f[] getSegments() {
    if (segments == null)
      calcSegments();
    return segments;
  }

  public Point3f getAxisStartPoint() {
    calcAxis();
    return axisA;
  }

  public Point3f getAxisEndPoint() {
    calcAxis();
    return axisB;
  }

  Point3f getStructureMidPoint(int index) {
    if (segments == null)
      calcSegments();
    return segments[index];
  }

  public void getInfo(Map<String, Object> info) {
    info.put("type", JmolConstants.getProteinStructureName(type, false));
    int[] leadAtomIndices = apolymer.getLeadAtomIndices();
    int[] iArray = new int[monomerCount];
    System.arraycopy(leadAtomIndices, monomerIndexFirst, iArray, 0, monomerCount);
    info.put("leadAtomIndices", iArray);
    calcAxis();
    if (axisA == null)
      return;
    info.put("axisA", axisA);
    info.put("axisB", axisB);
    info.put("axisUnitVector", axisUnitVector);
  }

  void resetAxes() {
    axisA = null;
    segments = null;
  }
}
