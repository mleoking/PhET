// edu.colorado.phet.idealgas.model.body.HollowSphere
/*
 * User: Administrator
 * Date: Jan 5, 2003
 * Time: 8:11:48 AM
 */
package edu.colorado.phet.idealgas.model.body;

import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.body.Body;
import edu.colorado.phet.physics.collision.CollidableBody;
import edu.colorado.phet.physics.collision.SphericalBody;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;

public class HollowSphere extends IdealGasParticle {
    private float radius;

    /**
     *
     */
    public HollowSphere( Point2D center,
                         Vector2D velocity,
                         Vector2D acceleration,
                         float mass,
                         float radius ) {
        super( center, velocity, acceleration, mass );
        this.radius = radius;
//        super( center, velocity, acceleration, mass, radius, 0 );
    }

    public Point2D getCM() {
        return super.getPosition();
    }

    public double getMomentOfInertia() {
        throw new RuntimeException( "not implemented" );
    }

    /**
     *
     */
    public boolean isInContactWithHollowSphere( HollowSphere sphere ) {
        return false;
    }

    public double getRadius() {
        return radius;
    }

    /**
     *
     */
    public boolean isInContactWithParticle( IdealGasParticle particle ) {

        double sep = this.getPosition().distance( particle.getPosition() );
        double distFromShell = Math.abs( sep - this.getRadius() );
        boolean result = distFromShell <= particle.getContactOffset();
//        boolean result = distFromShell <= particle.getRadius();
        if( this.containsBody( particle )
            && sep >= this.getRadius() ) {
            result = true;
        }
        if( !this.containsBody( particle )
            && sep <= this.getRadius() ) {
            result = true;
        }
        return result;
    }

    public void collideWithHollowSphere( HollowSphere sphere ) {
    }

    public void collideWithParticle( IdealGasParticle particle ) {

//        super.collideWithParticle( particle );
/*
        double sep = this.getPosition().distance( particle.getPosition() );

        // If the particle has escaped. retrieve it
        if( this.containsBody( particle )
            && sep >= this.getRadius() ) {

            // Move the particle back into the sphere to a point along the
            // line between their centers.
            double rat = sep / ( this.getRadius() - 2 * particle.getRadius() );
            double xDiff = ( particle.getPosition().getX() - this.getPosition().getX() ) / rat;
            double yDiff = ( particle.getPosition().getY() - this.getPosition().getY() ) / rat;
            IdealGasSystem idealGasSystem = (IdealGasSystem)this.getPhysicalSystem();
            idealGasSystem.relocateBodyY( particle, yDiff + this.getPosition().getY() );
            particle.setPosition( xDiff + this.getPosition().getX(), yDiff + this.getPosition().getY() );
            if( this.isInContactWithParticle( particle )) {
                super.collideWithParticle( particle );
            }
        }

        // If the particle has broken in, kick it out
        if( !this.containsBody( particle )
            && sep <= this.getRadius() ) {

            // Move the particle back out of the sphere to a point along the
            // line between their centers.
            // Make sure that energy is conserved
            double rat = sep / ( this.getRadius() + 2 * particle.getRadius() );
            double xDiff = ( particle.getPosition().getX() - this.getPosition().getX() ) / rat;
            double yDiff = ( particle.getPosition().getY() - this.getPosition().getY() ) / rat;
            IdealGasSystem idealGasSystem = (IdealGasSystem)this.getPhysicalSystem();
            idealGasSystem.relocateBodyY( particle, yDiff + this.getPosition().getY() );
            particle.setPosition( xDiff + this.getPosition().getX(), yDiff + this.getPosition().getY() );
            if( this.isInContactWithParticle( particle )) {
                super.collideWithParticle( particle );
            }
        }

        else {
            super.collideWithParticle( particle );
        }

        // Always check to see if we need to collide with the box
        IdealGasSystem idealGasSystem = (IdealGasSystem)this.getPhysicalSystem();
        idealGasSystem.getBox().collideWithHollowSphere( this );
*/
    }

    /**
     * This determination depends on whether the body is hitting the hollow sphere
     * on the outside or the inside
     */
    public double getContactOffset( /*CollidableBody*/ Body body ) {
//    public float getContactOffset( /*CollidableBody*/ Body body ) {
        double offset = 0;
//        float offset = 0;
        if( body instanceof SphericalBody ) {
//        if( body instanceof Sphere ) {

            // Is the body hitting from the outside or inside?
            Vector2D prevPos = body.getPositionPrev();
            double distSq = this.getPosition().distanceSquared( prevPos );

            // Hitting from outside?
            if( distSq > this.getRadius() * this.getRadius() ) {
                offset = this.getRadius();
            }
            // Hitting from inside?
            else {
                offset = this.getRadius() - ((SphericalBody)body).getRadius() * 2;
//                offset = this.getRadius() - ((Sphere)body).getRadius() * 2;
            }
        } else {
            offset = this.getRadius();
        }
        return offset;
    }
}
