/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 10, 2003
 * Time: 8:13:01 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.physics.*;
import edu.colorado.phet.physics.body.PhysicalEntity;

/**
 * Singleton class of constrain for keeping a particle in a box.
 */
public class HotAirBalloonMustContainParticle extends MustContain {

    /**
     * Constructor
     */
    public HotAirBalloonMustContainParticle( HotAirBalloon container, PhysicalEntity contained ) {
        super( container, contained );
    }

    public Object apply( Constraint.Spec spec ) {

        // If the constraint isn't active, jsut return
        if( !spec.isActive() ) {
            return null;
        }

        // Get references to the balloon and the particle
        MustContain.Spec mustContainSpec = null;
        if( spec instanceof MustContain.Spec ) {
            mustContainSpec = (MustContain.Spec)spec;
        }
        else {
            throw new RuntimeException( "Incorrect class for constraint specification " +
                                        "in method apply() in class HotAirBalloonMustContainParticle" );
        }
        PhysicalEntity body = mustContainSpec.getContainer();
        HotAirBalloon balloon = null;
        if( body instanceof HotAirBalloon ) {
            balloon = (HotAirBalloon)body;
        } else {
            throw new RuntimeException( "Container not instance of HotAirBalloon " +
                                        "in method apply() in class HotAirBalloonMustContainParticle" );
        }
        body = mustContainSpec.getContained();
        Particle particle = null;
        if( body instanceof Particle ) {
            particle = (Particle)body;
        } else {
            throw new RuntimeException( "Contained not instance of Particle " +
                                        "in method apply() in class HotAirBalloonMustContainParticle" );
        }

        // Apply the constraint. The particle must be completely contained in the balloon
        // Has particle has moved out of balloon?
        if( balloon.isInOpening( particle ) && particle.getVelocity().getY() > 0 ) {
            if( particle.getPosition().getY() - particle.getRadius() > balloon.getOpening().getMinY() ) {
                particle.removeConstraint( this );
                Constraint newSpec = new HotAirBalloonMustNotContainParticle(
                        balloon, particle );
                particle.addConstraint( newSpec );
                balloon.removeContainedBody( particle );

            }
        } else {
            double particleDistFromCenter = balloon.getCenter().distance( particle.getCenter() );
            if( particleDistFromCenter > balloon.getRadius() - particle.getRadius() ) {

                // get vector from balloon center to particle center, make it unit length,
                // the scale it by the balloon's radius, minus the radius of the particle.
                // The result, added to the center of the balloon, is the location of
                // the particle
                float properSeparation = balloon.getRadius() - particle.getRadius();
                Vector2D rHat = new Vector2D( particle.getCenter() );
                rHat = rHat.subtract( balloon.getCenter() );
                rHat.normalize();
                Vector2D r = rHat.multiply( properSeparation );
                r = r.add( balloon.getCenter() );
                particle.getPosition().setX( r.getX() );
                ((IdealGasSystem)PhysicalSystem.instance()).relocateBodyY( particle, r.getY() );
            }
        }
        return null;
    }
}
