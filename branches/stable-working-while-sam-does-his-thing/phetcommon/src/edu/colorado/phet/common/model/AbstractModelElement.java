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

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.util.EventRegistry;
import edu.colorado.phet.common.util.SimpleObservable;

import java.util.EventListener;
import java.util.EventObject;

/**
 * AbstractModelElement
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class AbstractModelElement extends SimpleObservable implements ModelElement, EventChannel {

    private EventRegistry eventRegistry = new EventRegistry();

    public void addListener(EventListener listener) {
        eventRegistry.addListener(listener);
    }

    public void removeListener(EventListener listener) {
        eventRegistry.removeListener(listener);
    }

    public void removeAllListeners() {
        eventRegistry = new EventRegistry();
    }

    public int getNumListeners() {
        return eventRegistry.getNumListeners();
    }

    public void fireEvent(EventObject event) {
        eventRegistry.fireEvent(event);
    }
}
