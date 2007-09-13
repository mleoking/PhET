package edu.colorado.phet.rotation.torque;

import java.awt.geom.Line2D;
import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.MotionBody;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;

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
    private DefaultTemporalVariable radius = new DefaultTemporalVariable();
    private DefaultTemporalVariable appliedForceMagnitude = new DefaultTemporalVariable();
    private DefaultTemporalVariable appliedForceSrcX = new DefaultTemporalVariable();
    private DefaultTemporalVariable appliedForceSrcY = new DefaultTemporalVariable();
    private DefaultTemporalVariable appliedForceDstX = new DefaultTemporalVariable();
    private DefaultTemporalVariable appliedForceDstY = new DefaultTemporalVariable();

    private UpdateStrategy forceDriven = new ForceDriven();
    private ArrayList listeners = new ArrayList();
    private boolean allowNonTangentialForces = false;
    private boolean showComponents = true;
    private boolean inited = false;

    public TorqueModel( ConstantDtClock clock ) {
        super( clock );
        getRotationPlatform().setUpdateStrategy( forceDriven );
        inited = true;
        clear();
        radius.setValue( RotationPlatform.MAX_RADIUS );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        momentOfInertia.addValue( getRotationPlatform().getMomentOfInertia(), getTime() );
        angularMomentum.addValue( getRotationPlatform().getMomentOfInertia() * getRotationPlatform().getVelocity(), getTime() );
        defaultUpdate( brakeForce );
        defaultUpdate( torque );
        defaultUpdate( force );
        defaultUpdate( radius );
        defaultUpdate( appliedForceMagnitude );
        defaultUpdate( appliedForceSrcX );
        defaultUpdate( appliedForceSrcY );
        defaultUpdate( appliedForceDstX );
        defaultUpdate( appliedForceDstY );
        notifyAppliedForceChanged();//todo: only notify during actual change for performance & elegance
    }

    private void defaultUpdate( ITemporalVariable variable ) {
        variable.addValue( variable.getValue(), getTime() );
    }

    protected void setPlaybackTime( double time ) {
        super.setPlaybackTime( time );
        setPlaybackTime( time, torque );
        setPlaybackTime( time, force );
        setPlaybackTime( time, brakeForce );
        setPlaybackTime( time, angularMomentum );
        setPlaybackTime( time, momentOfInertia );
        setPlaybackTime( time, radius );
        setPlaybackTime( time, appliedForceMagnitude );
        setPlaybackTime( time, appliedForceSrcX );
        setPlaybackTime( time, appliedForceSrcY );
        setPlaybackTime( time, appliedForceDstX );
        setPlaybackTime( time, appliedForceDstY );
        notifyAppliedForceChanged();//todo: only notify during actual change for performance & elegance
    }


    public void clear() {
        super.clear();
        if ( inited ) {
            torque.clear();
            force.clear();
            brakeForce.clear();
            angularMomentum.clear();
            momentOfInertia.clear();
            radius.clear();
            appliedForceMagnitude.clear();
            appliedForceSrcX.clear();
            appliedForceSrcY.clear();
            appliedForceDstX.clear();
            appliedForceDstY.clear();
        }
    }

    private void setPlaybackTime( double time, ITemporalVariable variable ) {
        variable.setPlaybackTime( time );
    }

    public double getBrakeForce() {
        return brakeForce.getValue();
    }

    public void setBrakeForce( double brakeForceValue ) {
        if ( brakeForceValue != getBrakeForce() ) {
            brakeForce.setValue( brakeForceValue );
            for ( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener) listeners.get( i ) ).brakeForceChanged();
            }
        }
    }

    public ITemporalVariable getTorqueTimeSeries() {
        return torque;
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
        if ( selected != showComponents ) {
            this.showComponents = selected;
            for ( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener) listeners.get( i ) ).showComponentsChanged();
            }
        }
    }

    public ITemporalVariable getRadiusSeries() {
        return radius;
    }

    public void setAppliedForceMagnitude( double appliedForceMag ) {
        appliedForceMagnitude.setValue( appliedForceMag );
        updateAppliedForceFromRF();
    }

    private void updateAppliedForceFromRF() {
        setAppliedForce( new Line2D.Double( getRotationPlatform().getCenter().getX(),
                                            getRotationPlatform().getCenter().getY() - radius.getValue(),
                                            getRotationPlatform().getCenter().getX() + appliedForceMagnitude.getValue(),
                                            getRotationPlatform().getCenter().getY() - radius.getValue() ) );
    }

    public void setAppliedForceRadius( double r ) {
        this.radius.setValue( r );
        updateAppliedForceFromRF();
    }

    public class ForceDriven implements UpdateStrategy {
        public void update( MotionBody motionBody, double dt, double time ) {//todo: factor out duplicated code in AccelerationDriven
            //assume a constant acceleration model with the given acceleration.
            torque.setValue( force.getValue() * getRotationPlatform().getRadius() );
            double mu = 1.2;
            double brakeForceVal = mu * brakeForce.getValue();
            if ( Math.abs( motionBody.getVelocity() ) < 1E-6 ) {
                brakeForceVal = 0.0;
            }
            double origAngVel = motionBody.getVelocity();
            double netTorque = torque.getValue() - MathUtil.getSign( origAngVel ) * brakeForceVal;

            double acceleration = netTorque / getRotationPlatform().getMomentOfInertia();
            double proposedVelocity = motionBody.getVelocity() + acceleration * dt;
            if ( MathUtil.getSign( proposedVelocity ) != MathUtil.getSign( origAngVel ) && brakeForceVal > Math.abs( torque.getValue() ) ) {
                proposedVelocity = 0.0;
            }

            motionBody.addAccelerationData( acceleration, time );
            motionBody.addVelocityData( proposedVelocity, time );

            //if the friction causes the velocity to change sign, set the velocity to zero?
            motionBody.addPositionData( motionBody.getPosition() + ( motionBody.getVelocity() + origAngVel ) / 2.0 * dt, time );
        }
    }

    public Line2D.Double getAppliedForce() {
        return new Line2D.Double( appliedForceSrcX.getValue(), appliedForceSrcY.getValue(),
                                  appliedForceDstX.getValue(), appliedForceDstY.getValue() );
    }

    public Line2D.Double getTangentialAppliedForce() {
        return getTangentialAppliedForce( getAppliedForce() );
    }

    /**
     * Computes the allowed portion of the desired applied force, result depends on whether allowNonTangentialForces is true
     *
     * @param appliedForce
     */
    public void setAllowedAppliedForce( Line2D.Double appliedForce ) {
        setAppliedForce( getAllowedAppliedForce( appliedForce ) );
    }

    public void setAppliedForce( Line2D.Double appliedForce ) {
        if ( !lineEquals( getAppliedForce(), appliedForce ) ) {
            this.appliedForceSrcX.setValue( appliedForce.getX1() );
            this.appliedForceSrcY.setValue( appliedForce.getY1() );
            this.appliedForceDstX.setValue( appliedForce.getX2() );
            this.appliedForceDstY.setValue( appliedForce.getY2() );

            this.radius.setValue( new Vector2D.Double( appliedForce.getP1() ).getMagnitude() );

            //determine the new applied torque
            //torque=r x F
            Vector2D.Double r = new Vector2D.Double( getRotationPlatform().getCenter(), appliedForce.getP1() );
            Vector2D.Double f = new Vector2D.Double( appliedForce.getP1(), appliedForce.getP2() );
            double tau = -r.getCrossProductScalar( f );
            torque.setValue( tau );
            force.setValue( torque.getValue() / getRotationPlatform().getRadius() );

            notifyAppliedForceChanged();
        }
    }

    private boolean lineEquals( Line2D.Double a, Line2D.Double b ) {
        return a.getP1().equals( b.getP1() ) && a.getP2().equals( b.getP2() );
    }

    private Line2D.Double getAllowedAppliedForce( Line2D.Double appliedForce ) {

        if ( !allowNonTangentialForces ) {
            appliedForce = getTangentialAppliedForce( appliedForce );
        }
        return appliedForce;
    }

    private Line2D.Double getTangentialAppliedForce( Line2D.Double appliedForce ) {
        Vector2D.Double v = new Vector2D.Double( appliedForce.getP1(), getRotationPlatform().getCenter() );
        v.rotate( Math.PI / 2 );
        if ( v.dot( new Vector2D.Double( appliedForce.getP1(), appliedForce.getP2() ) ) < 0 ) {
            v.rotate( Math.PI );
        }

        AbstractVector2D x = v;
        if ( x.getMagnitude() == 0 ) {
            return new Line2D.Double( appliedForce.getP1(), appliedForce.getP1() );
        }
        double magnitude = new Vector2D.Double( appliedForce.getP1(), appliedForce.getP2() ).dot( x.getNormalizedInstance() );
        if ( magnitude != 0 ) {
            x = x.getInstanceOfMagnitude( magnitude );
        }
        else {
            x = new Vector2D.Double( 0, 0 );
        }
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
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).appliedForceChanged();
        }
    }
}
