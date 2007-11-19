/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;


public class ProjectProperties {
    
    private static final String TITLE_FORMAT = "{0} : {1} {2}";
    private static final String COMMON_PROJECT_NAME_SEPARATOR = ":";
    
    private static final PhetApplicationConfig CONFIG = new PhetApplicationConfig( null /* args */, new FrameSetup.NoOp(), TUResources.getResourceLoader() );

    private ProjectProperties() {}
        
    public static String getName() {
        return TUResources.getString( "translation-utility.name" );
    }
    
    public static String getVersion() {
        return CONFIG.getVersion().formatForAboutDialog();
    }
    
    public static String getTitle() {
        String[] titleFormatArgs = { 
                TUResources.getString( "translation-utility.name" ),
                TUResources.getString( "label.version" ),
                getVersion()
        };
        return MessageFormat.format( TITLE_FORMAT, titleFormatArgs );
    }
    
    public static String[] getCommonProjectNames() {
        
        // get the list of common project names
        String allNames = CONFIG.getProjectProperty( "common.projects" );
        
        // remove all whitespace
        allNames = allNames.replaceAll( "\\s+", "" );
        
        // parse
        String[] names = allNames.split( COMMON_PROJECT_NAME_SEPARATOR );
        
        return names;
    }
}
