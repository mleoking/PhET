package edu.colorado.phet.movingman.motion.force1d;

import edu.colorado.phet.common.motion.model.AccelerationDriven;
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.MotionBody;
import edu.colorado.phet.common.motion.model.UpdateStrategy;

/**
 * Created by: Sam
 * Dec 5, 2007 at 3:32:30 AM
 */
public class ForceModel implements UpdateStrategy {
    private DefaultTemporalVariable appliedForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable frictionForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable wallForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable netForce = new DefaultTemporalVariable();

    private DefaultTemporalVariable time = new DefaultTemporalVariable();

    private DefaultTemporalVariable position = new DefaultTemporalVariable();
    private DefaultTemporalVariable velocity = new DefaultTemporalVariable();
    private DefaultTemporalVariable acceleration = new DefaultTemporalVariable();

    private DefaultTemporalVariable gravity = new DefaultTemporalVariable();
    private DefaultTemporalVariable staticFriction = new DefaultTemporalVariable();
    private DefaultTemporalVariable kineticFriction = new DefaultTemporalVariable();
    private DefaultTemporalVariable mass = new DefaultTemporalVariable( 1.0 );

    private AccelerationDriven accelerationDriven = new AccelerationDriven();

    public void setAppliedForce( double appliedForceValue ) {
        appliedForce.setValue( appliedForceValue );
        netForce.setValue( appliedForce.getValue() + frictionForce.getValue() + wallForce.getValue() );
        acceleration.setValue( netForce.getValue() / mass.getValue() );
    }

    public void update( MotionBody motionBody, double dt, double time ) {
        motionBody.setAcceleration( acceleration.getValue() );
        accelerationDriven.update( motionBody, dt, time );
    }

}
