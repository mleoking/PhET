/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.util;

import java.awt.geom.Point2D;


/**
 * Vector2D is a mutable vector.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Vector2D {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _magnitude; // magnitude
    private double _angle; // angle, in radians
    private double _x; // magnitude of the X component
    private double _y; // magnitude of the Y component

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a vector with zero magnitude and zero angle.
     */
    public Vector2D() {
        this( 0, 0 );
    }

    /**
     * Creates a vector with the specified X and Y components.
     * 
     * @param x the magnitude of the X component
     * @param y the magnitude of the Y component
     */    
    public Vector2D( double x, double y ) {
        setXY( x, y );
    }
    
    /**
     * Creates a vector that is a copy of a specified vector.
     * 
     * @param v the vector to copy
     */
    public Vector2D( Vector2D v ) {
        this( v.getX(), v.getY() );
    }

    /**
     * Copies a vector.
     * 
     * @param v the vector to copy
     */
    public void copy( Vector2D v ) {
        _x = v.getX();
        _y = v.getY();
        _magnitude = v.getMagnitude();
        _angle = v.getAngle();
    }

    //----------------------------------------------------------------------------
    // Accessors for magnitude and angle
    //----------------------------------------------------------------------------
    
    /**
     * Sets the magnitude and angle.
     * 
     * @param magnitude the magnitude
     * @param angle the angle, in radians
     */
    public void setMagnitudeAngle( double magnitude, double angle ) {
        _magnitude = magnitude;
        _angle = angle;
        _x = Math.cos( angle ) * magnitude;
        _y = Math.sin( angle ) * magnitude;
    }
    
    /**
     * Sets the magnitude.
     * 
     * @param magnitude
     */
    public void setMagnitude( double magnitude ) {
        setMagnitudeAngle( magnitude, _angle );
    }
    
    /**
     * Gets the magnitude.
     * 
     * @return the magnitude
     */
    public double getMagnitude() {
        return _magnitude;
    }
    
    /**
     * Sets the angle. 
     * Zero degrees points down the X axis.
     * Positive angles are clockwise
     * 
     * @param angle the angle, in radians
     */
    public void setAngle( double angle ) {
        setMagnitudeAngle( _magnitude, angle );
    }
    
    /**
     * Gets the angle.
     * 
     * @return the angle, in radians
     */
    public double getAngle() {
        return _angle;
    }
    
    //----------------------------------------------------------------------------
    // Accessors for X and Y components
    //----------------------------------------------------------------------------
    
    /**
     * Sets the X and Y components of the vector.
     * 
     * @param x the magnitude of the X component
     * @param y the magnitude of the Y component
     */
    public void setXY( double x, double y ) {
        _x = x;
        _y = y;
        _magnitude = Math.sqrt( ( _x * _x ) + ( _y * _y ) );
        _angle = Math.atan2( _y, _x );
    }
    
    /**
     * Sets the X component of the vector.
     * 
     * @param x the magnitude of the X component
     */
    public void setX( double x ) {
        setXY( x, _y );
    }
  
    /**
     * Gets the X component of the vector.
     * 
     * @return the magnitude of the X component
     */
    public double getX() {
        return _x;
    }
    
    /**
     * Sets the Y component of the vector.
     * 
     * @param y the magnitude of the Y component
     */
    public void setY( double y ) {
        setXY( _x, y );
    }

    /**
     * Gets the Y component of the vector.
     * 
     * @return the magnitude of the Y component
     */
    public double getY() {
        return _y;
    }
    
    //----------------------------------------------------------------------------
    // Operators
    //----------------------------------------------------------------------------
    
    /**
     * Clears the vector, by setting its magnitude and angle to zero.
     */
    public void clear() {
        setMagnitudeAngle( 0, 0 );
    }
    
    /**
     * Vector addition.
     * 
     * @param v the vector to add
     */
    public void add( Vector2D v ) {
        setXY( _x + v.getX(), _y + v.getY() );
    }
    
    /**
     * Vector subtraction.
     * 
     * @param v the vector to subtract
     */
    public void subtract( Vector2D v ) {
        setXY( _x - v.getX(), _y - v.getY() );
    }
    
    /**
     * Scales the vector.
     * The X and Y components are scaled uniformly.
     * Scaling is cumulative.
     * 
     * @param scale the scale
     */
    public void scale( double scale ) {
        setXY( _x * scale, _y * scale );
    }
    
    /**
     * Rotates the vector.
     * Positive rotation is clockwise.
     * Rotation is cumulative.
     * 
     * @param angle the angle, in radians
     */
    public void rotate( double angle ) {
        setAngle( _angle + angle );
    }
    
    //----------------------------------------------------------------------------
    // Object overrides
    //----------------------------------------------------------------------------

    /**
     * Tests whether some object equals this vector.
     * The object is equal if it is a Vector2D and it has
     * the same magnitude for its X and Y components.
     * 
     * @param object the object to test
     * @param return 
     */
    public boolean equals( Object object ) {
        boolean result = false;
        if ( getClass() == object.getClass() ) {
            Vector2D v = (Vector2D) object;
            result = ( _x == v.getX() && _y == v.getY() );
        }
        return result;
    }

    /**
     * Provides a string representation.
     * 
     * @return String
     */
    public String toString() {
        return getClass().getName() + 
            "=[x=" + _x + ", y=" + _y + ", magnitude=" + _magnitude + ", angle=" + Math.toDegrees( _angle ) + "]";
    }
}
