// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Class that represents a block in the model.  In the model, a block is two-
 * dimensional, so its shape is represented by a rectangle.
 *
 * @author John Blanco
 */
public abstract class Block extends UserMovableModelElement {

    // Height and width of the face, which is square.
    public static final double FACE_SIZE = 0.045; // In meters.
    private final Property<HorizontalSurface> topSurface = new Property<HorizontalSurface>( null );
    private final Property<HorizontalSurface> bottomSurface = new Property<HorizontalSurface>( null );

    public abstract Color getColor();

    /**
     * Get an image to use for the texture of the block when portrayed in the
     * view, if one should be used.  If no texture is used, the block will be
     * displayed with a solid color for the fill.
     *
     * @return An image to use for the texture, or null if no texture should
     *         be used.
     */
    public Image getTextureImage() {
        return null;
    }

    public abstract String getLabel();

    protected Block( ImmutableVector2D initialPosition ) {

        // Update the top an bottom surfaces whenever the position changes.
        position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( final ImmutableVector2D immutableVector2D ) {
                updateTopSurfaceProperty();
                updateBottomSurfaceProperty();
            }
        } );

        // Set the initial position.
        position.set( initialPosition );
    }

    public Property<HorizontalSurface> getTopSurfaceProperty() {
        return topSurface;
    }

    @Override public Property<HorizontalSurface> getBottomSurfaceProperty() {
        return bottomSurface;
    }

    /**
     * Get a rectangle the defines the current shape in model space.  By
     * convention for this simulation, the position is the middle of the
     * bottom of the block's defining rectangle.
     *
     * @return
     */
    public Rectangle2D getRect() {
        return new Rectangle2D.Double( position.get().getX() - FACE_SIZE / 2,
                                       position.get().getY(),
                                       FACE_SIZE,
                                       FACE_SIZE );
    }

    private void updateTopSurfaceProperty() {
        topSurface.set( new HorizontalSurface( new DoubleRange( getRect().getMinX(), getRect().getMaxX() ), getRect().getMaxY(), this ) );
    }

    private void updateBottomSurfaceProperty() {
        bottomSurface.set( new HorizontalSurface( new DoubleRange( getRect().getMinX(), getRect().getMaxX() ), getRect().getMinY(), this ) );
    }

    /**
     * Get the "raw shape" that should be used for depicting this block in the
     * view.  In this context, "raw" means that it is untranslated.  By
     * convention for this simulation, the point (0, 0) is the bottom center
     * of the block.
     *
     * @return
     */
    public static Shape getRawShape() {
        return new Rectangle2D.Double( -FACE_SIZE / 2, 0, FACE_SIZE, FACE_SIZE );
    }

    @Override public IUserComponentType getUserComponentType() {
        // Movable elements are considered sprites.
        return UserComponentTypes.sprite;
    }
}
