// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon.util.functionaljava;

import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;
import fj.data.List;
import fj.data.Option;

import java.util.ArrayList;
import java.util.Collections;

import static fj.Function.curry;
import static fj.Ord.doubleOrd;
import static fj.data.List.iterableList;

/**
 * Additional support for ordering different instances according to functional java ordering rules, to make it easier to use
 *
 * @author Sam Reid
 */
public class FJUtils {

    //Use the same function for computing ordering of objects by mapping to double
    public static <T> Ord<T> ord( final F<T, Double> f ) {
        return Ord.ord( curry( new F2<T, T, Ordering>() {
            public Ordering f( final T u1, final T u2 ) {
                return doubleOrd.compare( f.f( u1 ), f.f( u2 ) );
            }
        } ) );
    }

    //Shuffle the given list
    public static <T> List<T> shuffle( final List<T> list ) {
        ArrayList<T> c = new ArrayList<T>( list.toCollection() );
        Collections.shuffle( c );
        return iterableList( c );
    }

    public static <T> F<Option<T>, List<T>> optionToList() {
        return new F<Option<T>, List<T>>() {
            @Override public List<T> f( final Option<T> ts ) {
                return ts.toList();
            }
        };
    }

    public static <T> boolean contains( final List<T> list, final T value ) {
        return list.exists( new F<T, Boolean>() {
            @Override public Boolean f( final T t ) {
                return t.equals( value );
            }
        } );
    }
}