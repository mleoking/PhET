//  Copyright 2002-2011, University of Colorado

package edu.colorado.phet.densityandbuoyancy.model {
import edu.colorado.phet.flexcommon.model.BooleanProperty;

/**
 * Two-way inversion of a BooleanProperty.
 */
public class Not extends BooleanProperty {
    public function Not( predicate: BooleanProperty ) {
        super( !predicate.value );
        predicate.addListener( function(): void {
            value = !predicate.value;
        } );
        addListener( function(): void {
            predicate.value = !value;
        } );
    }
}
}
