/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2009-06-30 18:58:33 -0500 (Tue, 30 Jun 2009) $
 * $Revision: 11158 $
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
package org.jmol.modelkit;

import org.jmol.api.*;
import org.jmol.i18n.GT;
import org.jmol.util.Logger;
import org.jmol.viewer.Viewer;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class ModelKit extends JDialog implements JmolModelKitInterface {

  JmolViewer viewer;
  ModelKitPopup modelkitMenu;

  public ModelKit() {
    // necessary for reflection
  }

  /* (non-Javadoc)
   * @see org.jmol.modelkit.JmolModelKitInterface#getModelKit(org.jmol.viewer.Viewer, javax.swing.JFrame)
   */
  public JmolModelKitInterface getModelKit(Viewer viewer, Component display) {
    return new ModelKit(viewer,  display instanceof JmolFrame ? ((JmolFrame) display).getFrame() 
        : display instanceof JFrame ? (JFrame) display : null);
  }

  /**
   * @param parentFrame
   * @param viewer
   */
  private ModelKit(JmolViewer viewer, JFrame parentFrame) {
    super(parentFrame, "", false);
    this.viewer = viewer;
    
  }



  /* (non-Javadoc)
   * @see org.jmol.modelkit.JmolModelKitInterface#getMenus(boolean)
   */
  public void getMenus(boolean doTranslate) {
    GT.setDoTranslate(true);
    try {
      modelkitMenu = new ModelKitPopup(viewer, "modelkitMenu",
          new ModelKitPopupResourceBundle());
    } catch (Exception e) {
      Logger.error("Modelkit menus not loaded");
    }
    GT.setDoTranslate(doTranslate);
    
  }
  
  /* (non-Javadoc)
   * @see org.jmol.modelkit.JmolModelKitInterface#show(int, int, java.lang.String)
   */
  public void show(int x, int y, char type) {
      modelkitMenu.show(x, y);
  }  
}
