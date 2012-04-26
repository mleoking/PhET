// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.CellProteinSynthesisSimulator;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;

/**
 * Control panel that allows the user to change parameters related to
 * affinities of different biomolecules in the multi-cell model.
 *
 * @author John Blanco
 */
public class AffinityControlPanel extends CellParameterControlPanel {

    public AffinityControlPanel( MultipleCellsModel model ) {
        // TODO: i18n
        super( "Affinities", new VBox(
                20,
                new LogarithmicParameterSliderNode( UserComponents.transcriptionFactorAffinitySlider,
                                                    CellProteinSynthesisSimulator.TF_ASSOCIATION_PROBABILITY_RANGE.getMin(),
                                                    CellProteinSynthesisSimulator.TF_ASSOCIATION_PROBABILITY_RANGE.getMax(),
                                                    model.transcriptionFactorAssociationProbability,
                                                    "Positive Transcription Factor" ),
                new DoubleParameterSliderNode( UserComponents.polymeraseAffinitySlider,
                                               CellProteinSynthesisSimulator.POLYMERASE_ASSOCIATION_PROBABILITY_RANGE.getMin(),
                                               CellProteinSynthesisSimulator.POLYMERASE_ASSOCIATION_PROBABILITY_RANGE.getMax(),
                                               model.polymeraseAssociationProbability,
                                               "Polymerase" )
        ) );
    }
}
