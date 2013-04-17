// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.unfuddle;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.colorado.phet.unfuddle.process.BasicProcess;
import edu.colorado.phet.unfuddle.process.MyProcess;

/**
 * This IUnfuddleAccount provides information lookup based on using Curl dynamically, it is provided as an alternative
 * to the previous version which relied on a dump.xml (which became cumbersome to obtain).
 * Values are cached in system memory once read, and will be cleared when this application is restarted.
 *
 * @author Sam Reid
 */
public class UnfuddleAccountCurl implements IUnfuddleAccount {

    private final static Logger LOGGER = UnfuddleLogger.getLogger( UnfuddleAccountCurl.class );

    UnfuddleCurl curl;
    HashMap<Integer, IUnfuddlePerson> people = new HashMap<Integer, IUnfuddlePerson>();
    HashMap<Integer, String> components = new HashMap<Integer, String>();

    public UnfuddleAccountCurl( UnfuddleCurl curl ) {
        this.curl = curl;
    }

    public IUnfuddlePerson getPersonForID( int id ) {
        if ( !people.containsKey( id ) ) {
            try {
                String person = curl.execV1Command( "people/{" + id + "}" );
                XMLObject object = new XMLObject( person );
                System.out.println( "object = " + object );
                final UnfuddlePerson unfuddlePerson = new UnfuddlePerson( object.getNode() );
                people.put( id, unfuddlePerson );
            }
            catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }
        else {
            System.out.println( "Cache hit on person id: " + id );
        }
        return people.get( id );
    }

    public String getComponentForID( int id ) {
        if ( !components.containsKey( id ) ) {
            try {
                String component = curl.execProjectCommand( "components/{" + id + "}" );
                if ( component == null ) {
                    LOGGER.warning( "Null component for ID = " + id );
                    throw new RuntimeException( "Null component for ID: " + id );
                }
                XMLObject object = new XMLObject( component );
                final String name = new UnfuddleComponent( object.getNode() ).getName();
                components.put( id, name );
            }
            catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }
        else {
            System.out.println( "Cache hit on component id: " + id );
        }
        return components.get( id );
    }

    public String getEmailAddress( String username ) {
        String hashtableLookup = lookupEmail( username );
        if ( hashtableLookup != null ) {
            return hashtableLookup;
        }
        System.out.println( "No cached username: " + username + ", loading all people..." );
        try {
            readAllPeople();
        }
        catch ( Exception e ) {
            throw new RuntimeException( e );
        }

        String lookup2 = lookupEmail( username );
        if ( lookup2 != null ) { return lookup2; }
        throw new RuntimeException( "Couldn't find username in map after populating map.., username = " + username );
    }

    private void readAllPeople() throws IOException, InterruptedException, SAXException, ParserConfigurationException {
        String people = curl.execV1Command( "people" );
        XMLObject xmlObject = new XMLObject( people );
        int count = xmlObject.getNodeCount( "person" );
        for ( int i = 0; i < count; i++ ) {
            XMLObject person = xmlObject.getNode( i, "person" );
            UnfuddlePerson out = new UnfuddlePerson( person.getNode() );
            System.out.println( "Loaded person from people list: name = " + out.getName() + ", email=" + out.getEmail() + ", id=" + out.getID() + ", username=" + out.getUsername() );
            this.people.put( out.getID(), out );
        }
    }

    private String lookupEmail( String username ) {
        for ( IUnfuddlePerson iUnfuddlePerson : people.values() ) {
            if ( iUnfuddlePerson.getUsername().equals( username ) ) {
                return iUnfuddlePerson.getEmail();
            }
        }
        return null;
    }

    public static void main( String[] args ) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        String username = args[0];
        String password = args[1];
        String svnTrunk = args[2];
        MyProcess myProcess = new BasicProcess();
        final UnfuddleCurl curl = new UnfuddleCurl( myProcess, username, password, UnfuddleNotifierConstants.PHET_ACCOUNT_ID, svnTrunk );
        UnfuddleAccountCurl lazyUnfuddleAccount = new UnfuddleAccountCurl( curl );

//        IUnfuddlePerson out = lazyUnfuddleAccount.getPersonForID( 43148 );
//        System.out.println( "out = " + out );
//        System.out.println( "name = " + out.getName() + ", email=" + out.getEmail() + ", id=" + out.getID() + ", username=" + out.getUsername() );
//
//        String component = lazyUnfuddleAccount.getComponentForID( 47074 );
//        System.out.println( "component = " + component );
//
//        String email = lazyUnfuddleAccount.getEmailAddress( "oliver" );
//        System.out.println( "email = " + email );

        String e2 = lazyUnfuddleAccount.getEmailAddress( "samreid" );
        System.out.println( "e2 = " + e2 );

        String e3 = lazyUnfuddleAccount.getEmailAddress( "emoore" );
        System.out.println( "e3 = " + e3 );
    }
}
