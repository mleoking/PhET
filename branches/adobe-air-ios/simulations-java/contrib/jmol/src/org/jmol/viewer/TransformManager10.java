/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-12-18 10:29:29 -0600 (Mon, 18 Dec 2006) $
 * $Revision: 6502 $
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
package org.jmol.viewer;

import org.jmol.util.Logger;

class TransformManager10 extends TransformManager {


  TransformManager10(Viewer viewer) {
    super(viewer);
    perspectiveModel = 10;
  }

  TransformManager10(Viewer viewer, int width, int height) {
    super(viewer, width, height);
    perspectiveModel = 10;
  }

  // older Jmol 10 method 
  // -- applies cameraScaleFactor to scalePixelsPerAngstrom
  // -- no navigation 

  @Override
  protected void calcCameraFactors() {
    //(m) model coordinates
    //(s) screen coordinates = (m) * screenPixelsPerAngstrom
    //(p) plane coordinates = (s) / screenPixelCount
    
    if (Float.isNaN(cameraDepth)) {
      cameraDepth = cameraDepthSetting;
    }

    // distance from the front plane of the model at zoom=100, where p=0 
    cameraDistance = cameraDepth * screenPixelCount; //(s)
    
    // factor to apply as part of the transform
    // 1/f = (cameraDepth + 0.5)/cameraDepth
    // -- note -- in this model 0.02 was added empirically
    cameraScaleFactor = 1.02f + 0.5f / cameraDepth; //(unitless)

    // conversion factor Angstroms --> pixels
    // -- note -- in this model, it depends upon camera position
    scalePixelsPerAngstrom = 
      (scale3D && !perspectiveDepth ? 72 / scale3DAngstromsPerInch : scaleDefaultPixelsPerAngstrom * zoomPercent / 100
        * cameraScaleFactor); //(s/m)

    // model radius in pixels, for slab calculation
    modelRadiusPixels = modelRadius * scalePixelsPerAngstrom;

    // screen offset to fixed rotation center
    // -- note -- in this model, it floats with zoom and camera position
    modelCenterOffset = cameraDistance + modelRadiusPixels; //(s)

    // factor to apply based on screen Z
    // -- note -- in this model, plane 0 is "standard" plane
    // so that all normal adjustments scale DOWN
    referencePlaneOffset = cameraDistance; //(s)
    
    //System.out.println("transformman10: scalppa modelrad visual range " + scalePixelsPerAngstrom + " " + modelRadiusPixels + " " + visualRange);

  }
  
  @Override
  protected float getPerspectiveFactor(float z) {
    // all z's SHOULD be >= 0
    // so the more positive z is, the smaller the screen scale
    //new idea: phase out perspective depth when zoom is very large.
    //zoomPercent 1000 or larger starts removing this effect
    //we can go up to 200000
    float factor = (z <= 0 ? referencePlaneOffset : referencePlaneOffset / z);
    if (zoomPercent >= MAXIMUM_ZOOM_PERSPECTIVE_DEPTH)
      factor += (zoomPercent - MAXIMUM_ZOOM_PERSPECTIVE_DEPTH)
          / (MAXIMUM_ZOOM_PERCENTAGE - MAXIMUM_ZOOM_PERSPECTIVE_DEPTH)
          * (1 - factor);
    return factor;
  }

  @Override
  protected void adjustTemporaryScreenPoint() {

    //fixedRotation point is at the origin initially

    float z = point3fScreenTemp.z;

    //this could easily go negative -- behind the screen --
    //but we don't care. In fact, that just makes it easier, 
    //because it means we won't render it.
    //we should probably assign z = 0 as "unrenderable"

    if (Float.isNaN(z)) {
      if (!haveNotifiedNaN)
        Logger.debug("NaN seen in TransformPoint");
      haveNotifiedNaN = true;
      z = 1;
    } else if (z <= 0) {
      //just don't let z go past 1  BH 11/15/06
      z = 1;
    }
    point3fScreenTemp.z = z;

    // x and y are moved inward (generally) relative to 0, which
    // is either the fixed rotation center or the navigation center

    // at this point coordinates are centered on rotation center

    if (perspectiveDepth) {
      float factor = getPerspectiveFactor(z);
      point3fScreenTemp.x *= factor;
      point3fScreenTemp.y *= factor;
    }

    //now move the center point to where it needs to be

    point3fScreenTemp.x += fixedRotationOffset.x;
    point3fScreenTemp.y += fixedRotationOffset.y;

    if (Float.isNaN(point3fScreenTemp.x) && !haveNotifiedNaN) {
      Logger.debug("NaN found in transformPoint ");
      haveNotifiedNaN = true;
    }
    point3iScreenTemp.set((int) point3fScreenTemp.x, (int) point3fScreenTemp.y,
        (int) point3fScreenTemp.z);
  }
  
  /* ***************************************************************
   * Navigation support
   ****************************************************************/

   // none in this model
}
