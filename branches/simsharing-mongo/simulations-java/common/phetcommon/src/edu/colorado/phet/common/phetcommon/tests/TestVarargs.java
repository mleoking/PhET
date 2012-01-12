package edu.colorado.phet.common.phetcommon.tests;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;

/**
 * Test application for generics warnings, see #2996
 *
 * @author Sam Reid
 */
public class TestVarargs {
    public void combine( Option<Integer>... valueLists ) {
    }

    public static void main( String[] args ) {
        new TestVarargs().combine( new Some<Integer>( 3 ), new Some<Integer>( 4 ), new None<Integer>() );
    }
}
