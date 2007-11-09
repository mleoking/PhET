/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;


public class ProjectProperties {
    
    private static final String TITLE_FORMAT = "{0} : {1} {2}";
    private static final char COMMON_PROJECT_NAME_SEPARATOR = ':';
    
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
    
    public static String getPhetEmailAddress() {
        return CONFIG.getProjectProperty( "phet.email" );
    }
    
    public static String[] getCommonProjectNames() {
        
        // get the list of common project names
        String allNames = CONFIG.getProjectProperty( "common.projects" );
        
        // remove all whitespace
        String patternStr = "\\s+";
        String replaceStr = "";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(allNames);
        allNames = matcher.replaceAll(replaceStr);
        
        // parse
        ArrayList l = new ArrayList();
        int index = allNames.indexOf( COMMON_PROJECT_NAME_SEPARATOR );
        int previousIndex = -1;
        while ( index != -1 ) {
            String name = allNames.substring( previousIndex + 1, index );
            l.add( name );
            System.out.println("name=<" + name + ">");//XXX
            previousIndex = index;
            index = allNames.indexOf( COMMON_PROJECT_NAME_SEPARATOR, index + 1 );
        }
        
        // convert to array
        return (String[]) l.toArray( new String[l.size()] );
    }
}
