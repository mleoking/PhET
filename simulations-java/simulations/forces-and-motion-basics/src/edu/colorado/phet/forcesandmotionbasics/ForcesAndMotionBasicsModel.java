package edu.colorado.phet.forcesandmotionbasics;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * @author Sam Reid
 */
public class ForcesAndMotionBasicsModel {
    public final Property<Double> blockPosition = new Property<Double>( 0.0 );
    public final Property<Double> blockVelocity = new Property<Double>( 0.0 );
    public final Property<Double> cameraPosition = new Property<Double>( 0.0 );
    public final Property<Double> cameraVelocity = new Property<Double>( 0.0 );
    public final Property<Boolean> leftPressed = new Property<Boolean>( false );
    public final Property<Boolean> rightPressed = new Property<Boolean>( false );
    public final double acceleration = 0.03;

    public ForcesAndMotionBasicsModel( final IClock clock, final MyMode mode ) {
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                super.simulationTimeChanged( clockEvent );

                if ( rightPressed.get() ) {
                    blockVelocity.set( blockVelocity.get() + 1 * acceleration );
                }
                if ( leftPressed.get() ) {
                    blockVelocity.set( blockVelocity.get() - 1 * acceleration );
                }
                blockPosition.set( blockPosition.get() + blockVelocity.get() * clockEvent.getSimulationTimeChange() );

                if ( mode == MyMode.mode1 ) {
                    cameraPosition.set( blockPosition.get() );
                }
                else if ( mode == MyMode.mode2 ) {
                    //if the block has gone out of range, move the camera to keep up.
                    if ( blockPosition.get() > cameraPosition.get() + 2 ) {
                        cameraPosition.set( blockPosition.get() - 2 );
                    }
                    if ( blockPosition.get() < cameraPosition.get() - 2 ) {
                        cameraPosition.set( blockPosition.get() + 2 );
                    }
                }
                else if ( mode == MyMode.mode3 ) {
                    double displacement = 1.5 * Math.atan( blockVelocity.get() );
                    cameraPosition.set( blockPosition.get() - displacement );
                }
            }
        } );
    }
}