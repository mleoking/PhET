package edu.colorado.phet.buildtools.flash;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

/**
 * Functions to build Flash 8 simulation SWFs from their FLAs using the Flash IDE (tested on version 8)
 * <p/>
 * A JSFL (JavaScript FLash) file is put in a temporary directory, then Flash is instructed to run it.
 * The script publishes the SWF for the simulation to the deploy directory, and then saves the build output
 * (like errors, etc) in a file in the temporary directory. We can tell whether the build succeeded or failed from that
 * file.
 */
public class FlashBuildCommand {

    /**
     * Whether the FLA should be closed once publishing is complete. Inserted into JSFL script that the Flash IDE is
     * instructed to run
     */
    public static boolean closeFLA = true;

    /**
     * Whether the Flash IDE should be closed once publishing is complete. Inserted into JSFL script that the Flash IDE
     * is instructed to run
     */
    public static boolean closeIDE = false;

    /**
     * Location of temporary build directory where we will store the JSFL, and the corresponding output files
     */
    public static final String BUILD_OUTPUT_TEMP = BuildToolsPaths.SIMULATIONS_FLASH + "/build-output-temp";

    /**
     * Build the specified sim in the SVN trunk specified. This function handles error recognition (and will show a dialog
     * if a build error is detected), and will wait up to 5 minutes until the Flash IDE successfully builds to exit.
     *
     * @param sim   The sim name to build
     * @param trunk Path to trunk
     * @return Success of the build
     * @throws IOException
     */
    public static boolean build( String sim, File trunk ) throws IOException {

        boolean success;

        File outputFile = new File( trunk, BUILD_OUTPUT_TEMP + "/output-" + sim + ".txt" );

        // if the output file exists, remove it so we can correctly detect whether the build is completed
        if ( outputFile.exists() ) {
            boolean b = outputFile.delete();
            if ( !b ) {
                System.out.println( "Could not delete output file" );
                return false;
            }
        }

        // run the JSFL
        build( new String[]{sim}, trunk );
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
            String outputString = edu.colorado.phet.common.phetcommon.util.FileUtils.loadFileAsString( outputFile );

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

        edu.colorado.phet.common.phetcommon.util.FileUtils.delete( new File( trunk, BUILD_OUTPUT_TEMP ), true );

        return success;
    }

    /**
     * Builds the JSFL file and launches the Flash IDE. This will not wait until the build is finished! Use
     * build( String sim, File trunk ) for regular use and error handling.
     *
     * @param sims  Sim name
     * @param trunk Path to trunk
     * @throws IOException
     */
    public static void build( String[] sims, File trunk ) throws IOException {
        String template = edu.colorado.phet.common.phetcommon.util.FileUtils.loadFileAsString( new File( trunk, BuildToolsPaths.FLASH_BUILD_TEMPLATE ) );
        String out = template;

        String trunkPipe;

        // if we are using wine, we need to handle this path differently
        final boolean useWine = BuildLocalProperties.getInstance().getWine();
        if ( useWine ) {
            trunkPipe = BuildLocalProperties.getInstance().getWineTrunk().replace( ':', '|' ).replace( '\\', '/' );
        }
        else {
            trunkPipe = trunk.getAbsolutePath().replace( ':', '|' ).replace( '\\', '/' );
        }

        out = edu.colorado.phet.common.phetcommon.util.FileUtils.replaceAll( out, "@TRUNK@", trunkPipe );
        out = edu.colorado.phet.common.phetcommon.util.FileUtils.replaceAll( out, "@SIMS@", toSimsString( sims ) );
        out = edu.colorado.phet.common.phetcommon.util.FileUtils.replaceAll( out, "@CLOSE_IDE@", Boolean.toString( closeIDE ) );
        out = edu.colorado.phet.common.phetcommon.util.FileUtils.replaceAll( out, "@CLOSE_FLA@", Boolean.toString( closeFLA ) );

        String outputSuffix = BUILD_OUTPUT_TEMP + "/build.jsfl";
        File outputFile = new File( trunk, outputSuffix );
        edu.colorado.phet.common.phetcommon.util.FileUtils.writeString( outputFile, out );


        Process p;
        if ( useWine ) {
            p = Runtime.getRuntime().exec( new String[]{
                    "wine",
                    BuildLocalProperties.getInstance().getFlash(),
                    BuildLocalProperties.getInstance().getWineTrunk() + "\\" + outputSuffix.replace( '/', '\\' )
            } );
        }
        else {
            if ( PhetUtilities.isMacintosh() ) {
                // see #1446
                String flashName = BuildLocalProperties.getInstance().getMacFlashName();
                String volume = BuildLocalProperties.getInstance().getMacTrunkVolume();
                String macPath = unixToMacPath( outputFile.getAbsolutePath() );
                Object[] args = {flashName, volume, macPath};
                String pattern = "tell application \"{0}\" to open alias \"{1}:{2}\"";
                String actionScript = MessageFormat.format( pattern, args );
                p = Runtime.getRuntime().exec( new String[]{"osascript", "-e", actionScript} );
            }
            else {
                String cmd = BuildLocalProperties.getInstance().getFlash();
                p = Runtime.getRuntime().exec( new String[]{cmd, outputFile.getAbsolutePath()} );
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
