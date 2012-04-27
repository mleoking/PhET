// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Shape;
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
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Piccolo node that represents a block in the view.  The blocks in the model
 * are 2D, and this class gives them some perspective in order to make them
 * appear to be 3D.
 *
 * @author John Blanco
 */
public class BlockNode extends PComposite {

    private static final Font LABEL_FONT = new PhetFont( 32, false );
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Color OUTLINE_STROKE_COLOR = Color.DARK_GRAY;
    private static final double PERSPECTIVE_ANGLE = Math.PI / 4; // Positive is counterclockwise, a value of 0 produces a non-skewed rectangle.
    private static final double PERSPECTIVE_EDGE_PROPORTION = 0.33;

    private static final boolean SHOW_2D_REPRESENTATION = false;

    public BlockNode( Block block, final ModelViewTransform mvt ) {

        // Extract the scale transform from the MVT so that we can separate the
        // shape from the position of the block.
        AffineTransform scaleTransform = AffineTransform.getScaleInstance( mvt.getTransform().getScaleX(), mvt.getTransform().getScaleY() );

        // Create the shape for the front of the block.
        Rectangle2D blockRectInViewCoords = scaleTransform.createTransformedShape( Block.getRawShape() ).getBounds2D();
        double perspectiveEdgeSize = mvt.modelToViewDeltaX( block.getRect().getWidth() * PERSPECTIVE_EDGE_PROPORTION );
        ImmutableVector2D blockFaceOffset = new ImmutableVector2D( -perspectiveEdgeSize / 2, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE );
        ImmutableVector2D backCornersOffset = new ImmutableVector2D( perspectiveEdgeSize, 0 ).getRotatedInstance( -PERSPECTIVE_ANGLE );
        ImmutableVector2D lowerLeftCornerOfFace = new ImmutableVector2D( blockRectInViewCoords.getMinX(), blockRectInViewCoords.getMaxY() ).getAddedInstance( blockFaceOffset );
        ImmutableVector2D lowerRightCornerOfFace = new ImmutableVector2D( blockRectInViewCoords.getMaxX(), blockRectInViewCoords.getMaxY() ).getAddedInstance( blockFaceOffset );
        ImmutableVector2D upperRightCornerOfFace = new ImmutableVector2D( blockRectInViewCoords.getMaxX(), blockRectInViewCoords.getMinY() ).getAddedInstance( blockFaceOffset );
        ImmutableVector2D upperLeftCornerOfFace = new ImmutableVector2D( blockRectInViewCoords.getMinX(), blockRectInViewCoords.getMinY() ).getAddedInstance( blockFaceOffset );
        Shape blockFaceShape = new Rectangle2D.Double( lowerLeftCornerOfFace.getX(),
                                                       upperLeftCornerOfFace.getY(),
                                                       blockRectInViewCoords.getWidth(),
                                                       blockRectInViewCoords.getHeight() );

        // Create the shape of the top of the block.
        ImmutableVector2D backLeftCornerOfTop = upperLeftCornerOfFace.getAddedInstance( backCornersOffset );
        ImmutableVector2D backRightCornerOfTop = upperRightCornerOfFace.getAddedInstance( backCornersOffset );
        DoubleGeneralPath blockTopPath = new DoubleGeneralPath();
        blockTopPath.moveTo( upperLeftCornerOfFace );
        blockTopPath.lineTo( upperRightCornerOfFace );
        blockTopPath.lineTo( backRightCornerOfTop );
        blockTopPath.lineTo( backLeftCornerOfTop );
        blockTopPath.lineTo( upperLeftCornerOfFace );
        Shape blockTopShape = blockTopPath.getGeneralPath();

        // Create the shape of the side of the block.
        ImmutableVector2D upperBackCornerOfSide = upperRightCornerOfFace.getAddedInstance( backCornersOffset );
        ImmutableVector2D lowerBackCornerOfSide = lowerRightCornerOfFace.getAddedInstance( backCornersOffset );
        DoubleGeneralPath blockSidePath = new DoubleGeneralPath();
        blockSidePath.moveTo( upperRightCornerOfFace );
        blockSidePath.lineTo( lowerRightCornerOfFace );
        blockSidePath.lineTo( lowerBackCornerOfSide );
        blockSidePath.lineTo( upperBackCornerOfSide );
        blockSidePath.lineTo( upperRightCornerOfFace );
        Shape blockSideShape = blockSidePath.getGeneralPath();

        // Add the shapes that comprise the block representation.
        addChild( createSurface( blockFaceShape, block.getColor(), block.getFrontTextureImage() ) );
        addChild( createSurface( blockTopShape, block.getColor(), block.getTopTextureImage() ) );
        addChild( createSurface( blockSideShape, block.getColor(), block.getSideTextureImage() ) );

        if ( SHOW_2D_REPRESENTATION ) {
            addChild( new PhetPPath( scaleTransform.createTransformedShape( Block.getRawShape() ), new BasicStroke( 1 ), Color.RED ) );
        }

        // Update the offset if and when the model position changes.
        block.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D position ) {
                setOffset( mvt.modelToView( position ).toPoint2D() );
            }
        } );

        // Position and add the label.
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

    /*
    * Convenience method to avoid code duplication.  Adds a node of the given
    * shape, color, and texture (if a texture is specified).
    */
    private PNode createSurface( Shape blockFaceShape, Color fillColor, Image textureImage ) {

        PNode root = new PNode();

        // Add the filled shape.  Note that in cases where a texture is
        // provided, this may end up getting partially or entirely covered up.
        root.addChild( new PhetPPath( blockFaceShape, fillColor ) );

        if ( textureImage != null ) {

            // Add the clipped texture.
            PClip clippedTexture = new PClip();
            clippedTexture.setPathTo( blockFaceShape );
            PImage texture = new PImage( textureImage );

            // Scale up the texture image if needed.
            double textureScale = 1;
            if ( texture.getFullBoundsReference().width < clippedTexture.getFullBoundsReference().width ) {
                textureScale = clippedTexture.getFullBoundsReference().width / texture.getFullBoundsReference().width;
            }
            if ( texture.getFullBoundsReference().height < clippedTexture.getFullBoundsReference().height ) {
                textureScale = Math.max( clippedTexture.getFullBoundsReference().height / texture.getFullBoundsReference().height, textureScale );
            }
            texture.setScale( textureScale );

            // Add the texture to the clip node in order to clip it.
            texture.setOffset( clippedTexture.getFullBoundsReference().getMinX(), clippedTexture.getFullBoundsReference().getMinY() );
            clippedTexture.addChild( texture );
            root.addChild( clippedTexture );
        }

        // Add the outlined shape so that edges are visible.
        root.addChild( new PhetPPath( blockFaceShape, OUTLINE_STROKE, OUTLINE_STROKE_COLOR ) );

        return root;
    }
}
