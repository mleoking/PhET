/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-08-31 03:27:18 -0700 (Tue, 31 Aug 2010) $
 * $Revision: 14208 $
 *
 * Copyright (C) 2003-2005  The Jmol Development Team
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

package org.jmol.shapebio;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.modelsetbio.AlphaMonomer;
import org.jmol.modelsetbio.CarbohydratePolymer;
import org.jmol.modelsetbio.Monomer;
import org.jmol.modelsetbio.NucleicPolymer;
import org.jmol.modelsetbio.ProteinStructure;
import org.jmol.shape.MeshRenderer;
import org.jmol.shape.Mesh;
import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

import java.util.BitSet;

abstract class BioShapeRenderer extends MeshRenderer {

  //ultimately this renderer calls MeshRenderer.render1(mesh)

  private boolean invalidateMesh;
  private boolean invalidateSheets;
  private boolean isHighRes;
  private boolean isTraceAlpha;
  private boolean ribbonBorder = false;
  private boolean haveControlPointScreens;
  private int aspectRatio;
  private int hermiteLevel;
  private float sheetSmoothing;

  private Mesh[] meshes;
  private boolean[] meshReady;
  
  protected int monomerCount;
  protected Monomer[] monomers;

  protected boolean isNucleic;
  protected boolean isCarbohydrate;
  protected BitSet bsVisible = new BitSet();
  protected Point3i[] ribbonTopScreens;
  protected Point3i[] ribbonBottomScreens;
  protected Point3f[] controlPoints;
  protected Point3i[] controlPointScreens;
  
  protected int[] leadAtomIndices;
  protected Vector3f[] wingVectors;
  protected short[] mads;
  protected short[] colixes;
  protected byte[] structureTypes;

  protected abstract void renderBioShape(BioShape bioShape);

  @Override
  protected void render() {
    if (shape == null)
      return;
    
    invalidateMesh = false;
    boolean TF = exportType != Graphics3D.EXPORT_NOT || viewer.getHighResolution();
    if (TF != isHighRes)
      invalidateMesh = true;
    isHighRes = TF;

    int val = viewer.getRibbonAspectRatio();
    val = Math.min(Math.max(0, val), 20);
    if (val != aspectRatio && val != 0)
      invalidateMesh = true;
    aspectRatio = val;

    val = viewer.getHermiteLevel();
    if (val == 0 && exportType == Graphics3D.EXPORT_CARTESIAN)
      val = 5; // forces hermite for 3D exporters
    val = (val <= 0 ? -val : viewer.getInMotion() ? 0 : val);
    if (val != hermiteLevel && val != 0)
      invalidateMesh = true;
    hermiteLevel = Math.min(val, 8);
    if (hermiteLevel == 0)
      aspectRatio = 0;

    TF = (viewer.getTraceAlpha()); 
    if (TF != isTraceAlpha)
      invalidateMesh = true;
    isTraceAlpha = TF;

    invalidateSheets = false;    
    float fval = viewer.getSheetSmoothing();
    if (fval != sheetSmoothing && isTraceAlpha) {
      sheetSmoothing = fval;
      invalidateMesh = true;
      invalidateSheets = true;
    }

    BioShapeCollection mps = (BioShapeCollection) shape;
    for (int c = mps.bioShapes.length; --c >= 0;) {
      BioShape bioShape = mps.getBioShape(c);
      if ((bioShape.modelVisibilityFlags & myVisibilityFlag) == 0)
        continue;
      if (bioShape.monomerCount >= 2 && initializePolymer(bioShape)) {
        renderBioShape(bioShape);
        freeTempArrays();
      }
    }
  }

  private void freeTempArrays() {
    if (haveControlPointScreens)
      viewer.freeTempScreens(controlPointScreens);
    viewer.freeTempBytes(structureTypes);
  }

  private boolean initializePolymer(BioShape bioShape) {
    if (viewer.isJmolDataFrame(bioShape.modelIndex)) {
      controlPoints = bioShape.bioPolymer.getControlPoints(true, 0, false);
    } else {
      controlPoints = bioShape.bioPolymer.getControlPoints(isTraceAlpha,
          sheetSmoothing, invalidateSheets);
    }
    monomerCount = bioShape.monomerCount;
    monomers = bioShape.monomers;
    leadAtomIndices = bioShape.bioPolymer.getLeadAtomIndices();

    bsVisible.clear();
    boolean haveVisible = false;
    for (int i = monomerCount; --i >= 0;) {
      if (invalidateMesh)
        bioShape.falsifyMesh(i, false);
      if ((monomers[i].shapeVisibilityFlags & myVisibilityFlag) == 0
          || modelSet.isAtomHidden(leadAtomIndices[i]))
        continue;
      Atom lead = modelSet.atoms[leadAtomIndices[i]];
      if (!g3d.isInDisplayRange(lead.screenX, lead.screenY))
        continue;
      bsVisible.set(i);
      haveVisible = true;
    }
    if (!haveVisible)
      return false;
    ribbonBorder = viewer.getRibbonBorder();

    // note that we are not treating a PhosphorusPolymer
    // as nucleic because we are not calculating the wing
    // vector correctly.
    // if/when we do that then this test will become
    // isNucleic = bioShape.bioPolymer.isNucleic();
    
    isNucleic = bioShape.bioPolymer instanceof NucleicPolymer;
    isCarbohydrate = bioShape.bioPolymer instanceof CarbohydratePolymer;
    haveControlPointScreens = false;
    wingVectors = bioShape.wingVectors;
    meshReady = bioShape.meshReady;
    meshes = bioShape.meshes;
    mads = bioShape.mads;
    colixes = bioShape.colixes;
    setStructureTypes();
    return true;
  }

  private void setStructureTypes() {
    structureTypes = viewer.allocTempBytes(monomerCount + 1);
    for (int i = monomerCount; --i >= 0;) {
      structureTypes[i] = monomers[i].getProteinStructureType();
      if (structureTypes[i] == JmolConstants.PROTEIN_STRUCTURE_TURN)
        structureTypes[i] = JmolConstants.PROTEIN_STRUCTURE_NONE;
    }
    structureTypes[monomerCount] = structureTypes[monomerCount - 1];
  }

  protected boolean isHelix(int i) {
    return structureTypes[i] == JmolConstants.PROTEIN_STRUCTURE_HELIX;
  }

  protected void calcScreenControlPoints() {
    calcScreenControlPoints(controlPoints);
  }

  protected void calcScreenControlPoints(Point3f[] points) {
    int count = monomerCount + 1;
    controlPointScreens = viewer.allocTempScreens(count);
    for (int i = count; --i >= 0;) {
      viewer.transformPoint(points[i], controlPointScreens[i]);
    }
    haveControlPointScreens = true;
  }

  /**
   * calculate screen points based on control points and wing positions
   * (cartoon, strand, meshRibbon, and ribbon)
   * 
   * @param offsetFraction
   * @return Point3i array THAT MUST BE LATER FREED
   */
  protected Point3i[] calcScreens(float offsetFraction) {
    int count = controlPoints.length;
    Point3i[] screens = viewer.allocTempScreens(count);
    if (offsetFraction == 0) {
      for (int i = count; --i >= 0;)
        viewer.transformPoint(controlPoints[i], screens[i]);
    } else {
      float offset_1000 = offsetFraction / 1000f;
      for (int i = count; --i >= 0;)
        calc1Screen(controlPoints[i], wingVectors[i], (mads[i] == 0 && i > 0 ? mads[i-1] : mads[i]), offset_1000,
            screens[i]);
    }
    return screens;
  }

  private final Point3f pointT = new Point3f();

  private void calc1Screen(Point3f center, Vector3f vector, short mad,
                           float offset_1000, Point3i screen) {
    pointT.set(vector);
    float scale = mad * offset_1000;
    pointT.scaleAdd(scale, center);
    viewer.transformPoint(pointT, screen);
  }

  protected short getLeadColix(int i) {
    //System.out.println("bioshaperend " + monomers[i] + " getLeadColix i=" + i + " leadatom = " + monomers[i].getLeadAtom().getInfo() + " index=" + monomers[i].getLeadAtom().getAtomIndex());
    return Graphics3D.getColixInherited(colixes[i],
        monomers[i].getLeadAtom().getColix());
  }

  //// cardinal hermite constant cylinder (meshRibbon, strands)

  private int iPrev, iNext, iNext2, iNext3;
  private int diameterBeg, diameterMid, diameterEnd;
  private boolean doCap0, doCap1;

  private void setNeighbors(int i) {
    iPrev = Math.max(i - 1, 0);
    iNext = Math.min(i + 1, monomerCount);
    iNext2 = Math.min(i + 2, monomerCount);
    iNext3 = Math.min(i + 3, monomerCount);
  }

  protected void renderHermiteCylinder(Point3i[] screens, int i) {
    //strands
    colix = getLeadColix(i);
    if (!g3d.setColix(colix))
      return;
    setNeighbors(i);
    g3d.drawHermite(isNucleic ? 4 : 7, screens[iPrev],
        screens[i], screens[iNext], screens[iNext2]);
  }

  //// cardinal hermite variable conic (cartoons, rockets, trace)

  private boolean setMads(int i, boolean thisTypeOnly) {
    madMid = madBeg = madEnd = mads[i];
    if (isTraceAlpha) {
      if (!thisTypeOnly || structureTypes[i] == structureTypes[iNext]) {
        madEnd = mads[iNext];
        if (madEnd == 0 ) {
          if (this instanceof TraceRenderer) {
            madEnd = madBeg;
          } else {
            madEnd = madBeg;
          }
        }
        madMid = (short)((madBeg + madEnd) >> 1);
      }
    } else {
      if (!thisTypeOnly || structureTypes[i] == structureTypes[iPrev])
        madBeg = (short)(((mads[iPrev] == 0 ? madMid : mads[iPrev]) + madMid) >> 1);
      if (!thisTypeOnly || structureTypes[i] == structureTypes[iNext])
        madEnd = (short)(((mads[iNext] == 0 ? madMid : mads[iNext]) + madMid) >> 1);
    }
    doCap0 = (i == iPrev || thisTypeOnly
        && structureTypes[i] != structureTypes[iPrev]);
    doCap1 = (iNext == iNext2 || thisTypeOnly
        && structureTypes[i] != structureTypes[iNext]);
    diameterBeg = viewer.scaleToScreen(controlPointScreens[i].z, madBeg);
    diameterMid = viewer.scaleToScreen(monomers[i].getLeadAtom().screenZ, madMid);
    diameterEnd = viewer.scaleToScreen(controlPointScreens[iNext].z, madEnd);
    if ((aspectRatio <= 0 || (!checkDiameter(diameterBeg)
        && !checkDiameter(diameterMid) && !checkDiameter(diameterEnd))))
      return false;
    return true;
  }

  private boolean checkDiameter(int d) {
    return (isHighRes & d > ABSOLUTE_MIN_MESH_SIZE || d >= MIN_MESH_RENDER_SIZE);
  }

  //cartoons, rockets, trace:
  protected void renderHermiteConic(int i, boolean thisTypeOnly) {
    setNeighbors(i);
    colix = getLeadColix(i);
    if (!g3d.setColix(colix))
      return;
    if (setMads(i, thisTypeOnly)) {
      try {
        if ((meshes[i] == null || !meshReady[i])
            && !createMeshCylinder(i, madBeg, madMid, madEnd, 1))
          return;
        meshes[i].setColix(colix);
        render1(meshes[i]);
        return;
      } catch (Exception e) {
        Logger.error("render mesh error hermiteConic: " + e.toString());
        //e.printStackTrace();
      }
    }
    g3d.fillHermite(isNucleic ? 4 : 7, diameterBeg, diameterMid,
        diameterEnd, controlPointScreens[iPrev], controlPointScreens[i],
        controlPointScreens[iNext], controlPointScreens[iNext2]);
  }

  //// cardinal hermite box or flat ribbon or twin strand (cartoons, meshRibbon, ribbon)

  //cartoons, meshribbon
  protected void renderHermiteRibbon(boolean doFill, int i, boolean thisTypeOnly) {
    setNeighbors(i);
    colix = getLeadColix(i);
    if (!g3d.setColix(colix))
      return;
    if (doFill && aspectRatio != 0) {
      if (setMads(i, thisTypeOnly)) {
        try {
          if ((meshes[i] == null || !meshReady[i])
              && !createMeshCylinder(i, madBeg, madMid, madEnd, aspectRatio))
            return;
          meshes[i].setColix(colix);
          render1(meshes[i]);
          return;
        } catch (Exception e) {
          Logger.error("render mesh error hermiteRibbon: " + e.toString());
          //e.printStackTrace();
        }
      }
    }
    g3d.drawHermite(doFill, ribbonBorder, isNucleic ? 4 : 7,
        ribbonTopScreens[iPrev], ribbonTopScreens[i], ribbonTopScreens[iNext],
        ribbonTopScreens[iNext2], ribbonBottomScreens[iPrev],
        ribbonBottomScreens[i], ribbonBottomScreens[iNext],
        ribbonBottomScreens[iNext2], aspectRatio);
  }

  //// cardinal hermite (box or flat) arrow head (cartoon)

  private final Point3i screenArrowTop = new Point3i();
  private final Point3i screenArrowTopPrev = new Point3i();
  private final Point3i screenArrowBot = new Point3i();
  private final Point3i screenArrowBotPrev = new Point3i();

  //cartoons
  protected void renderHermiteArrowHead(int i) {
    colix = getLeadColix(i);
    if (!g3d.setColix(colix))
      return;
    setNeighbors(i);
    if (setMads(i, false)) {
      try {
        doCap0 = true;
        doCap1 = false;
        if ((meshes[i] == null || !meshReady[i])
            && !createMeshCylinder(i, (int) (madBeg * 1.2), (int) (madBeg * 0.6), 0,
              aspectRatio >> 1))
          return;
        meshes[i].setColix(colix);
        render1(meshes[i]);
        return;
      } catch (Exception e) {
        Logger.error("render mesh error hermiteArrowHead: " + e.toString());
        //e.printStackTrace();
      }
    }

    calc1Screen(controlPoints[i], wingVectors[i], madBeg, .0007f,
        screenArrowTop);
    calc1Screen(controlPoints[i], wingVectors[i], madBeg, -.0007f,
        screenArrowBot);
    calc1Screen(controlPoints[i], wingVectors[i], madBeg, 0.001f,
        screenArrowTopPrev);
    calc1Screen(controlPoints[i], wingVectors[i], madBeg, -0.001f,
        screenArrowBotPrev);
    g3d.drawHermite(true, ribbonBorder, isNucleic ? 4 : 7,
        screenArrowTopPrev, screenArrowTop, controlPointScreens[iNext],
        controlPointScreens[iNext2], screenArrowBotPrev, screenArrowBot,
        controlPointScreens[iNext], controlPointScreens[iNext2], aspectRatio);
    if (ribbonBorder && aspectRatio == 0) {
      g3d.fillCylinder(colix, colix, Graphics3D.ENDCAPS_SPHERICAL, 
          (exportType == Graphics3D.EXPORT_CARTESIAN ? 50 : 3), //may not be right 0.05 
          screenArrowTop.x, screenArrowTop.y, screenArrowTop.z,
          screenArrowBot.x, screenArrowBot.y, screenArrowBot.z);
    }
  }

  //  rockets --not satisfactory yet
  /**
   * @param i            IGNORED 
   * @param pointBegin   IGNORED
   * @param pointEnd     IGNORED
   * @param screenPtBegin 
   * @param screenPtEnd 
   * 
   */
  protected void renderCone(int i, Point3f pointBegin, Point3f pointEnd,
                  Point3f screenPtBegin, Point3f screenPtEnd) {
    int coneDiameter = mad + (mad >> 2);
    coneDiameter = viewer.scaleToScreen((int) Math.floor(screenPtBegin.z), 
          coneDiameter);
/*    
    if (false && aspectRatio > 0 && checkDiameter(coneDiameter)) {
      try {
        if (meshes[i] == null || !meshReady[i])
          createMeshCone(i, pointBegin, pointEnd, mad);
        meshes[i].colix = colix;
        render1(meshes[i]);
        return;
      } catch (Exception e) {
        Logger.error("render mesh error: renderCone" + e.toString());
        //e.printStackTrace();
      }
    }
*/    
    g3d.fillConeSceen(Graphics3D.ENDCAPS_FLAT, coneDiameter, screenPtBegin,
        screenPtEnd);
  }

  //////////////////////////// mesh 

  // Bob Hanson 11/04/2006 - mesh rendering of secondary structure.
  // mesh creation occurs at rendering time, because we don't
  // know what all the options are, and they aren't important,
  // until it gets rendered, if ever

  private final static int ABSOLUTE_MIN_MESH_SIZE = 3;
  private final static int MIN_MESH_RENDER_SIZE = 8;

  private Point3f[] controlHermites;
  private Vector3f[] wingHermites;
  private Point3f[] radiusHermites;

  private Vector3f norm = new Vector3f();
  private final Vector3f Z = new Vector3f(0.1345f, 0.5426f, 0.3675f); //random reference
  private final Vector3f wing = new Vector3f();
  private final Vector3f wing0 = new Vector3f();
  private final Vector3f wing1 = new Vector3f();
  private final Vector3f wingT = new Vector3f();
  private final AxisAngle4f aa = new AxisAngle4f();
  private final Point3f pt = new Point3f();
  private final Point3f pt1 = new Point3f();
  private final Point3f ptPrev = new Point3f();
  private final Point3f ptNext = new Point3f();
  private final Matrix3f mat = new Matrix3f();

  private boolean createMeshCylinder(int i, int madBeg, int madMid, int madEnd,
                                  int aspectRatio) {
    setNeighbors(i);
    if (controlPoints[i].distance(controlPoints[iNext]) == 0)
      return false;
    if (isHelix(i)) {
      ProteinStructure p = ((AlphaMonomer) monomers[i]).getProteinStructure();
      p.calcAxis();
      /*
       * 
      dumpPoint(p.center, Graphics3D.YELLOW);
      dumpPoint(p.axisA, Graphics3D.YELLOW);
      dumpPoint(p.axisB, Graphics3D.YELLOW);
      Vector3f v = new Vector3f(((Helix) p).vTemp);
      v.scale(1);
      dumpVector(p.center, v, Graphics3D.GREEN);
      v.set(((Helix) p).m);
      v.scale(1);
      dumpVector(p.center, v, Graphics3D.WHITE);
      dumpVector(p.center, new Vector3f(1, 1, 1), Graphics3D.BLUE);
      dumpPoint(controlPoints[i], Graphics3D.WHITE);
      */
    }
    boolean isEccentric = (aspectRatio != 1 && wingVectors != null);
    int nHermites = (hermiteLevel + 1) * 2 + 1; // 4 for hermiteLevel = 1
    int nPer = (nHermites - 1) * 2 - 2; // 6 for hermiteLevel 1
    Mesh mesh = meshes[i] = new Mesh("mesh_" + shapeID + "_" + i, g3d, (short) 0, i);
    boolean variableRadius = (madBeg != madMid || madMid != madEnd);
    if (controlHermites == null || controlHermites.length < nHermites + 1) {
      controlHermites = new Point3f[nHermites + 1];
    }
    Graphics3D.getHermiteList(isNucleic ? 4 : 7, controlPoints[iPrev],
        controlPoints[i], controlPoints[iNext], controlPoints[iNext2],
        controlPoints[iNext3], controlHermites, 0, nHermites);
    //System.out.println("create mesh " + thisChain + " mesh_" + shapeID + "_"+i+controlPoints[i] + controlPoints[iNext]);
    if (isEccentric) {
      if (wingHermites == null || wingHermites.length < nHermites + 1) {
        wingHermites = new Vector3f[nHermites + 1];
      }
      wing.set(wingVectors[iPrev]);
      if (madEnd == 0)
        wing.scale(2.0f); //adds a flair to an arrow
      Graphics3D.getHermiteList(isNucleic ? 4 : 7, wing, wingVectors[i],
          wingVectors[iNext], wingVectors[iNext2], wingVectors[iNext3],
          wingHermites, 0, nHermites);
    }
    float radius1 = madBeg / 2000f;
    float radius2 = madMid / 2000f;
    float radius3 = madEnd / 2000f;
    if (variableRadius) {
      if (radiusHermites == null
          || radiusHermites.length < ((nHermites + 1) >> 1) + 1) {
        radiusHermites = new Point3f[((nHermites + 1) >> 1) + 1];
      }
      ptPrev.set(radius1, radius1, 0);
      pt.set(radius1, radius2, 0);
      pt1.set(radius2, radius3, 0);
      ptNext.set(radius3, radius3, 0);
      // two for the price of one!
      Graphics3D.getHermiteList(4, ptPrev, pt, pt1, ptNext, ptNext,
          radiusHermites, 0, (nHermites + 1) >> 1);
    }
    if (!isEccentric) {
      norm.sub(controlHermites[1], controlHermites[0]);
      wing0.cross(norm, Z);
      wing0.cross(norm, wing0);
    }
    int nPoints = 0;
    int iMid = nHermites >> 1;
    for (int p = 0; p < nHermites; p++) {
      norm.sub(controlHermites[p + 1], controlHermites[p]);
      if (isEccentric) {
        wing.set(wingHermites[p]);
        wing1.set(wing);
        wing.scale(2f / aspectRatio);
        //dumpVector(controlHermites[p],wing)
      } else {
        wing.cross(norm, wing0);
        wing.normalize();
      }
      float scale = (!variableRadius ? radius1 : p < iMid ? radiusHermites[p].x
          : radiusHermites[p - iMid].y);
      wing.scale(scale);
      wing1.scale(scale);
      aa.set(norm, (float) (2 * Math.PI / nPer));
      mat.set(aa);
      pt1.set(controlHermites[p]);
      for (int k = 0; k < nPer; k++) {
        mat.transform(wing);
        wingT.set(wing);
        if (isEccentric) {
          if (k == (nPer + 2) / 4 || k == (3 * nPer + 2) / 4)
            wing1.scale(-1);
          wingT.add(wing1);
        }
        pt.add(pt1, wingT);
        if (isEccentric) {
          //dumpVector(wingHermites[p], pt);
        }
        mesh.addVertexCopy(pt);
      }
      if (p > 0) {
        for (int k = 0; k < nPer; k++) {
          mesh.addQuad(nPoints - nPer + k, nPoints - nPer + ((k + 1) % nPer),
              nPoints + ((k + 1) % nPer), nPoints + k);
        }
      }
      nPoints += nPer;
    }
    if (doCap0)
      for (int k = hermiteLevel * 2; --k >= 0;)
        mesh.addQuad(k + 2, k + 1, (nPer - k) % nPer, nPer - k - 1);
    if (doCap1)
      for (int k = hermiteLevel * 2; --k >= 0;)
        mesh.addQuad(nPoints - k - 1, nPoints - nPer + (nPer - k) % nPer,
            nPoints - nPer + k + 1, nPoints - nPer + k + 2);
    mesh.initialize(JmolConstants.FRONTLIT, null, null);
    //System.out.sprintln("mesh "+ mesh.thisID + " " + mesh.vertexCount+" "+mesh.vertices.length + " " + mesh.polygonCount + " " + mesh.polygonIndexes.length);
    mesh.setVisibilityFlags(1);
    return (meshReady[i] = true);
  }
  
  /*
  void createMeshCone(int i, Point3f pointBegin, Point3f pointEnd, int mad) {
    int level = 5;
    int nHermites = (level + 1) * 2 + 1; // (not used)
    int nPer = (nHermites - 1) * 2 - 2; //22 for hermiteLevel 5
    norm.sub(pointEnd, pointBegin);
    norm.normalize();
    norm.scale(0.19f);
    wing.cross(Z, norm);
    wing.normalize();
    wing.scale(mad * 1.2f / 2000f);
    Mesh mesh = meshes[i] = new Mesh("mesh_" + shapeID + "_" + i, g3d,
        (short) 0);
    aa.set(norm, (float) (2 * Math.PI / nPer));
    mat.set(aa);
    pt1.set(pointBegin);
    pt1.sub(norm);
    for (int k = 0; k < nPer; k++) {
      mat.transform(wing);
      pt.add(pt1, wing);
      mesh.addVertexCopy(pt);
    }
    mesh.addVertexCopy(pointEnd);
    for (int k = 0; k < nPer; k++)
      mesh.addTriangle((k + 1) % nPer, nPer, k);
    for (int k = level * 2; --k >= 0;)
      mesh.addQuad(k + 2, k + 1, (nPer - k) % nPer, nPer - k - 1);
    mesh.initialize(JmolConstants.FRONTLIT);
    meshReady[i] = true;
    mesh.setVisibilityFlags(1);
  }
  */
  /*
  private void dumpPoint(Point3f pt, short color) {
    Point3i pt1 = viewer.transformPoint(pt);
    g3d.fillSphereCentered(color, 20, pt1);
  }

  private void dumpVector(Point3f pt, Vector3f v, short color) {
    Point3f p1 = new Point3f();
    Point3i pt1 = new Point3i();
    Point3i pt2 = new Point3i();
    p1.add(pt, v);
    pt1.set(viewer.transformPoint(pt));
    pt2.set(viewer.transformPoint(p1));
    System.out.print("draw pt" + ("" + Math.random()).substring(3, 10) + " {"
        + pt.x + " " + pt.y + " " + pt.z + "} {" + p1.x + " " + p1.y + " "
        + p1.z + "}" + ";" + " ");
    g3d.fillCylinder(color, Graphics3D.ENDCAPS_FLAT, 2, pt1.x, pt1.y, pt1.z,
        pt2.x, pt2.y, pt2.z);
    g3d.fillSphereCentered(color, 5, pt2);
  }
  */
  
}
