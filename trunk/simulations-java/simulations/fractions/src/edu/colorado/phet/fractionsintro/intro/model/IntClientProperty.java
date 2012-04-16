// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;

/**
 * Integer property of the model, this provides an IntegerProperty interface to observing and interacting with the model.
 *
 * @author Sam Reid
 */
public class IntClientProperty extends ClientProperty<Integer> {

    public IntClientProperty( final Property<IntroState> state,
                              final F<IntroState, Integer> get,
                              final F<Integer, F<IntroState, IntroState>> change ) {
        super( state, get, change );
    }

    protected Property<Integer> createProperty() {
        return new IntegerProperty( get() );
    }

    //TODO: get rid of cast, could do so with generics gymnastics or by duplicating code.  To the reviewer--should we take care of this?
    public IntegerProperty toIntegerProperty() {
        return (IntegerProperty) toProperty();
    }
}