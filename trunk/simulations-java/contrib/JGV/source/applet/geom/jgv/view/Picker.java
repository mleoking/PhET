//
// Copyright (C) 2002-2004 DaerMar Communications, LLC <jgv@daermar.com>
// @author Daeron Meyer
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package geom.jgv.view;

import geom.geometry.Line;
import geom.geometry.Point;
import geom.jgv.model.Face;
import geom.jgv.model.PickSet;
import geom.jgv.util.HMatrix3D;
import geom.jgv.util.PickLine;
import java.util.Vector;

public class Picker {

  Camera3D cam;
  PickSet picked;
  PickLine pickLine;
  HMatrix3D LT;  // For aligning the face to the pickLine as Z axis.
  HMatrix3D LTinv;

  static double tolerance = 1e-15;
  static double tol2 = 1e-6;
  static double FUDGE2=1.e-12;

  public Picker(PickLine pickLine, Camera3D camera) {
    this.pickLine = pickLine;
    this.cam = camera;
    picked = new PickSet();

    // Construct alignment matrix
    LT = new HMatrix3D();
    HMatrix3D tmpM = new HMatrix3D();
    double a[] = {pickLine.a.x, pickLine.a.y, pickLine.a.z, 1};
    double b[] = {pickLine.b.x, pickLine.b.y, pickLine.b.z, 1};
    double a2[] = {0,0,0,1}; double b2[] = {0,0,0,1};
    LT.translate(-a[0], -a[1], -a[2]);
    LT.htransform(b, b2, 1);

    // Rotate about Y axis
    //tmpM.yrot(-Math.atan2(-b2[2], b2[0]));
    tmpM.rotabout(0, 1, 0, -Math.atan2(b2[0], -b2[2]));
    LT.rmult(tmpM);
    LT.htransform(b, b2, 1);

    // Rotate about X axis
    tmpM.unit();
    //tmpM.xrot(-Math.atan2(-b2[2], b2[1]));
    tmpM.rotabout(1, 0, 0, Math.atan2(b2[1], -b2[2]));
    LT.rmult(tmpM);
    LT.htransform(b, b2, 1);

    // Scale down the line
    tmpM.unit();
    tmpM.scale(-1.0 / b2[2], -1.0 / b2[2], -1.0 / b2[2]);
    LT.rmult(tmpM);

    // Get matrix inverse
    LTinv = new HMatrix3D();
    LTinv.inverseOf(LT);

    LT.htransform(a, a2, 1);
    LT.htransform(b, b2, 1);
    /*
    System.out.println("Original pickLine = " +
                       "(" + a[0] + ", " + a[1] + ", " + a[2] + ") - " +
                       "(" + b[0] + ", " + b[1] + ", " + b[2] + ")");
    System.out.println("Transformed pickLine = " +
                       "(" + a2[0] + ", " + a2[1] + ", " + a2[2] + ") - " +
                       "(" + b2[0] + ", " + b2[1] + ", " + b2[2] + ")");
    */
  }

  /*****
   *
   * This method determines whether the pick line intersects the
   * specified face. If is does, the intersection point and face are
   * added to the picklist.
   */
  public void pickFace(Face f) {
    Point vertices[] = new Point[f.nverts];
    double np[] = {0, 0, 0};
    for (int i = 0; i < f.nverts; i++) {
      Point v = f.vertices[i];
      double p[] = {v.x, v.y, v.z};
      LT.transformd(p, np, 1);
      vertices[i] = new Point(np[0], np[1], np[2]);
    }
    if ((f.nverts == 1) && (f.label != null)) {
      if (labelIntersects(f)) {
        Point ip = findIntersectionPoint(vertices);
        picked.add(f, ip);
      }
    } else if (polyIntersects(vertices)) {
      Point ip = findIntersectionPoint(vertices);
      Point ap = findAnnotationPoint(ip);
      picked.add(f, ip, ap);
        /*
        double p[] = {0, 0, 0};
        LTinv.transformd(p, np, 1);
        Point ip = new Point(np[0], np[1], np[2]);
        picked.add(f, ip);
        */
    }
  }

  private boolean labelIntersects(Face f) {
    boolean retVal = false;
    double p[] = {f.vertices[0].x, f.vertices[0].y, f.vertices[0].z};
    double np[] = {0,0,0};
    cam.O2S.transformd(p, np, 1);
    int xd = f.labelWidth/2;
    int yd = f.labelHeight;
    if (((np[0] - xd - 3) <= pickLine.x) &&
        ((np[0] + xd + 3) >= pickLine.x) &&
        ((np[1] + 3) >= pickLine.y) &&
        ((np[1] - yd - 3) <= pickLine.y)) {
          retVal = true;
    }
    return retVal;
  }

  private boolean polyBBOX(Point vertices[]) {
    boolean retVal = false;
    boolean posx = false, negx = false, posy = false, negy = false;
    for (int i = 0; i < vertices.length; i++) {
      if (vertices[i].x < tolerance) negx = true;
      if (vertices[i].x > -tolerance) posx = true;
      if (vertices[i].y < tolerance) negy = true;
      if (vertices[i].y > -tolerance) posy = true;
    }
    if (posx & negx & posy & negy) retVal = true;
    return retVal;
  }

  private boolean polyBarycentricTest(Point vertices[]) {
    boolean retVal = false;
    double vx0 = vertices[0].x;
    double vy0 = vertices[0].y;
    double u0 = -vx0;
    double v0 = -vy0;
    double u1 = 0, beta = 0, alpha = 0, v1 = 0, u2 = 0, denom = 0;
    int stopvert = vertices.length - 1;

    for (int i = 1; i < stopvert; i++) {
      boolean nextTri = false;
      u1 = vertices[i].x - vx0;
      if ( u1 == 0.0 ) {
        u2 = vertices[i+1].x - vx0;
        if (( u2 == 0.0 ) ||
            (( beta = u0 / u2 ) <= 0.0 ) ||
            ( beta > 1.0) ||
            (( v1 = vertices[i].y - vy0 ) == 0.0 ) ||
            (( alpha = ( v0 - beta *
             ( vertices[i+1].y - vy0 )) / v1 ) < 0.0 )) {
            // missed
            nextTri = true;
        }
      } else {
        u2 = vertices[i+1].x - vx0;
        v1 = vertices[i].y - vy0;
        denom = ( vertices[i+1].y - vy0 ) * u1 - u2 * v1;
        if (( denom == 0.0 ) ||
            (( beta = ( v0 * u1 - u0 * v1 ) / denom ) <= 0.0 ) ||
             (beta > 1.0 ) ||
             (( alpha = (u0 - beta * u2 ) / u1 ) < 0.0 )) {
          // missed
          nextTri = true;
        }
      }
      if (!nextTri && ( alpha + beta <= 1.0 )) {
        retVal = !retVal;
      }
    }
    return retVal;
  }

  private boolean lineHitTest(Point vertices[]) {
    boolean retVal = false;
    double shortest = 10000000;
    if (vertices.length == 2) {
      double ddh = (vertices[1].x - vertices[0].x) * (vertices[1].x - vertices[0].x) +
                   (vertices[1].y - vertices[0].y) * (vertices[1].y - vertices[0].y);
      double dd0 = vertices[0].x * vertices[0].x + vertices[0].y * vertices[0].y;
      double dd1 = vertices[1].x * vertices[1].x + vertices[1].y * vertices[1].y;

      if (dd1 > ddh + dd0) {
        shortest = dd0;
      } else if (dd0 > ddh + dd1) {
        shortest = dd1;
      } else {
        double a = vertices[0].y - vertices[1].y;
        double b = vertices[1].x - vertices[0].x;
        double c = vertices[0].x * vertices[1].y - vertices[1].x * vertices[0].y;
        shortest = (c * c) / (a * a + b * b);
      }
    }
    if (shortest < tol2) {
      retVal = true;
    }
    //System.out.println("shortest " + shortest);
    return retVal;
  }

  private boolean polyIntersects(Point vertices[]) {
    boolean retVal = false;
    if ((vertices.length == 2) || polyBBOX(vertices)) {
      if (vertices.length == 1) {
        // If it's a point the BBOX test is good enough
        retVal = true;
      } else if (vertices.length == 2) {
        // Otherwise, it's a line segment
        retVal = lineHitTest(vertices);
      } else {
        retVal = polyBarycentricTest(vertices);
      }
    }
    return retVal;
  }

  // find the "annotation" point for the given intersection point;
  private Point findAnnotationPoint(Point p) {
    double ip[] = {p.x, p.y, p.z};
    double op[] = {0, 0, 0};
    LT.transformd(ip, op, 1);
    op[2] += .003; // Annotation Z offset
    LTinv.transformd(op, ip, 1);
    Point ap = new Point(ip[0], ip[1], ip[2]);
    return ap;
  }

  private Point findIntersectionPoint(Point vertices[]) {
    Point p = null;
    if (vertices.length == 1) {
      p = new Point(vertices[0]);
    } else if (vertices.length == 2) {
      p = lineIntersectionPoint(vertices[0], vertices[1]);
    } else { // vertices.length > 2
      p = polyIntersectionPoint(vertices);
    }

    double ip[] = {p.x, p.y, p.z};
    double op[] = {0, 0, 0};
    LTinv.transformd(ip, op, 1);
    p = new Point(op[0], op[1], op[2]);
    return p;
  }

  private Point lineIntersectionPoint(Point v1, Point v2) {
    Point p = new Point(0, 0, 0);
    double denomx = v2.x - v1.x; double dx = Math.abs(denomx);
    double denomy = v2.y - v1.y; double dy = Math.abs(denomy);
    double denomz = v2.z - v1.z; double dz = Math.abs(denomz);
    double t = 0;
    if ((dx >= dy ) && (dx > tolerance)) {
      t = -v1.x / denomx;
    } else if (dy > tolerance) {
      t = -v1.y / denomy;
    }
    // Guard against points off the end of the line segment
    // This may happen when our pick line almost exactly matches
    // the line segment (i.e. when *all* points on the line segment
    // are also on the pick line)
    if (t < 0.0) { t = 0.0; }
    if (t > 1.0) { t = 1.0; }

    p.z = v1.z + t * (v2.z - v1.z);
    p.y = v1.y + t * (v2.y - v1.y);
    p.x = v1.x + t * (v2.x - v1.x);
    return p;
  }

  private Point polyIntersectionPoint(Point vertices[]) {
    Point p = null;
    Point origin = new Point(0, 0, 0);
    int bi = 0, ci = 0;
    double pz = 0, pw = 0;

    for (bi = 1; bi < vertices.length && vertices[bi].equals(vertices[0]); bi++);

    if (bi >= vertices.length) {
        return lineIntersectionPoint(vertices[0], vertices[1]);
    }


    for (ci = bi + 1; ci < vertices.length; ci++) {
      pz = (vertices[0].x * (vertices[bi].y - vertices[ci].y)
          - vertices[0].y * (vertices[bi].x - vertices[ci].x)
          + (vertices[bi].x * vertices[ci].y - vertices[bi].y * vertices[ci].x));
      if (pz * pz > FUDGE2) break;
    }
    if (ci >= vertices.length) {
      return lineIntersectionPoint(vertices[0], vertices[bi]);
    }

    pw = (vertices[0].x * (vertices[bi].z*vertices[ci].y - vertices[bi].y * vertices[ci].z)
        - vertices[0].y * (vertices[bi].z*vertices[ci].x - vertices[bi].x*vertices[ci].z)
        + vertices[0].z * (vertices[bi].y*vertices[ci].x - vertices[bi].x*vertices[ci].y));
    p = new Point(origin);
    p.z = -pw / pz;
    // System.out.println("Found Z at " + p.z);
    return p;
  }

  public PickSet getPickSet() {
    return picked;
  }
}