package edu.colorado.phet.common.phetcommon.tests;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.updates.dialogs.StreamGobbler;

public class TestRuntimeExecMac {

    public static void main( String[] args ) throws IOException {
        //This one fails on Mac due to double quotes in path names
//        Process p = Runtime.getRuntime().exec( "\"/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/bin/java\" -jar /tmp/updater17775.jar balloons balloons en \"/Users/cmalley/Downloads/balloons.jar\"" );
        // This one works fine on Mac.
        Process p = Runtime.getRuntime().exec( new String[]{"/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home/bin/java", "-jar", "/tmp/updater17775.jar", "balloons", "balloons", "en", "/Users/cmalley/Downloads/balloons.jar"} );
        new StreamGobbler( p.getErrorStream(), "ERR" ).start();
        new StreamGobbler( p.getInputStream(), "OUT" ).start();
        try {
            p.waitFor();
        }
        catch( InterruptedException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
