// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        phMeterIcon, phPaperIcon, conductivityTesterIcon, magnifyingGlassRadioButton,
        concentrationGraphRadioButton, liquidRadioButton, showSolventCheckBox, magnifyingGlassIcon,
        waterIcon, concentrationGraphIcon, liquidIcon, phColorKey, acidRadioButton, baseRadioButton,
        weakRadioButton, strongRadioButton, waterRadioButton, strongAcidRadioButton, weakAcidRadioButton,
        strongBaseRadioButton, weakBaseRadioButton, strongAcidIcon, weakAcidIcon, strongBaseIcon,
        weakBaseIcon, conductivityTesterRadioButton,
        customSolutionTab, introductionTab
    }

    //Identify the non-interactive components by text search through the sim
    public static List<UserComponents> nonInteractive = Arrays.asList( UserComponents.concentrationGraph, UserComponents.lightBulb, UserComponents.battery, UserComponents.phColorKey, UserComponents.reactionEquation );

    public static List<UserComponents> interactive = new ArrayList<UserComponents>() {{
        addAll( Arrays.asList( UserComponents.values() ) );
        removeAll( nonInteractive );
    }};

    public static enum ParameterKeys implements IParameterKey {
        handle, isCircuitCompleted, isInSolution, isPaperAlignedWithColorKey, lens, molecule
    }
}