package edu.colorado.phet.unfuddle;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.filters.StringInputStream;
import org.xml.sax.SAXException;

/**
 * Created by: Sam
 * Feb 21, 2008 at 3:01:06 PM
 */
public class ReadEmailList {
    private UnfuddleAccount account;
    private UnfuddleCurl curl;

    public ReadEmailList( UnfuddleAccount account, UnfuddleCurl curl ) {
        this.account = account;
        this.curl = curl;
    }

    public String[] getEmailsForComponent( String component ) throws IOException, SAXException, ParserConfigurationException {
        String s = curl.readString( "notebooks/7161/pages/23056/latest" );
//        System.out.println( "s = " + s );
        XMLObject xml = new XMLObject( s );
        String body = xml.getTextContent( "body" );
//        System.out.println( "body = " + body );

        Properties p = new Properties();
        p.load( new StringInputStream( body ) );
        ArrayList<String> emails = new ArrayList<String>();
        final Set set = p.keySet();
        for ( Object aSet : set ) {
            String key = (String) aSet;
            String value = p.getProperty( key );
            StringTokenizer st = new StringTokenizer( value, ", " );
            while ( st.hasMoreTokens() ) {
                String t = st.nextToken();
                final String email = getEmail( key );
                if ( t.equalsIgnoreCase( component ) && !emails.contains( email ) && email != null ) {
                    emails.add( email );
                }
            }
        }
        return emails.toArray( new String[emails.size()] );
    }

    private String getEmail( String username ) {
        return account.getEmailAddress( username );
    }

    public static void main( String[] args ) throws IOException, SAXException, ParserConfigurationException {
        if ( args.length != 3 ) {
            System.out.println( "usage: ReadEmailList unfuddleUsername unfuddlePassword svnTrunk" );
            System.exit( 1 );
        }
        UnfuddleCurl curl = new UnfuddleCurl( args[0], args[1], UnfuddleNotifierConstants.PHET_ACCOUNT_ID, args[2] );
        UnfuddleAccount unfuddleAccount = new UnfuddleAccount( new File( args[2] + "\\util\\unfuddle\\data\\phet.unfuddled.xml" ) );//TODO separator is Windows specific
        ReadEmailList readEmailList = new ReadEmailList( unfuddleAccount, curl );
        String[] s = readEmailList.getEmailsForComponent( "charts" );
        System.out.println( "Arrays.asList( = " + Arrays.asList( s ) );
    }

    //code to read "email=component..." format
//        Properties p = new Properties();
//        p.load( new FileInputStream( new File( "C:\\reid\\phet\\svn\\trunk\\team\\reids\\unfuddle\\data\\email.properties" ) ) );//TODO separator is Windows specific
//        String c = p.getProperty( component );
//        c = c == null ? "" : c;
//        StringTokenizer st = new StringTokenizer( c, ", " );
//        ArrayList s = new ArrayList();
//        while ( st.hasMoreTokens() ) {
//            s.add( st.nextToken() );
//        }
//        return (String[]) s.toArray( new String[0] );
}
