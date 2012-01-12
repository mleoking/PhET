/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-05-18 08:19:45 -0500 (Fri, 18 May 2007) $
 * $Revision: 7742 $

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

package org.jmol.symmetry;

import javax.vecmath.Point3f;

import org.jmol.util.SimpleUnitCell;

import java.util.Map;

class SymmetryInfo {

  boolean coordinatesAreFractional;
  boolean isMultiCell;
  String spaceGroup;
  String[] symmetryOperations;
  String symmetryInfoString;
  int modelIndex;
  int[] cellRange;
  private Point3f periodicOriginXyz;

  boolean isPeriodic() {
    return periodicOriginXyz != null;
  }

  SymmetryInfo() {    
  }
  
  float[] setSymmetryInfo(int modelIndex, Map<String, Object> info) {
    this.modelIndex = modelIndex;
    cellRange = (int[]) info.get("unitCellRange");
    periodicOriginXyz = (Point3f) info.get("periodicOriginXyz");
    spaceGroup = (String) info.get("spaceGroup");
    if (spaceGroup == null || spaceGroup == "")
      spaceGroup = "spacegroup unspecified";
    int symmetryCount = info.containsKey("symmetryCount") ? 
        ((Integer) info.get("symmetryCount")).intValue() 
        : 0;
    symmetryOperations = (String[]) info.get("symmetryOperations");
    symmetryInfoString = "Spacegroup: " + spaceGroup;
    if (symmetryOperations == null) {
      symmetryInfoString += "\nNumber of symmetry operations: ?"
          + "\nSymmetry Operations: unspecified\n";
    } else {
      symmetryInfoString += "\nNumber of symmetry operations: "
          + (symmetryCount == 0 ? 1 : symmetryCount) + "\nSymmetry Operations:";
      for (int i = 0; i < symmetryCount; i++)
        symmetryInfoString += "\n" + symmetryOperations[i];
    }
    symmetryInfoString += "\n";
    float[] notionalUnitcell = (float[]) info.get("notionalUnitcell");
    if (SimpleUnitCell.isValid(notionalUnitcell)) 
      coordinatesAreFractional = info.containsKey("coordinatesAreFractional") ? 
          ((Boolean) info.get("coordinatesAreFractional")).booleanValue() 
          : false;    
    else
      notionalUnitcell = null;
    isMultiCell = (coordinatesAreFractional && symmetryOperations != null);
    return notionalUnitcell;
  }
}

