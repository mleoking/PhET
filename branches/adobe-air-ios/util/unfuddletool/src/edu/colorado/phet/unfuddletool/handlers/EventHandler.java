package edu.colorado.phet.unfuddletool.handlers;

import java.util.*;

import javax.swing.*;

import edu.colorado.phet.unfuddletool.data.Event;

public class EventHandler {

    public Set<Event> events;

    private List<EventAddListener> listeners;

    private EventHandler() {
        events = new HashSet<Event>();

        listeners = new LinkedList<EventAddListener>();
    }

    private static EventHandler eventHandler;

    public static EventHandler getEventHandler() {
        if ( eventHandler == null ) {
            eventHandler = new EventHandler();
        }

        return eventHandler;
    }

    public Event getEventById( long id ) {
        Iterator<Event> iter = events.iterator();

        while ( iter.hasNext() ) {
            Event event = iter.next();

            if ( event.getId() == id ) {
                return event;
            }
        }

        return null;
    }

    public void checkEvent( Event event ) {
        long id = event.getId();
        for ( Iterator<Event> iterator = events.iterator(); iterator.hasNext(); ) {
            Event curEvent = iterator.next();
            if ( curEvent.getId() == id ) {
                return;
            }
        }

        // it's new!
        events.add( event );
        notifyAddEvent( event );
    }

    //----------------------------------------------------------------------------
    // Listeners and notifiers
    //----------------------------------------------------------------------------

    private void notifyAddEvent( final Event event ) {
        //System.out.println( "New event: " + event );
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Iterator<EventAddListener> iter = listeners.iterator();
                while ( iter.hasNext() ) {
                    iter.next().onEventAdded( event );
                }
            }
        } );
    }

    public void notifyCurrentEvents( final EventAddListener listener ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                Iterator<Event> iter = events.iterator();
                while ( iter.hasNext() ) {
                    listener.onEventAdded( iter.next() );
                }
            }
        } );
    }

    public void addEventAddListener( EventAddListener listener ) {
        listeners.add( listener );
    }

    public void removeEventAddListener( EventAddListener listener ) {
        listeners.remove( listener );
    }

    public interface EventAddListener {
        public void onEventAdded( Event event );
    }
}