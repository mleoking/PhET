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

import java.util.EventListener;
import java.util.EventObject;

/**
 * EventChannel
 * An interface defining a channel to which EventListeners can subscribe and through which creators of EventObjects
 * can send then to listeners.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface EventChannel {
    /**
     * Adds a listener to the channel
     * @param listener
     */
    void addListener( EventListener listener );

    /**
     * Removes a listener from the channel
     * @param listener
     */
    void removeListener( EventListener listener );

    /**
     * Removes all listeners from the channel
     */
    void removeAllListeners();

    /**
     * Causes the channel to call event handlers for particular listeners on the channel.
     * @param event
     */
    void fireEvent( EventObject event );

    /**
     * Gets the number of listeners on the channel
     * @return
     */
    int getNumListeners();
}
