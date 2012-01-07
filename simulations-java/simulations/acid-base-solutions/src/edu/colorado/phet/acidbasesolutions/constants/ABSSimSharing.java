// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.acidbasesolutions.constants;

import edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants.User.UserComponent;

/**
 * Strings related to sim-sharing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSSimSharing {

    public static enum Components implements UserComponent {
        battery, concentrationControl, concentrationGraph, conductivityTesterNegativeProbe, conductivityTesterPositiveProbe, lightBulb, magnifyingGlass, phMeter, phPaper, reactionEquation, weakStrengthControl,

        phMeterRadioButton, phPaperRadioButton, phMeterIcon, pHPaperIcon, conductivityTesterIcon, magnifyingGlassRadioButton, concentrationGraphRadioButton, liquidRadioButton, showSolventCheckBox, magnifyingGlassIcon, waterIcon, concentrationGraphIcon, liquidIcon, phColorKey, acidRadioButton, baseRadioButton, weakRadioButton, strongRadioButton, waterRadioButton, strongAcidRadioButton, weakAcidRadioButton, strongBaseRadioButton, weakBaseRadioButton, strongAcidIcon, weakAcidIcon, strongBaseIcon, weakBaseIcon, conductivityTesterRadioButton
    }

    public static enum ABSParameterKeys implements SimSharingConstants.ParameterKey {
        handle, isCircuitCompleted, isInSolution, isPaperAlignedWithColorKey, lens, molecule
    }
}