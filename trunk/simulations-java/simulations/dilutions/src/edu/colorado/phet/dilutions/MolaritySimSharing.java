// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Sim-sharing enums specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolaritySimSharing {

    public enum UserComponents implements IUserComponent {
        showValuesCheckBox, soluteComboBox,
        soluteAmountSlider, volumeSlider,
        concentrationBar, concentrationPointer, solutionBeaker
    }
}
