/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 19, 2003
 * Time: 2:48:01 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.idealgas.model.HollowSphere;
import edu.colorado.phet.idealgas.model.SphericalBody;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class HotAirBalloon extends HollowSphere {

    public static double s_heatSource = 0;

    private Rectangle2D.Double opening;
    private double theta;
    private double oxOffset;
    private double oyOffset;

    public HotAirBalloon( Point2D center,
                          Vector2D velocity,
                          Vector2D acceleration,
                          double mass,
                          double radius,
                          double openingAngle ) {
        super( center, velocity, acceleration, mass, radius );
        theta = openingAngle;
        setOpening();
    }

    public double getOpeningAngle() {
        return theta;
    }

    public void setOpeningAngle( double theta ) {
        this.theta = theta;

        // Set the current location of the opening. It moves with the balloon
        double angle = theta * Math.PI / 180;
        oxOffset = getRadius() * Math.sin( angle / 2 );
        oyOffset = getRadius() * Math.cos( angle / 2 );
        setOpening();
    }

    public void stepInTime( double dt ) {

        super.stepInTime( dt );

        // Set the current location of the opening. It moves with the balloon
        double angle = theta * Math.PI / 180;
        oxOffset = getRadius() * Math.sin( angle / 2 );
        oyOffset = getRadius() * Math.cos( angle / 2 );
        setOpening();

        // Add heat to the bodies contained in the balloon, and take away heat from other
        // bodies so the system stays more or less constant. We only do this if dt is
        // the complete system time step. Otherwise, we are just being asked to make an adjustment
        // in a collision, and we don't want to mess with the heat. This is a hack, I know,
        // but it's expedient
        if( s_heatSource != 0 /*&& dt == getPhysicalSystem().getDt()*/ ) {

            // This original line of code doesn't make sense to me, nor does the comment above the line
            //replacing it
//        if( s_heatSource != 0 && dt == getPhysicalSystem().getDt() ) {
            List containedBodies = this.getContainedBodies();
            for( int i = 0; i < containedBodies.size(); i++ ) {
                Particle body = (Particle)containedBodies.get( i );
                body.setVelocity( body.getVelocity().scale( 1 + s_heatSource / 1000 ) );
            }
        }
    }

    public Rectangle2D getOpening() {
        return opening;
    }

    private void setOpening() {
        double o2x = getPosition().getX() + oxOffset;
        double o1x = getPosition().getX() - oxOffset;
        double o1y = getPosition().getY() + oyOffset;
        opening = new Rectangle2D.Double( o1x, o1y, o2x - o1x, 20 );
    }

    public boolean isInContactWithParticle( SphericalBody particle ) {
        boolean result = false;
        if( isInOpening( particle ) ) {
            result = false;
        }
        else {
            result = super.isInContactWithParticle( particle );
        }
        return result;
    }

    public boolean isInOpening( SphericalBody particle ) {
        double px = particle.getPosition().getX();
        double py = particle.getPosition().getY();
        boolean b = px - particle.getRadius() >= opening.getMinX()
                    && px + particle.getRadius() <= opening.getMaxX()
                    && py + particle.getRadius() >= opening.getMinY();
        return b;
    }
}
