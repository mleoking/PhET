package edu.colorado.phet.updater;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Copies and launches specified JAR, see SimUpdater.
 */
public class UpdaterBootstrap {
    
    // do not enabled debug output for public releases, the log file will grow indefinitely!
    private static final boolean DEBUG_OUTPUT_ENABLED = true;
    
    private final File src;
    private final File dst;

    public UpdaterBootstrap( File src, File dst ) {
        this.src = src;
        this.dst = dst;
        File thisJar = FileUtils.getCodeSource();
        // When running in IDEs (Eclipse, IDEA,...) we aren't running a JAR.
        if ( FileUtils.hasSuffix( thisJar, "jar" ) ) {
            // this doesn't work (but causes no problems) on Windows, can't delete an open file.
            thisJar.deleteOnExit();
        }
    }

    private void replaceAndLaunch() throws IOException {
        //TODO: updater may need to wait explicitly, since the original JAR presumably must be exited before it can be overwritten
        //Todo: don't use exceptions for control logic
        for (int i=0;i<100;i++){//10 seconds of retries
            try{
                replace();
                break;
            }catch (IOException e){
                println( "Exception on replace() iteration: "+i+": "+e );
                try {
                    Thread.sleep(100);
                }
                catch( InterruptedException e1 ) {
                    e1.printStackTrace();
                }
            }
        }
        launch();
        println( "finished launch" );
    }

    private void replace() throws IOException {
        FileUtils.copyTo( src, dst );
        src.deleteOnExit();
    }

    /*
     * Launches the simulation (as specified by a JAR file) with the specified language code.
     */
    private void launch() throws IOException {
        //TODO: add support for language code when we have added support for non-English offline JARs
        String[] cmdArray = new String[] { getJavaPath(), "-jar", dst.getAbsolutePath() };
        println( "restarting sim with cmdArray=" + Arrays.asList( cmdArray ) );
        Process p = Runtime.getRuntime().exec( cmdArray );
        //TODO: display output from this process in case any errors occur
    }
    
    public static String getJavaPath() {
        return System.getProperty( "java.home" ) + System.getProperty( "file.separator" ) + "bin" + System.getProperty( "file.separator" ) + "java";
    }

    private static void println( String ms ) {
        if ( DEBUG_OUTPUT_ENABLED ) {
            DebugLogger.println( UpdaterBootstrap.class.getName() + "> " + ms );
        }
    }

    /*
     * This main is invoked by the Updater in UpdateButton or equivalent.
     * Arguments must be as in this example:
     * java -jar updater.jar C:/temp/alpha-decay0123.jar C:/user/phet/alpha-decay.jar
     */
    public static void main( String[] args ) {

        String src = args[0];
        String dst = args[1];
        println( "starting updater, src=" + src + ", target=" + dst );
        try {
            new UpdaterBootstrap( new File( src ), new File( dst ) ).replaceAndLaunch();
        }
        catch ( IOException e ) {
            e.printStackTrace();
            println( e.getMessage() );
            //TODO: open an error dialog here?
            System.exit( 1 ); // indicate abnormal exit
        }
    }
}
