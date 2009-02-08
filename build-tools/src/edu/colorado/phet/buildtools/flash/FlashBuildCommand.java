package edu.colorado.phet.buildtools.flash;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.util.FileUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Feb 4, 2009
 * Time: 10:17:40 AM
 */
public class FlashBuildCommand {
    public static void build( String cmdArray, String sim, File trunk ) throws IOException {
        build( cmdArray, new String[]{sim}, trunk );
    }

    public static void build( String cmdArray, String[] sims, File trunk ) throws IOException {
        String trunkPipe = trunk.getAbsolutePath().replace( ':', '|' );
        trunkPipe = trunkPipe.replace( '\\', '/' );
        String template = FileUtils.loadFileAsString( new File( trunk, "simulations-flash" + File.separator + "build-template.jsfl" ) );
        String out = template;

        // Start (temporary) hack
        if(System.getProperty("os.name").equals("Linux")) { trunkPipe = "C|/svn/trunk"; }
        // End

        out = FileUtils.replaceAll( out, "@TRUNK@", trunkPipe );
        out = FileUtils.replaceAll( out, "@SIMS@", toSimsString( sims ) );
        System.out.println( "out = " + out );

        String outputSuffix = "simulations-flash" + File.separator + "build-output-temp" + File.separator + "build.jsfl";
        File outputFile = new File( trunk, outputSuffix );
        FileUtils.writeString( outputFile, out );


        Process p;
        // Start (temporary) hack
        if(System.getProperty("os.name").equals("Linux")) {
            p = Runtime.getRuntime().exec(new String[]{
                    "wine",
                    "C:\\Program Files\\Macromedia\\Flash 8\\Flash.exe", // possibly replace this with cmdArray
                    "C:\\svn\\trunk\\" + outputSuffix.replace('/', '\\')
            });
        } else {
            p=Runtime.getRuntime().exec( cmdArray + " " + outputFile.getAbsolutePath() );
        }
        // End

        try {
            p.waitFor();
        }
        catch( InterruptedException e ) {
            e.printStackTrace();  
        }
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
                                 new File( "C:\\reid\\phet\\svn\\trunk" ) );
    }
}
