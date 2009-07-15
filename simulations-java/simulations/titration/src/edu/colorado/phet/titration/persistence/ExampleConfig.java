/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.persistence;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * ExampleConfig is a Java Bean compliant configuration of ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleConfig implements IProguardKeepClass {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Module
    private boolean active; // is the module active?
    
    // Clock
    private boolean clockRunning;
    private double clockDt;
    
    // Model
    private double exampleModelElementPositionX, exampleModelElementPositionY;
    private double exampleModelElementOrientation;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public ExampleConfig() {}
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public boolean isActive() {
        return active;
    }

    
    public void setActive( boolean active ) {
        this.active = active;
    }
    
    
    public boolean isClockRunning() {
        return clockRunning;
    }

    
    public void setClockRunning( boolean clockRunning ) {
        this.clockRunning = clockRunning;
    }
    
    public double getClockDt() {
        return clockDt;
    }

    
    public void setClockDt( double clockDt ) {
        this.clockDt = clockDt;
    }

    public double getExampleModelElementPositionX() {
        return exampleModelElementPositionX;
    }

    
    public void setExampleModelElementPositionX( double examplePositionX ) {
        this.exampleModelElementPositionX = examplePositionX;
    }

    
    public double getExampleModelElementPositionY() {
        return exampleModelElementPositionY;
    }

    
    public void setExampleModelElementPositionY( double examplePositionY ) {
        this.exampleModelElementPositionY = examplePositionY;
    }
    
    
    public double getExampleModelElementOrientation() {
        return exampleModelElementOrientation;
    }

    
    public void setExampleModelElementOrientation( double exampleOrientation ) {
        this.exampleModelElementOrientation = exampleOrientation;
    }
    
    //----------------------------------------------------------------------------
    // Convenience methods
    //----------------------------------------------------------------------------
    
    public Point2D getExampleModelElementPosition() {
        return new Point2D.Double( exampleModelElementPositionX, exampleModelElementPositionY );
    }
    
    
    public void setExampleModelElementPosition( Point2D p ) {
        exampleModelElementPositionX = p.getX();
        exampleModelElementPositionY = p.getY();
    }
}
