package edu.colorado.phet.common.phetcommon.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Provides the negation of a Property<Boolean>
 *
 * @author Sam Reid
 */
public class Not extends Property<Boolean> {
    public Not( final Property<Boolean> p ) {
        super( !p.getValue() );
        p.addObserver( new SimpleObserver() {
            public void update() {
                setValue( !p.getValue() );
            }
        } );
        addObserver( new SimpleObserver() {
            public void update() {
                p.setValue( !getValue() );
            }
        } );
    }

    public static Property<Boolean> not( Property<Boolean> p ) {
        return new Not( p );
    }
}
