// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Class that represents a block in the view.  In the model, a block is two-
 * dimensional, so its shape is represented by a rectangle.
 *
 * @author John Blanco
 */
public abstract class Block extends UserMovableModelElement implements RestingSurfaceOwner {

    // Height and width of the face, which is square.
    public static final double FACE_SIZE = 0.05; // In meters.
    private final Property<HorizontalSurface> restingSurface;

    public abstract Color getColor();

    public abstract String getLabel();

    protected Block() {
        this.restingSurface = new Property<HorizontalSurface>( getTopSurface() );
        position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( final ImmutableVector2D immutableVector2D ) {
                restingSurface.set( getTopSurface() );
            }
        } );
//        this.restingSurface = new CompositeProperty<HorizontalSurface>( new Function0<HorizontalSurface>() {
//            @Override public HorizontalSurface apply() {
//                return new HorizontalSurface( new DoubleRange( getRect().getMinX(), getRect().getMaxX() ), getRect().getMaxY() );
//            }
//        }, position );
    }

    public HorizontalSurface getTopSurface() {
        return new HorizontalSurface( new DoubleRange( getRect().getMinX(), getRect().getMaxX() ), getRect().getMaxY() );
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

    public ObservableProperty<HorizontalSurface> getRestingSurface() {
        return restingSurface;
    }

    @Override public HorizontalSurface getBottomSurface() {
        return new HorizontalSurface( new DoubleRange( getRect().getMinX(), getRect().getMaxX() ), getRect().getMinY() );
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
