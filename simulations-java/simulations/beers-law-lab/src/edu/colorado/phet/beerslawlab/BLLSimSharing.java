// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants;

/**
 * Strings related to sim-sharing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BLLSimSharing {

    public static enum Objects implements SimSharingConstants.User.UserComponent {concentrationMeterBody, concentrationMeterProbe, dropper, dropperButton, dropperIcon, evaporationSlider, inputFaucetSlider, outputFaucetSlider, shaker, shakerIcon, soluteComboBox}

    public static enum Parameters implements SimSharingConstants.ParameterKey {evaporationRate, flowRate, isInSolution, locationX, locationY}
}
