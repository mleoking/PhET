// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

import static edu.colorado.phet.energyformsandchanges.common.EFACConstants.ENERGY_TO_NUM_CHUNKS_MAPPER;
import static edu.colorado.phet.energyformsandchanges.common.EFACConstants.MAX_HEAT_EXCHANGE_TIME_STEP;
import static edu.colorado.phet.energyformsandchanges.intro.model.HeatTransferConstants.getHeatTransferFactor;

/**
 * A movable model element that contains thermal energy and that, at least in
 * the model, has an overall shape that can be represented as a rectangle.
 *
 * @author John Blanco
 */
public abstract class RectangularThermalMovableModelElement extends UserMovableModelElement implements ThermalEnergyContainer {

    public final BooleanProperty energyChunksVisible;
    protected double energy = 0; // In Joules.
    private final double specificHeat; // In J/kg-K
    protected final double mass; // In kg
    protected final ConstantDtClock clock;
    private final double width;
    private final double height;
    private int nextSliceIndex;

    // 2D "slices" of the container, used for 3D layering of energy chunks.
    protected final List<EnergyChunkContainerSlice> slices = new ArrayList<EnergyChunkContainerSlice>();

    // Energy chunks that are approaching this model element.
    public final ObservableList<EnergyChunk> approachingEnergyChunks = new ObservableList<EnergyChunk>();
    protected final List<EnergyChunkWanderController> energyChunkWanderControllers = new ArrayList<EnergyChunkWanderController>();

    /**
     * Constructor.
     *
     * @param clock
     */
    public RectangularThermalMovableModelElement( ConstantDtClock clock, Vector2D initialPosition, double width, double height, double mass, double specificHeat, BooleanProperty energyChunksVisible ) {
        super( initialPosition );
        this.clock = clock;
        this.mass = mass;
        this.width = width;
        this.height = height;
        this.specificHeat = specificHeat;
        this.energyChunksVisible = energyChunksVisible;

        energy = mass * specificHeat * EFACConstants.ROOM_TEMPERATURE;

        // Hook up to the clock for time dependent behavior.
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Add the slices, a.k.a. layers, where the energy chunks will live.
        addEnergyChunkSlices();
        nextSliceIndex = slices.size() / 2;

        // Add the initial energy chunks.
        addInitialEnergyChunks();
    }

    /**
     * Get the rectangle that defines this elements position and shape in
     * model space.
     */
    public abstract Rectangle2D getRect();

    public void changeEnergy( double deltaEnergy ) {
        energy += deltaEnergy;
    }

    public double getEnergy() {
        return energy;
    }

    public double getTemperature() {
        return energy / ( mass * specificHeat );
    }

    @Override public void reset() {
        super.reset();
        energy = mass * specificHeat * EFACConstants.ROOM_TEMPERATURE;
        addInitialEnergyChunks();
    }

    protected void stepInTime( double dt ) {

        // Distribute the energy chunks contained within this model element.
        EnergyChunkDistributor.updatePositions( slices, dt );

        // Animate the energy chunks that are outside this model element.
        animateNonContainedEnergyChunks( dt );
    }

    protected void animateNonContainedEnergyChunks( double dt ) {
        for ( EnergyChunkWanderController energyChunkWanderController : new ArrayList<EnergyChunkWanderController>( energyChunkWanderControllers ) ) {
            energyChunkWanderController.updatePosition( dt );
            if ( getSliceBounds().contains( energyChunkWanderController.getEnergyChunk().position.get().toPoint2D() ) ) {
                moveEnergyChunkToSlices( energyChunkWanderController.getEnergyChunk() );
            }
        }
    }

    public void addEnergyChunk( EnergyChunk ec ) {
        if ( getSliceBounds().contains( ec.position.get().toPoint2D() ) ) {
            // Energy chunk is positioned within container bounds, so add it
            // directly to a slice.
            addEnergyChunkToNextSlice( ec );
        }
        else {
            // Chunk is out of the bounds of this element, so make it wander
            // towards it.
            ec.zPosition.set( 0.0 );
            approachingEnergyChunks.add( ec );
            energyChunkWanderControllers.add( new EnergyChunkWanderController( ec, position ) );
        }
    }

    // Add an energy chunk to the next available slice.  Override for more elaborate behavior.
    protected void addEnergyChunkToNextSlice( EnergyChunk ec ) {
        slices.get( nextSliceIndex ).addEnergyChunk( ec );
        nextSliceIndex = ( nextSliceIndex + 1 ) % slices.size();
    }

    protected Rectangle2D getSliceBounds() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for ( EnergyChunkContainerSlice slice : slices ) {
            Rectangle2D sliceBounds = slice.getShape().getBounds2D();
            if ( sliceBounds.getMinX() < minX ) {
                minX = sliceBounds.getMinX();
            }
            if ( sliceBounds.getMaxX() > maxX ) {
                maxX = sliceBounds.getMaxX();
            }
            if ( sliceBounds.getMinY() < minY ) {
                minY = sliceBounds.getMinY();
            }
            if ( sliceBounds.getMaxY() > maxY ) {
                maxY = sliceBounds.getMaxY();
            }
        }
        return new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
    }

    protected void moveEnergyChunkToSlices( EnergyChunk ec ) {
        approachingEnergyChunks.remove( ec );
        for ( EnergyChunkWanderController energyChunkWanderController : new ArrayList<EnergyChunkWanderController>( energyChunkWanderControllers ) ) {
            if ( energyChunkWanderController.getEnergyChunk() == ec ) {
                energyChunkWanderControllers.remove( energyChunkWanderController );
            }
        }
        addEnergyChunkToNextSlice( ec );
    }

    public boolean removeEnergyChunk( EnergyChunk ec ) {
        for ( EnergyChunkContainerSlice slice : slices ) {
            if ( slice.energyChunkList.remove( ec ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extract the closest energy chunk on a randomly chosen energy chunk
     * slice.  The random part prevents odd situations where all the chunks
     * get pulled off of one slice.
     *
     * @param point
     * @return
     */
    public EnergyChunk extractClosestEnergyChunk( Vector2D point ) {
        EnergyChunk closestEnergyChunk = null;
        double closestCompensatedDistance = Double.POSITIVE_INFINITY;

        // Identify the closest energy chunk.
        for ( EnergyChunkContainerSlice slice : slices ) {
            for ( EnergyChunk ec : slice.energyChunkList ) {
                // Compensate for the Z offset.  Otherwise front chunk will
                // almost always be chosen.
                Vector2D compensatedEcPosition = ec.position.get().minus( 0, EFACConstants.Z_TO_Y_OFFSET_MULTIPLIER * ec.zPosition.get() );
                double compensatedDistance = compensatedEcPosition.distance( point );
                if ( compensatedDistance < closestCompensatedDistance ) {
                    closestEnergyChunk = ec;
                    closestCompensatedDistance = compensatedDistance;
                }
            }
        }

        // TODO: If this method can be private, make it so, and make it just find the chunk without removing it.
        removeEnergyChunk( closestEnergyChunk );
        return closestEnergyChunk;
    }

    /**
     * Extract an energy chunk that is a good choice for being transferred to
     * the provided shape.  Generally, this means that it is close to the
     * shape.  This routine is not hugely general - it makes some assumptions
     * that make it work for blocks in beakers and so forth.
     *
     * @param destinationShape
     * @return
     */
    public EnergyChunk extractClosestEnergyChunk( Shape destinationShape ) {
        EnergyChunk chunkToExtract = null;
        Rectangle2D myBounds = getSliceBounds();
        if ( destinationShape.contains( getThermalContactArea().getBounds() ) ) {
            // Our shape is contained by the destination.  Pick a chunk near
            // our right or left edge.
            double closestDistanceToVerticalEdge = Double.POSITIVE_INFINITY;
            for ( EnergyChunkContainerSlice slice : slices ) {
                for ( EnergyChunk ec : slice.energyChunkList ) {
                    double distanceToVerticalEdge = Math.min( Math.abs( myBounds.getMinX() - ec.position.get().getX() ), Math.abs( myBounds.getMaxX() - ec.position.get().getX() ) );
                    if ( distanceToVerticalEdge < closestDistanceToVerticalEdge ) {
                        chunkToExtract = ec;
                        closestDistanceToVerticalEdge = distanceToVerticalEdge;
                    }
                }
            }
        }
        else if ( getThermalContactArea().getBounds().contains( destinationShape.getBounds2D() ) ) {
            // Our shape encloses the destination shape.  Choose a chunk that
            // is close but doesn't overlap with the destination shape.
            double closestDistanceToDestinationEdge = Double.POSITIVE_INFINITY;
            Rectangle2D destinationBounds = destinationShape.getBounds2D();
            for ( EnergyChunkContainerSlice slice : slices ) {
                for ( EnergyChunk ec : slice.energyChunkList ) {
                    double distanceToDestinationEdge = Math.min( Math.abs( destinationBounds.getMinX() - ec.position.get().getX() ), Math.abs( destinationBounds.getMaxX() - ec.position.get().getX() ) );
                    if ( !destinationShape.contains( ec.position.get().toPoint2D() ) && distanceToDestinationEdge < closestDistanceToDestinationEdge ) {
                        chunkToExtract = ec;
                        closestDistanceToDestinationEdge = distanceToDestinationEdge;
                    }
                }
            }
        }
        else {
            // There is no or limited overlap, so use center points.
            chunkToExtract = extractClosestEnergyChunk( new Vector2D( destinationShape.getBounds2D().getCenterX(), destinationShape.getBounds2D().getCenterY() ) );
        }

        // Fail safe - If nothing found, get the first chunk.
        if ( chunkToExtract == null ) {
            System.out.println( getClass().getName() + " - Warning: No energy chunk found by extraction algorithm, return one arbitrarily." );
            assert getNumEnergyChunks() != 0; // Everything should always have at least one chunk.
            for ( EnergyChunkContainerSlice slice : slices ) {
                for ( EnergyChunk ec : slice.energyChunkList ) {
                    chunkToExtract = ec;
                    break;
                }
                if ( chunkToExtract != null ) {
                    break;
                }
            }
        }

        removeEnergyChunk( chunkToExtract );
        return chunkToExtract;
    }

    /**
     * Initialization method that add the "slices" where the energy chunks
     * reside.  Should be called only once at initialization.
     */
    protected void addEnergyChunkSlices() {

        assert ( slices.size() == 0 ); // Make sure this method isn't being misused.

        // Defaults to a single slice matching the outline rectangle, override
        // for more sophisticated behavior.
        slices.add( new EnergyChunkContainerSlice( getRect(), 0, position ) );
    }

    protected void addInitialEnergyChunks() {
        for ( EnergyChunkContainerSlice slice : slices ) {
            slice.energyChunkList.clear();
        }
        int targetNumChunks = ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy );
        Rectangle2D energyChunkBounds = getThermalContactArea().getBounds();
        while ( getNumEnergyChunks() < targetNumChunks ) {
            // Add a chunk at a random location in the block.
            addEnergyChunk( new EnergyChunk( clock, EnergyType.THERMAL, EnergyChunkDistributor.generateRandomLocation( energyChunkBounds ), energyChunksVisible, false ) );
        }
        // Distribute the energy chunks within the container.
        for ( int i = 0; i < 1000; i++ ) {
            EnergyChunkDistributor.updatePositions( slices, clock.getDt() );
        }
    }

    protected int getNumEnergyChunks() {
        int numChunks = 0;
        for ( EnergyChunkContainerSlice slice : slices ) {
            numChunks += slice.getNumEnergyChunks();
        }
        return numChunks + approachingEnergyChunks.size();
    }

    public List<EnergyChunkContainerSlice> getSlices() {
        return slices;
    }

    public void exchangeEnergyWith( ThermalEnergyContainer otherEnergyContainer, double dt ) {
        double thermalContactLength = getThermalContactArea().getThermalContactLength( otherEnergyContainer.getThermalContactArea() );
        if ( thermalContactLength > 0 ) {
            if ( Math.abs( otherEnergyContainer.getTemperature() - getTemperature() ) > EFACConstants.TEMPERATURES_EQUAL_THRESHOLD ) {
                // Exchange energy between this and the other energy container.
                double heatTransferConstant = getHeatTransferFactor( this.getEnergyContainerCategory(), otherEnergyContainer.getEnergyContainerCategory() );
                int numFullTimeStepExchanges = (int) Math.floor( dt / MAX_HEAT_EXCHANGE_TIME_STEP );
                double leftoverTime = dt - ( numFullTimeStepExchanges * MAX_HEAT_EXCHANGE_TIME_STEP );
                for ( int i = 0; i < numFullTimeStepExchanges + 1; i++ ) {
                    double timeStep = i < numFullTimeStepExchanges ? MAX_HEAT_EXCHANGE_TIME_STEP : leftoverTime;
                    double thermalEnergyGained = ( otherEnergyContainer.getTemperature() - getTemperature() ) * thermalContactLength * heatTransferConstant * timeStep;
                    otherEnergyContainer.changeEnergy( -thermalEnergyGained );
                    changeEnergy( thermalEnergyGained );
                }
            }
        }
    }

    /**
     * Get the shape as is is projected into 3D in the view.  Ideally, this
     * wouldn't even be in the model, because it would be purely handled in the
     * view, but it proved necessary.
     *
     * @return
     */
    public Shape getProjectedShape() {
        // TODO: Do I need the internal lines?  Assuming not for now, but this may change.
        // This projects a rectangle, override for other behavior.
        Vector2D forwardPerspectiveOffset = EFACConstants.MAP_Z_TO_XY_OFFSET.apply( Block.SURFACE_WIDTH / 2 );
        Vector2D backwardPerspectiveOffset = EFACConstants.MAP_Z_TO_XY_OFFSET.apply( -Block.SURFACE_WIDTH / 2 );

        DoubleGeneralPath path = new DoubleGeneralPath();
        Rectangle2D rect = getRect();
        path.moveTo( new Vector2D( rect.getX(), rect.getY() ).plus( forwardPerspectiveOffset ) );
        path.lineTo( new Vector2D( rect.getMaxX(), rect.getY() ).plus( forwardPerspectiveOffset ) );
        path.lineTo( new Vector2D( rect.getMaxX(), rect.getY() ).plus( backwardPerspectiveOffset ) );
        path.lineTo( new Vector2D( rect.getMaxX(), rect.getMaxY() ).plus( backwardPerspectiveOffset ) );
        path.lineTo( new Vector2D( rect.getMinX(), rect.getMaxY() ).plus( backwardPerspectiveOffset ) );
        path.lineTo( new Vector2D( rect.getMinX(), rect.getMaxY() ).plus( forwardPerspectiveOffset ) );
        path.closePath();
        return path.getGeneralPath();
    }

    public Vector2D getCenterPoint() {
        return new Vector2D( position.get().getX(), position.get().getY() + height / 2 );
    }

    /**
     * Get a number indicating the balance between the energy level and the
     * number of energy chunks owned by this model element.
     *
     * @return 0 if the number of energy chunks matches the energy level, a
     *         negative value if there is a deficit, and a positive value if there is
     *         a surplus.
     */
    public int getEnergyChunkBalance() {
        return getNumEnergyChunks() - EFACConstants.ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy );
    }

    public int getSystemEnergyChunkBalance() {
        // Override if the element can contain other elements.
        return getEnergyChunkBalance();
    }
}