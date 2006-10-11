package edu.colorado.phet.ec3.test;

import edu.colorado.phet.ec3.EC3LookAndFeel;
import edu.colorado.phet.ec3.EnergySkateParkApplication;
import edu.colorado.phet.ec3.EnergySkateParkStrings;

/**
 * User: Sam Reid
 * Date: Oct 9, 2006
 * Time: 11:42:52 PM
 * Copyright (c) Oct 9, 2006 by Sam Reid
 */

public class TestFallThrough {
    private EnergySkateParkApplication energySkateParkApplication;

    public TestFallThrough( String[] args ) {
        energySkateParkApplication = new EnergySkateParkApplication( args );
    }

    public static void main( String[] args ) {
        EnergySkateParkStrings.init( args, "localization/EnergySkateParkStrings" );
        new EC3LookAndFeel().initLookAndFeel();
        new TestFallThrough( args ).start();
    }

    private void start() {
        energySkateParkApplication.startApplication();
        energySkateParkApplication.getModule().getEnergyConservationModel().bodyAt( 0 ).setFrictionCoefficient( 1.0 );
    }
}
