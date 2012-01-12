/* $RCSfile$
 * $Author: egonw $
 * $Date: 2005-11-10 09:52:44 -0600 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
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
package org.jmol.shapesurface;

import java.text.NumberFormat;

import org.jmol.g3d.Graphics3D;



public class MolecularOrbitalRenderer extends IsosurfaceRenderer {

  private NumberFormat nf;

  @Override
  protected void render() {
    imageFontScaling = viewer.getImageFontScaling();
    MolecularOrbital mo = (MolecularOrbital) shape;
    int modelIndex = viewer.getCurrentModelIndex();
    for (int i = mo.meshCount; --i >= 0;)
      if (render1(imesh = (IsosurfaceMesh) mo.meshes[i]) && modelIndex >= 0)
        renderInfo();
  }

  private void renderInfo() {
    if (mesh.title == null || exportType != Graphics3D.EXPORT_NOT 
        || !g3d.setColix(viewer.getColixBackgroundContrast()))
      return;
    if (nf == null)
      nf = NumberFormat.getInstance();
    if (nf != null) {
      nf.setMaximumFractionDigits(3);
      nf.setMinimumFractionDigits(3);
    }
    byte fid = g3d.getFontFid("Monospaced", 14 * imageFontScaling);
    g3d.setFont(fid);
    int lineheight = (int) (15 * imageFontScaling);
    int x = (int) (5 * imageFontScaling);
    int y = lineheight;

    for (int i = 0; i < mesh.title.length; i++)
      if (mesh.title[i].length() > 0) {
        g3d.drawStringNoSlab(mesh.title[i], null, x, y, 0);
        y += lineheight;
      }
  }

  /*
  private String nfformat(float x) {
    if (nf == null)
      return "" + x;
    return nf.format(x);
  }
*/
}
