package edu.colorado.phet.unfuddle;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Created by: Sam
 * Feb 17, 2008 at 4:13:56 PM
 */
public class UnfuddleAccount extends XMLObject implements PeopleMapping {
    //    private static File FILE = new File( "C:/Users/Sam/Desktop/phet.unfuddled.20080217220129.xml" );
    private static File FILE = new File( "C:\\Users\\Sam\\Desktop\\phet.unfuddled.20080218003433.xml" );
    private String PROJECTS = "projects";

    public UnfuddleAccount( Node node ) {
        super( node );
    }

    public String toString() {
        return "unfuddle account, project0=" + getProject( 0 );
    }

    static class UnfuddlePerson extends XMLObject {

        public UnfuddlePerson( Node node ) {
            super( node );
        }

        public int getID() {
            return Integer.parseInt( getTextContent( "id" ) );
        }

        public String getName() {
            return getTextContent( "first-name" ) + " " + getTextContent( "last-name" );
        }
    }

    public String getPersonForID( int id ) {
        int numPeople = getListCount( "people", "person" );
        for ( int i = 0; i < numPeople; i++ ) {
            UnfuddlePerson p = new UnfuddlePerson( getListElement( "people", "person", i ) );
            if ( p.getID() == id ) {
                return p.getName();
            }
        }
        return null;
    }

    public int getProjectCount() {
        return getListCount( PROJECTS );
    }

    public UnfuddleProject getProject( int i ) {
        return new UnfuddleProject( getNode( PROJECTS ).getNode( i, "project" ).getNode(), this );
    }

    static class UnfuddleProject extends XMLObject {
        private String TICKETS = "tickets";
        private PeopleMapping mapping;

        public UnfuddleProject( Node node, PeopleMapping mapping ) {
            super( node );
            this.mapping = mapping;
        }

        public int getTicketCount() {
            return getListCount( TICKETS );
        }

        public UnfuddleTicket getTicket( int i ) {
            return new UnfuddleTicket( getListElement( TICKETS, i ), mapping );
        }

        public String toString() {
            return "Project: title=" + getTextContent( "title" ) + " ticketCount=" + getTicketCount();
        }
    }

    static class UnfuddleTicket extends XMLObject {
        private String COMMENTS = "comments";
        private PeopleMapping mapping;

        public UnfuddleTicket( Node node, PeopleMapping mapping ) {
            super( node );
            this.mapping = mapping;
        }

        public int getCommentCount() {
            return getListCount( COMMENTS );
        }

        public UnfuddleTicketComment getComment( int i ) {
            return new UnfuddleTicketComment( getListElement( COMMENTS, i ), mapping );
        }

        public String getDescription() {
            return getTextContent( "description" );
        }

        public String toString() {
            return "Ticket #" + getNumber() + ", description=" + getDescription();
        }

        private int getNumber() {
            return Integer.parseInt( getTextContent( "number" ) );
        }
    }

    static class UnfuddleTicketComment extends XMLObject {
        private PeopleMapping mapping;

        public UnfuddleTicketComment( Node node, PeopleMapping mapping ) {
            super( node );
            this.mapping = mapping;
        }

        public String toString() {
            return mapping.getPersonForID( getAuthorID() ) + " said on " + getCreatedAt() + ": " + getTextContent( "body" );
        }

        public String getCreatedAt() {
            return getTextContent( "created-at" );
        }

        public int getAuthorID() {
            return Integer.parseInt( getTextContent( "author-id" ) );
        }

        public String getUpdatedAt() {
            return getTextContent( "updated-at" );
        }
    }

    public String getUpdatedAt() {
        return getTextContent( "updated-at" );
    }

    public static void main( String[] args ) throws ParserConfigurationException, IOException, SAXException {
        File file = args.length > 0 ? new File( args[0] ) : FILE;

//        String str = FileUtils.loadFileAsString( file );
//        File f = File.createTempFile( file.getName(), "" );
//        System.out.println( "Using temp: " + f.getAbsolutePath() );
//        FileUtils.writeString( f, str.substring( str.indexOf( "<?xml" ) ) );
//        file = f;
        File f = file;

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document s = documentBuilder.parse( f );
        Element root = s.getDocumentElement();
        UnfuddleAccount unfuddleProject = new UnfuddleAccount( root );
        System.out.println( "unfuddleProject = " + unfuddleProject );
        final UnfuddleProject phetProject = unfuddleProject.getProject( 0 );
        for ( int i = 0; i < phetProject.getTicketCount(); i++ ) {
//            if ( i == 23 ) {
            final UnfuddleTicket ticket = phetProject.getTicket( i );
            System.out.println( "Ticket[" + i + "] = " + ticket );
            for ( int k = 0; k < ticket.getCommentCount(); k++ ) {
                System.out.println( "comment[" + k + "]= " + ticket.getComment( k ) );
            }
//            }
        }


    }

}
