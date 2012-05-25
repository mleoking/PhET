// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.CellProteinSynthesisSimulator;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;

/**
 * Control panel that allows the user to change parameters related to
 * degradation of biomolecules in the multi-cell model.
 *
 * @author John Blanco
 */
class DegradationControlPanel extends CellParameterControlPanel {

    public DegradationControlPanel( MultipleCellsModel model ) {
        super( GeneExpressionBasicsResources.Strings.DEGRADATION, new VBox(
                20,
                new DoubleParameterSliderNode( UserComponents.proteinDegradationRateSlider,
                                               CellProteinSynthesisSimulator.PROTEIN_DEGRADATION_RANGE.getMin(),
                                               CellProteinSynthesisSimulator.PROTEIN_DEGRADATION_RANGE.getMax(),
                                               model.proteinDegradationRate,
                                               GeneExpressionBasicsResources.Strings.PROTEIN,
                                               GeneExpressionBasicsResources.Strings.SLOW,
                                               GeneExpressionBasicsResources.Strings.FAST )
        ) );
    }
}
