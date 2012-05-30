// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model element that represents a beaker in the model.
 *
 * @author John Blanco
 */
public class Beaker extends RectangularThermalMovableModelElement {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double WIDTH = 0.085; // In meters.
    private static final double HEIGHT = WIDTH * 1.1;
    private static final double MATERIAL_THICKNESS = 0.001; // In meters.

    // Constants that control the nature of the fluid in the beaker.
    private static final double FLUID_SPECIFIC_HEAT = 4186; // In J/kg-K, source = design document.
    private static final double FLUID_DENSITY = 1000.0; // In kg/m^3, source = design document (and common knowledge).
    private static final double NON_DISPLACED_FLUID_LEVEL = 0.5;
    private static final double FLUID_VOLUME = Math.PI * Math.pow( WIDTH / 2, 2 ) * ( NON_DISPLACED_FLUID_LEVEL * HEIGHT ); // In m^3
    private static final double FLUID_MASS = FLUID_VOLUME * FLUID_DENSITY; // In kg

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Property that is used to control the amount of fluid in the beaker.
    public final Property<Double> fluidLevel = new Property<Double>( NON_DISPLACED_FLUID_LEVEL );

    // Top surface, which is the surface upon which other model elements can
    // sit.  For the beaker, this is only slightly above the bottom surface.
    private final Property<HorizontalSurface> topSurface = new Property<HorizontalSurface>( null );

    // Bottom surface, which is the surface that touches the ground, or sits
    // on a burner, etc.
    private final Property<HorizontalSurface> bottomSurface = new Property<HorizontalSurface>( null );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param clock
     * @param initialPosition The initial position in model space.  This is
     */
    public Beaker( ConstantDtClock clock, ImmutableVector2D initialPosition, BooleanProperty energyChunksVisible ) {
        super( clock, initialPosition, FLUID_MASS, FLUID_SPECIFIC_HEAT, energyChunksVisible );

        // Update the top and bottom surfaces whenever the position changes.
        position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( final ImmutableVector2D immutableVector2D ) {
                updateSurfaces();
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Get a rectangle that defines the outline of the burner.  In the model,
     * the burner is essentially a 2D rectangle.
     *
     * @return
     */
    public Rectangle2D getRect() {
        return new Rectangle2D.Double( position.get().getX() - WIDTH / 2,
                                       position.get().getY(),
                                       WIDTH,
                                       HEIGHT );
    }

    @Override public Dimension2D getSize() {
        return new PDimension( WIDTH, HEIGHT );
    }

    /**
     * Update the fluid level in the beaker based upon any displacement that
     * could be caused by the given rectangles.  This algorithm is strictly
     * two dimensional, even though displacement is more of the 3D concept.
     *
     * @param potentiallyDisplacingRectangles
     *
     */
    public void updateFluidLevel( List<Rectangle2D> potentiallyDisplacingRectangles ) {

        // Calculate the amount of overlap between the rectangle that
        // represents the fluid and the displacing rectangles.
        Rectangle2D fluidRectangle = new Rectangle2D.Double( getRect().getX(),
                                                             getRect().getY(),
                                                             WIDTH,
                                                             HEIGHT * fluidLevel.get() );
        double overlappingArea = 0;
        for ( Rectangle2D rectangle2D : potentiallyDisplacingRectangles ) {
            if ( rectangle2D.intersects( fluidRectangle ) ) {
                Rectangle2D intersection = rectangle2D.createIntersection( fluidRectangle );
                overlappingArea += intersection.getWidth() * intersection.getHeight();
            }
        }

        // Map the overlap to a new fluid height.  The scaling factor was
        // empirically determined to look good.
        double newFluidLevel = Math.min( NON_DISPLACED_FLUID_LEVEL + overlappingArea * 120, 1 );
        fluidLevel.set( newFluidLevel );
    }

    /**
     * Get the untranslated rectangle that defines the shape of the beaker.
     *
     * @return
     */
    public static final Rectangle2D getRawOutlineRect() {
        return new Rectangle2D.Double( -WIDTH / 2, 0, WIDTH, HEIGHT );
    }

    private void updateSurfaces() {
        topSurface.set( new HorizontalSurface( new DoubleRange( getRect().getMinX(), getRect().getMaxX() ),
                                               getRect().getMinY() + MATERIAL_THICKNESS,
                                               this ) );
        bottomSurface.set( new HorizontalSurface( new DoubleRange( getRect().getMinX(), getRect().getMaxX() ),
                                                  getRect().getMinY(),
                                                  this ) );
    }

    @Override public Property<HorizontalSurface> getTopSurfaceProperty() {
        return topSurface;
    }

    @Override public Property<HorizontalSurface> getBottomSurfaceProperty() {
        return bottomSurface;
    }

    // TODO: May not need to override base class for this.  Not sure yet.
    public void exchangeEnergyWith( ThermalEnergyContainer energyContainer, double dt ) {
        double thermalContactLength = getThermalContactArea().getThermalContactLength( energyContainer.getThermalContactArea() );
        if ( thermalContactLength > 0 && Math.abs( energyContainer.getTemperature() - getTemperature() ) > TEMPERATURES_EQUAL_THRESHOLD ) {
            // Exchange energy between the this and the other energy container.
            // TODO: The following is a first attempt and likely to need much adjustment.
            double thermalEnergyGained = ( energyContainer.getTemperature() - getTemperature() ) * thermalContactLength * 1000 * dt;
            changeEnergy( thermalEnergyGained );
            energyContainer.changeEnergy( -thermalEnergyGained );
            System.out.println( "thermalEnergyGained = " + thermalEnergyGained );
        }
    }

    // Had to override due to construction order issues.
    @Override protected void addInitialEnergyChunks() {
        int targetNumChunks = calculateNeededNumEnergyChunks();
        Rectangle2D energyChunkBounds = new Rectangle2D.Double( position.get().getX() - WIDTH / 2,
                                                                position.get().getY(),
                                                                WIDTH,
                                                                HEIGHT * NON_DISPLACED_FLUID_LEVEL );
        while ( targetNumChunks != getEnergyChunkList().size() ) {
            // Add a chunk at a random location in the beaker.
            addEnergyChunk( new EnergyChunk( clock, EnergyChunkDistributor.generateRandomLocation( energyChunkBounds ), energyChunksVisible, false ) );
        }
    }

    public ThermalContactArea getThermalContactArea() {
        return new ThermalContactArea( new Rectangle2D.Double( position.get().getX() - WIDTH / 2,
                                                               position.get().getY(),
                                                               WIDTH,
                                                               HEIGHT * fluidLevel.get() ), true );
    }

    @Override public IUserComponent getUserComponent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public IUserComponentType getUserComponentType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
