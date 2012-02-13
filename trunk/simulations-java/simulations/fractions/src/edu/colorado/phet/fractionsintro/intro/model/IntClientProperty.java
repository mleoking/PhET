// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * @author Sam Reid
 */
public class IntClientProperty extends SettableProperty<Integer> {
    private final Property<FractionsIntroModelState> state;
    private final Function1<FractionsIntroModelState, Integer> get;
    private final Function2<FractionsIntroModelState, Integer, FractionsIntroModelState> change;

    public IntClientProperty( final Property<FractionsIntroModelState> state,
                              final Function1<FractionsIntroModelState, Integer> get,
                              final Function2<FractionsIntroModelState, Integer, FractionsIntroModelState> change ) {
        super( get.apply( state.get() ) );
        this.state = state;
        this.get = get;
        this.change = change;
    }

    @Override public void set( Integer value ) {
        state.set( change.apply( state.get(), value ) );
    }

    @Override public Integer get() {
        return get.apply( state.get() );
    }

    //Wire up to IntegerProperty to use its richer interface like add, greaterThan, etc.
    public IntegerProperty toIntegerProperty() {
        final IntegerProperty p = new IntegerProperty( get() );
        addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer integer ) {
                p.set( integer );
            }
        } );
        p.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer integer ) {
                set( integer );
            }
        } );
        return p;
    }
}
