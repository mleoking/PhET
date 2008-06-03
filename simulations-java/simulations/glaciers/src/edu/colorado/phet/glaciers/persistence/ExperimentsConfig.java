/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;


/**
 * AdvancedConfig is a Java Bean compliant configuration of AdvancedModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExperimentsConfig implements IProguardKeepClass {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Module
    private boolean _active; // is the module active?
    
    // Clock
    private boolean _clockRunning;
    private double _clockDt;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public ExperimentsConfig() {}
    
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
}
