package edu.colorado.phet.buildtools.translate;

import java.io.File;
import java.io.IOException;
import java.io.FileFilter;
import java.util.Locale;

import javax.swing.*;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.flash.FlashSimulationProject;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.application.VersionInfoQuery;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

import com.jcraft.jsch.JSchException;

public class CommonTranslationDeployClient {

    private File resourceFile;
    private File trunk;
    private ResourceDeployClient client;
    private Translation translation;

    public CommonTranslationDeployClient( File resourceFile, File trunk ) {
        this.resourceFile = resourceFile;
        this.trunk = trunk;
    }

    public void deployCommonTranslation() {
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

            File propertiesFile = File.createTempFile( "resource", ".properties" );
            String propertiesString = "resourceFile=" + resourceFile.getName() + "\n";
            if ( type == Translation.TRANSLATION_JAVA ) {
                // TODO: properties should be constants somewhere
                propertiesString += "sims=" + getJavaSimNames() + "\n";
                propertiesString += "resourceDestination=/phetcommon/localization/\n";
                //propertiesString += "onlyAllJARs=true\n";
                //propertiesString += "generateJARs=true\n";
                propertiesString += "mode=java\n";
            }
            else if ( type == Translation.TRANSLATION_FLASH ) {
                propertiesString += "sims=" + getFlashSimNames() + "\n";
                propertiesString += "resourceDestination=/\n";
                //propertiesString += "onlyAllJARs=false\n";
                //propertiesString += "generateJARs=false\n";
                propertiesString += "mode=flash\n";
            }
            FileUtils.writeString( propertiesFile, propertiesString );

            client = new ResourceDeployClient( resourceFile, propertiesFile );

            System.out.println();
            System.out.println( "****** Uploading resource file and properties" );

            client.uploadResourceFile();

            if ( type == Translation.TRANSLATION_FLASH ) {
                uploadFlashHTMLs();
            }

            client.executeResourceDeployServer( trunk );


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

        File[] simDirs = getFlashSimulationDirs();

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

        System.out.println( "Getting tigercat info for " + simName + " (" + LocaleUtils.localeToString( locale ) + ")" );

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

    // comma-separated list of sim names
    public String getJavaSimNames() {
        return getDirNameList( getJavaSimulationDirs() );
    }

    // comma-separated list of sim names
    public String getFlashSimNames() {
        return getDirNameList( getFlashSimulationDirs() );
    }

    public File[] getJavaSimulationDirs() {
        //return new File[]{new File( trunk, "simulations-java/simulations/test-project" )};
        ///* TODO: switch to this after testing is done
        File simsDir = new File( trunk, "simulations-java/simulations" );

        File[] simDirs = simsDir.listFiles( new FileFilter() {
            public boolean accept( File file ) {
                return file.isDirectory() && !file.getName().startsWith( "." );
            }
        } );

        return simDirs;
        //*/
    }

    public File[] getFlashSimulationDirs() {
        //return new File[]{new File( trunk, "simulations-flash/simulations/test-flash-project" )};
        ///* TODO: switch to this after testing is done
        File simsDir = new File( trunk, "simulations-flash/simulations" );

        File[] simDirs = simsDir.listFiles( new FileFilter() {
            public boolean accept( File file ) {
                return file.isDirectory() && !file.getName().startsWith( "." );
            }
        } );

        return simDirs;
        //*/
    }

    public String getDirNameList( File[] dirs ) {
        String ret = "";

        if ( dirs.length == 0 ) {
            return ret;
        }

        for ( int i = 0; i < dirs.length; i++ ) {
            File dir = dirs[i];

            if ( i != 0 ) {
                ret += ",";
            }

            ret += dir.getName();
        }

        return ret;
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
            //testSSH();
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

        new CommonTranslationDeployClient( resourceFile, trunk ).deployCommonTranslation();


    }
}
