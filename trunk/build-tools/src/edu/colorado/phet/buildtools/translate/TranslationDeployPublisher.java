package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.buildtools.PhetServer;
import edu.colorado.phet.buildtools.util.FileUtils;

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
        ArrayList projectNameList = TranslationDeployServer.getProjectNameList( translationDir );
        for ( int i = 0; i < projectNameList.size(); i++ ) {
            String project = (String) projectNameList.get( i );
            String[] locales = TranslationDeployServer.getTranslatedLocales( translationDir, project );

            copyToSimsDir( translationDir, project, locales );
            generateJNLPs( translationDir, project, locales );

        }
        clearWebCaches( translationDir );
    }

    //copy new translated JARs and project_all.jar to the sims directory
    private void copyToSimsDir( File translationDir, String project, String[] locales ) throws IOException {
        for ( int i = 0; i < locales.length; i++ ) {
            File jar = new File( translationDir, project + "_" + locales[i] + ".jar" );
            FileUtils.copyToDir( jar, new File( sims, project ) );
        }
    }

    //generate new JNLPs in sims directory
    private void generateJNLPs( File translationDir, String project, String[] locales ) {
//        JNLP
    }

    //regenerate server HTML caches so new translations will appear
    private void clearWebCaches( File translationDir ) throws FileNotFoundException {
        System.out.println( "Clearing website cache" );
        FileUtils.download( PhetServer.PRODUCTION.getCacheClearUrl(), new File( translationDir, PhetServer.PRODUCTION.getCacheClearFile() ) );
    }
}