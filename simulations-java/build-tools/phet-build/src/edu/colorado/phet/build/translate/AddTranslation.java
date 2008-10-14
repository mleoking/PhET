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

    private static final boolean DEPLOY_ENABLED = false; // can be turned off for local debugging

    public static File TRANSLATIONS_TEMP_DIR = new File( FileUtils.getTmpDir(), "phet-translations-temp" );

    private File basedir;

    public static class AddTranslationReturnValue {

        private final String simulation;
        private final String language;
        private final boolean success;

        public AddTranslationReturnValue( String simulation, String language, boolean success ) {
            this.simulation = simulation;
            this.language = language;
            this.success = success;
        }

        public String getSimulation() {
            return simulation;
        }

        public String getLanguage() {
            return language;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public AddTranslation( File basedir ) {
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
    public AddTranslationReturnValue addTranslation( String simulation, String language, String user, String password ) {

        boolean success = true;

        try {
            PhetProject phetProject = new PhetProject( new File( basedir, "simulations" ), simulation );

            //Clear the temp directory for this simulation
            FileUtils.delete( getTempProjectDir( phetProject ), true );

            //check for existence of localization file for project, throw exception if doesn't exist
            if ( !phetProject.getLocalizationFile( language ).exists() ) {
                throw new RuntimeException( "localization file doesn't exist for sim: " + phetProject.getName() + ", lang=" + language );
            }

            // Get flavors once, reuse in each iteration
            PhetProjectFlavor[] flavors = phetProject.getFlavors();

            System.out.println( "Downloading all jars" );
            //Download all flavor JAR files for this project
            for ( int i = 0; i < flavors.length; i++ ) {
                downloadJAR( phetProject, flavors[i].getFlavorName() );
            }
            downloadJAR( phetProject, phetProject.getName() );//also download the webstart JAR
            System.out.println( "Finished downloading all jars" );

            System.out.println( "Updating all jars." );
            //Update all flavor JAR files
            for ( int i = 0; i < flavors.length; i++ ) {
                updateJAR( phetProject, flavors[i].getFlavorName(), language );
            }
            updateJAR( phetProject, phetProject.getName(), language );//also update the webstart JAR
            System.out.println( "Finished updating all jars" );

            //create a JNLP file for each flavor
            System.out.println( "Building JNLP" );
            PhetBuildJnlpTask.buildJNLPForSimAndLanguage( phetProject, language );
            checkMainClasses( phetProject, language );
            System.out.println( "Finished building JNLP" );


            if ( success && DEPLOY_ENABLED ) {//Can disable for local testing
                System.out.println( "Starting deploy" );
                //Deploy updated flavor JAR files
                for ( int i = 0; i < flavors.length; i++ ) {
                    deployJAR( phetProject, flavors[i].getFlavorName(), user, password );
                    deployJNLPFile( phetProject, flavors[i], language, user, password );
                }
                deployJAR( phetProject, phetProject.getName(), user, password );//also deploy the updated webstart JAR

                //poke the website to make sure it regenerates pages with the new info
                try {

                    //Note from Dano on 5/18/2008
                    //Line #118
                    //the web file referenced should be "cache-clear.php?cache=all" instead of "cache-clear-all.php"

                    /**
                     * Quick question:
//                    FileUtils.download( "http://phet.colorado.edu/new/admin/cache-clear.php?cache-all", new File( getTempProjectDir( phetProject ), "cache-clear-all.php" ) );

                    todo: Does the 2nd "cache-clear-all.php" need to change?

                    Dano's version:
                    FileUtils.download( "http://phet.colorado.edu/new/admin/cache-clear.php?cache-all", new File( getTempProjectDir( phetProject ), "cache-clear.php?cache-all" ) );

                     */

                    FileUtils.download( "http://phet.colorado.edu/admin/cache-clear.php?cache=all", new File( getTempProjectDir( phetProject ), "cache-clear-all.php" ) );
//                    FileUtils.download( "http://phet.colorado.edu/admin/cache-clear.php?cache=sims", new File( getTempProjectDir( phetProject ), "cache-clear-all.php" ) );
//                    FileUtils.download( "http://phet.colorado.edu/new/admin/cache-clear-all.php", new File( getTempProjectDir( phetProject ), "cache-clear-all.php" ) );
                    System.out.println( "Deployed: " + phetProject.getName() + " in language " + language + ", please test it to make sure it works correctly." );
                    System.out.println( "Finished deploy" );
                }
                catch ( FileNotFoundException e ) {
                    e.printStackTrace();
                    success = false;
                }
            }

        }
        catch ( Exception e ) {
            e.printStackTrace();
            success = false;
        }

        return new AddTranslationReturnValue( simulation, language, success );
    }

    private void checkMainClasses( PhetProject project, String language ) throws IOException {
        for ( int i = 0; i < project.getFlavorNames().length; i++ ) {
            //download JNLP from main site and use as template in case any changes in main-class
            final File localFile = new File( TRANSLATIONS_TEMP_DIR, "template-" + project.getName() + ".jnlp" );
            localFile.deleteOnExit();
            String url = "http://phet.colorado.edu/sims/" + project.getName() + "/" + project.getFlavors()[i].getFlavorName() + ".jnlp";
            try {
                FileUtils.download( url, localFile );
            }
            catch( FileNotFoundException e ) {//not all sims have a flavor name equal to project name
                JOptionPane.showMessageDialog( null,"Could not find path: "+url+", need to resolve this, need to redeploy sim." );
//                FileUtils.download( "http://phet.colorado.edu/sims/" + project.getName() + "/" + project.getName() + ".jnlp", localFile );
            }
            String desiredMainClass = getMainClass( localFile );

            File newJNLPFile = new File( project.getDefaultDeployDir(), "" + project.getName() + "_" + language + ".jnlp" );
            if (!newJNLPFile.exists()){//not all sims have a flavor name equal to project name
                newJNLPFile=new File( project.getDefaultDeployDir(), "" + project.getFlavors()[i].getFlavorName() + "_" + language + ".jnlp" );
            }
            String repositoryMainClass = getMainClass( newJNLPFile );
            if ( !repositoryMainClass.equals( desiredMainClass ) ) {
                System.out.println( "Mismatch of main classes for project: " + project.getName() );
                String JNLP = FileUtils.loadFileAsString( newJNLPFile, "utf-16" );
                JNLP=FileUtils.replaceAll( JNLP,repositoryMainClass, desiredMainClass );
                FileUtils.writeString( newJNLPFile, JNLP, "utf-16" );
                System.out.println( "Wrote new JNLP file with tigercat main-class: " + desiredMainClass + " instead of repository main class: " + repositoryMainClass+": "+newJNLPFile.getAbsolutePath());
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
     * @throws IOException
     * @throws JSchException
     */
    private void deployJAR( PhetProject phetProject, String jarBaseName, String user, String password ) throws JSchException, IOException {
        final String filename = getRemoteDirectory( phetProject ) + jarBaseName + ".jar";
        ScpTo.uploadFile( getJARTempFile( phetProject, jarBaseName ), user, "tigercat.colorado.edu", filename, password );
    }

    private void deployJNLPFile( PhetProject phetProject, PhetProjectFlavor phetProjectFlavor, String locale, String user, String password ) throws JSchException, IOException {
        String filename = getRemoteDirectory( phetProject ) + phetProjectFlavor.getFlavorName() + "_" + locale + ".jnlp";
        ScpTo.uploadFile( getJNLPFile( phetProject, phetProjectFlavor, locale ), user, "tigercat.colorado.edu", filename, password );
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
        final File fileName = getJARTempFile( phetProject, jarBaseName );
        System.out.println( "Starting download to: " + fileName.getAbsolutePath() );
        FileUtils.download( url, fileName );
        System.out.println( "Finished download." );
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
