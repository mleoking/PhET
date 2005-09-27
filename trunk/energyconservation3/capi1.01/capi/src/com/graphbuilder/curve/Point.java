package com.graphbuilder.curve;

/**
A representation of an n-dimensional point.  The dimension of the point is the length of the
array returned by the getLocation() method.

@see com.graphbuilder.curve.ControlPath
@see com.graphbuilder.curve.Curve
*/
public interface Point {

	/**
	Sets the location of the point.  Some implementations may copy the values of the
	array or may set the internal reference to the array.  Some implementations may check
	the length of the array or the values.  If the values need to be restricted, then the
	getLocation() method should return a copy of the data.
	*/
	public void setLocation(double[] p);

	/**
	Returns either a new array or internal temporary array with a copy of the data or a
	direct reference to the array.	In the case where a copy is returned and later modified,
	the setLocation must be called to apply the data.  The values in the returned array
	must represent the absolute location of the point.
	*/
	public double[] getLocation();
}