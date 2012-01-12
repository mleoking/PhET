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
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.modelset.Atom;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Quaternion;
import org.jmol.util.TextFormat;

/*
 * Bob Hanson 7/2008
 * 
 * brute force -- preliminary from BCCE20 meeting 2008
 * 
 * NEVER ACCESS THESE METHODS DIRECTLY! ONLY THROUGH CLASS Symmetry
 * 
 *
 */

class PointGroup {

  private final static int[] axesMaxN = new int[] { 
     15, // used for plane count
      0, // n/a 
      0, // not used -- would be S2 (inversion)
      1, // S3
      3, // S4
      1, // S5
      10,// S6
      0, // n/a
      1, // S8
      0, // n/a
      6, // S10
      0, // n/a 
      1, // S12
      0, // n/a
      0, // n/a firstProper = 14
      0, // n/a 
      15,// C2 
      10,// C3 
      6, // C4
      6, // C5
      10,// C6
      0, // C7
      1, // C8
  };

  private final static int[] nUnique = new int[] { 
     1, // used for plane count
     0, // n/a 
     0, // not used -- would be S2 (inversion)
     2, // S3
     2, // S4
     4, // S5
     2, // S6
     0, // n/a
     4, // S8
     0, // n/a
     4, // S10
     0, // n/a 
     4, // S12
     0, // n/a
     0, // n/a firstProper = 14
     0, // n/a 
     1, // C2 
     2, // C3 
     2, // C4
     4, // C5
     2, // C6
     0, // C7
     4, // C8
 };

  private final static int s3 = 3;
  private final static int s4 = 4;
  private final static int s5 = 5;
  private final static int s6 = 6;
  private final static int s8 = 8;
  private final static int s10 = 10;
  private final static int s12 = 12;
  private final static int firstProper = 14;
  private final static int c2 = firstProper + 2;
  private final static int c3 = firstProper + 3;
  private final static int c4 = firstProper + 4;
  private final static int c5 = firstProper + 5;
  private final static int c6 = firstProper + 6;
  private final static int c8 = firstProper + 8;
  private final static int maxAxis = axesMaxN.length;

  private int[]  nAxes = new int[maxAxis];
  private Operation[][] axes = new Operation[maxAxis][];
  private int nAtoms;
  private float radius;
  private float distanceTolerance = 0.2f;
  private float linearTolerance = 8f;
  private float cosTolerance = 0.99f; // 8 degrees
  private String name = "C_1?";
  private Operation principalAxis;
  private Operation principalPlane;

  String getName() {
    return name;
  }

  private final Vector3f vTemp = new Vector3f();
  private int centerAtomIndex = -1;
  private boolean haveInversionCenter;
  
  final private Point3f center = new Point3f();

  private Point3f[] atoms;
  private int[] elements;

  private BitSet bsAtoms;
  
  static PointGroup getPointGroup(PointGroup pgLast, Atom[] atomset,
                                         BitSet bsAtoms, boolean haveVibration,
                                         float distanceTolerance,
                                         float linearTolerance) {
    PointGroup pg = new PointGroup();
    return (pg.set(pgLast, atomset, bsAtoms, haveVibration, distanceTolerance,
        linearTolerance) ? pg : pgLast);
  }

  private PointGroup() {
  }
  
  private boolean isEqual(PointGroup pg) {
    if (pg == null)
      return false;
    if (this.linearTolerance != pg.linearTolerance 
        || this.distanceTolerance != pg.distanceTolerance
        || this.nAtoms != pg.nAtoms || this.bsAtoms.equals(pg.bsAtoms))
      return false;
    for (int i = 0; i < nAtoms; i++) {
      // real floating == 0 here because they must be IDENTICAL POSITIONS
      if (elements[i] != pg.elements[i] || atoms[i].distance(pg.atoms[i]) != 0)
        return false;
    }
    return true;
  }
  
  private boolean set(PointGroup pgLast, Atom[] atomset, BitSet bsAtoms,
      boolean haveVibration, float distanceTolerance, float linearTolerance) {
    this.distanceTolerance = distanceTolerance;
    this.linearTolerance = linearTolerance;
    this.bsAtoms = bsAtoms;
    cosTolerance = (float) (Math.cos(linearTolerance / 180 * Math.PI));
    if (!getAtomsAndElements(atomset, bsAtoms)) {
      Logger.error("Too many atoms for point group calculation");
      name = "point group not determined -- atomCount > " + ATOM_COUNT_MAX
          + " -- select fewer atoms and try again.";
      return true;
    }
    getElementCounts();
    if (haveVibration) {
      Point3f[] atomVibs = new Point3f[atoms.length];
      for (int i = atoms.length; --i >= 0;) {
        atomVibs[i] = new Point3f(atoms[i]);
        Vector3f v = ((Atom) atoms[i]).getVibrationVector();
        if (v != null)
          atomVibs[i].add(v);
      }
      atoms = atomVibs;
    }
    if (isEqual(pgLast))
      return false;
    findInversionCenter();

    if (isLinear(atoms)) {
      if (haveInversionCenter) {
        name = "D(infinity)h";
      } else {
        name = "C(infinity)v";
      }
      vTemp.sub(atoms[1], atoms[0]);
      addAxis(c2, vTemp);
      principalAxis = axes[c2][0];
      if (haveInversionCenter) {
        axes[0] = new Operation[1];
        principalPlane = axes[0][nAxes[0]++] = new Operation(vTemp);
      }
      return true;
    }
    axes[0] = new Operation[15];
    int nPlanes = 0;
    findCAxes();
    nPlanes = findPlanes();
    findAdditionalAxes(nPlanes);

    /* flow chart contribution of Dean Johnston */

    int n = getHighestOrder();
    if (nAxes[c3] > 1) {
      // must be Ix, Ox, or Tx
      if (nAxes[c5] > 1) {
        if (haveInversionCenter) {
          name = "Ih";
        } else {
          name = "I";
        }
      } else if (nAxes[c4] > 1) {
        if (haveInversionCenter) {
          name = "Oh";
        } else {
          name = "O";
        }
      } else {
        if (nPlanes > 0) {
          if (haveInversionCenter) {
            name = "Th";
          } else {
            name = "Td";
          }
        } else {
          name = "T";
        }
      }
    } else {
      // Cs, Ci, C1
      if (n < 2) {
        if (nPlanes == 1) {
          name = "Cs";
          return true;
        }
        if (haveInversionCenter) {
          name = "Ci";
          return true;
        }
        name = "C1";
      } else if ((n % 2) == 1 && nAxes[c2] > 0 || (n % 2) == 0 && nAxes[c2] > 1) {
        // Dnh, Dnd, Dn, S4

        // here based on the presence of C2 axes in any odd-order group
        // and more than one C2 if even order (since the one will be part of the 
        // principal axis

        principalAxis = setPrincipalAxis(n, nPlanes);
        if (nPlanes == 0) {
          if (n < firstProper) {
            name = "S" + n;
          } else {
            name = "D" + (n - firstProper);
          }
        } else {
          // highest axis may be S8, but this is really D4h/D4d
          if (n < firstProper)
            n = n / 2;
          else
            n -= firstProper;
          if (nPlanes == n) {
            name = "D" + n + "d";
          } else {
            name = "D" + n + "h";
          }
        }
      } else if (nPlanes == 0) {
        // Cn, S3, S6 
        principalAxis = axes[n][0];
        if (n < firstProper) {
          name = "S" + n;
        } else {
          name = "C" + (n - firstProper);
        }
      } else if (nPlanes == n - firstProper) {
        principalAxis = axes[n][0];
        name = "C" + nPlanes + "v";
      } else {
        principalAxis = axes[n < firstProper ? n + firstProper : n][0];
        principalPlane = axes[0][0];
        if (n < firstProper)
          n /= 2;
        else
          n -= firstProper;
        name = "C" + n + "h";
      }
    }
    return true;
  }

  private Operation setPrincipalAxis(int n, int nPlanes) {
    Operation principalPlane = setPrincipalPlane(n, nPlanes);
    if (nPlanes == 0 && n < firstProper || nAxes[n] == 1) {
      if (nPlanes > 0 && n < firstProper)
        n = firstProper + n / 2;
        return axes[n][0];
    }
    // D2, D2d, D2h -- which c2 axis is it?
    if (principalPlane == null)
      return null;
    for (int i = 0; i < nAxes[c2]; i++)
      if (isParallel(principalPlane.normalOrAxis, axes[c2][i].normalOrAxis)) {
        if (i != 0) {
          Operation o = axes[c2][0];
          axes[c2][0] = axes[c2][i];
          axes[c2][i] = o;
        }
        return axes[c2][0];
      }
    return null;
  }

  private Operation setPrincipalPlane(int n, int nPlanes) {
    // principal plane is perpendicular to more than two other planes
    if (nPlanes == 1)
      return principalPlane = axes[0][0];
    if (nPlanes == 0 || nPlanes == n - firstProper)
      return null;
    for (int i = 0; i < nPlanes; i++)
      for (int j = 0, nPerp = 0; j < nPlanes; j++)
        if (isPerpendicular(axes[0][i].normalOrAxis, axes[0][j].normalOrAxis) && ++nPerp > 2) {
          if (i != 0) {
            Operation o = axes[0][0];
            axes[0][0] = axes[0][i];
            axes[0][i] = o;
          }
          return principalPlane = axes[0][0];
        }
    return null;
  }

  private final static int ATOM_COUNT_MAX = 100;
  private boolean getAtomsAndElements(Atom[] atomset, BitSet bsAtoms) {
    int atomCount = BitSetUtil.cardinalityOf(bsAtoms);
    if (atomCount > ATOM_COUNT_MAX)
      return false;
    Point3f[] atoms = this.atoms = new Point3f[atomCount];
    elements = new int[atomCount];
    if (atomCount == 0) 
      return true;
    nAtoms = 0;
    for (int i = bsAtoms.nextSetBit(0); i >= 0; i = bsAtoms.nextSetBit(i + 1)) {
        atoms[nAtoms] = new Point3f(atomset[i]);
        int bondIndex = 1 + Math.max(3, atomset[i].getCovalentBondCount());
        elements[nAtoms] = atomset[i].getElementNumber() * bondIndex;
        center.add(atoms[nAtoms++]);
      }
    center.scale(1f / nAtoms);
    for (int i = nAtoms; --i >= 0;) {
      float r = center.distance(atoms[i]);
      if (r < distanceTolerance)
        centerAtomIndex = i;
      radius = Math.max(radius, r);
    }
    return true;
  }

  private void findInversionCenter() {
    haveInversionCenter = checkOperation(null, center, -1);
    if (haveInversionCenter) {
      axes[1] = new Operation[1];
      axes[1][0] = new Operation();
    }
  }

  private boolean checkOperation(Quaternion q, Point3f center, int iOrder) {
    Point3f pt = new Point3f();
    int nFound = 0;
    boolean isInversion = (iOrder < firstProper);
    out: for (int i = atoms.length; --i >= 0 && nFound < atoms.length;)
      if (i == centerAtomIndex) {
        nFound++;
      } else {
        Point3f a1 = atoms[i];
        int e1 = elements[i];
        if (q != null) {
          pt.set(a1);
          pt.sub(center);
          q.transform(pt, pt);
          pt.add(center);
        } else {
          pt.set(a1);
        }
        if (isInversion) {
          // A trick here: rather than 
          // actually doing a rotation/reflection
          // we do a rotation INVERSION. This works
          // due to the symmetry of S2, S4, and S8
          // For S3 and S6, we play the trick of 
          // rotating as C6 and C3, respectively, 
          // THEN doing the rotation/inversion. 
          vTemp.sub(center, pt);
          pt.scaleAdd(2, vTemp, pt);
        }
        if ((q != null || isInversion) && pt.distance(a1) < distanceTolerance) {
          nFound++;
          continue;
        }
        for (int j = atoms.length; --j >= 0;) {
          if (j == i || elements[j] != e1)
            continue;
          Point3f a2 = atoms[j];
          if (pt.distance(a2) < distanceTolerance) {
            nFound++;
            continue out;
          }
        }
      }
    return nFound == atoms.length;
  }

  private boolean isLinear(Point3f[] atoms) {
    Vector3f v1 = null;
    if (atoms.length < 2)
      return false;
    for (int i = atoms.length; --i >= 0;) {
      if (i == centerAtomIndex)
        continue;
      if (v1 == null) {
        v1 = new Vector3f();
        v1.sub(atoms[i], center);
        v1.normalize();
        vTemp.set(v1);
        continue;
      }
      vTemp.sub(atoms[i], center);
      vTemp.normalize();
      if (!isParallel(v1, vTemp))
        return false;
    }
    return true;
  }

  private boolean isParallel(Vector3f v1, Vector3f v2) {
    // note -- these MUST be unit vectors
    return (Math.abs(v1.dot(v2)) >= cosTolerance);
  }

  private boolean isPerpendicular(Vector3f v1, Vector3f v2) {
    // note -- these MUST be unit vectors
    return (Math.abs(v1.dot(v2)) <= 1 - cosTolerance);
  }

  int maxElement = 0;
  int[] eCounts;

  private void getElementCounts() {
    for (int i = atoms.length; --i >= 0;) {
      int e1 = elements[i];
      if (e1 > maxElement)
        maxElement = e1;
    }
    eCounts = new int[++maxElement];
    for (int i = atoms.length; --i >= 0;)
      eCounts[elements[i]]++; 
  }

  private int findCAxes() {
    Vector3f v1 = new Vector3f();
    Vector3f v2 = new Vector3f();
    Vector3f v3 = new Vector3f();

    // look for the proper and improper axes relating pairs of atoms

    for (int i = atoms.length; --i >= 0;) {
      if (i == centerAtomIndex)
        continue;
      Point3f a1 = atoms[i];
      int e1 = elements[i];
      for (int j = atoms.length; --j > i;) {
        Point3f a2 = atoms[j];
        if (elements[j] != e1)
          continue;

        // check if A - 0 - B is linear

        v1.sub(a1, center);
        v2.sub(a2, center);
        v1.normalize();
        v2.normalize();
        if (isParallel(v1, v2)) {
          getAllAxes(v1);
          continue;
        }

        // look for all axes to average position of A and B

        if (nAxes[c2] < axesMaxN[c2]) {
          v3.set(a1);
          v3.add(a2);
          v3.scale(0.5f);
          v3.sub(center);
          getAllAxes(v3);
        }

        // look for the axis perpendicular to the A -- 0 -- B plane

        float order = (float) (2 * Math.PI / v1.angle(v2));
        int iOrder = (int) (order + 0.01f);
        boolean isIntegerOrder = (order - iOrder <= 0.02f);
        if (!isIntegerOrder || (iOrder = iOrder + firstProper) >= maxAxis)
          continue;
        if (nAxes[iOrder] < axesMaxN[iOrder]) {
          v3.cross(v1, v2);
          checkAxisOrder(iOrder, v3, center);
        }
      }
    }

    // check all C2 axes for C3-related axes

    Vector3f[] vs = new Vector3f[nAxes[c2] * 2];
    for (int i = 0; i < vs.length; i++)
      vs[i] = new Vector3f();
    int n = 0;
    for (int i = 0; i < nAxes[c2]; i++) {
      vs[n++].set(axes[c2][i].normalOrAxis);
      vs[n].set(axes[c2][i].normalOrAxis);
      vs[n++].scale(-1);
    }
    for (int i = vs.length; --i >= 2;)
      for (int j = i; --j >= 1;)
        for (int k = j; --k >= 0;) {
          v3.set(vs[i]);
          v3.add(vs[j]);
          v3.add(vs[k]);
          if (v3.length() < 1.0)
            continue;
          checkAxisOrder(c3, v3, center);
        }

    // Now check for triples of elements that will define
    // axes using the element with the smallest
    // number of atoms n, with n >= 3 
    // cross all triples of vectors looking for standard
    // principal axes quantities.

    // Also check for vectors from {0 0 0} to
    // the midpoint of each triple of atoms

    // get minimum element count > 2

    int nMin = Integer.MAX_VALUE;
    int iMin = -1;
    for (int i = 0; i < maxElement; i++) {
      if (eCounts[i] < nMin && eCounts[i] > 2) {
        nMin = eCounts[i];
        iMin = i;
      }
    }

    out: for (int i = 0; i < atoms.length - 2; i++)
      if (elements[i] == iMin)
        for (int j = i + 1; j < atoms.length - 1; j++)
          if (elements[j] == iMin)
            for (int k = j + 1; k < atoms.length; k++)
              if (elements[k] == iMin) {
                v1.sub(atoms[i], atoms[j]);
                v2.sub(atoms[i], atoms[k]);
                v1.normalize();
                v2.normalize();
                v3.cross(v1, v2);
                getAllAxes(v3);
//                checkAxisOrder(3, v3, center);
                v1.set(atoms[i]);
                v1.add(atoms[j]);
                v1.add(atoms[k]);
                v1.normalize();
                if (!isParallel(v1, v3))
                  getAllAxes(v1);
                if (nAxes[c5] == axesMaxN[c5])
                  break out;
              }

    //check for C2 by looking for axes along element-based geometric centers

    vs = new Vector3f[maxElement];
    for (int i = atoms.length; --i >= 0;) {
      int e1 = elements[i];
      if (vs[e1] == null)
        vs[e1] = new Vector3f();
      else if (haveInversionCenter)
        continue;
      vs[e1].add(atoms[i]);
    }
    if (!haveInversionCenter)
      for (int i = 0; i < maxElement; i++)
        if (vs[i] != null)
          vs[i].scale(1f / eCounts[i]);

    // check for vectors from {0 0 0} to
    // the midpoint of each pair of atoms
    // within the same element if there is no inversion center,
    // otherwise, check for cross-product axis

    for (int i = 0; i < maxElement; i++)
      if (vs[i] != null)
        for (int j = 0; j < maxElement; j++) {
          if (i == j || vs[j] == null)
            continue;
          if (haveInversionCenter) {
           v1.cross(vs[i], vs[j]);
          } else {
            v1.set(vs[i]);
            v1.sub(vs[j]);
          }
          checkAxisOrder(c2, v1, center);
          
        }

    return getHighestOrder();
  }

  private void getAllAxes(Vector3f v3) {
    for (int o = c2; o < maxAxis; o++)
      if (nAxes[o] < axesMaxN[o])
        checkAxisOrder(o, v3, center);
  }

  private int getHighestOrder() {
    int n = 0;
    // highest S
    for (n = firstProper; --n > 1 && nAxes[n] == 0;) {
    }
    // or highest C
    if (n > 1)
      return (n + firstProper < maxAxis && nAxes[n + firstProper] > 0 ? n + firstProper : n);
    for (n = maxAxis; --n > 1 && nAxes[n] == 0;) {
    }
    return n;
  }

  private boolean checkAxisOrder(int iOrder, Vector3f v, Point3f center) {
    switch (iOrder) {
    case c8:
      if (nAxes[c3] > 0)
        return false;
      // fall through;
    case c6:
    case c4:
      if (nAxes[c5] > 0)
        return false;
      break;
    case c3:
      if (nAxes[c8] > 0)
        return false;
      break;
    case c5:
      if (nAxes[c4] > 0 || nAxes[c6] > 0 || nAxes[c8] > 0) 
        return false;
      break;
    }

    v.normalize();
    if (haveAxis(iOrder, v))
      return false;
    Quaternion q = new Quaternion(v, (iOrder < firstProper ? 180 : 0) + 360 / (iOrder % firstProper));
    if (!checkOperation(q, center, iOrder))
      return false;
    addAxis(iOrder, v);
    // check for Sn:
    switch (iOrder) {
    case c2:
      checkAxisOrder(s4, v, center);//D2d, D4h, D6d
      break;
    case c3:
      checkAxisOrder(s3, v, center);//C3h, D3h
      if (haveInversionCenter)
        addAxis(s6, v);
      break;
    case c4:
      addAxis(c2, v);
      checkAxisOrder(s4, v, center);//D2d, D4h, D6d
      checkAxisOrder(s8, v, center);//D4d
      break;
    case c5:
      checkAxisOrder(s5, v, center); //C5h, D5h
      if (haveInversionCenter)
        addAxis(s10, v);
      break;
    case c6:
      addAxis(c2, v);
      addAxis(c3, v);
      checkAxisOrder(s3, v, center);//C6h, D6h
      checkAxisOrder(s6, v, center);//C6h, D6h
      checkAxisOrder(s12, v, center);//D6d
      break;
    case c8:
      //note -- D8d would have a S16 axis. This will not be found.
      addAxis(c2, v);
      addAxis(c4, v);
      break;
    }
    return true;
  }

  private void addAxis(int iOrder, Vector3f v) {
    if (haveAxis(iOrder, v))
      return;
    if (axes[iOrder] == null)
      axes[iOrder] = new Operation[axesMaxN[iOrder]];
    axes[iOrder][nAxes[iOrder]++] = new Operation(v, iOrder);
  }

  private boolean haveAxis(int iOrder, Vector3f v) {
    if (nAxes[iOrder] == axesMaxN[iOrder]) {
      return true;
    }
    if (nAxes[iOrder] > 0)
      for (int i = nAxes[iOrder]; --i >= 0;) {
        if (isParallel(v, axes[iOrder][i].normalOrAxis))
          return true;
      }
    return false;
  }

  private int findPlanes() {
    Point3f pt = new Point3f();
    Vector3f v1 = new Vector3f();
    Vector3f v2 = new Vector3f();
    Vector3f v3 = new Vector3f();
    int nPlanes = 0;
    boolean haveAxes = (getHighestOrder() > 1);
    for (int i = atoms.length; --i >= 0;) {
      if (i == centerAtomIndex)
        continue;
      Point3f a1 = atoms[i];
      int e1 = elements[i];
      for (int j = atoms.length; --j > i;) {
        if (haveAxes && elements[j] != e1)
          continue;

        // plane are treated as S2 axes here

        // first, check planes through two atoms and the center
        // or perpendicular to a linear A -- 0 -- B set

        Point3f a2 = atoms[j];
        pt.add(a1, a2);
        pt.scale(0.5f);
        v1.sub(a1, center);
        v2.sub(a2, center);
        if (!isParallel(v1, v2)) {
          v3.cross(v1, v2);
          v3.normalize();
          nPlanes = getPlane(v3);
        }

        // second, look for planes perpendicular to the A -- B line

        v3.set(a2);
        v3.sub(a1);
        v3.normalize();
        nPlanes = getPlane(v3);
        if (nPlanes == axesMaxN[0])
          return nPlanes;
      }
    }

    // also look for planes normal to any C axis
    if (haveAxes)
      for (int i = c2; i < maxAxis; i++)
        for (int j = 0; j < nAxes[i]; j++)
          nPlanes = getPlane(axes[i][j].normalOrAxis);
    return nPlanes;
  }

  private int getPlane(Vector3f v3) {
    if (!haveAxis(0, v3)
        && checkOperation(new Quaternion(v3, 180), center,
            -1))
      axes[0][nAxes[0]++] = new Operation(v3);
    return nAxes[0];
  }

  private void findAdditionalAxes(int nPlanes) {

    Operation[] planes = axes[0];
    int Cn = 0;
    if (nPlanes > 1
        && ((Cn = nPlanes + firstProper) < maxAxis) 
        && nAxes[Cn] == 0) {
      // cross pairs of plane normals. We don't need many.
      vTemp.cross(planes[0].normalOrAxis, planes[1].normalOrAxis);
      if (!checkAxisOrder(Cn, vTemp, center)
          && nPlanes > 2) {
        vTemp.cross(planes[1].normalOrAxis, planes[2].normalOrAxis);
        checkAxisOrder(Cn - 1, vTemp, center);
      }
    }
    if (nAxes[c2] == 0 && nPlanes > 2) {
      // check for C2 axis relating 
      for (int i = 0; i < nPlanes - 1; i++) {
        for (int j = i + 1; j < nPlanes; j++) {
          vTemp.add(planes[1].normalOrAxis, planes[2].normalOrAxis);
          //if (
          checkAxisOrder(c2, vTemp, center);
          //)
            //Logger.error("found a C2 axis by adding plane normals");
        }
      }
    }
  }

  final static int OPERATION_PLANE = 0;
  final static int OPERATION_PROPER_AXIS = 1;
  final static int OPERATION_IMPROPER_AXIS = 2;
  final static int OPERATION_INVERSION_CENTER = 3;

  final static String[] typeNames = { "plane", "proper axis", "improper axis",
      "center of inversion" };

  int nOps = 0;
  private class Operation {
    int type;
    int order;
    int index;
    Vector3f normalOrAxis;

    Operation() {
      index = ++nOps;
      type = OPERATION_INVERSION_CENTER;
      order = 1;
      if (Logger.debugging)
        Logger.info("new operation -- " + typeNames[type]);
    }

    Operation(Vector3f v, int i) {
      index = ++nOps;
      type = (i < firstProper ? OPERATION_IMPROPER_AXIS : OPERATION_PROPER_AXIS);
      order = i % firstProper;
      normalOrAxis = new Quaternion(v, 180).getNormal();
      if (Logger.debugging)
        Logger.info("new operation -- " + (order == i ? "S" : "C") + order + " "
            + normalOrAxis);
    }

    Operation(Vector3f v) {
      if (v == null)
        return;
      index = ++nOps;
      type = OPERATION_PLANE;
      normalOrAxis = new Quaternion(v, 180).getNormal();
      if (Logger.debugging)
        Logger.info("new operation -- plane " + normalOrAxis);
    }

    String getLabel() {
      switch (type) {
      case OPERATION_PLANE:
        return "Cs";
      case OPERATION_IMPROPER_AXIS:
        return "S" + order;
      default:
        return "C" + order;
      }
    }
  }

  String drawInfo;
  String drawType = "";
  int drawIndex;
  Map<String, Object> info;
  String textInfo;
  
  Object getInfo(int modelIndex, boolean asDraw, boolean asInfo, String type,
                 int index, float scaleFactor) {
    info = (asInfo ? new Hashtable<String, Object>() : null);
    Vector3f v = new Vector3f();
    Operation op;
    if (scaleFactor == 0)
      scaleFactor = 1;
    int[][] nType = new int[4][2];
    for (int i = 1; i < maxAxis; i++)
      for (int j = nAxes[i]; --j >= 0;)
        nType[axes[i][j].type][0]++;
    StringBuffer sb = new StringBuffer("# " + nAtoms + " atoms\n");
    if (asDraw) {
      boolean haveType = (type != null && type.length() > 0);
      drawType = type = (haveType ? type : "");
      drawIndex = index;
      boolean anyProperAxis = (type.equalsIgnoreCase("Cn"));
      boolean anyImproperAxis = (type.equalsIgnoreCase("Sn"));
      sb.append("set perspectivedepth off;\n");
      String m = "_" + modelIndex + "_";
      if (!haveType)
        sb.append("draw pg0").append(m).append("* delete;draw pgva").append(m
           ).append("* delete;draw pgvp").append(m).append("* delete;");
      if (!haveType || type.equalsIgnoreCase("Ci"))
        sb.append("draw pg0").append(m).append(
            haveInversionCenter ? "inv " : " ").append(
            Escape.escape(center)).append(haveInversionCenter ? "\"i\";\n" : ";\n");
      float offset = 0.1f;
      for (int i = 2; i < maxAxis; i++) {
        if (i == firstProper)
          offset = 0.1f;
        if (nAxes[i] == 0)
          continue;
        String label = axes[i][0].getLabel();
        offset += 0.25f;
        float scale = scaleFactor * radius + offset;
        if (!haveType || type.equalsIgnoreCase(label) || anyProperAxis
            && i >= firstProper || anyImproperAxis && i < firstProper)
          for (int j = 0; j < nAxes[i]; j++) {
            if (index > 0 && j + 1 != index)
              continue;
            op = axes[i][j];
            v.set(op.normalOrAxis);
            v.add(center);
            if (op.type == OPERATION_IMPROPER_AXIS)
              scale = -scale;
            sb.append("draw pgva").append(m).append(label).append("_").append(
                j + 1).append(" width 0.05 scale ").append(scale).append(" ").append(
                Escape.escape(v));
            v.scaleAdd(-2, op.normalOrAxis, v);
            boolean isPA = (principalAxis != null && op.index == principalAxis.index);
            sb.append(Escape.escape(v)).append(
                "\"").append(label).append(isPA ? "*" : "").append("\" color ").append(
                isPA ? "red" : op.type == OPERATION_IMPROPER_AXIS ? "blue"
                    : "yellow").append(";\n");
          }
      }
      if (!haveType || type.equalsIgnoreCase("Cs"))
        for (int j = 0; j < nAxes[0]; j++) {
          if (index > 0 && j + 1 != index)
            continue;
          op = axes[0][j];
          sb.append("draw pgvp").append(m).append(j + 1).append(
              "disk scale ").append(scaleFactor * radius * 2).append(" CIRCLE PLANE ")
              .append(Escape.escape(center));
          v.set(op.normalOrAxis);
          v.add(center);
          sb.append(Escape.escape(v)).append(" color translucent yellow;\n");
          v.set(op.normalOrAxis);
          v.add(center);
          sb.append("draw pgvp").append(m).append(j + 1).append(
              "ring width 0.05 scale ").append(scaleFactor * radius * 2).append(" arc ")
              .append(Escape.escape(v));
          v.scaleAdd(-2, op.normalOrAxis, v);
          sb.append(Escape.escape(v));
          v.x += 0.011;
          v.y += 0.012;
          v.z += 0.013;
          sb
              .append(Escape.escape(v))
              .append("{0 360 0.5} color ")
              .append(
                  principalPlane != null && op.index == principalPlane.index ? "red"
                      : "blue").append(";\n");
        }
      sb.append("# name=").append(name);
      sb.append(", nCi=").append(haveInversionCenter ? 1 : 0);
      sb.append(", nCs=").append(nAxes[0]);
      sb.append(", nCn=").append(nType[OPERATION_PROPER_AXIS][0]);
      sb.append(", nSn=").append(nType[OPERATION_IMPROPER_AXIS][0]);
      sb.append(": ");
      for (int i = maxAxis; --i >= 2;)
        if (nAxes[i] > 0) {
          sb.append(" n").append(i < firstProper ? "S" : "C").append(i % firstProper);
          sb.append("=").append(nAxes[i]);
        }
      sb.append(";\n");
      drawInfo = sb.toString();
      return drawInfo;
    }
    int n = 0;
    int nTotal = 1;
    String ctype = (haveInversionCenter ? "Ci" : "center");
    if (haveInversionCenter)
      nTotal++;
    if (info == null)
      sb.append("\n\n").append(name).append("\t").append(ctype).append("\t").append(Escape.escape(center));
    else
      info.put(ctype, center);
    for (int i = maxAxis; --i >= 0;) {
      if (nAxes[i] > 0) {
        n = nUnique[i];
        String label = axes[i][0].getLabel();
        if (info == null)
          sb.append("\n\n").append(name).append("\tn").append(label).append("\t").append(nAxes[i]).append("\t").append(n);
        else
          info.put("n" + label, Integer.valueOf(nAxes[i]));
        n *= nAxes[i];
        nTotal += n;
        nType[axes[i][0].type][1] += n;
        List<Vector3f> vinfo = (info == null ? null : new ArrayList<Vector3f>());
        for (int j = 0; j < nAxes[i]; j++) {
          //axes[i][j].typeIndex = j + 1;
          if (vinfo == null)
            sb.append("\n").append(name).append("\t").append(label).append("_").append(j + 1).append("\t"
               ).append(axes[i][j].normalOrAxis);
          else
            vinfo.add(axes[i][j].normalOrAxis);
        }
        if (info != null)
          info.put(label, vinfo);
      }
    }
    
    if (info == null) {
      sb.append("\n");
      sb.append("\n").append(name).append("\ttype\tnType\tnUnique");
      sb.append("\n").append(name).append("\tE\t  1\t  1");

      n = (haveInversionCenter ? 1 : 0);
      sb.append("\n").append(name).append("\tCi\t  ").append(n).append("\t  ").append(n);

      sb.append("\n").append(name).append("\tCs\t");
      TextFormat.rFill(sb, "    ", nAxes[0] + "\t");
      TextFormat.rFill(sb, "    ", nAxes[0] + "\n");

      sb.append(name).append("\tCn\t");
      TextFormat.rFill(sb, "    ", nType[OPERATION_PROPER_AXIS][0] + "\t");
      TextFormat.rFill(sb, "    ", nType[OPERATION_PROPER_AXIS][1] + "\n");

      sb.append(name).append("\tSn\t");
      TextFormat.rFill(sb, "    ", nType[OPERATION_IMPROPER_AXIS][0] + "\t");
      TextFormat.rFill(sb, "    ", nType[OPERATION_IMPROPER_AXIS][1] + "\n");

      sb.append(name).append("\t\tTOTAL\t");
      TextFormat.rFill(sb, "    ", nTotal + "\n");
      textInfo = sb.toString();
      return textInfo;
    }
    info.put("name", name);
    info.put("nAtoms", new Integer(nAtoms));
    info.put("nTotal", new Integer(nTotal));
    info.put("nCi", new Integer(haveInversionCenter ? 1 : 0));
    info.put("nCs", new Integer(nAxes[0]));
    info.put("nCn", new Integer(nType[OPERATION_PROPER_AXIS][0]));
    info.put("nSn", new Integer(nType[OPERATION_IMPROPER_AXIS][0]));
    info.put("distanceTolerance", new Float(distanceTolerance));
    info.put("linearTolerance", new Float(linearTolerance));
    info.put("detail", sb.toString().replace('\n', ';'));
    if (principalAxis != null && principalAxis.index > 0)
      info.put("principalAxis", principalAxis.normalOrAxis);
    if (principalPlane != null && principalPlane.index > 0)
      info.put("principalPlane", principalPlane.normalOrAxis);
    return info;
  }

  boolean isDrawType(String type, int index) {
    return (drawInfo != null && drawType.equals(type == null ? "" : type) && drawIndex == index);
  }
  
}
