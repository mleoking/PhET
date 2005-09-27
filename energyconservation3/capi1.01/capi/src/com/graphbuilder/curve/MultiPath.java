package com.graphbuilder.curve;

import com.graphbuilder.geom.Geom;

/**
A multi-path is a series of paths (sequence of connected points) in n-dimensions.  The points and
move types (MOVE_TO or LINE_TO) are stored using arrays.

@see com.graphbuilder.curve.Curve
@see com.graphbuilder.curve.ShapeMultiPath
*/
public class MultiPath {

	public static final Object MOVE_TO = new Object();
	public static final Object LINE_TO = new Object();

	private double[][] point = new double[2][0];
	private Object[] type = new Object[point.length];
	private int size = 0;

	private double flatness = 1.0;
	private final int dimension;

	/**
	Constructs a multi-path specifying the minimum required dimension of each point appended
	to this multi-path.

	@throws IllegalArgumentException If dimension <= 0.
	*/
	public MultiPath(int dimension) {
		if (dimension <= 0)
			throw new IllegalArgumentException("dimension > 0 required");

		this.dimension = dimension;
	}

	/**
	Returns the dimension. The dimension is used by the BinaryCurveApproximationAlgorithm
	to know what dimension of points to create.  If the dimension of the multi-path is greater
	than the dimension of the control points for a curve, then an ArrayIndexOutOfBoundsException
	will occur when the curve is appended to the MultiPath.
	*/
	public int getDimension() {
		return dimension;
	}

	/**
	Returns the flatness.  The flatness is used by the BinaryCurveApproximationAlgorithm to
	determine how closely the line segements formed by the points of this multi-path should
	approximate a given curve.  The default flatness value is 1.0.  When using curves in a
	graphics environment, the flatness usually inversely proportional to the scale.

	@see #setFlatness(double)
	*/
	public double getFlatness() {
		return flatness;
	}

	/**
	Sets the flatness.  As the flatness value gets closer to zero, the BinaryCurveApproximationAlgorithm
	generates more points.

	@throws IllegalArgumentException If the flatness is <= 0.
	@see #getFlatness()
	*/
	public void setFlatness(double f) {
		if (f <= 0)
			throw new IllegalArgumentException("flatness > 0 required");

		flatness = f;
	}

	/**
	Returns a reference to the point at the specified index.

	@see #set(int, double[])
	*/
	public double[] get(int index) {
		return point[index];
	}

	/**
	Sets the point at the specified index.

	@throws IllegalArgumentException If the point is null or the dimension of the point does not
	meet the dimension requirement specified in the constructor.
	@see #get(int)
	*/
	public void set(int index, double[] p) {
		if (p == null)
			throw new IllegalArgumentException("Point cannot be null.");

		if (p.length < dimension)
			throw new IllegalArgumentException("p.length >= dimension required");

		if (point[index] == null)
			throw new ArrayIndexOutOfBoundsException(index);

		point[index] = p;
	}

	/**
	Returns the type of the point at the specified index.  The type can other be MultiPath.MOVE_TO
	or MultiPath.LINE_TO.

	@see #setType(int, Object)
	*/
	public Object getType(int index) {
		if (type[index] == null)
			throw new ArrayIndexOutOfBoundsException(index);

		return type[index];
	}

	/**
	Sets the type of the point at the specified index.  The first point must always be MOVE_TO.

	@throws IllegalArgumentException If the specified type unknown or the specified index is 0 and type
	is not MOVE_TO.
	@see #getType(int)
	*/
	public void setType(int index, Object type) {
		if (type != MOVE_TO && type != LINE_TO)
			throw new IllegalArgumentException("unknown type");

		if (this.type[index] == null)
			throw new ArrayIndexOutOfBoundsException(index);

		if (index == 0 && type != MOVE_TO)
			throw new IllegalArgumentException("type[0] must always be MOVE_TO");

		this.type[index] = type;
	}

	/**
	Returns the size counter.

	@see #setNumPoints(int)
	*/
	public int getNumPoints() {
		return size;
	}

	/**
	Sets the size counter.  The size counter can be changed to any value as long as all points from index location
	0 (inclusive) to n (exclusive) are non-null.  Thus, the maximum value the size counter can be is the first
	null index location.  Changing the size counter does not remove any of the points.

	@see #getNumPoints()
	*/
	public void setNumPoints(int n) {
		if (n != 0 && point[n-1] == null)
			throw new ArrayIndexOutOfBoundsException(n);
		size = n;
	}

	/**
	Returns the capacity of the internal point array.
	*/
	public int getCapacity() {
		return point.length;
	}

	/**
	Checks that the point array has the specified capacity, otherwise the capacity of the point
	array is increased to be the maximum between twice the current capacity and the specified capacity.
	*/
	public void ensureCapacity(int capacity) {
		if (point.length < capacity) {
			int x = 2 * point.length;

			if (x < capacity)
				x = capacity;

			double[][] p2 = new double[x][];
			for (int i = 0; i < size; i++)
				p2[i] = point[i];

			Object[] t2 = new Object[x];
			for (int i = 0; i < size; i++)
				t2[i] = type[i];

			point = p2;
			type = t2;
		}
	}

	/**
	Creates a new point array of exact size, copying the points from the old array into the
	new one.
	*/
	public void trimArray() {
		if (size < point.length) {
			double[][] p2 = new double[size][];
			for (int i = 0; i < size; i++)
				p2[i] = point[i];

			Object[] t2 = new Object[size];
			for (int i = 0; i < size; i++)
				t2[i] = type[i];

			point = p2;
			type = t2;
		}
	}

	/**
	Appends a point of type LINE_TO.  If the size counter is 0 then the request is interpretted as a
	MOVE_TO request.

	@throws IllegalArgumentException If the point is null or the dimension of the point does not meet
	the dimension requirement specified in the constructor.
	@see #moveTo(double[])
	*/
	public void lineTo(double[] p) {
		append(p, LINE_TO);
	}

	/**
	Appends a point of type MOVE_TO.

	@throws IllegalArgumentException If the point is null or the dimension of the point does not meet the
	dimension requirement specified in the constructor.
	@see #lineTo(double[])
	*/
	public void moveTo(double[] p) {
		append(p, MOVE_TO);
	}

	private void append(double[] p, Object t) {
		if (p == null)
			throw new IllegalArgumentException("Point cannot be null.");

		if (p.length < dimension)
			throw new IllegalArgumentException("p.length >= dimension required");

		if (size == 0) t = MOVE_TO;

		ensureCapacity(size + 1);
		point[size] = p;
		type[size] = t;
		size++;
	}

	/**
	Computes the minimum distance^2 from the specified point to the line segments formed by the
	points of this multi-path.  If the size counter is 0 then the value returned is Double.MAX_VALUE.

	@throws IllegalArgumentException If the point is null or the length of the point is less than the
	dimension specified in the constructor.
	*/
	public double getDistSq(double[] p) {
		if (p == null)
			throw new IllegalArgumentException("Point cannot be null.");

		if (p.length < dimension)
			throw new IllegalArgumentException("p.length >= dimension required");

		int n = getNumPoints();

		if (n == 0)
			return Double.MAX_VALUE;

		double dist = Double.MAX_VALUE;

		double[] b = get(0);

		double[] c = new double[dimension + 1];

		for (int i = 1; i < n; i++) {
			double[] a = get(i);

			if (getType(i) == MultiPath.LINE_TO) {
				double d = Geom.ptSegDistSq(a, b, p, c, dimension);

				if (d < dist)
					dist = d;
			}
		}

		return dist;
	}
}