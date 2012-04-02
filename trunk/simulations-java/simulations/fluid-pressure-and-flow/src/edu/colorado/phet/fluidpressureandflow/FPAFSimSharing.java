// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Sim-sharing enums specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FPAFSimSharing {

    public enum UserComponents implements IUserComponent {
        waterTowerFaucet, fluidDensitySlider, flowRateMetricSlider, gravitySlider, flowRateEnglishSlider,
        fluidPressureFaucet,
        slowMotionRadioButton,
        normalSpeedRadioButton,
    }
}