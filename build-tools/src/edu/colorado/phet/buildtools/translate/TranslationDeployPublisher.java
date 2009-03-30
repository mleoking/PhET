package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.colorado.phet.buildtools.JARGenerator;
import edu.colorado.phet.buildtools.PhetServer;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Mar 28, 2009
 * Time: 11:10:11 AM
 */
public class TranslationDeployPublisher {
    private File sims;

    public TranslationDeployPublisher( File sims ) {
        this.sims = sims;
    }

    public static void main( String[] args ) throws IOException, InterruptedException {
        new TranslationDeployPublisher( new File( args[0] ) ).publishTranslations( new File( args[1] ) );
    }

    private void publishTranslations( File translationDir ) throws IOException {
        ArrayList javaProjectNameList = TranslationDeployServer.getJavaProjectNameList( translationDir );
        for ( int i = 0; i < javaProjectNameList.size(); i++ ) {
            String project = (String) javaProjectNameList.get( i );
            String[] locales = TranslationDeployServer.getJavaTranslatedLocales( translationDir, project );

            copyToSimsDir( translationDir, project, locales );
            generateJNLPs( translationDir, project, locales );

        }

        ArrayList flashProjectNameList = TranslationDeployServer.getFlashProjectNameList( translationDir );
        for ( int i = 0; i < flashProjectNameList.size(); i++ ) {
            String project = (String) flashProjectNameList.get( i );
            String[] locales = TranslationDeployServer.getFlashTranslatedLocales( translationDir, project );

            copyHTMLs( translationDir, project, locales );

        }

        clearWebCaches( translationDir );
    }

    private void copyHTMLs( File translationDir, String project, String[] locales ) {
        for( int i = 0; i < locales.length; i++ ) {
            String HTMLName = project + "_" + locales[i] + ".html";
            File fromFile = new File( translationDir, HTMLName );
            File toFile = new File( sims, project + "/" + HTMLName );
            try {
                FileUtils.copyTo( fromFile, toFile );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    //copy new translated JARs and project_all.jar to the sims directory
    private void copyToSimsDir( File translationDir, String project, String[] locales ) throws IOException {
        String[] flavors = JARGenerator.getFlavors( getAllJAR( translationDir, project ) );
        for ( int i = 0; i < flavors.length; i++ ) {
            for ( int k = 0; k < locales.length; k++ ) {
                File jar = new File( translationDir, flavors[i] + "_" + locales[k] + ".jar" );
                FileUtils.copyToDir( jar, new File( sims, project ) );
            }
        }
        FileUtils.copyToDir( getAllJAR( translationDir, project ), new File( sims, project ) );
    }

    private File getAllJAR( File translationDir, String project ) {
        return new File( translationDir, project + "_all.jar" );
    }

    //generate new JNLPs in sims directory
    //TODO: this is currently very brittle, no parser for the JNLP and just using string matching
    private void generateJNLPs( File translationDir, String project, String[] locales ) throws IOException {
        //TODO generate fresh JNLPs instead?
        //might be safer to copy existing JNLP to make sure main class is right, etc.

        String[] flavors = JARGenerator.getFlavors( getAllJAR( translationDir, project ) );
        for ( int i = 0; i < flavors.length; i++ ) {
            //for now, copy english JNLP and replace value="en" with value="locale_STR"
            //also fix codebase
            String englishJNLP = FileUtils.loadFileAsString( new File( sims, project + "/" + flavors[i] + "_en.jnlp" ), "UTF-16" );
            for ( int j = 0; j < locales.length; j++ ) {
                StringTokenizer stringTokenizer = new StringTokenizer( locales[j], "_ " );
                String language = stringTokenizer.nextToken();
                String country = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : null;

                String replacement = "<property name=\"javaws.user.language\" value=\"" + language + "\" />";
                if ( country != null ) {
                    replacement = replacement + "<property name=\"javaws.user.country\" value=\"" + country + "\" />";
                }
                String out = FileUtils.replaceFirst( englishJNLP, "<property name=\"javaws.user.language\" value=\"en\" />", replacement );

                out = FileUtils.replaceAll( out, "href=\"" + flavors[i] + "_en.jnlp\"", "href=\"" + flavors[i] + "_" + locales[j] + ".jnlp\"" );
                FileUtils.writeString( new File( sims, project + "/" + flavors[i] + "_" + locales[j] + ".jnlp" ), out, "UTF-16" );
            }
        }
    }

    //regenerate server HTML caches so new translations will appear
    private void clearWebCaches( File translationDir ) throws FileNotFoundException {
        System.out.println( "Clearing website cache" );
        FileUtils.download( PhetServer.PRODUCTION.getCacheClearUrl(), new File( translationDir, PhetServer.PRODUCTION.getCacheClearFile() ) );
    }
}