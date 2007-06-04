/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

public class DNAPivot {

    private double yOffset;
    private double xOffset;
    private double xVelocity, yVelocity;
    private double xAcceleration, yAcceleration;

    public DNAPivot( double xOffset, double yOffset ) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        xVelocity = yVelocity = 0;
        xAcceleration = yAcceleration = 0;
    }

    public double getXOffset() {
        return xOffset;
    }

    public void setXOffset( double offset ) {
        xOffset = offset;
    }
    
    public double getYOffset() {
        return yOffset;
    }
    
    public void setYOffset( double offset ) {
        yOffset = offset;
    }
    
    public double getXVelocity() {
        return xVelocity;
    }
    
    public void setXVelocity( double vx ) {
        this.xVelocity = vx;
    }
    
    public double getYVelocity() {
        return yVelocity;
    }
    
    public void setYVelocity( double vy ) {
        this.yVelocity = vy;
    }
    
    public double getXAcceleration() {
        return xAcceleration;
    }

    public void setXAcceleration( double ax ) {
        this.xAcceleration = ax;
    }

    public double getYAcceleration() {
        return yAcceleration;
    }

    public void setYAcceleration( double ay ) {
        this.yAcceleration = ay;
    }
}