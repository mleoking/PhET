// ParticleJava2DGraphic

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 8:55:14 AM
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.collision.CollidableBody;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 *
 */
public class IdealGasParticle extends SphericalBody {

    //
    // Static fields and methods
    //

    // The default radius for a particle
    public final static float s_defaultRadius = 5.0f;

    public IdealGasParticle( Point2D position, Vector2D velocity, Vector2D acceleration,
                             double mass, double radius ) {
        //                     float mass, float radius, float charge ) {
        super( position, velocity, acceleration, mass, radius );
    }

    public IdealGasParticle( Point2D position, Vector2D velocity, Vector2D acceleration,
                             double mass ) {
        super( position, velocity, acceleration, mass, s_defaultRadius );
    }

    public void reInitialize() {
        throw new RuntimeException( "not implemented" );
        //        super.reInitialize();
    }

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

    public double getContactOffset( CollidableBody body ) {
        return this.getRadius();
    }
}