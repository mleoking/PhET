package edu.colorado.phet.unfuddletool.handlers;

import java.util.*;

import javax.swing.*;

import org.w3c.dom.Element;

import edu.colorado.phet.unfuddletool.data.Ticket;
import edu.colorado.phet.unfuddletool.util.Communication;

public class TicketHandler {

    public Set<Ticket> tickets;

    private List<TicketAddListener> addListeners;

    private TicketHandler() {
        tickets = new HashSet<Ticket>();
        addListeners = new LinkedList<TicketAddListener>();
    }

    private static TicketHandler ticketHandler;

    public static TicketHandler getTicketHandler() {
        if ( ticketHandler == null ) {
            ticketHandler = new TicketHandler();
        }

        return ticketHandler;
    }

    public synchronized Ticket requestNewTicketById( int id ) {
        Element element = Communication.getTicketElementFromServer( id );
        Ticket ticket = null;
        if ( element != null ) {
            ticket = new Ticket( element );
            tickets.add( ticket );
            notifyAddTicket( ticket );
        }
        return ticket;
    }

    public synchronized Ticket getTicketById( int id ) {
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
        //System.out.println( "Ticket update requested for " + id + " if older than " + latestDate );
        Ticket ticket = getTicketById( id );
        //if ( ticket.lastUpdateTime().compareTo( latestDate ) < 0 ) {
        if ( ticket.lastUpdateTime().getTime() < latestDate.getTime() ) {
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
        Collections.sort( list, new Ticket.RecentTicketComparator() );
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

    //----------------------------------------------------------------------------
    // Listeners and notifiers
    //----------------------------------------------------------------------------

    private void notifyAddTicket( final Ticket ticket ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Iterator<TicketAddListener> iter = addListeners.iterator();
                while ( iter.hasNext() ) {
                    iter.next().onTicketAdded( ticket );
                }
            }
        } );
    }

    public void notifyCurrentTickets( final TicketAddListener listener ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Iterator<Ticket> iter = tickets.iterator();
                while ( iter.hasNext() ) {
                    listener.onTicketAdded( iter.next() );
                }
            }
        } );
    }

    public void addTicketAddListener( TicketAddListener listener ) {
        addListeners.add( listener );
    }

    public void removeTicketAddListener( TicketAddListener listener ) {
        addListeners.remove( listener );
    }

    public interface TicketAddListener {
        public void onTicketAdded( Ticket ticket );
    }

}
