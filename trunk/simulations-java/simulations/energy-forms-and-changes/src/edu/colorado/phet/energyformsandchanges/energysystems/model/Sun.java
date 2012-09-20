// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

/**
 * Class that represents the sun (as an energy source) in the model.  This
 * includes the clouds that can block the sun's rays.
 *
 * @author John Blanco
 */
public class Sun extends EnergySource {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final double RADIUS = 0.02; // In meters, apparent size, not (obviously) actual size.
    public static final Vector2D OFFSET_TO_CENTER_OF_SUN = new Vector2D( -0.05, 0.12 );
    public static final double ENERGY_CHUNK_VELOCITY = 0.04; // Meters/sec, obviously not to scale.
    public static final double ENERGY_CHUNK_EMISSION_PERIOD = 0.1; // In seconds.
    private static final Random RAND = new Random();
    private static final double MAX_DISTANCE_OF_E_CHUNKS_FROM_SUN = 0.5; // In meters.

    // Energy production per square meter of the Earth's surface.
    private static final double ENERGY_PRODUCTION_RATE = 1000; // In joules/second per square meter of Earth.

    private final BooleanProperty energyChunksVisible;

    //TODO: This, and all image lists, should be removed once the prototypes have all been replaced.
    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>();

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Clouds that can potentially block the sun's rays.  The positions are
    // set so that they appear between the sun and the solar panel, and must
    // not overlap with one another.
    public final List<Cloud> clouds = new ArrayList<Cloud>() {{
//        add( new Cloud( new Vector2D( 0.01, 0.12 ) ) );  // TODO: For testing, immediately to right of sun.
//        add( new Cloud( new Vector2D( -0.04, 0.07 ) ) );   // TODO: For testing, immediately below the sun.
        add( new Cloud( new Vector2D( 0.01, 0.11 ), getObservablePosition() ) );
        add( new Cloud( new Vector2D( 0.02, 0.0925 ), getObservablePosition() ) );
        add( new Cloud( new Vector2D( -0.01, 0.085 ), getObservablePosition() ) );
    }};

    public final Property<Double> cloudiness = new Property<Double>( 0.0 );

    // Solar panel that will, at times, be absorbing the sun's rays.
    public final SolarPanel solarPanel;

    private double energyChunkEmissionCountdownTimer = ENERGY_CHUNK_EMISSION_PERIOD;

    // List of energy chunks that should be allowed to pass through the clouds
    // without bouncing (i.e. being reflected).
    private List<EnergyChunk> energyChunksPassingThroughClouds = new ArrayList<EnergyChunk>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected Sun( SolarPanel solarPanel, BooleanProperty energyChunksVisible ) {
        super( EnergyFormsAndChangesResources.Images.SUN_ICON, IMAGE_LIST );
        this.solarPanel = solarPanel;
        this.energyChunksVisible = energyChunksVisible;

        // Add/remove clouds based on the value of the cloudiness property.
        cloudiness.addObserver( new VoidFunction1<Double>() {
            public void apply( Double cloudiness ) {
                for ( int i = 0; i < clouds.size(); i++ ) {
                    // Stagger the existence strength of the clouds.
                    clouds.get( i ).existenceStrength.set( MathUtil.clamp( 0, cloudiness * clouds.size() - i, 1 ) );
                }
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt ) {

        Vector2D sunPosition = getPosition().plus( OFFSET_TO_CENTER_OF_SUN );
        double energyProduced = 0;

        if ( isActive() ) {
            // See if it is time to emit a new energy chunk.
            energyChunkEmissionCountdownTimer -= dt;
            if ( energyChunkEmissionCountdownTimer <= 0 ) {
                // Create a new chunk and start it on its way.
                double directionAngle = RAND.nextDouble() * Math.PI * 2;
                Vector2D initialPosition = sunPosition.plus( new Vector2D( RADIUS / 2, 0 ).getRotatedInstance( directionAngle ) );
                EnergyChunk energyChunk = new EnergyChunk( EnergyType.LIGHT, initialPosition.x, initialPosition.y, energyChunksVisible );
                energyChunk.setVelocity( new Vector2D( ENERGY_CHUNK_VELOCITY, 0 ).getRotatedInstance( directionAngle ) );
                energyChunkList.add( energyChunk );
                energyChunkEmissionCountdownTimer = ENERGY_CHUNK_EMISSION_PERIOD;
            }

            // Check for bouncing and absorption of the energy chunks.
            for ( EnergyChunk energyChunk : new ArrayList<EnergyChunk>( energyChunkList ) ) {
                if ( solarPanel.getAbsorptionShape().contains( energyChunk.position.get().toPoint2D() ) ) {
                    // This energy chunk was absorbed by the solar panel, so
                    // put it on the list of outgoing chunks.
                    outgoingEnergyChunks.add( energyChunk );
                    energyChunkList.remove( energyChunk );
                }
                else if ( energyChunk.position.get().distance( getPosition().plus( OFFSET_TO_CENTER_OF_SUN ) ) > MAX_DISTANCE_OF_E_CHUNKS_FROM_SUN ) {
                    // This energy chunk is out of visible range, so remove it.
                    energyChunkList.remove( energyChunk );
                    energyChunksPassingThroughClouds.remove( energyChunk );
                }
                else {
                    for ( Cloud cloud : clouds ) {
                        if ( cloud.getCloudAbsorptionReflectionShape().contains( energyChunk.position.get().toPoint2D() ) && !energyChunksPassingThroughClouds.contains( energyChunk ) ) {
                            // Decide whether this energy chunk should pass
                            // through the clouds or be reflected.
                            if ( RAND.nextDouble() < cloud.existenceStrength.get() ) {
                                // Reflect the energy chunk.
                                energyChunk.setVelocity( energyChunk.getVelocity().getRotatedInstance( Math.PI ) );
                            }
                            else {
                                // Let is pass through the cloud.
                                energyChunksPassingThroughClouds.add( energyChunk );
                            }
                        }
                    }
                }
            }

            // Move all the energy chunks away from the sun.
            for ( EnergyChunk energyChunk : energyChunkList ) {
                energyChunk.translateBasedOnVelocity( dt );
            }

            // Calculate the amount of energy produced.
            energyProduced = ENERGY_PRODUCTION_RATE * ( 1 - cloudiness.get() ) * dt;
        }

        // Produce the energy.
        return new Energy( EnergyType.LIGHT, energyProduced );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectSunButton;
    }
}
