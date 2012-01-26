// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ProteinA;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ProteinB;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ProteinC;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * A PNode that represents a labeled box where the user can collect protein
 * molecules.
 *
 * @author John Blanco
 */
public class ProteinCollectionNode extends PNode {

    // Upper limit on the width of the PNodes contained in this control panel.
    // This prevents translations from making this box too large.
    private static final double MAX_CONTENT_WIDTH = 300;

    // Attributes of various aspects of the box.
    private static final Font TITLE_FONT = new PhetFont( 20, true );
    private static final Font READOUT_FONT = new PhetFont( 18 );
    private static final Color BACKGROUND_COLOR = new Color( 255, 250, 205 );

    public ProteinCollectionNode( ManualGeneExpressionModel model, ModelViewTransform mvt ) {
        // Create the title and scale it if needed.
        // TODO: i18n
        PNode title = new HTMLNode( "<center>Your Protein<br>Collection:</center>", Color.BLACK, TITLE_FONT ) {{
            if ( getFullBoundsReference().getWidth() > MAX_CONTENT_WIDTH ) {
                // Scale title to fit.
                setScale( MAX_CONTENT_WIDTH / getFullBoundsReference().width );
            }
        }};

        // Create the collection area.
        PNode collectionArea = new ProteinCollectionArea( model, mvt );
        assert collectionArea.getFullBoundsReference().width <= MAX_CONTENT_WIDTH; // Need to make some adjustments if this gets hit.

        PNode contents = new VBox(
                title,
                collectionArea,
                new CollectionCountIndicator( model )
        );
        addChild( new ControlPanelNode( contents, BACKGROUND_COLOR ) );
    }

    /**
     * PNode that indicates the number of proteins that the user has collected
     * so far.  This monitors the model and updates automatically.
     */
    private static class CollectionCountIndicator extends PNode {
        public CollectionCountIndicator( final ManualGeneExpressionModel model ) {
            model.proteinACollected.addObserver( new SimpleObserver() {
                public void update() {
                    updateCount( model );
                }
            } );
            model.proteinBCollected.addObserver( new SimpleObserver() {
                public void update() {
                    updateCount( model );
                }
            } );
            model.proteinCCollected.addObserver( new SimpleObserver() {
                public void update() {
                    updateCount( model );
                }
            } );
        }

        private void updateCount( ManualGeneExpressionModel model ) {
            int numProteinsCollected = 0;
            if ( model.proteinACollected.get() > 0 ) {
                numProteinsCollected++;
            }
            if ( model.proteinBCollected.get() > 0 ) {
                numProteinsCollected++;
            }
            if ( model.proteinCCollected.get() > 0 ) {
                numProteinsCollected++;
            }
            removeAllChildren();
            VBox collectionCountIndicator = new VBox(
                    5,
                    // TODO: i18n.
                    new HBox( 4, new ReadoutPText( "You have: " ), new IntegerBox( numProteinsCollected ) ),
                    new ReadoutPText( "of 3 protein types." ) {{
                        setFont( READOUT_FONT );
                    }}
            );
            if ( collectionCountIndicator.getFullBoundsReference().getWidth() > MAX_CONTENT_WIDTH ) {
                // Make sure that this isn't wider than the max
                // allowed content width.
                setScale( MAX_CONTENT_WIDTH / collectionCountIndicator.getFullBoundsReference().width );
            }
            addChild( collectionCountIndicator );
        }
    }

    /**
     * Convenience class for putting an integer in a box, kind of like a
     * grayed out edit box.
     */
    private static class IntegerBox extends PNode {
        private static final Color BACKGROUND_COLOR = new Color( 240, 240, 240 );

        private IntegerBox( int value ) {
            PText valueText = new ReadoutPText( Integer.toString( value ) );
            Rectangle2D boxBounds = new Rectangle2D.Double( 0, 0, valueText.getFullBoundsReference().width * 2, valueText.getFullBoundsReference().height );
            PhetPPath box = new PhetPPath( boxBounds, BACKGROUND_COLOR, new BasicStroke( 1 ), Color.BLACK );
            addChild( box );
            valueText.centerFullBoundsOnPoint( box.getFullBoundsReference().getCenterX(), box.getFullBoundsReference().getCenterY() );
            addChild( valueText );
        }
    }

    /**
     * Convenience class for the text used in the readout.
     */
    private static class ReadoutPText extends PText {
        private ReadoutPText( String text ) {
            super( text );
            setFont( READOUT_FONT );
        }
    }

    // Class that represents the collection area, where potentially several
    // different types of protein can be collected.
    private static class ProteinCollectionArea extends PNode {
        private ProteinCollectionArea( ManualGeneExpressionModel model, ModelViewTransform mvt ) {
            // Get a transform that performs only the scaling portion of the mvt.
            double scale = mvt.getTransform().getScaleX();
            assert scale == -mvt.getTransform().getScaleY(); // This only handles symmetric transform case.
            AffineTransform transform = AffineTransform.getScaleInstance( scale, scale );

            addChild( new HBox(
                    0,
                    new ProteinCaptureNode( transform.createTransformedShape( new ProteinA().getFullyGrownShape() ), Color.BLACK, new ProteinA().colorProperty.get(), model.proteinACollected ),
                    new ProteinCaptureNode( transform.createTransformedShape( new ProteinB().getFullyGrownShape() ), Color.BLACK, new ProteinB().colorProperty.get(), model.proteinBCollected ),
                    new ProteinCaptureNode( transform.createTransformedShape( new ProteinC().getFullyGrownShape() ), Color.BLACK, new ProteinC().colorProperty.get(), model.proteinCCollected )
            ) );
        }
    }

    // Class that represents a node for collecting a single protein.
    private static class ProteinCaptureNode extends PNode {

        private static final Color FLASH_COLOR = new Color( 173, 255, 47 );
        private static final double SCALE_FOR_FLASH_NODE = 1.5;

        private ProteinCaptureNode( Shape proteinShape, final Color emptyColor, final Color fullBaseColor, Property<Integer> captureCountProperty ) {

            // Add the node that will flash when a capture occurs to make it
            // clear that something changed.
            Shape flashingNodeShape = AffineTransform.getScaleInstance( SCALE_FOR_FLASH_NODE, SCALE_FOR_FLASH_NODE ).createTransformedShape( proteinShape );
            final FlashingShapeNode captureIndicatorNode = new FlashingShapeNode( flashingNodeShape, FLASH_COLOR, 250, 250, 3 );
            addChild( captureIndicatorNode );

            // Add the node that will represent the spot where the protein can
            // be captured, which is a black shape (signifying emptiness)
            // until a protein is captured, then it changes to look filled in.
            final PPath captureAreaNode = new PPath( proteinShape );
            addChild( captureAreaNode );
            final Paint gradientPaint = MobileBiomoleculeNode.createGradientPaint( proteinShape, fullBaseColor );

            // Observe the capture indicator and set the state of the nodes
            // appropriately.
            captureCountProperty.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer captureCount ) {
                    if ( captureCount > 0 ) {
                        captureAreaNode.setPaint( gradientPaint );
                        captureIndicatorNode.flash();
                    }
                    else {
                        captureAreaNode.setPaint( emptyColor );
                    }
                }
            } );
        }
    }
}
