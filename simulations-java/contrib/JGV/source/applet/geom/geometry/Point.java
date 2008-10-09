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

public class Point extends Object {
  public double x, y, z;

  public Point() {
    x = 0;
    y = 0;
    z = 0;
  }

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Point(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Point(Point p) {
    x = p.x;
    y = p.y;
    z = p.z;
  }

  public final Object clone() {
    return new Point(x, y, z);
  }

  public final Point cross(Point p) {
    double i = (this.y * p.z) - (this.z * p.y);
    double j = -((this.x * p.z) - (this.z * p.x));
    double k = (this.x * p.y) - (this.y * p.x);

    return new Point (i, j, k);
  }

  public final double dot(Point p) {
    return this.x * p.x + this.y * p.y + this.z * p.z;
  }

  public boolean equals(Point p) {
    if (p == null) return false;
    else return (p.x == x && p.y == y);
  }

  public final Point minus(Point p) {
    double i = this.x - p.x;
    double j = this.y - p.y;
    double k = this.z - p.z;

    return new Point(i,j,k);
  }

  public final Point plus(Point p) {
    double i = this.x + p.x;
    double j = this.y + p.y;
    double k = this.z + p.z;

    return new Point(i,j,k);
  }

  // Returns n * <this>
  public final Point scalarMult(double n) {
    double i = this.x * n;
    double j = this.y * n;
    double k = this.z * n;

    return new Point(i,j,k);
  }

  // Classifies a point with respect to a plane by passing the (x,y,z) values
  // of the point into the equation:
  // c = Ax + By + Cz + D.
  // If c > 0, then the point is on the side of the plane pointed to by the
  // normal vector. If c == 0, then the point is on the plane. Else, it
  // is on the negative side.
  public final double classify(Plane p) {
    double D =
        (p.point.x * p.normal.x)
      + (p.point.y * p.normal.y)
      + (p.point.z * p.normal.z);

    return   (p.normal.x * this.x)
           + (p.normal.y * this.y)
           + (p.normal.z * this.z)
           - D;
  }

  public String toString() {
    return new String("("+x+", "+y+", "+z+")");
  }
}
