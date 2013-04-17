/* $RCSfile$
 * $Author: nicove $
 * $Date: 2010-07-31 02:51:00 -0700 (Sat, 31 Jul 2010) $
 * $Revision: 13783 $
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

package org.jmol.shape;

import org.jmol.g3d.Font3D;
import org.jmol.i18n.GT;

import java.awt.FontMetrics;
import java.util.BitSet;

public class Frank extends FontShape {

  // Axes, Bbcage, Frank, Uccage

  final static String defaultFontName = "SansSerif";
  final static String defaultFontStyle = "Bold";
  final static int defaultFontSize = 16;
  final static int frankMargin = 4;

  String frankString = "Jmol";
  Font3D currentMetricsFont3d;
  Font3D baseFont3d;
  int frankWidth;
  int frankAscent;
  int frankDescent;
  int x, y, dx, dy;

  @Override
  public void initShape() {
    super.initShape();
    myType = "frank";
    baseFont3d = font3d = g3d.getFont3D(defaultFontName, defaultFontStyle, defaultFontSize);
    calcMetrics();
  }

  @Override
  public boolean wasClicked(int x, int y) {
    int width = viewer.getScreenWidth();
    int height = viewer.getScreenHeight();
    return (width > 0 && height > 0 
        && x > width - frankWidth - frankMargin
        && y > height - frankAscent - frankMargin);
  }

  @Override
  public boolean checkObjectHovered(int x, int y, BitSet bsVisible) {
    if (!wasClicked(x, y) || !viewer.menuEnabled())
      return false;
    if (g3d.isDisplayAntialiased()) {
      //because hover rendering is done in FIRST pass only
      x <<= 1;
      y <<= 1;
    }      
    viewer.hoverOn(x, y, GT._("Click for menu..."));
    return true;
  }
  
  void calcMetrics() {
    if (viewer.isSignedApplet())
      frankString = "" + 'J' + 'm' + 'o' + 'l' + '_' + 'S';
    if (font3d == currentMetricsFont3d) 
      return;
    currentMetricsFont3d = font3d;
    FontMetrics fm = font3d.fontMetrics;
    frankWidth = fm.stringWidth(frankString);
    frankDescent = fm.getDescent();
    frankAscent = fm.getAscent();
  }

  void getFont(float imageFontScaling) {
    font3d = g3d.getFont3DScaled(baseFont3d, imageFontScaling);
    calcMetrics();
  }
}
