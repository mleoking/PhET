package edu.colorado.phet.movingman.motion.force1d;

import java.awt.*;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.IMotionBody;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.movingman.motion.movingman.MovingManMotionModel;

/**
 * Created by: Sam
 * Dec 5, 2007 at 3:32:30 AM
 */
public class ForceModel extends MovingManMotionModel implements UpdateStrategy, IForceModel {

    //handled in parent hierarchy: time, x,v,a

    private DefaultTemporalVariable appliedForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable frictionForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable wallForce = new DefaultTemporalVariable();
    private DefaultTemporalVariable netForce = new DefaultTemporalVariable();

    private DefaultTemporalVariable gravity = new DefaultTemporalVariable();
    private DefaultTemporalVariable staticFriction = new DefaultTemporalVariable();
    private DefaultTemporalVariable kineticFriction = new DefaultTemporalVariable();
    private DefaultTemporalVariable mass = new DefaultTemporalVariable( 1.0 );

    private ControlGraphSeries appliedForceSeries = new ControlGraphSeries( "Fa", Color.blue, "x", "m", new BasicStroke( 2 ), true, null, appliedForce );
    private ControlGraphSeries frictionForceSeries = new ControlGraphSeries( "Ff", Color.red, "v", "m/s", new BasicStroke( 2 ), true, null, frictionForce );
    private ControlGraphSeries wallForceSeries = new ControlGraphSeries( "Fw", Color.green, "a", "m/s^2", new BasicStroke( 2 ), true, null, wallForce );
    private ControlGraphSeries netForceSeries = new ControlGraphSeries( "Fnet", Color.green, "a", "m/s^2", new BasicStroke( 2 ), true, null, netForce );

    private ControlGraphSeries gravitySeries = new ControlGraphSeries( "Fg", Color.green, "a", "m/s^2", new BasicStroke( 2 ), true, null, gravity );
    private ControlGraphSeries staticFrictionSeries = new ControlGraphSeries( "us", Color.green, "a", "m/s^2", new BasicStroke( 2 ), true, null, staticFriction );
    private ControlGraphSeries kineticFrictionSeries = new ControlGraphSeries( "uk", Color.green, "a", "m/s^2", new BasicStroke( 2 ), true, null, kineticFriction );
    private ControlGraphSeries massSeries = new ControlGraphSeries( "m", Color.green, "a", "m/s^2", new BasicStroke( 2 ), true, null, mass );

    public ForceModel( ConstantDtClock clock ) {
        super( clock );
        addTemporalVariables( getForce1DVars() );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        defaultUpdate( getForce1DVars() );
    }

    private ITemporalVariable[] getForce1DVars() {
        return new ITemporalVariable[]{appliedForce, frictionForce, wallForce, netForce, gravity, staticFriction, kineticFriction, mass};
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

    public ControlGraphSeries getAppliedForceSeries() {
        return appliedForceSeries;
    }

    public DefaultTemporalVariable getAppliedForce() {
        return appliedForce;
    }

    public DefaultTemporalVariable getFrictionForce() {
        return frictionForce;
    }

    public DefaultTemporalVariable getWallForce() {
        return wallForce;
    }

    public DefaultTemporalVariable getNetForce() {
        return netForce;
    }

    public DefaultTemporalVariable getGravity() {
        return gravity;
    }

    public DefaultTemporalVariable getStaticFriction() {
        return staticFriction;
    }

    public DefaultTemporalVariable getKineticFriction() {
        return kineticFriction;
    }

    public DefaultTemporalVariable getMass() {
        return mass;
    }

    public ControlGraphSeries getFrictionForceSeries() {
        return frictionForceSeries;
    }

    public ControlGraphSeries getWallForceSeries() {
        return wallForceSeries;
    }

    public ControlGraphSeries getNetForceSeries() {
        return netForceSeries;
    }

    public ControlGraphSeries getGravitySeries() {
        return gravitySeries;
    }

    public ControlGraphSeries getStaticFrictionSeries() {
        return staticFrictionSeries;
    }

    public ControlGraphSeries getKineticFrictionSeries() {
        return kineticFrictionSeries;
    }

    public ControlGraphSeries getMassSeries() {
        return massSeries;
    }
}
