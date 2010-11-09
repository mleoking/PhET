package edu.colorado.phet.buildanatom.modules.game.view;

/**
 * This is a single-parameter function instance that takes a parameter of type U and returns a value of type T.
 *
 * @author Sam Reid
 */
public interface Function1<U, T> {
    T apply( U u );
}
