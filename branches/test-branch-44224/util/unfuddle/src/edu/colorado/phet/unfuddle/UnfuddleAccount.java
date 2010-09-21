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
public class UnfuddleAccount extends XMLObject implements IUnfuddleAccount {

    private final String PROJECTS = "projects";

    public UnfuddleAccount( File xmlDump ) throws IOException, SAXException, ParserConfigurationException {
        this( toNode( xmlDump ) );
    }

    private static Element toNode( File xmlDump ) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document s = documentBuilder.parse( xmlDump );
        return s.getDocumentElement();
    }

    public UnfuddleAccount( Node node ) {
        super( node );
    }

    public String toString() {
        return "unfuddle account, project0=" + getProject( 0 );
    }

    public String getEmailAddress( String username ) {
        int numPeople = numPeople();
        for ( int i = 0; i < numPeople; i++ ) {
            UnfuddlePerson p = new UnfuddlePerson( getListElement( "people", "person", i ) );
            if ( p.getUsername().equals( username ) ) {
                return p.getEmail();
            }
        }
        return null;
    }

    public UnfuddlePerson getPersonForID( int id ) {
        int numPeople = numPeople();
        for ( int i = 0; i < numPeople; i++ ) {
            UnfuddlePerson p = new UnfuddlePerson( getListElement( "people", "person", i ) );
            if ( p.getID() == id ) {
                return p;
            }
        }
        return null;
    }

    private int numPeople() {
        return getListCount( "people", "person" );
    }

    public String getComponentForID( int id ) {
        int componentCount = getProject( 0 ).getComponentCount();
        for ( int i = 0; i < componentCount; i++ ) {
            if ( getProject( 0 ).getComponent( i ).getID() == id ) {
                return getProject( 0 ).getComponent( i ).getName();
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
        private String COMPONENTS = "components";
        private IUnfuddleAccount mapping;

        public UnfuddleProject( Node node, IUnfuddleAccount mapping ) {
            super( node );
            this.mapping = mapping;
        }

        public int getTicketCount() {
            return getListCount( TICKETS );
        }

        public int getComponentCount() {

            return getListCount( COMPONENTS );
        }

        public UnfuddleComponent getComponent( int i ) {
            return new UnfuddleComponent( getListElement( COMPONENTS, i ) );
        }

        public UnfuddleTicket getTicket( int i ) {
            return new UnfuddleTicket( getListElement( TICKETS, i ), mapping );
        }

        public String toString() {
            return "Project: title=" + getTextContent( "title" ) + " ticketCount=" + getTicketCount();
        }
    }

    static class UnfuddleComponent extends XMLObject {

        public UnfuddleComponent( Node node ) {
            super( node );
        }

        public int getID() {
            return Integer.parseInt( getTextContent( "id" ) );
        }

        public String getName() {
            return getTextContent( "name" );
        }

        public String toString() {
            return getName();
        }
    }

    static class UnfuddleTicket extends XMLObject {
        private String COMMENTS = "comments";
        private IUnfuddleAccount mapping;

        public UnfuddleTicket( Node node, IUnfuddleAccount mapping ) {
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
        private IUnfuddleAccount mapping;

        public UnfuddleTicketComment( Node node, IUnfuddleAccount mapping ) {
            super( node );
            this.mapping = mapping;
        }

        public String toString() {
            return mapping.getPersonForID( getAuthorID() ).getName() + " said on " + getCreatedAt() + ": " + getTextContent( "body" );
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
        File file = new File( args[0] );
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document s = documentBuilder.parse( file );
        Element root = s.getDocumentElement();
        UnfuddleAccount unfuddleProject = new UnfuddleAccount( root );
        System.out.println( "unfuddleProject = " + unfuddleProject );
        final UnfuddleProject phetProject = unfuddleProject.getProject( 0 );
        for ( int i = 0; i < phetProject.getComponentCount(); i++ ) {
            System.out.println( "phetProject.getComponent( i ) = " + phetProject.getComponent( i ) );

        }
    }
}
