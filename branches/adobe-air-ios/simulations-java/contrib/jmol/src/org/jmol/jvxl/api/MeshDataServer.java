package org.jmol.jvxl.api;

import java.io.OutputStream;
import java.util.BitSet;

import javax.vecmath.Point3f;

import org.jmol.jvxl.data.MeshData;
import org.jmol.shapesurface.IsosurfaceMesh;
import org.jmol.util.BinaryDocument;



public interface MeshDataServer extends VertexDataServer {
  
  /*
   * An interface for interacting with 
   * the MarchingCubes and MarchingSquares classes 
   * as well as the SurfaceReader classes
   * during and after surface generation
   * 
   * Isosurface is an example.
   * 
   * SurfaceReader accepts vertexes from MarchingCubes/MarchingSquares
   * and then either consumes them or passes them on to Isosurface.
   * 
   * In addition, MeshData information is passed back and forth
   * via this mechanism.
   * 
   * This is crude. I would like to do it better.
   * 
   * Bob Hanson 20 Apr 2007
   * 
   */
  
  public abstract void invalidateTriangles();
  public abstract void fillMeshData(MeshData meshData, int mode, IsosurfaceMesh mesh);
  public abstract void notifySurfaceGenerationCompleted();
  public abstract void notifySurfaceMappingCompleted();
  public abstract Point3f[] calculateGeodesicSurface(BitSet bsSelected, float envelopeRadius);
  public abstract void addRequiredFile(String fileName);
  public abstract void setOutputStream(BinaryDocument binaryDoc, OutputStream os);
}
