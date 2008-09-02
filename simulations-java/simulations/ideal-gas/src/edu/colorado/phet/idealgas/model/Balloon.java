/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 15, 2003
 * Time: 1:53:09 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.idealgas.collision.CollidableBody;

import java.awt.geom.Point2D;

public class Balloon extends HollowSphere {

    public static final double MIN_RADIUS = 10;

    // Attributes for adjusting the size of the balloon
    private int timeStepsSinceLastRadiusAdjustment = 0;
    private int timeStepsBetweenRadiusAdjustments = 5;
    // Exponent for the power function that adjusts the size of the balloon when
    // the internal or external pressure changes
    private double dampingExponent = 0.02;
    // Temporary varaibles, pre-allocated for performance
    private Vector2D momentumPre = new Vector2D.Double();
    private Vector2D momentumPost = new Vector2D.Double();
    private Box2D box;
    private double accumulatedImpact = 0;


    /**
     * @param center
     * @param velocity
     * @param acceleration
     * @param mass
     * @param radius
     */
    public Balloon( Point2D center,
                    Vector2D velocity,
                    Vector2D acceleration,
                    double mass,
                    double radius,
                    Box2D box ) {
        super( center, velocity, acceleration, mass, radius );
        this.box = box;
    }

    /**
     * Records the impact on the inside or outside of the balloon
     */
    public void collideWithParticle( CollidableBody particle ) {

        // Get the new momentum of the balloon
        momentumPost.setX( this.getVelocity().getX() );
        momentumPost.setY( this.getVelocity().getY() );
        momentumPost = momentumPost.scale( this.getMass() );

        // Compute the change in momentum and record it as pressure
        Vector2D momentumChange = momentumPost.subtract( momentumPre );
        double impact = momentumChange.getMagnitude();
        // todo: change this to a test that relies on containsBody, when that is correctly implemented
        int sign = this.contains( particle ) ? 1 : -1;
        accumulatedImpact += impact * sign;
        momentumPre.setComponents( momentumPost.getX(), momentumPost.getY() );

        // Adjust the size of the balloon
        if( timeStepsSinceLastRadiusAdjustment >= timeStepsBetweenRadiusAdjustments ) {
            adjustRadius();
            // Reset accumulators
            accumulatedImpact = 0;
            timeStepsSinceLastRadiusAdjustment = 0;
        }

        // This will take care of containment issues
        super.collideWithParticle( particle );
    }

    private void adjustRadius() {
        // Adjust the radius of the balloon
        //Make sure the balloon doesn't expand beyond the box
        double maxRadius = 0.99 * Math.min( ( box.getMaxX() - box.getMinX() ) / 2,
                                            ( box.getMaxY() - box.getMinY() ) / 2 );
        double dr = Math.pow( Math.abs( accumulatedImpact ), dampingExponent ) * MathUtil.getSign( accumulatedImpact );
        double newRadius = Math.min( this.getRadius() + dr, maxRadius );
        if( !Double.isNaN( newRadius ) ) {
            newRadius = Math.min( maxRadius, Math.max( newRadius, MIN_RADIUS ) );
            this.setRadius( newRadius );
        }
    }

    /**
     * @param dt
     */
    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        timeStepsSinceLastRadiusAdjustment++;
    }
}

