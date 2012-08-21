// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functions.model;

import fj.F;

import edu.colorado.phet.functions.intro.Graphic;

/**
 * @author Sam Reid
 */
public class Functions {

    public static final F<Object, Object> INTEGER_TIMES_2 = new F<Object, Object>() {
        @Override public Object f( final Object o ) {
            return ( (Integer) o ) * 2;
        }
    };

    public static final F<Object, Object> INTEGER_PLUS_1 = new F<Object, Object>() {
        @Override public Object f( final Object o ) {
            return ( (Integer) o ) + 1;
        }
    };

    public static final F<Object, Object> INTEGER_MINUS_1 = new F<Object, Object>() {
        @Override public Object f( final Object o ) {
            return ( (Integer) o ) - 1;
        }
    };

    public static final F<Object, Object> INTEGER_POWER_2 = new F<Object, Object>() {
        @Override public Object f( final Object o ) {
            return (int) Math.pow( ( (Integer) o ), 2 );
        }
    };

    public static final F<Object, Object> ROTATE_GRAPHIC_RIGHT = new F<Object, Object>() {
        @Override public Object f( final Object o ) {
            return ( (Graphic) o ).rotateRight();
        }
    };
}