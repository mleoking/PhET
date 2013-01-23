// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.test;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.linegame.model.ChallengeFactory;
import edu.colorado.phet.linegraphing.linegame.model.ChallengeFactory1;
import edu.colorado.phet.linegraphing.linegame.model.ChallengeFactory2;
import edu.colorado.phet.linegraphing.linegame.model.ChallengeFactory3;
import edu.colorado.phet.linegraphing.linegame.model.ChallengeFactory4;
import edu.colorado.phet.linegraphing.linegame.model.ChallengeFactory5;
import edu.colorado.phet.linegraphing.linegame.model.ChallengeFactory6;

/**
 * Test harness for generating large numbers of random challenges, to see if anything fails.
 * "Fails" means that an assertion will fail, so be sure to run this with assertions enabled (-ea).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestChallengeFactories {

    private static final int TESTS_PER_FACTORY = 100000;

    public static void main( String[] args ) {

        // a factory for each level
        final ArrayList<ChallengeFactory> factories = new ArrayList<ChallengeFactory>() {{
            add( new ChallengeFactory1() );
            add( new ChallengeFactory2() );
            add( new ChallengeFactory3() );
            add( new ChallengeFactory4() );
            add( new ChallengeFactory5() );
            add( new ChallengeFactory6() );
        }};

        IntegerRange axisRange = new IntegerRange( -10, 10 );
        for ( ChallengeFactory factory : factories ) {
            System.out.println( "testing " + factory.getClass().getCanonicalName() );
            for ( int i = 0; i < TESTS_PER_FACTORY; i++ ) {
                factory.createChallenges( axisRange, axisRange );
            }
        }

        System.out.println( "test complete" );
    }
}
