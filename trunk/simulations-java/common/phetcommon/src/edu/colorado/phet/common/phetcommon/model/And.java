package edu.colorado.phet.common.phetcommon.model;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Returns a boolean AND over Property arguments.  This provides read-only access;
 * calling setValue on this AndProperty doesn't propagate back to the original properties.
 *
 * @author Sam Reid
 */
public class And extends Property<Boolean> {
    public And( final Property<Boolean> a, final Property<Boolean> b ) {
        super( a.getValue() && b.getValue() );
        final SimpleObserver updateState = new SimpleObserver() {
            public void update() {
                setValue( a.getValue() && b.getValue() );
            }
        };
        a.addObserver( updateState );
        b.addObserver( updateState );
    }

    public static And And( Property<Boolean> a, final Property<Boolean> b ) {
        return new And( a, b );
    }
}
