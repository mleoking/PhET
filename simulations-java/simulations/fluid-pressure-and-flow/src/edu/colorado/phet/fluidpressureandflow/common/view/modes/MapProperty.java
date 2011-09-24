// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view.modes;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * TODO: generalize interface to public MapProperty( final Property<T> parent, final Function1<T, U> map ) {
 *
 * @author Sam Reid
 */
public class MapProperty<T, U> extends CompositeProperty<U> {

    public MapProperty( final Property<T> parent, final Pair<T, U>... map ) {
        this( parent, new RuntimeExceptionFunction<T, U>(), map );
    }

    public MapProperty( final Property<T> parent, final U defaultValue, final Pair<T, U>... map ) {
        this( parent, new Function1.Constant<T, U>( defaultValue ), map );
    }

    public MapProperty( final Property<T> parent, final Function1<T, U> defaultValue, final Pair<T, U>... map ) {
        super( new Function0<U>() {
            public U apply() {
                for ( Pair<T, U> pair : map ) {
                    if ( parent.get() == pair._1 ) { return pair._2; }
                }
                return defaultValue.apply( parent.get() );
            }
        }, parent );
    }

    private static class RuntimeExceptionFunction<T, U> implements Function1<T, U> {
        public U apply( T t ) {
            throw new RuntimeException( "No match found for value: " + t );
        }
    }
}
