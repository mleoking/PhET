// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
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

            //Have to specify the type as dynamic or it won't move
            type = BodyType.DYNAMIC;
        }};
        body = world.createBody( bodyDef );

        //Add a little bit of linear and rotational friction so the system doesn't accelerate out of control
        body.setLinearDamping( 1 );
        body.setAngularDamping( 1 );

        //Add shapes for all of the constituents as rigid fixtures to the box2d shape
        for ( int i = 0; i < compound.numberConstituents(); i++ ) {

            final Constituent<SphericalParticle> constituent = compound.getConstituent( i );
            //Create the shape to add to the body
            final CircleShape shape = new CircleShape() {{
                m_radius = (float) transform.modelToViewDeltaX( constituent.particle.radius );

                //Set the position within the molecule
                ImmutableVector2D boxOffset = transform.modelToViewDelta( constituent.relativePosition );
                m_p.set( (float) boxOffset.getX(), (float) boxOffset.getY() );
            }};

            //Add the shape to the body
            Fixture f = body.createFixture( shape, 1 );

            //Add a little bit of bouncieness to keep things moving randomly
            f.setRestitution( 0.1f );
            //            f.setRestitution( 0.02f );
        }
    }

    //After the physics has been applied, update the true model position based on the box2D position
    public void worldStepped() {
        compound.setPositionAndAngle( transform.viewToModel( new ImmutableVector2D( body.getPosition().x, body.getPosition().y ) ), body.getAngle() );
    }

    //Apply a force in Newtons by converting it to box2d coordinates and applying it to the body
    public void applyModelForce( ImmutableVector2D force, ImmutableVector2D position ) {
        ImmutableVector2D box2DForce = transform.modelToViewDelta( force );
        final ImmutableVector2D box2DPosition = transform.modelToView( position );
        applyBox2DForce( box2DForce.getX(), box2DForce.getY(), box2DPosition );
    }

    //Apply a force to the body at the specified location
    public void applyBox2DForce( double fx, double fy, ImmutableVector2D box2DPosition ) {
        body.applyForce( new Vec2( (float) fx, (float) fy ), new Vec2( (float) box2DPosition.getX(), (float) box2DPosition.getY() ) );
    }

    //Convenience method to set the model position from double,double
    public void setModelPosition( double x, double y ) {
        setModelPosition( new ImmutableVector2D( x, y ) );
    }

    //Set the model position (in meters) of this compound, and update the box2D body to reflect the new coordinates so that it will be at the right place at the beginning of the next physics step
    public void setModelPosition( ImmutableVector2D immutableVector2D ) {
        compound.setPosition( immutableVector2D );
        final ImmutableVector2D box2D = transform.modelToView( immutableVector2D );
        body.setTransform( new Vec2( (float) box2D.getX(), (float) box2D.getY() ), (float) compound.getAngle() );
    }

    //Get the true model position (meters) of the compound
    public ImmutableVector2D getModelPosition() {
        return compound.getPosition();
    }
}