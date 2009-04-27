package edu.colorado.phet.unfuddletool;

import java.util.*;

import org.w3c.dom.Element;

import edu.colorado.phet.unfuddletool.data.Ticket;

public class TicketHandler {

    public Set<Ticket> tickets;

    private TicketListModel model;

    private TicketHandler() {
        tickets = new HashSet<Ticket>();
    }

    public void setModel( TicketListModel model ) {
        this.model = model;

        if ( tickets.size() > 0 ) {
            Iterator<Ticket> iter = tickets.iterator();
            while ( iter.hasNext() ) {
                model.addTicket( iter.next() );
            }
        }
    }

    private static TicketHandler ticketHandler;

    public static TicketHandler getTicketHandler() {
        if ( ticketHandler == null ) {
            ticketHandler = new TicketHandler();
        }

        return ticketHandler;
    }

    public Ticket requestNewTicketById( int id ) {
        Element element = Communication.getTicketElementFromServer( id );
        Ticket ticket = null;
        if ( element != null ) {
            ticket = new Ticket( element );
            tickets.add( ticket );
            if ( model != null ) {
                model.addTicket( ticket );
            }
        }
        return ticket;
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

    public void requestTicketUpdate( int id, Date latestDate ) {
        System.out.println( "Ticket update requested for " + id + " if older than " + latestDate );
        Ticket ticket = getTicketById( id );
        if( ticket.lastUpdateTime().compareTo( latestDate ) < 0 ) {
            System.out.println( "Ticket " + ticket + " out of date, updating" );
            ticket.update();
        }
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
