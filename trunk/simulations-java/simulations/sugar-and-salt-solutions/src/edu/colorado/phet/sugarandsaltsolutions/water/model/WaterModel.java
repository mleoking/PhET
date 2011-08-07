// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.model.AbstractSugarAndSaltSolutionsModel;

import static edu.colorado.phet.sugarandsaltsolutions.water.view.Element.CHLORINE_RADIUS;
import static edu.colorado.phet.sugarandsaltsolutions.water.view.Element.SODIUM_RADIUS;

/**
 * Model for "water" tab for sugar and salt solutions.
 *
 * @author Sam Reid
 */
public class WaterModel extends AbstractSugarAndSaltSolutionsModel {

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
    public final Property<Integer> randomness = new Property<Integer>( 5 );
    public final Property<Double> minInteractionDistance = new Property<Double>( 0.05 );
    public final Property<Double> maxInteractionDistance = new Property<Double>( 2.0 );
    public final Property<Double> probabilityOfInteraction = new Property<Double>( 0.5 );
    public final Property<Double> timeScale = new Property<Double>( 0.4 );
    public final Property<Integer> iterations = new Property<Integer>( 50 );
    private final VoidFunction1<VoidFunction0> addFrameListener = new VoidFunction1<VoidFunction0>() {
        public void apply( VoidFunction0 waterMolecule ) {
            addFrameListener( waterMolecule );
        }
    };

    //User settings
    public final Property<Boolean> showSugarAtoms = new Property<Boolean>( false );
    public final Property<Boolean> showWaterCharges = new Property<Boolean>( false );
    public final DoubleProperty oxygenCharge = new DoubleProperty( -0.8 );
    public final DoubleProperty hydrogenCharge = new DoubleProperty( 0.4 );
    public final DoubleProperty ionCharge = new DoubleProperty( 1.0 );
    public final BooleanProperty coulombForceOnAllMolecules = new BooleanProperty( true );

    //Turn down forces after salt disassociates
    private double timeSinceSaltAdded = 0;

    public WaterModel() {
        super( new ConstantDtClock( 30 ) );
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

    //Remove the SaltCrystal bodies from the box2d model so they won't collide.  This facilitates dragging from the bucket without causing interactions.
    public void unhook( SaltCrystal saltCrystal ) {
        world.destroyBody( saltCrystal.sodium.body );
        world.destroyBody( saltCrystal.sodium2.body );
        world.destroyBody( saltCrystal.chloride.body );
        world.destroyBody( saltCrystal.chloride2.body );
    }

    //Remove the sugar molecules bodies from the box2d model so they won't collide.  This facilitates dragging from the bucket without causing interactions.
    public void unhook( Sucrose sucrose ) {
        world.destroyBody( sucrose.body );
    }

    public ObservableProperty<Boolean> isAnySaltToRemove() {
        return sodiumList.count.plus( chlorineList.count ).greaterThan( 0 );
    }

    public ObservableProperty<Boolean> isAnySugarToRemove() {
        return sugarMoleculeList.count.greaterThan( 0 );
    }

    //Code that creates a single salt crystal, used when dragged from the bucket or created in the beaker
    public class SaltCrystal {
        private final double horizontalSeparation = CHLORINE_RADIUS + SODIUM_RADIUS;
        private final double verticalSeparation = horizontalSeparation * 0.85;

        public final DefaultParticle sodium;
        public final DefaultParticle chloride;

        public final DefaultParticle sodium2;
        public final DefaultParticle chloride2;

        public SaltCrystal( Point2D location ) {
            sodium = new DefaultParticle( world, modelToBox2D, location.getX(), location.getY(), 0, 0, 0, addFrameListener, ionCharge, SODIUM_RADIUS );
            chloride = new DefaultParticle( world, modelToBox2D, location.getX() + horizontalSeparation, location.getY(), 0, 0, 0, addFrameListener, ionCharge.times( -1 ), CHLORINE_RADIUS );

            sodium2 = new DefaultParticle( world, modelToBox2D, location.getX() + horizontalSeparation, location.getY() + verticalSeparation, 0, 0, 0, addFrameListener, ionCharge, SODIUM_RADIUS );
            chloride2 = new DefaultParticle( world, modelToBox2D, location.getX(), location.getY() + verticalSeparation, 0, 0, 0, addFrameListener, ionCharge.times( -1 ), CHLORINE_RADIUS );
        }
    }

    //Adds some NaCl molecules by adding nearby sodium and chlorine pairs, electrostatic forces are responsible for keeping them together until they are pulled apart by water
    public void addSalt( Point2D location ) {
        SaltCrystal saltCrystal = newSaltCrystal( location );

        //Move any waters away that these particles would overlap.  Otherwise the water can cause the Na to bump away from the Cl immediately instead of having them
        for ( WaterMolecule water : waterList.list ) {
            if ( water.intersects( saltCrystal.sodium ) || water.intersects( saltCrystal.chloride ) || water.intersects( saltCrystal.sodium2 ) || water.intersects( saltCrystal.chloride2 ) ) {
                water.setModelPosition( water.getModelPosition().plus( new ImmutableVector2D( 4 + Math.random(), 4 + Math.random() ) ) );
            }
        }

        sodiumList.add( saltCrystal.sodium );
        chlorineList.add( saltCrystal.chloride );

        sodiumList.add( saltCrystal.sodium2 );
        chlorineList.add( saltCrystal.chloride2 );

        timeSinceSaltAdded = 0;
    }

    public SaltCrystal newSaltCrystal( Point2D location ) {
        return new SaltCrystal( location );
    }

    //Adds a sugar crystal near the center of the screen
    public void addSugar( Point2D location ) {
        ArrayList<Sucrose> sugarCrystal = createSugarCrystal( location );
        for ( Sucrose sucrose : sugarCrystal ) {
            sugarMoleculeList.add( sucrose );
        }
    }

    public ArrayList<Sucrose> createSugarCrystal( Point2D location ) {
        final double x = location.getX();
        final double y = location.getY();
        final double delta = beakerHeight / 4 * 0.87;
        return new ArrayList<Sucrose>() {{
            add( newSugar( x, y - delta / 2 ) );
            add( newSugar( x, y + delta / 2 ) );
        }};
    }

    private void addSugar( double x, double y ) {
        sugarMoleculeList.add( newSugar( x, y ) );
    }

    public Sucrose newSugar( double x, double y ) {
        return new Sucrose( world, modelToBox2D, x, y, 0, 0, 0, addFrameListener, oxygenCharge, hydrogenCharge );
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

    protected void updateModel( double dt ) {
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
        for ( Molecule molecule : coulombForceOnAllMolecules.get() ? getAllMolecules() : waterList.list ) {
            for ( Atom atom : molecule.atoms ) {
                //Only apply the force in some interactions, to improve performance and increase randomness
                if ( Math.random() < probabilityOfInteraction.get() ) {
                    molecule.body.applyForce( getCoulombForce( atom.particle, false ), atom.particle.getBox2DPosition() );
                }
            }
        }

        //Na+ and Cl- should repel each other so they disassociate quickly
        for ( Molecule molecule : new ArrayList<Molecule>() {{
            addAll( sodiumList.list );
            addAll( chlorineList.list );
        }} ) {
            for ( Atom atom : molecule.atoms ) {
                molecule.body.applyForce( getCoulombForce( atom.particle, true ), atom.particle.getBox2DPosition() );
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
        timeSinceSaltAdded += dt;
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
    private Vec2 getCoulombForce( Particle target, boolean repel ) {
        Vec2 sumForces = new Vec2();
        for ( Molecule m : getAllMolecules() ) {
            for ( Atom atom : m.atoms ) {
                sumForces = sumForces.add( getCoulombForce( atom.particle, target, repel ) );
            }
        }
        return sumForces;
    }

    //Get the contribution to the total coulomb force from a single source
    private Vec2 getCoulombForce( Particle source, Particle target, boolean repel ) {
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
        if ( repel ) {
            magnitude = Math.abs( magnitude ) * 2.5;//Overcome the true attractive force, and then some

            //If the salt was just added, use full repulsive power, otherwise half the power
            if ( timeSinceSaltAdded > 0.75 ) {
                magnitude = magnitude / 2;
            }
        }
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
    public void reset() {
        initModel();
        showSugarAtoms.reset();
        showWaterCharges.reset();
    }

    //Set up the initial model state, used on init and after reset
    protected void initModel() {
        waterList.clear( world );
        clearSalt();
        clearSugar();

        //Add water particles
        addWaterParticles( System.currentTimeMillis(), DEFAULT_NUM_WATERS );
    }

    public void clearSalt() {
        sodiumList.clear( world );
        chlorineList.clear( world );
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

    public void removeSalt() {
        clearSalt();
    }

    //Called when the user presses a button to clear the sugar, removes all sugar (dissolved and crystals) from the sim
    public void removeSugar() {
        clearSugar();
    }

    private void clearSugar() {
        sugarMoleculeList.clear( world );
    }
}