// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents a block in the model.  In the model, a block is two-
 * dimensional, so its shape is represented by a rectangle.
 *
 * @author John Blanco
 */
public abstract class Block extends RectangularThermalMovableModelElement {

    // Height and width of all block surfaces, since it is a cube.
    public static final double SURFACE_WIDTH = 0.045; // In meters

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
    protected Block( ConstantDtClock clock, ImmutableVector2D initialPosition, double density, double specificHeat, BooleanProperty energyChunksVisible ) {
        super( clock, initialPosition, Math.pow( SURFACE_WIDTH, 3 ) * density, specificHeat, energyChunksVisible );

        // Update the top and bottom surfaces whenever the position changes.
        position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( final ImmutableVector2D immutableVector2D ) {
                updateTopSurfaceProperty();
                updateBottomSurfaceProperty();
            }
        } );
    }

    @Override public Dimension2D getSize() {
        return new PDimension( SURFACE_WIDTH, SURFACE_WIDTH );
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

    // TODO: May be able to move this up to parent class.  Would be shared with beaker, though, so needs investigating.
    public void exchangeEnergyWith( ThermalEnergyContainer otherEnergyContainer, double dt ) {
        double thermalContactLength = getThermalContactArea().getThermalContactLength( otherEnergyContainer.getThermalContactArea() );
        if ( thermalContactLength > 0 ) {

            if ( Math.abs( otherEnergyContainer.getTemperature() - getTemperature() ) > TEMPERATURES_EQUAL_THRESHOLD ) {
                // Exchange energy between the this and the other energy container.
                // TODO: The following is a first attempt and likely to need much adjustment.  Transfer constant was empirically chosen
                double thermalEnergyGained = ( otherEnergyContainer.getTemperature() - getTemperature() ) * thermalContactLength * 1000 * dt;
                changeEnergy( thermalEnergyGained );
                otherEnergyContainer.changeEnergy( -thermalEnergyGained );
            }

            // Exchange energy chunks.
            if ( otherEnergyContainer.needsEnergyChunk() && hasExcessEnergyChunks() ) {
                // The other energy container needs an energy chunk, and this
                // container has excess, so transfer one.
                otherEnergyContainer.addEnergyChunk( removeEnergyChunk() );
            }
            else if ( otherEnergyContainer.hasExcessEnergyChunks() && needsEnergyChunk() ) {
                // This energy container needs a chunk, and the other has
                // excess, so take one.
                energyChunkList.add( otherEnergyContainer.removeEnergyChunk() );
            }
        }
    }

    public ThermalContactArea getThermalContactArea() {
        return new ThermalContactArea( getRect(), false );
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

    @Override public IUserComponentType getUserComponentType() {
        // Movable elements are considered sprites.
        return UserComponentTypes.sprite;
    }
}
