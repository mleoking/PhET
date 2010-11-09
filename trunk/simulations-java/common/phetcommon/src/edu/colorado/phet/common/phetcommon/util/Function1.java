package edu.colorado.phet.common.phetcommon.util;

/**
 * A one-parameter function that takes an argument of type U and returns a value of type T.
 *
 * @author Sam Reid
 */
public interface Function1<U, T> {
    T apply( U u );
}
