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
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * A PNode that represents a box where the user can collect protein molecules.
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

        // TODO: i18n
        PNode title = new HTMLNode( "<center>Your Protein<br>Collection:</center>", Color.BLACK, TITLE_FONT ) {{
            if ( getFullBoundsReference().getWidth() > MAX_CONTENT_WIDTH ) {
                // Scale title to fit.
                setScale( MAX_CONTENT_WIDTH / getFullBoundsReference().width );
            }
        }};

        PNode contents = new VBox(
                title,
                new CollectionCountIndicator( model )
        );
        addChild( new ControlPanelNode( contents, BACKGROUND_COLOR ) );
    }

    private static class CollectionCountIndicator extends PNode {
        private CollectionCountIndicator( ManualGeneExpressionModel model ) {
            model.collectedProteinCount.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer numCollectedProteins ) {
                    removeAllChildren();
                    VBox collectionCountIndicator = new VBox(
                            5,
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

    private static class ReadoutPText extends PText {
        private ReadoutPText( String text ) {
            super( text );
            setFont( READOUT_FONT );
        }
    }
}
