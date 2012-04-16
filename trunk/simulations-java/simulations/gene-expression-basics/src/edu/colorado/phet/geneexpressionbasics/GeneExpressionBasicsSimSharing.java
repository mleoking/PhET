// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Sim-sharing enums that are specific to this sims.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GeneExpressionBasicsSimSharing {

    public static enum UserComponents implements IUserComponent {
        numberOfCellsSlider,
        transcriptionFactorLevelSlider,
        transcriptionFactorAffinitySlider,
        polymeraseAffinitySlider,
        proteinDegradationRateSlider,
        negativeTranscriptionFactorCheckBox,
        rnaDestroyerLevel
    }
}
