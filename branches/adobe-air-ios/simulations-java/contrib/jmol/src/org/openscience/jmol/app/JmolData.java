/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2009-06-26 23:35:44 -0500 (Fri, 26 Jun 2009) $
 * $Revision: 11131 $
 *
 * Copyright (C) 2000-2005  The Jmol Development Team
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
package org.openscience.jmol.app;

import java.awt.Dimension;
import org.jmol.api.JmolViewer;
import org.jmol.util.TextFormat;

public class JmolData {
  
  /*
   * no Java Swing to be found. No implementation of any graphics or 
   * containers at all. No shapes, no export, no writing of images,
   *  -- only the model and its associated data.
   * 
   * Just a great little answer machine that can load models, 
   * do scripted analysis of their structures, and spit out text
   * 
   */

  public JmolApp jmolApp;
  public JmolViewer viewer;
  
  public static JmolData getJmol(int width, int height, String commandOptions) {
    JmolApp jmolApp = new JmolApp();
    jmolApp.haveDisplay = false;
    jmolApp.startupHeight = height;
    jmolApp.startupWidth = width;
    jmolApp.isDataOnly = true;
    String[] args = TextFormat.split(commandOptions, ' '); // doesn't allow for double-quoted 
    jmolApp.parseCommandLine(args);
    return new JmolData(jmolApp);
  }

  public JmolData(JmolApp jmolApp) {
    this.jmolApp = jmolApp;
    viewer = JmolViewer.allocateViewer(null, null, 
        null, null, null, jmolApp.commandOptions, null);
    viewer.setScreenDimension(new Dimension(jmolApp.startupWidth, jmolApp.startupHeight));
    jmolApp.startViewer(viewer, null);
  }
  
  public static void main(String[] args) {
    // note that -o -n -x are all implied
    JmolApp jmolApp = new JmolApp();
    jmolApp.isDataOnly = true;
    jmolApp.haveConsole = false;
    jmolApp.haveDisplay = false;
    jmolApp.exitUponCompletion = true;
    jmolApp.parseCommandLine(args);    
    new JmolData(jmolApp);
  }
  
}  

