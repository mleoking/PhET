/**
 * Class: SphericalBody
 * Package: edu.colorado.phet.model.body
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.collision.CollidableBody;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;

/**
 * NOTE: This class is not thread-safe!!!!!
 */
//public class SphericalBody extends Particle {
public class SphericalBody extends CollidableBody {

    private double radius;

    private static Point2D.Double tempVector = new Point2D.Double();
    //    private static Vector2D tempVector = new Vector2D.Float( );

    public SphericalBody( double radius ) {
        this.radius = radius;
    }

    protected SphericalBody( Point2D center,
                             //    protected SphericalBody( Vector2D center,
                             Vector2D velocity,
                             Vector2D acceleration,
                             double mass,
                             double radius ) {
        super( center, velocity, acceleration, mass, 0 );
        this.radius = radius;
    }

    public Point2D getCM() {
        return this.getPosition();
    }

    public double getMomentOfInertia() {
        return getMass() * radius * radius * 2 / 5;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius( float radius ) {
        this.radius = radius;
    }

    public Point2D getCenter() {
        //    public Vector2D getCenter() {
        return this.getPosition();
    }

    public boolean isInContactWithBody( CollidableBody body ) {
        //    public boolean isInContactWithBody( Body body ) {
        tempVector.setLocation( this.getPosition().getX(), this.getPosition().getY() );
        //        tempVector.setX( this.getPosition().getX() );
        //        tempVector.setY( this.getPosition().getY() );
        double distance = tempVector.distance( body.getPosition() );
        //        float distance = tempVector.distance( body.getPosition() );
        return ( distance <= this.getContactOffset( body ) + body.getContactOffset( this ) );
    }

    //    public boolean isInContactWithParticle( Particle particle ) {
    //        return false;
    //    }

    public double getContactOffset( Body body ) {
        //    public float getContactOffset( Body body ) {
        return this.getRadius();
    }
}
