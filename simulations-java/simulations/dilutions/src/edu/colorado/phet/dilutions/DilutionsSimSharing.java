// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * XXX
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionsSimSharing {

    public enum UserComponents implements IUserComponent {
        showValuesCheckBox, soluteComboBox,
        soluteAmountSlider, volumeSlider, solutionVolumeSlider, dilutionVolumeSlider, concentrationSlider
    }
}
