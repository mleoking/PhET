// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Sim-sharing enums that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BLLSimSharing {

    public static enum UserComponents implements IUserComponent {
        concentrationTab, beersLawTab,
        shaker, shakerIcon,
        dropper, dropperButton, dropperIcon,
        solventFaucet, drainFaucet,
        concentrationMeterBody, concentrationMeterProbe,
        soluteComboBox,
        evaporationSlider,
        lightHousing, lightButton,
        ruler,
        cuvetteWidthHandle,
        solution,
        detectorBody, detectorProbe, transmittanceRadioButton, absorbanceRadioButton,
        solutionComboBox,
        concentrationSliderThumb, concentrationSliderTrack, concentrationTextField,
        lambdaMaxRadioButton, variableRadioButton,
        wavelengthControl
    }

    public static enum ParameterKeys implements IParameterKey {
        isInSolution, locationX, locationY, inBeam
    }
}
