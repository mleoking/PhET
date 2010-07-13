/* Copyright 2007-2009, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.util.JavaVersion;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

/**
 * This is a convenience wrapper around PhetResources and PhetApplicationConfig that provides 
 * access to localized strings and images that reside in the classpath.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TUResources {
    
    private static final PhetResources RESOURCES = new PhetResources( "translation-utility" );
    
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
        PhetVersion version = RESOURCES.getVersion();
        return version.formatForAboutDialog() + " " + version.formatTimestamp();
    }
    
    public static String getVersionMajorMinorDev() {
        return RESOURCES.getVersion().formatMajorMinorDev();
    }
    
    public static String getOSVersion() {
        return System.getProperty( "os.name" ) + " " + System.getProperty( "os.version" );
    }
    
    public static String getJREVersion() {
        return "JRE " + new JavaVersion.JREVersion().toString();
    }
    
    /**
     * Gets the program's title, to be displayed in the title bar of dialogs and windows.
     * @return String
     */
    public static String getTitle() {
        Object[] titleFormatArgs = { 
                TUResources.getString( "translation-utility.name" ),
                TUResources.getString( "label.version" ),
                getVersion(),
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
     * Gets the names of the preferred fonts for a specified language code.
     * 
     * @param languageCode
     * @return String[], possibly null
     */
    public static String[] getPreferredFontNames( Locale locale ) {
        return PhetCommonResources.getPreferredFontNames( locale );
    }
    
    /**
     * Gets the tmp directory name.
     * @return
     */
    public static final String getTmpdir() {
        String tmpdir = System.getProperty( "java.io.tmpdir" );
        // a fix to handle the crazy path that Mac OS 10.5 returns, see Unfuddle #1817
        if ( PhetUtilities.isMacintosh() && tmpdir.contains( "+" ) ) {
            tmpdir = "/tmp/";
        }
        return tmpdir;
    }
}
