/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.model;

import edu.colorado.phet.common.util.EventRegistry;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.EventChannel;

import java.util.EventListener;
import java.util.EventObject;

/**
 * AbstractModelElement
 * This subclass of ModelElement acts as an event channel. Aribitrary
 * implementations of EventListener can add themselves as listeners and will be notified
 * of any events fired by concrete subclasses of this one.
 *
 * It also extends SimpleObservable, so other objects can register for simple update() callbacks.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class AbstractModelElement extends SimpleObservable implements ModelElement, EventChannel {

    private EventRegistry eventRegistry = new EventRegistry();

    public void addListener( EventListener listener ) {
        eventRegistry.addListener( listener );
    }

    public void removeListener( EventListener listener ) {
        eventRegistry.removeListener( listener );
    }

    public void removeAllListeners() {
        eventRegistry = new EventRegistry();
    }

    public void fireEvent( EventObject event ) {
        eventRegistry.fireEvent( event );
    }
}
