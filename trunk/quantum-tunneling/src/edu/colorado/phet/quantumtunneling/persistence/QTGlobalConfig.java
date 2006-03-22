/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.persistence;

import edu.colorado.phet.quantumtunneling.color.QTColorScheme;


/**
 * QTGlobalConfig is a JavaBean-compliant data structure that stores
 * global configuration information.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTGlobalConfig implements QTSerializable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private String _versionNumber;
    private String _cvsTag;
    private String _colorSchemeName;
    private QTColorSchemeConfig _colorScheme;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public QTGlobalConfig() {}
    
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

    public String getColorSchemeName() {
        return _colorSchemeName;
    }

    public void setColorSchemeName( String colorSchemeSelection ) {
        _colorSchemeName = colorSchemeSelection;
    }

    public QTColorSchemeConfig getColorScheme() {
        return _colorScheme;
    }

    public void setColorScheme( QTColorSchemeConfig colorScheme ) {
        _colorScheme = colorScheme;
    }
    
    public void setColorScheme( QTColorScheme colorScheme ) {
        setColorScheme( new QTColorSchemeConfig( colorScheme ) );
    }
}
