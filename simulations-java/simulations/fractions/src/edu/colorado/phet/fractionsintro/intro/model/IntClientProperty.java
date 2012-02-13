// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * Integer property of the model, this provides an IntegerProperty interface to observing and interacting with the model.
 *
 * @author Sam Reid
 */
public class IntClientProperty extends ClientProperty<Integer> {

    public IntClientProperty( final Property<FractionsIntroModelState> state,
                              final Function1<FractionsIntroModelState, Integer> get,
                              final Function2<FractionsIntroModelState, Integer, FractionsIntroModelState> change ) {
        super( state, get, change );
    }

//    @Override protected Property<Integer> createProperty() {
//        return new IntegerProperty( get() );
//    }
//
//    //TODO: get rid of cast
//    public IntegerProperty toIntegerProperty() {
//        return (IntegerProperty) toProperty();
//    }
}