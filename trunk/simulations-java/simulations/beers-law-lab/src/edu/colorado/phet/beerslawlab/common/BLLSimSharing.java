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
        concentrationMeterBody, concentrationMeterProbe, dropper, dropperButton,
        dropperIcon, evaporationSlider, solventFaucet, drainFaucet,
        shaker, shakerIcon, soluteComboBox,
        lightOnOffButton
    }

    public static enum Parameters implements IParameterKey {
        isInSolution, locationX, locationY
    }
}
