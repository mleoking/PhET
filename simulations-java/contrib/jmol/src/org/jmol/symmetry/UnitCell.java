/* $RCSfile$
 * $Author: egonw $
 * $Date: 2005-11-10 09:52:44 -0600 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
 *
 * Copyright (C) 2003-2005  Miguel, Jmol Development, www.jmol.org
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
 *  Lesser General License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */


package org.jmol.symmetry;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.modelset.BoxInfo;
import org.jmol.util.Quadric;
import org.jmol.util.SimpleUnitCell;

/**
 * a class private to the org.jmol.symmetry package
 * to be accessed only through the SymmetryInterface API
 * 
 * adds vertices and offsets orientation, 
 * and a variety of additional calculations that in 
 * principle could be put in SimpleUnitCell
 * if desired, but for now are in this optional package.
 * 
 */

class UnitCell extends SimpleUnitCell {
  
  private Point3f[] vertices; // eight corners
  private Point3f cartesianOffset = new Point3f();
  private Point3f fractionalOffset = new Point3f();
  
  UnitCell(float[] notionalUnitcell) {
    super(notionalUnitcell);
    calcUnitcellVertices();
  }

  void setOrientation(Matrix3f mat) {
    if (mat == null)
      return;
    Matrix4f m = new Matrix4f();
    m.set(mat);
    matrixFractionalToCartesian.mul(m, matrixFractionalToCartesian);
    matrixCartesianToFractional.invert(matrixFractionalToCartesian);
    calcUnitcellVertices();
  }

  /**
   * when offset is null, 
   * @param pt
   * @param offset
   */
  final void toUnitCell(Point3f pt, Point3f offset) {
    if (matrixCartesianToFractional == null)
      return;
    if (offset == null) {
      // used redefined unitcell 
      matrixCartesianToFractional.transform(pt);
      switch (dimension) {
      case 3:
        pt.z = toFractional(pt.z);  
        // fall through
      case 2:
        pt.y = toFractional(pt.y);
        // fall through
      case 1:
        pt.x = toFractional(pt.x);
      }
      matrixFractionalToCartesian.transform(pt);
    } else {
      // use original unit cell
      matrixCtoFAbsolute.transform(pt);
      switch (dimension) {
      case 3:
        pt.z = toFractional(pt.z);  
        // fall through
      case 2:
        pt.y = toFractional(pt.y);
        // fall through
      case 1:
        pt.x = toFractional(pt.x);
      }
      pt.add(offset);      
      matrixFtoCAbsolute.transform(pt);
    }
  }
  
  private boolean allFractionalRelative = false;
  
  void setAllFractionalRelative(boolean TF) {
    allFractionalRelative = TF;
  }  
  
  void setOffset(Point3f pt) {
    if (pt == null)
      return;
    // from "unitcell {i j k}" via uccage
    fractionalOffset.set(pt);
    matrixCartesianToFractional.m03 = -pt.x;
    matrixCartesianToFractional.m13 = -pt.y;
    matrixCartesianToFractional.m23 = -pt.z;
    cartesianOffset.set(pt);
    matrixFractionalToCartesian.m03 = 0;
    matrixFractionalToCartesian.m13 = 0;
    matrixFractionalToCartesian.m23 = 0;
    matrixFractionalToCartesian.transform(cartesianOffset);
    matrixFractionalToCartesian.m03 = cartesianOffset.x;
    matrixFractionalToCartesian.m13 = cartesianOffset.y;
    matrixFractionalToCartesian.m23 = cartesianOffset.z;
    if (allFractionalRelative) {
      matrixCtoFAbsolute.set(matrixCartesianToFractional);
      matrixFtoCAbsolute.set(matrixFractionalToCartesian);
    }
  }

  void setOffset(int nnn) {
    // from "unitcell ijk" via uccage
    setOffset(ijkToPoint3f(nnn));
  }

  static Point3f ijkToPoint3f(int nnn) {
    Point3f cell = new Point3f();
    cell.x = nnn / 100 - 5;
    cell.y = (nnn % 100) / 10 - 5;
    cell.z = (nnn % 10) - 5;
    return cell;
  }
  
  void setMinMaxLatticeParameters(Point3i minXYZ, Point3i maxXYZ) {
    if (maxXYZ.x <= 555 && maxXYZ.y >= 555) {
      //alternative format for indicating a range of cells:
      //{111 666}
      //555 --> {0 0 0}
      minXYZ.x = (maxXYZ.x / 100) - 5;
      minXYZ.y = (maxXYZ.x % 100) / 10 - 5;
      minXYZ.z = (maxXYZ.x % 10) - 5;
      //555 --> {1 1 1}
      maxXYZ.x = (maxXYZ.y / 100) - 4;
      maxXYZ.z = (maxXYZ.y % 10) - 4;
      maxXYZ.y = (maxXYZ.y % 100) / 10 - 4;
    }
    switch (dimension) {
    case 1: // polymer
      minXYZ.y = 0;
      maxXYZ.y = 1;
      // fall through
    case 2: // slab
      minXYZ.z = 0;
      maxXYZ.z = 1;
    }
  }

  final String dumpInfo(boolean isFull) {
    return "a=" + a + ", b=" + b + ", c=" + c + ", alpha=" + alpha + ", beta=" + beta + ", gamma=" + gamma
       + (isFull ? "\nfractional to cartesian: " + matrixFractionalToCartesian 
       + "\ncartesian to fractional: " + matrixCartesianToFractional : "");
  }

  Point3f[] getVertices() {
    return vertices; // does not include offsets
  }
  
  Point3f getCartesianOffset() {
    // for slabbing isosurfaces and rendering the ucCage
    return cartesianOffset;
  }
  
  Point3f getFractionalOffset() {
    // no references
    return fractionalOffset;
  }
  
  final private static double twoP2 = 2 * Math.PI * Math.PI;
  
  Object[] getEllipsoid(float[] parBorU) {
    if (parBorU == null)
      return null;
    /*
     * 
     * returns {Vector3f[3] unitVectors, float[3] lengths} from J.W. Jeffery,
     * Methods in X-Ray Crystallography, Appendix VI, Academic Press, 1971
     * 
     * comparing with Fischer and Tillmanns, Acta Cryst C44 775-776, 1988, these
     * are really BETA values. Note that
     * 
     * T = exp(-2 pi^2 (a*b* U11h^2 + b*b* U22k^2 + c*c* U33l^2 + 2 a*b* U12hk +
     * 2 a*c* U13hl + 2 b*c* U23kl))
     * 
     * (ORTEP type 8) is the same as
     * 
     * T = exp{-2 pi^2^ sum~i~[sum~j~(U~ij~ h~i~ h~j~ a*~i~ a*~j~)]}
     * 
     * http://ndbserver.rutgers.edu/mmcif/dictionaries/html/cif_mm.dic/Items/
     * _atom_site.aniso_u[1][2].html
     * 
     * Ortep: http://www.ornl.gov/sci/ortep/man_pdf.html
     * 
     * Anisotropic temperature factor Types 0, 1, 2, 3, and 10 use the following
     * formula for the complete temperature factor.
     * 
     * Base^(-D(b11h2 + b22k2 + b33l2 + cb12hk + cb13hl + cb23kl))
     * 
     * The coefficients bij (i,j = 1,2,3) of the various types are defined with
     * the following constant settings.
     * 
     * Type 0: Base = e, c = 2, D = 1 Type 1: Base = e, c = 1, D = l Type 2:
     * Base = 2, c = 2, D = l Type 3: Base = 2, c = 1, D = l
     * 
     * Anisotropic temperature factor Types 4, 5, 8, and 9 use the following
     * formula for the complete temperature factor, in which a1* , a2*, a3* are
     * reciprocal cell dimensions.
     * 
     * exp[ -D(a1*a1*U11hh + a2*a2*U22kk + a3*a3*U33ll + C a1*a2*U12hk + C a1*a3
     * * U13hl + C a2*a3 * U23kl)]
     * 
     * The coefficients Uij (i,j = 1,2,3) of the various types are defined with
     * the following constant settings.
     * 
     * Type 4: C = 2, D = 1/4 Type 5: C = 1, D = 1/4 Type 8: C = 2, D = 2pi2
     * Type 9: C = 1, D = 2pi2
     * 
     * 
     * For beta, we use definitions at
     * http://www.iucr.org/iucr-top/comm/cnom/adp/finrepone/finrepone.html
     * 
     * that betaij = 2pi^2ai*aj* Uij
     * 
     * So if Type 8 is
     * 
     * exp[ -2pi^2(a1*a1*U11hh + a2*a2*U22kk + a3*a3*U33ll + 2a1*a2*U12hk +
     * 2a1*a3 * U13hl + 2a2*a3 * U23kl)]
     * 
     * then we have
     * 
     * exp[ -pi^2(beta11hh + beta22kk + beta33ll + 2beta12hk + 2beta13hl +
     * 2beta23kl)]
     * 
     * and the betaij should be entered as Type 0.
     */

    float[] lengths = new float[6]; // last three are for factored lengths
    if (parBorU[0] == 0) { // this is iso
      lengths[1] = (float) Math.sqrt(parBorU[7]);
      return new Object[] { null, lengths };
    }

    int ortepType = (int) parBorU[6];
    boolean isFractional = (ortepType == 4 || ortepType == 5 || ortepType == 8 || ortepType == 9);
    double cc = 2 - (ortepType % 2);
    double dd = (ortepType == 8 || ortepType == 9 || ortepType == 10 ? twoP2
        : ortepType == 4 || ortepType == 5 ? 0.25 : ortepType == 2
            || ortepType == 3 ? Math.log(2) : 1);
    // types 6 and 7 not supported

    // System.out.println("ortep type " + ortepType + " isFractional=" +
    // isFractional + " D = " + dd + " C=" + cc);
    double B11 = parBorU[0] * dd * (isFractional ? a_ * a_ : 1);
    double B22 = parBorU[1] * dd * (isFractional ? b_ * b_ : 1);
    double B33 = parBorU[2] * dd * (isFractional ? c_ * c_ : 1);
    double B12 = parBorU[3] * dd * (isFractional ? a_ * b_ : 1) * cc;
    double B13 = parBorU[4] * dd * (isFractional ? a_ * c_ : 1) * cc;
    double B23 = parBorU[5] * dd * (isFractional ? b_ * c_ : 1) * cc;

    // set bFactor = (U11*U22*U33)
    parBorU[7] = (float) Math.pow(B11 / twoP2 / a_ / a_ * B22 / twoP2
        / b_ / b_ * B33 / twoP2 / c_ / c_, 0.3333);

    double[] Bcart = new double[6];

    Bcart[0] = a * a * B11 + b * b * cosGamma * cosGamma * B22 + c
        * c * cosBeta * cosBeta * B33 + a * b * cosGamma * B12
        + b * c * cosGamma * cosBeta * B23 + a * c * cosBeta
        * B13;
    Bcart[1] = b * b * sinGamma * sinGamma * B22 + c * c * cA_
        * cA_ * B33 + b * c * cA_ * sinGamma * B23;
    Bcart[2] = c * c * cB_ * cB_ * B33;
    Bcart[3] = 2 * b * b * cosGamma * sinGamma * B22 + 2 * c * c
        * cA_ * cosBeta * B33 + a * b * sinGamma * B12 + b * c
        * (cA_ * cosGamma + sinGamma * cosBeta) * B23 + a
        * c * cA_ * B13;
    Bcart[4] = 2 * c * c * cB_ * cosBeta * B33 + b * c
        * cosGamma * B23 + a * c * cB_ * B13;
    Bcart[5] = 2 * c * c * cA_ * cB_ * B33 + b * c * cB_
        * sinGamma * B23;

    // System.out.println("UnitCell Bcart="+Bcart[0] + " " + Bcart[1] + " " +
    // Bcart[2] + " " + Bcart[3] + " " + Bcart[4] + " " + Bcart[5]);
    Vector3f unitVectors[] = new Vector3f[3];
    for (int i = 0; i < 3; i++)
      unitVectors[i] = new Vector3f();
    Quadric.getAxesForEllipsoid(Bcart, unitVectors, lengths);

    // note -- this is the ellipsoid in INVERSE CARTESIAN SPACE!

    double factor = Math.sqrt(0.5) / Math.PI;
    for (int i = 0; i < 3; i++)
      lengths[i] = (float) (factor / lengths[i]);
    return new Object[] { unitVectors, lengths };
  }
    
  Point3f[] getCanonicalCopy(float scale) {
    Point3f[] pts = new Point3f[8];
    for (int i = 0; i < 8; i++) {
      pts[i] = new Point3f(BoxInfo.unitCubePoints[i]);
      matrixFractionalToCartesian.transform(pts[i]);
      //pts[i].add(cartesianOffset);
    }
    return BoxInfo.getCanonicalCopy(pts, scale);
  }

  /// private methods
  
  
  private static float toFractional(float x) {
    // introduced in Jmol 11.7.36
    x = (float) (x - Math.floor(x));
    if (x > 0.9999f || x < 0.0001f) 
      x = 0;
    return x;
  }
  
  private void calcUnitcellVertices() {
    if (matrixFractionalToCartesian == null)
      return;
    matrixCtoFAbsolute = new Matrix4f(matrixCartesianToFractional);
    matrixFtoCAbsolute = new Matrix4f(matrixFractionalToCartesian);
    vertices = new Point3f[8];
    for (int i = 8; --i >= 0;) {
      vertices[i] = new Point3f(); 
      matrixFractionalToCartesian.transform(BoxInfo.unitCubePoints[i], vertices[i]);
      //System.out.println("UNITCELL " + vertices[i] + " " + BoxInfo.unitCubePoints[i]);
    }
  }

  /**
   * 
   * @param f1
   * @param f2
   * @param distance
   * @param dx
   * @param iRange
   * @param jRange
   * @param kRange
   * @param ptOffset TODO
   * @return       TRUE if pt has been set.
   */
  public boolean checkDistance(Point3f f1, Point3f f2, float distance, float dx,
                              int iRange, int jRange, int kRange, Point3f ptOffset) {
    Point3f p1 = new Point3f(f1);
    toCartesian(p1, true);
    for (int i = -iRange; i <= iRange; i++)
      for (int j = -jRange; j <= jRange; j++)
        for (int k = -kRange; k <= kRange; k++) {
          ptOffset.set(f2.x + i, f2.y + j, f2.z + k);
          toCartesian(ptOffset, true);
          float d = p1.distance(ptOffset);
          if (dx > 0 ? Math.abs(d - distance) <= dx : d <= distance && d > 0.1f) {
            ptOffset.set(i, j, k);
            return true;
          }
        }
    return false;
  }

}
