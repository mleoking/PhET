/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 10, 2003
 * Time: 8:13:01 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.collision.SphericalBody;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.mechanics.Body;

/**
 * Singleton class of constrain for keeping a particle in a box.
 */
public class HollowSphereMustContainParticle extends MustContain {
    private IdealGasModel idealGasModel;

    /**
     * Constructor
     */
    public HollowSphereMustContainParticle( HollowSphere container, Body contained, IdealGasModel idealGasModel ) {
        super( container, contained );
        this.idealGasModel = idealGasModel;
    }

    public Object apply( Constraint.Spec spec ) {

        MustContain.Spec mustContainSpec = null;
        if( spec instanceof MustContain.Spec ) {
            mustContainSpec = (MustContain.Spec)spec;

        }
        ModelElement body = mustContainSpec.getContainer();
        HollowSphere hollowSphere = null;
        if( body instanceof HollowSphere ) {
            hollowSphere = (HollowSphere)body;
        }
        else {
            throw new RuntimeException( "Container not instance of HollowSphere " +
                                        "in method apply() in class HollowSphereMustContainParticle" );
        }

        body = mustContainSpec.getContained();
        SphericalBody particle = null;
        if( body instanceof SphericalBody ) {
            particle = (SphericalBody)body;
        }
        else {
            throw new RuntimeException( "Contained not instance of SphericalBody " +
                                        "in method apply() in class HollowSphereMustContainParticle" );
        }


        // Apply the constraint. The particle must be completely contained in the hollowSphere
        double particleDistFromCenter = hollowSphere.getCenter().distance( particle.getCenter() );
        if( particleDistFromCenter > hollowSphere.getRadius() - particle.getRadius() ) {

            // get vector from hollow sphere center to particle center, make it unit length,
            // the scale it by the hollow sphere's radius, minus the radius of the particle.
            // The result, added to the center of the hollow sphere, is the location of
            // the particle
            double properSeparation = hollowSphere.getRadius() - particle.getRadius();
            Vector2D rHat = new Vector2D.Double( particle.getCenter() );
            rHat.setComponents( rHat.getX() - hollowSphere.getCenter().getX(),
                                rHat.getY() - hollowSphere.getCenter().getY() );
//            rHat = rHat.subtract( hollowSphere.getCenter() );
            rHat.normalize();
            Vector2D r = rHat.scale( properSeparation );
//            r = r.add( hollowSphere.getCenter());
            r.setComponents( r.getX() + hollowSphere.getCenter().getX(),
                             r.getY() + hollowSphere.getCenter().getY() );
//            particle.getPosition().setX( r.getX() );
            particle.setPosition( r.getX(), particle.getPosition().getY() );
//            ( (IdealGasSystem)PhysicalSystem.instance() ).relocateBodyY( particle, r.getY() );
            idealGasModel.relocateBodyY( particle, r.getY() );
        }
        return null;
    }
}
