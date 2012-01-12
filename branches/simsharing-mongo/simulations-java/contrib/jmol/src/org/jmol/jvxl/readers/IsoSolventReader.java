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

//import java.util.ArrayList;
import java.util.ArrayList; //import java.util.Arrays;
import java.util.BitSet; //import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.util.BitSetUtil;
import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.util.MeshSurface;

import org.jmol.api.AtomIndexIterator;
import org.jmol.jvxl.data.MeshData;

class IsoSolventReader extends AtomDataReader {

  IsoSolventReader(SurfaceGenerator sg) {
    super(sg);
  }

  ///// solvent-accessible, solvent-excluded surface //////

  /*
   * isosurface SOLVENT 1.4; isosurface MOLECULAR
   * 
   * Prior to Jmol 12.1.29, all isosurface SOLVENT/MOLECULAR calculations
   * only checked for pairs of atoms in calculating troughs. This was
   * not satisfactory; a full analysis of molecular surfaces
   * requires that the "ball" rolling around a pair of atoms 
   * may hit a third atom. If this is the case, then the surface area will
   * be somewhat less, and the valleys produced will be shallower.
   * 
   * Starting with Jmol 12.1.29, we take a new approach -- a modified MSMS
   * algorithm based loosely on:
   *    
   *    Sanner, M.F., Spehner, J.-C., and Olson, A.J. (1996) 
   *    Reduced surface: an efficient way to compute molecular surfaces. 
   *    Biopolymers, Vol. 38., (3), 305-320.
   *    
   * I have no idea how the MSMS program actually works; all I have is what
   * is published in the above account. 
   * 
   * Similarly to this algorithm, we catalog edges (two-point contacts) and
   * faces (three-point contacts). However, we never calculate a "reduced surface"
   * and we never associate specific triangulation vertices with specific atoms.
   * Instead, we generate a field of values that measure the closest distance to
   * the surface for a grid of points. 
   * 
   * Using a novel nonlinear Marching Cubes algorithm, we use this set of values
   * to generate exact positions of points on the surface. 
   * 
   * A bonus is that we can calculate interior cavities automatically at the same time. 
   * (They are inside-out and have negative volume. They can be visualized using
   * the SET option of the ISOSURFACE command.)
   * 
   * We can also calculate fragments of isosurfaces as well as external cavities. 
   * 
   * The calculation is quite fast. Note that for comparison with MSMS,
   * you will need to generate a .xyzrn file for MSMS input. This can be
   * done using the writeXyzrn script found in the drawMsMs.spt script:
   * 
   * http://chemapps.stolaf.edu/jmol/docs/examples-12/drawMsMs.spt
   * 
   * That script also includes  drawMsMs(fileroot), which draws the resulting
   * isosurface from MsMs in red (sphere), white (face), and blue (toroidal).
   * 
   * Bob Hanson, 11/31/2010
   *
   * 
   * The surface fragment idea:
   * 
   * ISOSURFACE solvent|sasurface both work on the SELECTED atoms, thus
   * allowing for a subset of the molecule to be involved. But in that
   * case we don't want to be creating a surface that goes right through
   * another atom. Rather, what we want (probably) is just the portion
   * of the OVERALL surface that involves these atoms. 
   * 
   * The addition of Mesh.voxelValue[] means that we can specify any 
   * voxel we want to NOT be excluded (NaN). Here we first exclude any 
   * voxel that would have been INSIDE a nearby atom. This will take care
   * of any portion of the van der Waals surface that would be there. Then
   * we exclude any special-case voxel that is between two nearby atoms. 
   *  
   *  Bob Hanson 13 Jul 2006
   *     
   * Jmol cavity rendering. Tim Driscoll suggested "filling a 
   * protein with foam. Here you go...
   * 
   * 1) Use a dot-surface extended x.xx Angstroms to define the 
   *    outer envelope of the protein.
   * 2) Identify all voxel points outside the protein surface (v > 0) 
   *    but inside the envelope (nearest distance to a dot > x.xx).
   * 3) First pass -- create the protein surface.
   * 4) Replace solvent atom set with "foam" ball of the right radius
   *    at the voxel vertex points.
   * 5) Run through a second time using these "atoms" to generate 
   *    the surface around the foam spheres. 
   *    
   *    Bob Hanson 3/19/07
   * 
   */

  private float cavityRadius;
  private float envelopeRadius;
  private Point3f[] dots;

  private boolean doCalculateTroughs;
  private boolean isCavity, isPocket;
  protected float solventRadius;
  private boolean havePlane;
  final private Point3f ptXyzTemp = new Point3f();
  private AtomIndexIterator iter;
  private float[] voxelRadii;
  private BitSet bsSurfaceVoxels, bsSurfacePoints, bsSurfaceDone;
  private BitSet[] bsLocale;
  private Map<String, Edge> htEdges;
  private List<Edge> vEdges;
  private Edge[] aEdges;
  private List<Face> vFaces;
  private BitSet validSpheres, noFaceSpheres;
  protected Vector3f vTemp = new Vector3f();
  protected Point4f plane = new Point4f();

  protected Point3f ptTemp2 = new Point3f();
  private Point3f ptS1 = new Point3f();
  private Point3f ptS2 = new Point3f();
  protected Vector3f vTemp2 = new Vector3f();
  private Vector3f vTemp3 = new Vector3f();
  private float dPX;
  final protected Point3f p = new Point3f();
  private float maxRadius;

  @Override
  protected void setup() {
    super.setup();
    cavityRadius = params.cavityRadius;
    envelopeRadius = params.envelopeRadius;
    solventRadius = params.solventRadius;
    point = params.point;

    isCavity = (params.isCavity && meshDataServer != null); // Jvxl cannot do this calculation on its own.
    isPocket = (params.pocket != null && meshDataServer != null);
    havePlane = (params.thePlane != null);

    doCalculateTroughs = (atomDataServer != null && !isCavity // Jvxl needs an atom iterator to do this.
        && solventRadius > 0 && (dataType == Parameters.SURFACE_SOLVENT || dataType == Parameters.SURFACE_MOLECULAR));
    doUseIterator = doCalculateTroughs;
    getAtoms(Float.NaN, false, true);
    if (isCavity || isPocket)
      dots = meshDataServer.calculateGeodesicSurface(bsMySelected,
          envelopeRadius);

    setHeader("solvent/molecular surface", params.calculationType);
    setRangesAndAddAtoms(params.solvent_ptsPerAngstrom, params.solvent_gridMax,
        params.thePlane != null ? Integer.MAX_VALUE : Math.min(firstNearbyAtom,
            100));
    if (bsNearby != null)
      bsMySelected.or(bsNearby);

  }

  //////////// meshData extensions ////////////

  @Override
  protected void generateCube() {
    // This is the starting point for the calculation.
    volumeData.getYzCount();
    if (isCavity && params.theProperty != null)
      return;
    Logger.startTimer();
    getMaxRadius();
    if (isCavity && dataType != Parameters.SURFACE_NOMAP
        && dataType != Parameters.SURFACE_PROPERTY) {
      volumeData.voxelData = voxelData = new float[nPointsX][nPointsY][nPointsZ];
      resetVoxelData(Float.MAX_VALUE);
      markSphereVoxels(cavityRadius, params.distance);
      generateSolventCavity();
      resetVoxelData(Float.MAX_VALUE);
      markSphereVoxels(0, Float.NaN);
    } else {
      generateSolventCube();
    }
    unsetVoxelData();
    if (params.cappingObject instanceof Point4f) {
      volumeData.capData((Point4f) params.cappingObject, params.cutoff);
      params.cappingObject = null;
    }
    Logger.checkTimer("solvent surface time");
  }

  private boolean isSurfacePoint;
  private static boolean testLinear = false;

  /**
   * 
   * TEST: alternative EXACT position of fraction for spherical MarchingCubes
   * FOR: ttest.xyz:
  2
  isosurface molecular test showing discontinuities
  C -2.70 0 0
  C 2.75 0 0 
   *
   * RESULT:
   * 
   *  LINEAR (points slightly within R):
  
  $ isosurface resolution 5 volume area solvent 1.5 full
  isosurface1 created with cutoff=0.0; number of isosurfaces = 1
  isosurfaceArea = [75.06620391572324]
  isosurfaceVolume = [41.639681683494324]
  
   *  NONLINEAR:
  
  $ isosurface resolution 5 volume area solvent 1.5 full
  isosurface1 created with cutoff=0.0; number of isosurfaces = 1
  isosurfaceArea = [75.11873783245028]
  isosurfaceVolume = [41.727027252180655]
  
   * MSMS:
  
  msms -if ttest.xyzrn -of ttest -density 5
  
  MSMS 2.6.1 started on Local PC
  Copyright M.F. Sanner (1994)
  Compilation flags
  INPUT  ttest.xyzrn 2 spheres 0 collision only, radii  1.700 to  1.700
  PARAM  Probe_radius  1.500 density  5.000 hdensity  3.000
    Couldn't find first face trying -all option
  ANALYTICAL SURFACE AREA :
    Comp. probe_radius,   reent,    toric,   contact    SES       SAS
      0       1.500       0.000     8.144    67.243    75.387   238.258
  NUMERICAL VOLUMES AND AREA
    Comp. probe_radius SES_volume SES_area)
       0      1.50       40.497     74.132
    Total ses_volume:    40.497
  MSMS terminated normally
  
   * CONCLUSION: 
   * 
   * -- surfaces are essentially identical
   * -- nonlinear is slightly closer to analytical area (75.387), as expected
   * -- both are better than MSMS triangulation for same "resolution":
   *    prog  parameters        %Error
   *    MSMS  -density 5         1.66% (1412 faces)
   *    MSMS  -density 20        0.36% (2968 faces) 
   *    JMOL LINEAR resol 5      0.42% (2720 faces) 
   *    JMOL NONLINEAR resol 5   0.32% (2720 faces)
   * -- Marching Cubes is slightly improved using nonlinear calc.  
   * 
   * @param cutoff
   * @param isCutoffAbsolute
   * @param valueA
   * @param valueB
   * @param pointA
   * @param edgeVector
   * @param fReturn
   * @param ptReturn
   * @return          fractional distance from A to B
   */
  @Override
  protected float getSurfacePointAndFraction(float cutoff,
                                             boolean isCutoffAbsolute,
                                             float valueA, float valueB,
                                             Point3f pointA,
                                             Vector3f edgeVector, int x, int y,
                                             int z, int vA0, int vB0,
                                             float[] fReturn, Point3f ptReturn) {

    // nonlinear Marching Cubes -- hansonr@stolaf.edu 12/31/2010

    // Generates exact position of circular arc based on two distances.
    // Only slightly different from linear Marching Cubes.
    // Uses a stored radius (-r for solvent radius; +r for atom)
    // associated with each voxel point. The algorithm then does
    // an exact positioning of the fractional distance based on cosine law:
    // 
    // dAS^2 + dAB^2 + 2(dAS)(dAB)cos(theta) = dBS^2
    // dAS^2 + dAX^2 + 2(dAS)(dAX)cos(theta) = dXS^2
    //
    //            B
    //           /|
    //          / |
    //         /  |
    //        S---X
    //         \  |
    //          \theta
    //           \|
    //            A
    //
    // So from this we can derive dAX, and thus f = dAX/dAB
    //
    // I don't know of any published report of doing this.
    //
    // Bob Hanson, 12/31/2010

    int vA = marchingCubes.getLinearOffset(x, y, z, vA0);
    int vB = marchingCubes.getLinearOffset(x, y, z, vB0);
    isSurfacePoint = (bsSurfaceVoxels != null && (bsSurfaceVoxels.get(vA) || bsSurfaceVoxels
        .get(vB)));

    if (testLinear || voxelRadii == null || voxelRadii[vA] == 0
        || voxelRadii[vA] != voxelRadii[vB])
      return super.getSurfacePointAndFraction(cutoff, isCutoffAbsolute, valueA,
          valueB, pointA, edgeVector, x, y, z, vA, vB, fReturn, ptReturn);
    float fraction = fReturn[0] = MeshSurface
        .getSphericalInterpolationFraction(voxelRadii[vA], valueA, valueB,
            edgeVector.length());
    ptReturn.scaleAdd(fraction, edgeVector, pointA);
    float diff = valueB - valueA;
    /*
    //for testing -- linear with ttest.xyz: 
    // float fractionLinear = (cutoff - valueA) / diff;
      sg.log(x + "\t" + y + "\t" + z + "\t" 
        + volumeData.getPointIndex(x, y, z) + "\t" 
        + vA + "\t" + vB + "\t" 
        + valueA + "\t" + valueB + "\t" 
        + fractionLinear + "\t" + fraction + "\td=" + ptReturn.distance(new Point3f(2.75f, 0, 0)));
    */
    return valueA + fraction * diff;
  }

  @Override
  public int addVertexCopy(Point3f vertexXYZ, float value, int assocVertex) {
    // Boolean isSurfacePoint has been set in getSurfacePointAndFraction.
    // We use it to identify all points derived from (a) all +F and (b)
    // all -S voxels for atoms that are not related to a face. 
    int i = super.addVertexCopy(vertexXYZ, value, assocVertex);
    if (isSurfacePoint && i >= 0)
      bsSurfacePoints.set(i);
    return i;
  }

  @Override
  public void selectPocket(boolean doExclude) {
    if (meshDataServer != null)
      meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_VERTICES, null);
    //mark VERTICES for proximity to surface
    Point3f[] v = meshData.vertices;
    int nVertices = meshData.vertexCount;
    float[] vv = meshData.vertexValues;
    int nDots = dots.length;
    for (int i = 0; i < nVertices; i++) {
      for (int j = 0; j < nDots; j++) {
        if (dots[j].distance(v[i]) < envelopeRadius) {
          vv[i] = Float.NaN;
          continue;
        }
      }
    }
    meshData.getSurfaceSet();
    int nSets = meshData.nSets;
    BitSet pocketSet = new BitSet(nSets);
    BitSet ss;
    for (int i = 0; i < nSets; i++)
      if ((ss = meshData.surfaceSet[i]) != null)
        for (int j = ss.nextSetBit(0); j >= 0; j = ss.nextSetBit(j + 1))
          if (Float.isNaN(meshData.vertexValues[j])) {
            pocketSet.set(i);
            //System.out.println("pocket " + i + " " + j + " " + surfaceSet[i]);
            break;
          }
    //now clear all vertices that match the pocket toggle
    //"POCKET"   --> pocket TRUE means "show just the pockets"
    //"INTERIOR" --> pocket FALSE means "show everything that is not a pocket"
    for (int i = 0; i < nSets; i++)
      if (meshData.surfaceSet[i] != null && pocketSet.get(i) == doExclude)
        meshData.invalidateSurfaceSet(i);
    updateSurfaceData();
    if (!doExclude)
      meshData.surfaceSet = null;
    if (meshDataServer != null) {
      meshDataServer.fillMeshData(meshData, MeshData.MODE_PUT_SETS, null);
      meshData = new MeshData();
    }
  }

  @Override
  protected void postProcessVertices() {
    // Here we identify the actual surface set and cull out the other fragments
    // created when the toroidal surfaces are split by faces.
    if (doCalculateTroughs && bsSurfacePoints != null) {
      if (meshDataServer != null)
        meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_VERTICES, null);
      BitSet[] bsSurfaces = meshData.getSurfaceSet();
      for (int i = 0; i < meshData.nSets; i++)
        if (!bsSurfaces[i].intersects(bsSurfacePoints))
          meshData.invalidateSurfaceSet(i);
      updateSurfaceData();
      if (meshDataServer != null) {
        meshDataServer.fillMeshData(meshData, MeshData.MODE_PUT_SETS, null);
        meshData = new MeshData();
      }
    }
  }

  /////////////// calculation methods //////////////

  private void generateSolventCavity() {
    //we have a ring of dots around the model.
    //1) identify which voxelData points are > 0 and within this volume
    //2) turn these voxel points into atoms with given radii
    //3) rerun the calculation to mark a solvent around these!
    BitSet bs = new BitSet(nPointsX * nPointsY * nPointsZ);
    int i = 0;
    int nDots = dots.length;
    int n = 0;
    float d;
    float r2 = envelopeRadius;// - cavityRadius;

    for (int x = 0; x < nPointsX; ++x)
      for (int y = 0; y < nPointsY; ++y) {
        out: for (int z = 0; z < nPointsZ; ++z, ++i)
          if ((d = voxelData[x][y][z]) < Float.MAX_VALUE && d >= cavityRadius) {
            volumeData.voxelPtToXYZ(x, y, z, ptXyzTemp);
            for (int j = 0; j < nDots; j++) {
              if (dots[j].distance(ptXyzTemp) < r2)
                continue out;
            }
            bs.set(i);
            n++;
          }
      }
    Logger.info("cavities include " + n + " voxel points");
    atomRadius = new float[n];
    atomXyz = new Point3f[n];
    for (int x = 0, ipt = 0, apt = 0; x < nPointsX; ++x)
      for (int y = 0; y < nPointsY; ++y)
        for (int z = 0; z < nPointsZ; ++z)
          if (bs.get(ipt++)) {
            volumeData.voxelPtToXYZ(x, y, z, (atomXyz[apt] = new Point3f()));
            atomRadius[apt++] = voxelData[x][y][z];
          }
    myAtomCount = firstNearbyAtom = n;
  }

  private void generateSolventCube() {
    voxelRadii = new float[volumeData.nPoints];
    if (dataType == Parameters.SURFACE_NOMAP)
      return;
    bsSurfaceDone = new BitSet();
    bsSurfaceVoxels = new BitSet();
    bsSurfacePoints = new BitSet();

    if (doCalculateTroughs) {

      // solvent excluded surfaces only

      iter = atomDataServer.getSelectedAtomIterator(bsMySelected, true, false);

      // PHASE I: Construction of the surface edge and face data

      // 1) -- same as MSMS -- get edges
      vEdges = new ArrayList<Edge>();
      bsLocale = new BitSet[myAtomCount];
      htEdges = new Hashtable<String, Edge>();
      getEdges();
      Logger.info(vEdges.size() + " edges");

      // 2) -- as in MSMS BUT get two faces for each atom triple
      // 3) -- check for interference of solvent position with other atoms
      vFaces = new ArrayList<Face>();
      getFaces();
      Logger.info(vFaces.size() + " faces");
      vEdges = null;
      bsLocale = null;
      htEdges = null;

      iter.release();
      iter = null;

      // PHASE II: Creating the voxel grid

      volumeData.voxelData = voxelData = new float[nPointsX][nPointsY][nPointsZ];
      resetVoxelData(Float.MAX_VALUE);

      //                     /
      //  (inside) .... (-) 0  (+) .... (outside)
      //                     \
      //
      // We also identify "must have" voxels (+F and -S) in this phase.

      // 1) -- First pass is to mark "+F" face voxels (just above the surface).
      //       This takes care of all singularities; we KNOW these are exposed 
      //       regions -- they have to be, or else this isn't a valid face.
      markFaceVoxels(true);

      // 2) -- Second pass is to mark -T and +T voxels that fall within 
      //       the specific toroidal range. It is not necessary to 
      //       worry about whether a face take priority or whether 
      //       the toroidal region is inward directed or not. This is
      //       because the faces will snip off interior segments, and 
      //       they will be discarded in post-processing of the surface
      //       fragment sets.
      markToroidVoxels();
      aEdges = null;

      // 3) -- Third pass is to mark "-F" voxels (just below the surface)
      markFaceVoxels(false);
      vFaces = null;
    } else {
      volumeData.voxelData = voxelData = new float[nPointsX][nPointsY][nPointsZ];
      resetVoxelData(Float.MAX_VALUE);
    }

    // 4) -- Final pass for SES and SAS is to mark "-S" (within atom sphere)
    //       and "+S" (just outside the sphere) voxels
    markSphereVoxels(0, doCalculateTroughs ? Float.MAX_VALUE : params.distance);
    noFaceSpheres = null;
    validSpheres = null;
  }

  private void getEdges() {
    /*
     * Collect a bit set for each atom indicating all 
     * other atoms within r1 + 2*solvent_radius + r2
     * 
     */
    for (int iatomA = 0; iatomA < myAtomCount; iatomA++)
      bsLocale[iatomA] = new BitSet();
    float dist2 = solventRadius + maxRadius;
    for (int iatomA = 0; iatomA < myAtomCount; iatomA++) {
      Point3f ptA = atomXyz[iatomA];
      float rA = atomRadius[iatomA] + solventRadius;
      atomDataServer.setIteratorForAtom(iter, atomIndex[iatomA], rA + dist2);
      while (iter.hasNext()) {
        int iB = iter.next();
        int iatomB = myIndex[iB];
        Point3f ptB = atomXyz[iatomB];
        float rB = atomRadius[iatomB] + solventRadius;
        float dAB = ptA.distance(ptB);
        if (dAB >= rA + rB)
          continue;
        Edge edge = new Edge(iatomA, iatomB);
        vEdges.add(edge);
        bsLocale[iatomA].set(iatomB);
        bsLocale[iatomB].set(iatomA);
        htEdges.put(edge.toString(), edge);
      }
    }
  }

  private class Edge {
    int ia, ib;
    int nFaces;
    int nInvalid;

    Edge(int ia, int ib) {
      this.ia = Math.min(ia, ib);
      this.ib = Math.max(ia, ib);
    }

//    private List<Face> aFaces;
    void addFace(Face f) {
      if (f == null) {
        nInvalid++;
        return;
      }
  //    if (aFaces == null)
    //    aFaces = new ArrayList<Face>();
      //aFaces.add(f);
      nFaces++;
    }

    int getType() {
      return (nFaces > 0 ? nFaces : nInvalid > 0 ? -nInvalid : 0);
    }

    /*
        void dump() {
          System.out.println("draw e" + (nTest++) + " @{point"
              + new Point3f(atomXyz[ia]) + "} @{point" + new Point3f(atomXyz[ib])
              + "} color green # " + getType());
          for (int i = 0; i < aFaces.size(); i++)
            aFaces.get(i).dump();
        }
    
   */
    @Override
    public String toString() {
      return ia + "_" + ib;
    }

  }

  protected Edge findEdge(int i, int j) {
    return htEdges.get(i < j ? i + "_" + j : j + "_" + i);
  }

  private class Face {
    int ia, ib, ic;
    boolean isValid;
    Point3f pS; // solvent position
    Edge[] edges = new Edge[3];

    Face(int ia, int ib, int ic, Edge edgeAB, Point3f pS) {
      this.ia = ia;
      this.ib = ib;
      this.ic = ic;
      this.pS = new Point3f(pS);
      edges[0] = edgeAB;
    }

    void setEdges() {
      if (edges[1] == null) {
        edges[1] = findEdge(ib, ic);
        edges[2] = findEdge(ic, ia);
      }
      Face f = (isValid ? this : null);
      for (int k = 0; k < 3; k++)
        edges[k].addFace(f);
    }

    public void dump() {
      Point3f ptA = atomXyz[ia];
      Point3f ptB = atomXyz[ib];
      Point3f ptC = atomXyz[ic];
      String color = "green";
      dumpLine(ptA, ptB, "f", color);
      dumpLine(ptB, ptC, "f", color);
      dumpLine(ptC, ptA, "f", color);
      dumpLine2(pS, ptA, "f", solventRadius, color, "white");
      dumpLine2(pS, ptB, "f", solventRadius, color, "white");
      dumpLine2(pS, ptC, "f", solventRadius, color, "white");
    }
  }

  private void getFaces() {
    /*
     * a) All possible faces are simply derived from ANDing 
     *     the bit sets of all pairs of atoms. Then a quick
     *     calculation of the solvent position determines if they
     *     are really in position or not.
     * b) Trick is to make TWO faces -- one for each direction.
     *     Unlike MSMS, we do not have any need for determining
     *     "THE" face set. It's just not necessary when you
     *     use Marching Cubes. 
     *      
     */
    BitSet bs = new BitSet();
    validSpheres = new BitSet();
    noFaceSpheres = BitSetUtil.setAll(myAtomCount);
    for (int i = vEdges.size(); --i >= 0;) {
      Edge edge = vEdges.get(i);
      int ia = edge.ia;
      int ib = edge.ib;
      bs.clear();
      bs.or(bsLocale[ia]);
      bs.and(bsLocale[ib]);
      for (int ic = bs.nextSetBit(ib + 1); ic >= 0; ic = bs.nextSetBit(ic + 1)) {
        if (getSolventPoints(ia, ib, ic)) {
          noFaceSpheres.clear(ia);
          noFaceSpheres.clear(ib);
          noFaceSpheres.clear(ic);
          Face f = new Face(ia, ib, ic, edge, ptS1);
          if (validateFace(f)) {
            vFaces.add(f);
          }
          f = new Face(ib, ia, ic, edge, ptS2);
          if (validateFace(f)) {
            vFaces.add(f);
          }
        }
      }
    }
    BitSet bsOK = new BitSet();
    for (int i = vEdges.size(); --i >= 0;)
      if (vEdges.get(i).getType() >= 0)
        bsOK.set(i);
    aEdges = new Edge[bsOK.cardinality()];
    for (int i = bsOK.nextSetBit(0), j = 0; i >= 0; i = bsOK.nextSetBit(i + 1))
      aEdges[j++] = vEdges.get(i);
  }

  private boolean getSolventPoints(int ia, int ib, int ic) {
    /*
     * 
     * A----------p-----B
     *           /|\
     *          / | \ 
     *         /  |  \
     *        S'--X---S  (both in plane perp to vAB through point p)
     *         \  |  / .
     *          \ | /   . rCS
     *           \|/     .
     *            T------C (T is projection of C onto plane perp to vAB)
     *               dCT
     * We want ptS such that 
     *   rAS = rA + rSolvent, 
     *   rBS = rB + rSolvent, and 
     *   rCS = rC + rSolvent
     * 
     * 1) define plane perpendicular to A-B axis and containing ptS
     * 2) project C onto plane as ptT
     * 3) calculate two possible ptS and ptS' in this plane
     * 
     */

    double dPS = getPointP(ia, ib);
    Point3f ptC = atomXyz[ic];
    float rCS = atomRadius[ic] + solventRadius;
    float dCT = Measure.distanceToPlane(plane, ptC);
    if (Math.abs(dCT) >= rCS)
      return false;
    double dST = Math.sqrt(rCS * rCS - dCT * dCT);
    ptTemp.scaleAdd(-dCT, vTemp, ptC);
    double dpT = p.distance(ptTemp);
    float dsp2 = (float) (dPS * dPS);
    double cosTheta = (dsp2 + dpT * dpT - dST * dST) / (2 * dPS * dpT);
    if (Math.abs(cosTheta) >= 1)
      return false;
    Vector3f vXS = vTemp2;
    vXS.set(ptTemp);
    vXS.sub(p);
    vXS.normalize();
    dPX = (float) (dPS * cosTheta);
    ptTemp.scaleAdd(dPX, vXS, p);
    vXS.cross(vTemp, vXS);
    vXS.normalize();
    vXS.scale((float) (Math.sqrt(1 - cosTheta * cosTheta) * dPS));
    ptS1.set(ptTemp);
    ptS1.add(vXS);
    ptS2.set(ptTemp);
    ptS2.sub(vXS);
    return true;
  }

  private boolean validateFace(Face f) {
    /*
     * We must check each solvent position to see if there
     * are any atoms present that would overlap with it. 
     * This is a very quick binary tree search for nearby atoms.
     * 
     * We catalog singularities -- faces for which the perpenticular
     * distance to the solvent atom is less than the solvent radius,
     * but we don't actually use it -- it is just informational.
     * 
     */
    float dist2 = solventRadius + maxRadius;
    atomDataServer.setIteratorForPoint(iter, modelIndex, f.pS, dist2);
    f.isValid = true;
    while (iter.hasNext()) {
      int ia = iter.next();
      int iatom = myIndex[ia];
      if (iatom == f.ia || iatom == f.ib || iatom == f.ic)
        continue;
      float d = atomData.atomXyz[ia].distance(f.pS);
      if (d < atomData.atomRadius[ia] + solventRadius) {
        f.isValid = false;
        break;
      }
    }
    f.setEdges();
    if (!f.isValid)
      return false;
    for (int k = 0; k < 3; k++) {
      validSpheres.set(f.edges[k].ia);
      validSpheres.set(f.edges[k].ib);
    }
    f.edges = null;
    return true;
  }

  private final Point3f ptY0 = new Point3f();
  private final Point3f ptZ0 = new Point3f();
  private final Point3i pt0 = new Point3i();
  private final Point3i pt1 = new Point3i();

  private void markFaceVoxels(boolean firstPass) {
    /*
     * We mark voxels for faces in two passes. In general,
     * we only mark voxels within the trigonal cone formed by the planes
     * ASB, BSC, and CSA (not just within the tetrahedron ABCS).
     * 
     * Pass 1:
     * 
     * In the first pass we are marking outside (+) voxels. The rules are:
     *   (a) If the voxel is overwriting one marked as part of a torus, 
     *       or if it has not been marked yet, then more (-), less (+) is better.
     *   (b) If the voxel is being re-written for this pass, (i.e. in bsDone),
     *       less (-), more (+) is better.
     *   
     * We also take this opportunity to create a bitset for all (+) values,
     * because we need those for identifying the TRUE surface
     *    
     * Pass 2:
     * 
     * In the second pass we are marking inside (-) voxels. The rules are:
     * 
     */
    BitSet bsThisPass = new BitSet();
    for (int fi = vFaces.size(); --fi >= 0;) {
      Face f = vFaces.get(fi);
      if (!f.isValid)
        continue;
      setGridLimitsForAtom(f.pS, solventRadius, pt0, pt1);
      volumeData.voxelPtToXYZ(pt0.x, pt0.y, pt0.z, ptXyzTemp);
      Point3f ptA = atomXyz[f.ia];
      Point3f ptB = atomXyz[f.ib];
      Point3f ptC = atomXyz[f.ic];
      Point3f ptS = f.pS;
      if (Logger.debugging) {
        f.dump();
      }
      // For the second pass (exterior of faces), we track 
      // voxels that have already been over-written by another face.
      // If they have, we go for the more positive one (further out);
      // if not, then we go for the less positive one (further in);
      for (int i = pt0.x; i < pt1.x; i++, ptXyzTemp.scaleAdd(1,
          volumetricVectors[0], ptY0)) {
        ptY0.set(ptXyzTemp);
        for (int j = pt0.y; j < pt1.y; j++, ptXyzTemp.scaleAdd(1,
            volumetricVectors[1], ptZ0)) {
          ptZ0.set(ptXyzTemp);
          for (int k = pt0.z; k < pt1.z; k++, ptXyzTemp
              .add(volumetricVectors[2])) {
            // must be in tetrahedron on second pass for markSphere to be correct...
            // but this does cause certain problems with reentrant faces in ttest4.xyz
            float value = solventRadius - ptXyzTemp.distance(ptS);
            float v = voxelData[i][j][k];
            int ipt = volumeData.getPointIndex(i, j, k);
            if (firstPass && value > 0)
              bsSurfaceDone.set(ipt);
            if (Measure.isInTetrahedron(ptXyzTemp, ptA, ptB, ptC, ptS, plane,
                vTemp, vTemp2, vTemp3, false)) {
              if (!firstPass ? !bsSurfaceDone.get(ipt) && value < 0
                  && value > -volumeData.maxGrid * 1.8f
                  && (value > v) == bsThisPass.get(ipt)
                  : (value > 0 && (v < 0 || v == Float.MAX_VALUE || (value > v) == bsThisPass
                      .get(ipt)))) {
                bsThisPass.set(ipt);
                setVoxel(i, j, k, value);
                voxelRadii[ipt] = -solventRadius;
                if (value > 0) {
                  bsSurfaceVoxels.set(ipt);
                }
              }
            }
          }
        }
      }
    }
  }

  private void setVoxel(int i, int j, int k, float value) {
    voxelData[i][j][k] = value;
  }

  private void markToroidVoxels() {
    Point3i ptA0 = new Point3i();
    Point3i ptB0 = new Point3i();
    Point3i ptA1 = new Point3i();
    Point3i ptB1 = new Point3i();

    for (int ei = 0; ei < aEdges.length; ei++) {
      Edge edge = aEdges[ei];
      int ia = edge.ia;
      int ib = edge.ib;
      Point3f ptA = atomXyz[ia];
      Point3f ptB = atomXyz[ib];
      float rAS = atomRadius[ia] + solventRadius;
      float rBS = atomRadius[ib] + solventRadius;
      float dAB = ptB.distance(ptA);

      setGridLimitsForAtom(ptA, atomRadius[ia] + solventRadius, ptA0, ptA1);
      setGridLimitsForAtom(ptB, atomRadius[ib] + solventRadius, ptB0, ptB1);
      mergeLimits(ptA0, ptB0, pt0, null);
      mergeLimits(ptA1, ptB1, null, pt1);
      volumeData.voxelPtToXYZ(pt0.x, pt0.y, pt0.z, ptXyzTemp);
      for (int i = pt0.x; i < pt1.x; i++, ptXyzTemp.scaleAdd(1,
          volumetricVectors[0], ptY0)) {
        ptY0.set(ptXyzTemp);
        for (int j = pt0.y; j < pt1.y; j++, ptXyzTemp.scaleAdd(1,
            volumetricVectors[1], ptZ0)) {
          ptZ0.set(ptXyzTemp);
          for (int k = pt0.z; k < pt1.z; k++, ptXyzTemp
              .add(volumetricVectors[2])) {
            float dVS = checkSpecialVoxel(ptA, rAS, ptB, rBS, dAB, ptXyzTemp);
            if (Float.isNaN(dVS))
              continue;
            float value = solventRadius - dVS;
            if (value < voxelData[i][j][k]) {
              setVoxel(i, j, k, value);
              int ipt = volumeData.getPointIndex(i, j, k);
              voxelRadii[ipt] = -solventRadius;
            }
          }
        }
      }
    }
    validSpheres.or(noFaceSpheres);
  }

  private void markSphereVoxels(float r0, float distance) {
    boolean isWithin = (distance != Float.MAX_VALUE && point != null);
    for (int iAtom = 0; iAtom < myAtomCount; iAtom++) {
      if (!havePlane && validSpheres != null && !validSpheres.get(iAtom))
        continue;
      boolean isSurface = (noFaceSpheres != null && noFaceSpheres.get(iAtom));
      boolean isNearby = (iAtom >= firstNearbyAtom);
      Point3f ptA = atomXyz[iAtom];
      float rA = atomRadius[iAtom];
      if (isWithin && ptA.distance(point) > distance + rA + 0.5)
        continue;
      setGridLimitsForAtom(ptA, rA + r0, pt0, pt1);
      volumeData.voxelPtToXYZ(pt0.x, pt0.y, pt0.z, ptXyzTemp);
      for (int i = pt0.x; i < pt1.x; i++, ptXyzTemp.scaleAdd(1,
          volumetricVectors[0], ptY0)) {
        ptY0.set(ptXyzTemp);
        for (int j = pt0.y; j < pt1.y; j++, ptXyzTemp.scaleAdd(1,
            volumetricVectors[1], ptZ0)) {
          ptZ0.set(ptXyzTemp);
          for (int k = pt0.z; k < pt1.z; k++, ptXyzTemp
              .add(volumetricVectors[2])) {
            float value = ptXyzTemp.distance(ptA) - rA;
            if (value < voxelData[i][j][k]) {
              if (isNearby || isWithin && ptXyzTemp.distance(point) > distance)
                value = Float.NaN;
              setVoxel(i, j, k, value);
              if (!Float.isNaN(value)) {
                int ipt = volumeData.getPointIndex(i, j, k);
                voxelRadii[ipt] = rA;
                if (value < 0 && isSurface)
                  bsSurfaceVoxels.set(ipt);
              }
            }
          }
        }
      }
    }
  }

  void resetVoxelData(float value) {
    for (int x = 0; x < nPointsX; ++x)
      for (int y = 0; y < nPointsY; ++y)
        for (int z = 0; z < nPointsZ; ++z)
          voxelData[x][y][z] = value;
  }

  void unsetVoxelData() {
    if (!havePlane) {
      for (int x = 0; x < nPointsX; ++x)
        for (int y = 0; y < nPointsY; ++y)
          for (int z = 0; z < nPointsZ; ++z)
            if (voxelData[x][y][z] == Float.MAX_VALUE)
              voxelData[x][y][z] = Float.NaN;
    } else { //solvent planes just focus on negative values
      for (int x = 0; x < nPointsX; ++x)
        for (int y = 0; y < nPointsY; ++y)
          for (int z = 0; z < nPointsZ; ++z)
            if (voxelData[x][y][z] < 0.001f) {
              // Float.NaN will also match ">=" this way
            } else {
              voxelData[x][y][z] = 0.001f;
            }
    }
  }

  void getMaxRadius() {
    maxRadius = 0;
    for (int iAtom = 0; iAtom < myAtomCount; iAtom++) {
      float rA = atomRadius[iAtom];
      if (rA > maxRadius)
        maxRadius = rA;
    }
  }

  private void setGridLimitsForAtom(Point3f ptA, float rA, Point3i pt0,
                                    Point3i pt1) {
    rA += volumeData.maxGrid * 1.8f; // to span corner-to-corner possibility
    volumeData.xyzToVoxelPt(ptA.x - rA, ptA.y - rA, ptA.z - rA, pt0);
    pt0.x = Math.max(pt0.x - 1, 0);
    pt0.y = Math.max(pt0.y - 1, 0);
    pt0.z = Math.max(pt0.z - 1, 0);
    volumeData.xyzToVoxelPt(ptA.x + rA, ptA.y + rA, ptA.z + rA, pt1);
    pt1.x = Math.min(pt1.x + 1, nPointsX);
    pt1.y = Math.min(pt1.y + 1, nPointsY);
    pt1.z = Math.min(pt1.z + 1, nPointsZ);
  }

  private static void mergeLimits(Point3i ptA, Point3i ptB, Point3i pt0,
                                  Point3i pt1) {
    if (pt0 != null) {
      pt0.x = Math.min(ptA.x, ptB.x);
      pt0.y = Math.min(ptA.y, ptB.y);
      pt0.z = Math.min(ptA.z, ptB.z);
    }
    if (pt1 != null) {
      pt1.x = Math.max(ptA.x, ptB.x);
      pt1.y = Math.max(ptA.y, ptB.y);
      pt1.z = Math.max(ptA.z, ptB.z);
    }
  }

  private float checkSpecialVoxel(Point3f ptA, float rAS, Point3f ptB,
                                  float rBS, float dAB, Point3f ptV) {
    /*
     * Checking here for voxels that are in the situation:
     * 
     * A------)(-----S-----)(------B  (not actually linear)
     * |-----rAS-----|-----rBS-----|
     * |-----------dAB-------------|
     *         ptV
     * |--dAV---|---------dBV------|
     *
     * A and B are the two atom centers; S is a hypothetical
     * PROJECTED solvent center based on the position of ptV 
     * in relation to first A, then B.
     * 
     * Where the projected solvent location for one voxel is 
     * within the solvent radius sphere of another, this voxel should
     * be checked in relation to solvent distance, not atom distance.
     * 
     * aa           bb
     *   aaa      bbb
     *      aa  bb
     *         S
     *+++    /  a\    +++
     *   ++ /  | ap ++
     *     +*  V  *aa     x     want V such that angle ASV < angle ASB
     *    /  *****  \
     *   A --+--+----B
     *        b
     * 
     *  ++   the van der Waals radius for each atom.
     *  aa   the extended solvent radius for atom A.
     *  bb   the extended solvent radius for atom B.
     *  p    the projection of voxel V onto aaaaaaa.  
     *  **   the key "trough" location. 
     *  
     *  The objective is to calculate dSV only when V
     *  is within triangle ABS.
     *  
     * Getting dVS:
     * 
     * Known: rAB, rAS, rBS, giving angle BAS (theta)
     * Known: rAB, rAV, rBV, giving angle VAB (alpha)
     * Determined: angle VAS (theta - alpha), and from that, dSV, using
     * the cosine law:
     * 
     *   a^2 + b^2 - 2ab Cos(theta) = c^2.
     * 
     * The trough issue:
     * 
     * Since the voxel might be at point x (above), outside the
     * triangle, we have to test for that. What we will be looking 
     * for in the "trough" will be that angle ASV < angle ASB
     * that is, cosASB < cosASV, for each point p within bbbbb.
     * 
     * If we find the voxel in the "trough", then we set its value to 
     * (solvent radius - dVS).
     * 
     */
    float dAV = ptA.distance(ptV);
    float dBV = ptB.distance(ptV);
    float dVS;
    float f = rAS / dAV;
    if (f > 1) {
      // within solvent sphere of atom A
      // calculate point on solvent sphere aaaa projected through ptV
      p.set(ptA.x + (ptV.x - ptA.x) * f, ptA.y + (ptV.y - ptA.y) * f, ptA.z
          + (ptV.z - ptA.z) * f);
      // If the distance of this point to B is less than the distance
      // of S to B, then we need to check this point
      if (ptB.distance(p) >= rBS)
        return Float.NaN;
      // we are somewhere in the arc SAB, within the solvent sphere of A
      dVS = solventDistance(rAS, rBS, dAB, dAV, dBV);
      return (voxelIsInTrough(dVS, rAS * rAS, rBS, dAB, dAV) ? dVS : Float.NaN);
    }
    if ((f = rBS / dBV) > 1) {
      // calculate point on solvent sphere B projected through ptV
      p.set(ptB.x + (ptV.x - ptB.x) * f, ptB.y + (ptV.y - ptB.y) * f, ptB.z
          + (ptV.z - ptB.z) * f);
      if (ptA.distance(p) >= rAS)
        return Float.NaN;
      // we are somewhere in the triangle ASB, within the solvent sphere of B
      dVS = solventDistance(rBS, rAS, dAB, dBV, dAV);
      return (voxelIsInTrough(dVS, rBS * rBS, rAS, dAB, dBV) ? dVS : Float.NaN);
    }
    // not within solvent sphere of A or B
    return Float.NaN;
  }

  private static boolean voxelIsInTrough(float dXC, float rAC2, float rBC,
                                         float dAB, float dAX) {
    /*
     *         C
     *        /|\
     *       / | \
     *      /  |  \
     *     /   X   \
     *    /         \
     *   A           B
     * 
     */
    //only calculate what we need -- a factor proportional to cos
    float cosACBf = (rAC2 + rBC * rBC - dAB * dAB) / rBC; //  /2 /rAS);
    float cosACXf = (rAC2 + dXC * dXC - dAX * dAX) / dXC; //  /2 /rAS);
    return (cosACBf < cosACXf);
  }

  private float solventDistance(float rAS, float rBS, float dAB, float dAV,
                                float dBV) {
    double dAV2 = dAV * dAV;
    double rAS2 = rAS * rAS;
    double dAB2 = dAB * dAB;
    double angleVAB = Math.acos((dAV2 + dAB2 - dBV * dBV) / (2 * dAV * dAB));
    double angleBAS = Math.acos((dAB2 + rAS2 - rBS * rBS) / (2 * dAB * rAS));
    float dVS = (float) Math.sqrt(rAS2 + dAV2 - 2 * rAS * dAV
        * Math.cos(angleBAS - angleVAB));
    return dVS;
  }

  protected double getPointP(int ia, int ib) {
    Point3f ptA = atomXyz[ia];
    Point3f ptB = atomXyz[ib];
    float rAS = atomRadius[ia] + solventRadius;
    float rBS = atomRadius[ib] + solventRadius;
    vTemp.set(ptB);
    vTemp.sub(ptA);
    float dAB = vTemp.length();
    vTemp.normalize();
    double rAS2 = rAS * rAS;
    double dAB2 = dAB * dAB;
    double cosAngleBAS = (dAB2 + rAS2 - rBS * rBS) / (2 * dAB * rAS);
    double angleBAS = Math.acos(cosAngleBAS);
    p.scaleAdd((float) (cosAngleBAS * rAS), vTemp, ptA);
    Measure.getPlaneThroughPoint(p, vTemp, plane);
    return Math.sin(angleBAS) * rAS;
  }

  ///////////////// debugging ////////////////

  protected int nTest;

  void dumpLine(Point3f pt1, Tuple3f pt2, String label, String color) {
    sg.log("draw ID \"" + label + (nTest++) + "\" @{point" + new Point3f(pt1)
        + "} @{point" + new Point3f(pt2) + "} color " + color);
  }

  void dumpLine2(Point3f pt1, Point3f pt2, String label, float d,
                 String color1, String color2) {
    Vector3f pt = new Vector3f();
    pt.set(pt2);
    pt.sub(pt1);
    pt.normalize();
    pt.scale(d);
    pt.add(pt1);
    sg.log("draw ID \"" + label + (nTest++) + "\" @{point" + new Point3f(pt1)
        + "} @{point" + new Point3f(pt) + "} color " + color1);
    sg.log("draw ID \"" + label + (nTest++) + "\" @{point" + new Point3f(pt)
        + "} @{point" + new Point3f(pt2) + "} color " + color2);
  }

  void dumpPoint(Point3f pt, String label, String color) {
    sg.log("draw ID \"" + label + (nTest++) + "\" @{point" + new Point3f(pt)
        + "} color " + color);
  }

}
