package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.SeriesVariable;

import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:10:11 AM
 */
public class TorqueModel extends RotationModel {
    private SeriesVariable torque = new SeriesVariable();
    private SeriesVariable force = new SeriesVariable();
    private SeriesVariable momentOfInertia = new SeriesVariable();
    private SeriesVariable angularMomentum = new SeriesVariable();

    private UpdateStrategy forceDriven = new ForceDriven();
    private UpdateStrategy torqueDriven = new TorqueDriven();

    private Line2D.Double appliedForce = new Line2D.Double();
    private ArrayList listeners = new ArrayList();

    public TorqueModel( ConstantDtClock clock ) {
        super( clock );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        momentOfInertia.updateSeriesAndState( getRotationPlatform().getMomentOfInertia(), getTime() );
        angularMomentum.updateSeriesAndState( getRotationPlatform().getMomentOfInertia() * getRotationPlatform().getVelocity(), getTime() );
        torque.updateSeriesAndState( torque.getValue(), getTime() );
        force.updateSeriesAndState( force.getValue(), getTime() );
    }

    protected void setTime( double time ) {
        super.setTime( time );
        torque.setValueForTime( time );
        force.setValueForTime( time );
    }

    public ISimulationVariable getTorqueVariable() {
        return torque.getVariable();
    }

    public ITimeSeries getTorqueTimeSeries() {
        return torque.getSeries();
    }

    public UpdateStrategy getTorqueDriven() {
        return torqueDriven;
    }

    public ISimulationVariable getForceVariable() {
        return force.getVariable();
    }

    public ITimeSeries getForceTimeSeries() {
        return force.getSeries();
    }

    public UpdateStrategy getForceDriven() {
        return forceDriven;
    }

    public ISimulationVariable getMomentOfInertiaVariable() {
        return momentOfInertia.getVariable();
    }

    public ITimeSeries getMomentOfInertiaTimeSeries() {
        return momentOfInertia.getSeries();
    }

    public ISimulationVariable getAngularMomentumVariable() {
        return angularMomentum.getVariable();
    }

    public ITimeSeries getAngularMomentumTimeSeries() {
        return angularMomentum.getSeries();
    }

    public class TorqueDriven implements UpdateStrategy {
        public void update( MotionBodySeries model, double dt, MotionBodyState state, double time ) {//todo: factor out duplicated code in AccelerationDriven
            //assume a constant acceleration model with the given acceleration.
            double acceleration = torque.getValue() / getRotationPlatform().getMomentOfInertia();
            double origAngVel = state.getVelocity();
            model.addAccelerationData( acceleration, time );
            model.addVelocityData( state.getVelocity() + acceleration * dt, time );
            model.addPositionData( state.getPosition() + ( state.getVelocity() + origAngVel ) / 2.0 * dt, time );
        }
    }

    public class ForceDriven implements UpdateStrategy {
        public void update( MotionBodySeries model, double dt, MotionBodyState state, double time ) {//todo: factor out duplicated code in AccelerationDriven
            //assume a constant acceleration model with the given acceleration.
            double torqu = force.getValue() * getRotationPlatform().getRadius();//todo: generalize to r x F (4 parameters)
            double acceleration = torqu / getRotationPlatform().getMomentOfInertia();
            torque.setValue( torqu );
//            torqueSeries.addValue( torque, );
            double origAngVel = state.getVelocity();
            model.addAccelerationData( acceleration, time );
            model.addVelocityData( state.getVelocity() + acceleration * dt, time );
            model.addPositionData( state.getPosition() + ( state.getVelocity() + origAngVel ) / 2.0 * dt, time );
        }
    }

    public Line2D.Double getAppliedForce() {
        return appliedForce;
    }

    public void setAppliedForce( Line2D.Double appliedForce ) {
        getRotationPlatform().setUpdateStrategy( torqueDriven );
        this.appliedForce = appliedForce;
        //determine the new applied torque
        torque.setValue( appliedForce.getP1().distance( appliedForce.getP2() ) );

        //todo: verify whether nontangential forces are allowed
        notifyListeners();//todo: only notify if changed
    }

    public static interface Listener {
        void changed();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    private void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).changed();
        }
    }
}
