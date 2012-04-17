// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
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
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Protein;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ProteinA;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ProteinB;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ProteinC;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

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

    // Total number of protein types that can be collected.
    private static final double NUM_PROTEIN_TYPES = 3;

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

        PNode collectionCompleteNode = new PText( "Collection Complete!" ) {{
            setFont( new PhetFont( 20 ) );
            if ( getFullBoundsReference().width > MAX_CONTENT_WIDTH ) {
                // Scale to fit.
                setScale( MAX_CONTENT_WIDTH / getFullBoundsReference().width );
            }
        }};

        // Constructor.
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
            int numProteinTypesCollected = 0;
            if ( model.proteinACollected.get() > 0 ) {
                numProteinTypesCollected++;
            }
            if ( model.proteinBCollected.get() > 0 ) {
                numProteinTypesCollected++;
            }
            if ( model.proteinCCollected.get() > 0 ) {
                numProteinTypesCollected++;
            }
            removeAllChildren();
            PNode collectedQuantityIndicator = new VBox(
                    5,
                    // TODO: i18n.
                    new HBox( 4, new ReadoutPText( "You have: " ), new IntegerBox( numProteinTypesCollected ) ),
                    new ReadoutPText( "of 3 protein types." ) {{
                        setFont( READOUT_FONT );
                    }}

            );
            if ( collectedQuantityIndicator.getFullBoundsReference().width > MAX_CONTENT_WIDTH ) {
                // Scale to fit.
                collectedQuantityIndicator.setScale( MAX_CONTENT_WIDTH / getFullBoundsReference().width );
            }

            // Offset the collection complete indicator so that it will be
            // centered when it is shown.
            collectionCompleteNode.centerFullBoundsOnPoint( collectedQuantityIndicator.getFullBoundsReference().getCenterX(),
                                                            collectedQuantityIndicator.getFullBoundsReference().getCenterY() );

            // Add both nodes, so that the overall size of the node is
            // consistent, but only show one of them based upon whether the
            // collection is complete.
            addChild( collectedQuantityIndicator );
            addChild( collectionCompleteNode );

            // Set the visibility.
            collectedQuantityIndicator.setVisible( numProteinTypesCollected != NUM_PROTEIN_TYPES );
            collectionCompleteNode.setVisible( numProteinTypesCollected == NUM_PROTEIN_TYPES );
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
            AffineTransform transform = AffineTransform.getScaleInstance( scale, -scale );

            // Figure out the max dimensions of the various protein types so
            // that the capture nodes can be properly laid out.
            Dimension2D captureNodeBackgroundSize = new PDimension( 0, 0 );
            for ( Class<? extends Protein> proteinClass : Arrays.asList( ProteinA.class, ProteinB.class, ProteinC.class ) ) {
                try {
                    Protein protein = proteinClass.newInstance();
                    Rectangle2D proteinShapeBounds = transform.createTransformedShape( protein.getFullyGrownShape() ).getBounds2D();
                    captureNodeBackgroundSize.setSize( Math.max( proteinShapeBounds.getWidth() * ProteinCaptureNode.SCALE_FOR_FLASH_NODE, captureNodeBackgroundSize.getWidth() ),
                                                       Math.max( proteinShapeBounds.getHeight() * ProteinCaptureNode.SCALE_FOR_FLASH_NODE, captureNodeBackgroundSize.getHeight() ) );
                }
                catch ( Exception e ) {
                    System.out.println( "Exception thrown when instantiating protein, e = " + e );
                    e.printStackTrace();
                    continue;
                }
            }

            // Add the collection area, which is a set of collection nodes.
            addChild( new HBox(
                    0,
                    new ProteinCaptureNode( model, ProteinA.class, transform, captureNodeBackgroundSize ),
                    new ProteinCaptureNode( model, ProteinB.class, transform, captureNodeBackgroundSize ),
                    new ProteinCaptureNode( model, ProteinC.class, transform, captureNodeBackgroundSize )
            ) );
        }
    }

    // Class that represents a node for collecting a single protein and a
    // counter of the number of the protein type collected.
    private static class ProteinCaptureNode extends PNode {

        private static final Color FLASH_COLOR = new Color( 173, 255, 47 );
        protected static final double SCALE_FOR_FLASH_NODE = 1.5;

        // Tweak warning: This is used to make sure that the counters on
        // the various protein nodes end up horizontally aligned.  This will
        // need to be adjust if the protein shapes change a lot.
        private static final double VERTICAL_DISTANCE_TO_COUNT_NODE = 35;

        // Constructor.
        private ProteinCaptureNode( final ManualGeneExpressionModel model, final Class<? extends Protein> proteinClass, AffineTransform transform, Dimension2D size ) {

            Shape proteinShape = new Rectangle2D.Double( -10, -10, 20, 20 ); // Arbitrary initial shape.
            Color fullBaseColor = Color.PINK; // Arbitrary initial color.

            // Get the shape of the protein.
            try {
                Protein protein = proteinClass.newInstance();
                proteinShape = transform.createTransformedShape( protein.getFullyGrownShape() );
                fullBaseColor = protein.colorProperty.get();
            }
            catch ( InstantiationException e ) {
                e.printStackTrace();
            }
            catch ( IllegalAccessException e ) {
                e.printStackTrace();
            }

            // Add the background node.  This is invisible, and exists only to
            // made the node a specific size.
            addChild( new PhetPPath( new Rectangle2D.Double( -size.getWidth() / 2, -size.getHeight() / 2, size.getWidth(), size.getHeight() ),
                                     new Color( 0, 0, 0, 0 ) ) );

            // Add the node that will flash when a protein is created, stay lit
            // until the protein is captured, and turn off once it is captured.
            Shape flashingCaptureNodeShape = AffineTransform.getScaleInstance( SCALE_FOR_FLASH_NODE, SCALE_FOR_FLASH_NODE ).createTransformedShape( proteinShape );
            final FlashingShapeNode flashingCaptureNode = new FlashingShapeNode( flashingCaptureNodeShape, FLASH_COLOR, 350, 350, 4, false, true );
            addChild( flashingCaptureNode );

            // Add the node that will represent the spot where the protein can
            // be captured, which is a black shape (signifying emptiness)
            // until a protein is captured, then it changes to look filled in.
            final PPath captureAreaNode = new PPath( proteinShape );
            addChild( captureAreaNode );
            final Paint gradientPaint = MobileBiomoleculeNode.createGradientPaint( proteinShape, fullBaseColor );

            // Add the node that represents a count of the collected type.
            final PText countNode = new PText() {{
                setFont( new PhetFont( 18 ) );
            }};
            addChild( countNode );
            model.getCollectedCounterForProteinType( proteinClass ).addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer proteinCaptureCount ) {
                    countNode.setText( Integer.toString( proteinCaptureCount ) );
                    countNode.setOffset( captureAreaNode.getFullBoundsReference().getCenterX() - countNode.getFullBoundsReference().width / 2,
                                         captureAreaNode.getFullBoundsReference().getCenterY() + VERTICAL_DISTANCE_TO_COUNT_NODE );
                }
            } );

            // Watch for a protein node of the appropriate type to become
            // fully grown and, when it does, flash a node in order to signal
            // the user that the protein should be placed here.
            model.mobileBiomoleculeList.addElementAddedObserver( new VoidFunction1<MobileBiomolecule>() {
                public void apply( MobileBiomolecule biomolecule ) {
                    if ( biomolecule.getClass() == proteinClass ) {
                        Protein protein = (Protein) biomolecule;
                        protein.fullGrown.addObserver( new ChangeObserver<Boolean>() {
                            public void update( Boolean isFullyFormed, Boolean wasFullyFormed ) {
                                if ( isFullyFormed && !wasFullyFormed ) {
                                    flashingCaptureNode.startFlashing();
                                }
                            }
                        } );
                    }
                }
            } );

            // Get the capture count property for this protein.
            Property<Integer> captureCount = model.getCollectedCounterForProteinType( proteinClass );

            // Observe the capture indicator and set the state of the nodes
            // appropriately.
            captureCount.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer captureCount ) {
                    if ( captureCount > 0 ) {
                        captureAreaNode.setPaint( gradientPaint );
                    }
                    else {
                        // No proteins capture, so set to black to appear empty.
                        captureAreaNode.setPaint( Color.BLACK );
                    }
                }
            } );

            // Observe the biomolecules and make sure that if none of the
            // protein that this collects is in the model, the highlight is off.
            model.mobileBiomoleculeList.addElementRemovedObserver( new VoidFunction1<MobileBiomolecule>() {
                public void apply( MobileBiomolecule biomolecule ) {
                    if ( biomolecule.getClass() == proteinClass && model.getProteinCount( proteinClass ) == 0 ) {
                        // Make sure highlight is off.
                        flashingCaptureNode.forceFlashOff();
                    }
                }
            } );
        }
    }
}
