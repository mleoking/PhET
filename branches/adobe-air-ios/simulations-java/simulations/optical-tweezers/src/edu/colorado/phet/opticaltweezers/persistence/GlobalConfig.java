// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * GlobalConfig is a JavaBean-compliant data structure that stores
 * global configuration information.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlobalConfig implements IProguardKeepClass {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private String _versionString;
    
    private String _versionMajor;
    private String _versionMinor;
    private String _versionDev;
    private String _versionRevision;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public GlobalConfig() {}

    
    //----------------------------------------------------------------------------
    // Accessors
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

}
