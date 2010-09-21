package edu.colorado.phet.unfuddletool.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

public class TicketSearch {

    public static List<Integer> getTicketSearchIDs( String searchString ) {
        List<Integer> ret = new LinkedList<Integer>();

        String responseString = null;
        try {

            String requestString = "projects/" + Configuration.getProjectIdString() + "/search?query=" + URLEncoder.encode( searchString, "UTF-8" ) + "&filter=tickets";
            //System.out.println( "Requesting: " + requestString );

            responseString = Communication.getXMLResponse( "<request></request>", requestString, Authentication.auth );
            //System.out.println( "Received: " + responseString );

            Document doc = Communication.toDocument( responseString );

            NodeList locationNodes = doc.getElementsByTagName( "location" );

            for ( int i = 0; i < locationNodes.getLength(); i++ ) {
                try {
                    Node locationNode = locationNodes.item( i );

                    Node child = locationNode.getFirstChild();

                    if ( child.getNodeType() == Node.TEXT_NODE ) {
                        String location = child.getNodeValue();
                        System.out.println( "Location: " + location );

                        int lastSlash = location.lastIndexOf( "/" );
                        if ( lastSlash != -1 ) {
                            Integer ticketId = Integer.valueOf( location.substring( lastSlash + 1 ) );
                            System.out.println( "Ticket ID: " + ticketId );
                            ret.add( ticketId );
                        }
                        else {
                            System.out.println( "WARNING: slash in location not encountered?" );
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
        catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
        finally {
            return ret;
        }
    }

}
