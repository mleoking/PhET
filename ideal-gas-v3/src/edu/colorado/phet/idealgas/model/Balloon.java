/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 15, 2003
 * Time: 1:53:09 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.collision.CollidableBody;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.util.ScalarDataRecorder;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;

public class Balloon extends HollowSphere {

    public static final double MIN_RADIUS = 10;

    private ScalarDataRecorder insidePressureRecorder;
    private ScalarDataRecorder outsidePressureRecorder;

    // Attributes for adjusting the size of the balloon
    private int timeStepsSinceLastRadiusAdjustment = 0;
    private int timeStepsBetweenRadiusAdjustments = 5;
//    private int timeStepsBetweenRadiusAdjustments = 20;
    private double aveInOutPressureRatio = 0;
    // Exponent for the power function that adjusts the size of the balloon when
    // the internal or external pressure changes
//    private double dampingExponent = 015;
    private double dampingExponent = 0.02;
    // Temporary varaibles, pre-allocated for performance
    private Vector2D momentumPre = new Vector2D.Double();
    private Vector2D momentumPost = new Vector2D.Double();
    private Box2D box;
    private double aDouble;


    public static Balloon instance;

    /**
     * @param center
     * @param velocity
     * @param acceleration
     * @param mass
     * @param radius
     */
    public Balloon(Point2D center,
                   Vector2D velocity,
                   Vector2D acceleration,
                   double mass,
                   double radius,
                   Box2D box,
                   AbstractClock clock) {
        super(center, velocity, acceleration, mass, radius);
        this.box = box;
        insidePressureRecorder = new ScalarDataRecorder(clock);
        outsidePressureRecorder = new ScalarDataRecorder(clock);

        instance = this;
    }

//    public void reInitialize() {
//        super.reInitialize();
//        insidePressureRecorder.clear();
//        outsidePressureRecorder.clear();
//    }

    /**
     * Records the impact on the inside or outside of the balloon
     */
//    SphericalBody sphericalBody = new SphericalBody( new Point2D.Double(),
//                                                     new Vector2D.Double(),
//                                                     new Vector2D.Double(),
//                                                     0,
//                                                     GasMolecule.s_defaultRadius );

    public void collideWithParticle(CollidableBody particle) {

        // Get the momentum of the balloon before the collision
//        momentumPre.setX( getVelocity().getX() );
//        momentumPre.setY( getVelocity().getY() );
//        momentumPre = momentumPre.scale( this.getMass() );

        // This bizarre copying from one object to another is a total hack that
        // was made neccessary by the creation of the CollisionGod class, and the
        // fact that some of the system uses Particles from the common code, and
        // some of it uses Particles from the ideal gas code. It is an embarassing
        // mess that ought to be straightened out.
//        sphericalBody.setPosition( particle.getPosition() );
//        sphericalBody.setVelocity( particle.getVelocity() );
//        super.collideWithParticle( sphericalBody );

        // Get the new momentum of the balloon
        momentumPost.setX(this.getVelocity().getX());
        momentumPost.setY(this.getVelocity().getY());
        momentumPost = momentumPost.scale(this.getMass());

        // Compute the change in momentum and record it as pressure
        Vector2D momentumChange = momentumPost.subtract(momentumPre);
        double impact = momentumChange.getMagnitude();
        // todo: change this to a test that relies on containsBody, when that is correctly implemented
        ScalarDataRecorder recorder = this.contains(particle)
                ? insidePressureRecorder
                : outsidePressureRecorder;
        recorder.addDataRecordEntry(impact);
        momentumPre.setComponents(momentumPost.getX(), momentumPost.getY());
    }

    private boolean contains(Body body) {
        double distSq = this.getCenter().distanceSq(body.getCM());
        return distSq < this.getRadius() * this.getRadius();
    }

    /**
     * @param dt
     */
    public void stepInTime(double dt) {

        super.stepInTime(dt);

        // Compute average pressure differential
        insidePressureRecorder.computeDataStatistics();
        outsidePressureRecorder.computeDataStatistics();
        double outsidePressure = Math.max(outsidePressureRecorder.getDataTotal(), 1);
        double currInOutPressureRatio = insidePressureRecorder.getDataTotal() / outsidePressure;
//        double currInOutPressureRatio = insidePressureRecorder.getDataTotal() / outsidePressureRecorder.getDataTotal();
        if (!Double.isNaN(currInOutPressureRatio)
                && currInOutPressureRatio != 0
                && currInOutPressureRatio != Double.POSITIVE_INFINITY
                && currInOutPressureRatio != Double.NEGATIVE_INFINITY) {
            aveInOutPressureRatio = ((aveInOutPressureRatio * timeStepsSinceLastRadiusAdjustment)
                    + (insidePressureRecorder.getDataTotal() / outsidePressureRecorder.getDataTotal()))
                    / (++timeStepsSinceLastRadiusAdjustment);
        }

        // Adjust the radius of the balloon
        //Make sure the balloon doesn't expand beyond the box
        double maxRadius = Math.min((box.getMaxX() - box.getMinX()) / 2,
                (box.getMaxY() - box.getMinY()) / 2);
        timeStepsSinceLastRadiusAdjustment++;
        if (timeStepsSinceLastRadiusAdjustment >= timeStepsBetweenRadiusAdjustments) {
            timeStepsSinceLastRadiusAdjustment = 0;

            double newRadius = Math.min(this.getRadius() * Math.pow(aveInOutPressureRatio, dampingExponent), maxRadius);
            if (!Double.isNaN(newRadius)) {
                aDouble = newRadius = Math.min(maxRadius, Math.max(newRadius, MIN_RADIUS));
                this.setRadius(newRadius);
            }
        }
    }
}

