// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.capacitorlab;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Data-collection enums that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLSimSharing {

    public static enum UserComponents implements IUserComponent {
        voltageSlider, plateChargeSlider, capacitanceSlider
    }
}
