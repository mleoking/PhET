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
import java.io.InputStreamReader;
import org.jmol.util.Logger;
import org.jmol.util.TextFormat;

/*
 * 
 * reads MSMS output files .vert with implied .face also present
 * 
 */


class MsmsReader extends PmeshReader {

  private String fileName;
  MsmsReader(SurfaceGenerator sg, String fileName, BufferedReader br) {
    super(sg, br);
    this.fileName = fileName;
    type = "msms";
    onePerLine = true;
    fixedCount = 3;
    vertexBase = 1;
    setHeader();
  }

  @Override
  protected boolean readVertices() throws Exception {
    skipHeader();
    return super.readVertices();
  }

  @Override
  protected boolean readPolygons() throws Exception {
    br.close();
    fileName = TextFormat.simpleReplace(fileName, ".vert", ".face");
    Logger.info("reading from file " + fileName);
    try {
    br = new BufferedReader(new InputStreamReader(sg.getAtomDataServer().getBufferedInputStream(
        fileName)));
    } catch (Exception e) {
      Logger.info("Note: file " + fileName + " was not found");
      br = null;
      return true;
    }
    sg.addRequiredFile(fileName);
    skipHeader();
    return super.readPolygons();
  }

  private void skipHeader() throws Exception {
    while (readLine() != null && line.indexOf("#") >= 0) {      
      // skip header
    }
    tokens = getTokens();
    iToken = 0;
  }

}
