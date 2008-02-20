package edu.colorado.phet.unfuddle;

import java.io.IOException;

/**
 * Created by: Sam
 * Feb 19, 2008 at 8:37:29 AM
 */
public class UnfuddleDump {
    public static void main( String[] args ) throws IOException {
        UnfuddleCurl curl = new UnfuddleCurl( args[0], args[1], 9404 );
//        String dump = curl.readString( "tickets" );
        String dump = curl.readString( "dump" );
        System.out.println( "dump.length = " + dump.length() );
    }
}
