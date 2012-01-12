package edu.colorado.phet.common.phetcommon.tests;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;

/**
 * Test application for generics warnings, see #2996
 *
 * @author Sam Reid
 */
public class TestVarargsExplicit {

    public Integer combine( Option<Integer> a ) {
        final ArrayList<Option<Integer>> list = new ArrayList<Option<Integer>>();
        list.add( a );
        return combine( list );
    }

    public Integer combine( Option<Integer> a, Option<Integer> b ) {
        final ArrayList<Option<Integer>> list = new ArrayList<Option<Integer>>();
        list.add( a );
        list.add( b );
        return combine( list );
    }

    public Integer combine( Option<Integer> a, Option<Integer> b, Option<Integer> c ) {
        final ArrayList<Option<Integer>> list = new ArrayList<Option<Integer>>();
        list.add( a );
        list.add( b );
        list.add( c );
        return combine( list );
    }

    public Integer combine( List<Option<Integer>> valueLists ) {
        int sum = 0;
        for ( Option<Integer> valueList : valueLists ) {
            sum += valueList.getOrElse( 0 );
        }
        return sum;
    }

    public static void main( String[] args ) {
        int value = new TestVarargsExplicit().combine( new Some<Integer>( 3 ), new Some<Integer>( 4 ), new None<Integer>() );
        System.out.println( "value = " + value );
    }
}
