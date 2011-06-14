// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.quantumtunneling.persistence;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.quantumtunneling.color.QTColorScheme;


/**
 * QTGlobalConfig is a JavaBean-compliant data structure that stores
 * global configuration information.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class QTGlobalConfig implements IProguardKeepClass {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private String _versionNumber;
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
