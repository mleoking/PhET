package edu.colorado.phet.buildtools.flash;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.JOptionPane;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Feb 4, 2009
 * Time: 10:17:40 AM
 */
public class FlashBuildCommand {
    // returns boolean success of whether the sim was built without errors
    public static boolean build( String cmd, String sim, File trunk, boolean useWine ) throws IOException {

        boolean success;

        File outputFile = new File( trunk, "simulations-flash/build-output-temp/output-" + sim + ".txt" );

        // if the output file exists, remove it so we can correctly detect whether the build is completed
        if ( outputFile.exists() ) {
            boolean b = outputFile.delete();
            if ( !b ) {
                System.out.println( "Could not delete output file" );
                return false;
            }
        }

        // run the JSFL
        build( cmd, new String[]{sim}, trunk, useWine );
        long timeout = 1000 * 60 * 5;
        long startTime = System.currentTimeMillis();
        System.out.print( "Building the SWF, please wait" );
        while ( !outputFile.exists() && System.currentTimeMillis() - startTime < timeout ) {
            try {
                Thread.sleep( 2000 );
                System.out.print( '.' );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }

        if ( outputFile.exists() ) {
            // found an output file, thus the build (either success or failure) has completed

            // outputString should contain any error reports
            String outputString = FileUtils.loadFileAsString( outputFile );

            if ( outputString.indexOf( "Error" ) == -1 ) {
                System.out.println( "Successful build of SWF" );

                success = true;
            }
            else {
                System.out.println( "Failed to build the SWF" );

                String messageString = "The following errors were encountered in the build: \n\n";
                messageString += outputString.substring( 3 );
                JOptionPane.showMessageDialog( null, messageString );

                success = false;
            }

            // delete the output file for future builds
            outputFile.delete();
        }
        else {
            System.out.println( "Could not find output file. Was the SWF build not completed?" );

            JOptionPane.showMessageDialog( null, "Could not find output file. Was the SWF build not completed?" );

            success = false;
        }

        FileUtils.delete( new File( trunk, "simulations-flash/build-output-temp" ), true );

        return success;
    }

    public static void build( String cmd, String[] sims, File trunk, boolean useWine ) throws IOException {
        String template = FileUtils.loadFileAsString( new File( trunk, "build-tools/data/flash/build-template.jsfl" ) );
        String out = template;

        String trunkPipe;
        if ( useWine ) {
            trunkPipe = "C|/svn/trunk";
        }
        else {
            trunkPipe = trunk.getAbsolutePath().replace( ':', '|' ).replace( '\\', '/' );
        }

        out = FileUtils.replaceAll( out, "@TRUNK@", trunkPipe );
        out = FileUtils.replaceAll( out, "@SIMS@", toSimsString( sims ) );
        out = FileUtils.replaceAll( out, "@CLOSEFLASH@", "false" );
        System.out.println( "out = " + out );

        String outputSuffix = "simulations-flash" + File.separator + "build-output-temp" + File.separator + "build.jsfl";
        File outputFile = new File( trunk, outputSuffix );
        FileUtils.writeString( outputFile, out );


        Process p;
        if ( useWine ) {
            p = Runtime.getRuntime().exec( new String[]{
                    "wine",
                    "C:\\Program Files\\Macromedia\\Flash 8\\Flash.exe", // possibly replace this with cmdArray
                    "C:\\svn\\trunk\\" + outputSuffix.replace( '/', '\\' )
            } );
        }
        else {
            if ( PhetUtilities.isMacintosh() ) {
                String flashName = BuildLocalProperties.getInstance().getMacFlashName();
                String volume = BuildLocalProperties.getInstance().getMacTrunkVolume();
                String macPath = unixToMacPath( outputFile.getAbsolutePath() );
                Object[] args = { flashName, volume, macPath  };
                String pattern = "tell application \"{0}\" to open alias \"{1}:{2}\"";
                String actionScript = MessageFormat.format( pattern, args );
                p = Runtime.getRuntime().exec( new String[] { "osascript", "-e", actionScript } );
            }
            else {
                p = Runtime.getRuntime().exec( new String[] { cmd, outputFile.getAbsolutePath() } );
            }
        }
    }
    
    private static String unixToMacPath( String path ) {
        return path.replace( '/', ':' );
    }

    private static String toSimsString( String[] sims ) {
        String s = "";
        for ( int i = 0; i < sims.length; i++ ) {
            String sim = sims[i];
            s += "\"" + sim + "\"";
            if ( i < sims.length - 1 ) {
                s += ",";
            }
        }
        return s;
    }

}
