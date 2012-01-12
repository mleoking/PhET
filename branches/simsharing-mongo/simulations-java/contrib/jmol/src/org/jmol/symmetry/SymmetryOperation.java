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
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.symmetry;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.api.SymmetryInterface;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.util.Parser;
import org.jmol.util.Quaternion;
import org.jmol.util.TriangleData;
import org.jmol.script.Token;

/*
 * Bob Hanson 4/2006
 * 
 * references: International Tables for Crystallography Vol. A. (2002) 
 *
 * http://www.iucr.org/iucr-top/cif/cifdic_html/1/cif_core.dic/Ispace_group_symop_operation_xyz.html
 * http://www.iucr.org/iucr-top/cif/cifdic_html/1/cif_core.dic/Isymmetry_equiv_pos_as_xyz.html
 *
 * LATT : http://macxray.chem.upenn.edu/LATT.pdf thank you, Patrick Carroll
 * 
 * NEVER ACCESS THESE METHODS DIRECTLY! ONLY THROUGH CLASS Symmetry
 * 
 *
 */

class SymmetryOperation extends Matrix4f {
  String xyzOriginal;
  String xyz;
  boolean doNormalize = true;
  boolean isFinalized;
  int opId;

  SymmetryOperation() {
  }

  SymmetryOperation(boolean doNormalize, int opId) {
    this.doNormalize = doNormalize;
    this.opId = opId;
  }

  SymmetryOperation(SymmetryOperation op, Point3f[] atoms,
                           int atomIndex, int count, boolean doNormalize) {
    /*
     * externalizes and transforms an operation for use in atom reader
     * 
     */
    this.doNormalize = doNormalize;
    xyzOriginal = op.xyzOriginal;
    xyz = op.xyz;
    this.opId = op.opId;
    set(op); // sets the underlying Matrix4f
    doFinalize();
    if (doNormalize)
      setOffset(atoms, atomIndex, count);
  }

  void doFinalize() {
    m03 /= 12f;
    m13 /= 12f;
    m23 /= 12f;
    isFinalized = true;
  }
  
  String getXyz(boolean normalized) {
    return (normalized || xyzOriginal == null ? xyz : xyzOriginal);
  }

  private Point3f temp3 = new Point3f();
  void newPoint(Point3f atom1, Point3f atom2,
                       int transX, int transY, int transZ) {
    temp3.set(atom1);
    transform(temp3, temp3);
    atom2.set(temp3.x + transX, temp3.y + transY, temp3.z + transZ);
  }

  String dumpInfo() {
    return "\n" + xyz + "\ninternal matrix representation:\n"
        + ((Matrix4f) this).toString();
  }

  final static String dumpSeitz(Matrix4f s) {
    return (new StringBuffer("{\t")).append((int) s.m00).append("\t").append((int) s.m01)
        .append("\t").append((int) s.m02).append("\t").append(twelfthsOf(s.m03)).append("\t}\n")
        .append("{\t").append((int) s.m10).append("\t").append((int) s.m11).append("\t").append((int) s.m12)
        .append("\t").append(twelfthsOf(s.m13)).append("\t}\n")
        .append("{\t").append((int) s.m20).append("\t").append((int) s.m21).append("\t").append((int) s.m22)
        .append("\t").append(twelfthsOf(s.m23)).append("\t}\n").append("{\t0\t0\t0\t1\t}\n").toString();
  }
  
  final static String dumpCanonicalSeitz(Matrix4f s) {
    return (new StringBuffer()).append("{\t").append((int) s.m00).append("\t").append((int) s.m01)
        .append("\t").append((int) s.m02).append("\t").append(twelfthsOf(s.m03+12)).append("\t}\n")
        .append("{\t").append((int) s.m10).append("\t").append((int) s.m11).append("\t").append((int) s.m12)
        .append("\t").append(twelfthsOf(s.m13+12)).append("\t}\n").append("{\t").append((int) s.m20)
        .append("\t").append((int) s.m21).append("\t")
        .append((int) s.m22).append("\t").append(twelfthsOf(s.m23+12)).append("\t}\n")
        .append("{\t0\t0\t0\t1\t}\n").toString();
  }
  
  boolean setMatrixFromXYZ(String xyz) {
    /*
     * sets symmetry based on an operator string "x,-y,z+1/2", for example
     * 
     */
    if (xyz == null)
      return false;
    xyzOriginal = xyz;
    xyz = xyz.toLowerCase();
    float[] temp = new float[16];
    boolean isReverse = (xyz.startsWith("!"));
    if (isReverse)
      xyz = xyz.substring(1);
    if (xyz.indexOf("xyz matrix:") == 0) {
      /* note: these terms must in unit cell fractional coordinates!
       * CASTEP CML matrix is in fractional coordinates, but do not take into accout
       * hexagonal systems. Thus, in wurtzite.cml, for P 6c 2'c:
       *
       * "transform3": 
       * 
       * -5.000000000000e-1  8.660254037844e-1  0.000000000000e0   0.000000000000e0 
       * -8.660254037844e-1 -5.000000000000e-1  0.000000000000e0   0.000000000000e0 
       *  0.000000000000e0   0.000000000000e0   1.000000000000e0   0.000000000000e0 
       *  0.000000000000e0   0.000000000000e0   0.000000000000e0   1.000000000000e0
       *
       * These are transformations of the STANDARD xyz axes, not the unit cell. 
       * But, then, what coordinate would you feed this? Fractional coordinates of what?
       * The real transform is something like x-y,x,z here.
       * 
       * The coordinates we are using here 
       */
      this.xyz = xyz;
      Parser.parseStringInfestedFloatArray(xyz, null, temp);
      for (int i = 0; i < 16; i++) {
        if (Float.isNaN(temp[i]))
          return false;
        float v = temp[i];
        if (Math.abs(v) < 0.00001f)
          v = 0;
        if (i % 4 == 3)
          v = normalizeTwelfths((v < 0 ? -1 : 1) * Math.round(Math.abs(v * 12)), doNormalize);
        temp[i] = v;
      }
      temp[15] = 1;
      set(temp);
      isFinalized = true;
      if (isReverse)
        invert(this);
      this.xyz = getXYZFromMatrix(this, true, false, false);
      return true;
    }
    if (xyz.indexOf("[[") == 0) {
      xyz = xyz.replace('[',' ').replace(']',' ').replace(',',' ');
      Parser.parseStringInfestedFloatArray(xyz, null, temp);
      for (int i = 0; i < 16; i++) {
        if (Float.isNaN(temp[i]))
          return false;
      }
      set(temp);
      isFinalized = true;
      if (isReverse)
        invert(this);
      this.xyz = getXYZFromMatrix(this, false, false, false);
      //System.out.println("SymmetryOperation: " + xyz + "\n" + (Matrix4f)this + "\n" + this.xyz);
      return true;
    }
    String strOut = getMatrixFromString(xyz, temp, doNormalize, false);
    if (strOut == null)
      return false;
    set(temp);
    if (isReverse) {
      invert(this);
      this.xyz = getXYZFromMatrix(this, true, false, false);
    } else {
      this.xyz = strOut;
    }
    if (Logger.debugging)
      Logger.debug("" + this);
    return true;
  }

  static String getMatrixFromString(String xyz, float[] temp,
                                    boolean doNormalize, boolean allowScaling) {
    boolean isDenominator = false;
    boolean isDecimal = false;
    boolean isNegative = false;
    char ch;
    int x = 0;
    int y = 0;
    int z = 0;
    float iValue = 0;
    String strOut = "";
    String strT;
    int rowPt = -1;
    float decimalMultiplier = 1f;
    xyz += ",";
    for (int i = 0; i < xyz.length(); i++) {
      ch = xyz.charAt(i);
      switch (ch) {
      case '\'':
      case ' ':
      case '{':
      case '}':
      case '!':
        continue;
      case '-':
        isNegative = true;
        continue;
      case '+':
        isNegative = false;
        continue;
      case '/':
        isDenominator = true;
        continue;
      case 'X':
      case 'x':
        x = (isNegative ? -1 : 1);
        if (allowScaling && iValue != 0) {
          x *= iValue;
          iValue = 0;
        }
        break;
      case 'Y':
      case 'y':
        y = (isNegative ? -1 : 1);
        if (allowScaling && iValue != 0) {
          y *= iValue;
          iValue = 0;
        }
        break;
      case 'Z':
      case 'z':
        z = (isNegative ? -1 : 1);
        if (allowScaling && iValue != 0) {
          z *= iValue;
          iValue = 0;
        }
        break;
      case ',':
        if (++rowPt > 2) {
          Logger.warn("Symmetry Operation? " + xyz);
          return null;
        }
        int tpt = rowPt * 4;
        // put translation into 12ths
        iValue = normalizeTwelfths(iValue, doNormalize);
        temp[tpt++] = x;
        temp[tpt++] = y;
        temp[tpt++] = z;
        temp[tpt] = iValue;
        strT = "";
        strT += (x == 0 ? "" : x < 0 ? "-x" : strT.length() == 0 ? "x" : "+x");
        strT += (y == 0 ? "" : y < 0 ? "-y" : strT.length() == 0 ? "y" : "+y");
        strT += (z == 0 ? "" : z < 0 ? "-z" : strT.length() == 0 ? "z" : "+z");
        strT += xyzFraction(iValue, false, true);
        strOut += (strOut == "" ? "" : ",") + strT;
        //note: when ptLatt[3] = -1, ptLatt[rowPt] MUST be 0.
        if (rowPt == 2) {
          temp[15] = 1;
          return strOut;
        }
        x = y = z = 0;
        iValue = 0;
        break;
      case '.':
        isDecimal = true;
        decimalMultiplier = 1f;
        continue;
      case '0':
        if (!isDecimal && (isDenominator || !allowScaling))
          continue;
        //allow to pass through
      default:
        //Logger.debug(isDecimal + " " + ch + " " + iValue);
        int ich = ch - '0';
        if (isDecimal && ich >= 0 && ich <= 9) {
          decimalMultiplier /= 10f;
          if (iValue < 0)
            isNegative = true;
          iValue += decimalMultiplier * ich * (isNegative ? -1 : 1);
          continue;
        }
        if (ich >= 0 && ich <= 9) {
          if (isDenominator) {
            iValue /= ich;
          } else {
            iValue = iValue * 10 + (isNegative ? -1 : 1) * ich;
            isNegative = false;
          }
        } else {
          Logger.warn("symmetry character?" + ch);
        }
      }
      isDecimal = isDenominator = isNegative = false;
    }
    return null;
  }

  private static float normalizeTwelfths(float iValue, boolean doNormalize) {
    iValue *= 12f;
    if (doNormalize) {
      while (iValue > 6)
        iValue -= 12;
      while (iValue <= -6)
        iValue += 12;
    }
    return iValue;
  }

  final static String getXYZFromMatrix(Matrix4f mat, boolean is12ths,
                                       boolean allPositive, boolean halfOrLess) {
    String str = "";
    float[] row = new float[4];
    for (int i = 0; i < 3; i++) {
      mat.getRow(i, row);
      String term = "";
      if (row[0] != 0)
        term += (row[0] < 0 ? "-" : "+") + "x";
      if (row[1] != 0)
        term += (row[1] < 0 ? "-" : "+") + "y";
      if (row[2] != 0)
        term += (row[2] < 0 ? "-" : "+") + "z";
      term += xyzFraction((is12ths ? row[3] : row[3] * 12), allPositive,
          halfOrLess);
      if (term.length() > 0 && term.charAt(0) == '+')
        term = term.substring(1);
      str += "," + term;
    }
    return str.substring(1);
  }

  private final static String twelfthsOf(float n12ths) {
    String str = "";
    int i12ths = Math.round(n12ths);
    if (i12ths == 12)
      return "1";
    if (i12ths == -12)
      return "-1";
    if (i12ths < 0) {
      i12ths = -i12ths;
      if (i12ths % 12 != 0)
        str = "-";
    }
    int n = i12ths / 12;
    if (n < 1)
      return str + twelfths[i12ths % 12];
    int m = 0;
    switch (i12ths % 12) {
    case 0:
      return str + n;
    case 1:
    case 5:
    case 7:
    case 11:
      m = 12;
      break;
    case 2:
    case 10:
      m = 6;
      break;
    case 3:
    case 9:
      m = 4;
      break;
    case 4:
    case 8:
      m = 3;
      break;
    case 6:
      m = 2;
      break;
    }
    return str + (i12ths * m / 12) + "/" + m;
  }
  
  private final static String[] twelfths = { "0", "1/12", "1/6", "1/4", "1/3",
      "5/12", "1/2", "7/12", "2/3", "3/4", "5/6", "11/12" };

  private final static String xyzFraction(float n12ths, boolean allPositive, boolean halfOrLess) {
    n12ths = Math.round(n12ths);
    if (allPositive) {
      while (n12ths < 0)
        n12ths += 12f;
    } else if (halfOrLess && n12ths > 6f) {
      n12ths -= 12f;
    }
    String s = twelfthsOf(n12ths);
    return (s.charAt(0) == '0' ? "" : n12ths > 0 ? "+" + s : s);
  }

  Point3f atomTest = new Point3f();

  private void setOffset(Point3f[] atoms, int atomIndex, int count) {
    /*
     * the center of mass of the full set of atoms is moved into the cell with this
     *  
     */
    int i1 = atomIndex;
    int i2 = i1 + count;
    float x = 0;
    float y = 0;
    float z = 0;
    for (int i = i1; i < i2; i++) {
      newPoint(atoms[i], atomTest, 0, 0, 0);
      x += atomTest.x;
      y += atomTest.y;
      z += atomTest.z;
    }
    
    while (x < -0.001 || x >= count + 0.001) {
      m03 += (x < 0 ? 1 : -1);
      x += (x < 0 ? count : -count);
    }
    while (y < -0.001 || y >= count + 0.001) {
      m13 += (y < 0 ? 1 : -1);
      y += (y < 0 ? count : -count);
    }
    while (z < -0.001 || z >= count + 0.001) {
      m23 += (z < 0 ? 1 : -1);
      z += (z < 0 ? count : -count);
    }
  }

  // action of this method depends upon setting of unitcell
  private void transformCartesian(UnitCell unitcell, Point3f pt) {
    unitcell.toFractional(pt, false);
    transform(pt);
    unitcell.toCartesian(pt, false);

  }
  
  Vector3f[] rotateEllipsoid(Point3f cartCenter, Vector3f[] vectors,
                                    UnitCell unitcell, Point3f ptTemp1, Point3f ptTemp2) {
    Vector3f[] vRot = new Vector3f[3];
    ptTemp2.set(cartCenter);
    transformCartesian(unitcell, ptTemp2);
    for (int i = vectors.length; --i >= 0;) {
      ptTemp1.set(cartCenter);
      ptTemp1.add(vectors[i]);
      transformCartesian(unitcell, ptTemp1);
      vRot[i] = new Vector3f(ptTemp1);
      vRot[i].sub(ptTemp2);
    }
    return vRot;
  }
  
  /**
   * 
   * @param uc
   * @param pt00
   * @param ptTarget 
   * @param id
   * @return Object[] containing: 
   *              [0]      xyz (Jones-Faithful calculated from matrix)
   *              [1]      xyzOriginal (Provided by calling method) 
   *              [2]      info ("C2 axis", for example) 
   *              [3]      draw commands 
   *              [4]      translation vector (fractional)  
   *              [5]      translation vector (Cartesian)  
   *              [6]      inversion point 
   *              [7]      axis point 
   *              [8]      axis vector (defines plane if angle = 0
   *              [9]      angle of rotation
   *              [10]      matrix representation
   */
  public Object[] getDescription(SymmetryInterface uc, Point3f pt00, Point3f ptTarget, String id) {
    if (!isFinalized)
      doFinalize();
    return getDescription(this, xyzOriginal, uc, pt00, ptTarget, id);
  }
  
  private static Object[] getDescription(SymmetryOperation m,
                                         String xyzOriginal,
                                         SymmetryInterface uc, Point3f pt00,
                                         Point3f ptTarget, String id) {
    Vector3f vtemp = new Vector3f();
    Point3f ptemp = new Point3f();
    Point3f pt01 = new Point3f();
    Point3f pt02 = new Point3f();
    Point3f pt03 = new Point3f();
    Vector3f ftrans = new Vector3f();
    Vector3f vtrans = new Vector3f();
    String xyz = getXYZFromMatrix(m, false, false, false);
    boolean typeOnly = (id == null);
    if (pt00 == null || Float.isNaN(pt00.x))
      pt00 = new Point3f();
    if (ptTarget != null) {
      // Check to see if the two points only differ by
      // a translation after transformation.
      // If so, add that difference to the matrix transformation 
      pt01.set(pt00);
      pt02.set(ptTarget);
      uc.toUnitCell(pt01, ptemp);
      uc.toUnitCell(pt02, ptemp);
      uc.toFractional(pt01, false);
      m.transform(pt01);
      uc.toCartesian(pt01, false);
      uc.toUnitCell(pt01, ptemp);
      if (pt01.distance(pt02) > 0.1f)
        return null;
      pt01.set(pt00);
      pt02.set(ptTarget);
      uc.toFractional(pt01, false);
      uc.toFractional(pt02, false);
      m.transform(pt01);
      vtrans.sub(pt02, pt01);
      pt01.set(0, 0, 0);
      pt02.set(0, 0, 0);
    }
    pt01.x = pt02.y = pt03.z = 1;
    pt01.add(pt00);
    pt02.add(pt00);
    pt03.add(pt00);

    Point3f p0 = new Point3f(pt00);
    Point3f p1 = new Point3f(pt01);
    Point3f p2 = new Point3f(pt02);
    Point3f p3 = new Point3f(pt03);

    uc.toFractional(p0, false);
    uc.toFractional(p1, false);
    uc.toFractional(p2, false);
    uc.toFractional(p3, false);
    m.transform(p0, p0);
    m.transform(p1, p1);
    m.transform(p2, p2);
    m.transform(p3, p3);
    p0.add(vtrans);
    p1.add(vtrans);
    p2.add(vtrans);
    p3.add(vtrans);
    approx(vtrans);
    uc.toCartesian(p0, false);
    uc.toCartesian(p1, false);
    uc.toCartesian(p2, false);
    uc.toCartesian(p3, false);

    Vector3f v01 = new Vector3f();
    v01.sub(p1, p0);
    Vector3f v02 = new Vector3f();
    v02.sub(p2, p0);
    Vector3f v03 = new Vector3f();
    v03.sub(p3, p0);

    vtemp.cross(v01, v02);
    boolean haveinversion = (vtemp.dot(v03) < 0);

    // The first trick is to check cross products to see if we still have a
    // right-hand axis.

    if (haveinversion) {

      // undo inversion for quaternion analysis (requires proper rotations only)

      p1.scaleAdd(-2, v01, p1);
      p2.scaleAdd(-2, v02, p2);
      p3.scaleAdd(-2, v03, p3);

    }

    // The second trick is to use quaternions. Each of the three faces of the
    // frame (xy, yz, and zx)
    // is checked. The helix() function will return data about the local helical
    // axis, and the
    // symop(sym,{0 0 0}) function will return the overall translation.

    Object[] info;
    info = (Object[]) Measure.computeHelicalAxis(null, Token.array, pt00, p0,
        Quaternion.getQuaternionFrame(p0, p1, p2).div(
            Quaternion.getQuaternionFrame(pt00, pt01, pt02)));
    Point3f pa1 = (Point3f) info[0];
    Vector3f ax1 = (Vector3f) info[1];
    int ang1 = (int) Math.abs(approx(((Point3f) info[3]).x, 1));
    float pitch1 = approx(((Point3f) info[3]).y);

    if (haveinversion) {

      // redo inversion

      p1.scaleAdd(2, v01, p1);
      p2.scaleAdd(2, v02, p2);
      p3.scaleAdd(2, v03, p3);

    }

    Vector3f trans = new Vector3f(p0);
    trans.sub(pt00);
    if (trans.length() < 0.1f)
      trans = null;

    // ////////// determination of type of operation from first principles

    Point3f ptinv = null; // inverted point for translucent frame
    Point3f ipt = null; // inversion center
    Point3f pt0 = null; // reflection center

    boolean istranslation = (ang1 == 0);
    boolean isrotation = !istranslation;
    boolean isinversion = false;
    boolean ismirrorplane = false;

    if (isrotation || haveinversion)
      trans = null;

    // handle inversion

    if (haveinversion && istranslation) {

      // simple inversion operation

      ipt = new Point3f(pt00);
      ipt.add(p0);
      ipt.scale(0.5f);
      ptinv = p0;
      isinversion = true;
    } else if (haveinversion) {

      /*
       * 
       * We must convert simple rotations to rotation-inversions; 2-screws to
       * planes and glide planes.
       * 
       * The idea here is that there is a relationship between the axis for a
       * simple or screw rotation of an inverted frame and one for a
       * rotation-inversion. The relationship involves two adjacent equilateral
       * triangles:
       * 
       * 
       *       o 
       *      / \
       *     /   \    i'
       *    /     \ 
       *   /   i   \
       * A/_________\A' 
       *  \         / 
       *   \   j   / 
       *    \     / 
       *     \   / 
       *      \ / 
       *       x
       *      
       * Points i and j are at the centers of the triangles. Points A and A' are
       * the frame centers; an operation at point i, j, x, or o is taking A to
       * A'. Point i is 2/3 of the way from x to o. In addition, point j is half
       * way between i and x.
       * 
       * The question is this: Say you have an rotation/inversion taking A to
       * A'. The relationships are:
       * 
       * 6-fold screw x for inverted frame corresponds to 6-bar at i for actual
       * frame 3-fold screw i for inverted frame corresponds to 3-bar at x for
       * actual frame
       * 
       * The proof of this follows. Consider point x. Point x can transform A to
       * A' as a clockwise 6-fold screw axis. So, say we get that result for the
       * inverted frame. What we need for the real frame is a 6-bar axis
       * instead. Remember, though, that we inverted the frame at A to get this
       * result. The real frame isn't inverted. The 6-bar must do that inversion
       * AND also get the frame to point A' with the same (clockwise) rotation.
       * The key is to see that there is another axis -- at point i -- that does
       * the trick.
       * 
       * Take a look at the angles and distances that arise when you project A
       * through point i. The result is a frame at i'. Since the distance i-i'
       * is the same as i-A (and thus i-A') and the angle i'-i-A' is 60 degrees,
       * point i is also a 6-bar axis transforming A to A'.
       * 
       * Note that both the 6-fold screw axis at x and the 6-bar axis at i are
       * both clockwise.
       * 
       * Similar analysis shows that the 3-fold screw i corresponds to the 3-bar
       * axis at x.
       * 
       * So in each case we just calculate the vector i-j or x-o and then factor
       * appropriately.
       * 
       * The 4-fold case is simpler -- just a parallelogram.
       */

      Vector3f d = (pitch1 == 0 ? new Vector3f() : ax1);
      float f = 0;
      switch (ang1) {
      case 60: // 6_1 at x to 6-bar at i
        f = 2f / 3f;
        break;
      case 120: // 3_1 at i to 3-bar at x
        f = 2;
        break;
      case 90: // 4_1 to 4-bar at opposite corner
        f = 1;
        break;
      case 180: // 2_1 to mirror plane
        // C2 with inversion is a mirror plane -- but could have a glide
        // component.
        pt0 = new Point3f();
        pt0.set(pt00);
        pt0.add(d);
        pa1.scaleAdd(0.5f, d, pt00);
        if (pt0.distance(p0) > 0.1f) {
          trans = new Vector3f(p0);
          trans.sub(pt0);
          ptemp.set(trans);
          uc.toFractional(ptemp, false);
          ftrans.set(ptemp);
        } else {
          trans = null;
        }
        isrotation = false;
        haveinversion = false;
        ismirrorplane = true;
      }
      if (f != 0) {
        // pa1 = pa1 + ((pt00 - pa1) + (p0 - (pa1 + d))) * f

        vtemp.set(pt00);
        vtemp.sub(pa1);
        vtemp.add(p0);
        vtemp.sub(pa1);
        vtemp.sub(d);
        vtemp.scale(f);
        pa1.add(vtemp);
        ipt = new Point3f();
        ipt.scaleAdd(0.5f, d, pa1);
        ptinv = new Point3f();
        ptinv.scaleAdd(-2, ipt, pt00);
        ptinv.scale(-1);
      }

    } else if (trans != null) {

      // get rid of unnecessary translations added to keep most operations
      // within cell 555

      ptemp.set(trans);
      uc.toFractional(ptemp, false);
      if (approx(ptemp.x) == 1) {
        ptemp.x = 0;
      }
      if (approx(ptemp.y) == 1) {
        ptemp.y = 0;
      }
      if (approx(ptemp.z) == 1) {
        ptemp.z = 0;
      }
      ftrans.set(ptemp);
      uc.toCartesian(ptemp, false);
      trans.set(ptemp);
    }

    // fix angle based on direction of axis

    int ang = ang1;
    approx0(ax1);

    if (isrotation) {

      Point3f pt1 = new Point3f();

      vtemp.set(ax1);

      // draw the lines associated with a rotation

      int ang2 = ang1;
      if (haveinversion) {
        pt1.set(pa1);
        pt1.add(vtemp);
        ang2 = (int) Measure.computeTorsion(ptinv, pa1, pt1, p0, true);
      } else if (pitch1 == 0) {
        pt1.set(pa1);
        ptemp.scaleAdd(1, pt1, vtemp);
        ang2 = (int) Measure.computeTorsion(pt00, pa1, ptemp, p0, true);
      } else {
        ptemp.set(pa1);
        ptemp.add(vtemp);
        pt1.scaleAdd(0.5f, vtemp, pa1);
        ang2 = (int) Measure.computeTorsion(pt00, pa1, ptemp, p0, true);
      }

      if (ang2 != 0)
        ang1 = ang2;
    }

    if (isrotation && !haveinversion && pitch1 == 0) {
      if (ax1.z < 0 || ax1.z == 0 && (ax1.y < 0 || ax1.y == 0 && ax1.x < 0)) {
        ax1.scale(-1);
        ang1 = -ang1;
      }
    }

    // time to get the description

    String info1 = "identity";
    StringBuffer draw1 = new StringBuffer();
    String drawid;

    if (isinversion) {
      ptemp.set(ipt);
      uc.toFractional(ptemp, false);
      info1 = "inversion center|" + fcoord(ptemp);
    } else if (isrotation) {
      if (haveinversion) {
        info1 = "" + (360 / ang) + "-bar axis";
      } else if (pitch1 != 0) {
        info1 = "" + (360 / ang) + "-fold screw axis";
        ptemp.set(ax1);
        uc.toFractional(ptemp, false);
        info1 += "|translation: " + fcoord(ptemp);
      } else {
        info1 = "C" + (360 / ang) + " axis";
      }
    } else if (trans != null) {
      String s = " " + fcoord(ftrans);
      if (istranslation) {
        info1 = "translation:" + s;
      } else if (ismirrorplane) {
        float fx = approx(ftrans.x);
        float fy = approx(ftrans.y);
        float fz = approx(ftrans.z);
        s = " " + fcoord(ftrans);
        if (fx != 0 && fy != 0 && fz != 0)
          info1 = "d-";
        else if (fx != 0 && fy != 0 || fy != 0 && fz != 0 || fz != 0 && fx != 0)
          info1 = "n-";
        else if (fx != 0)
          info1 = "a-";
        else if (fy != 0)
          info1 = "b-";
        else
          info1 = "c-";
        info1 += "glide plane |translation:" + s;
      }
    } else if (ismirrorplane) {
      info1 = "mirror plane";
    }

    if (haveinversion && !isinversion) {
      ptemp.set(ipt);
      uc.toFractional(ptemp, false);
      info1 += "|inversion center at " + fcoord(ptemp);
    }

    String cmds = null;
    if (!typeOnly) {
      drawid = "\ndraw ID " + id + "_";

      // delete previous elements of this user-settable ID

      draw1 = new StringBuffer();
      draw1.append("// " + xyzOriginal + "|" + xyz + "|" + info1 + "\n");
      draw1.append(drawid).append("* delete");

      // draw the initial frame

      drawLine(draw1, drawid + "frame1X", 0.15f, pt00, pt01, "red");
      drawLine(draw1, drawid + "frame1Y", 0.15f, pt00, pt02, "green");
      drawLine(draw1, drawid + "frame1Z", 0.15f, pt00, pt03, "blue");

      // draw the final frame just a bit fatter and shorter, in case they
      // overlap

      ptemp.set(p1);
      ptemp.sub(p0);
      ptemp.scaleAdd(0.9f, ptemp, p0);
      drawLine(draw1, drawid + "frame2X", 0.2f, p0, ptemp, "red");
      ptemp.set(p2);
      ptemp.sub(p0);
      ptemp.scaleAdd(0.9f, ptemp, p0);
      drawLine(draw1, drawid + "frame2Y", 0.2f, p0, ptemp, "green");
      ptemp.set(p3);
      ptemp.sub(p0);
      ptemp.scaleAdd(0.9f, ptemp, p0);
      drawLine(draw1, drawid + "frame2Z", 0.2f, p0, ptemp, "purple");

      String color;

      if (isrotation) {

        Point3f pt1 = new Point3f();

        color = "red";

        ang = ang1;
        float scale = 1.0f;
        vtemp.set(ax1);

        // draw the lines associated with a rotation

        if (haveinversion) {
          pt1.set(pa1);
          pt1.add(vtemp);
          if (pitch1 == 0) {
            pt1.set(ipt);
            vtemp.scale(3);
            ptemp.scaleAdd(-1, vtemp, pa1);
            draw1.append(drawid).append("rotVector2 diameter 0.1 ").append(
                Escape.escape(pa1)).append(Escape.escape(ptemp)).append(
                " color red");
          }
          scale = p0.distance(pt1);
          draw1.append(drawid).append("rotLine1 ").append(Escape.escape(pt1))
              .append(Escape.escape(ptinv)).append(" color red");
          draw1.append(drawid).append("rotLine2 ").append(Escape.escape(pt1))
              .append(Escape.escape(p0)).append(" color red");
        } else if (pitch1 == 0) {
          boolean isSpecial = (pt00.distance(p0) < 0.2f);
          if (!isSpecial) {
            draw1.append(drawid).append("rotLine1 ")
                .append(Escape.escape(pt00)).append(Escape.escape(pa1)).append(
                    " color red");
            draw1.append(drawid).append("rotLine2 ").append(Escape.escape(p0))
                .append(Escape.escape(pa1)).append(" color red");
          }
          vtemp.scale(3);
          ptemp.scaleAdd(-1, vtemp, pa1);
          draw1.append(drawid).append("rotVector2 diameter 0.1 ").append(
              Escape.escape(pa1)).append(Escape.escape(ptemp)).append(
              " color red");
          pt1.set(pa1);
          if (pitch1 == 0 && pt00.distance(p0) < 0.2)
            pt1.scaleAdd(0.5f, pt1, vtemp);
        } else {
          // screw
          color = "orange";
          draw1.append(drawid).append("rotLine1 ").append(Escape.escape(pt00))
              .append(Escape.escape(pa1)).append(" color red");
          ptemp.set(pa1);
          ptemp.add(vtemp);
          draw1.append(drawid).append("rotLine2 ").append(Escape.escape(p0))
              .append(Escape.escape(ptemp)).append(" color red");
          pt1.scaleAdd(0.5f, vtemp, pa1);
        }

        // draw arc arrow

        ptemp.set(pt1);
        ptemp.add(vtemp);
        if (haveinversion && pitch1 != 0) {
          draw1.append(drawid).append("rotRotLine1").append(Escape.escape(pt1))
              .append(Escape.escape(ptinv)).append(" color red");
          draw1.append(drawid).append("rotRotLine2").append(Escape.escape(pt1))
              .append(Escape.escape(p0)).append(" color red");
        }
        draw1.append(drawid).append(
            "rotRotArrow arrow width 0.10 scale " + scale + " arc ").append(
            Escape.escape(pt1)).append(Escape.escape(ptemp));
        if (haveinversion)
          ptemp.set(ptinv);
        else
          ptemp.set(pt00);
        if (ptemp.distance(p0) < 0.1f)
          ptemp.set((float) Math.random(), (float) Math.random(), (float) Math
              .random());
        draw1.append(Escape.escape(ptemp));
        ptemp.set(0, ang, 0);
        draw1.append(Escape.escape(ptemp)).append(" color red");
        // draw the main vector

        draw1.append(drawid).append("rotVector1 vector diameter 0.1 ").append(
            Escape.escape(pa1)).append(Escape.escape(vtemp)).append("color ")
            .append(color);
      }

      if (ismirrorplane) {

        // indigo arrow across plane from pt00 to pt0

        if (pt00.distance(pt0) > 0.2)
          draw1.append(drawid).append("planeVector arrow ").append(
              Escape.escape(pt00)).append(Escape.escape(pt0)).append(
              " color indigo");

        // faint inverted frame if trans is not null

        if (trans != null) {
          ptemp.scaleAdd(-1, p0, p1);
          ptemp.add(pt0);
          drawLine(draw1, drawid + "planeFrameX", 0.15f, pt0, ptemp,
              "translucent red");
          ptemp.scaleAdd(-1, p0, p2);
          ptemp.add(pt0);
          drawLine(draw1, drawid + "planeFrameY", 0.15f, pt0, ptemp,
              "translucent green");
          ptemp.scaleAdd(-1, p0, p3);
          ptemp.add(pt0);
          drawLine(draw1, drawid + "planeFrameZ", 0.15f, pt0, ptemp,
              "translucent blue");
        }

        color = (trans == null ? "green" : "blue");

        // ok, now HERE's a good trick. We use the Marching Cubes
        // algorithm to find the intersection points of a plane and the unit
        // cell.
        // We expand the unit cell by 5% in all directions just so we are
        // guaranteed to get cutoffs.

        vtemp.set(ax1);
        vtemp.normalize();
        // ax + by + cz + d = 0
        // so if a point is in the plane, then N dot X = -d
        float w = -vtemp.x * pa1.x - vtemp.y * pa1.y - vtemp.z * pa1.z;
        Point4f plane = new Point4f(vtemp.x, vtemp.y, vtemp.z, w);
        List<Object> v = new ArrayList<Object>();
        v.add(uc.getCanonicalCopy(1.05f));
        TriangleData.intersectPlane(plane, v, 3);

        // returns triangles and lines
        if (v != null)
          for (int i = v.size(); --i >= 0;) {
            Point3f[] pts = (Point3f[]) v.get(i);
            draw1.append(drawid).append("planep").append(i).append(
                Escape.escape(pts[0])).append(Escape.escape(pts[1]));
            if (pts.length == 3)
              draw1.append(Escape.escape(pts[2]));
            draw1.append(" color translucent ").append(color);
          }

        // and JUST in case that does not work, at least draw a circle

        if (v == null || v.size() == 0) {
          ptemp.set(pa1);
          ptemp.add(ax1);
          draw1.append(drawid).append("planeCircle scale 2.0 circle ").append(
              Escape.escape(pa1)).append(Escape.escape(ptemp)).append(
              " color translucent ").append(color).append(" mesh fill");
        }
      }

      if (haveinversion) {

        // draw a faint frame showing the inversion

        draw1.append(drawid).append("invPoint diameter 0.4 ").append(
            Escape.escape(ipt));
        draw1.append(drawid).append("invArrow arrow ").append(
            Escape.escape(pt00)).append(Escape.escape(ptinv)).append(
            " color indigo");
        if (!isinversion) {
          ptemp.set(ptinv);
          ptemp.add(pt00);
          ptemp.sub(pt01);
          drawLine(draw1, drawid + "invFrameX", 0.15f, ptinv, ptemp,
              "translucent red");
          ptemp.set(ptinv);
          ptemp.add(pt00);
          ptemp.sub(pt02);
          drawLine(draw1, drawid + "invFrameY", 0.15f, ptinv, ptemp,
              "translucent green");
          ptemp.set(ptinv);
          ptemp.add(pt00);
          ptemp.sub(pt03);
          drawLine(draw1, drawid + "invFrameZ", 0.15f, ptinv, ptemp,
              "translucent blue");
        }
      }

      // and display translation if still not {0 0 0}

      if (trans != null) {
        if (pt0 == null)
          pt0 = new Point3f(pt00);
        draw1.append(drawid).append("transVector vector ").append(
            Escape.escape(pt0)).append(Escape.escape(trans));
      }

      // color the targeted atoms opaque and add another frame if necessary

      draw1.append("\nvar pt00 = " + Escape.escape(pt00));
      draw1.append("\nvar p0 = " + Escape.escape(p0));
      draw1.append("\nif (within(0.2,p0).length == 0) {");
      draw1.append("\nvar set2 = within(0.2,p0.uxyz.xyz)");
      draw1.append("\nif (set2) {");
      draw1.append(drawid)
          .append("cellOffsetVector arrow @p0 @set2 color grey");
      draw1.append(drawid).append(
          "offsetFrameX diameter 0.20 @{set2.xyz} @{set2.xyz + ").append(
          Escape.escape(v01)).append("*0.9} color red");
      draw1.append(drawid).append(
          "offsetFrameY diameter 0.20 @{set2.xyz} @{set2.xyz + ").append(
          Escape.escape(v02)).append("*0.9} color green");
      draw1.append(drawid).append(
          "offsetFrameZ diameter 0.20 @{set2.xyz} @{set2.xyz + ").append(
          Escape.escape(v03)).append("*0.9} color purple");
      draw1.append("\n}}\n");

      cmds = draw1.toString();
      draw1 = null;
      drawid = null;
    }
    if (trans == null)
      ftrans = null;
    if (isrotation) {
      if (haveinversion) {
      } else if (pitch1 == 0) {
      } else {
        // screw
        trans = new Vector3f(ax1);
        ptemp.set(trans);
        uc.toFractional(ptemp, false);
        ftrans = new Vector3f(ptemp);
      }
      if (haveinversion && pitch1 != 0) {
      }
    }
    if (ismirrorplane) {
      if (trans != null) {
      }
      ang1 = 0;
    }
    if (haveinversion) {
      if (isinversion) {
        pa1 = null;
        ax1 = null;
        trans = null;
        ftrans = null;
      }
    } else if (istranslation) {
      pa1 = null;
      ax1 = null;
    }

    // and display translation if still not {0 0 0}
    if (ax1 != null)
      ax1.normalize();
    Matrix4f m2 = null;
    if (m != null) {
      m2 = new Matrix4f(m);
      if (vtrans.length() != 0) {
        m2.m03 += vtrans.x;
        m2.m13 += vtrans.y;
        m2.m23 += vtrans.z;
      }
      xyz = getXYZFromMatrix(m2, false, false, false);
    }
    return new Object[] { xyz, xyzOriginal, info1, cmds, approx0(ftrans),
        approx0(trans), approx0(ipt), approx0(pa1), approx0(ax1),
        Integer.valueOf(ang1), m2, vtrans };
  }

  private static void drawLine(StringBuffer s, String id, float diameter, Point3f pt0, Point3f pt1,
                        String color) {
    s.append(id).append(" diameter ").append(diameter)
        .append(Escape.escape(pt0)).append(Escape.escape(pt1))
        .append(" color ").append(color);
  }

  private static String fcoord(Tuple3f p) {
    return fc(p.x) + " " + fc(p.y) + " " + fc(p.z);
  }

  private static String fc(float x) {
    float xabs = Math.abs(x);
    int x24 = (int) approx(xabs * 24);
    String m = (x < 0 ? "-" : "");
    if (x24%8 != 0)
      return m + twelfthsOf(x24 >> 1);
    return (x24 == 0 ? "0" : x24 == 24 ? m + "1" : m + (x24/8) + "/3");
  }

  private static Tuple3f approx0(Tuple3f pt) {
    if (pt != null) {
      if (Math.abs(pt.x) < 0.0001f)
        pt.x = 0;
      if (Math.abs(pt.y) < 0.0001f)
        pt.y = 0;
      if (Math.abs(pt.z) < 0.0001f)
        pt.z = 0;
    }
    return pt;
  }
  
  private static Tuple3f approx(Tuple3f pt) {
    if (pt != null) {
      pt.x = approx(pt.x);
      pt.y = approx(pt.y);
      pt.z = approx(pt.z);
    }
    return pt;
  }
  
  private static float approx(float f) {
    return approx(f, 100);
  }

  private static float approx(float f, float n) {
    return ((int) (f * n + 0.5f * (f < 0 ? -1 : 1)) / n);
  }

  public static void normalizeTranslation(Matrix4f operation) {
    operation.m03 = ((int)operation.m03 + 12) % 12;
    operation.m13 = ((int)operation.m13 + 12) % 12;
    operation.m23 = ((int)operation.m23 + 12) % 12;    
  }

}
