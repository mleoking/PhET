package edu.colorado.phet.unfuddletool;

import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Element;

import edu.colorado.phet.unfuddletool.data.Ticket;

public class TicketHandler {

    public Set<Ticket> tickets;

    private TicketHandler() {
        tickets = new HashSet<Ticket>();
    }

    private static TicketHandler ticketHandler;

    public static TicketHandler getTicketHandler() {
        if ( ticketHandler == null ) {
            ticketHandler = new TicketHandler();
        }

        return ticketHandler;
    }

    public Ticket requestNewTicketById( int id ) {
        String xmlString = Communication.getXMLResponse( "<request></request>", "projects/9404/tickets/" + String.valueOf( id ), Authentication.auth );
        try {
            Ticket ticket = new Ticket( (Element) Communication.toDocument( xmlString ).getFirstChild() );

            tickets.add( ticket );

            return ticket;
        }
        catch( TransformerException e ) {
            e.printStackTrace();
        }
        catch( ParserConfigurationException e ) {
            e.printStackTrace();
        }

        return null;
    }

    public Ticket getTicketById( int id ) {
        Iterator<Ticket> iter = tickets.iterator();

        while ( iter.hasNext() ) {
            Ticket ticket = iter.next();

            if ( ticket.getId() == id ) {
                return ticket;
            }
        }

        return requestNewTicketById( id );
    }

    public Ticket getTicketByNumber( int number ) {
        Iterator<Ticket> iter = tickets.iterator();

        while ( iter.hasNext() ) {
            Ticket ticket = iter.next();

            if ( ticket.getNumber() == number ) {
                return ticket;
            }
        }

        return null;
    }

    public List<Ticket> getTicketList() {
        List<Ticket> list = new LinkedList<Ticket>();

        Iterator<Ticket> iter = tickets.iterator();

        while ( iter.hasNext() ) {
            Ticket ticket = iter.next();

            list.add( ticket );
        }

        return list;
    }

    public List<Ticket> getTicketListByUpdate() {
        List<Ticket> list = getTicketList();
        Collections.sort( list, new RecentTicketComparator() );
        Collections.reverse( list );
        return list;
    }

    public String ticketReportString() {

        String ret = "";

        List<Ticket> list = getTicketListByUpdate();

        ret += "Number of tickets in TicketHandler: " + String.valueOf( list.size() ) + "\n";

        Iterator<Ticket> iter = list.iterator();

        while ( iter.hasNext() ) {
            Ticket ticket = iter.next();

            ret += ticket.getUpdatedAt().toString() + "   " + ticket.toString() + "\n";
        }

        return ret;
    }

    private class RecentTicketComparator implements Comparator<Ticket> {
        public int compare( Ticket a, Ticket b ) {
            // Faster:
            //return a.getUpdatedAt().compareTo( b.getUpdatedAt() );

            // Slower (has to download all of the comments first):
            return a.lastUpdateTime().compareTo( b.lastUpdateTime() );
        }
    }
}
