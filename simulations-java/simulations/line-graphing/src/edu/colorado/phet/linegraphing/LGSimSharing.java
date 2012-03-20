// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Data-collection enums that are specific to this project.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LGSimSharing {

    public static enum UserComponents implements IUserComponent {
        introTab, gameTab,
        yFormSlopeInterceptRadioButton, xFormSlopeInterceptRadioButton,
        unitPositiveSlopeCheckBox, unitNegativeSlopeCheckBox
    }
}
