// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.jbox2d.collision.CircleDef;
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
public class WaterMolecule implements Removable, Particle {
    public final double oxygenRadius = 1E-10;
    public final double hydrogenRadius = 0.5E-10;

    public Body body;
    private ArrayList<VoidFunction0> removalListeners = new ArrayList<VoidFunction0>();

    public final Atom oxygen;
    public final Atom hydrogen1;

    public CircleDef h2;
    public final Property<ImmutableVector2D> hydrogen2Position;
    private Particle h2Particle;

    public WaterMolecule( World world, final ModelViewTransform transform, double x, double y, double vx, double vy, final double theta, VoidFunction1<VoidFunction0> addUpdateListener ) {
        //Model state in SI
        hydrogen2Position = new Property<ImmutableVector2D>( new ImmutableVector2D( x - 1E-10, y + 1E-10 ) );

        //Find the box2d coordinates
        final Point2D box2DLocation = transform.modelToView( x, y );
        Point2D box2DVelocity = transform.modelToView( vx, vy );

        //First create the body def at the right location
        BodyDef bodyDef = new BodyDef() {{
            position = new Vec2( (float) box2DLocation.getX(), (float) box2DLocation.getY() );
//            System.out.println( "position = " + position );
            this.angle = (float) theta;
        }};

        //Now create the Box2D body for physics updates
        body = world.createBody( bodyDef );

        oxygen = new Atom( x, y, transform, oxygenRadius, body, 0, 0, -2 );
        hydrogen1 = new Atom( x + 1E-10, y + 1E-10, transform, hydrogenRadius, body, 0.5E-10, 0.5E-10, 1 );

        //Construct other hydrogen
        final ImmutableVector2D h2ModelOffset = new ImmutableVector2D( -0.5E-10, 0.5E-10 );
        h2 = new CircleDef() {{
            restitution = 0.4f;
            ImmutableVector2D boxOffset = transform.modelToViewDelta( h2ModelOffset );
            localPosition = new Vec2( (float) boxOffset.getX(), (float) boxOffset.getY() );
            radius = (float) transform.modelToViewDeltaX( hydrogenRadius );
            density = 1;
        }};
        body.createShape( h2 );
        body.setMassFromShapes();

        //Set the velocity
        body.setLinearVelocity( new Vec2( (float) box2DVelocity.getX(), (float) box2DVelocity.getY() ) );

        addUpdateListener.apply( new VoidFunction0() {
            public void apply() {
                oxygen.position.set( new ImmutableVector2D( transform.viewToModel( body.getPosition().x, body.getPosition().y ) ) );
                hydrogen1.position.set( hydrogen1.modelOffset.getRotatedInstance( body.getAngle() ).plus( oxygen.position.get() ) );
                hydrogen2Position.set( h2ModelOffset.getRotatedInstance( body.getAngle() ).plus( oxygen.position.get() ) );
            }
        } );

        h2Particle = new Particle() {
            public Vec2 getBox2DPosition() {
                ImmutableVector2D x = transform.modelToView( hydrogen2Position.get() );
                return new Vec2( (float) x.getX(), (float) x.getY() );
            }

            public double getCharge() {
                return +1;
            }

            public ImmutableVector2D getModelPosition() {
                return transform.viewToModel( new ImmutableVector2D( getBox2DPosition().x, getBox2DPosition().y ) );
            }

            //Not implemented since the oxygen sets the reference point for this water
            public void setModelPosition( ImmutableVector2D immutableVector2D ) {
            }
        };

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

    public Particle getOxygenParticle() {
        return oxygen.particle;
    }

    public Particle getH1Particle() {
        return hydrogen1.particle;
    }

    public Particle getH2Particle() {
        return h2Particle;
    }

    public Vec2 getBox2DPosition() {
        return oxygen.particle.getBox2DPosition();
    }

    public double getCharge() {
        return 0;
    }

    public ImmutableVector2D getModelPosition() {
        return oxygen.particle.getModelPosition();
    }

    public void setModelPosition( ImmutableVector2D immutableVector2D ) {
        oxygen.particle.setModelPosition( immutableVector2D );
    }
}