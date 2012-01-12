// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.acidbasesolutions.constants;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Sim-sharing enums that are specific to this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSSimSharing {

    public static enum UserComponents implements IUserComponent {
        battery, concentrationControl, concentrationGraph,
        lightBulb, magnifyingGlass, phMeter, phPaper,
        reactionEquation, weakStrengthControl, phMeterRadioButton, phPaperRadioButton,
        phMeterIcon, pHPaperIcon, conductivityTesterIcon, magnifyingGlassRadioButton,
        concentrationGraphRadioButton, liquidRadioButton, showSolventCheckBox, magnifyingGlassIcon,
        waterIcon, concentrationGraphIcon, liquidIcon, phColorKey, acidRadioButton, baseRadioButton,
        weakRadioButton, strongRadioButton, waterRadioButton, strongAcidRadioButton, weakAcidRadioButton,
        strongBaseRadioButton, weakBaseRadioButton, strongAcidIcon, weakAcidIcon, strongBaseIcon,
        weakBaseIcon, conductivityTesterRadioButton,
        customSolutionTab, introductionTab
    }

    public static enum ParameterKeys implements IParameterKey {
        handle, isCircuitCompleted, isInSolution, isPaperAlignedWithColorKey, lens, molecule
    }
}