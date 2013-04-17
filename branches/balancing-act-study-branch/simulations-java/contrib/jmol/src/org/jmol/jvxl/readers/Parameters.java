/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-03-30 11:40:16 -0500 (Fri, 30 Mar 2007) $
 * $Revision: 7273 $
 *
 * Copyright (C) 2007 Miguel, Bob, Jmol Development
 *
 * Contact: hansonr@stolaf.edu
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

/*
 
 * The JVXL file format
 * --------------------
 * 
 * as of 3/29/07 this code is COMPLETELY untested. It was hacked out of the
 * Jmol code, so there is probably more here than is needed.
 * 
 * 
 * 
 * see http://www.stolaf.edu/academics/chemapps/jmol/docs/misc/JVXL-format.pdf
 *
 * The JVXL (Jmol VoXeL) format is a file format specifically designed
 * to encode an isosurface or planar slice through a set of 3D scalar values
 * in lieu of a that set. A JVXL file can contain coordinates, and in fact
 * it must contain at least one coordinate, but additional coordinates are
 * optional. The file can contain any finite number of encoded surfaces. 
 * However, the compression of 300-500:1 is based on the reduction of the 
 * data to a SINGLE surface. 
 * 
 * 
 * The original Marching Cubes code was written by Miguel Howard in 2005.
 * The classes Parser, ArrayUtil, and TextFormat are condensed versions
 * of the classes found in org.jmol.util.
 * 
 * All code relating to JVXL format is copyrighted 2006/2007 and invented by 
 * Robert M. Hanson, 
 * Professor of Chemistry, 
 * St. Olaf College, 
 * 1520 St. Olaf Ave.
 * Northfield, MN. 55057.
 * 
 * Implementations of the JVXL format should reference 
 * "Robert M. Hanson, St. Olaf College" and the opensource Jmol project.
 * 
 * 
 * implementing marching squares; see 
 * http://www.secam.ex.ac.uk/teaching/ug/studyres/COM3404/COM3404-2006-Lecture15.pdf
 * 
 * lines through coordinates are identical to CUBE files
 * after that, we have a line that starts with a negative number to indicate this
 * is a JVXL file:
 * 
 * line1:  (int)-nSurfaces  (int)edgeFractionBase (int)edgeFractionRange  
 * (nSurface lines): (float)cutoff (int)nBytesData (int)nBytesFractions
 * 
 * definition1
 * edgedata1
 * fractions1
 * colordata1
 * ....
 * definition2
 * edgedata2
 * fractions2
 * colordata2
 * ....
 * 
 * definitions: a line with detail about what sort of compression follows
 * 
 * edgedata: a list of the count of vertices ouside and inside the cutoff, whatever
 * that may be, ordered by nested for loops for(x){for(y){for(z)}}}.
 * 
 * nOutside nInside nOutside nInside...
 * 
 * fractions: an ascii list of characters represting the fraction of distance each
 * encountered surface point is along each voxel cube edge found to straddle the 
 * surface. The order written is dictated by the reader algorithm and is not trivial
 * to describe. Each ascii character is constructed by taking a base character and 
 * adding onto it the fraction times a range. This gives a character that can be
 * quoted EXCEPT for backslash, which MAY be substituted for by '!'. Jmol uses the 
 * range # - | (35 - 124), reserving ! and } for special meanings.
 * 
 * colordata: same deal here, but with possibility of "double precision" using two bytes.
 * 
 * 
 * 
 * THIS READER
 * -----------
 * 
 * This is a first attempt at a generic JVXL file reader and writer class.
 * It is an extraction of Jmol org.jmol.viewer.Isosurface.Java and related pieces.
 * 
 * The goal of the reader is to be able to read CUBE-like data and 
 * convert that data to JVXL file data.
 * 
 * 
 */

package org.jmol.jvxl.readers;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

import org.jmol.atomdata.RadiusData;
import org.jmol.util.ColorEncoder;
import org.jmol.util.Escape;
import org.jmol.util.Logger;

import org.jmol.viewer.JmolConstants;

public class Parameters {

  public final static int STATE_UNINITIALIZED = 0;
  public final static int STATE_INITIALIZED = 1;
  public final static int STATE_DATA_READ = 2;
  public final static int STATE_DATA_COLORED = 3;

  int state = STATE_UNINITIALIZED;

  boolean logMessages = false;
  boolean logCompression = false;
  boolean logCube = false;
  boolean isSilent = false;
  
  float assocCutoff = 0.3f; // fraction along the edge to use as a cutoff for averaging of normals  

  final static int NO_ANISOTROPY = 1 << 5;
  final static int IS_SILENT = 1 << 6;
  final public static int IS_SOLVENTTYPE = 1 << 7;
  final static int HAS_MAXGRID = 1 << 8;
  
  public int dataType;
  int surfaceType;

  final static int SURFACE_NONE = 0;

  //getSurface only:
  final static int SURFACE_SPHERE = 1 | IS_SILENT;
  final static int SURFACE_ELLIPSOID2 = 2 | IS_SILENT;
  final static int SURFACE_ELLIPSOID3 = 3 | IS_SILENT;
  final static int SURFACE_LOBE = 4 | IS_SILENT;
  final static int SURFACE_LCAOCARTOON = 5 | IS_SILENT;
  final static public int SURFACE_LONEPAIR = 6 | IS_SILENT;
  final static public int SURFACE_RADICAL = 7 | IS_SILENT;
  final static int SURFACE_FUNCTIONXY = 8;
  final static int SURFACE_FUNCTIONXYZ = 9;

  // getSurface or mapColor:
  final static int SURFACE_SOLVENT = 11 | IS_SOLVENTTYPE | NO_ANISOTROPY;
  final static int SURFACE_SASURFACE = 12 | IS_SOLVENTTYPE | NO_ANISOTROPY;
  final static int SURFACE_MOLECULARORBITAL = 13 | NO_ANISOTROPY | HAS_MAXGRID;
  final static int SURFACE_ATOMICORBITAL = 14 | HAS_MAXGRID;
  final static int SURFACE_MEP = 16 | NO_ANISOTROPY | HAS_MAXGRID;
  final static int SURFACE_MLP = 17 | NO_ANISOTROPY | HAS_MAXGRID;
  final static int SURFACE_MOLECULAR = 19 | IS_SOLVENTTYPE | NO_ANISOTROPY;

  // mapColor only:

  final static int SURFACE_NOMAP = 20 | IS_SOLVENTTYPE | NO_ANISOTROPY;
  final static int SURFACE_PROPERTY = 21 | IS_SOLVENTTYPE | NO_ANISOTROPY;

  void initialize() {
    addHydrogens = false;
    atomIndex = -1;
    blockCubeData = false; // Gaussian standard, but we allow for multiple surfaces one per data block
    bsIgnore = null;
    bsSelected = null;
    bsSolvent = null;
    calculationType = "";
    center = new Point3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    cappingObject= null;
    colorBySign = colorByPhase = colorBySets = false;
    colorDensity = false;
    colorEncoder = null;
    colorNeg = defaultColorNegative;
    colorNegLCAO = defaultColorNegativeLCAO;
    colorPos = defaultColorPositive;
    colorPosLCAO = defaultColorPositiveLCAO;
    cutoff = Float.MAX_VALUE;
    cutoffAutomatic = true;
    dataXYReversed = false;
    distance = Float.MAX_VALUE;
    doFullMolecular = false;
    envelopeRadius = 10f;
    fileIndex = 1;
    readAllData = true;
    fileName = "";
    functionXYinfo = null;
    iAddGridPoints = false;
    insideOut = false;
    isAngstroms = false;
    isBicolorMap = isCutoffAbsolute = isPositiveOnly = false;
    isCavity = false;
    isColorReversed = false;
    isSquared = false;
    isContoured = false;
    isEccentric = isAnisotropic = false;
    isSilent = false;
    iUseBitSets = false;
    logCube = logCompression = false;
    logMessages = Logger.debugging;
    mappedDataMin = Float.MAX_VALUE;
    minSet = 0;
    modelIndex = -1;
    boundingBox = null;
    bsExcluded = new BitSet[4];
    nContours = 0;
    colorSchemeTranslucent = false;
    contourIncrements = null;
    contoursDiscrete = null;
    contourColixes = null;
    mep_calcType = -1;
    pocket = null;
    propertyDistanceMax = Integer.MAX_VALUE;
    propertySmoothing = false;
    propertySmoothingPower = 4;
    rangeDefined = false;
    resolution = Float.MAX_VALUE;
    scale = Float.NaN;
    scale3d = 0;
    sigma = Float.NaN;
    slabbingObject = null;
    solventExtendedAtomRadius = 0;
    state = STATE_INITIALIZED;
    thePlane = null;
    theProperty = null;
    usePropertyForColorRange = true; // except for MEP and MLP
    thisContour = -1;
    contourFromZero = true;
    title = null;
    atomRadiusData = null;
    rangeAll = false;
    rangeSelected = false;
    isPeriodic = false;
  }
  
  String calculationType = "";

  //solvent/molecular-related:
  RadiusData atomRadiusData;
  boolean addHydrogens;
  float solventRadius;
  float solventExtendedAtomRadius;
  boolean propertySmoothing;
  int propertySmoothingPower = 4;
  float envelopeRadius;
  float cavityRadius;
  boolean isCavity;
  Boolean pocket; //three states: TRUE, FALSE, and NULL
  int minSet;
  public Object cappingObject;
  public Object slabbingObject;
  float slabPlaneOffset = Float.NaN;

  float[] theProperty;
  ///// solvent-accessible, solvent-excluded surface //////

  float solvent_ptsPerAngstrom = 4f;
  int solvent_gridMax = 60;


  //defaults
  
  final static float ANGSTROMS_PER_BOHR = JmolConstants.ANGSTROMS_PER_BOHR;
  final static int defaultEdgeFractionBase = 35; //#$%.......
  final static int defaultEdgeFractionRange = 90;
  final static int defaultColorFractionBase = 35;
  final static int defaultColorFractionRange = 90;
  final static float defaultMappedDataMin = 0f;
  final static float defaultMappedDataMax = 1.0f;
  final static float defaultCutoff = 0.02f;
  final static float defaultOrbitalCutoff = 0.14f;
  public final static float defaultQMOrbitalCutoff = 0.050f; // WebMO
  final static float defaultQMElectronDensityCutoff = 0.010f;
  final static int defaultContourCount = 11; //odd is better
  final static int nContourMax = 100;
  final static int defaultColorNegative = 0xFFFF0000; //red
  final static int defaultColorPositive =  0xFF0000FF; //blue
  final static int defaultColorNegativeLCAO = 0xFF800080; //purple
  final static int defaultColorPositiveLCAO = 0xFFFFA500; //orange
  final static float defaultSolventRadius = 1.2f;
  final static float defaultMepCutoff = 0.1f;
  final static float defaultMepMin = -0.1f;
  final static float defaultMepMax = 0.1f;

  //color
  
  boolean colorBySign;
  boolean colorByPhase;
  boolean colorBySets;
  int colorNeg;
  int colorPos;
  int colorPosLCAO;
  int colorNegLCAO;
  int colorPhase;
  public boolean colorDensity;

   
  //special effects
  boolean iAddGridPoints;
  boolean remappable;
  
  /////////////////////////////
  
  int atomIndex; //for lcaoCartoons
  
  boolean isAngstroms;
  float scale;
  public float scale3d;
  
  float[] anisotropy = new float[3];
  boolean isAnisotropic;

  void setAnisotropy(Point3f pt) { 
      anisotropy[0] = pt.x;
      anisotropy[1] = pt.y;
      anisotropy[2] = pt.z;
      isAnisotropic = true;
      if (center.x == Float.MAX_VALUE)
        center.set(0, 0, 0);
  }
  
  Matrix3f eccentricityMatrix;
  Matrix3f eccentricityMatrixInverse;
  boolean isEccentric;
  float eccentricityScale;
  float eccentricityRatio;
  float[] aniosU;

  void setEccentricity(Point4f info) {
    /*
     * {cx cy cz fab/c}
     * 
     * 1) set ecc = {cx cy cz}
     * 2) normalize
     * 3) add z and normalize again. This gives the vector about which a 180-degree
     *    rotation turns {0 0 1} into ecc. 
     * 
     */
    Vector3f ecc = new Vector3f(info.x, info.y, info.z);
    float c = (scale > 0 ? scale : info.w < 0 ? 1f : ecc.length());
    float fab_c = Math.abs(info.w);
    ecc.normalize();
    Vector3f z = new Vector3f(0, 0, 1);
    ecc.add(z);
    ecc.normalize();
    if (Float.isNaN(ecc.x)) // was exactly {0 0 -1} -- just rotate about x
      ecc.set(1, 0, 0);
    eccentricityMatrix = new Matrix3f();
    eccentricityMatrix.setIdentity();
    eccentricityMatrix.set(new AxisAngle4f(ecc, (float) Math.PI));
    eccentricityMatrixInverse = new Matrix3f();
    eccentricityMatrixInverse.invert(eccentricityMatrix);
    isEccentric = isAnisotropic = true;
    eccentricityScale = c;
    eccentricityRatio = fab_c;
    if (fab_c > 1)
      eccentricityScale *= fab_c;
    anisotropy[0] = fab_c * c;
    anisotropy[1] = fab_c * c;
    anisotropy[2] = c;
    if (center.x == Float.MAX_VALUE)
      center.set(0, 0, 0);
  }

  void setPlane(Point4f plane) {
    thePlane = plane;
    if (thePlane.x == 0 && thePlane.y == 0
        && thePlane.z == 0)
      thePlane.z = 1; //{0 0 0 w} becomes {0 0 1 w}
    isContoured = true;
  }

  void setSphere(float radius) {
    dataType = SURFACE_SPHERE;
    distance = radius;
    setEccentricity(new Point4f(0, 0, 1, 1));
    cutoff = Float.MIN_VALUE;
    isCutoffAbsolute = false;
    isSilent = !logMessages;
    script = getScriptParams() + " SPHERE " + radius + ";";
  }
  
  void setEllipsoid(Point4f v) {
    dataType = SURFACE_ELLIPSOID2;
    distance = 1f;
    setEccentricity(v);
    cutoff = Float.MIN_VALUE;
    isCutoffAbsolute = false;
    isSilent = !logMessages;
    //script = " center " + Escape.escape(center)
      //  + (Float.isNaN(scale) ? "" : " scale " + scale) + " ELLIPSOID {" + v.x
        //+ " " + v.y + " " + v.z + " " + v.w + "};";
  }

  float[] anisoB;
  public void setEllipsoid(float[] bList) {
    anisoB = bList;
    //for (int i = 0; i < 6; i++)System.out.print(bList[i] + " ");System.out.println( " in Parameters setEllipsoid" + center);
    dataType = SURFACE_ELLIPSOID3;
    distance = 0.3f * (Float.isNaN(scale) ? 1f : scale);
    cutoff = Float.MIN_VALUE;
    isCutoffAbsolute = false;
    isSilent = !logMessages;
    if (center.x == Float.MAX_VALUE)
      center.set(0, 0, 0);
    if (resolution == Float.MAX_VALUE)
      resolution = 6;
    //script = " center " + Escape.escape(center)
      //  + (Float.isNaN(scale) ? "" : " scale " + scale) + " ELLIPSOID {" + bList[0]
        //+ " " + bList[1] + " " + bList[2] + " " + bList[3] + " " + bList[4] + " " + bList[5] + "};";
  }

  void setLobe(Point4f v) {
    dataType = SURFACE_LOBE;
    setEccentricity(v);
    if (cutoff == Float.MAX_VALUE) {
      cutoff = defaultOrbitalCutoff;
      if (isSquared)
        cutoff = cutoff * cutoff;
    }
    isSilent = !logMessages;
    script = getScriptParams() + " LOBE {" + v.x + " "
        + v.y + " " + v.z + " " + v.w + "};";
  }
  
  private String getScriptParams() {
    return (slabbingObject == null ? "" : " slab within "
        + Escape.escape(slabbingObject))
        + (cappingObject == null ? "" : " cap within "
            + Escape.escape(cappingObject))
        + " center "
        + Escape.escape(center) + (Float.isNaN(scale) ? "" : " scale " + scale);
  }

  void setLp(Point4f v) {
    dataType = SURFACE_LONEPAIR;
    setEccentricity(v);
    if (cutoff == Float.MAX_VALUE) {
      cutoff = defaultOrbitalCutoff;
      if (isSquared)
        cutoff = cutoff * cutoff;
    }
    isSilent = !logMessages;
    script = " center " + Escape.escape(center)
        + (Float.isNaN(scale) ? "" : " scale " + scale) + " LP {" + v.x + " "
        + v.y + " " + v.z + " " + v.w + "};";
  }
  
  void setRadical(Point4f v) {
    dataType = SURFACE_RADICAL;
    setEccentricity(v);
    if (cutoff == Float.MAX_VALUE) {
      cutoff = defaultOrbitalCutoff;
      if (isSquared)
        cutoff = cutoff * cutoff;
    }
    isSilent = !logMessages;
    script = " center " + Escape.escape(center)
        + (Float.isNaN(scale) ? "" : " scale " + scale) + " RAD {" + v.x + " "
        + v.y + " " + v.z + " " + v.w + "};";
  }
  
  String lcaoType;

  void setLcao(String type, int colorPtr) {
    lcaoType = type;
    if (colorPtr == 1)
      colorPosLCAO = colorNegLCAO;
    isSilent = !logMessages;
  }
    
  void setRadius(RadiusData rd) {
    atomRadiusData = rd;
  }

  void setSolvent(String propertyName, float radius) {
    isEccentric = isAnisotropic = false;
    //anisotropy[0] = anisotropy[1] = anisotropy[2] = 1f;
    solventRadius = Math.abs(radius);
    dataType = ("nomap" == propertyName ? SURFACE_NOMAP
        : "molecular" == propertyName ? SURFACE_MOLECULAR
            : "sasurface" == propertyName || solventRadius == 0f ? SURFACE_SASURFACE
                : SURFACE_SOLVENT);

    switch (dataType) {
    case Parameters.SURFACE_NOMAP:
      calculationType = "unmapped plane";
      break;
    case Parameters.SURFACE_MOLECULAR:
      calculationType = "molecular surface with radius " + solventRadius;
      break;
    case Parameters.SURFACE_SOLVENT:
      calculationType = "solvent-excluded surface with radius " + solventRadius;
      break;
    case Parameters.SURFACE_SASURFACE:
      calculationType = "solvent-accessible surface with radius "
          + solventRadius;
      break;
    }

    switch (dataType) {
    case SURFACE_NOMAP:
      solventExtendedAtomRadius = solventRadius;
      solventRadius = 0f;
      isContoured = false;
      break;
    case SURFACE_MOLECULAR:
      solventExtendedAtomRadius = 0f;
      break;
    case SURFACE_SOLVENT:
      solventExtendedAtomRadius = 0f;
      if (bsIgnore == null)
        bsIgnore = bsSolvent;
      break;
    case SURFACE_SASURFACE:
      solventExtendedAtomRadius = solventRadius;
      solventRadius = 0f;
      if (bsIgnore == null)
        bsIgnore = bsSolvent;
      break;
    }
  }
  
  public List<Object> functionXYinfo;
  
  void setFunctionXY(List<Object> value) {
    dataType = SURFACE_FUNCTIONXY;
    functionXYinfo = value;
    cutoff = Float.MIN_VALUE;
    isEccentric = isAnisotropic = false;
  }

  void setFunctionXYZ(List<Object> value) {
    dataType = SURFACE_FUNCTIONXYZ;
    functionXYinfo = value;
    cutoff = Float.MIN_VALUE;
    isEccentric = isAnisotropic = false;
  }

  int psi_n = 2;
  int psi_l = 1;
  int psi_m = 1;
  float psi_Znuc = 1; // hydrogen
  float psi_ptsPerAngstrom = 5f;

  boolean setAtomicOrbital(float[] nlmZ) {
    dataType = SURFACE_ATOMICORBITAL;
    psi_n = (int) nlmZ[0];
    psi_l = (int) nlmZ[1];
    psi_m = (int) nlmZ[2];
    psi_Znuc = nlmZ[3];
    psi_ptsPerAngstrom = 10;
    // quantum rule is abs(m) <= l < n
    if (cutoff == Float.MAX_VALUE) {
      cutoff = defaultOrbitalCutoff;
      if (isSquared)
        cutoff = cutoff * cutoff;
    }
    isCutoffAbsolute = true;
    if (state < STATE_DATA_READ && thePlane == null && colorBySign)
      isBicolorMap = true;
    return (psi_Znuc > 0 && Math.abs(psi_m) <= psi_l && psi_l < psi_n);
  }  
  
 
  public final static int MEP_MAX_GRID = 40;
  int mep_gridMax = MEP_MAX_GRID;
  float mep_ptsPerAngstrom = 3f;
  float mep_marginAngstroms = 1f; // may have to adjust this
  public int mep_calcType = -1;

  void setMep(float[] charges, boolean isMLP) {
    dataType = (isMLP ? SURFACE_MLP : SURFACE_MEP);
    theProperty = charges;
    usePropertyForColorRange = false;
    isEccentric = isAnisotropic = false;
    if (cutoff == Float.MAX_VALUE) {
      cutoff = defaultMepCutoff;
      if (isSquared)
        cutoff = cutoff * cutoff;
    }
    isCutoffAbsolute = (cutoff > 0 && !isPositiveOnly);
    contourFromZero = false; // fills out the plane
    //colorBySign = false;
    //isBicolorMap = false;
    if (state >= STATE_DATA_READ || thePlane != null) {
      if (!rangeDefined && !rangeAll) {
        valueMappedToRed = defaultMepMin;
        valueMappedToBlue = defaultMepMax;
        rangeDefined = true;
      }
    } else {
      colorBySign = true;
      //colorByPhase = true;
      //colorPhase = 0;
      isBicolorMap = true;
    }
  }
  
  int qmOrbitalType;
  int qmOrbitalCount;
  
  final static int QM_TYPE_UNKNOWN = 0;
  final static int QM_TYPE_GAUSSIAN = 1;
  final static int QM_TYPE_SLATER = 2;
  
  Map<String, Object> moData;
  Map<String, Object> mo;
  float[] moCoefficients;
  int[][] dfCoefMaps;
  public final static int MO_MAX_GRID = 80;
  int qm_gridMax = MO_MAX_GRID;
  float qm_ptsPerAngstrom = 10f;
  float qm_marginAngstroms = 1f; // may have to adjust this
  int qm_nAtoms;
  int qm_moNumber = Integer.MAX_VALUE;
  float[] qm_moLinearCombination = null;
  
  @SuppressWarnings("unchecked")
  void setMO(int iMo, float[] linearCombination) {
    iUseBitSets = true;
    qm_moLinearCombination = linearCombination;
    qm_moNumber = (linearCombination == null ? Math.abs(iMo) : (int) linearCombination[1]);
    qmOrbitalType = (moData.containsKey("gaussians") ? QM_TYPE_GAUSSIAN
        : moData.containsKey("slaters") ? QM_TYPE_SLATER : QM_TYPE_UNKNOWN);
    boolean isElectronDensity = (iMo <= 0 && linearCombination == null);
    if (qmOrbitalType == QM_TYPE_UNKNOWN) {
      //TODO     value = moData; // must be generic surface info
      Logger
          .error("MO ERROR: No basis functions found in file for MO calculation. (GAUSSIAN 'gfprint' keyword may be missing?)");
      mo = null;
      title = new String[] {"no basis functions found in file"};
    } else {
      List<Object> mos = (List<Object>) moData.get("mos");
      qmOrbitalCount = mos.size();
      calculationType = (String) moData.get("calculationType");
      calculationType = "Molecular orbital #" + qm_moNumber + "/"
          + qmOrbitalCount + " "
          + (calculationType == null ? "" : calculationType);
      if (!isElectronDensity) {
        // qm_moNumber < 0 means this is an RHF electron density calculation 
        // through orbital -qm_moNumber
        if (title == null) {
          title = new String[5];
          title[0] = "%F";
          title[1] = "Model %M  MO %I/%N %T";
          title[2] = "?Energy = %E %U";
          title[3] = "?Symmetry = %S";
          title[4] = "?Occupancy = %O";
        }
        mo = (Map<String, Object>) mos.get(qm_moNumber - 1);
        moCoefficients = (float[]) mo.get("coefficients");
        dfCoefMaps = (int[][]) mo.get("dfCoefMaps");
      }
    }
    dataType = SURFACE_MOLECULARORBITAL;
    //  colorBySign = false;
    //  isBicolorMap = false;
    if (cutoff == Float.MAX_VALUE) {
      cutoff = (isElectronDensity ? defaultQMElectronDensityCutoff
          : defaultQMOrbitalCutoff);
      if (isSquared)
        cutoff = cutoff * cutoff;
    }
    isEccentric = isAnisotropic = false;
    isCutoffAbsolute = (cutoff > 0 && !isPositiveOnly);
    if (state >= STATE_DATA_READ || thePlane != null)
      return;
    colorBySign = true;
    if (colorByPhase && colorPhase == 0)
      colorByPhase = false;
    isBicolorMap = true;
  }
  
  Point3f center, point;
  float distance;
  
  String script;
  
  BitSet bsSelected;
  BitSet bsIgnore;
  BitSet bsSolvent;
  
  boolean iUseBitSets = false;
  
  String[] title;
  boolean blockCubeData;
  boolean readAllData;
  int fileIndex; //one-based
  String fileName;
  int modelIndex = -1; // zero-based
  public boolean isXLowToHigh;
  
  boolean insideOut;
  boolean dataXYReversed;
  public float cutoff = Float.MAX_VALUE;
  public float sigma = Float.MAX_VALUE; // for MrcReader
  boolean cutoffAutomatic = true;
  public boolean isCutoffAbsolute;
  boolean isPositiveOnly;
  
  boolean rangeAll;
  boolean rangeSelected;
  public boolean rangeDefined;
  float valueMappedToRed, valueMappedToBlue;
  float mappedDataMin;
  float mappedDataMax;
  boolean isColorReversed;
  boolean isBicolorMap;
  public boolean isSquared;

  public Point4f thePlane;
  public boolean isContoured;
  
  int nContours;
  int thisContour; 
  boolean contourFromZero;
 
  float resolution;
  int downsampleFactor;
  int maxSet;
  public float[] contoursDiscrete;
  public short[] contourColixes;
  Point3f contourIncrements;
  public Point3f[] boundingBox;
  public BitSet[] bsExcluded;
  public int contourType;
  public boolean colorSchemeTranslucent;
  public ColorEncoder colorEncoder;
  public boolean usePropertyForColorRange = true;
  public boolean isPeriodic;
  public boolean doFullMolecular;
  public float propertyDistanceMax = Integer.MAX_VALUE;
  
  void setMapRanges(SurfaceReader surfaceReader, boolean haveData) {
    if (!colorDensity)
      if (colorByPhase || colorBySign || (thePlane != null || isBicolorMap) && !isContoured) {
      mappedDataMin = -1;
      mappedDataMax = 1;
    }
    if (mappedDataMin == Float.MAX_VALUE || mappedDataMin == mappedDataMax) {
      float[] minMax = surfaceReader.getMinMaxMappedValues(haveData);
      mappedDataMin = minMax[0];
      mappedDataMax = minMax[1];
    }
    if (mappedDataMin == 0 && mappedDataMax == 0) {
      //just set default -1/1 if there is no obvious data
      mappedDataMin = -1;
      mappedDataMax = 1;
    }

    if (!rangeDefined) {
      valueMappedToRed = mappedDataMin;
      valueMappedToBlue = mappedDataMax;
    }
  }

}
