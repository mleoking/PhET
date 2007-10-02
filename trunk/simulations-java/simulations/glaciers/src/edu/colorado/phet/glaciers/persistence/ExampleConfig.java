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
    private double _examplePositionX, _examplePositionY;
    private double _exampleOrientation;
    
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

    
    public double getExampleOrientation() {
        return _exampleOrientation;
    }

    
    public void setExampleOrientation( double exampleOrientation ) {
        _exampleOrientation = exampleOrientation;
    }

    
    public double getExamplePositionX() {
        return _examplePositionX;
    }

    
    public void setExamplePositionX( double examplePositionX ) {
        _examplePositionX = examplePositionX;
    }

    
    public double getExamplePositionY() {
        return _examplePositionY;
    }

    
    public void setExamplePositionY( double examplePositionY ) {
        _examplePositionY = examplePositionY;
    }
}
