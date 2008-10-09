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

import geom.geometry.Line;
import geom.geometry.Point;
import geom.jgv.model.Face;
import geom.jgv.util.HMatrix3D;
import geom.jgv.util.PickLine;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;
import java.awt.Event;

public class Camera3D {
  double fov = 40*Math.PI/180;  // Field of view, in degrees
  double near = 0.05;		// Near clipping plane
  double far = 100.0;		// Far clipping plane
  double focus = 3;		// "focal length" -- distance to nominal point of interest
  public double focalpointW[] = { 0, 0, 0 };

  public int winx = 200, winy = 200;

  public HMatrix3D O2W_Normalized = new HMatrix3D();

  public HMatrix3D O2W = new HMatrix3D(); // Object-to-world matrix -- set by others
  public HMatrix3D C2W = new HMatrix3D();
  public HMatrix3D W2C = new HMatrix3D();

  // derived data
  double EyeO[] = {0,0,0};		  // Eye point in object coords
  public HMatrix3D O2S = new HMatrix3D(); // Object-to-screen matrix

  HMatrix3D C2Proj = new HMatrix3D();	  // Camera projection matrix
				          // (maps vis rgn -> -1..+1 cube)
  HMatrix3D C2O = new HMatrix3D();
  HMatrix3D W2O = new HMatrix3D();

  /* Our (for now) single light, transformed into object coords */
  public double lightO[] = new double[3];

  // These variables used for picking
  public HMatrix3D S2C = new HMatrix3D(); // Screen to camera (for picking)

  public Camera3D() {
    reset();
    preView();
  }

  public final void reset() {
    fov = 40*Math.PI/180;  // Field of view, in degrees
    near = 0.05;		// Near clipping plane
    far = 100.0;		// Far clipping plane
    focus = 3;		// "focal length" -- distance to nominal point of interest
    focalpointW[0] = 0;
    focalpointW[1] = 0;
    focalpointW[2] = 0;

    C2W.unit();
    C2W.translate(0, 0, focus);
    //O2W.unit();

    /* Normalize the world (TODO)
    O2W.scale(.5);
    O2W.translate(.5, .5, 0.0); */
    O2W.copyFrom(O2W_Normalized);
  }

  public void normalize(BSPTree t) {
    O2W_Normalized.unit();
    System.out.println("normalize start");
    if (t != null) {
        Line bb = new Line(new Point(), new Point());
        t.getBoundingBox(bb);
        double cx, cy, cz, dx, dy, dz, r, cs;
        cx = (bb.a.x + bb.b.x) * .5;   // .6
        cy = (bb.a.y + bb.b.y) * .5;
        cz = (bb.a.z + bb.b.z) * .5;
        dx = bb.b.x - bb.a.x;
        dy = bb.b.y - bb.a.y;
        dz = bb.b.z - bb.a.z;
        r = Math.sqrt(dx * dx + dy * dy + dz * dz);
        cs = (r == 0) ? 1 : 2.0/r;
        O2W_Normalized.scale(cs);
        O2W_Normalized.translate( -cx, -cy, -cz);
        O2W.copyFrom(O2W_Normalized);
    }
    System.out.println("normalize done");
  }

  public double getFov() {
        return fov;
  }

  public void setFov(double fov) {
        this.fov = fov;
  }

  /**
   * preView() does various camera-related computations that need to
   * be done in preparation for drawing.  Call it once before each
   * draw pass.
   */
  public void preView() {

    // Set the projection matrix C2Proj
    double aspect = (double)winx / (double)winy;
    //   Tangent of half the Y-direction field of view:
    double tanhyfov = Math.tan(fov / 2) / (winy < winx ? 1 : aspect);
    C2Proj.perspective( -aspect*tanhyfov*near, aspect*tanhyfov*near,
			-tanhyfov*near, tanhyfov*near,
			near, far );

    // Set the world-to-camera matrix (assuming camera-to-world is current)
    W2C.inverseOf(C2W);

    // Set the object-to-screen matrix:
    //   O2S = O2W * W2C * C2Proj * translate * scale
    O2S.unit();
    O2S.scale(.5*winx, -.5*winy, 1);
    O2S.translate(1, -1, 0);
    O2S.mult(C2Proj);

    // Take the inverse of the O2S at this point to get the Screen to Camera matrix
    S2C.inverseOf(O2S);

    O2S.mult(W2C);
    O2S.mult(O2W);

    // Set world-to-object matrix
    W2O.inverseOf(O2W);

    // Set camera-to-object matrix (C20 = C2W * W2O)
    C2O.copyFrom(W2O);
    C2O.mult(C2W);

    // For now, we just have a single distant light appearing from the
    // direction the camera is looking.  That's the camera's -Z
    // vector.  So we set light0, the light's position in object
    // space, by converting it's coords from camera space.
    lightO[0] = -C2O.T[12];
    lightO[1] = -C2O.T[13];
    lightO[2] = -C2O.T[14];
  }

  public double scalez() {
      return focus;
  }

  public double scaley() {
      double aspect = (double)winx / (double)winy;
      return focus * Math.tan(fov / 2) / Math.min(1.0, aspect);
  }

  public double scalex() {
      double aspect = (double)winx / (double)winy;
      return focus * Math.tan(fov / 2) * Math.max(1.0, aspect);
  }

  /** Returns eye-point in object coords as array of 3 doubles */
  public double[] getEyeObj() {
    double origin[] = {0,0,0};
    preView();
    C2O.transformd(origin, EyeO, 1);

    return EyeO;
  }

  public PickLine getPickLine(int x, int y) {
    double point1[] = {x,y,0}; double point2[] = {x,y,0};
    double point1C[] = {0,0,0}; double point2C[] = {0,0,0};

    // Transform points from screen coordinates into camera coordinates
    S2C.transformd(point1, point1C, 1);
    S2C.transformd(point2, point2C, 1);

    // Construct a pick line in front of the camera clip plane
    // that is perpendicular to the screen
    point1C[2] = -.1;
    point2C[0] *= 100; point2C[1] *= 100; point2C[2] = -10;

    // Transform the pick line into object coordinates
    C2O.transformd(point1C, point1, 1);
    C2O.transformd(point2C, point2, 1);

    PickLine pickLine = new PickLine(new Point(point1[0], point1[1], point1[2]),
                             new Point(point2[0], point2[1], point2[2]));
    pickLine.x = x; pickLine.y = y;

    /*
    System.out.println("Pick Line in Object Coordinates is:\n" +
                       "(" + point1[0] + ", " + point1[1] + ", " + point1[2] + ") - " +
                       "(" + point2[0] + ", " + point2[1] + ", " + point2[2] + ")\n");
    */
    return pickLine;
  }


  public final void setSize(int wx, int wy) {
    winx = wx;
    winy = wy;
  }

  public String toString() {
    return ""+winx+","+winy;
  }
}

