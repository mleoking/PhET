package edu.colorado.phet.ec3.test.apptests;

import edu.colorado.phet.ec3.EC3LookAndFeel;
import edu.colorado.phet.ec3.EnergySkateParkApplication;
import edu.colorado.phet.ec3.EnergySkateParkStrings;

/**
 * User: Sam Reid
 * Date: Oct 9, 2006
 * Time: 11:42:52 PM
 * Copyright (c) Oct 9, 2006 by Sam Reid
 */

public class TestStuckInTrack {
    private EnergySkateParkApplication energySkateParkApplication;

    public TestStuckInTrack( String[] args ) {
        energySkateParkApplication = new EnergySkateParkApplication( args );
    }

    public static void main( String[] args ) {
        EnergySkateParkStrings.init( args, "localization/EnergySkateParkStrings" );
        new EC3LookAndFeel().initLookAndFeel();
        new TestStuckInTrack( args ).start();
    }

    private void start() {
        energySkateParkApplication.startApplication();
        energySkateParkApplication.getModule().getEnergySkateParkModel().splineSurfaceAt( 1 ).translate( 1.2, -1.8 );
        energySkateParkApplication.getModule().getEnergySkateParkModel().bodyAt( 0 ).setCMRotation( Math.PI / 2 );
    }
}
