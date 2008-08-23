/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * EventChannel
 * <p/>
 * A proxy that broadcasts method calls to registered objects that implement
 * a specified interface. The primary use of this class is for doing event firing/handling.
 * It eliminates the need for clients to manage lists of listeners, and iterate those lists
 * and cast their elements when clients want to send events to those listeners.
 * <p/>
 * Some oddities in this class' use are due to the way the Proxy and InvocationHandler
 * classes work.
 * <p/>
 * Example of use:
 * <p/>
 * <code>
 * public class TestEventChannel {
 * <p/>
 * public static void main( String[] args ) {
 * // Create a channel and get a reference to its proxy
 * EventChannel eventChannel = new EventChannel( ITestListener.class );
 * ITestListener testListenerProxy = (ITestListener)eventChannel.getListenerProxy();
 * <p/>
 * // Create a listener, and attach it to the channel
 * TestListener testListener = new TestListener();
 * eventChannel.addListener( testListener );
 * <p/>
 * // Invoke a method on all listeners attached to the channel
 * testListenerProxy.aHappened( "FOO" );
 * }
 * <p/>
 * public interface ITestListener extends EventListener {
 * void aHappened( String s );
 * }
 * <p/>
 * public static class TestListener implements ITestListener {
 * public void aHappened( String s ) {
 * System.out.println( "TestProxy$TestListener.aHappened: " + s );
 * }
 * }
 * }
 * </code>
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EventChannel implements InvocationHandler {

    private List targets = new ArrayList();
    private Class targetInterface;
    private Object proxy;

    /**
     * Creates a proxy for a list of objects that implement a specified interface
     *
     * @param interf
     */
    public EventChannel( Class interf ) {
        if ( !EventListener.class.isAssignableFrom( interf ) ) {
            throw new RuntimeException( "Attempt to create proxy for an interface that is not an EventListener" );
        }
        targetInterface = interf;
        proxy = Proxy.newProxyInstance( interf.getClassLoader(),
                                        new Class[]{interf}, this );
    }

    /**
     * Registers a listener for all events for which it has handlers. A handler is recognized
     * by a name ending in "Occurred" and having a single parameter of type assignable to
     * EventObject.
     *
     * @param listener
     */
    public synchronized void addListener( EventListener listener ) {
        if ( targetInterface.isInstance( listener ) ) {
            targets.add( listener );
        }
    }

    /**
     * Removes a listener from the registry
     *
     * @param listener
     */
    public synchronized void removeListener( EventListener listener ) {
        targets.remove( listener );
    }

    /**
     * Removes all listeners from the registry
     */
    public void removeAllListeners() {
        targets.clear();
    }

    /**
     * Returns the number of registered listeners
     *
     * @return
     */
    public int getNumListeners() {
        return targets.size();
    }

    /**
     * Determines whether this EventChannel contains the specified listener.
     *
     * @param eventListener
     * @return true if this EventChannel contains the specified listener.
     */
    public boolean containsListener( EventListener eventListener ) {
        return targets.contains( eventListener );
    }

    /**
     * Returns the interface for which this object acts as a proxy.
     *
     * @return
     */
    public Class getInterface() {
        return targetInterface;
    }

    /**
     * Returns a reference to the proxy
     *
     * @return
     */
    public Object getListenerProxy() {
        return proxy;
    }

    //----------------------------------------------------------------------------
    // Invocation Handler
    //----------------------------------------------------------------------------

    /**
     * Invokes a specified method on all the instances in the channel that implememt it
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
        Object target = null;
        try {
            for ( int i = 0; i < targets.size(); i++ ) {
                target = targets.get( i );
                method.invoke( target, args );
            }
        }
        catch( InvocationTargetException ite ) {
            throw new InvocationTargetException( ite, "target = " + target );
        }
        return null;
    }
}
