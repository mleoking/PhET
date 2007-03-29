package edu.colorado.phet.energyskatepark.test.apptests;

import edu.colorado.phet.energyskatepark.EC3LookAndFeel;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkSpline;
import edu.colorado.phet.energyskatepark.model.spline.CubicSpline;
import edu.colorado.phet.energyskatepark.model.physics.CubicSpline2D;

/**
 * User: Sam Reid
 * Date: Oct 9, 2006
 * Time: 11:42:52 PM
 * Copyright (c) Oct 9, 2006 by Sam Reid
 */

public class TestUpsideDown {
    private EnergySkateParkApplication energySkateParkApplication;

    public TestUpsideDown( String[] args ) {
        energySkateParkApplication = new EnergySkateParkApplication( args );
    }

    public static void main( String[] args ) {
        EnergySkateParkStrings.init( args, "localization/EnergySkateParkStrings" );
        new EC3LookAndFeel().initLookAndFeel();
        new TestUpsideDown( args ).start();
    }

    private void start() {
        energySkateParkApplication.startApplication();
        energySkateParkApplication.getModule().getEnergySkateParkModel().removeAllSplineSurfaces();
        CubicSpline spline = new CubicSpline();
        //add control points
        spline.addControlPoint( 2.9071550255536627, 6.9361158432708665 );
        spline.addControlPoint( 6.5, 1.5 );
        spline.addControlPoint( 12.098807750812382, 3.9589341322445595 );
        spline.addControlPoint( 11.10316012198109, 5.302291326059799 );
        spline.addControlPoint( 5.746260633054336, 5.366175482788927 );
        energySkateParkApplication.getModule().getEnergySkateParkModel().addSplineSurface( new EnergySkateParkSpline( new CubicSpline2D( spline.getControlPoints() ) ) );
    }
}
