/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-03-30 11:40:16 -0500 (Fri, 30 Mar 2007) $
 * $Revision: 7273 $
 *
 * Copyright (C) 2007 Miguel, Bob, Jmol Development
 *
 * Contact: hansonr@stolaf.edu
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.jvxl.readers;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;

import org.jmol.jvxl.data.JvxlCoder;

class IsoFxyzReader extends VolumeDataReader {
  
  IsoFxyzReader(SurfaceGenerator sg) {
    super(sg);
    precalculateVoxelData = false;
  }

  private String functionName;
  private float[][][] data;
  
  @Override
  protected void setup() {
    functionName = (String) params.functionXYinfo.get(0);
    jvxlFileHeaderBuffer = new StringBuffer();
    jvxlFileHeaderBuffer.append("functionXYZ\n").append(functionName).append("\n");
    volumetricOrigin.set((Point3f) params.functionXYinfo.get(1));
    for (int i = 0; i < 3; i++) {
      Point4f info = (Point4f) params.functionXYinfo.get(i + 2);
      voxelCounts[i] = Math.abs((int) info.x);
      volumetricVectors[i].set(info.y, info.z, info.w);      
    }
    if (isAnisotropic)
      setVolumetricAnisotropy();
    data = (float[][][]) params.functionXYinfo.get(5);
    JvxlCoder.jvxlCreateHeaderWithoutTitleOrAtoms(volumeData, jvxlFileHeaderBuffer);
  }

  @Override
  public float getValue(int x, int y, int z, int ptyz) {
    return data[x][y][z];
  }
}
