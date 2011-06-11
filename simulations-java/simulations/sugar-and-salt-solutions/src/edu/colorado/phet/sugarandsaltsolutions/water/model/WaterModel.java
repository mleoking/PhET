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

    private int DEFAULT_NUM_WATERS = 150;

    //So we don't have to reallocate zeros all the time
    private final Vec2 zero = new Vec2();

    //Properties for developer controls
    public final Property<Integer> k = new Property<Integer>( 4000 );
    public final Property<Integer> pow = new Property<Integer>( 2 );
    public final Property<Integer> randomness = new Property<Integer>( 100 );
    private final VoidFunction1<VoidFunction0> addFrameListener = new VoidFunction1<VoidFunction0>() {
        public void apply( VoidFunction0 waterMolecule ) {
            addFrameListener( waterMolecule );
        }
    };

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

        addChlorineIon( 0, beakerHeight / 2 + separation );
        addSodiumIon( separation, beakerHeight / 2 + separation );
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
        sugarMoleculeList.add( new Sucrose( world, modelToBox2D, x, y, 0, 0, 0, addFrameListener ) );
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
        chlorineList.add( new DefaultParticle( world, modelToBox2D, x, y, 0, 0, 0, addFrameListener, -1, CHLORINE_RADIUS ) );
    }

    public void addSodiumIon( double x, double y ) {
        sodiumList.add( new DefaultParticle( world, modelToBox2D, x, y, 0, 0, 0, addFrameListener, +1, SODIUM_RADIUS ) );
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
        waterList.add( new WaterMolecule( world, modelToBox2D, x, y, 0, 0, angle, addFrameListener ) );
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

        //Apply coulomb forces between all pairs of particles
        for ( Molecule molecule : getAllMolecules() ) {
            for ( Atom atom : molecule.atoms ) {
                molecule.body.applyForce( getCoulombForce( atom.particle ), atom.particle.getBox2DPosition() );
            }
        }
        world.step( (float) dt / 2, 50 );

        //Apply periodic boundary conditions
        applyPeriodicBoundaryConditions( getAllMolecules() );

        //Notify listeners that the model changed
        for ( VoidFunction0 frameListener : frameListeners ) {
            frameListener.apply();
        }
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
        if ( source == target ||
             ( source.getBox2DPosition().x == target.getBox2DPosition().x && source.getBox2DPosition().y == target.getBox2DPosition().y ) ) {
            return zero;
        }
        Vec2 r = source.getBox2DPosition().sub( target.getBox2DPosition() );
        double distance = r.length();
//        System.out.println( "distance = " + distance );

        //Optimize forces for the distance between a sodium and chlorine so it is the strongest bond.
        //Units are box2d units
        if ( distance < 1.2 ) { distance = 1.2; }

        double q1 = source.getCharge();
        double q2 = target.getCharge();

        //Use a gaussian so that NaCl has a strong affinity
//        double x0 = 1.29499;
//        double distanceFunction = Math.exp( -Math.pow( distance - x0, 2 ) );

        double distanceFunction = 1 / Math.pow( distance, pow.get() );
        double magnitude = -k.get() * q1 * q2 * distanceFunction;

//        System.out.println( "distance = " + distance + ", mag = " + magnitude );
//        System.out.println( distance + "\t" + magnitude );

//        double MAX = 1000;
//        double MIN = 1E-3;
//        if ( Math.abs( mag ) > MAX && q1 * q2 > 0 ) {
//            magnitude = MAX;
//        }
//        else if ( magnitude < MIN ) {
//            return zero;
//        }
//        System.out.println( magnitude );
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