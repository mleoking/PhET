package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.MotionMath;
import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Author: Sam Reid
 * May 11, 2007, 12:12:12 AM
 */
public class RotationBody {
    private MotionBody xBody;
    private MotionBody yBody;
    private UpdateStrategy updateStrategy = new OffPlatform();
    private double orientation = 0.0;

    private ArrayList listeners = new ArrayList();
    private ISimulationVariable speedVariable;
    private ITimeSeries speedSeries;

    private ISimulationVariable accelMagnitudeVariable;
    private ITimeSeries accelMagnitudeSeries;

    private ISimulationVariable angleVariable = new DefaultSimulationVariable();
    private ITimeSeries angleTimeSeries = new DefaultTimeSeries();

    private String imageName;
    private boolean constrained;
    private RotationPlatform rotationPlatform;//the platform this body is riding on, or null if not on a platform
    private edu.colorado.phet.common.motion.model.UpdateStrategy angleDriven;
    private double initialAngleOnPlatform;

    public RotationBody() {
        this( "ladybug.gif" );
    }

    public RotationBody( String imageName ) {
        this( imageName, false );
    }

    public RotationBody( String imageName, boolean constrained ) {
        this.imageName = imageName;
        this.constrained = constrained;
        xBody = new MotionBody();
        yBody = new MotionBody();

        speedVariable = new DefaultSimulationVariable();
        speedSeries = new DefaultTimeSeries();

        accelMagnitudeVariable = new DefaultSimulationVariable();
        accelMagnitudeSeries = new DefaultTimeSeries();

        angleVariable = new DefaultSimulationVariable();
        angleTimeSeries = new DefaultTimeSeries();

        angleDriven = new AngleDriven();
    }

    private class AngleDriven implements edu.colorado.phet.common.motion.model.UpdateStrategy {

        public void update( MotionBodySeries model, double dt, MotionBodyState state, double time ) {
            int velocityWindow = 6;
            int accelerationWindow = 6;
            TimeData v = MotionMath.getDerivative( model.getRecentPositionTimeSeries( Math.min( velocityWindow, model.getVelocitySampleCount() ) ) );
            TimeData a = MotionMath.getDerivative( model.getRecentVelocityTimeSeries( Math.min( accelerationWindow, model.getAccelerationSampleCount() ) ) );

            model.addPositionData( state.getPosition(), time );
            model.addVelocityData( v.getValue(), v.getTime() );
            model.addAccelerationData( a.getValue(), a.getTime() );
        }
    }

    public void setOffPlatform() {
        setUpdateStrategy( new OffPlatform() );
    }

    private void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy.detach();
        this.updateStrategy = updateStrategy;
    }

    public void setOnPlatform( RotationPlatform rotationPlatform ) {
        setUpdateStrategy( new OnPlatform( rotationPlatform ) );
        this.rotationPlatform = rotationPlatform;
        //use the rotation platform to compute angle since it has the correct winding number
        this.initialAngleOnPlatform = getAngleOverPlatform() - rotationPlatform.getPosition();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void translate( double dx, double dy ) {
        setPosition( getPosition().getX() + dx, getPosition().getY() + dy );
    }

    public double getOrientation() {
        return orientation;
    }

    public double getX() {
        return xBody.getPosition();
    }

    public double getY() {
        return yBody.getPosition();
    }

    public double getAngleOverPlatform() {
        return new Vector2D.Double( rotationPlatform.getCenter(), getPosition() ).getAngle();
    }

    public void stepInTime( double time, double dt ) {
        Point2D origPosition = getPosition();
        if( isOffPlatform() ) {
            xBody.stepInTime( time, dt );
            yBody.stepInTime( time, dt );
        }
        else {
            updateBodyOnPlatform( time, dt );
        }
        if( !getPosition().equals( origPosition ) ) {//todo: integrate listener behavior into xBody and yBody?
            notifyPositionChanged();
        }
        speedVariable.setValue( getVelocity().getMagnitude() );
        speedSeries.addValue( getVelocity().getMagnitude(), time );

        accelMagnitudeVariable.setValue( getAcceleration().getMagnitude() );
        accelMagnitudeSeries.addValue( getAcceleration().getMagnitude(), time );


        notifyVectorsUpdated();
    }

    private boolean isOnPlatform() {
        return !isOffPlatform();
    }

    private boolean isOffPlatform() {
        return updateStrategy instanceof OffPlatform;
    }

    private void notifyVectorsUpdated() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.speedAndAccelerationUpdated();
        }
    }

    private void updateBodyOnPlatform( double time, double dt ) {
        double omega = rotationPlatform.getVelocity();
        double r = getPosition().distance( rotationPlatform.getCenter() );

        Point2D newX = Vector2D.Double.parseAngleAndMagnitude( r, getAngleOverPlatform() ).getDestination( rotationPlatform.getCenter() );
        Vector2D.Double centripetalVector = new Vector2D.Double( newX, rotationPlatform.getCenter() );
        AbstractVector2D newV = centripetalVector.getInstanceOfMagnitude( r * omega ).getNormalVector();
        AbstractVector2D newA = centripetalVector.getInstanceOfMagnitude( r * omega * omega );

        addPositionData( newX, time );
        addVelocityData( newV, time );
        addAccelerationData( newA, time );

        //use the rotation platform to compute angle since it has the correct winding number
        angleVariable.setValue( rotationPlatform.getPosition() + initialAngleOnPlatform );
        angleTimeSeries.addValue( rotationPlatform.getPosition() + initialAngleOnPlatform, time );

        updateXYStateFromSeries();

        checkCentripetalAccel();
    }

    public void checkCentripetalAccel() {
        if( rotationPlatform == null ) {
            return;
        }
        Vector2D.Double cv = new Vector2D.Double( getPosition(), rotationPlatform.getCenter() );
        Vector2D av = getAcceleration();
        //these should be colinear
        double angle = cv.getAngle() - av.getAngle();

        if( Math.abs( angle ) > 1E-2 & av.getMagnitude() > 1E-9 ) {
//            System.out.println( "RotationBody.updateBodyOnPlatform, angle="+angle );
        }
    }

    private void updateXYStateFromSeries() {
        xBody.updateStateFromSeries();
        yBody.updateStateFromSeries();
    }

    private void addAccelerationData( AbstractVector2D newAccel, double time ) {
        xBody.getMotionBodySeries().addAccelerationData( newAccel.getX(), time );
        yBody.getMotionBodySeries().addAccelerationData( newAccel.getY(), time );
    }

    private void addVelocityData( AbstractVector2D newVelocity, double time ) {
        xBody.getMotionBodySeries().addVelocityData( newVelocity.getX(), time );
        yBody.getMotionBodySeries().addVelocityData( newVelocity.getY(), time );
    }

    private void addPositionData( Point2D newLocation, double time ) {
        xBody.getMotionBodySeries().addPositionData( newLocation.getX(), time );
        yBody.getMotionBodySeries().addPositionData( newLocation.getY(), time );
    }

    public Vector2D getAcceleration() {
        return new Vector2D.Double( xBody.getAcceleration(), yBody.getAcceleration() );
    }

    public Vector2D getVelocity() {
        return new Vector2D.Double( xBody.getVelocity(), yBody.getVelocity() );
    }

    public ISimulationVariable getXPositionVariable() {
        return xBody.getXVariable();
    }

    public ISimulationVariable getXVelocityVariable() {
        return xBody.getVVariable();
    }

    public ITimeSeries getXVelocityTimeSeries() {
        return xBody.getVTimeSeries();
    }

    public ISimulationVariable getYVelocityVariable() {
        return yBody.getVVariable();
    }

    public ITimeSeries getYVelocityTimeSeries() {
        return yBody.getVTimeSeries();
    }

    public ITimeSeries getXPositionTimeSeries() {
        return xBody.getXTimeSeries();
    }

    public ISimulationVariable getYPositionVariable() {
        return yBody.getXVariable();
    }

    public ITimeSeries getYPositionTimeSeries() {
        return yBody.getXTimeSeries();
    }

    public ISimulationVariable getXAccelVariable() {
        return xBody.getAVariable();
    }

    public ITimeSeries getXAccelTimeSeries() {
        return xBody.getATimeSeries();
    }

    public ISimulationVariable getYAccelVariable() {
        return yBody.getAVariable();
    }

    public ITimeSeries getYAccelTimeSeries() {
        return yBody.getATimeSeries();
    }

    public ISimulationVariable getSpeedVariable() {
        return speedVariable;
    }

    public ITimeSeries getSpeedSeries() {
        return speedSeries;
    }

    public ISimulationVariable getAccelMagnitudeVariable() {
        return accelMagnitudeVariable;
    }

    public ITimeSeries getAccelMagnitudeSeries() {
        return accelMagnitudeSeries;
    }

    public String getImageName() {
        return imageName;
    }

    public void setTime( double time ) {
        xBody.setTime( time );
        yBody.setTime( time );
        accelMagnitudeVariable.setValue( accelMagnitudeSeries.getValueForTime( time ) );
        speedVariable.setValue( speedSeries.getValueForTime( time ) );
        notifyVectorsUpdated();
        notifyPositionChanged();
    }

    public boolean isConstrained() {
        return constrained;
    }

    public ISimulationVariable getAngleVariable() {
        return angleVariable;
    }

    public ITimeSeries getAngleTimeSeries() {
        return angleTimeSeries;
    }

    public edu.colorado.phet.common.motion.model.UpdateStrategy getAngleDriven() {
        return angleDriven;
    }

    private static abstract class UpdateStrategy implements Serializable {
        public abstract void detach();
    }

    private static Point2D rotate( Point2D pt, Point2D center, double angle ) {
        Vector2D.Double v = new Vector2D.Double( center, pt );
        v.rotate( angle );
        return v.getDestination( center );
    }

    private static Line2D rotate( Line2D line, Point2D center, double angle ) {
        return new Line2D.Double( rotate( line.getP1(), center, angle ), rotate( line.getP2(), center, angle ) );
    }

    private class OffPlatform extends UpdateStrategy {

        public void detach() {
        }
    }

    private class OnPlatform extends UpdateStrategy implements MotionBodyState.Listener {
        private RotationPlatform rotationPlatform;

        public OnPlatform( RotationPlatform rotationPlatform ) {
            this.rotationPlatform = rotationPlatform;
            rotationPlatform.getMotionBodyState().addListener( this );
        }

        public void positionChanged( double dtheta ) {
            Line2D segment = new Line2D.Double( getPosition(), Vector2D.Double.parseAngleAndMagnitude( 0.01, getOrientation() ).getDestination( getPosition() ) );
            setPosition( rotate( getPosition(), rotationPlatform.getCenter(), dtheta ) );
            Line2D rot = rotate( segment, rotationPlatform.getCenter(), dtheta );

            setOrientation( new Vector2D.Double( rot.getP1(), rot.getP2() ).getAngle() );
            notifyPositionChanged();
        }

        public void velocityChanged() {
        }

        public void accelerationChanged() {
        }

        public void detach() {
            rotationPlatform.getMotionBodyState().removeListener( this );
        }
    }

    private void setPosition( Point2D point2D ) {
        setPosition( point2D.getX(), point2D.getY() );
    }

    private void setOrientation( double orientation ) {
        this.orientation = orientation;
    }

    private void notifyPositionChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).positionChanged();
        }
    }

    public Point2D getPosition() {
        return new Point2D.Double( getX(), getY() );
    }

    public static interface Listener {
        void positionChanged();

        void speedAndAccelerationUpdated();
    }

    public void setPosition( double x, double y ) {
        if( this.getX() != x || this.getY() != y ) {
            xBody.setPosition( x );
            yBody.setPosition( y );
            notifyPositionChanged();
        }
    }
}
