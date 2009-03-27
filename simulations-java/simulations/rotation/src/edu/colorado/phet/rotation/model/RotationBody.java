package edu.colorado.phet.rotation.model;

import JSci.maths.LinearMath;
import JSci.maths.vectors.AbstractDoubleVector;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.colorado.phet.common.motion.MotionMath;
import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.motion.model.MotionBody;
import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.rotation.RotationResources;
import edu.colorado.phet.rotation.tests.CircularRegression;

/**
 * Author: Sam Reid
 * May 11, 2007, 12:12:12 AM
 */
public class RotationBody {
    private MotionBody xBody;
    private MotionBody yBody;
    private UpdateStrategy updateStrategy = new OffPlatform();

    private ITemporalVariable speed;
    private ITemporalVariable accelMagnitude;
    private ITemporalVariable angle;
    private ITemporalVariable angularVelocity;
    private ITemporalVariable angularAccel;
    private ITemporalVariable orientation;

    private String imageName;
    private boolean constrained;
    private boolean debug;
    private RotationPlatform rotationPlatform;//the platform this body is riding on, or null if not on a platform
    private boolean displayGraph = true;
    private CircularRegression.Circle circle;
    private CircularRegression circularRegression = new CircularRegression();
    private CircleDiscriminant circleDiscriminant = new DefaultCircleDiscriminant();

    private double lastNonZeroRadiusAngle = 0.0;//to remember the last angle the bug was at (in case the user shrinks the disk to radius zero)

    private ArrayList listeners = new ArrayList();

    private RotationPlatform.Listener listener;

    //angle of the bug relative to the platform, used when updating position on the platform 
    private double relAngleOnPlatform = 0.0;
    private static final double FLY_OFF_SPEED_THRESHOLD = 21.0 * 3;

    /**
     * @noinspection HardCodedStringLiteral
     */
    public RotationBody() {
        this( "ladybug.gif" );
    }

    public RotationBody( String imageName ) {
        this( imageName, false );
    }

    public RotationBody( String imageName, boolean constrained ) {
        this( imageName, constrained, false );
    }

    public RotationBody( String imageName, boolean constrained, boolean debug ) {
        this.imageName = imageName;
        this.constrained = constrained;
        this.debug = debug;
        listener = new RotationPlatform.Adapter() {
            public void innerRadiusChanged() {
                platformInnerRadiusChanged();
            }

            public void radiusChanged() {
                platformOuterRadiusChanged();
            }
        };
        xBody = new MotionBody( RotationModel.getTimeSeriesFactory() );
        yBody = new MotionBody( RotationModel.getTimeSeriesFactory() );

        speed = new RotationTemporalVariable();
        accelMagnitude = new RotationTemporalVariable();
        angle = new RotationTemporalVariable();
        angularVelocity = new RotationTemporalVariable();
        angularAccel = new RotationTemporalVariable();
        orientation = new RotationTemporalVariable();
    }

    public void clearVelocityAndAcceleration() {
        getVx().setValue( 0 );
        getVy().setValue( 0 );
        getAccelX().setValue( 0 );
        getAccelY().setValue( 0 );
        getAccelMagnitude().setValue( 0 );
    }

    public void clearWindingNumber() {
        angle.setValue( getAngleNoWindingNumber() );
    }

    public void clearAngularVelocity() {
        angularVelocity.setValue( 0.0 );
    }

    interface DoubleComparator {
        boolean compare( double a, double b );
    }

    interface DoubleNumber {
        double getValue();
    }

    private void platformDimensionChanged( DoubleComparator comparator, DoubleNumber f ) {
        if ( rotationPlatform != null && comparator.compare( getPosition().distance( rotationPlatform.getCenter() ), f.getValue() ) ) {
            if ( f.getValue() == 0 ) {
                setPosition( rotationPlatform.getCenter() );
            }
            else {
                AbstractVector2D vec = new Vector2D.Double( getX() - rotationPlatform.getCenter().getX(), getY() - rotationPlatform.getCenter().getY() );
                if ( vec.getMagnitudeSq() == 0 ) {
                    vec = Vector2D.Double.parseAngleAndMagnitude( 1.0, lastNonZeroRadiusAngle );
                }
                AbstractVector2D m = vec.getInstanceOfMagnitude( f.getValue() );
                setPosition( m.getX() + rotationPlatform.getCenter().getX(), m.getY() + rotationPlatform.getCenter().getY() );
            }
        }
    }

    private void platformInnerRadiusChanged() {
        platformDimensionChanged( new DoubleComparator() {
            public boolean compare( double a, double b ) {
                return a < b;
            }
        }, new DoubleNumber() {
            public double getValue() {
                return rotationPlatform.getInnerRadius();
            }
        } );
    }

    private void platformOuterRadiusChanged() {
        platformDimensionChanged( new DoubleComparator() {
            public boolean compare( double a, double b ) {
                return a > b;
            }
        }, new DoubleNumber() {
            public double getValue() {
                return rotationPlatform.getRadius();
            }
        } );
    }

    public void setOffPlatform() {
        if ( !isOffPlatform() ) {
            setUpdateStrategy( new OffPlatform() );
            rotationPlatform = null;
            notifyPlatformStateChanged();
        }
    }

    private void notifyPlatformStateChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).platformStateChanged();
        }
    }

    private void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy.detach();
        this.updateStrategy = updateStrategy;
    }

    public void setOnPlatform( RotationPlatform rotationPlatform ) {
        if ( this.rotationPlatform != null && rotationPlatform != this.rotationPlatform ) {
            this.rotationPlatform.removeListener( listener );
        }
        if ( !isOnPlatform( rotationPlatform ) ) {
            setUpdateStrategy( new OnPlatform( rotationPlatform ) );

            this.rotationPlatform = rotationPlatform;
            this.rotationPlatform.addListener( listener );
            this.relAngleOnPlatform = angle.getValue() - rotationPlatform.getPosition();
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
        return orientation.getValue();
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
        if ( debug ) {
            System.out.println( "Stepped: x=" + getPositionX() );
        }
        Point2D origPosition = getPosition();
        updateStrategy.stepInTime( time, dt );
        if ( !getPosition().equals( origPosition ) ) {//todo: integrate listener behavior into xBody and yBody?
            notifyPositionChanged();
        }
        speed.addValue( getVelocity().getMagnitude(), time );
        accelMagnitude.addValue( getAcceleration().getMagnitude(), time );
        orientation.addValue( getOrientation(), time );

//        debugSeries();
        notifyVectorsUpdated();
    }

    public void clear() {
        xBody.clear();
        yBody.clear();
        speed.clear();
        accelMagnitude.setValue( 0.0 );
        accelMagnitude.clear();
        angle.clear();
        orientation.clear();
        angularVelocity.clear();
        angularAccel.clear();
    }

    private void updateOffPlatform( double time ) {
        AbstractVector2D origAccel = getAcceleration();
        xBody.addPositionData( xBody.getPosition(), time );
        yBody.addPositionData( yBody.getPosition(), time );

        int velocityWindow = 6;
        TimeData vx = MotionMath.getDerivative( xBody.getRecentPositionTimeSeries( Math.min( velocityWindow, xBody.getPositionSampleCount() ) ) );
        xBody.addVelocityData( vx.getValue(), vx.getTime() );

        TimeData vy = MotionMath.getDerivative( yBody.getRecentPositionTimeSeries( Math.min( velocityWindow, yBody.getPositionSampleCount() ) ) );
        yBody.addVelocityData( vy.getValue(), vy.getTime() );

        Point2D[] pointHistory = getPointHistory( 25 );
        Rectangle2D.Double boundingBox = new Rectangle2D.Double( pointHistory[0].getX(), pointHistory[0].getY(), 0, 0 );
        for ( int i = 1; i < pointHistory.length; i++ ) {
            Point2D point2D = pointHistory[i];
            boundingBox.add( point2D );
        }
//        System.out.println( "boundingBox = " + boundingBox );
        //avoid the expense of circular regression if possible
        if ( boundingBox.getWidth() <= 0.2 && boundingBox.getHeight() <= 0.2 ) {
            updateAccelByDerivative();
        }
        else {
            circle = circularRegression.getCircle( pointHistory, 50, circle );
            if ( circle.getRadius() > 5.0 ) {
                //assume something went wrong in nonlinear regression
                circle = circularRegression.getCircle( pointHistory, 50, null );
            }
//        System.out.println( "linearRegressionMSE = " + linearRegressionMSE );
//        System.out.println( "MSE=" + circle.getMeanSquaredError( pointHistory ) + ", circle.getRadius() = " + circle.getRadius() );
            if ( circleDiscriminant.isCircularMotion( circle, pointHistory ) ) {
                AbstractVector2D accelVector = new Vector2D.Double( new Point2D.Double( xBody.getPosition(), yBody.getPosition() ),
                                                                    circle.getCenter2D() );
                double aMag = ( vx.getValue() * vx.getValue() + vy.getValue() * vy.getValue() ) / circle.getRadius();

                if ( accelVector.getMagnitude() < 0.1 ) {
                    accelVector = new Vector2D.Double( 0.1, 0.1 );//todo: remove this dummy test value
                }
                accelVector = accelVector.getInstanceOfMagnitude( aMag );
                accelVector = origAccel.getAddedInstance( accelVector ).getScaledInstance( 0.5 );
                xBody.addAccelerationData( accelVector.getX(), time );
                yBody.addAccelerationData( accelVector.getY(), time );
            }
            else {
//            System.out.println( "RotationBody.updateOffPlatform: Linear" );
                updateAccelByDerivative();
            }
        }

        angle.addValue( getUserSetAngle(), time );
        TimeData v = MotionMath.getDerivative( MotionMath.smooth( angle.getRecentSeries( Math.min( velocityWindow, angle.getSampleCount() ) ), 4 ) );
        angularVelocity.addValue( v.getValue(), v.getTime() );
        TimeData a = MotionMath.getDerivative( MotionMath.smooth( angularVelocity.getRecentSeries( Math.min( velocityWindow, angularVelocity.getSampleCount() ) ), 4 ) );
        angularAccel.addValue( a.getValue(), a.getTime() );
    }

    public static interface CircleDiscriminant {
        boolean isCircularMotion( CircularRegression.Circle circle, Point2D[] pointHistory );
    }

    public static class DefaultCircleDiscriminant implements CircleDiscriminant {
        double noncircularMotionMSE = 0.15;
        double thresholdCircularMSE = 1.0;

        public boolean isCircularMotion( CircularRegression.Circle circle, Point2D[] pointHistory ) {
            boolean radiusBigEnough = circle.getRadius() >= 0.5;
            boolean radiusSmallEnough = circle.getRadius() <= 5.0;
            double circleErr = circle.getMeanSquaredError( pointHistory );
            boolean circleErrorSmallEnough = circleErr < thresholdCircularMSE;
//            System.out.println( "RotationBody$DefaultCircleDiscriminant.isCircularMotion" );
            boolean lineErrorHighEnough = getLinearRegressionMSE( pointHistory ) > 0.01;
//            System.out.println( "radiusBigEnough= " + radiusBigEnough + ", radiusSmall= " + radiusSmallEnough + ", cErr= " + circleErrorSmallEnough + ", lErr= " + lineErrorHighEnough +", cErr="+circleErr);
            return radiusBigEnough &&
                   radiusSmallEnough &&
                   circleErrorSmallEnough &&
                   lineErrorHighEnough;
        }
    }

    private double getUserSetAngle() {
        return getLastAngle() + getDTheta();
    }

    private double getLastAngle() {
        //this implementation works even while the clock is paused and the user is manually dragging the RotationBody
        return angle.getValue();//todo: this implementation of getLastAngle only works if this value is not updated during computation
    }

    private double getDTheta() {
        double ang = getAngleNoWindingNumber();
        double lastAngle = getLastAngle();
        //ang * N should be close to lastAngle, unless the body crossed the origin
        AbstractVector2D vec = Vector2D.Double.parseAngleAndMagnitude( 1.0, lastAngle );
        lastAngle = vec.getAngle();

        double dt = ang - lastAngle;
        if ( dt > Math.PI ) {
            dt = dt - Math.PI * 2;
        }
        else if ( dt < -Math.PI ) {
            dt = dt + Math.PI * 2;
        }

        return dt;
    }

    private double getAngleNoWindingNumber() {
        return new Vector2D.Double( getX(), getY() ).getAngle();
    }

    private void updateAccelByDerivative() {
        int accelWindow = 6;
        TimeData ax = MotionMath.getDerivative( xBody.getRecentVelocityTimeSeries( Math.min( accelWindow, xBody.getVelocitySampleCount() ) ) );
        xBody.addAccelerationData( ax.getValue(), ax.getTime() );

        TimeData ay = MotionMath.getDerivative( yBody.getRecentVelocityTimeSeries( Math.min( accelWindow, yBody.getVelocitySampleCount() ) ) );
        yBody.addAccelerationData( ay.getValue(), ay.getTime() );
    }

    private static double getLinearRegressionMSE( Point2D[] pointHistory ) {
        double[][] pts = new double[2][pointHistory.length];
        for ( int i = 0; i < pts[0].length; i++ ) {

            pts[0][i] = pointHistory[i].getX();
            pts[1][i] = pointHistory[i].getY();
        }
        AbstractDoubleVector result = LinearMath.linearRegression( pts );
        double offset = result.getComponent( 0 );
        double slope = result.getComponent( 1 );
//        System.out.println( "slope = " + slope + ", offset=" + offset );

        double sumSq = 0;
        for ( int i = 0; i < pointHistory.length; i++ ) {
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
        for ( int i = 0; i < xBody.getPositionVariable().getSampleCount() && i < maxPts; i++ ) {
            list.add( new Point2D.Double( xBody.getPositionVariable().getRecentData( i ).getValue(), yBody.getPositionVariable().getRecentData( i ).getValue() ) );
        }
        Collections.reverse( list );
        return (Point2D[]) list.toArray( new Point2D.Double[0] );
    }

    public boolean isOnPlatform() {
        return updateStrategy instanceof OnPlatform;
    }

    private boolean isOnPlatform( RotationPlatform rotationPlatform ) {
        if ( updateStrategy instanceof OnPlatform ) {
            OnPlatform onPlatform = (OnPlatform) updateStrategy;
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
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
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

        xBody.getVelocityVariable().setValue( newV.getX() );
        yBody.getVelocityVariable().setValue( newV.getY() );

        xBody.getAccelerationVariable().setValue( newA.getX() );
        yBody.getAccelerationVariable().setValue( newA.getY() );
    }

    private void updateOnPlatform( double time ) {
        double omega = rotationPlatform.getVelocity();
        double r = getPosition().distance( rotationPlatform.getCenter() );
        boolean centered = rotationPlatform.getCenter().equals( getPosition() ) || r < 1E-6;
        Point2D newX = centered ? new Point2D.Double( rotationPlatform.getCenter().getX(), rotationPlatform.getCenter().getY() )
                                : Vector2D.Double.parseAngleAndMagnitude( r, getAngleOverPlatform() ).getDestination( rotationPlatform.getCenter() );
        Vector2D.Double centripetalVector = new Vector2D.Double( newX, rotationPlatform.getCenter() );
        AbstractVector2D newV = centered ? zero() : centripetalVector.getInstanceOfMagnitude( r * omega ).getNormalVector();
        AbstractVector2D newA = centered ? zero() : centripetalVector.getInstanceOfMagnitude( r * omega * omega );
        boolean offsetVelocityDueToConstantAccel = !centered && ( rotationPlatform.isAccelDriven() || rotationPlatform.isForceDriven() );
        boolean offsetVelocityDueToUserControl = !centered && ( rotationPlatform.isPositionDriven() );// && Math.abs( rotationPlatform.getVelocity() ) > 1E-2;
//        System.out.println( "offsetVelocityDueToUserControl = " + offsetVelocityDueToUserControl );
        if ( offsetVelocityDueToConstantAccel ) {
            //add on the tangential part under constant angular acceleration
            AbstractVector2D tanVector = centripetalVector.getInstanceOfMagnitude( r * rotationPlatform.getAcceleration() ).getNormalVector();
            newA = newA.getAddedInstance( tanVector );
        }
        else if ( offsetVelocityDueToUserControl ) {
            double avgAccel = rotationPlatform.getAccelerationVariable().estimateAverage( 2 );
            AbstractVector2D tanVector = centripetalVector.getInstanceOfMagnitude( r * avgAccel ).getNormalVector();
//            System.out.println( "avgAccel = " + avgAccel+", tanVector="+tanVector );
            newA = newA.getAddedInstance( tanVector );
//            newA=new Vector2D.Double(5,5);
        }

        addPositionData( newX, time );
        addVelocityData( newV, time );
        addAccelerationData( newA, time );

        //ToDo: these next 3 lines entail the assumption that the rotation platform has stepped in time first, and has at least one recorded value for recent position time series
        angle.addValue( rotationPlatform.getPosition() + relAngleOnPlatform, rotationPlatform.getRecentPositionTimeSeries( 1 )[0].getTime() );
        angularVelocity.addValue( rotationPlatform.getVelocity(), rotationPlatform.getRecentVelocityTimeSeries( 1 )[0].getTime() );
        angularAccel.addValue( rotationPlatform.getAcceleration(), rotationPlatform.getRecentAccelerationTimeSeries( 1 )[0].getTime() );
        checkCentripetalAccel();
        if ( r > 0 ) {
            lastNonZeroRadiusAngle = getAngleOverPlatform();
        }
//        System.out.println( "newV.getMagnitude() = " + newV.getMagnitude() );
        if ( newV.getMagnitude() > FLY_OFF_SPEED_THRESHOLD ) {
//            System.out.println( "flying off" );
            setUpdateStrategy( new FlyingOff( newV ) );
//            RotationResources.getInstance().getAudioClip( "bug-flyoff.wav" );
            String[] audio = new String[]{"whee5", "whoah-7", "words2"};
            RotationResources.getInstance().getAudioClip( audio[random.nextInt( audio.length )] + ".wav" ).play();
        }
    }

    static final Random random = new Random();

    private double getDAngle() {
        if ( rotationPlatform.getPositionSampleCount() >= 2 ) {
            double lastPlatformAngle = rotationPlatform.getRecentPositionTimeSeries( 2 )[0].getValue();
            double currentPlatformAngle = rotationPlatform.getRecentPositionTimeSeries( 2 )[1].getValue();
            return currentPlatformAngle - lastPlatformAngle;
        }
        else {
            //todo: could handle case in which there is exactly one point of recorded data
            return 0;
        }
    }

    private Vector2D.Double zero() {
        return new Vector2D.Double( 0, 0 );
    }

    public void checkCentripetalAccel() {
        if ( rotationPlatform == null ) {
            return;
        }
        Vector2D.Double cv = new Vector2D.Double( getPosition(), rotationPlatform.getCenter() );
        Vector2D av = getAcceleration();
        //these should be colinear
        double angle = cv.getAngle() - av.getAngle();

        if ( Math.abs( angle ) > 1E-2 & av.getMagnitude() > 1E-9 ) {
//            System.out.println( "RotationBody.updateBodyOnPlatform, angle="+angle );
        }
    }

    private void addAccelerationData( AbstractVector2D newAccel, double time ) {
        xBody.addAccelerationData( newAccel.getX(), time );
        yBody.addAccelerationData( newAccel.getY(), time );
    }

    private void addVelocityData( AbstractVector2D newVelocity, double time ) {
        xBody.addVelocityData( newVelocity.getX(), time );
        yBody.addVelocityData( newVelocity.getY(), time );
    }

    private void addPositionData( Point2D newLocation, double time ) {
        xBody.addPositionData( newLocation.getX(), time );
        yBody.addPositionData( newLocation.getY(), time );
    }

    public Vector2D getAcceleration() {
        return new Vector2D.Double( xBody.getAcceleration(), yBody.getAcceleration() );
    }

    public Vector2D getVelocity() {
        return new Vector2D.Double( xBody.getVelocity(), yBody.getVelocity() );
    }

    public String getImageName() {
        return imageName;
    }

    public void setTime( double time ) {
        xBody.setTime( time );
        yBody.setTime( time );
        accelMagnitude.setPlaybackTime( time );
        speed.setPlaybackTime( time );
        orientation.setPlaybackTime( time );
        if ( angle.getSampleCount() > 0 ) {
            angle.setPlaybackTime( time );
        }
        if ( angularVelocity.getSampleCount() > 0 ) {
            angularVelocity.setPlaybackTime( time );
        }
        if ( angularAccel.getSampleCount() > 0 ) {
            angularAccel.setPlaybackTime( time );
        }

        notifyVectorsUpdated();
        notifyPositionChanged();
    }

    public boolean isConstrained() {
        return constrained;
    }

    public void setDisplayGraph( boolean selected ) {
        if ( this.displayGraph != selected ) {
            this.displayGraph = selected;
            for ( int i = 0; i < listeners.size(); i++ ) {
                ( (Listener) listeners.get( i ) ).displayGraphChanged();
            }
        }
    }

    public boolean getDisplayGraph() {
        return displayGraph;
    }

    public ITemporalVariable getAccelMagnitude() {
        return accelMagnitude;
    }

    public ITemporalVariable getAccelX() {
        return xBody.getAccelerationVariable();
    }

    public ITemporalVariable getAccelY() {
        return yBody.getAccelerationVariable();
    }

    public ITemporalVariable getSpeed() {
        return speed;
    }

    public ITemporalVariable getVx() {
        return xBody.getVelocityVariable();
    }

    public ITemporalVariable getVy() {
        return yBody.getVelocityVariable();
    }

    public ITemporalVariable getPositionX() {
        return xBody.getPositionVariable();
    }

    public ITemporalVariable getPositionY() {
        return yBody.getPositionVariable();
    }

    public ITemporalVariable getAngularAcceleration() {
        return angularAccel;
    }

    public ITemporalVariable getAngularVelocity() {
        return angularVelocity;
    }

    public ITemporalVariable getAngle() {
        return angle;
    }

    private static abstract class UpdateStrategy implements Serializable {
        public abstract void detach();

        public abstract void stepInTime( double time, double dt );
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

        public void stepInTime( double time, double dt ) {
            updateOffPlatform( time );
        }

    }

    private class OnPlatform extends UpdateStrategy implements IVariable.Listener {
        private RotationPlatform rotationPlatform;
        private double prevAngle;

        public OnPlatform( RotationPlatform rotationPlatform ) {
            this.rotationPlatform = rotationPlatform;
            rotationPlatform.getPositionVariable().addListener( this );
            this.prevAngle = rotationPlatform.getPosition();
        }

        private void positionChanged( double dtheta ) {
            Line2D segment = new Line2D.Double( getPosition(), Vector2D.Double.parseAngleAndMagnitude( 0.01, getOrientation() ).getDestination( getPosition() ) );
            setPosition( rotate( getPosition(), rotationPlatform.getCenter(), dtheta ) );
            Line2D rot = rotate( segment, rotationPlatform.getCenter(), dtheta );

            setOrientation( new Vector2D.Double( rot.getP1(), rot.getP2() ).getAngle() );
//            updateVectorsOnPlatform();  //todo: was this necessary when repaints weren't synchronized?
            notifyPositionChanged();
        }

        public void detach() {
            rotationPlatform.getPositionVariable().removeListener( this );
        }

        public void stepInTime( double time, double dt ) {
            updateOnPlatform( time );
        }

        public void valueChanged() {
            positionChanged( rotationPlatform.getPosition() - prevAngle );
            this.prevAngle = rotationPlatform.getPosition();
        }
    }

    private void setPosition( Point2D point2D ) {
        setPosition( point2D.getX(), point2D.getY() );
    }

    public void setOrientation( double orientationValue ) {
        orientation.setValue( orientationValue );
        notifyOrientationChanged();
    }

    private void notifyOrientationChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).orientationChanged();
        }
    }

    private void notifyPositionChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).positionChanged();
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
        if ( Double.isNaN( x ) || Double.isNaN( y ) ) {
            throw new IllegalArgumentException( "x=" + x + ", y=" + y );
        }
        if ( this.getX() != x || this.getY() != y ) {
            xBody.setPosition( x );
            yBody.setPosition( y );
            updateAngleValue();
            notifyPositionChanged();
        }
    }

    private void updateAngleValue() {
        angle.setValue( getUserSetAngle() );
    }

    private class FlyingOff extends UpdateStrategy {
        private AbstractVector2D velocity;

        public FlyingOff( AbstractVector2D velocity ) {
            this.velocity = velocity;
        }

        public void detach() {
        }

        public void stepInTime( double time, double dt ) {
            double r = 4.5;
            if ( getPosition().distance( 0, 0 ) > r ) {
                velocity = new Vector2D.Double( 0, 0 );
                //scurry back to near the platform if offscreen?

                Vector2D.Double vec = new Vector2D.Double( xBody.getPosition(), yBody.getPosition() );
                AbstractVector2D a = vec.getInstanceOfMagnitude( r );
                xBody.setPosition( a.getX() );
                yBody.setPosition( a.getY() );
            }

            xBody.addPositionData( xBody.getPosition() + velocity.getX() * dt, time );
            yBody.addPositionData( yBody.getPosition() + velocity.getY() * dt, time );

            xBody.addVelocityData( velocity.getX(), time );
            yBody.addVelocityData( velocity.getY(), time );

            xBody.addAccelerationData( 0, time );
            yBody.addAccelerationData( 0, time );

            angle.addValue( getUserSetAngle(), time );
            angularVelocity.addValue( 0, time );
            angularAccel.addValue( 0, time );

        }
    }
}
