package edu.colorado.phet.updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Updater {
    private ArrayList listeners = new ArrayList();
    private String project;
    private String sim;
    private String locale;
    private File targetLocation;

    public Updater( String project, String sim, String locale, File targetLocation ) {
        this.project = project;
        this.sim = sim;
        this.locale = locale;
        this.targetLocation = targetLocation;
    }

    /*
    * Downloads and launches the jar for the specified simulation.
    */
    private void updateAndLaunch() {
        //todo: updater may need to wait explicitly, since the original JAR presumably must be exited before it can be overwritten
        notifyUpdaterStarted();
        // Download the new, updated version of the sim.
        try {
            notifyDownloadStarted();
            download( project, sim, locale, targetLocation );
            println( "finished download" );
            notifyDownloadFinished();
        }
        catch( FileNotFoundException e ) {
            notifyErrorOccurred( e );
            e.printStackTrace();
            println( e.toString() );
        }

        // Execute the newly downloaded sim.
        notifyLaunchingNewSim();
        launchSimulation( locale, targetLocation );
        println( "finished launch" );
    }

    private void notifyLaunchingNewSim() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).newSimLaunching();
        }
    }

    private void notifyErrorOccurred( FileNotFoundException e ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).errorInDownload( e );
        }
    }

    private void notifyDownloadFinished() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).downloadFinished();
        }
    }

    private void notifyDownloadStarted() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).downloadStarted();
        }
    }

    private void notifyUpdaterStarted() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).updaterStarted();
        }
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
    private void download( String project, String flavor, String locale, File targetLocation ) throws FileNotFoundException {
        String localeSuffix = locale.equals( "en" ) ? "" : "_" + locale;
        Util.download( "http://phet.colorado.edu/sims/" + project + "/" + flavor + localeSuffix + ".jar", targetLocation );
    }

    private static void println( String ms ) {
        DebugLogger.println( Updater.class.getName() + "> " + ms );
    }

    public static interface Listener {
        void updaterStarted();

        void downloadStarted();

        void downloadFinished();

        void errorInDownload( FileNotFoundException e );

        void newSimLaunching();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
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
        Updater updater = new Updater( project, sim, locale, targetLocation );
//        updater.addListener( new UpdaterDialog( updater ) );
        updater.updateAndLaunch();
    }
}
