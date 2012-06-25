package edu.colorado.phet.functions.buildafunction.tests;

import fj.F;
import fj.F3;
import fj.P1;

/**
 * @author Sam Reid
 */
public class TestRecursion {
    public static <T> F3<Boolean, P1<T>, P1<T>, T> if1() {
        return new F3<Boolean, P1<T>, P1<T>, T>() {
            @Override public T f( final Boolean condition, final P1<T> then, final P1<T> _else ) {
                return condition ? _else._1() : _else._1();
            }
        };
    }

    public static <T> T _if( boolean condition, T then, T _else ) {
        return condition ? then : _else;
    }

    public static <T> T __if( boolean condition, T then, F<Void, T> _else ) {
        return condition ? then : _else.f( null );
    }

    public static void main( String[] args ) {
        int one = 1;
        double x = 0;
//        double y = _if( x <= 1, 1, f( x - 1 ) * x );
//        double y = __if( x <= 1, 1, new F<Void, Integer>() {
//            @Override public Integer f( final Void _null ) {
//                return
//            }
//        } );
//        System.out.println( "y = " + y );
    }
}