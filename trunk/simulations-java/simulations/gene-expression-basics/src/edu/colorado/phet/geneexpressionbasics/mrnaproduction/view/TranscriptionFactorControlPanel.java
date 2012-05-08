// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.StubGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.DnaMoleculeNode;
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

    public static final double TRANSCRIPTION_FACTOR_SCALE = 0.08;
    private static final ModelViewTransform TRANSCRIPTION_FACTOR_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                                                  new Point2D.Double( 0, 0 ),
                                                                                                                                  TRANSCRIPTION_FACTOR_SCALE );

    public static final double DNA_SCALE = 0.1;
    private static final ModelViewTransform DNA_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                                 new Point2D.Double( 0, 0 ),
                                                                                                                 DNA_SCALE );

    /**
     * Constructor.
     */
    public TranscriptionFactorControlPanel( MessengerRnaProductionModel model, TranscriptionFactorConfig transcriptionFactorConfig, Property<Double> affinityProperty ) {

        final String titleText;
        final IntegerProperty tfLevelProperty;
        if ( transcriptionFactorConfig.isPositive ) {
            transcriptionFactorConfig = model.POSITIVE_TRANSCRIPTION_FACTOR_CONFIG;
            titleText = GeneExpressionBasicsResources.Strings.POSITIVE_TRANSCRIPTION_FACTOR_HTML;
            tfLevelProperty = model.positiveTranscriptionFactorCount;
        }
        else {
            transcriptionFactorConfig = model.NEGATIVE_TRANSCRIPTION_FACTOR_CONFIG;
            titleText = GeneExpressionBasicsResources.Strings.NEGATIVE_TRANSCRIPTION_FACTOR_HTML;
            tfLevelProperty = model.negativeTranscriptionFactorCount;
        }

        PNode title = new HTMLNode( titleText ) {{
            setFont( new PhetFont( 16, true ) );
        }};

        PNode transcriptionFactorNode = new MobileBiomoleculeNode( TRANSCRIPTION_FACTOR_MVT, new TranscriptionFactor( transcriptionFactorConfig ) );
        PNode dnaFragmentNode = new DnaMoleculeNode( new DnaMolecule( DnaMolecule.BASE_PAIRS_PER_TWIST + 1, 0.0, true ), DNA_MVT, 2, false );
        PNode contents = new VBox(
                20,
                title,
                new ConcentrationController( transcriptionFactorConfig, tfLevelProperty, 0, model.MAX_TRANSCRIPTION_FACTOR_COUNT ),
                new AffinityController( transcriptionFactorNode, dnaFragmentNode, affinityProperty )
        );

        addChild( new ControlPanelNode( contents ) );
    }

    // Class definition for slider that controls the concentration of a
    // transcription factor.
    private static class ConcentrationController extends PNode {

        private ConcentrationController( TranscriptionFactorConfig transcriptionFactorConfig, IntegerProperty tfLevelProperty, int min, int max ) {
            PText caption = new PText( GeneExpressionBasicsResources.Strings.CONCENTRATIONS ) {{
                setFont( new PhetFont( 14, false ) );
            }};
            PNode molecule = new MobileBiomoleculeNode( TRANSCRIPTION_FACTOR_MVT, new TranscriptionFactor( new StubGeneExpressionModel(), transcriptionFactorConfig ) );
            molecule.setPickable( false );
            molecule.setChildrenPickable( false );
            addChild( new VBox( 5,
                                caption,
                                molecule,
                                new HorizontalSliderWithLabelsAtEnds( new UserComponent( UserComponents.transcriptionFactorLevelSlider ),
                                                                      new IntegerToDoublePropertyWrapper( tfLevelProperty ),
                                                                      (double) min,
                                                                      (double) max,
                                                                      GeneExpressionBasicsResources.Strings.NONE,
                                                                      GeneExpressionBasicsResources.Strings.LOTS ) ) );
        }
    }

    // Convenience class that connects an integer property to a double property.
    private static class IntegerToDoublePropertyWrapper extends DoubleProperty {
        private IntegerToDoublePropertyWrapper( final IntegerProperty integerProperty ) {
            super( (double) integerProperty.get() );

            // Connect from integer to double.
            integerProperty.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer integerValue ) {
                    set( (double) integerValue );
                }
            } );

            // Connect from double to integer.
            addObserver( new VoidFunction1<Double>() {
                public void apply( Double doubleValue ) {
                    integerProperty.set( (int) Math.round( doubleValue ) );
                }
            } );
        }
    }
}
