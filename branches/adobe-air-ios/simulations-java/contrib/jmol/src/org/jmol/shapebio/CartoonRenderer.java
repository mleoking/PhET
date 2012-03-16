/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-03-11 14:30:16 -0500 (Sun, 11 Mar 2007) $
 * $Revision: 7068 $
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

package org.jmol.shapebio;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelsetbio.NucleicMonomer;
import org.jmol.modelsetbio.ProteinStructure;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;

public class CartoonRenderer extends RocketsRenderer {

  private boolean newRockets = true;
  private boolean renderAsRockets;
  private boolean renderEdges;
  private short colixSugarEdge;
  private short colixWatsonCrickEdge;
  private short colixHoogsteenEdge;
  
  @Override
  protected void renderBioShape(BioShape bioShape) {
    if (bioShape.wingVectors == null || isCarbohydrate)
      return;
    calcScreenControlPoints();
    if (isNucleic) {
      renderNucleic();
      return;
    }
    boolean val = viewer.getCartoonRocketFlag();
    if (renderAsRockets != val) {
      for (int i = 0; i < monomerCount; i++)
        bioShape.falsifyMesh(i, false);
      renderAsRockets = val;
    }
    val = !viewer.getRocketBarrelFlag();
    if (renderArrowHeads != val) {
      for (int i = 0; i < monomerCount; i++)
        bioShape.falsifyMesh(i, false);
      renderArrowHeads = val;
    }
    ribbonTopScreens = calcScreens(0.5f);
    ribbonBottomScreens = calcScreens(-0.5f);
    calcRopeMidPoints(newRockets);
    if (!renderArrowHeads) {
      calcScreenControlPoints(cordMidPoints);
      controlPoints = cordMidPoints;
    }
    render1();
    viewer.freeTempPoints(cordMidPoints);
    viewer.freeTempScreens(ribbonTopScreens);
    viewer.freeTempScreens(ribbonBottomScreens);
  }

  Point3i ptConnect = new Point3i();
  void renderNucleic() {
    renderEdges = viewer.getCartoonBaseEdgesFlag();
    boolean isTranslucent = Graphics3D.isColixTranslucent(colix);
    if (renderEdges) {
      float tl = Graphics3D.getColixTranslucencyLevel(colix);
      colixSugarEdge = Graphics3D.getColixTranslucent(Graphics3D.RED, isTranslucent, tl);
      colixWatsonCrickEdge = Graphics3D.getColixTranslucent(Graphics3D.GREEN, isTranslucent, tl);
      colixHoogsteenEdge = Graphics3D.getColixTranslucent(Graphics3D.BLUE, isTranslucent, tl);
    }
    boolean isTraceAlpha = viewer.getTraceAlpha();
    for (int i = bsVisible.nextSetBit(0); i >= 0; i = bsVisible
        .nextSetBit(i + 1)) {
      if (isTraceAlpha) {
        ptConnect.set(
            (controlPointScreens[i].x + controlPointScreens[i + 1].x) / 2,
            (controlPointScreens[i].y + controlPointScreens[i + 1].y) / 2,
            (controlPointScreens[i].z + controlPointScreens[i + 1].z) / 2);
      } else {
        ptConnect.set(controlPointScreens[i + 1]);
      }
      renderHermiteConic(i, false);
      colix = getLeadColix(i);
      if (g3d.setColix(colix))
        renderNucleicBaseStep((NucleicMonomer) monomers[i], mads[i], ptConnect);
    }
  }

  @Override
  protected void render1() {
    boolean lastWasSheet = false;
    boolean lastWasHelix = false;
    ProteinStructure previousStructure = null;
    ProteinStructure thisStructure;

    // Key structures that must render properly
    // include 1crn and 7hvp

    // this loop goes monomerCount --> 0, because
    // we want to hit the heads first

    for (int i = monomerCount; --i >= 0;) {
      // runs backwards, so it can render the heads first
      thisStructure = monomers[i].getProteinStructure();
      if (thisStructure != previousStructure) {
        if (renderAsRockets)
          lastWasHelix = false;
        lastWasSheet = false;
      }
      previousStructure = thisStructure;
      boolean isHelix = isHelix(i);
      boolean isSheet = isSheet(i);
      boolean isHelixRocket = (renderAsRockets || !renderArrowHeads ? isHelix : false);
      if (bsVisible.get(i)) {
        if (isHelixRocket) {
          //next pass
        } else if (isSheet || isHelix) {
          if (lastWasSheet && isSheet || lastWasHelix && isHelix)
            //uses topScreens
            renderHermiteRibbon(true, i, true);
          else
            renderHermiteArrowHead(i);
        } else if (i != monomerCount - 1) {
          renderHermiteConic(i, true);
        }
      }
      lastWasSheet = isSheet;
      lastWasHelix = isHelix;
    }

    if (renderAsRockets || !renderArrowHeads)
      renderRockets();
  }

  private void renderRockets() {
    // doing the cylinders separately because we want to connect them if we can.

    // Key structures that must render properly
    // include 1crn and 7hvp

    // this loop goes 0 --> monomerCount, because
    // the special segments routine takes care of heads
    tPending = false;
    for (int i = bsVisible.nextSetBit(0); i >= 0; i = bsVisible
        .nextSetBit(i + 1))
      if (isHelix(i))
        renderSpecialSegment(monomers[i], getLeadColix(i), mads[i]);
    renderPending();
  }
  
  //// nucleic acid base rendering
  
  private final Point3f[] ring6Points = new Point3f[6];
  private final Point3i[] ring6Screens = new Point3i[6];
  private final Point3f[] ring5Points = new Point3f[5];
  private final Point3i[] ring5Screens = new Point3i[5];

  {
    ring6Screens[5] = new Point3i();
    for (int i = 5; --i >= 0; ) {
      ring5Screens[i] = new Point3i();
      ring6Screens[i] = new Point3i();
    }
  }

  private void renderNucleicBaseStep(NucleicMonomer nucleotide,
                             short thisMad, Point3i backboneScreen) {
    if (renderEdges) {
      renderLenotisWesthofEdges(nucleotide, thisMad);
      return;
    }
    nucleotide.getBaseRing6Points(ring6Points);
    viewer.transformPoints(ring6Points, ring6Screens);
    renderRing6();
    boolean hasRing5 = nucleotide.maybeGetBaseRing5Points(ring5Points);
    Point3i stepScreen;
    if (hasRing5) {
      viewer.transformPoints(ring5Points, ring5Screens);
      renderRing5();
      stepScreen = ring5Screens[3];//was 2
    } else {
      stepScreen = ring6Screens[2];//was 1
    }
    mad = (short) (thisMad > 1 ? thisMad / 2 : thisMad);
    g3d.fillCylinderScreen(Graphics3D.ENDCAPS_SPHERICAL,
                     viewer.scaleToScreen(backboneScreen.z,
                                          mad),
                     backboneScreen, stepScreen);
    --ring6Screens[5].z;
    for (int i = 5; --i >= 0; ) {
      --ring6Screens[i].z;
      if (hasRing5)
        --ring5Screens[i].z;
    }
    for (int i = 6; --i > 0; )
      g3d.fillCylinderScreen(Graphics3D.ENDCAPS_SPHERICAL, 3,
                       ring6Screens[i], ring6Screens[i - 1]);
    if (hasRing5) {
      for (int i = 5; --i > 0; )
        g3d.fillCylinderScreen(Graphics3D.ENDCAPS_SPHERICAL, 3,
                         ring5Screens[i], ring5Screens[i - 1]);
    } else {
      g3d.fillCylinderScreen(Graphics3D.ENDCAPS_SPHERICAL, 3,
                       ring6Screens[5], ring6Screens[0]);
    }
  }

  private void renderLenotisWesthofEdges(NucleicMonomer nucleotide,
                                         short thisMad) {
    //                Nasalean L, Strombaugh J, Zirbel CL, and Leontis NB in 
    //                Non-Protein Coding RNAs, 
    //                Nils G. Walter, Sarah A. Woodson, Robert T. Batey, Eds.
    //                Chapter 1, p 6.
    // http://books.google.com/books?hl=en&lr=&id=se5JVEqO11AC&oi=fnd&pg=PR11&dq=Non-Protein+Coding+RNAs&ots=3uTkn7m3DA&sig=6LzQREmSdSoZ6yNrQ15zjYREFNE#v=onepage&q&f=false
    
    if (!nucleotide.getEdgePoints(ring6Points))
      return;
    viewer.transformPoints(ring6Points, ring6Screens);
    renderTriangle();
    mad = (short) (thisMad > 1 ? thisMad / 2 : thisMad);
    g3d.fillCylinderScreen(Graphics3D.ENDCAPS_SPHERICAL, 3,
        ring6Screens[0], ring6Screens[1]);
    g3d.fillCylinderScreen(Graphics3D.ENDCAPS_SPHERICAL, 3,
        ring6Screens[1], ring6Screens[2]);
    g3d.setColix(colixSugarEdge);
    g3d.fillCylinderScreen(Graphics3D.ENDCAPS_SPHERICAL, 3,
        ring6Screens[2], ring6Screens[3]);
    g3d.setColix(colixWatsonCrickEdge);
    g3d.fillCylinderScreen(Graphics3D.ENDCAPS_SPHERICAL, 3,
        ring6Screens[3], ring6Screens[4]);
    g3d.setColix(colixHoogsteenEdge);
    g3d.fillCylinderScreen(Graphics3D.ENDCAPS_SPHERICAL, 3,
        ring6Screens[4], ring6Screens[5]);
   }

  private void renderTriangle() {
    g3d.setNoisySurfaceShade(ring6Screens[2], ring6Screens[3], ring6Screens[4]);
    g3d.fillTriangle(ring6Screens[2], ring6Screens[3], ring6Screens[4]);
  }

  private void renderRing6() {
    g3d.setNoisySurfaceShade(ring6Screens[0], ring6Screens[2], ring6Screens[4]);
    g3d.fillTriangle(ring6Screens[0], ring6Screens[2], ring6Screens[4]);
    g3d.fillTriangle(ring6Screens[0], ring6Screens[1], ring6Screens[2]);
    g3d.fillTriangle(ring6Screens[0], ring6Screens[4], ring6Screens[5]);
    g3d.fillTriangle(ring6Screens[2], ring6Screens[3], ring6Screens[4]);
  }

  private void renderRing5() {
    // shade was calculated previously by renderRing6();
    g3d.fillTriangle(ring5Screens[0], ring5Screens[2], ring5Screens[3]);
    g3d.fillTriangle(ring5Screens[0], ring5Screens[1], ring5Screens[2]);
    g3d.fillTriangle(ring5Screens[0], ring5Screens[3], ring5Screens[4]);
  }  
  
}
