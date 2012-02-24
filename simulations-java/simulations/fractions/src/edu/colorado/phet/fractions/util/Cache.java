// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.util;

import fj.F;

import java.util.HashMap;

/**
 * Map that also provides a function for creating and caching items that are not yet in the map
 *
 * @author Sam Reid
 */
public class Cache<T, U> extends F<T, U> {
    private final F<T, U> f;
    private final HashMap<T, U> map = new HashMap<T, U>();

    public Cache( F<T, U> f ) {
        this.f = f;
    }

    @Override public U f( T t ) {
        if ( !map.containsKey( t ) ) {
            map.put( t, f.f( t ) );
        }
        return map.get( t );
    }

    //Utility method to avoid providing type arguments in client instantiation
    public static <T, U> Cache<T, U> cache( F<T, U> f ) {
        return new Cache<T, U>( f );
    }
}