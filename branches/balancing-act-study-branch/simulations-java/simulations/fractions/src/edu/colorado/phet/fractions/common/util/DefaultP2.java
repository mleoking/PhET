// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.util;

import fj.P2;
import lombok.EqualsAndHashCode;

/**
 * Default concrete implementation of abstract P2 class.
 *
 * @author Sam Reid
 */
public @EqualsAndHashCode(callSuper = false) class DefaultP2<A, B> extends P2<A, B> {
    public final A _1;
    public final B _2;

    //Lombok would generate this, but the IDEA plugin doesn't handle generics, so we code it explicitly
    public DefaultP2( final A a, final B b ) {
        this._1 = a;
        this._2 = b;
    }

    @Override public A _1() { return _1; }

    @Override public B _2() { return _2; }

    public static <A, B> DefaultP2<A, B> p2( A a, B b ) { return new DefaultP2<A, B>( a, b ); }

    @Override public String toString() {
        return "(" + _1() + "," + _2() + ")";
    }
}