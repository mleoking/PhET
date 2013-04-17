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

import org.jmol.api.MOCalculationInterface;
import org.jmol.api.VolumeDataInterface;
import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

import javax.vecmath.Point3f;

import java.util.List;
import java.util.BitSet;

/*
 * See J. Computational Chemistry, vol 7, p 359, 1986.
 * thanks go to Won Kyu Park, wkpark@chem.skku.ac.kr, 
 * jmol-developers list communication "JMOL AND CALCULATED ORBITALS !!!!"
 * and his http://chem.skku.ac.kr/~wkpark/chem/mocube.f
 * based on PSI88 http://www.ccl.net/cca/software/SOURCES/FORTRAN/psi88/index.shtml
 * http://www.ccl.net/cca/software/SOURCES/FORTRAN/psi88/src/psi1.f
 * 
 * While we are not exactly copying this code, I include here the information from that
 * FORTRAN as acknowledgment of the source of the algorithmic idea to use single 
 * row arrays to reduce the number of calculations.
 *  
 * Slater functions provided by JR Schmidt and Will Polik. Many thanks!
 * 
 * Spherical functions by Matthew Zwier <mczwier@gmail.com>
 * 
 * A neat trick here is using Java Point3f. null atoms allow selective removal of
 * their contribution to the MO. Maybe a first time this has ever been done?
 * 
 * Bob Hanson hansonr@stolaf.edu 7/3/06
 * 
 C
 C      DANIEL L. SEVERANCE
 C      WILLIAM L. JORGENSEN
 C      DEPARTMENT OF CHEMISTRY
 C      YALE UNIVERSITY
 C      NEW HAVEN, CT 06511
 C
 C      THIS CODE DERIVED FROM THE PSI1 PORTION OF THE ORIGINAL PSI77
 C      PROGRAM WRITTEN BY WILLIAM L. JORGENSEN, PURDUE.
 C      IT HAS BEEN REWRITTEN TO ADD SPEED AND BASIS FUNCTIONS. DLS
 C
 C      THE CONTOURING CODE HAS BEEN MOVED TO A SEPARATE PROGRAM TO ALLOW
 C      MULTIPLE CONTOURS TO BE PLOTTED WITHOUT RECOMPUTING THE
 C      ORBITAL VALUE MATRIX.
 C
 C Redistribution and use in source and binary forms are permitted
 C provided that the above paragraphs and this one are duplicated in 
 C all such forms and that any documentation, advertising materials,
 C and other materials related to such distribution and use acknowledge 
 C that the software was developed by Daniel Severance at Purdue University
 C The name of the University or Daniel Severance may not be used to endorse 
 C or promote products derived from this software without specific prior 
 C written permission.  The authors are now at Yale University.
 C THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 C IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 C WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */

/*
 * NOTE -- THIS CLASS IS INSTANTIATED USING Interface.getOptionInterface
 * NOT DIRECTLY -- FOR MODULARIZATION. NEVER USE THE CONSTRUCTOR DIRECTLY!
 * 
 */

public class MOCalculation extends QuantumCalculation implements
    MOCalculationInterface {

  private final static double CUT = -50;
  
  // slater coefficients in Bohr
  private double[] CX, CY, CZ;
  // d-orbital partial coefficients in Bohr
  private double[] DXY, DXZ, DYZ;
    // exp(-alpha x^2...)
  private double[] EX, EY, EZ;
  
  private String calculationType;
  private List<int[]> shells;
  private float[][] gaussians;
  //Hashtable aoOrdersDF;
  private SlaterData[] slaters;
  private float[] moCoefficients;
  private int moCoeff;
  private int gaussianPtr;
  private int firstAtomOffset;
  private boolean isElectronDensity;
  private float occupancy = 2f; //for now -- RHF only
  //private float coefMax = Integer.MAX_VALUE;
  private boolean doNormalize = true;
  private boolean nwChemMode = false;
  //                                              S           P           SP          DS         DC          FS          FC
  private int[][] dfCoefMaps = new int[][] {new int[1], new int[3], new int[4], new int[5], new int[6], new int[7], new int[10]};

//  private float[] nuclearCharges;

  protected float[][][] voxelDataTemp;

  private float[] linearCombination;

  private float[][] coefs;

  private double moFactor = 1;

  public MOCalculation() {
  }

  public void calculate(VolumeDataInterface volumeData, BitSet bsSelected,
                        String calculationType, Point3f[] atomCoordAngstroms,
                        int firstAtomOffset, List<int[]> shells,
                        float[][] gaussians, int[][] dfCoefMaps,
                        Object slaters,
                        float[] moCoefficients, float[] linearCombination, float[][] coefs,
                        float[] nuclearCharges, boolean doNormalize) {
    boolean testing = false;
    this.calculationType = calculationType;
    this.firstAtomOffset = firstAtomOffset;
    this.shells = shells;
    this.gaussians = gaussians;
    if (dfCoefMaps != null)
      this.dfCoefMaps = dfCoefMaps;
    this.slaters = (SlaterData[]) slaters;
    this.moCoefficients = moCoefficients;
    this.linearCombination = linearCombination;
    this.coefs = coefs;
    //this.nuclearCharges = nuclearCharges;
    this.isElectronDensity = (testing || nuclearCharges != null);
    this.doNormalize = doNormalize;
    int[] countsXYZ = volumeData.getVoxelCounts();
    initialize(countsXYZ[0], countsXYZ[1], countsXYZ[2]);
    voxelData = volumeData.getVoxelData();
    voxelDataTemp = (isElectronDensity ? new float[nX][nY][nZ] : voxelData);
    setupCoordinates(volumeData.getOriginFloat(), 
        volumeData.getVolumetricVectorLengths(), 
        bsSelected, atomCoordAngstroms);
    atomIndex = firstAtomOffset - 1;
    doDebug = (Logger.debugging);
    createCube();
    if (doDebug || testing || isElectronDensity)
      calculateElectronDensity(nuclearCharges);
  }  
  
  @Override
  protected void initialize(int nX, int nY, int nZ) {
    super.initialize(nX, nY, nZ);
    
    CX = new double[nX];
    CY = new double[nY];
    CZ = new double[nZ];

    DXY = new double[nX];
    DXZ = new double[nX];
    DYZ = new double[nY];

    EX = new double[nX];
    EY = new double[nY];
    EZ = new double[nZ];
  }

  float integration = 0;

  public void calculateElectronDensity(float[] nuclearCharges) {
    //TODO
    for (int ix = nX; --ix >= 0;)
      for (int iy = nY; --iy >= 0;)
        for (int iz = nZ; --iz >= 0;) {
          float x = voxelData[ix][iy][iz];
          integration += x * x;
        }
    float volume = stepBohr[0] * stepBohr[1] * stepBohr[2]; 
        // / bohr_per_angstrom / bohr_per_angstrom / bohr_per_angstrom;
    integration *= volume;
    Logger.info("Integrated density = " + integration);
    //processMep(nuclearCharges);
  }

  private void createCube() {
    if (slaters == null && !checkCalculationType())
      return;
    if (linearCombination == null) {
      process();
    } else {
      double sum = 0;
      for (int i = 0; i < linearCombination.length; i += 2)
        sum += linearCombination[i] * linearCombination[i];
      sum = Math.sqrt(sum);
      if (sum == 0)
        return;
      for (int i = 0; i < linearCombination.length; i += 2) {
        moFactor = linearCombination[i] / sum;
        if (moFactor == 0)
          continue;
        moCoefficients = coefs[(int) linearCombination[i + 1] - 1];
        process();
      }
    }
  }

  private void process() {
    moCoeff = 0;
    if (slaters == null) {
      // each STO shell is the combination of one or more gaussians
      int nShells = shells.size();
      for (int i = 0; i < nShells; i++)
        processShell(i);
      return;
    }
      for (int i = 0; i < slaters.length; i++) {
        if (!processSlater(i))
          break;
      }
  }

  private boolean checkCalculationType() {
    if (calculationType == null) {
      Logger.warn("calculation type not identified -- continuing");
      return true;
    }
    nwChemMode = (calculationType.indexOf("NWCHEM") >= 0);
    /*if (calculationType.indexOf("5D") >= 0) {
     Logger
     .error("QuantumCalculation.checkCalculationType: can't read 5D basis sets yet: "
     + calculationType + " -- exit");
     return false;
     }*/
    if (calculationType.indexOf("+") >= 0 || calculationType.indexOf("*") >= 0) {
      Logger
          .warn("polarization/diffuse wavefunctions have not been tested fully: "
              + calculationType + " -- continuing");
    }
    if (calculationType.indexOf("?") >= 0) {
      Logger
          .warn("unknown calculation type may not render correctly -- continuing");
    } else {
      Logger.info("calculation type: " + calculationType + " OK.");
    }
    return true;
  }

  private int nGaussians;
  private boolean doShowShellType;
  private int basisType;
  
  private void processShell(int iShell) {
    int lastAtom = atomIndex;
    int[] shell = shells.get(iShell);
    atomIndex = shell[0] + firstAtomOffset;
    basisType = shell[1];
    gaussianPtr = shell[2];
    nGaussians = shell[3];
    doShowShellType = doDebug;
    if (atomIndex != lastAtom && (thisAtom = qmAtoms[atomIndex]) != null)
      thisAtom.setXYZ(true);
    if (!setCoeffs())
      return;
    switch (basisType) {
    case JmolConstants.SHELL_S:
      addDataS();
      break;
    case JmolConstants.SHELL_P:
      addDataP();
      break;
    case JmolConstants.SHELL_SP:
      addDataSP();
      break;
    case JmolConstants.SHELL_D_SPHERICAL:
      addData5D();
      break;
    case JmolConstants.SHELL_D_CARTESIAN:
        addData6D();
      break;
    case JmolConstants.SHELL_F_SPHERICAL:
      addData7F();
      break;
    case JmolConstants.SHELL_F_CARTESIAN:
        addData10F();
      break;
    default:
      Logger.warn(" Unsupported basis type for atomno=" + (atomIndex + 1) + ": " + basisType);
      break;
    }
  }
  
  private void setTemp() {
    for (int ix = xMax; --ix >= xMin;) {
      for (int iy = yMax; --iy >= yMin;) {
        for (int iz = zMax; --iz >= zMin;) {
          float value = voxelDataTemp[ix][iy][iz];
          voxelData[ix][iy][iz] += value * value * occupancy;
          voxelDataTemp[ix][iy][iz] = 0;
        }
      }
    }
  }
  
  private double getContractionNormalization(int el, int cpt) {
    double sum;
    double df = (el == 3 ? 15 : el == 2 ? 3 : 1);
    double f = df * Math.pow(Math.PI, 1.5) / Math.pow(2, el);
    double p = 0.75 + el / 2.0;
    if (nGaussians == 1) {
      sum = Math.pow(2, -2 * p) * Math.pow(gaussians[gaussianPtr][cpt], 2);
    } else {
      sum = 0;
      for (int ig1 = 0; ig1 < nGaussians; ig1++) {
        double alpha1 = gaussians[gaussianPtr + ig1][0];
        double c1 = gaussians[gaussianPtr + ig1][cpt];
        double f1 = Math.pow(alpha1, p);
        for (int ig2 = 0; ig2 < nGaussians; ig2++) {
          double alpha2 = gaussians[gaussianPtr + ig2][0];
          double c2 = gaussians[gaussianPtr + ig2][cpt];
          double f2 = Math.pow(alpha2, p);
          sum += c1 * f1 * c2 * f2 / Math.pow(alpha1 + alpha2, 2 * p);
          //if (nGaussians == 1)System.out.println(c1 + " " + f1 + " " + c2 + " " + f2);
        }
      }
    }
    sum = 1 / Math.sqrt(f * sum);
    if (Logger.debugging)
      Logger.debug("\t\t\tnormalization for l=" + el + " nGaussians=" + nGaussians + " is " + sum);
    return sum;
  }

  private final double[] coeffs = new double[10];
  private int[] map;
  
  private boolean setCoeffs() {
    boolean isOK = false;
    int mapType = basisType;
    map = dfCoefMaps[mapType];
    if (thisAtom == null) {
      moCoeff += map.length;
      return false;
    }
    for (int i = 0; i < map.length; i++)
      isOK |= ((coeffs[i] = moCoefficients[map[i] + moCoeff++]) != 0);
    isOK &= (coeffs[0] != Integer.MIN_VALUE);
    if (isOK && doDebug)
      dumpInfo(mapType);
    return isOK;
  }
  
  private void addDataS() {
    double norm, c1;
    
    if (doNormalize) {
      if (nwChemMode) {
        // contraction needs to be normalized
        norm = getContractionNormalization(0, 1);
      } else {
        // (8 alpha^3/pi^3)^0.25 exp(-alpha r^2)
        norm = 0.712705470f; // (8/pi^3)^0.25 = (2/pi)^3/4
      }
    } else {
      norm = 1;
    }
   
    double m1 = coeffs[0];
    for (int ig = 0; ig < nGaussians; ig++) {
      double alpha = gaussians[gaussianPtr + ig][0];
      c1 = gaussians[gaussianPtr + ig][1];
      double a = norm * m1 * c1 * moFactor;
      if (doNormalize)
        a *= Math.pow(alpha, 0.75);
      // the coefficients are all included with the X factor here
      for (int i = xMax; --i >= xMin;) {
        EX[i] = a *  Math.exp(-X2[i] * alpha);
      }
      for (int i = yMax; --i >= yMin;) {
        EY[i] =  Math.exp(-Y2[i] * alpha);
      }
      for (int i = zMax; --i >= zMin;) {
        EZ[i] =  Math.exp(-Z2[i] * alpha);
      }

      for (int ix = xMax; --ix >= xMin;) {
        double eX = EX[ix];
        for (int iy = yMax; --iy >= yMin;) {
          double eXY = eX * EY[iy];
          for (int iz = zMax; --iz >= zMin;) {
            voxelDataTemp[ix][iy][iz] += eXY * EZ[iz];
          }
        }
      }
    }
    if (isElectronDensity)
      setTemp();
  }

  private void addDataP() {
    double mx = coeffs[0];
    double my = coeffs[1];
    double mz = coeffs[2];
    double norm;
    
    if (doNormalize) {
      if (nwChemMode) {
        norm = getContractionNormalization(1, 1);
      } else {
        // (128 alpha^5/pi^3)^0.25 [x|y|z]exp(-alpha r^2)
        norm = 1.42541094f;
      }
    } else {
      norm = 1;
    }
    
    if (isElectronDensity) {
      for (int ig = 0; ig < nGaussians; ig++) {
        double alpha = gaussians[gaussianPtr + ig][0];
        double c1 = gaussians[gaussianPtr + ig][1];
        double a = c1;
        if (doNormalize)
          a *=  Math.pow(alpha, 1.25) * norm;
        calcSP(alpha, 0, a * mx, 0, 0);
      }
      setTemp();
      for (int ig = 0; ig < nGaussians; ig++) {
        double alpha = gaussians[gaussianPtr + ig][0];
        double c1 = gaussians[gaussianPtr + ig][1];
        double a = c1;
        if (doNormalize)
          a *=  Math.pow(alpha, 1.25) * norm;
        calcSP(alpha, 0, 0, a * my, 0);
      }
      setTemp();
      for (int ig = 0; ig < nGaussians; ig++) {
        double alpha = gaussians[gaussianPtr + ig][0];
        double c1 = gaussians[gaussianPtr + ig][1];
        double a = c1;
        if (doNormalize)
          a *=  Math.pow(alpha, 1.25) * norm;
        calcSP(alpha, 0, 0, 0, a * mz);
      }
      setTemp();
    } else {
      for (int ig = 0; ig < nGaussians; ig++) {
        double alpha = gaussians[gaussianPtr + ig][0];
        double c1 = gaussians[gaussianPtr + ig][1];
        double a = c1;
        if (doNormalize)
          a *=  Math.pow(alpha, 1.25) * norm;
        calcSP(alpha, 0, a * mx, a * my, a * mz);
      }
    }
  }

  private void addDataSP() {
    // spartan uses format "1" for BOTH SP and P, which is fine, but then
    // when c1 = 0, there is no mo coefficient, of course. 
    boolean isP = (map.length == 3);
    int pPt = (isP ? 0 : 1);
    double ms = (isP ? 0 : coeffs[0]);
    double mx = coeffs[pPt++];
    double my = coeffs[pPt++];
    double mz = coeffs[pPt++];
    double norm1, norm2;
    if (doNormalize) {
      if (nwChemMode) {
        norm1 = getContractionNormalization(0, 1);
        norm2 = getContractionNormalization(1, 2);
      } else {
        norm1 = 0.712705470f;
        norm2 = 1.42541094f;
      }
    } else {
      norm1 = norm2 = 1;
    }
    double a1, a2, c1, c2, alpha;
    if (isElectronDensity) {
      if (ms != 0)
        for (int ig = 0; ig < nGaussians; ig++) {
          alpha = gaussians[gaussianPtr + ig][0];
          c1 = gaussians[gaussianPtr + ig][1];
          a1 = c1;
          if (doNormalize)
            a1 *= Math.pow(alpha, 0.75) * norm1;
          calcSP(alpha, a1 * ms, 0, 0, 0);
        }
      setTemp();
      if (mx != 0)
        for (int ig = 0; ig < nGaussians; ig++) {
          alpha = gaussians[gaussianPtr + ig][0];
          c2 = gaussians[gaussianPtr + ig][2];
          a2 = c2;
          if (doNormalize)
            a2 *= Math.pow(alpha, 1.25) * norm2;
          calcSP(alpha, 0, a2 * mx, 0, 0);
        }
      setTemp();
      if (my != 0)
        for (int ig = 0; ig < nGaussians; ig++) {
          alpha = gaussians[gaussianPtr + ig][0];
          c2 = gaussians[gaussianPtr + ig][2];
          a2 = c2;
          if (doNormalize)
            a2 *= Math.pow(alpha, 1.25) * norm2;
          calcSP(alpha, 0, 0, a2 * my, 0);
        }
      setTemp();
      if (mz != 0)
        for (int ig = 0; ig < nGaussians; ig++) {
          alpha = gaussians[gaussianPtr + ig][0];
          c2 = gaussians[gaussianPtr + ig][2];
          a2 = c2;
          if (doNormalize)
            a2 *= Math.pow(alpha, 1.25) * norm2;
          calcSP(alpha, 0, 0, 0, a2 * mz);
        }
      setTemp();
    } else {
      for (int ig = 0; ig < nGaussians; ig++) {
        alpha = gaussians[gaussianPtr + ig][0];
        c1 = gaussians[gaussianPtr + ig][1];
        c2 = gaussians[gaussianPtr + ig][2];
        a1 = c1;
        a2 = c2;
        if (doNormalize) {
          a1 *= Math.pow(alpha, 0.75) * norm1;
          a2 *= Math.pow(alpha, 1.25) * norm2;
        }
        calcSP(alpha, a1 * ms, a2 * mx, a2 * my, a2 * mz);
      }
    }
  }

  private void setCE(double[] CX, double[] EX, double alpha, double as, double ax,
                     double ay, double az) {
    for (int i = xMax; --i >= xMin;) {
      CX[i] = as + ax * X[i];
      EX[i] =  Math.exp(-X2[i] * alpha) * moFactor;
    }
    for (int i = yMax; --i >= yMin;) {
      CY[i] = ay * Y[i];
      EY[i] =  Math.exp(-Y2[i] * alpha);
    }
    for (int i = zMax; --i >= zMin;) {
      CZ[i] = az * Z[i];
      EZ[i] =  Math.exp(-Z2[i] * alpha);
    }
  }

  private void setE(double[] EX, double alpha) {
    for (int i = xMax; --i >= xMin;)
      EX[i] =  Math.exp(-X2[i] * alpha) * moFactor;
    for (int i = yMax; --i >= yMin;)
      EY[i] =  Math.exp(-Y2[i] * alpha);
    for (int i = zMax; --i >= zMin;)
      EZ[i] =  Math.exp(-Z2[i] * alpha);
  }

  private void calcSP(double alpha, double as, double ax, double ay, double az) {
    setCE(CX, EX, alpha, as, ax, ay, az);
    for (int ix = xMax; --ix >= xMin;) {
      double eX = EX[ix];
      double cX = CX[ix];
      for (int iy = yMax; --iy >= yMin;) {
        double eXY = eX * EY[iy];
        double cXY = cX + CY[iy];
        for (int iz = zMax; --iz >= zMin;) {
          voxelDataTemp[ix][iy][iz] += (cXY + CZ[iz]) * eXY * EZ[iz];
        }
      }
    }
  }

  private final static double ROOT3 = 1.73205080756887729f;

  private void addData6D() {
    //expects 6 orbitals in the order XX YY ZZ XY XZ YZ
    if (isElectronDensity) 
      return;
    double mxx = coeffs[0];
    double myy = coeffs[1];
    double mzz = coeffs[2];
    double mxy = coeffs[3];
    double mxz = coeffs[4];
    double myz = coeffs[5];
    double norm1, norm2;
    if (doNormalize) {
      if (nwChemMode) {
        norm1 = getContractionNormalization(2, 1);
        norm2 = norm1;
      } else {
        norm1 = 2.8508219178923f;
        norm2 = norm1 / ROOT3;
      }
    } else {
      norm2 = 1 / ROOT3;
      norm1 = 1;
    }
    for (int ig = 0; ig < nGaussians; ig++) {
      double alpha = gaussians[gaussianPtr + ig][0];
      double c1 = gaussians[gaussianPtr + ig][1];
      // xx|yy|zz: (2048 alpha^7/9pi^3)^0.25 [xx|yy|zz]exp(-alpha r^2)
      // xy|xz|yz: (2048 alpha^7/pi^3)^0.25 [xy|xz|yz]exp(-alpha r^2)
      double a = c1;
      if (doNormalize)
        a *=  Math.pow(alpha, 1.75);
      double axy = a * norm1 * mxy;
      double axz = a * norm1 * mxz;
      double ayz = a * norm1 * myz;
      double axx = a * norm2 * mxx;
      double ayy = a * norm2 * myy;
      double azz = a * norm2 * mzz;
      setCE(CX, EX, alpha, 0, axx, ayy, azz);

      for (int i = xMax; --i >= xMin;) {
        DXY[i] = axy * X[i];
        DXZ[i] = axz * X[i];
      }
      for (int i = yMax; --i >= yMin;) {
        DYZ[i] = ayz * Y[i];
      }
      for (int ix = xMax; --ix >= xMin;) {
        double axx_x2 = CX[ix] * X[ix];
        double axy_x = DXY[ix];
        double axz_x = DXZ[ix];
        double eX = EX[ix];
        for (int iy = yMax; --iy >= yMin;) {
          double axx_x2__ayy_y2__axy_xy = axx_x2 + (CY[iy] + axy_x) * Y[iy];
          double axz_x__ayz_y = axz_x + DYZ[iy];
          double eXY = eX * EY[iy];
          for (int iz = zMax; --iz >= zMin;) {
            voxelDataTemp[ix][iy][iz] += (axx_x2__ayy_y2__axy_xy + (CZ[iz] + axz_x__ayz_y) * Z[iz])
                * eXY * EZ[iz];
            // giving (axx_x2 + ayy_y2 + azz_z2 + axy_xy + axz_xz + ayz_yz)e^-br2; 
          }
        }
      }
    }
  }

  private void addData5D() {
    // expects 5 real orbitals in the order d0, d+1, d-1, d+2, d-2
    // (i.e. dz^2, dxz, dyz, dx^2-y^2, dxy)
    // To avoid actually having to use spherical harmonics, we use 
    // linear combinations of Cartesian harmonics.  

    // For conversions between spherical and Cartesian gaussians, see
    // "Trasnformation Between Cartesian and Pure Spherical Harmonic Gaussians",
    // Schlegel and Frisch, Int. J. Quant. Chem 54, 83-87, 1995

    if (isElectronDensity)
      return;
    double alpha, c1, a;
    double x, y, z;
    double cxx, cyy, czz, cxy, cxz, cyz;
    double ad0, ad1p, ad1n, ad2p, ad2n;

    /*
     Cartesian forms for d (l = 2) basis functions:
     Type         Normalization
     xx           [(2048 * alpha^7) / (9 * pi^3))]^(1/4)
     xy           [(2048 * alpha^7) / (1 * pi^3))]^(1/4)
     xz           [(2048 * alpha^7) / (1 * pi^3))]^(1/4)
     yy           [(2048 * alpha^7) / (9 * pi^3))]^(1/4)
     yz           [(2048 * alpha^7) / (1 * pi^3))]^(1/4)
     zz           [(2048 * alpha^7) / (9 * pi^3))]^(1/4)
     */

    double norm1, norm2, norm3, norm4;
    
    if (doNormalize) {
      if (nwChemMode) {
        norm2 = getContractionNormalization(2, 1);
        norm1 = norm2 * ROOT3;
        norm4 = -1;
      } else {
        // same as above, except for norm4. 
        // norm4 verified using CeO2.log 
        norm1 = Math.pow(2048.0 / (Math.PI * Math.PI * Math.PI), 0.25);
        norm2 = norm1 / ROOT3;
        norm4 = 1;
      }
      norm3 = ROOT3 / 2; // Normalization constant that shows up for dx^2-y^2
    } else {
      norm1 = norm2 = norm3 = norm4 = 1;
    }

    double m0 = coeffs[0];
    double m1p = coeffs[1];
    double m1n = coeffs[2];
    double m2p = coeffs[3];
    double m2n = coeffs[4];
    
    for (int ig = 0; ig < nGaussians; ig++) {
      alpha = gaussians[gaussianPtr + ig][0];
      c1 = gaussians[gaussianPtr + ig][1];
      a = c1;
      if (doNormalize)
        a *=  Math.pow(alpha, 1.75);

      ad0 = a * m0;
      ad1p = norm4 * a * m1p;
      ad1n = a * m1n;
      ad2p = a * m2p;
      ad2n = a * m2n;

      setE(EX, alpha);

      for (int ix = xMax; --ix >= xMin;) {
        x = X[ix];
        double eX = EX[ix];
        cxx = norm2 * x * x;

        for (int iy = yMax; --iy >= yMin;) {
          y = Y[iy];
          double eXY = eX * EY[iy];

          cyy = norm2 * y * y;
          cxy = norm1 * x * y;

          for (int iz = zMax; --iz >= zMin;) {
            z = Z[iz];

            czz = norm2 * z * z;
            cxz = norm1 * x * z;
            cyz = norm1 * y * z;

            voxelDataTemp[ix][iy][iz] += (
                ad0 * (czz - 0.5f * (cxx + cyy)) 
                + ad1p * cxz 
                + ad1n * cyz 
                + ad2p * norm3 * (cxx - cyy) 
                + ad2n * cxy)
                * eXY * EZ[iz];
          }
        }
      }
    }
  }

  private void addData10F() {
    // expects 10 orbitals in the order XXX, YYY, ZZZ, XYY, XXY, 
    //                                  XXZ, XZZ, YZZ, YYZ, XYZ
    if (isElectronDensity) 
      return;
    double alpha;
    double c1;
    double a;
    double x, y, z, xx, yy, zz;
    double axxx, ayyy, azzz, axyy, axxy, axxz, axzz, ayzz, ayyz, axyz;
    double cxxx, cyyy, czzz, cxyy, cxxy, cxxz, cxzz, cyzz, cyyz, cxyz;

    /*
     Cartesian forms for f (l = 3) basis functions:
     Type         Normalization
     xxx          [(32768 * alpha^9) / (225 * pi^3))]^(1/4)
     xxy          [(32768 * alpha^9) / (9 * pi^3))]^(1/4)
     xxz          [(32768 * alpha^9) / (9 * pi^3))]^(1/4)
     xyy          [(32768 * alpha^9) / (9 * pi^3))]^(1/4)
     xyz          [(32768 * alpha^9) / (1 * pi^3))]^(1/4)
     xzz          [(32768 * alpha^9) / (9 * pi^3))]^(1/4)
     yyy          [(32768 * alpha^9) / (225 * pi^3))]^(1/4)
     yyz          [(32768 * alpha^9) / (9 * pi^3))]^(1/4)
     yzz          [(32768 * alpha^9) / (9 * pi^3))]^(1/4)
     zzz          [(32768 * alpha^9) / (225 * pi^3))]^(1/4)
     */

    double norm1, norm2, norm3;
    if (doNormalize) {
      if (nwChemMode) {
        norm1 = getContractionNormalization(3, 1);
        norm2 = norm1;
        norm3 = norm1;        
      } else {
        norm1 = Math.pow(32768.0 / (Math.PI * Math.PI * Math.PI), 0.25);
        norm2 = norm1 / Math.sqrt(3);
        norm3 = norm1 / Math.sqrt(15);
      }

    } else {
      norm1 = norm2 = norm3 = 1;
    }

    double mxxx = coeffs[0];
    double myyy = coeffs[1];
    double mzzz = coeffs[2];
    double mxyy = coeffs[3];
    double mxxy = coeffs[4];
    double mxxz = coeffs[5];
    double mxzz = coeffs[6];
    double myzz = coeffs[7];
    double myyz = coeffs[8];
    double mxyz = coeffs[9];
    for (int ig = 0; ig < nGaussians; ig++) {
      alpha = gaussians[gaussianPtr + ig][0];
      c1 = gaussians[gaussianPtr + ig][1];
      setE(EX, alpha);

      // common factor of contraction coefficient and alpha normalization 
      // factor; only call pow once per primitive
      a = c1;
      if (doNormalize)
        a *= Math.pow(alpha, 2.25);

      axxx = a * norm3 * mxxx;
      ayyy = a * norm3 * myyy;
      azzz = a * norm3 * mzzz;
      axyy = a * norm2 * mxyy;
      axxy = a * norm2 * mxxy;
      axxz = a * norm2 * mxxz;
      axzz = a * norm2 * mxzz;
      ayzz = a * norm2 * myzz;
      ayyz = a * norm2 * myyz;
      axyz = a * norm1 * mxyz;

      for (int ix = xMax; --ix >= xMin;) {
        x = X[ix];
        xx = x * x;

        double Ex = EX[ix];
        cxxx = axxx * xx * x;

        for (int iy = yMax; --iy >= yMin;) {
          y = Y[iy];
          yy = y * y;
          double Exy = Ex * EY[iy];
          cyyy = ayyy * yy * y;
          cxxy = axxy * xx * y;
          cxyy = axyy * x * yy;

          for (int iz = zMax; --iz >= zMin;) {
            z = Z[iz];
            zz = z * z;
            czzz = azzz * zz * z;
            cxxz = axxz * xx * z;
            cxzz = axzz * x * zz;
            cyyz = ayyz * yy * z;
            cyzz = ayzz * y * zz;
            cxyz = axyz * x * y * z;
            voxelDataTemp[ix][iy][iz] += (cxxx + cyyy + czzz + cxyy + cxxy
                + cxxz + cxzz + cyzz + cyyz + cxyz)
                * Exy * EZ[iz];
          }
        }
      }
    }
  }

  private void addData7F() {
    // expects 7 real orbitals in the order f0, f+1, f-1, f+2, f-2, f+3, f-3

    if (isElectronDensity)
      return;    
    double alpha, c1, a;
    double x, y, z, xx, yy, zz;
    double cxxx, cyyy, czzz, cxyy, cxxy, cxxz, cxzz, cyzz, cyyz, cxyz;
    double af0, af1p, af1n, af2p, af2n, af3p, af3n;
    double f0, f1p, f1n, f2p, f2n, f3p, f3n;
    /*
     Cartesian forms for f (l = 3) basis functions:
     Type         Normalization
     xxx          [(32768 * alpha^9) / (225 * pi^3))]^(1/4)
     xxy          [(32768 * alpha^9) / (9 * pi^3))]^(1/4)
     xxz          [(32768 * alpha^9) / (9 * pi^3))]^(1/4)
     xyy          [(32768 * alpha^9) / (9 * pi^3))]^(1/4)
     xyz          [(32768 * alpha^9) / (1 * pi^3))]^(1/4)
     xzz          [(32768 * alpha^9) / (9 * pi^3))]^(1/4)
     yyy          [(32768 * alpha^9) / (225 * pi^3))]^(1/4)
     yyz          [(32768 * alpha^9) / (9 * pi^3))]^(1/4)
     yzz          [(32768 * alpha^9) / (9 * pi^3))]^(1/4)
     zzz          [(32768 * alpha^9) / (225 * pi^3))]^(1/4)
     */

    
    // TODO:  Check if norm4 should be -1 for all programs
    
    double norm1, norm2, norm3, norm4;
    if (doNormalize) {
      if (nwChemMode) {
        norm3 = getContractionNormalization(3, 1);
        norm2 = norm3 * Math.sqrt(5);
        norm1 = norm3 * Math.sqrt(15);
        norm4 = -1;
      } else {
        norm1 = Math.pow(32768.0 / (Math.PI * Math.PI * Math.PI), 0.25);
        norm2 = norm1 / Math.sqrt(3);
        norm3 = norm1 / Math.sqrt(15);
        // norm4 verified for Gaussian using CeO2.log 
        norm4 = 1;
      }

    } else {
      norm1 = norm2 = norm3 = norm4 = 1;
    }

    // Linear combination coefficients for the various Cartesian gaussians
    final double c0_xxz_yyz = (3.0 / (2.0 * Math.sqrt(5)));

    final double c1p_xzz = Math.sqrt(6.0 / 5.0);
    final double c1p_xxx = Math.sqrt(3.0 / 8.0);
    final double c1p_xyy = Math.sqrt(3.0 / 40.0);
    final double c1n_yzz = c1p_xzz;
    final double c1n_yyy = c1p_xxx;
    final double c1n_xxy = c1p_xyy;

    final double c2p_xxz_yyz = Math.sqrt(3.0 / 4.0);

    final double c3p_xxx = Math.sqrt(5.0 / 8.0);
    final double c3p_xyy = 0.75f * Math.sqrt(2);
    final double c3n_yyy = c3p_xxx;
    final double c3n_xxy = c3p_xyy;

    double m0 = coeffs[0];
    double m1p = coeffs[1];
    double m1n = coeffs[2];
    double m2p = coeffs[3];
    double m2n = coeffs[4];
    double m3p = coeffs[5];
    double m3n = coeffs[6];

    for (int ig = 0; ig < nGaussians; ig++) {
      alpha = gaussians[gaussianPtr + ig][0];
      c1 = gaussians[gaussianPtr + ig][1];
      a = c1;
      if (doNormalize)
        a *= Math.pow(alpha, 2.25);

      af0 = a * m0;
      af1p = a * m1p;
      af1n = a * m1n;
      af2p = a * m2p;
      af2n = a * m2n;
      af3p = a * m3p;
      af3n = a * m3n;

      setE(EX, alpha);

      for (int ix = xMax; --ix >= xMin;) {
        x = X[ix];
        xx = x * x;
        double eX = EX[ix];
        cxxx = norm3 * x * xx;
        for (int iy = yMax; --iy >= yMin;) {
          y = Y[iy];
          yy = y * y;
          double eXY = eX * EY[iy];

          cyyy = norm3 * y * yy;
          cxyy = norm2 * x * yy;
          cxxy = norm2 * xx * y;

          for (int iz = zMax; --iz >= zMin;) {
            z = Z[iz];
            zz = z * z;

            czzz = norm3 * z * zz;
            cxxz = norm2 * xx * z;
            cxzz = norm2 * x * zz;
            cyyz = norm2 * yy * z;
            cyzz = norm2 * y * zz;
            cxyz = norm1 * x * y * z;

            f0 = af0 * (czzz - c0_xxz_yyz * (cxxz + cyyz));
            f1p = norm4 * af1p * (c1p_xzz * cxzz - c1p_xxx * cxxx - c1p_xyy * cxyy);
            f1n = af1n * (c1n_yzz * cyzz - c1n_yyy * cyyy - c1n_xxy * cxxy);
            f2p = af2p * (c2p_xxz_yyz * (cxxz - cyyz));
            f2n = af2n * cxyz;
            f3p = norm4 * af3p * (c3p_xxx * cxxx - c3p_xyy * cxyy);
            f3n = -af3n * (c3n_yyy * cyyy - c3n_xxy * cxxy);
            voxelDataTemp[ix][iy][iz] += (f0 + f1p + f1n + f2p + f2n + f3p + f3n)
                * eXY * EZ[iz];
          }
        }
      }
    }
  }

  private boolean processSlater(int slaterIndex) {
    /*
     * We have two data structures for each slater, using the WebMO format: 
     * 
     * int[] slaterInfo[] = {iatom, a, b, c, d}
     * float[] slaterData[] = {zeta, coef}
     * 
     * where
     * 
     *  psi = (coef)(x^a)(y^b)(z^c)(r^d)exp(-zeta*r)
     * 
     * except: a == -2 ==> z^2 ==> (coef)(2z^2-x^2-y^2)(r^d)exp(-zeta*r)
     *    and: b == -2 ==> (coef)(x^2-y^2)(r^d)exp(-zeta*r)
     *    
     *    NOTE: A negative zeta means this is contracted!
     */

    int lastAtom = atomIndex;
    SlaterData slater = slaters[slaterIndex];
    atomIndex = slater.iAtom;
    //System.out.println("MOCALC SLATER " + slaterIndex + " " + lastAtom + " " + atomIndex);
    double minuszeta = -slater.zeta;
    if ((thisAtom = qmAtoms[atomIndex]) == null) {
      if (minuszeta <= 0)
        moCoeff++;
      return true;
    }
    if (minuszeta > 0) { //this is contracted; use previous moCoeff
      minuszeta = -minuszeta;
      moCoeff--;
    }
    if (moCoeff >= moCoefficients.length)
      return false;
    double coef = slater.coef * moCoefficients[moCoeff++];
    //coefMax = 0.2f;
    if (coef == 0) { //|| coefMax != Integer.MAX_VALUE && Math.abs(coef) > coefMax) {
      atomIndex = -1;
      return true;
    }
    coef *= moFactor;
    if (atomIndex != lastAtom)
      thisAtom.setXYZ(true);
    int a = slater.x;
    int b = slater.y;
    int c = slater.z;
    int d = slater.r;
    //  System.out.println("MOCALC " + slaterIndex + " atomNo=" + (atomIndex+1) + "\tx^" + a + " y^"+ b + " z^" + c + " r^" + d + "\tzeta=" + (-minuszeta) + "\tcoef=" + coef);
          //+ " minmax " + xMin + " " + xMax + " " + yMin + " " + yMax + " " + zMin + " " + zMax
    if (a == -2) /* if dz2 */
      for (int ix = xMax; --ix >= xMin;) {
        double dx2 = X2[ix];
        for (int iy = yMax; --iy >= yMin;) {
          double dy2 = Y2[iy];
          double dx2y2 = dx2 + dy2;
          for (int iz = zMax; --iz >= zMin;) {
            double dz2 = Z2[iz];
            double r2 = dx2y2 + dz2;
            double r = Math.sqrt(r2);
            double exponent = minuszeta * r;
            if (exponent < CUT)
              continue;
            double value = (coef * Math.exp(exponent)
                * (3 * dz2 - r2));
            switch(d) {
            case 3:
              value *= r;
              //fall through
            case 2:
              value *= r2;
              break;
            case 1:
              value *= r;
              break;
            }
            voxelDataTemp[ix][iy][iz] += value;
          }
        }
      }
    else if (b == -2) /* if dx2-dy2 */
      for (int ix = xMax; --ix >= xMin;) {
        double dx2 = X2[ix];
        for (int iy = yMax; --iy >= yMin;) {
          double dy2 = Y2[iy];
          double dx2y2 = dx2 + dy2;
          double dx2my2 = coef * (dx2 - dy2);
          for (int iz = zMax; --iz >= zMin;) {
            double dz2 = Z2[iz];
            double r2 = dx2y2 + dz2;
            double r = Math.sqrt(r2);
            double exponent = minuszeta * r;
            if (exponent < CUT)
              continue;
            double value = dx2my2 * Math.exp(exponent);
            switch(d) {
            case 3:
              value *= r;
              //fall through
            case 2:
              value *= r2;
              break;
            case 1:
              value *= r;
              break;
            }
            voxelDataTemp[ix][iy][iz] += value;
          }
        }
      }
    else
      /* everything else */
      for (int ix = xMax; --ix >= xMin;) {
        double dx2 = X2[ix];
        double vdx = coef;
        switch(a) {
        case 3:
          vdx *= X[ix];
          //fall through
        case 2:
          vdx *= dx2;
          break;
        case 1:
          vdx *= X[ix];
          break;
        }
        for (int iy = yMax; --iy >= yMin;) {
          double dy2 = Y2[iy];
          double dx2y2 = dx2 + dy2;
          double vdy = vdx;
          switch(b) {
          case 3:
            vdy *= Y[iy];
            //fall through
          case 2:
            vdy *= dy2;
            break;
          case 1:
            vdy *= Y[iy];
            break;
          }
          for (int iz = zMax; --iz >= zMin;) {
            double dz2 = Z2[iz];
            double r2 = dx2y2 + dz2;
            double r = Math.sqrt(r2);
            double exponent = minuszeta * r;
            if (exponent < CUT)
              continue;
            double value = vdy * Math.exp(exponent);
            switch(c) {
            case 3:
              value *= Z[iz];
              //fall through
            case 2:
              value *= dz2;
              break;
            case 1:
              value *= Z[iz];
              break;
            }
            switch(d) {
            case 3:
              value *= r;
              //fall through
            case 2:
              value *= r2;
              break;
            case 1:
              value *= r;
              break;
            }
            voxelDataTemp[ix][iy][iz] += value;
            //if (ix == 27 && iy == 27)
              //System.out.println(iz + "\t"  
                //  + xBohr[ix] + " " + yBohr[iy] + " " 
                //  +  zBohr[iz] + "\t" 
                //  + X[ix] + " " + Y[iy] + " " + Z[iz] 
                //  + "--  r=" + r + " v=" + value + "\t" + voxelDataTemp[ix][iy][iz]);
             
            

          }
        }
      }
    
/*    for (int iz = 0; iz < 55; iz++) {
      System.out.println(iz + "\t"  
          //  + xBohr[ix] + " " + yBohr[iy] + " " 
            +  zBohr[iz] + "\t" 
          //  + X[ix] + " " + Y[iy] + " " + Z[iz] 
            + "\t"  + voxelDataTemp[27][27][iz]);
    }
*/ 
    if (isElectronDensity)
      setTemp();
    return true;
  }

  private void dumpInfo(int shell) {
    if (doShowShellType) {
      Logger.debug("\n\t\t\tprocessShell: " + shell + " type="
          + JmolConstants.getQuantumShellTag(shell) + " nGaussians="
          + nGaussians + " atom=" + atomIndex);
      doShowShellType = false;
    }
    if (Logger.isActiveLevel(Logger.LEVEL_DEBUGHIGH))
      for (int ig = 0; ig < nGaussians; ig++) {
        double alpha = gaussians[gaussianPtr + ig][0];
        double c1 = gaussians[gaussianPtr + ig][1];
        Logger.debug("\t\t\tGaussian " + (ig + 1) + " alpha=" + alpha + " c="
            + c1);
      }
    String[] so = JmolConstants.getShellOrder(shell);
    for (int i = 0; i < so.length; i++) {
      double c = coeffs[i];
        Logger.debug("MO coeff " + (so == null ? "?" + i : so[i]) + " "
            + (map[i] + moCoeff - map.length + i + 1) + "\t" + c + "\t" + thisAtom.atom);
    }
  }

}
