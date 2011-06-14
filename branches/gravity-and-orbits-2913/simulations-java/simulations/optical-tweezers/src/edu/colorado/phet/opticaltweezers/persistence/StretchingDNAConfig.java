// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * StretchingDNAConfig describes a configuration of the "Stretching DNA" flavor.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StretchingDNAConfig implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GlobalConfig _globalConfig;
    private DNAConfig _dnaConfig;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public StretchingDNAConfig() {
        _globalConfig = new GlobalConfig();
        _dnaConfig = new DNAConfig();
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
    
    public void setDNAConfig( DNAConfig dnaConfig ) {
        _dnaConfig = dnaConfig;
    }
    
    public DNAConfig getDNAConfig() {
        return _dnaConfig;
    }
}
