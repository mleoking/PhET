/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-11-23 12:49:25 -0600 (Fri, 23 Nov 2007) $
 * $Revision: 8655 $
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
 *  Lesser General License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.jmol.minimize.forcefield;

import java.util.List;
import java.util.Map;

import javax.vecmath.Vector3d;

import org.jmol.minimize.MinAtom;
import org.jmol.minimize.MinBond;
import org.jmol.minimize.Util;
import org.jmol.util.ArrayUtil;

abstract class Calculations {

  final public static double RAD_TO_DEG = (180.0 / Math.PI);
  final public static double DEG_TO_RAD = (Math.PI / 180.0);

  final static double KCAL_TO_KJ = 4.1868;

  final static int CALC_DISTANCE = 0; // do not change these
  final static int CALC_ANGLE = 1;    // first three numbers
  final static int CALC_TORSION = 2;
  final static int CALC_OOP = 3;
  final static int CALC_VDW = 4;
  final static int CALC_ES = 5;
  final static int CALC_MAX = 6;

  ForceField ff;
  List<Object[]>[] calculations = ArrayUtil.createArrayOfArrayList(CALC_MAX);
  public Map<String, FFParam> ffParams;
  
  int atomCount;
  int bondCount;
  MinAtom[] atoms;
  MinBond[] bonds;
  int[][] angles;
  int[][] torsions;
  double[] partialCharges;
  boolean havePartialCharges;
  List<Object[]> constraints;
  boolean isPreliminary;

  public void setConstraints(List<Object[]> constraints) {
    this.constraints = constraints;
  }

  Calculations(ForceField ff, MinAtom[] minAtoms, MinBond[] minBonds, 
      int[][] angles, int[][] torsions, double[] partialCharges, 
      List<Object[]> constraints) {
    this.ff = ff;
    atoms = minAtoms;
    bonds = minBonds;
    this.angles = angles;
    this.torsions = torsions;
    this.constraints = constraints;
    atomCount = atoms.length;
    bondCount = bonds.length;
    if (partialCharges != null && partialCharges.length == atomCount)
      for (int i = atomCount; --i >= 0;)
        if (partialCharges[i] != 0) {
          havePartialCharges = true;
          break;
        }
    if (!havePartialCharges)
      partialCharges = null;
    this.partialCharges = partialCharges;
  }

  boolean haveParams() {
    return (ffParams != null);
  }

  void setParams(Map<String, FFParam> temp) {
    ffParams = temp;
  }

  static FFParam getParameter(String a, Map<String, FFParam> ffParams) {
    return ffParams.get(a);
  }

  abstract boolean setupCalculations();

  abstract String getAtomList(String title);

  abstract boolean setupElectrostatics();

  abstract String getDebugHeader(int iType);

  abstract String getDebugFooter(int iType, double energy);

  abstract String getUnit();

  abstract double compute(int iType, Object[] dataIn);

  void addForce(Vector3d v, int i, double dE) {
    atoms[i].force[0] += v.x * dE;
    atoms[i].force[1] += v.y * dE;
    atoms[i].force[2] += v.z * dE;
  }

  boolean gradients;

  boolean silent;
  
  public void setSilent(boolean TF) {
    silent = TF;
  }
  
  StringBuffer logData = new StringBuffer();
  public String getLogData() {
    return logData.toString();
  }

  void appendLogData(String s) {
    logData.append(s).append("\n");
  }
  
  boolean logging;
  boolean loggingEnabled;
  
  void setLoggingEnabled(boolean TF) {
    loggingEnabled = TF;
    if (loggingEnabled)
      logData = new StringBuffer();
  }

  void setPreliminary(boolean TF) {
    isPreliminary = TF;
  }
  
  private double calc(int iType, boolean gradients) {
    logging = loggingEnabled && !silent;
    this.gradients = gradients;
    List<Object[]> calc = calculations[iType];
    int nCalc;
    double energy = 0;
    if (calc == null || (nCalc = calc.size()) == 0)
      return 0;
    if (logging)
      appendLogData(getDebugHeader(iType));
    for (int ii = 0; ii < nCalc; ii++)
      energy += compute(iType, calculations[iType].get(ii));
    if (logging)
      appendLogData(getDebugFooter(iType, energy));
    if (constraints != null && iType <= CALC_TORSION)
      energy += constraintEnergy(iType);
    return energy;
  }

  double energyStrBnd(@SuppressWarnings("unused") boolean gradients) {
    return 0.0f;
  }

  double energyBond(boolean gradients) {
    return calc(CALC_DISTANCE, gradients);
  }

  double energyAngle(boolean gradients) {
    return calc(CALC_ANGLE, gradients);
  }

  double energyTorsion(boolean gradients) {
    return calc(CALC_TORSION, gradients);
  }

  double energyOOP(boolean gradients) {
    return calc(CALC_OOP, gradients);
  }

  double energyVDW(boolean gradients) {
    return calc(CALC_VDW, gradients);
  }

  double energyES(boolean gradients) {
    return calc(CALC_ES, gradients);
  }
  
  final Vector3d da = new Vector3d();
  final Vector3d db = new Vector3d();
  final Vector3d dc = new Vector3d();
  final Vector3d dd = new Vector3d();
  int ia, ib, ic, id;

  final Vector3d v1 = new Vector3d();
  final Vector3d v2 = new Vector3d();
  final Vector3d v3 = new Vector3d();
  
  private final static double PI_OVER_2 = Math.PI / 2;
  private final static double TWO_PI = Math.PI * 2;
  
  private double constraintEnergy(int iType) {

    double value = 0;
    double k = 0;
    double energy = 0;

    for (int i = constraints.size(); --i >= 0; ) {
      Object[] c = constraints.get(i);
      int nAtoms = ((int[]) c[0])[0];
      if (nAtoms != iType + 2)
        continue;
      int[] minList = (int[]) c[1];
      double targetValue = ((Float)c[2]).doubleValue();

      switch (iType) {
      case CALC_TORSION:
        id = minList[3];
        if (gradients)
          dd.set(atoms[id].coord);
        //fall through
      case CALC_ANGLE:
        ic = minList[2];
        if (gradients)
          dc.set(atoms[ic].coord);
        //fall through
      case CALC_DISTANCE:
        ib = minList[1];
        ia = minList[0];
        if (gradients) {
          db.set(atoms[ib].coord);
          da.set(atoms[ia].coord);
        }
      }

      k = 10000.0;

      switch (iType) {
      case CALC_TORSION:
        targetValue *= DEG_TO_RAD;
        value = (gradients ? Util.restorativeForceAndTorsionAngleRadians(da, db, dc, dd)
            : Util.getTorsionAngleRadians(atoms[ia].coord, 
              atoms[ib].coord, atoms[ic].coord, atoms[id].coord, v1, v2, v3));
        if (value < 0 && targetValue >= PI_OVER_2)
          value += TWO_PI; 
        else if (value > 0 && targetValue <= -PI_OVER_2)
          targetValue += TWO_PI;
       break;
      case CALC_ANGLE:
        targetValue *= DEG_TO_RAD;
        value = (gradients ? Util.restorativeForceAndAngleRadians(da, db, dc)
            : Util.getAngleRadiansABC(atoms[ia].coord, atoms[ib].coord,
              atoms[ic].coord));
        break;
      case CALC_DISTANCE:
        value = (gradients ? Util.restorativeForceAndDistance(da, db, dc)
            : Math.sqrt(Util.distance2(atoms[ia].coord, atoms[ib].coord)));
        break;
      }
      energy += constrainQuadratic(value, targetValue, k, iType);
    }
    return energy;
  }

  private double constrainQuadratic(double value, double targetValue, double k, int iType) {

    if (!Util.isFinite(value))
      return 0;

    double delta = value - targetValue;

    if (gradients) {
      double dE = 2.0 * k * delta;
      switch(iType) {
      case CALC_TORSION:
        addForce(dd, id, dE);
        //fall through
      case CALC_ANGLE:
        addForce(dc, ic, dE);
        //fall through
      case CALC_DISTANCE:
        addForce(db, ib, dE);
        addForce(da, ia, dE);
      }
    }
    return k * delta * delta;
  }

}
