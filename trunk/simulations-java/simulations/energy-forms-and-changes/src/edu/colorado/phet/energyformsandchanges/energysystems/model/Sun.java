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

    public static final double RADIUS = 0.02; // In meters, apparent size, not (obviously) actual size.
    public static final Vector2D OFFSET_TO_CENTER_OF_SUN = new Vector2D( -0.05, 0.12 );
    public static final double ENERGY_CHUNK_VELOCITY = 0.001; // Meters/sec, obviously not to scale.
    public static final double ENERGY_CHUNK_EMISSION_PERIOD = 0.25; // In seconds.
    private static final Random RAND = new Random();
    private static final double MAX_DISTANCE_OF_E_CHUNKS_FROM_SUN = 0.2; // In meters.

    // Clouds that can potentially block the sun's rays.  The positions are
    // set so that they appear between the sun and the solar panel, and must
    // not overlap with one another.
    public final List<Cloud> clouds = new ArrayList<Cloud>() {{
//        add( new Cloud( new Vector2D( 0.01, 0.12 ) ) );  // TODO: For testing, immediately to right of sun.
//        add( new Cloud( new Vector2D( -0.04, 0.07 ) ) );   // TODO: For testing, immediately below the sun.
        add( new Cloud( new Vector2D( 0.01, 0.11 ) ) );
        add( new Cloud( new Vector2D( 0.02, 0.0925 ) ) );
        add( new Cloud( new Vector2D( -0.01, 0.085 ) ) );
    }};

    public final Property<Double> cloudiness = new Property<Double>( 0.0 );

    // Energy production per square meter of the Earth's surface.
    private static final double ENERGY_PRODUCTION_RATE = 1000; // In joules/second per square meter of Earth.

    private double energyChunkEmissionCountdownTimer = ENERGY_CHUNK_EMISSION_PERIOD;

    //TODO: This, and all image lists, should be removed once the prototypes have all been replaced.
    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>();

    protected Sun() {
        super( EnergyFormsAndChangesResources.Images.SUN_ICON, IMAGE_LIST );
        cloudiness.addObserver( new VoidFunction1<Double>() {
            public void apply( Double cloudiness ) {
                for ( int i = 0; i < clouds.size(); i++ ) {
                    // Stagger the existence strength of the clouds.
                    clouds.get( i ).existenceStrength.set( MathUtil.clamp( 0, cloudiness * clouds.size() - i, 1 ) );
                }
            }
        } );
    }

    @Override public Energy stepInTime( double dt ) {

        Vector2D sunPosition = getPosition().plus( OFFSET_TO_CENTER_OF_SUN );
        double energyProduced = 0;

        if ( active ) {
            // See if it is time to emit an energy chunk.
            energyChunkEmissionCountdownTimer -= dt;
            if ( energyChunkEmissionCountdownTimer <= 0 ) {
                // Create a new chunk and start it on its way.
                Vector2D initialPosition = sunPosition.plus( new Vector2D( RADIUS / 2, 0 ).getRotatedInstance( RAND.nextDouble() * Math.PI * 2 ) );
                EnergyChunk energyChunk = new EnergyChunk( EnergyType.SOLAR, initialPosition.x, initialPosition.y, new BooleanProperty( true ) );
                energyChunkList.add( energyChunk );
                energyChunkEmissionCountdownTimer = ENERGY_CHUNK_EMISSION_PERIOD;
            }

            // See if any energy chunks should be removed.
            for ( EnergyChunk energyChunk : new ArrayList<EnergyChunk>( energyChunkList ) ) {
                if ( energyChunk.position.get().distance( getPosition().plus( OFFSET_TO_CENTER_OF_SUN ) ) > MAX_DISTANCE_OF_E_CHUNKS_FROM_SUN ) {
                    energyChunkList.remove( energyChunk );
                }
            }

            // Move all the energy chunks away from the sun.
            for ( EnergyChunk energyChunk : energyChunkList ) {
                double direction = energyChunk.position.get().minus( sunPosition ).getAngle();
                energyChunk.translate( new Vector2D( ENERGY_CHUNK_VELOCITY, 0 ).getRotatedInstance( direction ) );
            }

            // Calculate the amount of energy produced.
            energyProduced = ENERGY_PRODUCTION_RATE * ( 1 - cloudiness.get() ) * dt;
        }

        // Produce the energy.
        return new Energy( EnergyType.SOLAR, energyProduced );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectSunButton;
    }
}
