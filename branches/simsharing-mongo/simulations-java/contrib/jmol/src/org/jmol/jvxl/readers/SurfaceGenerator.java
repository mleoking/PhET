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
 * fractions: an ascii list of characters representing the fraction of distance each
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


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.util.*;
import org.jmol.atomdata.AtomDataServer;
import org.jmol.atomdata.RadiusData;
import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.jvxl.data.JvxlData;
import org.jmol.jvxl.data.VolumeData;
import org.jmol.jvxl.data.MeshData;
import org.jmol.jvxl.api.MeshDataServer;
import org.jmol.jvxl.calc.MarchingSquares;

public class SurfaceGenerator {

  private JvxlData jvxlData;
  private MeshData meshData;
  private Parameters params;
  private VolumeData volumeData;
  private MeshDataServer meshDataServer;
  private AtomDataServer atomDataServer;
  private MarchingSquares marchingSquares;
  private String version;
  private boolean isValid = true;
  public boolean isValid() {
    return isValid;
  }
  private String fileType;
  public String getFileType() {
    return fileType;
  }
  private OutputStream os;
    
  public void setVersion(String version) {
    this.version = version;
  }
  
  SurfaceReader surfaceReader;

  public SurfaceGenerator() {
    // for Jvxl.java
    setup(null, null, null, null);
  }

  public SurfaceGenerator(AtomDataServer atomDataServer, MeshDataServer meshDataServer,
                          MeshData meshData, JvxlData jvxlData) {
    // for Jmol.java
    setup(atomDataServer, meshDataServer, meshData, jvxlData);
  }

  private void setup(AtomDataServer atomDataServer, MeshDataServer meshDataServer,
      MeshData meshData, JvxlData jvxlData) {
    this.atomDataServer = atomDataServer;
    this.meshDataServer = meshDataServer;
    params = new Parameters();
    this.meshData = (meshData == null ? new MeshData() : meshData);
    //System.out.println("SurfaceGenerator setup vertexColixs =" + this.meshData.vertexColixes);
    this.jvxlData = (jvxlData == null ? new JvxlData() : jvxlData);
    volumeData = new VolumeData();
    initializeIsosurface();
  }
  
  public boolean isStateDataRead() {
    return params.state == Parameters.STATE_DATA_READ;
  }

  public int getDataType() {
    return params.dataType;
  }
  
  public String getFileName() {
    return params.fileName;
  }
  
  MeshDataServer getMeshDataServer() {
    return meshDataServer;
  }

  AtomDataServer getAtomDataServer() {
    return atomDataServer;
  }

  public ColorEncoder getColorEncoder() {
    return params.colorEncoder;
  }

  public void setJvxlData(JvxlData jvxlData) {
    this.jvxlData = jvxlData;
    if (jvxlData != null)
      jvxlData.version = version;
  }

  public JvxlData getJvxlData() {
    return jvxlData;
  }

  MeshData getMeshData() {
    return meshData;
  }
/*
  public void setMeshData(MeshData meshData) {
    this.meshData = meshData;
  }
*/
  void setMarchingSquares(MarchingSquares marchingSquares) {
    this.marchingSquares = marchingSquares;  
  }
  
  MarchingSquares getMarchingSquares() {
    return marchingSquares;
  }
  
  public Parameters getParams() {
    return params;
  }

  public String getScript() {
    //System.out.println("getting script " + params.script);
    return params.script;
  }
  
  public String[] getTitle() {
    return params.title;
  }
  
  public BitSet getBsSelected() {
    return params.bsSelected;
  }
  
  public BitSet getBsIgnore() {
    return params.bsIgnore;
  }
  
  VolumeData getVolumeData() {
    return volumeData;
  }

  public Point4f getPlane() {
    return params.thePlane;
  }
  
  public int getColor(int which) {
    switch(which) {
    case -1:
      return params.colorNeg;
    case 1:
      return params.colorPos;
    }
    return 0;
  }
/*  
  public void setScript(String script) {
    params.script = script;
  }
*/  
  public void setModelIndex(int modelIndex) {
    params.modelIndex = modelIndex;
  }

  public boolean getIUseBitSets() {
    return params.iUseBitSets;
  }

  public boolean getIAddGridPoints() {
    return params.iAddGridPoints;
  }

  public boolean getIsPositiveOnly() {
    return params.isPositiveOnly;
  }
  
  public boolean isInsideOut() {
    return params.insideOut != params.dataXYReversed;
  }

  public float getCutoff() {
    return params.cutoff;
  }
  
  public Map<String, Object> getMoData() {
    return params.moData;
  }
  
  public boolean isCubeData() {
    return jvxlData.wasCubic;
  }
  
  //////////////////////////////////////////////////////////////

  int colorPtr;

  /**
   * setParameter is the main interface for surface generation. 
   * 
   * @param propertyName
   * @param value
   * @return         True if handled; False if not
   * 
   */

  public boolean setParameter(String propertyName, Object value) {
    return setParameter(propertyName, value, null);
  }

  /**
   * 
   * @param propertyName
   * @param value
   * @param bs
   * @return TRUE if done processing
   */
  @SuppressWarnings("unchecked")
  public boolean setParameter(String propertyName, Object value, BitSet bs) {

    if ("debug" == propertyName) {
      boolean TF = ((Boolean) value).booleanValue();
      params.logMessages = TF;
      // logCompression = TF;
      params.logCube = TF;
      return true;
    }

    if ("init" == propertyName) {
      initializeIsosurface();
      if (value instanceof Parameters) {
        params = (Parameters) value;
      } else {
        params.script = (String) value;
        if (params.script != null && params.script.indexOf(";#") >= 0) {
          // crude hack for ScriptEvaluator messing up
          params.script = TextFormat.simpleReplace(params.script, ";#", "; #");
        }
      }
      return false; // more to do
    }

    if ("finalize" == propertyName) {
      initializeIsosurface();
      return true;
    }

    if ("clear" == propertyName) {
      if (surfaceReader != null)
        surfaceReader.discardTempData(true);
      return false;
    }

    if ("fileIndex" == propertyName) {
      params.fileIndex = ((Integer) value).intValue();
      if (params.fileIndex < 1)
        params.fileIndex = 1;
      params.readAllData = false;
      return true;
    }

    if ("blockData" == propertyName) {
      params.blockCubeData = ((Boolean) value).booleanValue();
      return true;
    }

    if ("withinPoints" == propertyName) {
      params.boundingBox = (Point3f[]) ((Object[]) value)[1];
      return true;
    }

    if ("boundingBox" == propertyName) {
      Point3f[] pts = (Point3f[]) value;
      params.boundingBox = new Point3f[] { new Point3f(pts[0]),
          new Point3f(pts[pts.length - 1]) };
      return true;
    }

    if ("bsSolvent" == propertyName) {
      params.bsSolvent = (BitSet) value;
      return true;
    }

    if ("select" == propertyName) {
      params.bsSelected = (BitSet) value;
      return true;
    }

    if ("ignore" == propertyName) {
      params.bsIgnore = (BitSet) value;
      return true;
    }

    if ("propertySmoothing" == propertyName) {
      params.propertySmoothing = ((Boolean) value).booleanValue();
      return true;
    }

    if ("propertyDistanceMax" == propertyName) {
      params.propertyDistanceMax = ((Float) value).floatValue();
      return true;
    }

    if ("propertySmoothingPower" == propertyName) {
      params.propertySmoothingPower = ((Integer) value).intValue();
      return true;
    }

    if ("title" == propertyName) {
      if (value == null) {
        params.title = null;
        return true;
      } else if (value instanceof String[]) {
        params.title = (String[]) value;
        for (int i = 0; i < params.title.length; i++)
          if (params.title[i].length() > 0)
            Logger.info(params.title[i]);
      }
      return true;
    }

    if ("sigma" == propertyName) {
      // not all readers will take this, so we assign
      // cutoff to the value as well.
      params.cutoff = params.sigma = ((Float) value).floatValue();
      params.isPositiveOnly = false;
      params.cutoffAutomatic = false;
      return true;
    }

    if ("cutoff" == propertyName) {
      params.cutoff = ((Float) value).floatValue();
      params.isPositiveOnly = false;
      params.cutoffAutomatic = false;
      return true;
    }

    if ("cutoffPositive" == propertyName) {
      params.cutoff = ((Float) value).floatValue();
      params.isPositiveOnly = true;
      return true;
    }

    if ("cap" == propertyName) {
      params.cappingObject = value;
      return true;
    }

    if ("slab" == propertyName) {
      params.slabbingObject = value;
      return true;
    }

    if ("scale" == propertyName) {
      params.scale = ((Float) value).floatValue();
      return true;
    }

    if ("scale3d" == propertyName) {
      params.scale3d = ((Float) value).floatValue();
      return true;
    }

    if ("angstroms" == propertyName) {
      params.isAngstroms = true;
      return true;
    }

    if ("resolution" == propertyName) {
      float resolution = ((Float) value).floatValue();
      params.resolution = (resolution > 0 ? resolution : Float.MAX_VALUE);
      return true;
    }

    if ("downsample" == propertyName) {
      int rate = ((Integer) value).intValue();
      params.downsampleFactor = (rate >= 0 ? rate : 0);
      return true;
    }

    if ("anisotropy" == propertyName) {
      if ((params.dataType & Parameters.NO_ANISOTROPY) == 0)
        params.setAnisotropy((Point3f) value);
      return true;
    }

    if ("eccentricity" == propertyName) {
      params.setEccentricity((Point4f) value);
      return true;
    }

    if ("addHydrogens" == propertyName) {
      params.addHydrogens = ((Boolean) value).booleanValue();
      return true;
    }

    if ("squareData" == propertyName) {
      params.isSquared = ((Boolean) value).booleanValue();
      return true;
    }

    if ("gridPoints" == propertyName) {
      params.iAddGridPoints = true;
      return true;
    }

    if ("atomIndex" == propertyName) {
      params.atomIndex = ((Integer) value).intValue();
      return true;
    }

    // / color options

    if ("remappable" == propertyName) {
      params.remappable = true;
      return true;
    }

    if ("insideOut" == propertyName) {
      params.insideOut = true;
      return true;
    }

    if ("sign" == propertyName) {
      params.isCutoffAbsolute = true;
      params.colorBySign = true;
      colorPtr = 0;
      return true;
    }

    if ("colorRGB" == propertyName) {
      int rgb = ((Integer) value).intValue();
      params.colorPos = params.colorPosLCAO = rgb;
      if (colorPtr++ == 0)
        params.colorNeg = params.colorNegLCAO = rgb;
      return true;
    }

    if ("rangeAll" == propertyName) {
      params.rangeAll = true;
      return true;
    }

    if ("rangeSelected" == propertyName) {
      params.rangeSelected = true;
      return true;
    }

    if ("red" == propertyName) {
      params.valueMappedToRed = ((Float) value).floatValue();
      return true;
    }

    if ("blue" == propertyName) {
      params.valueMappedToBlue = ((Float) value).floatValue();
      if (params.valueMappedToRed > params.valueMappedToBlue) {
        float f = params.valueMappedToRed;
        params.valueMappedToRed = params.valueMappedToBlue;
        params.valueMappedToBlue = f;
        params.isColorReversed = !params.isColorReversed;
      }
      params.rangeDefined = true;
      params.rangeAll = false;
      return true;
    }

    if ("reverseColor" == propertyName) {
      params.isColorReversed = true;
      return true;
    }

    if ("setColorScheme" == propertyName) {
      getSurfaceSets();
      params.colorBySets = true;
      mapSurface();
      return true;
    }

    if ("center" == propertyName) {
      params.center.set((Point3f) value);
      return true;
    }

    if ("withinDistance" == propertyName) {
      params.distance = ((Float) value).floatValue();
      return true;
    }

    if ("withinPoint" == propertyName) {
      params.point = (Point3f) value;
      return true;
    }

    if ("progressive" == propertyName) {
      // an option for JVXL.java
      params.isXLowToHigh = true;
      return true;
    }

    if ("phase" == propertyName) {
      String color = (String) value;
      params.isCutoffAbsolute = true;
      params.colorBySign = true;
      params.colorByPhase = true;
      params.colorPhase = SurfaceReader.getColorPhaseIndex(color);
      if (params.colorPhase < 0) {
        Logger.warn(" invalid color phase: " + color);
        params.colorPhase = 0;
      }
      params.colorByPhase = params.colorPhase != 0;
      if (params.state >= Parameters.STATE_DATA_READ) {
        params.dataType = params.surfaceType;
        params.state = Parameters.STATE_DATA_COLORED;
        params.isBicolorMap = true;
        surfaceReader.applyColorScale();
      }
      return true;
    }

    /*
     * Based on the form of the parameters, returns and encoded radius as
     * follows:
     * 
     * script meaning range encoded
     * 
     * +1.2 offset [0 - 10] x -1.2 offset 0) x 1.2 absolute (0 - 10] x + 10 -30%
     * 70% (-100 - 0) x + 200 +30% 130% (0 x + 200 80% percent (0 x + 100
     * 
     * in each case, numbers can be integer or float
     */

    if ("radius" == propertyName) {
      Logger.info("solvent probe radius set to " + value);
      params.setRadius((RadiusData) value);
      return true;
    }

    if ("envelopeRadius" == propertyName) {
      params.envelopeRadius = ((Float) value).floatValue();
      return true;
    }

    if ("cavityRadius" == propertyName) {
      params.cavityRadius = ((Float) value).floatValue();
      return true;
    }

    if ("cavity" == propertyName) {
      params.isCavity = true;
      return true;
    }

    if ("doFullMolecular" == propertyName) {
      params.doFullMolecular = true;
      return true;
    }

    if ("pocket" == propertyName) {
      params.pocket = (Boolean) value;
      return true;
    }

    if ("minset" == propertyName) {
      params.minSet = ((Integer) value).intValue();
      return true;
    }

    if ("maxset" == propertyName) {
      params.maxSet = ((Integer) value).intValue();
      return true;
    }

    if ("plane" == propertyName) {
      params.setPlane((Point4f) value);
      return true;
    }

    if ("contour" == propertyName) {
      params.isContoured = true;
      int n;
      if (value instanceof float[]) {
        // discrete values
        params.contoursDiscrete = (float[]) value;
        params.nContours = params.contoursDiscrete.length;
      } else if (value instanceof Point3f) {
        Point3f pt = params.contourIncrements = (Point3f) value;
        float from = pt.x;
        float to = pt.y;
        float step = pt.z;
        if (step <= 0)
          step = 1;
        n = 0;
        for (float p = from; p <= to + step / 10; p += step, n++) {
        }
        params.contoursDiscrete = new float[n];
        float p = from;
        for (int i = 0; i < n; i++, p += step) {
          params.contoursDiscrete[i] = p;
        }
        params.nContours = n;
      } else {
        n = ((Integer) value).intValue();
        if (n == 0)
          params.nContours = MarchingSquares.defaultContourCount;
        else if (n > 0)
          params.nContours = n;
        else
          params.thisContour = -n;
      }
      return true;
    }

    if ("colorDiscrete" == propertyName) {
      params.contourColixes = (short[]) value;
      return true;
    }

    if ("colorDensity" == propertyName) {
      params.colorDensity = true;
      return true;
    }
    if ("fullPlane" == propertyName) {
      // fullPlane == true --> params.contourFromZero is false
      // fullPlane == false --> params.contourFromZero is true
      // this only relates to projections onto a plane
      // the default is contourFromZero TRUE
      // but MEP default is contourFromZero FALSE
      // the setting is ignored when discrete contours
      // are specified, because in that case we just
      // define the triangle color by their centers

      params.contourFromZero = !((Boolean) value).booleanValue();
      return true;
    }
    // / final actions ///

    if ("property" == propertyName) {
      params.dataType = Parameters.SURFACE_PROPERTY;
      params.theProperty = (float[]) value;
      mapSurface();
      return true;
    }

    // these next four set the reader themselves.
    if ("sphere" == propertyName) {
      params.setSphere(((Float) value).floatValue());
      surfaceReader = new IsoShapeReader(this, params.distance);
      generateSurface();
      return true;
    }

    if ("ellipsoid" == propertyName) {
      if (value instanceof Point4f)
        params.setEllipsoid((Point4f) value);
      else if (value instanceof float[])
        params.setEllipsoid((float[]) value);
      else
        return true;
      surfaceReader = new IsoShapeReader(this, params.distance);
      generateSurface();
      return true;
    }

    if ("ellipsoid3" == propertyName) {
      params.setEllipsoid((float[]) value);
      surfaceReader = new IsoShapeReader(this, params.distance);
      generateSurface();
      return true;
    }

    if ("lp" == propertyName) {
      params.setLp((Point4f) value);
      surfaceReader = new IsoShapeReader(this, 3, 2, 0, 15);
      generateSurface();
      return true;
    }

    if ("rad" == propertyName) {
      params.setRadical((Point4f) value);
      surfaceReader = new IsoShapeReader(this, 3, 2, 0, 15);
      generateSurface();
      return true;
    }

    if ("lobe" == propertyName) {
      params.setLobe((Point4f) value);
      surfaceReader = new IsoShapeReader(this, 3, 2, 0, 15);
      generateSurface();
      return true;
    }

    if ("hydrogenOrbital" == propertyName) {
      if (!params.setAtomicOrbital((float[]) value)) {
        isValid = false;
        return true;
      }
      surfaceReader = new IsoShapeReader(this, params.psi_n, params.psi_l,
          params.psi_m, params.psi_Znuc);
      processState();
      return true;
    }

    if ("functionXY" == propertyName) {
      params.setFunctionXY((List<Object>) value);
      if (params.isContoured)
        volumeData.setPlaneParameters(new Point4f(0, 0, 1, 0)); // xy plane
      // through
      // origin
      if (((String) params.functionXYinfo.get(0)).indexOf("_xyz") >= 0)
        getFunctionZfromXY();
      processState();
      return true;
    }

    if ("functionXYZ" == propertyName) {
      params.setFunctionXYZ((List<Object>) value);
      processState();
      return true;
    }

    if ("lcaoType" == propertyName) {
      params.setLcao((String) value, colorPtr);
      return true;
    }

    if ("lcaoCartoonCenter" == propertyName) {
      if (++params.state != Parameters.STATE_DATA_READ)
        return true;
      if (params.center.x == Float.MAX_VALUE)
        params.center.set((Vector3f) value);
      return false;
    }

    if ("molecular" == propertyName || "solvent" == propertyName
        || "sasurface" == propertyName || "nomap" == propertyName) {
      params.setSolvent(propertyName, ((Float) value).floatValue());
      Logger.info(params.calculationType);
      if (params.state < Parameters.STATE_DATA_READ
          && (params.cutoffAutomatic || !params.colorDensity))
        params.cutoff = 0.0f;
      processState();
      return true;
    }

    if ("moData" == propertyName) {
      params.moData = (Map<String, Object>) value;
      return true;
    }

    if ("mepCalcType" == propertyName) {
      params.mep_calcType = ((Integer) value).intValue();
      return true;
    }

    if ("mep" == propertyName) {
      params.setMep((float[]) value, false); // mep charges
      processState();
      return true;
    }

    if ("mlp" == propertyName) {
      params.setMep((float[]) value, true); // mlp charges
      processState();
      return true;
    }

    if ("charges" == propertyName) {
      params.theProperty = (float[]) value;
      return true;
    }

    if ("molecularOrbital" == propertyName) {
      int iMo = 0;
      float[] linearCombination = null;
      if (value instanceof Integer) {
        iMo = ((Integer) value).intValue();
      } else {
        linearCombination = (float[]) value;
      }
      params.setMO(iMo, linearCombination);
      Logger.info(params.calculationType);
      processState();
      return true;
    }

    if ("fileType" == propertyName) {
      fileType = (String) value;
      return true;
    }

    if ("fileName" == propertyName) {
      params.fileName = (String) value;
      return true;
    }

    if ("outputStream" == propertyName) {
      os = (OutputStream) value;
      return true;
    }

    if ("readFile" == propertyName) {
      if ((surfaceReader = setFileData(value)) == null) {
        Logger.error("Could not set the surface data");
        return true;
      }
      surfaceReader.setOutputStream(os);
      generateSurface();
      return true;
    }

    if ("getSurfaceSets" == propertyName) {
      getSurfaceSets();
      return true;
    }

    if ("mapColor" == propertyName) {
      if ((surfaceReader = setFileData(value)) == null) {
        Logger.error("Could not set the mapping data");
        return true;
      }
      surfaceReader.setOutputStream(os);
      mapSurface();
      return true;
    }

    if ("periodic" == propertyName) {
      params.isPeriodic = true;
    }

    // continue with operations in calling class...
    return false;
  }

  private void getSurfaceSets() {
    if (meshDataServer == null) {
      meshData.getSurfaceSet();
    } else {
      meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_VERTICES, null);
      meshData.getSurfaceSet();
      meshDataServer.fillMeshData(meshData, MeshData.MODE_PUT_SETS, null);
    }
  }

  private void processState() {   
    if (params.state == Parameters.STATE_INITIALIZED && params.thePlane != null)
      params.state++;
    if (params.state >= Parameters.STATE_DATA_READ) {
      mapSurface();
    } else {
      generateSurface();
    }
  }
  
  private boolean setReader() {
    if (surfaceReader != null)
      return !surfaceReader.vertexDataOnly;
    switch (params.dataType) {
    case Parameters.SURFACE_NOMAP:
      surfaceReader = new IsoPlaneReader(this);
      break;
    case Parameters.SURFACE_PROPERTY:
      surfaceReader = new AtomPropertyMapper(this, null);
      break;
    case Parameters.SURFACE_SOLVENT:
    case Parameters.SURFACE_MOLECULAR:
    case Parameters.SURFACE_SASURFACE:
      surfaceReader = new IsoSolventReader(this);
      break;
    case Parameters.SURFACE_MOLECULARORBITAL:
      surfaceReader = new IsoMOReader(this);
      break;
    case Parameters.SURFACE_FUNCTIONXY:
      surfaceReader = new IsoFxyReader(this);
      break;
    case Parameters.SURFACE_FUNCTIONXYZ:
      surfaceReader = new IsoFxyzReader(this);
      break;
    case Parameters.SURFACE_MEP:
      if (params.state == Parameters.STATE_DATA_COLORED)
        surfaceReader = new AtomPropertyMapper(this, "Mep");
      else
        surfaceReader = new IsoMepReader(this);
      break;
    case Parameters.SURFACE_MLP:
      if (params.state == Parameters.STATE_DATA_COLORED)
        surfaceReader = new AtomPropertyMapper(this, "Mlp");
      else
        surfaceReader = new IsoMlpReader(this);
      break;
    }
    return true;
  }
  
  private void generateSurface() {       
    if (++params.state != Parameters.STATE_DATA_READ)
      return;
    setReader();    
    boolean haveMeshDataServer = (meshDataServer != null);
    if (params.colorBySign)
      params.isBicolorMap = true;
    if (surfaceReader == null) {
      Logger.error("surfaceReader is null for " + params.dataType);
      return;
    }
    if (!surfaceReader.createIsosurface(false)) {
      Logger.error("Could not create isosurface");
      params.cutoff = Float.NaN;
      surfaceReader.closeReader();
      return;
    }
    
    if (params.pocket != null && haveMeshDataServer)
      surfaceReader.selectPocket(!params.pocket.booleanValue());

    if (params.minSet > 0)
      surfaceReader.excludeMinimumSet();

    if (params.maxSet > 0)
      surfaceReader.excludeMaximumSet();

    if (params.slabbingObject != null)
      surfaceReader.slabIsosurface(params.slabbingObject, false);

    if (params.cappingObject != null)
      surfaceReader.slabIsosurface(params.cappingObject, true);

    if (haveMeshDataServer)
      meshDataServer.notifySurfaceGenerationCompleted();
    
    if (jvxlData.jvxlDataIs2dContour) {
      surfaceReader.colorIsosurface();
      params.state = Parameters.STATE_DATA_COLORED;
    }
    if (params.colorBySign || params.isBicolorMap) {
      params.state = Parameters.STATE_DATA_COLORED;
      surfaceReader.applyColorScale();
    }
    surfaceReader.jvxlUpdateInfo();
    setMarchingSquares(surfaceReader.marchingSquares);
    surfaceReader.discardTempData(false);
    params.mappedDataMin = Float.MAX_VALUE;
    surfaceReader.closeReader();
    if (surfaceReader.hasColorData || params.colorDensity) {
      params.state = Parameters.STATE_DATA_COLORED;
      colorIsosurface();
    } else {
      surfaceReader = null; // resets voxel reader for mapping
    }
  }

  private void mapSurface() {
    if (params.state == Parameters.STATE_INITIALIZED && params.thePlane != null)
      params.state++;
    if (++params.state != Parameters.STATE_DATA_COLORED)
      return;
    if (!setReader())
      return;    
    //if (params.dataType == Parameters.SURFACE_FUNCTIONXY)
      //params.thePlane = new Point4f(0, 0, 1, 0);
    if (params.isPeriodic)
      volumeData.isPeriodic = true;
    if (params.thePlane != null) {
      boolean isSquared = params.isSquared;
      params.isSquared = false;
      params.cutoff = 0;
      surfaceReader.setMappingPlane(params.thePlane);
      surfaceReader.createIsosurface(!params.isPeriodic);//but don't read volume data yet
      if (params.slabbingObject != null)
        surfaceReader.slabIsosurface(params.slabbingObject, false);
      if (params.cappingObject != null)
        surfaceReader.slabIsosurface(params.cappingObject, true);
      if (meshDataServer != null)
        meshDataServer.notifySurfaceGenerationCompleted();
      if (params.dataType == Parameters.SURFACE_NOMAP) {
        // just a simple plane
        surfaceReader.discardTempData(true);
        return;
      }
      params.isSquared = isSquared;
      params.mappedDataMin = Float.MAX_VALUE;
      surfaceReader.readVolumeData(true);
    } else if (!params.colorBySets) {
      surfaceReader.readAndSetVolumeParameters();
      params.mappedDataMin = Float.MAX_VALUE;
      surfaceReader.readVolumeData(true);
    }
    colorIsosurface();
    surfaceReader.closeReader();
  }

  void colorIsosurface() {
    surfaceReader.colorIsosurface();
    surfaceReader.jvxlUpdateInfo();
    surfaceReader.updateTriangles();
    surfaceReader.discardTempData(true);
    if (meshDataServer != null)
      meshDataServer.notifySurfaceMappingCompleted();
  }
  
  public Object getProperty(String property, int index) {
    if (property == "jvxlFileData")
      return JvxlCoder.jvxlGetFile(jvxlData, null, params.title, "", true,
          index, null, null); // for Jvxl.java
    if (property == "jvxlFileInfo")
      return JvxlCoder.jvxlGetInfo(jvxlData); // for Jvxl.java
    return null;
  }

  @SuppressWarnings("unchecked")
  private SurfaceReader setFileData(Object value) {
    String fileType = this.fileType;
    this.fileType = null;
    if (value instanceof VolumeData) {
      volumeData = (VolumeData) value;
      return new VolumeDataReader(this);
    }
    if (value instanceof Map) {
      volumeData = (VolumeData) ((Map<String, Object>) value).get("volumeData");
      return new VolumeDataReader(this);
    }
    String data = null;
    if (value instanceof String) {
      data = (String) value;
      // this will be OK, because any string will be a simple string, 
      // not a binary file.
      value = new BufferedReader(new StringReader((String) value));
    }
    BufferedReader br = (BufferedReader) value;
    if (fileType == null)
      fileType = SurfaceFileTyper.determineSurfaceFileType(br);
    if (fileType != null && fileType.startsWith("UPPSALA")) {
      //"http://eds.bmc.uu.se/cgi-bin/eds/gen_maps_zip.pl?POST?pdbCode=1blu&mapformat=ccp4&maptype=2fofc&page=generate"
      // -- ah, but this does not work, because it is asynchronous!
      // -- first a message is sent back that suggests you might have to wait, 
      //    then a message that says, "Here is your map" is sent.
      
      String fname = params.fileName;
      fname = fname.substring(0, fname.indexOf("/", 10));
      fname += Parser.getNextQuotedString(fileType,
          fileType.indexOf("A HREF") + 1);
      params.fileName = fname;
      value = atomDataServer.getBufferedInputStream(fname);
      if (value == null) {
        Logger.error("Isosurface: could not open file " + fname);
        return null;
      }
      br = new BufferedReader(
          new InputStreamReader((BufferedInputStream) value));
      fileType = SurfaceFileTyper.determineSurfaceFileType(br);
    }
    if (fileType == null)
      fileType = "UNKNOWN";
    Logger.info("data file type was determined to be " + fileType);
    if (fileType.equals("Jvxl+"))
      return new JvxlReader(this, br);
    if (fileType.equals("Jvxl"))
      return new JvxlReader(this, br);
    if (fileType.equals("JvxlXML"))
      return new JvxlXmlReader(this, br);
    if (fileType.equals("Apbs"))
      return new ApbsReader(this, br);
    if (fileType.equals("Cube"))
      return new CubeReader(this, br);
    if (fileType.equals("Jaguar"))
      return new JaguarReader(this, br);
    if (fileType.equals("Xplor"))
      return new XplorReader(this, br);
    if (fileType.equals("PltFormatted"))
      return new PltFormattedReader(this, br);
    if (fileType.equals("MRC")) {
      try {
        br.close();
      } catch (IOException e) {
        // ignore
      }
      br = null;
      return new MrcBinaryReader(this, params.fileName);
    }
    if (fileType.equals("DSN6")) {
      try {
        br.close();
      } catch (IOException e) {
        // ignore
      }
      br = null;
      return new Dsn6BinaryReader(this, params.fileName, data);
    }
    if (fileType.equals("Efvet"))
      return new EfvetReader(this, br);
    if (fileType.equals("Pmesh"))
      return new PmeshReader(this, params.fileName, br);
    if (fileType.equals("Obj"))
      return new ObjReader(this, br);
    if (fileType.equals("Msms"))
      return new MsmsReader(this, params.fileName, br);
    return null;
  }

  void initializeIsosurface() {
    params.initialize();
    colorPtr = 0;
    surfaceReader = null;
    marchingSquares = null;
    initState();
  }

  public void initState() {
    params.state = Parameters.STATE_INITIALIZED;
    params.dataType = params.surfaceType = Parameters.SURFACE_NONE;
  }

  public String setLcao() {
    params.colorPos = params.colorPosLCAO;
    params.colorNeg = params.colorNegLCAO;
    return params.lcaoType;

  }

  private void getFunctionZfromXY() {
    Point3f origin = (Point3f) params.functionXYinfo.get(1);
    int[] counts = new int[3];
    int[] nearest = new int[3];
    Vector3f[] vectors = new Vector3f[3];
    for (int i = 0; i < 3; i++) {
      Point4f info = (Point4f) params.functionXYinfo.get(i + 2);
      counts[i] = Math.abs((int) info.x);
      vectors[i] = new Vector3f(info.y, info.z, info.w);
    }
    int nx = counts[0];
    int ny = counts[1];
    Point3f pt = new Point3f();
    Point3f pta = new Point3f();
    Point3f ptb = new Point3f();
    Point3f ptc = new Point3f();

    float[][] data = (float[][]) params.functionXYinfo.get(5);
    float[][] data2 = new float[nx][ny];
    float[] d;
    //int n = 0;
    //for (int i = 0; i < data.length; i++) 
      //System.out.println("draw pt"+(++n)+" {" + data[i][0] + " " + data[i][1] + " " + data[i][2] + "} color yellow");
    for (int i = 0; i < nx; i++)
      for (int j = 0; j < ny; j++) {
        pt.scaleAdd(i, vectors[0], origin);
        pt.scaleAdd(j, vectors[1], pt);
        float dist = findNearestThreePoints(pt.x, pt.y, data, nearest);
        pta.set((d = data[nearest[0]])[0], d[1], d[2]);
        if (dist < 0.00001) {
          pt.z = d[2];
        } else {
          ptb.set((d = data[nearest[1]])[0], d[1], d[2]);
          ptc.set((d = data[nearest[2]])[0], d[1], d[2]);
          pt.z = distanceVerticalToPlane(pt.x, pt.y, pta, ptb, ptc);
        }
        data2[i][j] = pt.z;
        //System.out.println("draw pt"+(++n)+" " + Escape.escape(pt) + " color red");
      }
    params.functionXYinfo.set(5, data2);
  }

  final Vector3f vAC = new Vector3f();
  final Vector3f vAB = new Vector3f();
  final Vector3f vNorm = new Vector3f();
  final Point3f ptRef = new Point3f(0, 0, 1e15f);
  
  private float distanceVerticalToPlane(float x, float y, Point3f pta,
                                              Point3f ptb, Point3f ptc) {
    // ax + by + cz + d = 0

    float d = Measure.getDirectedNormalThroughPoints(pta, ptb, ptc, ptRef, vNorm, vAB, vAC);
    return (vNorm.x * x + vNorm.y * y + d) / -vNorm.z;
  }
  
  private static float findNearestThreePoints(float x, float y, float[][] xyz, int[] result) {
    //result should be int[3];
    float d, dist1, dist2, dist3;
    int i1, i2, i3;
    i1 = i2 = i3 = -1;
    dist1 = dist2 = dist3 = Float.MAX_VALUE;
    for (int i = xyz.length; --i >= 0;) {
      d = (d = xyz[i][0] - x) * d + (d = xyz[i][1] - y) * d;
      if (d < dist1) {
        dist3 = dist2;
        dist2 = dist1;
        dist1 = d;
        i3 = i2;
        i2 = i1;
        i1 = i;
      } else if (d < dist2) {
        dist3 = dist2;
        dist2 = d;
        i3 = i2;
        i2 = i;
      } else if (d < dist3) {
        dist3 = d;
        i3 = i;
      }
    }
    //System.out.println("findnearest " + dist1);
    result[0] = i1;
    result[1] = i2;
    result[2] = i3;
    return dist1;
  }

  public void addRequiredFile(String fileName) {
    if (meshDataServer == null)
      return;
    meshDataServer.addRequiredFile(fileName);    
  }

  void log(String msg) {
    if (atomDataServer == null)
      System.out.println(msg);
    else
      atomDataServer.log(msg);
  }

  void setOutputStream(BinaryDocument binaryDoc, OutputStream os) {
    if (meshDataServer == null)
      return;
     meshDataServer.setOutputStream(binaryDoc, os);    
  }


}
