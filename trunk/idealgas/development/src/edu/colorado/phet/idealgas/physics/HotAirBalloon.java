/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 19, 2003
 * Time: 2:48:01 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.idealgas.physics.body.HollowSphere;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.collision.CollidableBody;
import edu.colorado.phet.physics.body.Particle;

import java.awt.geom.Rectangle2D;
import java.util.List;

public class HotAirBalloon extends HollowSphere {

    private Rectangle2D.Float opening;
    private float theta;
    private float oxOffset;
    private float oyOffset;

    /**
     *
     * @param center
     * @param velocity
     * @param acceleration
     * @param mass
     * @param radius
     * @param openingAngle
     */
    public HotAirBalloon( Vector2D center,
                          Vector2D velocity,
                          Vector2D acceleration,
                          float mass,
                          float radius,
                          float openingAngle ) {
        super( center, velocity, acceleration, mass, radius );
        theta = openingAngle;
        setOpening();
    }

    /**
     *
     */
    public float getOpeningAngle() {
        return theta;
    }

    /**
     *
     * @param theta
     */
    public void setOpeningAngle( float theta ) {
        this.theta = theta;

        // Set the current location of the opening. It moves with the balloon
        float angle = theta * (float)Math.PI / 180;
        oxOffset = getRadius() * (float)Math.sin( angle / 2 );
        oyOffset = getRadius() * (float)Math.cos( angle / 2 );
        setOpening();
    }

    /**
     *
     * @param dt
     */
    public void stepInTime( float dt ) {

        super.stepInTime( dt );

        // Set the current location of the opening. It moves with the balloon
        float angle = theta * (float)Math.PI / 180;
        oxOffset = getRadius() * (float)Math.sin( angle / 2 );
        oyOffset = getRadius() * (float)Math.cos( angle / 2 );
        setOpening();

        // Add heat to the bodies contained in the balloon, and take away heat from other
        // bodies so the system stays more or less constant. We only do this if dt is
        // the complete system time step. Otherwise, we are just being asked to make an adjustment
        // in a collision, and we don't want to mess with the heat. This is a hack, I know,
        // but it's expedient
        if( s_heatSource != 0 && dt == getPhysicalSystem().getDt() ) {
            List containedBodies = this.getContainedBodies();
            float totalPreKE = getPhysicalSystem().getTotalKineticEnergy();
            for( int i = 0; i < containedBodies.size(); i++ ) {
                Particle body = (Particle)containedBodies.get( i );
                body.setVelocity( body.getVelocity().multiply( 1 + s_heatSource / 1000 ) );
            }
            float totalPostKE = getPhysicalSystem().getTotalKineticEnergy();

            // Adjust the kinetic energy of all particles to account for the heat we
            // added
            float ratio = (float)Math.sqrt( totalPreKE / totalPostKE );
            List allBodies = this.getPhysicalSystem().getBodies();
            for( int i = 0; i < allBodies.size(); i++ ) {
                Particle body = (Particle)allBodies.get( i );
                if( true || !containedBodies.contains( body ) ) {
                    body.getVelocity().multiply( ratio );
                }
            }
        }
    }

    public Rectangle2D.Float getOpening() {
        return opening;
    }

    private void setOpening() {
        float o2x = getPosition().getX() + oxOffset;
        float o1x = getPosition().getX() - oxOffset;
        float o1y = getPosition().getY() + oyOffset;
        opening = new Rectangle2D.Float( o1x, o1y, o2x - o1x, 20 );
    }

    /**
     *
     * @param particle
     * @return
     */
    public boolean isInContactWithParticle( edu.colorado.phet.idealgas.physics.body.Particle particle ) {
        boolean result = false;
        if( isInOpening( particle ) ) {
            result = false;
        } else {
            result = super.isInContactWithParticle( particle );
        }
        return result;
    }

    /**
     * Determines if a particle is in the opening of the hot air balloon
     * @param particle
     * @return
     */
    public boolean isInOpening( edu.colorado.phet.idealgas.physics.body.Particle particle ) {
        double px = particle.getPosition().getX();
        double py = particle.getPosition().getY();
        boolean b = px - particle.getRadius() >= opening.getMinX()
                && px + particle.getRadius() <= opening.getMaxX()
                && py + particle.getRadius() >= opening.getMinY();
        return b;
    }

    //
    // Static fields and methods
    //
    public static float s_heatSource = 0;
}
