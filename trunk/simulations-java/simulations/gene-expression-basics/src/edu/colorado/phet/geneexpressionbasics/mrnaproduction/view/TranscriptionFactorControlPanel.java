// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents;
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
        if ( positive ) {
            transcriptionFactorConfig = model.POSITIVE_TRANSCRIPTION_FACTOR_CONFIG;
            titleText = "<center>Positive<br>Transcription Factor</center>";
        }
        else {
            transcriptionFactorConfig = model.NEGATIVE_TRANSCRIPTION_FACTOR_CONFIG;
            titleText = "<center>Negative<br>Transcription Factor</center>";
        }

        PNode title = new HTMLNode( titleText ) {{
            setFont( new PhetFont( 14 ) );
        }};

        PNode contents = new VBox(
                title,
                new ConcentrationController( mvt, transcriptionFactorConfig )
        );

        addChild( new ControlPanelNode( contents ) );
    }

    private static class ConcentrationController extends PNode {

        private ConcentrationController( ModelViewTransform mvt, TranscriptionFactorConfig transcriptionFactorConfig ) {
            PNode molecule = new MobileBiomoleculeNode( mvt, new TranscriptionFactor( new StubGeneExpressionModel(), transcriptionFactorConfig ) );
            molecule.setPickable( false );
            molecule.setChildrenPickable( false );
            PText caption = new PText( "Concentration" ) {{
                setFont( new PhetFont( 14, false ) );
            }};
            PNode slider = new HSliderNode( new UserComponent( UserComponents.transcriptionFactorLevelSlider ),
                                            0,
                                            1,
                                            100,
                                            5,
                                            new Property<Double>( 0.0 ),
                                            new BooleanProperty( true ) );
            addChild( new VBox( 0,
                                molecule,
                                caption,
                                slider) );
        }
    }

    private static class HorizontalSliderWithLabelsAtEnds extends PNode {
        private static final double OVERALL_WIDTH = 120;
        private static final Font LABEL_FONT = new PhetFont( 11 );

        private HorizontalSliderWithLabelsAtEnds() {
            
        }
    }
}
