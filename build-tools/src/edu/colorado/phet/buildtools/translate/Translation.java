package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;

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

        if( simname != null && ( simname.equals( "java-common-strings" ) || simname.equals( "flash-common-strings" ) ) ) {
            simname = null;
        }

        return simname;
    }

    public String getType() {
        String filename = file.getName();

        if( filename.endsWith( ".properties" ) ) {
            return TRANSLATION_JAVA;
        } else if( filename.endsWith( ".xml" ) ) {
            return TRANSLATION_FLASH;
        }

        return null;
    }

    public File getSimulationsDir( File trunk ) {
        if( getType().equals( TRANSLATION_JAVA) ) {
            return new File( trunk, "simulations-java/simulations" );
        } else if( getType().equals( TRANSLATION_FLASH ) ) {
            return new File( trunk, "simulations-flash/simulations" );
        }

        return null;
    }

    public PhetProject getProject( File trunk ) throws IOException {
        String simName = getSimName();
        
        if( getType().equals( TRANSLATION_JAVA) ) {
            return new JavaSimulationProject( new File( getSimulationsDir( trunk ), simName ) );
        } else if( getType().equals( TRANSLATION_FLASH ) ) {
            return new FlashSimulationProject( new File( getSimulationsDir( trunk ), simName ) );
        }

        return null;
    }

    public String toString() {
        return file.getName() + " (" + getSimName() + ", " + getType() + ")";
    }

}
