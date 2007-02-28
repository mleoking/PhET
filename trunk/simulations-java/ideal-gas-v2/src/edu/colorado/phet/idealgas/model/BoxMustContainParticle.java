/**
 * Class: BoxMustContainParticle
 * Package: edu.colorado.phet.idealgas.model
 * User: Ron LeMaster
 * Date: Feb 10, 2003
 * Time: 8:13:01 PM
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.idealgas.model.body.IdealGasParticle;
import edu.colorado.phet.physics.Constraint;
import edu.colorado.phet.physics.MustContain;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.body.PhysicalEntity;

/**
 * Singleton class of constrain for keeping a particle in a box.
 */
public class BoxMustContainParticle extends MustContain {

    /**
     * Constructor
     */
    public BoxMustContainParticle( Box2D container, PhysicalEntity contained ) {
        super( container, contained );
    }

    public Object apply( Constraint.Spec spec ) {

        // Get references to the box and the particle
        MustContain.Spec mustContainSpec = null;
        if( spec instanceof MustContain.Spec ) {
            mustContainSpec = (MustContain.Spec)spec;
        }
        PhysicalEntity body = mustContainSpec.getContainer();
        Box2D box = null;
        if( body instanceof Box2D ) {
            box = (Box2D)body;
        } else {
            throw new RuntimeException( "Container not instance of Box2D " +
                                        "in method apply() in class BoxMustContainParticle" );
        }
        body = mustContainSpec.getContained();
        IdealGasParticle particle = null;
        if( body instanceof IdealGasParticle ) {
            particle = (IdealGasParticle)body;
        } else {
            throw new RuntimeException( "Contained not instance of IdealGasParticle " +
                                        "in method apply() in class BoxMustContainParticle" );
        }

        // Apply the constraint. The particle must be completely contained in the box
        if( !box.isInOpening( particle ) ) {

            // If the body is infinitely massive, that means it is fixed
            if( particle.getMass() != Float.POSITIVE_INFINITY ) {
                float x = particle.getPosition().getX();
                float newX = x;
                float y = particle.getPosition().getY();
                float newY = y;

                // Have we violated a the constraint with a vertical wall?
                if( x < box.getMinX() + particle.getRadius() ) {
                    newX = box.getMinX() + particle.getRadius();
                    particle.getVelocity().setX( -particle.getVelocity().getX() );
                }
                else if ( x > box.getMaxX() - particle.getRadius() ) {
                    newX = box.getMaxX() - particle.getRadius();
                    particle.getVelocity().setX( -particle.getVelocity().getX() );
                }

                // Have we violated a the constraint with a horizontal wall?
                if( y < box.getMinY() + particle.getRadius() ) {
                    newY = box.getMinY() + particle.getRadius();
                    particle.getVelocity().setY( -particle.getVelocity().getY() );
                }
                else if ( y > box.getMaxY() - particle.getRadius() ) {
                    newY = box.getMaxY() - particle.getRadius();
                    particle.getVelocity().setY( -particle.getVelocity().getY() );
                }

                // Note that we must not call setPosition(), because it modifies
                // the previousPosition attribute of the particle. We call relocateBodyY()
                // to ensure conservation of energy if gravity it on
                if( newX != x ) {
                    particle.getPosition().setX( newX );
                }
                if( newY != y ) {
                    ((IdealGasSystem)PhysicalSystem.instance()).relocateBodyY( particle, newY );
                }
            }
        }
        return null;
    }
}
