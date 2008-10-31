package edu.colorado.phet.updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Updater {

    private void update( String project, String sim, String locale, File targetLocation ) {
        // Download the new, updated version of the sim.
        try {
            download( project, sim, targetLocation );
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
            println( e.toString() );
        }
        println( "finished download" );

        // Execute the newly downloaded sim.
        launchSimulation( locale, targetLocation );
        println( "finished launch" );
    }

    private void launchSimulation( String locale, File targetLocation ) {
        String javaPath = System.getProperty( "java.home" ) + System.getProperty( "file.separator" ) + "bin" + System.getProperty( "file.separator" ) + "java";
        try {
            String[] cmd = new String[]{javaPath, "-jar", targetLocation.getAbsolutePath()};
            println( "exec'ing command=" + Arrays.asList( cmd ) );
            Process p = Runtime.getRuntime().exec( cmd );
        }
        catch( IOException e ) {
            e.printStackTrace();
            println( e.toString() );
        }

    }

    private void download( String project, String flavor, File targetLocation ) throws FileNotFoundException {
        Util.download( "http://phet.colorado.edu/sims/" + project + "/" + flavor + ".jar", targetLocation );
    }

    public static void main( String[] args ) {
        String project = args[0];
        String sim = args[1];
        String locale = args[2];
        File targetLocation = new File( args[3] );

        println( "started updater, project=" + project + ", sim=" + sim + ", locale=" + locale + ", targetlocation=" + targetLocation );
        new Updater().update( project, sim, locale, targetLocation );
    }

    private static void println( String ms ) {
        DebugLogger.println( Updater.class.getName() + "> " + ms );
    }
}
