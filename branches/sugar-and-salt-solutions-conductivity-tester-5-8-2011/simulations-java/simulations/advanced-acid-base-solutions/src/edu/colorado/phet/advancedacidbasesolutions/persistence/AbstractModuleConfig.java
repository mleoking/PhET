// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * Base class for module configuration persistence.
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
    // Accessors
    //----------------------------------------------------------------------------
    
    public boolean isActive() {
        return active;
    }

    
    public void setActive( boolean active ) {
        this.active = active;
    }
}
