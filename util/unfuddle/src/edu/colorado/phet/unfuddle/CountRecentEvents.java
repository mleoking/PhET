package edu.colorado.phet.unfuddle;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * Created by: Sam
 * Feb 22, 2008 at 7:25:45 AM
 */
public class CountRecentEvents {
    public static void main( String[] args ) throws IOException, SAXException, ParserConfigurationException {
        if ( args.length != 2 ) {
            System.out.println( "usage: CountRecentEvents username password" );
        }
        UnfuddleCurl unfuddleCurl = new UnfuddleCurl( args[0], args[1], UnfuddleNotifierConstants.PHET_ACCOUNT_ID );
        System.out.println( "limit\tevents" );
        for ( int limit = 1; limit < 50; limit++ ) {
            XMLObject events = new XMLObject( unfuddleCurl.readString( "activity.xml?limit=" + limit ) );
            int e = events.getNodeCount( "event" );
            System.out.println( limit + "\t" + e );
        }
    }
}
