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
    private boolean deployEnabled = false;

    public AddTranslationTask( File basedir ) {
        this.basedir = basedir;
    }

    /**
     * This method is performed phase-wise (i.e. download all, then update all, then deploy all)
     * instead of (download #1, then update #1 then deploy #1) in order to make it easy to disable a single phase
     * and to facilitate batch deploy.
     *
     * @param simulation
     * @param language
     * @throws IOException
     */
    private void addTranslation( String simulation, String language ) throws IOException {
        PhetProject phetProject = new PhetProject( basedir, simulation );

        //Clear the temp directory for this simulation
        FileUtils.delete( getTempProjectDir( phetProject ), true );

        //Download all flavor JAR files for this project
        for ( int i = 0; i < phetProject.getFlavors().length; i++ ) {
            downloadFlavorJAR( phetProject, phetProject.getFlavors()[i] );
        }

        //Update all flavor JAR files
        for ( int i = 0; i < phetProject.getFlavors().length; i++ ) {
            updateFlavorJAR( phetProject, phetProject.getFlavors()[i] );
        }

        if ( deployEnabled ) {//Can disable for local testing
            //Deploy updated flavor JAR files
            for ( int i = 0; i < phetProject.getFlavors().length; i++ ) {
                deployFlavorJAR( phetProject, phetProject.getFlavors()[i] );
            }
        }
    }

    /**
     * Uploads the new JAR file to tigercat.
     *
     * @param phetProject
     * @param phetProjectFlavor
     */
    private void deployFlavorJAR( PhetProject phetProject, PhetProjectFlavor phetProjectFlavor ) {
    }

    /**
     * Creates a backup of the file, then integrates the specified sim translation file and all common translation files, if they exist.
     * This also tests for errors: it does not overwrite existing files, and it verifies afterwards that the
     * JAR just contains a single new file.
     *
     * @param phetProject
     * @param phetProjectFlavor
     */
    private void updateFlavorJAR( PhetProject phetProject, PhetProjectFlavor phetProjectFlavor ) throws IOException {
        FileUtils.copyTo( getFlavorJARTempFile( phetProject, phetProjectFlavor ), getFlavorJARTempBackupFile( phetProject, phetProjectFlavor ) );
    }

    private File getTempProjectDir( PhetProject phetProject ) {
        File dir = new File( TRANSLATIONS_TEMP_DIR, phetProject.getName() );
        dir.mkdirs();
        return dir;
    }

    private void downloadFlavorJAR( PhetProject phetProject, PhetProjectFlavor phetProjectFlavor ) throws FileNotFoundException {
        String url = phetProject.getDeployedFlavorJarURL( phetProjectFlavor.getFlavorName() );
        FileDownload.download( url, getFlavorJARTempFile( phetProject, phetProjectFlavor ) );
        System.out.println( "dest = " + getFlavorJARTempFile( phetProject, phetProjectFlavor ) );
    }

    private File getFlavorJARTempBackupFile( PhetProject phetProject, PhetProjectFlavor phetProjectFlavor ) {
        return getFlavorJARTempFile( phetProject, phetProjectFlavor, "_backup.jar" );
    }

    private File getFlavorJARTempFile( PhetProject phetProject, PhetProjectFlavor phetProjectFlavor ) {
        return getFlavorJARTempFile( phetProject, phetProjectFlavor, ".jar" );
    }

    private File getFlavorJARTempFile( PhetProject phetProject, PhetProjectFlavor phetProjectFlavor, String suffix ) {
        return new File( getTempProjectDir( phetProject ), phetProjectFlavor.getFlavorName() + suffix );
    }

    public static void main( String[] args ) throws IOException {
        File basedir = new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations" );
        new AddTranslationTask( basedir ).addTranslation( "cck", "nl" );
    }
}
