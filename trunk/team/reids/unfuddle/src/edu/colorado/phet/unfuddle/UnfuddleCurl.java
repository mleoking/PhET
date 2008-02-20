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

    public UnfuddleCurl( String username, String password, int accountID ) {
        this.username = username;
        this.password = password;
        this.accountID = accountID;
    }

    public static void main( String[] args ) throws IOException, ParserConfigurationException, SAXException {
        File ticketFile = new File( "C:/users/sam/desktop/ticket-out-1.xml" );
        File ticketCommentFile = new File( "C:/users/sam/desktop/ticket-comment-out-1.xml" );

        boolean readFromWeb = true || !ticketFile.exists() || !ticketCommentFile.exists();

        if ( readFromWeb ) {
            String username = args[0];
            String password = args[1];
            String tickets = new UnfuddleCurl( username, password, 9404 ).readString( "tickets" );
            String ticketComments = new UnfuddleCurl( username, password, 9404 ).readString( "tickets/comments" );
            FileUtils.writeString( ticketFile, tickets );
            FileUtils.writeString( ticketCommentFile, ticketComments );
        }

        XMLObject ticketObject = new XMLObject( parseXML( ticketFile ) );
        int numTickets = ticketObject.getNodeCount( "ticket" );
        System.out.println( "numTickets = " + numTickets );

        XMLObject commentObject = new XMLObject( parseXML( ticketCommentFile ) );

        //process tickets
//        processTickets( ticketObject );

        //process Comments
        processComments( new DefaultNotifyHandler(), commentObject );
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

    private static void processComments( INotifyHandler notifyHandler, XMLObject commentObject ) {
        int numComments = commentObject.getNodeCount( "comment" );
        System.out.println( "numComments = " + numComments );
        for ( int i = 0; i < numComments; i++ ) {
            XMLObject comment = commentObject.getNode( 0, "comment" );
            if ( !notifyHandler.didNotifyComment( comment ) ) {
                notifyComment( comment );
                notifyHandler.setNotifiedComment( comment );
            }
        }
    }

    private static void notifyComment( XMLObject comment ) {
        int id = Integer.parseInt( comment.getTextContent( "id" ) );
        int ticketID = Integer.parseInt( comment.getTextContent( "parent-id" ) );
        String body = comment.getTextContent( "body" );
//        TestEmail.sendEmail();
    }

    private static void processTickets( XMLObject ticketObject ) {
        int numTickets = ticketObject.getNodeCount( "ticket" );
        System.out.println( "numTickets = " + numTickets );
    }

    private static Element parseXML( File ticketFile ) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document s = documentBuilder.parse( ticketFile );
        return s.getDocumentElement();
    }

    public String readStringBAT( String readARG ) throws IOException {
        String CURL = "C:\\reid\\phet\\svn\\trunk\\team\\unfuddle\\curl_717_1_ssl\\curl.exe";
        String cmdArg = accountID + "/" + readARG;
        String cmd = CURL + " -k -i -u " + username + ":" + password + " -X GET -H \"Accept: application/xml\" https://phet.unfuddle.com/api/v1/projects/" + cmdArg;
        System.out.println( "cmd = " + cmd );
        File batchFile = File.createTempFile( "phet-unfuddle", ".bat" );
        File outFile = File.createTempFile( "phet-unfuddle-out", ".txt" );
        FileUtils.writeString( batchFile, cmd + " > " + outFile );
        System.out.println( "Batch file: " + batchFile.getAbsolutePath() );
        Process p = Runtime.getRuntime().exec( batchFile.getAbsolutePath() );
        try {
            System.out.println( "Started wait" );
            int wait = p.waitFor();
            System.out.println( "Ended wait" );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        String s = FileUtils.loadFileAsString( outFile );
//        // Get the input stream and read from it
//        StringBuffer s = new StringBuffer();
//        InputStream in = p.getInputStream();
//        int c;
//        int count = 0;
//        while ( ( c = in.read() ) != -1 ) {
//            s.append( (char) c );
//            count++;
//            if ( count % 1000 == 0 ) {
//                System.out.print( "." );
//            }
//        }
//        in.close();
////        System.out.println( "s = " + s );
        int xmlIndex = s.indexOf( "<?xml" );
        //        System.out.println( "x = " + x );
        System.out.println( "" );
        return s.substring( xmlIndex );
    }

    //fails for dump (timeout)
    public String readString( String readARG ) throws IOException {
        String CURL = "C:\\reid\\phet\\svn\\trunk\\team\\unfuddle\\curl_717_1_ssl\\curl.exe";
        String cmdArg = accountID + "/" + readARG;
        String cmd = CURL + " -k -i -u " + username + ":" + password + " -X GET -H \"Accept: application/xml\" https://phet.unfuddle.com/api/v1/projects/" + cmdArg;
        System.out.println( "cmd = " + cmd );
        Process p = Runtime.getRuntime().exec( cmd );
//        try {
//            System.out.println( "started waiting" );
//            p.waitFor();
//            System.out.println( "Finished waiting" );
//        }
//        catch( InterruptedException e ) {
//            e.printStackTrace();
//        }
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
        in.close();
//        System.out.println( "s = " + s );
        int xmlIndex = s.indexOf( "<?xml" );
        //        System.out.println( "x = " + x );
        System.out.println( "" );
        return s.substring( xmlIndex );
    }
}
