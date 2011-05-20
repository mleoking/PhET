// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

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
public class WaterMolecule {
    public Body body;
    public CircleDef oxygen;
    public CircleDef h1;
    public CircleDef h2;
    private ArrayList<VoidFunction0> removalListeners = new ArrayList<VoidFunction0>();
    public final double oxygenRadius = 1E-10;
    public final double hydrogenRadius = 0.5E-10;
    public final Property<ImmutableVector2D> oxygenPosition;
    public final Property<ImmutableVector2D> hydrogen1Position;
    public final Property<ImmutableVector2D> hydrogen2Position;

    public WaterMolecule( World world, final ModelViewTransform transform, double x, double y, double vx, double vy, final double theta, VoidFunction1<VoidFunction0> addUpdateListener ) {

        //Model state in SI
        oxygenPosition = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        hydrogen1Position = new Property<ImmutableVector2D>( new ImmutableVector2D( x + 1E-10, y + 1E-10 ) );
        hydrogen2Position = new Property<ImmutableVector2D>( new ImmutableVector2D( x - 1E-10, y + 1E-10 ) );

        //Find the box2d coordinates
        final Point2D box2DLocation = transform.modelToView( x, y );
        Point2D box2DVelocity = transform.modelToView( vx, vy );

        //First create the body def at the right location
        BodyDef bodyDef = new BodyDef() {{
            position = new Vec2( (float) box2DLocation.getX(), (float) box2DLocation.getY() );
            System.out.println( "position = " + position );
            this.angle = (float) theta;
        }};

        //Now create the body
        body = world.createBody( bodyDef );

        //Make it a bouncy circle
        oxygen = new CircleDef() {{
            restitution = 0.4f;
            density = 1;
            radius = (float) transform.modelToViewDeltaX( oxygenRadius );
        }};
        body.createShape( oxygen );

        //Construct hydrogen
        final ImmutableVector2D h1ModelOffset = new ImmutableVector2D( 0.5E-10, 0.5E-10 );
        h1 = new CircleDef() {{
            restitution = 0.4f;
            ImmutableVector2D boxOffset = transform.modelToViewDelta( h1ModelOffset );
            localPosition = new Vec2( (float) boxOffset.getX(), (float) boxOffset.getY() );
            radius = (float) transform.modelToViewDeltaX( hydrogenRadius );
            density = 1;
        }};
        body.createShape( h1 );

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
                oxygenPosition.set( new ImmutableVector2D( transform.viewToModel( body.getPosition().x, body.getPosition().y ) ) );
                hydrogen1Position.set( h1ModelOffset.getRotatedInstance( body.getAngle() ).plus( oxygenPosition.get() ) );
                hydrogen2Position.set( h2ModelOffset.getRotatedInstance( body.getAngle() ).plus( oxygenPosition.get() ) );
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
}