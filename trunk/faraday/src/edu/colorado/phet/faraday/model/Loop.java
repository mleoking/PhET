/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * Loop is the model of a loop of wire.
 * This class is package private.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
class Loop extends SimpleObservable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Radius of the loop.
    private double _radius;
    // Location
    private Point2D _location;
    // Direction, in degrees
    private double _direction;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor.
     * Creates a loop with radius=10, location=(0,0), direction=0.
     */
    public Loop() {
        // Initialize member data.
        _radius = 10.0;
        _location = new Point2D.Double( 0, 0 );
        _direction = 0.0;   
    }
    
    /**
     * Fully-specified constructor.
     * 
     * @param radius the radius of the loop
     * @param location the location of the loop's center
     * @param direction the direction in degrees (see setDirection)
     */
    public Loop( double radius, Point2D location, double direction ) {
        this();
        setRadius( radius );
        setLocation( location );
        setDirection( direction );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the radius.
     * 
     * @param radius the radius
     * @throws IllegalArgumentException if radius is not > 0
     */
    public void setRadius( double radius ) {
        if ( radius <= 0 ) {
            throw new IllegalArgumentException( "radius must be > 0" );
        }
        _radius = radius;
        notifyObservers();
    }
    
    /**
     * Gets the radius.
     * 
     * @return the radius
     */
    public double getRadius() {
        return _radius;
    }
    
    /**
     * Gets the surface area of the loop.
     * 
     * @return the area of the loop
     */
    public double getArea() {
        return Math.PI * Math.pow( _radius, 2.0 );
    }
    
    /**
     * Sets the location.
     * This determines the location of the left edge of the loop,
     * prior to applying any direction rotation.
     * 
     * @param location the location
     */
    public void setLocation( Point2D location ) {
        _location.setLocation( location.getX(), location.getY() );
        notifyObservers();
    }
    
    /**
     * Convenience method for setting the location.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     */
    public void setLocation( double x, double y ) {
        _location.setLocation( x, y );
        notifyObservers();
    }
    
    /**
     * Gets the location.
     * 
     * @return the location
     */
    public Point2D getLocation() {
        return new Point2D.Double( _location.getX(), _location.getY() );
    }
    
    /**
     * Gets the X coordinate of the location.
     * 
     * @return the X coordinate
     */
    public double getX() {
        return _location.getX();
    }
    
    /**
     * Gets th Y coordinate of the location.
     * 
     * @return the Y coordinate
     */
    public double getY() {
        return _location.getY();
    }
    
    /**
     * Sets the direction. 
     * A direction of zero makes the loop's surface area vector parallel to the X axis.
     * 
     * @param direction the direction, in degrees
     */
    public void setDirection( double direction ) {
        _direction = direction;
        notifyObservers();
    }
    
    /**
     * Gets the directions.
     * 
     * @return the direction, in degrees
     */
    public double getDirection() {
        return _direction;
    }
    
}
