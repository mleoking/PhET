/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-05-13 19:17:06 -0500 (Sat, 13 May 2006) $
 * $Revision: 5114 $
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
package org.jmol.quantum;

import javax.vecmath.Point3f;

import org.jmol.modelset.Atom;
import org.jmol.util.Logger;

import java.util.BitSet;

abstract class QuantumCalculation {

  protected boolean doDebug = false;

  protected final static float bohr_per_angstrom = 1 / 0.52918f;

  protected float[][][] voxelData;
  protected int xMin, xMax, yMin, yMax, zMin, zMax;

  protected QMAtom[] qmAtoms;
  protected int atomIndex;
  protected QMAtom thisAtom;

  // absolute grid coordinates in Bohr 
  protected float[] xBohr, yBohr, zBohr;
  protected float[] originBohr = new float[3];
  protected float[] stepBohr = new float[3];
  protected int nX, nY, nZ;
  
  // grid coordinates relative to orbital center in Bohr 
  protected float[] X, Y, Z;

  // grid coordinate squares relative to orbital center in Bohr
  protected float[] X2, Y2, Z2;

  // range in bohr to consider affected by an atomic orbital
  // this is a cube centered on an atom of side rangeBohr*2
  protected float rangeBohrOrAngstroms = 10; //bohr; about 5 Angstroms
  
  protected float unitFactor = bohr_per_angstrom;

  protected void initialize(int nX, int nY, int nZ) {
    
    this.nX = nX;
    this.nY = nY;
    this.nZ = nZ;
    
    // absolute grid coordinates in Bohr
    xBohr = new float[nX];
    yBohr = new float[nY];
    zBohr = new float[nZ];

    // grid coordinates relative to orbital center in Bohr 
    X = new float[nX];
    Y = new float[nY];
    Z = new float[nZ];

    // grid coordinate squares relative to orbital center in Bohr
    X2 = new float[nX];
    Y2 = new float[nY];
    Z2 = new float[nZ];
  }

  protected float volume = 1;

  protected void setupCoordinates(float[] originXYZ, float[] stepsXYZ,
                                  BitSet bsSelected, Point3f[] atomCoordAngstroms) {

    // all coordinates come in as angstroms, not bohr, and are converted here into bohr

    for (int i = 3; --i >= 0;) {
      originBohr[i] = originXYZ[i] * unitFactor;
      stepBohr[i] = stepsXYZ[i] * unitFactor;
      volume *= stepBohr[i];
    }
    setXYZBohr(xBohr, 0, nX);
    setXYZBohr(yBohr, 1, nY);
    setXYZBohr(zBohr, 2, nZ);
    
    /* 
     * allowing null atoms allows for selectively removing
     * atoms from the rendering. Maybe a first time this has ever been done?
     * 
     */

    qmAtoms = new QMAtom[atomCoordAngstroms.length];
    boolean isAll = (bsSelected == null);
    int i0 = (isAll ? qmAtoms.length - 1 : bsSelected.nextSetBit(0));
    for (int i = i0; i >= 0; i = (isAll ? i - 1 : bsSelected.nextSetBit(i + 1)))
      qmAtoms[i] = new QMAtom((Atom) atomCoordAngstroms[i], X, Y, Z, X2, Y2, Z2);
    if (doDebug)
      Logger.debug("QuantumCalculation:\n origin(Bohr)= " + originBohr[0] + " "
          + originBohr[1] + " " + originBohr[2] + "\n steps(Bohr)= "
          + stepBohr[0] + " " + stepBohr[1] + " " + stepBohr[2] + "\n counts= "
          + nX + " " + nY + " " + nZ);
  }

  private void setXYZBohr(float[] bohr, int i, int n) {
    bohr[0] = originBohr[i];
    float inc = stepBohr[i];
    for (int j = 0; ++j < n;)
      bohr[j] = bohr[j - 1] + inc;
  }

  class QMAtom extends Point3f {

    // grid coordinates relative to orbital center in Bohr 
    private float[] myX, myY, myZ;

    // grid coordinate squares relative to orbital center in Bohr
    private float[] myX2, myY2, myZ2;

    Atom atom;
    
    QMAtom(Atom atom, float[] X, float[] Y, float[] Z, 
        float[] X2, float[] Y2, float[] Z2) {
      myX = X;
      myY = Y;
      myZ = Z;
      myX2 = X2;
      myY2 = Y2;
      myZ2 = Z2;
      
      set(this.atom = atom);
      scale(unitFactor);
    }

    protected void setXYZ(boolean setMinMax) {
      int i;
      try {
      if (setMinMax) {
        i = (int) Math.floor((x - xBohr[0] - rangeBohrOrAngstroms) / stepBohr[0]);
        xMin = (i < 0 ? 0 : i);
        i = (int) Math.floor(1 + (x - xBohr[0] + rangeBohrOrAngstroms) / stepBohr[0]);
        xMax = (i >= nX ? nX : i + 1);
        i = (int) Math.floor((y - yBohr[0] - rangeBohrOrAngstroms) / stepBohr[1]);
        yMin = (i < 0 ? 0 : i);
        i = (int) Math.floor(1 + (y - yBohr[0] + rangeBohrOrAngstroms) / stepBohr[1]);
        yMax = (i >= nY ? nY : i + 1);
        i = (int) Math.floor((z - zBohr[0] - rangeBohrOrAngstroms) / stepBohr[2]);
        zMin = (i < 0 ? 0 : i);
        i = (int) Math.floor(1 + (z - zBohr[0] + rangeBohrOrAngstroms) / stepBohr[2]);
        zMax = (i >= nZ ? nZ : i + 1);
      }
      for (i = xMax; --i >= xMin;) {
        myX2[i] = myX[i] = xBohr[i] - x;
        myX2[i] *= myX[i];
      }
      for (i = yMax; --i >= yMin;) {
        myY2[i] = myY[i] = yBohr[i] - y;
        myY2[i] *= myY[i];
      }
      for (i = zMax; --i >= zMin;) {
        myZ2[i] = myZ[i] = zBohr[i] - z;
        myZ2[i] *= myZ[i];
      }
      
      } catch (Exception e) {
        Logger.error("Error in QuantumCalculation setting bounds");
      }
    }
  }
}
