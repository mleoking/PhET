package edu.colorado.phet.updater;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Updater {

    private void update( String project, String sim, String locale, File targetLocation ) throws FileNotFoundException {

        // Download the new, updated version of the sim.
        download( project, sim, targetLocation );

        // Execute the newly downloaded sim.
        launchSimulation( sim, locale, targetLocation );

    }

    private void launchSimulation( String sim, String locale, File targetLocation ) {
        String sep = System.getProperty( "file.separator" );
        String javaPath = System.getProperty( "java.home" ) + sep + "bin" + sep + "java";
        try {
            Process p = Runtime.getRuntime().exec( javaPath + " -jar " + targetLocation.getAbsolutePath() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

    }

    private void download( String project, String flavor, File targetLocation ) throws FileNotFoundException {
        Util.download( "http://phet.colorado.edu/sims/" + project + "/" + flavor + ".jar", targetLocation );
    }

    public static void main( String[] args ) throws FileNotFoundException {
        String project = args[0];
        String sim = args[1];
        String locale = args[2];
        File targetLocation = new File( args[3] );

        new Updater().update( project, sim, locale, targetLocation );
    }
}
