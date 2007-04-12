package edu.colorado.phet.energyskatepark.model.physics;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 2, 2007
 * Time: 2:06:41 PM
 * Copyright (c) Mar 2, 2007 by Sam Reid
 */

public class DefaultTestSet {
    private ArrayList tests = new ArrayList();
    private TestState dualTrackTest;
    private TestState.SplineTestState upsideDownLoop;
    private TestState.SplineTestState fallThroughValley;
    private TestState.FreeFallTestState fallThroughValley2;
    private TestState.SplineTestState fallThroughPeak;
    private TestState.SplineTestState fallOffSteep;
    private TestState.SplineTestState testApproachPeak;

    public DefaultTestSet() {
        dualTrackTest = new TestState.SplineTestState( "Dual Track Test", 0, 0.92, 0.0, false );
        dualTrackTest.addCubicSpline2D( new Point2D.Double[]{new Point2D.Double( 4.560000000000002, 5.269999999999993 ), new Point2D.Double( 3.4899999999999984, 3.1999999999999953 ), new Point2D.Double( 4.999999999999996, 3.3499999999999908 ), new Point2D.Double( 3.1999999999999966, 5.2099999999999875 ), new Point2D.Double( 1.009999999999998, 2.1999999999999984 )} );
        dualTrackTest.addCubicSpline2D( new Point2D.Double[]{new Point2D.Double( 0, 0 ), new Point2D.Double( 1, 1 ), new Point2D.Double( 2, 2 )} );
        tests.add( dualTrackTest );

        upsideDownLoop = new TestState.SplineTestState( "Upside Down Loop", 0, 1.0, 0.0, false );
        upsideDownLoop.addCubicSpline2D( new Point2D.Double[]{new Point2D.Double( 4.560000000000002, 5.269999999999993 ), new Point2D.Double( 3.4899999999999984, 3.1999999999999953 ), new Point2D.Double( 4.999999999999996, 3.3499999999999908 ), new Point2D.Double( 3.1999999999999966, 5.2099999999999875 ), new Point2D.Double( 1.009999999999998, 2.1999999999999984 )} );
        tests.add( upsideDownLoop );

        fallThroughValley = new TestState.SplineTestState( "Fall Through Valley", 0, 0.5, 0, true );
        fallThroughValley.addCubicSpline2D( new Point2D[]{new Point2D.Double( 1, 0.5 ), new Point2D.Double( 2, 1 ), new Point2D.Double( 3, 0.5 ), new Point2D.Double( 4, 2 ), new Point2D.Double( 5, 0.5 )} );
        tests.add( fallThroughValley );

        double w = 1.0;
        fallThroughValley2 = new TestState.FreeFallTestState( "Fall Valley 2", 1, 0, 0, 0 );
        fallThroughValley2.addCubicSpline2D( new Point2D[]{new Point2D.Double( 1 - w / 2, 0 ), new Point2D.Double( 1, 3 ), new Point2D.Double( 1 + w / 2, 0 )} );
        tests.add( fallThroughValley2 );

        fallThroughPeak = new TestState.SplineTestState( "Fall through Peak", 0, 0.92, 0.0, true );
        fallThroughPeak.addCubicSpline2D( new Point2D[]{new Point2D.Double( 0, 0 ), new Point2D.Double( 1, 2 ), new Point2D.Double( 2, 1 ), new Point2D.Double( 3, 2 ), new Point2D.Double( 4, 0 )} );
        tests.add( fallThroughPeak );

        fallOffSteep = new TestState.SplineTestState( "Fall Steep", 0, 0.92, 0.0, true );
        fallOffSteep.addCubicSpline2D( new Point2D.Double[]{new Point2D.Double( 1.0, 0.5 ), new Point2D.Double( 2.0, 1.0 ), new Point2D.Double( 2.7499999999999996, 4.519999999999998 ), new Point2D.Double( 4.699999999999995, 4.219999999999991 ), new Point2D.Double( 4.070000000000002, 0.12999999999999984 )} );
        tests.add( fallOffSteep );

        testApproachPeak = new TestState.SplineTestState( "Approach Peak", 0, 0.91075, 0, true );
        testApproachPeak.addCubicSpline2D( new Point2D[]{new Point2D.Double( 0, 0 ), new Point2D.Double( 1, 2 ), new Point2D.Double( 2, 1 ), new Point2D.Double( 3, 2 ), new Point2D.Double( 4, 0 )} );
        tests.add( testApproachPeak );
    }

    public TestState.FreeFallTestState getFallThroughValley2() {
        return fallThroughValley2;
    }

    public TestState.SplineTestState getUpsideDownLoop() {
        return upsideDownLoop;
    }

    public TestState getDualTrackTest() {
        return dualTrackTest;
    }

    public int getTestCount() {
        return tests.size();
    }

    public TestState getTest( int index ) {
        return (TestState)tests.get( index );
    }

    public TestState.SplineTestState getFallThroughValley() {
        return fallThroughValley;
    }

    public static void main( String[] args ) {
//        new DefaultTestSet().getDualTrackTest().start();
//        new DefaultTestSet().getUpsideDownLoop().start();
//        new DefaultTestSet().getFallThroughValley().start();
//        new DefaultTestSet().getFallThroughValley2().start();
//        new DefaultTestSet().fallThroughPeak.start();
//        new DefaultTestSet().fallOffSteep.start();
        new DefaultTestSet().testApproachPeak.start();
    }
}
