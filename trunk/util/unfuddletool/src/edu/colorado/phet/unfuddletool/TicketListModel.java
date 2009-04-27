package edu.colorado.phet.unfuddletool;

import java.util.*;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import edu.colorado.phet.unfuddletool.data.Ticket;

public class TicketListModel implements ListModel, Ticket.TicketListener {

    private List<Ticket> tickets;

    private List<ListDataListener> listeners;

    private List<TicketListDisplay> displays;

    public TicketListModel() {
        tickets = new LinkedList<Ticket>();
        listeners = new LinkedList<ListDataListener>();
        displays = new LinkedList<TicketListDisplay>();
    }

    public void addTicket( Ticket ticket ) {
        ticket.addListener( this );

        if ( tickets.size() == 0 ) {
            tickets.add( ticket );
            notifyContentsChanged();
            return;
        }

        ListIterator<Ticket> iter = tickets.listIterator();
        Date ticketDate = ticket.lastUpdateTime();

        while ( iter.hasNext() ) {
            Ticket otherTicket = iter.next();

            // if ticket is more recent than the one in the list, insert it before
            if ( ticketDate.compareTo( otherTicket.lastUpdateTime() ) > 0 ) {
                iter.previous();
                iter.add( ticket );
                notifyAddedIndex( iter.previousIndex() );
                return;
            }
        }

        // all other tickets are more recent, insert at the end
        iter.add( ticket );
        notifyAddedIndex( iter.previousIndex() );
    }

    public void removeTicket( Ticket ticket ) {
        ticket.removeListener( this );

        int index = tickets.indexOf( ticket );
        tickets.remove( index );
        notifyRemovedIndex( index );
    }

    public void changeTicket( Ticket ticket ) {
        // TODO: if the ticket is selected, there is more we should do!
        int oldIndex = getTicketIndex( ticket );
        LinkedList<TicketListDisplay> changedDisplays = new LinkedList<TicketListDisplay>();
        Iterator<TicketListDisplay> firstIter = displays.iterator();
        while ( firstIter.hasNext() ) {
            TicketListDisplay display = firstIter.next();
            if ( display.getSelectedIndex() == oldIndex ) {
                changedDisplays.add( display );
            }
        }

        removeTicket( ticket );
        addTicket( ticket );


        int newIndex = getTicketIndex( ticket );

        Iterator<TicketListDisplay> secondIter = changedDisplays.iterator();
        while ( secondIter.hasNext() ) {
            System.out.println( "Updating display" );
            TicketListDisplay display = secondIter.next();
            display.clearSelection();
            display.setSelectedIndex( newIndex );
        }
    }

    public int getTicketIndex( Ticket ticket ) {
        ListIterator<Ticket> iter = tickets.listIterator();

        while ( iter.hasNext() ) {
            Ticket listedTicket = iter.next();
            if ( ticket == listedTicket ) {
                return iter.previousIndex();
            }
        }

        return -1;
    }


    public int getSize() {
        return tickets.size();
    }

    public Object getElementAt( int i ) {
        return tickets.get( i );
    }

    public void addListDataListener( ListDataListener listDataListener ) {
        listeners.add( listDataListener );
    }

    public void removeListDataListener( ListDataListener listDataListener ) {
        listeners.remove( listDataListener );
    }


    private void notifyAddedIndex( int index ) {
        ListDataEvent event = new ListDataEvent( this, ListDataEvent.INTERVAL_ADDED, index, index );

        Iterator<ListDataListener> iter = listeners.iterator();
        while ( iter.hasNext() ) {
            iter.next().intervalAdded( event );
        }
    }

    private void notifyRemovedIndex( int index ) {
        ListDataEvent event = new ListDataEvent( this, ListDataEvent.INTERVAL_REMOVED, index, index );

        Iterator<ListDataListener> iter = listeners.iterator();
        while ( iter.hasNext() ) {
            iter.next().intervalRemoved( event );
        }
    }

    private void notifyContentsChanged() {
        ListDataEvent event = new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, 0, getSize() - 1 );

        Iterator<ListDataListener> iter = listeners.iterator();
        while ( iter.hasNext() ) {
            iter.next().contentsChanged( event );
        }
    }

    public void onTicketUpdate( Ticket ticket ) {
        changeTicket( ticket );
    }


    public void addDisplay( TicketListDisplay display ) {
        displays.add( display );
    }

    public void removeDisplay( TicketListDisplay display ) {
        displays.remove( display );
    }

    public interface TicketListDisplay {
        public int getSelectedIndex();

        public void setSelectedIndex( int index );

        public void clearSelection();
    }
}
