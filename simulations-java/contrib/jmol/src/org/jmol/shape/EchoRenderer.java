/* $RCSfile$
 * $Author: nicove $
 * $Date: 2010-08-06 16:30:20 -0700 (Fri, 06 Aug 2010) $
 * $Revision: 13880 $
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

import java.util.Iterator;

import javax.vecmath.Point3i;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;

public class EchoRenderer extends ShapeRenderer {

  float imageFontScaling;
  Atom ptAtom;
  Point3i pt = new Point3i();
  @Override
  protected void render() {
    if (viewer.isPreviewOnly())
      return;
    Echo echo = (Echo)shape;
    Iterator<Text> e = echo.objects.values().iterator();
    float scalePixelsPerMicron = (viewer.getFontScaling() ? viewer.getScalePixelsPerAngstrom(true) * 10000 : 0);
    imageFontScaling = viewer.getImageFontScaling();
    while (e.hasNext()) {
      Text t = e.next();
      if (!t.visible || t.hidden) {
        continue;
      }
      if (t.valign == Object2d.VALIGN_XYZ) {
        viewer.transformPoint(t.xyz, pt);
        t.setXYZs(pt.x, pt.y, pt.z, pt.z);
      } else if (t.movableZPercent != Integer.MAX_VALUE) {
        int z = viewer.zValueFromPercent(t.movableZPercent);
        t.setZs(z, z);
      }
      t.render(g3d, scalePixelsPerMicron, imageFontScaling, false);
    }
    String frameTitle = viewer.getFrameTitle();
    if (frameTitle != null && frameTitle.length() > 0) {
      if (frameTitle.indexOf("%{") >= 0 || frameTitle
          .indexOf("@{") >= 0) 
        frameTitle =  viewer.formatText(frameTitle);
      renderFrameTitle(frameTitle);
    }
  }
  
  private void renderFrameTitle(String frameTitle) {
    if (exportType != Graphics3D.EXPORT_NOT || !g3d.setColix(viewer.getColixBackgroundContrast()))
      return;
    byte fid = g3d.getFontFid("Monospaced", 14 * imageFontScaling);
    g3d.setFont(fid);
    int y = (int) (viewer.getScreenHeight() * (g3d.isAntialiased() ? 2 : 1) - 10 * imageFontScaling);
    int x = (int) (5 * imageFontScaling);
    g3d.drawStringNoSlab(frameTitle, null, x, y, 0);
  }
}
