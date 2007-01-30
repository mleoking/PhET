/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.persistence;



/**
 * OTGlobalConfig is a JavaBean-compliant data structure that stores
 * global configuration information.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GlobalConfig implements OTSerializable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private String _versionNumber;
    private String _cvsTag;

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
    
    public String getCvsTag() {
        return _cvsTag;
    }
    
    public void setCvsTag( String buildNumber ) {
        _cvsTag = buildNumber;
    }
}
