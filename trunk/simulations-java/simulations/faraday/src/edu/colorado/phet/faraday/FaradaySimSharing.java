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

    public static enum Components implements IUserComponent, ISystemObject {

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
        okButton,
        cancelButton,
        needleSpacingSlider,
        needleSizeSlider,
        maxAmplitudeSlider,
        frequencySlider,
        faucetSlider,
        batterySlider,

        // Menu items
        backgroundColorMenuItem,
        gridControlsMenuItem,

        // Secondary windows
        backgroundColorDialog,
        fieldControlsDialog,

        // Sprites
        barMagnet,
        pickupCoil,
        fieldMeter,
        compass,
        electromagnet,
        waterWheel,
        faucet,
        acPowerSupply
    }
}
