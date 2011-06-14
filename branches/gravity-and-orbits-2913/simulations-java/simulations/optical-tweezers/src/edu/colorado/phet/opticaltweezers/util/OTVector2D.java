// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.util;



/**
 * OTVector2D is a mutable 2D vector, with Cartesian and Polar subclasses.
 * <p>
 * This implementation stores vector components in both Carteisan and Polar coordinates
 * to improve the speed of "get" calls.  The expense of converting between coordinate
 * systems is incurred on deman.  When components are set in one of the 
 * coordinate systems, the components for the other coordinate system are marked
 * as dirty, and are not recomputed until a relevant getter is called.
 * For example, calling setXY would mark the Polar coordinates as dirty,
 * and the Polar coordinates would be recomputed when getMagnitude or getAngle is called.
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
    
    // mark Cartesian or Polar coordinates as dirty for faster "set" performance
    private boolean _cartesianDirty;
    private boolean _polarDirty;

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
        
        _cartesianDirty = false;
        _polarDirty = false;
    }
    
    //----------------------------------------------------------------------------
    // Component updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the stored Cartesian coordinates.
     */
    private void updateCartesian() {
        assert( _cartesianDirty && !_polarDirty );
        _x = _magnitude * Math.cos( _angle );
        _y = _magnitude * Math.sin( _angle );
        _cartesianDirty = false;
    }
    
    /*
     * Updates the stored Polar coordinates.
     */
    private void updatePolar() {
        assert( _polarDirty && !_cartesianDirty );
        _magnitude = Math.sqrt( ( _x * _x ) + ( _y * _y ) );
        _angle = Math.atan2( _y, _x );
        _polarDirty = false;
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
        _cartesianDirty = false;
        _polarDirty = true;
    }
    
    /**
     * Sets the X component of the vector.
     * 
     * @param x the magnitude of the X component
     */
    public void setX( double x ) {
        setXY( x, getY() );
    }
  
    /**
     * Gets the X component of the vector.
     * 
     * @return the magnitude of the X component
     */
    public double getX() {
        if ( _cartesianDirty ) {
            updateCartesian();
        }
        return _x;
    }
    
    /**
     * Sets the Y component of the vector.
     * 
     * @param y the magnitude of the Y component
     */
    public void setY( double y ) {
        setXY( getX(), y );
    }

    /**
     * Gets the Y component of the vector.
     * 
     * @return the magnitude of the Y component
     */
    public double getY() {
        if ( _cartesianDirty ) {
            updateCartesian();
        }
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
        _magnitude = magnitude;
        _angle = angle;
        _polarDirty = false;
        _cartesianDirty = true;
    }
    
    /**
     * Sets the magnitude.
     * 
     * @param magnitude
     */
    public void setMagnitude( double magnitude ) {
        setMagnitudeAngle( magnitude, getAngle() );
    }
    
    /**
     * Gets the magnitude.
     * 
     * @return the magnitude
     */
    public double getMagnitude() {
        if ( _polarDirty ) {
            updatePolar();
        }
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
        setMagnitudeAngle( getMagnitude(), angle );
    }
    
    /**
     * Gets the angle.
     * 
     * @return the angle, in radians
     */
    public double getAngle() {
        if ( _polarDirty ) {
            updatePolar();
        }
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
        setXY( getX() + v.getX(), getY() + v.getY() );
    }
    
    /**
     * Vector subtraction.
     * 
     * @param v the vector to subtract
     */
    public void subtract( OTVector2D v ) {
        setXY( getX() - v.getX(), getY() - v.getY() );
    }
    
    /**
     * Scales the vector.
     * The X and Y components are scaled uniformly.
     * Scaling is cumulative.
     * 
     * @param scale the scale
     */
    public void scale( double scale ) {
        setXY( getX() * scale, getY() * scale );
    }
    
    /**
     * Rotates the vector.
     * Positive rotation is clockwise.
     * Rotation is cumulative.
     * 
     * @param angle the angle, in radians
     */
    public void rotate( double angle ) {
        setAngle( getAngle() + angle );
    }
}
