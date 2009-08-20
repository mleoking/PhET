/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simtemplate.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * TemplateConfig describes a configuration of this simulation.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimTemplateConfig implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Global config
    private String versionString;
    private String versionMajor;
    private String versionMinor;
    private String versionDev;
    private String _versionRevision;
    
    // Modules
    private ExampleConfig exampleConfig;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public SimTemplateConfig() {
        exampleConfig = new ExampleConfig();
    }

    //----------------------------------------------------------------------------
    // Accessors for global information
    //----------------------------------------------------------------------------
    
    public String getVersionString() {
        return versionString;
    }
    
    public void setVersionString( String versionString ) {
        this.versionString = versionString;
    }
    
    public String getVersionMajor() {
        return versionMajor;
    }
    
    public void setVersionMajor( String versionMajor ) {
        this.versionMajor = versionMajor;
    }

    public String getVersionMinor() {
        return versionMinor;
    }
    
    public void setVersionMinor( String versionMinor ) {
        this.versionMinor = versionMinor;
    }
    
    public String getVersionDev() {
        return versionDev;
    }

    public void setVersionDev( String versionDev ) {
        this.versionDev = versionDev;
    }
    
    public String getVersionRevision() {
        return _versionRevision;
    }
    
    public void setVersionRevision( String versionRevision ) {
        this._versionRevision = versionRevision;
    }
    
    //----------------------------------------------------------------------------
    // Accessors for module configurations
    //----------------------------------------------------------------------------
    
    public void setExampleConfig( ExampleConfig exampleConfig ) {
        this.exampleConfig = exampleConfig;
    }
    
    public ExampleConfig getExampleConfig() {
        return exampleConfig;
    }
}
