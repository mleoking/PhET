// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * This can be used to represent an observable model value in a MVC style pattern. Notifications are sent to observers when they
 * register with addObserver, and also when the value changes (it is up to subclasses to guarantee that notifications are sent when necessary, and not duplicated).
 * <p/>
 * ObservableProperty should be used instead of SettableProperty whenever possible, i.e. when the value needs to be observed but not set.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public abstract class ObservableProperty<T> extends SimpleObservable {
    /**
     * Adds a SimpleObserver to observe the value of this instance.
     * If notifyOnAdd is true, also immediately notifies the SimpleObserver,
     * so that the client code is not responsible for doing so.
     * This helps the SimpleObserver to always be synchronized with this instance.
     *
     * @param simpleObserver
     * @param notifyOnAdd
     */
    public void addObserver( SimpleObserver simpleObserver, boolean notifyOnAdd ) {
        super.addObserver( simpleObserver );
        if ( notifyOnAdd ) {
            simpleObserver.update();
        }
    }

    /**
     * Adds and immediately notifies a SimpleObserver.
     *
     * @param simpleObserver
     */
    @Override
    public void addObserver( SimpleObserver simpleObserver ) {
        addObserver( simpleObserver, true /* notifyOnAdd */ );
    }

    public abstract T getValue();

    @Override
    public String toString() {
        return getValue().toString();
    }
}
