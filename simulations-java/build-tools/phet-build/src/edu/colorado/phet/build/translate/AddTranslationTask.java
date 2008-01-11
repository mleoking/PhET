package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.build.PhetBuildJnlpTask;
import edu.colorado.phet.build.PhetProject;
import edu.colorado.phet.build.PhetProjectFlavor;

import com.jcraft.jsch.JSchException;

/**
 * Created by: Sam
 * Jan 11, 2008 at 11:36:47 AM
 */
public class AddTranslationTask {
    private File basedir;
    private boolean deployEnabled = true;

    public static File TRANSLATIONS_TEMP_DIR = new File( FileUtils.getTmpDir(), "phet-translations-temp" );


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
    private void addTranslation( String simulation, String language, String user, String password ) throws Exception {
        PhetProject phetProject = new PhetProject( basedir, simulation );

        //Clear the temp directory for this simulation
        FileUtils.delete( getTempProjectDir( phetProject ), true );

        //Download all flavor JAR files for this project
        for ( int i = 0; i < phetProject.getFlavors().length; i++ ) {
            downloadFlavorJAR( phetProject, phetProject.getFlavors()[i].getFlavorName() );
        }
        downloadFlavorJAR( phetProject, phetProject.getName() );//also download the webstart JAR

        //Update all flavor JAR files
        for ( int i = 0; i < phetProject.getFlavors().length; i++ ) {
            updateFlavorJAR( phetProject, phetProject.getFlavors()[i].getFlavorName(), language );
        }
        updateFlavorJAR( phetProject,phetProject.getName(),language );//also update the webstart JAR

        //create a JNLP file for each flavor
        PhetBuildJnlpTask.buildJNLPForSimAndLanguage( phetProject, language );

        if ( deployEnabled ) {//Can disable for local testing
            //Deploy updated flavor JAR files
            for ( int i = 0; i < phetProject.getFlavors().length; i++ ) {
                deployFlavorJAR( phetProject, phetProject.getFlavors()[i].getFlavorName(), user, password );
                deployJNLPFile( phetProject, phetProject.getFlavors()[i], language, user, password );
            }
            deployFlavorJAR( phetProject,phetProject.getName(), user, password );//also deploy the updated webstart JAR
        }

        //poke the website to make sure it regenerates pages with the new info
        FileDownload.download( "http://phet.colorado.edu/new/admin/test.php", new File( getTempProjectDir( phetProject ), "test.php" ) );
    }

    /**
     * Creates a backup of the file, then integrates the specified sim translation file and all common translation files, if they exist.
     * This also tests for errors: it does not overwrite existing files, and it verifies afterwards that the
     * JAR just contains a single new file.
     *
     * @param phetProject
     */
    private void updateFlavorJAR( PhetProject phetProject, String flavor, String language ) throws IOException {
        //create a backup copy of the JAR
        //todo: should we make a long-term backup?  This file will be deleted when temp dir is cleared on the next run.
        FileUtils.copyTo( getFlavorJARTempFile( phetProject, flavor ), getFlavorJARTempBackupFile( phetProject, flavor ) );

        //TODO: check that no files will be overwritten?

        //TODO: update with common localization files as well (for dependencies only)

        //Run the JAR update command
        String sim = phetProject.getName();
        String pathSep = File.separator;
        String command = "jar.exe uf " + flavor + ".jar" +
                         " -C " + getProjectDataDir( phetProject ) + " " + sim + pathSep + "localization" + pathSep + sim + "-strings_" + language + ".properties";
        System.out.println( "Running: " + command );
        Runtime.getRuntime().exec( command, new String[]{}, getTempProjectDir( phetProject ) );

        //TODO: Verify that new JAR is the same as the old JAR with the addition of the new file
    }

    private File getProjectDataDir( PhetProject phetProject ) {
        return new File( phetProject.getProjectDir(), "data" );
    }

    /**
     * Uploads the new JAR file to tigercat.
     *
     * @param phetProject
     */
    private void deployFlavorJAR( PhetProject phetProject, String flavor, String user, String password ) {
        final String filename = getRemoteDirectory( phetProject ) + flavor + ".jar";
        try {
            ScpTo.uploadFile( getFlavorJARTempFile( phetProject, flavor ), user, "tigercat.colorado.edu", filename, password );
        }
        catch( JSchException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void deployJNLPFile( PhetProject phetProject, PhetProjectFlavor phetProjectFlavor, String locale, String user, String password ) {
        String filename = getRemoteDirectory( phetProject ) + phetProjectFlavor.getFlavorName() + "_" + locale + ".jnlp";
        try {
            ScpTo.uploadFile( getJNLPFile( phetProject, phetProjectFlavor, locale ), user, "tigercat.colorado.edu", filename, password );
        }
        catch( JSchException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private File getJNLPFile( PhetProject phetProject, PhetProjectFlavor phetProjectFlavor, String locale ) {
        return new File( phetProject.getDefaultDeployDir(), phetProjectFlavor.getFlavorName() + "_" + locale + ".jnlp" );
    }

    private String getRemoteDirectory( PhetProject phetProject ) {
//        return "/home/tigercat/phet/reids/";
        return "/web/htdocs/phet/sims/" + phetProject.getName() + "/";
    }

    private File getTempProjectDir( PhetProject phetProject ) {
        File dir = new File( TRANSLATIONS_TEMP_DIR, phetProject.getName() );
        dir.mkdirs();
        return dir;
    }

    private void downloadFlavorJAR( PhetProject phetProject, String flavor ) throws FileNotFoundException {
        String url = phetProject.getDeployedFlavorJarURL( flavor );
        FileDownload.download( url, getFlavorJARTempFile( phetProject, flavor ) );
        System.out.println( "dest = " + getFlavorJARTempFile( phetProject, flavor ) );
    }

    private File getFlavorJARTempBackupFile( PhetProject phetProject, String flavorname ) {
        return getFlavorJARTempFile( phetProject, flavorname, "_backup.jar" );
    }

    private File getFlavorJARTempFile( PhetProject phetProject, String flavorname ) {
        return getFlavorJARTempFile( phetProject, flavorname, ".jar" );
    }

    private File getFlavorJARTempFile( PhetProject phetProject, String flavorname, String suffix ) {
        return new File( getTempProjectDir( phetProject ), flavorname + suffix );
    }

    public static void main( String[] args ) throws Exception {
        File basedir = new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations" );
        if ( args.length == 4 ) {
            new AddTranslationTask( basedir ).addTranslation( args[0], args[1], args[2], args[3] );
        }
        else {
            new AddTranslationTask( basedir ).addTranslation( prompt( "sim-name (e.g. cck)" ), prompt( "Language (e.g. es)" ), prompt( "username" ), prompt( "password" ) );
        }
        System.exit( 0 );//daemon thread running?
    }

    private static String prompt( String title ) {
        return JOptionPane.showInputDialog( title );
    }
}
