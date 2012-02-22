// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.StubGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MobileBiomoleculeNode;
import edu.colorado.phet.geneexpressionbasics.mrnaproduction.model.MessengerRnaProductionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Control panel that controls the concentration and affinity for a control
 * panel.
 *
 * @author John Blanco
 */
public class TranscriptionFactorControlPanel extends PNode {

    public TranscriptionFactorControlPanel( MessengerRnaProductionModel model, final ModelViewTransform mvt, boolean positive ) {

        final TranscriptionFactorConfig transcriptionFactorConfig;
        String titleText;
        if ( positive ){
            transcriptionFactorConfig = model.POSITIVE_TRANSCRIPTION_FACTOR_CONFIG;
            titleText = "<center>Positive<br>Transcription Factor</center>";
        }
        else{
            transcriptionFactorConfig = model.NEGATIVE_TRANSCRIPTION_FACTOR_CONFIG;
            titleText = "<center>Negative<br>Transcription Factor</center>";
        }

        PNode title = new HTMLNode( titleText ){{
            setFont( new PhetFont( 14 ) );
        }};

        PNode contents = new VBox(
                title,
                new MobileBiomoleculeNode( mvt, new TranscriptionFactor( new StubGeneExpressionModel(), transcriptionFactorConfig ) )
        );

        addChild( new ControlPanelNode( contents ) );
    }
}
