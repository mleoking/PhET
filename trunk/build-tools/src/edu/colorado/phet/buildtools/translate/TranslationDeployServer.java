package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.JARGenerator;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.PhetJarSigner;
import edu.colorado.phet.common.phetcommon.util.StreamReaderThread;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Mar 28, 2009
 * Time: 7:56:50 AM
 */
public class TranslationDeployServer {
    private String jarCommand;
    private File buildLocalProperties;
    private File pathToSimsDir;

    public TranslationDeployServer( String jarCommand, File buildLocalProperties, File pathToSimsDir ) {
        this.jarCommand = jarCommand;
        this.buildLocalProperties = buildLocalProperties;
        this.pathToSimsDir = pathToSimsDir;
    }

    public static void main( String[] args ) throws IOException, InterruptedException {
        new TranslationDeployServer( args[0], new File( args[1] ), new File( args[2] ) ).integrateTranslations( new File( args[3] ) );
    }

    public void integrateTranslations( File translationDir ) throws IOException, InterruptedException {
        ArrayList javaList = getJavaProjectNameList( translationDir );
        for ( int i = 0; i < javaList.size(); i++ ) {
            integrateJavaTranslations( translationDir, (String) javaList.get( i ) );
        }

        ArrayList flashList = getFlashProjectNameList( translationDir );
        for ( int i = 0; i < flashList.size(); i++ ) {
            integrateFlashTranslations( translationDir, (String) flashList.get( i ) );
        }
        
        signifyReadyForTesting( translationDir );
    }

    private void integrateJavaTranslations( File translationDir, String project ) throws IOException, InterruptedException {
        copySimJAR( translationDir, project );
        createBackupOfJAR( getLocalCopyOfAllJAR( translationDir, project ) );
        updateSimJAR( translationDir, project );
        signJAR( translationDir, project );
//        createTestJNLPFiles( translationDir, project );//todo: implement optional JNLP test
        createOfflineJARFiles( translationDir, project );//todo: only create JARs for new submissions
        //todo: clean up JARs when done testing
    }

    private void integrateFlashTranslations( File translationDir, String project ) throws IOException {
        copyFlashSWF( translationDir, project );
    }

    private void copyFlashSWF( File translationDir, String project ) throws IOException {
        FileUtils.copyToDir( new File( pathToSimsDir, project + "/" + project + ".swf" ), translationDir );
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

    private void signifyReadyForTesting( File translationDir ) throws IOException {
        //could use "touch" but this should work on non-unix machines too
        FileUtils.writeString( new File( translationDir, "finished.txt" ), "finished at " + new Date() );
    }

    private void updateSimJAR( File translationDir, String project ) throws IOException, InterruptedException {
        //integrate translations with jar -uf
        String[] locales = getTranslatedLocales( translationDir, project );
        for ( int i = 0; i < locales.length; i++ ) {
            copyTranslationSubDir( translationDir, project, locales[i] );
            File dst = getLocalCopyOfAllJAR( translationDir, project );
            String command = jarCommand + " uf " + dst.getAbsolutePath() + " -C " + translationDir.getAbsolutePath() + " " + project + "/localization/" + project + "-strings_" + locales[i] + ".properties";
            System.out.println( "Running command: " + command );
            Process p = Runtime.getRuntime().exec( command );
            new StreamReaderThread( p.getErrorStream(), "err>" ).start();
            new StreamReaderThread( p.getInputStream(), "" ).start();
            p.waitFor();
        }
    }

    private void copyTranslationSubDir( File translationDir, String project, String locale ) throws IOException {
        File translation = new File( translationDir, project + "-strings_" + locale + ".properties" );
        FileUtils.copyToDir( translation, new File( translationDir, project + "/localization" ) );
    }

    public static String[] getTranslatedLocales( File translationDir, final String project ) {
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
            int endIndex = name.indexOf( ".properties" );
            locales[i] = name.substring( startIndex, endIndex );
        }
        return locales;
    }

    private void createOfflineJARFiles( File translationDir, String project ) throws IOException, InterruptedException {
        new JARGenerator().generateOfflineJARs( getLocalCopyOfAllJAR( translationDir, project ), jarCommand, BuildLocalProperties.getProperties( buildLocalProperties ) );
    }

    private void createTestJNLPFiles( File translationDir, String project ) {
    }

    private void signJAR( File translationDir, String project ) {
        PhetJarSigner phetJarSigner = new PhetJarSigner( BuildLocalProperties.initFromPropertiesFile( buildLocalProperties ) );
        phetJarSigner.signJar( getLocalCopyOfAllJAR( translationDir, project ) );
    }

    private File getLocalCopyOfAllJAR( File translationDir, String project ) {
        return new File( translationDir, project + "_all.jar" );
    }

    private void copySimJAR( File translationDir, String project ) throws IOException {
        FileUtils.copyToDir( getLocalCopyOfAllJAR( pathToSimsDir, project + "/" + project ), translationDir );
    }

    private static HashSet getJavaProjectNames( File translationDir ) {
        File[] f = translationDir.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return (name.endsWith( ".properties" ) ) && name.indexOf( "-strings_" ) > 0;
            }
        } );
        HashSet set = new HashSet();
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            String projectName = file.getName().substring( 0, file.getName().indexOf( "-strings_" ) );
            System.out.println( "Found Java project: " + projectName );
            set.add( projectName );
        }
        return set;
    }

    private static HashSet getFlashProjectNames( File translationDir ) {
        File[] f = translationDir.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return (name.endsWith( ".xml" ) ) && name.indexOf( "-strings_" ) > 0;
            }
        } );
        HashSet set = new HashSet();
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            String projectName = file.getName().substring( 0, file.getName().indexOf( "-strings_" ) );
            System.out.println( "Found Flash project: " + projectName );
            set.add( projectName );
        }
        return set;
    }

    public static class Test {

        public static void main( String[] args ) throws IOException, InterruptedException {
            TranslationDeployServer server = new TranslationDeployServer( "C:\\j2sdk1.4.2_17\\bin\\jar.exe", new File( "C:\\reid\\phet\\svn\\trunk\\build-tools\\build-local.properties" ), new File( "C:\\reid\\phet\\sims" ) );
            server.integrateTranslations( new File( "C:\\Users\\Sam\\Desktop\\tx" ) );
        }
    }
}
