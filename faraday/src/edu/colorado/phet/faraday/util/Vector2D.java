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
    
    private double _x;
    private double _y;
    private double _magnitude;
    private double _angle;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Vector2D() {
        this( 0, 0 );
    }

    public Vector2D( double x, double y ) {
        setXY( x, y );
    }

    public Vector2D( Point2D p ) {
        this( p.getX(), p.getY() );
    }
    
    public Vector2D( Vector2D v ) {
        this( v.getX(), v.getY() );
    }

    public void copy( Vector2D v ) {
        _x = v.getX();
        _y = v.getY();
        _magnitude = v.getMagnitude();
        _angle = v.getAngle();
    }
    
    //----------------------------------------------------------------------------
    // XY accessors
    //----------------------------------------------------------------------------
    
    public void setXY( double x, double y ) {
        _x = x;
        _y = y;
        _magnitude = Math.sqrt( ( _x * _x ) + ( _y * _y ) );
        _angle = Math.atan2( _y, _x );
    }
    
    public void setX( double x ) {
        setXY( x, _y );
    }
    
    public void setY( double y ) {
        setXY( _x, y );
    }
    
    public double getX() {
        return _x;
    }
    
    public double getY() {
        return _y;
    }

    //----------------------------------------------------------------------------
    // Magnitude and Angle accessors
    //----------------------------------------------------------------------------
    
    public void setMagnitudeAngle( double magnitude, double angle ) {
        _magnitude = magnitude;
        _angle = angle;
        _x = Math.cos( angle ) * magnitude;
        _y = Math.sin( angle ) * magnitude;
    }
    
    public void setMagnitude( double magnitude ) {
        setMagnitudeAngle( magnitude, _angle );
    }
    
    public double getMagnitude() {
        return _magnitude;
    }
    
    public void setAngle( double angle ) {
        setMagnitudeAngle( _magnitude, angle );
    }
    
    public double getAngle() {
        return _angle;
    }
    
    //----------------------------------------------------------------------------
    // Operators
    //----------------------------------------------------------------------------
    
    public void add( Vector2D v ) {
        setXY( _x + v.getX(), _y + v.getY() );
    }
    
    public void subtract( Vector2D v ) {
        setXY( _x - v.getX(), _y - v.getY() );
    }
    
    public void scale( double scale ) {
        setXY( _x * scale, _y * scale );
    }
    
    public void rotate( double angle ) {
        setAngle( _angle + angle );
    }
    
    //----------------------------------------------------------------------------
    // Object overrides
    //----------------------------------------------------------------------------

    public boolean equals( Object object ) {
        boolean result = false;
        if ( getClass() == object.getClass() ) {
            Vector2D v = (Vector2D) object;
            result = ( _x == v.getX() && _y == v.getY() );
        }
        return result;
    }

    public String toString() {
        return getClass().getName() + "[" + _x + ", " + _y + "]";
    }
}
