package edu.colorado.phet.unfuddletool;

import java.util.*;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import edu.colorado.phet.unfuddletool.data.Ticket;

public class TicketListModel implements ListModel {

    private List<Ticket> tickets;

    private List<ListDataListener> listeners;

    public TicketListModel() {
        tickets = new LinkedList<Ticket>();
        listeners = new LinkedList<ListDataListener>();
    }

    public void addTicket( Ticket ticket ) {
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
        int index = tickets.indexOf( ticket );
        tickets.remove( index );
        notifyRemovedIndex( index );
    }

    public void changeTicket( Ticket ticket ) {
        // TODO: if the ticket is selected, there is more we should do!
        removeTicket( ticket );
        addTicket( ticket );
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
}
