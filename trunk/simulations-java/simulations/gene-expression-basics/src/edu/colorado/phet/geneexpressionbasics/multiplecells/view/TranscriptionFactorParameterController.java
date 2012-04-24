// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.CellProteinSynthesisSimulator;
import edu.colorado.phet.geneexpressionbasics.multiplecells.model.MultipleCellsModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel that allows the user to alter various parameters of cell
 * operation within a population of cells.
 *
 * @author John Blanco
 */
public class TranscriptionFactorParameterController extends PNode {

    private static final Font TITLE_LABEL_FONT = new PhetFont( 16, true );
    private static final Color BACKGROUND_COLOR = new Color( 220, 236, 255 );

    public TranscriptionFactorParameterController( MultipleCellsModel model ) {
        // Create the controls for this panel.
        PNode controls = new VBox(
                20,
                new LogarithmicIntegerParameterSliderNode( UserComponents.transcriptionFactorLevelSlider,
                                                           CellProteinSynthesisSimulator.TRANSCRIPTION_FACTOR_COUNT_RANGE.getMin(),
                                                           CellProteinSynthesisSimulator.TRANSCRIPTION_FACTOR_COUNT_RANGE.getMax(),
                                                           model.transcriptionFactorLevel,
                                                           "<center>Concentration</center>" ),
                new LogarithmicParameterSliderNode( UserComponents.transcriptionFactorAffinitySlider,
                                                    CellProteinSynthesisSimulator.TF_ASSOCIATION_PROBABILITY_RANGE.getMin(),
                                                    CellProteinSynthesisSimulator.TF_ASSOCIATION_PROBABILITY_RANGE.getMax(),
                                                    model.transcriptionFactorAssociationProbability,
                                                    "<center>Affinity</center>" )
        );

        addChild( new CollapsibleControlPanel( BACKGROUND_COLOR, "Transcription Factors", TITLE_LABEL_FONT, controls ) );
    }
}
