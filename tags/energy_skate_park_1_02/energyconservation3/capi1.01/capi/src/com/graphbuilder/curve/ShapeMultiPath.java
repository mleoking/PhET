package com.graphbuilder.curve;

import java.awt.*;
import java.awt.geom.*;
import com.graphbuilder.geom.Geom;
import sun.awt.geom.Curve;

/**
The ShapeMultiPath is-a MultiPath and implements the java.awt.Shape interface.
Here is an example of how to use a ShapeMultiPath:

<pre>
ControlPath cp = new ControlPath();
cp.addPoint(...); // add points
Curve c = new BezierCurve(cp, new GroupIterator("0:n-1"));

ShapeMultiPath smp = new ShapeMultiPath();
c.appendTo(smp);

Graphics2D g = ...;
g.draw(smp);

</pre>
*/
public class ShapeMultiPath extends MultiPath implements Shape {

	private int windingRule = PathIterator.WIND_EVEN_ODD;
	private int ai0 = 0;
	private int ai1 = 1;

	/**
	Constructs a new ShapeMultiPath with a dimension of 2.
	*/
	public ShapeMultiPath() {
		super(2);
	}

	/**
	Constructs a new ShapeMultiPath with the specified dimension requirement.

	@throws IllegalArgumentException If the specified dimension is less than 2.
	*/
	public ShapeMultiPath(int dimension) {
		super(dimension);

		if (dimension < 2)
			throw new IllegalArgumentException("dimension >= 2 required");
	}

	/**
	The basis vectors specify which index corresponds to the x-axis and which index
	corresponds to the y-axis.  The value of the x-axis is at index location 0 and the
	value of the y-axis is at index location 1.

	@throws IllegalArgumentException If the axis values are less than 0 or greater than or
	equal to the dimension.

	@see #getBasisVectors()
	*/
	public void setBasisVectors(int[] b) {
		int b0 = b[0];
		int b1 = b[1];

		int dimension = getDimension();

		if (b0 < 0 || b1 < 0 || b0 >= dimension || b1 >= dimension)
			throw new IllegalArgumentException("basis vectors must be >= 0 and < dimension");

		ai0 = b0;
		ai1 = b1;
	}

	/**
	Returns a new integer array with the basis vectors.  The default basis vectors are {0, 1}.

	@see #setBasisVectors(int[])
	*/
	public int[] getBasisVectors() {
		return new int[] { ai0, ai1 };
	}

	/**
	Returns the minimum distance^2 from the specified point to the line segments of this multi-path.
	*/
	public double getDistSq(double x, double y) {
		int n = getNumPoints();

		if (n == 0)
			return Double.MAX_VALUE;

		double[] p = get(0);

		double x2 = p[ai0];
		double y2 = p[ai1];
		double dist = Double.MAX_VALUE;

		for (int i = 1; i < n; i++) {
			p = get(i);
			double x1 = p[ai0];
			double y1 = p[ai1];

			if (getType(i) == MultiPath.LINE_TO) {
				double d = Geom.ptSegDistSq(x1, y1, x2, y2, x, y, null);
				if (d < dist)
					dist = d;
			}

			x2 = x1;
			y2 = y1;			
		}

		return dist;
	}


	//------------------------------------------------------------------------------------------
	// methods for Shape interface:


	/**
	Returns the value of the winding rule. The default value is PathIterator.WIND_EVEN_ODD.

	@see #setWindingRule(int)
	*/
	public int getWindingRule() {
		return windingRule;
	}

	/**
	Sets the winding rule.  The winding rule can either by PathIterator.WIND_EVEN_ODD or
	PathIterator.WIND_NON_ZERO, otherwise an IllegalArgumentException is thrown.
	*/
	public void setWindingRule(int rule) {
		if (rule != PathIterator.WIND_EVEN_ODD && rule != PathIterator.WIND_NON_ZERO)
			throw new IllegalArgumentException("winding rule must be WIND_EVEN_ODD or WIND_NON_ZERO");

		windingRule = rule;
	}

	/**
	Returns a new PathIterator object.
	*/
	public PathIterator getPathIterator(AffineTransform at) {
		return new ShapeMultiPathIterator(this, at);
	}

	/**
	Returns a new PathIterator object.  The flatness parameter is ignored since a multi-path, by
	definition, is already flat.
	*/
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new ShapeMultiPathIterator(this, at);
	}

	//---------------------------------------------------------------

	/**
	See the getBounds2D() method.

	@see #getBounds2D()
	*/
	public Rectangle getBounds() {
		Rectangle2D r = getBounds2D();
		if (r == null) return null;
		return r.getBounds();
	}

	/**
	Computes the bounding box of the points.  When computing the bounding box, a point is considered if it is
	of type LINE_TO or it is of type MOVE_TO and the next point is of type LINE_TO.  A value of null is
	returned if there is not enough data to define a bounding box.
	*/
	public Rectangle2D getBounds2D() {
		int n = getNumPoints();

		double x1 = Double.MAX_VALUE;
		double y1 = Double.MAX_VALUE;
		double x2 = -Double.MAX_VALUE;
		double y2 = -Double.MAX_VALUE;

		boolean defined = false;

		for (int i = 0; i < n; i++) {
			double[] p = get(i);

			boolean b = false;

			if (getType(i) == MultiPath.MOVE_TO) {
				if (i < n - 1 && getType(i+1) == MultiPath.LINE_TO)
					b = true;
			}
			else {
				b = true;
			}

			if (b) {
				defined = true;
				if (p[ai0] < x1) x1 = p[ai0];
				if (p[ai1] < y1) y1 = p[ai1];
				if (p[ai0] > x2) x2 = p[ai0];
				if (p[ai1] > y2) y2 = p[ai1];
			}
		}

		if (!defined)
			return null;

		return new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
	}


	//---------------------------------------------------------------

	/**
	Returns true if the point is contained inside the shape. Otherwise false is returned.
	*/
	public boolean contains(double x, double y) {
		int cross = Curve.crossingsForPath(getPathIterator(null), x, y);

		if (windingRule == PathIterator.WIND_NON_ZERO)
			return cross != 0;

		return (cross & 1) != 0;
	}

	/**
	See the contains(x, y) method.

	@see #contains(double,double)
	*/
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	/**
	Returns true only if the shape contains all points of the rectangle. First, if any of
	the four corners is not contained in the shape then false is returned.  Now we know
	that all four corners are inside the shape.  Next, we check to see if any line segment
	of this shape intersects any of the 4 line segments formed by the rectangle.  If there
	is an intersection, then false is returned.  Otherwise true is returned.
	*/
	public boolean contains(double x1, double y1, double w, double h) {
		double x2 = x1 + w;
		double y2 = y1 + h;

		if (!contains(x1, y1)) return false;
		if (!contains(x1, y2)) return false;
		if (!contains(x2, y1)) return false;
		if (!contains(x2, y2)) return false;

		int n = getNumPoints();

		if (n == 0) return false;

		double[] p = get(0);

		double xb = p[ai0];
		double yb = p[ai1];

		for (int i = 1; i < n; i++) {
			p = get(i);
			double xa = p[ai0];
			double ya = p[ai1];

			if (getType(i) == MultiPath.LINE_TO) {
				if (Geom.getSegSegIntersection(xa, ya, xb, yb, x1, y1, x2, y1, null) == Geom.INTERSECT)
					return false;
				if (Geom.getSegSegIntersection(xa, ya, xb, yb, x1, y1, x1, y2, null) == Geom.INTERSECT)
					return false;
				if (Geom.getSegSegIntersection(xa, ya, xb, yb, x1, y2, x2, y2, null) == Geom.INTERSECT)
					return false;
				if (Geom.getSegSegIntersection(xa, ya, xb, yb, x2, y1, x2, y2, null) == Geom.INTERSECT)
					return false;
			}

			xb = xa;
			yb = ya;
		}

		return true;
	}

	/**
	See the contains(x, y, w, h) method.

	@see #contains(double,double,double,double)
	*/
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	//---------------------------------------------------------------

	/**
	This method returns true if any line segment in this multi-path intersects any of the
	4 line segments formed by the rectangle or any corner of the rectangle is inside the
	shape or any point of the shape is inside the rectangle.  Otherwise false is returned.
	*/
	public boolean intersects(double x1, double y1, double w, double h) {
		double x2 = x1 + w;
		double y2 = y1 + h;

		if (contains(x1, y1)) return true;
		if (contains(x1, y2)) return true;
		if (contains(x2, y1)) return true;
		if (contains(x2, y2)) return true;

		int n = getNumPoints();

		if (n == 0) return false;

		double[] p = get(0);

		double xb = p[ai0];
		double yb = p[ai1];

		for (int i = 1; i < n; i++) {
			p = get(i);
			double xa = p[ai0];
			double ya = p[ai1];

			if (getType(i) == MultiPath.LINE_TO) {
				if (Geom.getSegSegIntersection(xa, ya, xb, yb, x1, y1, x2, y1, null) == Geom.INTERSECT)
					return true;
				if (Geom.getSegSegIntersection(xa, ya, xb, yb, x1, y1, x1, y2, null) == Geom.INTERSECT)
					return true;
				if (Geom.getSegSegIntersection(xa, ya, xb, yb, x1, y2, x2, y2, null) == Geom.INTERSECT)
					return true;
				if (Geom.getSegSegIntersection(xa, ya, xb, yb, x2, y1, x2, y2, null) == Geom.INTERSECT)
					return true;

				if (xa >= x1 && ya >= y1 && xa <= x2 && ya <= y2) return true;
				if (xb >= x1 && yb >= y1 && xb <= x2 && yb <= y2) return true;
			}

			xb = xa;
			yb = ya;
		}

		return false;
	}

	/**
	See the intersects(x, y, w, h) method.

	@see #intersects(double,double,double,double)
	*/
	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
}