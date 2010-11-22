package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;

/**
 * @author Sam Reid
 */
public class FluidFlowModel extends FluidPressureAndFlowModel implements VelocitySensor.Context {
    private Pipe pipe = new Pipe();
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    private Random random = new Random();
    private ArrayList<VoidFunction1<Particle>> particleAddedObservers = new ArrayList<VoidFunction1<Particle>>();
    private ArrayList<VoidFunction1<FoodColoring>> foodColoringObservers = new ArrayList<VoidFunction1<FoodColoring>>();
    //    private Property<Boolean> dropperOnProperty = new Property<Boolean>( false );
    private Property<Double> dropperRateProperty = new Property<Double>( 10.0 );//percent probability to drop in each frame
    private ArrayList<FoodColoring> foodColorings = new ArrayList<FoodColoring>();

    public FluidFlowModel() {
        getClock().addClockListener( new ClockAdapter() {
            @Override
            public void clockTicked( ClockEvent clockEvent ) {
                double value = dropperRateProperty.getValue() / 100.0;
                if ( random.nextDouble() < value ) {
                    addDrop();
                }

                //UPDATE PARTICLES
                final double dt = clockEvent.getSimulationTimeChange();
                {
                    ArrayList<Particle> toRemove = new ArrayList<Particle>();
                    for ( int i = 0; i < particles.size(); i++ ) {
                        Particle particle = particles.get( i );
                        boolean remove = updateParticle( dt, particle );
                        if ( remove ) {
                            toRemove.add( particle );
                        }
                    }
                    for ( int i = 0; i < toRemove.size(); i++ ) {
                        removeParticle( toRemove.get( i ) );
                    }
                }

                //UPDATE FOOD COLORING
                {
                    ArrayList<FoodColoring> toRemove = new ArrayList<FoodColoring>();
                    for ( int i = 0; i < foodColorings.size(); i++ ) {
                        boolean canRemove = true;
                        FoodColoring foodColoring = foodColorings.get( i );
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
                    for ( int i = 0; i < toRemove.size(); i++ ) {
                        removeFoodColoring( toRemove.get( i ) );
                    }
                }
            }
        } );

        // TODO: handle creation of sensors when one is dragged off of the "pile"
        addPressureSensor( new PressureSensor( this, 3.8, 1.1882302540898015 ) );
        addPressureSensor( new PressureSensor( this, 3.8, 1.1882302540898015 ) );
        addVelocitySensor( new VelocitySensor( this, 3.8, 0.973501677688827 ) );
        addVelocitySensor( new VelocitySensor( this, 3.8, 0.973501677688827 ) );
    }

    private void removeFoodColoring( FoodColoring foodColoring ) {
        foodColorings.remove( foodColoring );
        foodColoring.notifyRemoved();
    }

    private void removeParticle( Particle particle ) {
        particles.remove( particle );
        particle.notifyRemoved();
    }

    @Override
    public double getPressure( Point2D position ) {
        ImmutableVector2D velocity = getVelocity( position.getX(), position.getY() );
        double vSquared = velocity.getMagnitudeSq();
        double K = 101325;//choose a base value for pipe internal pressure, also ensure that pressure is never negative in the pipe in a narrow region
        if ( pipe.getShape().contains( position ) ) {
            double pressure = K - 0.5 * getLiquidDensity() * vSquared - getLiquidDensity() * getGravity() * position.getY();
            return pressure;
        }
        else if ( position.getY() < 0 ) {
            return Double.NaN;
        }
        else {
            return super.getPressure( position );
        }
    }

    /**
     * Returns true if the particle should be removed because it exited the model.
     *
     * @param dt
     * @param particle
     * @return
     */
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

    public Pipe getPipe() {
        return pipe;
    }

    public Particle[] getParticles() {
        return particles.toArray( new Particle[0] );
    }

    public void addParticleAddedObserver( VoidFunction1<Particle> listener ) {
        particleAddedObservers.add( listener );
    }

    public void addFoodColoringObserver( VoidFunction1<FoodColoring> listener ) {
        foodColoringObservers.add( listener );
    }

    public ImmutableVector2D getVelocity( double x, double y ) {
        if ( pipe.contains( x, y ) ) {
            return pipe.getTweakedVelocity( x, y );//assumes velocity same at all y along a specified x
        }
        else {
            return new ImmutableVector2D();
        }
    }

    public void addVelocityUpdateListener( SimpleObserver observer ) {
        getPipe().addShapeChangeListener( observer );
    }

    public void addFluidChangeObserver( SimpleObserver updatePressure ) {
        super.addFluidChangeObserver( updatePressure );
        pipe.addShapeChangeListener( updatePressure );
    }

    public Property<Double> getDropperRateProperty() {
        return dropperRateProperty;
    }

    public void pourFoodColoring() {
        final FoodColoring foodColoring = new FoodColoring( pipe.getMinX() + 1E-6, 0.75, pipe );
        for ( VoidFunction1<FoodColoring> foodColoringObserver : foodColoringObservers ) {
            foodColoringObserver.apply( foodColoring );
        }
        foodColorings.add( foodColoring );
    }

    @Override
    public void reset() {
        while ( particles.size() > 0 ) {
            removeParticle( particles.get( 0 ) );
        }
        while ( foodColorings.size() > 0 ) {
            removeFoodColoring( foodColorings.get( 0 ) );
        }
        super.reset();
        pipe.reset();
        dropperRateProperty.reset();
        //TODO: remove particle and food coloring
    }

    public void addDrop() {
        final Particle newParticle = new Particle( pipe.getMinX() + 1E-6, random.nextDouble(), pipe, 0.1 );
        particles.add( newParticle );
        for ( VoidFunction1<Particle> particleAddedObserver : particleAddedObservers ) {
            particleAddedObserver.apply( newParticle );
        }
    }
}