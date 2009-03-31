package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

// Handles information pertaining to a particular translation

public class Translation {

    // the file where the translation is stored
    private File file;

    // types of translations
    public static final String TRANSLATION_JAVA = "java";
    public static final String TRANSLATION_FLASH = "flash";

    public Translation( File translationFile ) {
        this.file = translationFile;
    }

    // does this appear to be an actual translation?
    public boolean isValid() {
        return ( getSimName() != null && getType() != null );
    }

    // get the translation file
    public File getFile() {
        return file;
    }

    // sim name for the translation
    public String getSimName() {
        String simname = null;
        final int index = file.getName().indexOf( "-strings_" );
        if ( index != -1 ) {
            simname = file.getName().substring( 0, index );
        }

        // if strings for these sims somehow appear, they are not to be thought of as valid!!!
        if ( simname != null && ( simname.equals( "java-common-strings" ) || simname.equals( "flash-common-strings" ) ) ) {
            simname = null;
        }

        return simname;
    }

    // is this a common-strings translation? (IE not a simulation translation)
    public boolean isCommonTranslation() {
        boolean ret = false;

        String name = file.getName();

        if ( isJavaTranslation() ) {
            ret = name.startsWith( "phetcommon-strings_" );
        }
        else if ( isFlashTranslation() ) {
            ret = name.startsWith( "common-strings_" );
        }

        return ret;
    }

    // is this a sim translation? (IE not a common-string translation)
    public boolean isSimulationTranslation() {
        return isValid() && !isCommonTranslation();
    }

    // get the locale of the translation
    public Locale getLocale() {
        Locale ret = null;
        String search = "-strings_";
        int index = file.getName().indexOf( search );

        if ( index != -1 ) {
            index += search.length();
            String localeString = file.getName().substring( index, file.getName().indexOf( ".", index ) );
            ret = LocaleUtils.stringToLocale( localeString );
        }

        return ret;
    }

    // is it a translation for Java (sim or common)?
    public boolean isJavaTranslation() {
        String type = getType();
        if ( type == null ) { return false; }
        return type.equals( TRANSLATION_JAVA );
    }

    // is it a translation for Flash (sim or common)?
    public boolean isFlashTranslation() {
        String type = getType();
        if ( type == null ) { return false; }
        return type.equals( TRANSLATION_FLASH );
    }

    // return the type of the translation.
    // generally, use isJavaTranslation() or isFlashTranslation() instead.
    // this may return null!
    public String getType() {
        String filename = file.getName();

        if ( filename.endsWith( ".properties" ) ) {
            return TRANSLATION_JAVA;
        }
        else if ( filename.endsWith( ".xml" ) ) {
            return TRANSLATION_FLASH;
        }

        return null;
    }

    // convenience function for where simulations are stored
    private File getSimulationsDir( File trunk ) {
        if ( isJavaTranslation() ) {
            return new File( trunk, "simulations-java/simulations" );
        }
        else if ( isFlashTranslation() ) {
            return new File( trunk, "simulations-flash/simulations" );
        }

        return null;
    }

    // return a new copy of the project corresponding to this translation
    public PhetProject getProject( File trunk ) throws IOException {
        String simName = getSimName();

        if ( isJavaTranslation() ) {
            return new JavaSimulationProject( new File( getSimulationsDir( trunk ), simName ) );
        }
        else if ( isFlashTranslation() ) {
            return new FlashSimulationProject( new File( getSimulationsDir( trunk ), simName ) );
        }

        return null;
    }

    // return a string about the translation
    public String toString() {
        if ( isValid() ) {
            return file.getName() + " (" + getSimName() + ", " + getType() + ", " + LocaleUtils.localeToString( getLocale() ) + ")";
        }
        else {
            return file.getName() + " (possibly invalid)";
        }
    }

}
