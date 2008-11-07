package edu.colorado.phet.updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Updater {

    /*
     * Downloads and launches the jar for the specified simulation.
     */
    private void update( String project, String sim, String locale, File targetLocation ) {
        //todo: updater may need to wait explicitly, since the original JAR presumably must be exited before it can be overwritten

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

    /*
     * Launches the simulation (as specified by a JAR file) in the specified locale.
     */
    private void launchSimulation( String locale, File targetLocation ) {
        //todo: add support for locale when we have added support for non-english offline JARs
        String javaPath = System.getProperty( "java.home" ) + System.getProperty( "file.separator" ) + "bin" + System.getProperty( "file.separator" ) + "java";
        try {
            String[] cmd = new String[]{javaPath, "-jar", targetLocation.getAbsolutePath()};
            println( "exec'ing command=" + Arrays.asList( cmd ) );
            Process p = Runtime.getRuntime().exec( cmd );
            //todo: display output from this process in case any errors occur
        }
        catch( IOException e ) {
            e.printStackTrace();
            println( e.toString() );
        }
    }

    /*
     * Downloads the JAR for the specified simulation to the specified location.
     */
    private void download( String project, String flavor, File targetLocation ) throws FileNotFoundException {
        Util.download( "http://phet.colorado.edu/sims/" + project + "/" + flavor + ".jar", targetLocation );
    }

    private static void println( String ms ) {
        DebugLogger.println( Updater.class.getName() + "> " + ms );
    }

    /*
    This main is invoked by the Updater in UpdateButton or equivalent.
    Arguments must be as in this example:
    java -jar updater.jar nuclear-physics alpha-decay en C:/temp/alpha-decay.jar
    */
    public static void main( String[] args ) {
        String project = args[0];
        String sim = args[1];
        String locale = args[2];
        File targetLocation = new File( args[3] );

        println( "started updater, project=" + project + ", sim=" + sim + ", locale=" + locale + ", targetlocation=" + targetLocation );
        new Updater().update( project, sim, locale, targetLocation );
    }
}
