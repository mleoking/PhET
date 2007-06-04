/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

public class DNAPivot {

    private double _x, _y;
    private double _xVelocity, _yVelocity;
    private double _xAcceleration, _yAcceleration;

    public DNAPivot( double x, double y ) {
        _x = x;
        _y = y;
        _xVelocity = _yVelocity = 0;
        _xAcceleration = _yAcceleration = 0;
    }
    
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
        _x = xVelocity;
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