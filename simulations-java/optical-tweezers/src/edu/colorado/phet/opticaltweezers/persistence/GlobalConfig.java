/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.persistence;

/**
 * GlobalConfig is a JavaBean-compliant data structure that stores
 * global configuration information.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlobalConfig implements OTSerializable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private String _versionNumber;

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
    
    public String getVersionNumber() {
        return _versionNumber;
    }
    
    public void setVersionNumber( String versionNumber ) {
        _versionNumber = versionNumber;
    }
}
