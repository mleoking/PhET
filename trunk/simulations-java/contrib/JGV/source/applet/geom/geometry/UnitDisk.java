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
import geom.geometry.Point;

public class UnitDisk extends Canvas {
    public boolean draw_origin = false;
    public int size = 0;

    public Hashtable objects;

    // double buffering
    Dimension offScreenSize = null;
    Graphics offScreenGraphics = null;
    Image offScreenImage = null;

    public UnitDisk(int s) {
        super();
	objects = new Hashtable();
	size = s;
    }

    public void addLine(Line l) {
	objects.put(l.toString(), l);
    }

    public void addPoint(Point p) {
        objects.put(p.toString(), p);
    }

    public void addTriangle(Triangle t) {
        objects.put(t.toString(), t);
    }

    public void clear() {
      for (Enumeration e = objects.elements(); e.hasMoreElements(); ) {
	Object o = e.nextElement();
	objects.remove(o);
      }
    }

    public void drawLine(Graphics g, Line l) {
        // convert to screen coords
        int ax = (int)(l.a.x * size);
	int ay = (int)(l.a.y * -size);
	int bx = (int)(l.b.x * size);
	int by = (int)(l.b.y * -size);

	g.drawLine(ax, ay, bx, by);
    }

    public void drawLineSegment(Graphics g, Line l) {
        drawLine(g, l);
    }

    public void drawPoint(Graphics g, Point p) {
        // convert to screen coords
        int x = (int)(p.x * size);
	int y = (int)(p.y * -size);

	g.fillOval(x - 2, y - 2, 4, 4);
    }

    public void drawTriangle(Graphics g, Triangle t) {
        // convert to screen coords
	int ax = (int)(t.a.x * size);
	int ay = (int)(t.a.x * -size);
	int bx = (int)(t.b.x * size);
	int by = (int)(t.b.x * -size);
	int cx = (int)(t.c.x * size);
	int cy = (int)(t.c.x * -size);

        g.drawLine(ax, ay, bx, by);
	g.drawLine(bx, by, cx, cy);
	g.drawLine(cx, cy, ax, ay);
    }

    // returns the center of the circle containing points a & b which intersects
    // the unit disk at right angles
    public static Point findCenter(Point a, Point b) {
        double c1 = (a.x*a.x*b.y - b.x*b.x*a.y + a.y*a.y*b.y -
	             a.y*b.y*b.y + b.y - a.y) /
	            (2 * (b.y*a.x - a.y*b.x));
	double c2 = (b.y*b.y*a.x - a.y*a.y*b.x + a.x*b.x*b.x -
	             a.x*a.x*b.x + a.x - b.x) /
	            (2 * (b.y*a.x - a.y*b.x));

	Point c = new Point(c1, c2);

	return c;
    }

    public void paint(Graphics g) {
        g.setColor(Color.white);
	g.fillRect(0, 0, getSize().width, getSize().height);

        // translate origin
	g.translate(size, size);

        g.setColor(Color.black);

        // draw boundary
	g.drawOval(-size, -size, 2*size - 1, 2*size - 1);

	// draw origin
	if (draw_origin) g.fillOval(0, 0, 4, 4);

	for (Enumeration e = objects.elements(); e.hasMoreElements(); ) {
            Object o = e.nextElement();

	    // Points
	    if (o.getClass().getName().equals("geom.geometry.Point"))
	        drawPoint(g, (Point) o);
	    else if (o.getClass().getName().equals("geom.geometry.Line")) {
	        drawLine(g, (Line) o);
		drawPoint(g, ((Line)o).a);
		drawPoint(g, ((Line)o).b);
	    }
	    else if (o.getClass().getName().equals("geom.geometry.Triangle"))
	        drawTriangle(g, (Triangle)o);
	}

	g.translate(-size, -size);  // detranslate so update() doesn't biff
    }

    public Dimension getPreferredSize() {
        return new Dimension(size*2, size*2);
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

    public double[] viewportToWorld(int x, int y) {
      double w[] = new double[2];

      x -= size;
      y -= size;
      y *= -1;

      w[0] = (double)(x) / size;
      w[1] = (double)(y) / size;

      return w;
    }

    public int[] worldToViewport(double x, double y) {
      int w[] = new int[2];

      w[0] = (int)(x * size);
      w[1] = (int)(y * size);

      w[1] *= -1;
      w[0] += size;
      w[1] += size;

      return w;
    }
}
