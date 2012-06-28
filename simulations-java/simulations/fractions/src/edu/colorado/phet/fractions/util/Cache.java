// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.util;

import fj.F;

import java.util.HashMap;

/**
 * Map that also provides a function for creating and caching items that are not yet in the map.
 * In the Fractions Intro sim, this is used to store images that are expensive to create to improve performance.
 *
 * @author Sam Reid
 */
public class Cache<T, U> extends F<T, U> {
    private final F<T, U> f;
    private final boolean debug;
    private final HashMap<T, U> map = new HashMap<T, U>();

    //Max size of the cache, or -1 if unlimited
    private int cacheSize;

    public Cache( F<T, U> f ) { this( -1, f, false ); }

    public Cache( int cacheSize, F<T, U> f ) { this( cacheSize, f, false ); }

    public Cache( int cacheSize, F<T, U> f, boolean debug ) {
        this.cacheSize = cacheSize;
        this.f = f;
        this.debug = debug;
    }

    @Override public U f( T t ) {
        if ( cacheSize > 0 && map.size() > cacheSize ) {
            map.clear();
        }
        if ( !map.containsKey( t ) ) {
            if ( debug ) { System.out.println( "cache miss for key = " + t ); }
            final U result = f.f( t );
            map.put( t, result );
            return result;
        }
        else {
            if ( debug ) { System.out.println( "cache hit for key = " + t ); }
            return map.get( t );
        }
    }

    //Utility method to avoid providing type arguments in client instantiation
    public static <T, U> Cache<T, U> cache( int cacheSize, F<T, U> f ) { return new Cache<T, U>( cacheSize, f, false ); }

    //Utility method that can be used at maintenance/debugging time in order to identify cache hits/misses
    public static <T, U> Cache<T, U> cache( int cacheSize, F<T, U> f, boolean debug ) { return new Cache<T, U>( cacheSize, f, debug ); }
}