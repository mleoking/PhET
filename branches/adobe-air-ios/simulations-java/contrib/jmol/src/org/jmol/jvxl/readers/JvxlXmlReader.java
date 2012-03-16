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
 *  Lesser General License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.jvxl.readers;


import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.jvxl.data.JvxlData;
import org.jmol.jvxl.data.MeshData;
import org.jmol.shapesurface.IsosurfaceMesh;
import org.jmol.util.ArrayUtil;
import org.jmol.util.ColorEncoder;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.XmlReader;

public class JvxlXmlReader extends VolumeFileReader {

  protected String JVXL_VERSION = "2.1";
  
  protected int surfaceDataCount;
  protected int edgeDataCount;
  protected int colorDataCount;
  private int excludedTriangleCount;
  private int excludedVertexCount;
  private int invalidatedVertexCount;  
  protected boolean haveContourData;

  private XmlReader xr;
  
  protected boolean isXmlFile= true;
  JvxlXmlReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    jvxlData.wasJvxl = isJvxl = true;
    isXLowToHigh = false;
    xr = new XmlReader(br);
  }

  protected boolean thisInside;

  /////////////reading the format///////////

  @Override
  protected boolean readVolumeData(boolean isMapData) {
    if (!super.readVolumeData(isMapData))
      return false;
    strFractionTemp = jvxlEdgeDataRead;
    fractionPtr = 0;
    return true;
  }
  @Override
  protected boolean gotoAndReadVoxelData(boolean isMapData) {
    initializeVolumetricData();
    if (nPointsX < 0 || nPointsY < 0 || nPointsZ < 0) 
      return true;
    try {
      gotoData(params.fileIndex - 1, nPointsX * nPointsY * nPointsZ);
      if (vertexDataOnly)
        return true;
      readSurfaceData(isMapData);
      if (edgeDataCount > 0)
        jvxlEdgeDataRead = jvxlReadData("edge", edgeDataCount);
      params.bsExcluded = jvxlData.jvxlExcluded = new BitSet[4];
      if (colorDataCount > 0)
        jvxlColorDataRead = jvxlReadData("color", colorDataCount);
      if (excludedVertexCount > 0) {
        jvxlData.jvxlExcluded[0]  
            = JvxlCoder.jvxlDecodeBitSet(
                xr.getXmlData("jvxlExcludedVertexData", null, false, false));
        if (xr.isNext("jvxlExcludedPlaneData"))
          jvxlData.jvxlExcluded[2]  
                                = JvxlCoder.jvxlDecodeBitSet(
                                    xr.getXmlData("jvxlExcludedPlaneData", null, false, false));          
      }
      if (excludedTriangleCount > 0)
        jvxlData.jvxlExcluded[3]  
            = JvxlCoder.jvxlDecodeBitSet(
                xr.getXmlData("jvxlExcludedTriangleData", null, false, false));
      if (invalidatedVertexCount > 0)
        jvxlData.jvxlExcluded[1]  
                            = JvxlCoder.jvxlDecodeBitSet(
                                xr.getXmlData("jvxlInvalidatedVertexData", null, false, false));
      if (haveContourData)
        jvxlDecodeContourData(jvxlData, xr.getXmlData("jvxlContourData", null, false, false));
    } catch (Exception e) {
      Logger.error(e.toString());
      return false;
    }
    return true;
  }
  
  String tempDataXml; 
  
  @Override
  protected void readParameters() throws Exception {
    String s = xr.getXmlData("jvxlFileTitle", null, false, false);
    jvxlFileHeaderBuffer = new StringBuffer(s);
    xr.toTag("jvxlVolumeData");
    String data = tempDataXml = xr.getXmlData("jvxlVolumeData", null, true, false);
    volumetricOrigin.set(xr.getXmlPoint(data, "origin"));
   isAngstroms = true;
   readVector(0);
   readVector(1);
   readVector(2);
   line = xr.toTag("jvxlSurfaceSet");
   nSurfaces = parseInt(XmlReader.getXmlAttrib(line, "count"));
   Logger.info("jvxl file surfaces: " + nSurfaces);
   Logger.info("using default edge fraction base and range");
   Logger.info("using default color fraction base and range");
   cJvxlEdgeNaN = (char) (edgeFractionBase + edgeFractionRange);
  }

  protected void readVector(int voxelVectorIndex) throws Exception {
    String data = xr.getXmlData("jvxlVolumeVector", tempDataXml, true, true);
    tempDataXml = tempDataXml.substring(tempDataXml.indexOf(data) + data.length());
    int n = parseInt(XmlReader.getXmlAttrib(data, "count"));
    if (n == Integer.MIN_VALUE)
      vertexDataOnly = true;
    voxelCounts[voxelVectorIndex] = (n < 0 ? 0 : n);
    volumetricVectors[voxelVectorIndex].set(xr.getXmlPoint(data, "vector"));
    if (isAnisotropic)
      setVectorAnisotropy(volumetricVectors[voxelVectorIndex]);
  }

  @Override
  protected void gotoData(int n, int nPoints) throws Exception {
    if (n > 0)
      Logger.info("skipping " + n + " data sets, " + nPoints + " points each");
    vertexDataOnly = jvxlData.vertexDataOnly = (nPoints == 0);
    for (int i = 0; i < n; i++) {
      jvxlSkipData(nPoints, true);
    }
    xr.toTag("jvxlSurface");
    jvxlReadSurfaceInfo();
  }

  protected void jvxlSkipData(@SuppressWarnings("unused") int nPoints,
                              @SuppressWarnings("unused") boolean doSkipColorData)
      throws Exception {
    readLine();
    xr.skipTag("jvxlSurface");
  }

  protected void jvxlReadSurfaceInfo() throws Exception {
    String s;
    String data = xr.getXmlData("jvxlSurfaceInfo", null, true, true);
    isXLowToHigh = XmlReader.getXmlAttrib(data, "isXLowToHigh").equals("true");
    jvxlCutoff = parseFloat(XmlReader.getXmlAttrib(data, "cutoff"));
    if (!Float.isNaN(jvxlCutoff))
      Logger.info("JVXL read: cutoff " + jvxlCutoff);
    int nContourData = parseInt(XmlReader.getXmlAttrib(data, "nContourData"));
    haveContourData = (nContourData > 0);
    params.isContoured = XmlReader.getXmlAttrib(data, "contoured").equals("true");
    if (params.isContoured) {
      int nContoursRead = parseInt(XmlReader.getXmlAttrib(data, "nContours"));
      if (nContoursRead <= 0) {
        nContoursRead = 0;
      } else {
        s = XmlReader.getXmlAttrib(data, "contourValues");
        if (s.length() > 0) {
          jvxlData.contourValues = params.contoursDiscrete = parseFloatArray(s);
          Logger.info("JVXL read: contourValues " + Escape.escapeArray(jvxlData.contourValues));            
        }
        s = XmlReader.getXmlAttrib(data, "contourColors");
        if (s.length() > 0) {
          jvxlData.contourColixes = params.contourColixes = Graphics3D.getColixArray(s);
          jvxlData.contourColors = Graphics3D.getHexCodes(jvxlData.contourColixes);
          Logger.info("JVXL read: contourColixes " +
              Graphics3D.getHexCodes(jvxlData.contourColixes));        }
        params.contourFromZero = XmlReader.getXmlAttrib(data, "contourFromZero").equals("true");
      }
      params.nContours = (haveContourData ? nContourData : nContoursRead);
      //TODO ? params.contourFromZero = false; // MEP data to complete the plane
    }
    params.isBicolorMap = XmlReader.getXmlAttrib(data, "bicolorMap").equals("true");
    if (params.isBicolorMap || params.colorBySign)
      jvxlCutoff = 0;
    jvxlDataIsColorMapped = params.isBicolorMap || XmlReader.getXmlAttrib(data, "colorMapped").equals("true");
    //next is for information only -- will be superceded by "encoding" attribute of jvxlColorData
    jvxlData.isJvxlPrecisionColor = XmlReader.getXmlAttrib(data, "precisionColor").equals("true");
    jvxlData.colorDensity = params.colorDensity = XmlReader.getXmlAttrib(data, "colorDensity").equals("true");
    s = XmlReader.getXmlAttrib(data, "plane");
    if (s.indexOf("{") >= 0) {
      try {
        params.thePlane = (Point4f) Escape.unescapePoint(s);
        Logger.info("JVXL read: plane " + params.thePlane);
        if (params.scale3d == 0)
          params.scale3d = parseFloat(XmlReader.getXmlAttrib(data, "scale3d"));
        if (Float.isNaN(params.scale3d))
          params.scale3d = 0;
      } catch (Exception e) {
        Logger
            .error("Error reading 4 floats for PLANE definition -- setting to 0 0 1 0  (z=0)");
        params.thePlane = new Point4f(0, 0, 1, 0);
      }
      surfaceDataCount = 0;
      edgeDataCount = 0;
    } else {
      params.thePlane = null;
      surfaceDataCount = parseInt(XmlReader.getXmlAttrib(data, "nSurfaceInts"));
      edgeDataCount = parseInt(XmlReader.getXmlAttrib(data, "nBytesUncompressedEdgeData"));
    }
    excludedVertexCount = parseInt(XmlReader.getXmlAttrib(data, "nExcludedVertexes"));
    excludedTriangleCount = parseInt(XmlReader.getXmlAttrib(data, "nExcludedTriangles"));
    invalidatedVertexCount = parseInt(XmlReader.getXmlAttrib(data, "nInvalidatedVertexes"));
    colorDataCount = Math.max(0, parseInt(XmlReader.getXmlAttrib(data, "nBytesUncompressedColorData")));
    jvxlDataIs2dContour = (params.thePlane != null && jvxlDataIsColorMapped);
    if (jvxlDataIs2dContour)
      params.isContoured = true;
    
    if (params.colorBySign)
      params.isBicolorMap = true;
    boolean insideOut = XmlReader.getXmlAttrib(data, "insideOut").equals("true");
    float dataMin = Float.NaN;
    float dataMax = Float.NaN;
    float red = Float.NaN;
    float blue = Float.NaN;
    if (jvxlDataIsColorMapped) {
      dataMin = parseFloat(XmlReader.getXmlAttrib(data, "dataMinimum"));
      dataMax = parseFloat(XmlReader.getXmlAttrib(data, "dataMaximum"));
      red = parseFloat(XmlReader.getXmlAttrib(data, "valueMappedToRed"));
      blue = parseFloat(XmlReader.getXmlAttrib(data, "valueMappedToBlue"));
      if (Float.isNaN(dataMin)) {
        dataMin = red = -1f;
        dataMax = blue = 1f;
      }
    }
    jvxlSetColorRanges(dataMin, dataMax, red, blue, insideOut);
  }

  protected void jvxlSetColorRanges(float dataMin, float dataMax, float red,
                                  float blue, boolean insideOut) {
    if (jvxlDataIsColorMapped) {
    if (!Float.isNaN(dataMin) && !Float.isNaN(dataMax)) {
      if (dataMax == 0 && dataMin == 0) {
        //set standard -1/1; bit of a hack
        dataMin = -1;
        dataMax = 1;
      }
      params.mappedDataMin = dataMin;
      params.mappedDataMax = dataMax;
      Logger.info("JVXL read: data_min/max " + params.mappedDataMin + "/"
          + params.mappedDataMax);
    }
    if (!params.rangeDefined)
      if (!Float.isNaN(red) && !Float.isNaN(blue)) {
        if (red == 0 && blue == 0) {
          //set standard -1/1; bit of a hack
          red = -1;
          blue = 1;
        }
        params.valueMappedToRed = Math.min(red, blue);
        params.valueMappedToBlue = Math.max(red, blue);
        params.isColorReversed = (red > blue);
        params.rangeDefined = true;
      } else {
        params.valueMappedToRed = 0f;
        params.valueMappedToBlue = 1f;
        params.rangeDefined = true;
      }
    Logger.info("JVXL read: color red/blue: " + params.valueMappedToRed + "/"
        + params.valueMappedToBlue);
    }
    jvxlData.valueMappedToRed = params.valueMappedToRed;
    jvxlData.valueMappedToBlue = params.valueMappedToBlue;
    jvxlData.mappedDataMin = params.mappedDataMin;
    jvxlData.mappedDataMax = params.mappedDataMax;
    jvxlData.isColorReversed = params.isColorReversed;
    jvxlData.insideOut = insideOut;
    if (params.insideOut)
      jvxlData.insideOut = !jvxlData.insideOut;
    params.insideOut = jvxlData.insideOut;
  }

  @Override
  protected void readSurfaceData(boolean isMapDataIgnored) throws Exception {
    thisInside = !params.isContoured;
    if (readSurfaceData())
      return;
    tempDataXml = xr.getXmlData("jvxlEdgeData", null, true, false);
    bsVoxelBitSet = JvxlCoder.jvxlDecodeBitSet(xr.getXmlData("jvxlEdgeData",
        tempDataXml, false, false));
    // if (thisInside)
    // bsVoxelBitSet = BitSetUtil.copyInvert(bsVoxelBitSet,
    // bsVoxelBitSet.size());
    readVolumeFileSurfaceData();
  }

  protected boolean readSurfaceData() throws Exception {
    if (vertexDataOnly) {
      getEncodedVertexData();
      return true;
    } 
    if (params.thePlane != null) {
      volumeData.setDataDistanceToPlane(params.thePlane);
      setVolumeData(volumeData);
      params.cutoff = 0f;
      jvxlData.setSurfaceInfo(params.thePlane, 0, "");
      jvxlData.scale3d = params.scale3d;
      return true;
    }
    return false;
  }
  
  protected void readVolumeFileSurfaceData() throws Exception {
    super.readSurfaceData(false);
  }

  protected String jvxlReadData(String type,
                                @SuppressWarnings("unused") int nPoints) {
    String str;
    try {
      if (type.equals("edge")) {
        str = JvxlCoder.jvxlUncompressString(XmlReader.getXmlAttrib(tempDataXml, "data"));
      } else {
        String data = xr.getXmlData("jvxlColorData", null, true, false);
        jvxlData.isJvxlPrecisionColor = XmlReader.getXmlAttrib(data, "encoding").endsWith("2");
        str = JvxlCoder.jvxlUncompressString(XmlReader.getXmlAttrib(data, "data"));
      }
    } catch (Exception e) {
      Logger.error("Error reading " + type + " data " + e);
      throw new NullPointerException();
    }
    return str;
  }
  
  protected BitSet bsVoxelBitSet;

  @Override
  protected BitSet getVoxelBitSet(int nPoints) throws Exception {
    if (bsVoxelBitSet != null)
      return bsVoxelBitSet;
    BitSet bs = new BitSet();
    int bsVoxelPtr = 0;
    if (surfaceDataCount <= 0)
      return bs; //unnecessary -- probably a plane or color density
    int nThisValue = 0;
    while (bsVoxelPtr < nPoints) {
      nThisValue = parseInt();
      if (nThisValue == Integer.MIN_VALUE) {
        readLine();
        // note -- does not allow for empty lines;
        // must be a continuous block of numbers.
        if (line == null || (nThisValue = parseInt(line)) == Integer.MIN_VALUE) {
          if (!endOfData)
            Logger.error("end of file in JvxlReader?" + " line=" + line);
          endOfData = true;
          nThisValue = 10000;
          //throw new NullPointerException();
        }
      } 
      thisInside = !thisInside;
      ++jvxlNSurfaceInts;
      if (thisInside)
        bs.set(bsVoxelPtr, bsVoxelPtr + nThisValue);
      bsVoxelPtr += nThisValue;
    }
    return bs;
  }

  @Override
  protected float getSurfacePointAndFraction(float cutoff,
                                             boolean isCutoffAbsolute,
                                             float valueA, float valueB,
                                             Point3f pointA,
                                             Vector3f edgeVector,
                                             int x, int y, int z, int vA, int vB, float[] fReturn, Point3f ptReturn) {
    if (edgeDataCount <= 0)
      return super.getSurfacePointAndFraction(cutoff, isCutoffAbsolute, valueA,
          valueB, pointA, edgeVector, x, y, z, vA, vB, fReturn, ptReturn);
    ptReturn.scaleAdd(fReturn[0] = jvxlGetNextFraction(edgeFractionBase,
        edgeFractionRange, 0.5f), edgeVector, pointA);
    if (Float.isNaN(valueMin))
      setValueMinMax();      
    return (valueCount == 0 || includeValueNaN && Float.isNaN(fReturn[0]) 
        ? fReturn[0] : getNextValue());
  }

  private float getNextValue() {
    float fraction = Float.NaN;
    while (colorPtr < valueCount && Float.isNaN(fraction)) {
      if (jvxlData.isJvxlPrecisionColor) {
        // this COULD be an option for mapped surfaces; 
        // necessary for planes; used for vertex/triangle 2.0 style
        // precision is used for FULL-data range encoding, allowing full
        // treatment of JVXL files as though they were CUBE files.
        // the two parts of the "double-character-precision" value
        // are in separate lines, separated by n characters.
        fraction = JvxlCoder.jvxlFractionFromCharacter2(jvxlColorDataRead
            .charAt(colorPtr), jvxlColorDataRead.charAt((colorPtr++)
            + valueCount), colorFractionBase, colorFractionRange);
      } else {
        // my original encoding scheme
        // low precision only allows for mapping relative to the defined color range
        fraction = JvxlCoder.jvxlFractionFromCharacter(jvxlColorDataRead
            .charAt(colorPtr++), colorFractionBase, colorFractionRange, 0.5f);
      }
      break;
    }
    return valueMin + fraction * valueRange;
  }
  
  private void setValueMinMax() {
    valueCount = jvxlColorDataRead.length();
    if (jvxlData.isJvxlPrecisionColor)
      valueCount /= 2;
    includeValueNaN = (valueCount != jvxlEdgeDataRead.length());
    valueMin = (!jvxlData.isJvxlPrecisionColor ? params.valueMappedToRed
        : params.mappedDataMin == Float.MAX_VALUE ? defaultMappedDataMin
            : params.mappedDataMin);
    valueRange = (!jvxlData.isJvxlPrecisionColor ? params.valueMappedToBlue
        : params.mappedDataMin == Float.MAX_VALUE ? defaultMappedDataMax
            : params.mappedDataMax)
        - valueMin;
    haveReadColorData = true;
  }

  private boolean includeValueNaN = true;
  private int valueCount;
  private float valueMin = Float.NaN;
  private float valueRange = Float.NaN;
  private int fractionPtr;
  private int colorPtr;
  private String strFractionTemp = "";

  private float jvxlGetNextFraction(int base, int range, float fracOffset) {
    if (fractionPtr >= strFractionTemp.length()) {
      if (!endOfData)
        Logger.error("end of file reading compressed fraction data");
      endOfData = true;
      strFractionTemp = "" + (char) base;
      fractionPtr = 0;
    }
    return JvxlCoder.jvxlFractionFromCharacter(strFractionTemp.charAt(fractionPtr++),
        base, range, fracOffset);
  }

  boolean haveReadColorData;
  
  @Override
  protected String readColorData() {
    // overloads SurfaceReader
    // standard jvxl file read for color 

    int vertexCount = jvxlData.vertexCount = meshData.vertexCount;
    // the problem is that the new way to read data in Marching Cubes
    // is to ignore all points that are NaN. But then we also have to
    // remove those points from the color string. 

    short[] colixes = meshData.vertexColixes;
    float[] vertexValues = meshData.vertexValues;
    /*
     * haveReadColorData?
     = (isJvxl ? jvxlColorDataRead : "");
    if (isJvxl && strValueTemp.length() == 0) {
      Logger
          .error("You cannot use JVXL data to map onto OTHER data, because it only contains the data for one surface. Use ISOSURFACE \"file.jvxl\" not ISOSURFACE .... MAP \"file.jvxl\".");
      return "";
    }
    */
    if (params.colorEncoder == null)
      params.colorEncoder = new ColorEncoder(null);
    params.colorEncoder.setColorScheme(null, false);
    params.colorEncoder.setRange(params.valueMappedToRed,
        params.valueMappedToBlue, params.isColorReversed);
    Logger.info("JVXL reading color data mapped min/max: "
        + params.mappedDataMin + "/" + params.mappedDataMax + " for "
        + vertexCount + " vertices." + " using encoding keys "
        + colorFractionBase + " " + colorFractionRange);
    Logger.info("mapping red-->blue for " + params.valueMappedToRed + " to "
        + params.valueMappedToBlue + " colorPrecision:"
        + jvxlData.isJvxlPrecisionColor);
    boolean getValues = (Float.isNaN(valueMin));
    if (getValues)
      setValueMinMax();
    float contourPlaneMinimumValue = Float.MAX_VALUE;
    float contourPlaneMaximumValue = -Float.MAX_VALUE;
    if (colixes == null || colixes.length < vertexCount)
      meshData.vertexColixes = colixes = new short[vertexCount];
    //hasColorData = true;
    short colixNeg = 0, colixPos = 0;
    if (params.colorBySign) {
      colixPos = Graphics3D.getColix(params.isColorReversed ? params.colorNeg
          : params.colorPos);
      colixNeg = Graphics3D.getColix(params.isColorReversed ? params.colorPos
          : params.colorNeg);
    }
    int vertexIncrement = meshData.vertexIncrement;
    // here's the problem: we are assuming here that vertexCount == nPointsRead
    boolean needContourMinMax = (params.mappedDataMin == Float.MAX_VALUE);
    for (int i = 0; i < vertexCount; i += vertexIncrement) {
      float value;
      if(getValues)
        value = vertexValues[i] = getNextValue();
      else
        value = vertexValues[i];
      if (needContourMinMax) {
        if (value < contourPlaneMinimumValue)
          contourPlaneMinimumValue = value;
        if (value > contourPlaneMaximumValue)
          contourPlaneMaximumValue = value;
      }
    }
    if (needContourMinMax) {
      params.mappedDataMin = contourPlaneMinimumValue;
      params.mappedDataMax = contourPlaneMaximumValue;
    }

    for (int i = 0; i < vertexCount; i += vertexIncrement) {
      float value = vertexValues[i];
      //note: these are just default colorings
      //orbital color had a bug through 11.2.6/11.3.6
      if (marchingSquares != null && params.isContoured) {
        marchingSquares.setContourData(i, value);
      } else if (params.colorBySign) {
        colixes[i] = ((params.isColorReversed ? value > 0 : value <= 0) ? colixNeg
            : colixPos);
      } else {
        colixes[i] = params.colorEncoder.getColorIndex(value);
      }
    }
    return jvxlColorDataRead + "\n";
  }

  /**
   * retrieve Jvxl 2.0 format vertex/triangle/color data found
   * within <jvxlSurfaceData> element 
   * 
   * @throws Exception
   */
  protected void getEncodedVertexData() throws Exception {
    String data = xr.getXmlData("jvxlSurfaceData", null, true, false);
    String tData = xr.getXmlData("jvxlTriangleData", data, true, false);
    jvxlDecodeVertexData(xr.getXmlData("jvxlVertexData", data, true, false), false);
    String polygonColorData = xr.getXmlData("jvxlPolygonColorData", data, false, false);
    jvxlDecodeTriangleData(tData, polygonColorData);
    Logger.info("Checking for vertex values");
    data = xr.getXmlData("jvxlColorData", data, true, false);
    jvxlData.isJvxlPrecisionColor = XmlReader.getXmlAttrib(data, "encoding").endsWith("2");
    jvxlColorDataRead = JvxlCoder.jvxlUncompressString(XmlReader.getXmlAttrib(data, "data"));
    if (jvxlColorDataRead.length() == 0)
      jvxlColorDataRead = xr.getXmlData("jvxlColorData", data, false, false);
    jvxlDataIsColorMapped = (jvxlColorDataRead.length() > 0);
    if (haveContourData)
      jvxlDecodeContourData(jvxlData, xr.getXmlData("jvxlContourData", null, false, false));
  }

  /**
   * decode vertex data found within <jvxlVertexData> element
   * as created by jvxlEncodeVertexData (see above)
   * 
   * @param data      tag and contents 
   * @param asArray   or just addVertexCopy    
   * @return          Point3f[] if desired 
   * @throws Exception 
   *    
   */
  public Point3f[] jvxlDecodeVertexData(String data, boolean asArray) throws Exception {
    int vertexCount = parseInt(XmlReader.getXmlAttrib(data, "count"));
    if (!asArray)
      Logger.info("Reading " + vertexCount + " vertices");
    Point3f min = xr.getXmlPoint(data, "min");
    Point3f range = xr.getXmlPoint(data, "max");
    range.sub(min);
    int colorFractionBase = jvxlData.colorFractionBase;
    int colorFractionRange = jvxlData.colorFractionRange;
    int ptCount = vertexCount * 3;
    Point3f[] vertices = (asArray ? new Point3f[vertexCount] : null);
    Point3f p = (asArray ? null : new Point3f());
    float fraction;
    String s = JvxlCoder.jvxlUncompressString(XmlReader.getXmlAttrib(data, "data"));
    if (s.length() == 0)
      s = xr.getXmlData("jvxlVertexData", data, false, false); 
    for (int i = 0, pt = -1; i < vertexCount; i++) {
      if (asArray)
        p = vertices[i] = new Point3f();
      fraction = JvxlCoder.jvxlFractionFromCharacter2(s.charAt(++pt), s.charAt(pt
          + ptCount), colorFractionBase, colorFractionRange);
      p.x = min.x + fraction * range.x;
      fraction = JvxlCoder.jvxlFractionFromCharacter2(s.charAt(++pt), s.charAt(pt
          + ptCount), colorFractionBase, colorFractionRange);
      p.y = min.y + fraction * range.y;
      fraction = JvxlCoder.jvxlFractionFromCharacter2(s.charAt(++pt), s.charAt(pt
          + ptCount), colorFractionBase, colorFractionRange);
      p.z = min.z + fraction * range.z;
      if (!asArray)
        addVertexCopy(p, 0, i);
    }
    return vertices;
  }

  /**
   * decode triangle data found within <jvxlTriangleData> element as created
   * with jvxlEncodeTriangleData (see above)
   * 
   * @param data
   *          tag and contents
   * @param colorData
   * @return int[][] if desired
   * @throws Exception
   */
  int[][] jvxlDecodeTriangleData(String data, String colorData)
      throws Exception {
    int nColors = (colorData == null ? -1 : 0);
    int color = 0;
    int nData = parseInt(XmlReader.getXmlAttrib(data, "count"));
    Logger.info("Reading " + nData + " triangles");
    int[][] triangles = null;
    int[] triangle = new int[3];
    String s = JvxlCoder.jvxlUncompressString(XmlReader.getXmlAttrib(data, "data"));
    if (s.length() == 0)
      s = xr.getXmlData("jvxlTriangleData", data, false, false);
    int[] nextp = new int[1];
    int[] nextc = new int[1];
    int ilast = 0;
    int p = 0;
    int b0 = '\\';
    for (int i = 0, pt = -1; i < nData;) {
      char ch = s.charAt(++pt);
      int idiff;
      switch (ch) {
      case '!':
        idiff = 0;
        break;
      case '+':
      case '.':
      case ' ':
      case '\n':
      case '\r':
      case '\t':
      case ',':
        continue;
      case '-':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        nextp[0] = pt;
        idiff = Parser.parseInt(s, nextp);
        pt = nextp[0] - 1;
        break;
      default:
        idiff = ch - b0;
      }
      ilast += idiff;
      triangle[p] = ilast;
      if (++p % 3 == 0) {
        i++;
        p = 0;
        if (nColors >= 0) {
          if (nColors == 0) {
            nColors = Parser.parseInt(colorData, nextc);
            color = Parser.parseInt(colorData, nextc);
            if (color == Integer.MIN_VALUE)
              color = nColors = 0;
          }
          nColors--;
        }
        addTriangleCheck(triangle[0], triangle[1], triangle[2], 7, 0, false,
            color);
      }
    }
    return triangles;
  }

  protected void jvxlDecodeContourData(JvxlData jvxlData, String data) throws Exception {
    List<List<Object>> vs = new ArrayList<List<Object>>();
    StringBuffer values = new StringBuffer();
    StringBuffer colors = new StringBuffer();
    int pt = -1;
    jvxlData.vContours = null;
    if (data == null)
      return;
    while ((pt = data.indexOf("<jvxlContour", pt + 1)) >= 0) {
      List<Object> v = new ArrayList<Object>();
      String s = xr.getXmlData("jvxlContour", data.substring(pt), true, false);
      float value = parseFloat(XmlReader.getXmlAttrib(s, "value"));
      values.append(" ").append(value);
      short colix = Graphics3D.getColix(Graphics3D.getArgbFromString(XmlReader.getXmlAttrib(s,
          "color")));
      int color = Graphics3D.getArgb(colix);
      colors.append(" ").append(Escape.escapeColor(color));
      String fData = JvxlCoder.jvxlUncompressString(XmlReader.getXmlAttrib(s, "data"));
      BitSet bs = JvxlCoder.jvxlDecodeBitSet(xr.getXmlData("jvxlContour", s, false, false));
      int n = bs.length();
      IsosurfaceMesh.setContourVector(v, n, bs, value, colix, color, new StringBuffer(
          fData));
      vs.add(v);
    }
    int n = vs.size();
    if (n > 0) {
      jvxlData.vContours = ArrayUtil.createArrayOfArrayList(n);
      // 3D contour values and colors
      jvxlData.contourColixes = params.contourColixes = new short[n];
      jvxlData.contourValues = params.contoursDiscrete = new float[n];
      for (int i = 0; i < n; i++) {
        jvxlData.vContours[i] = vs.get(i);
        jvxlData.contourValues[i] = ((Float) jvxlData.vContours[i].get(2))
            .floatValue();
        jvxlData.contourColixes[i] = ((short[]) jvxlData.vContours[i].get(3))[0];
      }
      jvxlData.contourColors = Graphics3D.getHexCodes(jvxlData.contourColixes);
      Logger.info("JVXL read: " + n + " discrete contours");
      Logger.info("JVXL read: contour values: " + values);
      Logger.info("JVXL read: contour colors: " + colors);
    }
  }
  
  @Override
  protected void postProcessVertices() {
    BitSet bsInvalid = params.bsExcluded[1]; 
    if (bsInvalid != null) {
      if (meshDataServer != null)
        meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_VERTICES, null);
      meshData.invalidateVertices(bsInvalid); 
      if (meshDataServer != null) {
        meshDataServer.fillMeshData(meshData, MeshData.MODE_PUT_VERTICES, null);
        meshData = new MeshData();
      }
      updateTriangles();
    }
  }

}
