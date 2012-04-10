// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
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
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Color OUTLINE_STROKE_COLOR = Color.DARK_GRAY;
    private static final double PERSPECTIVE_ANGLE = Math.PI / 4; // Positive is counterclockwise, a value of 0 produces a non-skewed rectangle.
    private static final double PERSPECTIVE_EDGE_PROPORTION = 0.33;

    private static final boolean SHOW_2D_REPRESENTATION = true;

    public BlockNode( Block block, final ModelViewTransform mvt ) {

        // Extract the scale transform from the MVT so that we can separate the
        // shape from the position of the block.
        AffineTransform scaleTransform = AffineTransform.getScaleInstance( mvt.getTransform().getScaleX(), mvt.getTransform().getScaleY() );

        // Add the representation of the block.  This is projected into a 3D
        // appearance based on the size and position of the 2D block in model
        // space.  It is projected and offset both forward and backward so that
        // the model position is in the center of the 3D shape.
        double perspectiveEdgeSize = mvt.modelToViewDeltaX( block.getRect().getWidth() * PERSPECTIVE_EDGE_PROPORTION );
        ImmutableVector2D forwardPerspectiveOffset = new ImmutableVector2D( -perspectiveEdgeSize / 2, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE );
        ImmutableVector2D backwardPerspectiveOffset = new ImmutableVector2D( perspectiveEdgeSize / 2, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE );
        Rectangle2D blockRectInViewCoords = scaleTransform.createTransformedShape( Block.getRawShape() ).getBounds2D();
        ImmutableVector2D lowerLeftCornerOfFace = new ImmutableVector2D( blockRectInViewCoords.getMinX(), blockRectInViewCoords.getMaxY() ).getAddedInstance( forwardPerspectiveOffset );
        ImmutableVector2D lowerRightCornerOfFace = new ImmutableVector2D( blockRectInViewCoords.getMaxX(), blockRectInViewCoords.getMaxY() ).getAddedInstance( forwardPerspectiveOffset );
        ImmutableVector2D upperRightCornerOfFace = new ImmutableVector2D( blockRectInViewCoords.getMaxX(), blockRectInViewCoords.getMinY() ).getAddedInstance( forwardPerspectiveOffset );
        ImmutableVector2D upperLeftCornerOfFace = new ImmutableVector2D( blockRectInViewCoords.getMinX(), blockRectInViewCoords.getMinY() ).getAddedInstance( forwardPerspectiveOffset );
        ImmutableVector2D upperBackLeftCorner = new ImmutableVector2D( blockRectInViewCoords.getMinX(), blockRectInViewCoords.getMinY() ).getAddedInstance( backwardPerspectiveOffset );
        ImmutableVector2D upperBackRightCorner = new ImmutableVector2D( blockRectInViewCoords.getMaxX(), blockRectInViewCoords.getMinY() ).getAddedInstance( backwardPerspectiveOffset );
        ImmutableVector2D lowerBackRightCorner = new ImmutableVector2D( blockRectInViewCoords.getMaxX(), blockRectInViewCoords.getMaxY() ).getAddedInstance( backwardPerspectiveOffset );
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( lowerLeftCornerOfFace );
        path.lineTo( lowerRightCornerOfFace );
        path.lineTo( upperRightCornerOfFace );
        path.lineTo( upperLeftCornerOfFace );
        path.lineTo( lowerLeftCornerOfFace );
        path.moveTo( upperLeftCornerOfFace );
        path.lineTo( upperBackLeftCorner );
        path.lineTo( upperBackRightCorner );
        path.lineTo( upperRightCornerOfFace );
        path.moveTo( upperBackRightCorner );
        path.lineTo( lowerBackRightCorner );
        path.lineTo( lowerRightCornerOfFace );
        path.lineTo( upperRightCornerOfFace );
        addChild( new PhetPPath( path.getGeneralPath(), block.getColor(), OUTLINE_STROKE, OUTLINE_STROKE_COLOR ) );

        if ( SHOW_2D_REPRESENTATION ) {
            addChild( new PhetPPath( scaleTransform.createTransformedShape( Block.getRawShape() ), new BasicStroke( 1 ), Color.RED ) );
        }

        // Update the offset if and when the model position changes.
        block.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D position ) {
                setOffset( mvt.modelToView( position ).toPoint2D() );
            }
        } );

        // Add and position the label.
        PText label = new PText( block.getLabel() );
        label.setFont( LABEL_FONT );
        if ( label.getFullBoundsReference().width >= mvt.modelToViewDeltaX( Block.FACE_SIZE * 0.9 ) ) {
            // Scale the label to fit on the face of the block.
            double scale = ( mvt.modelToViewDeltaX( Block.FACE_SIZE * 0.9 ) / getFullBoundsReference().width );
            label.setScale( scale );
        }
        double labelCenterX = ( upperLeftCornerOfFace.getX() + upperRightCornerOfFace.getX() ) / 2;
        double labelCenterY = ( upperLeftCornerOfFace.getY() + label.getFullBoundsReference().height );
        label.centerFullBoundsOnPoint( labelCenterX, labelCenterY );

        addChild( label );


        // Add the cursor handler.
        addInputEventListener( new CursorHandler( CursorHandler.HAND ) );

        // Add the drag handler.
        addInputEventListener( new MovableElementDragHandler( block, this, mvt ) );
    }
}
