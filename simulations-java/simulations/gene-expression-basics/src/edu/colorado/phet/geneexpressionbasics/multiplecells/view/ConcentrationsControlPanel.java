// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.CellProteinSynthesisSimulator;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;

/**
 * Control panel that allows the user to change the concentrations of various
 * biomolecules in the multi-cell model.
 *
 * @author John Blanco
 */
public class ConcentrationsControlPanel extends CellParameterControlPanel {

    public ConcentrationsControlPanel( MultipleCellsModel model ) {
        super( GeneExpressionBasicsResources.Strings.CONCENTRATIONS, new VBox(
                20,
                new LogarithmicIntegerParameterSliderNode( UserComponents.transcriptionFactorLevelSlider,
                                                           CellProteinSynthesisSimulator.TRANSCRIPTION_FACTOR_COUNT_RANGE.getMin(),
                                                           CellProteinSynthesisSimulator.TRANSCRIPTION_FACTOR_COUNT_RANGE.getMax(),
                                                           model.transcriptionFactorLevel,
                                                           GeneExpressionBasicsResources.Strings.POSITIVE_TRANSCRIPTION_FACTOR ),
                new LogarithmicParameterSliderNode( UserComponents.rnaDestroyerLevel,
                                                    CellProteinSynthesisSimulator.MRNA_DEGRADATION_RATE_RANGE.getMin(),
                                                    CellProteinSynthesisSimulator.MRNA_DEGRADATION_RATE_RANGE.getMax(),
                                                    model.mRnaDegradationRate,
                                                    GeneExpressionBasicsResources.Strings.MRNA_DESTROYER )
        ) );
    }
}
