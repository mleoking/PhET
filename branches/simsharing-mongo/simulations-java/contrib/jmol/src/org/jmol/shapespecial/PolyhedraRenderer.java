/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-03-12 11:05:36 -0500 (Mon, 12 Mar 2007) $
 * $Revision: 7077 $
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
package org.jmol.shapespecial;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.shape.ShapeRenderer;

public class PolyhedraRenderer extends ShapeRenderer {

  private int drawEdges;
  private boolean isAll;
  private boolean frontOnly;

  @Override
  protected void render() {
    Polyhedra polyhedra = (Polyhedra) shape;
    Polyhedra.Polyhedron[] polyhedrons = polyhedra.polyhedrons;
    drawEdges = polyhedra.drawEdges;
    short[] colixes = polyhedra.colixes;
    Point3i[] screens = null;
    for (int i = polyhedra.polyhedronCount; --i >= 0;) {
      int iAtom = polyhedrons[i].centralAtom.getIndex();
      short colix = (colixes == null || iAtom >= colixes.length ? 
          Graphics3D.INHERIT_ALL : polyhedra.colixes[iAtom]);
      screens = render1(polyhedrons[i], colix, screens);
    }
  }

  private Point3i[] render1(Polyhedra.Polyhedron p, short colix, Point3i[] screens) {
    if (p.visibilityFlags == 0)
      return screens;
    colix = Graphics3D.getColixInherited(colix, p.centralAtom.getColix());
    Point3f[] vertices = p.vertices;
    byte[] planes;
    if (screens == null || screens.length < vertices.length) {
      screens = new Point3i[vertices.length];
      for (int i = vertices.length; --i >= 0;)
        screens[i] = new Point3i();
    }
    planes = p.planes;
    for (int i = vertices.length; --i >= 0;) {
      Atom atom = (vertices[i] instanceof Atom ? (Atom) vertices[i] : null);
      if (atom == null)
        viewer.transformPoint(vertices[i], screens[i]);
      else
        screens[i].set(atom.screenX, atom.screenY, atom.screenZ);
    }

    isAll = (drawEdges == Polyhedra.EDGES_ALL);
    frontOnly = (drawEdges == Polyhedra.EDGES_FRONT);

    // no edges to new points when not collapsed
    if (g3d.setColix(colix))
      for (int i = 0, j = 0; j < planes.length;)
        fillFace(p.normixes[i++], screens[planes[j++]], screens[planes[j++]],
            screens[planes[j++]]);
    if (g3d.setColix(Graphics3D.getColixTranslucent(colix, false, 0)))
    for (int i = 0, j = 0; j < planes.length;)
      drawFace(p.normixes[i++], screens[planes[j++]],
          screens[planes[j++]], screens[planes[j++]]);
    return screens;
  }

  private void drawFace(short normix, Point3i A, Point3i B, Point3i C) {
    if (isAll || frontOnly && g3d.isDirectedTowardsCamera(normix)) {
      drawCylinderTriangle(A.x, A.y, A.z, B.x, B.y, B.z, C.x, C.y, C.z);
    }
  }

  private void drawCylinderTriangle(int xA, int yA, int zA, int xB, int yB,
                                   int zB, int xC, int yC, int zC) {
    
    g3d.fillCylinderScreen(Graphics3D.ENDCAPS_SPHERICAL, 3, xA, yA, zA, xB, yB, zB);
    g3d.fillCylinderScreen(Graphics3D.ENDCAPS_SPHERICAL, 3, xB, yB, zB, xC, yC, zC);
    g3d.fillCylinderScreen(Graphics3D.ENDCAPS_SPHERICAL, 3, xA, yA, zA, xC, yC, zC);
  }

  private void fillFace(short normix, Point3i A, Point3i B, Point3i C) {
    g3d.fillTriangleTwoSided(normix, A.x, A.y, A.z, B.x, B.y, B.z, C.x, C.y, C.z);
  }
}
