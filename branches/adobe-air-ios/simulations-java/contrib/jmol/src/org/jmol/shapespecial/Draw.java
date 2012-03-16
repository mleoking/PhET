/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-02-25 17:19:14 -0600 (Sat, 25 Feb 2006) $
 * $Revision: 4529 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
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

package org.jmol.shapespecial;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.MeshSurface;

import org.jmol.util.Measure;
import org.jmol.util.Point3fi;
import org.jmol.util.TextFormat;
import org.jmol.viewer.ActionManager;
import org.jmol.viewer.JmolConstants;
import org.jmol.g3d.Graphics3D;
import org.jmol.shape.Mesh;
import org.jmol.shape.MeshCollection;

public class Draw extends MeshCollection {

  // bob hanson hansonr@stolaf.edu 3/2006
  
  public Draw() {
    htObjects = new Hashtable<String, Mesh>();
  }

  DrawMesh[] dmeshes = new DrawMesh[4];
  DrawMesh thisMesh;
  
  @Override
  public void allocMesh(String thisID, Mesh m) {
    int index = meshCount++;
    meshes = dmeshes = (DrawMesh[]) ArrayUtil.ensureLength(dmeshes,
        meshCount * 2);
    currentMesh = thisMesh = dmeshes[index] = (m == null ? new DrawMesh(thisID,
        g3d, colix, index) : (DrawMesh) m);
    currentMesh.index = index;
    if (thisID != null && thisID != JmolConstants.PREVIOUS_MESH_ID
        && htObjects != null)
      htObjects.put(thisID.toUpperCase(), currentMesh);
  }

  void setPropertySuper(String propertyName, Object value, BitSet bs) {
    currentMesh = thisMesh;
    super.setProperty(propertyName, value, bs);
    thisMesh = (DrawMesh)currentMesh;  
  }
  
 @Override
public void initShape() {
    super.initShape();
    myType = "draw";
  }
  
  private Point3f[] ptList;
  private Vector3f offset = new Vector3f();
  private int nPoints;
  private int diameter;
  private float width;
  private float newScale;
  private float length;
  private boolean isCurve;
  private boolean isArc;
  private boolean isArrow;
  private boolean isLine;
  private boolean isVector;
  private boolean isCircle;
  private boolean isPerpendicular;
  private boolean isCylinder;
  private boolean isVertices;
  private boolean isPlane;
  private boolean isReversed;
  private boolean isRotated45;
  private boolean isCrossed;
  private boolean isValid;
  private boolean noHead;
  private boolean isBarb;
  private int indicatedModelIndex = -1;
  private int[] modelInfo;
  private boolean makePoints;
  private int nidentifiers;
  private int nbitsets;
  private Point4f plane;
  private BitSet bsAllModels;
  private List<Object> polygon;
  
  private List<Object[]> vData;
  private String intersectID;
  private Point3f[] boundBox;
  
  private List<Point3f[]> lineData;
  private final static int PT_COORD = 1;
  private final static int PT_IDENTIFIER = 2;
  private final static int PT_BITSET = 3;
  private final static int PT_MODEL_INDEX = 4;
  private final static int PT_MODEL_BASED_POINTS = 5;

  @SuppressWarnings("unchecked")
  @Override
  public void setProperty(String propertyName, Object value, BitSet bs) {

    if ("init" == propertyName) {
      colix = Graphics3D.ORANGE;
      newScale = 0;
      isFixed = isReversed = isRotated45 = isCrossed = noHead = isBarb = false;
      isCurve = isArc = isArrow = isPlane = isCircle = isCylinder = isLine = false;
      isVertices = isPerpendicular = isVector = false;
      isValid = true;
      length = Float.MAX_VALUE;
      diameter = 0;
      width = 0;
      indicatedModelIndex = -1;
      offset = null;
      plane = null;
      polygon = null;
      nidentifiers = nbitsets = 0;
      vData = new ArrayList<Object[]>();
      modelCount = viewer.getModelCount();
      bsAllModels = null;
      intersectID = null;
      slabData = null;
      boundBox = null;
      explicitID = false;
      setPropertySuper("thisID", JmolConstants.PREVIOUS_MESH_ID, null);
      setPropertySuper("init", value, bs);
      return;
    }

    if ("length" == propertyName) {
      length = ((Float) value).floatValue();
      return;
    }

    if ("fixed" == propertyName) {
      isFixed = ((Boolean) value).booleanValue();
      return;
    }

    if ("intersect" == propertyName) {
      if (value instanceof Point3f[])
        boundBox = (Point3f[]) value;
      else
        intersectID = (String) value;
      return;
    }
    
    if ("slab" == propertyName) {
      int meshIndex = getIndexFromName((String) value);
      if (meshIndex < 0) {
        // could be isosurface?
        return;
      }
      Mesh m = meshes[meshIndex];
      if (m.checkByteCount != 1)
        return;
      slabData = new MeshSurface(m.vertices, new float[m.vertexCount], m.vertexCount, 
          m.polygonIndexes, m.polygonCount, 1);
      return;
    }
    
    if ("lineData" == propertyName) {
      lineData = new ArrayList<Point3f[]>();
      if (indicatedModelIndex < 0)
        indicatedModelIndex = viewer.getCurrentModelIndex();
      float[] fdata = (float[]) value;
      int n = fdata.length / 6;
      for (int i = 0, pt = 0; i < n; i++)
        lineData.add(new Point3f[] {
            new Point3f(fdata[pt++], fdata[pt++], fdata[pt++]),
            new Point3f(fdata[pt++], fdata[pt++], fdata[pt++])
            });
      return;
    }
    
    if ("modelIndex" == propertyName) {
      //from saved state -- used to set modelVertices
      indicatedModelIndex = ((Integer) value).intValue();
      if (indicatedModelIndex < 0 || indicatedModelIndex >= modelCount)
        return;
      vData.add(new Object[] { Integer.valueOf(PT_MODEL_INDEX),
          (modelInfo = new int[] { indicatedModelIndex, 0 }) });
      return;
    }

    if ("planedef" == propertyName) {
      plane = (Point4f) value;
      if (intersectID != null || boundBox != null || slabData != null)
        return;
     if (isCircle || isArc)
        isPlane = true;
      vData.add(new Object[] { Integer.valueOf(PT_COORD), new Point3f(Float.NaN, Float.NaN, Float.NaN) });
      return;
    }

    if ("perp" == propertyName) {
      isPerpendicular = true;
      return;
    }

    if ("cylinder" == propertyName) {
      isCylinder = true;
      return;
    }

    if ("plane" == propertyName) {
      isPlane = true;
      return;
    }

    if ("curve" == propertyName) {
      isCurve = true;
      return;
    }

    if ("arrow" == propertyName) {
      isArrow = true;
      return;
    }

    if ("line" == propertyName) {
      isLine = true;
      isCurve = true;
      return;
    }

    if ("arc" == propertyName) {
      isCurve = true;
      isArc = true;
      if (isArrow) {
        isArrow = false;
        isVector = true;
      }
      return;
    }

    if ("circle" == propertyName) {
      isCircle = true;
      return;
    }

    if ("vector" == propertyName) {
      isArrow = true;
      isVector = true;
      return;
    }

    if ("vertices" == propertyName) {
      isVertices = true;
      return;
    }

    if ("reverse" == propertyName) {
      isReversed = true;
      return;
    }

    if ("nohead" == propertyName) {
      noHead = true;
      return;
    }

    if ("isbarb" == propertyName) {
      isBarb = true;
      return;
    }

    if ("rotate45" == propertyName) {
      isRotated45 = true;
      return;
    }

    if ("crossed" == propertyName) {
      isCrossed = true;
      return;
    }

    if ("points" == propertyName) {
      newScale = ((Integer) value).floatValue() / 100;
      if (newScale == 0)
        newScale = 1;
      return;
    }

    if ("scale" == propertyName) {
      newScale = ((Integer) value).floatValue() / 100;
      if (newScale == 0)
        newScale = 0.01f; // very tiny but still sizable;
      if (thisMesh != null) {
        // no points in this script statement
        scaleDrawing(thisMesh, newScale);
        thisMesh.initialize(JmolConstants.FULLYLIT, null, null);
      }
      return;
    }

    if ("diameter" == propertyName) {
      diameter = ((Float) value).intValue();
      return;
    }

    if ("width" == propertyName) {
      width = ((Float) value).floatValue();
      return;
    }

    if ("identifier" == propertyName) {
      String thisID = (String) value;
      int meshIndex = getIndexFromName(thisID);
      if (meshIndex >= 0) {
        vData.add(new Object[] { Integer.valueOf(PT_IDENTIFIER),
            new int[] { meshIndex, isReversed ? 1 : 0, isVertices ? 1 : 0 } });
        isReversed = isVertices = false;
        nidentifiers++;
      } else {
        Logger.error("draw identifier " + value + " not found");
        isValid = false;
      }
      return;
    }

    if ("polygon" == propertyName) {
      polygon = (List<Object>) value;
      if (polygon == null)
        polygon = new ArrayList<Object>();
      return;
    }

    if ("coord" == propertyName) {
      vData.add(new Object[] { Integer.valueOf(PT_COORD), value });
      if (indicatedModelIndex >= 0)
        modelInfo[1]++; // counts vertices
      return;
    }

    if ("offset" == propertyName) {
      offset = new Vector3f((Point3f) value);
      if (thisMesh != null)
        thisMesh.offset(offset);
      return;
    }

    if ("atomSet" == propertyName) {
      if (BitSetUtil.cardinalityOf((BitSet) value) == 0)
        return;
      BitSet bsAtoms = (BitSet) value;
      vData.add(new Object[] { Integer.valueOf(PT_BITSET), bsAtoms });
      nbitsets++;
      if (isCircle && diameter == 0 && width == 0)
        width = viewer.calcRotationRadius(bsAtoms) * 2.0f;
      return;
    }

    if ("modelBasedPoints" == propertyName) {
      vData.add(new Object[] { Integer.valueOf(PT_MODEL_BASED_POINTS), value });
      return;
    }
    
    if ("set" == propertyName) {
      if (thisMesh == null) {
        allocMesh(null, null);
        thisMesh.colix = colix;
      }
      thisMesh.isValid = (isValid ? setDrawing() : false);
      if (thisMesh.isValid) {
        if (thisMesh.vertexCount > 2 && length != Float.MAX_VALUE
            && newScale == 1)
          newScale = length;
        scaleDrawing(thisMesh, newScale);
        thisMesh.initialize(JmolConstants.FULLYLIT, null, null);
        setAxes(thisMesh);
        thisMesh.title = title;
        thisMesh.visible = true;
      }
      nPoints = -1; // for later scaling
      vData = null;
      lineData = null;
      return;
    }
    
    if (propertyName == "deleteModelAtoms") {
      int modelIndex = ((int[]) ((Object[]) value)[2])[0];
      //int firstAtomDeleted = ((int[])((Object[])value)[2])[1];
      //int nAtomsDeleted = ((int[])((Object[])value)[2])[2];
      for (int i = meshCount; --i >= 0;) {
        DrawMesh m = dmeshes[i];
        if (m == null)
          continue;
        boolean deleteMesh = (m.modelIndex == modelIndex);
        if (m.modelFlags != null) {
          m.deleteAtoms(modelIndex);
          deleteMesh = (m.modelFlags.length() == 0);
          if (!deleteMesh)
            continue;
        } 
        if (deleteMesh) {
          meshCount--;
          if (meshes[i] == currentMesh)
            currentMesh = thisMesh = null;
          meshes = dmeshes = (DrawMesh[]) ArrayUtil
              .deleteElements(meshes, i, 1);
        } else if (meshes[i].modelIndex > modelIndex) {
          meshes[i].modelIndex--;
        }
      }
      resetObjects();
      return;
    }

    setPropertySuper(propertyName, value, bs);
  }

 private void resetObjects() {
    htObjects.clear();
    for (int i = 0; i < meshCount; i++) {
      Mesh m = meshes[i];
      m.index = i;
      htObjects.put(m.thisID.toUpperCase(), m);
    }    
  }

  @Override
  public boolean getProperty(String property, Object[] data) {
    if (property == "getCenter") {
      String id = (String) data[0];
      int index = ((Integer) data[1]).intValue();
      int modelIndex = ((Integer) data[2]).intValue();
      data[2] = getSpinCenter(id, index, modelIndex);
      return (data[2] != null);
    }
    if (property == "getSpinAxis") {
      String id = (String) data[0];
      int index = ((Integer) data[1]).intValue();
      data[2] =  getSpinAxis(id, index);
      return (data[2] != null);
    }
    return super.getProperty(property, data);
  }

  @Override
  public Object getProperty(String property, int index) {
    if (property == "command")
      return getDrawCommand(thisMesh);
    if (property == "type")
      return Integer.valueOf(thisMesh == null ? JmolConstants.DRAW_NONE : thisMesh.drawType);
    return super.getProperty(property, index);
  }

  private Point3f getSpinCenter(String axisID, int vertexIndex, int modelIndex) {
    String id;
    int pt = axisID.indexOf("[");
    int pt2;
    if (pt > 0) {
      id = axisID.substring(0, pt);
      if ((pt2 = axisID.lastIndexOf("]")) < pt)
        pt2 = axisID.length();
      try {
        vertexIndex = Integer.parseInt(axisID.substring(pt + 1, pt2));
      } catch (Exception e) {
        // ignore
      }
    } else {
      id = axisID;
    }
    DrawMesh m = (DrawMesh) getMesh(id);
    if (m == null || m.vertices == null)
      return null;
    // >= 0 ? that vertexIndex
    // < 0 and no ptCenters or modelIndex < 0 -- center point
    // < 0 center for modelIndex
    if (vertexIndex == Integer.MAX_VALUE)
      return new Point3f(m.index + 1, meshCount, m.vertexCount);
    if (vertexIndex != Integer.MIN_VALUE) 
      vertexIndex = m.getVertexIndexFromNumber(vertexIndex);
    return (vertexIndex >= 0 ? m.vertices[vertexIndex] : m.ptCenters == null
        || modelIndex < 0 || modelIndex >= m.ptCenters.length 
        ? m.ptCenter : m.ptCenters[modelIndex]);
  }
   
  private Vector3f getSpinAxis(String axisID, int modelIndex) {
    DrawMesh m = (DrawMesh) getMesh(axisID);
    return (m == null || m.vertices == null ? null 
        : m.ptCenters == null || modelIndex < 0 ? m.axis : m.axes[modelIndex]);
   }
  
  private boolean setDrawing() {
    if (thisMesh == null)
      allocMesh(null, null);
    thisMesh.clear("draw");
    thisMesh.diameter = diameter;
    thisMesh.width = width;
    if (intersectID != null || boundBox != null)
      setIntersectData();
    else if (slabData != null)
      setSlabData();
    if (polygon == null && (lineData != null ? lineData.size() == 0 : vData.size() == 0))
      return false;
    if (polygon != null || lineData != null || indicatedModelIndex < 0
        && (isFixed || isArrow || isCurve || isCircle || isCylinder || modelCount == 1)) {
      // make just ONE copy 
      // arrows and curves simply can't be handled as
      // multiple frames yet
      thisMesh.modelIndex = (lineData == null ? viewer.getCurrentModelIndex() : indicatedModelIndex);
      thisMesh.isFixed = (isFixed || lineData == null && thisMesh.modelIndex < 0 && modelCount > 1);
      if (isFixed && modelCount > 1)
        thisMesh.modelIndex = -1;
      else if (lineData == null && thisMesh.modelIndex < 0)
        thisMesh.modelIndex = 0;
      thisMesh.ptCenters = null;
      thisMesh.modelFlags = null;
      thisMesh.drawTypes = null;
      thisMesh.drawVertexCounts = null;
      if (polygon != null) {
        if (polygon.size() == 0)
          return false;
        thisMesh.isPolygonSet = true;
        thisMesh.vertices = (Point3f[]) polygon.get(0);
        thisMesh.polygonIndexes = (int[][]) polygon.get(1);          
        thisMesh.drawVertexCount = thisMesh.vertexCount = thisMesh.vertices.length;
        thisMesh.polygonCount = thisMesh.polygonIndexes.length;
        for (int i = 0; i < thisMesh.polygonCount; i++) {
          for (int j = 0; j < 3; j++)
            if (thisMesh.polygonIndexes[i][j] >= thisMesh.vertexCount)
              return false;
        }
        thisMesh.drawType = JmolConstants.DRAW_POLYGON;
        thisMesh.checkByteCount = 1;
      } else if (lineData != null) {
        thisMesh.lineData = lineData;
      } else {
        thisMesh.setPolygonCount(1);      
        if (setPoints(-1, -1))
          setPoints(-1, nPoints);
        setPolygon(0); 
      }
    } else {
      // multiple copies, one for each model involved
      thisMesh.modelIndex = -1;
      thisMesh.setPolygonCount(modelCount);
      thisMesh.ptCenters = new Point3f[modelCount];
      thisMesh.modelFlags = new BitSet();
      thisMesh.drawTypes = new int[modelCount];
      thisMesh.drawVertexCounts = new int[modelCount];
      thisMesh.vertexCount = 0;
      if (indicatedModelIndex >= 0) {
        setPoints(-1, 0);
        thisMesh.drawType = JmolConstants.DRAW_MULTIPLE;
        thisMesh.drawVertexCount = -1;
        thisMesh.modelFlags.set(indicatedModelIndex);
        indicatedModelIndex = -1;
      } else {
        BitSet bsModels = viewer.getVisibleFramesBitSet();
        for (int iModel = 0; iModel < modelCount; iModel++) {
          if (bsModels.get(iModel) && setPoints(iModel, -1)) {
            setPoints(iModel, nPoints);
            setPolygon(iModel); 
            thisMesh.setCenter(iModel);
            thisMesh.drawTypes[iModel] = thisMesh.drawType;
            thisMesh.drawVertexCounts[iModel] = thisMesh.drawVertexCount;
            thisMesh.drawType = JmolConstants.DRAW_MULTIPLE;
            thisMesh.drawVertexCount = -1;
            thisMesh.modelFlags.set(iModel);
          } else {
            thisMesh.drawTypes[iModel] = JmolConstants.DRAW_NONE;
            thisMesh.polygonIndexes[iModel] = new int[0];
          }
        }
      }
    }
    thisMesh.isVector = isVector;
    thisMesh.noHead = noHead;
    thisMesh.isBarb = isBarb;
    thisMesh.width= (thisMesh.drawType == JmolConstants.DRAW_CYLINDER || 
        thisMesh.drawType == JmolConstants.DRAW_CIRCULARPLANE ? -Math.abs(width) : width);
    thisMesh.setCenter(-1);
    if (offset != null)
      thisMesh.offset(offset);
    if (thisMesh.thisID == null) {
      thisMesh.thisID = JmolConstants.getDrawTypeName(thisMesh.drawType) + (++nUnnamed);
      htObjects.put(thisMesh.thisID, thisMesh);
    }
    clean();
    return true;
  }

  private void setIntersectData() {
    if (boundBox != null) {
      // TODO
      if (plane == null) {
        
      }
    } else if (plane != null && intersectID != null) {
      List<Point3f[]> vData = new ArrayList<Point3f[]>();
      Object[] data = new Object[] { intersectID, plane, vData, null };
      viewer.getShapeProperty(JmolConstants.SHAPE_ISOSURFACE, "intersectPlane",
          data);
      if (vData.size() == 0)
        return;
      indicatedModelIndex = ((Integer)data[3]).intValue();
      lineData = vData;
    }
  }

  MeshSurface slabData;
  
  private void setSlabData() {
    if (plane != null) {
      slabData.getIntersection(plane, null, 0, null, false, true);
      polygon = new ArrayList<Object>();
      polygon.add(slabData.vertices);
      polygon.add(slabData.polygonIndexes);
    }
  }

  private void addPoint(Point3f newPt, int iModel) {
    boolean isOK = (iModel < 0 || bsAllModels.get(iModel));
    if (makePoints) {
      if (!isOK)
        return;
      ptList[nPoints] = new Point3f(newPt);
      if (newPt.z == Float.MAX_VALUE || newPt.z == -Float.MAX_VALUE)
        thisMesh.haveXyPoints = true;
    } else if (iModel >= 0) {
      bsAllModels.set(iModel);
    }
    nPoints++;
  }

  private boolean setPoints(int iModel, int n) {
    // {x,y,z} points are already defined in ptList
    // $drawID references may be fixed or not
    // Prior to 11.5.37, points were created in the order:
    //  1) all {x,y,z} points
    //  2) all $drawID points
    //  3) all {atomExpression} points
    //  4) all {atomExpression}.split() points
    // Order is only important when there are four points, 
    // where they may become crossed, so
    // we also provide a flag CROSSED to uncross them
    this.makePoints = (n >= 0);
    if (makePoints) {
      ptList = new Point3f[Math.max(5,n)];
      if (bsAllModels == null)
        bsAllModels = viewer.getVisibleFramesBitSet();
    }
    nPoints = 0;
    int nData = vData.size();
    int modelIndex = 0;
    BitSet bs;
    BitSet bsModel = (iModel < 0 ? null : viewer.getModelUndeletedAtomsBitSet(iModel));
    for (int i = 0; i < nData; i++) {
      Object[] info = vData.get(i);
      switch (((Integer) info[0]).intValue()) {
      case PT_MODEL_INDEX:
        // from the saved state
        int[] modelInfo = (int[]) info[1];
        modelIndex = modelInfo[0];
        nPoints = modelInfo[1];
        int nVertices = Math.max(nPoints, 3);
        int n0 = thisMesh.vertexCount;
        if (nPoints > 0) {
          int[] p = thisMesh.polygonIndexes[modelIndex] = new int[nVertices];
          for (int j = 0; j < nPoints; j++) {
            info = vData.get(++i);
            p[j] = thisMesh.addVertexCopy((Point3f) info[1]);
          }
          for (int j = nPoints; j < 3; j++) {
            p[j] = n0 + nPoints - 1;
          }
          thisMesh.drawTypes[modelIndex] = thisMesh.drawVertexCounts[modelIndex] = nPoints;
          thisMesh.modelFlags.set(modelIndex);
        }
        break;
      case PT_COORD:
        addPoint((Point3f) info[1], (makePoints ? iModel : -1));
        break;
      case PT_BITSET:
        // (atom set) references must be filtered for relevant model
        // note that if a model doesn't have a relevant point, one may
        // get a line instead of a plane, a point instead of a line, etc.
        bs = BitSetUtil.copy((BitSet) info[1]);
        if (bsModel != null)
          bs.and(bsModel);
        if (bs.length() > 0)
          addPoint(viewer.getAtomSetCenter(bs), (makePoints ? iModel : -1));
        break;
      case PT_IDENTIFIER:
        int[] idInfo = (int[]) info[1];
        DrawMesh m = dmeshes[idInfo[0]];
        boolean isReversed = (idInfo[1] == 1);
        boolean isVertices = (idInfo[2] == 1);
        if (m.modelIndex > 0 && m.modelIndex != iModel)
          return false;
        if (bsAllModels == null)
          bsAllModels = new BitSet();
        if (isPlane && !isCircle || isPerpendicular || isVertices) {
          if (isReversed) {
            if (iModel < 0 || iModel >= m.polygonCount)
              for (int ipt = m.drawVertexCount; --ipt >= 0;)
                addPoint(m.vertices[ipt], iModel);
            else if (m.polygonIndexes[iModel] != null)
              for (int ipt = m.drawVertexCounts[iModel]; --ipt >= 0;)
                addPoint(m.vertices[m.polygonIndexes[iModel][ipt]], iModel);
          } else {
            if (iModel < 0 || iModel >= m.polygonCount)
              for (int ipt = 0; ipt < m.drawVertexCount; ipt++)
                addPoint(m.vertices[ipt], iModel);
            else if (m.polygonIndexes[iModel] != null)
              for (int ipt = 0; ipt < m.drawVertexCounts[iModel]; ipt++)
                addPoint(m.vertices[m.polygonIndexes[iModel][ipt]], iModel);
          }
        } else {
          if (iModel < 0 || m.ptCenters == null || m.ptCenters[iModel] == null)
            addPoint(m.ptCenter, iModel);
          else
            addPoint(m.ptCenters[iModel], iModel);
        }
        break;
      case PT_MODEL_BASED_POINTS:
        // from list variables
        String[] modelBasedPoints = (String[]) info[1];
        if (bsAllModels == null)
          bsAllModels = new BitSet();
        for (int j = 0; j < modelBasedPoints.length; j++)
          if (iModel < 0 || j == iModel) {
            Object point = Escape.unescapePointOrBitsetOrMatrixOrArray(modelBasedPoints[j]);
            bsAllModels.set(j);
            if (point instanceof Point3f) {
              addPoint((Point3f) point, j);
            } else if (point instanceof BitSet) {
              bs = (BitSet) point;
              if (bsModel != null)
                bs.and(bsModel);
              if (bs.length() > 0)
                addPoint(viewer.getAtomSetCenter(bs), j);
            }
          }
        break;
      }
    }
    if (makePoints && isCrossed && nPoints == 4) {
      Point3f pt = ptList[1];
      ptList[1] = ptList[2];
      ptList[2] = pt;
    }
    return (nPoints > 0);
  }

  private final Vector3f vAB = new Vector3f();
  private final Vector3f vAC = new Vector3f();

  private void setPolygon(int nPoly) {
    int nVertices = nPoints;
    int drawType = JmolConstants.DRAW_POINT;
    if (isArc) {
      if (nVertices >= 2) {
        drawType = JmolConstants.DRAW_ARC;
      } else {
        isArc = false;
        isVector = false;
        isCurve = false;
        isArrow = true;
      }
    }
    if (isCircle) {
      length = 0;
      if (nVertices == 2)
        isPlane = true;
      if (!isPlane)
        drawType = JmolConstants.DRAW_CIRCLE;
      if (width == 0)
        width = 1;
    } else if ((isCurve || isArrow) && nVertices >= 2 && !isArc) {
      drawType = (isLine ? JmolConstants.DRAW_LINE_SEGMENT
          : isCurve ? JmolConstants.DRAW_CURVE : JmolConstants.DRAW_ARROW);
    }
    if (isVector && !isArc) {
      if (nVertices > 2)
        nVertices = 2;
      else if (plane == null && nVertices != 2)
        isVector = false;
    }
    if (thisMesh.haveXyPoints) {
      isPerpendicular = false;
      if (nVertices == 3 && isPlane)
        isPlane = false;
      length = Float.MAX_VALUE;
      thisMesh.diameter = 0;
    } else if (nVertices == 2 && isVector) {
      ptList[1].add(ptList[0]);
    }
    float dist = 0;
    if (isArc || plane != null && isCircle) {
      if (plane != null) {
        dist = Measure.distanceToPlane(plane, ptList[0]);
        vAC.set(-plane.x, -plane.y, -plane.z);
        vAC.normalize();
        if (dist < 0)
          vAC.scale(-1);
        if (isCircle) {
          vAC.scale(0.005f);
          ptList[0].sub(vAC);
          vAC.scale(2);
        }
        vAC.add(ptList[0]);
        ptList[1] = new Point3f(vAC);
        drawType = (isArrow ? JmolConstants.DRAW_ARROW
            : isArc ? JmolConstants.DRAW_ARC : JmolConstants.DRAW_CIRCULARPLANE);
      }
      if (isArc) {
        dist = Math.abs(dist);
        if (nVertices > 3) {
          // draw arc {center} {pt2} {ptRef} {angleOffset theta
          // fractionalAxisOffset}
        } else if (nVertices == 3) {
          // draw arc {center} {pt2} {angleOffset theta fractionalAxisOffset}
          ptList[3] = new Point3f(ptList[2]);
          ptList[2] = randomPoint();
        } else {
          if (nVertices == 2) {
            // draw arc {center} {pt2}
            ptList[2] = randomPoint();
          }
          ptList[3] = new Point3f(0, 360, 0);
        }
        if (plane != null)
          ptList[3].z *= dist;
        nVertices = 4;
      }
      plane = null;
    } else if (drawType == JmolConstants.DRAW_POINT) {
      Point3f pt;
      Point3f center = new Point3f();
      Vector3f normal = new Vector3f();
      if (nVertices == 2 && plane != null) {
        ptList[1] = new Point3f(ptList[0]);
        Vector3f vTemp = new Vector3f();
        Measure.getPlaneProjection(ptList[1], plane, ptList[1], vTemp);
        nVertices = -2;
        if (isArrow)
          drawType = JmolConstants.DRAW_ARROW;
        plane = null;
      }
      if (nVertices == 3 && isPlane && !isPerpendicular) {
        // three points define a plane
        pt = new Point3f(ptList[1]);
        pt.sub(ptList[0]);
        pt.scale(0.5f);
        ptList[3] = new Point3f(ptList[2]);
        ptList[2].add(pt);
        ptList[3].sub(pt);
        nVertices = 4;
      } else if (nVertices >= 3 && !isPlane && isPerpendicular) {
        // normal to plane
        Measure.calcNormalizedNormal(ptList[0], ptList[1], ptList[2],
            normal, vAB, vAC);
        center = new Point3f();
        Measure.calcAveragePointN(ptList, nVertices, center);
        dist = (length == Float.MAX_VALUE ? ptList[0].distance(center) : length);
        normal.scale(dist);
        ptList[0].set(center);
        ptList[1].set(center);
        ptList[1].add(normal);
        nVertices = 2;
      } else if (nVertices == 2 && isPerpendicular) {
        // perpendicular line to line or plane to line
        Measure.calcAveragePoint(ptList[0], ptList[1], center);
        dist = (length == Float.MAX_VALUE ? ptList[0].distance(center) : length);
        if (isPlane && length != Float.MAX_VALUE)
          dist /= 2f;
        if (isPlane && isRotated45)
          dist *= 1.4142f;
        Measure.calcXYNormalToLine(ptList[0], ptList[1], normal);
        normal.scale(dist);
        if (isPlane) {
          ptList[2] = new Point3f(center);
          ptList[2].sub(normal);
          pt = new Point3f(center);
          pt.add(normal);
          // pt
          // |
          // 0-------+--------1
          // |
          // 2
          Measure.calcNormalizedNormal(ptList[0], ptList[1], ptList[2],
              normal, vAB, vAC);
          normal.scale(dist);
          ptList[3] = new Point3f(center);
          ptList[3].add(normal);
          ptList[1].set(center);
          ptList[1].sub(normal);
          ptList[0].set(pt);
          //             
          // pt,0 1
          // |/
          // -------+--------
          // /|
          // 3 2

          if (isRotated45) {
            Measure.calcAveragePoint(ptList[0], ptList[1], ptList[0]);
            Measure.calcAveragePoint(ptList[1], ptList[2], ptList[1]);
            Measure.calcAveragePoint(ptList[2], ptList[3], ptList[2]);
            Measure.calcAveragePoint(ptList[3], pt, ptList[3]);
          }
          nVertices = 4;
        } else {
          ptList[0].set(center);
          ptList[1].set(center);
          ptList[0].sub(normal);
          ptList[1].add(normal);
        }
        if (isArrow && nVertices != -2)
          isArrow = false;
      } else if (nVertices == 2 && length != Float.MAX_VALUE) {
        Measure.calcAveragePoint(ptList[0], ptList[1], center);
        normal.set(ptList[1]);
        normal.sub(center);
        normal.scale(0.5f / normal.length() * (length == 0 ? 0.01f : length));
        if (length == 0)
          center.set(ptList[0]);
        ptList[0].set(center);
        ptList[1].set(ptList[0]);
        ptList[0].sub(normal);
        ptList[1].add(normal);
      }
      if (nVertices > 4)
        nVertices = 4; // for now

      switch (nVertices) {
      case -2:
        nVertices = 2;
        break;
      case 1:
        break;
      case 2:
        drawType = (isArc ? JmolConstants.DRAW_ARC
            : isPlane && isCircle ? JmolConstants.DRAW_CIRCULARPLANE
                : isCylinder ? JmolConstants.DRAW_CYLINDER
                    : JmolConstants.DRAW_LINE);
        break;
      default:
        drawType = JmolConstants.DRAW_PLANE;
      }
    }
    thisMesh.drawType = drawType;
    thisMesh.drawVertexCount = nVertices;

    if (nVertices == 0)
      return;
    int nVertices0 = thisMesh.vertexCount;

    for (int i = 0; i < nVertices; i++) {
      thisMesh.addVertexCopy(ptList[i]);
    }
    int npoints = (nVertices < 3 ? 3 : nVertices);
    thisMesh.setPolygonCount(nPoly + 1);
    thisMesh.polygonIndexes[nPoly] = new int[npoints];
    for (int i = 0; i < npoints; i++) {
      thisMesh.polygonIndexes[nPoly][i] = nVertices0
          + (i < nVertices ? i : nVertices - 1);
    }
    return;
  }

  private static void scaleDrawing(DrawMesh mesh, float newScale) {
    /*
     * allows for Draw to scale object
     * have to watch out for double-listed vertices
     * 
     */
    if (newScale == 0 || mesh.vertexCount == 0 || mesh.scale == newScale)
      return;
    float f = newScale / mesh.scale;
    mesh.scale = newScale;
    if (mesh.haveXyPoints || mesh.drawType == JmolConstants.DRAW_ARC || mesh.drawType == JmolConstants.DRAW_CIRCLE || mesh.drawType == JmolConstants.DRAW_CIRCULARPLANE)
      return; // done in renderer
    Vector3f diff = new Vector3f();
    int iptlast = -1;
    int ipt = 0;
    for (int i = mesh.polygonCount; --i >= 0;) {
      Point3f center = (mesh.isVector ? mesh.vertices[0] 
          : mesh.ptCenters == null ? mesh.ptCenter
          : mesh.ptCenters[i]);
      if (center == null)
        return;
      if (mesh.polygonIndexes[i] == null)
        continue;
      iptlast = -1;
      for (int iV = mesh.polygonIndexes[i].length; --iV >= 0;) {
        ipt = mesh.polygonIndexes[i][iV];
        if (ipt == iptlast)
          continue;
        iptlast = ipt;
        diff.sub(mesh.vertices[ipt], center);
        diff.scale(f);
        diff.add(center);
        mesh.vertices[ipt].set(diff);
      }
    }
  }

  private final static void setAxes(DrawMesh m) {
    m.axis = new Vector3f(0, 0, 0);
    m.axes = new Vector3f[m.polygonCount > 0 ? m.polygonCount : 1];
    if (m.vertices == null)
      return;
    int n = 0;
    for (int i = m.polygonCount; --i >= 0;) {
      int[] p = m.polygonIndexes[i];
      m.axes[i] = new Vector3f();
      if (p == null || p.length == 0) {
      } else if (m.drawVertexCount == 2 || m.drawVertexCount < 0
          && m.drawVertexCounts[i] == 2) {
        m.axes[i].sub(m.vertices[p[0]],
            m.vertices[p[1]]);
        n++;
      } else {
        Measure.calcNormalizedNormal(m.vertices[p[0]],
            m.vertices[p[1]],
            m.vertices[p[2]], m.axes[i], m.vAB, m.vAC);
        n++;
      }
      m.axis.add(m.axes[i]);
    }
    if (n == 0)
      return;
    m.axis.scale(1f / n);
  }

  @Override
  public void setVisibilityFlags(BitSet bs) {
    /*
     * set all fixed objects visible; others based on model being displayed note
     * that this is NOT done with atoms and bonds, because they have mads. When
     * you say "frame 0" it is just turning on all the mads.
     */
    for (int i = 0; i < meshCount; i++) {
      DrawMesh m = dmeshes[i];
      if (m == null) {
        //System.out.println("mesh is null");
        continue;
      }
      m.visibilityFlags = (m.isValid && m.visible ? myVisibilityFlag : 0);
      if (m.modelIndex >= 0 && !bs.get(m.modelIndex) || m.modelFlags != null
          && !BitSetUtil.haveCommon(bs, m.modelFlags)) {
        m.visibilityFlags = 0;
      } else if (m.modelFlags != null) {
        m.bsMeshesVisible.clear();
        m.bsMeshesVisible.or(m.modelFlags);
        m.bsMeshesVisible.and(bs);
      }

    }
  }
  
  private final static int MAX_OBJECT_CLICK_DISTANCE_SQUARED = 10 * 10;

  private final Point3i ptXY = new Point3i();
  private final Point3fi PT_NAN = new Point3fi(Float.NaN, 0.0f, 0.0f);
  
  @Override
  public Point3fi checkObjectClicked(int x, int y, int action, BitSet bsVisible) {
    boolean isPickingMode = (viewer.getPickingMode() == ActionManager.PICKING_DRAW);
    boolean isSpinMode = (viewer.getPickingMode() == ActionManager.PICKING_SPIN);
    boolean isDrawPicking = viewer.getDrawPicking();
    if (!isPickingMode && !isDrawPicking && !isSpinMode
        || Graphics3D.isColixTranslucent(colix))
      return null;
    if (!findPickedObject(x, y, false, bsVisible))
      return null;
    Point3f v = pickedMesh.vertices[pickedMesh.polygonIndexes[pickedModel][pickedVertex]];
    if (isDrawPicking && !isPickingMode) {
      if (action != 0) // not mouseMove
        setStatusPicked(-2, v);
      return getPickedPoint(v);
    }
    if (action == 0 || pickedMesh.polygonIndexes[pickedModel][0] == pickedMesh.polygonIndexes[pickedModel][1]) {
      return (action == 0 ? getPickedPoint(v) : null); 
    }
    boolean isClockwise = viewer.isBound(action, ActionManager.ACTION_spinDrawObjectCW);
    if (pickedVertex == 0) {
      viewer.startSpinningAxis(
          pickedMesh.vertices[pickedMesh.polygonIndexes[pickedModel][1]],
          pickedMesh.vertices[pickedMesh.polygonIndexes[pickedModel][0]],
          isClockwise);
    } else {
      viewer.startSpinningAxis(
          pickedMesh.vertices[pickedMesh.polygonIndexes[pickedModel][0]],
          pickedMesh.vertices[pickedMesh.polygonIndexes[pickedModel][1]],
          isClockwise);
    }
    return PT_NAN;
  }

  private Point3fi getPickedPoint(Point3f v) {
    Point3fi pt = new Point3fi();
    pt.set(v);
    pt.modelIndex = (short) pickedMesh.modelIndex;
    BitSet bs = ((DrawMesh) pickedMesh).modelFlags;
    if (pt.modelIndex < 0 && bs != null && BitSetUtil.cardinalityOf(bs) == 1)
      pt.modelIndex = (short) bs.nextSetBit(0);
    return pt; 
  }

  @Override
  public boolean checkObjectHovered(int x, int y, BitSet bsVisible) {
    if (Graphics3D.isColixTranslucent(colix))
      return false;
    if (!findPickedObject(x, y, false, bsVisible))
      return false;
    if (g3d.isDisplayAntialiased()) {
      //because hover rendering is done in FIRST pass only
      x <<= 1;
      y <<= 1;
    }      
    String s = (pickedMesh.title == null ? pickedMesh.thisID
        : pickedMesh.title[0]);
    if (s.length() > 1 && s.charAt(0) == '>')
      s = s.substring(1);
    viewer.hoverOn(x, y, s);
    return true;
  }

  @Override
  public synchronized boolean checkObjectDragged(int prevX, int prevY, int x,
                                                 int y, int action,
                                                 BitSet bsVisible) {
    //TODO -- can dispense with this first check:
    if (viewer.getPickingMode() != ActionManager.PICKING_DRAW)
      return false;
    boolean moveAll = viewer.isBound(action,
        ActionManager.ACTION_dragDrawObject);
    boolean movePoint = viewer.isBound(action,
        ActionManager.ACTION_dragDrawPoint);
    if (!moveAll && !movePoint)
      return false;
    // mouse down ?
    if (prevX == Integer.MIN_VALUE)
      return findPickedObject(x, y, true, bsVisible);
    // mouse up ?
    if (prevX == Integer.MAX_VALUE) {
      pickedMesh = null;
      return false;
    }
    if (pickedMesh == null)
      return false;
    DrawMesh dm = (DrawMesh) pickedMesh;
    move2D(dm, dm.polygonIndexes[pickedModel], pickedVertex, x,
        y, moveAll);
    thisMesh = dm;
    return true;
  }
  
  private void move2D(DrawMesh mesh, int[] vertexes, 
                      int iVertex, int x, int y,
                      boolean moveAll) {
    if (vertexes == null || vertexes.length == 0)
      return;
    if (g3d.isAntialiased()) {
      x <<= 1;
      y <<= 1;
    }
    Point3f pt = new Point3f();
    Point3f coord = new Point3f(mesh.vertices[vertexes[iVertex]]);
    Point3f newcoord = new Point3f();
    Vector3f move = new Vector3f();
    viewer.transformPoint(coord, pt);
    pt.x = x;
    pt.y = y;
    viewer.unTransformPoint(pt, newcoord);
    move.set(newcoord);
    move.sub(coord);
    int klast = -1;
    for (int i = (moveAll ? 0 : iVertex); i < vertexes.length; i++)
      if (moveAll || i == iVertex) {
        int k = vertexes[i];
        if (k == klast)
            break;
        mesh.vertices[k].add(move);
        if (!moveAll)
          break;
        klast = k;
      }
    if (mesh.ptCenters == null)
      mesh.setCenter(-1);
    else
      for (int i = mesh.ptCenters.length; --i >= 0; )
        mesh.setCenter(i);
    if (Logger.debugging)
      Logger.debug(getDrawCommand(mesh));
    viewer.refresh(3, "draw");
  }
  
  /**
   * 
   * @param x
   * @param y
   * @param isPicking IGNORED
   * @param bsVisible
   * @return  true if found
   */
  private boolean findPickedObject(int x, int y, boolean isPicking, BitSet bsVisible) {
    int dmin2 = MAX_OBJECT_CLICK_DISTANCE_SQUARED;
    if (g3d.isAntialiased()) {
      x <<= 1;
      y <<= 1;
      dmin2 <<= 1;
    }
    pickedModel = 0;
    pickedVertex = 0;
    pickedMesh = null;
    for (int i = 0; i < meshCount; i++) {
      DrawMesh m = dmeshes[i];
      if (m.visibilityFlags != 0) {
        int mCount = (m.modelFlags == null ? 1 : modelCount);
        for (int iModel = mCount; --iModel >= 0;) {
          if (m.modelFlags != null && !m.modelFlags.get(iModel) 
              || m.polygonIndexes == null 
              || iModel >= m.polygonIndexes.length || m.polygonIndexes[iModel] == null)
            continue;
          for (int iVertex = m.polygonIndexes[iModel].length; --iVertex >= 0;) {
            int d2 = coordinateInRange(x, y, m.vertices[m.polygonIndexes[iModel][iVertex]], dmin2, ptXY);
            if (d2 >= 0) {
              pickedMesh = m;
              dmin2 = d2;
              pickedModel = iModel;
              pickedVertex = iVertex;
            }
          }
        }
      }
    }
    return (pickedMesh != null);
  }

  private String getDrawCommand(DrawMesh mesh) {
    modelCount = viewer.getModelCount();
    if (mesh != null)
      return getDrawCommand(mesh, mesh.modelIndex);
    
    StringBuffer sb = new StringBuffer();
    String key = (explicitID && previousMeshID != null
        && TextFormat.isWild(previousMeshID) ? previousMeshID.toUpperCase()
        : null);
    if (key != null && key.length() == 0)
      key = null;
    for (int i = 0; i < meshCount; i++) {
      DrawMesh m = (DrawMesh) meshes[i];
      if (key == null
          || TextFormat.isMatch(m.thisID.toUpperCase(), key, true, true))
        sb.append(getDrawCommand(m, m.modelIndex));
    }
    return sb.toString();
  }

  private String getDrawCommand(DrawMesh mesh, int iModel) {
    if (mesh.drawType == JmolConstants.DRAW_NONE  
        && mesh.lineData == null
        && mesh.drawVertexCount == 0 && mesh.drawVertexCounts == null)
      return "";
    StringBuffer str = new StringBuffer();
    if (!mesh.isFixed && iModel >= 0 && modelCount > 1)
      appendCmd(str, "frame " + viewer.getModelNumberDotted(iModel));
    str.append("  draw ID ").append(Escape.escape(mesh.thisID));
    if (mesh.isFixed)
      str.append(" fixed");
    if (iModel < 0)
      iModel = 0;
    if (mesh.noHead)
      str.append(" noHead");
    else if (mesh.isBarb)
      str.append(" barb");
    if (mesh.scale != 1
        && (mesh.haveXyPoints || mesh.drawType == JmolConstants.DRAW_CIRCLE || mesh.drawType == JmolConstants.DRAW_ARC))
      str.append(" scale ").append(mesh.scale);
    if (mesh.width != 0)
      str.append(" diameter ").append(
          (mesh.drawType == JmolConstants.DRAW_CYLINDER ? Math.abs(mesh.width)
              : mesh.drawType == JmolConstants.DRAW_CIRCULARPLANE ? Math
                  .abs(mesh.width * mesh.scale) : mesh.width));
    else if (mesh.diameter > 0)
      str.append(" diameter ").append(mesh.diameter);
    if (mesh.lineData != null) {
      str.append("  lineData [");
      int n = mesh.lineData.size();
      for (int j = 0; j < n;) {
        Point3f[] pts = mesh.lineData.get(j);
        str.append(Escape.escape(pts[0]));
        str.append(" ");
        str.append(Escape.escape(pts[1]));
        if (++j < n)
          str.append(", ");
      }
      str.append("]");
    } else {
      int nVertices = mesh.drawVertexCount > 0  || mesh.drawVertexCounts == null ? mesh.drawVertexCount
          : mesh.drawVertexCounts[iModel >= 0 ? iModel : 0];
      switch (mesh.drawTypes == null ? mesh.drawType : mesh.drawTypes[iModel]) {
      case JmolConstants.DRAW_POLYGON:
        str.append(" POLYGON ").append(nVertices);
        break;
      case JmolConstants.DRAW_LINE_SEGMENT:
        str.append(" LINE");
        break;
      case JmolConstants.DRAW_ARC:
        str.append(mesh.isVector ? " ARROW ARC" : " ARC");
        break;
      case JmolConstants.DRAW_ARROW:
        str.append(mesh.isVector ? " VECTOR" : " ARROW");
        break;
      case JmolConstants.DRAW_CIRCLE:
        str.append(" CIRCLE");
        break;
      case JmolConstants.DRAW_CURVE:
        str.append(" CURVE");
        break;
      case JmolConstants.DRAW_CIRCULARPLANE:
      case JmolConstants.DRAW_CYLINDER:
        str.append(" CYLINDER");
        break;
      case JmolConstants.DRAW_POINT:
        nVertices = 1; // because this might be multiple points
        break;
      case JmolConstants.DRAW_LINE:
        nVertices = 2; // because this might be multiple lines
        break;
      }
      if (mesh.modelIndex < 0 && !mesh.isFixed) {
        for (int i = 0; i < modelCount; i++)
          if (isPolygonDisplayable(mesh, i)) {
            if (nVertices == 0)
              nVertices = mesh.drawVertexCounts[i];
            str.append(" [ " + i);
            String s = getVertexList(mesh, i, nVertices);
            if (s.indexOf("NaN") >= 0)
              return "";
            str.append(s);
            str.append(" ] ");
          }
      } else if (mesh.drawType == JmolConstants.DRAW_POLYGON) {
        for (int i = 0; i < mesh.vertexCount; i++)
          str.append(" ").append(Escape.escape(mesh.vertices[i]));
        str.append(" ").append(mesh.polygonCount);
        for (int i = 0; i < mesh.polygonCount; i++)
          if (mesh.polygonIndexes[i] == null)
            str.append(" [0 0 0 0]");
          else
            str.append(" ").append(Escape.escapeArray(mesh.polygonIndexes[i]));
      } else {
        String s = getVertexList(mesh, iModel, nVertices);
        if (s.indexOf("NaN") >= 0)
          return "";
        str.append(s);
      }
    }
    if (mesh.title != null) {
      String s = "";
      for (int i = 0; i < mesh.title.length; i++)
        s += "|" + mesh.title[i];
      str.append(Escape.escape(s.substring(1)));
    }
    str.append(";\n");
    appendCmd(str, mesh.getState("draw"));
    appendCmd(str, getColorCommand("draw", mesh.colix));
    return str.toString();
  }

  static boolean isPolygonDisplayable(Mesh mesh, int i) {
    return (i < mesh.polygonIndexes.length 
        && mesh.polygonIndexes[i] != null 
        && mesh.polygonIndexes[i].length > 0);
  }
  
  private static String getVertexList(DrawMesh mesh, int iModel, int nVertices) {
    String str = "";
    try {
      if (iModel >= mesh.polygonIndexes.length)
        iModel = 0; // arrows and curves may not have multiple model representations
      boolean adjustPt = (mesh.isVector && mesh.drawType != JmolConstants.DRAW_ARC);
      for (int i = 0; i < nVertices; i++) {
        Point3f pt = mesh.vertices[mesh.polygonIndexes[iModel][i]];
        if (pt.z == Float.MAX_VALUE || pt.z == -Float.MAX_VALUE) {
          str += (i == 0 ? " " : " ,") + "[" + (int) pt.x + " " + (int) pt.y + (pt.z < 0 ? " %]" : "]");
        } else if (adjustPt && i == 1){
          Point3f pt1 = new Point3f(pt);
          pt1.sub(mesh.vertices[mesh.polygonIndexes[iModel][0]]);
          str += " " + Escape.escape(pt1);
        } else {
          str += " " + Escape.escape(pt);
        }
      }
    } catch (Exception e) {
      Logger.error("Unexpected error in Draw.getVertexList");
    }
    return str;
  }
  
  @Override
  public List<Map<String, Object>> getShapeDetail() {
    List<Map<String, Object>> V = new ArrayList<Map<String,Object>>();
    for (int i = 0; i < meshCount; i++) {
      DrawMesh mesh = dmeshes[i];
      if (mesh.vertexCount == 0)
        continue;
      Map<String, Object> info = new Hashtable<String, Object>();
      info.put("fixed", mesh.ptCenters == null ? Boolean.TRUE : Boolean.FALSE);
      info.put("ID", (mesh.thisID == null ? "<noid>" : mesh.thisID));
      info.put("drawType", JmolConstants.getDrawTypeName(mesh.drawType));
      if (mesh.diameter > 0)
        info.put("diameter", Integer.valueOf(mesh.diameter));
      if (mesh.width != 0)
        info.put("width", new Float(mesh.width));
      info.put("scale", new Float(mesh.scale));
      if (mesh.drawType == JmolConstants.DRAW_MULTIPLE) {
        List<Map<String, Object>> m = new ArrayList<Map<String,Object>>();
        modelCount = viewer.getModelCount();
        for (int k = 0; k < modelCount; k++) {
          if (mesh.ptCenters[k] == null)
            continue;
          Map<String, Object> mInfo = new Hashtable<String, Object>();
          mInfo.put("modelIndex", Integer.valueOf(k));
          mInfo.put("command", getDrawCommand(mesh, k));
          mInfo.put("center", mesh.ptCenters[k]);
          int nPoints = mesh.drawVertexCounts[k];
          mInfo.put("vertexCount", Integer.valueOf(nPoints));
          if (nPoints > 1)
            mInfo.put("axis", mesh.axes[k]);
          List<Point3f> v = new ArrayList<Point3f>();
          for (int ipt = 0; ipt < nPoints; ipt++)
            v.add(mesh.vertices[mesh.polygonIndexes[k][ipt]]);
          mInfo.put("vertices", v);
          if (mesh.drawTypes[k] == JmolConstants.DRAW_LINE) {
            float d = mesh.vertices[mesh.polygonIndexes[k][0]]
                .distance(mesh.vertices[mesh.polygonIndexes[k][1]]);
            mInfo.put("length_Ang", new Float(d));
          }
          m.add(mInfo);
        }
        info.put("models", m);
      } else {
        info.put("command", getDrawCommand(mesh));
        info.put("center", mesh.ptCenter);
        if (mesh.drawVertexCount > 1)
          info.put("axis", mesh.axis);
        List<Point3f> v = new ArrayList<Point3f>();
        for (int j = 0; j < mesh.vertexCount; j++)
          v.add(mesh.vertices[j]);
        info.put("vertices", v);
        if (mesh.drawType == JmolConstants.DRAW_LINE)
          info.put("length_Ang", new Float(mesh.vertices[0]
              .distance(mesh.vertices[1])));
      }
      V.add(info);
    }
    return V;
  }

  @Override
  public String getShapeState() {
    StringBuffer s = new StringBuffer("\n");
    appendCmd(s, "draw delete");
    modelCount = viewer.getModelCount();
    for (int i = 0; i < meshCount; i++) {
      DrawMesh mesh = dmeshes[i];
      if (mesh.vertexCount == 0 && mesh.lineData == null)
        continue;
      s.append(getDrawCommand(mesh, mesh.modelIndex));
      if (!mesh.visible)
        s.append(" draw " + mesh.thisID + " off;\n");
    }
    return s.toString();
  }

  static Point3f randomPoint() {
    return new Point3f((float) Math.random(), (float) Math.random(), (float) Math.random());
  }

}
