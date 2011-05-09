package edu.colorado.phet.unfuddle;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.tools.ant.filters.StringInputStream;
import org.xml.sax.SAXException;

import edu.colorado.phet.unfuddle.process.BasicProcess;

/**
 * Created by: Sam
 * Feb 21, 2008 at 3:01:06 PM
 */
public class EmailList {
    private IUnfuddleAccount account;
    private UnfuddleCurl curl;

    public EmailList( IUnfuddleAccount account, UnfuddleCurl curl ) {
        this.account = account;
        this.curl = curl;
    }

    public String[] getEmailsForComponent( String component ) throws IOException, SAXException, ParserConfigurationException, InterruptedException {
        String s = curl.execProjectCommand( "notebooks/7161/pages/23056/latest" );
        XMLObject xml = new XMLObject( s );
        String body = xml.getTextContent( "body" );

        Properties p = new Properties();
        p.load( new StringInputStream( body ) );
        ArrayList<String> emails = new ArrayList<String>();
        Set set = p.keySet();
        for ( Object element : set ) {
            String key = (String) element;
            String value = p.getProperty( key );
            StringTokenizer st = new StringTokenizer( value, ", " );
            while ( st.hasMoreTokens() ) {
                String listedComponent = st.nextToken();
                String email = getEmail( key );
                if ( ( listedComponent.equals( "*" ) || listedComponent.equalsIgnoreCase( component ) ) &&
                     ( !emails.contains( email ) && email != null ) ) {
                    emails.add( email );
                }
            }
        }
        return emails.toArray( new String[emails.size()] );
    }

    private String getEmail( String username ) {
        return account.getEmailAddress( username );
    }

    public static void main( String[] args ) throws IOException, SAXException, ParserConfigurationException, InterruptedException {
        if ( args.length != 3 ) {
            System.out.println( "usage: ReadEmailList unfuddleUsername unfuddlePassword svnTrunk" );
            System.exit( 1 );
        }
        UnfuddleCurl curl = new UnfuddleCurl( new BasicProcess(), args[0], args[1], UnfuddleNotifierConstants.PHET_ACCOUNT_ID, args[2] );
        UnfuddleAccountDump unfuddleAccount = new UnfuddleAccountDump( new File( args[2] + "\\util\\unfuddle\\data\\phet.unfuddled.xml" ) );//TODO separator is Windows specific
        EmailList readEmailList = new EmailList( unfuddleAccount, curl );
        String[] s = readEmailList.getEmailsForComponent( "charts" );
        System.out.println( "Arrays.asList( = " + Arrays.asList( s ) );
    }
}
