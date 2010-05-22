package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import org.apache.log4j.Logger;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.JARGenerator;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.PhetJarSigner;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.util.StreamReaderThread;

/**
 * Takes translation files for Java and Flash in a temporary translation dir
 * and does the necessary tasks to construct testable JARs, JNLPs and HTMLs for translations to test, plus all
 * other files necessary.
 */
public class WebsiteTranslationDeployServer {

    private String jarCommand;
    private BuildLocalProperties buildLocalProperties;
    private File pathToSimsDir;

    private static final Logger logger = Logger.getLogger( WebsiteTranslationDeployServer.class.getName() );

    public WebsiteTranslationDeployServer( String jarCommand, BuildLocalProperties buildLocalProperties, File pathToSimsDir ) {
        this.jarCommand = jarCommand;
        this.buildLocalProperties = buildLocalProperties;
        this.pathToSimsDir = pathToSimsDir;
    }

    public void integrateTranslations( File translationDir ) throws IOException, InterruptedException {
        // java projects
        for ( Object o : getJavaProjectNameList( translationDir ) ) {
            integrateJavaTranslations( translationDir, (String) o );
        }

        // flash projects
        for ( Object o : getFlashProjectNameList( translationDir ) ) {
            integrateFlashTranslations( translationDir, (String) o );
        }

        runStringCommand( "chmod -R g+w " + translationDir.getCanonicalPath() );
    }

    public static Logger getLogger() {
        return logger;
    }

    private void integrateJavaTranslations( File translationDir, String project ) throws IOException, InterruptedException {
        logger.info( "**********************************" );
        logger.info( "**** Integrating java translations for: " + project );
        logger.info( "**********************************" );

        logger.info( "Copying sim JAR" );
        copySimJAR( translationDir, project );

        logger.info( "Creating backup of JAR" );
        createBackupOfJAR( getLocalCopyOfAllJAR( translationDir, project ) );

        logger.info( "Updating sim JAR" );
        updateSimJAR( translationDir, project );

        logger.info( "Signing JAR" );
        signJAR( translationDir, project );
//        createTestJNLPFiles( translationDir, project );//todo: implement optional JNLP test

        logger.info( "Creating offline JARs" );
        createOfflineJARFiles( translationDir, project );//todo: only create JARs for new submissions
        //todo: clean up JARs when done testing
    }

    private void integrateFlashTranslations( File translationDir, String project ) throws IOException, InterruptedException {
        if ( project.equals( "common" ) ) {
            // we can't copy htdocs/sims/common/common.swf, why even try?
            return;
        }

        copyFlashSWF( translationDir, project );
        File JAR = copyFlashJAR( translationDir, project );
        createFlashJARs( translationDir, project, JAR );
        JAR.delete();
    }

    private void copyFlashSWF( File translationDir, String project ) throws IOException {
        FileUtils.copyToDir( new File( pathToSimsDir, project + "/" + project + ".swf" ), translationDir );
    }

    private File copyFlashJAR( File translationDir, String project ) throws IOException {
        File JAR = new File( translationDir, project + "_all.jar" );
        FileUtils.copyTo( new File( pathToSimsDir, project + "/" + project + "_en.jar" ), JAR );
        return JAR;
    }

    private void createFlashJARs( File translationDir, String project, File JAR ) throws IOException, InterruptedException {
        String[] locales = getFlashTranslatedLocales( translationDir, project );
        for ( int i = 0; i < locales.length; i++ ) {
            String localeString = locales[i];
            Locale locale = LocaleUtils.stringToLocale( localeString );

            // get the JAR that we'll be poking stuff into (language JAR)
            File localJAR = new File( translationDir, project + "_" + localeString + ".jar" );
            FileUtils.copyTo( JAR, localJAR );

            // translation
            File localXML = new File( translationDir, project + "-strings_" + localeString + ".xml" );

            // add the translated strings into the JAR
            String addXMLCommand = jarCommand + " uf " + localJAR.getAbsolutePath() + " -C " + translationDir.getAbsolutePath() + " " + localXML.getName();
            runStringCommand( addXMLCommand );

            // build flash-launcher-args
            File flashLauncherArgs = new File( translationDir, "flash-launcher-args.txt" );
            FileUtils.writeString( flashLauncherArgs, project + " " + locale.getLanguage() + " " + locale.getCountry() );

            // add flash-launcher-args into the JAR
            String addFlashLauncherArgscommand = jarCommand + " uf " + localJAR.getAbsolutePath() + " -C " + translationDir.getAbsolutePath() + " " + flashLauncherArgs.getName();
            runStringCommand( addFlashLauncherArgscommand );

            flashLauncherArgs.delete();

            PhetJarSigner phetJarSigner = new PhetJarSigner( BuildLocalProperties.getInstance() );
            phetJarSigner.signJar( localJAR );
        }
    }

    private void createBackupOfJAR( File localCopyOfAllJAR ) throws IOException {
        FileUtils.copyTo( localCopyOfAllJAR, new File( localCopyOfAllJAR.getParentFile(), localCopyOfAllJAR.getName() + ".bak" ) );
    }

    public static ArrayList getJavaProjectNameList( File translationDir ) {
        HashSet projectNames = getJavaProjectNames( translationDir );
        ArrayList list = new ArrayList( projectNames );
        Collections.sort( list );//iterate in order in case any problems happen halfway through
        return list;
    }

    public static ArrayList getFlashProjectNameList( File translationDir ) {
        HashSet projectNames = getFlashProjectNames( translationDir );
        ArrayList list = new ArrayList( projectNames );
        Collections.sort( list );//iterate in order in case any problems happen halfway through
        return list;
    }

    private void updateSimJAR( File translationDir, String project ) throws IOException, InterruptedException {
        //integrate translations with jar -uf
        //logger.info( "Getting translated locales" );
        String[] locales = getJavaTranslatedLocales( translationDir, project );
        for ( int i = 0; i < locales.length; i++ ) {
            //logger.info( "Updating sim JAR for locale: " + locales[i] );
            copyTranslationSubDir( translationDir, project, locales[i] );
            //logger.info( "Copied translation sub dir" );
            File dst = getLocalCopyOfAllJAR( translationDir, project );

            String command = jarCommand + " uf " + dst.getAbsolutePath() + " -C " + translationDir.getAbsolutePath() + " " + project + "/localization/" + propertiesFilename( project, locales[i] );
            logger.info( "Updating sim JAR for " + project + " (" + locales[i] + ")" );
            runStringCommand( command );
        }
    }

    private void runStringCommand( String command ) throws IOException, InterruptedException {
        logger.info( "Running command: " + command );
        Process p = Runtime.getRuntime().exec( command );
        new StreamReaderThread( p.getErrorStream(), "err>" ).start();
        new StreamReaderThread( p.getInputStream(), "" ).start();
        p.waitFor();
    }

    private void copyTranslationSubDir( File translationDir, String project, String locale ) throws IOException {
        File translation = new File( translationDir, propertiesFilename( project, locale ) );
        //logger.info( "Translation file copy for: " + translation.getAbsolutePath() );
        FileUtils.copyToDir( translation, new File( translationDir, project + "/localization" ) );
    }

    private String propertiesFilename( String project, String locale ) {
        if ( locale.equals( "en" ) ) {
            return project + "-strings.properties";
        }
        else {
            return project + "-strings_" + locale + ".properties";
        }
    }

    public static String[] getJavaTranslatedLocales( File translationDir, final String project ) {
        return getTranslatedLocales( translationDir, project, ".properties" );
    }

    public static String[] getFlashTranslatedLocales( File translationDir, final String project ) {
        return getTranslatedLocales( translationDir, project, ".xml" );
    }

    public static String[] getTranslatedLocales( File translationDir, final String project, String endString ) {
        File[] f = translationDir.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return name.startsWith( project + "-strings" );
            }
        } );
        String[] locales = new String[f.length];
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            String name = file.getName();
            int startIndex = name.indexOf( '_' ) + 1;
            int endIndex = name.indexOf( endString );
            if ( startIndex == 0 ) {
                locales[i] = "en";
            }
            else {
                locales[i] = name.substring( startIndex, endIndex );
            }
        }
        return locales;
    }

    private void createOfflineJARFiles( File translationDir, String project ) throws IOException, InterruptedException {
        new JARGenerator().generateOfflineJARs( getLocalCopyOfAllJAR( translationDir, project ), jarCommand, buildLocalProperties );
    }

    private void createTestJNLPFiles( File translationDir, String project ) {
    }

    private void signJAR( File translationDir, String project ) {
        PhetJarSigner phetJarSigner = new PhetJarSigner( BuildLocalProperties.getInstance() );
        phetJarSigner.signJar( getLocalCopyOfAllJAR( translationDir, project ) );
    }

    private File getLocalCopyOfAllJAR( File translationDir, String project ) {
        return new File( translationDir, project + "_all.jar" );
    }

    private void copySimJAR( File translationDir, String project ) throws IOException {
        FileUtils.copyToDir( getLocalCopyOfAllJAR( pathToSimsDir, project + "/" + project ), translationDir );
    }

    private static HashSet getJavaProjectNames( File translationDir ) {
        return getProjectNames( translationDir, ".properties", "java" );
    }

    private static HashSet getFlashProjectNames( File translationDir ) {
        return getProjectNames( translationDir, ".xml", "flash" );
    }

    private static HashSet getProjectNames( File translationDir, final String endString, String typeString ) {
        File[] f = translationDir.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return ( name.endsWith( endString ) ) && name.indexOf( "-strings_" ) > 0;
            }
        } );
        HashSet set = new HashSet();
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            String projectName = file.getName().substring( 0, file.getName().indexOf( "-strings_" ) );
            logger.info( "Found " + typeString + " project: " + projectName );
            set.add( projectName );
        }
        return set;
    }

}