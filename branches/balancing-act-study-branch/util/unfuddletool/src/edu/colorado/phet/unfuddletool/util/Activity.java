package edu.colorado.phet.unfuddletool.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.colorado.phet.unfuddletool.Authentication;
import edu.colorado.phet.unfuddletool.Configuration;
import edu.colorado.phet.unfuddletool.data.Comment;
import edu.colorado.phet.unfuddletool.data.Event;
import edu.colorado.phet.unfuddletool.data.Ticket;
import edu.colorado.phet.unfuddletool.handlers.EventHandler;
import edu.colorado.phet.unfuddletool.handlers.TicketHandler;

public class Activity {

    public static Date lastUpdateDate = null;

    public static String getDateXMLString( String startDateString, String endDateString ) {
        return Communication.getXMLResponse( "<request></request>", "projects/" + Configuration.getProjectIdString() + "/activity?start_date=" + startDateString + "&end_date=" + endDateString, Authentication.auth );
    }

    public static String stringFromCalendar( Calendar cal ) {
        return String.valueOf( cal.get( Calendar.YEAR ) ) + "/" + String.valueOf( cal.get( Calendar.MONTH ) + 1 ) + "/" + String.valueOf( cal.get( Calendar.DAY_OF_MONTH ) );
    }

    public static void parseEvents( String str ) {
        try {
            Document doc = Communication.toDocument( str );
            NodeList events = doc.getFirstChild().getChildNodes();

            System.out.println( "Found " + String.valueOf( events.getLength() ) + " events." );

            EventHandler eventHandler = EventHandler.getEventHandler();
            TicketHandler ticketHandler = TicketHandler.getTicketHandler();

            for ( int i = 0; i < events.getLength(); i++ ) {
                Node node = events.item( i );

                if ( node.getNodeType() == Node.ELEMENT_NODE ) {
                    Element eventElement = (Element) node;
                    if ( eventElement.getTagName().equals( "event" ) ) {
                        Event event = new Event( eventElement );

                        eventHandler.checkEvent( event );

                        if ( event.getType() == Event.Type.COMMENT ) {
                            Comment comment = event.getCommentRecord();
                            ticketHandler.requestTicketUpdate( comment.rawParentId, comment.rawUpdatedAt.getDate() );
                        }

                        if ( event.getType() == Event.Type.TICKET ) {
                            Ticket ticket = event.getTicketRecord();
                            ticketHandler.requestTicketUpdate( ticket.getId(), ticket.rawUpdatedAt.getDate() );
                        }
                    }
                }

            }
        }
        catch( TransformerException e ) {
            e.printStackTrace();
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
        }
    }

    public static void dayRoll( Calendar cal, boolean dir ) {
        cal.setTimeInMillis( cal.getTimeInMillis() + ( ( dir ? 1 : -1 ) * 1000 * 60 * 60 * 24 ) );
    }

    public static void requestRecentActivity( int days ) {
        Calendar cal = new GregorianCalendar();
        cal.setTime( new Date() );
        dayRoll( cal, true );
        dayRoll( cal, true );

        String oldString = stringFromCalendar( cal );

        while ( days-- > 0 ) {
            dayRoll( cal, false );
            //cal.roll( Calendar.DATE, false );

            if ( lastUpdateDate != null && cal.getTime().getTime() < lastUpdateDate.getTime() - ( 1000 * 60 * 60 * 24 ) ) {
                continue;
            }

            String newString = stringFromCalendar( cal );

            System.out.println( "Downloading activity from " + newString + " to " + oldString );

            parseEvents( getDateXMLString( newString, oldString ) );

            oldString = newString;
        }

        lastUpdateDate = new Date();

    }


}
