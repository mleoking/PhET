// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Block;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Piccolo node that represents a block in the view.  The blocks in the model
 * are two dimensional, and this class gives them some perspective in order to
 * make them appear to be three dimensional.
 *
 * @author John Blanco
 */
public class BlockNode extends PNode {

    private static final Font LABEL_FONT = new PhetFont( 32, false );

    public BlockNode( Block block, final ModelViewTransform mvt ) {

        // Extract the scale transform from the MVT so that we can separate the
        // shape from the position of the block.
        AffineTransform scaleTransform = AffineTransform.getScaleInstance( mvt.getTransform().getScaleX(), mvt.getTransform().getScaleY() );

        // Add the representation of the block.
        addChild( new PhetPPath( scaleTransform.createTransformedShape( Block.getRawShape() ), block.getColor(), new BasicStroke( 2 ), Color.LIGHT_GRAY ) );

        // Update the offset if and when the model position changes.
        block.position.addObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D position ) {
                setOffset( mvt.modelToView( position ) );
            }
        } );

        // Add the label.
        PNode label = new PText( block.getLabel() ) {{
            setFont( LABEL_FONT );
            if ( getFullBoundsReference().width >= mvt.modelToViewDeltaX( Block.FACE_SIZE * 0.9 ) ) {
                // Scale the label to fit on the face of the block.
                double scale = ( mvt.modelToViewDeltaX( Block.FACE_SIZE * 0.9 ) / getFullBoundsReference().width );
                setScale( scale );
            }
            double centerX = mvt.modelToViewDeltaX( Block.FACE_SIZE / 2 );
            double centerY = mvt.modelToViewDeltaY( Block.FACE_SIZE * 0.75 );
            centerFullBoundsOnPoint( centerX, centerY );

        }};
        addChild( label );


        // Add the cursor handler.
        addInputEventListener( new CursorHandler( CursorHandler.HAND ) );

        // Add the drag handler.
        addInputEventListener( new MovableElementDragHandler( block, this, mvt ) );
    }
}
