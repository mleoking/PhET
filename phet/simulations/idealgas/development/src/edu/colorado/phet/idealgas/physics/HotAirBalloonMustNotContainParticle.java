/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 10, 2003
 * Time: 8:13:01 PM
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.physics.*;
import edu.colorado.phet.physics.body.PhysicalEntity;

/**
 * Singleton class of constrain for keeping a particle in a box.
 */
public class HotAirBalloonMustNotContainParticle extends MustNotContain {

    /**
     * Constructor
     */
    public HotAirBalloonMustNotContainParticle( HotAirBalloon container, PhysicalEntity contained ) {
        super( container, contained );
    }

    public Object apply( Constraint.Spec spec ) {

        // If the constraint isn't active, jsut return
        if( !spec.isActive() ) {
            return null;
        }

        // Get references to the balloon and the particle
        MustNotContain.Spec mustNotContainSpec = null;
        if( spec instanceof MustNotContain.Spec ) {
            mustNotContainSpec = (MustNotContain.Spec)spec;
        }
        PhysicalEntity body = mustNotContainSpec.getContainer();
        HotAirBalloon balloon = null;
        if( body instanceof HotAirBalloon ) {
            balloon = (HotAirBalloon)body;
        } else {
            throw new RuntimeException( "Container not instance of HotAirBalloon " +
                                        "in method apply() in class HotAirBalloonMustNotContainParticle" );
        }
        body = mustNotContainSpec.getExcluded();
        Particle particle = null;
        if( body instanceof Particle ) {
            particle = (Particle)body;
        } else {
            throw new RuntimeException( "Contained not instance of Particle " +
                                        "in method apply() in class HotAirBalloonMustNotContainParticle" );
        }

        // Apply the constraint. The particle must be completely contained in the balloon
        // Has particle moved into the balloon?
        if( balloon.isInOpening( particle ) && particle.getVelocity().getY() < 0 ) {
            if( particle.getPosition().getY() - particle.getRadius() < balloon.getOpening().getMinY() ) {
                particle.removeConstraint( this );
                Constraint newSpec = new HotAirBalloonMustContainParticle( balloon, particle );
                particle.addConstraint( newSpec );
                balloon.addContainedBody( particle );
            }
        }
        else {
            double particleDistFromCenter = balloon.getCenter().distance( particle.getCenter() );
            if( particleDistFromCenter < balloon.getRadius() + particle.getRadius() ) {

                // get vector from hollow sphere center to particle center, make it unit length,
                // the scale it by the hollow sphere's radius, minus the radius of the particle.
                // The result, added to the center of the hollow sphere, is the location of
                // the particle
                float properSeparation = balloon.getRadius() + particle.getRadius();
                Vector2D rHat = new Vector2D( particle.getCenter() );
                rHat = rHat.subtract( balloon.getCenter() );
                rHat.normalize();
                Vector2D r = rHat.multiply( properSeparation );
                r = r.add( balloon.getCenter() );
                particle.getPosition().setX( r.getX() );
                ( (IdealGasSystem)PhysicalSystem.instance() ).relocateBodyY( particle, r.getY() );
            }
        }
        return null;
    }
}
