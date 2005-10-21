/**
 * Class: SphericalBody
 * Package: edu.colorado.phet.physics.body
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.physics.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.coreadditions.Body;

import java.awt.geom.Point2D;


public abstract class SphericalBody extends CollidableBody {

    private float radius;

    protected SphericalBody( Point2D.Double center,
                      Vector2D velocity,
                      Vector2D acceleration,
                      float mass,
                      float radius,
                      float charge ) {
        super( center, velocity, acceleration, mass, 0 );
        this.radius = radius;
    }


    public SphericalBody( float radius ) {
        setRadius( radius );
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius( float radius ) {
        this.radius = radius;
    }

    public Point2D getCenter() {
        return this.getLocation();
    }

//    public boolean isInContactWithBody( CollidableBody body ) {
//        double distance = this.getLocation().distance( body.getLocation() );
//        return ( distance <= this.getContactOffset( body ) + body.getContactOffset( this ));
//    }

//    public boolean isInContactWithParticle( Particle particle ) {
//        return false;
//    }

    public float getContactOffset( Body body ) {
        return this.getRadius();
    }
}
