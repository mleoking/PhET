// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.model;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
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
    private final ArrayList<FoodColoring> foodColorings = new ArrayList<FoodColoring>();
    public final FluxMeter fluxMeter = new FluxMeter( pipe );

    //Observers
    private final ArrayList<VoidFunction1<Particle>> particleAddedObservers = new ArrayList<VoidFunction1<Particle>>();
    private final ArrayList<VoidFunction1<FoodColoring>> foodColoringObservers = new ArrayList<VoidFunction1<FoodColoring>>();

    //percent probability to drop in each frame
    public final Property<Double> dropperRate = new Property<Double>( 33.0 );

    //true if the dropper is dropping in red dots
    public final Property<Boolean> dropperEnabled = new Property<Boolean>( true );

    public FluidFlowModel() {
        super( UnitSet.METRIC );
        getClock().addClockListener( new ClockAdapter() {
            @Override
            public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent );
            }
        } );

        //Add the sensors at positions that were determined empirically
        addPressureSensor( new PressureSensor( this, 3, 1.188 ) );
        addPressureSensor( new PressureSensor( this, 3, 1.188 ) );
        addVelocitySensor( new FPAFVelocitySensor( this, 1, 0.4735 ) );
        addVelocitySensor( new FPAFVelocitySensor( this, 1, 0.4735 ) );
    }

    //When time passes, the circular particles and rectangular dye moves
    private void stepInTime( ClockEvent clockEvent ) {
        updateParticles( clockEvent.getSimulationTimeChange() );
        updateFoodColoring( clockEvent.getSimulationTimeChange() );
    }

    //Update the solid red patches (food coloring)
    private void updateFoodColoring( double dt ) {
        ArrayList<FoodColoring> toRemove = new ArrayList<FoodColoring>();
        for ( FoodColoring foodColoring : foodColorings ) {
            boolean canRemove = true;
            ArrayList<Particle> p = foodColoring.getParticles();
            for ( Particle particle : p ) {
                boolean remove = updateParticle( dt, particle );
                canRemove = canRemove && remove;//only remove if all particles have exited
            }
            foodColoring.notifyObservers();
            if ( canRemove ) {
                toRemove.add( foodColoring );
            }
        }
        for ( FoodColoring foodColoring : toRemove ) {
            removeFoodColoring( foodColoring );
        }
    }

    //Update the red dots
    private void updateParticles( double dt ) {
        double value = dropperRate.get() / 100.0;
        if ( random.nextDouble() < value && dropperEnabled.get() ) {
            addDrop();
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

    private void removeFoodColoring( FoodColoring foodColoring ) {
        foodColorings.remove( foodColoring );
        foodColoring.notifyRemoved();
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

    public void addFoodColoringObserver( VoidFunction1<FoodColoring> listener ) {
        foodColoringObservers.add( listener );
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

    public void addFluidChangeObserver( SimpleObserver updatePressure ) {
        super.addFluidChangeObserver( updatePressure );
        pipe.addShapeChangeListener( updatePressure );
        pipe.flowRate.addObserver( updatePressure );
        pipe.friction.addObserver( updatePressure );
    }

    public void pourFoodColoring() {
        final FoodColoring foodColoring = new FoodColoring( pipe.getMinX() + 1E-6, 0.75, pipe );
        for ( VoidFunction1<FoodColoring> foodColoringObserver : foodColoringObservers ) {
            foodColoringObserver.apply( foodColoring );
        }
        foodColorings.add( foodColoring );
    }

    //Reset the model for "reset all"
    @Override public void reset() {
        while ( particles.size() > 0 ) {
            removeParticle( particles.get( 0 ) );
        }
        while ( foodColorings.size() > 0 ) {
            removeFoodColoring( foodColorings.get( 0 ) );
        }
        super.reset();
        pipe.reset();
        dropperRate.reset();
        dropperEnabled.reset();
        fluxMeter.reset();
    }

    //Add a drop at a random location
    public void addDrop() {

        //Don't show any particles near the edges, since their velocity should be zero in physical reality (or a full-blown fluid dynamics simulation)
        double min = 0.1;
        double max = 1 - min;
        double range = max - min;
        final Particle newParticle = new Particle( pipe.getMinX() + 1E-6, random.nextDouble() * range + min, pipe, 0.1 );
        particles.add( newParticle );
        for ( VoidFunction1<Particle> particleAddedObserver : particleAddedObservers ) {
            particleAddedObserver.apply( newParticle );
        }
    }
}