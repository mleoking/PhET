// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;

/**
 * Model for "micro" tab for sugar and salt solutions.
 *
 * @author Sam Reid
 */
public class MicroscopicModel extends SugarAndSaltSolutionModel {

    //List of all model objects objects
    private ArrayList<CircleBody> bodyList = new ArrayList<CircleBody>();

    //Listeners who are called back when the physics updates
    private ArrayList<VoidFunction0> frameListeners = new ArrayList<VoidFunction0>();

    //Box2d world which updates the physics
    private World world;

    public MicroscopicModel() {
        //Set the bounds of the physics engine
        AABB worldAABB = new AABB();
        worldAABB.lowerBound = new Vec2( -100, -100 );
        worldAABB.upperBound = new Vec2( 100, 100 );

        //Create the world
        world = new World( worldAABB, new Vec2( 0, 1 ), true );

        //Add water particles
        Random random = new Random();
        for ( int i = 0; i < 200; i++ ) {
            addBody( random.nextFloat() * 100, random.nextFloat() * 100, random.nextFloat(), random.nextFloat() );
        }

        //Create beaker floor
        createBarrier( 0, 99, 100, 2 );

        //Create sides
        createBarrier( 99, 0, 2, 100 );
        createBarrier( -99, 0, 2, 100 );
    }

    //Creates a rectangular barrier
    private void createBarrier( float x, float y, float width, float height ) {
        PolygonDef floor = new PolygonDef();
        floor.setAsBox( width, height );
        BodyDef bd = new BodyDef();
        floor.restitution = 0.4f;
        bd.position = new Vec2( x, y );
        Body body = world.createBody( bd );

        body.createShape( floor );
        body.setMassFromShapes();
    }

    //Wrapper class which contains Body and shape
    public static class CircleBody {
        Body body;
        CircleDef circleDef;

        CircleBody( Body body, CircleDef circleDef ) {
            this.body = body;
            this.circleDef = circleDef;
        }
    }

    //Adds a circular body at the specified location with the given velocity
    private void addBody( float x, float y, float vx, float vy ) {

        //First create the body def at the right location
        BodyDef bodyDef = new BodyDef();
        bodyDef.position = new Vec2( x, y );

        //Now create the body
        Body body = world.createBody( bodyDef );

        //Make it a bouncy circle
        CircleDef shapeDef = new CircleDef();
        shapeDef.restitution = 0.8f;
        shapeDef.density = 1;
        shapeDef.radius = 4;

        //Update the shape
        body.createShape( shapeDef );
        body.setMassFromShapes();

        //Set the velocity
        body.setLinearVelocity( new Vec2( vx, vy ) );

        //Add the body to the list
        bodyList.add( new CircleBody( body, shapeDef ) );
    }


    @Override protected void updateModel( double dt ) {
        //Ignore super update for now
//        super.updateModel( dt );
        world.step( (float) dt * 10, 1 );

        //Notify listeners that the model changed
        for ( VoidFunction0 frameListener : frameListeners ) {
            frameListener.apply();
        }
    }

    //Get all bodies in the model
    public ArrayList<CircleBody> getBodyList() {
        return bodyList;
    }

    //Register for a callback when the model steps
    public void addFrameListener( VoidFunction0 listener ) {
        frameListeners.add( listener );
    }
}