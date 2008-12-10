package edu.colorado.phet.unfuddle;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import edu.colorado.phet.unfuddle.process.BasicProcess;
import edu.colorado.phet.unfuddle.process.MyProcess;


/**
 * Created by: Sam
 * Feb 17, 2008 at 9:02:08 PM
 */
public class UnfuddleCurl {
    private MyProcess myProcess;
    private String username;
    private String password;
    private int accountID;
    private String svnTrunk;

    public UnfuddleCurl( MyProcess myProcess, String username, String password, int accountID, String svnTrunk ) {
        this.myProcess = myProcess;
        this.username = username;
        this.password = password;
        this.accountID = accountID;
        this.svnTrunk = svnTrunk;
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
    public String readString( String readARG ) throws IOException, InterruptedException {
//        String curl = svnTrunk + "\\util\\unfuddle\\contrib\\curl\\curl.exe"; //TODO this is Windows specific, users should have curl in their path
        String curl="curl";
        String cmdArg = accountID + "/" + readARG;
        String cmd = curl + " -k -i -u " + username + ":" + password + " -X GET -H \"Accept: application/xml\" https://phet.unfuddle.com/api/v1/projects/" + cmdArg;
        System.out.println( "cmd = " + cmd );
        return execCommand( cmd );
    }

    protected String execCommand( String cmd ) throws IOException, InterruptedException {
        String s = myProcess.invoke( cmd );
        return s.substring( s.indexOf( "<?xml" ) );
    }

    public static void main( String[] args ) throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        File ticketFile = new File( "C:/users/sam/desktop/ticket-out-1.xml" );
        File ticketCommentFile = new File( "C:/users/sam/desktop/ticket-comment-out-1.xml" );

        boolean readFromWeb = true || !ticketFile.exists() || !ticketCommentFile.exists();

        if ( readFromWeb ) {
            String username = args[0];
            String password = args[1];
            String svnTrunk = args[2];
            MyProcess myProcess = new BasicProcess();
            String tickets = new UnfuddleCurl( myProcess, username, password, UnfuddleNotifierConstants.PHET_ACCOUNT_ID, svnTrunk ).readString( "tickets" );
            String ticketComments = new UnfuddleCurl( myProcess, username, password, UnfuddleNotifierConstants.PHET_ACCOUNT_ID, svnTrunk ).readString( "tickets/comments" );
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
