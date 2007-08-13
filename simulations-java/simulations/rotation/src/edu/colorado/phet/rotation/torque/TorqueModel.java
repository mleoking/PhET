package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
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
    private boolean allowNonTangentialForces = false;
    private boolean showComponents = true;

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
        return torque;
    }

    public ITimeSeries getTorqueTimeSeries() {
        return torque;
    }

    public UpdateStrategy getTorqueDriven() {
        return torqueDriven;
    }

    public ISimulationVariable getForceVariable() {
        return force;
    }

    public ITimeSeries getForceTimeSeries() {
        return force;
    }

    public UpdateStrategy getForceDriven() {
        return forceDriven;
    }

    public ISimulationVariable getMomentOfInertiaVariable() {
        return momentOfInertia;
    }

    public ITimeSeries getMomentOfInertiaTimeSeries() {
        return momentOfInertia;
    }

    public ISimulationVariable getAngularMomentumVariable() {
        return angularMomentum;
    }

    public ITimeSeries getAngularMomentumTimeSeries() {
        return angularMomentum;
    }

    public boolean isAllowNonTangentialForces() {
        return allowNonTangentialForces;
    }

    public void setAllowNonTangentialForces( boolean selected ) {
        this.allowNonTangentialForces = selected;
    }

    public double getAppliedForceMagnitude() {
        return getAppliedForce().getP1().distance( getAppliedForce().getP2() );
    }

    public boolean isShowComponents() {
        return showComponents;
    }

    public void setShowComponents( boolean selected ) {
        if( selected != showComponents ) {
            this.showComponents = selected;
            for( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener)listeners.get( i ) ).showComponentsChanged();
            }
        }
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

    public Line2D.Double getTangentialAppliedForce() {
        return getTangentialAppliedForce( appliedForce );
    }

    public void setAppliedForce( Line2D.Double appliedForce ) {
        Line2D.Double orig = new Line2D.Double( this.appliedForce.getP1(), this.appliedForce.getP2() );
        if( !allowNonTangentialForces ) {
            appliedForce = getTangentialAppliedForce( appliedForce );
        }
        getRotationPlatform().setUpdateStrategy( torqueDriven );
        this.appliedForce = appliedForce;
        //determine the new applied torque
        //torque=r x F
        Vector2D.Double r = new Vector2D.Double( getRotationPlatform().getCenter(), appliedForce.getP1() );
        Vector2D.Double f = new Vector2D.Double( appliedForce.getP1(), appliedForce.getP2() );
        double tau = -r.getCrossProductScalar( f );
        torque.setValue( tau );

        notifyAppliedForceChanged();//todo: only notify if changed
    }

    private Line2D.Double getTangentialAppliedForce( Line2D.Double appliedForce ) {
//        double dist = appliedForce.getP1().distance( appliedForce.getP2() );
        Vector2D.Double v = new Vector2D.Double( appliedForce.getP1(), getRotationPlatform().getCenter() );
        v.rotate( Math.PI / 2 );
        if( v.dot( new Vector2D.Double( appliedForce.getP1(), appliedForce.getP2() ) ) < 0 ) {
            v.rotate( Math.PI );
        }

        AbstractVector2D x = v;//.getInstanceOfMagnitude( dist );
        if (x.getMagnitude()==0){
            return new Line2D.Double( appliedForce.getP1(), appliedForce.getP1() );
        }
        double magnitude = new Vector2D.Double( appliedForce.getP1(), appliedForce.getP2() ).dot( x.getNormalizedInstance( ) );
//        System.out.println( "magnitude = " + magnitude );
        if( magnitude != 0 ) {
            x = x.getInstanceOfMagnitude( magnitude );
        }
        else {
            x = new Vector2D.Double( 0, 0 );
        }
//        Vector2D rHat = new Vector2D.Double( appliedForce.getP1(), getRotationPlatform().getCenter() );
//        double scale = Math.abs( x.dot( rHat.getNormalVector() ));
//        x = x.getScaledInstance( scale/dist );
        return new Line2D.Double( appliedForce.getP1(), x.getDestination( appliedForce.getP1() ) );
    }

    public static interface Listener {
        void appliedForceChanged();

        void showComponentsChanged();
    }

    public static class Adapter implements Listener {
        public void appliedForceChanged() {
        }

        public void showComponentsChanged() {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    private void notifyAppliedForceChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).appliedForceChanged();
        }
    }
}
