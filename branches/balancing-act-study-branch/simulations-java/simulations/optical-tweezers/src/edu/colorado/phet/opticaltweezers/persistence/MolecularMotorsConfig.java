// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * MolecularMotorsConfig describes a configuration of the "Molecular Motors" flavor.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolecularMotorsConfig implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GlobalConfig _globalConfig;
    private MotorsConfig _motorsConfig;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public MolecularMotorsConfig() {
        _globalConfig = new GlobalConfig();
        _motorsConfig = new MotorsConfig();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setGlobalConfig( GlobalConfig globalConfig ) {
        _globalConfig = globalConfig;
    }
    
    public GlobalConfig getGlobalConfig() {
        return _globalConfig;
    }    
    
    public void setMotorsConfig( MotorsConfig motorsConfig ) {
        _motorsConfig = motorsConfig;
    }
    
    public MotorsConfig getMotorsConfig() {
        return _motorsConfig;
    }
}
