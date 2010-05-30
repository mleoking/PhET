package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.PhetWebsite;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.resource.ResourceDeployClient;
import edu.colorado.phet.buildtools.resource.ResourceDeployUtils;
import edu.colorado.phet.buildtools.resource.WebsiteResourceDeployClient;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

import com.jcraft.jsch.JSchException;

/**
 * Deploying a common simulation has four phases:
 * <p/>
 * (A) Run CommonTranslationDeployClient on your machine
 * (1) A dialog will open. Select the common strings file that you want to deploy and hit "open"
 * (2) This will create a new directory on tigercat under htdocs/sims/resources/
 * (3) The translation, related metadata, and if applicable new Flash HTML files will be uploaded
 * (4) Instructions will be printed for the steps below
 * (B) Run ResourceDeployServer on tigercat
 * shortcut: "htdocs/sims/resources/server (absolute-path-to-tmp-dir)"
 * This will not modify the live sim directory, just the temporary directory under resources.
 * Necessary files will be copied, backed up, poked with files, signed and/or generated
 * (C) Thoroughly test everything under htdocs/sims/resources/(tmp-dir)/test/
 * Every file under test/ will replace its live counterpart during publishing (except for SWFs, which are not changed)
 * (D) Run ResourceDeployPublisher on tigercat
 * shortcut: "htdocs/sims/resources/publish (absolute-path-to-tmp-dir)"
 * This will copy over the necessary files from the test/ dir to the live sim dirs.
 * <p/>
 * If something goes wrong with publishing (or some time after publishing) due to the deployment, this can be reverted:
 * (E) Run ResourceDeployReverter on tigercat
 * shortcut: "htdocs/sims/resources/revert (absolute-path-to-tmp-dir)"
 * This will replace any files copied into the live sim dirs with what was previously there (from the backup dir)
 * If the deployment created a new file, this file will not be automatically deleted (but you will be notified of its existence)
 * <p/>
 * For more details on the process, see ResourceDeployClient for an explanation of the temporary directory and storage
 */
public class WebsiteCommonTranslationDeployClient {

    private File resourceFile;
    private File trunk;
    private WebsiteResourceDeployClient client;
    private Translation translation;

    public WebsiteCommonTranslationDeployClient( File resourceFile, File trunk ) {
        this.resourceFile = resourceFile;
        this.trunk = trunk;
    }

    public void deployCommonTranslation( PhetWebsite website ) {
        translation = new Translation( resourceFile );
        if ( !translation.isValid() ) {
            System.out.println( "Not a valid translation file" );
            return;
        }
        if ( !translation.isCommonTranslation() ) {
            System.out.println( "Not a common translation file" );
            return;
        }

        try {
            String type = translation.getType();

            // create the properties file that we will use on the server to know what to do (and what the resource file is)

            File propertiesFile = File.createTempFile( "resource", ".properties" );
            String propertiesString = "resourceFile=" + resourceFile.getName() + "\n";
            if ( type.equals( Translation.TRANSLATION_JAVA ) ) {
                // TODO: properties should be constants somewhere
                propertiesString += "sims=" + ResourceDeployUtils.getJavaSimNames( trunk ) + "\n";
                propertiesString += "resourceDestination=" + BuildToolsPaths.JAVA_JAR_LOCALIZATION + "\n";
                propertiesString += "mode=java\n";
            }
            else if ( type.equals( Translation.TRANSLATION_FLASH ) ) {
                propertiesString += "sims=" + ResourceDeployUtils.getFlashSimNames( trunk ) + "\n";
                propertiesString += "resourceDestination=" + BuildToolsPaths.FLASH_JAR_LOCALIZATION + "\n";
                propertiesString += "mode=flash\n";
            }
            FileUtils.writeString( propertiesFile, propertiesString );

            // initialize the client

            client = new WebsiteResourceDeployClient( website, resourceFile, propertiesFile );

            System.out.println();
            System.out.println( "****** Uploading resource file and properties" );

            // uploads just the resource file and properties file (also creates the temporary directory structure)
            boolean success = client.uploadResourceFile();

            if ( !success ) {
                System.out.println( "Failure to upload resource file, aborting" );
                return;
            }

            if ( type == Translation.TRANSLATION_FLASH ) {
                // if applicable, build and upload the Flash HTMLs that need to be modified
                uploadFlashHTMLs();
            }

            // handle the rest of the deployment from the server
            PhetWebsite.openBrowser( website.getDeployResourceUrl( client.getTemporaryDirPath() ) );


        }
        catch( IOException e ) {
            e.printStackTrace();
            return;
        }
        catch( JSchException e ) {
            e.printStackTrace();
        }


    }

    private void uploadFlashHTMLs() throws IOException {
        System.out.println();
        System.out.println( "****** Building and sending Flash HTMLs" );

        File[] simDirs = ResourceDeployUtils.getFlashSimulationDirs( trunk );

        for ( int i = 0; i < simDirs.length; i++ ) {
            File simDir = simDirs[i];

            FlashSimulationProject project = new FlashSimulationProject( simDir );

            if ( project.hasLocale( translation.getLocale() ) ) {
                buildAndSendFlashHTML( simDir.getName(), translation.getLocale(), project );
            }
        }
    }

    // TODO: refactor this from TranslationDeployClient and here into somewhere common!

    private void buildAndSendFlashHTML( final String simName, final Locale locale, final FlashSimulationProject project ) {
        System.out.println( "*** Building and sending HTML for " + simName + " " + LocaleUtils.localeToString( locale ) );

        System.out.println( "Getting server info for " + simName + " (" + LocaleUtils.localeToString( locale ) + ")" );

        // fake info for phet-info
        PhetVersion oldVersion = new PhetVersion( "1", "00", "00", "20000", "10" );

        final VersionInfoQuery query = new VersionInfoQuery( simName, simName, oldVersion, false );

        query.addListener( new VersionInfoQuery.VersionInfoQueryListener() {

            public void done( final VersionInfoQuery.Response result ) {
                PhetVersion version = result.getSimResponse().getVersion();
                try {
                    System.out.println( "Obtained version information, generating HTML" );

                    // build the HTML into the correct deploy directory
                    project.buildHTML( locale, version );

                    String HTMLName = project.getName() + "_" + LocaleUtils.localeToString( locale ) + ".html";

                    // transfer the HTML file
                    File localHTMLFile = new File( project.getDeployDir(), HTMLName );
                    client.uploadExtraFile( localHTMLFile, simName );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
                catch( JSchException e ) {
                    e.printStackTrace();
                }
            }

            public void exception( Exception e ) {
                e.printStackTrace();
            }
        } );
        query.send();
        System.out.println( "end of BuildAndSendFlashHTML: " + simName );
    }

    public static void main( String[] args ) {
        if ( args.length == 0 ) {
            System.out.println( "You must specify the path to trunk!" );
            return;
        }

        File trunk;

        try {
            trunk = ( new File( args[0] ) ).getCanonicalFile();
            BuildLocalProperties.initRelativeToTrunk( trunk );
        }
        catch( IOException e ) {
            e.printStackTrace();
            return;
        }

        final JFileChooser fileChooser = new JFileChooser();
        int ret = fileChooser.showOpenDialog( null );
        if ( ret != JFileChooser.APPROVE_OPTION ) {
            System.out.println( "File was not selected, aborting" );
            return;
        }

        File resourceFile = fileChooser.getSelectedFile();

        new WebsiteCommonTranslationDeployClient( resourceFile, trunk ).deployCommonTranslation( PhetWebsite.FIGARO );


    }
}