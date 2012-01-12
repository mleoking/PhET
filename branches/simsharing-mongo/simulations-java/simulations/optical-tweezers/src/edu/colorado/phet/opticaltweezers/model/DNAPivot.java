// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;

/**
 * A DNA strand is modeled as a set of connected line segements.
 * Each segement is a simple spring, and the springs are joined 
 * at pivot points. DNAPivot is the data structure that describes
 * these pivot points.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAPivot {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _x, _y;  // position components
    private double _xVelocity, _yVelocity; // velocity components
    private double _xAcceleration, _yAcceleration; // acceleration components

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DNAPivot( double x, double y ) {
        _x = x;
        _y = y;
        _xVelocity = _yVelocity = 0;
        _xAcceleration = _yAcceleration = 0;
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setPosition( double x, double y ) {
        _x = x;
        _y = y;
    }

    public double getX() {
        return _x;
    }
 
    public double getY() {
        return _y;
    }
    
    public void setVelocity( double xVelocity, double yVelocity ) {
        _xVelocity = xVelocity;
        _yVelocity = yVelocity;
    }
    
    public double getXVelocity() {
        return _xVelocity;
    }
    
    public double getYVelocity() {
        return _yVelocity;
    }

    public void setAcceleration( double xAcceleration, double yAcceleration ) {
        _xAcceleration = xAcceleration;
        _yAcceleration = yAcceleration;
    }
    
    public double getXAcceleration() {
        return _xAcceleration;
    }

    public double getYAcceleration() {
        return _yAcceleration;
    }
}