/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.components;

import edu.colorado.phet.common.util.EventRegistry;

import java.util.EventListener;
import java.util.EventObject;

/**
 * PComponent
 * This is the base class for all GUI components. It acts as an event channel. Aribitrary
 * implementations of EventListener can add themselves as listeners and will be notified
 * of any events fired by concrete subclasses of this one.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class PComponent {
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
