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

public class TestFloorFeetBounce {
    private EnergySkateParkApplication energySkateParkApplication;

    public TestFloorFeetBounce( String[] args ) {
        energySkateParkApplication = new EnergySkateParkApplication( args );
    }

    public static void main( String[] args ) {
        EnergySkateParkStrings.init( args, "localization/EnergySkateParkStrings" );
        new EC3LookAndFeel().initLookAndFeel();
        new TestFloorFeetBounce( args ).start();
    }

    private void start() {
        energySkateParkApplication.startApplication();
        energySkateParkApplication.getModule().getEnergyConservationModel().splineSurfaceAt( 1 ).translate( 5, 0 );
    }
}
