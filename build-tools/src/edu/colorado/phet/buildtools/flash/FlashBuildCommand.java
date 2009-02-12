package edu.colorado.phet.buildtools.flash;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.util.FileUtils;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Feb 4, 2009
 * Time: 10:17:40 AM
 */
public class FlashBuildCommand {
    // returns boolean success
    public static boolean build( String cmd, String sim, File trunk, boolean useWine ) throws IOException {
        build( cmd, new String[]{sim}, trunk, useWine );

        JOptionPane.showMessageDialog( null, "Building the Flash SWF, press OK when finished." );

        File outputFile = new File( trunk, "simulations-flash/build-output-temp/output-" +  sim + ".txt" );

        if(outputFile.exists()) {
            System.out.println( "Found output file." );

            String outputString = FileUtils.loadFileAsString( outputFile );

            if( outputString.indexOf( "Error") == -1 ) {
                System.out.println( "Successful build of SWF" );
            } else {
                System.out.println( "Failed to build the SWF" );
                return false;
            }
        } else {
            System.out.println( "Could not find output file." );
        }
        return true;
    }

    public static void build( String cmd, String[] sims, File trunk, boolean useWine ) throws IOException {
        String template = FileUtils.loadFileAsString( new File( trunk, "simulations-flash/build-tools/flash-build/data/build-template.jsfl") );
        String out = template;

        String trunkPipe;
        if(useWine) {
            trunkPipe = "C|/svn/trunk";
        } else {
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
        if(useWine) {
            p = Runtime.getRuntime().exec(new String[]{
                    "wine",
                    "C:\\Program Files\\Macromedia\\Flash 8\\Flash.exe", // possibly replace this with cmdArray
                    "C:\\svn\\trunk\\" + outputSuffix.replace('/', '\\')
            });
        } else {
            p = Runtime.getRuntime().exec( new String[]{cmd ,outputFile.getAbsolutePath()} );
        }

//        try {
//            p.waitFor();
//        }
//        catch( InterruptedException e ) {
//            e.printStackTrace();
//        }
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

    public static void main( String[] args ) throws IOException {
        FlashBuildCommand.build( "C:\\Program Files\\Macromedia\\Flash 8\\Flash.exe",
                                 new String[]{"pendulum-lab", "test-flash-project"},
                                 new File( "C:\\reid\\phet\\svn\\trunk" ), false );
    }
}
