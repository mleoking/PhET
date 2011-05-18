package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.buildtools.*;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.util.ScpTo;
import edu.colorado.phet.buildtools.util.SshUtils;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

import com.jcraft.jsch.JSchException;

/*
 * This has been re-written to work with the Wicket website and has been transferred over on 6.21.10.
 * TODO: Review and redo documentation here.
 *
 * For Flash, as of Sep. 21st 2010, we build the following components client-side for translation deployment:
 * - Meta XML (this contains the simulation names and titles)
 * - HTML for each translation (for each simulation)
 * The translation files are also directly uploaded.   
 *
 *
 * ------------------------ OLDER DOCUMENTATION -----------------------------
 *
 * This is the 3-27-2009 rewrite of translation deploying.
 * Here's the basic technique:
 * 1. Client identifies new localization files to integrate, by putting them in a directory and telling the program which directory.
 * 2. Client uploads localization files to a new, unique folder on tigercat
 * 3. Client runs server side integration program, passing
 *          the path to "jar" utility
 *          the path to build-local.properties (for signing)
 *          the directory containing new localization files
 * 4. Client opens a browser in the unique directory
 * 5. User is instructed to wait for a "finished.txt" file to appear; this signifies that server side code is finished.
 * 4. For each project in the unique-dir, the server
 *      a. Copies the project_all.jar to the unique directory
 *      b. Runs java -jar to integrate the new translations into the project_all.jar
 *      c. Signs the modified project_all.jar
 *      d. Create the language JARs for testing (must be signed)
 *      e. (Optional) create JNLPs for testing (will need to be rewritten for actual codebase)
 * 5. Notifies completion with a file finished.txt or creates an error log error.txt
 * 6. User tests the new project_all.jar files and/or JNLP files
 * 7. User signifies to server that testing is complete
 * 8. Server copies the new project_all.jar file to the sim directory
 * 9. Server copies the language JARs to the server
 * 10. Server creates new JNLP files for production
 * 11. Server regenerates HTML to indicate new sims available.
 *
 * This technique won't exactly work for redeploying phetcommon translations, but it should provide many
 * of the right building blocks.
 *
 *
 * The process for Flash translations:
 * (1) Client identifies new localization files (including common strings files), and installs them into trunk
 * (2) User REALLY SHOULD manually commit these (but is not required to)
 * (3) Each Flash translation follows the following process (in the client)
 *     (a) If the translation is for common strings, we do the following process for each
 *         simulation translation for that particular locale
 *     (b) Pull the tigercat version information from phet-info. This will be the version that is the latest on the
 *         server, NOT what is on the local .properties
 *     (c) Build the HTML for the translation (in the regular deploy directory) using the version info from (b)
 *     (d) Generated HTML is SCP'ed to the temp directory on the server.
 *     (e) Simulation XML is SCP'ed to the temp directory on the server (significant for common string updates, so
 *         that WebsiteTranslationDeployServer and WebsiteTranslationDeployPublisher will see them.
 * (4) Server looks at each of the Flash translations in the temp directory, doing the following for each:
 *     (a) Copy the corresponding SWF from htdocs/sims/ into the temp directory for testing
 * (5) Wait for user to test the translations
 * (6) If OK, then the Publisher simply copies over the HTML from the temp directory into the corresponding sims directory
 * (7) Webcache is reset so that the new translations appear on the website
 *
 */
public class WebsiteTranslationDeployClient {
    private final File trunk;
    private final PhetWebsite server;
    private final AuthenticationInfo authenticationInfo;

    public WebsiteTranslationDeployClient( File trunk, PhetWebsite server ) {
        this.trunk = trunk;
        this.server = server;
        this.authenticationInfo = server.getServerAuthenticationInfo( BuildLocalProperties.getInstance() );
    }

    public void startClient() throws IOException {
        giveInstructions();
        String dirName = JOptionPane.showInputDialog( "Enter the name of the directory where your localization files are:" );
        // import the translations into the IDE workspace
        File localTranslationDir = new File( dirName );
        if ( !localTranslationDir.exists() ) {
            // added a less-ambiguous error message if the user selects a directory that does not exist
            System.err.println( "Directory was not found!" );
            return;
        }
        new ImportTranslations( trunk ).importTranslations( localTranslationDir );
        instructUserToCommit();

        buildMetaXML( localTranslationDir );

        String deployDirName = new SimpleDateFormat( "M-d-yyyy_h-ma" ).format( new Date() );
        System.out.println( "Deploying to: " + deployDirName );
        String remoteTranslationDirName = server.getTranslationStagingPath() + "/" + deployDirName;
        System.out.println( "Will upload to " + remoteTranslationDirName );

        boolean dirSuccess = mkdir( remoteTranslationDirName );
        if ( !dirSuccess ) {
            System.out.println( "Error was encountered while trying to create dirs on the server. Stopping process, please see console output" );
            return;
        }
        transfer( localTranslationDir, remoteTranslationDirName );

        transferFlashHTMLs( trunk, localTranslationDir, remoteTranslationDirName );

        SshUtils.executeCommand( "chmod o+rw " + remoteTranslationDirName, server.getServerHost(), authenticationInfo );

        String url = server.getDeployTranslationUrl( remoteTranslationDirName );

        PhetWebsite.openBrowser( url );

        System.out.println( "Opening browser to " + url );
        System.out.println( "If this does not succeed, please open elsewhere" );

    }

    private void buildMetaXML( File dir ) {
        for ( File file : dir.listFiles() ) {
            Translation translation = new Translation( file, trunk );
            if ( !translation.isValid() ) {
                System.out.println( "Skipping " + file.getAbsolutePath() + ", does not represent a valid translation" );
                continue;
            }
            try {
                PhetProject project = translation.getProject( trunk );
                project.writeMetaXML();
                edu.colorado.phet.common.phetcommon.util.FileUtils.copyToDir( project.getMetaXMLFile(), dir );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private void buildAndSendFlashTranslation( final String projectName, final String remotePathDir, final Locale locale,
                                               final FlashSimulationProject project, final AuthenticationInfo authenticationInfo ) {
        System.out.println( "Getting production server info for " + projectName + " (" + LocaleUtils.localeToString( locale ) + ")" );

        // fake info for phet-info
        PhetVersion oldVersion = new PhetVersion( "1", "00", "00", "20000", "10" );

        for ( final String simulationName : project.getSimulationNames() ) {
            final VersionInfoQuery query = new VersionInfoQuery( projectName, simulationName, oldVersion, false );

            query.addListener( new VersionInfoQuery.VersionInfoQueryListener() {
                public void done( final VersionInfoQuery.Response result ) {
                    PhetVersion version = result.getSimResponse().getVersion();
                    try {
                        System.out.println( "Obtained version information, generating HTML" );

                        // build the HTML into the correct deploy directory
                        project.buildHTML( simulationName, locale, version );

                        String htmlFilename = simulationName + "_" + LocaleUtils.localeToString( locale ) + ".html";

                        // transfer the HTML file
                        File localHTMLFile = new File( project.getDeployDir(), htmlFilename );
                        ScpTo.uploadFile( localHTMLFile, authenticationInfo.getUsername(), server.getServerHost(), remotePathDir + "/" + htmlFilename, authenticationInfo.getPassword() );
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
            query.send(); // TODO: we only print out an exception here if the version info query fails. should we do more?
        }

        // transfer the translation XML (in case transfer() only copied over common-strings_XX_YY.xml)
        File localSimXMLFile = project.getLocalizationFile( locale );
        try {
            ScpTo.uploadFile( localSimXMLFile, authenticationInfo.getUsername(), server.getServerHost(), remotePathDir + "/" + localSimXMLFile.getName(), authenticationInfo.getPassword() );
        }
        catch( JSchException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void transferFlashHTMLs( File trunk, File localTranslationDir, final String remoteTranslationDirName ) throws IOException {
        for ( File file : localTranslationDir.listFiles() ) {
            final Translation translation = new Translation( file, trunk );

            if ( !translation.isValid() ) {
                System.out.println( "Invalid translation for " + file.getName() );
                continue;
            }

            if ( !translation.isFlashTranslation() ) {
                continue;
            }

            if ( translation.isSimulationTranslation() ) {
                buildAndSendFlashTranslation( translation.getProjectName(), remoteTranslationDirName, translation.getLocale(), (FlashSimulationProject) translation.getProject( trunk ), authenticationInfo );
            }
            else if ( translation.isCommonTranslation() ) {
                // common strings instead, so we need to find all simulations with sim-strings for the same locale

                File[] projectDirectories = new File( trunk, BuildToolsPaths.SIMULATIONS_FLASH ).listFiles( new FileFilter() {
                    public boolean accept( File pathname ) {
                        return pathname.isDirectory() && !pathname.getName().startsWith( "." );
                    }
                } );

                for ( File projectDir : projectDirectories ) {
                    FlashSimulationProject project = new FlashSimulationProject( projectDir );

                    // for each sim with sim-strings of the same locale, we build and send the HTML like the user specified all of them individually
                    if ( project.hasLocale( translation.getLocale() ) ) {
                        buildAndSendFlashTranslation( project.getName(), remoteTranslationDirName, translation.getLocale(), project, authenticationInfo );
                        // have server copy over sim XML?
                    }
                }
            }

        }
    }

    private void instructUserToCommit() {
        JOptionPane.showMessageDialog( null,
                                       "<html>Localization files have been imported into your IDE workspace.<br>" +
                                       "Please refresh your workspace, examine the files,<br>" +
                                       "and manually commit them to the SVN repository.<br><br>" +
                                       "Press OK when you are ready to integrate the files into<br>" +
                                       "the PHET production server." );
    }

    private void giveInstructions() {
        JOptionPane.showMessageDialog( null,
                                       "<html>Put the localization files that you wish to deploy in a directory.<br>" +
                                       "When you have finished this step, press OK to continue.<br>" +
                                       "You will be prompted for the directory name.</html>" );
    }

    private boolean mkdir( String serverDir ) {
        return SshUtils.executeCommands( new String[]{
                "mkdir -p -m 777 " + serverDir
        }, server.getServerHost(), authenticationInfo );
    }

    private void transfer( File srcDir, String remotePathDir ) {

        //for some reason, the securechannelfacade fails with a "server didn't expect this file" error
        //the failure is on tigercat, but scf works properly on spot
        //but our code works on both; therefore there is probably a problem with the handshaking in securechannelfacade
        File[] files = srcDir.listFiles(); //TODO: should handle recursive for future use (if we ever want to support nested directories)
        for ( File file : files ) {
            if ( file.getName().startsWith( "." ) ) {
                //ignore
            }
            else {
                //server.getHost(), authenticationInfo.getUsername(), authenticationInfo.getPassword()
                try {
                    ScpTo.uploadFile( file, authenticationInfo.getUsername(), server.getServerHost(), remotePathDir + "/" + file.getName(), authenticationInfo.getPassword() );
                }
                catch( JSchException e ) {
                    e.printStackTrace();
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
//                    sshConnection.executeTask( new ScpUpload( new ScpFile( f[i],  ) ) );
            }
        }
    }

}