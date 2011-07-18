/* $RCSfile$
 * $Author: nicove $
 * $Date: 2010-07-31 02:51:00 -0700 (Sat, 31 Jul 2010) $
 * $Revision: 13783 $
 *
 * Copyright (C) 2002-2006  Miguel, Jmol Development, www.jmol.org
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
import java.text.NumberFormat;

import org.jmol.api.SymmetryInterface;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.BoxInfo;
import org.jmol.util.TextFormat;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.StateManager;

public class UccageRenderer extends CageRenderer {

  NumberFormat nf;
  byte fid;
  boolean doLocalize;
  
  @Override
  protected void setEdges() {
    tickEdges = BoxInfo.uccageTickEdges;    
  }

  final Point3f[] verticesT = new Point3f[8];  
  {
    for (int i = 8; --i >= 0; ) {
      verticesT[i] = new Point3f();
    }
  }

  @Override
  protected void initRenderer() {
    super.initRenderer();
    draw000 = false;
  }
  
  @Override
  protected void render() {
    imageFontScaling = viewer.getImageFontScaling();
    font3d = g3d.getFont3DScaled(((Uccage)shape).font3d, imageFontScaling);
    int mad = viewer.getObjectMad(StateManager.OBJ_UNITCELL);
    colix = viewer.getObjectColix(StateManager.OBJ_UNITCELL);
    if (mad == 0 || !g3d.setColix(colix) || viewer.isJmolDataFrame())
      return;
    doLocalize = viewer.getUseNumberLocalization();
    render1(mad);
  }

  void render1(int mad) {
    SymmetryInterface symmetry = viewer.getCurrentUnitCell();
    if (symmetry == null || !symmetry.haveUnitCell())
      return;
    isPolymer = symmetry.isPolymer();
    isSlab = symmetry.isSlab();
    Point3f[] vertices = symmetry.getUnitCellVertices();
    Point3f offset = symmetry.getCartesianOffset();
    for (int i = 8; --i >= 0;)
      verticesT[i].add(vertices[i], offset);
    Point3f[] axisPoints = viewer.getAxisPoints();
    boolean drawAllLines = (viewer.getObjectMad(StateManager.OBJ_AXIS1) == 0 
        || viewer.getAxesScale() < 2 || axisPoints == null);
    
    render(mad, verticesT, axisPoints, drawAllLines ? 0 : 3);
    if (viewer.getDisplayCellParameters() && !viewer.isPreviewOnly() && !symmetry.isPeriodic())
      renderInfo(symmetry);
  }
  
  private String nfformat(float x) {
    return (doLocalize && nf != null ? nf.format(x) : TextFormat.formatDecimal(x, 3));
  }

  private void renderInfo(SymmetryInterface symmetry) {
    if (exportType != Graphics3D.EXPORT_NOT
        || !g3d.setColix(viewer.getColixBackgroundContrast()))
      return;
    if (nf == null) {
      nf = NumberFormat.getInstance();
    }

    fid = g3d.getFontFid("Monospaced", 14 * imageFontScaling);

    if (nf != null) {
      nf.setMaximumFractionDigits(3);
      nf.setMinimumFractionDigits(3);
    }
    g3d.setFont(fid);

    int lineheight = (int) (15 * imageFontScaling);
    int x = (int) (5 * imageFontScaling);
    int y = lineheight;

    String spaceGroup = symmetry.getSpaceGroupName();
    if (isPolymer)
      spaceGroup = "polymer";
    else if (isSlab)
      spaceGroup = "slab";
    if (spaceGroup != null & !spaceGroup.equals("-- [--]")) {
      y += lineheight;
      g3d.drawStringNoSlab(spaceGroup, null, x, y, 0);
    }
    y += lineheight;
    g3d.drawStringNoSlab("a="
        + nfformat(symmetry.getUnitCellInfo(JmolConstants.INFO_A)) + "\u00C5",
        null, x, y, 0);
    if (!isPolymer) {
      y += lineheight;
      g3d.drawStringNoSlab(
          "b=" + nfformat(symmetry.getUnitCellInfo(JmolConstants.INFO_B))
              + "\u00C5", null, x, y, 0);
    }
    if (!isPolymer && !isSlab) {
      y += lineheight;
      g3d.drawStringNoSlab(
          "c=" + nfformat(symmetry.getUnitCellInfo(JmolConstants.INFO_C))
              + "\u00C5", null, x, y, 0);
    }
    if (nf != null)
      nf.setMaximumFractionDigits(1);
    if (!isPolymer) {
      if (!isSlab) {
        y += lineheight;
        g3d.drawStringNoSlab("\u03B1="
            + nfformat(symmetry.getUnitCellInfo(JmolConstants.INFO_ALPHA))
            + "\u00B0", null, x, y, 0);
        y += lineheight;
        g3d.drawStringNoSlab("\u03B2="
            + nfformat(symmetry.getUnitCellInfo(JmolConstants.INFO_BETA))
            + "\u00B0", null, x, y, 0);
      }
      y += lineheight;
      g3d.drawStringNoSlab("\u03B3="
          + nfformat(symmetry.getUnitCellInfo(JmolConstants.INFO_GAMMA))
          + "\u00B0", null, x, y, 0);
    }
  }

}

