
package edu.colorado.phet.licensing;

import java.io.File;

public class Config {

    public static File getTrunk( String[] args ) {
        if ( args.length == 0 ) {
            System.err.println( "The path to trunk must be specified as args[0]" );
            System.exit( 1 );
        }
        File trunk = new File( args[0] );
        if ( !trunk.exists() ) {
            System.err.println( trunk + " does not exist." );
            System.exit( 1 );
        }
        else if ( !trunk.isDirectory() ) {
            System.err.println( trunk + " is not a directory." );
            System.exit( 1 );
        }
        return trunk;
    }
}
