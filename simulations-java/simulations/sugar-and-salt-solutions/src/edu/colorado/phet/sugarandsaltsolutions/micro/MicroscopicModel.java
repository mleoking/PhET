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

    //Listeners who are called back when the physics updates
    private ArrayList<VoidFunction0> frameListeners = new ArrayList<VoidFunction0>();

    //Box2d world which updates the physics
    private World world;

    //Listeners that are notified when a water molecule enters the model.  Removal listeners are added to the water molecule
    private ArrayList<VoidFunction1<WaterMolecule>> waterAddedListeners = new ArrayList<VoidFunction1<WaterMolecule>>();

    private Random random = new Random();
    public final Barrier floor;
    public final Barrier rightWall;
    public final Barrier leftWall;

    //units for water molecules are in SI


    //Beaker floor should be about 40 angstroms, to accommodate about 20 water molecules side-to-side
    //But keep box2d within -10..10 (i.e. 20 boxes wide), so scale factor is about
    double scaleFactor = 20 / 40E-10;
    private final ModelViewTransform modelToBox2D = ModelViewTransform.createSinglePointScaleMapping( new Point(), new Point(), scaleFactor );

    public MicroscopicModel() {
        //Set the bounds of the physics engine.  The docs say things should be mostly between 0.1 and 10 units
        AABB worldAABB = new AABB();
        worldAABB.lowerBound = new Vec2( -200, -200 );
        worldAABB.upperBound = new Vec2( 200, 200 );

        //Create the world
        world = new World( worldAABB, new Vec2( 0, -9.8f ), true );

        //Add water particles
        addWaterParticles( System.currentTimeMillis() );

        //Create beaker floor
        floor = createBarrier( -10E-10, 0, 20E-10, 1E-10 );

        //Create sides
        rightWall = createBarrier( -10E-10, 0, 1E-10, 10E-10 );
        leftWall = createBarrier( 10E-10, 0, 1E-10, 10E-10 );

        //Move to a stable state on startup
        //Commented out because it takes too long
//        long startTime = System.currentTimeMillis();
//        for ( int i = 0; i < 10; i++ ) {
//            world.step( (float) ( clock.getDt() * 10 ), 1 );
//        }
//        System.out.println( "stable start time: " + ( System.currentTimeMillis() - startTime ) );
    }

    private void addWaterParticles( long seed ) {
        Random random = new Random( seed );
        for ( int i = 0; i < 100; i++ ) {
            float float1 = (float) ( ( random.nextFloat() - 0.5 ) * 2 );
            float float2 = (float) ( ( random.nextFloat() - 0.5 ) * 2 );
//            WaterMolecule water = createWater( float1 * 200 - 100, random.nextFloat() * 200 - 100, 0, 0, (float) ( random.nextFloat() * Math.PI * 2 ) );
            WaterMolecule water = new WaterMolecule( world, modelToBox2D, float1 * 10E-10, float2 * 10E-10, 0, 0, (float) ( random.nextFloat() * Math.PI * 2 ), new VoidFunction1<VoidFunction0>() {
                public void apply( VoidFunction0 waterMolecule ) {
                    addFrameListener( waterMolecule );
                }
            } );
            waterList.add( water );
            for ( VoidFunction1<WaterMolecule> waterAddedListener : waterAddedListeners ) {
                waterAddedListener.apply( water );
            }
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
//            waterMolecule.body.applyForce( new Vec2( rand1 * 1000, random.nextFloat() ), waterMolecule.body.getPosition() );

            //Setting random velocity looks funny
//            double randomAngle = random.nextDouble() * Math.PI * 2;
//            ImmutableVector2D v = ImmutableVector2D.parseAngleAndMagnitude( rand1 * 1, randomAngle );
//            Vec2 linearVelocity = waterMolecule.body.getLinearVelocity();
//            waterMolecule.body.setLinearVelocity( new Vec2( linearVelocity.x + (float) v.getX(), linearVelocity.y + (float) v.getY() ) );
        }
        world.step( (float) dt, 10 );

        //Notify listeners that the model changed
        for ( VoidFunction0 frameListener : frameListeners ) {
            frameListener.apply();
        }
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