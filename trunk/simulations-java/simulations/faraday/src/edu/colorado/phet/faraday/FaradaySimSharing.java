// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.faraday;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Sim-sharing enums that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaradaySimSharing {

    public static enum UserComponent implements IUserComponent {

        // Tabs
        barMagnetTab,
        pickupCoilTab,
        electromagnetTab,
        transformerTab,
        generatorTab,

        // Bar Magnet control panel
        barMagnetStrengthSlider,
        flipPolarityButton,
        seeInsideMagnetCheckBox,
        showFieldCheckBox,
        showCompassCheckBox,
        showFieldMeterCheckBox,
        showEarthCheckBox
    }
}
