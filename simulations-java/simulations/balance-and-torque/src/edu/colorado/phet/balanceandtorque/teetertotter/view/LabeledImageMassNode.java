// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.LabeledImageMass;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author John Blanco
 */
public class LabeledImageMassNode extends ImageMassNode {
    private static final double INSET_PROPORTION = 0.2;

    public LabeledImageMassNode( final ModelViewTransform mvt, final LabeledImageMass mass, PhetPCanvas canvas, BooleanProperty massLabelVisibilityProperty ) {
        super( mvt, mass, canvas, massLabelVisibilityProperty );
        double inset = getFullBoundsReference().width * INSET_PROPORTION;
        // Create the label.
        TestLabelNode label = new TestLabelNode( mass.getLabelText() );
        // Scale the label to fit.
        double widthScale = ( getFullBoundsReference().width - ( 2 * inset ) ) / label.getFullBoundsReference().width;
        double heightScale = ( getFullBoundsReference().height - ( 2 * inset ) ) / label.getFullBoundsReference().height;
        label.setScale( Math.min( widthScale, heightScale ) );
        label.centerFullBoundsOnPoint( getFullBoundsReference().getWidth() / 2, getFullBoundsReference().getHeight() * 0.6 );
        // Add the label as a child.
        addChild( label );
    }

    private static class TestLabelNode extends PNode {
        private static final Font FONT = new PhetFont( 14, true );

        private TestLabelNode( String text ) {
            PText textNode = new PText( text ) {{
                setFont( FONT );
                setTextPaint( Color.BLACK );
            }};
            double dimension = Math.max( textNode.getFullBoundsReference().width,
                                         textNode.getFullBoundsReference().height );
            RoundRectangle2D.Double backgroundShape = new RoundRectangle2D.Double( 0,
                                                                                   0,
                                                                                   dimension,
                                                                                   dimension,
                                                                                   3,
                                                                                   3 );
            PNode backgroundNode = new PhetPPath( backgroundShape,
                                                  Color.WHITE,
                                                  new BasicStroke( 0.5f ),
                                                  Color.BLACK );
            addChild( backgroundNode );
            textNode.centerFullBoundsOnPoint( backgroundShape.getCenterX(), backgroundShape.getCenterY() );
            addChild( textNode );
        }
    }
}
