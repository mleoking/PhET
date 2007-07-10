package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.rotation.model.RotationModel;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:10:11 AM
 */
public class TorqueModel extends RotationModel {

    private ISimulationVariable torqueVariable = new DefaultSimulationVariable();
    private ITimeSeries torqueSeries = new DefaultTimeSeries();
    private UpdateStrategy torqueDriven = new TorqueDriven();

    private ISimulationVariable forceVariable = new DefaultSimulationVariable();
    private ITimeSeries forceTimeSeries = new DefaultTimeSeries();
    private UpdateStrategy forceDriven = new ForceDriven();

    private ISimulationVariable momentOfInertiaVariable = new DefaultSimulationVariable();
    private ITimeSeries momentOfInertiaTimeSeries = new DefaultTimeSeries();
    
    private ISimulationVariable angularMomentumVariable = new DefaultSimulationVariable();
    private ITimeSeries angularMomentumTimeSeries = new DefaultTimeSeries();

    public TorqueModel( ConstantDtClock clock ) {
        super( clock );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        torqueSeries.addValue( torqueVariable.getValue(), getTime() );
        forceTimeSeries.addValue( forceVariable.getValue(), getTime() );
    }

    public ISimulationVariable getTorqueVariable() {
        return torqueVariable;
    }

    public ITimeSeries getTorqueTimeSeries() {
        return torqueSeries;
    }

    public UpdateStrategy getTorqueDriven() {
        return torqueDriven;
    }

    public ISimulationVariable getForceVariable() {
        return forceVariable;
    }

    public ITimeSeries getForceTimeSeries() {
        return forceTimeSeries;
    }

    public UpdateStrategy getForceDriven() {
        return forceDriven;
    }

    public ISimulationVariable getMomentOfInertiaVariable() {
        return momentOfInertiaVariable;
    }

    public ITimeSeries getMomentOfInertiaTimeSeries() {
        return momentOfInertiaTimeSeries;
    }

    public ISimulationVariable getAngularMomentumVariable() {
        return angularMomentumVariable;
    }

    public ITimeSeries getAngularMomentumTimeSeries() {
        return angularMomentumTimeSeries;
    }

    public class TorqueDriven implements UpdateStrategy {
        public void update( MotionBodySeries model, double dt, MotionBodyState state, double time ) {//todo: factor out duplicated code in AccelerationDriven
            //assume a constant acceleration model with the given acceleration.
            double acceleration = torqueVariable.getValue() / getRotationPlatform().getMomentOfInertia();
            double origAngVel = state.getVelocity();
            model.addAccelerationData( acceleration, time );
            model.addVelocityData( state.getVelocity() + acceleration * dt, time );
            model.addPositionData( state.getPosition() + ( state.getVelocity() + origAngVel ) / 2.0 * dt, time );
        }
    }

    public class ForceDriven implements UpdateStrategy {
        public void update( MotionBodySeries model, double dt, MotionBodyState state, double time ) {//todo: factor out duplicated code in AccelerationDriven
            //assume a constant acceleration model with the given acceleration.
            double torque = forceVariable.getValue() * getRotationPlatform().getRadius();//todo: generalize to r x F (4 parameters)
            double acceleration = torque / getRotationPlatform().getMomentOfInertia();
            torqueVariable.setValue( torque );
//            torqueSeries.addValue( torque, );
            double origAngVel = state.getVelocity();
            model.addAccelerationData( acceleration, time );
            model.addVelocityData( state.getVelocity() + acceleration * dt, time );
            model.addPositionData( state.getPosition() + ( state.getVelocity() + origAngVel ) / 2.0 * dt, time );
        }
    }
}
