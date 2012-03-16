// Copyright 2002-2012, University of Colorado
package org.reid.scenic.tests;

import lombok.val;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * @author Sam Reid
 */
public class TestVal {
    public static void main( String[] args ) {
        val functions = new ArrayList<Function1<Integer, String>>();
        functions.add( new Function1<Integer, String>() {
            @Override public String apply( final Integer integer ) {
                return integer.toString();
            }
        } );
        System.out.println( "functions = " + functions );
        val y = new TestVal().x;
        System.out.println( "x = " + y );
    }
}
