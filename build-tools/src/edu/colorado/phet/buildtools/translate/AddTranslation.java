package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.buildtools.*;
import edu.colorado.phet.buildtools.java.projects.JavaSimulationProject;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.ScpTo;

import com.jcraft.jsch.JSchException;

/**
 * Created by: Sam
 * Jan 11, 2008 at 11:36:47 AM
 */
public class AddTranslation {

    private static final boolean DEPLOY_ENABLED = true; // can be turned off for local debugging

    public static File TRANSLATIONS_TEMP_DIR = new File( FileUtils.getTmpDir(), "phet-translations-temp" );

    private File basedir;

    public static class AddTranslationReturnValue {

        private final String simulation;
        private final Locale locale;
        private final boolean success;

        public AddTranslationReturnValue( String simulation, Locale locale, boolean success ) {
            this.simulation = simulation;
            this.locale = locale;
            this.success = success;
        }

        public String getSimulation() {
            return simulation;
        }

        public Locale getLocale() {
            return locale;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public AddTranslation( File basedir ) {
        this.basedir = basedir;
    }

    /*
     * This method is performed phase-wise (i.e. download all, then update all, then deploy all)
     * instead of (download #1, then update #1 then deploy #1) in order to make it easy to disable a single phase
     * and to facilitate batch deploy.
     *
     */
    public AddTranslationReturnValue addTranslation( String simulation, Locale locale, String user, String password ) {

        boolean success = true;

        try {
            PhetProject phetProject = new JavaSimulationProject( new File( basedir, "simulations" ), simulation );

            //Clear the temp directory for this simulation
            FileUtils.delete( getTempProjectDir( phetProject ), true );

            //check for existence of localization file for project, throw exception if doesn't exist
            if ( !phetProject.getLocalizationFile( locale ).exists() ) {
                throw new RuntimeException( "localization file doesn't exist for sim: " + phetProject.getName() + ", locale=" + locale );
            }

            // Get simulations once, reuse in each iteration
            Simulation[] simulations = phetProject.getSimulations();
            System.out.println( "Downloading <project>_all.jar" );
            downloadJAR( phetProject );
            System.out.println( "Finished downloading the jar" );

            System.out.println( "Updating the jar." );
            updateJAR( phetProject, locale );
            System.out.println( "Finished updating project_all.jar" );

            //create a JNLP file for each simulation
            System.out.println( "Building JNLP" );

            //todo: delete AddTranslation, should be using new deploy system
//            BuildJNLPTask.buildJNLPForSimAndLocale( phetProject, locale );
            checkMainClasses( phetProject, locale );
            System.out.println( "Finished building JNLP" );

            if ( success && DEPLOY_ENABLED ) {//Can disable for local testing
                System.out.println( "Starting deploy" );
                //Deploy updated simulation JAR files
                for ( int i = 0; i < simulations.length; i++ ) {
//                    deployJAR( phetProject, simulations[i].getName(), user, password );
                    deployJNLPFile( phetProject, simulations[i], locale, user, password );
                }
                deployJAR( phetProject, user, password );//also deploy the updated webstart JAR

                //run server side scripts to generate simulation x locale jars
                BuildScript.generateOfflineJars( phetProject, PhetServer.PRODUCTION, new AuthenticationInfo( user, password ) );

                //poke the website to make sure it regenerates pages with the new info
                try {
                    if ( ( PhetServer.PRODUCTION.getCacheClearUrl() != null ) && ( PhetServer.PRODUCTION.getCacheClearFile() != null ) ) {
                        System.out.println( "Clearing website cache" );
                        FileUtils.download( PhetServer.PRODUCTION.getCacheClearUrl(), new File( getTempProjectDir( phetProject ), PhetServer.PRODUCTION.getCacheClearFile() ) );
                    }
                    System.out.println( "Deployed: " + phetProject.getName() + " in locale " + locale + ", please test it to make sure it works correctly." );
                    System.out.println( "Finished deploy" );
                }
                catch( FileNotFoundException e ) {
                    e.printStackTrace();
                    success = false;
                }
            }

        }
        catch( Exception e ) {
            e.printStackTrace();
            success = false;
        }

        return new AddTranslationReturnValue( simulation, locale, success );
    }

    private void checkMainClasses( PhetProject project, Locale locale ) throws IOException {
        for ( int i = 0; i < project.getSimulationNames().length; i++ ) {
            //download JNLP from main site and use as template in case any changes in main-class
            final File localFile = new File( TRANSLATIONS_TEMP_DIR, "template-" + project.getName() + ".jnlp" );
            localFile.deleteOnExit();
            String url = "http://phet.colorado.edu/sims/" + project.getName() + "/" + project.getSimulations()[i].getName() + ".jnlp";
            try {
                FileUtils.download( url, localFile );
            }
            catch( FileNotFoundException e ) {//not all sims have a simulation name equal to project name
                JOptionPane.showMessageDialog( null, "Could not find path: " + url + ", need to resolve this, need to redeploy sim." );
            }
            String desiredMainClass = getMainClass( localFile );

            //See #1052
//            File newJNLPFile = new File( project.getDefaultDeployDir(), "" + project.getName() + "_" + language + ".jnlp" );
//            if ( !newJNLPFile.exists() ) {//not all sims have a simulation name equal to project name
            File newJNLPFile = new File( project.getDeployDir(), "" + project.getSimulations()[i].getName() + "_" + locale + ".jnlp" );
//            }
            String repositoryMainClass = getMainClass( newJNLPFile );
            if ( !repositoryMainClass.equals( desiredMainClass ) ) {
                System.out.println( "Mismatch of main classes for project: " + project.getName() );
                String JNLP = FileUtils.loadFileAsString( newJNLPFile, "utf-16" );
                JNLP = FileUtils.replaceAll( JNLP, repositoryMainClass, desiredMainClass );
                FileUtils.writeString( newJNLPFile, JNLP, "utf-16" );
                System.out.println( "Wrote new JNLP file with " + PhetServer.PRODUCTION.getHost() + " main-class: " + desiredMainClass + " instead of repository main class: " + repositoryMainClass + ": " + newJNLPFile.getAbsolutePath() );
            }
            //make sure main class is correct
        }
    }

    private String getMainClass( File localFile ) throws IOException {
        String text = FileUtils.loadFileAsString( localFile, "utf-16" );
        final String mainclassKey = "main-class=\"";
        String mainClass = text.substring( text.indexOf( mainclassKey ) + mainclassKey.length() );
        mainClass = mainClass.substring( 0, mainClass.indexOf( "\"" ) );
        System.out.println( "mainClass = " + mainClass );
        return mainClass;
    }

    /**
     * Creates a backup of the file, then iterates over all subprojects (including the sim itself) to update the jar
     */
    private void updateJAR( PhetProject phetProject, Locale locale ) throws IOException {

        //TODO: may later want to add a build-simulation-by-svn-number to handle revert

        //create a backup copy of the JAR
        FileUtils.copyTo( getJARTempFile( phetProject ), getJARBackupFile( phetProject ) );

        //add localization files for each subproject (such as phetcommon), including the simulation project itself
        for ( int i = 0; i < phetProject.getAllDependencies().length; i++ ) {

            //check existence of localization file for dependency before calling updateJARForDependency
            if ( phetProject.getAllDependencies()[i].getLocalizationFile( locale ).exists() ) {
                updateJAR( phetProject, locale, phetProject.getAllDependencies()[i] );
            }
            else {
                System.out.println( "Simulation: " + phetProject.getName() + " depends on " + phetProject.getAllDependencies()[i].getName() + ", which does not contain a translation to: " + locale );
            }
        }
    }

    /*
     * integrates the specified sim translation file and all common translation files, if they exist.
     * This also tests for errors: it does not overwrite existing files, and it verifies afterwards that the
     * JAR just contains a single new file.
     */
    private void updateJAR( PhetProject sim, Locale locale, PhetProject dependency ) throws IOException {
        //Run the JAR update command

        String command = "jar uf " + sim.getName() + "_all.jar" +
                         " -C " + getProjectDataDir( dependency ) + " " + getLocalizationFilePathInDataDirectory( dependency, locale );
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

    private String getLocalizationFilePathInDataDirectory( PhetProject dependency, Locale locale ) {
        String pathSep = File.separator;
        return dependency.getName() + pathSep + "localization" + pathSep + dependency.getName() + "-strings_" + locale + ".properties";
    }

    private File getProjectDataDir( PhetProject phetProject ) {
        return new File( phetProject.getProjectDir(), "data" ).getAbsoluteFile();
    }

    /*
     * Uploads the new JAR file to the production server.
     */
    private void deployJAR( PhetProject phetProject, String user, String password ) throws JSchException, IOException {
        final String filename = getRemoteDirectory( phetProject ) + phetProject.getName() + "_all.jar";
        ScpTo.uploadFile( getJARTempFile( phetProject ), user, PhetServer.PRODUCTION.getHost(), filename, password );
    }

    private void deployJNLPFile( PhetProject phetProject, Simulation simulation, Locale locale, String user, String password ) throws JSchException, IOException {
        String filename = getRemoteDirectory( phetProject ) + simulation.getName() + "_" + locale + ".jnlp";
        ScpTo.uploadFile( getJNLPFile( phetProject, simulation, locale ), user, PhetServer.PRODUCTION.getHost(), filename, password );
    }

    private File getJNLPFile( PhetProject phetProject, Simulation simulation, Locale locale ) {
        return new File( phetProject.getDeployDir(), simulation.getName() + "_" + locale + ".jnlp" );
    }

    private String getRemoteDirectory( PhetProject phetProject ) {
        return PhetServer.PRODUCTION.getServerDeployPath( phetProject ) + "/";
    }

    private File getTempProjectDir( PhetProject phetProject ) {
        File dir = new File( TRANSLATIONS_TEMP_DIR, phetProject.getName() );
        dir.mkdirs();
        return dir;
    }

    private void downloadJAR( PhetProject phetProject ) throws FileNotFoundException {
        String url = phetProject.getDeployedSimulationJarURL();
        final File fileName = getJARTempFile( phetProject );
        System.out.println( "Starting download to: " + fileName.getAbsolutePath() );
        FileUtils.download( url, fileName );
        System.out.println( "Finished download." );
    }

    private File getJARBackupFile( PhetProject phetProject ) {
        return new File( getTempProjectDir( phetProject ), phetProject.getName() + "_all_backup.jar" );
    }

    private File getJARTempFile( PhetProject phetProject ) {
        return new File( getTempProjectDir( phetProject ), phetProject.getName() + "_all.jar" );
    }

    public static String prompt( String title ) {
        return JOptionPane.showInputDialog( title );
    }

    public static void main( String[] args ) throws Exception {
        File basedir = new File( args[0] );
        if ( args.length == 5 ) {
            new AddTranslation( basedir ).addTranslation( args[1], new Locale( args[2] ), args[3], args[4] );
        }
        else {
            new AddTranslation( basedir ).addTranslation( prompt( "sim-name (e.g. cck)" ), new Locale( prompt( "Locale (e.g. es)" ) ), prompt( "username" ), prompt( "password" ) );
        }
        System.exit( 0 );//daemon thread running?
    }

}
