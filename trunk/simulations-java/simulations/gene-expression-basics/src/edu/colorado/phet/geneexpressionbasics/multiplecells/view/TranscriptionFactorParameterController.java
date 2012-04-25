// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.CellProteinSynthesisSimulator;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;

/**
 * Control panel that allows the user to change parameters related to
 * transcription factors in a multi-cell model.
 *
 * @author John Blanco
 */
public class TranscriptionFactorParameterController extends CellParameterControlPanel {

    public TranscriptionFactorParameterController( MultipleCellsModel model ) {
        // TODO: i18n
        super( "Transcription Factors", new VBox(
                20,
                new LogarithmicIntegerParameterSliderNode( UserComponents.transcriptionFactorLevelSlider,
                                                           CellProteinSynthesisSimulator.TRANSCRIPTION_FACTOR_COUNT_RANGE.getMin(),
                                                           CellProteinSynthesisSimulator.TRANSCRIPTION_FACTOR_COUNT_RANGE.getMax(),
                                                           model.transcriptionFactorLevel,
                                                           "<center>Positive TF Concentration</center>" ),
                new LogarithmicParameterSliderNode( UserComponents.transcriptionFactorAffinitySlider,
                                                    CellProteinSynthesisSimulator.TF_ASSOCIATION_PROBABILITY_RANGE.getMin(),
                                                    CellProteinSynthesisSimulator.TF_ASSOCIATION_PROBABILITY_RANGE.getMax(),
                                                    model.transcriptionFactorAssociationProbability,
                                                    "<center>Positive TF Affinity</center>" )
        ) );
    }
}
