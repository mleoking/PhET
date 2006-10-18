package edu.colorado.phet.ec3.test.apptests;

import edu.colorado.phet.ec3.EC3LookAndFeel;
import edu.colorado.phet.ec3.EnergySkateParkApplication;
import edu.colorado.phet.ec3.EnergySkateParkStrings;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;

/**
 * User: Sam Reid
 * Date: Oct 9, 2006
 * Time: 11:42:52 PM
 * Copyright (c) Oct 9, 2006 by Sam Reid
 */

public class TestLoop {
    private EnergySkateParkApplication energySkateParkApplication;

    public TestLoop( String[] args ) {
        energySkateParkApplication = new EnergySkateParkApplication( args );
    }

    public static void main( String[] args ) {
        EnergySkateParkStrings.init( args, "localization/EnergySkateParkStrings" );
        new EC3LookAndFeel().initLookAndFeel();
        new TestLoop( args ).start();
    }

    private void start() {
        energySkateParkApplication.startApplication();
        energySkateParkApplication.getModule().getEnergyConservationModel().removeAllSplineSurfaces();
        CubicSpline spline = new CubicSpline();
//add control points
        spline.addControlPoint( 2.856047700170355, 6.399488926746162 );
        spline.addControlPoint( 7.202725724020448, 0.6311754684838161 );
        spline.addControlPoint( 11.856047955241685, 2.3107228886329763 );
        spline.addControlPoint( 10.617640530839696, 4.842325397610053 );
        spline.addControlPoint( 8.365511016504522, 4.101089191193068 );
        spline.addControlPoint( 9.250519214740605, 1.213525333233952 );
        spline.addControlPoint( 11.60145654446832, 0.43431858330000445 );
        spline.addControlPoint( 13.62360305213442, 1.073160150591316 );

        energySkateParkApplication.getModule().getEnergyConservationModel().addSplineSurface( new SplineSurface( spline ) );
    }
}
