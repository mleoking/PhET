/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * EventRegistry. This class lets instances of EventListener register to be notified of events.
 * It's purpose is to provide a simple and easy to use mechanism for custom events conforming to the
 * event delegation model of the AWT.
 * When fireEvent( EventObject event ) is called on the registry, every registered EventListener that has
 * a public method that takes that type of event as a single parameter and has a return type of void
 * will have that method called on it.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EventRegistry implements EventChannel {

    // Key: event types
    // Value: list of listener types that handle the key event
    Map eventTypeToListenerTypeMap = new IdentityHashMap();
    // Key: listener type
    // Value: list of listeners of the key type
    Map listenerTypeToListenersMap = new IdentityHashMap();
    // Key: event type
    // Value: maps that associate listener types with the methods they use for handling the key event
    Map eventTypeToInvocationMethodMap = new IdentityHashMap();

    /**
     * Registers a listener for all events for which it has handlers. A handler is recognized
     * by a name ending in "Occurred" and having a single parameter of type assignable to
     * EventObject.
     *
     * @param listener
     */
    public synchronized void addListener( EventListener listener ) {
        Class listenerType = listener.getClass();
        Method[] methods = listenerType.getMethods();
        for( int i = 0; i < methods.length; i++ ) {
            Method method = methods[i];
            if( method.getParameterTypes().length == 1
                     && EventObject.class.isAssignableFrom( method.getParameterTypes()[0] ) ) {

                // Register the listener on the event type in the method's signature
                registerListenerForEvent( listener, method );
            }
        }
    }

    /**
     * Removes a listener from the registry
     *
     * @param listener
     */
    public synchronized void removeListener( EventListener listener ) {
        Class listenerType = listener.getClass();
        Set set = (Set)listenerTypeToListenersMap.get( listenerType );
        set.remove( listener );
    }

    private void registerListenerForEvent( EventListener listener, Method method ) {
        Class eventType = method.getParameterTypes()[0];
        if( !eventTypeToInvocationMethodMap.containsKey( eventType ) ) {
            eventTypeToInvocationMethodMap.put( eventType, new HashMap() );
        }
        Map m = (Map)eventTypeToInvocationMethodMap.get( eventType );
        Method testMethod = (Method)m.get( listener.getClass() );
        if( testMethod == null ) {
            method.setAccessible( true );
            m.put( listener.getClass(), method );
        }

        // Put the listener in the map keyed by its type
        if( !listenerTypeToListenersMap.containsKey( listener.getClass() ) ) {
            listenerTypeToListenersMap.put( listener.getClass(), new HashSet() );
        }
        ( (Set)listenerTypeToListenersMap.get( listener.getClass() ) ).add( listener );

        // If the listeners type isn't already identified with the event type,
        // make the association
        if( !eventTypeToListenerTypeMap.containsKey( eventType ) ) {
            eventTypeToListenerTypeMap.put( eventType, new HashSet() );
        }
        Set listenerTypes = (Set)eventTypeToListenerTypeMap.get( eventType );
        listenerTypes.add( listener.getClass() );
    }

    /**
     * Causes all listeners registered for a specified event to be notified through
     * their registered methods
     *
     * @param event
     */
    public void fireEvent( EventObject event ) {
        Set listenerTypeList = (Set)eventTypeToListenerTypeMap.get( event.getClass() );
        if( listenerTypeList != null ) {
            for( Iterator iterator0 = listenerTypeList.iterator(); iterator0 != null && iterator0.hasNext(); ) {
                Class listenerType = (Class)iterator0.next();
                Set listeners = (Set)listenerTypeToListenersMap.get( listenerType );
                for( Iterator iterator = listeners.iterator(); iterator != null && iterator.hasNext(); ) {
                    EventListener listener = (EventListener)iterator.next();
                    Map methodMap = (Map)eventTypeToInvocationMethodMap.get( event.getClass() );
                    Method method = (Method)methodMap.get( listenerType );
                    try {
                        method.invoke( listener, new EventObject[]{event} );
                    }
                    catch( IllegalAccessException e ) {
                        e.printStackTrace();
                    }
                    catch( InvocationTargetException e ) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Removes all listeners from the registry
     */
    public void removeAllListeners() {
        eventTypeToListenerTypeMap = new IdentityHashMap();
        listenerTypeToListenersMap = new IdentityHashMap();
        eventTypeToInvocationMethodMap = new IdentityHashMap();
    }

    /**
     * Returns the number of registered listeners
     * @return
     */
    public int getNumListeners() {
        int num = 0;
        Collection list = this.listenerTypeToListenersMap.values();
        for( Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            Collection collection = (Collection)iterator.next();
            num += collection.size();
        }
        return num;
    }
}
