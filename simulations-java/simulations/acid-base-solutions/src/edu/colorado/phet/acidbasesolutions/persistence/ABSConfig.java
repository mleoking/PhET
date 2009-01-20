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
    private String _versionString;
    private String _versionMajor;
    private String _versionMinor;
    private String _versionDev;
    private String _versionRevision;
    
    // Modules
    private SolutionsConfig _solutionsConfig;
    private ComparingConfig _comparingConfig;
    private MatchingGameConfig _matchGameConfig;
    private FindUnknownConfig _findUnknownConfig;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance, required by XMLEncoder.
     */
    public ABSConfig() {
        _solutionsConfig = new SolutionsConfig();
        _comparingConfig = new ComparingConfig();
        _matchGameConfig = new MatchingGameConfig();
        _findUnknownConfig = new FindUnknownConfig();
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
    
    public SolutionsConfig getSolutionsConfig() {
        return _solutionsConfig;
    }
    
    public void setSolutionsConfig( SolutionsConfig exampleConfig ) {
        _solutionsConfig = exampleConfig;
    }
    
    public ComparingConfig getComparingConfig() {
        return _comparingConfig;
    }

    public void setComparingConfig( ComparingConfig config ) {
        _comparingConfig = config;
    }

    public MatchingGameConfig getMatchGameConfig() {
        return _matchGameConfig;
    }

    public void setMatchGameConfig( MatchingGameConfig gameConfig ) {
        _matchGameConfig = gameConfig;
    }

    public FindUnknownConfig getFindUnknownConfig() {
        return _findUnknownConfig;
    }
    
    public void setFindUnknownConfig( FindUnknownConfig unknownConfig ) {
        _findUnknownConfig = unknownConfig;
    }
}
