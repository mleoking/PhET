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

import geom.geometry.Point;

public class Triangle {
    public Point a = null;
    public Point b = null;
    public Point c = null;

    public Triangle() { }

    public Triangle(Point aa, Point bb, Point cc) {
        if (aa.equals(bb) || aa.equals(cc) || bb.equals(cc)) return;

        a = (Point)aa.clone();
	b = (Point)bb.clone();
	c = (Point)cc.clone();
    }

    public Triangle(Triangle t) {
        a = (Point)t.a.clone();
	b = (Point)t.b.clone();
	c = (Point)t.c.clone();
    }

    public Object clone() {
        return new Triangle(a, b, c);
    }

    public boolean equals(Triangle t) {
        if (!t.a.equals(a) ||
	    !t.a.equals(b) ||
	    !t.a.equals(c))
	    return false;
        if (!t.b.equals(a) ||
	    !t.b.equals(b) ||
	    !t.b.equals(c))
	    return false;
        if (!t.c.equals(a) ||
	    !t.c.equals(b) ||
	    !t.c.equals(c))
	    return false;

        return true;
    }
}
