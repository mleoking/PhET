package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.MotionBody;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.model.RotationModel;

import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:10:11 AM
 */
public class TorqueModel extends RotationModel {
    private DefaultTemporalVariable torque = new DefaultTemporalVariable();
    private DefaultTemporalVariable force = new DefaultTemporalVariable();
    private DefaultTemporalVariable momentOfInertia = new DefaultTemporalVariable();
    private DefaultTemporalVariable angularMomentum = new DefaultTemporalVariable();
    private DefaultTemporalVariable brakeForce = new DefaultTemporalVariable();

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
        momentOfInertia.addValue( getRotationPlatform().getMomentOfInertia(), getTime() );
        angularMomentum.addValue( getRotationPlatform().getMomentOfInertia() * getRotationPlatform().getVelocity(), getTime() );
        brakeForce.addValue( brakeForce.getValue(), getTime() );
        torque.addValue( torque.getValue(), getTime() );
        force.addValue( force.getValue(), getTime() );
    }

    protected void setPlaybackTime( double time ) {
        super.setPlaybackTime( time );
        torque.setPlaybackTime( time );
        force.setPlaybackTime( time );
    }

    public double getBrakeForce() {
        return brakeForce.getValue();
    }

    public void setBrakeForce( double brakeForceValue ) {
        if( brakeForceValue != getBrakeForce() ) {
            brakeForce.setValue( brakeForceValue );
            for( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener)listeners.get( i ) ).brakeForceChanged();
            }
        }
    }

    public ITemporalVariable getTorqueTimeSeries() {
        return torque;
    }

    public UpdateStrategy getTorqueDriven() {
        return torqueDriven;
    }

    public ITemporalVariable getForceTimeSeries() {
        return force;
    }

    public UpdateStrategy getForceDriven() {
        return forceDriven;
    }

    public ITemporalVariable getMomentOfInertiaTimeSeries() {
        return momentOfInertia;
    }

    public ITemporalVariable getAngularMomentumTimeSeries() {
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
        public void update( MotionBody motionBody, double dt, double time ) {//todo: factor out duplicated code in AccelerationDriven
            //assume a constant acceleration model with the given acceleration.
            finishUpdate( motionBody, dt, time );
        }
    }

    public class ForceDriven implements UpdateStrategy {
        public void update( MotionBody motionBody, double dt, double time ) {//todo: factor out duplicated code in AccelerationDriven
            //assume a constant acceleration model with the given acceleration.
            torque.setValue( force.getValue() * getRotationPlatform().getRadius() );
            finishUpdate( motionBody, dt, time );
        }
    }

    private void finishUpdate( MotionBody motionBody, double dt, double time ) {
        double mu = 1.2;
        double brakeForceVal = mu * brakeForce.getValue();
        if( Math.abs( motionBody.getVelocity() ) < 1E-6 ) {
            brakeForceVal = 0.0;
        }
        double origAngVel = motionBody.getVelocity();
//        double origPosition
        double netTorque = torque.getValue() - MathUtil.getSign( origAngVel ) * brakeForceVal;

//        System.out.println( "netTorque = " + netTorque + ", vel=" + origAngVel + ", torque=" + torque.getValue() + ", brakeForce=" + brakeForceVal );

        double acceleration = netTorque / getRotationPlatform().getMomentOfInertia();
        double proposedVelocity = motionBody.getVelocity() + acceleration * dt;
        if( MathUtil.getSign( proposedVelocity ) != MathUtil.getSign( origAngVel ) && brakeForceVal > Math.abs( torque.getValue() ) ) {
            proposedVelocity = 0.0;
        }

        motionBody.addAccelerationData( acceleration, time );
        motionBody.addVelocityData( proposedVelocity, time );

        //if the friction causes the velocity to change sign, set the velocity to zero?

        motionBody.addPositionData( motionBody.getPosition() + ( motionBody.getVelocity() + origAngVel ) / 2.0 * dt, time );
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
        if( x.getMagnitude() == 0 ) {
            return new Line2D.Double( appliedForce.getP1(), appliedForce.getP1() );
        }
        double magnitude = new Vector2D.Double( appliedForce.getP1(), appliedForce.getP2() ).dot( x.getNormalizedInstance() );
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

        void brakeForceChanged();
    }

    public static class Adapter implements Listener {
        public void appliedForceChanged() {
        }

        public void showComponentsChanged() {
        }

        public void brakeForceChanged() {
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
