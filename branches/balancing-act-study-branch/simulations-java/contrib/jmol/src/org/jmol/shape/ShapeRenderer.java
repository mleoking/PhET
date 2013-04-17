/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2010-01-22 04:55:13 -0800 (Fri, 22 Jan 2010) $
 * $Revision: 12180 $
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

import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.ModelSet;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;

public abstract class ShapeRenderer {

  //public void finalize() {
  //  System.out.println("ShapeRenderer " + shapeID + " " + this + " finalized");
  //}
  
  protected Viewer viewer;
  protected JmolRendererInterface g3d;
  protected ModelSet modelSet;
  protected Shape shape;

  protected int myVisibilityFlag;
  protected int shapeID;
  
  //working values
  protected short colix;
  protected short mad;
  protected short madBeg;
  protected short madMid;
  protected short madEnd;
  protected int exportType;

  public final void setViewerG3dShapeID(Viewer viewer, JmolRendererInterface g3d, int shapeID) {
    this.viewer = viewer;
    this.g3d = g3d;
    this.shapeID = shapeID;
    myVisibilityFlag = JmolConstants.getShapeVisibilityFlag(shapeID);
    initRenderer();
  }

  protected void initRenderer() {
  }

  public void render(JmolRendererInterface g3d, ModelSet modelSet, Shape shape) {
    this.g3d = g3d;
    this.modelSet = modelSet;
    this.shape = shape;
    exportType = g3d.getExportType();
    render();
    exportType = Graphics3D.EXPORT_NOT;
  }

  abstract protected void render();
  

}

