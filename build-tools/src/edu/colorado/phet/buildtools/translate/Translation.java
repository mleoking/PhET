package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class Translation {

    private File file;

    public static final String TRANSLATION_JAVA = "java";
    public static final String TRANSLATION_FLASH = "flash";

    public Translation( File translationFile ) {
        this.file = translationFile;

        System.out.println( "Created " + toString() );
    }

    public boolean isValid() {
        return ( getSimName() != null && getType() != null );
    }

    public File getFile() {
        return file;
    }

    public String getSimName() {
        String simname = null;
        final int index = file.getName().indexOf( "-strings_" );
        if ( index != -1 ) {
            simname = file.getName().substring( 0, index );
        }

        if ( simname != null && ( simname.equals( "java-common-strings" ) || simname.equals( "flash-common-strings" ) ) ) {
            simname = null;
        }

        return simname;
    }

    public boolean isCommonTranslation() {
        boolean ret = false;

        String type = getType();
        String name = file.getName();

        if ( isJavaTranslation() ) {
            ret = name.startsWith( "phetcommon-strings_" );
        }
        else if ( isFlashTranslation() ) {
            ret = name.startsWith( "common-strings_" );
        }

        return ret;
    }

    public boolean isSimulationTranslation() {
        return isValid() && !isCommonTranslation();
    }

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

    public boolean isJavaTranslation() {
        String type = getType();
        if ( type == null ) { return false; }
        return type.equals( TRANSLATION_JAVA );
    }

    public boolean isFlashTranslation() {
        String type = getType();
        if ( type == null ) { return false; }
        return type.equals( TRANSLATION_FLASH );
    }

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

    public File getSimulationsDir( File trunk ) {
        if ( isJavaTranslation() ) {
            return new File( trunk, "simulations-java/simulations" );
        }
        else if ( isFlashTranslation() ) {
            return new File( trunk, "simulations-flash/simulations" );
        }

        return null;
    }

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

    public String toString() {
        return file.getName() + " (" + getSimName() + ", " + getType() + ", " + LocaleUtils.localeToString( getLocale() ) + ")";
    }

}
