// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

/**
 * Class that represents a block in the model.  In the model, a block is two-
 * dimensional, so its shape is represented by a rectangle.
 *
 * @author John Blanco
 */
public abstract class Block extends RectangularThermalMovableModelElement {

    // Height and width of all block surfaces, since it is a cube.
    public static final double SURFACE_WIDTH = 0.045; // In meters

    // Number of slices where energy chunks may be placed.
    private static final int NUM_ENERGY_CHUNK_SLICES = 4;

    // Surfaces used for stacking and thermal interaction.
    private final Property<HorizontalSurface> topSurface = new Property<HorizontalSurface>( null );
    private final Property<HorizontalSurface> bottomSurface = new Property<HorizontalSurface>( null );

    /**
     * Constructor.
     *
     * @param clock
     * @param initialPosition
     * @param density             In kg/m^3
     * @param specificHeat        in J/kg-K
     * @param energyChunksVisible
     */
    protected Block( ConstantDtClock clock, Vector2D initialPosition, double density, double specificHeat, BooleanProperty energyChunksVisible ) {
        super( clock, initialPosition, SURFACE_WIDTH, SURFACE_WIDTH, Math.pow( SURFACE_WIDTH, 3 ) * density, specificHeat, energyChunksVisible );

        // Update the top and bottom surfaces whenever the position changes.
        position.addObserver( new VoidFunction1<Vector2D>() {
            public void apply( final Vector2D immutableVector2D ) {
                updateTopSurfaceProperty();
                updateBottomSurfaceProperty();
            }
        } );
    }

    public abstract Color getColor();

    /**
     * Get an image to use for the texture of the front of the block when
     * portrayed in the view, if one should be used.
     *
     * @return An image to use for the texture, or null if no texture should
     *         be used.
     */
    public Image getFrontTextureImage() {
        return null;
    }

    /**
     * Get an image to use for the texture of the top of the block when
     * portrayed in the view, if one should be used.
     *
     * @return An image to use for the texture, or null if no texture should
     *         be used.
     */
    public Image getTopTextureImage() {
        return null;
    }

    /**
     * Get an image to use for the texture of the side of the block when
     * portrayed in the view, if one should be used.
     *
     * @return An image to use for the texture, or null if no texture should
     *         be used.
     */
    public Image getSideTextureImage() {
        return null;
    }

    public abstract String getLabel();

    public Property<HorizontalSurface> getTopSurfaceProperty() {
        return topSurface;
    }

    @Override public Property<HorizontalSurface> getBottomSurfaceProperty() {
        return bottomSurface;
    }

    public ThermalContactArea getThermalContactArea() {
        return new ThermalContactArea( getRect(), false );
    }

    @Override protected void addEnergyChunkSlices() {
        // The slices for the block are intended to match the projection used in the view.
        Vector2D projectionToFront = EFACConstants.MAP_Z_TO_XY_OFFSET.apply( SURFACE_WIDTH / 2 );
        for ( int i = 0; i < NUM_ENERGY_CHUNK_SLICES; i++ ) {
            Vector2D projectionOffsetVector = EFACConstants.MAP_Z_TO_XY_OFFSET.apply( i * ( -SURFACE_WIDTH / ( NUM_ENERGY_CHUNK_SLICES - 1 ) ) );
            AffineTransform transform = AffineTransform.getTranslateInstance( projectionToFront.getX() + projectionOffsetVector.getX(),
                                                                              projectionToFront.getY() + projectionOffsetVector.getY() );
            slices.add( new EnergyChunkContainerSlice( transform.createTransformedShape( getRect() ), -i * ( SURFACE_WIDTH / NUM_ENERGY_CHUNK_SLICES ), position ) );
        }
    }

    /**
     * Get a rectangle the defines the current shape in model space.  By
     * convention for this simulation, the position is the middle of the
     * bottom of the block's defining rectangle.
     *
     * @return
     */
    public Rectangle2D getRect() {
        return new Rectangle2D.Double( position.get().getX() - SURFACE_WIDTH / 2,
                                       position.get().getY(),
                                       SURFACE_WIDTH,
                                       SURFACE_WIDTH );
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
        return new Rectangle2D.Double( -SURFACE_WIDTH / 2, 0, SURFACE_WIDTH, SURFACE_WIDTH );
    }
}
