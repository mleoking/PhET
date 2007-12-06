package edu.colorado.phet.movingman.motion.force1d;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.IMotionBody;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.movingman.motion.movingman.MovingManMotionModel;

/**
 * Created by: Sam
 * Dec 5, 2007 at 3:32:30 AM
 */
public class ForceModel extends MovingManMotionModel implements UpdateStrategy, IForceModel {
    private DefaultTemporalVariable appliedForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable frictionForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable wallForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable netForce = new DefaultTemporalVariable();

    //handled in parent hierarchy
//    private DefaultTemporalVariable time = new DefaultTemporalVariable();
//    private DefaultTemporalVariable position = new DefaultTemporalVariable();
//    private DefaultTemporalVariable velocity = new DefaultTemporalVariable();
//    private DefaultTemporalVariable acceleration = new DefaultTemporalVariable();

    private DefaultTemporalVariable gravity = new DefaultTemporalVariable();
    private DefaultTemporalVariable staticFriction = new DefaultTemporalVariable();
    private DefaultTemporalVariable kineticFriction = new DefaultTemporalVariable();
    private DefaultTemporalVariable mass = new DefaultTemporalVariable( 1.0 );

    public ForceModel( ConstantDtClock clock ) {
        super( clock );
    }

    public void setAppliedForce( double appliedForceValue ) {
        appliedForce.setValue( appliedForceValue );
        netForce.setValue( appliedForce.getValue() + frictionForce.getValue() + wallForce.getValue() );
        getAVariable().setValue( netForce.getValue() / mass.getValue() );
    }

    public void update( IMotionBody motionBody, double dt, double time ) {
        motionBody.setAcceleration( getAcceleration() );
//        System.out.println( "acceleration.getValue() = " + acceleration.getValue() );
        getAccelDriven().update( motionBody, dt, time );
    }

}
