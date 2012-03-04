// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.util;

import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;

import static fj.Function.curry;
import static fj.Ord.doubleOrd;

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
}