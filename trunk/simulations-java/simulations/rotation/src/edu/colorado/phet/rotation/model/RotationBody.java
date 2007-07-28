package edu.colorado.phet.rotation.model;

import JSci.maths.LinearMath;
import JSci.maths.vectors.AbstractDoubleVector;
import edu.colorado.phet.common.motion.MotionMath;
import edu.colorado.phet.common.motion.model.*;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.rotation.tests.CircularRegression;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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

    private SeriesVariable speed;
    private SeriesVariable accel;
    private SeriesVariable angle;

    private double orientation = 0.0;
    private ITimeSeries orientationSeries = new DefaultTimeSeries();

    private String imageName;
    private boolean constrained;
    private RotationPlatform rotationPlatform;//the platform this body is riding on, or null if not on a platform
    private boolean displayGraph = true;
    private CircularRegression.Circle circle;
    private CircularRegression circularRegression = new CircularRegression();

    private ArrayList listeners = new ArrayList();
    private SeriesVariable angularVelocity;
    private SeriesVariable angularAccel;

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

        speed = new SeriesVariable();
        accel = new SeriesVariable();
        angle = new SeriesVariable();
        angularVelocity = new SeriesVariable();
        angularAccel = new SeriesVariable();
    }

    public void setOffPlatform() {
        if( !isOffPlatform() ) {
            setUpdateStrategy( new OffPlatform() );
            rotationPlatform = null;
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
            notifyPlatformStateChanged();
        }
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
//            updateOffPlatformORIG( time, dt );
        }
        else {
            updateBodyOnPlatform( time, dt );
        }
        if( !getPosition().equals( origPosition ) ) {//todo: integrate listener behavior into xBody and yBody?
            notifyPositionChanged();
        }
        speed.updateSeriesAndState( getVelocity().getMagnitude(), time );
        accel.updateSeriesAndState( getAcceleration().getMagnitude(), time );
        orientationSeries.addValue( getOrientation(), time );

//        debugSeries();
        notifyVectorsUpdated();
    }

    public void clear() {
        xBody.clear();
        yBody.clear();
        speed.clear();
        accel.setValue( 0.0 );
        accel.clear();
        angle.clear();
//        angleTimeSeries.clear();
        orientationSeries.clear();
    }

    private void updateOffPlatform( double time, double dt ) {
        AbstractVector2D origAccel = getAcceleration();
        xBody.getMotionBodySeries().addPositionData( xBody.getPosition(), time );
        yBody.getMotionBodySeries().addPositionData( yBody.getPosition(), time );

        int velocityWindow = 6;
        TimeData vx = MotionMath.getDerivative( xBody.getMotionBodySeries().getRecentPositionTimeSeries( Math.min( velocityWindow, xBody.getMotionBodySeries().getPositionSampleCount() ) ) );
        xBody.getMotionBodySeries().addVelocityData( vx.getValue(), vx.getTime() );

        TimeData vy = MotionMath.getDerivative( yBody.getMotionBodySeries().getRecentPositionTimeSeries( Math.min( velocityWindow, yBody.getMotionBodySeries().getPositionSampleCount() ) ) );
        yBody.getMotionBodySeries().addVelocityData( vy.getValue(), vy.getTime() );

        Point2D[] pointHistory = getPointHistory( 25 );
        Rectangle2D.Double boundingBox = new Rectangle2D.Double( pointHistory[0].getX(), pointHistory[0].getY(), 0, 0 );
        for( int i = 1; i < pointHistory.length; i++ ) {
            Point2D point2D = pointHistory[i];
            boundingBox.add( point2D );
        }
//        System.out.println( "boundingBox = " + boundingBox );
        //avoid the expense of circular regression if possible
        if( boundingBox.getWidth() <= 0.2 && boundingBox.getHeight() <= 0.2 ) {
            updateAccelByDerivative();
            updateXYStateFromSeries();
            return;
        }
        circle = circularRegression.getCircle( pointHistory, 50, circle );
        if( circle.getRadius() > 5.0 ) {
            //assume something went wrong in nonlinear regression
            circle = circularRegression.getCircle( pointHistory, 50, null );
        }
        double circularMotionMSE = 0.01;
        double noncircularMotionMSE = 0.15;
        double thresholdMSE = ( noncircularMotionMSE + circularMotionMSE ) / 2.0;

        double linearRegressionMSE = getLinearRegressionMSE( pointHistory );
//        System.out.println( "linearRegressionMSE = " + linearRegressionMSE );
//        System.out.println( "MSE=" + circle.getMeanSquaredError( pointHistory ) + ", circle.getRadius() = " + circle.getRadius() );
        if( circle.getRadius() >= 0.5 && circle.getRadius() <= 5.0 && circle.getMeanSquaredError( pointHistory ) < thresholdMSE && linearRegressionMSE > 0.01 ) {
            AbstractVector2D accelVector = new Vector2D.Double( new Point2D.Double( xBody.getPosition(), yBody.getPosition() ),
                                                                circle.getCenter2D() );
            double aMag = ( vx.getValue() * vx.getValue() + vy.getValue() * vy.getValue() ) / circle.getRadius();


            if( accelVector.getMagnitude() < 0.1 ) {
                accelVector = new Vector2D.Double( 0.1, 0.1 );//todo: remove this dummy test value
            }
            accelVector = accelVector.getInstanceOfMagnitude( aMag );
            accelVector = origAccel.getAddedInstance( accelVector ).getScaledInstance( 0.5 );
            xBody.getMotionBodySeries().addAccelerationData( accelVector.getX(), time );
            yBody.getMotionBodySeries().addAccelerationData( accelVector.getY(), time );
        }
        else {
//            System.out.println( "RotationBody.updateOffPlatform: Linear" );
            updateAccelByDerivative();
        }

        updateXYStateFromSeries();
        angle.updateSeriesAndState( getUserSetAngle(), time );
        TimeData v = MotionMath.getDerivative( MotionMath.smooth( angle.getRecentSeries( Math.min( velocityWindow, angle.getSampleCount() ) ), 4 ) );
        angularVelocity.updateSeriesAndState( v.getValue(), v.getTime() );//when on the platform, angul
        TimeData a = MotionMath.getDerivative( MotionMath.smooth( angularVelocity.getRecentSeries( Math.min( velocityWindow, angularVelocity.getSampleCount() ) ), 4 ) );
        angularAccel.updateSeriesAndState( a.getValue(), a.getTime() );
    }

    private double getUserSetAngle() {
        return getLastAngle() + getDTheta();
    }

    private double getLastAngle() {
        return angle.getSampleCount() > 0 ? angle.getLastValue() : getAngleNoWindingNumber();
    }

    private double getDTheta() {
        double ang = getAngleNoWindingNumber();
        double lastAngle = getLastAngle();
        //ang * N should be close to lastAngle, unless the body crossed the origin
        AbstractVector2D vec = Vector2D.Double.parseAngleAndMagnitude( 1.0, lastAngle );
        lastAngle = vec.getAngle();

        double dt = ang - lastAngle;
        if( dt > Math.PI ) {
            dt = dt - Math.PI * 2;
        }
        else if( dt < -Math.PI ) {
            dt = dt + Math.PI * 2;
        }

        return dt;
    }

    private double getAngleNoWindingNumber() {
        return new Vector2D.Double( getX(), getY() ).getAngle();
    }

    private void updateAccelByDerivative() {
        int accelWindow = 6;
        TimeData ax = MotionMath.getDerivative( xBody.getMotionBodySeries().getRecentVelocityTimeSeries( Math.min( accelWindow, xBody.getMotionBodySeries().getVelocitySampleCount() ) ) );
        xBody.getMotionBodySeries().addAccelerationData( ax.getValue(), ax.getTime() );

        TimeData ay = MotionMath.getDerivative( yBody.getMotionBodySeries().getRecentVelocityTimeSeries( Math.min( accelWindow, yBody.getMotionBodySeries().getVelocitySampleCount() ) ) );
        yBody.getMotionBodySeries().addAccelerationData( ay.getValue(), ay.getTime() );
    }


    private double getLinearRegressionMSE( Point2D[] pointHistory ) {
        double[][] pts = new double[2][pointHistory.length];
//        pts[0]=new double[pointHistory.length];
//        pts[1]=new double[pointHistory.length];
        for( int i = 0; i < pts[0].length; i++ ) {

            pts[0][i] = pointHistory[i].getX();
            pts[1][i] = pointHistory[i].getY();
        }
        AbstractDoubleVector result = LinearMath.linearRegression( pts );
        double offset = result.getComponent( 0 );
        double slope = result.getComponent( 1 );
//        System.out.println( "slope = " + slope + ", offset=" + offset );

        double sumSq = 0;
        for( int i = 0; i < pointHistory.length; i++ ) {
            Point2D point2D = pointHistory[i];
            double proposedY = offset + slope * point2D.getX();
            double actualY = point2D.getY();
            sumSq += ( proposedY - actualY ) * ( proposedY - actualY );
        }
        return sumSq / pointHistory.length;
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

        updateXYStateFromSeries();
        angle.updateSeriesAndState( getUserSetAngle(), time );
        angularVelocity.updateSeriesAndState( rotationPlatform.getVelocity(), time );//when on the platform, angul
        angularAccel.updateSeriesAndState( rotationPlatform.getAcceleration(), time );
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
        return speed.getVariable();
    }

    public ITimeSeries getSpeedSeries() {
        return speed.getSeries();
    }

    public ISimulationVariable getAccelMagnitudeVariable() {
        return accel.getVariable();
    }

    public ITimeSeries getAccelMagnitudeSeries() {
        return accel.getSeries();
    }

    public String getImageName() {
        return imageName;
    }

    public void setTime( double time ) {
        xBody.setTime( time );
        yBody.setTime( time );
        accel.setValueForTime( time );
        speed.setValueForTime( time );

        setOrientation( orientationSeries.getValueForTime( time ) );
        if( angle.getSampleCount() > 0 ) {
            angle.setValueForTime( time );
        }

        notifyVectorsUpdated();
        notifyPositionChanged();
    }

    public boolean isConstrained() {
        return constrained;
    }

    public ISimulationVariable getAngleVariable() {
        return angle.getVariable();
    }

    public ITimeSeries getAngleTimeSeries() {
        return angle.getSeries();
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

    public ISimulationVariable getAngularVelocityVariable() {
        return angularVelocity.getVariable();
    }

    public ITimeSeries getAngularVelocityTimeSeries() {
        return angularVelocity.getSeries();
    }

    public ISimulationVariable getAngularAccelerationVariable() {
        return angularAccel.getVariable();
    }

    public ITimeSeries getAngularAccelerationTimeSeries() {
        return angularAccel.getSeries();
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
