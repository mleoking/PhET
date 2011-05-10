// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property5;

import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * @author Sam Reid
 */
public class CompositeBooleanProperty extends CompositeProperty<Boolean> {
    public CompositeBooleanProperty( Function0<Boolean> function, ObservableProperty<?>... properties ) {
        super( function, properties );
    }

    //Returns a property that is an 'or' conjunction of this and the provided argument, makes it possible to chain property calls such as:
    //anySolutes = molesOfSalt.greaterThan( 0 ).or( molesOfSugar.greaterThan( 0 ) );//From IntroModel in "Sugar and Salt Solutions"
    public Or or( ObservableProperty<Boolean> p ) {
        return new Or( this, p );
    }

    public And and( SettableProperty<Boolean> p ) {
        return new And( this, p );
    }
}