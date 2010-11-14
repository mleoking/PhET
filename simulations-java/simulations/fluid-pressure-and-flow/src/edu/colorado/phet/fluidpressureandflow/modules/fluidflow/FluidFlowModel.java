package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;

/**
 * @author Sam Reid
 */
public class FluidFlowModel extends FluidPressureAndFlowModel {
    private Pipe pipe = new Pipe();
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    private Random random = new Random();
    private ArrayList<Function1<Particle, Void>> particleAddedObservers = new ArrayList<Function1<Particle, Void>>();
    private VelocitySensor velocitySensor = new VelocitySensor( 0, 0, this );

    public FluidFlowModel() {
        getClock().addClockListener( new ClockAdapter() {
            @Override
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                final Particle newParticle = new Particle( pipe.getMinX() + 1E-6, random.nextDouble(), pipe );
                particles.add( newParticle );
                for ( Function1<Particle, Void> particleAddedObserver : particleAddedObservers ) {
                    particleAddedObserver.apply( newParticle );
                }

                ArrayList<Particle> toRemove = new ArrayList<Particle>();
                for ( int i = 0; i < particles.size(); i++ ) {
                    Particle particle = particles.get( i );
                    double x = particle.getX();
                    double x2 = x + pipe.getSpeed( x ) * clockEvent.getSimulationTimeChange();
                    if ( x2 >= pipe.getMaxX() ) {
                        toRemove.add( particle );
                    }
                    else {
                        particle.setX( x2 );
                    }
                }
                for ( int i = 0; i < toRemove.size(); i++ ) {
                    Particle particle = toRemove.get( i );
                    particles.remove( particle );
                    particle.notifyRemoved();
                }
            }
        } );
    }

    public Pipe getPipe() {
        return pipe;
    }

    public Particle[] getParticles() {
        return particles.toArray( new Particle[0] );
    }

    public void addParticleAddedObserver( Function1<Particle, Void> listener ) {
        particleAddedObservers.add( listener );
    }

    public ImmutableVector2D getVelocity( double x, double y ) {
        if ( pipe.contains( x, y ) ) {
            return pipe.getVelocity( x, y );//assumes velocity same at all y along a specified x
        }
        else {
            return new ImmutableVector2D();
        }
    }

    public VelocitySensor getVelocitySensor() {
        return velocitySensor;
    }
}
