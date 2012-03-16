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

import java.io.BufferedReader;

import org.jmol.util.Logger;
import org.jmol.util.Parser;

class CubeReader extends VolumeFileReader {

  CubeReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
  }
  
  @Override
  protected void readParameters() throws Exception {
    jvxlFileHeaderBuffer = new StringBuffer();
    jvxlFileHeaderBuffer.append(readLine()).append('\n');
    jvxlFileHeaderBuffer.append(readLine()).append('\n');
    String atomLine = readLine();
    String[] tokens = Parser.getTokens(atomLine, 0);
    atomCount = parseInt(tokens[0]);
    negativeAtomCount = (atomCount < 0); // MO list
    if (negativeAtomCount)
      atomCount = -atomCount;
    volumetricOrigin.set(parseFloat(tokens[1]), parseFloat(tokens[2]),
        parseFloat(tokens[3]));
    VolumeFileReader.checkAtomLine(isXLowToHigh, isAngstroms, tokens[0],
        atomLine, jvxlFileHeaderBuffer);
    if (!isAngstroms)
      volumetricOrigin.scale(ANGSTROMS_PER_BOHR);
    for (int i = 0; i < 3; ++i)
      readVoxelVector(i);
    for (int i = 0; i < atomCount; ++i)
      jvxlFileHeaderBuffer.append(readLine() + "\n");

    if (!negativeAtomCount) {
      nSurfaces = 1;
    } else {
      readLine();
      Logger.info("Reading extra CUBE information line: " + line);
      nSurfaces = parseInt(line);
    }
  }  
}


