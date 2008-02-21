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
//        String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                   "<page>\n" +
//                   "  <author-id type=\"integer\">12197</author-id>\n" +
//                   "  <body>samreid=circuit-construction-kit, wave-interference, website\n" +
//                   "\n" +
//                   "exampleuser=installer, moving-man\n" +
//                   "</body>\n" +
//                   "  <id type=\"integer\">23056</id>\n" +
//                   "  <message></message>\n" +
//                   "  <notebook-id type=\"integer\">7161</notebook-id>\n" +
//                   "  <number type=\"integer\">1</number>\n" +
//                   "  <title>Email Notification Association List</title>\n" +
//                   "  <version type=\"integer\">3</version>\n" +
//                   "  <created-at>2008-02-21T13:44:20-08:00</created-at>\n" +
//                   "  <updated-at>2008-02-21T13:54:17-08:00</updated-at>\n" +
//                   "</page>";
        System.out.println( "s = " + s );
        XMLObject xml = new XMLObject( s );
        String body = xml.getTextContent( "body" );
        System.out.println( "body = " + body );

        String email = account.getEmailAddress( "samreid" );
        System.out.println( "email = " + email );

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
                if ( t.equalsIgnoreCase( component ) && !emails.contains( getEmail( key ) ) ) {
                    emails.add( getEmail( key ) );
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
        UnfuddleAccount unfuddleAccount = new UnfuddleAccount( new File( "C:\\reid\\phet\\svn\\trunk\\team\\reids\\unfuddle\\data\\phet.unfuddled.20080221215524.xml" ) );
        ReadEmailList readEmailList = new ReadEmailList( unfuddleAccount, curl );
        String[] s = readEmailList.getEmailsForComponent( "charts" );
        System.out.println( "Arrays.asList( = " + Arrays.asList( s ) );
    }
}
