package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.motion.MotionMath;
import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.rotation.tests.CircularRegression;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

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

    private ITimeSeries orientationSeries = new DefaultTimeSeries();

    private String imageName;
    private boolean constrained;
    private RotationPlatform rotationPlatform;//the platform this body is riding on, or null if not on a platform
    private edu.colorado.phet.common.motion.model.UpdateStrategy angleDriven;
    private double initialAngleOnPlatform;
    private boolean displayGraph = true;
    private CircularRegression.Circle circle;


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
    }

    public void setOffPlatform() {
        if( !isOffPlatform() ) {
            setUpdateStrategy( new OffPlatform() );
            notifyPlatformStateChanged();
        }
    }

    private void notifyPlatformStateChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).platformStateChanged();
        }
    }

    private void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy.detach();
        this.updateStrategy = updateStrategy;
    }

    public void setOnPlatform( RotationPlatform rotationPlatform ) {
        if( !isOnPlatform( rotationPlatform ) ) {
            setUpdateStrategy( new OnPlatform( rotationPlatform ) );
            this.rotationPlatform = rotationPlatform;
            //use the rotation platform to compute angle since it has the correct winding number
            this.initialAngleOnPlatform = getAngleOverPlatform() - rotationPlatform.getPosition();
            notifyPlatformStateChanged();
        }
    }

    //workaround for controlling the platform angle via the character angle
    public double getInitialAngleOnPlatform() {
        return initialAngleOnPlatform;
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
            updateOffPlatform( time, dt );
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

        orientationSeries.addValue( getOrientation(), time );


        notifyVectorsUpdated();
    }

    private CircularRegression circularRegression = new CircularRegression();

    private void updateOffPlatform( double time, double dt ) {
        AbstractVector2D origAccel = getAcceleration();
        xBody.getXTimeSeries().addValue( xBody.getPosition(), time );
        yBody.getXTimeSeries().addValue( yBody.getPosition(), time );

        int velocityWindow = 6;
        TimeData vx = MotionMath.getDerivative( xBody.getMotionBodySeries().getRecentPositionTimeSeries( Math.min( velocityWindow, xBody.getMotionBodySeries().getPositionSampleCount() ) ) );
        xBody.getMotionBodySeries().addVelocityData( vx.getValue(), vx.getTime() );

        TimeData vy = MotionMath.getDerivative( yBody.getMotionBodySeries().getRecentPositionTimeSeries( Math.min( velocityWindow, yBody.getMotionBodySeries().getPositionSampleCount() ) ) );
        yBody.getMotionBodySeries().addVelocityData( vy.getValue(), vy.getTime() );

        Point2D[] pointHistory = getPointHistory( 25 );
        circle = circularRegression.getCircle( pointHistory, 50, circle );
//        System.out.println( "circle = " + circle );
        AbstractVector2D accelVector = new Vector2D.Double( new Point2D.Double( xBody.getPosition(), yBody.getPosition() ),
                                                            circle.getCenter2D() );
        double aMag = 1.0;//todo: fix acceleration when circular regression is bogus
        if( circle.getRadius() > 0.1 ) {
            aMag = ( vx.getValue() * vx.getValue() + vy.getValue() * vy.getValue() ) / circle.getRadius();
        }

//        System.out.println( "aMag = " + aMag );
        if( accelVector.getMagnitude() < 0.1 ) {
            accelVector = new Vector2D.Double( 0.1, 0.1 );//todo: remove this dummy test value
        }
        accelVector = accelVector.getInstanceOfMagnitude( aMag );
        accelVector = origAccel.getAddedInstance( accelVector ).getScaledInstance( 0.5 );
        xBody.getMotionBodySeries().addAccelerationData( accelVector.getX(), time );
        yBody.getMotionBodySeries().addAccelerationData( accelVector.getY(), time );
        updateXYStateFromSeries();
    }

    public CircularRegression.Circle getCircle() {
        return circle;
    }

    public Point2D[] getPointHistory( int maxPts ) {
        ArrayList list = new ArrayList( maxPts );
        for( int i = 0; i < xBody.getXTimeSeries().getSampleCount() && i < maxPts; i++ ) {
            list.add( new Point2D.Double( xBody.getXTimeSeries().getRecentData( i ).getValue(), yBody.getXTimeSeries().getRecentData( i ).getValue() ) );
        }
        Collections.reverse( list );
        return (Point2D[])list.toArray( new Point2D.Double[0] );
    }

    private void updateOffPlatformORIG( double time, double dt ) {
        xBody.stepInTime( time, dt );
        yBody.stepInTime( time, dt );
    }

    public boolean isOnPlatform() {
        return updateStrategy instanceof OnPlatform;
    }

    private boolean isOnPlatform( RotationPlatform rotationPlatform ) {
        if( updateStrategy instanceof OnPlatform ) {
            OnPlatform onPlatform = (OnPlatform)updateStrategy;
            return onPlatform.rotationPlatform == rotationPlatform;
        }
        else {
            return false;
        }
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

    //when position changes, we have to update the current vectors as well to make sure they are tangential and centripetal
    //todo: this code duplicates much work done in updateBodyOnPlatform
    private void updateVectorsOnPlatform() {
        double omega = rotationPlatform.getVelocity();
        double r = getPosition().distance( rotationPlatform.getCenter() );

        Point2D newX = Vector2D.Double.parseAngleAndMagnitude( r, getAngleOverPlatform() ).getDestination( rotationPlatform.getCenter() );
        Vector2D.Double centripetalVector = new Vector2D.Double( newX, rotationPlatform.getCenter() );
        AbstractVector2D newV = centripetalVector.getInstanceOfMagnitude( r * omega ).getNormalVector();
        AbstractVector2D newA = centripetalVector.getInstanceOfMagnitude( r * omega * omega );

        xBody.getVVariable().setValue( newV.getX() );
        yBody.getVVariable().setValue( newV.getY() );

        xBody.getAVariable().setValue( newA.getX() );
        yBody.getAVariable().setValue( newA.getY() );
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
//        System.out.println( "rotationPlatform.getPosition() = " + rotationPlatform.getPosition() );
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
        setOrientation( orientationSeries.getValueForTime( time ) );
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

    public void setDisplayGraph( boolean selected ) {
        if( this.displayGraph != selected ) {
            this.displayGraph = selected;
            for( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener)listeners.get( i ) ).displayGraphChanged();
            }
        }
    }

    public boolean getDisplayGraph() {
        return displayGraph;
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
            updateVectorsOnPlatform();
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

    //todo: add notify
    public void setOrientation( double orientation ) {
        this.orientation = orientation;
        notifyOrientationChanged();
    }

    private void notifyOrientationChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener)listeners.get( i ) ).orientationChanged();
        }
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

        void platformStateChanged();

        void displayGraphChanged();

        void orientationChanged();
    }

    public static class Adapter implements Listener {

        public void positionChanged() {
        }

        public void speedAndAccelerationUpdated() {
        }

        public void platformStateChanged() {
        }

        public void displayGraphChanged() {
        }

        public void orientationChanged() {
        }

    }

    public void setPosition( double x, double y ) {
        if( this.getX() != x || this.getY() != y ) {
            xBody.setPosition( x );
            yBody.setPosition( y );
            notifyPositionChanged();
        }
    }
}
