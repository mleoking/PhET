package edu.colorado.phet.wickettest.authentication;

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
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

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
            String compatibleHash = compatibleHashPassword( password );

            Query query = session.createQuery( "select u from PhetUser as u where (u.email = :email and (u.password = :password or u.password = :compatiblePassword))" );
            query.setString( "email", username );
            query.setString( "password", hash );
            query.setString( "compatiblePassword", compatibleHash );

            //System.out.println( "Attempting to authenticate " + username + " with " + hash );

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

    public static String hashPassword( final String password ) {
        // TODO: possibly move hashPassword elsewhere?
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

    public static String compatibleHashPassword( final String password ) {
        // TODO: possibly move hashPassword elsewhere?
        byte[] bytes;
        try {
            MessageDigest digest = MessageDigest.getInstance( "MD5" );
            digest.reset();
            digest.update( ( password + "_phetx1225" ).getBytes( "UTF-8" ) );
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

        //return base64Encode( new String( bytes ) );
        return hexEncode( bytes );
    }

    private static String hexEncode( byte[] bytes ) {
        StringBuffer buffer = new StringBuffer();
        for ( byte b : bytes ) {
            buffer.append( byteToHex( b ) );
        }
        return buffer.toString();
    }

    private static String byteToHex( byte b ) {
        char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] array = {hexDigit[( b >> 4 ) & 0x0f], hexDigit[b & 0x0f]};
        return new String( array );
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

    public void signOut() {
        signedIn = false;
        invalidateNow();
    }

    private static class Test {
        public static void main( String[] args ) {
            System.out.println( hashPassword( args[0] ) );
            System.out.println( compatibleHashPassword( args[0] ) );
        }
    }
}
