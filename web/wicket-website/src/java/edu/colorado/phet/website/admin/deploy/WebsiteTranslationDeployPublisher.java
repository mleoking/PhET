package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import edu.colorado.phet.buildtools.BuildScript;
import edu.colorado.phet.buildtools.JARGenerator;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.website.data.Project;

/**
 * Publishes a translation deployment from the temporary dir to the live sim dir
 */
public class WebsiteTranslationDeployPublisher {

    private File sims;
    private File docRoot;

    private static final Logger logger = Logger.getLogger( WebsiteTranslationDeployPublisher.class.getName() );

    public WebsiteTranslationDeployPublisher( File sims, File docRoot ) {
        this.sims = sims;
        this.docRoot = docRoot;
    }

    public void publishTranslations( File translationDir ) throws IOException {
        ArrayList javaProjectNameList = WebsiteTranslationDeployServer.getJavaProjectNameList( translationDir );
        for ( int i = 0; i < javaProjectNameList.size(); i++ ) {
            String project = (String) javaProjectNameList.get( i );

            logger.info( "backing up " + project );
            Project.backupProject( docRoot, project );
            String[] locales = WebsiteTranslationDeployServer.getJavaTranslatedLocales( translationDir, project );

            copyToSimsDir( translationDir, project, locales );
            generateJNLPs( translationDir, project, locales );

        }

        ArrayList flashProjectNameList = WebsiteTranslationDeployServer.getFlashProjectNameList( translationDir );
        for ( int i = 0; i < flashProjectNameList.size(); i++ ) {
            String project = (String) flashProjectNameList.get( i );

            if ( project.equals( "common" ) ) {
                logger.info( "Not publishing common strings XML directly" );
                continue;
            }

            logger.info( "backing up " + project );
            Project.backupProject( docRoot, project );

            String[] locales = WebsiteTranslationDeployServer.getFlashTranslatedLocales( translationDir, project );

            copyFlashFiles( translationDir, project, locales );

        }

        copyMetaXML( translationDir, javaProjectNameList );
        copyMetaXML( translationDir, flashProjectNameList );

        BuildScript.clearWebCaches();
    }

    public static Logger getLogger() {
        return logger;
    }

    private void copyMetaXML( File translationDir, ArrayList projectNameList ) {
        for ( Object ob : projectNameList ) {
            String project = (String) ob;
            try {
                FileUtils.copyToDir( new File( translationDir, project + ".xml" ), new File( sims, project ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    // copy the necessary flash HTML files in translationDir (with project and locales) to the main location

    private void copyFlashFiles( File translationDir, String project, String[] locales ) {
        for ( int i = 0; i < locales.length; i++ ) {
            try {
                // copy the JAR
                String JARName = project + "_" + locales[i] + ".jar";
                File fromJARFile = new File( translationDir, JARName );
                File toJARFile = new File( sims, project + "/" + JARName );
                FileUtils.copyTo( fromJARFile, toJARFile );

                // copy the HTML
                String HTMLName = project + "_" + locales[i] + ".html";
                File fromHTMLFile = new File( translationDir, HTMLName );
                File toHTMLFile = new File( sims, project + "/" + HTMLName );
                FileUtils.copyTo( fromHTMLFile, toHTMLFile );
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

}