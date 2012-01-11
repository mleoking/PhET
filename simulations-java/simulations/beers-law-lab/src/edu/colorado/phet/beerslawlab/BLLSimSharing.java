// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab;

import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

/**
 * Sim-sharing enums that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BLLSimSharing {

    public static enum UserComponents implements UserComponent {
        concentrationMeterBody, concentrationMeterProbe, dropper, dropperButton,
        dropperIcon, evaporationSlider, inputFaucetSlider, outputFaucetSlider,
        shaker, shakerIcon, soluteComboBox
    }

    public static enum Parameters implements ParameterKey {
        evaporationRate, flowRate, isInSolution, locationX, locationY
    }
}
