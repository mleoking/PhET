package edu.colorado.phet.buildtools.gui;

import java.io.File;

import edu.colorado.phet.common.phetcommon.util.AbstractPropertiesFile;

/**
 * Properties file that holds persisent information for PhetBuildGUI.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhetBuildGUIProperties extends AbstractPropertiesFile {
    
    private static final String FILENAME = System.getProperty( "user.home" ) + "/.phet/phet-build-gui.properties";
    private static final String PROPERTY_PROJECT_SELECTED = "project.selected";
    
    public static final PhetBuildGUIProperties INSTANCE = new PhetBuildGUIProperties();

    public static PhetBuildGUIProperties getInstance() {
        return INSTANCE;
    }
    
    private PhetBuildGUIProperties() {
        super( new File( FILENAME ) );
    }
    
    public String getProjectSelected() {
        return getProperty( PROPERTY_PROJECT_SELECTED );
    }
    
    public void setProjectSelected( String value ) {
        setProperty( PROPERTY_PROJECT_SELECTED, value );
    }
}
