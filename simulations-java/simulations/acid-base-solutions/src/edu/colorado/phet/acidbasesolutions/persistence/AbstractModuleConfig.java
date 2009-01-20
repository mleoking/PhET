/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.persistence;

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
    private boolean _active; // is the module active?
    
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
        return _active;
    }

    
    public void setActive( boolean active ) {
        _active = active;
    }
}
