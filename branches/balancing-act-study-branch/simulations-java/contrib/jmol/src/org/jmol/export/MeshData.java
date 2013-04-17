package org.jmol.export;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.geodesic.Geodesic;
import org.jmol.util.MeshSurface;

/**
 * Class to generate mesh data (faces, vertices, and normals) for several kinds
 * of generic meshes.  This allows the same routines to be used in different
 * exporters and possibly in other places, as well.<br><br>
 * The meshes implemented are circle, cone, cylinder, and sphere.
 */
class MeshData {

  /**
   * This internal class is a container for the return values of the getXxxData
   * methods.
   */
  static class Data {
    private int[][] faces;
    private Tuple3f[] normals;
    private int nVertices;
    private int nNormals;

    /**
     * Constructor.
     * 
     * @param faces
     * @param vertexes
     * @param nVertices TODO
     * @param normals
     * @param nNormals TODO
     */

    /**
     * @return The faces.
     */
    int[][] getFaces() {
      return faces;
    }

    /**
     * @return vertex count
     */
    int getVertexCount() {
      return nVertices;
    }
    
    /**
     * @return vertex count
     */
    int getNormalCount() {
      return nNormals;
    }
    

    /**
     * @return The normals.
     */
    Tuple3f[] getNormals() {
      return normals;
    }
  }

  /**
   * Calculates the data (faces, vertices, normals) for a circle.
   * 
   * @return The data.
   */
  static MeshSurface getCircleData() {
    int ndeg = 10;
    int n = 360 / ndeg;
    int vertexCount = n + 1;
    int[][] faces = new int[n][];
    for (int i = 0; i < n; i++) {
      faces[i] = new int[] { i, (i + 1) % n, n };
    }
    Point3f[] vertexes = new Point3f[vertexCount];
    Point3f[] normals = new Point3f[vertexCount];
    for (int i = 0; i < n; i++) {
      float x = (float) (Math.cos(i * ndeg / 180. * Math.PI));
      float y = (float) (Math.sin(i * ndeg / 180. * Math.PI));
      vertexes[i] = new Point3f(x, y, 0);
      normals[i] = new Point3f(0, 0, 1);
    }
    vertexes[n] = new Point3f(0, 0, 0);
    normals[n] = new Point3f(0, 0, 1);
    return new MeshSurface(faces, vertexes, 0, normals, 0);
  }

  /**
   * Calculates the data (faces, vertices, normals) for a triangle.
   * 
   * @param pt1 Vertex 1.
   * @param pt2 Vertex 2.
   * @param pt3 Vertex 3.
   * @return The data.
   */
  static MeshSurface getTriangleData(Point3f pt1, Point3f pt2,
                                              Point3f pt3) {
    Point3f[] vertexes = new Point3f[] { pt1, pt2, pt3 };
    Vector3f v1 = new Vector3f();
    Vector3f v2 = new Vector3f();
    v1.set(pt3);
    v1.sub(pt1);
    v2.set(pt2);
    v2.sub(pt1);
    v2.cross(v2, v1);
    v2.normalize();
    Vector3f[] normals = new Vector3f[] { v2, v2, v2 };
    int[][] faces = { { 0, 1, 2 } };
    return new MeshSurface(faces, vertexes, 0, normals, 0);
  }

  /**
   * Calculates the data (faces, vertices, normals) for a cone.
   * 
   * @return The data.
   */
  static MeshSurface getConeData() {
    int ndeg = 10;
    int n = 360 / ndeg;
    Point3f[] vertices = new Point3f[n + 1];
    int[][] faces = new int[n][];
    for (int i = 0; i < n; i++)
      faces[i] = new int[] { i, (i + 1) % n, n };
    double d = ndeg / 180. * Math.PI;
    for (int i = 0; i < n; i++) {
      float x = (float) (Math.cos(i * d));
      float y = (float) (Math.sin(i * d));
      vertices[i] = new Point3f(x, y, 0);
    }
    vertices[n] = new Point3f(0, 0, 1);
    return new MeshSurface(faces, vertices, 0, vertices, 0);
  }

  /**
   * Calculates the data (faces, vertices, normals) for a cylinder.
   * 
   * @param inSide Whether inside or not.
   * @return The data.
   */
  static MeshSurface getCylinderData(boolean inSide) {
    int ndeg = 10;
    int vertexCount = 360 / ndeg * 2;
    int n = vertexCount / 2;
    int[][] faces = new int[vertexCount][];
    int fpt = -1;
    for (int i = 0; i < n; i++) {
      if (inSide) {
        // Adobe 9 bug: 
        // does not treat normals properly --
        // if you have normals, you should use them to decide
        // which faces to render - but NO, faces are rendered
        // strictly on the basis of windings. What??!

        faces[++fpt] = new int[] { i + n, (i + 1) % n, i };
        faces[++fpt] = new int[] { i + n, (i + 1) % n + n, (i + 1) % n };
      } else {
        faces[++fpt] = new int[] { i, (i + 1) % n, i + n };
        faces[++fpt] = new int[] { (i + 1) % n, (i + 1) % n + n, i + n };
      }
    }
    Point3f[] vertexes = new Point3f[vertexCount];
    Point3f[] normals = new Point3f[vertexCount];
    for (int i = 0; i < n; i++) {
      float x = (float) (Math.cos(i * ndeg / 180. * Math.PI));
      float y = (float) (Math.sin(i * ndeg / 180. * Math.PI));
      vertexes[i] = new Point3f(x, y, 0);
      normals[i] = new Point3f(x, y, 0);
    }
    for (int i = 0; i < n; i++) {
      float x = (float) (Math.cos((i + 0.5) * ndeg / 180 * Math.PI));
      float y = (float) (Math.sin((i + 0.5) * ndeg / 180 * Math.PI));
      vertexes[i + n] = new Point3f(x, y, 1);
      normals[i + n] = normals[i];
    }
    if (inSide)
      for (int i = 0; i < n; i++)
        normals[i].scale(-1);
    return new MeshSurface(faces, vertexes, 0, normals, 0);
  }

  /**
   * Calculates the data (faces, vertices, normals) for a sphere.
   * 
   * @return The data.
   */
  static MeshSurface getSphereData() {
    int vertexCount = Geodesic.getVertexCount(2);
    short[] f = Geodesic.getFaceVertexes(2);
    int nFaces = f.length / 3;
    int[][] faces = new int[nFaces][];
    int fpt = -1;
    for (int i = 0; i < nFaces; i++)
      faces[i] = new int[] { f[++fpt], f[++fpt], f[++fpt] };
    Vector3f[] vertexes = new Vector3f[vertexCount];
    for (int i = 0; i < vertexCount; i++)
      vertexes[i] = Geodesic.getVertexVector(i);
    return new MeshSurface(faces, vertexes, 0, vertexes, 0);
  }

}
