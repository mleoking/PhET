// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property.integerproperty;

import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * ObservableProperty that is true if the specified ObservableProperty is greater or equal to another specified value.
 *
 * @author Sam Reid
 */
public class LessThanOrEqualTo extends CompositeBooleanProperty {
    public LessThanOrEqualTo( final ObservableProperty<Integer> a, final ObservableProperty<Integer> b ) {
        super( new Function0<Boolean>() {
            public Boolean apply() {
                return a.get() <= b.get();
            }
        }, a, b );
    }
}