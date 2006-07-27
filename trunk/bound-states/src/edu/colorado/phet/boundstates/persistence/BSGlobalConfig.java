/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.persistence;

import edu.colorado.phet.boundstates.color.BSColorScheme;



/**
 * BSGlobalConfig is a JavaBean-compliant data structure that stores
 * global configuration information.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSGlobalConfig implements BSSerializable {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final int ONE_WELL_MODULE = 1;
    public static final int TWO_WELL_MODULE = 2;
    public static final int MANY_WELL_MODULE = 3;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private String _applicationClassName;
    private String _versionNumber;
    private String _cvsTag;
    private String _colorSchemeName;
    private BSColorSchemeConfig _colorScheme;
    private String _activeModuleId;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public BSGlobalConfig() {}
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public String getApplicationClassName() {
        return _applicationClassName;    
    }
    
    public void setApplicationClassName( String applicationClassName ) {
        _applicationClassName = applicationClassName;
    }
    
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

    public BSColorSchemeConfig getColorScheme() {
        return _colorScheme;
    }

    public void setColorScheme( BSColorSchemeConfig colorScheme ) {
        _colorScheme = colorScheme;
    }
    
    public String getColorSchemeName() {
        return _colorSchemeName;
    }
 
    public void setColorSchemeName( String colorSchemeName ) {
        _colorSchemeName = colorSchemeName;
    }
    
    public void setColorScheme( BSColorScheme colorScheme ) {
        setColorScheme( new BSColorSchemeConfig( colorScheme ) );
    }

    public String getActiveModuleId() {
        return _activeModuleId;
    }
    
    public void setActiveModuleId( String selectedModuleId ) {
        _activeModuleId = selectedModuleId;
    }  
}
