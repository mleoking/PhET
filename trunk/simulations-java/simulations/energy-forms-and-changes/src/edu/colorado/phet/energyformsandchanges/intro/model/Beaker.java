// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

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
    private static final int NUM_SLICES = 6;
    private static final Random RAND = new Random( 1 ); // This is seeded for consistent initial energy chunk distribution.

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

    // List of model elements that may be moved into this beaker.
    private final List<RectangularThermalMovableModelElement> potentiallyContainedElements;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param clock
     * @param initialPosition The initial position in model space.  This is
     */
    public Beaker( ConstantDtClock clock, Vector2D initialPosition, List<RectangularThermalMovableModelElement> potentiallyContainedElements, BooleanProperty energyChunksVisible ) {
        super( clock, initialPosition, WIDTH, HEIGHT * NON_DISPLACED_FLUID_LEVEL, FLUID_MASS, FLUID_SPECIFIC_HEAT, energyChunksVisible );
        this.potentiallyContainedElements = potentiallyContainedElements;

        // Update the top and bottom surfaces whenever the position changes.
        position.addObserver( new VoidFunction1<Vector2D>() {
            public void apply( final Vector2D immutableVector2D ) {
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
        double proportionateIncrease = newFluidLevel / fluidLevel.get();
        fluidLevel.set( newFluidLevel );

        // Update the shapes of the energy chunk slices.
        for ( EnergyChunkContainerSlice slice : slices ) {
            Shape originalShape = slice.getShape();
            Shape expandedOrCompressedShape = AffineTransform.getScaleInstance( 1, proportionateIncrease ).createTransformedShape( originalShape );
            AffineTransform translationTransform = AffineTransform.getTranslateInstance( originalShape.getBounds2D().getX() - expandedOrCompressedShape.getBounds2D().getX(),
                                                                                         originalShape.getBounds2D().getY() - expandedOrCompressedShape.getBounds2D().getY() );
            slice.setShape( translationTransform.createTransformedShape( expandedOrCompressedShape ) );
        }
    }

    /**
     * Get the untranslated rectangle that defines the shape of the beaker.
     *
     * @return
     */
    public static Rectangle2D getRawOutlineRect() {
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

    @Override protected void addInitialEnergyChunks() {
        for ( EnergyChunkContainerSlice slice : slices ) {
            slice.energyChunkList.clear();
        }
        int targetNumChunks = EFACConstants.ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy );
        Rectangle2D initialChunkBounds = getSliceBounds();
        while ( getNumEnergyChunks() < targetNumChunks ) {
            // Add a chunk at a random location in the beaker.
            addEnergyChunkToNextSlice( new EnergyChunk( clock, EnergyType.THERMAL, EnergyChunkDistributor.generateRandomLocation( initialChunkBounds ), energyChunksVisible, false ) );
        }
    }

    @Override protected void addEnergyChunkToNextSlice( EnergyChunk ec ) {
        double totalSliceArea = 0;
        for ( EnergyChunkContainerSlice slice : slices ) {
            totalSliceArea += slice.getShape().getBounds2D().getWidth() * slice.getShape().getBounds2D().getHeight();
        }
        double sliceSelectionValue = RAND.nextDouble();
        EnergyChunkContainerSlice chosenSlice = slices.get( 0 );
        double accumulatedArea = 0;
        for ( EnergyChunkContainerSlice slice : slices ) {
            accumulatedArea += slice.getShape().getBounds2D().getWidth() * slice.getShape().getBounds2D().getHeight();
            if ( accumulatedArea / totalSliceArea >= sliceSelectionValue ) {
                chosenSlice = slice;
                break;
            }
        }
        chosenSlice.addEnergyChunk( ec );
    }

    public ThermalContactArea getThermalContactArea() {
        return new ThermalContactArea( new Rectangle2D.Double( position.get().getX() - WIDTH / 2,
                                                               position.get().getY(),
                                                               WIDTH,
                                                               HEIGHT * fluidLevel.get() ), true );
    }

    @Override protected void addEnergyChunkSlices() {
        assert slices.size() == 0; // Check that his has not been already called.
        final Rectangle2D fluidRect = new Rectangle2D.Double( position.get().getX() - WIDTH / 2,
                                                              position.get().getY(),
                                                              WIDTH,
                                                              HEIGHT * NON_DISPLACED_FLUID_LEVEL );
        double widthYProjection = Math.abs( WIDTH * EFACConstants.Z_TO_Y_OFFSET_MULTIPLIER );
        for ( int i = 0; i < NUM_SLICES; i++ ) {
            double proportion = ( i + 1 ) * ( 1 / (double) ( NUM_SLICES + 1 ) );
            DoubleGeneralPath slicePath = new DoubleGeneralPath();
            {
                // The slice width is calculated to fit into the 3D projection.
                // It uses an exponential function that is shifted in order to
                // yield width value proportional to position in Z-space.
                double sliceWidth = ( -Math.pow( ( 2 * proportion - 1 ), 2 ) + 1 ) * fluidRect.getWidth();
                double bottomY = fluidRect.getMinY() - ( widthYProjection / 2 ) + ( proportion * widthYProjection );
                double topY = bottomY + fluidRect.getHeight();
                double centerX = fluidRect.getCenterX();
                double controlPointYOffset = ( bottomY - fluidRect.getMinY() ) * 0.5;
                slicePath.moveTo( centerX - sliceWidth / 2, bottomY );
                slicePath.curveTo( centerX - sliceWidth * 0.33, bottomY + controlPointYOffset, centerX + sliceWidth * 0.33, bottomY + controlPointYOffset, centerX + sliceWidth / 2, bottomY );
                slicePath.lineTo( centerX + sliceWidth / 2, topY );
                slicePath.curveTo( centerX + sliceWidth * 0.33, topY + controlPointYOffset, centerX - sliceWidth * 0.33, topY + controlPointYOffset, centerX - sliceWidth / 2, topY );
                slicePath.lineTo( centerX - sliceWidth / 2, bottomY );
            }
            slices.add( new EnergyChunkContainerSlice( slicePath.getGeneralPath(), -proportion * WIDTH, position ) );
        }
    }

    /**
     * Get the total number of excess or deficit energy chunks for the beaker
     * and for anything contained by the beaker.
     *
     * @return
     */
    @Override public int getSystemEnergyChunkBalance() {
        int totalBalance = getEnergyChunkBalance();
        for ( RectangularThermalMovableModelElement thermalElement : potentiallyContainedElements ) {
            if ( getThermalContactArea().getBounds().contains( thermalElement.getRect() ) ) {
                totalBalance += thermalElement.getEnergyChunkBalance();
            }
        }
        return totalBalance;
    }

    @Override protected void animateNonContainedEnergyChunks( double dt ) {
        for ( EnergyChunkWanderController energyChunkWanderController : new ArrayList<EnergyChunkWanderController>( energyChunkWanderControllers ) ) {
            EnergyChunk ec = energyChunkWanderController.getEnergyChunk();
            if ( isEnergyChunkObscured( ec ) ) {
                // This chunk is being transferred from a container in the
                // beaker to the fluid, so move it sideways.
                double xVel = 0.05 * dt * ( getCenterPoint().getX() > ec.position.get().getX() ? -1 : 1 );
                Vector2D motionVector = new Vector2D( xVel, 0 );
                ec.translate( motionVector );
            }
            else {
                energyChunkWanderController.updatePosition( dt );
            }

            if ( !isEnergyChunkObscured( ec ) && getSliceBounds().contains( ec.position.get().toPoint2D() ) ) {
                // Chunk is in a place where it can migrate to the slices and
                // stop moving.
                moveEnergyChunkToSlices( energyChunkWanderController.getEnergyChunk() );
            }
        }
    }

    @Override public void addEnergyChunk( EnergyChunk ec ) {
        if ( isEnergyChunkObscured( ec ) ) {
            // Chunk obscured by a model element in the beaker, probably
            // because the chunk just came from the model element.
            ec.zPosition.set( 0.0 );
            approachingEnergyChunks.add( ec );
            energyChunkWanderControllers.add( new EnergyChunkWanderController( ec, position ) );
        }
        else {
            super.addEnergyChunk( ec );
        }
    }

    private boolean isEnergyChunkObscured( EnergyChunk ec ) {
        for ( RectangularThermalMovableModelElement potentiallyContainedElement : potentiallyContainedElements ) {
            if ( this.getThermalContactArea().getBounds().contains( potentiallyContainedElement.getRect() ) && potentiallyContainedElement.getProjectedShape().contains( ec.position.get().toPoint2D() ) ) {
                return true;
            }
        }
        return false;
    }

    public EnergyContainerCategory getEnergyContainerCategory() {
        return EnergyContainerCategory.WATER;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.beaker;
    }
}
