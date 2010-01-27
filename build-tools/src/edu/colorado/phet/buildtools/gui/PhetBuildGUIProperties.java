package edu.colorado.phet.buildtools.gui;

import java.io.File;

import edu.colorado.phet.common.phetcommon.util.AbstractPropertiesFile;


public class PhetBuildGUIProperties extends AbstractPropertiesFile {
    
    private static final String FILENAME = System.getProperty( "user.home" ) + "/.phet/phet-build-gui.properties";
    
    public static final PhetBuildGUIProperties INSTANCE = new PhetBuildGUIProperties();

    public static PhetBuildGUIProperties getInstance() {
        return INSTANCE;
    }
    
    private PhetBuildGUIProperties() {
        super( new File( FILENAME ) );
    }
    
    public String getSelectedProject() {
        return getProperty( "project.selected" );
    }
    
    public void setSelectedProject( String value ) {
        setProperty( "project.selected", value );
    }
}
