// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property;

import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Provides the negation of an ObservableProperty<Boolean>, but unlike Not, its value cannot be set (only observed)
 *
 * @author Sam Reid
 */
public class ObservableNot extends CompositeBooleanProperty {

    public ObservableNot( final ObservableProperty<Boolean> parent ) {
        super( new Function0<Boolean>() {
                   public Boolean apply() {
                       return !parent.get();
                   }
               }, parent );
    }

    public static ObservableNot not( ObservableProperty<Boolean> p ) {
        return new ObservableNot( p );
    }
}