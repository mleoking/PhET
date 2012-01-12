// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.CellProteinSynthesisSimulator;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Control panel that allows the user to alter various parameters of cell
 * operation within a population of cells.
 *
 * @author John Blanco
 */
public class MultiCellParameterController extends PNode {

    private static final Font TITLE_LABEL_FONT = new PhetFont( 16, true );
    //    private static final Color BACKGROUND_COLOR = new Color( 162, 205, 90 );
    private static final Color BACKGROUND_COLOR = new Color( 220, 236, 255 );

    public MultiCellParameterController( MultipleCellsModel model ) {
        // Create the content for this panel.
        PNode content = new VBox(
                20,
                new PText( "Cell Parameters" ) {{setFont( TITLE_LABEL_FONT );}},
                new IntegerParameterSliderNode( CellProteinSynthesisSimulator.TRANSCRIPTION_FACTOR_COUNT_RANGE.getMin(),
                                                CellProteinSynthesisSimulator.TRANSCRIPTION_FACTOR_COUNT_RANGE.getMax(),
                                                model.transcriptionFactorLevel,
                                                "<center>Transcription Factor<br>Level</center>" ),
                new DoubleParameterSliderNode( CellProteinSynthesisSimulator.TF_ASSOCIATION_PROBABILITY_RANGE.getMin(),
                                               CellProteinSynthesisSimulator.TF_ASSOCIATION_PROBABILITY_RANGE.getMax(),
                                               model.transcriptionFactorAssociationProbability,
                                               "<center>Transcription Factor<br>Affinity</center>" ),
                new DoubleParameterSliderNode( CellProteinSynthesisSimulator.POLYMERASE_ASSOCIATION_PROBABILITY_RANGE.getMin(),
                                               CellProteinSynthesisSimulator.POLYMERASE_ASSOCIATION_PROBABILITY_RANGE.getMax(),
                                               model.polymeraseAssociationProbability,
                                               "<center>Polymerase<br>Affinity</center>" ),
                new DoubleParameterSliderNode( CellProteinSynthesisSimulator.PROTEIN_DEGRADATION_RANGE.getMin(),
                                               CellProteinSynthesisSimulator.PROTEIN_DEGRADATION_RANGE.getMax(),
                                               model.proteinDegradationRate,
                                               "<center>Protein<br>Degradation Rate</center>" )
        );

        // Add the content to a control panel.
        addChild( new ControlPanelNode( content, BACKGROUND_COLOR ) );
    }

}
