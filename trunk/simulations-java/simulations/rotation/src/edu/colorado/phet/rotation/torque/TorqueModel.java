package edu.colorado.phet.rotation.torque;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
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
import edu.colorado.phet.rotation.util.RotationUtil;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:10:11 AM
 */
public class TorqueModel extends RotationModel {
    private DefaultTemporalVariable torque = new DefaultTemporalVariable();
    private DefaultTemporalVariable force = new DefaultTemporalVariable();//todo: sort out difference between force and appliedForceMagnitude
    private DefaultTemporalVariable brakeForceMagnitude = new DefaultTemporalVariable();
    private DefaultTemporalVariable momentOfInertia = new DefaultTemporalVariable();
    private DefaultTemporalVariable angularMomentum = new DefaultTemporalVariable();

    private DefaultTemporalVariable appliedForceMagnitude = new DefaultTemporalVariable();
    private AppliedForce appliedForce = new AppliedForce();
    private AppliedForce brakeForce = new AppliedForce();

    private UpdateStrategy forceDriven = new ForceDriven();
    private ArrayList listeners = new ArrayList();
    private boolean allowNonTangentialForces = false;
    private boolean showComponents = true;
    private boolean inited = false;
    private double brakePressure = 0;
    private ITemporalVariable netForce = new DefaultTemporalVariable();

    public TorqueModel( ConstantDtClock clock ) {
        super( clock );
        getRotationPlatform().setUpdateStrategy( forceDriven );
        inited = true;
        clear();
        appliedForce.setRadius( RotationPlatform.MAX_RADIUS );
        brakeForce.setRadius( RotationPlatform.MAX_RADIUS );
        getRotationPlatform().getVelocityVariable().addListener( new ITemporalVariable.ListenerAdapter() {
            public void valueChanged() {
                updateBrakeForce();
            }
        } );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        momentOfInertia.addValue( getRotationPlatform().getMomentOfInertia(), getTime() );
        angularMomentum.addValue( getRotationPlatform().getMomentOfInertia() * getRotationPlatform().getVelocity(), getTime() );
        defaultUpdate( torque );
        defaultUpdate( force );
        defaultUpdate( appliedForceMagnitude );
        defaultUpdate( netForce );
        defaultUpdate( brakeForceMagnitude );

        brakeForce.stepInTime( dt, getTime() );
        appliedForce.stepInTime( dt, getTime() );
        notifyAppliedForceChanged();//todo: only notify during actual change for performance & clarity
    }

    private void defaultUpdate( ITemporalVariable variable ) {
        variable.addValue( variable.getValue(), getTime() );
    }

    protected void setPlaybackTime( double time ) {
        super.setPlaybackTime( time );
        torque.setPlaybackTime( time );
        force.setPlaybackTime( time );
        angularMomentum.setPlaybackTime( time );
        momentOfInertia.setPlaybackTime( time );
        appliedForceMagnitude.setPlaybackTime( time );
        netForce.setPlaybackTime( time );
        brakeForceMagnitude.setPlaybackTime( time );

        appliedForce.setPlaybackTime( time );
        brakeForce.setPlaybackTime( time );
        notifyAppliedForceChanged();//todo: only notify during actual change for performance & clarity
    }

    public void clear() {
        super.clear();
        if ( inited ) {
            torque.clear();
            force.clear();
            angularMomentum.clear();
            momentOfInertia.clear();

            appliedForceMagnitude.clear();

            appliedForce.clear();
            brakeForce.clear();
            netForce.clear();
            brakeForceMagnitude.clear();
            brakePressure = 0;
        }
    }

    public ITemporalVariable getBrakeForceMagnitudeVariable() {
        return brakeForceMagnitude;
    }

    public double getBrakeForceMagnitude() {
        return brakeForce.getForceMagnitude();
    }

    public double getBrakePressure() {
        return brakePressure;
    }

    public void setBrakePressure( double brakePressure ) {
        if ( brakePressure != this.brakePressure ) {
            this.brakePressure = brakePressure;
            updateBrakeForce();
            for ( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener) listeners.get( i ) ).brakePressureChanged();
            }
        }
    }

    private Line2D.Double computeBrakeForce() {
        Point2D.Double src = new Point2D.Double( getRotationPlatform().getRadius() * Math.sqrt( 2 ) / 2, -getRotationPlatform().getRadius() * Math.sqrt( 2 ) / 2 );
        AbstractVector2D vec = Vector2D.Double.parseAngleAndMagnitude( brakePressure, Math.PI / 4 + ( getRotationPlatform().getVelocity() > 0 ? Math.PI : 0 ) );
        Point2D dst = vec.getDestination( src );
        if ( Math.abs( getRotationPlatform().getVelocity() ) < 1E-6 ) {
            dst = new Point2D.Double( src.getX(), src.getY() );
        }
        return new Line2D.Double( src, dst );
    }

    private void updateBrakeForce() {
        brakeForce.setValue( computeBrakeForce() );
        brakeForceMagnitude.setValue( brakeForce.getForceMagnitude() * -getVelocitySign() );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).brakeForceChanged();
        }
        updateNetForce();
    }

    private int getVelocitySign() {
        return MathUtil.getSign( getRotationPlatform().getVelocity() );
    }

    private void updateNetForce() {
        netForce.setValue( -getVelocitySign()*getBrakeForceMagnitude() + getAppliedForceMagnitude() );
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
        return appliedForce.getRadiusSeries();
    }

    public void setAppliedForceMagnitude( double appliedForceMag ) {
        appliedForceMagnitude.setValue( appliedForceMag );
        updateAppliedForceFromRF();
    }

    private void updateAppliedForceFromRF() {
        setAppliedForce( new Line2D.Double( getRotationPlatform().getCenter().getX(),
                                            getRotationPlatform().getCenter().getY() - getRadiusSeries().getValue(),
                                            getRotationPlatform().getCenter().getX() + appliedForceMagnitude.getValue(),
                                            getRotationPlatform().getCenter().getY() - getRadiusSeries().getValue() ) );
    }

    public void setAppliedForceRadius( double r ) {
        appliedForce.setRadius( r );
        updateAppliedForceFromRF();
    }

    public Line2D.Double getBrakeForceValue() {
        return brakeForce.toLine2D();
    }

    public AppliedForce getBrakeForceObject() {
        return brakeForce;
    }

    public AppliedForce getAppliedForceObject() {
        return appliedForce;
    }

    public ITemporalVariable getBrakeRadiusSeries() {
        return brakeForce.getRadiusSeries();
    }

    public ITemporalVariable getNetForce() {
        return netForce;
    }

    public class ForceDriven implements UpdateStrategy {
        public void update( MotionBody motionBody, double dt, double time ) {//todo: factor out duplicated code in AccelerationDriven
            //assume a constant acceleration model with the given acceleration.
            torque.setValue( force.getValue() * getRotationPlatform().getRadius() );
            double mu = 1.2;
            double brakeForceVal = mu * getBrakeForceMagnitude();
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
        return appliedForce.toLine2D();
    }

    public Line2D.Double getTangentialAppliedForce() {
        return getTangentialAppliedForce( getAppliedForce() );
    }

    /*
     * Computes the allowed portion of the desired applied force, result depends on whether allowNonTangentialForces is true
     */
    public void setAllowedAppliedForce( Line2D.Double appliedForce ) {
        setAppliedForce( getAllowedAppliedForce( appliedForce ) );
    }

    public void setAppliedForce( Line2D.Double appliedForce ) {
        if ( !RotationUtil.lineEquals( getAppliedForce(), appliedForce ) ) {
            this.appliedForce.setValue( appliedForce );

            //determine the new applied torque
            //torque=r x F
            Vector2D.Double r = new Vector2D.Double( getRotationPlatform().getCenter(), appliedForce.getP1() );
            Vector2D.Double f = new Vector2D.Double( appliedForce.getP1(), appliedForce.getP2() );
            double tau = -r.getCrossProductScalar( f );
            torque.setValue( tau );
            force.setValue( torque.getValue() / getRotationPlatform().getRadius() );

            updateNetForce();
            notifyAppliedForceChanged();
        }
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

    public static interface Listener {
        void appliedForceChanged();

        void showComponentsChanged();

        void brakeForceChanged();

        void brakePressureChanged();
    }

    public static class Adapter implements Listener {
        public void appliedForceChanged() {
        }

        public void showComponentsChanged() {
        }

        public void brakeForceChanged() {
        }

        public void brakePressureChanged() {
        }
    }

}
