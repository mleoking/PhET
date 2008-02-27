package edu.colorado.phet.unfuddle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import edu.colorado.phet.build.FileUtils;

/**
 * Created by: Sam
 * Feb 17, 2008 at 9:02:08 PM
 */
public class UnfuddleCurl {
    private String username;
    private String password;
    private int accountID;
    public static int PHET_PROJECT_ID = 9404;

    public UnfuddleCurl( String username, String password, int accountID ) {
        this.username = username;
        this.password = password;
        this.accountID = accountID;
    }

    static interface INotifyHandler {
        public boolean didNotifyComment( XMLObject comment );

        public void setNotifiedComment( XMLObject comment );
    }

    public static class DefaultNotifyHandler implements INotifyHandler {
        public boolean didNotifyComment( XMLObject comment ) {
            return true;
        }

        public void setNotifiedComment( XMLObject comment ) {
        }
    }

    private static Element parseXML( File ticketFile ) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document s = documentBuilder.parse( ticketFile );
        return s.getDocumentElement();
    }

    //fails for dump (timeout)
    public String readString( String readARG ) throws IOException {
        String CURL = ProcessRecentChanges.SVN_TRUNK+"\\team\\unfuddle\\curl_717_1_ssl\\curl.exe";
        String cmdArg = accountID + "/" + readARG;
        String cmd = CURL + " -k -i -u " + username + ":" + password + " -X GET -H \"Accept: application/xml\" https://phet.unfuddle.com/api/v1/projects/" + cmdArg;
        System.out.println( "cmd = " + cmd );
        return execCommand(cmd);
    }

    protected String execCommand( String cmd ) throws IOException {

        Process p = Runtime.getRuntime().exec( cmd );
        // Get the input stream and read from it
        StringBuffer s = new StringBuffer();
        InputStream in = p.getInputStream();
        int c;
        int count = 0;
        while ( ( c = in.read() ) != -1 ) {
            s.append( (char) c );
            count++;
            if ( count % 1000 == 0 ) {
                System.out.print( "." );
            }
        }
        System.out.println( "" );
        in.close();

        return s.substring( s.indexOf( "<?xml" ) );
    }

    public static void main( String[] args ) throws IOException, ParserConfigurationException, SAXException {
        File ticketFile = new File( "C:/users/sam/desktop/ticket-out-1.xml" );
        File ticketCommentFile = new File( "C:/users/sam/desktop/ticket-comment-out-1.xml" );

        boolean readFromWeb = true || !ticketFile.exists() || !ticketCommentFile.exists();

        if ( readFromWeb ) {
            String username = args[0];
            String password = args[1];

            String tickets = new UnfuddleCurl( username, password, PHET_PROJECT_ID ).readString( "tickets" );
            String ticketComments = new UnfuddleCurl( username, password, PHET_PROJECT_ID ).readString( "tickets/comments" );
            FileUtils.writeString( ticketFile, tickets );
            FileUtils.writeString( ticketCommentFile, ticketComments );
        }

        XMLObject ticketObject = new XMLObject( parseXML( ticketFile ) );
        int numTickets = ticketObject.getNodeCount( "ticket" );
        System.out.println( "numTickets = " + numTickets );

        XMLObject commentObject = new XMLObject( parseXML( ticketCommentFile ) );
        System.out.println( "commentObject = " + commentObject );
    }

}
