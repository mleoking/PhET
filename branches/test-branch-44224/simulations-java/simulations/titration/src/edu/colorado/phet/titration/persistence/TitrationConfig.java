/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * Describes a complete configuration of this simulation.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TitrationConfig implements IProguardKeepClass {

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
    private TitrateConfig titrateConfig;
    private AdvancedConfig advancedConfig;
    private PolyproticConfig polyproticConfig;
    private CompareConfig compareConfig;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public TitrationConfig() {
        titrateConfig = new TitrateConfig();
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
    
    public TitrateConfig getTitrateConfig() {
        return titrateConfig;
    }
    
    public void setTitrateConfig( TitrateConfig exampleConfig ) {
        this.titrateConfig = exampleConfig;
    }
    
    public AdvancedConfig getAdvancedConfig() {
        return advancedConfig;
    }
    
    public void setAdvancedConfig( AdvancedConfig advancedConfig ) {
        this.advancedConfig = advancedConfig;
    }
    
    public PolyproticConfig getPolyproticConfig() {
        return polyproticConfig;
    }

    public void setPolyproticConfig( PolyproticConfig polyproticConfig ) {
        this.polyproticConfig = polyproticConfig;
    }
    
    public CompareConfig getCompareConfig() {
        return compareConfig;
    }
    
    public void setCompareConfig( CompareConfig compareConfig ) {
        this.compareConfig = compareConfig;
    }
}
