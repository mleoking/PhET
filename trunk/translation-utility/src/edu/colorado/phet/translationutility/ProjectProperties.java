/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * ProjectProperties is the program's interface to its project properties file.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ProjectProperties {
    
    private static final String TITLE_FORMAT = "{0} : {1} {2}";
    private static final String COMMON_PROJECT_NAME_SEPARATOR = ",";
    private static final String FONT_NAME_SEPARATOR = ",";
        
    private static final PhetApplicationConfig CONFIG = new PhetApplicationConfig( null /* args */, new FrameSetup.NoOp(), TUResources.getResourceLoader() );

    /* not intended for instantiation */
    private ProjectProperties() {}
        
    /**
     * Gets the visible name for the program.
     * @return String
     */
    public static String getName() {
        return TUResources.getString( "translation-utility.name" );
    }
    
    /**
     * Gets the programs full version number.
     * @return
     */
    public static String getVersion() {
        return CONFIG.getVersion().formatForAboutDialog();
    }
    
    /**
     * Gets the program's title, to be displayed in the title bar of dialogs and windows.
     * @return String
     */
    public static String getTitle() {
        String[] titleFormatArgs = { 
                TUResources.getString( "translation-utility.name" ),
                TUResources.getString( "label.version" ),
                getVersion()
        };
        return MessageFormat.format( TITLE_FORMAT, titleFormatArgs );
    }
    
    /**
     * Gets the names of all common projects in the PhET source code repository.
     * This includes any common projects that are used to build simulations, and that 
     * may contain localized strings.
     * 
     * @return String[]
     */
    public static String[] getCommonProjectNames() {
        
        // get the list of common project names
        String allNames = CONFIG.getProjectProperty( "common.projects" );
        
        // remove all whitespace
        allNames = allNames.replaceAll( "\\s+", "" );
        
        // parse
        String[] names = allNames.split( COMMON_PROJECT_NAME_SEPARATOR );
        
        return names;
    }
    
    /**
     * Gets the names of the preferred fonts for a specified language code.
     * 
     * @param languageCode
     * @return String[], possibly null
     */
    public static String[] getPreferredFontNames( String languageCode ) {
        String[] names = null;
        String key = "fonts." + languageCode; // eg, fonts.ja
        String allNames = CONFIG.getProjectProperty( key );
        if ( allNames != null ) {
            names = allNames.split( FONT_NAME_SEPARATOR );
        }
        return names;
    }
}
