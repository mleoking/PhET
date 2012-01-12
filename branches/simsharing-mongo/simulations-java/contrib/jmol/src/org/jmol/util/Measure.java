/* $RCSfile$
 * $Author: egonw $
 * $Date: 2005-11-10 09:52:44 -0600 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
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
package org.jmol.util;


import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.viewer.JmolConstants;
import org.jmol.modelset.Atom;
import org.jmol.script.Token;

final public class Measure {

  public final static float radiansPerDegree = (float) (2 * Math.PI / 360);
  
  public static float computeAngle(Tuple3f pointA, Tuple3f pointB, Tuple3f pointC, Vector3f vectorBA, Vector3f vectorBC, boolean asDegrees) {
    vectorBA.sub(pointA, pointB);
    vectorBC.sub(pointC, pointB);
    float angle = vectorBA.angle(vectorBC);
    return (asDegrees ? angle / radiansPerDegree : angle);
  }

  public static float computeAngle(Tuple3f pointA, Tuple3f pointB, Tuple3f pointC, boolean asDegrees) {
    Vector3f vectorBA = new Vector3f();
    Vector3f vectorBC = new Vector3f();        
    return computeAngle(pointA, pointB, pointC, vectorBA, vectorBC, asDegrees);
  }

  public static float computeTorsion(Tuple3f p1, Tuple3f p2, Tuple3f p3, Tuple3f p4, boolean asDegrees) {
  
    float ijx = p1.x - p2.x;
    float ijy = p1.y - p2.y;
    float ijz = p1.z - p2.z;
  
    float kjx = p3.x - p2.x;
    float kjy = p3.y - p2.y;
    float kjz = p3.z - p2.z;
  
    float klx = p3.x - p4.x;
    float kly = p3.y - p4.y;
    float klz = p3.z - p4.z;
  
    float ax = ijy * kjz - ijz * kjy;
    float ay = ijz * kjx - ijx * kjz;
    float az = ijx * kjy - ijy * kjx;
    float cx = kjy * klz - kjz * kly;
    float cy = kjz * klx - kjx * klz;
    float cz = kjx * kly - kjy * klx;
  
    float ai2 = 1f / (ax * ax + ay * ay + az * az);
    float ci2 = 1f / (cx * cx + cy * cy + cz * cz);
  
    float ai = (float) Math.sqrt(ai2);
    float ci = (float) Math.sqrt(ci2);
    float denom = ai * ci;
    float cross = ax * cx + ay * cy + az * cz;
    float cosang = cross * denom;
    if (cosang > 1) {
      cosang = 1;
    }
    if (cosang < -1) {
      cosang = -1;
    }
  
    float torsion = (float) Math.acos(cosang);
    float dot = ijx * cx + ijy * cy + ijz * cz;
    float absDot = Math.abs(dot);
    torsion = (dot / absDot > 0) ? torsion : -torsion;
    return (asDegrees ? torsion / radiansPerDegree : torsion);
  }

  public static Object computeHelicalAxis(String id, int tokType, Point3f a, Point3f b,
                                    Quaternion dq) {
    /*
                b
           |   /|
           |  / |
           | /  |
           |/   c
         b'+   / \
           |  /   \      Vcb = Vab . n
         n | /     \d    Vda = (Vcb - Vab) / 2
           |/theta  \
         a'+---------a
                r 
    */
    
    Vector3f vab = new Vector3f();
    vab.sub(b, a);
    /*
     * testing here to see if directing the normal makes any difference -- oddly
     * enough, it does not. When n = -n and theta = -theta vab.n is reversed,
     * and that magnitude is multiplied by n in generating the A'-B' vector.
     * 
     * a negative angle implies a left-handed axis (sheets)
     */
    float theta = dq.getTheta();
    Vector3f n = dq.getNormal();
    float v_dot_n = vab.dot(n);
    if (Math.abs(v_dot_n) < 0.0001f)
      v_dot_n = 0;
    if (tokType == Token.axis) {
      if (v_dot_n != 0)
        n.scale(v_dot_n);
      return n;
    }
    Vector3f va_prime_d = new Vector3f();
    va_prime_d.cross(vab, n);
    if (va_prime_d.dot(va_prime_d) != 0)
      va_prime_d.normalize();
    Vector3f vda = new Vector3f();
    Vector3f vcb = new Vector3f(n);
    if (v_dot_n == 0)
      v_dot_n = Float.MIN_VALUE; // allow for perpendicular axis to vab
    vcb.scale(v_dot_n);
    vda.sub(vcb, vab);
    vda.scale(0.5f);
    va_prime_d.scale(theta == 0 ? 0 : (float) (vda.length() / Math
        .tan(theta / 2 / 180 * Math.PI)));
    Vector3f r = new Vector3f(va_prime_d);
    if (theta != 0)
      r.add(vda);
    if (tokType == Token.radius)
      return r;
    Point3f pt_a_prime = new Point3f(a);
    pt_a_prime.sub(r);
    if (tokType == Token.point) {
      return pt_a_prime;
    }
    if (v_dot_n != Float.MIN_VALUE)
      n.scale(v_dot_n);
    // must calculate directed angle:
    Point3f pt_b_prime = new Point3f(pt_a_prime);
    pt_b_prime.add(n);
    theta = computeTorsion(a, pt_a_prime, pt_b_prime, b, true);
    if (Float.isNaN(theta) || r.length() < 0.0001f)
      theta = dq.getThetaDirected(n); // allow for r = 0
    if (tokType == Token.angle)
      return new Float(theta);
    /*
    System.out.println("draw ID test VECTOR " + Escape.escape(pt_a_prime)
          + " " + Escape.escape(n) + " color "
          + (theta < 0 ? "{255.0 200.0 0.0}" : "{255.0 0.0 128.0};")
          +"measure " + Escape.escape(a) + Escape.escape(pt_a_prime) + Escape.escape(pt_b_prime) + Escape.escape(b));
    */
    if (tokType == Token.draw)
      return "draw ID " + id + " VECTOR " + Escape.escape(pt_a_prime)
          + " " + Escape.escape(n) + " color "
          + (theta < 0 ? "{255.0 200.0 0.0}" : "{255.0 0.0 128.0}");
    if (tokType == Token.measure)
      return "measure " + Escape.escape(a) + Escape.escape(pt_a_prime) + Escape.escape(pt_b_prime) + Escape.escape(b);
    // for now... array:
    float residuesPerTurn = Math.abs(theta == 0 ? 0 : 360f / theta);
    float pitch = Math.abs(v_dot_n == Float.MIN_VALUE ? 0 : n.length() * (theta == 0 ? 1 : 360f / theta));
    switch (tokType) {
    case Token.array:
      return new Object[] {pt_a_prime, n, r,  new Point3f(theta, pitch, residuesPerTurn)};
    case Token.list:
      return new String[] { 
          Escape.escape(pt_a_prime), // a' 
          Escape.escape(n), // n
          Escape.escape(r), // r
          Escape.escape(new Point3f(theta /*(degrees)*/,pitch, residuesPerTurn))
          };
    default:
      return null;
    }
  }

  public static void getPlaneThroughPoints(Point3f pointA,
                                              Point3f pointB,
                                              Point3f pointC, Vector3f vNorm,
                                              Vector3f vAB, Vector3f vAC, Point4f plane) {
    float w = getNormalThroughPoints(pointA, pointB, pointC, vNorm, vAB, vAC);
    plane.set(vNorm.x, vNorm.y, vNorm.z, w);
  }
  
  public static void getPlaneThroughPoint(Point3f pt, Vector3f normal, Point4f plane) {
    plane.set(normal.x, normal.y, normal.z, -normal.dot(new Vector3f(pt)));
  }
  
  public static float distanceToPlane(Point4f plane, Point3f pt) {
    return (plane == null ? Float.NaN 
        : (plane.x * pt.x + plane.y * pt.y + plane.z * pt.z + plane.w)
        / (float) Math.sqrt(plane.x * plane.x + plane.y * plane.y + plane.z
            * plane.z));
  }

  public static float distanceToPlane(Point4f plane, float d, Point3f pt) {
    return (plane == null ? Float.NaN : (plane.x * pt.x + plane.y
        * pt.y + plane.z * pt.z + plane.w) / d);
  }

  public static float distanceToPlane(Vector3f norm, float w, Point3f pt) {
    return (norm == null ? Float.NaN 
        : (norm.x * pt.x + norm.y * pt.y + norm.z * pt.z + w)
        / (float) Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z
            * norm.z));
  }

  public static void calcNormalizedNormal(Point3f pointA, Point3f pointB,
         Point3f pointC, Vector3f vNormNorm, Vector3f vAB, Vector3f vAC) {
    vAB.sub(pointB, pointA);
    vAC.sub(pointC, pointA);
    vNormNorm.cross(vAB, vAC);
    vNormNorm.normalize();
  }

  public static float getDirectedNormalThroughPoints(Point3f pointA, 
         Point3f pointB, Point3f pointC, Point3f ptRef, Vector3f vNorm, 
         Vector3f vAB, Vector3f vAC) {
    // for x = plane({atomno=1}, {atomno=2}, {atomno=3}, {atomno=4})
    float nd = getNormalThroughPoints(pointA, pointB, pointC, vNorm, vAB, vAC);
    if (ptRef != null) {
      Point3f pt0 = new Point3f(pointA);
      pt0.add(vNorm);
      float d = pt0.distance(ptRef);
      pt0.set(pointA);
      pt0.sub(vNorm);
      if (d > pt0.distance(ptRef)) {
        vNorm.scale(-1);
        nd = -nd;
      }
    }
    return nd;
  }
  
  public static float getNormalThroughPoints(Point3f pointA, Point3f pointB,
                                   Point3f pointC, Vector3f vNorm, Vector3f vAB, Vector3f vAC) {
    // for Polyhedra
    calcNormalizedNormal(pointA, pointB, pointC, vNorm, vAB, vAC);
    // ax + by + cz + d = 0
    // so if a point is in the plane, then N dot X = -d
    vAB.set(pointA);
    return -vAB.dot(vNorm);
  }

  public static void getPlaneProjection(Point3f pt, Point4f plane, Point3f ptProj, Vector3f vNorm) {
    float dist = distanceToPlane(plane, pt);
    vNorm.set(plane.x, plane.y, plane.z);
    vNorm.normalize();
    vNorm.scale(-dist);
    ptProj.set(pt);
    ptProj.add(vNorm);
  }

  /**
   * 
   * @param ptCenter
   * @param ptA
   * @param ptB
   * @param ptC
   * @param isOutward
   * @param normal
   * @return        true if winding is proper; false if not
   */
  public static boolean getNormalFromCenter(Point3f ptCenter, Point3f ptA, Point3f ptB,
                            Point3f ptC, boolean isOutward, Vector3f normal) {
    // for Polyhedra
    Vector3f vAB = new Vector3f();
    Vector3f vAC = new Vector3f();
    float d = getNormalThroughPoints(ptA, ptB, ptC, normal, vAB, vAC);
    boolean isReversed = (distanceToPlane(normal, d, ptCenter) > 0);
    if (isReversed == isOutward)
      normal.scale(-1f);
    //System.out.println("Draw v vector scale 2.0 " + Escape.escape(ptCenter) + Escape.escape(normal));
    return !isReversed;
  }

  public static void calcXYNormalToLine(Point3f pointA, Point3f pointB,
                                   Vector3f vNormNorm) {
    // vector in xy plane perpendicular to a line between two points RMH
    Vector3f axis = new Vector3f(pointA);
    axis.sub(pointB);
    vNormNorm.cross(axis, JmolConstants.axisY);
    vNormNorm.normalize();
    if (Float.isNaN(vNormNorm.x))
      vNormNorm.set(1, 0, 0);
  }
  
  public static void projectOntoAxis(Point3f point, Point3f axisA,
                                     Vector3f axisUnitVector,
                                     Vector3f vectorProjection) {
    vectorProjection.sub(point, axisA);
    float projectedLength = vectorProjection.dot(axisUnitVector);
    point.set(axisUnitVector);
    point.scaleAdd(projectedLength, axisA);
    vectorProjection.sub(point, axisA);
  }
  
  public static void calcBestAxisThroughPoints(Point3f[] points, Point3f axisA,
                                               Vector3f axisUnitVector,
                                               Vector3f vectorProjection,
                                               int nTriesMax) {
    // just a crude starting point.

    int nPoints = points.length;
    axisA.set(points[0]);
    axisUnitVector.sub(points[nPoints - 1], axisA);
    axisUnitVector.normalize();

    /*
     * We now calculate the least-squares 3D axis
     * through the helix alpha carbons starting with Vo
     * as a first approximation.
     * 
     * This uses the simple 0-centered least squares fit:
     * 
     * Y = M cross Xi
     * 
     * minimizing R^2 = SUM(|Y - Yi|^2) 
     * 
     * where Yi is the vector PERPENDICULAR of the point onto axis Vo
     * and Xi is the vector PROJECTION of the point onto axis Vo
     * and M is a vector adjustment 
     * 
     * M = SUM_(Xi cross Yi) / sum(|Xi|^2)
     * 
     * from which we arrive at:
     * 
     * V = Vo + (M cross Vo)
     * 
     * Basically, this is just a 3D version of a 
     * standard 2D least squares fit to a line, where we would say:
     * 
     * y = m xi + b
     * 
     * D = n (sum xi^2) - (sum xi)^2
     * 
     * m = [(n sum xiyi) - (sum xi)(sum yi)] / D
     * b = [(sum yi) (sum xi^2) - (sum xi)(sum xiyi)] / D
     * 
     * but here we demand that the line go through the center, so we
     * require (sum xi) = (sum yi) = 0, so b = 0 and
     * 
     * m = (sum xiyi) / (sum xi^2)
     * 
     * In 3D we do the same but 
     * instead of x we have Vo,
     * instead of multiplication we use cross products
     * 
     * A bit of iteration is necessary.
     * 
     * Bob Hanson 11/2006
     * 
     */

    calcAveragePointN(points, nPoints, axisA);

    int nTries = 0;
    while (nTries++ < nTriesMax
        && findAxis(points, nPoints, axisA, axisUnitVector, vectorProjection) > 0.001) {
    }

    /*
     * Iteration here gets the job done.
     * We now find the projections of the endpoints onto the axis
     * 
     */

    Point3f tempA = new Point3f(points[0]);
    projectOntoAxis(tempA, axisA, axisUnitVector, vectorProjection);
    axisA.set(tempA);
  }

  public static float findAxis(Point3f[] points, int nPoints, Point3f axisA,
                        Vector3f axisUnitVector, Vector3f vectorProjection) {
    Vector3f sumXiYi = new Vector3f();
    Vector3f vTemp = new Vector3f();
    Point3f pt = new Point3f();
    Point3f ptProj = new Point3f();
    Vector3f a = new Vector3f(axisUnitVector);

    float sum_Xi2 = 0;
    float sum_Yi2 = 0;
    for (int i = nPoints; --i >= 0;) {
      pt.set(points[i]);
      ptProj.set(pt);
      projectOntoAxis(ptProj, axisA, axisUnitVector,
          vectorProjection);
      vTemp.sub(pt, ptProj);
      sum_Yi2 += vTemp.lengthSquared();
      vTemp.cross(vectorProjection, vTemp);
      sumXiYi.add(vTemp);
      sum_Xi2 += vectorProjection.lengthSquared();
    }
    Vector3f m = new Vector3f(sumXiYi);
    m.scale(1 / sum_Xi2);
    vTemp.cross(m, axisUnitVector);
    axisUnitVector.add(vTemp);
    axisUnitVector.normalize();
    //check for change in direction by measuring vector difference length
    vTemp.set(axisUnitVector);
    vTemp.sub(a);
    return vTemp.length();
  }
  
  
  public static void calcAveragePoint(Point3f pointA, Point3f pointB,
                                      Point3f pointC) {
    pointC.set((pointA.x + pointB.x) / 2, (pointA.y + pointB.y) / 2,
        (pointA.z + pointB.z) / 2);
  }
  
  public static void calcAveragePointN(Point3f[] points, int nPoints,
                                Point3f averagePoint) {
    averagePoint.set(points[0]);
    for (int i = 1; i < nPoints; i++)
      averagePoint.add(points[i]);
    averagePoint.scale(1f / nPoints);
  }

  public static Point3f[] getCenterAndPoints(List<Point3f> vPts) {
    int n = vPts.size();
    Point3f[] pts = new Point3f[n + 1];
    pts[0] = new Point3f();
    if (n > 0) {
      for (int i = 0; i < n; i++) {
        pts[0].add(pts[i + 1] = vPts.get(i));
      }
      pts[0].scale(1f / n);
    }
    return pts;
  }

  public static float getTransformMatrix4(List<Point3f> ptsA, List<Point3f> ptsB, Matrix4f m, Point3f centerA) {
    Point3f[] cptsA = getCenterAndPoints(ptsA);
    Point3f[] cptsB = getCenterAndPoints(ptsB);
    float[] retStddev = new float[2];
    Quaternion q = calculateQuaternionRotation(new Point3f[][] { cptsA,
        cptsB }, retStddev, false);
    Vector3f v = new Vector3f(cptsB[0]);
    v.sub(cptsA[0]);
    m.set(q.getMatrix(), v, 1);
    if (centerA != null)
      centerA.set(cptsA[0]);
    return retStddev[1];
  }
  
  public static Quaternion calculateQuaternionRotation(
                                                       Point3f[][] centerAndPoints,
                                                       float[] retStddev,
                                                       boolean doReport) {

    retStddev[1] = Float.NaN;
    Quaternion q = new Quaternion();
    if (centerAndPoints[0].length == 1
        || centerAndPoints[0].length != centerAndPoints[1].length)
      return q;

    /*
     * see Berthold K. P. Horn,
     * "Closed-form solution of absolute orientation using unit quaternions" J.
     * Opt. Soc. Amer. A, 1987, Vol. 4, pp. 629-642
     * http://www.opticsinfobase.org/viewmedia.cfm?uri=josaa-4-4-629&seq=0
     * 
     * 
     * A similar treatment was developed independently (and later!) 
     * by G. Kramer, in G. R. Kramer,
     * "Superposition of Molecular Structures Using Quaternions"
     * Molecular Simulation, 1991, Vol. 7, pp. 113-119. 
     * 
     *  In that treatment there is a lot of unnecessary calculation 
     *  along the trace of matrix M (eqn 20). 
     *  I'm not sure why the extra x^2 + y^2 + z^2 + x'^2 + y'^2 + z'^2
     *  is in there, but they are unnecessary and only contribute to larger
     *  numerical averaging errors an additional processing time, as far as
     *  I can tell. Adding aI, where a is a scalar and I is the 4x4 identity
     *  just offsets the eigenvalues but doesn't change the eigenvectors.
     * 
     * and Lydia E. Kavraki, "Molecular Distance Measures"
     * http://cnx.org/content/m11608/latest/
     * 
     */

    int n = centerAndPoints[0].length - 1;
    if (doReport)
      for (int i = 1; i <= n; i++) {
        Point3f aij = centerAndPoints[0][i];
        Point3f bij = centerAndPoints[1][i];
        if (aij instanceof Atom)
          Logger.info(" atom 1 " + ((Atom) aij).getInfo() + "\tatom 2 "
              + ((Atom) bij).getInfo());
        else
          break;
      }

    if (n < 2)
      return q;

    double Sxx = 0, Sxy = 0, Sxz = 0, Syx = 0, Syy = 0, Syz = 0, Szx = 0, Szy = 0, Szz = 0;
    for (int i = n + 1; --i >= 1;) {
      Point3f aij = centerAndPoints[0][i];
      Point3f bij = centerAndPoints[1][i];
      Point3f ptA = new Point3f(aij);
      ptA.sub(centerAndPoints[0][0]);
      Point3f ptB = new Point3f(bij);
      ptB.sub(centerAndPoints[0][1]);
      Sxx += (double) ptA.x * (double) ptB.x;
      Sxy += (double) ptA.x * (double) ptB.y;
      Sxz += (double) ptA.x * (double) ptB.z;
      Syx += (double) ptA.y * (double) ptB.x;
      Syy += (double) ptA.y * (double) ptB.y;
      Syz += (double) ptA.y * (double) ptB.z;
      Szx += (double) ptA.z * (double) ptB.x;
      Szy += (double) ptA.z * (double) ptB.y;
      Szz += (double) ptA.z * (double) ptB.z;
    }
    retStddev[0] = getRmsd(centerAndPoints, q);
    double[][] N = new double[4][4];
    N[0][0] = Sxx + Syy + Szz;
    N[0][1] = N[1][0] = Syz - Szy;
    N[0][2] = N[2][0] = Szx - Sxz;
    N[0][3] = N[3][0] = Sxy - Syx;

    N[1][1] = Sxx - Syy - Szz;
    N[1][2] = N[2][1] = Sxy + Syx;
    N[1][3] = N[3][1] = Szx + Sxz;

    N[2][2] = -Sxx + Syy - Szz;
    N[2][3] = N[3][2] = Syz + Szy;

    N[3][3] = -Sxx - Syy + Szz;

    Eigen eigen = new Eigen(N);

    float[] v = eigen.getEigenvectorsFloatTransposed()[3];
    q = new Quaternion(new Point4f(v[1], v[2], v[3], v[0]));
    retStddev[1] = getRmsd(centerAndPoints, q);
    // System.out.println("Measure" + q.getInfo());
    return q;
  }

  public static float getRmsd(Point3f[][] centerAndPoints, Quaternion q) {
    double sum = 0;
    double sum2 = 0;
    int n = centerAndPoints[0].length - 1;
    Point3f ptAnew = new Point3f();
    for (int i = n + 1; --i >= 1;) {
      ptAnew.set(centerAndPoints[0][i]);
      ptAnew.sub(centerAndPoints[0][0]);
      q.transform(ptAnew, ptAnew);
      ptAnew.add(centerAndPoints[1][0]);
      double d = ptAnew.distance(centerAndPoints[1][i]);
      sum += d;
      sum2 += d * d;
    }
    return (float) Math.sqrt((sum2 - sum * sum / n) / (n - 1));
  }

  public static List<Point3f> transformPoints(List<Point3f> vPts, Matrix4f m4, Point3f center) {
    List<Point3f> v = new ArrayList<Point3f>();
    for (int i = 0; i < vPts.size(); i++) {
      Point3f pt = new Point3f(vPts.get(i));
      pt.sub(center);
      m4.transform(pt, pt);
      pt.add(center);
      v.add(pt);
    }
    return v;
  }

  public static boolean isInTetrahedron(Point3f pt, Point3f ptA, Point3f ptB,
                                        Point3f ptC, Point3f ptD,
                                        Point4f plane, Vector3f vTemp,
                                        Vector3f vTemp2, Vector3f vTemp3, boolean fullyEnclosed) {
    getPlaneThroughPoints(ptC, ptD, ptA, vTemp, vTemp2, vTemp3, plane);
    boolean b = (distanceToPlane(plane, pt) >= 0);
    getPlaneThroughPoints(ptA, ptD, ptB, vTemp, vTemp2, vTemp3, plane);
    if (b != (distanceToPlane(plane, pt) >= 0))
      return false;
    getPlaneThroughPoints(ptB, ptD, ptC, vTemp, vTemp2, vTemp3, plane);
    if (b != (distanceToPlane(plane, pt) >= 0))
      return false;
    getPlaneThroughPoints(ptA, ptB, ptC, vTemp, vTemp2, vTemp3, plane);
    float d = distanceToPlane(plane, pt);
    if (fullyEnclosed)
      return (b == (d >= 0));
    float d1 = distanceToPlane(plane, ptD);
    return d1 * d <= 0 || Math.abs(d1) > Math.abs(d);
  }


  /**
   * 
   * @param plane1
   * @param plane2
   * @return       [ point, vector ] or []
   */
  public static List<Object> getIntersection(Point4f plane1, Point4f plane2) {
    float a1 = plane1.x;
    float b1 = plane1.y;
    float c1 = plane1.z;
    float d1 = plane1.w;
    float a2 = plane2.x;
    float b2 = plane2.y;
    float c2 = plane2.z;
    float d2 = plane2.w;
    Vector3f norm1 = new Vector3f(a1, b1, c1);
    Vector3f norm2 = new Vector3f(a2, b2, c2);
    Vector3f nxn = new Vector3f();
    nxn.cross(norm1, norm2);
    float ax = Math.abs(nxn.x);
    float ay = Math.abs(nxn.y);
    float az = Math.abs(nxn.z);
    float x, y, z, diff;
    int type = (ax > ay ? (ax > az ? 1 : 3) : ay > az ? 2 : 3);
    switch(type) {
    case 1:
      x = 0;
      diff = (b1 * c2 - b2 * c1);
      if (Math.abs(diff) < 0.01) return null;
      y = (c1 * d2 - c2 * d1) / diff;
      z = (b2 * d1 - d2 * b1) / diff;
      break;
    case 2:
      diff = (a1 * c2 - a2 * c1);
      if (Math.abs(diff) < 0.01) return null;
      x = (c1 * d2 - c2 * d1) / diff;
      y = 0;
      z = (a2 * d1 - d2 * a1) / diff;
      break;
    case 3:
    default:
      diff = (a1 * b2 - a2 * b1);
      if (Math.abs(diff) < 0.01) return null;
      x = (b1 * d2 - b2 * d1) / diff;
      y = (a2 * d1 - d2 * a1) / diff;
      z = 0;
    }
    List<Object>list = new ArrayList<Object>();
    list.add(new Point3f(x, y, z));
    list.add(nxn);
    return list;
  }

  /**
   * 
   * @param pt1  point on line
   * @param v    unit vector of line
   * @param plane 
   * @return      point of intersection of line with plane
   */
  public static Point3f getIntersection(Point3f pt1, Vector3f v,
                                               Point4f plane) {
    Vector3f norm = new Vector3f();
    Point3f pt = new Point3f();
    getPlaneProjection(pt1, plane, pt, norm);
    norm.set(plane.x, plane.y, plane.z);
    norm.normalize();
    if (v == null)
      v = new Vector3f(norm);
    float l_dot_n = v.dot(norm);
    if (Math.abs(l_dot_n) < 0.01) return null;
    Vector3f perp = new Vector3f(pt);
    perp.sub(pt1);
    pt.scaleAdd(perp.dot(norm) / l_dot_n, v, pt1);
    return pt;
  }
}
