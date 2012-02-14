// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Generic property of the model, this provides an Property interface to observing and interacting with the model.
 *
 * @author Sam Reid
 */
public class ClientProperty<T> extends SettableProperty<T> {
    private final Property<IntroState> state;
    private final Function1<IntroState, T> get;
    private final Function2<IntroState, T, IntroState> change;

    public ClientProperty( final Property<IntroState> state,
                           final Function1<IntroState, T> get,
                           final Function2<IntroState, T, IntroState> change ) {
        super( get.apply( state.get() ) );
        this.state = state;
        this.get = get;
        this.change = change;
        state.addObserver( new SimpleObserver() {
            public void update() {
                notifyIfChanged();
            }
        } );
    }

    @Override public void set( T value ) {
        state.set( change.apply( state.get(), value ) );
        notifyIfChanged();
    }

    @Override public T get() {
        return get.apply( state.get() );
    }

    //    Wire up to Property to use its richer interface like add, greaterThan, etc.
    public Property<T> toProperty() {
        final Property<T> p = createProperty();
        addObserver( new VoidFunction1<T>() {
            public void apply( T t ) {
                p.set( t );
            }
        } );
        p.addObserver( new VoidFunction1<T>() {
            public void apply( T T ) {
                set( T );
            }
        } );
        return p;
    }

    protected Property<T> createProperty() {return new Property<T>( get() );}
}
