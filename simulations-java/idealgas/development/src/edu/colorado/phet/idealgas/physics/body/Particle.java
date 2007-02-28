// ParticleJava2DGraphic

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 8:55:14 AM
 */
package edu.colorado.phet.idealgas.physics.body;

import edu.colorado.phet.idealgas.physics.HotAirBalloon;
import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.collision.CollidableBody;
import edu.colorado.phet.physics.collision.SphericalBody;
import sun.audio.AudioStream;

import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class Particle extends /*Sphere*/ SphericalBody {

    public Particle( Vector2D position, Vector2D velocity, Vector2D acceleration,
                     float mass, float radius, float charge ) {
        super( position, velocity, acceleration, mass, radius, charge );
        init();
    }

    public Particle( Vector2D position, Vector2D velocity, Vector2D acceleration,
                     float mass ) {
        super( position, velocity, acceleration, mass, s_defaultRadius, 0 );
        init();
    }

    public Particle( Vector2D position, Vector2D velocity, Vector2D acceleration,
                     float mass, float charge ) {
        super( position, velocity, acceleration, mass, s_defaultRadius, charge );
        init();
    }

    private void init() {
        s_numParticles++;
    }

    /**
     *
     */
    public void removeFromSystem() {
        Particle.removeParticle( this );
        setChanged();
        notifyObservers( CollidableBody.S_REMOVE_BODY );
    }

    public void reInitialize() {
        super.reInitialize();
        this.init();
    }

    /**
     *
     */
    public boolean isInContactWithBox2D( Box2D box ) {
        return box.isInContactWithParticle( this );
    }

    public boolean isInContactWithHollowSphere( HollowSphere sphere ) {
        return sphere.isInContactWithParticle( this );
    }

    public boolean isInContactWithHotAirBalloon( HotAirBalloon hotAirBalloon ) {
        return hotAirBalloon.isInContactWithParticle( this );
    }

    /**
     * Handle collision with a box
     */
    public void collideWithBox2D( Box2D box ) {
        box.collideWithParticle( this );
    }

    /**
     * TODO: get rid of this
     */
    public void collideWithHollowSphere( HollowSphere sphere ) {
        sphere.collideWithParticle( this );
    }

    public double getContactOffset( CollidableBody body ) {
        return this.getRadius();
    }


    //
    // Static fields and methods
    //

    // The number of particles in the system
    private static int s_numParticles = 0;
    // The default radius for a particle
    private static float s_defaultRadius = 5.0f;

    public static int getNumParticles() {
        return s_numParticles;
    }

    public static void removeParticle( Particle particle ) {
        particle.getPhysicalSystem().removeBody( particle );
        s_numParticles--;
    }
}