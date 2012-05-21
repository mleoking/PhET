// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents a block in the model.  In the model, a block is two-
 * dimensional, so its shape is represented by a rectangle.
 *
 * @author John Blanco
 */
public abstract class Block extends RectangularMovableModelElement implements ThermalEnergyContainer {

    // Height and width of all block surfaces, since it is a cube.
    public static final double SURFACE_WIDTH = 0.045; // In meters

    private final Property<HorizontalSurface> topSurface = new Property<HorizontalSurface>( null );
    private final Property<HorizontalSurface> bottomSurface = new Property<HorizontalSurface>( null );

    private final ObservableList<EnergyChunk> energyChunkList = new ObservableList<EnergyChunk>();
    protected final BooleanProperty energyChunksVisible;

    private double energy = 0; // In Joules.

    private final double specificHeat; // In J/gK
    private final double initialThermalEnergy;
    private final double mass; // In grams

    /**
     * Constructor.
     *
     * @param initialPosition
     * @param density             In g/cm^3
     * @param specificHeat        in J/g-K
     * @param energyChunksVisible
     */
    protected Block( ImmutableVector2D initialPosition, double density, double specificHeat, BooleanProperty energyChunksVisible ) {
        super( initialPosition );
        this.specificHeat = specificHeat;
        this.energyChunksVisible = energyChunksVisible;

        mass = Math.pow( SURFACE_WIDTH * 100, 3 ) * density;
        initialThermalEnergy = mass * specificHeat * EFACConstants.ROOM_TEMPERATURE;
        energy = initialThermalEnergy;

        // Update the top and bottom surfaces whenever the position changes.
        position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( final ImmutableVector2D immutableVector2D ) {
                updateTopSurfaceProperty();
                updateBottomSurfaceProperty();
            }
        } );

        // Update positions of energy chunks when this moves.
        position.addObserver( new ChangeObserver<ImmutableVector2D>() {
            public void update( ImmutableVector2D newPosition, ImmutableVector2D oldPosition ) {
                ImmutableVector2D movement = newPosition.getSubtractedInstance( oldPosition );
                for ( EnergyChunk energyChunk : energyChunkList ) {
                    energyChunk.position.set( energyChunk.position.get().getAddedInstance( movement ) );
                }
            }
        } );

        // TODO: Temp for prototyping.
        energyChunkList.add( new EnergyChunk( initialPosition.getAddedInstance( 0.02, 0.01 ), energyChunksVisible ) );
//        energyChunkList.add( new EnergyChunk( 0, 0 ) );
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

    public void changeEnergy( double deltaEnergy ) {
        energy += deltaEnergy;
    }

    public void exchangeEnergyWith( ThermalEnergyContainer energyContainer, double dt ) {
        double thermalContactLength = getThermalContactArea().getThermalContactLength( energyContainer.getThermalContactArea() );
        if ( thermalContactLength > 0 && Math.abs( energyContainer.getTemperature() - getTemperature() ) > TEMPERATURES_EQUAL_THRESHOLD ) {
            // Exchange energy between the this and the other energy container.
            // TODO: The following is a first attempt and likely to need much adjustment.
            double thermalEnergyGained = ( energyContainer.getTemperature() - getTemperature() ) * thermalContactLength * 0.01 * dt;
            changeEnergy( thermalEnergyGained );
            energyContainer.changeEnergy( -thermalEnergyGained );
            System.out.println( "thermalEnergyGained = " + thermalEnergyGained );

        }
    }

    public double getEnergy() {
        return energy;
    }

    public ThermalContactArea getThermalContactArea() {
        return new ThermalContactArea( getRect(), false );
    }

    public double getTemperature() {
        return energy / ( mass * specificHeat );
    }

    @Override public void reset() {
        super.reset();
        energy = initialThermalEnergy;
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

    public ObservableList<EnergyChunk> getEnergyChunkList() {
        return energyChunkList;
    }

    @Override public IUserComponentType getUserComponentType() {
        // Movable elements are considered sprites.
        return UserComponentTypes.sprite;
    }
}
