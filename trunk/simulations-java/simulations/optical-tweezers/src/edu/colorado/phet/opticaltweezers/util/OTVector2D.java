/* Copyright 2005-2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.util;

import java.awt.geom.Point2D;


/**
 * OTVector2D is a mutable 2D vector, with Cartesian and Polar subclasses.
 * <p>
 * This implementation stores vector components in both Carteisan and Polar coordinates
 * to improve the speed of "get" calls at the expense of "set" calls.
 * <p>
 * Once way to improve the performance of this implementation would be 
 * to mark Cartesian or Polar data as dirty when changed, and defer 
 * recomputing until a relevant getter is called.  For example, calling
 * setXY would mark the Polar coordinates as dirty, and the Polar coordinates
 * would be recomputed when getMagnitude or getAngle is called.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class OTVector2D {

    //----------------------------------------------------------------------------
    // Concrete classes
    //----------------------------------------------------------------------------
    
    /**
     * Cartesian describes a 2D vector in Cartesian coordinates.
     */
    public static class Cartesian extends OTVector2D {
        
        public Cartesian() {
            this( 0, 0 );
        }
        
        public Cartesian( OTVector2D v ) {
            this( v.getX(), v.getY() );
        }
        
        public Cartesian( double x, double y ) {
            super();
            setXY( x, y );
        }
    }
    
    /**
     * Polar describes a 2D vector in Polar coordinates.
     */
    public static class Polar extends OTVector2D {
        
        public Polar() {
            this( 0, 0 );
        }
        
        public Polar( OTVector2D v ) {
            this( v.getMagnitude(), v.getAngle() );
        }
        
        public Polar( double magnitude, double angle ) {
            super();
            setMagnitudeAngle( magnitude, angle );
        }
    }
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // store both Cartesian and Polar coordinates for faster "get" performance
    private double _x; // magnitude of the X component
    private double _y; // magnitude of the Y component
    private double _magnitude; // magnitude
    private double _angle; // angle, in radians

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /*
     * Creates a zero vector.
     */
    protected OTVector2D() {
        _x = 0;
        _y = 0;
        _magnitude = 0;
        _angle = 0;
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
    // Accessors for magnitude and angle
    //----------------------------------------------------------------------------
    
    /**
     * Sets the magnitude and angle.
     * 
     * @param magnitude the magnitude
     * @param angle the angle, in radians
     */
    public void setMagnitudeAngle( double magnitude, double angle ) {
        double x = magnitude * Math.cos( angle );
        double y = magnitude * Math.sin( angle );
        setXY( x, y );
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
    // Operators
    //----------------------------------------------------------------------------
    
    /**
     * Zeros out the vector, so that its magnitude and angle are zero.
     */
    public void zero() {
        setMagnitudeAngle( 0, 0 );
    }
    
    /**
     * Vector addition.
     * 
     * @param v the vector to add
     */
    public void add( OTVector2D v ) {
        setXY( _x + v.getX(), _y + v.getY() );
    }
    
    /**
     * Vector subtraction.
     * 
     * @param v the vector to subtract
     */
    public void subtract( OTVector2D v ) {
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
}
