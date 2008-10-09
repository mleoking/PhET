/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * This is a convenience wrapper around PhetResources and PhetApplicationConfig that provides 
 * access to localized strings and images that reside in the classpath.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TUResources {
    
    private static final PhetResources RESOURCES = new PhetResources( "translation-utility" );
    private static final Properties LANGUAGE_CODES = RESOURCES.getProperties( "language-codes.properties" );
    private static final Properties COMMON_PROJECTS = RESOURCES.getProperties( "common-projects.properties" );

    private static final String COMMON_PROJECTS_SEPARATOR = ",";
    private static final String LANGUAGE_CODES_SEPARATOR = ",";
    
    /* not intended for instantiation */
    private TUResources() {}
    
    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name  );
    }
    
    public static final char getChar( String name, char defaultValue ) {
        return RESOURCES.getLocalizedChar( name, defaultValue );
    }

    public static final Icon getIcon( String name ) {
        return new ImageIcon( RESOURCES.getImage( name ) );
    }
    
    public static final BufferedImage getCommonImage( String name ) {
        return PhetCommonResources.getInstance().getImage( name );
    }
    
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
        return RESOURCES.getVersion().formatForAboutDialog();
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
        return MessageFormat.format( "{0} : {1} {2}", titleFormatArgs );
    }
    
    /**
     * Gets a property from the project properties file (translation-utility.properties).
     * 
     * @param key
     * @return
     */
    public static String getProjectProperty( String key ) {
        return RESOURCES.getProjectProperty( key );
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
        String allNames = COMMON_PROJECTS.getProperty( "common.projects" );
        
        // remove all whitespace
        allNames = allNames.replaceAll( "\\s+", "" );
        
        // parse
        String[] names = allNames.split( COMMON_PROJECTS_SEPARATOR );
        
        return names;
    }
    
    /**
     * Gets the names of the preferred fonts for a specified language code.
     * 
     * @param languageCode
     * @return String[], possibly null
     */
    public static String[] getPreferredFontNames( String languageCode ) {
        return PhetCommonResources.getPreferredFontNames( languageCode );
    }
    
    public static String[] getLanguageCodes() {
        String[] codes = null;
        String allNames = LANGUAGE_CODES.getProperty( "language.codes" );
        if ( allNames != null ) {
            codes = allNames.split( LANGUAGE_CODES_SEPARATOR );
        }
        return codes;
    }
    
    public static String getLanguageName( String languageCode ) {
        String key = "language." + languageCode;
        return LANGUAGE_CODES.getProperty( key );
    }
}
