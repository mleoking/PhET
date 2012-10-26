// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
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
    // TODO: This is temp until we figure out how much it should really put out.
    private static final double MAX_ENERGY_OUTPUT_RATE = 10; // In joules / (radians / sec)
    private static final double CRANK_TO_REAR_WHEEL_RATIO = 1;
    private static final int INITIAL_NUM_ENERGY_CHUNKS = 15;
    private static final Random RAND = new Random();
    public static final double ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT = 10; // In joules, but empirically determined.

    // Offset of the bike frame center.  Most other image offsets are relative
    // to this one.
    private static final Vector2D FRAME_CENTER_OFFSET = new Vector2D( 0.0, 0.01 );

    // Images used when representing this model element in the view.  The
    // offsets, which are in meters, were empirically determined.  The values
    // aren't really to scale, since there are so many things in this model
    // with very different scales.
    public static final ModelElementImage FRAME_IMAGE = new ModelElementImage( BICYCLE_FRAME_2, FRAME_CENTER_OFFSET );
    public static final ModelElementImage REAR_WHEEL_SPOKES_IMAGE = new ModelElementImage( BICYCLE_SPOKES, FRAME_CENTER_OFFSET.plus( new Vector2D( 0.035, -0.020 ) ) );
    public static final ModelElementImage RIDER_UPPER_BODY_IMAGE = new ModelElementImage( BICYCLE_RIDER, FRAME_CENTER_OFFSET.plus( new Vector2D( -0.0025, 0.062 ) ) );

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
    public ObservableList<EnergyChunk> movingEnergyChunks = new ObservableList<EnergyChunk>();
    private double energyProducedSinceLastChunkEmitted = ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT * 0.9;

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
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt ) {
        if ( isActive() ) {

            // If there is no energy, the target speed is 0, otherwise it is
            // the current set point.
            double target = energyChunkList.size() > 0 ? targetCrankAngularVelocity.get() : 0;

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

            // Decide if new chem energy chunk should start on its way.
            if ( targetCrankAngularVelocity.get() > 0 && mechanicalPoweredSystemIsNext.get()){
                energyProducedSinceLastChunkEmitted += MAX_ENERGY_OUTPUT_RATE * ( crankAngularVelocity / MAX_ANGULAR_VELOCITY_OF_CRANK ) * dt;
            }
            if ( energyProducedSinceLastChunkEmitted >= ENERGY_REQUIRED_FOR_CHUNK_TO_EMIT &&
                 targetCrankAngularVelocity.get() > 0 &&
                 mechanicalPoweredSystemIsNext.get() ) {

                // Start a new chunk moving.
                if ( energyChunkList.size() > 0 ) {
                    EnergyChunk energyChunk = energyChunkList.get( 0 );
                    energyChunkMovers.add( new EnergyChunkPathMover( energyChunk, createChemicalEnergyChunkPath( getPosition() ), EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                    energyChunkList.remove( energyChunk );
                    movingEnergyChunks.add( energyChunk );
                    energyProducedSinceLastChunkEmitted = 0;
                }
            }

            // Move the energy chunks.
            for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( energyChunkMovers ) ) {
                energyChunkMover.moveAlongPath( dt );
                if ( energyChunkMover.isPathFullyTraversed() ) {
                    if ( energyChunkMover.energyChunk.energyType.get() == EnergyType.CHEMICAL ) {

                        // Turn this into mechanical energy.
                        energyChunkMover.energyChunk.energyType.set( EnergyType.MECHANICAL );
                        energyChunkMovers.remove( energyChunkMover );

                        // Add new mover for the mechanical energy chunk.
                        energyChunkMovers.add( new EnergyChunkPathMover( energyChunkMover.energyChunk,
                                                                         createMechanicalEnergyChunkPath( getPosition() ),
                                                                         EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                    }
                    else {
                        // Must be mechanical.  Pass this on to the next energy
                        // system.
                        outgoingEnergyChunks.add( energyChunkMover.energyChunk );
                        energyChunkMovers.remove( energyChunkMover );
                    }
                }
            }
        }
        return new Energy( EnergyType.MECHANICAL, Math.abs( crankAngularVelocity / MAX_ANGULAR_VELOCITY_OF_CRANK * MAX_ENERGY_OUTPUT_RATE ), -Math.PI / 2 );
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
        movingEnergyChunks.clear();
        energyChunkMovers.clear();
    }

    @Override public List<EnergyChunk> extractOutgoingEnergyChunks() {
        List<EnergyChunk> retVal = new ArrayList<EnergyChunk>( outgoingEnergyChunks );
        movingEnergyChunks.removeAll( outgoingEnergyChunks );
        outgoingEnergyChunks.clear();
        return retVal;
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

    private static List<Vector2D> createChemicalEnergyChunkPath( final Vector2D panelPosition ) {
        return new ArrayList<Vector2D>() {{
            add( panelPosition.plus( BIKER_BUTTOCKS_OFFSET ) );
            add( panelPosition.plus( TOP_TUBE_ABOVE_CRANK_OFFSET ) );
        }};
    }

    private static List<Vector2D> createMechanicalEnergyChunkPath( final Vector2D panelPosition ) {
        return new ArrayList<Vector2D>() {{
            add( panelPosition.plus( BIKE_CRANK_OFFSET ) );
            add( panelPosition.plus( BOTTOM_OF_BACK_WHEEL_OFFSET ) );
            add( panelPosition.plus( NEXT_ENERGY_SYSTEM_OFFSET ) );
        }};
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectWaterPoweredGeneratorButton;
    }
}
