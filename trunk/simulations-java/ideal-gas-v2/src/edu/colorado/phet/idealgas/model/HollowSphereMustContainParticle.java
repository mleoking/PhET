/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 10, 2003
 * Time: 8:13:01 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.idealgas.model.body.HollowSphere;
import edu.colorado.phet.idealgas.model.body.IdealGasParticle;
import edu.colorado.phet.physics.Constraint;
import edu.colorado.phet.physics.MustContain;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.body.PhysicalEntity;

/**
 * Singleton class of constrain for keeping a particle in a box.
 */
public class HollowSphereMustContainParticle extends MustContain {

    /**
     * Constructor
     */
    public HollowSphereMustContainParticle( HollowSphere container, PhysicalEntity contained ) {
        super( container, contained );
    }

    public Object apply( Constraint.Spec spec ) {

        MustContain.Spec mustContainSpec = null;
        if( spec instanceof MustContain.Spec ) {
            mustContainSpec = (MustContain.Spec)spec;

        }
        PhysicalEntity body = mustContainSpec.getContainer();
        HollowSphere hollowSphere = null;
        if( body instanceof HollowSphere ) {
            hollowSphere = (HollowSphere)body;
        } else {
            throw new RuntimeException( "Container not instance of HollowSphere " +
                                        "in method apply() in class HollowSphereMustContainParticle" );
        }

        body = mustContainSpec.getContained();
        IdealGasParticle particle = null;
        if( body instanceof IdealGasParticle ) {
            particle = (IdealGasParticle)body;
        } else {
            throw new RuntimeException( "Contained not instance of IdealGasParticle " +
                                        "in method apply() in class HollowSphereMustContainParticle" );
        }


        // Apply the constraint. The particle must be completely contained in the hollowSphere
        double particleDistFromCenter = hollowSphere.getCenter().distance( particle.getCenter() );
        if( particleDistFromCenter > hollowSphere.getRadius() - particle.getRadius() ) {

            // get vector from hollow sphere center to particle center, make it unit length,
            // the scale it by the hollow sphere's radius, minus the radius of the particle.
            // The result, added to the center of the hollow sphere, is the location of
            // the particle
            float properSeparation = hollowSphere.getRadius() - particle.getRadius();
            Vector2D rHat = new Vector2D( particle.getCenter());
            rHat = rHat.subtract( hollowSphere.getCenter() );
            rHat.normalize();
            Vector2D r = rHat.multiply( properSeparation );
            r = r.add( hollowSphere.getCenter());
            particle.getPosition().setX( r.getX() );
            ((IdealGasSystem)PhysicalSystem.instance()).relocateBodyY( particle, r.getY() );
        }
        return null;
    }
}
