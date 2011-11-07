// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

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
    private static final double MAX_CONTENT_WIDTH = 200;

    // Attributes of various aspects of the box.
    private static final Font TITLE_FONT = new PhetFont( 20, true );
    private static final Font READOUT_FONT = new PhetFont( 16 );
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
        PNode collectionArea = new ProteinCollectionArea( mvt );
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
        private CollectionCountIndicator( ManualGeneExpressionModel model ) {
            model.collectedProteinCount.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer numCollectedProteins ) {
                    removeAllChildren();
                    VBox collectionCountIndicator = new VBox(
                            5,
                            // TODO: i18n.
                            new HBox( 4, new ReadoutPText( "You have: " ), new IntegerBox( numCollectedProteins ) ),
                            new ReadoutPText( "of 3 proteins." ) {{
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
            } );
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

    private static class ProteinCollectionArea extends PNode {
        private ProteinCollectionArea( ModelViewTransform mvt ) {
            addChild( new HBox(
                    new PhetPPath( mvt.modelToView( new ProteinA().getFullyGrownShape() ), Color.BLACK ),
                    new PhetPPath( mvt.modelToView( new ProteinB().getFullyGrownShape() ), Color.BLACK ),
                    new PhetPPath( mvt.modelToView( new ProteinC().getFullyGrownShape() ), Color.BLACK )
            ) );
        }
    }
}
