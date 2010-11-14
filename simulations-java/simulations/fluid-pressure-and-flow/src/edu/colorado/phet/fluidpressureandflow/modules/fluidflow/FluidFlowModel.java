package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;

/**
 * @author Sam Reid
 */
public class FluidFlowModel extends FluidPressureAndFlowModel {
    private Pipe pipe = new Pipe();
    private ArrayList<Particle> particles = new ArrayList<Particle>();


    public FluidFlowModel() {
        for ( int i = 0; i <= 10; i++ ) {
            particles.add( new Particle( -4, i / 10.0, pipe ) );
        }

        getClock().addClockListener( new ClockAdapter() {
            @Override
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                for ( int i = 0; i < particles.size(); i++ ) {
                    Particle particle = particles.get( i );
                    double x = particle.getX();
                    double x2 = x + pipe.getVelocity(x)*clockEvent.getSimulationTimeChange();
                    particle.setX( x2 );
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
}
