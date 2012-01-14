// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Copied rather than introduce a compile dependency on other java sims.
 *
 * @author Sam Reid
 */
public class AcidBaseSolutionsJavaImport {
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
}