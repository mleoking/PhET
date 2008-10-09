//
// Copyright (C) 1998-2000 Geometry Technologies, Inc. <info@geomtech.com>
// Copyright (C) 2002-2004 DaerMar Communications, LLC <jgv@daermar.com>
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package geom.jgv.view;

import geom.jgv.model.Face;
import geom.jgv.model.Geom;
import geom.jgv.model.PickSet;
import geom.jgv.util.HMatrix3D;
import geom.jgv.util.PolyConstants;
import geom.geometry.Plane;
import geom.geometry.Point;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Color;

public class Render {
  // rendering scratch space
  int xverts[] = new int[200];
  int yverts[] = new int[200];
  double zverts[] = new double[200];

  static int pointRadius = 6;
  static int pointOffset = pointRadius/2;

  public PickSet picked = null;
  private boolean drawPickPoint;
  private boolean drawActionString;

  public Render() {
  }

  /**
   * Render a single face in the canvas, as seen from this.eye.
   */
  public void renderFace(Face f, Graphics gr, Camera3D camera) {

    HMatrix3D O2S = camera.O2S;
    int r, g, b;
    double nw;
    Plane pl;

    // First transform all the vertices into screen space.  We access
    // the elements of the O2S HMatrix3D directly for speed, rather
    // than calling its linear algebra methods, and so that we don't
    // need to unpack all those Points into an array.
    for (int i = 0; i < f.nverts; i++) {
      Point cp = f.vertices[i];
      nw =   O2S.T[3]  * cp.x
	   + O2S.T[7]  * cp.y
	   + O2S.T[11] * cp.z
	   + O2S.T[15];
      xverts[i] = (int)((  O2S.T[0]*cp.x
			 + O2S.T[4]*cp.y
			 + O2S.T[8]*cp.z
			 + O2S.T[12]     ) / nw);
      yverts[i] = (int)((  O2S.T[1]*cp.x
			 + O2S.T[5]*cp.y
			 + O2S.T[9]*cp.z
			 + O2S.T[13]     ) / nw);
      zverts[i] = ((  O2S.T[2]*cp.x
			 + O2S.T[6]*cp.y
			 + O2S.T[10]*cp.z
			 + O2S.T[14]     ) / nw);
      // Don't draw the face if any part of it falls behind the clipping plane
      if (zverts[i] > 1.0) return;
    }

    drawPickPoint = false;
    drawActionString = false;
    Color c;
    switch (f.nverts) {
    case 1:
      // It's a point (or possibly an annotation)
      c = getColor(new Color(f.cr, f.cg, f.cb), f);


      if (f.label != null) {
          // Annotation
          gr.setFont(new Font("Helvetica", Font.PLAIN, 12));
          FontMetrics fm = gr.getFontMetrics();
          f.labelWidth = fm.stringWidth(f.label);
          f.labelHeight = fm.getHeight();
          int strOffset = f.labelWidth/2;
          gr.setColor(new Color(0, 0, 0));
          int xpos = xverts[0] - pointOffset - strOffset;
          int ypos = yverts[0] - pointOffset;
          gr.drawString(f.label, xpos + 1, ypos + 1 );
          gr.setColor( c );
          gr.drawString(f.label, xpos, ypos);
          if (picked != null) {
            if (f.equals(picked.getPickedFace())) {
              gr.setColor(new Color(0, 0, 0));
              gr.drawRect(xpos - 1, ypos - f.labelHeight + 3,
                          strOffset * 2 + 5, f.labelHeight + 2);
              gr.setColor(new Color(255, 255, 255));
              gr.drawRect(xpos - 2, ypos - f.labelHeight + 2,
                          strOffset * 2 + 5, f.labelHeight + 2);
            }
          }
      } else {
          // Point
          gr.setColor( c );
          gr.fillOval(xverts[0]-pointOffset, yverts[0]-pointOffset,
		  pointRadius, pointRadius);
      }
      break;
    case 2:
      // It's a line segment
      c = getColor(new Color(f.cr, f.cg, f.cb), f);
      gr.setColor( c );

      gr.drawLine(xverts[0], yverts[0], xverts[1], yverts[1]);
      break;
    default:
      // It's a polygon

      // Dot product of surface normal with light vector
      // for ordinary matte shading model
      if (f.parent.appearance.drawfaces) {
	pl = f.getPlane();
	float dot = (float)(pl.normal.x*camera.lightO[0] +
			    pl.normal.y*camera.lightO[1] +
			    pl.normal.z*camera.lightO[2] );
	float s =
	  (float)Math.abs(dot) /
	    (float) Math.sqrt( pl.normal.dot(pl.normal)
			      * (camera.lightO[0]*camera.lightO[0] +
				 camera.lightO[1]*camera.lightO[1] +
				 camera.lightO[2]*camera.lightO[2]));

	float cs = (s*.8f + .2f)/255f;	// .2 "ambient" -- don't go completely black

        c = getColor(new Color(
			       f.cr*cs,
			       //(dot<0 ? .5f*f.cg*cs : f.cg*cs), // Back-facing magenta
			       f.cg*cs,
			       f.cb*cs), f);
        gr.setColor(c);
	gr.fillPolygon(xverts, yverts, f.nverts);
        gr.drawPolygon(xverts, yverts, f.nverts);

      }

      if (f.parent.appearance.drawedges) {
	gr.setColor(Color.black);
	gr.drawPolygon(xverts, yverts, f.nverts);
      }
    }
    if (drawPickPoint) {
      gr.setColor(new Color(255, 0, 0));
      Point cp = picked.getPickedPoint(f);
      nw =   O2S.T[3]  * cp.x
           + O2S.T[7]  * cp.y
           + O2S.T[11] * cp.z
           + O2S.T[15];
      xverts[0] = (int)((  O2S.T[0]*cp.x
                   + O2S.T[4]*cp.y
                   + O2S.T[8]*cp.z
                   + O2S.T[12]     ) / nw);
      yverts[0] = (int)((  O2S.T[1]*cp.x
                   + O2S.T[5]*cp.y
                   + O2S.T[9]*cp.z
                   + O2S.T[13]     ) / nw);
      zverts[0] = ((  O2S.T[2]*cp.x
                   + O2S.T[6]*cp.y
                   + O2S.T[10]*cp.z
			 + O2S.T[14]     ) / nw);
      gr.fillOval(xverts[0]-pointOffset, yverts[0]-pointOffset,
		  pointRadius, pointRadius);
    }
    if (drawActionString) {
      gr.setFont(new Font("Helvetica", Font.PLAIN, 12));
      c = new Color(0,0,0);
      gr.setColor(c);
      gr.drawString(picked.getPickedGeom().getAction().toString(), 5,
                    camera.winy - gr.getFontMetrics().getHeight()/2 + 1);
      c = new Color(255,255,255);
      c.darker();
      gr.setColor(c);
      gr.drawString(picked.getPickedGeom().getAction().toString(), 4,
                    camera.winy - gr.getFontMetrics().getHeight()/2);
    }
  }

  private Color getColor(Color original, Face f) {
    Color retVal = original;
    if (picked != null) {
      if (picked.isMouseover()) {
        Geom p = picked.getPickedGeom();
        if (p != null) {
          if (picked.getPickedGeom().equals(f.parent) && p.getAction() != null) {
            if (f.label == null) {
              retVal = original.brighter();
            } else {
              retVal = original;

            }
            drawActionString = true;
          }
        }
      } else if (picked.contains(f)) {
        drawPickPoint = true;
        if (picked.getPickedFace().equals(f)) {
          retVal = new Color(0, 255, 255);
        } else {
          retVal = new Color(0, 255, 0);
        }
      }
    }
    return retVal;
  }
}