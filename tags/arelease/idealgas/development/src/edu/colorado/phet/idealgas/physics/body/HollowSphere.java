// edu.colorado.phet.idealgas.physics.body.HollowSphere
/*
 * User: Administrator
 * Date: Jan 5, 2003
 * Time: 8:11:48 AM
 */
package edu.colorado.phet.idealgas.physics.body;

import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.body.Body;
import edu.colorado.phet.physics.collision.CollidableBody;
import edu.colorado.phet.physics.collision.SphericalBody;

public class HollowSphere extends Particle {

    /**
     *
     */
    public HollowSphere( Vector2D center,
                         Vector2D velocity,
                         Vector2D acceleration,
                         float mass,
                         float radius ) {
        super( center, velocity, acceleration, mass, radius, 0 );
    }

    /**
     *
     */
    public boolean isInContactWithHollowSphere( HollowSphere sphere ) {
        return false;
    }

    /**
     *
     */
    public boolean isInContactWithParticle( Particle particle ) {

        double sep = this.getPosition().distance( particle.getPosition() );
        double distFromShell = Math.abs( sep - this.getRadius() );
        boolean result = distFromShell <= particle.getRadius();
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

    public void collideWithParticle( Particle particle ) {

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
    public float getContactOffset( /*CollidableBody*/ Body body ) {
        float offset = 0;
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
