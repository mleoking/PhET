package edu.colorado.phet.website.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.event.*;

public class HibernateEventListener implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    private static Logger logger = Logger.getLogger( HibernateEventListener.class.getName() );

    private static Map<Class, List<IChangeListener>> listenermap = new HashMap<Class, List<IChangeListener>>();

    public static void addListener( Class eClass, IChangeListener listener ) {
        List<IChangeListener> listeners = listenermap.get( eClass );
        if ( listeners == null ) {
            listeners = new LinkedList<IChangeListener>();
            listenermap.put( eClass, listeners );
        }
        listeners.add( listener );
    }

    public static void removeListener( Class eClass, IChangeListener listener ) {
        List<IChangeListener> listeners = listenermap.get( eClass );
        if ( listeners == null ) {
            throw new RuntimeException( "No listeners registered for that class" );
        }
        else {
            listeners.remove( listener );
        }
    }

    public void onPostInsert( PostInsertEvent event ) {
        Object entity = event.getEntity();
        Class eClass = entity.getClass();
        List<IChangeListener> listeners = listenermap.get( eClass );
        if ( listeners != null ) {
            List<IChangeListener> copy = new LinkedList<IChangeListener>( listeners );
            for ( IChangeListener listener : copy ) {
                listener.onInsert( entity, event );
            }
        }
    }

    public void onPostUpdate( PostUpdateEvent event ) {
        Object entity = event.getEntity();
        Class eClass = entity.getClass();

        List<IChangeListener> listeners = listenermap.get( eClass );
        if ( listeners != null ) {
            List<IChangeListener> copy = new LinkedList<IChangeListener>( listeners );
            for ( IChangeListener listener : copy ) {
                listener.onUpdate( entity, event );
            }
        }

        if ( entity instanceof DataListener ) {
            ( (DataListener) entity ).onUpdate();
        }

//        logger.debug( "----" );
//        logger.debug( eClass.getCanonicalName() );
//        logger.debug( "id: " + event.getId() );
//        logger.debug( "name: " + event.getPersister().getPropertyNames()[1] );
//        logger.debug( "lazy: " + event.getPersister().getPropertyLaziness()[1] );
//        logger.debug( "old: " + event.getOldState()[1] );
//        logger.debug( "new: " + event.getState()[1] );


    }

    public void onPostDelete( PostDeleteEvent event ) {
        Object entity = event.getEntity();
        Class eClass = entity.getClass();
        List<IChangeListener> listeners = listenermap.get( eClass );
        if ( listeners != null ) {
            List<IChangeListener> copy = new LinkedList<IChangeListener>( listeners );
            for ( IChangeListener listener : copy ) {
                listener.onDelete( entity, event );
            }
        }
    }
}
