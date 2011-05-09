package edu.colorado.phet.unfuddletool.gui;

import java.util.*;

import javax.swing.table.AbstractTableModel;

import edu.colorado.phet.unfuddletool.data.Ticket;
import edu.colorado.phet.unfuddletool.handlers.TicketHandler;

public class TicketTableModel extends AbstractTableModel implements Ticket.TicketListener, TicketHandler.TicketAddListener {

    public static final int INDEX_LAST_MODIFIED = 0;
    public static final int INDEX_NUMBER = 1;
    public static final int INDEX_SUMMARY = 2;
    public static final int INDEX_COMPONENT = 3;
    public static final int INDEX_STATUS = 4;
    public static final int INDEX_ASSIGNEE = 5;
    public static final int INDEX_REPORTER = 6;
    public static final int INDEX_PRIORITY = 7;
    public static final int INDEX_MILESTONE = 8;

    public static String ID_LAST_MODIFIED = "Last Modified";
    public static String ID_NUMBER = "Number";
    public static String ID_SUMMARY = "Summary";
    public static String ID_COMPONENT = "Component";
    public static String ID_STATUS = "Status";
    public static String ID_ASSIGNEE = "Assignee";
    public static String ID_REPORTER = "Reporter";
    public static String ID_PRIORITY = "Priority";
    public static String ID_MILESTONE = "Milestone";

    private List<Ticket> tickets;

    private List<TicketTableDisplay> displays;

    public TicketTableModel() {
        tickets = new LinkedList<Ticket>();
        displays = new LinkedList<TicketTableDisplay>();

    }


    public synchronized void addTicket( Ticket ticket ) {
        ticket.addListener( this );

        if ( tickets.size() == 0 ) {
            tickets.add( ticket );
            fireTableDataChanged();
            return;
        }

        ListIterator<Ticket> iter = tickets.listIterator();
        Date ticketDate = ticket.lastUpdateTime();

        while ( iter.hasNext() ) {
            Ticket otherTicket = iter.next();

            // if ticket is more recent than the one in the list, insert it before
            //if ( ticketDate.compareTo( otherTicket.lastUpdateTime() ) > 0 ) {
            //if ( Ticket.compare( ticket, otherTicket ) > 0 ) {
            if ( ticketDate.getTime() > otherTicket.lastUpdateTime().getTime() ) {
                iter.previous();
                iter.add( ticket );
                fireTableRowsInserted( iter.previousIndex(), iter.previousIndex() );
                return;
            }
        }

        // all other tickets are more recent, insert at the end
        iter.add( ticket );
        fireTableRowsInserted( iter.previousIndex(), iter.previousIndex() );
    }

    public synchronized void removeTicket( Ticket ticket ) {
        ticket.removeListener( this );

        int index = tickets.indexOf( ticket );
        tickets.remove( index );
        fireTableRowsDeleted( index, index );
    }

    public synchronized void changeTicket( Ticket ticket ) {
        // TODO: fix when table rows are sorted differently?
        int oldIndex = getTicketIndex( ticket );
        LinkedList<TicketTableDisplay> changedDisplays = new LinkedList<TicketTableDisplay>();
        Iterator<TicketTableDisplay> firstIter = displays.iterator();
        while ( firstIter.hasNext() ) {
            TicketTableDisplay display = firstIter.next();
            if ( display.getSelectedIndex() == oldIndex ) {
                changedDisplays.add( display );
            }
        }

        removeTicket( ticket );
        addTicket( ticket );

        int newIndex = getTicketIndex( ticket );

        Iterator<TicketTableDisplay> secondIter = changedDisplays.iterator();
        while ( secondIter.hasNext() ) {
            System.out.println( "Updating display" );
            TicketTableDisplay display = secondIter.next();
            display.clearSelection();
            display.setSelectedIndex( newIndex );
        }
    }

    public synchronized void clear() {
        Object[] ticketArray = tickets.toArray();
        for ( int i = 0; i < ticketArray.length; i++ ) {
            if ( ticketArray[i] instanceof Ticket ) {
                removeTicket( (Ticket) ticketArray[i] );
            }
        }
    }

    public void addTicketList( List<Ticket> ticketList ) {
        Iterator<Ticket> iter = ticketList.iterator();
        while ( iter.hasNext() ) {
            addTicket( iter.next() );
        }
    }

    public void addTicketIDList( List<Integer> ticketIDList ) {
        Iterator<Integer> iter = ticketIDList.iterator();

        TicketHandler handler = TicketHandler.getTicketHandler();

        while ( iter.hasNext() ) {
            addTicket( handler.getTicketById( iter.next() ) );
        }
    }

    public boolean hasTicket( Ticket ticket ) {
        return getTicketIndex( ticket ) != -1;
    }

    public Ticket getTicketAt( int index ) {
        return tickets.get( index );
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

    public int getRowCount() {
        return tickets.size();
    }

    public int getColumnCount() {
        return 9;
    }

    public String getColumnName( int column ) {
        switch( column ) {
            case INDEX_LAST_MODIFIED:
                return ID_LAST_MODIFIED;
            case INDEX_SUMMARY:
                return ID_SUMMARY;
            case INDEX_NUMBER:
                return ID_NUMBER;
            case INDEX_COMPONENT:
                return ID_COMPONENT;
            case INDEX_STATUS:
                return ID_STATUS;
            case INDEX_ASSIGNEE:
                return ID_ASSIGNEE;
            case INDEX_REPORTER:
                return ID_REPORTER;
            case INDEX_PRIORITY:
                return ID_PRIORITY;
            case INDEX_MILESTONE:
                return ID_MILESTONE;
        }

        return "Unknown";
    }

    public Class getColumnClass( int column ) {
        switch( column ) {
            case INDEX_LAST_MODIFIED:
                return Date.class;
            case INDEX_SUMMARY:
                return Ticket.class;
        }

        return Object.class;
    }

    public Object getValueAt( int i, int j ) {
        Ticket ticket = tickets.get( i );

        switch( j ) {
            case INDEX_LAST_MODIFIED:
                return ticket.lastUpdateTime();
            case INDEX_SUMMARY:
                //return ticket.getSummary();
                return ticket;
            case INDEX_NUMBER:
                return Integer.valueOf( ticket.getNumber() );
            case INDEX_COMPONENT:
                return ticket.getComponentName();
            case INDEX_STATUS:
                return ticket.getStatus();
            case INDEX_ASSIGNEE:
                return ticket.getAssigneeName();
            case INDEX_REPORTER:
                return ticket.getReporterName();
            case INDEX_PRIORITY:
                return Integer.valueOf( ticket.getPriority() );
            case INDEX_MILESTONE:
                return ticket.getMilestoneTitle();
        }

        return null;
    }

    public void onTicketUpdate( Ticket ticket ) {
        changeTicket( ticket );
    }

    public void onTicketAdded( Ticket ticket ) {
        addTicket( ticket );
    }


    public void addDisplay( TicketTableDisplay display ) {
        displays.add( display );
    }

    public void removeDisplay( TicketTableDisplay display ) {
        displays.remove( display );
    }

    public interface TicketTableDisplay {
        public int getSelectedIndex();

        public void setSelectedIndex( int index );

        public void clearSelection();
    }
}
