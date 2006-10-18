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

public class TestHeadBounce {
    private EnergySkateParkApplication energySkateParkApplication;

    public TestHeadBounce( String[] args ) {
        energySkateParkApplication = new EnergySkateParkApplication( args );
    }

    public static void main( String[] args ) {
        EnergySkateParkStrings.init( args, "localization/EnergySkateParkStrings" );
        new EC3LookAndFeel().initLookAndFeel();
        new TestHeadBounce( args ).start();
    }

    private void start() {
        energySkateParkApplication.startApplication();
        energySkateParkApplication.getModule().getEnergyConservationModel().bodyAt( 0 ).convertToFreefall();
        energySkateParkApplication.getModule().getEnergyConservationModel().bodyAt( 0 ).setCMRotation( 0 );
        energySkateParkApplication.getModule().getEnergyConservationModel().bodyAt( 0 ).setAttachmentPointPosition( 5, 8 );
    }
}
