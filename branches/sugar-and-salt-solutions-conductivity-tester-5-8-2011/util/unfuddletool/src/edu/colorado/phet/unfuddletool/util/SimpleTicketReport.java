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
        return getDynamicReport( "title=temporary+report&fields_string=number&conditions_string=assignee-eq-current,status-neq-closed" );
    }

    public static List<Ticket> getAllActiveTickets() {
        return getDynamicReport( "title=temporary+report&fields_string=number&conditions_string=status-neq-closed" );
    }

    public static List<Integer> getMyActiveTicketIDs() {
        return getDynamicReportIDs( "title=temporary+report&fields_string=number&conditions_string=assignee-eq-current,status-neq-closed" );
    }

    public static List<Integer> getAllActiveTicketIDs() {
        return getDynamicReportIDs( "title=temporary+report&fields_string=number&conditions_string=status-neq-closed" );
    }

    private static List<Ticket> getDynamicReport( String args ) {
        List<Ticket> ret = new LinkedList<Ticket>();

        String responseString = Communication.getXMLResponse( "<request></request>", "projects/" + Configuration.getProjectIdString() + "/ticket_reports/dynamic?" + args, Authentication.auth );

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

                        //System.out.println( "Checking for ticket id " + ticketId );

                        Ticket ticket = TicketHandler.getTicketHandler().getTicketById( ticketId );

                        if ( ticket != null ) {
                            //System.out.println( "Adding ticket id " + ticketId );
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

    private static List<Integer> getDynamicReportIDs( String args ) {
        List<Integer> ret = new LinkedList<Integer>();

        String responseString = Communication.getXMLResponse( "<request></request>", "projects/" + Configuration.getProjectIdString() + "/ticket_reports/dynamic?" + args, Authentication.auth );

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

                        ret.add( ticketId );
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
