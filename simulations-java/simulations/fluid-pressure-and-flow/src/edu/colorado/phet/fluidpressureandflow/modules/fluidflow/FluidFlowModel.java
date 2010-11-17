package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;

/**
 * @author Sam Reid
 */
public class FluidFlowModel extends FluidPressureAndFlowModel {
    private Pipe pipe = new Pipe();
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    private Random random = new Random();
    private ArrayList<Function1<Particle, Void>> particleAddedObservers = new ArrayList<Function1<Particle, Void>>();
    private ArrayList<Function1<FoodColoring, Void>> foodColoringObservers = new ArrayList<Function1<FoodColoring, Void>>();
    private VelocitySensor velocitySensor0 = new VelocitySensor( 0, 0, this );
    private VelocitySensor velocitySensor1 = new VelocitySensor( -1, 1, this );
    private Property<Boolean> dropperOnProperty = new Property<Boolean>( false );
    private ArrayList<FoodColoring> foodColorings = new ArrayList<FoodColoring>();

    public FluidFlowModel() {
        getClock().addClockListener( new ClockAdapter() {
            @Override
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                if ( dropperOnProperty.getValue() ) {
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
                        Particle particle = toRemove.get( i );
                        particles.remove( particle );
                        particle.notifyRemoved();
                    }
                }

                //UPDATE FOOD COLORING
                {
                    ArrayList<FoodColoring> toRemove = new ArrayList<FoodColoring>();
                    for ( int i = 0; i < foodColorings.size(); i++ ) {
                        FoodColoring foodColoring = foodColorings.get( i );
                        ArrayList<Particle> p = foodColoring.getParticles();
                        for ( Particle particle : p ) {
                            boolean remove = updateParticle( dt, particle );
                            //todo: handle removes
                        }
                        foodColoring.notifyObservers();
                    }
                    for ( int i = 0; i < toRemove.size(); i++ ) {
                        FoodColoring particle = toRemove.get( i );
                        foodColorings.remove( particle );
                        particle.notifyRemoved();
                    }
                }
            }
        } );
        addPressureSensor( new PressureSensor( this, 1, 0 ) );
        addPressureSensor( new PressureSensor( this, -4, 1 ) );
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
        double x = particle.getX();
        ImmutableVector2D velocity = pipe.getVelocity( particle.getX(), particle.getY() );
        ImmutableVector2D xVelocity = new ImmutableVector2D( velocity.getX(), 0 );
        double x2 = x + ( pipe.getSpeed( x ) / ( velocity.getMagnitude() / xVelocity.getMagnitude() ) ) * dt;
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

    public void addParticleAddedObserver( Function1<Particle, Void> listener ) {
        particleAddedObservers.add( listener );
    }

    public void addFoodColoringObserver( Function1<FoodColoring, Void> listener ) {
        foodColoringObservers.add( listener );
    }

    public ImmutableVector2D getVelocity( double x, double y ) {
        if ( pipe.contains( x, y ) ) {
            return pipe.getVelocity( x, y );//assumes velocity same at all y along a specified x
        }
        else {
            return new ImmutableVector2D();
        }
    }

    public void addFluidChangeObserver( SimpleObserver updatePressure ) {
        super.addFluidChangeObserver( updatePressure );
        pipe.addShapeChangeListener( updatePressure );
    }

    public VelocitySensor getVelocitySensor0() {
        return velocitySensor0;
    }

    public VelocitySensor getVelocitySensor1() {
        return velocitySensor1;
    }

    public Property<Boolean> getDropperOnProperty() {
        return dropperOnProperty;
    }

    public void pourFoodColoring() {
        final FoodColoring foodColoring = new FoodColoring( pipe.getMinX() + 1E-6, 0.75, pipe );
        for ( Function1<FoodColoring, Void> foodColoringObserver : foodColoringObservers ) {
            foodColoringObserver.apply( foodColoring );
        }
        foodColorings.add( foodColoring );
    }

    @Override
    public void reset() {
        super.reset();
        pipe.reset();
        velocitySensor0.reset();
        dropperOnProperty.reset();
        dropperOnProperty.reset();
        //TODO: remove particle and food coloring
    }

    public void addDrop() {
        final Particle newParticle = new Particle( pipe.getMinX() + 1E-6, random.nextDouble(), pipe );
        particles.add( newParticle );
        for ( Function1<Particle, Void> particleAddedObserver : particleAddedObservers ) {
            particleAddedObserver.apply( newParticle );
        }
    }
}