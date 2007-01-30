/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.persistence;



/**
 * PhysicsConfig is a JavaBean-compliant data structure that stores
 * configuration information related to PhysicsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PhysicsConfig implements OTSerializable {
    
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
    public PhysicsConfig() {}
    
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
