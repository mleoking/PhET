/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2009-07-31 09:22:19 -0500 (Fri, 31 Jul 2009) $
 * $Revision: 11291 $
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
package org.jmol.multitouch;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.SwingUtilities;
import javax.vecmath.Point3f;

import org.jmol.util.Logger;
import org.jmol.viewer.Viewer;

public abstract class JmolMultiTouchClientAdapter implements JmolMultiTouchAdapter {

  protected JmolMultiTouchClient actionManager;
  protected Component display;
  protected boolean isServer;
  
  public boolean isServer() {
    return isServer;
  }
  
  // methods Jmol needs -- from viewer.ActionManagerMT

  public abstract void dispose();
  
  public boolean setMultiTouchClient(Viewer viewer, JmolMultiTouchClient client,
                              boolean isSimulation) {
    this.display = viewer.getDisplay();
    actionManager = client; // ActionManagerMT
    return true;
  }
  
  private static int screenWidth, screenHeight;
  static {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    screenWidth = screen.width;
    screenHeight = screen.height;
    if (Logger.debugging)
      Logger.info("screen resolution: " + screenWidth + " x " + screenHeight);
  }
  
  public void mouseMoved(int x, int y) {
    // for debugging purposes
    //System.out.println("mouseMove " + x + " " + y);
  }

  protected Point xyTemp = new Point();
  protected Point3f ptTemp = new Point3f();
  protected void fixXY(float x, float y, boolean isAbsolute) {
    xyTemp.setLocation(x * screenWidth, y * screenHeight);
    if (isAbsolute)
      SwingUtilities.convertPointFromScreen(xyTemp, display);
    ptTemp.set(xyTemp.x, xyTemp.y, Float.NaN);
  }
} 
