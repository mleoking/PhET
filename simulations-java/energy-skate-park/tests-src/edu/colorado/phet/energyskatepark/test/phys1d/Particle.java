package edu.colorado.phet.energyskatepark.test.phys1d;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:42:19 AM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */

public class Particle {
    private Particle1D particle1D;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double g = 9.8;
    private double mass = 1.0;

    double elasticity = 0.9;
    double stickiness = 0.25;//see neumann

    private UpdateStrategy updateStrategy = new Particle1DUpdate();
    private ParticleStage particleStage;
    private boolean convertNormalVelocityToThermalOnLanding = false;
    //    private double angle = 0.0;
    private double angle = Math.PI / 2;
    private boolean userControlled = false;
    private double thermalEnergy = 0.0;
    private double zeroPointPotentialY;

    public Particle( ParametricFunction2D parametricFunction2D ) {
        this( new ParticleStage( parametricFunction2D ) );
    }

    public Particle( ParticleStage particleStage ) {
        particle1D = new Particle1D( particleStage.getCubicSpline2DCount() > 0 ? particleStage.getCubicSpline2D( 0 ) : null, true, g );
        this.particleStage = particleStage;
        setUpdateStrategy( particleStage.getCubicSpline2DCount() > 0 ? ( (UpdateStrategy)new Particle1DUpdate() ) : new FreeFall() );
    }

    public void stepInTime( double dt ) {
        if( !userControlled ) {
            double origEnergy = getTotalEnergy();
            updateStrategy.stepInTime( dt );
            double finalEnergy = getTotalEnergy();
            double dE = finalEnergy - origEnergy;
            if( Math.abs( dE ) > 1E-6 ) {
                System.out.println( "Particle.stepInTime: de = " + dE );
            }
            update();
        }
    }

    public void setGravity( double g ) {
        this.g = g;
        particle1D.setGravity( g );
    }

    public boolean isFreeFall() {
        return updateStrategy instanceof FreeFall;
    }

    public double getElasticity() {
        return elasticity;
    }

    public void setElasticity( double elasticity ) {
        this.elasticity = elasticity;
    }

    public double getStickiness() {
        return stickiness;
    }

    public void setStickiness( double stickiness ) {
        this.stickiness = stickiness;
    }

    public boolean isAboveSpline1D() {
        return particle1D.isSplineTop();
    }

    public Particle1D getParticle1D() {
        return particle1D;
    }

    public boolean isSplineMode() {
        return updateStrategy instanceof Particle1DUpdate;
    }

    public double getAngle() {
        return angle;
    }

    public double getKineticEnergy() {
        return 0.5 * mass * getSpeed() * getSpeed();
    }

    public double getTotalEnergy() {
        return getKineticEnergy() + getPotentialEnergy() + getThermalEnergy();
    }

    public double getThermalEnergy() {
        return thermalEnergy;
    }

    public double getPotentialEnergy() {
        return -mass * g * ( y - zeroPointPotentialY );//todo: should particle1d know about this as well?
    }

    public double getGravity() {
        return g;
    }

    public boolean isOnSpline( ParametricFunction2D parametricFunction2D ) {
        return updateStrategy instanceof Particle1DUpdate && particle1D.getCubicSpline2D() == parametricFunction2D;
    }

    public ParametricFunction2D getSpline() {
        return ( updateStrategy instanceof Particle1DUpdate ) ? particle1D.getCubicSpline2D() : null;
    }

    public boolean isUserControlled() {
        return userControlled;
    }

    public void translate( double dx, double dy ) {
        setPosition( x + dx, y + dy );
    }

    public void resetThermalEnergy() {
        this.thermalEnergy = 0.0;
    }

    public void setMass( double mass ) {
        this.mass = mass;
        particle1D.setMass(mass);
    }

    public void setZeroPointPotentialY( double zeroPointPotentialY ) {
        this.zeroPointPotentialY = zeroPointPotentialY;
    }

    interface UpdateStrategy {
        void stepInTime( double dt );
    }

    public double getMass() {
        return mass;
    }

//    DebugFrame debugFrame = new DebugFrame( "debug outsideCircle" );

    class Particle1DUpdate implements UpdateStrategy {
        public void stepInTime( double dt ) {

//            AbstractVector2D sideVector = getSideVector( particle1D.getCubicSpline2D(), particle1D.getAlpha(), particle1D.isSplineTop() );
            AbstractVector2D sideVector = particle1D.getSideVector();
            boolean outsideCircle = sideVector.dot( particle1D.getCurvatureDirection() ) < 0;

//            System.out.println( "outsideCircle= " + outsideCircle );
//            if( !debugFrame.isVisible() ) {
//                debugFrame.setVisible( true );
//            }
//            debugFrame.appendLine( "outsideCircle=" + outsideCircle );

            //compare a to v/r^2 to see if it leaves the track
            double r = Math.abs( particle1D.getRadiusOfCurvature() );
            double centripForce = getMass() * particle1D.getSpeed() * particle1D.getSpeed() / r;
            double gravForceRad = particle1D.getNetForce().dot( particle1D.getCurvatureDirection() );

//            System.out.println( "r = " + r );
//            System.out.println( "particle1D.getCurvatureDirection() = " + particle1D.getCurvatureDirection() );
//            System.out.println( "centripForce = " + centripForce );
//            System.out.println( "gravForceRad = " + gravForceRad );

            boolean leaveTrack = false;
            if( gravForceRad < centripForce && outsideCircle ) {
                leaveTrack = true;
            }
            if( gravForceRad > centripForce && !outsideCircle ) {
                leaveTrack = true;
            }
            if( leaveTrack ) {
                System.out.println( "Switching to freefall" );
                switchToFreeFall();

                System.out.println( "switched to freefall, above=" + isAboveSplineZero() );
                Particle.this.stepInTime( dt );
            }
            else {
                particle1D.stepInTime( dt );
                updateStateFrom1D();
                if( !particle1D.isReflect() && ( particle1D.getAlpha() < 0 || particle1D.getAlpha() > 1.0 ) ) {
                    switchToFreeFall();
                }
            }
        }
    }

    private void updateStateFrom1D() {
        x = particle1D.getX();
        y = particle1D.getY();
        AbstractVector2D vel = particle1D.getVelocity2D();
        vx = vel.getX();
        vy = vel.getY();
        angle = particle1D.getSideVector().getAngle();
    }

    private void switchToFreeFall() {
        setVelocity( particle1D.getVelocity2D() );
        setFreeFall();
        //todo: update location so it's guaranteed on the right side of the spline?
        offsetOnSpline( particle1D.getCubicSpline2D(), particle1D.getAlpha(), particle1D.isSplineTop() );
        particle1D.setCubicSpline2D( null, false );
    }

    public boolean isAboveSpline( int index ) {
        return isAboveSpline( particleStage.getCubicSpline2D( index ), particleStage.getCubicSpline2D( index ).getClosestPoint( new Point2D.Double( x, y ) ), new Point2D.Double( x, y ) );
    }

    public boolean isAboveSplineZero() {
        return isAboveSpline( 0 );
    }

    public boolean[] getOrigAbove() {
        boolean[] orig = new boolean[particleStage.getCubicSpline2DCount()];
        for( int i = 0; i < particleStage.getCubicSpline2DCount(); i++ ) {
            orig[i] = isAboveSpline( i );
        }
        return orig;
    }

    //todo: test this function
    public static double pointSegmentDistance( Point2D pt, Line2D.Double line ) {
        double distToInfiniteLine = line.ptLineDist( pt );

        double distToP1 = line.getP1().distance( pt );
        double distToP2 = line.getP2().distance( pt );
        if( distToP1 > distToInfiniteLine || distToP2 > distToInfiniteLine ) {
            return Math.min( distToP1, distToP2 );
        }
        else {
            return distToInfiniteLine;
        }
    }

    class FreeFall implements UpdateStrategy {

        public void stepInTime( double dt ) {
            double origEnergy = getTotalEnergy();
            boolean[] origAbove = getOrigAbove();
//            System.out.println( "stepping freefall, origAbove=" + origAbove );

            Point2D origLoc = new Point2D.Double( x, y );
            vy += g * dt;
            vx += 0;

            y += vy * dt + 0.5 * g * dt * dt;
            x += vx * dt;

            double dE = getTotalEnergy() - origEnergy;
//            System.out.println( "FreeFall dE[0]= " + Math.abs( dE ) );
            double dH = dE / ( getMass() * getGravity() );
            y += dH;
//            System.out.println( "FreeFall dE[1]= " + Math.abs( getTotalEnergy() - origEnergy ) );

            Point2D newLoc = new Point2D.Double( x, y );

            //take a min over all possible crossover points
            double closestDist = Double.POSITIVE_INFINITY;
            ParametricFunction2D closestTrack = null;
            double closestAlpha = 0;
            int closestIndex = -1;

//            System.out.println( "particleStage = " + particleStage.toStringSerialization() );
            for( int i = 0; i < particleStage.getCubicSpline2DCount(); i++ ) {
//                System.out.println( "Checking spline[" + i + "]" );
                ParametricFunction2D cubicSpline = particleStage.getCubicSpline2D( i );
                double alpha = cubicSpline.getClosestPoint( newLoc );

                boolean above = isAboveSpline( cubicSpline, alpha, newLoc );
//                System.out.println( "above = " + above );

                //check for crossover
                boolean crossed = origAbove[i] != above;

                if( crossed && ( alpha > 0.0 && alpha < 1.0 ) ) {
                    double ptLineDist = pointSegmentDistance( cubicSpline.evaluate( alpha ), new Line2D.Double( origLoc, newLoc ) );
//                    double ptLineDist = new Line2D.Double( origLoc, newLoc ).ptLineDist( cubicSpline.evaluate( alpha ) );
//                    System.out.println( "crossed spline[" + i + "] at alpha=" + alpha + ", ptLineDist=" + ptLineDist );
                    if( ptLineDist < closestDist ) {
                        closestDist = ptLineDist;
                        closestTrack = cubicSpline;
                        closestAlpha = alpha;
                        closestIndex = i;
                    }
                }
            }

            if( closestDist < 0.2 ) {//this number was determined heuristically for a set of tests (free parameter)
                ParametricFunction2D cubicSpline = closestTrack;
                double alpha = closestAlpha;
                AbstractVector2D parallel = cubicSpline.getUnitParallelVector( alpha );
                AbstractVector2D norm = cubicSpline.getUnitNormalVector( alpha );
                //reflect the velocity about the parallel direction
                AbstractVector2D parallelVelocity = parallel.getInstanceOfMagnitude( parallel.dot( getVelocity() ) );

                AbstractVector2D newNormalVelocity = norm.getInstanceOfMagnitude( norm.dot( getVelocity() ) ).getScaledInstance( elasticity );
                AbstractVector2D newVelocity = parallelVelocity.getSubtractedInstance( newNormalVelocity );

                double testVal = Math.abs( newNormalVelocity.getMagnitude() / newVelocity.getMagnitude() );
//                System.out.println( "testv = " + testVal );
                boolean bounce = testVal >= stickiness;

                double newAlpha = cubicSpline.getClosestPoint( newLoc );

                //make sure the velocity is toward the track to enable switching to track (otherwise over a tight curve, the particle doesn't leave the track when N~0)
                boolean velocityTowardTrack = isVelocityTowardTrack( origLoc, cubicSpline, newAlpha );
//                System.out.println( "velocityTowardTrack = " + velocityTowardTrack );
                if( bounce || !velocityTowardTrack ) {
                    double energyBeforeBounce = getTotalEnergy();
                    setVelocity( newVelocity );

                    //set the position to be just on top of the spline
                    offsetOnSpline( cubicSpline, newAlpha, origAbove[closestIndex] );

                    double energyAfterBounce = getTotalEnergy();
                    thermalEnergy += ( energyBeforeBounce - energyAfterBounce );

//                    System.out.println( "bounced" );
                }
                else {
                    switchToTrack( cubicSpline, newAlpha, origAbove[closestIndex] );
//                    System.out.println( "grabbed track" );
                }
            }
        }
    }

    private boolean isVelocityTowardTrack( Point2D origPosition, ParametricFunction2D cubicSpline, double newAlpha ) {
        Vector2D vel = getVelocity();
        Vector2D toTrack = new Vector2D.Double( origPosition, cubicSpline.evaluate( newAlpha ) );
        return vel.dot( toTrack ) > 0;
    }

    private void offsetOnSpline( ParametricFunction2D cubicSpline, double alpha, boolean top ) {
        AbstractVector2D norm = cubicSpline.getUnitNormalVector( alpha );
        Point2D splineLoc = cubicSpline.evaluate( alpha );
        double sign = top ? 1.0 : -1.0;
        Point2D finalPosition = norm.getInstanceOfMagnitude( 1.0E-3 * sign ).getDestination( splineLoc );//todo: determine this free parameter
        setPosition( finalPosition );
    }


    private void setVelocity( AbstractVector2D velocity ) {
        setVelocity( velocity.getX(), velocity.getY() );
    }

    public Vector2D.Double getVelocity() {
        return new Vector2D.Double( vx, vy );
    }

    public boolean isAboveSpline( ParametricFunction2D parametricFunction2D, double alpha, Point2D loc ) {
        AbstractVector2D v = parametricFunction2D.getUnitNormalVector( alpha );
        Vector2D.Double a = new Vector2D.Double( parametricFunction2D.evaluate( alpha ), loc );
        return a.dot( v ) > 0;
    }

    public void setFreeFall() {
        setUpdateStrategy( new FreeFall() );
    }

    public void switchToTrack( ParametricFunction2D spline, double alpha, boolean top ) {
        Vector2D.Double origVel = getVelocity();
        particle1D.setAlpha( alpha );
        particle1D.setCubicSpline2D( spline, top );
        double sign = spline.getUnitParallelVector( alpha ).dot( getVelocity() ) > 0 ? 1.0 : -1.0;

        double newVelocityMagnitude = convertNormalVelocityToThermalOnLanding ? getParallelSpeed( spline, alpha ) : getSpeed();
        particle1D.setVelocity( newVelocityMagnitude * sign );

        setUpdateStrategy( new Particle1DUpdate() );
        AbstractVector2D newVelocity = particle1D.getVelocity2D();
        double dot = newVelocity.dot( origVel );
        System.out.println( "switched to track, velocity dot product= " + dot );
        if( dot < 0 ) {
            System.out.println( "Velocities were in contrary directions" );
        }
        updateStateFrom1D();
    }

    public boolean isConvertNormalVelocityToThermalOnLanding() {
        return convertNormalVelocityToThermalOnLanding;
    }

    public void setConvertNormalVelocityToThermalOnLanding( boolean convertNormalVelocityToThermalOnLanding ) {
        this.convertNormalVelocityToThermalOnLanding = convertNormalVelocityToThermalOnLanding;
    }

    private double getParallelSpeed( ParametricFunction2D parametricFunction2D, double alpha ) {
        return Math.abs( parametricFunction2D.getUnitParallelVector( alpha ).dot( getVelocity() ) );
    }

    public double getSpeed() {
        return Math.sqrt( vx * vx + vy * vy );
    }

    public void setUserControlled( boolean userControlled ) {
        this.userControlled = userControlled;
//        setUpdateStrategy( new UserUpdateStrategy() );
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy = updateStrategy;
        update();
    }

    private void update() {
        notifyListeners();
    }

    private ArrayList listeners = new ArrayList();

    public Point2D getPosition() {
        return new Point2D.Double( x, y );
    }

    public void setPosition( Point2D pt ) {
        setPosition( pt.getX(), pt.getY() );
    }

    public void setPosition( double x, double y ) {
        setFreeFall();
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setVelocity( double vx, double vy ) {
        this.vx = vx;
        this.vy = vy;
        notifyListeners();
    }

    public static interface Listener {
        void particleChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.particleChanged();
        }
    }

}
