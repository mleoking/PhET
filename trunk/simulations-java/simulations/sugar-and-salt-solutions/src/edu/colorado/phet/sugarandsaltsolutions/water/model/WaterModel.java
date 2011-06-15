// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;

import static edu.colorado.phet.sugarandsaltsolutions.water.view.S3Element.CHLORINE_RADIUS;
import static edu.colorado.phet.sugarandsaltsolutions.water.view.S3Element.SODIUM_RADIUS;

/**
 * Model for "water" tab for sugar and salt solutions.
 *
 * @author Sam Reid
 */
public class WaterModel extends SugarAndSaltSolutionModel {

    //Lists of all model objects
    public final ParticleList<WaterMolecule> waterList = new ParticleList<WaterMolecule>();
    public final ParticleList<DefaultParticle> sodiumList = new ParticleList<DefaultParticle>();
    public final ParticleList<DefaultParticle> chlorineList = new ParticleList<DefaultParticle>();
    public final ParticleList<Sucrose> sugarMoleculeList = new ParticleList<Sucrose>();

    //Listeners who are called back when the physics updates
    private ArrayList<VoidFunction0> frameListeners = new ArrayList<VoidFunction0>();

    //Box2d world which updates the physics
    private World world;

    private Random random = new Random();

    public final double beakerWidth = 40E-10;
    public final double beakerHeight = beakerWidth * 0.6;
    private final double box2DWidth = 20;

    //units for water molecules are in SI

    //Beaker floor should be about 40 angstroms, to accommodate about 20 water molecules side-to-side
    //But keep box2d within -10..10 (i.e. 20 boxes wide), so scale factor is about
    double scaleFactor = box2DWidth / beakerWidth;
    private final ModelViewTransform modelToBox2D = ModelViewTransform.createSinglePointScaleMapping( new Point(), new Point(), scaleFactor );

    //Shapes for boundaries, used in periodic boundary conditions
    private ImmutableRectangle2D bottomWallShape;
    private ImmutableRectangle2D rightWallShape;
    private ImmutableRectangle2D leftWallShape;
    private ImmutableRectangle2D topWallShape;

    private int DEFAULT_NUM_WATERS = 180;

    //So we don't have to reallocate zeros all the time
    private final Vec2 zero = new Vec2();

    //Properties for developer controls
    public final Property<Integer> k = new Property<Integer>( 100 );
    public final Property<Integer> pow = new Property<Integer>( 2 );
    public final Property<Integer> randomness = new Property<Integer>( 100 );
    public final Property<Double> minInteractionDistance = new Property<Double>( 0.5 );
    public final Property<Double> maxInteractionDistance = new Property<Double>( 2.0 );
    public final Property<Double> probabilityOfInteraction = new Property<Double>( 0.5 );
    public final Property<Double> timeScale = new Property<Double>( 0.5 );
    public final Property<Integer> iterations = new Property<Integer>( 100 );
    private final VoidFunction1<VoidFunction0> addFrameListener = new VoidFunction1<VoidFunction0>() {
        public void apply( VoidFunction0 waterMolecule ) {
            addFrameListener( waterMolecule );
        }
    };

    //User settings
    public final SettableProperty<Boolean> showSugarAtoms = new Property<Boolean>( false );
    public final SettableProperty<Boolean> hideWater = new Property<Boolean>( false );//Allow the user to hide the water molecules so they can focus on the solutes
    public final DoubleProperty oxygenCharge = new DoubleProperty( -0.8 );
    public final DoubleProperty hydrogenCharge = new DoubleProperty( 0.4 );
    public final DoubleProperty ionCharge = new DoubleProperty( 1.0 );

    public WaterModel() {
        //Set the bounds of the physics engine.  The docs say things should be mostly between 0.1 and 10 units
        AABB worldAABB = new AABB();
        worldAABB.lowerBound = new Vec2( -200, -200 );
        worldAABB.upperBound = new Vec2( 200, 200 );

        //Create the Box2D world with no gravity
        world = new World( worldAABB, new Vec2( 0, 0 ), true );

        //Create beaker floor
        double glassThickness = 1E-10;
        bottomWallShape = new ImmutableRectangle2D( -beakerWidth / 2, 0, beakerWidth, glassThickness );
        topWallShape = new ImmutableRectangle2D( -beakerWidth / 2, beakerHeight, beakerWidth, glassThickness + beakerHeight );

        //Create sides
        rightWallShape = new ImmutableRectangle2D( beakerWidth / 2, 0, glassThickness, beakerHeight );
        leftWallShape = new ImmutableRectangle2D( -beakerWidth / 2, 0, glassThickness, beakerHeight );

        //Move to a stable state on startup
        //Commented out because it takes too long
//        long startTime = System.currentTimeMillis();
//        for ( int i = 0; i < 10; i++ ) {
//            world.step( (float) ( clock.getDt() * 10 ), 1 );
//        }
//        System.out.println( "stable start time: " + ( System.currentTimeMillis() - startTime ) );

        //Set up initial state, same as reset() method would do
        initModel();
    }

    //Adds some NaCl molecules by adding nearby sodium and chlorine pairs, electrostatic forces are responsible for keeping them together until they are pulled apart by water
    public void addSalt() {
        final double separation = CHLORINE_RADIUS + SODIUM_RADIUS;

        addSodiumIon( 0, beakerHeight / 2 );
        addChlorineIon( separation, beakerHeight / 2 );

        //Only showing one NaCl because if there are 2, the opposite partners join together too easily
//        addChlorineIon( 0, beakerHeight / 2 + separation );
//        addSodiumIon( separation, beakerHeight / 2 + separation );
    }

    //Adds a sugar crystal near the center of the screen
    public void addSugar() {
        double x = 0;
        double y = beakerHeight / 2;
        final double delta = beakerHeight / 4 * 0.87;
        addSugar( x, y - delta / 2 );
        addSugar( x, y + delta / 2 );
    }

    private void addSugar( double x, double y ) {
        sugarMoleculeList.add( newSugar( x, y ) );
    }

    public Sucrose newSugar( double x, double y ) {
        return new Sucrose( world, modelToBox2D, x, y, 0, 0, 0, addFrameListener, oxygenCharge, hydrogenCharge );
    }

    //Adds some random sodium particles
    private void addSodiumParticles( long seed ) {
        Random random = new Random( seed );
        for ( int i = 0; i < 10; i++ ) {
            float float1 = (float) ( ( random.nextFloat() - 0.5 ) * 2 );
            final double x = float1 * beakerWidth / 2;
            final double y = random.nextFloat() * beakerHeight;
            addSodiumIon( x, y );
        }
    }

    //Adds a chlorine ion
    public void addChlorineIon( double x, double y ) {
        chlorineList.add( new DefaultParticle( world, modelToBox2D, x, y, 0, 0, 0, addFrameListener, ionCharge.times( -1 ), CHLORINE_RADIUS ) );
    }

    public void addSodiumIon( double x, double y ) {
        sodiumList.add( new DefaultParticle( world, modelToBox2D, x, y, 0, 0, 0, addFrameListener, ionCharge, SODIUM_RADIUS ) );
    }

    //Adds default water particles
    private void addWaterParticles( long seed, int numParticles ) {
        Random random = new Random( seed );
        for ( int i = 0; i < numParticles; i++ ) {
            float float1 = (float) ( ( random.nextFloat() - 0.5 ) * 2 );
            final double x = float1 * beakerWidth / 2;
            final double y = random.nextFloat() * beakerHeight;
            final float angle = (float) ( random.nextFloat() * Math.PI * 2 );
            addWater( x, y, angle );
        }
    }

    //Adds a single water molecule
    public void addWater( double x, double y, float angle ) {
        waterList.add( new WaterMolecule( world, modelToBox2D, x, y, 0, 0, angle, addFrameListener, oxygenCharge, hydrogenCharge ) );
    }

    public void addWaterAddedListener( VoidFunction1<WaterMolecule> waterAddedListener ) {
        waterList.itemAddedListeners.add( waterAddedListener );
    }

    @Override protected void updateModel( double dt ) {
        //Ignore super update for now
//        super.updateModel( dt );

        //Apply a random force so the system doesn't settle down, setting random velocity looks funny
        for ( WaterMolecule waterMolecule : waterList.list ) {
            float rand1 = ( random.nextFloat() - 0.5f ) * 2;
            float rand2 = ( random.nextFloat() - 0.5f ) * 2;
            waterMolecule.body.applyForce( new Vec2( rand1 * randomness.get(), rand2 * randomness.get() ), waterMolecule.body.getPosition() );
        }

        long t = System.currentTimeMillis();
        //Apply coulomb forces between all pairs of particles
        for ( Molecule molecule : getAllMolecules() ) {
            for ( Atom atom : molecule.atoms ) {
                //Only apply the force in some interactions, to improve performance and increase randomness
                if ( Math.random() < probabilityOfInteraction.get() ) {
                    molecule.body.applyForce( getCoulombForce( atom.particle ), atom.particle.getBox2DPosition() );
                }
            }
        }
        long t2 = System.currentTimeMillis();
//        System.out.println( "delta = " + ( t2 - t ) );

        //Box2D will exception unless values are within its sweet spot range.
        //if DT gets too low, it is hard to recover from assertion errors in box2D
        //It is supposed to run at 60Hz, with velocities not getting too large (300m/s is too large): http://www.box2d.org/forum/viewtopic.php?f=4&t=1205
        world.step( (float) ( dt * timeScale.get() ), iterations.get() );

        //Apply periodic boundary conditions
        applyPeriodicBoundaryConditions( getAllMolecules() );

        //Factor out center of mass motion so no large scale drifts can occur
        Vec2 initTotalVelocity = getTotalMomentum();
        for ( Molecule molecule : getAllMolecules() ) {
            Vec2 v = molecule.body.getLinearVelocity();
            final Vec2 delta = initTotalVelocity.mul( (float) ( -1 / getTotalMass() ) );
            molecule.body.setLinearVelocity( v.add( delta ) );
        }
//        System.out.println( "init tot mom = " + initTotalVelocity + ", after = " + getTotalMomentum() );

        //Notify listeners that the model changed
        for ( VoidFunction0 frameListener : frameListeners ) {
            frameListener.apply();
        }
    }

    private Vec2 getTotalMomentum() {
        Vec2 totalMomentum = new Vec2();
        for ( Molecule molecule : getAllMolecules() ) {
            Vec2 v = molecule.body.getLinearVelocity();
            totalMomentum.x += v.x * molecule.body.getMass();
            totalMomentum.y += v.y * molecule.body.getMass();
        }
        return totalMomentum;
    }

    private double getTotalMass() {
        double m = 0.0;
        for ( Molecule molecule : getAllMolecules() ) {
            m += molecule.body.getMass();
        }
        return m;
    }

    private ArrayList<Molecule> getAllMolecules() {
        return new ArrayList<Molecule>() {{
            addAll( waterList.list );
            addAll( sugarMoleculeList.list );
            addAll( sodiumList.list );
            addAll( chlorineList.list );
        }};
    }

    //Move particles from one side of the screen to the other if they went out of bounds
    private void applyPeriodicBoundaryConditions( ArrayList<? extends Particle> list ) {
        for ( Particle particle : list ) {
            if ( particle.getModelPosition().getX() > rightWallShape.x ) {
                particle.setModelPosition( new ImmutableVector2D( leftWallShape.getMaxX(), particle.getModelPosition().getY() ) );
            }
            if ( particle.getModelPosition().getX() < leftWallShape.getMaxX() ) {
                particle.setModelPosition( new ImmutableVector2D( rightWallShape.x, particle.getModelPosition().getY() ) );
            }
            if ( particle.getModelPosition().getY() < bottomWallShape.getMaxY() ) {
                particle.setModelPosition( new ImmutableVector2D( particle.getModelPosition().getX(), topWallShape.y ) );
            }
            if ( particle.getModelPosition().getY() > topWallShape.y ) {
                particle.setModelPosition( new ImmutableVector2D( particle.getModelPosition().getX(), bottomWallShape.getMaxY() ) );
            }
        }
    }

    //Gets the force on a single particle
    private Vec2 getCoulombForce( Particle target ) {
        Vec2 sumForces = new Vec2();
        for ( Molecule m : getAllMolecules() ) {
            for ( Atom atom : m.atoms ) {
                sumForces = sumForces.add( getCoulombForce( atom.particle, target ) );
            }
        }
        return sumForces;
    }

    //Get the contribution to the total coulomb force from a single source
    private Vec2 getCoulombForce( Particle source, Particle target ) {
        //Precompute for performance reasons
        final Vec2 sourceBox2DPosition = source.getBox2DPosition();
        final Vec2 targetBox2DPosition = target.getBox2DPosition();

        if ( source == target ||
             ( sourceBox2DPosition.x == targetBox2DPosition.x && sourceBox2DPosition.y == targetBox2DPosition.y ) ) {
            return zero;
        }
        Vec2 r = sourceBox2DPosition.sub( targetBox2DPosition );
        double distance = r.length();
//        System.out.println( distance );

        //Limit the max force or objects will get accelerated too much
        //After particles get far enough apart, just ignore the force.  Otherwise Na+ and Cl- will seek each other out from far away.
        //Units are box2d units
        final double MIN = minInteractionDistance.get();
        final double MAX = maxInteractionDistance.get();
        if ( distance < MIN ) {
            distance = MIN;
        }
        else if ( distance > MAX ) {
            return zero;
        }

        double q1 = source.getCharge();
        double q2 = target.getCharge();

        double distanceFunction = 1 / Math.pow( distance, pow.get() );
        double magnitude = -k.get() * q1 * q2 * distanceFunction;
        r.normalize();
        return r.mul( (float) magnitude );
    }

    //Get all bodies in the model
    public ArrayList<WaterMolecule> getWaterList() {
        return waterList.list;
    }

    //Register for a callback when the model steps
    public void addFrameListener( VoidFunction0 listener ) {
        frameListeners.add( listener );
    }

    //Resets the model, clearing water molecules and starting over
    @Override public void reset() {
        initModel();
    }

    //Set up the initial model state, used on init and after reset
    private void initModel() {
        waterList.clear( world );
        sodiumList.clear( world );
        chlorineList.clear( world );
        sugarMoleculeList.clear( world );

        //Add water particles
        addWaterParticles( System.currentTimeMillis(), DEFAULT_NUM_WATERS );
    }

    public ArrayList<DefaultParticle> getSodiumIonList() {
        return sodiumList.list;
    }

    public void addSodiumIonAddedListener( VoidFunction1<DefaultParticle> listener ) {
        sodiumList.itemAddedListeners.add( listener );
    }

    public ArrayList<DefaultParticle> getChlorineIonList() {
        return chlorineList.list;
    }

    public void addChlorineIonAddedListener( VoidFunction1<DefaultParticle> createNode ) {
        chlorineList.itemAddedListeners.add( createNode );
    }

    //Gets a random number within the horizontal range of the beaker
    public double getRandomX() {
        return (float) ( SugarAndSaltSolutionsApplication.random.nextFloat() * beakerWidth - beakerWidth / 2 );
    }

    //Gets a random number within the vertical range of the beaker
    public double getRandomY() {
        return (float) ( SugarAndSaltSolutionsApplication.random.nextFloat() * beakerHeight );
    }

    public void addSugarAddedListener( VoidFunction1<Sucrose> createNode ) {
        sugarMoleculeList.itemAddedListeners.add( createNode );
    }
}