// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

public class CompositeProperty<T> extends ObservableProperty<T> {
    private Function0<T> function;

    public CompositeProperty( Function0<T> function, ObservableProperty<?>... properties ) {
        super( function.apply() );
        this.function = function;
        SimpleObserver simpleObserver = new SimpleObserver() {
            public void update() {
                notifyIfChanged();
            }
        };
        for ( ObservableProperty<?> property : properties ) {
            property.addObserver( simpleObserver );
        }
    }

    @Override
    public T getValue() {
        return function.apply();
    }
}
