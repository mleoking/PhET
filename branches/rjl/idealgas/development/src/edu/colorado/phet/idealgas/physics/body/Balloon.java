/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Jan 15, 2003
 * Time: 1:53:09 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.physics.body;

import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.idealgas.physics.body.HollowSphere;
import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.idealgas.physics.ScalarDataRecorder;
import edu.colorado.phet.idealgas.physics.IdealGasSystem;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.body.Body;

public class Balloon extends HollowSphere {

    private ScalarDataRecorder insidePressureRecorder = new ScalarDataRecorder();
    private ScalarDataRecorder outsidePressureRecorder = new ScalarDataRecorder();

    // Attributes for adjusting the size of the balloon
    private int timeStepsSinceLastRadiusAdjustment = 0;
    private int timeStepsBetweenRadiusAdjustments = 20;
    private double aveInOutPressureRatio = 0;
    private double dampingFactor = 0.1;
    // Temporary varaibles, pre-allocated for performance
    private Vector2D momentumPre = new Vector2D();
    private Vector2D momentumPost = new Vector2D();

    /**
     *
     * @param center
     * @param velocity
     * @param acceleration
     * @param mass
     * @param radius
     */
    public Balloon( Vector2D center,
                    Vector2D velocity,
                    Vector2D acceleration,
                    float mass,
                    float radius ) {
        super( center, velocity, acceleration, mass, radius );
    }

    public void reInitialize() {
        super.reInitialize();
        insidePressureRecorder.clear();
        outsidePressureRecorder.clear();
    }

    /**
     * Records the impact on the inside or outside of the balloon
     */
    public void collideWithParticle( Particle particle ) {

        // Get the momentum of the balloon before the collision
        momentumPre.setX( getVelocity().getX() );
        momentumPre.setY( getVelocity().getY() );
        momentumPre = momentumPre.multiply( this.getMass() );

        // Perform the collision
        super.collideWithParticle( particle );

        // Get the new momentum of the balloon
        momentumPost.setX( this.getVelocity().getX() );
        momentumPost.setY( this.getVelocity().getY() );
        momentumPost = momentumPost.multiply( this.getMass() );

        // Compute the change in momentum and record it as pressure
        Vector2D momentumChange = momentumPost.subtract( momentumPre );
        float impact = momentumChange.getLength();
        ScalarDataRecorder recorder = this.containsBody( particle )
                ? insidePressureRecorder
                : outsidePressureRecorder;
        recorder.addDataRecordEntry( impact );
    }

    /**
     *
     * @return
     */
    public float getInsidePressure() {
        return insidePressureRecorder.getDataTotal();
    }

    /**
     *
     * @return
     */
    public float getOutsidePressure() {
        return outsidePressureRecorder.getDataTotal();
    }

    /**
     *
     * @param dt
     */
    public void stepInTime( float dt ) {

        super.stepInTime( dt );

        // Compute average pressure differential
        double currInOutPressureRatio = insidePressureRecorder.getDataTotal() / outsidePressureRecorder.getDataTotal();

        if( !Double.isNaN( currInOutPressureRatio )
                && currInOutPressureRatio != 0
                && currInOutPressureRatio != Double.POSITIVE_INFINITY
                && currInOutPressureRatio != Double.NEGATIVE_INFINITY ) {
            aveInOutPressureRatio = ( ( aveInOutPressureRatio * timeStepsSinceLastRadiusAdjustment )
                    + ( insidePressureRecorder.getDataTotal() / outsidePressureRecorder.getDataTotal() ) )
                    / ( ++timeStepsSinceLastRadiusAdjustment );
        }

        // Adjust the radius of the balloon
        //Make sure the balloon doesn't expand beyond the box
        Box2D box = ((IdealGasSystem)this.getPhysicalSystem()).getBox();
        double maxRadius = Math.min( ( box.getMaxX() - box.getMinX() ) / 2,
                           ( box.getMaxY() - box.getMinY()) / 2 );
        if( timeStepsSinceLastRadiusAdjustment >= timeStepsBetweenRadiusAdjustments ) {
            timeStepsSinceLastRadiusAdjustment = 0;

            float newRadius =  (float)Math.min( this.getRadius() * Math.pow( aveInOutPressureRatio, dampingFactor ), maxRadius );
            if( !Double.isNaN( newRadius )) {
                newRadius = Math.max( newRadius, 20 );
                this.setRadius( newRadius );
            }
        }
    }

    private void setRadius( float newRadius ) {
        this.setRadius( newRadius );
    }

    /**
     *
     * @param body
     * @return
     */
    public boolean isInContactWithBody( Body body ) {
        throw new RuntimeException( "Not implemented" );
    }
}

