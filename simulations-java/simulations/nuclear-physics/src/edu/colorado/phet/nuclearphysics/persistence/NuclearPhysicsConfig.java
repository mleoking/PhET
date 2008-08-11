/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * TemplateConfig describes a configuration of this simulation.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NuclearPhysicsConfig implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Global config
    private String _versionString;
    private String _versionMajor;
    private String _versionMinor;
    private String _versionDev;
    private String _versionRevision;
    
    // Modules
    private ExampleConfig _exampleConfig;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public NuclearPhysicsConfig() {
        _exampleConfig = new ExampleConfig();
    }

    //----------------------------------------------------------------------------
    // Accessors for global information
    //----------------------------------------------------------------------------
    
    public String getVersionString() {
        return _versionString;
    }
    
    public void setVersionString( String versionString ) {
        _versionString = versionString;
    }
    
    public String getVersionMajor() {
        return _versionMajor;
    }
    
    public void setVersionMajor( String versionMajor ) {
        _versionMajor = versionMajor;
    }

    public String getVersionMinor() {
        return _versionMinor;
    }
    
    public void setVersionMinor( String versionMinor ) {
        _versionMinor = versionMinor;
    }
    
    public String getVersionDev() {
        return _versionDev;
    }

    public void setVersionDev( String versionDev ) {
        _versionDev = versionDev;
    }
    
    public String getVersionRevision() {
        return _versionRevision;
    }
    
    public void setVersionRevision( String versionRevision ) {
        _versionRevision = versionRevision;
    }
    
    //----------------------------------------------------------------------------
    // Accessors for module configurations
    //----------------------------------------------------------------------------
    
    public void setExampleConfig( ExampleConfig exampleConfig ) {
        _exampleConfig = exampleConfig;
    }
    
    public ExampleConfig getExampleConfig() {
        return _exampleConfig;
    }
}
