// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.faraday;

import edu.colorado.phet.common.phetcommon.simsharing.messages.ISystemObject;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Sim-sharing enums that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaradaySimSharing {

    public static enum UserComponents implements IUserComponent {

        // Tabs
        barMagnetTab,
        pickupCoilTab,
        electromagnetTab,
        transformerTab,
        generatorTab,

        // Panels
        barMagnetControlPanel,
        pickupCoilControlPanel,
        electromagnetControlPanel,

        // Controls
        strengthControl,
        flipPolarityButton,
        seeInsideMagnetCheckBox,
        showFieldCheckBox,
        showCompassCheckBox,
        showFieldMeterCheckBox,
        showEarthCheckBox,
        showElectrons,
        dcRadioButton,
        acRadioButton,
        loopsSpinner,
        lightbuldRadioButton,
        voltmeterRadioButton,
        loopAreaControl,

        // Menu items
        backgroundColorMenuItem,
        gridControlsMenuItem
    }

    public static enum SystemObjects implements ISystemObject {

        // Secondary windows
        backgroundColorDialog,
        gridControlsDialog
    }
}
