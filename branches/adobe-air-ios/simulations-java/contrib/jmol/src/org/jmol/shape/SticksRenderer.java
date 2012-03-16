/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-12-06 20:36:47 -0800 (Mon, 06 Dec 2010) $
 * $Revision: 14757 $

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

package org.jmol.shape;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.api.JmolEdge;
import org.jmol.g3d.*;
import org.jmol.modelset.Atom;
import org.jmol.modelset.Bond;
import org.jmol.viewer.JmolConstants;

public class SticksRenderer extends ShapeRenderer {

  private boolean showMultipleBonds;
  private float multipleBondSpacing;
  private float multipleBondRadiusFactor;
  private byte modeMultipleBond;
  //boolean showHydrogens;
  private byte endcaps;

  private boolean ssbondsBackbone;
  private boolean hbondsBackbone;
  private boolean bondsBackbone;
  private boolean hbondsSolid;
  
  private Atom atomA, atomB;
  private Bond bond;
  private int xA, yA, zA;
  private int xB, yB, zB;
  private int dx, dy;
  private int mag2d;
  private short colixA, colixB;
  private int width;
  private boolean lineBond;
  private int bondOrder;
  private boolean renderWireframe;
  private boolean isAntialiased;
  private boolean slabbing;
  private boolean slabByAtom;


  @Override
  protected void render() {
    slabbing = viewer.getSlabEnabled();
    slabByAtom = viewer.getSlabByAtom();          
    endcaps = Graphics3D.ENDCAPS_SPHERICAL;
    multipleBondSpacing = viewer.getMultipleBondSpacing();
    multipleBondRadiusFactor = viewer.getMultipleBondRadiusFactor();
    if (multipleBondSpacing > 0) {
      z = new Vector3f();
      x = new Vector3f();
      y = new Vector3f();
      p1 = new Point3f();
      p2 = new Point3f();
      s1 = new Point3i();
      s2 = new Point3i();
    }
    showMultipleBonds = multipleBondSpacing != 0 && viewer.getShowMultipleBonds();
    modeMultipleBond = viewer.getModeMultipleBond();
    renderWireframe = viewer.getInMotion() && viewer.getWireframeRotation();
    ssbondsBackbone = viewer.getSsbondsBackbone();
    hbondsBackbone = viewer.getHbondsBackbone();
    bondsBackbone = hbondsBackbone | ssbondsBackbone;
    hbondsSolid = viewer.getHbondsSolid();
    isAntialiased = g3d.isAntialiased();
    Bond[] bonds = modelSet.getBonds();
    for (int i = modelSet.getBondCount(); --i >= 0; ) {
      bond = bonds[i];
      if ((bond.getShapeVisibilityFlags() & myVisibilityFlag) != 0) 
        renderBond();
    }
  }

  private void renderBond() {
    atomA = bond.getAtom1();
    atomB = bond.getAtom2();
    int order = bond.getOrder() & ~JmolEdge.BOND_NEW;
    if (bondsBackbone) {
      if (ssbondsBackbone && (order & JmolEdge.BOND_SULFUR_MASK) != 0) {
        // for ssbonds, always render the sidechain,
        // then render the backbone version
        /*
         mth 2004 04 26
         No, we are not going to do this any more
         render(bond, atomA, atomB);
         */

        atomA = atomA.getGroup().getLeadAtom(atomA);
        atomB = atomB.getGroup().getLeadAtom(atomB);
      } else if (hbondsBackbone && Bond.isHydrogen(order)) {
        atomA = atomA.getGroup().getLeadAtom(atomA);
        atomB = atomB.getGroup().getLeadAtom(atomB);
      }
    }
    if (!atomA.isInFrame() || !atomB.isInFrame()
        || !g3d.isInDisplayRange(atomA.screenX, atomA.screenY)
        || !g3d.isInDisplayRange(atomB.screenX, atomB.screenY)
        || modelSet.isAtomHidden(atomA.getIndex())
        || modelSet.isAtomHidden(atomB.getIndex()))
      return;

    if (slabbing) {
      if (g3d.isClippedZ(atomA.screenZ) && g3d.isClippedZ(atomB.screenZ))
        return;
      if(slabByAtom && 
          (g3d.isClippedZ(atomA.screenZ) || g3d.isClippedZ(atomB.screenZ)))
        return;          
    }
    colixA = atomA.getColix();
    colixB = atomB.getColix();
    if (((colix = bond.getColix()) & Graphics3D.OPAQUE_MASK) == Graphics3D.USE_PALETTE) {
      colix = (short) (colix & ~Graphics3D.OPAQUE_MASK);
      colixA = Graphics3D.getColixInherited((short) (colix | viewer
          .getColixAtomPalette(atomA, JmolConstants.PALETTE_CPK)), colixA);
      colixB = Graphics3D.getColixInherited((short) (colix | viewer
          .getColixAtomPalette(atomB, JmolConstants.PALETTE_CPK)), colixB);
    } else {
      colixA = Graphics3D.getColixInherited(colix, colixA);
      colixB = Graphics3D.getColixInherited(colix, colixB);
    }
    xA = atomA.screenX;
    yA = atomA.screenY;
    zA = atomA.screenZ;
    xB = atomB.screenX;
    yB = atomB.screenY;
    zB = atomB.screenZ;
    if (zA == 1 || zB == 1)
      return;
    
    // set the rendered bond order
    
    bondOrder = order & ~JmolEdge.BOND_NEW;
    if ((bondOrder & JmolEdge.BOND_PARTIAL_MASK) == 0) {
      if ((bondOrder & JmolEdge.BOND_SULFUR_MASK) != 0)
        bondOrder &= ~JmolEdge.BOND_SULFUR_MASK;
      if ((bondOrder & JmolEdge.BOND_COVALENT_MASK) != 0) {
        if (!showMultipleBonds
            || modeMultipleBond == JmolConstants.MULTIBOND_NEVER
            || (modeMultipleBond == JmolConstants.MULTIBOND_NOTSMALL && mad > JmolConstants.madMultipleBondSmallMaximum)) {
          bondOrder = 1;
        }
      }
    }
    
    // set the mask
    
    int mask = 0;
    switch (bondOrder) {
    case 1:
    case 2:
    case 3:
    case 4:
      break;
    case JmolEdge.BOND_ORDER_UNSPECIFIED:
    case JmolEdge.BOND_AROMATIC_SINGLE:
      bondOrder = 1;
      mask = (order == JmolEdge.BOND_AROMATIC_SINGLE ? 0 : 1);
      break;
    case JmolEdge.BOND_AROMATIC:
    case JmolEdge.BOND_AROMATIC_DOUBLE:
      bondOrder = 2;
      mask = (order == JmolEdge.BOND_AROMATIC ? getAromaticDottedBondMask()
          : 0);
      break;
    default:
      if ((bondOrder & JmolEdge.BOND_PARTIAL_MASK) != 0) {
        bondOrder = JmolConstants.getPartialBondOrder(order);
        mask = JmolConstants.getPartialBondDotted(order);
      } else if (Bond.isHydrogen(bondOrder)) {
        bondOrder = 1;
        if (!hbondsSolid)
          mask = -1;
      } else if (bondOrder == JmolEdge.BOND_STRUT) {
        bondOrder = 1;
      }
    }
    
    // set the diameter
    
    mad = bond.getMad();
    if (multipleBondRadiusFactor > 0 && bondOrder > 1)
      mad *= multipleBondRadiusFactor;
    dx = xB - xA;
    dy = yB - yA;
    width = viewer.scaleToScreen((zA + zB) / 2, mad);
    if (renderWireframe && width > 0)
      width = 1;
    lineBond = (width <= 1);
    if (lineBond && (isAntialiased)) {
      width = 3;
      lineBond = false;
    }
    
    // draw the bond
    
    switch (mask) {
    case -1:
      renderHbondDashed();
      break;
    default:
      drawBond(mask);
      break;
    }
  }
    
  private Vector3f x, y, z;
  private Point3f p1, p2;
  private Point3i s1, s2;
  
  private void drawBond(int dottedMask) {
    if (exportType == Graphics3D.EXPORT_CARTESIAN && bondOrder == 1) {
      // bypass screen rendering and just use the atoms themselves
      g3d.drawBond(atomA, atomB, colixA, colixB, endcaps, mad);
      return;
    }
    boolean isEndOn = (dx == 0 && dy == 0);
    if (isEndOn && lineBond)
      return;
    boolean doFixedSpacing = (bondOrder > 1
        && multipleBondSpacing > 0
        && (viewer.getHybridizationAndAxes(atomA.index, z, x, "pz") != null 
            || viewer.getHybridizationAndAxes(atomB.index, z, x, "pz") != null) 
        && !Float.isNaN(x.x));
    if (isEndOn && !doFixedSpacing) {
      // end-on view
      int space = width / 8 + 3;
      int step = width + space;
      int y = yA - (bondOrder - 1) * step / 2;
      do {
        fillCylinder(colixA, colixA, endcaps, width, xA, y, zA, xA, y, zA);
        y += step;
      } while (--bondOrder > 0);
      return;
    }
    if (bondOrder == 1) {
      if ((dottedMask & 1) != 0)
        drawDashed(xA, yA, zA, xB, yB, zB);
      else
        fillCylinder(colixA, colixB, endcaps, width, xA, yA, zA, xB, yB, zB);
      return;
    }
    if (doFixedSpacing) {
      x.sub(atomB, atomA);
      y.cross(x, z);
      y.normalize();
      y.scale(multipleBondSpacing);
      x.set(y);
      x.scale((bondOrder - 1) / 2f);
      p1.sub(atomA, x);
      p2.sub(atomB, x);
      while (true) {
        viewer.transformPoint(p1, s1);
        viewer.transformPoint(p2, s2);
        p1.add(y);
        p2.add(y);
        if ((dottedMask & 1) != 0)
          drawDashed(s1.x, s1.y, s1.z, s2.x, s2.y, s2.z);
        else
          fillCylinder(colixA, colixB, endcaps, width, s1.x, s1.y, s1.z, s2.x,
              s2.y, s2.z);
        dottedMask >>= 1;
        if (--bondOrder <= 0)
          break;
        stepAxisCoordinates();
      }
      return;
    }
    int dxB = dx * dx;
    int dyB = dy * dy;
    mag2d = (int) (Math.sqrt(dxB + dyB) + 0.5);
    resetAxisCoordinates();
    while (true) {
      if ((dottedMask & 1) != 0)
        drawDashed(xAxis1, yAxis1, zA, xAxis2, yAxis2, zB);
      else
        fillCylinder(colixA, colixB, endcaps, width, xAxis1, yAxis1, zA,
            xAxis2, yAxis2, zB);
      dottedMask >>= 1;
      if (--bondOrder <= 0)
        break;
      stepAxisCoordinates();
    }
  }

  private int xAxis1, yAxis1, xAxis2, yAxis2, dxStep, dyStep;

  private void resetAxisCoordinates() {
    int space = mag2d >> 3;
    if (multipleBondSpacing != -1 && multipleBondSpacing < 0)
      space *= -multipleBondSpacing;
    int step = width + space;
    dxStep = step * dy / mag2d;
    dyStep = step * -dx / mag2d;
    xAxis1 = xA;
    yAxis1 = yA;
    xAxis2 = xB;
    yAxis2 = yB;
    int f = (bondOrder - 1);
    xAxis1 -= dxStep * f / 2;
    yAxis1 -= dyStep * f / 2;
    xAxis2 -= dxStep * f / 2;
    yAxis2 -= dyStep * f / 2;
  }

  private void stepAxisCoordinates() {
    xAxis1 += dxStep; yAxis1 += dyStep;
    xAxis2 += dxStep; yAxis2 += dyStep;
  }

  private int getAromaticDottedBondMask() {
    Atom atomC = atomB.findAromaticNeighbor(atomA.getIndex());
    if (atomC == null)
      return 1;
    int dxAC = atomC.screenX - xA;
    int dyAC = atomC.screenY - yA;
    return ((dx * dyAC - dy * dxAC) < 0 ? 2 : 1);
  }

  private void drawDashed(int xA, int yA, int zA, int xB, int yB, int zB) {
    int dx = xB - xA;
    int dy = yB - yA;
    int dz = zB - zA;
    int i = 2;
    while (i <= 9) {
      int xS = xA + (dx * i) / 12;
      int yS = yA + (dy * i) / 12;
      int zS = zA + (dz * i) / 12;
      i += 3;
      int xE = xA + (dx * i) / 12;
      int yE = yA + (dy * i) / 12;
      int zE = zA + (dz * i) / 12;
      i += 2;
      fillCylinder(colixA, colixB, Graphics3D.ENDCAPS_FLAT, width,
                         xS, yS, zS, xE, yE, zE);
    }
  }

  private void renderHbondDashed() {
    int dx = xB - xA;
    int dy = yB - yA;
    int dz = zB - zA;
    int i = 1;
    while (i < 10) {
      int xS = xA + (dx * i) / 10;
      int yS = yA + (dy * i) / 10;
      int zS = zA + (dz * i) / 10;
      short colixS = i < 5 ? colixA : colixB;
      i += 2;
      int xE = xA + (dx * i) / 10;
      int yE = yA + (dy * i) / 10;
      int zE = zA + (dz * i) / 10;
      short colixE = i < 5 ? colixA : colixB;
      ++i;
      fillCylinder(colixS, colixE, Graphics3D.ENDCAPS_FLAT, width,
                         xS, yS, zS, xE, yE, zE);
    }
  }
    
  private void fillCylinder(short colixA, short colixB, byte endcaps,
                              int diameter, int xA, int yA, int zA, int xB,
                              int yB, int zB) {
    if (lineBond)
      g3d.drawLine(colixA, colixB, xA, yA, zA, xB, yB, zB);
    else
      g3d.fillCylinder(colixA, colixB, endcaps, 
          (exportType == Graphics3D.EXPORT_NOT || mad == 1 ? diameter : mad), 
          xA, yA, zA, xB, yB, zB);
  }

}
