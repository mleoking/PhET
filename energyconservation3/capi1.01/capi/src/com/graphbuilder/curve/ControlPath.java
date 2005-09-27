package com.graphbuilder.curve;

import com.graphbuilder.struc.Bag;

/**
<p>A ControlPath is a container of Point objects and Curve objects.  The control-path
uses arrays to store the points and curves.  Methods that accept objects will throw an
IllegalArgumentException if those objects are null.  Methods that accept index values
will throw an IllegalArgumentException if those index values are out of range.
*/
public class ControlPath {

	private Bag curveBag = new Bag();
	private Bag pointBag = new Bag();

	/**
	Creates a control-path.
	*/
	public ControlPath() {}

	/**
	Adds a curve to the curve array at index location numCurves.

	@throws IllegalArgumentException If the specified curve is null.
	*/
	public void addCurve(Curve c) {
		if (c == null)
			throw new IllegalArgumentException("Curve cannot be null.");

		curveBag.add(c);
	}

	/**
	Adds a point to the point array at index location numPoints.

	@throws IllegalArgumentException If the specified point is null.
	*/
	public void addPoint(Point p) {
		if (p == null)
			throw new IllegalArgumentException("Point cannot be null.");

		pointBag.add(p);
	}

	/**
	Inserts a curve at the specified index in the curve array.

	@throws IllegalArgumentException If the specified curve is null.
	*/
	public void insertCurve(Curve c, int index) {
		if (c == null)
			throw new IllegalArgumentException("Curve cannot be null.");

		curveBag.insert(c, index);
	}

	/**
	Inserts a point at the specified index in the point array.

	@throws IllegalArgumentException If the specified point is null.
	*/
	public void insertPoint(Point p, int index) {
		if (p == null)
			throw new IllegalArgumentException("Point cannot be null.");

		pointBag.insert(p, index);
	}

	/**
	Sets a curve at the specified index in the curve array, returning the curve
	that was at that index.

	@throws IllegalArgumentException If the specified curve is null.
	*/
	public Curve setCurve(Curve c, int index) {
		if (c == null)
			throw new IllegalArgumentException("Curve cannot be null.");

		return (Curve) curveBag.set(c, index);
	}

	/**
	Sets a point at the specified index in the point array, returning the point
	that was at that index.

	@throws IllegalArgumentException If the specified point is null.
	*/
	public Point setPoint(Point p, int index) {
		if (p == null)
			throw new IllegalArgumentException("Point cannot be null.");

		return (Point) pointBag.set(p, index);
	}

	/**
	Returns the curve at the specified index.
	*/
	public Curve getCurve(int index) {
		return (Curve) curveBag.get(index);
	}

	/**
	Returns the point at the specified index.
	*/
	public Point getPoint(int index) {
		return (Point) pointBag.get(index);
	}

	/**
	Returns the number of curves in the control-path.
	*/
	public int numCurves() {
		return curveBag.size();
	}

	/**
	Returns the number of points in the control-path.
	*/
	public int numPoints() {
		return pointBag.size();
	}

	/**
	Removes the first occurrence of the specified curve from the curve array.
	*/
	public void removeCurve(Curve c) {
		curveBag.remove(c);
	}

	/**
	Removes the first occurrence of the specified point from the point array.
	*/
	public void removePoint(Point p) {
		pointBag.remove(p);
	}

	/**
	Removes the curve at the specified index location.
	*/
	public void removeCurve(int index) {
		curveBag.remove(index);
	}

	/**
	Removes the point at the specified index location.
	*/
	public void removePoint(int index) {
		pointBag.remove(index);
	}

	/**
	Checks that the curve array has the specified capacity, otherwise the capacity of the
	curve array is increased to be the maximum between twice the current capacity or
	the specified capacity.
	*/
	public void ensureCurveCapacity(int capacity) {
		curveBag.ensureCapacity(capacity);
	}

	/**
	Checks that the point array has the specified capacity, otherwise the capacity of the
	point array is increased to be the maximum between twice the current capacity or
	the specified capacity.
	*/
	public void ensurePointCapacity(int capacity) {
		pointBag.ensureCapacity(capacity);
	}

	/**
	Creates a new curve array of exact size, copying the curves from the old array into the
	new one.
	*/
	public void trimCurveArray() {
		curveBag.trimArray();
	}

	/**
	Creates a new point array of exact size, copying the points from the old array into the
	new one.
	*/
	public void trimPointArray() {
		pointBag.trimArray();
	}
}