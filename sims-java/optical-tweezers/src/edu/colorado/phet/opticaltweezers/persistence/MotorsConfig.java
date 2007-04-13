/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.persistence;

/**
 * MotorsConfig is a JavaBean-compliant data structure that stores
 * configuration information related to MotorsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MotorsConfig implements OTSerializable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Clock
    private boolean _clockRunning;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public MotorsConfig() {}
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setClockRunning( boolean clockRunning ) {
        _clockRunning = clockRunning;
    }
    
    public boolean isClockRunning() {
        return _clockRunning;
    }
    
    //----------------------------------------------------------------------------
    // Convenience methods for non-JavaBean objects
    //----------------------------------------------------------------------------
}
