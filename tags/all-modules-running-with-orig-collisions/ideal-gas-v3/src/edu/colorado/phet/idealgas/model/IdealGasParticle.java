// ParticleJava2DGraphic

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 8:55:14 AM
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.collision.CollidableBody;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 */
public class IdealGasParticle extends SphericalBody {

    // The default radius for a particle
    public final static float s_defaultRadius = 5.0f;


    // List of contraints that must be applied to the IdealGasParticle's state
    // at the end of each stepInTime
    protected ArrayList constraints = new ArrayList();
    // Working copy of constraint list used in case a constraint
    // needs to modify the real constraint list
    private ArrayList workingList = new ArrayList();

    ArrayList containedBodies = new ArrayList();


    public IdealGasParticle( Point2D position, Vector2D velocity, Vector2D acceleration,
                             double mass, double radius ) {
        //                     float mass, float radius, float charge ) {
        super( position, velocity, acceleration, mass, radius );
    }

    public IdealGasParticle( Point2D position, Vector2D velocity, Vector2D acceleration,
                             double mass ) {
        super( position, velocity, acceleration, mass, s_defaultRadius );
    }

//    public void reInitialize() {
//        throw new RuntimeException( "not implemented" );
//        //        super.reInitialize();
//    }

    /**
     *
     */
    public boolean isInContactWithBox2D( Box2D box ) {
        return box.isInContactWithParticle( this );
    }

    //    public boolean isInContactWithHollowSphere( HollowSphere sphere ) {
    //        return sphere.isInContactWithParticle( this );
    //    }
    //
    //    public boolean isInContactWithHotAirBalloon( HotAirBalloon hotAirBalloon ) {
    //        return hotAirBalloon.isInContactWithParticle( this );
    //    }
    //
    //    /**
    //     * Handle collision with a box
    //     */
    //    public void collideWithBox2D( Box2D box ) {
    //        box.collideWithParticle( this );
    //    }
    //
    //    /**
    //     * TODO: get rid of this
    //     */
    //    public void collideWithHollowSphere( HollowSphere sphere ) {
    //        sphere.collideWithParticle( this );
    //    }


    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // Iterate the receiver's constraints. We iterate a copy of the list, in case
        // any of the constraints need to add or remove constraints from the list
        workingList.clear();
        workingList.addAll( constraints );
        for( Iterator iterator = workingList.iterator(); iterator.hasNext(); ) {
            Constraint constraintSpec = (Constraint)iterator.next();
//            constraintSpec.apply();
        }
    }

    public double getContactOffset( CollidableBody body ) {
        return this.getRadius();
    }

    public void addConstraint( Constraint constraintSpec ) {
        constraints.add( constraintSpec );
    }

    public void removeConstraint( Constraint constraintSpec ) {
        constraints.remove( constraintSpec );
    }


    /**
     * Containment methods
     */
    public void addContainedBody( Body body ) {
        containedBodies.add( body );
    }

    public void removeContainedBody( Body body ) {
        containedBodies.remove( body );
    }

    public boolean containsBody( Body body ) {
        return containedBodies.contains( body );
    }

    public int numContainedBodies() {
        return containedBodies.size();
    }

}

