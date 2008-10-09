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

package geom.geometry;

import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;

import geom.geometry.Line;

public class PlaneCanvas extends Canvas {
  public double corner[] = new double[2];
  public double size[] = new double[2];
  public int prefsize[] = new int[2];

  public boolean draw_axes = false;

  public Hashtable objects;

  protected double reset[] = new double[4];

  // double buffering
  Dimension offScreenSize = null;
  Graphics offScreenGraphics = null;
  Image offScreenImage = null;

  public PlaneCanvas(double cx, double cy, double sx, double sy) {
    super();

    corner[0] = cx;
    corner[1] = cy;
    size[0] = sx;
    size[1] = sy;
    reset[0] = cx;
    reset[1] = cy;
    reset[2] = sx;
    reset[3] = sy;

    objects = new Hashtable();
  }

  public PlaneCanvas(double cx, double cy, double sx, double sy, int x, int y) {
    super();

    corner[0] = cx;
    corner[1] = cy;
    size[0] = sx;
    size[1] = sy;
    reset[0] = cx;
    reset[1] = cy;
    reset[2] = sx;
    reset[3] = sy;

    prefsize[0] = x;
    prefsize[1] = y;

    objects = new Hashtable();
  }

  public void clear() {
    for (Enumeration e = objects.elements(); e.hasMoreElements(); ) {
      Object o = e.nextElement();
      objects.remove(o);
    }
  }

  public void drawTickmarks(Graphics g) {
    double logten = Math.log(10);
    double powx = Math.floor(Math.log(size[0]/10)/logten);
    double powy = Math.floor(Math.log(size[1]/10)/logten);
    double incx = Math.pow(10, powx);
    double incy = Math.pow(10, powy);

    double startx = corner[0];
    if (powx != 0) {
      startx *= 10 * powx;
      startx = Math.round(startx);
      startx /= 10 * powx;
    }
    else startx = Math.round(startx);

    double starty = corner[0];
    if (powx != 0) {
      starty *= 10 * powx;
      starty = Math.round(starty);
      starty /= 10 * powx;
    }
    else starty = Math.round(starty);

    g.setColor(Color.gray);

    incx *= Math.floor(size[0] / incx / 6);
    incy *= Math.floor(size[1] / incy / 6);

    g.setFont(new Font("courier", Font.PLAIN, 10));

    int j = 0;
    for (double i = corner[0]; i < corner[0]+size[0]; i += incx) {
      double ax1 = corner[0];
      double ay1 = corner[1] - size[1] + j*incy;
      int x1 = worldToViewport(corner[0], corner[1] - size[1] + j*incy)[0];
      int y1 = worldToViewport(corner[0], corner[1] - size[1] + j*incy)[1];
      double bx1 = corner[0] + j*incx;
      double by1 = corner[1] - size[1];
      int x2 = worldToViewport(corner[0] + j*incx,
                               corner[1] - size[1])[0];
      int y2 = worldToViewport(corner[0] + j*incx,
                               corner[1] - size[1])[1];

      g.drawLine(x1, y1, x1+5, y1);
      g.drawLine(x2, y2, x2, y2-5);

      if (Double.toString(ay1).length() < 6 || Math.abs(ay1) < 0.001)
        g.drawString(Double.toString(ay1), x1+7, y1);
      else g.drawString(Double.toString(ay1).substring(0,5), x1+7, y1);

      if (Double.toString(bx1).length() < 6 || Math.abs(bx1) < 0.0001)
        g.drawString(Double.toString(bx1), x2, y2-10);
      else g.drawString(Double.toString(bx1).substring(0,5), x2, y2-10);

      j++;
    }
  }

  public void paint(Graphics g) {
    if (getSize().width != prefsize[0] || getSize().height != prefsize[1])
      setSize(prefsize[0], prefsize[1]);

    g.setColor(Color.white);
    g.fillRect(0, 0, getSize().width, getSize().height);

    g.setColor(Color.black);

/*
    // reset aspect ratio
    if (size().width > size().height)
      size[1] = size[0] * (double)size().height / (double)size().width;
    else size[0] = size[1] * (double)size().width / (double)size().height;

System.out.println(corner[0]+","+corner[1]+"x"+size[0]+","+size[1]);
*/

    int origin[] = worldToViewport(0, 0);

    // draw axes
    if (draw_axes) {
      g.setColor(Color.gray);

      g.drawLine(origin[0], -getSize().height, origin[0], getSize().height);
      g.drawLine(-getSize().width, origin[1], getSize().width, origin[1]);
    }

    g.setColor(Color.black);
  }

  public Dimension getPreferredSize() {
    return new Dimension(prefsize[0], prefsize[1]);
  }

  public void reset() {
    corner[0] = reset[0];
    corner[1] = reset[1];
    size[0] = reset[2];
    size[1] = reset[3];

    repaint();
  }

  public String toString() {
    return new String("["+Double.toString(corner[0])+","+
                      Double.toString(corner[0]+size[0])+"] x ["+
                      Double.toString(corner[1])+","+
		      Double.toString(corner[1]-size[1])+"]");
  }

  public double[] viewportToWorld(int x, int y) {
    double v[] = new double[2];

    double factorx = ((double)getSize().width) / size[0];
    double factory = ((double)getSize().height) / size[1];

    v[0] = ((double)x / factorx) + corner[0];
    v[1] = ((double)y - getSize().height) / (-1*factory) + (corner[1] - size[1]);

    return v;
  }

  public final synchronized void update (Graphics g) {
    Dimension d = getSize();
    if ((offScreenImage == null) || (d.width != offScreenSize.width) ||
	(d.height != offScreenSize.height)) {
      offScreenImage = createImage(d.width, d.height);
      offScreenSize = d;
      offScreenGraphics = offScreenImage.getGraphics();
      offScreenGraphics.setFont(getFont());
    }
    paint(offScreenGraphics);
    g.drawImage(offScreenImage, 0, 0, this);
  }

  public int[] worldToViewport(double x, double y) {
    int v[] = new int[2];

    double factorx = ((double)getSize().width) / size[0];
    double factory = ((double)getSize().height) / size[1];

    v[0] = (int)((x - corner[0]) * factorx);
    v[1] = (int)(-1*(y - (corner[1] - size[1])) * factory + getSize().height);

    return v;
  }
}
