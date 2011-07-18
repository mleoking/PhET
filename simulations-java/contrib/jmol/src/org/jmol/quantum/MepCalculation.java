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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Map;

import javax.vecmath.Point3f;

import org.jmol.api.MepCalculationInterface;
import org.jmol.api.VolumeDataInterface;
import org.jmol.modelset.Atom;
import org.jmol.util.Logger;
import org.jmol.util.Parser;

/*
 * a simple molecular electrostatic potential cube generator
 * just using q/r here
 * 
 * http://teacher.pas.rochester.edu/phy122/Lecture_Notes/Chapter25/Chapter25.html
 * 
 * applying some of the tricks in QuantumCalculation for speed
 * 
 * NOTE -- THIS CLASS IS INSTANTIATED USING Interface.getOptionInterface
 * NOT DIRECTLY -- FOR MODULARIZATION. NEVER USE THE CONSTRUCTOR DIRECTLY!
 */
public class MepCalculation extends QuantumCalculation implements MepCalculationInterface {

  // 
  // Implemented here is a flexible way of mapping atomic potential data onto 
  // a surface. Given a set of atoms, we assign numbers to the atoms from:
  // 
  // a) a resource in this package and based on atom names, or
  // b) a table of data read from a file and based on atom names, or
  // c) an array of properties assigned to the atoms. 
  // 
  // In addition, once those potentials are assigned, we can apply a variety 
  // of functions of distance:
  // 
  // (0)   Coulomb's law distance function (same as rasmol potential distance function)
  // (1)   f * e^(-d/2)
  //         Gaillard, P., Carrupt, P.A., Testa, B. and Boudon, A., J.Comput.Aided Mol.Des. 8, 83-96 (1994)
  // (2)   f/(1 + d)
  //         Audry, E.; Dubost, J. P.; Colleter, J. C.; Dallet, P. A new approach to structure-activity relations: the "molecular lipophilicity potential". Eur. J. Med. Chem. 1986, 21, 71-72.
  // (3)   f * e^(-d)
  //         Fauchere, J. L.; Quarendon, P.; Kaetterer, L. Estimating and representing hydrophobicity potential. J. Mol. Graphics 1988, 6, 203-206.

  // from http://www.life.illinois.edu/crofts/bc1_in_chime/chime_talk/chimescript2.html

  // The difference between MEP and MLP is simply the defaults for resource and function:
  //
  // a) the default resource is property partialCharges for MEP and atomicLipophilicity.txt for MLP
  // b) the default function is Coulomb for MEP and Fauchere for MLP

  // Bob Hanson hansonr@stolaf.edu 6/15/2010
  
  protected final static int ONE_OVER_D = 0;
  protected final static int E_MINUS_D_OVER_2 = 1;
  protected final static int ONE_OVER_ONE_PLUS_D = 2;
  protected final static int E_MINUS_D = 3;
  
  protected int distanceMode = ONE_OVER_D;
  private float[] potentials;
  private Point3f[] atomCoordAngstroms;
  private BitSet bsSelected;
  
  public MepCalculation() {
    rangeBohrOrAngstroms = 8; // Angstroms
    distanceMode = ONE_OVER_D;
    unitFactor = 1;
  }
  
  public void assignPotentials(Atom[] atoms, float[] potentials,
                               BitSet bsAromatic, BitSet bsCarbonyl,
                               BitSet bsIgnore, String data) {
    getAtomicPotentials(data, null);
    for (int i = 0; i < atoms.length; i++) {
      float f;
      if (bsIgnore != null && bsIgnore.get(i)) {
        f = Float.NaN;
      } else {
        f = getTabulatedPotential(atoms[i]);
        if (Float.isNaN(f))
          f = 0;
      }
      if (Logger.debugging)
        Logger.info(atoms[i].getInfo() + " " + f);
      potentials[i] = f;
    }
  }

  public void setup(int calcType, float[] potentials,
                    Point3f[] atomCoordAngstroms, BitSet bsSelected) {
    if (calcType >= 0)
      distanceMode = calcType;
    this.potentials = potentials;
    this.atomCoordAngstroms = atomCoordAngstroms;
    this.bsSelected = bsSelected;
  }
  
  public void calculate(VolumeDataInterface volumeData, BitSet bsSelected,
                        Point3f[] atomCoordAngstroms, float[] potentials,
                        int calcType) {
    setup(calcType, potentials, atomCoordAngstroms, bsSelected);
    voxelData = volumeData.getVoxelData();
    int[] countsXYZ = volumeData.getVoxelCounts();
    initialize(countsXYZ[0], countsXYZ[1], countsXYZ[2]);
    setupCoordinates(volumeData.getOriginFloat(), volumeData
        .getVolumetricVectorLengths(), bsSelected, atomCoordAngstroms);
    processMep();
  }

  public float getValueAtPoint(Point3f pt) {
    float value = 0;
    for (int i = bsSelected.nextSetBit(0); i >= 0; i = bsSelected
        .nextSetBit(i + 1)) {
      float x = potentials[i];
      float d2 = pt.distanceSquared(atomCoordAngstroms[i]);
      value += valueFor(x, d2, distanceMode);
    }
    return value;
  }
  
  private void processMep() {
    for (int atomIndex = qmAtoms.length; --atomIndex >= 0;) {
      if ((thisAtom = qmAtoms[atomIndex]) == null)
        continue;
      float x0 = potentials[atomIndex];
      if (Logger.debugging)
        Logger.info("process map for atom " + atomIndex + thisAtom + "  charge=" + x0);
      thisAtom.setXYZ(true);
      for (int ix = xMax; --ix >= xMin;) {
        float dX = X2[ix];
        for (int iy = yMax; --iy >= yMin;) {
          float dXY = dX + Y2[iy];
          for (int iz = zMax; --iz >= zMin;) {
            voxelData[ix][iy][iz] += valueFor(x0, dXY + Z2[iz], distanceMode);
          }
        }
      }
    }
    
  }

  public float valueFor(float x0, float d2, int distanceMode) {
    switch (distanceMode) {
    case ONE_OVER_D:
      return (d2 == 0 ? x0 * Float.POSITIVE_INFINITY : x0 / (float) Math.sqrt(d2));
    case ONE_OVER_ONE_PLUS_D:
      return  x0 / (1 + (float) Math.sqrt(d2));
    case E_MINUS_D_OVER_2:
      return x0 * (float) Math.exp(-Math.sqrt(d2) / 2);
    case E_MINUS_D:
      return x0 * (float) Math.exp(-Math.sqrt(d2));
    }
    return x0;
  }

  protected Map<String, Object> htAtomicPotentials;
  
  protected float getTabulatedPotential(Atom atom) {
    String name = atom.getAtomType();
    String g1 = atom.getGroup1('\0');
    String type = atom.getGroupType();
    if (g1.length() == 0) {
      g1 = atom.getGroup3(true);
      if (g1 == null)
        g1 = "";
    }
    String key = g1 + name;
    Object o = htAtomicPotentials.get(key);
    if (o == null && type.length() > 0)
      o = htAtomicPotentials.get("_" + type.charAt(0) + name);
    return (o instanceof Float ? ((Float)o).floatValue() : Float.NaN);
  }

  protected String resourceName;
  
  protected void getAtomicPotentials(String data, String resourceName) {
    BufferedReader br = null;
    htAtomicPotentials = new Hashtable<String, Object>();
    try {
      InputStream is;
      if (data == null) {
        URL url = null;
        if ((url = this.getClass().getResource(resourceName)) == null) {
          Logger.error("Couldn't find file: " + resourceName);
          return;
        }
        is = (InputStream) url.getContent();
      } else {
        is = new ByteArrayInputStream(data.getBytes());
      }
      //turns out from the Jar file
      // it's a sun.net.www.protocol.jar.JarURLConnection$JarURLInputStream
      // and within Eclipse it's a BufferedInputStream

      br = new BufferedReader(new InputStreamReader(is));
      String line;
      while ((line = br.readLine()) != null) {
        if (line.startsWith("#"))
          continue;
        String[] vs = Parser.getTokens(line);
        if (vs.length < 2)
          continue;
        if (Logger.debugging)
          Logger.info(line);
        htAtomicPotentials.put(vs[0], new Float(Parser.parseFloat(vs[1])));
      }
      br.close();
    } catch (Exception e) {
      Logger.error("Exception " + e.getMessage() + " in getResource "
          + resourceName);
      try {
        br.close();
      } catch (Exception ee) {
        // ignore        
      }
    }
  }


}
