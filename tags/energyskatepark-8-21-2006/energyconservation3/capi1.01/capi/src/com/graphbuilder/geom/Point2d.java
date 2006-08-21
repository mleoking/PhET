package com.graphbuilder.geom;

/**
The Point2d interface represents a two-dimensional point using double precision.
This interface is useful when writing general geometry algorithms.
*/
public interface Point2d {

	/**
	Sets the location of the point.
	*/
	public void setLocation(double x, double y);

	/**
	Returns the x-coordinate.
	*/
	public double getX();

	/**
	Returns the y-coordinate.
	*/
	public double getY();

}