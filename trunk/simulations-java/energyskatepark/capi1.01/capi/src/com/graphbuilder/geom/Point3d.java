package com.graphbuilder.geom;

/**
The Point3d interface represents a three-dimensional point using double precision.
This interface is useful when writing general geometry algorithms.
*/
public interface Point3d extends Point2d {

	/**
	Sets the location of the point.
	*/
	public void setLocation(double x, double y, double z);

	/**
	Returns the z-coordinate.
	*/
	public double getZ();
}