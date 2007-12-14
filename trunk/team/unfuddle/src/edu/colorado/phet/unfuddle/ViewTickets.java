package edu.colorado.phet.unfuddle;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Feasibility test code to view tickets using unfuddle api
 * <p/>
 * Tickets are read using, e.g.:
 * curl -k -i -u username:password -X GET -H "Accept: application/xml" https://phet.unfuddle.com/api/v1/projects/9404/tickets/
 * <p/>
 * Currently brittle and untested
 * Created by: Sam
 * Dec 13, 2007 at 10:32:47 PM
 */
public class ViewTickets {
    private static final File file = new File( "C:\\Users\\Sam\\Desktop\\tickets-12-13-2007.xml" );

    public static void main( String[] args ) throws IOException, SAXException, ParserConfigurationException {
        File file = args.length > 0 ? new File( args[0] ) : ViewTickets.file;
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document s = documentBuilder.parse( file );
        Element root = s.getDocumentElement();
        ArrayList tickets = getTickets( root );
        for ( int i = 0; i < tickets.size(); i++ ) {
            TicketElement ticketElement = (TicketElement) tickets.get( i );
            System.out.println( "ticketElement = " + ticketElement );
        }
        Collections.sort( tickets, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                TicketElement t = (TicketElement) o1;
                TicketElement t2 = (TicketElement) o2;
                return Double.compare( t.getUpdatedAt().getTime(), t2.getUpdatedAt().getTime() );//t.getUpdatedAt().compareTo( t2.getUpdatedAt() );
            }
        } );
    }

    static class TicketElement {
        private Node ticket;
        private Node node;

        public TicketElement( Node node ) {
            this.node = node;
        }

        Node getChildNode( String type ) {
            for ( int i = 0; i < node.getChildNodes().getLength(); i++ ) {
                if ( node.getChildNodes().item( i ).getNodeName().equals( type ) ) {
                    return node.getChildNodes().item( i );
                }
            }
            return null;
        }

        public Date getUpdatedAt() {
            return getDate( "updated-at" );
        }

        public String toString() {
//            return "ticket: created-at: " + getValue( "created-at" ) + ", summary=" + getValue( "summary" ) + " lastChanged=" + getValue( "updated-at" );
            return "ticket[" + getValue( "component-id" ) + "]: created-at: " + getDate( "created-at" ) + ", updatedAt=" + getDate( "updated-at" ) + ", summary=" + getValue( "summary" );
        }

        private Date getDate( String s ) {
            return getTime( getValue( s ) );
        }

        public Date getTime( String value ) {
            try {
                DateFormat d = new SimpleDateFormat( "yyyy-MM-dd-hh:mm:ss" );
                return d.parse( value.replace( 'T', '-' ) );
            }
            catch( ParseException e ) {
                e.printStackTrace();
                return null;
            }
        }

        private String getValue( String node ) {
            return getValue( getChildNode( node ) );
        }

        private String getValue( Node node ) {
            if ( node == null || node.getChildNodes().getLength() == 0 ) {
                return "null";
            }
            return node.getChildNodes().item( 0 ).getNodeValue();
        }
    }

    static ArrayList getTickets( Node node ) {
        ArrayList t = new ArrayList();
        getTickets( node, t );
        return t;
    }

    static void getTickets( Node node, ArrayList tickets ) {
        if ( node.getNodeName().equals( "ticket" ) ) {
            tickets.add( new TicketElement( node ) );
        }
        if ( node.getNodeType() == Node.ELEMENT_NODE ) {
            NodeList children = node.getChildNodes();
            for ( int i = 0; i < children.getLength(); i++ ) {
                getTickets( children.item( i ), tickets );
            }
        }
    }

}
