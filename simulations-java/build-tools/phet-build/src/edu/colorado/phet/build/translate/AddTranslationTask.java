package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.PhetProjectFlavor;

/**
 * Created by: Sam
 * Jan 11, 2008 at 11:36:47 AM
 */
public class AddTranslationTask {
    private File basedir;
    public static File TRANSLATIONS_TEMP_DIR = new File( FileUtils.getTmpDir(), "phet-translations-temp" );

    public AddTranslationTask( File basedir ) {
        this.basedir = basedir;
    }

    private void addTranslation( String simulation, String language ) throws IOException {
        PhetProject phetProject = new PhetProject( basedir, simulation );

        //Download all flavor JAR files for this project
        for ( int i = 0; i < phetProject.getFlavors().length; i++ ) {
            downloadFlavorJAR( phetProject, phetProject.getFlavors()[i] );
        }

        //Update all flavor JAR files
        for ( int i = 0; i < phetProject.getFlavors().length; i++ ) {
//            updateFlavorJAR();
        }
    }

    private File getTempProjectDir( PhetProject phetProject ) {
        File dir = new File( TRANSLATIONS_TEMP_DIR, phetProject.getName() );
        dir.mkdirs();
        return dir;
    }

    private void downloadFlavorJAR( PhetProject phetProject, PhetProjectFlavor phetProjectFlavor ) throws FileNotFoundException {
        String url = phetProject.getDeployedFlavorJarURL( phetProjectFlavor.getFlavorName() );
        FileDownload.download( url, new File( getTempProjectDir( phetProject ), phetProjectFlavor.getFlavorName() + ".jar" ) );
    }

    public static void main( String[] args ) throws IOException {
        File basedir = new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java" );
        new AddTranslationTask( basedir ).addTranslation( "cck", "nl" );
    }
}
