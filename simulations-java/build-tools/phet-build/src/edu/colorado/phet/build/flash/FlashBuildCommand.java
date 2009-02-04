package edu.colorado.phet.build.flash;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.build.util.FileUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Feb 4, 2009
 * Time: 10:17:40 AM
 */
public class FlashBuildCommand {
    public static void build( String[] cmdArray, String[] sims, File trunk ) throws IOException {
        String trunkPipe = trunk.getAbsolutePath().replace( ':', '|' );
        trunkPipe = trunkPipe.replace( '\\', '/' );
        String template = FileUtils.loadFileAsString( new File( trunk, "simulations-flash\\build-template.jsfl" ) );
        String out = template;
        out = FileUtils.replaceAll( out, "@TRUNK@", trunkPipe );
        out = FileUtils.replaceAll( out, "@SIMS@", toSimsString( sims ) );
        System.out.println( "out = " + out );
        File outputFile = new File( trunk, "simulations-flash\\build-output-temp\\build.jsfl" );
        FileUtils.writeString( outputFile, out );

        List a = Arrays.asList( cmdArray );
        ArrayList b = new ArrayList( a );

        b.add( outputFile.getAbsolutePath() );
        Runtime.getRuntime().exec( (String[]) b.toArray( new String[0] ) );
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
        File flashEXE = new File( "C:\\Program Files\\Macromedia\\Flash 8\\Flash.exe" );
        FlashBuildCommand.build( new String[]{flashEXE.getAbsolutePath()},
                                 new String[]{"pendulum-lab", "test-flash-project"},
                                 new File( "C:\\reid\\phet\\svn\\trunk" ) );
    }
}
