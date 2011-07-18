package org.jmol.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.BoxInfo;

public class MeshSurface {

  protected static final int SEED_COUNT = 25;
  public boolean haveQuads;
  public short colix;
  public boolean isColorSolid = true;
  public int vertexCount;
  public Point3f[] vertices;
  public Point3f offset;
  public Tuple3f[] altVertices;
  public short[] vertexColixes;
  public int polygonCount;
  public int[][] polygonIndexes;
  public short[] polygonColixes;
  public Tuple3f[] normals;
  public int normalCount;
  public BitSet bsPolygons;
  public Point3f ptOffset;
  public float scale3d;
  public float[] vertexValues;
  public BitSet[] surfaceSet;
  public int[] vertexSets;
  public int nSets = 0;
  public int checkCount = 2;

  

  public MeshSurface() {
  }
  
  public MeshSurface(int[][] polygonIndexes, Tuple3f[] vertices, int nVertices,
      Tuple3f[] normals, int nNormals) {
    this.polygonIndexes = polygonIndexes;
    if (vertices instanceof Point3f[])
      this.vertices = (Point3f[]) vertices;
    else
      this.altVertices = vertices;
    this.vertexCount = (nVertices == 0 ? vertices.length : nVertices);
    this.normals = normals;
    this.normalCount = (nNormals == 0  && normals != null ? normals.length : nNormals);
  }
  
  public MeshSurface(Point3f[] vertices, float[] vertexValues, int vertexCount,
      int[][] polygonIndexes, int polygonCount, int checkCount) {
    this.vertices = vertices;
    this.vertexValues = vertexValues;
    this.vertexCount = vertexCount;
    this.polygonIndexes = polygonIndexes;
    this.polygonCount = polygonCount;
    this.checkCount = checkCount;
  }

  /**
   * @return The vertices.
   */
  public Tuple3f[] getVertices() {
    return (altVertices == null ? vertices : altVertices);
  }
  
  /**
   * @return  faces, if defined (in exporter), otherwise polygonIndexes
   */
  public int[][] getFaces() {
    return polygonIndexes;
  }

  public void setColix(short colix) {
    this.colix = colix;
  }

  public int addVertexCopy(Point3f vertex) { //used by mps and surfaceGenerator
    if (vertexCount == 0)
      vertices = new Point3f[SEED_COUNT];
    else if (vertexCount == vertices.length)
      vertices = (Point3f[]) ArrayUtil.doubleLength(vertices);
    vertices[vertexCount] = new Point3f(vertex);
    return vertexCount++;
  }

  public void addTriangle(int vertexA, int vertexB, int vertexC) {
    addPolygon(new int[] { vertexA, vertexB, vertexC });
  }

  public void addQuad(int vertexA, int vertexB, int vertexC, int vertexD) {
    haveQuads = true;
    addPolygon(new int[] { vertexA, vertexB, vertexC, vertexD });
  }

  protected int addPolygon(int[] polygon) {
    int n = polygonCount;
    if (polygonCount == 0)
      polygonIndexes = new int[SEED_COUNT][];
    else if (polygonCount == polygonIndexes.length)
      polygonIndexes = (int[][]) ArrayUtil.doubleLength(polygonIndexes);
    polygonIndexes[polygonCount++] = polygon;
    return n;
  }

  public void setPolygonCount(int polygonCount) {    
    this.polygonCount = polygonCount;
    if (polygonCount < 0)
      return;
    if (polygonIndexes == null || polygonCount > polygonIndexes.length)
      polygonIndexes = new int[polygonCount][];
  }

  public int addVertexCopy(Point3f vertex, float value) {
    if (vertexCount == 0)
      vertexValues = new float[SEED_COUNT];
    else if (vertexCount >= vertexValues.length)
      vertexValues = ArrayUtil.doubleLength(vertexValues);
    vertexValues[vertexCount] = value;
    return addVertexCopy(vertex);
  } 

  public int addTriangleCheck(int vertexA, int vertexB, int vertexC, int check,
                              int check2, int color) {
    return (vertices == null
        || vertexValues != null
        && (Float.isNaN(vertexValues[vertexA])
            || Float.isNaN(vertexValues[vertexB]) 
            || Float.isNaN(vertexValues[vertexC])) 
        || Float.isNaN(vertices[vertexA].x)
        || Float.isNaN(vertices[vertexB].x) 
        || Float.isNaN(vertices[vertexC].x) 
        ? -1 
      : checkCount == 2 ? addPolygon(new int[] { vertexA, vertexB, vertexC, check, check2 },
        color)
      : addPolygon(new int[] { vertexA, vertexB, vertexC, check }));
  }

  private int lastColor;
  private short lastColix;
    
  private int addPolygon(int[] polygon, int color) {
    if (color != 0) {
      if (polygonColixes == null || polygonCount == 0)
        lastColor = 0;
      short colix = (color == lastColor ? lastColix : (lastColix = Graphics3D
          .getColix(lastColor = color)));
      setPolygonColix(polygonCount, colix);
    }
    return addPolygon(polygon);
  }

  private void setPolygonColix(int index, short colix) {
    if (polygonColixes == null) {
      polygonColixes = new short[SEED_COUNT];
    } else if (index == polygonColixes.length) {
      polygonColixes = ArrayUtil.doubleLength(polygonColixes);
    }
    polygonColixes[index] = colix;
  }
  
  public void invalidatePolygons() {
    for (int i = polygonCount; --i >= 0;)
      if (!setABC(i))
        polygonIndexes[i] = null;
  }

  protected int iA, iB, iC;
  
  protected boolean setABC(int i) {
    int[] vertexIndexes = polygonIndexes[i];
    return vertexIndexes != null
          && !(Float.isNaN(vertexValues[iA = vertexIndexes[0]])
            || Float.isNaN(vertexValues[iB = vertexIndexes[1]]) 
            || Float.isNaN(vertexValues[iC = vertexIndexes[2]]));
  }

  public void slabPolygons(Object slabbingObject, boolean andCap) {
    if (slabbingObject instanceof Point4f) {
      getIntersection((Point4f) slabbingObject, null, 0, null, andCap, false);
      return;
    }
    if (slabbingObject instanceof Point3f[]) {
      Point4f[] faces = BoxInfo.getFacesFromCriticalPoints((Point3f[]) slabbingObject);
      for (int i = 0; i < faces.length; i++)
        getIntersection(faces[i], null, 0, null, andCap, false);
      return; 
    }
    if (slabbingObject instanceof Object[]) {
      Object[] o = (Object[]) slabbingObject;
      float distance = ((Float) o[0]).floatValue();
      Point3f center = (Point3f) o[1];
      getIntersection(null, center, distance, null, andCap, false);
    }
  }

  public boolean getIntersection(Point4f plane, Point3f ptCenter,
                                 float distance, List<Point3f[]> vData,
                                 boolean andCap, boolean doClean) {
    boolean isSlab = (vData == null);
    if (ptCenter != null)
      andCap = false; // can only cap faces
    Point3f[] pts;
    int iD, iE;
    double absD = Math.abs(distance);
    List<int[]> iPts = (andCap ? new ArrayList<int[]>() : null);
    for (int i = polygonIndexes.length; --i >= 0;) {
      if (!setABC(i))
        continue;
      int check1 = polygonIndexes[i][3];
      int check2 = (checkCount == 2 ? polygonIndexes[i][4] : 0);
      Point3f vA = vertices[iA];
      Point3f vB = vertices[iB];
      Point3f vC = vertices[iC];
      float d1, d2, d3;
      if (ptCenter == null) {
        d1 = Measure.distanceToPlane(plane, vA);
        d2 = Measure.distanceToPlane(plane, vB);
        d3 = Measure.distanceToPlane(plane, vC);
      } else if (distance < 0) {
        d1 = -vA.distance(ptCenter) - distance;
        d2 = -vB.distance(ptCenter) - distance;
        d3 = -vC.distance(ptCenter) - distance;
      } else {
        d1 = vA.distance(ptCenter) - distance;
        d2 = vB.distance(ptCenter) - distance;
        d3 = vC.distance(ptCenter) - distance;
      }
      int test1 = (d1 < 0 ? 1 : 0) + (d2 < 0 ? 2 : 0) + (d3 < 0 ? 4 : 0);
      int test2 = (d1 >= 0 ? 1 : 0) + (d2 >= 0 ? 2 : 0) + (d3 >= 0 ? 4 : 0);
      
      pts = null;
      switch (test1) {
      case 0:
      case 7:
        // all on the same side
        break;
      case 1:
      case 6:
        // BC on same side
        if (ptCenter == null)
          pts = new Point3f[] { interpolatePoint(vA, vB, -d1, d2),
              interpolatePoint(vA, vC, -d1, d3) };
        else
          pts = new Point3f[] { interpolateSphere(vA, vB, -d1, -d2, absD),
              interpolateSphere(vA, vC, -d1, -d3, absD) };
        break;
      case 2:
      case 5:
        //AC on same side
        if (ptCenter == null)
          pts = new Point3f[] { interpolatePoint(vB, vA, -d2, d1),
              interpolatePoint(vB, vC, -d2, d3) };
        else
          pts = new Point3f[] { interpolateSphere(vB, vA, -d2, -d1, absD),
              interpolateSphere(vB, vC, -d2, -d3, absD) };
        break;
      case 3:
      case 4:
        //AB on same side need A-C, B-C
        if (ptCenter == null)
          pts = new Point3f[] { interpolatePoint(vC, vA, -d3, d1),
              interpolatePoint(vC, vB, -d3, d2) };
        else
          pts = new Point3f[] { interpolateSphere(vC, vA, -d3, -d1, absD),
              interpolateSphere(vC, vB, -d3, -d2, absD) };
        break;
      }
      if (isSlab) {
        iD = iE = -1;
        //             A
        //            / \
        //           B---C
        switch (test2) {
        case 0:
          // all on the same side
          continue;
        case 7:
          // all on the same side
          break;
        case 1:
          // BC on side to keep
          //TODO: must set edges for doClean
          if (false && doClean) {
            if (pts[0].distance(vA) < 0.01f  || pts[1].distance(vA) < 0.01f)              
              continue;
            if (pts[0].distance(vB) < 0.01f) {
              iD = iB;
            }
            if (pts[1].distance(vC) < 0.01f) {
              iE = iC;
            }
            if (iD >= 0 && iE >= 0)
              continue;
          }
          if (iE < 0) {
            iE = addVertexCopy(pts[0], vertexValues[iA]); //AB
            addTriangleCheck(iE, iB, iC, check1 & 3, check2, 0);            
          }
          if (iD < 0) {
            iD = addVertexCopy(pts[1], vertexValues[iA]); //AC
            addTriangleCheck(iD, iE, iC, check1 & 4 | 1, check2, 0);
          }
          break;
        case 2:
          // AC on side to keep
          if (false && doClean) {
            if (pts[0].distance(vB) < 0.01f  || pts[1].distance(vB) < 0.01f)              
              continue;
            if (pts[0].distance(vA) < 0.01f) {
              iD = iA;
            }
            if (pts[1].distance(vC) < 0.01f) {
              iE = iC;
            }
            if (iD >= 0 && iE >= 0)
              continue;
          }
          if (iD < 0) {
            iD = addVertexCopy(pts[0], vertexValues[iB]); //AB
            addTriangleCheck(iA, iD, iC, check1 & 5, check2, 0);
          }
          if (iE < 0) {
            iE = addVertexCopy(pts[1], vertexValues[iB]); //BC
            addTriangleCheck(iD, iE, iC, check1 & 2 | 1, check2, 0);
          }
          break;
        case 3:
          //AB on side to toss
          iD = addVertexCopy(pts[0], vertexValues[iA]); //AC
          iE = addVertexCopy(pts[1], vertexValues[iB]); //BC
          addTriangleCheck(iD, iE, iC, check1 & 6 | 1, check2, 0);
          break;
        case 4:
          //AB on side to keep
          if (false && doClean) {
            if (pts[0].distance(vC) < 0.01f  || pts[1].distance(vC) < 0.01f)              
              continue;
            if (pts[0].distance(vA) < 0.01f) {
              iD = iA;
            }
            if (pts[1].distance(vB) < 0.01f) {
              iE = iB;
            }
            if (iD >= 0 && iE >= 0)
              continue;
          }
          if (iE < 0) {
            iE = addVertexCopy(pts[0], vertexValues[iC]); //AC
            addTriangleCheck(iA, iB, iE, check1 & 5, check2, 0);
          }
          if (iD < 0) {
            iD = addVertexCopy(pts[1], vertexValues[iC]); //BC
            addTriangleCheck(iE, iB, iD, check1 & 2 | 4, check2, 0);
          }
          break;
        case 5:
          //AC on side to toss
          iD = addVertexCopy(pts[1], vertexValues[iC]); //BC
          iE = addVertexCopy(pts[0], vertexValues[iA]); //AB
          addTriangleCheck(iE, iB, iD, check1 & 3 | 4, check2, 0);
          break;
        case 6:
          // BC on side to toss
          if (doClean && 
              (pts[0].distance(vA) < 0.01f  || pts[1].distance(vA) < 0.01f))              
            continue;
          iD = addVertexCopy(pts[0], vertexValues[iB]); //AB
          iE = addVertexCopy(pts[1], vertexValues[iC]); //AC
          addTriangleCheck(iA, iD, iE, check1 & 5 | 2, check2, 0);
          break;
        }
        //if (test1 != 0 && test1 != 7)      System.out.println("meshsurface d1 d2 d3 " + d1 + "\t" + d2 + "\t" + d3);
        polygonIndexes[i] = null;
        if (andCap && iD > 0)
          iPts.add(new int[] { iD, iE });
      } else if (pts != null) {
        vData.add(pts);
      }
    }
    if (andCap && iPts.size() > 0) {
      Point3f center = new Point3f();
      for (int i = iPts.size(); --i >= 0;) {
        int[] ipts = iPts.get(i);
        center.add(vertices[ipts[0]]);
        center.add(vertices[ipts[1]]);
      }
      center.scale(0.5f / iPts.size());
      int v0 = addVertexCopy(center);
      for (int i = iPts.size(); --i >= 0;) {
        int[] ipts = iPts.get(i);
        iD = addVertexCopy(vertices[ipts[0]], vertexValues[ipts[0]]);
        iE = addVertexCopy(vertices[ipts[1]], vertexValues[ipts[1]]);
        addTriangleCheck(iD, v0, iE, 0, 0, 0);
      }
    }
    if (!doClean)
      return false;
    BitSet bsv = new BitSet();
    BitSet bsp = new BitSet();
    for (int i = 0; i < polygonCount; i++) {
      if (polygonIndexes[i] == null)
        continue;
      bsp.set(i);
      for (int j = 0; j < 3; j++)
        bsv.set(polygonIndexes[i][j]);
    }
    int n = 0;
    int nPoly = bsp.cardinality();
    if (nPoly != polygonCount) {
      int[] map = new int[vertexCount];
      for (int i = 0; i < vertexCount; i++)
        if (bsv.get(i))
          map[i] = n++;
      Point3f[] vTemp = new Point3f[n];
      n = 0;
      for (int i = 0; i < vertexCount; i++)
        if (bsv.get(i))
          vTemp[n++] = vertices[i];
      int[][] pTemp = new int[nPoly][];
      nPoly = 0;
      for (int i = 0; i < polygonCount; i++)
        if (polygonIndexes[i] != null) {
          for (int j = 0; j < 3; j++)
            polygonIndexes[i][j] = map[polygonIndexes[i][j]];
          pTemp[nPoly++] = polygonIndexes[i];
        }
      vertices = vTemp;
      vertexCount = n;
      polygonIndexes = pTemp;
      polygonCount = nPoly;
    }
    return false;
  }

  private Point3f interpolateSphere(Point3f v1, Point3f v2, float d1, float d2,
                                    double absD) {
    return interpolateFraction(v1, v2, getSphericalInterpolationFraction(absD, d1,
        d2, v1.distance(v2)));
  }

  private static Point3f interpolatePoint(Point3f v1, Point3f v2, float d1, float d2) {
    return interpolateFraction(v1, v2, d1 / (d1 + d2));
  }

  private static Point3f interpolateFraction(Point3f v1, Point3f v2, float f) {
    if (f < 0.0001)
      f = 0;
    else if (f > 0.9999)
      f = 1;
    return new Point3f(v1.x + (v2.x - v1.x) * f, 
        v1.y + (v2.y - v1.y) * f, 
        v1.z + (v2.z - v1.z) * f);
  }

  public static float getSphericalInterpolationFraction(double r, double valueA,
                                                      double valueB, double d) {
    double ra = Math.abs(r + valueA) / d;
    double rb = Math.abs(r + valueB) / d;
    r /= d;
    double ra2 = ra * ra;
    double q = ra2 - rb * rb + 1;
    double p = 4 * (r * r - ra2);
    double factor = (ra < rb ? 1 : -1);
    return (float) (((q) + factor * Math.sqrt(q * q + p)) / 2);
  }

}
