package edu.colorado.phet.wickettest.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.data.PhetUser;

public class PhetSession extends WebSession {

    private boolean signedIn = false;
    private PhetUser user = null;

    public static PhetSession get() {
        return (PhetSession) Session.get();
    }

    public PhetSession( Request request ) {
        super( request );
    }

    private boolean authenticate( PhetRequestCycle currentCycle, final String username, final String password ) {
        user = null;
        org.hibernate.Session session = currentCycle.getHibernateSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String hash = hashPassword( password );

            Query query = session.createQuery( "select u from PhetUser as u where (u.email = :email and u.password = :password)" );
            query.setString( "email", username );
            query.setString( "password", hash );

            System.out.println( "Attempting to authenticate " + username + " with " + hash );

            user = (PhetUser) query.uniqueResult();

            tx.commit();
        }
        catch( RuntimeException e ) {
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
        }

        return user != null;
    }

    public PhetUser getUser() {
        return user;
    }

    public boolean signIn( PhetRequestCycle currentCycle, final String username, final String password ) {
        signedIn = authenticate( currentCycle, username, password );
        return isSignedIn();
    }

    public boolean isSignedIn() {
        return signedIn;
    }

    private static String hashPassword( final String password ) {
        byte[] bytes;
        try {
            MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
            digest.reset();
            digest.update( password.getBytes( "UTF-8" ) );
            bytes = digest.digest();
        }
        catch( NoSuchAlgorithmException e ) {
            e.printStackTrace();
            throw new RuntimeException( "No such algorithm", e );
        }
        catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }

        return base64Encode( new String( bytes ) );
    }

    private static String base64Data = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";


    private static String base64Encode( String string ) {
        String ret = "";
        int count = ( 3 - ( string.length() % 3 ) ) % 3;
        string += "\0\0".substring( 0, count );
        for ( int i = 0; i < string.length(); i += 3 ) {
            int j = ( string.charAt( i ) << 16 ) + ( string.charAt( i + 1 ) << 8 ) + string.charAt( i + 2 );
            ret = ret + base64Data.charAt( ( j >> 18 ) & 0x3f ) + base64Data.charAt( ( j >> 12 ) & 0x3f ) + base64Data.charAt( ( j >> 6 ) & 0x3f ) + base64Data.charAt( j & 0x3f );
        }
        return ret.substring( 0, ret.length() - count ) + "==".substring( 0, count );
    }

    private static class Test {
        public static void main( String[] args ) {
            System.out.println( hashPassword( args[0] ) );
        }
    }
}
