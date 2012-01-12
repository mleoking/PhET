/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-09-17 05:53:18 -0700 (Fri, 17 Sep 2010) $
 * $Revision: 14321 $
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
package org.jmol.api;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;

import org.jmol.viewer.Viewer;

/**
 * This is the high-level API for the JmolViewer for simple access.
 **/

abstract public class JmolSimpleViewer {

  /**
   *  This is the main access point for creating an application
   *  or applet viewer. 
   *    
   * @param container
   * @param jmolAdapter
   * @return              a JmolViewer object
   */
  static public JmolSimpleViewer
    allocateSimpleViewer(Container container, JmolAdapter jmolAdapter) {
    return Viewer.allocateViewer(container, jmolAdapter, 
        null, null, null, null, null);
  }

  abstract public void renderScreenImage(Graphics g, Dimension size,
                                         Rectangle clip);

  abstract public String evalFile(String strFilename);
  abstract public String evalString(String strScript);

  abstract public String openStringInline(String strModel);
  abstract public String openDOM(Object DOMNode);
  abstract public String openFile(String fileName);
  abstract public String openFiles(String[] fileNames);
  // File reading now returns the error directly.
  // The following was NOT what you think it was:
  //   abstract public String getOpenFileError();
  // Somewhere way back when, "openFile" became a method that did not create
  // the model set, but just an intermediary AtomSetCollection called the "clientFile"
  // (and did not necessarily close the file)
  // then "getOpenFileError()" actually created the model set, deallocated the file open thread,
  // and closed the file.
  //
  // For Jmol 11.7.14, the openXXX methods in this interface do everything --
  // open the file, create the intermediary atomSetCollection, close the file,
  // deallocate the file open thread, create the ModelSet, and return any error message.
  // so there is no longer any need for getOpenFileError().
  
  /**
   * @param returnType "JSON", "string", "readable", and anything else returns the Java object.
   * @param infoType 
   * @param paramInfo  
   * @return            property data -- see org.jmol.viewer.PropertyManager.java
   */
  abstract public Object getProperty(String returnType, String infoType, Object paramInfo);
}
