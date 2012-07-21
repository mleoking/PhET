// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.rotation.torque;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.IMotionBody;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.math.vector.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.model.RotationTemporalVariable;
import edu.colorado.phet.rotation.util.RotationUtil;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:10:11 AM
 */
public class TorqueModel extends RotationModel {
    //independent values
    private AppliedForce appliedForceObject;
    private AppliedForce brakeForceObject;
    private DefaultTemporalVariable brakePressure = new RotationTemporalVariable();

    //dependent values
    private DefaultTemporalVariable netForce = new RotationTemporalVariable();
    private DefaultTemporalVariable netTorque = new RotationTemporalVariable();
    private DefaultTemporalVariable momentOfInertia = new RotationTemporalVariable();
    private DefaultTemporalVariable angularMomentum = new RotationTemporalVariable();

    private UpdateStrategy forceDriven = new ForceDriven();
    private ArrayList listeners = new ArrayList();

    private boolean allowNonTangentialForces = DEFAULT_ALLOW_NONTANGENTIAL_FORCES;
    private boolean showComponents = DEFAULT_SHOW_COMPONENTS;
    private boolean inited = false;

    private boolean overwhelmingBrake = false;

    private static boolean DEFAULT_ALLOW_NONTANGENTIAL_FORCES = false;
    private static boolean DEFAULT_SHOW_COMPONENTS = true;

    public TorqueModel( ConstantDtClock clock ) {
        super( clock );
        appliedForceObject = new AppliedForce( getRotationPlatform() );
        brakeForceObject = new AppliedForce( getRotationPlatform() );
        getRotationPlatform().setUpdateStrategy( forceDriven );
        inited = true;
        clear();
        getRotationPlatform().getVelocityVariable().addListener( new ITemporalVariable.ListenerAdapter() {
            public void valueChanged() {
                updateBrakeForce();
            }
        } );
        getRotationPlatform().addListener( new RotationPlatform.Adapter() {
            public void radiusChanged() {
                if ( appliedForceObject.getRadius() > getRotationPlatform().getRadius() ) {
                    setAppliedForceFromRadius( getRotationPlatform().getRadius() );
                }
            }

            public void innerRadiusChanged() {
                if ( appliedForceObject.getRadius() < getRotationPlatform().getInnerRadius() ) {
                    setAppliedForceFromRadius( getRotationPlatform().getInnerRadius() );
                }
            }
        } );
        resetAll();//since this subclass couldn't init from parent call
    }


    public void stepInTime( double dt ) {
        super.stepInTime( dt );
        momentOfInertia.addValue( getRotationPlatform().getMomentOfInertia(), getTime() );
        angularMomentum.addValue( getRotationPlatform().getMomentOfInertia() * getRotationPlatform().getVelocity(), getTime() );
        defaultUpdate( brakePressure );
        defaultUpdate( netForce );
        defaultUpdate( netTorque );

        brakeForceObject.stepInTime( dt, getTime() );
        appliedForceObject.stepInTime( dt, getTime() );
        notifyAppliedForceChanged();//todo: only notify during actual change for performance & clarity
    }

    public void resetAll() {
        super.resetAll();
        if ( inited ) {
            setAppliedForceRadius( getRotationPlatform().getRadius() );
            updateBrakeForce();
            updateNetForce();
            setAllowNonTangentialForces( DEFAULT_ALLOW_NONTANGENTIAL_FORCES );
            setShowComponents( DEFAULT_SHOW_COMPONENTS );
            setBrakePressure( 0.0 );
        }
    }

    protected void setPlaybackTime( double time ) {
        super.setPlaybackTime( time );
        angularMomentum.setPlaybackTime( time );
        momentOfInertia.setPlaybackTime( time );
        netForce.setPlaybackTime( time );
        double origBP = brakePressure.getValue();
        brakePressure.setPlaybackTime( time );
        if ( brakePressure.getValue() != origBP ) {
            notifyBrakePressureChanged();
        }

        netTorque.setPlaybackTime( time );

        appliedForceObject.setPlaybackTime( time );
        brakeForceObject.setPlaybackTime( time );
        notifyAppliedForceChanged();//todo: only notify during actual change for performance & clarity
    }

    public void clear() {
        super.clear();
        if ( inited ) {
            angularMomentum.clear();
            momentOfInertia.clear();
            appliedForceObject.clear();
            brakeForceObject.clear();
            netForce.clear();
            netTorque.clear();
            brakePressure.clear();
        }
    }

    public ITemporalVariable getBrakeForceMagnitudeVariable() {
        return brakeForceObject.getSignedForceSeries();
    }

    public double getBrakeForceMagnitude() {
        return brakeForceObject.getForceMagnitude();
    }

    public double getBrakePressure() {
        return brakePressure.getValue();
    }

    public void setBrakePressure( double brakePressure ) {
        if ( brakePressure != this.brakePressure.getValue() ) {
            this.brakePressure.setValue( brakePressure );
            updateBrakeForce();
            notifyBrakePressureChanged();
        }
    }

    private void notifyBrakePressureChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).brakePressureChanged();
        }
    }

    private Line2D.Double computeBrakeForce() {
        Point2D.Double src = new Point2D.Double( getRotationPlatform().getRadius() * Math.sqrt( 2 ) / 2, -getRotationPlatform().getRadius() * Math.sqrt( 2 ) / 2 );
        Vector2D vec = getBrakeForceVector();
        if ( vec == null ) {
            return new Line2D.Double( src, src );
        }
        Point2D dst = vec.getDestination( src );

        return new Line2D.Double( src, dst );
    }

    private Vector2D getBrakeForceVector() {
        boolean clockwise = getRotationPlatform().getVelocity() > 0;
        if ( getRotationPlatform().getVelocity() == 0 ) {
            if ( Math.abs( appliedForceObject.getTorque() ) == 0 ) {
                return null;
            }
            clockwise = appliedForceObject.getTorque() > 0;
        }
        double magnitude = brakePressure.getValue();
        double requestedBrakeTorqueMagnitude = Math.abs( brakePressure.getValue() * getRotationPlatform().getRadius() );
        double appliedTorqueMagnitude = Math.abs( appliedForceObject.getTorque( getPlatformCenter() ) );
        //todo: remove need for magic number
        double VELOCITY_THRESHOLD = 1;
        if ( requestedBrakeTorqueMagnitude > appliedTorqueMagnitude && Math.abs( getRotationPlatform().getVelocity() ) < VELOCITY_THRESHOLD ) {
            magnitude = appliedTorqueMagnitude / getRotationPlatform().getRadius();
            overwhelmingBrake = true;
        }
        else {
            overwhelmingBrake = false;
        }
        return MutableVector2D.createPolar( magnitude, Math.PI / 4 + ( clockwise ? Math.PI : 0 ) );
    }

    private void updateBrakeForce() {
        brakeForceObject.setValue( computeBrakeForce() );

        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).brakeForceChanged();
        }
        updateNetForce();
    }

    private void updateNetForce() {
        netForce.setValue( getSignedNetForceValue() );
    }

    private double getSignedNetForceValue() {
        return getAppliedForceObject().getSignedForce( getRotationPlatform().getCenter() ) +
               getBrakeForceObject().getSignedForce( getRotationPlatform().getCenter() );
    }

    public ITemporalVariable getAppliedTorqueTimeSeries() {
        return appliedForceObject.getTorqueSeries();
    }

    public ITemporalVariable getAppliedForceVariable() {
        return appliedForceObject.getSignedForceSeries();
    }

    public ITemporalVariable getNetForce() {
        return netForce;
    }

    public ITemporalVariable getNetTorque() {
        return netTorque;
    }

    public ITemporalVariable getBrakeTorque() {
        return brakeForceObject.getTorqueSeries();
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
        return appliedForceObject.getForceMagnitude();
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
        return appliedForceObject.getRadiusSeries();
    }

    public void setAppliedForce( double r, double magnitude ) {
        setAppliedForce( new Line2D.Double( getRotationPlatform().getCenter().getX(),
                                            getRotationPlatform().getCenter().getY() - r,
                                            getRotationPlatform().getCenter().getX() + magnitude,
                                            getRotationPlatform().getCenter().getY() - r ) );
    }

    public void setAppliedForceFromRadius( double radius ) {
        if ( radius == 0 ) {
            radius = 1E-6;//workaround for not representing force as magnitude + direction and application point
        }
        setAppliedForce( radius, appliedForceObject.getSignedForce( getPlatformCenter() ) );
    }

    public void setAppliedForceRadius( double r ) {
        setAppliedForceFromRadius( r );
    }

    public Line2D.Double getBrakeForceValue() {
        return brakeForceObject.toLine2D();
    }

    public AppliedForce getBrakeForceObject() {
        return brakeForceObject;
    }

    public AppliedForce getAppliedForceObject() {
        return appliedForceObject;
    }

    public ITemporalVariable getBrakeRadiusSeries() {
        return brakeForceObject.getRadiusSeries();
    }

    public double getAppliedForceRadius() {
        return appliedForceObject.getRadius();
    }

    public class ForceDriven implements UpdateStrategy {
        public void update( IMotionBody motionBody, double dt, double time ) {//todo: factor out duplicated code in AccelerationDriven
            //assume a constant acceleration model with the given acceleration.
            double origAngVel = motionBody.getVelocity();
//            System.out.println( "net torque value=" + ( appliedTorque.getValue() + brakeTorque.getValue() ) + ", applied=" + appliedTorque.getValue() + ", brake=" + brakeTorque.getValue() );
            TorqueModel.this.netTorque.setValue( appliedForceObject.getTorque() + brakeForceObject.getTorque() );//todo: should probably update even while paused

            //todo: better handling for zero moment?
            double acceleration = getMomentOfInertia() > 0 ? netTorque.getValue() / getMomentOfInertia() : 0;

            //if brake overwhelms applied force, do not change direction
            double proposedVelocity = motionBody.getVelocity() + acceleration * dt;
            updateBrakeForce();//todo: this is a workaround to make sure overwhelming brake is correctly computed
            if ( overwhelmingBrake ) {
                proposedVelocity = 0.0;
                acceleration = 0.0;
            }
            motionBody.addAccelerationData( acceleration, time );
            motionBody.addVelocityData( proposedVelocity, time );

            //if the friction causes the velocity to change sign, set the velocity to zero?
            motionBody.addPositionData( motionBody.getPosition() + proposedVelocity * dt, time );
        }


        private double getMomentOfInertia() {
            return getRotationPlatform().getMomentOfInertia();
        }
    }

    private Point2D getPlatformCenter() {
        return getRotationPlatform().getCenter();
    }

    public Line2D.Double getAppliedForce() {
        return appliedForceObject.toLine2D();
    }

    public Line2D.Double getTangentialAppliedForce() {
        return getTangentialAppliedForce( getAppliedForce() );
    }

    /*
     * Computes the allowed portion of the desired applied appliedForce, result depends on whether allowNonTangentialForces is true
     */
    public void setAllowedAppliedForce( Line2D.Double appliedForce ) {
        setAppliedForce( getAllowedAppliedForce( appliedForce ) );
    }

    public void setAppliedForce( Line2D.Double appliedForce ) {
        if ( !RotationUtil.lineEquals( getAppliedForce(), appliedForce ) ) {
            appliedForceObject.setValue( appliedForce );


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
        MutableVector2D v = new MutableVector2D( appliedForce.getP1(), getRotationPlatform().getCenter() );
        v.rotate( Math.PI / 2 );
        if ( v.dot( new MutableVector2D( appliedForce.getP1(), appliedForce.getP2() ) ) < 0 ) {
            v.rotate( Math.PI );
        }

        AbstractVector2D x = v;
        if ( x.getMagnitude() == 0 ) {
            return new Line2D.Double( appliedForce.getP1(), appliedForce.getP1() );
        }
        double magnitude = new MutableVector2D( appliedForce.getP1(), appliedForce.getP2() ).dot( x.getNormalizedInstance() );
        if ( magnitude != 0 ) {
            x = x.getInstanceOfMagnitude( magnitude );
        }
        else {
            x = new MutableVector2D( 0, 0 );
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
