/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * ABSConfig describes a configuration of this simulation.
 * It encapsulates all of the settings that the user can change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSConfig implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Global config
    private String versionString;
    private String versionMajor;
    private String versionMinor;
    private String versionDev;
    private String versionRevision;
    
    // Modules
    private SolutionsConfig solutionsConfig;
    private ComparingConfig comparingConfig;
    private MatchingGameConfig matchGameConfig;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public ABSConfig() {
        solutionsConfig = new SolutionsConfig();
        comparingConfig = new ComparingConfig();
        matchGameConfig = new MatchingGameConfig();
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
        return versionRevision;
    }
    
    public void setVersionRevision( String versionRevision ) {
        this.versionRevision = versionRevision;
    }
    
    //----------------------------------------------------------------------------
    // Accessors for module configurations
    //----------------------------------------------------------------------------
    
    public SolutionsConfig getSolutionsConfig() {
        return solutionsConfig;
    }
    
    public void setSolutionsConfig( SolutionsConfig exampleConfig ) {
        solutionsConfig = exampleConfig;
    }
    
    public ComparingConfig getComparingConfig() {
        return comparingConfig;
    }

    public void setComparingConfig( ComparingConfig config ) {
        comparingConfig = config;
    }

    public MatchingGameConfig getMatchGameConfig() {
        return matchGameConfig;
    }

    public void setMatchGameConfig( MatchingGameConfig gameConfig ) {
        matchGameConfig = gameConfig;
    }
}
