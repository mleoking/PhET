// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.view;

import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Returns a boolean OR over Property arguments.  This provides read-only access;
 * calling setValue on this AndProperty doesn't propagate back to the original properties.
 *
 * @author Sam Reid
 */
public class MultiwayOr extends Property<Boolean> {
    public MultiwayOr( final List<Property<Boolean>> p ) {
        super( or( p ) );
        final SimpleObserver updateState = new SimpleObserver() {
            public void update() {
                set( or( p ) );
            }
        };
        for ( Property<Boolean> v : p ) {
            v.addObserver( updateState );
        }
    }

    private static Boolean or( List<Property<Boolean>> p ) {
        for ( Property<Boolean> booleanProperty : p ) {
            if ( booleanProperty.get() ) { return true; }
        }
        return false;
    }
}