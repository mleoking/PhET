package edu.colorado.phet.updater;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Copies and launches specified JAR, see SimUpdater.
 */
public class UpdaterBootstrap {
    private File src;
    private File dst;

    public UpdaterBootstrap( File src, File dst ) {
        this.src = src;
        this.dst = dst;
    }

    private void copyAndLaunch() {
        //todo: updater may need to wait explicitly, since the original JAR presumably must be exited before it can be overwritten
        // Download the new, updated version of the sim.
        copy();
        launchSimulation();
        println( "finished launch" );
    }

    private void copy() {
        try {
            FileUtils.copyTo( src, dst );
        }
        catch( IOException e ) {
            e.printStackTrace();
            println( e.getMessage() );
        }
    }

    /*
     * Launches the simulation (as specified by a JAR file) in the specified locale.
     */
    private void launchSimulation() {
        //todo: add support for locale when we have added support for non-english offline JARs
        String javaPath = System.getProperty( "java.home" ) + System.getProperty( "file.separator" ) + "bin" + System.getProperty( "file.separator" ) + "java";
        try {
            String[] cmd = new String[]{javaPath, "-jar", dst.getAbsolutePath()};
            println( "exec'ing command=" + Arrays.asList( cmd ) );
            Process p = Runtime.getRuntime().exec( cmd );
            //todo: display output from this process in case any errors occur
        }
        catch( IOException e ) {
            e.printStackTrace();
            println( e.toString() );
        }
    }

    private static void println( String ms ) {
        DebugLogger.println( UpdaterBootstrap.class.getName() + "> " + ms );
    }

    /*
    This main is invoked by the Updater in UpdateButton or equivalent.
    Arguments must be as in this example:
    java -jar updater.jar C:/temp/alpha-decay0123.jar C:/user/phet/alpha-decay.jar
    */
    public static void main( String[] args ) {
        String src = args[0];
        String dst = args[1];

        println( "started updater, src=" + src + ", target=" + dst );
        new UpdaterBootstrap( new File( src ), new File( dst ) ).copyAndLaunch();
    }


}
