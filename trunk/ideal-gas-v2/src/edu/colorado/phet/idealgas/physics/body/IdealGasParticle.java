// ParticleJava2DGraphic

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 8:55:14 AM
 */
package edu.colorado.phet.idealgas.physics.body;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.idealgas.physics.HotAirBalloon;
import edu.colorado.phet.physics.collision.CollidableBody;

import java.awt.geom.Point2D;

/**
 *
 */
public abstract class IdealGasParticle extends CollidableBody {
//public class IdealGasParticle extends Body {
//public class IdealGasParticle extends Particle {

//    private float mass;

    protected IdealGasParticle( Point2D position, Vector2D velocity, Vector2D acceleration, double mass ) {
//    protected IdealGasParticle( Point2D position, Vector2D velocity, Vector2D acceleration, float mass ) {
        super( position, velocity, acceleration, mass, 0 );
//        super( position, velocity, acceleration );
//        this.mass = mass;
    }

//    public float getMass() {
//        return mass;
//    }

    public void reInitialize() {
        throw new RuntimeException( "No longer supported" );
//        super.reInitialize();
    }

    /**
     *
     */
//    public boolean isInContactWithBox2D( Box2D box ) {
//        return box.isInContactWithParticle( this );
//    }

    public boolean isInContactWithHollowSphere( HollowSphere sphere ) {
        return sphere.isInContactWithParticle( this );
    }

    public boolean isInContactWithHotAirBalloon( HotAirBalloon hotAirBalloon ) {
        return hotAirBalloon.isInContactWithParticle( this );
    }

    /**
     * Handle collision with a box
     */
//    public void collideWithBox2D( Box2D box ) {
//        box.collideWithParticle( this );
//    }

    /**
     * TODO: get rid of this
     */
    public void collideWithHollowSphere( HollowSphere sphere ) {
        sphere.collideWithParticle( this );
    }

//    public double getContactOffset( CollidableBody body ) {
//        return this.getRadius();
//    }

//    public void stepInTime( float dt ) {
////        IdealGasSystem inst=(IdealGasSystem)IdealGasSystem.instance();
//        IdealGasApplication app=(IdealGasApplication)PhetApplication.instance();
//        CollisionGod cg=app.getCollisionGod();
//        super.stepInTime( dt );
//        cg.assignToRegions(this);
//    }

    //
    // Static fields and methods
    //

    // The default radius for a particle
    public final static float s_defaultRadius = 5.0f;
}