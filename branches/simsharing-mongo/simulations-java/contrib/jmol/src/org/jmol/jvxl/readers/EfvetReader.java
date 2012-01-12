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
import org.jmol.util.Logger;

/*
 * A simple EFVET file reader -- vertices and triangles only
 * http://ef-site.hgc.jp/eF-site/about.html
 * 
 */
class EfvetReader extends PolygonFileReader {

  EfvetReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    jvxlFileHeaderBuffer.append("efvet file format\nvertices and triangles only\n");
    JvxlCoder.jvxlCreateHeaderWithoutTitleOrAtoms(volumeData, jvxlFileHeaderBuffer);
    hasColorData = true;
  }

  /*
   *
   * 
   * 
   * 
<efvet xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://pdbj.protein.osaka-u.ac.jp/eF-site http://ef-site.hgc.jp/eF-site/schema/efvet30.xsd"
       xmlns="http://pdbj.protein.osaka-u.ac.jp/eF-site"
       pdb_id="1lmb"
       model_id=""
       chain_ids="3"
       domain_definition=""
       domain_source=""
       revision="2008-11-02">

  <vertices count="5671" id_numbers="ignore">
    <!--
    [items of image attribute]
    image="x y z nx ny nz R G B"
    where (x,y,z), (nx,ny,nz) and (R,G,B) stand for coordinates, normal vector and color, respectively.

    [items of property attribute]
    property="electrostatic_potential hydrophobicity temperature_factor minimum_curvature maximum_curvature"
    -->
    <vertex id="1" image="20.170221 -19.704086 -9.601047 -0.34588054 -0.38787818 -0.8543519 255.0 127.5 127.5" property="-0.054762 4.200000 27.460000 -0.142968 0.024753"/>
    <vertex id="2" image="19.908976 -19.848297 -9.464497 -0.08880216 -0.5433132 -0.83482033 255.0 127.5 127.5" property="-0.055614 4.200000 27.460000 -0.194855 0.206080"/>


  <edges count="17007">
    <edge id="1" vertex="1 2"/>
    <edge id="2" vertex="2 3"/>

  <triangle_array count="11338">
    <triangle id="1" vertex="3426 3427 3428" edge="9969 9970 9971"/>
    <triangle id="2" vertex="3427 3426 3429" edge="-9969 9972 9973"/>

   *
   * Bob Hanson, 11/2008
   */

  @Override
  void getSurfaceData() throws Exception{
    getHeader();
    getVertices();
    getTriangles();
    Logger.info("efvet file contains " + nVertices + " vertices and " + nTriangles + " triangles");
  }

  private void getHeader() throws Exception {
    skipTo("<efvet", null);
    while(readLine().length() > 0 && line.indexOf(">") < 0)
      jvxlFileHeaderBuffer.append("# " + line + "\n");
    Logger.info(jvxlFileHeaderBuffer.toString());
  }
  
  private void getVertices() throws Exception {
    Point3f pt = new Point3f();
    float value = 0;
    skipTo("<vertices", "count");
    jvxlData.vertexCount = nVertices = parseInt(); 
    skipTo("property=", null);
    line = line.replace('"',' ');
    String[] tokens = getTokens();
    int dataIndex = params.fileIndex;
    if (dataIndex > 0 && dataIndex < tokens.length)
      Logger.info("property " + tokens[dataIndex]);
    else
      Logger.info(line);
    for (int i = 0; i < nVertices; i++) {
      skipTo("<vertex", "image");
      pt.set(parseFloat(), parseFloat(), parseFloat());
      skipTo(null, "property");
      for(int j = 0; j < dataIndex; j++)
        value = parseFloat();
      if (isAnisotropic)
        setVertexAnisotropy(pt);
      addVertexCopy(pt, value, i);
    }
  }
  
  private void getTriangles() throws Exception {
    skipTo("<triangle_array", "count");
    nTriangles = parseInt();
    for (int i = 0; i < nTriangles; i++) {
      skipTo("<triangle", "vertex");
      addTriangleCheck(parseInt() - 1, parseInt() - 1, parseInt() - 1, 7, 0, false, 0);
    }
  }

}
