// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * OpticalTweezersConfig describes a configuration of the "Optical Tweezers" flavor.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OpticalTweezersConfig implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GlobalConfig _globalConfig;
    private PhysicsConfig _physicsConfig;
    private DNAConfig _dnaConfig;
    private MotorsConfig _motorsConfig;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public OpticalTweezersConfig() {
        _globalConfig = new GlobalConfig();
        _physicsConfig = new PhysicsConfig();
        _dnaConfig = new DNAConfig();
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
    
    public void setPhysicsConfig( PhysicsConfig physicsConfig ) {
        _physicsConfig = physicsConfig;
    }
    
    public PhysicsConfig getPhysicsConfig() {
        return _physicsConfig;
    }
    
    public void setDNAConfig( DNAConfig dnaConfig ) {
        _dnaConfig = dnaConfig;
    }
    
    public DNAConfig getDNAConfig() {
        return _dnaConfig;
    }

    public void setMotorsConfig( MotorsConfig motorsConfig ) {
        _motorsConfig = motorsConfig;
    }
    
    public MotorsConfig getMotorsConfig() {
        return _motorsConfig;
    }
}
