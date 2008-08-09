package edu.colorado.phet.unfuddle;

import java.io.IOException;

import edu.colorado.phet.unfuddle.process.BasicProcess;

/**
 * This doesn't work.
 * <p/>
 * Anytime we have a new person join, a change of email address, a new component added,
 * and possibly other things, you must manually get a new dump from the Unfuddle website.
 * Go to the PhET project's Setting page, and select "Download an XML representation
 * of this project only" from the right column.
 * <p/>
 * Created by: Sam
 * Feb 19, 2008 at 8:37:29 AM
 */
public class UnfuddleDump {
    public static void main( String[] args ) throws IOException, InterruptedException {
        if ( args.length != 3 ) {
            System.out.println( "usage: UnfuddleDump unfuddleUsername unfuddlePassword svnTrunk" );
        }
        UnfuddleCurl curl = new UnfuddleCurl( new BasicProcess(), args[0], args[1], UnfuddleNotifierConstants.PHET_ACCOUNT_ID, args[2] );
//        String dump = curl.readString( "tickets" );
        String dump = curl.readString( "dump" );
        System.out.println( "dump.length = " + dump.length() );
    }
}
