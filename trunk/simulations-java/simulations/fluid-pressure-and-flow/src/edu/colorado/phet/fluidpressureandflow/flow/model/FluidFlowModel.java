// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.model;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.model.VelocitySensorContext;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.watertower.model.FPAFVelocitySensor;

/**
 * Model for the "Flow" tab
 *
 * @author Sam Reid
 */
public class FluidFlowModel extends FluidPressureAndFlowModel implements VelocitySensorContext {

    //Source of randomness
    private final Random random = new Random();

    //Model elements
    public final Pipe pipe = new Pipe();
    private final ArrayList<Particle> particles = new ArrayList<Particle>();
    public final FluxMeter fluxMeter = new FluxMeter( pipe );

    //Observers
    private final ArrayList<VoidFunction1<Particle>> particleAddedObservers = new ArrayList<VoidFunction1<Particle>>();

    //percent probability to drop in each frame
    public final Property<Double> dropperRate = new Property<Double>( 33.0 );

    //true if the dropper is dropping in red dots
    public final Property<Boolean> dropperEnabled = new Property<Boolean>( true );

    public FluidFlowModel() {
        super( UnitSet.METRIC );
        getClock().addSimulationTimeChangeListener( new VoidFunction1<Double>() {
            public void apply( final Double dt ) {
                if ( dt > 0 ) { stepInTime( dt ); }
            }
        } );

        //Add the sensors at positions that were determined empirically
        addPressureSensor( new PressureSensor( UserComponents.pressureSensor0, this, 3, 1.188 ) );
        addPressureSensor( new PressureSensor( UserComponents.pressureSensor1, this, 3, 1.188 ) );
        addVelocitySensor( new FPAFVelocitySensor( UserComponents.velocitySensor0, this, 1, 0.4735 ) );
        addVelocitySensor( new FPAFVelocitySensor( UserComponents.velocitySensor1, this, 1, 0.4735 ) );
    }

    //When time passes, the circular particles and rectangular dye moves
    private void stepInTime( double dt ) {
        updateParticles( dt );
    }

    //Update the red dots
    private void updateParticles( double dt ) {
        double value = dropperRate.get() / 100.0;
        if ( random.nextDouble() < value && dropperEnabled.get() ) {

            //Don't show any particles near the edges, since their velocity should be zero in physical reality (or a full-blown fluid dynamics simulation)
            double min = 0.1;
            double max = 1 - min;
            double range = max - min;

            addDrop( pipe.getMinX() + 1E-6, random.nextDouble() * range + min, 0.1, Color.red );
        }
        ArrayList<Particle> toRemove = new ArrayList<Particle>();
        for ( Particle particle : particles ) {
            boolean remove = updateParticle( dt, particle );
            if ( remove ) {
                toRemove.add( particle );
            }
        }
        for ( Particle particle : toRemove ) {
            removeParticle( particle );
        }
    }

    private void removeParticle( Particle particle ) {
        particles.remove( particle );
        particle.notifyRemoved();
    }

    //Gets the pressure at the specified location
    @Override public double getPressure( double x, double y ) {
        Option<ImmutableVector2D> velocity = getVelocity( x, y );

        double vSquared = velocity.isSome() ? velocity.get().getMagnitudeSq() : 0.0;
        double K = 101325;//choose a base value for pipe internal pressure, also ensure that pressure is never negative in the pipe in a narrow region
        if ( pipe.getShape().contains( x, y ) ) {
            return K - 0.5 * liquidDensity.get() * vSquared - liquidDensity.get() * gravity.get() * y;
        }
        else if ( y < 0 ) {
            return Double.NaN;
        }
        else {
            return super.getPressure( x, y );
        }
    }

    //Returns true if the particle should be removed because it exited the model.
    private boolean updateParticle( double dt, Particle particle ) {
        double x2 = particle.getX() + pipe.getTweakedVx( particle.getX(), particle.getY() ) * dt;
        if ( x2 >= pipe.getMaxX() ) {
            return true;
        }
        else {
            particle.setX( x2 );
            return false;
        }
    }

    public Particle[] getParticles() {
        return particles.toArray( new Particle[particles.size()] );
    }

    public void addParticleAddedObserver( VoidFunction1<Particle> listener ) {
        particleAddedObservers.add( listener );
    }

    public Option<ImmutableVector2D> getVelocity( double x, double y ) {
        if ( pipe.contains( x, y ) ) {
            return new Option.Some<ImmutableVector2D>( pipe.getTweakedVelocity( x, y ) );//assumes velocity same at all y along a specified x
        }
        else {
            return new Option.None<ImmutableVector2D>();
        }
    }

    //Attach an observer that will be notified when the velocity might have changed
    public void addVelocityUpdateListener( SimpleObserver observer ) {

        //When the pipe changes shape, flow rate or friction, the velocity at each position might change
        pipe.addShapeChangeListener( observer );
        pipe.flowRate.addObserver( observer );
        pipe.friction.addObserver( observer );
    }

    public void addPressureChangeObserver( SimpleObserver updatePressure ) {
        super.addPressureChangeObserver( updatePressure );
        pipe.addShapeChangeListener( updatePressure );
        pipe.flowRate.addObserver( updatePressure );
        pipe.friction.addObserver( updatePressure );
    }

    @Override public boolean isInWaterTowerWater( final double x, final double y ) {
        return false;
    }

    public void pourFoodColoring() {
        double x0 = pipe.getMinX() + 1E-6;
        double width = 0.75;

        //Okay to have the fluid go all the way to the sides of the pipe, since friction is accounted for elsewhere
        double yMin = 0;
        double yMax = 1;
        double delta = 0.1;

        //top
        for ( double x = x0; x <= x0 + width; x += delta * 2 ) {
            for ( double y = yMin + delta; y <= yMax - delta; y += delta ) {
                addDrop( x, y, 0.05, Color.black );
            }
        }
    }

    //Reset the model for "reset all"
    @Override public void reset() {
        while ( particles.size() > 0 ) {
            removeParticle( particles.get( 0 ) );
        }
        super.reset();
        pipe.reset();
        dropperRate.reset();
        dropperEnabled.reset();
        fluxMeter.reset();
    }

    //Add a drop at a random location
    public void addDrop( final double x, double y, final double radius, final Paint paint ) {

        final Particle newParticle = new Particle( x, y, pipe, radius, paint );
        particles.add( newParticle );
        for ( VoidFunction1<Particle> particleAddedObserver : particleAddedObservers ) {
            particleAddedObserver.apply( newParticle );
        }
    }
}