package edu.colorado.phet.unfuddletool.gui;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.table.AbstractTableModel;

import edu.colorado.phet.unfuddletool.data.Event;

public class EventTableModel extends AbstractTableModel {

    public static final int INDEX_TIME = 0;
    public static final int INDEX_EVENT = 1;
    public static final int INDEX_SUMMARY = 2;
    public static final int INDEX_DESCRIPTION = 3;
    public static final int INDEX_PERSON = 4;

    public static String ID_TIME = "Time";
    public static String ID_EVENT = "Event";
    public static String ID_SUMMARY = "Summary";
    public static String ID_DESCRIPTION = "Description";
    public static String ID_PERSON = "Person";

    private List<Event> events;

    public EventTableModel() {
        events = new LinkedList<Event>();
    }

    public void addEvent( Event event ) {
        //event.addListener( this );

        if ( events.size() == 0 ) {
            events.add( event );
            fireTableDataChanged();
            return;
        }

        ListIterator<Event> iter = events.listIterator();
        Date eventDate = event.getCreatedAt();

        while ( iter.hasNext() ) {
            Event otherEvent = iter.next();

            if ( eventDate.getTime() > otherEvent.getCreatedAt().getTime() ) {
                iter.previous();
                iter.add( event );
                fireTableRowsInserted( iter.previousIndex(), iter.previousIndex() );
                return;
            }
        }

        // all other events are more recent, insert at the end
        iter.add( event );
        fireTableRowsInserted( iter.previousIndex(), iter.previousIndex() );
    }

    public int getRowCount() {
        return events.size();
    }

    public int getColumnCount() {
        return 5;
    }

    public String getColumnName( int column ) {
        switch( column ) {
            case INDEX_TIME:
                return ID_TIME;
            case INDEX_EVENT:
                return ID_EVENT;
            case INDEX_SUMMARY:
                return ID_SUMMARY;
            case INDEX_DESCRIPTION:
                return ID_DESCRIPTION;
            case INDEX_PERSON:
                return ID_PERSON;
            default:
                return "unknown";
        }
    }

    public Class getColumnClass( int column ) {
        return Object.class;
    }

    public Object getValueAt( int i, int j ) {
        Event event = events.get( i );

        switch( j ) {
            case INDEX_TIME:
                return event.getCreatedAt();
            case INDEX_EVENT:
                return event.getEventString();
            case INDEX_SUMMARY:
                return event.getSummary();
            case INDEX_DESCRIPTION:
                return event.getDescription();
            case INDEX_PERSON:
                return event.getPersonName();
            default:
                return "unknown";
        }
    }


}
