// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.jbox2d.collision.CircleDef;
import org.jbox2d.collision.MassData;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Model for a water molecule, including mappings between box2d physics engine and SI coordinates
 *
 * @author Sam Reid
 */
public class DefaultParticle implements Removable, Particle {
    public Body body;
    public CircleDef circleDef;
    private ArrayList<VoidFunction0> removalListeners = new ArrayList<VoidFunction0>();
    public final double radius;

    //The model position in SI
    public final Property<ImmutableVector2D> position;

    private final ModelViewTransform transform;
    private double charge;
    private BodyDef bodyDef;
    private boolean grabbed;

    public DefaultParticle( World world, final ModelViewTransform transform, final double x, final double y, double vx, double vy, final double theta, VoidFunction1<VoidFunction0> addUpdateListener, double charge, double radius ) {
        this.radius = radius;
        this.transform = transform;
        this.charge = charge;

        //Model state in SI
        position = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );

        //Find the box2d coordinates
        Point2D box2DVelocity = transform.modelToView( vx, vy );

        //First create the body def at the right location
        bodyDef = new BodyDef() {{
            final Point2D box2DLocation = transform.modelToView( x, y );
            position = new Vec2( (float) box2DLocation.getX(), (float) box2DLocation.getY() );
//            System.out.println( "position = " + position );
            this.angle = (float) theta;
        }};

        //Now create the body
        body = world.createBody( bodyDef );

        //Make it a bouncy circle
        circleDef = new CircleDef() {{
            restitution = 0.4f;
            density = 1;
            radius = (float) transform.modelToViewDeltaX( DefaultParticle.this.radius );
        }};
        body.createShape( circleDef );
        body.setMassFromShapes();

        //Set the velocity
        body.setLinearVelocity( new Vec2( (float) box2DVelocity.getX(), (float) box2DVelocity.getY() ) );

        //Update the model when box2d updates
        addUpdateListener.apply( new VoidFunction0() {
            public void apply() {
                position.set( new ImmutableVector2D( transform.viewToModel( body.getPosition().x, body.getPosition().y ) ) );
            }
        } );
    }

    //Add a listener that will be notified when this water leaves the model
    public void addRemovalListener( VoidFunction0 removalListener ) {
        removalListeners.add( removalListener );
    }

    //Notify listeners that this water molecule has left the model
    public void notifyRemoved() {
        for ( VoidFunction0 removalListener : removalListeners ) {
            removalListener.apply();
        }
    }

    //Box2D position
    public Vec2 getBox2DPosition() {
        return body.getPosition();
    }

    public double getCharge() {
        return charge;
    }

    public ImmutableVector2D getModelPosition() {
        return position.get();
    }

    //Translate when the user drags the particle
    public void translate( Dimension2D delta ) {
        position.set( position.get().getAddedInstance( delta ) );
        final Point2D box2DLocation = transform.modelToView( position.get().getX(), position.get().getY() );
        body.setXForm( new Vec2( (float) box2DLocation.getX(), (float) box2DLocation.getY() ), 0 );
        body.setLinearVelocity( new Vec2() );
    }

    //Turn off physics updates when grabbed by the user by turning the mass to zero
    public void setGrabbed( boolean b ) {
        grabbed = b;
        if ( grabbed ) { body.setMass( new MassData() ); }
        else { body.setMassFromShapes(); }
    }

    //Sets the model position and updates the box2D Position
    public void setModelPosition( ImmutableVector2D position ) {
        this.position.set( position );

        final Point2D box2DLocation = transform.modelToView( position.getX(), position.getY() );
        Vec2 v = new Vec2( (float) box2DLocation.getX(), (float) box2DLocation.getY() );

        body.setXForm( v, body.getAngle() );
    }
}