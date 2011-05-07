// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5;

import java.util.List;

import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Returns a boolean AND over Property arguments.  This provides read-only access;
 * calling setValue on this AndProperty doesn't propagate back to the original properties.
 *
 * @author Sam Reid
 */
public class Or extends CompositeProperty<Boolean> {
    public Or( final ObservableProperty<Boolean> a, final ObservableProperty<Boolean> b ) {
        super( new Function0<Boolean>() {
                   public Boolean apply() {
                       return a.getValue() || b.getValue();
                   }
               }, a, b );
    }

    public And and( ObservableProperty<Boolean> b ) {
        return new And( this, b );
    }

    public static Boolean or( List<Property<Boolean>> p ) {
        for ( Property<Boolean> booleanProperty : p ) {
            if ( booleanProperty.getValue() ) { return true; }
        }
        return false;
    }
}
