// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import org.jbox2d.collision.MassData;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Molecule composed of atoms.
 * TODO: can this be replaced / augmented with micro model Compound?
 *
 * @author Sam Reid
 */
public class Molecule implements Removable, Particle {
    public final Body body;
    private final ArrayList<VoidFunction0> removalListeners = new ArrayList<VoidFunction0>();
    protected final ArrayList<Atom> atoms = new ArrayList<Atom>();
    private Point2D box2DVelocity;
    private final ModelViewTransform transform;
    private final VoidFunction1<VoidFunction0> addUpdateListener;
    private boolean grabbed;

    public Molecule( World world, final ModelViewTransform transform, double x, double y, double vx, double vy, final double theta, VoidFunction1<VoidFunction0> addUpdateListener ) {
        this.transform = transform;
        this.addUpdateListener = addUpdateListener;
        //Find the box2d coordinates
        final Point2D box2DLocation = transform.modelToView( x, y );
        box2DVelocity = transform.modelToView( vx, vy );

        //First create the body def at the right location
        BodyDef bodyDef = new BodyDef() {{
            position = new Vec2( (float) box2DLocation.getX(), (float) box2DLocation.getY() );
            angle = (float) theta;
        }};

        //Now create the Box2D body for physics updates
        body = world.createBody( bodyDef );
    }

    //Translate when the user drags the particle
    public void translate( Dimension2D delta ) {
        atoms.get( 0 ).position.set( atoms.get( 0 ).position.get().getAddedInstance( delta ) );
        final Point2D box2DLocation = transform.modelToView( atoms.get( 0 ).position.get().getX(), atoms.get( 0 ).position.get().getY() );

        //Translate, but keep the same angle
        body.setXForm( new Vec2( (float) box2DLocation.getX(), (float) box2DLocation.getY() ), body.getAngle() );
        body.setLinearVelocity( new Vec2() );
    }

    //Turn off physics updates when grabbed by the user by turning the mass to zero
    public void setGrabbed( boolean b ) {
        grabbed = b;
        if ( grabbed ) { body.setMass( new MassData() ); }
        else { body.setMassFromShapes(); }
    }

    protected void initAtoms( Atom... atom ) {
        atoms.addAll( Arrays.asList( atom ) );

        //Construct other hydrogen
        body.setMassFromShapes();

        //Set the velocity
        body.setLinearVelocity( new Vec2( (float) box2DVelocity.getX(), (float) box2DVelocity.getY() ) );

        addUpdateListener.apply( new VoidFunction0() {
            public void apply() {
                final ImmutableVector2D origin = new ImmutableVector2D( transform.viewToModel( body.getPosition().x, body.getPosition().y ) );
                for ( Atom atom : atoms ) {
                    atom.updatePosition( body.getAngle(), origin );
                }
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

    public Vec2 getBox2DPosition() {
        return atoms.get( 0 ).particle.getBox2DPosition();
    }

    public double getCharge() {
        return 0;
    }

    public ImmutableVector2D getModelPosition() {
        return atoms.get( 0 ).particle.getModelPosition();
    }

    public void setModelPosition( ImmutableVector2D immutableVector2D ) {
        atoms.get( 0 ).particle.setModelPosition( immutableVector2D );
    }
}
