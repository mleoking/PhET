package edu.colorado.phet.unfuddle.test;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.colorado.phet.unfuddle.UnfuddleCurl;
import edu.colorado.phet.unfuddle.UnfuddleNotifierConstants;
import edu.colorado.phet.unfuddle.XMLObject;
import edu.colorado.phet.unfuddle.process.BasicProcess;

/**
 * Created by: Sam
 * Feb 22, 2008 at 7:25:45 AM
 */
public class CountRecentEvents {
    public static void main( String[] args ) throws IOException, SAXException, ParserConfigurationException, InterruptedException {
        if ( args.length != 3 ) {
            System.out.println( "usage: CountRecentEvents unfuddleUsername unfuddlePassword svnTrunk" );
        }
        UnfuddleCurl unfuddleCurl = new UnfuddleCurl( new BasicProcess(), args[0], args[1], UnfuddleNotifierConstants.PHET_ACCOUNT_ID, args[2] );
        System.out.println( "limit\tevents" );
        for ( int limit = 1; limit < 50; limit++ ) {
            XMLObject events = new XMLObject( unfuddleCurl.execProjectCommand( "activity.xml?limit=" + limit ) );
            int e = events.getNodeCount( "event" );
            System.out.println( limit + "\t" + e );
        }
    }
}
