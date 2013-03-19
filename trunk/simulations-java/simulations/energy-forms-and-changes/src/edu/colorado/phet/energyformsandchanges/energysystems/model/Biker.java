// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;

/**
 * This class represents a bicycle being peddled by a rider in order to
 * generate energy.
 *
 * @author John Blanco
 */
public class Biker extends EnergySource {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final double MAX_ANGULAR_VELOCITY_OF_CRANK = 3 * Math.PI; // In radians/sec.
    private static final double ANGULAR_ACCELERATION = Math.PI / 2; // In radians/(sec^2).
    private static final double MAX_ENERGY_OUTPUT_WHEN_CONNECTED_TO_GENERATOR = EFACConstants.MAX_ENERGY_PRODUCTION_RATE; // In joules / sec
    private static final double MAX_ENERGY_OUTPUT_WHEN_RUNNING_FREE = MAX_ENERGY_OUTPUT_WHEN_CONNECTED_TO_GENERATOR / 5; // In joules / sec
    private static final double CRANK_TO_REAR_WHEEL_RATIO = 1;
    private static final int INITIAL_NUM_ENERGY_CHUNKS = 15;
    private static final Random RAND = new Random();
    public static final int MECHANICAL_TO_THERMAL_CHUNK_RATIO = 5;

    // Offset of the bike frame center.  Most other image offsets are relative
    // to this one.
    private static final Vector2D FRAME_CENTER_OFFSET = new Vector2D( 0.0, 0.01 );

    // Images used when representing this model element in the view.  The
    // offsets, which are in meters, were empirically determined.  The values
    // aren't really to scale, since there are so many things in this model
    // with very different scales.
    public static final ModelElementImage FRAME_IMAGE = new ModelElementImage( BICYCLE_FRAME_3, FRAME_CENTER_OFFSET );
    public static final ModelElementImage REAR_WHEEL_SPOKES_IMAGE = new ModelElementImage( BICYCLE_SPOKES, FRAME_CENTER_OFFSET.plus( new Vector2D( 0.035, -0.020 ) ) );
    public static final ModelElementImage RIDER_NORMAL_UPPER_BODY_IMAGE = new ModelElementImage( BICYCLE_RIDER, FRAME_CENTER_OFFSET.plus( new Vector2D( -0.0025, 0.062 ) ) );
    public static final ModelElementImage RIDER_TIRED_UPPER_BODY_IMAGE = new ModelElementImage( BICYCLE_RIDER_TIRED, FRAME_CENTER_OFFSET.plus( new Vector2D( -0.0032, 0.056 ) ) );

    private static final Vector2D LEG_IMAGE_OFFSET = new Vector2D( 0.009, 0.002 );

    // Images used for animation.
    public static final List<ModelElementImage> FRONT_LEG_IMAGES = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( FRONT_LEG_01, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_02, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_03, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_04, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_05, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_06, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_07, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_08, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_09, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_10, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_11, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_12, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_13, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_14, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_15, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_16, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_17, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_18, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_19, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_20, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_21, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_22, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_23, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( FRONT_LEG_24, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
    }};

    public static final List<ModelElementImage> BACK_LEG_IMAGES = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( BACK_LEG_01, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_02, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_03, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_04, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_05, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_06, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_07, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_08, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_09, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_10, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_11, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_12, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_13, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_14, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_15, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_16, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_17, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_18, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_19, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_20, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_21, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_22, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_23, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
        add( new ModelElementImage( BACK_LEG_24, FRAME_CENTER_OFFSET.plus( LEG_IMAGE_OFFSET ) ) );
    }};

    public static final int NUM_LEG_IMAGES = FRONT_LEG_IMAGES.size();

    // Offsets used for creating energy chunk paths.  These need to be
    // coordinated with the images.
    private static final Vector2D BIKER_BUTTOCKS_OFFSET = new Vector2D( 0.02, 0.04 );
    private static final Vector2D TOP_TUBE_ABOVE_CRANK_OFFSET = new Vector2D( 0.007, 0.015 );
    private static final Vector2D BIKE_CRANK_OFFSET = new Vector2D( 0.0052, -0.006 );
    private static final Vector2D CENTER_OF_BACK_WHEEL_OFFSET = new Vector2D( 0.03, -0.01 );
    private static final Vector2D BOTTOM_OF_BACK_WHEEL_OFFSET = new Vector2D( 0.03, -0.03 );
    private static final Vector2D NEXT_ENERGY_SYSTEM_OFFSET = new Vector2D( 0.13, -0.01 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private Property<Double> crankAngle = new Property<Double>( 0.0 ); // In radians.
    private Property<Double> rearWheelAngle = new Property<Double>( 0.0 ); // In radians.
    private double crankAngularVelocity = 0; // In radians/s.
    private final ObservableProperty<Boolean> energyChunksVisible;
    private List<EnergyChunkPathMover> energyChunkMovers = new ArrayList<EnergyChunkPathMover>();
    private double energyProducedSinceLastChunkEmitted = EFACConstants.ENERGY_PER_CHUNK * 0.9;
    private int mechanicalChunksSinceLastThermal = 0;
    public BooleanProperty bikerHasEnergy = new BooleanProperty( true );

    // Property through which the target pedaling rate is set.
    public Property<Double> targetCrankAngularVelocity = new Property<Double>( 0.0 ); // In radians/sec

    private final ObservableProperty<Boolean> mechanicalPoweredSystemIsNext;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public Biker( ObservableProperty<Boolean> energyChunksVisible, ObservableProperty<Boolean> mechanicalPoweredSystemIsNext ) {
        super( EnergyFormsAndChangesResources.Images.BICYCLE_ICON );
        this.energyChunksVisible = energyChunksVisible;
        this.mechanicalPoweredSystemIsNext = mechanicalPoweredSystemIsNext;

        // Monitor target rotation rate for validity.
        targetCrankAngularVelocity.addObserver( new VoidFunction1<Double>() {
            public void apply( Double targetAngularVelocity ) {
                assert targetAngularVelocity >= 0 && targetAngularVelocity <= MAX_ANGULAR_VELOCITY_OF_CRANK;
            }
        } );

        // Add initial set of energy chunks.
        replenishEnergyChunks();

        // Get the crank into a position where animation will start right away.
        setCrankToPoisedPosition();

        // Add a handler for the situation when energy chunks were in transit
        // to the next energy system and that system is swapped out.
        mechanicalPoweredSystemIsNext.addObserver( new ChangeObserver<Boolean>() {
            public void update( Boolean isNext, Boolean wasNext ) {
                Vector2D hubPosition = getPosition().plus( CENTER_OF_BACK_WHEEL_OFFSET );
                for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( energyChunkMovers ) ) {
                    EnergyChunk ec = energyChunkMover.energyChunk;
                    if ( ec.energyType.get() == EnergyType.MECHANICAL ) {
                        if ( ec.position.get().getX() > hubPosition.getX() ) {
                            // Just remove this energy chunk.
                            energyChunkMovers.remove( energyChunkMover );
                            energyChunkList.remove( ec );
                        }
                        else {
                            // Make sure that this energy chunk turns to thermal energy.
                            energyChunkMovers.remove( energyChunkMover );
                            energyChunkMovers.add( new EnergyChunkPathMover( ec,
                                                                             createMechanicalToThermalEnergyChunkPath( getPosition(), ec.position.get() ),
                                                                             EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                        }
                    }
                }
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt ) {
        if ( isActive() ) {

            // Update energy state.
            bikerHasEnergy.set( bikerHasEnergy() );

            // If there is no energy, the target speed is 0, otherwise it is
            // the current set point.
            double target = bikerHasEnergy.get() ? targetCrankAngularVelocity.get() : 0;

            // Speed up or slow down the angular velocity of the crank.
            double previousAngularVelocity = crankAngularVelocity;
            double angularVelocityDiffFromTarget = target - crankAngularVelocity;
            if ( angularVelocityDiffFromTarget != 0 ) {
                double change = ANGULAR_ACCELERATION * dt;
                if ( angularVelocityDiffFromTarget > 0 ) {
                    // Accelerate
                    crankAngularVelocity = Math.min( crankAngularVelocity + change, targetCrankAngularVelocity.get() );
                }
                else {
                    // Decelerate
                    crankAngularVelocity = Math.max( crankAngularVelocity - change, 0 );
                }
            }
            crankAngle.set( ( crankAngle.get() + crankAngularVelocity * dt ) % ( 2 * Math.PI ) );
            rearWheelAngle.set( ( rearWheelAngle.get() + crankAngularVelocity * dt * CRANK_TO_REAR_WHEEL_RATIO ) % ( 2 * Math.PI ) );

            if ( crankAngularVelocity == 0 && previousAngularVelocity != 0 ) {
                // Set crank to a good position where animation will start
                // right away when motion is restarted.
                setCrankToPoisedPosition();
            }

            // Determine how much energy is produced in this time step.
            if ( targetCrankAngularVelocity.get() > 0 ) {

                // Less energy is produced if not hooked up to generator.
                double maxEnergyProductionRate = mechanicalPoweredSystemIsNext.get() ? MAX_ENERGY_OUTPUT_WHEN_CONNECTED_TO_GENERATOR : MAX_ENERGY_OUTPUT_WHEN_RUNNING_FREE;
                energyProducedSinceLastChunkEmitted += maxEnergyProductionRate * ( crankAngularVelocity / MAX_ANGULAR_VELOCITY_OF_CRANK ) * dt;
            }

            // Decide if new chem energy chunk should start on its way.
            if ( energyProducedSinceLastChunkEmitted >= EFACConstants.ENERGY_PER_CHUNK && targetCrankAngularVelocity.get() > 0 ) {

                // Start a new chunk moving.
                if ( bikerHasEnergy() ) {
                    EnergyChunk energyChunk = findNonMovingEnergyChunk();
                    energyChunkMovers.add( new EnergyChunkPathMover( energyChunk, createChemicalEnergyChunkPath( getPosition() ), EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                    energyProducedSinceLastChunkEmitted = 0;
                }
            }

            // Move the energy chunks.
            for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( energyChunkMovers ) ) {
                energyChunkMover.moveAlongPath( dt );
                if ( energyChunkMover.isPathFullyTraversed() ) {
                    EnergyChunk energyChunk = energyChunkMover.energyChunk;
                    if ( energyChunk.energyType.get() == EnergyType.CHEMICAL ) {

                        // Turn this into mechanical energy.
                        energyChunk.energyType.set( EnergyType.MECHANICAL );
                        energyChunkMovers.remove( energyChunkMover );

                        // Add new mover for the mechanical energy chunk.
                        if ( mechanicalChunksSinceLastThermal >= MECHANICAL_TO_THERMAL_CHUNK_RATIO || !mechanicalPoweredSystemIsNext.get() ) {
                            // Make this chunk travel to the rear hub, where it
                            // will become a chunk of thermal energy.
                            energyChunkMovers.add( new EnergyChunkPathMover( energyChunk,
                                                                             createMechanicalToThermalEnergyChunkPath( getPosition(), energyChunk.position.get() ),
                                                                             EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                            mechanicalChunksSinceLastThermal = 0;
                        }
                        else {
                            // Send this chunk to the next energy system.
                            energyChunkMovers.add( new EnergyChunkPathMover( energyChunk,
                                                                             createMechanicalEnergyChunkPath( getPosition() ),
                                                                             EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                            mechanicalChunksSinceLastThermal++;
                        }
                    }
                    else if ( energyChunk.energyType.get() == EnergyType.MECHANICAL && energyChunk.position.get().distance( getPosition().plus( CENTER_OF_BACK_WHEEL_OFFSET ) ) < 1E-6 ) {
                        // This is a mechanical energy chunk that has traveled
                        // to the hub and should now become thermal energy.
                        energyChunkMovers.remove( energyChunkMover );
                        energyChunk.energyType.set( EnergyType.THERMAL );
                        energyChunkMovers.add( new EnergyChunkPathMover( energyChunk,
                                                                         createThermalEnergyChunkPath( getPosition() ),
                                                                         EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                    }
                    else if ( energyChunk.energyType.get() == EnergyType.THERMAL ) {
                        // This is a radiating thermal energy chunk that has
                        // reached the end of its route.  Delete it.
                        energyChunkMovers.remove( energyChunkMover );
                        energyChunkList.remove( energyChunk );
                    }
                    else {
                        // Must be mechanical energy that is being passed to
                        // the next energy system.
                        outgoingEnergyChunks.add( energyChunk );
                        energyChunkMovers.remove( energyChunkMover );
                    }
                }
            }
        }
        return new Energy( EnergyType.MECHANICAL, Math.abs( crankAngularVelocity / MAX_ANGULAR_VELOCITY_OF_CRANK * MAX_ENERGY_OUTPUT_WHEN_CONNECTED_TO_GENERATOR * dt ), -Math.PI / 2 );
    }

    @Override public void preLoadEnergyChunks() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Energy getEnergyOutputRate() {
        return new Energy( EnergyType.MECHANICAL, Math.abs( crankAngularVelocity / MAX_ANGULAR_VELOCITY_OF_CRANK * MAX_ENERGY_OUTPUT_WHEN_CONNECTED_TO_GENERATOR ) );
    }

    /*
     * Set the crank to a position where a very small amount of motion will
     * cause a new image to be chosen.  This is generally done when the biker
     * stops so that the animation starts right away the next time the motion
     * starts.
     */
    private void setCrankToPoisedPosition() {
        int currentImage = mapAngleToImageIndex( crankAngle.get() );
        double radiansPerImage = 2 * Math.PI / NUM_LEG_IMAGES;
        crankAngle.set( ( currentImage % NUM_LEG_IMAGES * radiansPerImage + ( radiansPerImage - 1E-7 ) ) );
    }

    @Override public void deactivate() {
        super.deactivate();
        targetCrankAngularVelocity.set( 0.0 );
        crankAngularVelocity = targetCrankAngularVelocity.get();
    }

    @Override public void activate() {
        super.activate();
        // TODO: Don't really want to replenish every time, I don't think.
        replenishEnergyChunks();
    }

    @Override public void clearEnergyChunks() {
        super.clearEnergyChunks();
        energyChunkMovers.clear();
    }

    public ObservableProperty<Double> getRearWheelAngle() {
        return rearWheelAngle;
    }

    public Property<Double> getCrankAngle() {
        return crankAngle;
    }

    public void replenishEnergyChunks() {
        Vector2D nominalInitialOffset = new Vector2D( 0.019, 0.05 );
        for ( int i = 0; i < INITIAL_NUM_ENERGY_CHUNKS; i++ ) {
            Vector2D displacement = new Vector2D( ( RAND.nextDouble() - 0.5 ) * 0.02, 0 ).getRotatedInstance( Math.PI * 0.7 );
            EnergyChunk newEnergyChunk = new EnergyChunk( EnergyType.CHEMICAL, getPosition().plus( nominalInitialOffset ).plus( displacement ),
                                                          energyChunksVisible );
            energyChunkList.add( newEnergyChunk );
        }
    }

    public static int mapAngleToImageIndex( double angle ) {
        int temp = (int) ( Math.floor( ( angle % ( 2 * Math.PI ) ) / ( Math.PI * 2 / NUM_LEG_IMAGES ) ) );
        if ( temp >= NUM_LEG_IMAGES || temp < 0 ) {
            assert false;
        }
        return (int) ( Math.floor( ( angle % ( 2 * Math.PI ) ) / ( Math.PI * 2 / NUM_LEG_IMAGES ) ) );
    }

    private static List<Vector2D> createChemicalEnergyChunkPath( final Vector2D centerPosition ) {
        return new ArrayList<Vector2D>() {{
            add( centerPosition.plus( BIKER_BUTTOCKS_OFFSET ) );
            add( centerPosition.plus( TOP_TUBE_ABOVE_CRANK_OFFSET ) );
        }};
    }

    private static List<Vector2D> createMechanicalEnergyChunkPath( final Vector2D centerPosition ) {
        return new ArrayList<Vector2D>() {{
            add( centerPosition.plus( BIKE_CRANK_OFFSET ) );
            add( centerPosition.plus( BOTTOM_OF_BACK_WHEEL_OFFSET ) );
            add( centerPosition.plus( NEXT_ENERGY_SYSTEM_OFFSET ) );
        }};
    }

    // Create a path for an energy chunk that will travel to the hub and then become thermal.
    private static List<Vector2D> createMechanicalToThermalEnergyChunkPath( final Vector2D centerPosition, final Vector2D currentPosition ) {
        return new ArrayList<Vector2D>() {{
            Vector2D crankPosition = centerPosition.plus( BIKE_CRANK_OFFSET );
            if ( currentPosition.getY() > crankPosition.getY() ) {
                // Only add the crank position if the current position
                // indicates that the chunk hasn't reached the crank yet.
                add( centerPosition.plus( BIKE_CRANK_OFFSET ) );
            }
            add( centerPosition.plus( CENTER_OF_BACK_WHEEL_OFFSET ) );
        }};
    }

    private static List<Vector2D> createThermalEnergyChunkPath( final Vector2D centerPosition ) {
        final double segmentLength = 0.05;
        final double maxAngle = Math.PI / 8;
        final int numSegments = 3;
        return new ArrayList<Vector2D>() {{
            Vector2D offset = centerPosition.plus( CENTER_OF_BACK_WHEEL_OFFSET );
            add( new Vector2D( offset ) );

            // The chuck needs to move up and to the right to avoid overlapping with the biker.
            offset = offset.plus( new Vector2D( segmentLength, 0 ).getRotatedInstance( Math.PI * 0.4 ) );

            // Add a set of path segments that make the chunk move up in a somewhat random path.
            add( new Vector2D( offset ) );
            for ( int i = 0; i < numSegments; i++ ) {
                offset = offset.plus( new Vector2D( 0, segmentLength ).getRotatedInstance( ( RAND.nextDouble() - 0.5 ) * maxAngle ) );
                add( new Vector2D( offset ) );
            }
        }};
    }

    // Choose a non-moving energy chunk, returns null if all chunks are moving.
    private EnergyChunk findNonMovingEnergyChunk(){
        List<EnergyChunk> movingEnergyChunks = new ArrayList<EnergyChunk>(  );
        for ( EnergyChunkPathMover energyChunkMover : energyChunkMovers ) {
            movingEnergyChunks.add( energyChunkMover.energyChunk );
        }
        EnergyChunk nonMovingEnergyChunk = null;
        for ( EnergyChunk energyChunk : energyChunkList ) {
            if ( !movingEnergyChunks.contains( energyChunk )){
                nonMovingEnergyChunk = energyChunk;
                break;
            }
        }
        return nonMovingEnergyChunk;
    }

    private boolean bikerHasEnergy(){
        return energyChunkList.size() > 0 && energyChunkList.size() > energyChunkMovers.size();
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectWaterPoweredGeneratorButton;
    }
}
