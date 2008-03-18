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
        ArrayList emails = new ArrayList();
        final Set set = p.keySet();
        for ( Iterator iterator = set.iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
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
        return (String[]) emails.toArray( new String[0] );
    }

    private String getEmail( String username ) {
        return account.getEmailAddress( username );
    }

    public static void main( String[] args ) throws IOException, SAXException, ParserConfigurationException {
        UnfuddleCurl curl = new UnfuddleCurl( args[0], args[1], UnfuddleCurl.PHET_PROJECT_ID );
        UnfuddleAccount unfuddleAccount = new UnfuddleAccount( new File( ProcessRecentChanges.SVN_TRUNK+"\\util\\unfuddle\\data\\phet.unfuddled.xml" ) );
        ReadEmailList readEmailList = new ReadEmailList( unfuddleAccount, curl );
        String[] s = readEmailList.getEmailsForComponent( "charts" );
        System.out.println( "Arrays.asList( = " + Arrays.asList( s ) );
    }

    //code to read "email=component..." format
//        Properties p = new Properties();
//        p.load( new FileInputStream( new File( "C:\\reid\\phet\\svn\\trunk\\team\\reids\\unfuddle\\data\\email.properties" ) ) );
//        String c = p.getProperty( component );
//        c = c == null ? "" : c;
//        StringTokenizer st = new StringTokenizer( c, ", " );
//        ArrayList s = new ArrayList();
//        while ( st.hasMoreTokens() ) {
//            s.add( st.nextToken() );
//        }
//        return (String[]) s.toArray( new String[0] );
}
