// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.CellProteinSynthesisSimulator;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;

/**
 * Control panel for controlling polymerase parameters in the multi-cell model.
 *
 * @author John Blanco
 */
public class PolymeraseParameterController extends CellParameterControlPanel {

    public PolymeraseParameterController( MultipleCellsModel model ) {
        // TODO: i18n
        super( "Polymerase",
               new VBox(
                       new DoubleParameterSliderNode( UserComponents.polymeraseAffinitySlider,
                                                      CellProteinSynthesisSimulator.POLYMERASE_ASSOCIATION_PROBABILITY_RANGE.getMin(),
                                                      CellProteinSynthesisSimulator.POLYMERASE_ASSOCIATION_PROBABILITY_RANGE.getMax(),
                                                      model.polymeraseAssociationProbability,
                                                      "<center>Polymerase<br>Affinity</center>" )
               ) );
    }
}
