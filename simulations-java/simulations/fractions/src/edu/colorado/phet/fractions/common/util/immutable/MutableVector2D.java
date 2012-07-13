// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.util.immutable;

/**
 * Demonstration of one way to have Mutable definition based on immutable definition.
 *
 * @author Sam Reid
 */
public class MutableVector2D {
    private Vector2D value;

    public MutableVector2D( double x, double y ) {
        this.value = new Vector2D( x, y );
    }

    public void add( double dx, double dy ) {
        this.value = value.plus( dx, dy );
    }
}