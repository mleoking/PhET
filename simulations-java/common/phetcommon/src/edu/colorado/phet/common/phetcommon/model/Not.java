// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Provides the negation of a Property<Boolean>
 *
 * @author Sam Reid
 */
public class Not extends SettableProperty<Boolean> {
    private Property<Boolean> parent;

    public Not( final Property<Boolean> parent ) {
        this.parent = parent;
        parent.addObserver( new SimpleObserver() {
            public void update() {
                notifyObservers();
            }
        } );
    }

    @Override
    public Boolean getValue() {
        return !parent.getValue();
    }

    public void setValue( Boolean value ) {
        parent.setValue( !value );//we'll observe the change in the constructor listener, and fire notifications.
    }

    public static SettableProperty<Boolean> not( Property<Boolean> p ) {
        return new Not( p );
    }
}
