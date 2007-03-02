package edu.colorado.phet.ec3.test.phys1d;

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

    private UpdateStrategy updateStrategy = new Particle1DUpdate();
    private ParticleStage particleStage;

    public Particle( CubicSpline2D cubicSpline2D ) {
        this( new ParticleStage( cubicSpline2D ) );
    }

    public Particle( ParticleStage particleStage ) {
        particle1D = new Particle1D( particleStage.getCubicSpline2D( 0 ), true, g );
        this.particleStage = particleStage;
    }

    public void stepInTime( double dt ) {
        updateStrategy.stepInTime( dt );
        update();
    }

    public boolean isFreeFall() {
        return updateStrategy instanceof FreeFall;
    }

    public boolean isBelowSpline1D() {
        return !particle1D.isSplineTop();
    }

    interface UpdateStrategy {
        void stepInTime( double dt );
    }

    public double getMass() {
        return mass;
    }

    class Particle1DUpdate implements UpdateStrategy {
        public void stepInTime( double dt ) {

            //compare a to v/r^2 to see if it leaves the track
            double r = Math.abs( particle1D.getRadiusOfCurvature() );//todo: how can I be certain units are correct here?
            double normalForce = getMass() * particle1D.getSpeed() * particle1D.getSpeed() / r - getMass() * g;

//            System.out.println( "r = " + r );
//            System.out.println( "normalForce = " + normalForce );
//            System.out.println( "particle1D.getCurvatureDirection() = " + particle1D.getCurvatureDirection() + ", y>=0: " + ( particle1D.getCurvatureDirection().getY() >= 0 ) );

            //if normal force is positive on the top of a hill fly off
            //if normal force is negative on a valley fly off
            if( normalForce > 0 && particle1D.getCurvatureDirection().getY() >= 0 ) {
                System.out.println( "Switching to freefall" );
                switchToFreeFall();
                
                System.out.println( "switched to freefall, below=" + isBelowSplineZero() );
                Particle.this.stepInTime( dt );
            }
            else {
                particle1D.stepInTime( dt );
                x = particle1D.getX();
                y = particle1D.getY();
                AbstractVector2D vel = particle1D.getVelocity2D();
                vx = vel.getX();
                vy = vel.getY();
            }
        }
    }

    private void switchToFreeFall() {
        setVelocity( particle1D.getVelocity2D() );
        setFreeFall();
        //todo: update location so it's guaranteed on the right side of the spline?
        offsetOnSpline( particle1D.getCubicSpline2D(), particle1D.getAlpha(), particle1D.isSplineTop() );
    }

    public boolean isBelowSplineZero() {
        //        System.out.println( "below = " + below );
        return isBelowSpline( particleStage.getCubicSpline2D( 0 ), particleStage.getCubicSpline2D( 0 ).getClosestPoint( new Point2D.Double( x, y ) ), new Point2D.Double( x, y ) );
    }

    class FreeFall implements UpdateStrategy {

        public void stepInTime( double dt ) {
            boolean origBelow = isBelowSplineZero();
            System.out.println( "stepping freefall, below=" + isBelowSplineZero() );

            Point2D origLoc = new Point2D.Double( x, y );
            vy += g * dt;
            vx += 0;

            y += vy * dt + 0.5 * g * dt * dt;
            x += vx * dt;

            Point2D newLoc = new Point2D.Double( x, y );

            //check for crossover
            for( int i = 0; i < particleStage.numCubicSpline2Ds(); i++ ) {

                CubicSpline2D cubicSpline = particleStage.getCubicSpline2D( i );
                double alpha = cubicSpline.getClosestPoint( new Line2D.Double( origLoc, newLoc ) );
//                double alpha = cubicSpline.getClosestPoint( new Point2D.Double( (origLoc.getX()+newLoc.getX())/2,(origLoc.getY()+newLoc.getY())/2) );

                boolean below = isBelowSpline( cubicSpline, alpha, newLoc );
                System.out.println( "below = " + below );
                boolean crossed = origBelow != below;

                if( crossed ) {
                    System.out.println( "crossed over" );
                    double ptLineDist = new Line2D.Double( origLoc, newLoc ).ptLineDist( cubicSpline.evaluate( alpha ) );
                    System.out.println( "ptLineDist = " + ptLineDist );
                    if( ptLineDist < 0.5 ) {//this number was determined heuristically for a set of tests (free parameter)
                        //todo: should take a min over all possible crossover points (for this spline and others)

                        AbstractVector2D parallel = cubicSpline.getUnitParallelVector( alpha );
                        AbstractVector2D norm = cubicSpline.getUnitNormalVector( alpha );
                        //reflect the velocity about the parallel direction
                        AbstractVector2D parallelVelocity = parallel.getInstanceOfMagnitude( parallel.dot( getVelocity() ) );
                        double elasticity = 0.9;
                        AbstractVector2D newNormalVelocity = norm.getInstanceOfMagnitude( norm.dot( getVelocity() ) ).getScaledInstance( elasticity );
                        AbstractVector2D newVelocity = parallelVelocity.getSubtractedInstance( newNormalVelocity );

                        double stickiness = 0.25;

                        double testVal = Math.abs( newNormalVelocity.getMagnitude() / newVelocity.getMagnitude() );
                        System.out.println( "testv = " + testVal );
                        boolean bounce = testVal >= stickiness;

                        double newAlpha= cubicSpline.getClosestPoint( newLoc );
                        if( bounce ) {
                            setVelocity( newVelocity );
                            //set the position to be just on top of the spline
//                            offsetOnSpline( cubicSpline, alpha, isAboveSpline( cubicSpline, alpha, origLoc ) );
//                            offsetOnSpline( cubicSpline, alpha, !origBelow );
                            offsetOnSpline( cubicSpline, newAlpha, !origBelow );
                            System.out.println( "bounced" );
                        }
                        else {
//                            switchToTrack( cubicSpline, alpha, isAboveSpline( cubicSpline, alpha, origLoc ) );
                            
//                            switchToTrack( cubicSpline, alpha, !origBelow );
                            switchToTrack( cubicSpline, newAlpha, !origBelow );
                            System.out.println( "grabbed track" );
                        }
                        break;
                    }
                }
            }
        }
    }

    private void offsetOnSpline( CubicSpline2D cubicSpline, double alpha, boolean top ) {
        AbstractVector2D norm = cubicSpline.getUnitNormalVector( alpha );
        Point2D splineLoc = cubicSpline.evaluate( alpha );
        double sign = top ? 1.0 : -1.0;
//        Point2D finalPosition = norm.getInstanceOfMagnitude( 1.0E-4 * sign ).getDestination( splineLoc );//todo: determine this free parameter
        Point2D finalPosition = norm.getInstanceOfMagnitude( 1.0E-6 * sign ).getDestination( splineLoc );//todo: determine this free parameter
//        Point2D finalPosition = norm.getInstanceOfMagnitude( 1.0 * sign ).getDestination( splineLoc );//todo: determine this free parameter
        setPosition( finalPosition );
    }

    private void setVelocity( AbstractVector2D velocity ) {
        setVelocity( velocity.getX(), velocity.getY() );
    }

    public Vector2D.Double getVelocity() {
        return new Vector2D.Double( vx, vy );
    }

    public boolean isAboveSpline( CubicSpline2D cubicSpline2D, double alpha, Point2D loc ) {
        AbstractVector2D v = cubicSpline2D.getUnitNormalVector( alpha );
        Vector2D.Double a = new Vector2D.Double( cubicSpline2D.evaluate( alpha ), loc );
        return a.dot( v ) > 0;
    }

    public boolean isBelowSpline( CubicSpline2D cubicSpline2D, double alpha, Point2D loc ) {
        AbstractVector2D v = cubicSpline2D.getUnitNormalVector( alpha );
        Vector2D.Double a = new Vector2D.Double( cubicSpline2D.evaluate( alpha ), loc );
        return a.dot( v ) < 0;
    }

    class UserUpdateStrategy implements UpdateStrategy {
        public void stepInTime( double dt ) {
        }
    }

    public void setFreeFall() {
        setUpdateStrategy( new FreeFall() );
    }

    public void switchToTrack( CubicSpline2D spline, double alpha, boolean top ) {
        Vector2D.Double origVel=getVelocity();
        particle1D.setAlpha( alpha );
        particle1D.setCubicSpline2D( spline, top );
        double sign = spline.getUnitParallelVector( alpha ).dot( getVelocity() ) > 0 ? 1.0 : -1.0;
        particle1D.setVelocity( getSpeed() * sign );

        setUpdateStrategy( new Particle1DUpdate() );
        AbstractVector2D newVelocity= particle1D.getVelocity2D();
        double dot=newVelocity.dot( origVel );
        System.out.println( "switched to track, velocity dot product= " + dot );
        if (dot<0){
            System.out.println( "Velocities were in contrary directions" );
        }
    }

    public double getSpeed() {
        return Math.sqrt( vx * vx + vy * vy );
    }

    public void setUserUpdateStrategy() {
        setUpdateStrategy( new UserUpdateStrategy() );
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
