// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * @author Sam Reid
 */
public class GAOSimSharing {
    public static enum UserComponents implements IUserComponent {
        showVelocityCheckBox, showMassCheckBox, showPathCheckBox, showGridCheckBox, showMeasuringTapeCheckbox, showGravityForceCheckBox,
        gravityOnRadioButton,
        gravityOffRadioButton,

        sunEarthRadioButton, sunEarthMoonRadioButton, earthMoonRadioButton, earthSpaceStationRadioButton,

        satellite, moon, planet, star,

        cartoonTab, toScaleTab,

        resetButton,
        zoomInButton, zoomOutButton
    }
}
