// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.model;

import fj.F;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Generic property of the model, this provides an Property interface to observing and interacting with the model.
 *
 * @author Sam Reid
 */
class ClientProperty<T> extends SettableProperty<T> {
    private final Property<IntroState> state;
    private final F<IntroState, T> get;
    private final F<T, F<IntroState, IntroState>> change;

    public ClientProperty( final Property<IntroState> state,
                           final F<IntroState, T> get,
                           final F<T, F<IntroState, IntroState>> change ) {
        super( get.f( state.get() ) );
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

        //No need to check whether the value has actually changed, the Property<IntroState> state is private and not used elsewhere.
        //The "if changed" part happens in the last line in this method.
        state.set( change.f( value ).f( state.get() ) );
        notifyIfChanged();
    }

    @Override public T get() {
        return get.f( state.get() );
    }

    //Wire up to Property to use its richer interface like add, greaterThan, etc.
    Property<T> toProperty() {
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

    Property<T> createProperty() {return new Property<T>( get() );}
}