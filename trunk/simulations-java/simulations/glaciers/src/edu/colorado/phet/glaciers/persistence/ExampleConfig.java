/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.persistence;

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
    private boolean _active; // is the module active?
    
    // Clock
    private boolean _clockRunning;
    private double _clockDt;
    
    // Model
    private double _exampleModelElementPositionX, _exampleModelElementPositionY;
    private double _exampleModelElementOrientation;
    
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
        return _active;
    }

    
    public void setActive( boolean active ) {
        _active = active;
    }
    
    
    public boolean isClockRunning() {
        return _clockRunning;
    }

    
    public void setClockRunning( boolean clockRunning ) {
        _clockRunning = clockRunning;
    }
    
    public double getClockDt() {
        return _clockDt;
    }

    
    public void setClockDt( double clockDt ) {
        _clockDt = clockDt;
    }

    
    public double getExampleModelElementOrientation() {
        return _exampleModelElementOrientation;
    }

    
    public void setExampleModelElementOrientation( double exampleOrientation ) {
        _exampleModelElementOrientation = exampleOrientation;
    }

    
    public double getExampleModelElementPositionX() {
        return _exampleModelElementPositionX;
    }

    
    public void setExampleModelElementPositionX( double examplePositionX ) {
        _exampleModelElementPositionX = examplePositionX;
    }

    
    public double getExampleModelElementPositionY() {
        return _exampleModelElementPositionY;
    }

    
    public void setExampleModelElementPositionY( double examplePositionY ) {
        _exampleModelElementPositionY = examplePositionY;
    }
}
