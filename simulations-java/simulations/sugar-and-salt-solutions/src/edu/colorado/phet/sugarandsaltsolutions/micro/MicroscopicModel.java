// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;

/**
 * Model for "micro" tab for sugar and salt solutions.
 *
 * @author Sam Reid
 */
public class MicroscopicModel extends SugarAndSaltSolutionModel {

    //List of all model objects objects
    private ArrayList<WaterMolecule> waterList = new ArrayList<WaterMolecule>();

    //List of all Sodium ions
    private ArrayList<DefaultParticle> sodiumList = new ArrayList<DefaultParticle>();

    //List of all Chlorine ions
    private ArrayList<DefaultParticle> chlorineList = new ArrayList<DefaultParticle>();

    //Listeners who are called back when the physics updates
    private ArrayList<VoidFunction0> frameListeners = new ArrayList<VoidFunction0>();

    //Box2d world which updates the physics
    private World world;

    //Listeners that are notified when something enters the model.  Removal listeners are added to the particle itself
    private ArrayList<VoidFunction1<WaterMolecule>> waterAddedListeners = new ArrayList<VoidFunction1<WaterMolecule>>();
    private ArrayList<VoidFunction1<DefaultParticle>> sodiumAddedListeners = new ArrayList<VoidFunction1<DefaultParticle>>();
    private ArrayList<VoidFunction1<DefaultParticle>> chlorineAddedListeners = new ArrayList<VoidFunction1<DefaultParticle>>();

    private Random random = new Random();
    public final Barrier floor;
    public final Barrier rightWall;
    public final Barrier leftWall;

    public final double beakerWidth = 40E-10;
    public final double beakerHeight = beakerWidth * 0.6;
    private final double box2DWidth = 20;

    //units for water molecules are in SI

    //Beaker floor should be about 40 angstroms, to accommodate about 20 water molecules side-to-side
    //But keep box2d within -10..10 (i.e. 20 boxes wide), so scale factor is about
    double scaleFactor = box2DWidth / beakerWidth;
    private final ModelViewTransform modelToBox2D = ModelViewTransform.createSinglePointScaleMapping( new Point(), new Point(), scaleFactor );

    public MicroscopicModel() {
        //Set the bounds of the physics engine.  The docs say things should be mostly between 0.1 and 10 units
        AABB worldAABB = new AABB();
        worldAABB.lowerBound = new Vec2( -200, -200 );
        worldAABB.upperBound = new Vec2( 200, 200 );

        //Create the world
        //TODO: fix units for gravity, should be in box2d coordinate frame, not SI
        world = new World( worldAABB, new Vec2( 0, -9.8f ), true );

        //Add water particles
//        addWaterParticles( System.currentTimeMillis() );
//        addSodiumParticles( System.currentTimeMillis() );

        //Create beaker floor
        double glassThickness = 1E-10;
        floor = createBarrier( -beakerWidth / 2, 0, beakerWidth, glassThickness );

        //Create sides
        rightWall = createBarrier( -beakerWidth / 2, 0, glassThickness, beakerHeight );
        leftWall = createBarrier( beakerWidth / 2, 0, glassThickness, beakerHeight );

        //Move to a stable state on startup
        //Commented out because it takes too long
//        long startTime = System.currentTimeMillis();
//        for ( int i = 0; i < 10; i++ ) {
//            world.step( (float) ( clock.getDt() * 10 ), 1 );
//        }
//        System.out.println( "stable start time: " + ( System.currentTimeMillis() - startTime ) );
    }

    //Adds a NaCl molecule by adding a nearby sodium and chlorine, electrostatic forces are responsible for keeping them together
    public void addSalt( double x, double y ) {
        addSodiumIon( x, y, 0 );
        addChlorineIon( x + DefaultParticle.radius, y, 0 );
    }

    //Adds some random sodium particles
    private void addSodiumParticles( long seed ) {
        Random random = new Random( seed );
        for ( int i = 0; i < 10; i++ ) {
            float float1 = (float) ( ( random.nextFloat() - 0.5 ) * 2 );
            final double x = float1 * beakerWidth / 2;
            final double y = random.nextFloat() * beakerHeight;
            final float angle = (float) ( random.nextFloat() * Math.PI * 2 );
            addSodiumIon( x, y, angle );
        }
    }

    //Adds a chlorine ion
    public void addChlorineIon( double x, double y, float angle ) {
        DefaultParticle chlorineIon = new DefaultParticle( world, modelToBox2D, x, y, 0, 0, angle, new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 chlorineMolecule ) {
                addFrameListener( chlorineMolecule );
            }
        }, -1 );
        chlorineList.add( chlorineIon );
        for ( VoidFunction1<DefaultParticle> chlorineAddedListener : chlorineAddedListeners ) {
            chlorineAddedListener.apply( chlorineIon );
        }
    }

    public void addSodiumIon( double x, double y, float angle ) {
        DefaultParticle sodiumIon = new DefaultParticle( world, modelToBox2D, x, y, 0, 0, angle, new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 sodiumMolecule ) {
                addFrameListener( sodiumMolecule );
            }
        }, 1 );
        sodiumList.add( sodiumIon );
        for ( VoidFunction1<DefaultParticle> sodiumAddedListener : sodiumAddedListeners ) {
            sodiumAddedListener.apply( sodiumIon );
        }
    }

    //Adds default water particles
    private void addWaterParticles( long seed ) {
        Random random = new Random( seed );
        for ( int i = 0; i < 50; i++ ) {
            float float1 = (float) ( ( random.nextFloat() - 0.5 ) * 2 );
            final double x = float1 * beakerWidth / 2;
            final double y = random.nextFloat() * beakerHeight;
            final float angle = (float) ( random.nextFloat() * Math.PI * 2 );
            addWater( x, y, angle );
        }
    }

    //Adds a single water molecule
    public void addWater( double x, double y, float angle ) {
        WaterMolecule water = new WaterMolecule( world, modelToBox2D, x, y, 0, 0, angle, new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 waterMolecule ) {
                addFrameListener( waterMolecule );
            }
        } );
        waterList.add( water );
        for ( VoidFunction1<WaterMolecule> waterAddedListener : waterAddedListeners ) {
            waterAddedListener.apply( water );
        }
    }

    //Creates a rectangular barrier
    private Barrier createBarrier( final double x, final double y, final double width, final double height ) {
        ImmutableRectangle2D modelRect = new ImmutableRectangle2D( x, y, width, height );
        final ImmutableRectangle2D box2DRect = modelToBox2D.modelToView( modelRect );
        PolygonDef shape = new PolygonDef() {{
            restitution = 0.2f;
            setAsBox( (float) box2DRect.width, (float) box2DRect.height );
        }};
        BodyDef bd = new BodyDef() {{
            position = new Vec2( (float) box2DRect.x, (float) box2DRect.y );
        }};
        Body body = world.createBody( bd );
        body.createShape( shape );
        body.setMassFromShapes();

        return new Barrier( body, modelRect );
    }

    public void addWaterAddedListener( VoidFunction1<WaterMolecule> waterAddedListener ) {
        waterAddedListeners.add( waterAddedListener );
    }

    @Override protected void updateModel( double dt ) {
        //Ignore super update for now
//        super.updateModel( dt );

        for ( WaterMolecule waterMolecule : waterList ) {
            //Apply a random force so the system doesn't settle down
            float rand1 = ( random.nextFloat() - 0.5f ) * 2;
            float rand2 = ( random.nextFloat() - 0.5f ) * 2;
            waterMolecule.body.applyForce( new Vec2( rand1 * randomness.get(), rand2 * randomness.get() ), waterMolecule.body.getPosition() );

            //Setting random velocity looks funny
//            double randomAngle = random.nextDouble() * Math.PI * 2;
//            ImmutableVector2D v = ImmutableVector2D.parseAngleAndMagnitude( rand1 * 1, randomAngle );
//            Vec2 linearVelocity = waterMolecule.body.getLinearVelocity();
//            waterMolecule.body.setLinearVelocity( new Vec2( linearVelocity.x + (float) v.getX(), linearVelocity.y + (float) v.getY() ) );
        }

        //Apply coulomb forces between all pairs of particles
        for ( WaterMolecule waterMolecule : waterList ) {
            final Vec2 coulombForce = getCoulombForce( waterMolecule.getOxygenParticle() );
//            System.out.println( "coulombForce = " + coulombForce );
            waterMolecule.body.applyForce( coulombForce, waterMolecule.body.getPosition() );
            waterMolecule.body.applyForce( getCoulombForce( waterMolecule.getH1Particle() ), waterMolecule.getH1Particle().getPosition() );
            waterMolecule.body.applyForce( getCoulombForce( waterMolecule.getH2Particle() ), waterMolecule.getH2Particle().getPosition() );
        }
        for ( DefaultParticle sodiumIon : sodiumList ) {
            sodiumIon.body.applyForce( getCoulombForce( sodiumIon ), sodiumIon.body.getPosition() );
        }
        for ( DefaultParticle chlorineIon : chlorineList ) {
            chlorineIon.body.applyForce( getCoulombForce( chlorineIon ), chlorineIon.body.getPosition() );
        }
        world.step( (float) dt, 1000 );

        //Notify listeners that the model changed
        for ( VoidFunction0 frameListener : frameListeners ) {
            frameListener.apply();
        }
    }

    //Gets the force on a single particle
    private Vec2 getCoulombForce( Particle target ) {
        Vec2 sumForces = new Vec2();
        for ( DefaultParticle ion : sodiumList ) {
            sumForces = sumForces.add( getCoulombForce( ion, target ) );
        }
        for ( DefaultParticle ion : chlorineList ) {
            sumForces = sumForces.add( getCoulombForce( ion, target ) );
        }
        for ( WaterMolecule water : waterList ) {
            sumForces = sumForces.add( getCoulombForce( water.getOxygenParticle(), target ) );
            sumForces = sumForces.add( getCoulombForce( water.getH1Particle(), target ) );
            sumForces = sumForces.add( getCoulombForce( water.getH2Particle(), target ) );
        }
        return sumForces;
    }

    //So we don't have to reallocate zeros all the time
    private final Vec2 zero = new Vec2();

    //Properties for developer controls
    public final Property<Integer> k = new Property<Integer>( 100 );
    public final Property<Integer> pow = new Property<Integer>( 3 );
    public final Property<Integer> randomness = new Property<Integer>( 3 );

    //Get the contribution to the total coulomb force from a single source
    private Vec2 getCoulombForce( Particle source, Particle target ) {
        if ( source == target ||
             ( source.getPosition().x == target.getPosition().x && source.getPosition().y == target.getPosition().y ) ) {
            return zero;
        }
        Vec2 r = target.getPosition().sub( source.getPosition() );
        double distance = r.length();

        double q1 = source.getCharge();
        double q2 = target.getCharge();

        //Use a gaussian so that NaCl has a strong affinity
        double x0 = 1.29499;
        double distanceFunction = Math.exp( -Math.pow( distance - x0, 2 ) );
        double magnitude = k.get() * q1 * q2 * distanceFunction;
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
        return waterList;
    }

    //Register for a callback when the model steps
    public void addFrameListener( VoidFunction0 listener ) {
        frameListeners.add( listener );
    }

    //Resets the model, clearing water molecules and starting over
    @Override public void reset() {
        clearWater();
        addWaterParticles( System.currentTimeMillis() );
    }

    //Removes all water from the model
    private void clearWater() {
        for ( WaterMolecule waterMolecule : waterList ) {
            world.destroyBody( waterMolecule.body );
            waterMolecule.notifyRemoved();
        }
        waterList.clear();
    }

    public ArrayList<DefaultParticle> getSodiumIonList() {
        return sodiumList;
    }

    public void addSodiumIonAddedListener( VoidFunction1<DefaultParticle> listener ) {
        sodiumAddedListeners.add( listener );
    }

    public ArrayList<DefaultParticle> getChlorineIonList() {
        return chlorineList;
    }

    public void addChlorineIonAddedListener( VoidFunction1<DefaultParticle> createNode ) {
        chlorineAddedListeners.add( createNode );
    }

    //Model object representing a barrier, such as the beaker floor or wall which particles shouldn't pass through
    public static class Barrier {
        public final Body body;
        public final ImmutableRectangle2D shape;

        public Barrier( Body body, ImmutableRectangle2D shape ) {
            this.body = body;
            this.shape = shape;
        }
    }
}