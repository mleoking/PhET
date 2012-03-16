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

import javax.vecmath.Point3f;

import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.util.BinaryDocument;
import org.jmol.util.Logger;
import org.jmol.util.SurfaceFileTyper;

/*
 * 
 * ASCII format:

 100
 3.0000 3.0000 1.0000
 2.3333 3.0000 1.0000
 ...(98 more like this)
 81
 5
 0
 10
 11
 1
 0
 ...(80 more sets like this)

 * The first line defines the number of grid points 
 *   defining the surface (integer, n)
 * The next n lines define the Cartesian coordinates 
 *   of each of the grid points (n lines of x, y, z floating point data points)
 * The next line specifies the number of polygons, m, to be drawn (81 in this case).
 * The next m sets of numbers, one number per line, 
 *   define the polygons. In each set, the first number, p, specifies 
 *   the number of points in each set. Currently this number must be either 
 *   4 (for triangles) or 5 (for quadrilaterals). The next p numbers specify 
 *   indexes into the list of data points (starting with 0). 
 *   The first and last of these numbers must be identical in order to 
 *   "close" the polygon.
 * 
 * Jmol does not care about lines. 
 * 
 * Binary format: 
 * 
 * note that there is NO redundant extra vertex in this format 
 *
 *  4 bytes: P M \1 \0 
 *  4 bytes: ignored
 *  4 bytes: (int) 1 -- first byte used to determine big(==0) or little(!=0) endian
 *  4 bytes: (int) nVertices
 *  4 bytes: (int) nPolygons
 * 64 bytes: reserved
 *  ------------------------------
 *  float[nVertices*3]vertices {x,y,z}
 *  [nPolygons] polygons 
 *  --each polygon--
 *    4 bytes: (int)nVertices (1,2,3, or 4)
 *    [4 bytes * nVertices] int[nVertices]
 *    
 *
 */


class PmeshReader extends PolygonFileReader {

  final static String PMESH_BINARY_MAGIC_NUMBER = SurfaceFileTyper.PMESH_BINARY_MAGIC_NUMBER;

  private boolean isBinary;
  protected int nPolygons;
  protected String pmeshError;
  protected String type;
  protected boolean isClosedFace; // a b c d a (pmesh only)
  protected int fixedCount;
  protected boolean onePerLine;
  protected int vertexBase;
  
  PmeshReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
  }

  PmeshReader(SurfaceGenerator sg, String fileName, BufferedReader br) {
    super(sg, br);
    type = "pmesh";
    setHeader();
    isBinary = checkBinary(fileName);
    isClosedFace = !isBinary;
  }
  
  protected void setHeader() {
    jvxlFileHeaderBuffer.append(type
        + " file format\nvertices and triangles only\n");
    JvxlCoder.jvxlCreateHeaderWithoutTitleOrAtoms(volumeData,
        jvxlFileHeaderBuffer);
  }

  protected boolean checkBinary(String fileName) {
    try {
      br.mark(4);
      char[] buf = new char[5];
      br.read(buf);
      if ((new String(buf)).startsWith(PMESH_BINARY_MAGIC_NUMBER)) {
        br.close();
        binarydoc = new BinaryDocument();
        binarydoc.setStream(sg.getAtomDataServer().getBufferedInputStream(
            fileName), (buf[4] == '\0'));
        return true;
      }
      br.reset();
    } catch (Exception e) {
    }
    return false;
  }

  @Override
  void getSurfaceData() throws Exception {
    if (readVerticesAndPolygons())
      Logger.info((isBinary ? "binary " : "") + type  + " file contains "
          + nVertices + " vertices and " + nPolygons + " polygons for "
          + nTriangles + " triangles");
    else
      Logger.error(params.fileName + ": " 
          + (pmeshError == null ? "Error reading pmesh data "
              : pmeshError));
  }

  protected boolean readVerticesAndPolygons() {
    try {
      if (isBinary && !readBinaryHeader())
        return false;
      if (readVertices() && readPolygons())
        return true;
    } catch (Exception e) {
      if (pmeshError == null)
        pmeshError = type  + " ERROR: " + e;
    }
    return false;
  }

  boolean readBinaryHeader() {
    pmeshError = "could not read binary Pmesh file header";
    try {
      byte[] ignored = new byte[64];
      binarydoc.readByteArray(ignored, 0, 8);
      nVertices = binarydoc.readInt();
      nPolygons = binarydoc.readInt();
      binarydoc.readByteArray(ignored, 0, 64);
    } catch (Exception e) {
      pmeshError += " " + e.getMessage();
      binarydoc.close();
      return false;
    }
    pmeshError = null;
    return true;
  }

  protected int[] vertexMap;
  
  protected boolean readVertices() throws Exception {
    pmeshError = type + " ERROR: vertex count must be positive";
    if (!isBinary)
      nVertices = getInt();
    if (onePerLine)
      iToken = Integer.MAX_VALUE;
    if (nVertices <= 0) {
      pmeshError += " (" + nVertices + ")";
      return false;
    }
    pmeshError = type + " ERROR: invalid vertex list";
    Point3f pt = new Point3f();
    vertexMap = new int[nVertices];
    for (int i = 0; i < nVertices; i++) {
      pt.set(getFloat(), getFloat(), getFloat());
      if (isAnisotropic)
        setVertexAnisotropy(pt);
      if (Logger.debugging)
        Logger.debug(i + ": " + pt);
      vertexMap[i] = addVertexCopy(pt, 0, i);
      if (onePerLine)
        iToken = Integer.MAX_VALUE;
    }
    pmeshError = null;
    return true;
  }

  protected boolean readPolygons() throws Exception {
    pmeshError = type  + " ERROR: polygon count must be zero or positive";
    if (!isBinary)
      nPolygons = getInt();
    if (nPolygons < 0) {
      pmeshError += " (" + nPolygons + ")";
      return false;
    }
    if (onePerLine)
      iToken = Integer.MAX_VALUE;
    int[] vertices = new int[5];
    for (int iPoly = 0; iPoly < nPolygons; iPoly++) {
      int intCount = (fixedCount == 0 ? getInt() : fixedCount);
      int vertexCount = intCount - (isClosedFace ? 1 : 0);
      // (we will ignore the redundant extra vertex when not binary and not msms)
      if (vertexCount < 1 || vertexCount > 4) {
        pmeshError = type  + " ERROR: bad polygon (must have 1-4 vertices) at #"
            + (iPoly + 1);
        return false;
      }
      boolean isOK = true;
      for (int i = 0; i < intCount; ++i) {
        if ((vertices[i] = getInt() - vertexBase) < 0 || vertices[i] >= nVertices) {
          pmeshError = type  + " ERROR: invalid vertex index: " + vertices[i];
          return false;
        }
        if ((vertices[i] = vertexMap[vertices[i]]) < 0)
          isOK = false;
      }
      if (onePerLine)
        iToken = Integer.MAX_VALUE;
      if (!isOK)
        continue;
      // allow for point or line definition here
      if (vertexCount < 3)
        for (int i = vertexCount; i < 3; ++i)
          vertices[i] = vertices[i - 1];
      // check: 1 (ab) | 2(bc) | 4(ac)
      //    1
      //  a---b
      // 4 \ / 2
      //    c
      //
      //    1
      //  a---b      b
      // 4 \     +    \ 1 
      //    d      d---c
      //             2
      if (vertexCount == 4) {
        nTriangles += 2;
        addTriangleCheck(vertices[0], vertices[1], vertices[3], 5, 0, false, 0);
        addTriangleCheck(vertices[1], vertices[2], vertices[3], 3, 0, false, 0);
      } else {
        nTriangles++;
        addTriangleCheck(vertices[0], vertices[1], vertices[2], 7, 0, false, 0);
      }
    }
    if (isBinary)
      nBytes = binarydoc.getPosition();
    return true;
  }

  @Override
  public int addTriangleCheck(int iA, int iB, int iC, int check,
                               int check2, boolean isAbsolute, int color) {
    if (Logger.debugging)
      Logger.debug("tri: " + iA + " " + iB + " " + iC);
    return super.addTriangleCheck(iA, iB, iC, check, check2, isAbsolute, color); 
  }

  //////////// file reading

  protected String[] tokens = new String[0];
  protected int iToken = 0;

  private String nextToken() throws Exception {
    while (iToken >= tokens.length) { 
      iToken = 0;
      readLine();
      tokens = getTokens();
    }
    return tokens[iToken++];
  }

  private int getInt() throws Exception {
    return (isBinary ? binarydoc.readInt() : parseInt(nextToken()));
  }

  private float getFloat() throws Exception {
    return (isBinary ? binarydoc.readFloat() : parseFloat(nextToken()));
  }

}
