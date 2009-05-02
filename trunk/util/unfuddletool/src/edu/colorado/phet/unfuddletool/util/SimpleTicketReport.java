package edu.colorado.phet.unfuddletool.util;

import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.colorado.phet.unfuddletool.Authentication;
import edu.colorado.phet.unfuddletool.Configuration;
import edu.colorado.phet.unfuddletool.data.Ticket;
import edu.colorado.phet.unfuddletool.handlers.TicketHandler;

public class SimpleTicketReport {

    public static List<Ticket> getMyActiveTickets() {
        List<Ticket> ret = new LinkedList<Ticket>();

        String responseString = Communication.getXMLResponse( "<request></request>", "projects/" + Configuration.getProjectIdString() + "/ticket_reports/dynamic?title=temporary+report&fields_string=number&conditions_string=assignee-eq-current,status-neq-closed", Authentication.auth );

        Document doc = null;
        try {
            doc = Communication.toDocument( responseString );
            //NodeList topItems = doc.getFirstChild().getChildNodes();

            NodeList idNodes = doc.getElementsByTagName( "id" );

            for ( int i = 0; i < idNodes.getLength(); i++ ) {
                try {
                    Node idNode = idNodes.item( i );

                    Node child = idNode.getFirstChild();

                    if ( child.getNodeType() == Node.TEXT_NODE ) {
                        int ticketId = Integer.valueOf( child.getNodeValue() );

                        Ticket ticket = TicketHandler.getTicketHandler().getTicketById( ticketId );

                        if ( ticket != null ) {
                            ret.add( ticket );
                        }
                    }
                    else {
                        System.out.println( "WARNING: not text node" );
                    }
                }
                catch( NumberFormatException e ) {
                    e.printStackTrace();
                }
                catch( DOMException e ) {
                    e.printStackTrace();
                }
            }

            /*
            for ( int i = 0; i < topItems.getLength(); i++ ) {
                Node topNode = topItems.item( i );

                System.out.println( topNode.getNodeName() );

                if ( topNode.getNodeName().equals( "groups" ) ) {
                    NodeList groups = topNode.getChildNodes();

                    for( int j = 0; j < groups.getLength(); j++ ) {
                        Node groupNode = groups.item( j );

                        
                    }

                }
            }
            */

        }
        catch( TransformerException e ) {
            e.printStackTrace();
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
        }
        finally {
            return ret;
        }
    }

}
