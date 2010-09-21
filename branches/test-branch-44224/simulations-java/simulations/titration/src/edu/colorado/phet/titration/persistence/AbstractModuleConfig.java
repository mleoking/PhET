/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * A Java Bean compliant configuration that is the base class for all module configurations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AbstractModuleConfig implements IProguardKeepClass {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Module
    private boolean active; // is the module active?
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public AbstractModuleConfig() {}
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isActive() {
        return active;
    }

    
    public void setActive( boolean active ) {
        this.active = active;
    }
}
