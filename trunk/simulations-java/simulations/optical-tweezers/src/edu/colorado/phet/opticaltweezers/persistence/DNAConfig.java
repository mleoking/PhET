/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.persistence;

/**
 * DNAConfig is a JavaBean-compliant data structure that stores
 * configuration information related to DNAModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAConfig implements OTSerializable {
    
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
    public DNAConfig() {}
    
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
