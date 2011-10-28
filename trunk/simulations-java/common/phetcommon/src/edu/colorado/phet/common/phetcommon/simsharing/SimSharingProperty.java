// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Property that can record its value with the sim sharing features.
 *
 * @author Sam Reid
 */
public class SimSharingProperty<T> extends Property<T> {
    public SimSharingProperty( final String name, final T value ) {
        this( name, value, new Function1<T, String>() {
            public String apply( T t ) {
                return t.toString();
            }
        } );
    }

    public SimSharingProperty( final String name, final T value, final Function1<T, String> toString ) {
        super( value );

        //Observe changes but do not notify about the initial value//TODO: Or should we notify about the initial value for purposes of knowing how the sim started up in case it is changed later?
        addObserver( new SimpleObserver() {
            public void update() {
                SimSharingEvents.actionPerformed( name + ": " + toString.apply( get() ) );
            }
        }, false );
    }
}
