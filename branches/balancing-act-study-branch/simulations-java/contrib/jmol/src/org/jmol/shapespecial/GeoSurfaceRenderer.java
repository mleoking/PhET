/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-03-19 12:18:17 -0500 (Mon, 19 Mar 2007) $
 * $Revision: 7171 $
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

package org.jmol.shapespecial;

import org.jmol.g3d.Graphics3D;
import org.jmol.geodesic.Geodesic;
import org.jmol.util.FastBitSet;

import javax.vecmath.Point3i;

/*
 * A simple geodesic surface renderer that turns into just dots
 * if the model is rotated.
 *
 * Bob Hanson, 3/2007
 * 
 * see org.jmol.viewer.DotsRenderer
 */

public class GeoSurfaceRenderer extends DotsRenderer {
  
  @Override
  protected void render() {
    GeoSurface gs = (GeoSurface) shape;
    iShowSolid = !(viewer.getInMotion() && gs.ec.getDotsConvexMax() > 100);
    if (!iShowSolid && !g3d.setColix(Graphics3D.BLACK))
      return;
    if (iShowSolid && faceMap == null)
      faceMap = new int[screenDotCount];
    //testRadiusAdjust = -1.2f;
    render1(gs);
  }
  
 @Override
protected void renderConvex(short colix, FastBitSet visibilityMap, int nPoints) {
    this.colix = colix;
    if (iShowSolid) {
      if (g3d.setColix(colix))       
        renderSurface(visibilityMap);
      return;
    }
    renderDots(nPoints);
  }
  
  private Point3i facePt1 = new Point3i();
  private Point3i facePt2 = new Point3i();
  private Point3i facePt3 = new Point3i();
  
  private void renderSurface(FastBitSet points) {
    if (faceMap == null)
      return;
    short[] faces = Geodesic.getFaceVertexes(screenLevel);
    int[] coords = screenCoordinates;
    short p1, p2, p3;
    int mapMax = points.size();
    //Logger.debug("geod frag "+mapMax+" "+dotCount);
    if (screenDotCount < mapMax)
      mapMax = screenDotCount;
    for (int f = 0; f < faces.length;) {
      p1 = faces[f++];
      p2 = faces[f++];
      p3 = faces[f++];
      if (p1 >= mapMax || p2 >= mapMax || p3 >= mapMax)
        continue;
      //Logger.debug("geod frag "+p1+" "+p2+" "+p3+" "+dotCount);
      if (!points.get(p1) || !points.get(p2)
          || !points.get(p3))
        continue;
      facePt1.set(coords[faceMap[p1]], coords[faceMap[p1] + 1], coords[faceMap[p1] + 2]);
      facePt2.set(coords[faceMap[p2]], coords[faceMap[p2] + 1], coords[faceMap[p2] + 2]);
      facePt3.set(coords[faceMap[p3]], coords[faceMap[p3] + 1], coords[faceMap[p3] + 2]);
//      g3d.setNoisySurfaceShade(facePt1, facePt2, facePt3);
      g3d.fillTriangle(facePt1, colix, p1, facePt2, colix, p2, facePt3, colix, p3);
    }
  }  
}

