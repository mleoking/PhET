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
public class AddTranslation {
    private File basedir;
    private boolean deployEnabled = true;

    public static File TRANSLATIONS_TEMP_DIR = new File( FileUtils.getTmpDir(), "phet-translations-temp" );

    public AddTranslation( File basedir ) {
        this( basedir, true );
    }

    public AddTranslation( File basedir, boolean deployEnabled ) {
        this.basedir = basedir;
        this.deployEnabled = deployEnabled;
    }

    public void setDeployEnabled( boolean deployEnabled ) {
        this.deployEnabled = deployEnabled;
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
    public void addTranslation( String simulation, String language, String user, String password ) throws Exception {
        PhetProject phetProject = new PhetProject( new File( basedir, "simulations" ), simulation );

        //Clear the temp directory for this simulation
        FileUtils.delete( getTempProjectDir( phetProject ), true );

        //check for existence of localization file for project, throw exception if doesn't exist
        if ( !phetProject.getLocalizationFile( language ).exists() ) {
            throw new RuntimeException( "localization file doesn't exist for sim: " + phetProject.getName() + ", lang=" + language );
        }

        // Get flavors once, reuse in each iteration
        PhetProjectFlavor[] flavors = phetProject.getFlavors();

        //Download all flavor JAR files for this project
        for ( int i = 0; i < flavors.length; i++ ) {
            downloadJAR( phetProject, flavors[i].getFlavorName() );
        }
        downloadJAR( phetProject, phetProject.getName() );//also download the webstart JAR

        //Update all flavor JAR files
        for ( int i = 0; i < flavors.length; i++ ) {
            updateJAR( phetProject, flavors[i].getFlavorName(), language );
        }
        updateJAR( phetProject, phetProject.getName(), language );//also update the webstart JAR

        //create a JNLP file for each flavor
        PhetBuildJnlpTask.buildJNLPForSimAndLanguage( phetProject, language );

        if ( deployEnabled ) {//Can disable for local testing
            //Deploy updated flavor JAR files
            for ( int i = 0; i < flavors.length; i++ ) {
                deployJAR( phetProject, flavors[i].getFlavorName(), user, password );
                deployJNLPFile( phetProject, flavors[i], language, user, password );
            }
            deployJAR( phetProject, phetProject.getName(), user, password );//also deploy the updated webstart JAR
            
            //poke the website to make sure it regenerates pages with the new info
            FileUtils.download( "http://phet.colorado.edu/new/admin/test.php", new File( getTempProjectDir( phetProject ), "test.php" ) );
        }

    }

    /**
     * Creates a backup of the file, then iterates over all subprojects (including the sim itself) to update the jar
     *
     * @param phetProject
     */
    private void updateJAR( PhetProject phetProject, String jarBaseName, String language ) throws IOException {

        //todo: may later want to add a build-simulation-by-svn-number to handle revert

        //create a backup copy of the JAR
        FileUtils.copyTo( getJARTempFile( phetProject, jarBaseName ), getJARBackupFile( phetProject, jarBaseName ) );

        //add localization files for each subproject, including the simulation project itself
        for ( int i = 0; i < phetProject.getAllDependencies().length; i++ ) {

            //check existence of localization file for dependency before calling updateJARForDependency
            if ( phetProject.getAllDependencies()[i].getLocalizationFile( language ).exists() ) {
                updateJAR( phetProject, jarBaseName, language, phetProject.getAllDependencies()[i] );
            }
            else {
                System.out.println( "Simulation: " + phetProject.getName() + " depends on " + phetProject.getAllDependencies()[i].getName() + ", which does not contain a translation to: " + language );
            }
        }
    }

    /**
     * integrates the specified sim translation file and all common translation files, if they exist.
     * This also tests for errors: it does not overwrite existing files, and it verifies afterwards that the
     * JAR just contains a single new file.
     *
     * @param sim
     * @param jarBaseName
     * @param language
     * @param dependency
     * @throws IOException
     */
    private void updateJAR( PhetProject sim, String jarBaseName, String language, PhetProject dependency ) throws IOException {
        //Run the JAR update command

        String command = "jar uf " + jarBaseName + ".jar" +
                         " -C " + getProjectDataDir( dependency ) + " " + getLocalizationFilePathInDataDirectory( dependency, language );
        System.out.println( "Running: " + command + ", in directory: " + getTempProjectDir( sim ) );
        Process p = Runtime.getRuntime().exec( command, new String[]{}, getTempProjectDir( sim ) );
        try {
            int val = p.waitFor();
            if ( val != 0 ) {
                //TODO: what if JAR fails?
                throw new RuntimeException( "Exec failed: " + command );
            }
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        //TODO: Verify that new JAR is the same as the old JAR with the addition of the new file
    }

    private String getLocalizationFilePathInDataDirectory( PhetProject dependency, String language ) {
        String pathSep = File.separator;
        return dependency.getName() + pathSep + "localization" + pathSep + dependency.getName() + "-strings_" + language + ".properties";
    }

    private File getProjectDataDir( PhetProject phetProject ) {
        return new File( phetProject.getProjectDir(), "data" ).getAbsoluteFile();
    }

    /**
     * Uploads the new JAR file to tigercat.
     *
     * @param phetProject
     */
    private void deployJAR( PhetProject phetProject, String jarBaseName, String user, String password ) {
        final String filename = getRemoteDirectory( phetProject ) + jarBaseName + ".jar";
        try {
            ScpTo.uploadFile( getJARTempFile( phetProject, jarBaseName ), user, "tigercat.colorado.edu", filename, password );
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

    private void downloadJAR( PhetProject phetProject, String jarBaseName ) throws FileNotFoundException {
        String url = phetProject.getDeployedFlavorJarURL( jarBaseName );
        FileUtils.download( url, getJARTempFile( phetProject, jarBaseName ) );
        System.out.println( "dest = " + getJARTempFile( phetProject, jarBaseName ) );
    }

    private File getJARBackupFile( PhetProject phetProject, String jarBaseName ) {
        return getJARTempFile( phetProject, jarBaseName, "_backup.jar" );
    }

    private File getJARTempFile( PhetProject phetProject, String jarBaseName ) {
        return getJARTempFile( phetProject, jarBaseName, ".jar" );
    }

    private File getJARTempFile( PhetProject phetProject, String jarBaseName, String suffix ) {
        return new File( getTempProjectDir( phetProject ), jarBaseName + suffix );
    }

    public static String prompt( String title ) {
        return JOptionPane.showInputDialog( title );
    }

    public static void main( String[] args ) throws Exception {
        File basedir = new File( args[0] );
        if ( args.length == 5 ) {
            new AddTranslation( basedir ).addTranslation( args[1], args[2], args[3], args[4] );
        }
        else {
            new AddTranslation( basedir ).addTranslation( prompt( "sim-name (e.g. cck)" ), prompt( "Language (e.g. es)" ), prompt( "username" ), prompt( "password" ) );
        }
        System.exit( 0 );//daemon thread running?
    }

}
