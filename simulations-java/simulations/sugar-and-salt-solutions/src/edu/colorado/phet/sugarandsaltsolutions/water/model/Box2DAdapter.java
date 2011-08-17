// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import org.jbox2d.collision.CircleDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;

/**
 * The Box2DAdapter creates a connection between the Compound model object and its box2D representation, and can use values from one to update the other.
 * This is so we can use Box2D for physics and piccolo for graphics
 *
 * @author Sam Reid
 */
public class Box2DAdapter {

    //The Box2D world instance
    public final World world;

    //The compound to represent
    public final Compound<SphericalParticle> compound;

    //The transform from true model coordinates (meters) to box2D coordinates, see WaterModel for a description of these coordinates
    public final ModelViewTransform transform;

    //The Box2D body instance
    public final Body body;

    public Box2DAdapter( World world, final Compound<SphericalParticle> compound, final ModelViewTransform transform ) {
        this.world = world;
        this.compound = compound;
        this.transform = transform;

        //First create the body def at the right location
        BodyDef bodyDef = new BodyDef() {{
            ImmutableVector2D box2DPosition = transform.modelToView( compound.getPosition() );
            position = new Vec2( (float) box2DPosition.getX(), (float) box2DPosition.getY() );
            angle = (float) compound.getAngle();
        }};
        body = world.createBody( bodyDef );

        for ( final Constituent<SphericalParticle> component : compound ) {
            //Make it a bouncy circle
            CircleDef circleDef = new CircleDef() {{
                restitution = 0.4f;
                density = 1;
                ImmutableVector2D boxOffset = transform.modelToViewDelta( component.relativePosition );
                localPosition = new Vec2( (float) boxOffset.getX(), (float) boxOffset.getY() );
                radius = (float) transform.modelToViewDeltaX( component.particle.radius );
            }};
            body.createShape( circleDef );
        }

        //Update the Box2D body model so it will be ready for propagation.  This call is not necessary in later versions of C++ box2d: http://code.google.com/p/box2d/source/detail?r=13
        body.setMassFromShapes();
    }

    //After the physics has been applied, update the true model position based on the box2D position
    public void worldStepped() {
        compound.setPosition( transform.viewToModel( new ImmutableVector2D( body.getPosition().x, body.getPosition().y ) ) );
    }

    //Apply a force to the body at its center
    //Note that this will be insufficient for polyatomic compounds
    //TODO: add a variant for polyatomic compounds
    public void applyForce( double fx, double fy ) {
        body.applyForce( new Vec2( (float) fx, (float) fy ), body.getPosition() );
    }

    //Set the model position (in meters) of this compound, and update the box2D body to reflect the new coordinates so that it will be at the right place at the beginning of the next physics step
    public void setModelPosition( ImmutableVector2D immutableVector2D ) {
        compound.setPosition( immutableVector2D );
        final ImmutableVector2D box2D = transform.modelToView( immutableVector2D );
        body.setXForm( new Vec2( (float) box2D.getX(), (float) box2D.getY() ), (float) compound.getAngle() );
    }

    //Get the true model position (meters) of the compound
    public ImmutableVector2D getModelPosition() {
        return compound.getPosition();
    }
}