// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.model.property;

import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * Returns a boolean computation over Property<Boolean> arguments, such as And or Or.
 *
 * @author Sam Reid
 */
public class BinaryBooleanProperty extends CompositeProperty<Boolean> {
    public BinaryBooleanProperty( final ObservableProperty<Boolean> a, final ObservableProperty<Boolean> b, final Function2<Boolean, Boolean, Boolean> combiner ) {
        super( new Function0<Boolean>() {
                   public Boolean apply() {
                       return combiner.apply( a.getValue(), b.getValue() );
                   }
               }, a, b );
    }
}